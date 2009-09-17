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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class represents a {@link DataReader DataReader} object that reads its data from
 * a local file. The type of file is not restricted.
 * @param <T> The data type that will be read from the file.
 * @author andrich
 */
public abstract class FileReader<T> implements DataReader<T> {

    protected File file;
    private BufferedReader reader;
    private String previousReadLine;
    //private Scanner lineReader;

    /**
     * Initialises the lineReader from a file channel constructed off a file input
     * stream.
     * @throws CIlibIOException {@inheritDoc }
     */
    @Override
    public void open() throws CIlibIOException {
        if (file == null) {
            throw new CIlibIOException("Source URL not set.");
        }
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(this.file);
            reader = new BufferedReader(new InputStreamReader(stream));
            //FileChannel channel = stream.getChannel();
            //lineReader = new Scanner(channel);
            //lineReader.useDelimiter("\\n");
        } catch (IOException ex) {
            throw new CIlibIOException(ex);
        }
    }

    /**
     * Whether there are still lines left to be read.
     * @return true if there are lines left.
     */
    @Override
    public boolean hasNextRow() throws CIlibIOException {
        if (previousReadLine != null) {
            return true;
        }
        try {
            previousReadLine = reader.readLine();
            if (previousReadLine == null)
                return false;
        } catch (IOException ex) {
            throw new CIlibIOException(ex);
        }
        return true;
        /*if (lineReader == null)
            throw new UnsupportedOperationException("FileReader has not been opened for reading.");
            return lineReader.hasNext();*/
        /*if (lineReader == null)
            throw new UnsupportedOperationException("FileReader has not been opened for reading.");
        return lineReader.hasNext();*/
    }

    public String nextLine() {
        try {
            if (previousReadLine != null) {
                String tmp = previousReadLine;
                previousReadLine = null;
                return tmp;
            }
            return reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Closes the lineReader (which in turn closes all wrapped channels and streams).
     */
    @Override
    public void close() throws CIlibIOException {
        try {
            reader.close();
            reader = null;
            //lineReader.close();
            //lineReader = null;
        } catch (IOException ex) {
            throw new CIlibIOException(ex);
        }
    }

    /**
     * Get the absolute path of the file local to the machine.
     * @return the file path.
     */
    @Override
    public String getSourceURL() {
        return file.getAbsolutePath();
    }

    /**
     * Constructs a new file using the passed parameter as the pathname.
     * @param sourceURL the location of the file.
     */
    @Override
    public void setSourceURL(String sourceURL) {
        file = new File(sourceURL);
    }

    /**
     * Gets the File instance.
     * @return the file instance (possibly null).
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file instance.
     * @param file the new file instance.
     */
    public void setFile(File file) {
        this.file = file;
    }
}
