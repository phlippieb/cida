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

import cs.cirg.cida.analysis.DescriptiveStatsCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Real;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public class DataTableExperiment implements IExperiment {

    private StandardDataTable<Numeric> data;
    private HashMap<String, List<DescriptiveStatistics>> variableToStatsMap;
    private List<String> variableNames;
    private String experimentName;
    private String dataSource;
    private int id;
    private boolean initisalised;

    public DataTableExperiment(int id, StandardDataTable<Numeric> dataTable) {
        data = dataTable;
        variableToStatsMap = new HashMap<String, List<DescriptiveStatistics>>();
        experimentName = "experiment";
        dataSource = "";
        this.id = id;
        this.initisalised = false;
    }

    @Override
    public void initialise() {
        if (!initisalised) {
            this.determineVariableNames();
            this.calculateStatistics();
            initisalised = true;
        }
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
                // if necesary strip the sample number: (X)
                if (columnName.contains("(")) {
                    columnName = columnName.substring(0, columnName.indexOf("(") - 1);
                }
                if (columnName.compareTo(variableName) == 0) {
                    selectedColumns.add(i);
                }
            }

            DescriptiveStatsCalculator calculator = new DescriptiveStatsCalculator((StandardDataTable<Numeric>) data, selectedColumns);
            calculator.calculate();
            variableToStatsMap.put(variableName, calculator.getIterationsDescriptiveStatistics());
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
        final DataTableExperiment other = (DataTableExperiment) obj;
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

    @Override
    public List<Numeric> getIterationColumn() {
        return data.getColumn(0);
    }

    @Override
    public StandardDataTable<Numeric> getData() {
        return data;
    }

    @Override
    public void setData(StandardDataTable<Numeric> data) {
        this.data = data;
    }

    @Override
    public String getName() {
        return experimentName;
    }

    @Override
    public void setName(String name) {
        this.experimentName = name;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public List<String> getVariableNames() {
        return variableNames;
    }

    @Override
    public void setVariableNames(List<String> variableNames) {
        this.variableNames = variableNames;
    }

    @Override
    public List<DescriptiveStatistics> getStatistics(String variableName) {
        return variableToStatsMap.get(variableName);
    }

    @Override
    public List<Real> getStatistic(String variableName, VariableStatistic statistic) {
        return statistic.getStatistic(variableToStatsMap.get(variableName));
    }

    @Override
    public List<Numeric> getBottomRow() {
        return data.getRow(data.getNumRows() - 1);
    }

    @Override
    public DescriptiveStatistics getBottomRowStatistics(String variableName) {
        int size = variableToStatsMap.get(variableName).size();
        return variableToStatsMap.get(variableName).get(size - 1);
    }
}
