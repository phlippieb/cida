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

import com.google.common.collect.Sets;
import cs.cirg.cida.CIDAPromptDialog;
import cs.cirg.cida.CIDAView;
import cs.cirg.cida.analysis.MannWhitneyUTest;
import cs.cirg.cida.components.IOBridgeTableModel;
import cs.cirg.cida.components.SynopsisTableModel;
import java.util.List;
import java.util.Set;
import net.sourceforge.cilib.io.DataTable;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.type.types.Numeric;

/**
 *
 * @author andrich
 */
public class ExperimentTestController {

    private CIDAView view;
    private ExperimentTestModel model;

    public ExperimentTestController(CIDAView view, ExperimentTestModel model) {
        this.view = view;
        this.model = model;
    }

    public void addExperiment(IExperiment experiment) {
        model.addExperiment(experiment);
        Set<String> variableSet = Sets.newHashSet(model.getTestVariables());
        Set<String> newVariables = Sets.newHashSet(experiment.getVariableNames());
        variableSet = Sets.union(variableSet, newVariables);
        model.getTestVariables().clear();
        model.getTestVariables().addAll(variableSet);

        view.getVariablesTestComboBox().removeAllItems();
        for (String variable : model.getTestVariables()) {
            view.getVariablesTestComboBox().addItem(variable);
        }

        SynopsisTableModel synopsisTableModel = (SynopsisTableModel) view.getTestExperimentsTable().getModel();
        synopsisTableModel.setExperiments(model.getExperiments());
        synopsisTableModel.setVariables(model.getTestVariables());

        SynopsisTableModel newModel = new SynopsisTableModel(synopsisTableModel);
        view.getTestExperimentsTable().setModel(newModel);
    }

    public void mannWhitneyUTest() {
        List<IExperiment> testExperiments = model.getExperiments();
        if (testExperiments.size() > 2) {
            CIDAPromptDialog dialog = new CIDAPromptDialog(view.getFrame(), "Mann Whitney U test is only applicable to 2 experiments.");
            dialog.displayPrompt();
            return;
        }
        MannWhitneyUTest test = new MannWhitneyUTest();
        for (IExperiment experiment : testExperiments) {
            test.addExperiment(experiment);
        }

        if (((String) view.getHypothesisComboBox().getSelectedItem()).compareTo("Not Equal") == 0) {
            test.setAlternativeHypothesisNotEquals();
        } else if (((String) view.getHypothesisComboBox().getSelectedItem()).compareTo("Less Than") == 0) {
            test.setAlternativeHypothesisLessThan();
        } else {
            test.setAlternativeHypothesisGreaterThan();
        }

        test.performTest((String) view.getVariablesTestComboBox().getSelectedItem());
        DataTable table = test.getResults();
        IOBridgeTableModel ioModelBridge = new IOBridgeTableModel();
        ioModelBridge.setDataTable((StandardDataTable<Numeric>) table);
        view.getTestResultsTable().setModel(ioModelBridge);
    }

}
