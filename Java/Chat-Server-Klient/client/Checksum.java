/* Checksum.java
 * Given fil till laboration 2 - Distribuerad Chat på kursen Datakommunikation
 * och Datornät C, 5p vid Umeå Universitet ht 2001 och vt 2002
 * Av Per Nordlinder (per@cs.umu.se) och Jon Hollström (jon@cs.umu.se)
 */

public class Checksum {

  /* Namn: calc
   * Syfte: Beräknar checksumma på en byte-array.
   * Argument: buf   - Datat som checksumman skall beräknas på.
   *           count - Det antal bytes som checksumman skall beräknas på.
   * Returnerar: checksumman som en byte.
   */
   public static byte calc(byte[] buf, int count) {
      int sum = 0;
      int i = 0;

      while((count--) != 0) {
         sum += (buf[i] & 0x000000FF);
         i++;
         if((sum & 0x00000100) != 0) {
            sum &= 0x000000FF;
            sum++;
         }     
      }

      return (byte)~(sum & 0xFF);
   }
}