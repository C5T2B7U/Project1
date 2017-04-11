/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordvault.security.vault;

import java.io.File;
import java.security.UnrecoverableKeyException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author oKevi
 */
public class VaultTest {
    public static final String TEST_FILENAME = "build\\VaultTest.st";
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
        String expResult = "";
        String result = instance.getKeyFile();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetKeyFile() {
        System.out.println("setKeyFile");
        String keyFile = "";
        Vault instance = null;
        instance.setKeyFile(keyFile);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSave() {
        System.out.println("save");
        Vault instance = null;
        instance.save();
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetFirstEntryId() {
        System.out.println("getFirstEntryId");
        Vault instance = null;
        int expResult = 0;
        int result = instance.getFirstEntryId();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetLastEntryId() {
        System.out.println("getLastEntryId");
        Vault instance = null;
        int expResult = 0;
        int result = instance.getLastEntryId();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetFirstEntryId() {
        System.out.println("setFirstEntryId");
        int id = 0;
        Vault instance = null;
        instance.setFirstEntryId(id);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetLastEntryId() {
        System.out.println("setLastEntryId");
        int id = 0;
        Vault instance = null;
        instance.setLastEntryId(id);
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
            assertTrue("firstEntry == NULL?", vault.getFirstEntry() == null);
            assertTrue("lastEntry == NULL?", vault.getLastEntry() == null);
            
            vault.save();
            assertTrue("vault saved?", TEST_FILE.isFile());
        } catch (UnrecoverableKeyException ex) {
            fail("UnrecoverableKeyException: "+ ex.getMessage());
        }
    }
}
