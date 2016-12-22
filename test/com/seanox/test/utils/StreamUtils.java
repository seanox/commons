/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt. Diese
 *  Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2016 Seanox Software Solutions
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of version 2 of the GNU General Public License as published
 *  by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.seanox.test.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  Utils for streams.
 */
public class StreamUtils {

    /**
     *  Reads all bytes from a data stream.
     *  @param  input input stream
     *  @return readed bytes as array
     *  @throws IOException
     *      In case of incorrect access to the data stream
     */
    public static byte[] read(InputStream input)
            throws IOException {
        return StreamUtils.read(input, true);
    }
    
    /**
     *  Reads all bytes from a data stream.
     *  @param  input input stream
     *  @param  smart reads until the data stream no longer supplies data.
     *  @return readed bytes as array
     *  @throws IOException
     *      In case of incorrect access to the data stream
     */
    public static byte[] read(InputStream input, boolean smart)
            throws IOException {

        if (!(input instanceof BufferedInputStream))
            input = new BufferedInputStream(input);

        byte[] bytes = new byte[65535];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int size;
        while ((size = input.read(bytes)) >= 0) {
            result.write(bytes, 0, size);
            if (smart && input.available() <= 0
                    && result.size() > 0)
                break;
        } 
        
        return result.toByteArray();
    }
}