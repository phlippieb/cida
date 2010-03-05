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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author andrich
 */
public class ExperimentCollection {

    private List<DataTableExperiment> experiments;
    private AtomicInteger idCounter;

    public ExperimentCollection() {
        experiments = new ArrayList<DataTableExperiment>();
        idCounter = new AtomicInteger(0);
    }

    public int nextExperimentID() {
        return idCounter.incrementAndGet();
    }

    public void clear() {
        experiments.clear();
        idCounter = new AtomicInteger(0);
    }

    public List<DataTableExperiment> getExperiments() {
        return experiments;
    }

    public void addExperiment(DataTableExperiment experiment) {
        experiment.initialise();
        experiments.add(experiment);
    }

    public boolean removeExperiment(DataTableExperiment experiment) {
        return experiments.remove(experiment);
    }

    public DataTableExperiment getExperiment(String name) {
        for (DataTableExperiment experiment : experiments) {
            if (experiment.getName().equals(name))
                return experiment;
        }
        return null;
    }

    public DataTableExperiment getExperiment(int id) {
        for (DataTableExperiment experiment : experiments) {
            if (experiment.getId() == id)
                return experiment;
        }
        return null;
    }
}
