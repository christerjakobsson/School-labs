import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		// Skapar klient
		Client client = new Client();

		// Startar gui't
		ChatGUI chatgui = new ChatGUI(client);
		
	}
}
