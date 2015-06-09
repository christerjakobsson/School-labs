import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * This class is extends Thread and is always running and is waiting on incoming
 * tcp connections from chatclientes, when a client connects the welcome socket
 * will accept them and then we creates a CLienteThread that gets the socket it
 * connected to, it also gets a link to the server and a timestamp
 * 
 * @author Christer
 * 
 */
public class Listener extends Thread {

	private Server server;
	Date date = new Date();

	public Listener(Server server) {
		this.server = server;
		setName("Listener");
	}

	/**
	 * Runs as long as the server is on.
	 */
	@Override
	public void run() {

		while (true) {
			ClientThread cl = null;
			if (server.getClient().size() < 255) {

				try {
					ServerSocket welcomeSocket = new ServerSocket(
							Integer.parseInt(server.getInternalPort()));
					Socket connectionSocket = welcomeSocket.accept();
					System.out.print("Connection Established");
					cl = new ClientThread(connectionSocket, server,
							(date.getTime() / 1000));
					welcomeSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				server.getClient().add(cl);
			}
		}
	}
}