import java.io.IOException;

/**
 * Skapar ett server och ett serverGUI objekt. notera att serverGUI tar en
 * server som parameter, därmed så vet GUIt allt den behöver veta för att kunna
 * göra det den behöver-
 * 
 * @author Christer
 * 
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		ServerGUI serverGUI = new ServerGUI(server);
	}
}
