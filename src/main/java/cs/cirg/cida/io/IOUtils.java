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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Utility methods used in the I/O packages.
 * @author andrich
 */
public class IOUtils {

    public static final int KILOBYTE = 1024;
    public static final int MEGABYTE = KILOBYTE * KILOBYTE;
    public static int characterBufferSize = 4 * KILOBYTE;

    /**
     * Writes a variable number of string to the given ByteBuffer.
     * @param buffer the ByteBuffer to put data in.
     * @param strings the string to write to the buffer.
     * @throws java.nio.charset.CharacterCodingException An encoding exception
     * that could occur during the writing of the strings.
     */
    public static void writeStrings(ByteBuffer buffer, String... strings) throws CharacterCodingException {
        for (String string : strings) {
            CharBuffer cb = CharBuffer.allocate(characterBufferSize);
            cb.put(string);
            cb.flip();
            Charset cs = Charset.defaultCharset();
            CharsetEncoder cse = cs.newEncoder();
            buffer.put(cse.encode(cb));
        }
    }

    /**
     * Writes the given string to the given ByteBuffer.
     * @param buffer the buffer to write to.
     * @param string the string to write the buffer to.
     * @throws java.nio.charset.CharacterCodingException An encoding exception
     * that could occur during the writing of the strings.
     */
    public static void writeString(ByteBuffer buffer, String string) throws CharacterCodingException {
        CharBuffer cb = CharBuffer.allocate(characterBufferSize);
        cb.put(string);
        cb.flip();
        Charset cs = Charset.defaultCharset();
        CharsetEncoder cse = cs.newEncoder();
        buffer.put(cse.encode(cb));
    }

    /**
     * Writes the given string as a new line to the ByteBuffer.
     * @param buffer the buffer to write to.
     * @param string the line to write.
     * @throws java.nio.charset.CharacterCodingException An encoding exception
     * that could occur during the writing of the strings.
     */
    public static void writeLine(ByteBuffer buffer, String string) throws CharacterCodingException {
        CharBuffer cb = CharBuffer.allocate(characterBufferSize);
        cb.put(string);
        cb.put('\n');
        cb.flip();
        Charset cs = Charset.defaultCharset();
        CharsetEncoder cse = cs.newEncoder();
        buffer.put(cse.encode(cb));
    }

    /**
     * Gets the buffer size of the temporary buffer used during writing.
     * @return the character buffer size.
     */
    public static int getCharacterBufferSize() {
        return characterBufferSize;
    }

    /**
     * Sets the buffer size of the temporary buffer used during writing.
     * @param characterBufferSize the new character buffer size.
     */
    public static void setCharacterBufferSize(int characterBufferSize) {
        IOUtils.characterBufferSize = characterBufferSize;
    }
}
