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

package cs.cirg.cida.analysis;

import cs.cirg.cida.experiment.Experiment;
import cs.cirg.cida.io.DataTable;
import java.util.ArrayList;

/**
 *
 * @author andrich
 */
public abstract class StatisticalTest {

    protected ArrayList<Experiment> experiments;
    protected DataTable results;

    public StatisticalTest() {
        experiments = new ArrayList<Experiment>();
    }

    public void addExperiment(Experiment experiment) {
        experiments.add(experiment);
    }

    public abstract DataTable performTest(String... variableNames);

    public ArrayList<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(ArrayList<Experiment> experiments) {
        this.experiments = experiments;
    }

    public DataTable getResults() {
        return results;
    }

    public void setResults(DataTable results) {
        this.results = results;
    }
}
