/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordvault;

/**
 *
 * @author Brian Sumner
 */
public class Debug {

    // DEBUG MODE:  CHOOSE ONE OF THE FOLLOWING
    static boolean ENABLE_DEBUG = true;
//  static boolean ENABLE_DEBUG = false;

    
    public static void debugMsg(String msg) {
        if (ENABLE_DEBUG)
            System.out.println("DEBUG:  " + msg);
    }
   
    
    
}
