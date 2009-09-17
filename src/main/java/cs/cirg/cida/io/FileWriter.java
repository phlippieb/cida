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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Abstract data writer that writes data to a local file.
 * @author andrich
 */
public abstract class FileWriter implements DataWriter {

    protected File file;
    protected ByteBuffer outputBuffer;
    protected FileChannel outputChannel;
    private int outputBufferSize;

    /**
     * Default constructor. Initializes the output buffer to a default size.
     */
    public FileWriter() {
        outputBufferSize = 32 * IOUtils.MEGABYTE;
    }

    /**
     * Opens the output channel for writing.
     * @throws net.sourceforge.cilib.io.exception.CIlibIOException {@inheritDoc }
     */
    @Override
    public void open() throws CIlibIOException {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            outputChannel = stream.getChannel();
            outputBuffer = ByteBuffer.allocate(outputBufferSize);
        } catch (IOException ex) {
            throw new CIlibIOException(ex);
        }
    }

    /**
     * Closes the output channel that was used for writing.
     */
    @Override
    public void close() {
        try {
            outputChannel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the destination URL that the data is written to as the absolute
     * path of the wrapped file.
     * @return the destination URL.
     */
    @Override
    public String getDestinationURL() {
        return file.getAbsolutePath();
    }

    /**
     * Sets the destination URL that the data is written to, constructs the
     * wrapped file from the URL.
     * @param destinationURL
     */
    @Override
    public void setDestinationURL(String destinationURL) {
        this.file = new File(destinationURL);
    }

    /**
     * Gets the wrapped File object.
     * @return the file object.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the wrapped File object.
     * @param file the file object.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets the output buffer that data is written to.
     * @return the output buffer.
     */
    public ByteBuffer getOutputBuffer() {
        return outputBuffer;
    }

    /**
     * Sets the output buffer that data is written to.
     * @param outputBuffer the output buffer.
     */
    public void setOutputBuffer(ByteBuffer outputBuffer) {
        this.outputBuffer = outputBuffer;
    }

    /**
     * Gets the size of the output buffer used.
     * @return the size of the output buffer.
     */
    public int getOutputBufferSize() {
        return outputBufferSize;
    }

    /**
     * Sets the size of the output buffer used.
     * @param outputBufferSize the size of the output buffer.
     */
    public void setOutputBufferSize(int outputBufferSize) {
        this.outputBufferSize = outputBufferSize;
    }

    /**
     * Gets the output channel used in conjunction with the output buffer to write
     * data.
     * @return the output channel.
     */
    public FileChannel getOutputChannel() {
        return outputChannel;
    }

    /**
     * Sets the output channel used in conjunction with the output buffer to write
     * data.
     * @param outputChannel the output channel.
     */
    public void setOutputChannel(FileChannel outputChannel) {
        this.outputChannel = outputChannel;
    }
}
