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

package cs.cirg.cida.io.transform;

import cs.cirg.cida.io.DataTable;
import cs.cirg.cida.io.exception.CIlibIOException;

/**
 * Interface for classes that perform an operation on data in a DataTable.
 * @author andrich
 */
public interface DataOperator {

    /**
     * Apply an operation to the given DataTable.
     * @param dataTable the DataTable to operate on.
     * @return the resulting DataTable.
     * @throws net.sourceforge.cilib.io.exception.CIlibIOException A wrapper exception
     * that occured during the operation.
     */
    public DataTable operate(DataTable dataTable) throws CIlibIOException;
}
