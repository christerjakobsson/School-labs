import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Packager {
	Client c;

	/**
	 * Takes care of making a packet depending on which op to send
	 *
	 * @param c
	 */
	public Packager(Client c) {
		this.c = c;
	}

	/**
	 * Creates a changeTopic packet
	 *
	 * @param topic
	 * @return
	 */
	public byte[] opChangeTopicPacket(String topic) {
		byte[] tp = null;
		try {
			tp = topic.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		int pduSize = pduSize(4 + tp.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.CHTOPIC);
		pdu.setByte(1, (byte) tp.length);
		pdu.setSubrange(4, tp);

		return pdu.getBytes();
	}

	/**
	 * Creates a packet for nick change
	 *
	 * @param newNick
	 * @return
	 */
	public byte[] opChNick(String newNick) {
		byte[] nick = null;
		try {
			nick = newNick.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		int pduSize = pduSize(4 + nick.length);

		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.CHNICK);
		pdu.setByte(1, (byte) nick.length);
		pdu.setSubrange(4, nick);

		int offset = 4 + nick.length;
		for (int i = offset; i < pduSize; i++) {
			pdu.setByte(i, (byte) '\0');
		}

		return pdu.getBytes();
	}

	/**
	 * Compresses the message and packages it.
	 *
	 * @param message
	 * @return
	 */
	private PDU opCompMess(String message) {
		byte[] mess = c.encoder(message);

		byte[] compMess = null;
		try {
			compMess = Compressor.compressMessage(c.decoder(mess));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int pduSize = pduSize(8 + compMess.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) 0);
		pdu.setByte(1, (byte) 0);
		pdu.setShort(2, (short) compMess.length);
		pdu.setShort(4, (short) mess.length);
		pdu.setSubrange(8, compMess);

		byte checkSum = Checksum.calc(pdu.getBytes(), pdu.length());
		pdu.setByte(1, checkSum);
		return pdu;
	}

	/**
	 * Crypts a message and compresses it and packages it.
	 *
	 * @param compPdu
	 * @param cryptKey
	 * @return
	 */
	public PDU opCryptMess(PDU compPdu, String cryptKey) {
		byte[] src = compPdu.getBytes();
		int srclen = src.length;
		byte[] key = cryptKey.getBytes();
		int keylen = key.length;

		Crypt.encrypt(src, srclen, key, keylen);

		int pduSize = pduSize(8 + srclen);

		PDU pdu = new PDU(pduSize);
		pdu.setByte(0, (byte) 0);
		pdu.setByte(1, (byte) 0); // Checksumma
		pdu.setShort(2, (short) src.length);
		pdu.setShort(4, (short) compPdu.length());
		pdu.setSubrange(8, src);

		byte checkSum = Checksum.calc(pdu.getBytes(), pdu.length());
		pdu.setByte(1, checkSum);

		return pdu;
	}

	/**
	 * Crypts a message and packages it in a PDU.
	 *
	 * @param mess
	 * @param cryptKey
	 * @return
	 */
	public PDU opCryptMess(String mess, String cryptKey) {
		byte[] src = c.encoder(mess);
		int srclen = src.length;
		byte[] key = cryptKey.getBytes();
		int keylen = key.length;

		int pduSize = pduSize(8 + srclen);

		Crypt.encrypt(src, srclen, key, keylen);

		PDU pdu = new PDU(pduSize);
		pdu.setByte(0, (byte) 0);
		pdu.setByte(1, (byte) 0);
		pdu.setShort(2, (short) src.length);
		pdu.setShort(4, (short) mess.length());
		pdu.setSubrange(8, src);

		byte checkSum = Checksum.calc(pdu.getBytes(), pdu.length());
		pdu.setByte(1, checkSum);
		return pdu;
	}

	/**
	 * Creates Getlist packet.
	 * @return
	 */
	public byte[] opGetListPacket() {
		PDU pdu = new PDU(4);
		pdu.setByte(0, (byte) OpCodes.GETLIST);
		return pdu.getBytes();
	}

	/**
	 * Creates join packet
	 * byteArray
	 *
	 * @param nick
	 * @return
	 */
	public byte[] opJoin(String nickName) {
		byte[] nick = c.encoder(nickName);
		int pduSize = pduSize(4 + nick.length);


		PDU pdu = new PDU(pduSize);
		pdu.setByte(0, (byte) OpCodes.JOIN);
		pdu.setByte(1, (byte) nick.length);
		pdu.setSubrange(4, nick);

		int offset = 4 + nick.length;
		for (int i = offset; i < pduSize; i++) {
			pdu.setByte(i, (byte) '\0');
		}

		return pdu.getBytes();
	}

	/**
	 * Packages the message depending on what kind of message it is.
	 *
	 * @param message
	 * @param msg
	 * @param cryptKey
	 * @return
	 */
	public byte[] opMessagePacket(String message, int msg, String cryptKey) {
		byte[] mess = c.encoder(message);

		PDU pdu = null;

		if (msg == MsgTypes.TEXT) {
			int pduSize = pduSize(12 + mess.length);
			pdu = new PDU(pduSize);

			pdu.setByte(0, (byte) OpCodes.MESSAGE);
			pdu.setByte(1, (byte) MsgTypes.TEXT); // KRYPTERINGSNYCKE
			pdu.setByte(2, (byte) 0); // Nick
			pdu.setByte(3, (byte) 0); // CHECKSUMMA
			pdu.setShort(4, (short) mess.length);
			pdu.setInt(8, 0);
			pdu.setSubrange(12, mess);
		}
		else if (msg == MsgTypes.COMPCRYPT) {
			PDU compPdu = opCompMess(message);
			PDU cryptPdu = opCryptMess(compPdu, cryptKey);

			int pduSize = pduSize(12 + cryptPdu.length());
			pdu = new PDU(pduSize);

			pdu.setByte(0, (byte) OpCodes.MESSAGE);
			pdu.setByte(1, (byte) MsgTypes.COMPCRYPT); // KRYPTERINGSNYCKE
			pdu.setByte(2, (byte) 0);
			pdu.setByte(3, (byte) 0); // CHECKSUMMA
			pdu.setShort(4, (short) cryptPdu.length());
			pdu.setInt(8, (int) 0);
			pdu.setSubrange(12, cryptPdu.getBytes());

		}
		else if (msg == MsgTypes.CRYPT) {
			PDU cryptPdu = opCryptMess(message, cryptKey);
			int pduSize = pduSize(12 + cryptPdu.length());
			pdu = new PDU(pduSize);

			pdu.setByte(0, (byte) OpCodes.MESSAGE);
			pdu.setByte(1, (byte) MsgTypes.CRYPT);
			pdu.setByte(2, (byte) 0);
			pdu.setByte(3, (byte) 0);
			pdu.setShort(4, (short) cryptPdu.length());
			pdu.setInt(8, (int) 0);
			pdu.setSubrange(12, cryptPdu.getBytes());
		}
		else if (msg == MsgTypes.COMP) {
			PDU compPdu = opCompMess(message);
			int pduSize = pduSize(12 + compPdu.length());
			pdu = new PDU(pduSize);

			pdu.setByte(0, (byte) OpCodes.MESSAGE);
			pdu.setByte(1, (byte) MsgTypes.COMP);
			pdu.setByte(2, (byte) 0);
			pdu.setByte(3, (byte) 0);
			pdu.setShort(4, (short) compPdu.length());
			pdu.setInt(8, (int) 0);
			pdu.setSubrange(12, compPdu.getBytes());
		}
		byte checksum = Checksum.calc(pdu.getBytes(), pdu.length());
		pdu.setByte(3, checksum);
		return pdu.getBytes();
	}

	/**
	 * Creates quit packet.
	 * @return
	 */
	public byte[] opQuitPacket() {
		System.out.print("Sending Quit");

		PDU pdu = new PDU(4);
		pdu.setByte(0, (byte) OpCodes.QUIT);
		return pdu.getBytes();
	}

	/**
	 * Creates whois packet
	 * @param nick
	 * @return
	 */
	public byte[] opWhoIs(String nick) {
		byte[] nickName = null;
		try {
			nickName = nick.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		int pduSize = pduSize(4 + nickName.length);
		PDU pdu = new PDU(pduSize);

		pdu.setByte(0, (byte) OpCodes.WHOIS);
		pdu.setByte(1, (byte) nickName.length);
		pdu.setSubrange(4, nickName);

		return pdu.getBytes();
	}

	public int pduSize(int size) {
		while (size % 4 != 0) {
			size++;
		}
		return size;
	}

}