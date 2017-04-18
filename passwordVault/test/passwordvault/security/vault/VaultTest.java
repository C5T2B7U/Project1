/*
 * Sites:
 * How 2 Unit Test:
 *      http://stackoverflow.com/questions/6694715/junit-testing-private-variables
 */
package passwordvault.security.vault;

import java.io.File;
import java.security.UnrecoverableKeyException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class VaultTest {
    public static final String TEST_FILENAME = "build\\VaultTest.st";
    public static final File TEST_FILE = new File(TEST_FILENAME);
    public static final char[] PASSWORD = "pass".toCharArray();
    
    public static final String TEMP_TEST_FILENAME = "build\\TempVaultTest.st";
    public static final File TEMP_TEST_FILE = new File(TEMP_TEST_FILENAME);
    
    public VaultTest() {
    }
    
    /**
     * Test the Entry's linked list on insertion...
     * so... Vault.getFirstEntry(), Vault.getLastEntry(),
     * VaultEntry.getPreviousEntry(), and VaultEntry.getNextEntry()
     * 
     * Also try getEntryById()
     */
    @Test
    public void testSaveVault() {
        System.out.println("save filled vault");
        makeVault();
    }
    public static Vault makeVault() {
        System.out.println("\tmaking vault");
        if (TEST_FILE.isFile()) // Remake the test vault
            TEST_FILE.delete();
        
        try {
            Vault vault = new Vault(TEST_FILENAME, PASSWORD);
            VaultEntry firstEntry = new VaultEntry(vault, "user1", "pass1".toCharArray());
            VaultEntry vEntry = vault.getFirstEntry();
            assertTrue("vault's firstEntry set?", vault.getFirstEntry().equals(firstEntry));
            assertTrue("vault's lastEntry set?", vault.getLastEntry().equals(firstEntry));
            assertTrue("firstEntry's prev == null?", firstEntry.getPreviousEntry() == null);
            assertTrue("firstEntry's next == null?", firstEntry.getNextEntry() == null);
            // getEntryById...
            int firstId = firstEntry.getId();
            assertTrue("getEntry(missing id) not set?", vault.getEntry(VaultEntry.MISSING_ID) == null);
            assertTrue("getEntry(first) found?", vault.getEntry(firstId).equals(firstEntry));
            assertTrue("getEntry(second) not set?", vault.getEntry(firstId +1) == null);
            
            VaultEntry secondEntry = new VaultEntry(vault, "u2", "2".toCharArray());
            assertTrue("vault's firstEntry kept?", vault.getFirstEntry().equals(firstEntry));
            assertTrue("vault's lastEntry == second?", vault.getLastEntry().equals(secondEntry));
            assertTrue("firstEntry's prev == null?", firstEntry.getPreviousEntry() == null);
            assertTrue("firstEntry's next set?", firstEntry.getNextEntry().equals(secondEntry));
            assertTrue("secondEntry's prev == first?", secondEntry.getPreviousEntry().equals(firstEntry));
            assertTrue("secondEntry's next == null?", secondEntry.getNextEntry() == null);
            // getEntryById...
            int secondId = secondEntry.getId();
            assertTrue("getEntry(missing id) not set?", vault.getEntry(VaultEntry.MISSING_ID) == null);
            assertTrue("getEntry(first) found?", vault.getEntry(firstId).equals(firstEntry));
            assertTrue("getEntry(second) found?", vault.getEntry(secondId).equals(secondEntry));
            assertTrue("getEntry(third) not set?", vault.getEntry(secondId +1) == null);
            
            VaultEntry thirdEntry = new VaultEntry(vault, "the third user", "password 3 SPACE".toCharArray());
            assertTrue("vault's firstEntry kept?", vault.getFirstEntry().equals(firstEntry));
            assertTrue("vault's lastEntry == third?", vault.getLastEntry().equals(thirdEntry));
            assertTrue("firstEntry's prev == null?", firstEntry.getPreviousEntry() == null);
            assertTrue("firstEntry's next kept?", firstEntry.getNextEntry().equals(secondEntry));
            assertTrue("secondEntry's prev kept?", secondEntry.getPreviousEntry().equals(firstEntry));
            assertTrue("secondEntry's next == third?", secondEntry.getNextEntry().equals(thirdEntry));
            assertTrue("thirdEntry's prev == second?", thirdEntry.getPreviousEntry().equals(secondEntry));
            assertTrue("thirdEntry's next == null?", thirdEntry.getNextEntry() == null);
            // getEntryById...
            int thirdId = thirdEntry.getId();
            assertTrue("getEntry(missing id) not set?", vault.getEntry(VaultEntry.MISSING_ID) == null);
            assertTrue("getEntry(first) found?", vault.getEntry(firstId).equals(firstEntry));
            assertTrue("getEntry(second) found?", vault.getEntry(secondId).equals(secondEntry));
            assertTrue("getEntry(third) found?", vault.getEntry(thirdId).equals(thirdEntry));
            assertTrue("getEntry(fourth) not set?", vault.getEntry(thirdId +1) == null);
            
            vault.save();
            assertTrue("vault saved?", TEST_FILE.isFile());
            return vault;
        } catch (UnrecoverableKeyException ex) {
            fail("UnrecoverableKeyException: "+ ex.getMessage());
            return null;
        }
    }
    
    @Test
    public void testDeleteEntries() {
        // Delete first entry
        if (TEST_FILE.isFile()) // Remake the test vault
            TEST_FILE.delete();
        Vault vault = makeVault();
        VaultEntry firstEntry = vault.getFirstEntry();
        VaultEntry secondEntry = firstEntry.getNextEntry();
        VaultEntry thirdEntry = secondEntry.getNextEntry();
        firstEntry.delete();
        assertTrue("vault's firstEntry == second?", vault.getFirstEntry().equals(secondEntry));
        assertTrue("vault's lastEntry == third?", vault.getLastEntry().equals(thirdEntry));
        assertTrue("secondEntry's prev  == null?", secondEntry.getPreviousEntry() == null);
        assertTrue("secondEntry's next kept?", secondEntry.getNextEntry().equals(thirdEntry));
        assertTrue("thirdEntry's prev kept?", thirdEntry.getPreviousEntry().equals(secondEntry));
        assertTrue("thirdEntry's next kept?", thirdEntry.getNextEntry() == null);
        // Then delete second
        secondEntry.delete();
        assertTrue("vault's firstEntry == third?", vault.getFirstEntry().equals(thirdEntry));
        assertTrue("vault's lastEntry == third?", vault.getLastEntry().equals(thirdEntry));
        assertTrue("thirdEntry's prev == null?", thirdEntry.getPreviousEntry() == null);
        assertTrue("thirdEntry's next kept?", thirdEntry.getNextEntry() == null);
        // And delete the third
        assertTrue("vault's firstEntry == null?", vault.getFirstEntry() == null);
        assertTrue("vault's lastEntry == null?", vault.getLastEntry() == null);
        
        // Delete second entry
        TEST_FILE.delete(); // Remake the test vault
        vault = makeVault();
        firstEntry = vault.getFirstEntry();
        secondEntry = firstEntry.getNextEntry();
        thirdEntry = secondEntry.getNextEntry();
        secondEntry.delete();
        assertTrue("vault's firstEntry kept?", vault.getFirstEntry().equals(firstEntry));
        assertTrue("vault's lastEntry kept?", vault.getLastEntry().equals(thirdEntry));
        assertTrue("firstEntry's prev kept?", firstEntry.getPreviousEntry() == null);
        assertTrue("firstEntry's next == third?", firstEntry.getNextEntry().equals(thirdEntry));
        assertTrue("thirdEntry's prev == first?", thirdEntry.getPreviousEntry().equals(firstEntry));
        assertTrue("thirdEntry's next kept?", thirdEntry.getNextEntry() == null);
        // Then delete third entry
        thirdEntry.delete();
        assertTrue("vault's firstEntry kept?", vault.getFirstEntry().equals(firstEntry));
        assertTrue("vault's lastEntry == first?", vault.getLastEntry().equals(firstEntry));
        assertTrue("firstEntry's prev kept?", firstEntry.getPreviousEntry() == null);
        assertTrue("firstEntry's next == null?", firstEntry.getNextEntry() == null);
        
        // Delete third entry
        TEST_FILE.delete(); // Remake the test vault
        vault = makeVault();
        firstEntry = vault.getFirstEntry();
        secondEntry = firstEntry.getNextEntry();
        thirdEntry = secondEntry.getNextEntry();
        thirdEntry.delete();
        assertTrue("vault's firstEntry kept?", vault.getFirstEntry().equals(firstEntry));
        assertTrue("vault's lastEntry == second?", vault.getLastEntry().equals(secondEntry));
        assertTrue("firstEntry's prev == null?", firstEntry.getPreviousEntry() == null);
        assertTrue("firstEntry's next kept?", firstEntry.getNextEntry().equals(secondEntry));
        assertTrue("secondEntry's prev kept?", secondEntry.getPreviousEntry().equals(firstEntry));
        assertTrue("secondEntry's next == null?", secondEntry.getNextEntry() == null);
        
        // ... and keep a clean copy of test vault
        TEST_FILE.delete(); // Remake the test vault
        makeVault();
    }

    /*
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
        if (TEMP_TEST_FILE.isFile())
            TEMP_TEST_FILE.delete();
        try {
            Vault vault = new Vault(TEMP_TEST_FILENAME, PASSWORD);
            assertTrue("firstEntry == NULL?", vault.getFirstEntry() == null);
            assertTrue("lastEntry == NULL?", vault.getLastEntry() == null);
            
            vault.save();
            assertTrue("vault saved?", TEMP_TEST_FILE.isFile());
        } catch (UnrecoverableKeyException ex) {
            fail("UnrecoverableKeyException: "+ ex.getMessage());
        }
    }
}
