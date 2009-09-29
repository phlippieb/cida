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

import cs.cirg.cida.io.FileReader;
import cs.cirg.cida.io.exception.CIlibIOException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.type.types.Int;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.Type;

public class CIlibOutputReader extends FileReader<List<Type>> {

    private ArrayList<String> columnNames;
    private String firstDataLine;

    @Override
    public void open() throws CIlibIOException {
        super.open();
        columnNames = new ArrayList<String>();
        this.processHeader();
    }
    
    public List<Type> nextRow() {
        String line = null;
        if (firstDataLine != null) {
            line = firstDataLine;
            firstDataLine = null;
        } else {
            line = this.nextLine();
        }
        String[] types = line.split("\\s");
        ArrayList<Type> row = new ArrayList<Type>(types.length+1);
        row.add(new Int(Integer.parseInt(types[0])));
        for (int i = 1; i < types.length; i++) {
            if (!types[i].isEmpty())
                row.add(new Real(Double.parseDouble(types[i])));
        }
        return row;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    private void processHeader() {
        String line = this.nextLine();
        while (line.charAt(0)=='#') {
            int seperatorIndex = line.indexOf('-');
            int column = Integer.parseInt(line.substring(1,seperatorIndex - 1).trim());
            String columnName = line.substring(seperatorIndex + 2);
            columnNames.add(columnName);
            line = this.nextLine();
        }
        firstDataLine = line;
    }
}
