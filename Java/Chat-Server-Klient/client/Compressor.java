import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compressor {

	Compressor(){

	}

	/**
	 * Takes a sting, compresses it and returns it to a byteArray
	 */
	public static byte[] compressMessage(String mess) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream zipOut = new GZIPOutputStream(outputStream);
        zipOut.write(mess.getBytes());
        zipOut.close();
        return outputStream.toByteArray();
	}

	/**
	 * Takes a byteArrat, decompresses and returns a string
	 */
	public static String decompressMessage(byte[] s) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s);
        GZIPInputStream zipIn = new GZIPInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipIn, "ISO-8859-1"));
        String line;
        String unzippedString = "";
        while ((line = reader.readLine())!= null) {
        	unzippedString = unzippedString + line;
        }
        return unzippedString;
     }
}
