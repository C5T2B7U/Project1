package hashing; 

import java.security.MessageDigest;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.charset.*;

public class Hashing {

 /*   public static void main(String[] args) {
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
        //CALL HASHING FUNCTIONM1
        //PRINT RESULTS
        System.out.println("This is the hash for your string using SHA-256!");
        String hash1 = new String();
        hash1 = getCharHash(password1);
        System.out.println(hash1);
        System.out.println("this is the hash for your file using SHA-256!");
        String hash2 = new String();
        hash2 = getFileHash(file1);
        System.out.println(hash2);
        System.out.println("This is the final hash using SHA-256!");
        String hash3 = new String();
        hash3 = hash1 + hash2;
        hash3 = getStrHash(hash3);
        System.out.println(hash3);
    }
 */   
    public static String getCharHash(char[] password){
        String result = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] pwbytes = toBytes(password); 
            byte[] hash = digest.digest(pwbytes);
            Arrays.fill(pwbytes, (byte) 0); // clear sensitive data
            return DatatypeConverter.printHexBinary(hash);
        }catch(Exception ex){
           ex.printStackTrace();
        }
        return result;
    }//*/
    
    public static String getFileHash(String filename){
        String result = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(filename);
            byte[] hash = new byte[1024];
            int x = 0;
            while ((x = fis.read(hash)) != -1){
                digest.update(hash, 0 , x);
            }
            byte[] hash2 = digest.digest();
            return DatatypeConverter.printHexBinary(hash2);
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
    public static String getStrHash(String password){
        String result = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return DatatypeConverter.printHexBinary(hash);
        }catch(Exception ex){
           ex.printStackTrace();
        }
    return result;
    }
}