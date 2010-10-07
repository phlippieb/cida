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

import cs.cirg.cida.CIDAConstants;
import cs.cirg.cida.CIDAView;
import cs.cirg.cida.components.IOBridgeTableModel;
import cs.cirg.cida.components.SynopsisTableModel;
import cs.cirg.cida.exception.CIDAException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTable;
import net.sourceforge.cilib.io.DataTable;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.type.types.Numeric;

/**
 *
 * @author andrich
 */
public class TableConstructionController {

    private ExperimentAnalysisModel experimentData;
    private CIDAView cidaView;

    public enum Intent {

        ADD_ONE, ADD_ALL
    }

    public enum Target {

        EXPERIMENT, VARIABLE, EXPERIMENT_AND_VARIABLE
    }

    public TableConstructionController(CIDAView cv, ExperimentAnalysisModel data) {
        experimentData = data;
        cidaView = cv;
    }

    private void addStatistic(IExperiment experiment, DataTable analysisDataTable,
            VariableStatistic statistic, String variableName, int rows) {
        analysisDataTable.addColumn(Util.trimList(experiment.getStatistic(variableName, statistic), rows));
        analysisDataTable.setColumnName(analysisDataTable.getNumColums() - 1, experiment.getName() + " " + variableName + " " + statistic);
    }

    protected void resolveAddOneIntent(IExperiment experiment, String variableName, int rowsProposed,
            DataTable analysisDataTable, SynopsisTableModel synopsisTableModel)
            throws CIDAException {

        int rows = getRowsActual(experiment, variableName, rowsProposed);
        setupEmptyAnalysisTable(analysisDataTable, experiment, rows);

        try {
            addStatistic(experiment, analysisDataTable, VariableStatistic.Mean, variableName, rows);
            addStatistic(experiment, analysisDataTable, VariableStatistic.Median, variableName, rows);
            addStatistic(experiment, analysisDataTable, VariableStatistic.StdDev, variableName, rows);

            synopsisTableModel.addExperiment(experiment);
            synopsisTableModel.addVariable(variableName);
        } catch (UnsupportedOperationException ex) {
            throw new CIDAException(ex.getMessage());
        }
    }

    public void addDataToTables(Intent intent, Target target, int experimentID, String variableName, int rows)
            throws CIDAException {
        JTable analysisTable = cidaView.getAnalysisTable();
        JTable synopsisTable = cidaView.getSynopsisTable();

        SynopsisTableModel synopsisTableModel = (SynopsisTableModel) synopsisTable.getModel();
        IOBridgeTableModel ioBridgeTableModel = (IOBridgeTableModel) analysisTable.getModel();

        DataTable analysisDataTable = ioBridgeTableModel.getDataTable();

        if (intent.equals(Intent.ADD_ONE)) {
            IExperiment experiment = experimentData.getExperimentCollection().getExperiment(experimentID);

            int rowsActual = getRowsActual(experiment, variableName, rows);
            resolveAddOneIntent(experiment, variableName, rowsActual, analysisDataTable, synopsisTableModel);

        } else if (intent.equals(Intent.ADD_ALL)) {
            if (target.equals(Target.EXPERIMENT)) {
                Set<Map.Entry<Integer, IExperiment>> experiments = experimentData.getExperimentCollection().getExperiments().entrySet();
                for (Map.Entry<Integer, IExperiment> entry : experiments) {
                    resolveAddOneIntent(entry.getValue(), variableName, rows, analysisDataTable, synopsisTableModel);
                }
            } else if (target.equals(Target.VARIABLE)) {
                IExperiment experiment = experimentData.getExperimentCollection().getExperiment(experimentID);
                for (String variable : experiment.getVariableNames()) {
                    resolveAddOneIntent(experiment, variable, rows, analysisDataTable, synopsisTableModel);
                }
            } else if (target.equals(Target.EXPERIMENT_AND_VARIABLE)) {
                Set<Map.Entry<Integer, IExperiment>> experiments = experimentData.getExperimentCollection().getExperiments().entrySet();
                for (Map.Entry<Integer, IExperiment> entry : experiments) {
                    IExperiment experiment = entry.getValue();
                    for (String variable : experiment.getVariableNames()) {
                        resolveAddOneIntent(experiment, variable, rows, analysisDataTable, synopsisTableModel);
                    }
                }
            }
        }
        analysisTable.setModel(new IOBridgeTableModel((StandardDataTable<Numeric>) analysisDataTable));
        synopsisTable.setModel(new SynopsisTableModel(synopsisTableModel));
    }

    public int getRowsActual(IExperiment experiment, String variableName, int rows) {
        int rowsActual = experiment.getStatistics(variableName).size();
        if (rows != 0) {
            rowsActual = rows;
        }
        return rowsActual;
    }

    public void setupEmptyAnalysisTable(DataTable table, IExperiment experiment, int rows) {
        if (table.getNumColums() == 0) {
            List<Numeric> iterations = experiment.getIterationColumn();
            Util.trimList(iterations, rows);
            table.addColumn(iterations);
            table.setColumnName(0, CIDAConstants.TABLE_ITERATIONS_LABEL);
        }
    }
}
