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
package cs.cirg.cida.io.transform;

import cs.cirg.cida.io.DataTable;
import cs.cirg.cida.io.StandardDataTable;
import cs.cirg.cida.io.StandardPatternDataTable;
import cs.cirg.cida.io.exception.CIlibIOException;
import cs.cirg.cida.io.pattern.StandardPattern;
import java.util.List;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Class implements a DataOperator that converts a <u>typed</u> datatable into
 * a datatable containing StandardPatternDataTable, where each row in the typed
 * table is a pattern in the new table.
 *
 * Type checking is not done, the logical use of the operators is left up to the user.
 *
 * In order for a pattern
 * to be constructed, the index of the class of the pattern in the typed row needs
 * to be known. Some patterns also have an entire vector as the class, hence the class
 * length can also be set. The default class index is the last item in a row, and the
 * default length is 1.
 * @author andrich
 */
public class PatternConversionOperator extends SelectiveDataOperator {

    private int classIndex;
    private int classLength;

    /**
     * Default constructor. Initialise class index to -1, and class length to 1.
     */
    public PatternConversionOperator() {
        classIndex = -1;
        classLength = 1;
    }

    /**
     * Checks whether i is in the range min to max.
     * @param i number to test.
     * @param min minimum of range.
     * @param max maximum of range (exclusive).
     * @return true if i is in range.
     */
    private boolean isInRange(int i, int min, int max) {
        if (i < min) {
            return false;
        }
        if (i >= max) {
            return false;
        }
        return true;
    }

    /**
     * Converts a StandardDataTable<Type> to a StandardPatternDataTable. Each row
     * represents a StandardPattern, with all items (barring those that is defined
     * as part of the class) being part of the feature vector. All rows defined
     * in the {@link #selectedItems selectedItems} list are processed. If the list is
     * empty, all rows are processed.
     * @param dataTable a StandardDataTable<Type> where each row represents a StandardPattern.
     * @return a StandardPatternDataTable.
     * @throws net.sourceforge.cilib.io.exception.CIlibIOException {@inheritDoc}
     */
    @Override
    public DataTable operate(DataTable dataTable) throws CIlibIOException {
        StandardDataTable<Type> typedTable = (StandardDataTable<Type>) dataTable;

        int rowLength = typedTable.getNumColums();
        if (classIndex == -1) {
            classIndex = rowLength - 1;
        }
        int classRange = classIndex + classLength;

        StandardPatternDataTable patterns = new StandardPatternDataTable();
        int size = typedTable.size();
        for (int r = 0; r < size; r++) {
            if (selectedItems.isEmpty() || selectedItems.contains(r)) {
                List<Type> row = typedTable.getRow(r);
                StandardPattern pattern = new StandardPattern();
                Vector vector = new Vector();
                for (int i = 0; i < rowLength; i++) {
                    if (!isInRange(i, classIndex, classRange)) {
                        vector.add((Numeric)row.get(i));
                    }
                }
                Type classification = null;
                //assume it is a vector target if class length is not 1
                if (classLength > 1) {
                    Vector target = new Vector();
                    for (int i = classIndex; i < classRange; i++) {
                        target.add((Numeric)row.get(i));
                    }
                    classification = target;
                } else {
                    classification = row.get(classIndex);
                }
                pattern.setVector(vector);
                pattern.setTarget(classification);
                patterns.addRow(pattern);
            }
        }
        return patterns;
    }

    /**
     * The index of the feature vector's class.
     * @return the class index.
     */
    public int getClassIndex() {
        return classIndex;
    }

    /**
     * Sets the index in a row where the feature vector's class starts.
     * @param classIndex the class index.
     */
    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    /**
     * Gets the length of the class.
     * @return the class length.
     */
    public int getClassLength() {
        return classLength;
    }

    /**
     * Sets the length of the class.
     * @param classLength the length of the target class.
     */
    public void setClassLength(int classLength) {
        this.classLength = classLength;
    }
}
