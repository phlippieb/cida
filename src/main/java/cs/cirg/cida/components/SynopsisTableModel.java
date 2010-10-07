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

import cs.cirg.cida.experiment.IExperiment;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.rank.Median;

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
        return variables.size() * 3 + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "";
        }
        if (column % 3 == 1) {
            return variables.get((column-1) / 3) + " Mean";
        }
        if (column % 3 == 2) {
            return variables.get((column-1) / 3) + " Median";
        }
        return variables.get((column-1) / 3) + " StdDev";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return experiments.get(rowIndex).getName();
        }
        if (columnIndex % 3 == 1) {
            DescriptiveStatistics descriptiveStatistics = experiments.get(rowIndex).getBottomRowStatistics(variables.get((columnIndex-1) / 3));
            return descriptiveStatistics.getMean();
        }
        if (columnIndex % 3 == 2) {
            DescriptiveStatistics descriptiveStatistics = experiments.get(rowIndex).getBottomRowStatistics(variables.get((columnIndex-1) / 3));
            return descriptiveStatistics.apply(new Median());
        }
        DescriptiveStatistics descriptiveStatistics = experiments.get(rowIndex).getBottomRowStatistics(variables.get((columnIndex-1) / 3));
        return descriptiveStatistics.getStandardDeviation();
    }

    public List<IExperiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<IExperiment> experiments) {
        this.experiments = experiments;
    }

    public boolean addVariable(String variableName) {
        if (!this.getVariables().contains(variableName)) {
            this.getVariables().add(variableName);
            return true;
        }
        return false;
    }

    public boolean addExperiment(IExperiment experiment) {
        if (!this.getExperiments().contains(experiment)) {
            this.getExperiments().add(experiment);
            return true;
        }
        return false;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }
}
