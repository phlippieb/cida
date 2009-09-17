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

import cs.cirg.cida.analysis.DescriptiveStatsCalculator;
import cs.cirg.cida.io.StandardDataTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sourceforge.cilib.type.types.Numeric;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public class Experiment {

    private StandardDataTable<Numeric> data;
    private HashMap<String, List<DescriptiveStatistics>> variableStatistics;
    private List<String> variableNames;
    private String name;
    private String dataSource;
    private int id;

    public Experiment(int id, StandardDataTable<Numeric> dataTable) {
        data = dataTable;
        variableStatistics = new HashMap<String, List<DescriptiveStatistics>>();
        name = "experiment";
        dataSource = "";
        this.id = id;
    }

    public void initialise() {
        this.determineVariableNames();
        this.calculateStatistics();
    }

    public void determineVariableNames() {
        variableNames = new ArrayList<String>();
        List<String> columnNames = data.getColumnNames();
        for (String columnName : columnNames) {
            String variableName = columnName.replaceAll("[\\W||[\\d]]", "");
            if (!variableName.equalsIgnoreCase("ITERATIONS") && !variableNames.contains(variableName)) {
                variableNames.add(variableName);
            }
        }
    }

    public void calculateStatistics() {
        for (String variableName : variableNames) {

            List<Integer> selectedColumns = new ArrayList<Integer>();
            List<String> columnNames = data.getColumnNames();

            int size = columnNames.size();
            for (int i = 0; i < size; i++) {
                String columnName = columnNames.get(i);
                if (columnName.contains(variableName)) {
                    selectedColumns.add(i);
                }
            }

            DescriptiveStatsCalculator calculator = new DescriptiveStatsCalculator((StandardDataTable<Numeric>) data, selectedColumns);
            calculator.calculate();
            variableStatistics.put(variableName, calculator.getIterationsDescriptiveStatistics());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Experiment other = (Experiment) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.id;
        return hash;
    }

    public List<Numeric> getIterationColumn() {
        return data.getColumn(0);
    }

    public List<Numeric> getFinalIteration() {
        return data.getRow(data.getNumRows() - 1);
    }

    public StandardDataTable<Numeric> getData() {
        return data;
    }

    public void setData(StandardDataTable<Numeric> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

    public void setVariableNames(List<String> variableNames) {
        this.variableNames = variableNames;
    }

    public List<DescriptiveStatistics> getStatistics(String variableName) {
        return variableStatistics.get(variableName);
    }

    public DescriptiveStatistics getFinalIterationStatistics(String variableName) {
        int size = variableStatistics.get(variableName).size();
        return variableStatistics.get(variableName).get(size - 1);
    }
}
