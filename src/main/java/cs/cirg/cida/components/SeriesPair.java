/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.cirg.cida.components;

import net.sourceforge.cilib.container.Pair;

/**
 *
 * @author andrich
 */
public class SeriesPair extends Pair<Integer, String> {

    public SeriesPair() {
    }

    public SeriesPair(Integer index, String name) {
        this.setKey(index);
        this.setValue(name);
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
