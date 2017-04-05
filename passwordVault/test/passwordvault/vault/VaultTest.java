/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordvault.vault;

import passwordvault.vault.VaultEntry;
import passwordvault.vault.Vault;
import java.io.File;
import java.security.UnrecoverableKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author oKevi
 */
public class VaultTest {
    public static final String TEST_FILENAME = "build\\key.st";
    public static final File TEST_FILE = new File(TEST_FILENAME);
    public static final char[] PASSWORD = "pass".toCharArray();
    
    public VaultTest() {
    }

    /*
    @Test
    public void testGetFirstEntry() {
        System.out.println("getFirstEntry");
        Vault instance = null;
        VaultEntry expResult = null;
        VaultEntry result = instance.getFirstEntry();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetLastEntry() {
        System.out.println("getLastEntry");
        Vault instance = null;
        VaultEntry expResult = null;
        VaultEntry result = instance.getLastEntry();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetEntry() {
        System.out.println("getEntry");
        int id = 0;
        Vault instance = null;
        VaultEntry expResult = null;
        VaultEntry result = instance.getEntry(id);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetKeyFile() {
        System.out.println("getKeyFile");
        Vault instance = null;
        File expResult = null;
        File result = instance.getKeyFile();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetKeyFile() {
        System.out.println("setKeyFile");
        File keyFile = null;
        Vault instance = null;
        instance.setKeyFile(keyFile);
        fail("The test case is a prototype.");
    }
    */

    @Test
    public void testSaveEmpty() {
        System.out.println("save empty vault");
        if (TEST_FILE.isFile())
            TEST_FILE.delete();
        try {
            Vault vault = new Vault(TEST_FILENAME, PASSWORD);
            assertTrue("firstEntry == MISSING_ID?", vault.firstEntryId == VaultEntry.MISSING_ID);
            assertTrue("lastEntry == MISSING_ID?", vault.lastEntryId == VaultEntry.MISSING_ID);
            
            vault.save();
            assertTrue("vault saved?", TEST_FILE.isFile());
        } catch (UnrecoverableKeyException ex) {
            fail("UnrecoverableKeyException: "+ ex.getMessage());
        }
    }
}
