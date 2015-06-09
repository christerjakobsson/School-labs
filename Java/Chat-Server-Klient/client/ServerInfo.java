import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

public class ServerInfo {
	private String ip;
	private int port;
	private String topic;
	private int nrOfClients;
	PDU pdu;

	/**
	 * Class that contains information about a server
	 * @param pdu
	 * @throws UnknownHostException
	 */
	ServerInfo(PDU pdu) throws UnknownHostException {
		this.pdu = pdu;
		this.nrOfClients = pdu.getByte(6);
		this.topic = decoder(pdu.getSubrange(8, pdu.getByte(7)));
		this.port = pdu.getShort(4);
		this.ip = convertIpToString();
	}

	public String decoder(byte[] b) {
		String s = null;
		try {
			s = new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}


	/**
	 * Converts a ip address to a string.
	 *
	 * @return
	 */
	private String convertIpToString() {
		return "" + pdu.getByte(0) + "." + pdu.getByte(1) + "."
				+ pdu.getByte(2) + "." + pdu.getByte(3);
	}

	/**
	 * Gets ip
	 *
	 * @return
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Gets nrOfClients
	 *
	 * @return
	 */
	public int getNrOfClients() {
		return nrOfClients;
	}

	/**
	 * Gets port
	 *
	 * @return
	 */
	public String getPort() {
		return Integer.toString(port);
	}

	/**
	 * Gets topic
	 *
	 * @return
	 */
	public String getTopic() {
		return topic;
	}
}
