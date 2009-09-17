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
package cs.cirg.cida.analysis;


import cs.cirg.cida.io.DataTable;
import cs.cirg.cida.io.exception.CIlibIOException;
import cs.cirg.cida.io.transform.SelectiveDataOperator;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.type.types.Numeric;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class ColumnBasedDescriptiveStatistics extends SelectiveDataOperator {

    private List<DescriptiveStatistics> iterationsDescriptiveStatistics;

    @Override
    public DataTable operate(DataTable dataTable) throws CIlibIOException {
        iterationsDescriptiveStatistics = new ArrayList<DescriptiveStatistics>();

        List<Integer> selectedColumns = this.getSelectedItems();
        int size = dataTable.getNumRows();
        for (int rowIndex = 0; rowIndex < size; rowIndex++) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            List<Numeric> row = (List<Numeric>) dataTable.getRow(rowIndex);
            for (Integer i : selectedColumns) {
                stats.addValue(row.get(i).getReal());
            }
            iterationsDescriptiveStatistics.add(stats);
        }
        return dataTable;
    }

    public List<DescriptiveStatistics> getIterationsDescriptiveStatistics() {
        return iterationsDescriptiveStatistics;
    }

    public void setIterationsDescriptiveStatistics(List<DescriptiveStatistics> iterationDescriptiveStatistics) {
        this.iterationsDescriptiveStatistics = iterationDescriptiveStatistics;
    }
}
