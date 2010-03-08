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

import cs.cirg.cida.components.CIlibOutputReader;
import java.io.File;
import net.sourceforge.cilib.io.DataTableBuilder;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.io.exception.CIlibIOException;
import net.sourceforge.cilib.type.types.Numeric;

/**
 *
 * @author andrich
 */
public class ExperimentAnalysisModel {

    private ExperimentCollection experimentCollection;
    private IExperiment activeExperiment;
    private String analysisName;
    private String dataDirectory;

    public ExperimentAnalysisModel(String dataDirectory) {
        experimentCollection = new ExperimentCollection();
        reset(dataDirectory);
    }

    public void reset(String dataDirectory) {
        experimentCollection.clear();
        this.setAnalysisName("");
        this.setDataDirectory(dataDirectory);
    }

    public void addExperiment(File dataFile, String experimentName) throws CIlibIOException {
        CIlibOutputReader reader = new CIlibOutputReader();
        reader.setSourceURL(dataFile.getAbsolutePath());
        DataTableBuilder dataTableBuilder = new DataTableBuilder(reader);
        try {
            dataTableBuilder.buildDataTable();
        } catch (NumberFormatException ex) {
            throw new CIlibIOException(ex);
        }

        IExperiment experiment = new DataTableExperiment(experimentCollection.nextExperimentID(),
                (StandardDataTable<Numeric>) dataTableBuilder.getDataTable());
        experiment.setDataSource(dataFile.getAbsolutePath());
        experiment.setName(experimentName);
        experimentCollection.addExperiment(experiment);
    }

    public ExperimentCollection getExperimentCollection() {
        return experimentCollection;
    }

    public void setExperimentCollection(ExperimentCollection experimentCollection) {
        this.experimentCollection = experimentCollection;
    }

    public void updateAnalysisName(IExperiment experiment, String variableName) {
        if (!analysisName.contains(experiment.getName())) {
            analysisName = experiment.getName() + "_" + analysisName;
        }
        if (!analysisName.contains(variableName)) {
            analysisName = analysisName + variableName;
        }
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        if (this.dataDirectory.charAt(this.dataDirectory.length() - 1) != '/') {
            this.dataDirectory += '/';
        }
    }

    public IExperiment getActiveExperiment() {
        return activeExperiment;
    }

    public void setActiveExperiment(IExperiment activeExperiment) {
        this.activeExperiment = activeExperiment;
    }
}
