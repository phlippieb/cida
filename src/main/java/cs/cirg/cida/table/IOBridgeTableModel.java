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
package cs.cirg.cida.table;

import cs.cirg.cida.io.StandardDataTable;
import javax.swing.table.AbstractTableModel;
import net.sourceforge.cilib.type.types.Type;

public class IOBridgeTableModel extends AbstractTableModel {

    private StandardDataTable<? extends Type> dataTable;

    public IOBridgeTableModel() {
        dataTable = new StandardDataTable<Type>();
    }

    public int getRowCount() {
        return dataTable.getNumRows();
    }

    public int getColumnCount() {
        return dataTable.getNumColums();
    }

    @Override
    public String getColumnName(int column) {
        return dataTable.getColumnName(column);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return dataTable.getRow(rowIndex).get(columnIndex);
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
