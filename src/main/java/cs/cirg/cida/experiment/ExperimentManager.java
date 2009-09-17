/**
 * Copyright (C) 2009
 * Andrich van Wyk
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
public class ExperimentManager {

    private List<Experiment> experiments;
    private AtomicInteger idCounter;

    private ExperimentManager() {
        experiments = new ArrayList<Experiment>();
        idCounter = new AtomicInteger(0);
    }

    public int nextExperimentID() {
        return idCounter.incrementAndGet();
    }

    public void addExperiment(Experiment experiment) {
        experiment.initialise();
        experiments.add(experiment);
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public Experiment getExperiment(String name) {
        for (Experiment experiment : experiments) {
            if (experiment.getName().equals(name))
                return experiment;
        }
        return null;
    }

    public Experiment getExperiment(int id) {
        for (Experiment experiment : experiments) {
            if (experiment.getId() == id)
                return experiment;
        }
        return null;
    }

    public static ExperimentManager getInstance() {
        return ExperimentManagerHolder.INSTANCE;
    }

    private static class ExperimentManagerHolder {

        private static final ExperimentManager INSTANCE = new ExperimentManager();
    }
}
