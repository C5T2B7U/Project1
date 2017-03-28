/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordvault;

import static passwordvault.Debug.debugMsg;

/**
 *
 * @author LAPTOP
 */
public class PasswordVault {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // NOTE: THIS IS NO LONGER THE DEFAULT CLASS IN THE CONFIGURATION
        // NOTE: RUNNING PROGRAM RUNS MAIN FROM PasswordVaultUI CLASS

        // THEREFORE THIS WILL NOT RUN UNLESS CALLED
        
        // OUTPUT MESSAGE TO CONSOLE
        System.out.println("Hello World");
        
        // USE Debug CLASS TO OUTPUT MESSAGE TO CONSOLE IF DEBUG ENABLED
        debugMsg("Test");
        
        // NO IMPORT FOR Debug CLASS NEEDED FOR THIS FORM:
        Debug.debugMsg("Also test");
    }
    
}
