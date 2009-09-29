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

package cs.cirg.cida.io;

import java.util.ArrayList;
import java.util.List;

/**
 * Class reads data from a local text based file. A row is a line in the text file,
 * the line is tokenized using the regular expression delimiter {@link #delimiter delimiter},
 * the resulting tokens form the columns of the row.
 * @author andrich
 */
public class TextFileReader extends FileReader<String> {

    /** Default constructor. Initializes the delimiter to be a comma, i.e.
     * the class is a csv reader.
     */
    public TextFileReader() {
    }

    /**
     * Returns the next line in the file.
     * @return a tokenized line in the file.
     */
    @Override
    public String nextRow() {
        return this.nextLine();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> getColumnNames() {
        return new ArrayList<String>();
    }
}
