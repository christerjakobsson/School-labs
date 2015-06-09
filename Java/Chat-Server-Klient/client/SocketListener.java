import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class SocketListener extends Thread {
	Client c;
	PDU pdu;
	byte[] receiveData;
	DatagramPacket receivePacket;
	Packager packager;

	public SocketListener(Client c) {
		this.c = c;
		packager = new Packager(c);
	}

	@Override
	public void run() {

		try {
			DataInputStream inFromServer = new DataInputStream(c.getTcpSocket()
					.getInputStream());
			//System.out.print("Listening...");
			byte[] receiveData = new byte[4];
			PDU recPdu = new PDU(4);
			inFromServer.readFully(receiveData, 0, 4);
			recPdu.setSubrange(0, receiveData);

			if (recPdu.getByte(0) == OpCodes.MESSAGE) {
				byte[] messLength = new byte[4];
				recPdu.extendTo(8);
				inFromServer.readFully(messLength, 0, 4);
				recPdu.extendTo(12 + recPdu.getByte(2) + recPdu.getShort(4));
				byte[] restMessage = new byte[recPdu.getByte(2)
						+ recPdu.getShort(4)];
				inFromServer.readFully(restMessage, 0, 4 + recPdu.getByte(2)
						+ recPdu.getShort(4));
				String message = new String(recPdu.getSubrange(12,
						recPdu.getShort(4)));
				String nick = new String(recPdu.getSubrange(
						12 + recPdu.getShort(4), recPdu.getByte(2)));

				System.out.print(nick + ": " + message);
			}
			inFromServer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		// pdu.setSubrange(0, receivePacket.getData());
		// if(pdu.getByte(0) == OpCodes.MESSAGE) {
		// System.out.print("Retrieve Message");
		// }
		// else if(pdu.getByte(0) == OpCodes.NICKS) {
		// System.out.print("Retrieving Nicks");
		// }
		// else if(pdu.getByte(0) == OpCodes.UINFO) {
		// System.out.print("Retrieving Nick Info");
		// }
		// else if(pdu.getByte(0) == OpCodes.UJOIN) {
		// System.out.print("Someone Joined");
		// }
		// else if(pdu.getByte(0) == OpCodes.ULEAVE) {
		// System.out.print("Someone Left");
		// }
		// else if(pdu.getByte(0) == OpCodes.CHNICK) {
		// System.out.print("change nick");
		// }
		// else if(pdu.getByte(0) == OpCodes.UCNICK) {
		// System.out.print("Someone changed nickname");
		// }
	}
}
