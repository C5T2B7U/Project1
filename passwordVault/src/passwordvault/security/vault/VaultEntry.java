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
    private Vault vault; // TODO: Make sure vault exists when writing/getting
    // Not to self: Since id is final, alias shouldn't change either
    private final String aliasUser;
    private final String aliasPassword;
    private final String aliasNextEntry;
    private final String aliasPrevEntry;
    // Not sure if users need over 2^31 entries.
    
    private boolean alertChanges = true; // Used to alertListener()
    
    static final int MISSING_ID = -1; // Id to use when something doesn't exist
    
    // These are used as a prefix for alias...
    // so actual alias would be: <alias_...><id>
    private static final String ALIAS_USER = "user";
    private static final String ALIAS_PASSWORD = "pass";
    private static final String ALIAS_NEXT_ENTRY = "nextEnt";
    private static final String ALIAS_PREV_ENTRY = "prevEnt";
    
    private static final String NEXT_FREE_ID_ALIAS = "freeId";
    private static final Object LOCK_NEXT_FREE_ID = new Object();
    
    /**
     * Make a new VaultEntry.
     * @param vault
     * @param username
     * @param password 
     */
    public VaultEntry(Vault vault, String username, char password[]) {
        this(vault, reserveFreeId(vault));
        alertChanges = false;
        setUsername(username);
        setPassword(password);
        
        setPreviousEntryId(vault.getLastEntryId());
        setNextEntryId(MISSING_ID);
        
        // Update references
        if (vault.getFirstEntry() == null)
            vault.setFirstEntryId(id);
        VaultEntry prevEntry = vault.getLastEntry();
        if (prevEntry != null)
            prevEntry.setNextEntryId(id);
        vault.setLastEntryId(id); // Just added entry, so last id = this
        
        alertListenerOnAdd();
        alertChanges = true;
    }
    /**
     * Reserve the next open id for use. The id returned will not be used by
     * other VaultEntries.
     * @return Id that can be used for this VaultEntry
     */
    private static int reserveFreeId(Vault vault) {
        // nextId is shared between all members of VaultEntry...
        synchronized(LOCK_NEXT_FREE_ID) {
            // Each vault may have a different free id, so this value is also stored in vault
            int nextFreeId = MISSING_ID;
            try {
                nextFreeId = vault.keyStore.getIdKey(NEXT_FREE_ID_ALIAS);
            } catch (InstanceNotFoundException ex) {
                // TODO: manually search for free id?
                Logger.getLogger(VaultEntry.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Increment free id
            vault.keyStore.addIdKey(NEXT_FREE_ID_ALIAS, nextFreeId+1);
            // TODO: handle full vault & int overflow
            return nextFreeId;
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
            // Since entry exists, password is expected to exist
            Logger.getLogger(VaultEntry.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR: MISSING PASSWORD".toCharArray();
        }
    }
    
    // For now, I'm planning on having VaultEntries be a doubly-linked list.
    // TODO: Test
    public VaultEntry getPreviousEntry() {
        int prevId = getPreviousEntryId();
        return vault.getEntry(prevId);
    }
    public VaultEntry getNextEntry() {
        int nextId = getNextEntryId();
        return vault.getEntry(nextId);
    }
    
    /**
     * Changes the username associated with this entry.
     * @param username 
     */
    public void setUsername(String username) {
        vault.keyStore.addKey(aliasUser, username.toCharArray());
        alertListenerOnChange();
    }
    /**
     * Changes the password associated with this entry.
     * @param password 
     */
    public void setPassword(char password[]) {
        vault.keyStore.addKey(aliasPassword, password);
        alertListenerOnChange();
    }
    
    /**
     * Delete this entry from the Vault.
     * After this has been called, the VaultEntry can no longer be changed.
     *     TODO: remove ability to change VaultEntry after deleted
     */
    public void delete() {
        vault.keyStore.deleteKey(aliasUser);
        vault.keyStore.deleteKey(aliasPassword);
        
        // Fix pointers before deleting this key
        VaultEntry prevEntry = getPreviousEntry();
        if (prevEntry != null)
            prevEntry.setNextEntryId( getNextEntryId() );
        VaultEntry nextEntry = getNextEntry();
        if (nextEntry != null)
             nextEntry.setPreviousEntryId( getPreviousEntryId() );
        vault.keyStore.deleteKey(aliasNextEntry);
        vault.keyStore.deleteKey(aliasPrevEntry);
        
        alertListenerOnRemove();
        vault = null;
    }
    
    //**************************/
    
    /**
     * Find the VaultEntry by its id.
     * If none found, returns null.
     * @param vault Vault to search
     * @param id Id of the VaultEntry to look for
     * @return VaultEntry found
     */
    static VaultEntry getEntry(Vault vault, int id) {
        if (id == VaultEntry.MISSING_ID)
            return null;
        VaultEntry entry = new VaultEntry(vault, id);
        try {
            vault.keyStore.getKey(entry.aliasUser);
            vault.keyStore.getKey(entry.aliasPassword);
            vault.keyStore.getKey(entry.aliasPrevEntry);
            vault.keyStore.getKey(entry.aliasNextEntry);
            return entry;
        // If a key wasn't found, this entry doesn't actually exist
        } catch (InstanceNotFoundException ex) {
            return null;
        }
    }
    
    int getPreviousEntryId() {
        try {
            return vault.keyStore.getIdKey(aliasPrevEntry);
        } catch (InstanceNotFoundException ex) { // Shouldn't happen normally...
            return MISSING_ID;
        }
    }
    int getNextEntryId() {
        try {
            return vault.keyStore.getIdKey(aliasNextEntry);
        } catch (InstanceNotFoundException ex) {
            return MISSING_ID;
        }
    }
    
    void setPreviousEntryId(int id) {
        vault.keyStore.addIdKey(aliasPrevEntry, id);
    }
    void setNextEntryId(int id) {
        vault.keyStore.addIdKey(aliasNextEntry, id);
    }
    
    //**************************/
    
    /**
     * Tell the listener that a VaultEntry has been added to the list.
     */
    void alertListenerOnAdd() {
        if (vault.listener != null && vault.listener.get() != null)
            vault.listener.get().onKeyAdded(this);
    }
    /**
     * Tell the listener that a VaultEntry has been changed.
     */
    void alertListenerOnChange() {
        if (alertChanges && vault.listener != null && vault.listener.get() != null)
            vault.listener.get().onKeyChanged(this);
    }
    /**
     * Tell the listener that a VaultEntry has been removed from the list.
     */
    void alertListenerOnRemove() {
        if (vault.listener != null && vault.listener.get() != null)
            vault.listener.get().onKeyRemoved(this);
    }
}
