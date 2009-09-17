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
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.cilib.type.types.StringType;

/**
 * Class reads data from a local text based file. A row is a line in the text file,
 * the line is tokenized using the regular expression delimiter {@link #delimiter delimiter},
 * the resulting tokens form the columns of the row.
 * @author andrich
 */
public class DelimitedTextFileReader extends FileReader<List<StringType>> {

    private String delimiter;

    /** Default constructor. Initializes the delimiter to be a comma, i.e.
     * the class is a csv reader.
     */
    public DelimitedTextFileReader() {
        delimiter = "\\,";
    }

    /**
     * Returns the next line in the file.
     * @return a tokenized line in the file.
     */
    @Override
    public List<StringType> nextRow() {
        try {
            this.hasNextRow();
            String line = this.nextLine();
            String[] tokens = line.split(delimiter);
            List<StringType> result = new ArrayList<StringType>(tokens.length);
            for (String token : tokens) {
                result.add(new StringType(token));
            }
            return result;
        } catch (CIlibIOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the regular expression used to tokenize lines.
     * @return the delimiting regular expression.
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the regular expression used to tokenize lines.
     * @param delimiter the new delimiting regular expression.
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<String> getColumnNames() {
        return new ArrayList<String>();
    }


}
