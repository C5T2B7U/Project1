package passwordvault.security; 

import java.security.MessageDigest;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.charset.*;
import java.lang.*;

public class Hashing {

    public static void main(String[] args) {
        simpleUI();
    }
    
    private static void simpleUI(){
        // GET STRING TO HASH
        Scanner sn = new Scanner(System.in);
        System.out.println("Please enter a password to be hashed");
        char[] password1 = new char[1024];
        password1 = sn.next().toCharArray();
        System.out.println("Please enter a filename for your file authentication");
        String file1 = new String();
        file1 = sn.next();
        char[] temp = getHash(password1, file1);
        System.out.println(temp);
    }
    //----------------------------------------------------------------------------------
    // getHash FUNCTION
    // Precondition: char[] password is passed in, and String filename is passed in
    // Postcondition: Hashes the 2 items 
    // Returns: char[] containing the hash
    // Comments: Just pass in password and filename to get a char[] hash.
    //----------------------------------------------------------------------------------
    public static char[] getHash(char[] password, String filename){
        char[] rtrn = new char[50];
        byte[] hash1 = getCharHash(password);
        byte[] hash2 = getFileHash(filename);
        byte[] hash3 = new byte[64];
        //if filename == null... password concated to itself. 
        //concatenate the 2 hashes and hash it again
        System.arraycopy(hash1, 0, hash3, 0, hash1.length);
        System.arraycopy(hash2, 0, hash3, hash1.length, hash2.length);
        Arrays.fill(password, '\u0000'); // clear sensitive data
        Arrays.fill(hash1, (byte) 0); // clear sensitive data
        Arrays.fill(hash2, (byte) 0); // clear sensitive data
        try{
        //Convert the hash to a char[] and return it. 
        char rtrnHash[] = DatatypeConverter.printHexBinary(hash3).toCharArray();
        Arrays.fill(hash3, (byte) 0); // clear sensitive data\
        return rtrnHash;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return rtrn;
    }
        //OVERLOADED getHash
        public static char[] getHash(char[] password){
        char[] rtrn = new char[50];
        byte[] hash1 = getCharHash(password);
        byte[] hash3 = new byte[64];
        //if filename == null... password concated to itself. 
        //concatenate the 2 hashes and hash it again
        System.arraycopy(hash1, 0, hash3, 0, hash1.length);
        System.arraycopy(hash1, 0, hash3, hash1.length, hash1.length);
        Arrays.fill(password, '\u0000'); // clear sensitive data
        Arrays.fill(hash1, (byte) 0); // clear sensitive data
        try{
        //Convert the hash to a char[] and return it. 
        char rtrnHash[] = DatatypeConverter.printHexBinary(hash3).toCharArray();
        Arrays.fill(hash3, (byte) 0); // clear sensitive data
        return rtrnHash;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return rtrn;
    }
    public static byte[] getCharHash(char[] password){
        byte[] result = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] pwbytes = toBytes(password); 
            byte[] hash = digest.digest(pwbytes);
            Arrays.fill(pwbytes, (byte) 0); // clear sensitive data
            return hash;
        }catch(Exception ex){
           ex.printStackTrace();
        }
        return result;
    }//*/
    
    public static byte[] getFileHash(String filename){
        byte[] result = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(filename);
            byte[] hash = new byte[32];
            int x = 0;
            while ((x = fis.read(hash)) != -1){
                digest.update(hash, 0 , x);
            }
            byte[] hash2 = digest.digest();
            return hash2;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
    public static byte[] toBytes(char[] chars) {
    CharBuffer charBuffer = CharBuffer.wrap(chars);
    ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
    byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
            byteBuffer.position(), byteBuffer.limit());
    Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
    Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
    return bytes;
}

}
