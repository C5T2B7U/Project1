package passwordvault.security;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * This class handles the KeyStore object.
 */
class KeyStoreWrapper implements Closeable {
    private KeyStore keyStore;
    private File file;
    private char password[]; // TODO: Encrypt password
    private KeyStore.ProtectionParameter contentProtection;
    
    private static final String KEYSTORE_TYPE = "JCEKS"; // Default type: JKS only holds private keys!
    private static final String KEY_TYPE = "PBE";
    // Used to encrypt/decrypt keys
    private static SecretKeyFactory keyFactory;
    static {
        try {
            keyFactory = SecretKeyFactory.getInstance(KEY_TYPE);
        } catch(NullPointerException | NoSuchAlgorithmException ex) { // No name | Not implemented
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Load a vault from a file. If file doesn't exist, an empty vault will be made.
     * @param file File to load KeyStore from
     * @param password Password used to encrypt KeyStore
     * @throws UnrecoverableKeyException If password was incorrect
     */
    public KeyStoreWrapper(File file, char password[]) throws UnrecoverableKeyException {
        this.file = file;
        this.password = password;
        // Make a new KeyStore
        try {
            keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            System.out.println("Using KeyStore type: "+ keyStore.getType());
        } catch (KeyStoreException ex) { // If no implementation. (shouldn't happen)
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Initialize the keyStore
        try {
            try (InputStream istream = new FileInputStream(file)) { // File exists
                // TODO: Figure out when password is invalid & throw error
                try {
                    keyStore.load(istream, password);
                } catch(IOException ioex) {
                    ioex.printStackTrace();
                }
            } catch(FileNotFoundException ex) { // File doesn't exist
                try {
                    keyStore.load(null);
                } catch(IOException ioex) {
                    ioex.printStackTrace();
                }
            }
        } catch (IOException ex) { // Read fail
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) { // TODO: Handle properly
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Use same password to protect contents of key store
        contentProtection = new KeyStore.PasswordProtection(password);
    }
    
    /**
     * Add a key to the KeyStore.
     * TODO: Convert this into private method.
     * @param alias Key
     * @param contents Value
     */
    public void addKey(String alias, String contents) {
        byte[] salt = "Some salt".getBytes();
        try {
            System.out.println(contents.getBytes("UTF-8").length);
//            SecretKeySpec spec = new SecretKeySpec(contents.getBytes("UTF-8"), KEY_TYPE);
            PBEKeySpec spec = new PBEKeySpec(contents.toCharArray(), salt, 1000);
            SecretKey key = keyFactory.generateSecret(spec);
            keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(key), contentProtection);
            
//            System.out.print("\tinput bytes:");
//            System.out.println(new String(contents.getBytes("UTF-8"), "UTF-8"));
            System.out.print("\tspec (decoded):");
            System.out.println(spec.getPassword());
            System.out.print("\tkey (encoded):");
            System.out.println(new String(key.getEncoded(), "UTF-8"));
            
            PBEKeySpec regenSpec = ((PBEKeySpec) keyFactory.getKeySpec(key, PBEKeySpec.class));
            System.out.print("\tre-decoded:");
            System.out.println(new String(regenSpec.getPassword()));
            
        } catch (InvalidKeySpecException ex) { // Failed to make key
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) { // KeyStore not loaded
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeyException ex) { // Key is too short
//            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public char[] getKey(String alias) {
        try {
            SecretKey key = ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, contentProtection)).getSecretKey();
//            SecretKey key = (SecretKey) keyStore.getKey(alias, password);
            PBEKeySpec spec = (PBEKeySpec) keyFactory.getKeySpec(key, PBEKeySpec.class);
            
            System.out.print("alg:"+key.getAlgorithm()+"__");
            System.out.print("\tencoded: ");
            System.out.println(new String(key.getEncoded(), "UTF-8"));
            System.out.print("\tdecoded > ");
            System.out.println(spec.getPassword());
            System.out.println("\tspec class: "+ keyFactory.getKeySpec(key, PBEKeySpec.class).getClass());
            
            return spec.getPassword();
        } catch (KeyStoreException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableEntryException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void save() {
        try {
            keyStore.isKeyEntry(""); // Throw KeySToreException BEFORE trying to save file
            
            try(OutputStream ostream = new FileOutputStream(file)) {
                keyStore.store(ostream, password);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CertificateException ex) { // Login fail
                Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (KeyStoreException ex) { // KeyStore not loaded
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calls save()
     * @throws IOException 
     */
    @Override
    public void close() {
        if (keyStore != null)
            save();
        keyStore = null;
    }
}
