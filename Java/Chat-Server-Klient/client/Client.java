import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client extends Thread {
	private String nickName = "User";
	private String message;
	private String tempName = "User";
	private String messageNick;
	private String allNicks;
	private String topic;
	private String DNS;
	private String encryptionKey = "foobar";
	private String whoIsNick;
	private ArrayList<String> usersList;
	private ArrayList<ServerInfo> serverList = new ArrayList<ServerInfo>();
	boolean isCompressed = false;

	boolean isEncrypted = false;
	private int DNSPort = 0;
	private int timeOut = 4000;
	private Packager packager;

	private Socket tcpSocket;
	private DataOutputStream outToServer;
	private DataInputStream inFromServer;
	private boolean keepGoing = false;
	public Client() throws IOException {
		usersList = new ArrayList<String>();
		serverList = new ArrayList<ServerInfo>();
		loadSettings();
		packager = new Packager(this);
		setName("Client");
	}
	/**
	 * Takes a PDU containing the whole message packet and checks if the
	 * checksum is correct, returns true if it is correct.
	 *
	 * @param recPdu
	 * @return
	 */
	private boolean checkCheckSum(PDU recPdu) {
		int msgType = recPdu.getByte(1);
		PDU innerPdu;
		byte checkSum = Checksum.calc(recPdu.getBytes(), recPdu.length());
		byte innerCheckSum;
		byte moreInnerCheckSum;
		boolean check = false;

		switch (msgType) {
		case MsgTypes.TEXT:
			if (checkSum == 0) {
				check = true;
			}
			break;

		case MsgTypes.CRYPT:
			innerPdu = new PDU(recPdu.getSubrange(12, recPdu.getShort(4)),
					recPdu.getShort(4));
			innerCheckSum = Checksum.calc(innerPdu.getBytes(),
					innerPdu.length());

			if (checkSum == 0 && innerCheckSum == 0) {
				check = true;
			}
			break;

		case MsgTypes.COMP:
			innerPdu = new PDU(recPdu.getSubrange(12, recPdu.getShort(4)),
					recPdu.getShort(4));
			innerCheckSum = Checksum.calc(innerPdu.getBytes(),
					innerPdu.length());

			if (checkSum == 0 && innerCheckSum == 0) {
				check = true;
			}
			break;

		case MsgTypes.COMPCRYPT:
			innerPdu = new PDU(recPdu.getSubrange(12, recPdu.getShort(4)),
					recPdu.getShort(4));
			innerCheckSum = Checksum.calc(innerPdu.getBytes(),
					innerPdu.length());
			byte[] decrypt = innerPdu.getSubrange(8, innerPdu.getShort(2));

			Crypt.decrypt(decrypt, decrypt.length, encryptionKey.getBytes(),
					encryptionKey.getBytes().length);

			PDU moreInnerPdu = new PDU(decrypt, decrypt.length);
			moreInnerCheckSum = Checksum.calc(moreInnerPdu.getBytes(),
					moreInnerPdu.length());
			if (checkSum == 0 && innerCheckSum == 0 && moreInnerCheckSum == 0) {
				check = true;
			}
			break;
		default:
			break;
		}
		return check;
	}

	/**
	 * Connect the client to a server, takes an ip as a string and a port as
	 * integer. if the server is full a socket will open and the client Thread
	 * starts.
	 *
	 * @param ip
	 * @param tcp
	 * @throws IOException
	 */
	public void connectToServer(String ip, String tcp) throws IOException {
		boolean isMaxUsers = false;

		for (int i = 0; i < serverList.size(); i++) {
			if (ip.equals(serverList.get(i).getIp())) {
				if (serverList.get(i).getNrOfClients() == 255) {
					System.out.print("Too many users on that server (max 255)");
					isMaxUsers = true;
				}
			}
		}
		if (!isMaxUsers) {
			int tcpPort = Integer.parseInt(tcp);
			System.out.print("Connected to " + InetAddress.getByName(ip) + ":"
					+ tcpPort);
			tcpSocket = new Socket(InetAddress.getByName(ip), tcpPort);
			outToServer = new DataOutputStream(tcpSocket.getOutputStream());
			inFromServer = new DataInputStream(tcpSocket.getInputStream());
			outToServer.write(packager.opJoin(nickName));
			new Thread(this).start();
		}
	}

	/**
	 * Splits a string with nicknames, the first nick is the clients nickname.
	 *
	 * @param s
	 */
	public void createUserList(String s, int nrOfNicks) {
		Scanner scanner = new Scanner(s);
		scanner.useDelimiter("\0");
		usersList.clear();
		nickName = scanner.next();
		usersList.add(nickName);
		String temp;
		int i = 1;
		while (i < nrOfNicks) {
			temp = scanner.next();
			usersList.add(temp);
			i++;
		}
		scanner.close();
		Collections.sort(usersList);

	}

	/**
	 * Returns a integer depending on if encrypt/compress settings.
	 *
	 * @return
	 */
	public int currentMessageSettings() {
		int code = 0;
		if (isCompressed && !isEncrypted) {
			code = MsgTypes.COMP;
		} else if (!isCompressed && isEncrypted) {
			code = MsgTypes.CRYPT;
		} else if (isCompressed && isEncrypted) {
			code = MsgTypes.COMPCRYPT;
		} else {
			code = MsgTypes.TEXT;
		}
		return code;
	}

	/**
	 * Decodes a byte array to a string that supports UTF-8
	 *
	 * @param b
	 * @return
	 */
	public String decoder(byte[] b) {
		String s = null;
		try {
			s = new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Decompresses a message. Takes a compressed byte array and returns a
	 * string.
	 *
	 * @param subrange
	 * @return
	 */
	private String decompressedMessage(byte[] subrange) {
		PDU compPdu = new PDU(subrange, subrange.length);
		byte[] comp = compPdu.getSubrange(8, compPdu.getShort(2));
		try {
			message = Compressor.decompressMessage(comp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * Decrypts a message. Takes a PDU, the bytearray with the message is
	 * retrieved. Then it decrypts te message and returns it.
	 *
	 * @param recPdu
	 * @return
	 */
	private byte[] decryptMessage(PDU recPdu) {
		byte[] cryptByte = recPdu.getSubrange(12, recPdu.getShort(4));
		PDU cryptPdu = new PDU(recPdu.getShort(4));
		cryptPdu.setSubrange(0, cryptByte);

		byte[] cryptedMess = cryptPdu.getSubrange(8, cryptPdu.getShort(2));
		int srclen = cryptedMess.length;
		byte[] key = encryptionKey.getBytes();
		int keylen = key.length;
		Crypt.decrypt(cryptedMess, srclen, key, keylen);
		return cryptedMess;
	}

	/**
	 * Encodes a String to a byte array to make it support UTF-8
	 *
	 * @param s
	 * @return
	 */
	public byte[] encoder(String s) {
		byte[] encode = null;
		try {
			encode = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encode;
	}

	/**
	 * Takes current time as parameter and formats it.
	 *
	 * @param l
	 * @return
	 */
	public String formatTime(long l) {
		SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
		String formattedDate = sdf.format(l * 1000);
		return formattedDate;
	}

	/**
	 * Gets Dns address
	 *
	 * @return
	 */
	public String getDNS() {
		return DNS;
	}

	/**
	 * Gets Dns Port.
	 *
	 * @return
	 */
	public String getDNSPort() {
		return Integer.toString(DNSPort);
	}

	/**
	 * Gets Crypt key
	 *
	 * @return
	 */
	public String getEncryptionKey() {
		return encryptionKey;
	}

	/**
	 * Gets keepGoing
	 *
	 * @return
	 */
	public boolean getKeepGoing() {
		return keepGoing;
	}

	/**
	 * Gets nickName
	 *
	 * @return
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * Gets outToServer
	 *
	 * @return
	 */
	public DataOutputStream getOutToServer() {
		return outToServer;
	}

	/**
	 * Gets serverList array
	 *
	 * @return
	 */
	public ArrayList<ServerInfo> getServers() {
		return serverList;
	}

	/**
	 * Gets tcpSocket
	 *
	 * @return
	 */
	public Socket getTcpSocket() {
		return tcpSocket;
	}

	public String getTempName() {
		return tempName;
	}

	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * Gets topic
	 *
	 * @return
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Gets userList
	 *
	 * @return
	 */
	public ArrayList<String> getUsersList() {
		return usersList;
	}

	public String getWhoIsNick() {
		return whoIsNick;
	}

	/**
	 * Loads settings.
	 *
	 * @throws IOException
	 */
	private void loadSettings() throws IOException {
		File file = new File("client.cfg");

		// Testar om filen finns, om inte skapas den och standardvärden sätts
		if (!file.exists()) {
			PrintWriter writer = new PrintWriter("client.cfg", "UTF-8");
			writer.println("127.0.0.1");
			writer.println(1337);
			writer.println("user");
			writer.close();
		}
		BufferedReader fileRead = new BufferedReader(new FileReader(file));

		// Laddar inställningar
		DNS = fileRead.readLine();
		DNSPort = Integer.parseInt(fileRead.readLine());
		nickName = fileRead.readLine();
		fileRead.close();
	}

	/**
	 * Processes incominng messages depending on which kind of message it is.
	 *
	 * @param recPdu
	 */
	private void messageHandler(PDU recPdu) {
		String messType = "";
		long time = recPdu.getInt(8);

		if (recPdu.getByte(1) == MsgTypes.TEXT) {
			message = decoder(recPdu.getSubrange(12, recPdu.getShort(4)));

		} else if (recPdu.getByte(1) == MsgTypes.CRYPT) {
			byte[] decrypted = decryptMessage(recPdu);

			message = decoder(decrypted);
			messType = " <encrypt> ";
		} else if (recPdu.getByte(1) == MsgTypes.COMPCRYPT) {
			byte[] decrypted = decryptMessage(recPdu);

			message = decompressedMessage(decrypted);
			messType = " <compcrypt> ";

		} else if (recPdu.getByte(1) == MsgTypes.COMP) {
			int compPduLength = recPdu.getShort(4);
			message = decompressedMessage(recPdu.getSubrange(12, compPduLength));
			messType = " <comp> ";
		}
		int nickStart = packager.pduSize(12 + recPdu.getShort(4));

		messageNick = decoder(recPdu.getSubrange(nickStart, recPdu.getByte(2)));

		Scanner scanner = new Scanner(messageNick);
		scanner.useDelimiter("\0");
		messageNick = scanner.next();
		scanner.close();

		System.out.print(formatTime(time) + " " + messageNick + "" + messType
				+ ": " + message);
	}

	/**
	 * Gets the serverlist from the dns. A socket is created with a timeout.
	 * Packager creates the getlist packet and then it gets sent to the DNS,
	 * then the list is received. and put into a PDU and then each server gets
	 * split up and put in the class ServerInfo that gets stored in a arrayList.
	 *
	 * @throws IOException
	 */
	public void retrieveServerList() throws IOException {
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(timeOut);
		serverList.clear();
		byte[] sendData = packager.opGetListPacket();
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, InetAddress.getByName(DNS), DNSPort);
		byte[] receiveData = new byte[65507];
		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);
		socket.send(sendPacket);
		socket.receive(receivePacket);
		PDU recPdu = new PDU(receivePacket.getData(), receivePacket.getLength());
		int offsetVal = 4;
		int packetLength;

		int pduSize = packager.pduSize(receivePacket.getLength());
		PDU temp = new PDU(pduSize);

		while (offsetVal < receivePacket.getLength()) {
			packetLength = 8 + recPdu.getByte(offsetVal + 7);
			temp.setSubrange(0, recPdu.getSubrange(offsetVal, packetLength));
			serverList.add(new ServerInfo(temp));
			offsetVal = offsetVal + packetLength;
			while (offsetVal % 4 != 0) {
				offsetVal++;
			}
		}

		int nrOfServers = recPdu.getShort(2);
		int seqNr = recPdu.getByte(1);

		while (serverList.size() < nrOfServers) {
			socket.receive(receivePacket);
			recPdu = new PDU(receivePacket.getData(), receivePacket.getLength());
			if (seqNr + 1 == recPdu.getByte(1)) {
				offsetVal = 4;

				pduSize = packager.pduSize(receivePacket.getLength());
				temp = new PDU(pduSize);

				while (offsetVal < receivePacket.getLength()) {

					packetLength = 8 + recPdu.getByte(offsetVal + 7);
					temp.setSubrange(0,
							recPdu.getSubrange(offsetVal, packetLength));
					serverList.add(new ServerInfo(temp));
					offsetVal = offsetVal + packetLength;
					while (offsetVal % 4 != 0) {
						offsetVal++;
					}
				}
			} else {
				serverList.clear();
				System.out.print("Packets out of order, Please refresh");
			}
		}
		socket.close();
	}

	/**
	 * Thread in client that listens for incoming messages. keepGoing sets to
	 * true which in the client shows that the connection is up. as long as the
	 * stream doesnt contain a -1 it will run.
	 *
	 */
	public void run() {
		keepGoing = true;
		while (keepGoing) {
			try {
				tcpSocket.setSoTimeout(timeOut);
				byte[] oneByte = new byte[1];
				PDU recPdu = new PDU(4);
				if (inFromServer.read(oneByte) != -1) {
					byte[] receiveData = new byte[3];
					inFromServer.read(receiveData, 0, 3);
					recPdu.setSubrange(0, oneByte);
					recPdu.setSubrange(1, receiveData);
					if (recPdu.getByte(0) == OpCodes.MESSAGE) {
						messageNick = "";
						message = "";
						byte[] messLength = new byte[8];
						recPdu.extendTo(12);
						inFromServer.read(messLength, 0, 8);
						recPdu.setSubrange(4, messLength);

						int nickStart = packager
								.pduSize(12 + recPdu.getByte(2));

						int pduSize = packager.pduSize(nickStart
								+ recPdu.getShort(4));
						recPdu.extendTo(pduSize);

						byte[] restMessage = new byte[(pduSize - 12)];
						inFromServer.read(restMessage, 0, (pduSize - 12));
						recPdu.setSubrange(12, restMessage);

						if (checkCheckSum(recPdu)) {
							messageHandler(recPdu);
						} else
							System.out.print("Wrong Checksum, ignoring packet");

					} else if (recPdu.getByte(0) == OpCodes.QUIT) {
						keepGoing = false;
					} else if (recPdu.getByte(0) == OpCodes.NICKS) {
						usersList.clear();
						allNicks = "";

						int pduSize = packager.pduSize(4 + recPdu.getShort(2));
						recPdu.extendTo(pduSize);
						byte[] restMess = new byte[pduSize - 4];
						inFromServer.read(restMess, 0, pduSize - 4);
						recPdu.setSubrange(4, restMess);

						int nrOfNicks = recPdu.getByte(1);

						allNicks = decoder(recPdu.getSubrange(4,
								recPdu.getShort(2)));

						createUserList(allNicks, nrOfNicks);

					} else if (recPdu.getByte(0) == OpCodes.UINFO) {
						String address = "";

						recPdu.extendTo(12);
						byte[] restMess = new byte[8];
						inFromServer.read(restMess, 0, 8);
						recPdu.setSubrange(4, restMess);

						int time = (int) recPdu.getInt(8);
						address = new String(recPdu.getByte(4) + "."
								+ recPdu.getByte(5) + "." + recPdu.getByte(6)
								+ "." + recPdu.getByte(7));

						System.out.print(whoIsNick + " is: " + "Adress: "
								+ address + " (Joined: " + formatTime(time)
								+ ")");
					} else if (recPdu.getByte(0) == OpCodes.UJOIN) {
						String nick;

						int pduSize = packager.pduSize(8 + recPdu.getByte(1));
						recPdu.extendTo(pduSize);

						byte[] restMess = new byte[pduSize - 4];
						inFromServer.read(restMess, 0, pduSize - 4);
						recPdu.setSubrange(4, restMess);
						nick = decoder(recPdu.getSubrange(8, pduSize - 8));
						Scanner scanner = new Scanner(nick);
						scanner.useDelimiter("\0");
						nick = scanner.next();
						scanner.close();

						int time = (int) recPdu.getInt(4);

						System.out.print(formatTime(time) + ": " + nick
								+ " Joined the server.");
						boolean nickExist = false;
						for (int i = 0; i < usersList.size(); i++) {
							if (usersList.get(i).contentEquals(nick)) {
								nickExist = true;
							}
						}
						if (!nickExist) {
							usersList.add(nick);
						}
						Collections.sort(usersList);
					} else if (recPdu.getByte(0) == OpCodes.ULEAVE) {
						String nick = "";
						long time = 0;
						int pduSize = packager.pduSize(8 + recPdu.getByte(1));
						recPdu.extendTo(pduSize);

						byte[] restMess = new byte[pduSize - 4];
						inFromServer.read(restMess, 0, pduSize - 4);
						recPdu.setSubrange(4, restMess);
						nick = decoder(recPdu.getSubrange(8, pduSize - 8));
						Scanner scanner = new Scanner(nick);
						scanner.useDelimiter("\0");
						nick = scanner.next();
						scanner.close();
						time = (int) recPdu.getInt(4);

						for (int i = 0; i < usersList.size(); i++) {
							if (usersList.get(i).contentEquals(nick)) {
								usersList.remove(i);
							}
						}
						System.out.print(formatTime(time) + ": " + nick
								+ " Left the server.");

					} else if (recPdu.getByte(0) == OpCodes.UCNICK) {
						String oldNick = "";
						String newNick = "";

						int pduSize = packager.pduSize(8 + recPdu.getByte(1));
						int newNickStart = pduSize;

						pduSize = packager.pduSize(newNickStart
								+ recPdu.getByte(2));
						recPdu.extendTo(pduSize);

						byte[] restMess = new byte[pduSize - 4];
						inFromServer.read(restMess, 0, pduSize - 4);
						recPdu.setSubrange(4, restMess);

						oldNick = decoder(recPdu.getSubrange(8,
								recPdu.getByte(1)));
						Scanner scanner = new Scanner(oldNick);

						scanner.useDelimiter("\0");
						oldNick = scanner.next();
						scanner.close();

						newNick = decoder(recPdu.getSubrange(newNickStart,
								recPdu.getByte(2)));
						scanner = new Scanner(newNick);
						scanner.useDelimiter("\0");
						newNick = scanner.next();
						scanner.close();
						int time = (int) recPdu.getInt(4);
						if (tempName.contentEquals(oldNick)) {
							nickName = newNick;
						}

						for (int i = 0; i < usersList.size(); i++) {
							if (usersList.get(i).contentEquals(oldNick)) {
								if (usersList.get(i).contentEquals(nickName)) {
									nickName = newNick;
								}
								usersList.remove(i);
								usersList.add(newNick);

							}
						}
						Collections.sort(usersList);

						System.out.print(formatTime(time) + " " + oldNick
								+ ": " + "Changed nickname to " + newNick);
					} else if (recPdu.getByte(0) == OpCodes.CHTOPIC) {
						String newTopic = "";
						int pduSize = packager.pduSize(recPdu.getByte(1) + 4);
						recPdu.extendTo(pduSize);

						byte[] restMess = new byte[(pduSize - 4)];
						inFromServer.read(restMess, 0, (pduSize - 4));
						recPdu.setSubrange(4, restMess);

						newTopic = decoder(recPdu.getSubrange(4,
								recPdu.getByte(1)));
						setTopic(newTopic);

						System.out.print("Server: Topic changed to " + topic);
					} else {
						System.out.print("Wrong Op code received from server");
					}
				} else {
					System.out.print("Server closed connection, retry");
					keepGoing = false;
				}
			} catch (NoSuchElementException e) {
				System.err.print("Found no slash zeros");
			}
			catch (SocketTimeoutException e) {
				if (!keepGoing) {
					System.out.print("Socket timed out");
				}
			} catch (SocketException e) {
				if (!keepGoing) {
					System.out.print("Socket closed");
				}
			} catch (IOException e) {
				System.err.print("Connection Closed");
				e.printStackTrace();
			}
		}
		stopConnection();
	}

	/**
	 * Saves the dns address och the nick to a config file
	 *
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void saveSettings() throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("client.cfg", "UTF-8");
		writer.println(DNS);
		writer.println(DNSPort);
		writer.println(nickName);
		writer.close();
	}

	/**
	 * Sends a message packet, takes a string, msgtype and crypt key
	 *
	 * @param message
	 * @param msg
	 * @param cryptKey
	 */
	public void sendMessage(String message, int msg, String cryptKey) {
		if (!keepGoing) {
			System.out.print("Not connected to a chat server");
		} else {
			try {
				outToServer.write(packager.opMessagePacket(message, msg,
						cryptKey));
				outToServer.flush();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends packets
	 *
	 * @param send
	 */
	public void sendPacket(byte[] send) {
		if (!keepGoing) {
			System.out.print("Not connected to a chat server");
		} else {
			try {
				outToServer.write(send);
				outToServer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets Dns Address
	 *
	 * @param s
	 */
	public void setDNS(String s) {
		DNS = s;
	}

	/**
	 * Sets Dns port
	 *
	 * @param s
	 */
	public void setDNSPort(String s) {
		DNSPort = Integer.parseInt(s);
	}

	/**
	 * Sets the crypt key
	 *
	 * @param s
	 */
	public void setEncryptionKey(String s) {
		encryptionKey = s;
	}

	/**
	 * Sets the boolean keepGoing
	 *
	 * @param keepGoing
	 */
	public void setKeepGoing(boolean keepGoing) {
		this.keepGoing = keepGoing;
	}

	/**
	 * Sets nickName
	 *
	 * @param name
	 */
	public void setNick(String name) {
		nickName = name;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	/**
	 * Sets topic
	 *
	 * @param topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * Setter for whoIsNick
	 *
	 * @param whoIsNick
	 */
	public void setWhoIsNick(String whoIsNick) {
		this.whoIsNick = whoIsNick;
	}

	/**
	 * Shuts down program
	 *
	 */
	public void shutDown() {
		stopConnection();
		System.exit(0);
	}

	/**
	 * Shuts down the connection to the server. Sends a quit packet, closes
	 * socket.
	 *
	 */
	public void stopConnection() {
		try {
			outToServer.write(packager.opQuitPacket());
			outToServer.flush();
			keepGoing = false;
			Thread.sleep(1000);
			inFromServer.close();
			outToServer.close();
			tcpSocket.close();
		} catch (IOException e) {
			System.out.print("Couldnt close socket and/or streams...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compress on/off
	 */
	public void switchCompression() {
		if (isCompressed == false) {
			isCompressed = true;
		} else {
			isCompressed = false;
		}
	}

	/**
	 * Crypt on/off
	 */
	public void switchEncryption() {
		if (isEncrypted == false) {
			isEncrypted = true;
		} else {
			isEncrypted = false;
		}
	}
}