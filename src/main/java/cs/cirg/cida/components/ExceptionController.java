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

import cs.cirg.cida.CIDAPromptDialog;
import java.awt.Frame;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author andrich
 */
public class ExceptionController {

    private PrintStream outputStream;

    public ExceptionController() {
        outputStream = System.err;
    }

    public ExceptionController(PrintStream stream) {
        outputStream = stream;
    }

    /**
     * Gets the stack trace of the given exception as a String, each call is on a
     * new line.
     * @param ex the exception that the stack trace is retreived from.
     * @return a String representing a stack trace.
     */
    public static String getStackTrace(Exception ex) {
        StringBuffer exception = new StringBuffer();
        StackTraceElement[] stackTrace = ex.getStackTrace();

        exception.append(ex + "\n");
        for (int i = 0; i < stackTrace.length; i++) {
            exception.append("\tat " + stackTrace[i] + "\n");
        }
        return exception.toString();
    }

    public CIDAPromptDialog handleException(Frame owner, Exception ex, String prompt) {
        String message = ex.getMessage();
        String stackTrace = getStackTrace(ex);
        printToStream(message, stackTrace);
        return new CIDAPromptDialog(owner, prompt + "\n" + message);
    }

    public void handleException(Exception ex) {
        String message = ex.getMessage();
        String stackTrace = getStackTrace(ex);
        printToStream(message, stackTrace);
    }

    public void printToStream(String message, String stackTrace) {
        outputStream.println(message);
        outputStream.println(stackTrace);
    }
}
