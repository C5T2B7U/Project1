/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordvault.security.vault;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class runs various tests on VaultEntry.
 * These tests only impact one VaultEntry.
 */
public class VaultEntryTest {
    
    public VaultEntryTest() {
    }

    /**
     * This test checks to see that NullPointerExceptions are thrown when
     * params are null...
     */
    @Test
    public void testEntryTestNullParams() {
        Vault vault = VaultTest.remakeVault();
        String label = "label";
        String user = "user";
        String pass = "pass";
        
        // Null vault
        try {
            new VaultEntry(null, label, user, pass);
            fail("Didn't catch null Vault");
        } catch(NullPointerException ex) { }
        // Null label
        try {
            new VaultEntry(vault, null, user, pass);
            fail("Didn't catch null label");
        } catch(NullPointerException ex) { }
        // Null username
        try {
            new VaultEntry(vault, label, null, pass);
            fail("Didn't catch null username");
        } catch(NullPointerException ex) { }
        // Null pass
        try {
            char ch[] = null;
            new VaultEntry(vault, label, user, ch);
            fail("Didn't catch null password");
        } catch(NullPointerException ex) { }
        try {
            String str = null;
            new VaultEntry(vault, label, user, str);
            fail("Didn't catch null password (string)");
        } catch(NullPointerException ex) { }
    }
    
    /**
     * Try all the set_ methods with null.
     * They should throw NullPointerExceptions.
     */
    @Test
    public void testSetNull() {
        Vault vault = VaultTest.remakeVault();
        VaultEntry entry = vault.getFirstEntry();
        // Label
        try {
            entry.setLabel(null);
            fail("Didn't catch setLabel(null)");
        } catch(NullPointerException ex) { }
        // Username
        try {
            entry.setUsername(null);
            fail("Didn't catch setUsername(null)");
        } catch(NullPointerException ex) { }
        // Password
        try {
            entry.setPassword(null);
            fail("Didn't catch setPassword(null)");
        } catch(NullPointerException ex) { }
        try {
            entry.setPasswordAsStr(null);
            fail("Didn't catch setPasswordAsStr(null)");
        } catch(NullPointerException ex) { }
    }
    
    /*
    @Test
    public void testGetUsername() {
        System.out.println("getUsername");
        VaultEntry instance = null;
        String expResult = "";
        String result = instance.getUsername();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetPassword() {
        System.out.println("getPassword");
        VaultEntry instance = null;
        char[] expResult = null;
        char[] result = instance.getPassword();
        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetPreviousEntry() {
        System.out.println("getPreviousEntry");
        VaultEntry instance = null;
        VaultEntry expResult = null;
        VaultEntry result = instance.getPreviousEntry();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetNextEntry() {
        System.out.println("getNextEntry");
        VaultEntry instance = null;
        VaultEntry expResult = null;
        VaultEntry result = instance.getNextEntry();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetUsername() {
        System.out.println("setUsername");
        String username = "";
        VaultEntry instance = null;
        instance.setUsername(username);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetPassword() {
        System.out.println("setPassword");
        char[] password = null;
        VaultEntry instance = null;
        instance.setPassword(password);
        fail("The test case is a prototype.");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");
        VaultEntry instance = null;
        instance.delete();
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetEntry() {
        System.out.println("getEntry");
        Vault vault = null;
        int id = 0;
        VaultEntry expResult = null;
        VaultEntry result = VaultEntry.getEntry(vault, id);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetPreviousEntryId() {
        System.out.println("getPreviousEntryId");
        VaultEntry instance = null;
        int expResult = 0;
        int result = instance.getPreviousEntryId();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetNextEntryId() {
        System.out.println("getNextEntryId");
        VaultEntry instance = null;
        int expResult = 0;
        int result = instance.getNextEntryId();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetPreviousEntryId() {
        System.out.println("setPreviousEntryId");
        int id = 0;
        VaultEntry instance = null;
        instance.setPreviousEntryId(id);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetNextEntryId() {
        System.out.println("setNextEntryId");
        int id = 0;
        VaultEntry instance = null;
        instance.setNextEntryId(id);
        fail("The test case is a prototype.");
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        VaultEntry instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        VaultEntry instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testAlertListenerOnAdd() {
        System.out.println("alertListenerOnAdd");
        VaultEntry instance = null;
        instance.alertListenerOnAdd();
        fail("The test case is a prototype.");
    }

    @Test
    public void testAlertListenerOnChange() {
        System.out.println("alertListenerOnChange");
        VaultEntry instance = null;
        instance.alertListenerOnChange();
        fail("The test case is a prototype.");
    }

    @Test
    public void testAlertListenerOnRemove() {
        System.out.println("alertListenerOnRemove");
        VaultEntry instance = null;
        instance.alertListenerOnRemove();
        fail("The test case is a prototype.");
    }
    */
}
