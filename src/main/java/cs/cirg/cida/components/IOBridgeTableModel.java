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

package cs.cirg.cida.components;

import javax.swing.table.AbstractTableModel;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.type.types.Type;

public class IOBridgeTableModel extends AbstractTableModel {

    private StandardDataTable<? extends Type> dataTable;

    public IOBridgeTableModel() {
        dataTable = new StandardDataTable<Type>();
    }

    public IOBridgeTableModel(StandardDataTable<? extends Type> table) {
        dataTable = table;
    }

    @Override
    public int getRowCount() {
        return dataTable.getNumRows();
    }

    @Override
    public int getColumnCount() {
        return dataTable.getNumColums();
    }

    @Override
    public String getColumnName(int column) {
        return dataTable.getColumnName(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < dataTable.getNumRows() && columnIndex < dataTable.getNumColums())
            return dataTable.getRow(rowIndex).get(columnIndex);
        else
            return null;
    }

    @Override
    public Class getColumnClass(int c) {
        return this.getValueAt(0, c).getClass();
    }

    public StandardDataTable<? extends Type> getDataTable() {
        return dataTable;
    }

    public void setDataTable(StandardDataTable<? extends Type> dataTable) {
        this.dataTable = dataTable;
    }
}
