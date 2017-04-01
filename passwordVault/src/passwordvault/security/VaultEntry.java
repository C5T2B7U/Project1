/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordvault.security;

/**
 *
 */
public class VaultEntry {
    private final int id = 0; // The identifier that links VaultEntry to KeyStore aliases.
    private Vault vault;
    private String aliasUser;
    private String aliasPassword;
    private String aliasNextEntry;
    private String aliasPrevEntry;
    // Not sure if users need over 2^31 entries.
    
    // TODO: Maybe use in Vault? If not, set private.
    final static String ALIAS_USER = "user";
    final static String ALIAS_PASSWORD = "pass";
    final static String ALIAS_NEXT_ENTRY = "nextEnt";
    final static String ALIAS_PREV_ENTRY = "prevEnt";
    
    /**
     * Make a new VaultEntry.
     * @param vault
     * @param username
     * @param password 
     */
    public VaultEntry(Vault vault, char username[], char password[]) {
        // TODO: set id
        aliasUser = ALIAS_USER + id;
        aliasPassword = ALIAS_PASSWORD + id;
        aliasNextEntry = ALIAS_NEXT_ENTRY + id;
        aliasPrevEntry = ALIAS_PREV_ENTRY + id;
        this.vault = vault;
        
        vault.keyStore.addKey(aliasUser, new String(username));
        vault.keyStore.addKey(aliasPassword, new String(password));
        // TODO: Add next & prev entries...
    }
    /**
     * Get an entry from the Vault.
     * Should only be called by the Vault class.
     */
    VaultEntry(Vault vault, int id) {
        // TODO: check that this entry actually exists...
        aliasUser = ALIAS_USER + id;
        aliasPassword = ALIAS_PASSWORD + id;
        aliasNextEntry = ALIAS_NEXT_ENTRY + id;
        aliasPrevEntry = ALIAS_PREV_ENTRY + id;
        this.vault = vault;
    }
    
    /**
     * Returns the username stored in this entry.
     * @return Username
     */
    public char[] getUsername() {
        return vault.keyStore.getKey(aliasUser);
    }
    /**
     * Returns the password stored in this entry.
     * TODO: Consider encryption
     * @return Password
     */
    public char[] getPassword() {
        return vault.keyStore.getKey(aliasPassword);
    }
    
    // For now, I'm planning on having VaultEntries be a doubly-linked list.
    // TODO: Implement
    public VaultEntry getPreviousEntry() { return null; }
    public VaultEntry getNextEntry() { return null; }
    
    // TO DO: Implement (after main features)
    public void setUsername() { }
    public void setPassword() { }
}
