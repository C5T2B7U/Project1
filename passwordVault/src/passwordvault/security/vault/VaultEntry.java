/*
 * This class' responsibility is to abstract away the indicies/values inside
 * the KeyStore.
 * It's also the interface the end-user employs to modify the Vault.
 *
 * Sites:
 * VaultEntry.reserveFreeId() inspiration
 *      http://stackoverflow.com/questions/11758440/initialize-final-variable-within-constructor-in-another-method#11758483
 * Overridding equals()
 *      http://stackoverflow.com/a/27609
 */
package passwordvault.security.vault;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class holds one entry in the Vault.
 * Each VaultEntry has a label, username, and password.
 * Remember: vaultEntry1 == vaultEntry2 checks if both objects point to the same address.
 *      To check if both objects are the same VaultEntry, use vaultEntry1.equals(vaultEntry2)
 * 
 * Probably NOT thread-safe. Multiple threads might write to the Vault at once.
 * TODO: thread safety? For now, low priority
 */
public class VaultEntry {
    private final int id; // The identifier that links VaultEntry to KeyStore aliases.
    private Vault vault; // TODO: Make sure vault exists when writing/getting
    // Not to self: Since id is final, alias shouldn't change either
    private final String aliasLabel;
    private final String aliasUser;
    private final String aliasPassword;
    private final String aliasNextEntry;
    private final String aliasPrevEntry;
    // Not sure if users need over 2^31 entries.
    
    private boolean alertChanges = true; // Used to alertListener()
    
    static final int MISSING_ID = -1; // Id to use when something doesn't exist
    static final int STARTING_ID = 0; // First id to use
    
    // These are used as a prefix for alias...
    // so actual alias would be: <alias_...><id>
    private static final String ALIAS_LABEL = "label";
    private static final String ALIAS_USER = "user";
    private static final String ALIAS_PASSWORD = "pass";
    private static final String ALIAS_NEXT_ENTRY = "nextEnt";
    private static final String ALIAS_PREV_ENTRY = "prevEnt";
    
    private static final String NEXT_FREE_ID_ALIAS = "freeId";
    private static final Object LOCK_NEXT_FREE_ID = new Object();
    
    /**
     * Make a new VaultEntry.
     * @param vault
     * @param label
     * @param username
     * @param password
     * @throws NullPointerException If any parameter is null
     */
    public VaultEntry(Vault vault, String label, String username, char password[]) throws NullPointerException {
        this(vault, reserveFreeId(vault));
        // Don't add any keys if a parameter is null
        // vault is checked in the constructor call above
        if (label == null || username == null || password == null)
            throw new NullPointerException();
        
        alertChanges = false;
        setLabel(label);
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
     * WARNING: SHOULD NOT CALL THIS METHOD. THIS IS JUST FOR QUICKLY MAKING THE UI.
     * Makes a new VaultEntry inside the Vault.
     * TODO: Properly set password from the UI
     * @param vault
     * @param label
     * @param username
     * @param password 
     */
    public VaultEntry(Vault vault, String label, String username, String password) {
        this(vault, label, username, password.toCharArray());
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
            int nextFreeId = STARTING_ID;
            try {
                nextFreeId = vault.keyStore.getIdKey(NEXT_FREE_ID_ALIAS);
            } catch (InstanceNotFoundException ex) {
                // TODO: manually search for free id?
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
     * @throws NullPointerException If vault is null
     */
    private VaultEntry(Vault vault, int id) throws NullPointerException {
        if (vault == null)
            throw new NullPointerException();
        this.id = id;
        aliasLabel = ALIAS_LABEL + id;
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
     * Returns the label stored in this entry.
     * @return Label inside this VaultEntry
     */
    public String getLabel() {
        try {
            return new String(vault.keyStore.getKey(aliasLabel));
        } catch (InstanceNotFoundException ex) {
            // Since entry exists, label is expected to exist
            Logger.getLogger(VaultEntry.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR: MISSING LABEL";
        }
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
    /**
     * WARNING: SHOULD NOT CALL THIS METHOD. THIS IS JUST FOR QUICKLY MAKING THE UI.
     * TODO: Properly get password from the UI
     * @return Password, as a string
     */
    public String getPasswordAsStr() {
        return new String(getPassword());
    }
    
    // For now, I'm planning on having VaultEntries be a doubly-linked list.
    /**
     * Returns the previous entry. If it doesn't exist, returns null.
     * Notice: The Vault is arranged as a doubly-linked list.
     * @return Previous entry
     */
    public VaultEntry getPreviousEntry() {
        int prevId = getPreviousEntryId();
        return vault.getEntry(prevId);
    }
    /**
     * Returns the next entry. If it doesn't exist, returns null.
     * Notice: The Vault is arranged as a doubly-linked list.
     * @return Next entry
     */
    public VaultEntry getNextEntry() {
        int nextId = getNextEntryId();
        return vault.getEntry(nextId);
    }
    
    /**
     * Changes the label associated with this entry.
     * @param label 
     * @throws NullPointerException If label is null
     */
    public void setLabel(String label) throws NullPointerException {
        if (label == null)
            throw new NullPointerException();
        vault.keyStore.addKey(aliasLabel, label.toCharArray());
        alertListenerOnChange();
    }
    /**
     * Changes the username associated with this entry.
     * @param username 
     * @throws NullPointerException If username is null
     */
    public void setUsername(String username) throws NullPointerException {
        if (username == null)
            throw new NullPointerException();
        vault.keyStore.addKey(aliasUser, username.toCharArray());
        alertListenerOnChange();
    }
    /**
     * Changes the password associated with this entry.
     * @param password
     * @throws NullPointerException If password is null
     */
    public void setPassword(char password[]) throws NullPointerException {
        if (password == null)
            throw new NullPointerException();
        vault.keyStore.addKey(aliasPassword, password);
        alertListenerOnChange();
    }
    /**
     * WARNING: SHOULD NOT CALL THIS METHOD. THIS IS JUST FOR QUICKLY MAKING THE UI.
     * TODO: Properly set password from the UI
     * @param password 
     * @throws NullPointerException If password is null
     */
    public void setPasswordAsStr(String password) throws NullPointerException {
        setPassword(password.toCharArray());
    }
    
    /**
     * Delete this entry from the Vault.
     * After this has been called, the VaultEntry can no longer be changed.
     */
    public void delete() {
        // Fix vault pointers
        VaultEntry prevEntry = getPreviousEntry();
        VaultEntry nextEntry = getNextEntry();
        if (vault.getFirstEntry().equals(this)) {
            if (nextEntry != null)
                vault.setFirstEntryId(nextEntry.getId());
            else
                vault.setFirstEntryId(MISSING_ID);
        }
        if (vault.getLastEntry().equals(this)) {
            if (prevEntry != null)
                vault.setLastEntryId(prevEntry.getId());
            else
                vault.setLastEntryId(MISSING_ID);
        }
        
        // Fix entry pointers before deleting this key
        if (prevEntry != null)
            prevEntry.setNextEntryId( getNextEntryId() );
        if (nextEntry != null)
             nextEntry.setPreviousEntryId( getPreviousEntryId() );
        
        alertListenerOnRemove();
        vault.keyStore.deleteKey(aliasLabel);
        vault.keyStore.deleteKey(aliasUser);
        vault.keyStore.deleteKey(aliasPassword);
        vault.keyStore.deleteKey(aliasNextEntry);
        vault.keyStore.deleteKey(aliasPrevEntry);
        vault = null; // TODO: test change entry after deleted
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
            vault.keyStore.getKey(entry.aliasLabel);
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
        } catch (InstanceNotFoundException ex) { // Again, shouldn't happen normally
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
    
    // Multiple objects can reference the same entry
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof VaultEntry))
            return false;
        return (id == ((VaultEntry) obj).id);
    }
    // ... and since equals() was overridden, hashCode must be also.
    @Override
    public int hashCode() {
        return id;
    }
    
    // Used to display the entry in a JList.
    @Override
    public String toString() {
        return getLabel();
    }
    
    //**************************/
    
    /**
     * Tell the listener that a VaultEntry has been added to the list.
     */
    void alertListenerOnAdd() {
        if (vault.listener != null && vault.listener.get() != null)
            vault.listener.get().onEntryAdded(this);
    }
    /**
     * Tell the listener that a VaultEntry has been changed.
     */
    void alertListenerOnChange() {
        if (alertChanges && vault.listener != null && vault.listener.get() != null)
            vault.listener.get().onEntryChanged(this);
    }
    /**
     * Tell the listener that a VaultEntry has been removed from the list.
     */
    void alertListenerOnRemove() {
        if (vault.listener != null && vault.listener.get() != null)
            vault.listener.get().onEntryRemoved(this);
    }
}
