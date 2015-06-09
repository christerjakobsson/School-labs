/* Crypt.java
 * Given fil till laboration 2 - Distribuerad Chat på kursen Datakommunikation
 * och Datornät C, 5p vid Umeå Universitet ht 2001 och vt 2002.
 * Av Per Nordlinder (per@cs.umu.se) och Jon Hollström (jon@cs.umu.se)
 */

public class Crypt {

   /* Namn:       {en, de}crypt 
    * Purpose:    Krypterar eller dekrypterar data 
    * Argument:   src - Buffert med datan som ska behandlas 
    *             srclen - Längden i bytes på src 
    *             key - Krypteringsnyckel som skall användas
    *             keylen - Längden i bytes på krypteringsnyckeln
    * Returnerar: Ingenting 
    */ 
    
   public static void encrypt(byte[] src, int srclen, byte[] key, int keylen) {
      
      for(int i=0; i<srclen; i++) 
         src[i] ^= key[i%keylen]; 
   }
   
  	public static void decrypt(byte[] src, int srclen, byte[] key, int keylen) {
      encrypt(src, srclen, key, keylen);
  	}
}