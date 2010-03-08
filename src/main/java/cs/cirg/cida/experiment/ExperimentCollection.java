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
import cs.cirg.cida.exception.ExperimentNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author andrich
 */
public class ExperimentCollection {

    private Map<Integer, IExperiment> experiments;
    private AtomicInteger idCounter;

    public ExperimentCollection() {
        experiments = new HashMap<Integer, IExperiment>();
        idCounter = new AtomicInteger(0);
    }

    public int nextExperimentID() {
        return idCounter.incrementAndGet();
    }

    public void clear() {
        experiments.clear();
        idCounter = new AtomicInteger(0);
    }

    public Map<Integer, IExperiment> getExperiments() {
        return experiments;
    }

    public void addExperiment(IExperiment experiment) {
        experiment.initialise();
        experiments.put(experiment.getId(), experiment);
    }

    public IExperiment removeExperiment(IExperiment experiment) throws CIDAException {
        IExperiment value = experiments.remove(experiment.getId());
        if (value == null) {
            throw new ExperimentNotFoundException("Unable to remove exception: " + experiment.getName());
        }
        return value;
    }

    public boolean containsExperiment(IExperiment experiment) {
        return experiments.containsKey(experiment.getId());
    }

    public IExperiment getExperiment(int id) throws CIDAException {
        IExperiment value = experiments.get(id);
        if (value == null) {
            throw new ExperimentNotFoundException("Unable to find exception: " + id);
        }
        return value;
    }

    public IExperiment getExperiment(String name) throws ExperimentNotFoundException {
        Collection<IExperiment> values = experiments.values();
        for (IExperiment experiment : values) {
            if (experiment.getName().equals(name))
                return experiment;
        }
        throw new ExperimentNotFoundException("Unable to find exception, id: " + name);
    }
}
