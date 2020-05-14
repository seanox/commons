/**
 * LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 * im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 * Diese Software unterliegt der Version 2 der GNU General Public License.
 *
 * Seanox Commons, Advanced Programming Interface
 * Copyright (C) 2020 Seanox Software Solutions
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.seanox.xml;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Writer for the output of XML data.<br>
 * <br>
 * <b>Note</b> - This class is based on the sources of the XMLWriter.java file
 * by Remy Maucherat from the org.apache.catalina.util package of the WebDAV
 * project (http://webdav-servlet.sourceforge.net/index.html)<br>
 * <br>
 * Stream 1.0.0 20130301<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * Alle Rechte vorbehalten.
 *
 * @author  Seanox Software Solutions
 * @version 1.0.0 20130301
 */
public class Writer implements Closeable {

    /** Output stream */
    private OutputStream output;

    /** Encoding to be used */
    private String encoding;
    
    /** Enumeration of element types */
    public enum ElementType {
        
        /** opening element */
        OPENING,
        
        /** closing element */
        CLOSING,
        
        /** Closing element without content */
        EMPTY
    }
    
    /**
     * Constructor, creates the XML writer with UTF-8 encoding.
     * @param output Output stream.
     */
    public Writer(OutputStream output) {
        
        this.output = output;
        this.encoding = "UTF-8";
    }

    /**
     * Constructor, creates the XML writer with the specified encoding.
     * @param output   Output stream.
     * @param encoding Encoding to be used
     */
    public Writer(OutputStream output, String encoding) {
        
        this.output = output;
        this.encoding = encoding == null ? "" : encoding.trim();
        if (this.encoding.length() == 0)
            this.encoding = "UTF-8";
    }
    
    /**
     * Writes an XML parameter into the data stream.
     * @param  space Namespace (short)
     * @param  note  Namespace note
     * @param  name  Element name
     * @param  value Element value
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeProperty(String space, String note, String name, String value)
            throws IOException {
        
        this.writeElement(space, note, name, ElementType.OPENING);
        this.output.write(String.valueOf(value).getBytes(this.encoding));
        this.writeElement(space, note, name, ElementType.CLOSING);
    }

    /**
     * Writes an XML parameter into the data stream.
     * @param  space Namespace (short)
     * @param  name  Element name
     * @param  value Element value
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeProperty(String space, String name, String value)
            throws IOException {
        
        this.writeElement(space, name, ElementType.OPENING);
        this.output.write(String.valueOf(value).getBytes(this.encoding));
        this.writeElement(space, name, ElementType.CLOSING);
    }

    /**
     * Swrites an XML parameter as data segment into the data stream.
     * @param  space Namespace (short)
     * @param  name  Element name
     * @param  value Element value
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writePropertyData(String space, String name, String value)
            throws IOException {
        
        this.writeElement(space, name, ElementType.OPENING);
        this.output.write(("<![CDATA[").concat(String.valueOf(value)).concat("]]>").getBytes(this.encoding));
        this.writeElement(space, name, ElementType.CLOSING);
    }

    /**
     * Writes an XML element into the data stream.
     * @param  space Namespace (short)
     * @param  name  Element name
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeProperty(String space, String name)
            throws IOException {
        this.writeElement(space, name, ElementType.EMPTY);
    }

    /**
     * Writes an XML element into the data stream.
     * @param  space Namespace (short)
     * @param  name  Element name
     * @param  type  Element type
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeElement(String space, String name, ElementType type)
            throws IOException {
        this.writeElement(space, null, name, type);
    }

    /**
     * Writes an XML element into the data stream.
     * @param  space Namespace (short)
     * @param  note  Namespace note
     * @param  name  Element name
     * @param  type  Element type
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeElement(String space, String note, String name, ElementType type)
            throws IOException {

        String close;
        String open;

        switch (type) {

            case OPENING:
                open = "<";  close = ">"; 
                break;
            case CLOSING:
                open = "</"; close = ">"; 
                break;
            case EMPTY:
                open = "<";  close = "/>";
                break;
            default:
                open = "<";  close = "/>";
                break;
        }

        if (space == null
                || space.length() == 0) {
            open = open.concat(String.valueOf(name));
        } else {
            open = open.concat(space).concat(":").concat(String.valueOf(name));
            if (note != null)
                open = open.concat(" xmlns:").concat(space).concat("=\"").concat(String.valueOf(note));
        }

        this.output.write(open.concat(close).getBytes(this.encoding));
    }

    /**
     * Writes the passed string as text into the data stream.
     * @param  text Text
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeText(String text)
            throws IOException {
        this.output.write(String.valueOf(text).getBytes(this.encoding));
    }

    /**
     * Writes the passed string as data segment into the data stream.
     * @param  data Content of the data segment
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeData(String data)
            throws IOException {
        this.output.write(("<![CDATA[").concat(String.valueOf(data)).concat("]]>").getBytes(this.encoding));
    }

    /**
     * Writes the XML header into the data stream.
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void writeXmlHeader()
            throws IOException {
        this.output.write(("<?xml version=\"1.0\" encoding=\"").concat(this.encoding).concat("\"?>").getBytes(this.encoding));
    }

    /**
     * Writes the data in the output buffer to the data stream.
     * @throws IOException
     *     In case of faulty access to the data stream
     */
    public void flush()
            throws IOException {
        this.output.flush();
    }

    @Override
    public void close()
            throws IOException {
        this.output.close();
    }
}