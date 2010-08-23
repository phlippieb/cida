package cs.cirg.cida.experiment;

import java.util.List;
import net.sourceforge.cilib.type.types.Numeric;

/**
 *
 * @author andrich
 */
public class Util {

    public static List<? extends Numeric> trimList(List<? extends Numeric> list, int targetSize) {
        int size = list.size();
        for (int i = size - 1; i >= targetSize; i--) {
            list.remove(i);
        }
        return list;
    }

}
