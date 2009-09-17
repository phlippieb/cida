/*
 * Copyright (C) 2003 - 2008
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
package cs.cirg.cida.io;

import cs.cirg.cida.io.exception.CIlibIOException;
import cs.cirg.cida.io.transform.DataOperator;
import java.util.LinkedList;
import java.util.List;

/**
 * A class that builds a new instance of a DataTable object. By changing the type
 * of {@link #dataTable dataTable} and {@link #dataReader dataReader} that is used,
 * the behaviour changes. The default behaviour is to read a text file from the local
 * machine to a text datatable.
 * @author andrich
 */
public class DataTableBuilder {

    private DataTable dataTable;
    private DataReader dataReader;
    private List<DataOperator> operatorPipeline;

    /**
     * Default constructor. Initialises dataTable to a new TextDataTable and
     * dataReader to be a new TextDataReader.
     * @param reader 
     */
    public DataTableBuilder(DataReader reader) {
        dataReader = reader;
        dataTable = new StandardDataTable();
        operatorPipeline = new LinkedList<DataOperator>();
    }

    /**
     * This method reads all rows from the {@link #dataReader DataReader} object and
     * adds them into the {@link #dataTable DataTable} object. If the default
     * behaviour is not sufficient or desired, method should be overriden.
     * @return the constructed datatable.
     * @throws CIlibIOException wraps another Exception that might occur during IO
     */
    public DataTable buildDataTable() throws CIlibIOException {
        dataReader.open();
        while (dataReader.hasNextRow()) {
            dataTable.addRow(dataReader.nextRow());
        }
        dataTable.setColumnNames(dataReader.getColumnNames());
        dataReader.close();
        for (DataOperator operator : operatorPipeline) {
            this.setDataTable(operator.operate(this.getDataTable()));
        }
        return (DataTable) this.dataTable.getClone();
    }

    /**
     * Adds a DataOperator to the pipeline.
     * @param dataOperator a new DataOperator.
     */
    public void addDataOperator(DataOperator dataOperator) {
        operatorPipeline.add(dataOperator);
    }

    /**
     * Get the DataReader object.
     * @return the data reader.
     */
    public DataReader getDataReader() {
        return dataReader;
    }

    /**
     * Set the DataReader object.
     * @param dataReader the data reader object.
     */
    public void setDataReader(DataReader dataReader) {
        this.dataReader = dataReader;
    }

    /**
     * Get the DataTable object.
     * @return the data table.
     */
    public DataTable getDataTable() {
        return (DataTable) this.dataTable.getClone();
    }

    /**
     * Sets the DataTable object.
     * @param dataTable the data table.
     */
    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    /**
     * Gets the operator pipeline.
     * @return the operator pipeline.
     */
    public List<DataOperator> getOperatorPipeline() {
        return operatorPipeline;
    }

    /**
     * Sets the operator pipeline.
     * @param operatorPipeline the new operator pipeline.
     */
    public void setOperatorPipeline(List<DataOperator> operatorPipeline) {
        this.operatorPipeline = operatorPipeline;
    }

    /**
     * Convenience method for getting the source URL that the datatable is built
     * from. Delegates to: {@link #dataReader dataReader} object.
     * @return the source URL.
     */
    public String getSourceURL() {
        return this.dataReader.getSourceURL();
    }

    /**
     * Convenience method for setting the source URL that the datatable is built
     * from. Delegates to: {@link #dataReader dataReader} object.
     * @param sourceURL the new source URL.
     */
    public void setSourceURL(String sourceURL) {
        this.dataReader.setSourceURL(sourceURL);
    }
}
