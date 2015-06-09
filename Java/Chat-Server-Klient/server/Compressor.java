import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Jonas
 *
 */
public class Compressor {

	Compressor(){

	}

	/**
	 * Compresses a string and returns a array
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static byte[] compressMessage(String s) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream zipOut = new GZIPOutputStream(outputStream);
        zipOut.write(s.getBytes());
        zipOut.close();
        return outputStream.toByteArray();
	}

	/**
	 * Decompresses a byte array and returns it as a string
	 * @param s
	 * @return
	 * @throws IOException
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
