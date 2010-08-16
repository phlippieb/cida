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

import cs.cirg.cida.CIDAView;
import cs.cirg.cida.components.IOBridgeTableModel;
import cs.cirg.cida.components.SynopsisTableModel;
import cs.cirg.cida.exception.CIDAException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import net.sourceforge.cilib.io.CSVFileWriter;
import net.sourceforge.cilib.io.DataTable;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.io.exception.CIlibIOException;
import net.sourceforge.cilib.type.types.Numeric;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public class ExperimentController {

    private CIDAView view;
    private ExperimentAnalysisModel model;
    //test sub-controller
    private ExperimentTestController experimentTestController;

    public ExperimentController(CIDAView view, ExperimentAnalysisModel model) {
        this.view = view;
        this.model = model;
        experimentTestController = new ExperimentTestController(view, new ExperimentTestModel());
    }

    public void addExperiments(File[] dataFiles, String[] experimentName) throws CIlibIOException {
        for (int i = 0; i < dataFiles.length; i++) {
            model.addExperiment(dataFiles[i], experimentName[i]);
            view.getExperimentsComboBox().addItem(experimentName[i]);
            view.getExperimentsComboBox().setSelectedItem(experimentName[i]);
        }
    }

    public void setActiveExperiment(String experimentName) throws CIDAException {
        model.setActiveExperiment(model.getExperimentCollection().getExperiment(experimentName));
        List<String> variableNames = model.getActiveExperiment().getVariableNames();

        String currentVariableSelection = (String) view.getVariablesComboBox().getSelectedItem();
        view.getVariablesComboBox().removeAllItems();
        boolean currentSelectionAvailable = false;
        for (String variableName : variableNames) {
            if ((currentVariableSelection != null) && (variableName.compareTo(currentVariableSelection) == 0)) {
                currentSelectionAvailable = true;
            }
            view.getVariablesComboBox().addItem(variableName);
        }
        if (currentSelectionAvailable) {
            view.getVariablesComboBox().setSelectedItem(currentVariableSelection);
        }

        IOBridgeTableModel ioTableModel = new IOBridgeTableModel();
        ioTableModel.setDataTable(model.getActiveExperiment().getData());
        view.getRawTable().setModel(ioTableModel);
    }

    private List<? extends Numeric> trimList(List<? extends Numeric> list, int targetSize) {
        int size = list.size();
        for (int i = size - 1; i >= targetSize; i--) {
            list.remove(i);
        }
        return list;
    }

    public void addVariableToAnalysisTable(String variableName, int selectedIterations) throws CIDAException {
        IExperiment selectedExperiment = model.getActiveExperiment();
        model.updateAnalysisName(selectedExperiment, variableName);

        JTable analysisTable = view.getAnalysisTable();
        DataTable analysisDataTable = ((IOBridgeTableModel) analysisTable.getModel()).getDataTable();

        List<DescriptiveStatistics> statistics = selectedExperiment.getStatistics(variableName);

        int iterationsToAdd = statistics.size();
        if (selectedIterations != 0) {
            if (selectedIterations < 0 || selectedIterations > iterationsToAdd) {
                throw new CIDAException("Invalid number of iterations specified: " + iterationsToAdd);
            }
            iterationsToAdd = selectedIterations;
        }

        if (analysisDataTable.getNumColums() == 0) {
            List<Numeric> iterations = selectedExperiment.getIterationColumn();

            trimList(iterations, iterationsToAdd);
            analysisDataTable.addColumn(iterations);
            analysisDataTable.setColumnName(0, "Iterations");
        }

        try {
            analysisDataTable.addColumn(trimList(selectedExperiment.getStatistic(variableName, VariableStatistic.Mean), iterationsToAdd));
            analysisDataTable.setColumnName(analysisDataTable.getNumColums() - 1, selectedExperiment.getName() + " " + variableName + " Mean");
            analysisDataTable.addColumn(trimList(selectedExperiment.getStatistic(variableName, VariableStatistic.StdDev), iterationsToAdd));
            analysisDataTable.setColumnName(analysisDataTable.getNumColums() - 1, selectedExperiment.getName() + " " + variableName + " Std Dev");
        } catch (UnsupportedOperationException ex) {
            throw new CIDAException(ex.getMessage());
        }

        IOBridgeTableModel ioBridgeTable = new IOBridgeTableModel();
        ioBridgeTable.setDataTable((StandardDataTable<Numeric>) analysisDataTable);
        analysisTable.setModel(ioBridgeTable);

        addVariableToSynopsisTable(variableName);
    }

    public void addVariableToSynopsisTable(String variableName) {
        IExperiment selectedExperiment = model.getActiveExperiment();
        JTable synopsisTable = view.getSynopsisTable();
        SynopsisTableModel synopsisTableModel = (SynopsisTableModel) synopsisTable.getModel();

        if (!synopsisTableModel.getVariables().contains(variableName)) {
            synopsisTableModel.getVariables().add(variableName);
        }

        if (!synopsisTableModel.getExperiments().contains(selectedExperiment)) {
            synopsisTableModel.getExperiments().add(selectedExperiment);
        }

        SynopsisTableModel newModel = new SynopsisTableModel(synopsisTableModel);
        synopsisTable.setModel(newModel);
    }

    public void exportSynopsisTable(File file) {
        BufferedWriter writer = null;
        DecimalFormat formatter = new DecimalFormat("#.#####");
        try {
            JTable synopsisTable = view.getSynopsisTable();
            writer = new BufferedWriter(new FileWriter(file));

            writer.write("\\begin{table}[htb]\n");
            writer.write("\\begin{center}\n");
            writer.write("\\begin{tabular}{");
            int numCols = synopsisTable.getColumnCount();
            writer.write(" | l");
            for (int i = 0; i < (numCols - 1) / 2; i++) {
                writer.write(" | p{1.2cm}");
            }
            writer.write(" |}\n");
            writer.write("\\hline\\hline\n");
            //writer.write("{\\bf " + synopsisTable.getModel().getColumnName(0) + "}");
            writer.write("{\\bf Problem}");
            for (int i = 1; i < numCols; i += 2) {
                writer.write(" & {\\bf " + synopsisTable.getModel().getColumnName(i) + "}");
            }
            writer.write("\\\\\n");
            writer.write("\\hline\n");

            int numRows = synopsisTable.getRowCount();
            for (int i = 0; i < numRows; i++) {
                Object value = synopsisTable.getModel().getValueAt(i, 0);
                if (value instanceof Number) {
                    value = formatter.format(value);
                }
                writer.write(value.toString() + "\t");
                for (int j = 1; j < numCols; j += 2) {
                    //means
                    value = synopsisTable.getModel().getValueAt(i, j);
                    if (value instanceof Number) {
                        value = formatter.format(value);
                    }
                    writer.write("\t&\t" + value.toString());

                    //stddevs
                    value = synopsisTable.getModel().getValueAt(i, j + 1);
                    if (value instanceof Number) {
                        value = formatter.format(value);
                    }
                    writer.write(" (" + value.toString() + ")");
                }
                writer.write("\\\\\n");
                writer.write("\\hline\n");
            }
            writer.write("\\hline\n");
            writer.write("\\end{tabular}\n");
            writer.write("\\end{center}\n");
            writer.write("\\end{table}\n");
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addExperimentToTest() {
        experimentTestController.addExperiment(model.getActiveExperiment());
    }

    public void exportRawTable() throws CIlibIOException {
        exportTable(view.getRawTable(), model.getActiveExperiment().getName() + "_raw.csv");
    }

    public void exportAnalysisTable() throws CIlibIOException {
        exportTable(view.getAnalysisTable(), model.getAnalysisName() + ".csv");
    }

    public void exportTable(JTable table, String filename) throws CIlibIOException {
        JFileChooser chooser = new JFileChooser(model.getDataDirectory());
        //model.getActiveExperiment().getName() + ".csv"
        chooser.setSelectedFile(new File(filename));
        int returnVal = chooser.showOpenDialog(view.getComponent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            CSVFileWriter writer = new CSVFileWriter();
            writer.setFile(chooser.getSelectedFile());
            writer.open();
            writer.write(((IOBridgeTableModel) table.getModel()).getDataTable());
            writer.close();
        }
    }

    public void mannWhitneyUTest() {
        experimentTestController.mannWhitneyUTest();
    }

    public String getDataDirectory() {
        return model.getDataDirectory();
    }

    public void setDataDirectory(String directory) {
        model.setDataDirectory(directory);
    }

    public String getAnalysisName() {
        return model.getAnalysisName();
    }

    public void setAnalysisName(String name) {
        model.setAnalysisName(name);
    }
}
