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

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.io.FileReader;
import net.sourceforge.cilib.io.exception.CIlibIOException;
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

    @Override
    public List<Type> nextRow() {

        String line = null;
        if (firstDataLine != null) {
            line = firstDataLine;
            firstDataLine = null;
        } else {
            line = this.nextLine();
        }
        String[] types = line.split("\\s");
        ArrayList<Type> row = new ArrayList<Type>(types.length + 1);
        row.add(new Int(Integer.parseInt(types[0])));
        for (int i = 1; i < types.length; i++) {
            if (!types[i].isEmpty()) {
                //try {
                row.add(new Real(Double.parseDouble(types[i])));
                //} catch (NumberFormatException ex) {
                //    row.add(row.get(i-1));
                //}
            }
        }
        return row;

    }

    @Override
    public List<String> getColumnNames() {
        return columnNames;
    }

    private void processHeader() throws CIlibIOException {
        String line = this.nextLine();
        while (line.charAt(0) == '#') {
            int seperatorIndex = line.indexOf('-');
            int column = Integer.parseInt(line.substring(1, seperatorIndex - 1).trim());

            String columnName = line.substring(seperatorIndex + 2).trim();
            columnNames.add(columnName);
            line = this.nextLine();
            if (line == null) // this shouldn't happen, the next line after
            {
                throw new CIlibIOException("Unexpected end of file: " + // the header should contain data
                        this.getFile().getName()
                        + ", data expected after header.");
            }
        }
        firstDataLine = line;
    }
}
