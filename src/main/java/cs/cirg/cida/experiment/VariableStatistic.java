/**
 * Copyright (C) 2009
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
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

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.type.types.Real;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public enum VariableStatistic {

    Mean {
        @Override
        public List<Real> getStatistic(List<DescriptiveStatistics> stats) {
            int size = stats.size();
            List<Real> statList = new ArrayList<Real>(size);
            for (int i = 0; i < size; i++) {
                statList.add(new Real(stats.get(i).getMean()));
            }
            return statList;
        }
    },
    StdDev {
        @Override
        public List<Real> getStatistic(List<DescriptiveStatistics> stats) {
            int size = stats.size();
            List<Real> statList = new ArrayList<Real>(size);
            for (int i = 0; i < size; i++) {
                statList.add(new Real(stats.get(i).getStandardDeviation()));
            }
            return statList;
        }
    };

    public abstract List<Real> getStatistic(List<DescriptiveStatistics> stats);
}
