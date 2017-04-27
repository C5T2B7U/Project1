/*
 * This class' responsibility is to abstract the KeyStore away from the Vault's code.
 * This way, if KeyStore is changed, only one class needs to be modified.
 ***************************
 * Intended for this vault package's use ONLY.
 *
 * Sites:
 *      http://docs.oracle.com/javase/7/docs/technotes/guides/security/index.html
 *      https://docs.oracle.com/javase/7/docs/api/overview-summary.html
 *
 * Keystore type
 *      http://stackoverflow.com/questions/18243248/java-keystore-setentry-using-an-aes-secretkey#comment26751703_18243248
 * Printing bytes
 *      http://stackoverflow.com/questions/21250330/decryption-of-aes-encrypted-field-in-java#21250773
 */
package passwordvault.security.vault;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
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
    
    private static final String PASSWORD_FAIL_MESSAGE = "Keystore was tampered with, or password was incorrect";
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
     * @throws IOException If file failed to open
     * @throws UnrecoverableKeyException If password was incorrect
     */
    public KeyStoreWrapper(File file, char password[]) throws IOException, UnrecoverableKeyException {
        this.file = file;
        setPassword(password);
        // Make a new KeyStore
        try {
            keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
//            System.out.println("Using KeyStore type: "+ keyStore.getType()); // DEBUG
        } catch (KeyStoreException ex) { // If no implementation. (shouldn't happen)
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Initialize the keyStore
        try {
            try (InputStream istream = new FileInputStream(file)) { // File exists
                try {
                    keyStore.load(istream, password);
                } catch(IOException ioex) {
                    // If password failed...
                    if (ioex.getCause() instanceof UnrecoverableKeyException) {
                        throw (UnrecoverableKeyException) ioex.getCause();
                    // For some reason, this KeyStore doesn't set the cause properly
                    } else if (ioex.getMessage().equals(PASSWORD_FAIL_MESSAGE)) {
                        System.out.println("Used alternate detection: password failed.");
                        throw new UnrecoverableKeyException();
                    } else { // Read fail?
                        throw ioex;
                    }
                }
            } catch(FileNotFoundException ex) { // File doesn't exist
                keyStore.load(null);
            }
        } catch (NoSuchAlgorithmException ex) { // ... Shouldn't happen
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Use same password to protect contents of key store
        contentProtection = new KeyStore.PasswordProtection(password);
    }
    
    /**
     * Add a key to the KeyStore.
     * @param alias Key
     * @param contents Value
     */
    public void addKey(String alias, char[] contents) {
        byte[] salt = "Some salt".getBytes();
        try {
//            SecretKeySpec spec = new SecretKeySpec(contents.getBytes("UTF-8"), KEY_TYPE);
            PBEKeySpec spec = new PBEKeySpec(contents, salt, 1000);
            SecretKey key = keyFactory.generateSecret(spec);
            keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(key), contentProtection);
            
//            // DEBUG
//            System.out.print("\tspec (decoded):");
//            System.out.println(spec.getPassword());
//            System.out.print("\tkey (encoded):");
//            System.out.println(new String(key.getEncoded(), "UTF-8"));
            
        } catch (InvalidKeySpecException ex) { // Failed to make key
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) { // KeyStore not loaded (shouldn't happen)
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeyException ex) { // Key is too short
//            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) { // DEBUG
//            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Wraps the addKey() method for KeyEntry's ids.
     * Use: Remove lots of duplicated calls to convert char[] into ids (integers).
     * @param alias
     * @param id 
     */
    public void addIdKey(String alias, int id) {
        addKey(alias, String.valueOf(id).toCharArray());
    }
    
    /**
     * Get a key from the KeyStore.
     * @param alias Alias of the KeyStore
     * @return The key, if it is found
     * @throws InstanceNotFoundException When key isn't found
     */
    public char[] getKey(String alias) throws InstanceNotFoundException {
        try {
            KeyStore.Entry entry = keyStore.getEntry(alias, contentProtection);
            if (entry == null) // If Entry not found, entry == null...
                throw new InstanceNotFoundException();
            SecretKey key = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
//            SecretKey key = (SecretKey) keyStore.getKey(alias, password);
            PBEKeySpec spec = (PBEKeySpec) keyFactory.getKeySpec(key, PBEKeySpec.class);
            
//            // DEBUG
//            System.out.print("alg:"+key.getAlgorithm()+"__");
//            System.out.print("\tencoded: ");
//            System.out.println(new String(key.getEncoded(), "UTF-8"));
//            System.out.print("\tdecoded > ");
//            System.out.println(spec.getPassword());
//            System.out.println("\tspec class: "+ keyFactory.getKeySpec(key, PBEKeySpec.class).getClass());
            
            return spec.getPassword();
        } catch (KeyStoreException ex) { // Keystore not loaded (shouldn't happen)
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) { // Shouldn't happen
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        // Wrong entry password (shouldn't happen)
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableEntryException ex) {
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) { // Wanted key type doesn't match key
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) { // DEBUG
//            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    /**
     * Wraps getKey() method for KeyEntry's id.
     * Use: Remove lots of duplicated calls to convert char[] into ids (integers).
     * @param alias
     * @return id
     */
    public int getIdKey(String alias) throws InstanceNotFoundException {
        return Integer.parseInt(new String(getKey(alias)));
    }
    
    /**
     * Deletes a key from the key store.
     * If key isn't found, nothing happens.
     * @param alias 
     */
    public void deleteKey(String alias) {
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException ex) { // Keystore not init. (shouldn't happen)
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //*******************************************/
    
    /**
     * Change the KeyStore's password
     * @param newPassword New password
     */
    public void setPassword(char[] newPassword) {
        try {
            if (keyStore != null) { // Only change entries if keyStore already initialized
                KeyStore.ProtectionParameter newContentProtection = new KeyStore.PasswordProtection(newPassword);
                for (Enumeration<String> iter =keyStore.aliases(); iter.hasMoreElements();) {
                    String alias = iter.nextElement();
                    // Overwrite old key's protection
                    try {
                        KeyStore.Entry entry = keyStore.getEntry(alias, contentProtection);
                        keyStore.setEntry(alias, entry, newContentProtection);

                    } catch (NoSuchAlgorithmException ex) { // Shouldn't happen
                        Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnrecoverableEntryException ex) { // Wrong password for entry. (shouldn't happen)
                        Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                contentProtection = newContentProtection;
            }
            
            password = newPassword;
        } catch (KeyStoreException ex) { // Shouldn't happen...
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Changes the location the Vault will be saved at.
     * Doesn't error until the vault is saved.
     * @param newFile 
     */
    public void setFilepath(File newFile) {
        file = newFile;
    }
    
    /**
     * Tries to save the file.
     * @throws IOException If failed to save the file for some reason.
     *      Might be an instance of FileNotFoundException.
     */
    public void save() throws IOException {
        try {
            keyStore.isKeyEntry(""); // Throw KeyStoreException BEFORE trying to save file
            
            try(OutputStream ostream = new FileOutputStream(file)) {
                keyStore.store(ostream, password);
            } catch (NoSuchAlgorithmException ex) { // Shouldn't happen
                Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CertificateException ex) { // Login fail (shouldn't happen)
                Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (KeyStoreException ex) { // KeyStore not loaded (shouldn't happen)
            Logger.getLogger(KeyStoreWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calls save()
     */
    @Override
    public void close() throws IOException {
        if (keyStore != null)
            save();
        keyStore = null;
    }
}
