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
package cs.cirg.cida;

/**
 *
 * @author andrich
 */
public class CIDAConstants {

    // Numeric constants
    public static final int DEFAULT_CHART_HORIZONTAL_RES = 800;
    public static final int DEFAULT_CHART_VERTICAL_RES = 600;
    // String constants
    public static final String CHART_ITERATIONS_LABEL = "Iterations";
    public static final String CHART_VALUE_LABEL = "Value";
    public static final String DEFAULT_CHART_NAME = "Chart";
    public static final String DEFAULT_TABLE_NAME = "table.tex";
    public static final String DEFAULT_RAW_FILE_NAME = "_raw"; // part of a larger concat
    public static final String EXT_CSV = ".csv";
    public static final String EXT_EPS = ".eps";
    public static final String EXT_PNG = ".png";
    public static final String TABLE_ITERATIONS_LABEL = CHART_ITERATIONS_LABEL;
    // String messages
    public static final String DIALOG_NEW_NAME_MSG = "Enter new name: ";
    public static final String DIALOG_NUM_ROWS_MSG = "Number of rows:";
    public static final String DIALOG_TXT_CSV_MSG = "Text and CSV files";
    public static final String DIALOG_CHOOSE_COLOR_MSG = "Choose colour";
    public static final String RENAME_EXPERIMENT_MSG = "Rename experiment:";
    // Exception messages
    public static final String EXCEPTION_OCCURRED = "An Exception has occured: ";
    public static final String EXCEPTION_EXPERIMENT_NOT_FOUND = "Experiment not found: ";
    public static final String EXCEPTION_SELECTED_ITEM_NULL = "Selected item is null";
    public static final String EXCEPTION_VARIABLE_BOX_EMPTY = "Variable box is empty";
}
