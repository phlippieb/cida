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

import cs.cirg.cida.io.StandardDataTable;
import cs.cirg.cida.io.exception.CIlibIOException;
import java.util.List;
import net.sourceforge.cilib.type.types.Numeric;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class DescriptiveStatsCalculator {

    private StandardDataTable<Numeric> dataTable;
    private List<Integer> selectedColumns;
    private ColumnBasedDescriptiveStatistics operator;

    public DescriptiveStatsCalculator(StandardDataTable<Numeric> dataTable, List<Integer> selectedColumns) {
        this.dataTable = dataTable;
        this.selectedColumns = selectedColumns;
        this.operator = new ColumnBasedDescriptiveStatistics();
    }

    public void calculate() {
        try {
            operator.setSelectedItems(selectedColumns);
            operator.operate(dataTable);
        } catch (CIlibIOException ex) {
            ex.printStackTrace();
        }
    }

    public List<Integer> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(List<Integer> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public StandardDataTable<Numeric> getDataTable() {
        return dataTable;
    }

    public void setDataTable(StandardDataTable<Numeric> dataTable) {
        this.dataTable = dataTable;
    }

    public List<DescriptiveStatistics> getIterationsDescriptiveStatistics() {
        return operator.getIterationsDescriptiveStatistics();
    }

    public void setIterationsDescriptiveStatistics(List<DescriptiveStatistics> iterationDescriptiveStatistics) {
        this.operator.setIterationsDescriptiveStatistics(iterationDescriptiveStatistics);
    }
}
