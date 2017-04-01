/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// The security package is so that Vault & VaultEntry can share methods
// that other classes don't need access to. (implementation details)
package passwordvault.security;

import java.io.File;
import java.security.UnrecoverableKeyException;

/**
 * This class opens a KeyStore from a file. Once opened, it lets people view the
 * usernames/passwords stored inside.
 * 
 * KeyStore can throw a lot of errors. Not sure whether I should just capture them,
 * or re-throw them.
 */
public class Vault {
    // Note to self: Vault shouldn't hold references to VaultEntries.
    // VaultEntries already hold references to the vault.
    //     => circular-reference thing, stopping both from being garbage-collected
    KeyStoreWrapper keyStore; // Can be accessed by VaultEntry
    
    /**
     * Load a vault from a file. If file doesn't exist, an empty vault will be made.
     * Char[] is used to ensure that it won't be cached.
     * @param file File to read from
     * @param password Password the file was encrypted with
     * @throws UnrecoverableKeyException 
     */
    public Vault(File file, char password[]) throws UnrecoverableKeyException {
        keyStore = new KeyStoreWrapper(file, password);
    }
    
    /*
    For now, I'm planning on having VaultEntries be a doubly-linked list.
    To get all the entries: start from the first entry,
    and call VaultEntry.getNextEntryId()...
    */
    
    /**
     * Get the first VaultEntry in the vault.
     * @return First vault entry in the vault.
     */
    public VaultEntry getFirstEntry() { return null; }
    /**
     * Get the last VaultEntry in the vault.
     * @return Last vault entry in the vault.
     */
    public VaultEntry getLastEntry() { return null; }
    /**
     * Get a VaultEntry by its id.
     * If it doesn't exist, returns null.
     * @param id 
     */
    public VaultEntry getEntry(int id) { return null; }
    
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
