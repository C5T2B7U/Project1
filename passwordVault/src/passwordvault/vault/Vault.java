/*
 * This class' responsibility is to abstract away the KeyStore from the end-user.
 *
 * Also, the vault package is so that Vault & VaultEntry can share methods
 * that other classes don't need access to. (implementation details)
 ***************************
 *  For now, I'm planning on having VaultEntries be a doubly-linked list.
 *  To get all the entries: start from the first entry,
 *          and call VaultEntry.getNextEntryId()...
 * TODO: Allow user to delete entries
 */
package passwordvault.vault;

import java.io.File;
import java.security.UnrecoverableKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class opens a KeyStore from a file. Once opened, it lets people view the
 * usernames/passwords stored inside.
 * 
 * KeyStore can throw a lot of errors. Not sure whether I should just capture them,
 * or re-throw them.
 */
public class Vault {
    /* Note to self: Vault shouldn't hold references to VaultEntries.
     * VaultEntries already hold references to the vault.
     *     => circular-reference thing, stopping both from being garbage-collected
     */
    KeyStoreWrapper keyStore; // Can be accessed by VaultEntry
    int firstEntryId;
    int lastEntryId;
    
    private static final String FIRST_ENTRY_ALIAS = "first";
    private static final String LAST_ENTRY_ALIAS = "last";
    
    /**
     * Load a vault from a file. If file doesn't exist, an empty vault will be made.
     * Char[] is used to ensure that it won't be cached.
     * @param filename File to read from
     * @param password Password the file was encrypted with
     * @throws UnrecoverableKeyException 
     */
    public Vault(String filename, char password[]) throws UnrecoverableKeyException {
        keyStore = new KeyStoreWrapper(new File(filename), password);
        try {
            firstEntryId = keyStore.getIdKey(FIRST_ENTRY_ALIAS);
        } catch (InstanceNotFoundException ex) { // Set default firstEntryId
            // TODO: Find proper firstEntry? low priority
            firstEntryId = VaultEntry.MISSING_ID;
            keyStore.addIdKey(FIRST_ENTRY_ALIAS, firstEntryId);
        }
        
        try {
            lastEntryId = keyStore.getIdKey(FIRST_ENTRY_ALIAS);
        } catch (InstanceNotFoundException ex) { // Set default lastEntryId
            // TODO: Find proper lastEntry? low priority
            lastEntryId = VaultEntry.MISSING_ID;
            keyStore.addIdKey(LAST_ENTRY_ALIAS, lastEntryId);
        }
        // TODO: lastEntry
    }
    
    /**
     * Get the first VaultEntry in the vault.
     * @return First vault entry in the vault.
     */
    public VaultEntry getFirstEntry() {
        return VaultEntry.getEntry(this, firstEntryId);
    }
    /**
     * Get the last VaultEntry in the vault.
     * TODO: implement this
     * @return Last vault entry in the vault.
     */
    public VaultEntry getLastEntry() { return null; }
    /**
     * Get a VaultEntry by its id.
     * If it doesn't exist, returns null.
     * @param id 
     */
    public VaultEntry getEntry(int id) {
        // I want VaultEntry to manage its aliases, so Vault calls VaultEntry
        return VaultEntry.getEntry(this, id);
    }
    
    // Might be useful for prompting to change keyFiles.
    /**
     * Get the keyFile associated with this vault.
     * If vault doesn't use a keyFile, returns null.
     * @return Key file used for the password to lock/unlock this vault
     */
    public File getKeyFile() { return null; }
    
    // For use with getKeyFile. Might just be a path to the keyFile.
    /**
     * Change the vault's password to use this keyFile.
     * If keyFile is null, the keyFile will be removed.
     * @param keyFile 
     */
    public void setKeyFile(File keyFile) {}
    
    /**
     * Save the vault to a file.
     */
    public void save() {
        keyStore.save();
    }
}
