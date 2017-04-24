/*
 * This class' responsibility is to change the Vault interface to a ListModel.
 * Swing should use this class to display the Vault.
 *
 * Warning: Class hasn't been tested!
 * TODO: test???
 *
 * Sites:
 * ListModel
 *      https://docs.oracle.com/javase/tutorial/uiswing/components/list.html
 * Weak References
 *      https://docs.oracle.com/javase/7/docs/api/java/lang/ref/WeakReference.html
 */
package passwordvault.security.vault;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;
import passwordvault.security.vault.Vault.VaultListener;

/**
 * This class acts as an interface, so that Swing can access the Vault.
 */
public class VaultListModel extends AbstractListModel implements VaultListener {
    ArrayList<VaultEntry> entries;
    
    /**
     * Make a new VaultListModel. This allows the Swing UI to interface with 
     * the Vault.
     * @param vault Vault to wrap with a ListModel
     */
    public VaultListModel(Vault vault) {
        entries = new ArrayList<>();
        
        // Get all entries that are already inside the vault
        for (VaultEntry entry = vault.getFirstEntry(); entry != null; entry = entry.getNextEntry())
            entries.add(entry);
        vault.setListener(this); // Listen to changes within the vault
    }

    @Override
    public int getSize() {
        return entries.size();
    }

    @Override
    public Object getElementAt(int index) {
        return entries.get(index);
    }

    //*********************/
    
    @Override
    public void onEntryAdded(VaultEntry entry) {
        entries.add(entry);
        this.fireIntervalAdded(this, entries.size() -1, entries.size() -1);
    }

    @Override
    public void onEntryChanged(VaultEntry entry) {
        for (int index = 0; index < entries.size(); ++index) {
            if (entries.get(index).equals(entry)) {
                this.fireContentsChanged(this, index, index);
//                System.out.println("detected change in index "+ index +", aka Entry: "+ e);
                break;
            }
        }
    }

    @Override
    public void onEntryRemoved(VaultEntry entry) {
        for (int index = 0; index < entries.size(); ++index) {
            if (entries.get(index).equals(entry)) {
                entries.remove(index);
                this.fireIntervalRemoved(this, index, index);
                break;
            }
        }
    }
    
}
