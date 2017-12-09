/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt. Diese
 *  Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2012 Seanox Software Solutions
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
package com.seanox.xml;

import java.io.IOException;
import java.io.OutputStream;

/**
 *  Stream stellt einen Datenstrom f&uuml;r die Ausgabe von XML Daten zur
 *  Verf&uuml;gung.<br>
 *  <br>
 *  <b>Hinweis</b> - diese Klasse basiert auf den Quellen der Datei
 *  XMLWriter.java von Remy Maucherat aus dem org.apache.catalina.util Paket
 *  des WebDAV-Projekts (http://webdav-servlet.sourceforge.net/index.html).<br>
 *  <br>
 *  Stream 1.2013.0301<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0301
 */
public class Stream {

    /** Datenausgabestrom */
    private OutputStream output;

    /** zu verwendendes Encoding */
    private String encoding;

    /** Konstante f&uuml;r ein &ouml;ffnendes Element */
    public static final int OPENING = 0;

    /** Konstante f&uuml;r ein schliessendes Element */
    public static final int CLOSING = 1;

    /** Konstante f&uuml;r ein schliessendes Element ohne Inhalt */
    public static final int EMPTY = 2;

    /**
     *  Konstruktor, richtet den XML-Stream f&uuml;r UTF-8 ein.
     *  @param output Datenausgabestrom.
     */
    public Stream(OutputStream output) {

        this.output = output;

        this.encoding = "UTF-8";
    }

    /**
     *  Konstruktor, richtet den XML-Stream mit dem angegebenen Encoding ein.
     *  @param output   Datenausgabestrom.
     *  @param encoding zu verwendendes Encoding
     */
    public Stream(OutputStream output, String encoding) {

        this.output = output;

        this.encoding = encoding == null ? "" : encoding.trim();

        if (this.encoding.length() == 0) this.encoding = "UTF-8";
    }

    /**
     *  Schreibt einen XML Parameter in den Datenstrom.
     *  @param  space Namespace (Kurzform)
     *  @param  note  Namespace Info
     *  @param  name  Element Name
     *  @param  value Element Wert
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeProperty(String space, String note, String name, String value) throws IOException {

        this.writeElement(space, note, name, OPENING);
        this.output.write(String.valueOf(value).getBytes(this.encoding));
        this.writeElement(space, note, name, CLOSING);
    }

    /**
     *  Schreibt einen XML Parameter in den Datenstrom.
     *  @param  space Namespace (Kurzform)
     *  @param  name  Element Name
     *  @param  value Element Wert
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeProperty(String space, String name, String value) throws IOException {

        this.writeElement(space, name, OPENING);
        this.output.write(String.valueOf(value).getBytes(this.encoding));
        this.writeElement(space, name, CLOSING);
    }

    /**
     *  Schreibt einen XML Parameter als Datensegment in den Datenstrom.
     *  @param  space Namespace (Kurzform)
     *  @param  name  Element Name
     *  @param  value Element Wert
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writePropertyData(String space, String name, String value) throws IOException {

        this.writeElement(space, name, OPENING);
        this.output.write(("<![CDATA[").concat(String.valueOf(value)).concat("]]>").getBytes(this.encoding));
        this.writeElement(space, name, CLOSING);
    }

    /**
     *  Schreibt einen XML Parameter in den Datenstrom.
     *  @param  space Namespace (Kurzform)
     *  @param  name  Element Name
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeProperty(String space, String name) throws IOException {

        this.writeElement(space, name, EMPTY);
    }

    /**
     *  Schreibt ein XML Element in den Datenstrom.
     *  @param  space Namespace (Kurzform)
     *  @param  name  Element Name
     *  @param  type  Element Typ
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeElement(String space, String name, int type) throws IOException {

        this.writeElement(space, null, name, type);
    }

    /**
     *  Schreibt ein XML Element in den Datenstrom.
     *  @param  space Namespace (Kurzform)
     *  @param  note  Namespace Info
     *  @param  name  Element Name
     *  @param  type  Element Typ
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeElement(String space, String note, String name, int type) throws IOException {

        String close;
        String open;

        switch (type) {

            case OPENING: open = "<";  close = ">";  break;
            case CLOSING: open = "</"; close = ">";  break;
            case EMPTY:   open = "<";  close = "/>"; break;
            default:      open = "<";  close = "/>"; break;
        }

        if (space == null || space.length() == 0) {

            open = open.concat(String.valueOf(name));

        } else {

            open = open.concat(space).concat(":").concat(String.valueOf(name));

            if (note != null) open = open.concat(" xmlns:").concat(space).concat("=\"").concat(String.valueOf(note));
        }

        this.output.write(open.concat(close).getBytes(this.encoding));
    }

    /**
     *  Schreibt den &uuml;bergebenen String als Text in den Datenstrom.
     *  @param  text Text
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeText(String text) throws IOException {

        this.output.write(String.valueOf(text).getBytes(this.encoding));
    }

    /**
     *  Schreibt den &uuml;bergebenen String als Datensegment in den Datenstrom.
     *  @param  data Inhalt des Datensegments
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeData(String data) throws IOException {

        this.output.write(("<![CDATA[").concat(String.valueOf(data)).concat("]]>").getBytes(this.encoding));
    }

    /**
     *  Schreibt den XML-Header in den Datenstrom.
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void writeXmlHeader() throws IOException {

        this.output.write(("<?xml version=\"1.0\" encoding=\"").concat(this.encoding).concat("\"?>").getBytes(this.encoding));
    }

    /**
     *  Schreibt im Ausgabepuffer des Stream vorgehaltene Daten in Datenstrom.
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void flush() throws IOException {

        this.output.flush();
    }

    /**
     *  Schliesst den Datenstrom des Streams.
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void close() throws IOException {

        this.output.close();
    }
}