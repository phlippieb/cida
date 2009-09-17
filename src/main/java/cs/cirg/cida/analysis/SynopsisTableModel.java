package cs.cirg.cida.analysis;

import cs.cirg.cida.experiment.Experiment;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public class SynopsisTableModel extends AbstractTableModel {

    List<Experiment> experiments;
    List<String> variables;

    public SynopsisTableModel() {
        experiments = new ArrayList<Experiment>();
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
            DescriptiveStatistics descriptiveStatistics = experiments.get(rowIndex).getFinalIterationStatistics(variables.get(columnIndex / 2));
            return descriptiveStatistics.getMean();
        }
        DescriptiveStatistics descriptiveStatistics = experiments.get(rowIndex).getFinalIterationStatistics(variables.get(columnIndex / 2 - 1));
        return descriptiveStatistics.getStandardDeviation();
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public List<String> getVariables() {
        return variables;
    }

    public void setVariables(List<String> variables) {
        this.variables = variables;
    }
}
