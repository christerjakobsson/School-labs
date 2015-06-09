import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server, this class is the main class in the server part. It handles the
 * connection to the DNS server , keep tracks of all the connected clients that
 * is in an arrayList (clientList). Keep tracks of variables and is the center
 * of the server structure.
 *
 *
 * @author Christer
 *
 */
public class Server {

	private Packager packager = new Packager(this);

	private CopyOnWriteArrayList<ClientThread> clientList = new CopyOnWriteArrayList<ClientThread>();
	private int internalPort;
	private int id;
	private int DNSPort;
	private int timeOut = 4000;
	private String topic;
	private String DNS;

	private IsAlive alive;

	/**
	 * Creates the object and loads configuration from the config file, if there
	 * exists no config file the variables sets to standard values
	 *
	 *
	 * @throws IOException
	 */
	public Server() throws IOException {
		loadSettings();
	}

	/**
	 * Returns the time in seconds since 1970
	 *
	 * @return
	 */
	public long getTime() {
		Date date = new Date();
		return (date.getTime() / 1000);
	}

	/**
	 * Tries to connect and register on the DNS server. if it went well it
	 * starts a Thread (alive) that vill keep the server online on the DNS, it
	 * also saves the id it gets back from the DNS
	 *
	 * @throws IOException
	 */
	public void connectDNS() throws IOException {
		DatagramSocket connectSocket = new DatagramSocket();
		connectSocket.setSoTimeout(timeOut);
		byte[] sendData = packager.opRegPacket();
		byte[] receiveData = new byte[4];
		DatagramPacket sendPacket = new DatagramPacket(sendData,
				sendData.length, InetAddress.getByName(DNS), DNSPort);

		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);

		connectSocket.send(sendPacket);
		connectSocket.receive(receivePacket);

		PDU recPdu = new PDU(receivePacket.getLength());

		recPdu.setSubrange(0, receivePacket.getData());

		if (recPdu.getByte(0) == OpCodes.ACK) {
			System.out.print("Server registered succesfully");
			id = recPdu.getShort(2);
			alive = new IsAlive(this);
			alive.start();
		}
		connectSocket.close();
	}

	/**
	 * Sends a packet to all connected clientes, it gets packaged with help from
	 * the PDU and gets sent as a byte array
	 *
	 * @param sendData
	 */
	public void echoMessage(byte[] sendData) {
		for (int i = 0; i < clientList.size(); i++) {
			clientList.get(i).sender(sendData);
		}

	}

	/**
	 * Compares if a new nickname is identical to any user already connected if
	 * so adds a suffix at the end of the nickname.
	 *
	 * @param name
	 * @return
	 */
	public byte[] formatNick(byte[] nameByte) {
		System.out.print("Is " + "["+decoder(nameByte)+"]" + " a unique nickname?");
		int suffix = 1;
		byte[] tempByte = nameByte;

		int i = 0;
		while (i < clientList.size()) {
			if (Arrays.equals(tempByte, clientList.get(i).getNickName())) {
				byte[] suffixByte = null;
				try {
					suffixByte = Integer.toString(suffix).getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				int totalLength = nameByte.length + suffixByte.length;
				tempByte = new byte[totalLength];

				int k;
				for (k = 0; k < nameByte.length; k++) {
					tempByte[k] = nameByte[k];
				}
				for (int j = 0; j < suffixByte.length; j++) {
					tempByte[k] = suffixByte[j];
					k++;
				}

				suffix++;
				i = 0;
			}
			i++;
		}
		if (nameByte.equals(tempByte)) {
			System.out.print("It is. " + "["+decoder(nameByte)+"]" + " is "
					+ decoder(nameByte));
		} else {
			System.out.print("It is not. " + decoder(nameByte)
					+ " new name is " + decoder(tempByte));
		}
		nameByte = null;
		nameByte = tempByte;
		System.out.print("Nick changed to: " + decoder(nameByte));
		return nameByte;
	}

	/**
	 * @return clientList.
	 */
	public CopyOnWriteArrayList<ClientThread> getClient() {
		return clientList;
	}

	/**
	 * @return DNS
	 */
	public String getDNS() {
		return DNS;
	}

	/**
	 * @return DNSPort as a String.
	 */
	public String getDNSPort() {
		return Integer.toString(DNSPort);
	}

	/**
	 * @return id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return internalPort as String.
	 */
	public String getInternalPort() {
		return Integer.toString(internalPort);
	}

	/**
	 * Makes use of the website http://api.externalip.net/ip/ to find out what
	 * the external ip that the server has, used to send the correct ip address
	 * to the DNS
	 *
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getIpAddress() throws MalformedURLException, IOException {
		URL myIP = new URL("http://api.externalip.net/ip/");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				myIP.openStream()));
		return in.readLine();
	}

	/**
	 * @return timeOut.
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * @return topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Converts a ip number to a string in decimal form
	 *
	 * @param ip
	 * @return
	 */
	public long ipToDecimal(String ip) {

		String[] addrArray = ip.split("\\.");

		long ipDecimal = 0;

		for (int i = 0; i < addrArray.length; i++) {

			int power = 3 - i;
			ipDecimal += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256,
					power)));
		}
		return ipDecimal;
	}

	/**
	 * Load settings
	 *
	 * @throws IOException
	 */
	private void loadSettings() throws IOException {
		File file = new File("server.cfg");

		// Checks if the file exists, if not it gets created with standard
		// values
		if (!file.exists()) {
			PrintWriter writer = new PrintWriter("server.cfg", "UTF-8");
			writer.println("127.0.0.1");
			writer.println(1337);
			writer.println("TEST");
			writer.println(8888);
			writer.close();
		}

		BufferedReader fileRead = new BufferedReader(new FileReader(file));

		// Load settings
		DNS = fileRead.readLine();
		DNSPort = Integer.parseInt(fileRead.readLine());
		topic = fileRead.readLine();
		internalPort = Integer.parseInt(fileRead.readLine());
		fileRead.close();
	}

	/**
	 * Creats a byte array with all connected users nicknames and the param
	 * nickName is the first one in the list
	 *
	 * @param ownName
	 * @return
	 */
	public byte[] putTogetherNicks(byte[] ownName) {

		int totalLength = 0;
		byte[] noll = encoder("\0");
		PDU pdu = new PDU(ownName.length + noll.length);

		for (int i = 0; i < clientList.size(); i++) {
			if (!(clientList.get(i).getNickName() == ownName)) {
				totalLength += clientList.get(i).getNickName().length;
				totalLength += noll.length;
			} else if (clientList.get(i).getNickName() == ownName) {

				pdu.setSubrange(0, ownName);
				pdu.setSubrange(ownName.length, noll);
			}
		}

		int k = pdu.length();
		int pduSize = packager.pduSize(pdu.length() + totalLength);

		pdu.extendTo(pduSize);

		for (int i = 0; i < clientList.size(); i++) {
			if (!(clientList.get(i).getNickName() == ownName)) {
				pdu.setSubrange(k, clientList.get(i).getNickName());
				k += clientList.get(i).getNickName().length;
				pdu.setSubrange(k, noll);
				k += noll.length;
			}
		}
		return pdu.getBytes();
	}

	/**
	 * Removes a clientThread object from the list
	 *
	 * @param c
	 */
	public void removeClient(ClientThread c) {
		for (int i = 0; i < clientList.size(); i++) {
			if (clientList.get(i).getNickName() == c.getNickName()) {
				clientList.remove(i);
			}
		}
	}

	/**
	 * Converts a string to a byte array that is utf 8 format
	 *
	 * @param s
	 * @return
	 */
	public byte[] encoder(String s) {
		byte[] encode = null;
		try {
			encode = s.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encode;
	}

	/**
	 * Converts a utf 8 byte array to a string that supports utf 8
	 *
	 * @param b
	 * @return
	 */
	public String decoder(byte[] b) {
		String s = null;
		try {
			s = new String(b, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Iterates through clientList and changes the name for one user
	 *
	 * @param oldNick
	 * @param newNick
	 */
	public void replaceUserNick(byte[] oldNick, byte[] newNick) {

		for (int i = 0; i < clientList.size(); i++) {
			if (oldNick == clientList.get(i).getNickName()) {
				clientList.get(i).setNickName(newNick);
				i = clientList.size() + 1;
			}
		}
	}

	/**
	 * Saves settings in config file
	 *
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void saveSettings() throws FileNotFoundException,
			UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("server.cfg", "UTF-8");
		writer.println(DNS);
		writer.println(DNSPort);
		writer.println(topic);
		writer.println(internalPort);
		writer.close();
	}

	/**
	 * Sends a packet to the DNS and with the new topic
	 *
	 * @param newTop
	 */
	public void sendTopicToDns(byte[] packet) {

		PDU pdu = new PDU(packet, packet.length);
		pdu.setShort(2, (short) getId());
		byte[] sendData = new byte[pdu.length()];
		sendData = pdu.getBytes();
		try {
			DatagramSocket socket = new DatagramSocket();

			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, InetAddress.getByName(DNS), DNSPort);

			System.out.print("Sending topic Change to DNS");
			socket.send(sendPacket);
			socket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setter for DNS.
	 *
	 * @param s
	 */
	public void setDNS(String s) {
		DNS = s;
	}

	/**
	 * Setter for DNSPort.
	 *
	 * @param s
	 */
	public void setDNSPort(String s) {
		DNSPort = Integer.parseInt(s);
	}

	/**
	 * Setter for id.
	 *
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Setter for internalPort as string.
	 *
	 * @param s
	 */
	public void setInternalPort(String s) {
		internalPort = Integer.parseInt(s);
	}

	/**
	 * Setter for topic.
	 *
	 * @param text
	 */
	public void setTopic(String text) {
		topic = text;
	}

	/**
	 * Closes the program
	 */
	public void shutDown() {
		ShutDownAllConnections();
		System.exit(0);
	}

	/**
	 * CLoses all the connections to every client.

	 */
	public void ShutDownAllConnections() {
			echoMessage(packager.opMessagePacket(
					"Server is shutting down, May the force be with you.",
					"Server"));
			echoMessage(packager.opQuitPacket());

			for (int i = 0; i < clientList.size(); i++) {
				clientList.get(i).setKeepGoing(false);
			}
	}
}