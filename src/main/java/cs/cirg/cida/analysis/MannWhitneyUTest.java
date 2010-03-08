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

import cs.cirg.cida.experiment.IExperiment;
import java.util.ArrayList;
import jsc.independentsamples.MannWhitneyTest;
import jsc.tests.H1;
import net.sourceforge.cilib.io.DataTable;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.type.types.StringType;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public class MannWhitneyUTest extends StatisticalTest {

    private H1 alternativeHypothesis;

    @Override
    public DataTable performTest(String... variableNames) {
        if (this.getExperiments().size() < 2) {
            return null;
        }
        IExperiment experiment1 = this.getExperiments().get(0);
        IExperiment experiment2 = this.getExperiments().get(1);

        DescriptiveStatistics stats1 = experiment1.getBottomRowStatistics(variableNames[0]);
        DescriptiveStatistics stats2 = experiment2.getBottomRowStatistics(variableNames[0]);

        double[] xA = new double[(int) stats1.getN()];
        double[] xB = new double[(int) stats2.getN()];
        for (int i = 0; i < xA.length; i++) {
            xA[i] = stats1.getElement(i);
            xB[i] = stats2.getElement(i);
        }

        MannWhitneyTest mannWhitneyTest = new MannWhitneyTest(xA, xB, alternativeHypothesis, 0.0, false);

        results = new StandardDataTable<StringType>();

        ArrayList<StringType> row = new ArrayList<StringType>();
        row.add(new StringType("N_A"));
        row.add(new StringType(stats1.getN() + ""));
        results.addRow(row);

        row = new ArrayList<StringType>();
        row.add(new StringType("N_B"));
        row.add(new StringType(stats2.getN() + ""));
        results.addRow(row);

        row = new ArrayList<StringType>();
        row.add(new StringType("P value"));
        row.add(new StringType(mannWhitneyTest.exactSP() + ""));
        results.addRow(row);

        row = new ArrayList<StringType>();
        row.add(new StringType("Approx P value"));
        row.add(new StringType(mannWhitneyTest.approxSP() + ""));
        results.addRow(row);

        row = new ArrayList<StringType>();
        row.add(new StringType("Ranks Sum A"));
        row.add(new StringType(mannWhitneyTest.getRankSumA() + ""));
        results.addRow(row);

        row = new ArrayList<StringType>();
        row.add(new StringType("Ranks Sum B"));
        row.add(new StringType(mannWhitneyTest.getRankSumB() + ""));
        results.addRow(row);

        row = new ArrayList<StringType>();
        row.add(new StringType("U"));
        row.add(new StringType(mannWhitneyTest.getStatistic() + ""));
        results.addRow(row);

        row = new ArrayList<StringType>();
        row.add(new StringType("Z"));
        row.add(new StringType(mannWhitneyTest.getZ() + ""));
        results.addRow(row);

        results.setColumnName(0, "Statistic");
        results.setColumnName(1, "Value");

        return this.getResults();
    }

    public void setAlternativeHypothesisNotEquals() {
        this.alternativeHypothesis = H1.NOT_EQUAL;
    }

    public void setAlternativeHypothesisLessThan() {
        this.alternativeHypothesis = H1.LESS_THAN;
    }
    public void setAlternativeHypothesisGreaterThan() {
        this.alternativeHypothesis = H1.GREATER_THAN;
    }
}
