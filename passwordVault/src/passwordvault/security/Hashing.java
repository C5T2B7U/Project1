package passwordvault.security;
import java.security.MessageDigest;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;
import java.io.*;

public class Hashing {

//    public static void main(String[] args) {
//        simpleUI();
//    }
    
    private static void simpleUI(){
        // GET STRING TO HASH
        Scanner sn = new Scanner(System.in);
        System.out.println("Please enter a password to be hashed");
        String password = sn.nextLine();
        System.out.println("Please enter a filename for your file authentication");
        String filename = sn.nextLine();
        //CALL HASHING FUNCTION
        Hashing sj = new Hashing();
        String hash = sj.getStrHash(password);
        String hash2 = sj.getFileHash(filename);
        String temp = hash + hash2;
        String hash3 = sj.getStrHash(temp);
        //PRINT RESULTS
        System.out.println("This is the hash for your string using SHA-256!");
        System.out.println(hash);
        System.out.println("this is the hash for your file using SHA-256!");
        System.out.println(hash2);
        System.out.println("This is the hash for both tokens combined using SHA-256!");
        System.out.println(hash3);
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
}
