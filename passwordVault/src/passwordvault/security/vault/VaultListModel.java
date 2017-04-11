/*
 * This class' responsibility is to change the Vault interface to a ListModel.
 * Swing should use this class to display the Vault.
 *
 * Warning: Class hasn't been tested!
 * TODO: test???
 */
package passwordvault.security.vault;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;
import passwordvault.security.vault.Vault.VaultListener;

/**
 *
 * @author oKevi
 */
public class VaultListModel extends AbstractListModel implements VaultListener {
    ArrayList<ListDataListener> listeners;
    ArrayList<VaultEntry> entries;
    
    public VaultListModel(Vault vault) {
        listeners = new ArrayList<>();
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

    @Override
    public void addListDataListener(ListDataListener l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    //*********************/
    
    @Override
    public void onKeyAdded(VaultEntry entry) {
        entries.add(entry);
        this.fireIntervalAdded(this, entries.size() -1, entries.size() -1);
    }

    @Override
    public void onKeyChanged(VaultEntry entry) {
        for (int index = 0; index < entries.size(); ++index) {
            // Can't just use the entry, since multiple objects can point to the same aliases
            if (entries.get(index).getId() == entry.getId())
                this.fireContentsChanged(this, index, index);
        }
    }

    @Override
    public void onKeyRemoved(VaultEntry entry) {
        for (int index = 0; index < entries.size(); ++index) {
            // Can't just use the entry, since multiple objects can point to the same aliases
            if (entries.get(index).getId() == entry.getId()) {
                this.fireIntervalRemoved(this, index, index);
                entries.remove(index);
            }
        }
    }
    
}
