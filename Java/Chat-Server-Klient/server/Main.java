import java.io.IOException;

/**
 * Skapar ett server och ett serverGUI objekt. notera att serverGUI tar en
 * server som parameter, d�rmed s� vet GUIt allt den beh�ver veta f�r att kunna
 * g�ra det den beh�ver-
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
