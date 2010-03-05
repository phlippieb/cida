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

/**
 *
 * @author andrich
 */
public class ExperimentAnalysisModel {

    private ExperimentCollection experimentCollection;
    private DataTableExperiment selectedExperiment;
    private String analysisName;
    private String analysisDirectory;

    public ExperimentAnalysisModel() {
        experimentCollection = new ExperimentCollection();
        reset();
    }

    public void reset() {
        experimentCollection.clear();
        this.setAnalysisName("");
        this.setAnalysisDirectory("");
    }

    public ExperimentCollection getExperimentCollection() {
        return experimentCollection;
    }

    public void setExperimentCollection(ExperimentCollection experimentCollection) {
        this.experimentCollection = experimentCollection;
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    public String getAnalysisDirectory() {
        return analysisDirectory;
    }

    public void setAnalysisDirectory(String analysisDirectory) {
        this.analysisDirectory = analysisDirectory;
        if (this.analysisDirectory.charAt(this.analysisDirectory.length() - 1) != '/') {
            this.analysisDirectory += '/';
        }
    }
}
