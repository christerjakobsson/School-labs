import java.io.*;
import java.net.*;

/**
 * This class takes handles the tcp connection to one specific client, it keep
 * track of its nickname and the time the user connected. it extends thread and
 * the thread listens for incoming packets at a inputstream and handles the
 * package depending of what kind of package it is.
 *
 * @author Christer
 */
public class ClientThread extends Thread {

	private boolean keepGoing = true;
	private long timeStamp;
	private byte[] nickName;
	private byte[] oldNick;
	private byte[] tempNick;
	private String cryptKey = "foobar";
	private Server s;
	private Packager pk;
	private Socket socket;
	private DataOutputStream outFromServer;
	private DataInputStream inToServer;
	private long lastTime;
	private int floodTimes = 0;
	private boolean joinReceived = false;

	/**
	 * Constructor, when a client connects to the server this class gets created
	 * and it handles all the communication between the server and the client
	 *
	 * @param connectionSocket
	 * @param s Server

	 * @param t the time the user connected
	 *
	 * @throws IOException
	 */
	ClientThread(Socket connectionSocket, Server s, long t) throws IOException {
		socket = connectionSocket;
		this.s = s;
		pk = new Packager(s);
		timeStamp = t;

		this.start();
	}

	/**
	 * @return Cryptkey as a string
	 */
	public String getCryptKey() {
		return cryptKey;
	}

	/**
	 * @return Inputstream
	 */
	public DataInputStream getInToServer() {
		return inToServer;
	}

	/**
	 * @return The current nickName
	 */
	public byte[] getNickName() {
		return nickName;
	}

	/**
	 * @return Outputstream that the server uses to send packets to the client.
	 */
	public DataOutputStream getOutFromServer() {
		return outFromServer;
	}

	/**
	 * @return the socket.
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @return timestamp for when the client connected to the server.
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * This method is a thread that always runs as long as the client is
	 * connected. it takes in the packets via the inputstream and processes the
	 * data and does appropriate arrangements depending of what type of packet
	 * it was. if the stream returns a -1 it means that the connection is closed
	 * and the loop stops and the whole object shuts down
	 *
	 */
	public void run() {
		keepGoing = true;
		while (keepGoing) {
			try {
				socket.setSoTimeout(s.getTimeOut());
				inToServer = new DataInputStream(socket.getInputStream());
				outFromServer = new DataOutputStream(socket.getOutputStream());
				byte[] oneByte = new byte[1];

				if (inToServer.read(oneByte) != -1) {
					byte[] receiveData = new byte[3];
					inToServer.read(receiveData, 0, 3);
					PDU recPdu = new PDU(4);
					recPdu.setSubrange(0, oneByte);
					recPdu.setSubrange(1, receiveData);

					// flooding
					if (s.getTime() < lastTime + 2) {
						System.out.print("Flooding!");
						sender(pk.opMessagePacket(
								"You're flooding the server, " + floodTimes
										+ " warning", "Server"));
						floodTimes++;
					} else if (s.getTime() > lastTime + 2) {
						if (!(floodTimes <= 0)) {
							floodTimes--;
						}
					}
					if (floodTimes > 5) {
						sender(pk.opMessagePacket("You got kicked spammer!!!",
								"Server"));
						keepGoing = false;
					}
					lastTime = s.getTime();

					if (recPdu.getByte(0) == OpCodes.JOIN && !joinReceived) {
						joinReceived = true;
						System.out.print("JOIN received");
						tempNick = null;

						int pduSize = pk.pduSize(8 + recPdu.getByte(1));
						recPdu.extendTo(pduSize);

						byte[] restMessage = new byte[pduSize - 4];

						inToServer.read(restMessage, 0, (pduSize - 4));
						recPdu.setSubrange(4, restMessage);

						tempNick = recPdu.getSubrange(4, recPdu.getByte(1));
						nickName = null;
						nickName = s.formatNick(tempNick);

						byte[] nickList = s.putTogetherNicks(nickName);
						sender(pk.opNickPacket(nickList));

						s.echoMessage(pk.opUJoin(nickName));

					} else if (recPdu.getByte(0) == OpCodes.MESSAGE
							&& joinReceived) {
						recPdu.extendTo(12);

						byte[] messLength = new byte[8];
						inToServer.read(messLength, 0, 8);

						recPdu.setSubrange(4, messLength);

						int pduSize = pk.pduSize((recPdu.length() + recPdu
								.getShort(4)));

						int newPduSize = pk.pduSize(pduSize + nickName.length);
						recPdu.extendTo(newPduSize);

						byte[] restMessage = new byte[(pduSize - 12)];

						inToServer.read(restMessage, 0, (pduSize - 12));
						recPdu.setSubrange(12, restMessage);

						byte checkSum = Checksum.calc(recPdu.getBytes(),
								recPdu.length());
						if (checkSum == 0) {

							recPdu.setByte(2, (byte) nickName.length);
							recPdu.setInt(8, (int) s.getTime());
							recPdu.setSubrange(pduSize, nickName);

							recPdu.setByte(3, (byte) 0);
							checkSum = Checksum.calc(recPdu.getBytes(),
									recPdu.length());
							recPdu.setByte(3, checkSum);

							int offset = pduSize + nickName.length;
							for (int i = offset; i < recPdu.length(); i++) {
								recPdu.setByte(i, (byte) '\0');
							}

							s.echoMessage(recPdu.getBytes());
						} else
							System.out.print("Wrong Checksum, ignoring packet");

					} else if (recPdu.getByte(0) == OpCodes.QUIT
							&& joinReceived) {
						keepGoing = false;
						System.out.print("QUIT received");

					} else if (recPdu.getByte(0) == OpCodes.WHOIS
							&& joinReceived) {
						System.out.print("WHOIS received");

						int pduSize = pk.pduSize(4 + recPdu.getByte(1));
						recPdu.extendTo(pduSize);

						byte[] restMessage = new byte[(pduSize - 4)];
						inToServer.read(restMessage, 0, pduSize - 4);
						recPdu.setSubrange(4, restMessage);
						byte[] nick = recPdu.getSubrange(4, recPdu.getByte(1));

						sender(pk.opUInfoPacket(nick));

					} else if (recPdu.getByte(0) == OpCodes.CHNICK
							&& joinReceived) {
						System.out.print("CHNICK received");
						int pduSize = pk.pduSize(4 + recPdu.getByte(1));
						recPdu.extendTo(pduSize);

						byte[] restMess = new byte[(pduSize - 4)];
						inToServer.read(restMess, 0, (pduSize - 4));
						recPdu.setSubrange(4, restMess);

						byte[] nick = recPdu.getSubrange(4, recPdu.getByte(1));
						oldNick = nickName;

						nickName = s.formatNick(nick);

						s.replaceUserNick(oldNick, nickName);

						s.echoMessage(pk.opUCNickPacket(oldNick, nickName,
								(int) s.getTime()));

					} else if (recPdu.getByte(0) == OpCodes.CHTOPIC
							&& joinReceived) {
						System.out.print("CHTOPIC received");
						int pduSize = pk.pduSize(4 + recPdu.getByte(1));
						recPdu.extendTo(4 + pduSize);

						byte[] restMess = new byte[pduSize - 4];
						inToServer.read(restMess, 0, pduSize - 4);
						recPdu.setSubrange(4, restMess);

						byte[] newTopic = recPdu.getSubrange(4,
								recPdu.getByte(1));

						System.out.print("New topic " + newTopic);
						s.setTopic(s.decoder(newTopic));

						s.sendTopicToDns(pk.opChangeTopicPacket(newTopic));
						s.echoMessage(pk.opChangeTopicPacket(newTopic));

					} else {
						if (joinReceived && keepGoing) {
							sender(pk
									.opMessagePacket(
											"You havent sent a join packet to the server",
											"Server"));
						}
						if (keepGoing) {
							sender(pk
									.opMessagePacket("Wrong OP code", "Server"));
						}

						keepGoing = false;
					}
				} else {
					keepGoing = false;
				}
			} catch (SocketTimeoutException e) {
				if (!keepGoing) {
					System.out.print("Socket timed out");
				}
			} catch (SocketException e) {
				if (!keepGoing) {
					System.out.print("Connection reset");
				}
				keepGoing = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		stopConnection();
	}

	/**
	 * Closes the connection for the client and sends a message to all the other
	 * clients about it, tries to close socket and streams and remove the object
	 *
	 */
	public void stopConnection() {
		sender(pk.opQuitPacket());
		;
		s.echoMessage(pk.opULeave(getNickName(), s.getTime()));
		try {
			s.removeClient(this);
			outFromServer.close();
			inToServer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that sends a packet through the stream
	 *
	 * @param sendData
	 */
	public void sender(byte[] sendData) {
		try {
			outFromServer.write(sendData);
			outFromServer.flush();
		} catch (SocketException e) {
			keepGoing = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to the the cryptkey.
	 *
	 * @param cryptKey
	 */
	public void setCryptKey(String cryptKey) {
		this.cryptKey = cryptKey;
	}

	/**
	 * Method to set the nickName
	 *
	 * @param nickName
	 */
	public void setNickName(byte[] nickName) {
		this.nickName = nickName;
	}

	public boolean getKeepGoing() {
		return keepGoing;
	}

	public void setKeepGoing(boolean bool) {
		keepGoing = bool;
	}
}
