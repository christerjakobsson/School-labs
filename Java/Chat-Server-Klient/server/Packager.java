import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

/**
 * This helper class creates all the packages with the help from the PDU, this
 * simplifies the sending of packets. every method creates a PDU and returns a
 * byte array.
 *
 * @author Christer
 *
 */
public class Packager {
	Server s;

	/**
	 * Constructor, takes the server as parameter.
	 *
	 * @param server
	 */
	public Packager(Server server) {
		this.s = server;
	}

	public int pduSize(int size) {
		while (size % 4 != 0) {
			size++;
		}
		return size;
	}

	/**
	 * Creates packet to change topic.
	 *
	 * @param newTopic
	 *
	 * @return
	 */
	public byte[] opChangeTopicPacket(byte[] newTopic) {
		System.out.print("Sending Topic");

		int pduSize = pduSize(4 + newTopic.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.CHTOPIC);
		pdu.setByte(1, (byte) newTopic.length);
		pdu.setSubrange(4, newTopic);

		return pdu.getBytes();
	}

	/**
	 * Creates isAlive packet
	 *
	 * @return
	 */
	public byte[] opIsAlivePacket() {
		System.out.print("Sending Alive");

		PDU pdu = new PDU(4);
		pdu.setByte(0, (byte) OpCodes.ALIVE);
		pdu.setByte(1, (byte) s.getClient().size());
		pdu.setShort(2, (byte) s.getId());

		return pdu.getBytes();
	}

	/**
	 * Creates message packet
	 *
	 * @param message
	 * @param nickName
	 * @return
	 */
	public byte[] opMessagePacket(String message, String nickName) {
		System.out.print("Sending Message");
		byte[] mess = s.encoder(message);
		byte[] nick = s.encoder(nickName);

		int nickStart = pduSize(12 + mess.length);

		int pduSize = pduSize(nickStart + nick.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.MESSAGE);
		pdu.setByte(1, (byte) MsgTypes.TEXT); // KRYPTERINGSNYCKEL
		pdu.setByte(2, (byte) nick.length);
		pdu.setByte(3, (byte) 0); // CHECKSUM
		pdu.setShort(4, (byte) mess.length);
		pdu.setInt(8, (int) s.getTime());
		pdu.setSubrange(12, mess);
		pdu.setSubrange(nickStart, nick);

		int offset = nickStart+nick.length;
		for (int i = offset; i < pduSize; i++) {
			pdu.setByte(i, (byte) '\0');
		}


		byte checkSum = Checksum.calc(pdu.getBytes(), pdu.length());
		pdu.setByte(3, checkSum);
		return pdu.getBytes();
	}

	/**
	 * Creates packet containing all nicks as a byte array with '\0' as
	 * separator
	 *
	 * @param str
	 * @return
	 */
	public byte[] opNickPacket(byte[] str) {
		System.out.print("Sending Nicks");

		int pduSize = pduSize(4 + str.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.NICKS);
		pdu.setByte(1, (byte) s.getClient().size());
		pdu.setShort(2, (short) str.length);
		pdu.setSubrange(4, str);

		int offset = 4 + str.length;
		for (int i = offset; i < pduSize; i++) {
			pdu.setByte(i, (byte) '\0');
		}

		return pdu.getBytes();
	}

	/**
	 * Creates a quit packet.
	 *
	 * @return
	 */
	public byte[] opQuitPacket() {
		System.out.print("Sending Quit");

		PDU pdu = new PDU(4);
		pdu.setByte(0, (byte) OpCodes.QUIT);
		return pdu.getBytes();
	}

	/**
	 * Creates register packet, used when registering to the DNS
	 *
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public byte[] opRegPacket() throws MalformedURLException, IOException {
		byte[] topic = s.encoder(s.getTopic());

		int pduSize = pduSize(8 + topic.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.REG);
		pdu.setByte(1, (byte) topic.length);
		pdu.setShort(2, (short) Integer.parseInt(s.getInternalPort()));
		System.out.print(s.getDNS());
		if (s.getDNS().contentEquals("127.0.0.1")) {
			pdu.setInt(4, (int) s.ipToDecimal("127.0.0.1"));
		} else {
			pdu.setInt(4, (int) s.ipToDecimal(s.getIpAddress()));
		}
		pdu.setSubrange(8, topic);

		return pdu.getBytes();
	}

	/**
	 * Creates packet f�r when a user changes nick.
	 *
	 * @param oldNick
	 * @param newNick
	 * @param time
	 * @return
	 */
	public byte[] opUCNickPacket(byte[] oldNick, byte[] newNick, int time) {
		System.out.print("Sending UCNICK");

		int pduSize = pduSize(8 + oldNick.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.UCNICK);
		pdu.setByte(1, (byte) oldNick.length);
		pdu.setByte(2, (byte) newNick.length);
		pdu.setInt(4, time);
		pdu.setSubrange(8, oldNick);

		int newPduSize = pduSize(pduSize + newNick.length);
		pdu.extendTo(newPduSize);
		pdu.setSubrange(pduSize, newNick);

		int offset = pduSize+newNick.length;
		for (int i = offset; i < pduSize; i++) {
			pdu.setByte(i, (byte) '\0');
		}

		return pdu.getBytes();
	}

	/**
	 * Creates a packet with information about a specific user.
	 *
	 * @param nick
	 * @return
	 */
	public byte[] opUInfoPacket(byte[] nick) {
		System.out.print("Sending UINFO");
		int index = 0;

		PDU pdu = new PDU(12);
		for (int i = 0; i < s.getClient().size(); i++) {
			if (Arrays.equals(nick, s.getClient().get(i).getNickName())) {
				index = i;
			}
		}

		int ip = (int) s.ipToDecimal(s.getClient().get(index).getSocket()
				.getInetAddress().getHostAddress());
		int timeStamp = (int) s.getClient().get(index).getTimeStamp();

		pdu.setByte(0, (byte) OpCodes.UINFO);
		pdu.setInt(4, ip);
		pdu.setInt(8, timeStamp);

		return pdu.getBytes();
	}

	/**
	 * Creates a packet for when a user joins the server, the packet gets sent
	 * to all other users
	 *
	 * @param nick
	 * @return
	 */
	public byte[] opUJoin(byte[] nick) {
		System.out.print("Sending UJOIN");

		int pduSize = pduSize(8 + nick.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.UJOIN);
		pdu.setByte(1, (byte) nick.length);
		pdu.setInt(4, (int) s.getTime());
		pdu.setSubrange(8, nick);


		int offset = 8 + nick.length;
		for (int i = offset; i < pduSize; i++) {
			pdu.setByte(i, (byte) '\0');
		}

		return pdu.getBytes();
	}

	/**
	 * Creates a packet for when a user leaves the server, the packet gets sent
	 * to all other users.
	 *
	 * @param bs
	 * @param timeStamp
	 * @return
	 */
	public byte[] opULeave(byte[] nick, long timeStamp) {
		int pduSize = pduSize(8 + nick.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.ULEAVE);
		pdu.setByte(1, (byte) nick.length);
		pdu.setInt(4, (int) timeStamp);
		pdu.setSubrange(8, nick);

		int offset = 8 + nick.length;
		for (int i = offset; i < pduSize; i++) {
			pdu.setByte(i, (byte) '\0');
		}

		return pdu.getBytes();
	}
}
