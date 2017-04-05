/*
 * This class' responsibility is to abstract away the indicies/values inside
 * the KeyStore.
 * It's also the interface the end-user employs to modify the Vault.
 */
package passwordvault.security.vault;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class holds one entry in the Vault.
 * Each VaultEntry has a username and a password.
 * 
 * Probably NOT thread-safe. Multiple threads might write to the Vault at once.
 * TODO: thread safety? For now, low priority
 */
public class VaultEntry {
    private final int id; // The identifier that links VaultEntry to KeyStore aliases.
    private final Vault vault; // TODO: Make sure vault exists when writing/getting
    // Not to self: Since id is final, alias shouldn't change either
    private final String aliasUser;
    private final String aliasPassword;
    private final String aliasNextEntry;
    private final String aliasPrevEntry;
    // Not sure if users need over 2^31 entries.
    
    static final int MISSING_ID = -1; // Id to use when something doesn't exist
    
    private static final String ALIAS_USER = "user";
    private static final String ALIAS_PASSWORD = "pass";
    private static final String ALIAS_NEXT_ENTRY = "nextEnt";
    private static final String ALIAS_PREV_ENTRY = "prevEnt";
    
    private static int nextFreeId = 0;
    private static final Object LOCK_NEXT_FREE_ID = new Object();
    
    /**
     * Make a new VaultEntry.
     * @param vault
     * @param username
     * @param password 
     */
    public VaultEntry(Vault vault, String username, char password[]) {
        this(vault, reserveFreeId());
        vault.keyStore.addKey(aliasUser, username.toCharArray());
        vault.keyStore.addKey(aliasPassword, password);
        
        // TODO: properly set firstEntryId & lastEntryId
        vault.firstEntryId = this.id;
        // TODO: Add next & prev entries...
    }
    /**
     * Reserve the next open id for use. The id returned will not be used by
     * other VaultEntries.
     * @return Id that can be used for this VaultEntry
     */
    static private int reserveFreeId() {
        // nextId is shared between all members of VaultEntry...
        synchronized(LOCK_NEXT_FREE_ID) {
            return nextFreeId++;
        }
    }
    /**
     * Only for private use. Sets up aliases based on this id.
     * @param vault
     * @param id 
     */
    private VaultEntry(Vault vault, int id) {
        this.id = id;
        aliasUser = ALIAS_USER + id;
        aliasPassword = ALIAS_PASSWORD + id;
        aliasNextEntry = ALIAS_NEXT_ENTRY + id;
        aliasPrevEntry = ALIAS_PREV_ENTRY + id;
        this.vault = vault;
    }
    
    /**
     * Find the VaultEntry by its id.
     * If none found, returns null.
     * @param vault Vault to search
     * @param id Id of the VaultEntry to look for
     * @return VaultEntry found
     */
    static VaultEntry getEntry(Vault vault, int id) {
        // TODO: Check that entry exists
        return new VaultEntry(vault, id);
    }
    
    /**
     * Returns the identifier used to locate this VaultEntry.
     * @return Identifier for this VaultEntry
     */
    public int getId() {
        return id;
    }
    /**
     * Returns the username stored in this entry.
     * @return Username inside this VaultEntry
     */
    public String getUsername() {
        try {
            return new String(vault.keyStore.getKey(aliasUser));
        } catch (InstanceNotFoundException ex) {
            // Since entry exists, username is expected to exist
            Logger.getLogger(VaultEntry.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR: MISSING USERNAME";
        }
    }
    /**
     * Returns the password stored in this entry.
     * TODO: Consider encryption
     * @return Password inside this VaultEntry
     */
    public char[] getPassword() {
        try {
            return vault.keyStore.getKey(aliasPassword);
        } catch (InstanceNotFoundException ex) {
            // Since entry exists, username is expected to exist
            Logger.getLogger(VaultEntry.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR: MISSING PASSWORD".toCharArray();
        }
    }
    
    // For now, I'm planning on having VaultEntries be a doubly-linked list.
    // TODO: Implement
    public VaultEntry getPreviousEntry() {
        try {
            int prevId = vault.keyStore.getIdKey(aliasPrevEntry);
            return vault.getEntry(prevId);
        } catch (InstanceNotFoundException ex) {
            // Since entry exists, entry ptr is expected to exist
            Logger.getLogger(VaultEntry.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public VaultEntry getNextEntry() {
        try {
            int nextId = vault.keyStore.getIdKey(aliasNextEntry);
            return vault.getEntry(nextId);
        } catch (InstanceNotFoundException ex) {
            // Since entry exists, entry ptr is expected to exist
            Logger.getLogger(VaultEntry.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Delete this entry from the Vault.
     * After this has been called, the VaultEntry can no longer be changed.
     *     TODO: implement
     *     TODO: remove ability to change VaultEntry after deleted
     */
    public void delete() {
        
    }
    
    // TO DO: Implement (after main features)
    public void setUsername(String username) { }
    public void setPassword(char password[]) { }
}
