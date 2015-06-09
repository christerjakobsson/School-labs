import java.io.IOException;
import java.net.*;

/**
 * This class extends Thread, this makes it possible for it to run parallell to
 * the main thread. this class has the responsibility to send a message to the
 * DNS server that the chatserver is still alive this message gets sent in a 30
 * second interval.
 *
 * @author Christer
 *
 */
public class IsAlive extends Thread {

	int DNSPort;
	String DNS;
	Server server;
	private int timeOut;
	private boolean allGood = false;

	/**
	 * Creates the object and gets the variables the class need to be able to
	 * send the message
	 *
	 *
	 * @param server
	 */
	IsAlive(Server server) {
		this.server = server;
		this.DNS = server.getDNS();
		this.DNSPort = Integer.parseInt(server.getDNSPort());
		this.timeOut = server.getTimeOut();
		setName("isAlive");
	}

	/**
	 * @return boolean allGood.
	 */
	public boolean getAllGood() {
		return allGood;
	}

	/**
	 * This method runs as long as the isnt shutdown, it sleeps 30 seconds then it sends a messge and repeats this.
	 */
	public void run() {

		int tries = 0;
		try {
			PDU pdu = new PDU(4);
			byte[] sendData = new byte[4];
			byte[] receiveData = new byte[4];

			DatagramPacket sendPacket;
			DatagramPacket receivePacket;

			while (true) {
				Thread.sleep(30000);
				DatagramSocket socket = new DatagramSocket();
				socket.setSoTimeout(timeOut);
				pdu.setByte(0, (byte) OpCodes.ALIVE);
				pdu.setByte(1, (byte) server.getClient().size());
				pdu.setShort(2, (short) server.getId());

				sendData = pdu.getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName(DNS), DNSPort);

				sendData = pdu.getBytes();
				sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName(DNS), DNSPort);
				socket.send(sendPacket);
				receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				socket.receive(receivePacket);
				pdu.setSubrange(0, receivePacket.getData());

				if (pdu.getByte(0) == OpCodes.ACK) {
					allGood = true;
				} else if (allGood == false && tries < 5) {
					System.out.print("Server not registered, retrying");
					Thread.sleep(2000);
					tries++;
					server.connectDNS();
				} else if (tries >= 5) {
					System.out
							.print("Could not connect to DNS, Wrong address or port?");
					socket.close();
				}

				socket.close();
			}
		} catch (SocketException e) {
			System.out.print("Connection to DNS lost!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
