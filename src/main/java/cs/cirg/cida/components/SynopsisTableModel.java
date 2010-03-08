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

package cs.cirg.cida.components;

import cs.cirg.cida.experiment.DataTableExperiment;
import cs.cirg.cida.experiment.IExperiment;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public class SynopsisTableModel extends AbstractTableModel {

    List<IExperiment> experiments;
    List<String> variables;

    public SynopsisTableModel() {
        experiments = new ArrayList<IExperiment>();
        variables = new ArrayList<String>();
    }

    public SynopsisTableModel(SynopsisTableModel orig) {
        experiments = orig.experiments;
        variables = orig.variables;
    }

    public int getRowCount() {
        return experiments.size();
    }

    public int getColumnCount() {
        return variables.size()*2 + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        }
        if (column % 2 != 0) {
            return variables.get(column / 2) + " Mean";
        }
        return variables.get(column / 2 - 1) + " StdDev";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return experiments.get(rowIndex).getName();
        }
        if (columnIndex % 2 != 0) {
            DescriptiveStatistics descriptiveStatistics = experiments.get(rowIndex).getBottomRowStatistics(variables.get(columnIndex / 2));
            return descriptiveStatistics.getMean();
        }
        DescriptiveStatistics descriptiveStatistics = experiments.get(rowIndex).getBottomRowStatistics(variables.get(columnIndex / 2 - 1));
        return descriptiveStatistics.getStandardDeviation();
    }

    public List<IExperiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<IExperiment> experiments) {
        this.experiments = experiments;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }
}
