/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.cirg.cida.components;

import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author andrich
 */
public class SelectionListener implements ListSelectionListener {

    private List<Integer> selection;

    public SelectionListener(List<Integer> wrappedList) {
        selection = wrappedList;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        boolean isAdjusting = e.getValueIsAdjusting();
        if (!isAdjusting) {
            selection.clear();
            if (!lsm.isSelectionEmpty()) {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        selection.add(i);
                    }
                }
            }
        }
    }
}
