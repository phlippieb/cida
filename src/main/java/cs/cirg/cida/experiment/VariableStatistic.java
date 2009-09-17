/**
 * Copyright (C) 2009
 * Andrich van Wyk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cs.cirg.cida.experiment;

import java.util.List;
import net.sourceforge.cilib.type.types.Real;

/**
 *
 * @author andrich
 */
public class VariableStatistic {

    public static enum Statistic {
        MEAN, STDDEV, NORMMEAN
    }

    public String variable;
    public Statistic stat;
    public List<Real> values;

    public static String makeDescriptor(String variableName, Statistic statistic) {
        return variableName + "." + statistic;
    }

    public String getDescriptor() {
        return makeDescriptor(variable, stat);
    }

}
