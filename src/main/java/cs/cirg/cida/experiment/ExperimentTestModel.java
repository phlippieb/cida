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

import cs.cirg.cida.exception.CIDAException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andrich
 */
public class ExperimentTestModel {

    private List<IExperiment> experiments;
    private List<String> testVariables;

    public ExperimentTestModel() {
        experiments = new ArrayList<IExperiment>();
        testVariables = new ArrayList<String>();
    }

    public void addExperiment(IExperiment experiment) {
        if (!experiments.contains(experiment))
            experiments.add(experiment);
    }

    public IExperiment getExperiment(String experimentName) throws CIDAException {
        IExperiment experiment = null;
        for (IExperiment exp : experiments) {
            if (exp.getName().equals(experimentName))
                experiment = exp;
        }
        if (experiment == null) {
            throw new CIDAException("Experiment not found: "+experimentName);
        }
        return experiment;
    }

    public void addTestVariable(String variable) {
        testVariables.add(variable);
    }

    public List<IExperiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<IExperiment> experiments) {
        this.experiments = experiments;
    }

    public List<String> getTestVariables() {
        return testVariables;
    }

    public void setTestVariables(List<String> testVariables) {
        this.testVariables = testVariables;
    }
}
