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
package com.seanox.network.http;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 *  Content, stellt ein Objekt f&uuml;r den einfachen Zugriff auf die Felder,
 *  welche die Daten des HTTP-Response zur Verf&uuml;gung.<br>
 *  <br>
 *  Content 1.2011.0803<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2011.0803
 */
public class Content implements Serializable {

    /** Liste der Felder und Werte */
    private Hashtable fields;

    /** Puffer der Headerinformationen */
    private String header;

    /** Puffer des Datenbereiches */
    private byte[] data;

    /** Status beim kompletten Header */
    public final boolean complete;

    /** Status bei korrektem Header */
    public final boolean correct;

    /** Konstante f&uuml;r das Header-Feld Fistline */
    public static final String FIELD_HEADER_REQUEST = "HEADER-REQUEST";

    /** Konstante f&uuml;r das Header-Feld Protokoll */
    public static final String FIELD_HEADER_PROTOCOL = "HEADER-PROTOCOL";

    /** Konstante f&uuml;r das Header-Feld Text */
    public static final String FIELD_HEADER_STATUS = "HEADER-STATUS";

    /** Konstante f&uuml;r das Header-Feld Text */
    public static final String FIELD_HEADER_TEXT = "HEADER-TEXT";

    /** Konstante f&uuml;r das Header-Feld Version */
    public static final String FIELD_HEADER_VERSION = "HEADER-VERSION";

    /** Versionskennung f&uuml;r die Serialisierung */
    private static final long serialVersionUID = 8628747505341184937L;

    /**
     *  Konstruktor, richtet das Content Objekt mit ByteArray ein. F&uuml;r die
     *  Einrichtung wird der normale Aufbau eines HTTP-Response erwartet.
     *  <code>HEADER[CRLF]...[CRLF][CRLF]BODY</code>
     *  @param bytes Basisdaten als Bytearray
     */
    public Content(byte[] bytes) {

        StringTokenizer tokenizer;
        String          string;

        boolean         control;
        int             loop;
        int             pointer;
        int             size;

        //Data und Header werden eingerichtet
        this.data   = new byte[0];
        this.fields = new Hashtable();
        this.header = new String();

        //null wird wie leerere Daten behandelt
        if (bytes == null) bytes = new byte[0];

        //die Position des Headers wird ermittelt, die Daten werden solange
        //gelesen bis \r\n\r\n im Datenarray ermittelt werden
        size = bytes.length -1;

        for (loop = pointer = 0; loop <= size && pointer < 4; loop++) {

            pointer = (bytes[loop] == ((pointer % 2) == 0 ? 13 : 10)) ? pointer +1 : 0;
        }

        //ist der Header komplett wird der entsprechende Status gesetzt
        this.complete = (pointer == 4);

        //die Position wird ermittelt, wurde der gesuchte Eintrag nicht gefunden
        //wird die Position auf -1 gesetzt
        pointer = (pointer == 4) ? loop : -1;

        //der Header wird gesetzt, wurde zuvor der gesuchte Eintrag nicht
        //gefunden wird davon ausgegangen das der Header sich auf die gesamt
        //Laenge bezieht der Datenbereich wird vom verbleibenden Rest gesetzt

        //der Datenpuffer wird neu dimensioniert
        this.data = new byte[size -((pointer < 0) ? size : pointer -1)];

        //der Request Body wird uebernommen
        System.arraycopy(bytes, (pointer < 0) ? size : pointer, this.data, 0, size -((pointer < 0) ? size : pointer -1));

        //der Request Header wird ermittelt
        string = new String(bytes, 0, (pointer < 0) ? size +1 : pointer -4);

        //der Header wird fuer die Parameterabfragen optimiert
        //die Tokens werden aus dem Header ermittelt
        tokenizer = new StringTokenizer(string, "\r\n");

        //die einzelnen Eintraege werden getrimmt
        for (control = true; tokenizer.hasMoreTokens();) {

            string = tokenizer.nextToken().trim();

            if (string.length() > 32768) {

                string = string.substring(0, 32768);

                control = false;
            }

            if (string.length() > 0) this.header = this.header.concat(string).concat("\r\n");
        }

        //ist der Header korrekt wird der entsprechende Status gesetzt
        this.correct = (this.complete && control);

        //die Felder werden ermittelt
        this.initialize();
    }

    /** Initialisiert alle Felder vom Response. */
    private void initialize() {

        StringTokenizer tokenizer;
        String          buffer;
        String          stream;
        String          string;

        String[]        strings;
        String[]        streams;

        int             cursor;

        //die Zeilen des Headers werden ermittelt
        tokenizer = new StringTokenizer(this.header, "\r\n");

        //die erste Zeile wird zwischengespeichert
        buffer = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";

        //die Parameter werden aus allen Zeilen ermittelt ":"
        while (tokenizer.hasMoreTokens()) {

            string = tokenizer.nextToken();
            cursor = string.indexOf(':');
            stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
            string = (cursor < 0) ? string.trim() : string.substring(0, cursor).trim().toLowerCase();

            //Leerstrings werden nicht beruecksichtigt
            if (string.length() > 0) {

                strings = (String[])this.fields.get(string);

                if (strings == null) strings = new String[0];

                streams = new String[strings.length +1];
                System.arraycopy(strings, 0, streams, 0, strings.length);
                streams[strings.length] = stream;
                strings = streams;

                this.fields.put(string, strings);
            }
        }

        //die komplette erste Zeile des des Headers wird ermittelt und gesetzt
        this.fields.put(Content.FIELD_HEADER_REQUEST.toLowerCase(), buffer);

        cursor = buffer.indexOf(' ');
        stream = (cursor < 0) ? buffer.trim() : buffer.substring(0, cursor).trim().toUpperCase();
        string = (cursor < 0) ? "" : buffer.substring(cursor +1).trim();

        cursor = stream.indexOf('/');

        //das Protokoll des Headers wird ermittelt und als Feld gesetzt
        this.fields.put(Content.FIELD_HEADER_PROTOCOL.toLowerCase(), (cursor < 0) ? "" : stream.substring(0, cursor));

        //die Version des Headers wird ermittelt und als Feld gesetzt
        this.fields.put(Content.FIELD_HEADER_VERSION.toLowerCase(), (cursor < 0) ? "" : stream.substring(cursor +1));

        cursor = string.indexOf(' ');
        stream = (cursor < 0) ? string.trim() : string.substring(0, cursor).trim();
        string = (cursor < 0) ? "" : string.substring(cursor +1).trim();

        //der Status des Headers wird ermittelt und als Feld gesetzt
        this.fields.put(Content.FIELD_HEADER_STATUS.toLowerCase(), stream);

        //der Text des Headers wird ermittelt und als Feld gesetzt
        this.fields.put(Content.FIELD_HEADER_TEXT.toLowerCase(), string);
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn der Header komplett ist.
     *  @return <code>true</code> wenn der Header komplett ist
     */
    public boolean isComplete() {

        return this.complete;
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn der Header korrekt &uuml;bergeben
     *  wurde. Zur Ermittlung wird die maximal zul&auml;ssige L&auml;nge der
     *  Header-Zeilen von 32768 Zeichen gepr&uuml;ft. Mit &uuml;berschreiten
     *  dieser Gr&ouml;sse wird <code>false</code> zur&uuml;ckgegeben.
     *  @return <code>true</code> wenn der Header g&uuml;ltig ist
     */
    public boolean isCorrect() {

        return this.correct;
    }

    /**
     *  R&uuml;ckgabe vom Wert des angegebenen Felds oder ein leerer String,
     *  wenn das Feld nicht im Header vom Content enthalten ist. Die Gross- und
     *  Kleinschreibung wird beim Feldnamen ignoriert.
     *  @param  name Name des Felds
     *  @return der Wert des Felds oder ein leerer String
     */
    public String getField(String name) {

        String[] strings;

        strings = this.getFields(name);

        return (strings.length > 0) ? strings[0] : "";
    }

    /**
     *  R&uuml;ckgabe aller Werte des angegebenen Feldnamens oder ein leeres
     *  String Array, wenn das Feld nicht im Header vom Content enthalten ist.
     *  Die Gross- und Kleinschreibung wird beim Feldnamen ignoriert.
     *  @param  name Name des Felds
     *  @return die Werte der enstsprechenden Felder als String-Array
     */
    public String[] getFields(String name) {

        Object object;

        if (name == null || name.trim().length() == 0) return new String[0];

        object = this.fields.get(name.trim().toLowerCase());

        if (object == null) object = "";

        if (object instanceof String && ((String)object).trim().length() == 0) return new String[0];

        return (object.getClass().isArray()) ? (String[])object : new String[] {(String)object};
    }

    /**
     *  R&uuml;ckgabe aller Felder des Headers vom Content als String Array.
     *  @return alle Felder des Content als String Array
     */
    public String[] getFields() {

        Enumeration enumeration;
        String      stream;

        String[]    strings;
        String[]    streams;

        if (this.header == null || this.header.trim().length() == 0) return new String[0];

        enumeration = this.fields.keys();
        strings     = new String[0];

        while (enumeration.hasMoreElements()) {

            stream = ((String)enumeration.nextElement()).trim();

            if (stream.length() == 0) continue;

            streams = new String[strings.length +1];
            System.arraycopy(strings, 0, streams, 0, strings.length);
            streams[strings.length] = stream;
            strings = streams;
        }

        return strings;
    }

    /**
     *  Pr&uuml;ft ob das angegebene Feld im Header vom Content enthalten ist.
     *  Die Gross- und Kleinschreibung wird beim Feldnamen ignoriert.
     *  R&uuml;ckgabe <code>true</code> wenn das Feld enthalten ist.
     *  @param  name Feldname
     *  @return <code>true</code> wenn das Feld enthalten ist
     */
    public boolean containsField(String name) {

        if (name == null || name.trim().length() == 0) return false;

        if (this.header == null || this.header.trim().length() == 0) return false;

        return this.fields.containsKey(name.toLowerCase().trim());
    }

    /**
     *  R&uuml;ckgabe aller Cookies des Content als Cookie Array.
     *  @return alle Cookies des Content als Cookie Array
     */
    public Cookie[] getCookies() {

        Cookie          cookie;
        StringTokenizer tokenizer;
        String          string;
        String          stream;

        Cookie[]        array;
        Cookie[]        cookies;
        String[]        strings;

        int             cursor;
        int             loop;

        //die Cookies werden ermittelt
        strings = this.getFields("set-cookie");

        //das Cookie Array wird eingerichtet
        cookies = new Cookie[0];

        for (loop = 0; loop < strings.length; loop++) {

            //die Elemente des Cookies werden ermittelt
            tokenizer = new StringTokenizer(strings[loop], ";");

            //der Wert und der Name werden ermittelt
            string = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
            cursor = string.indexOf('=');
            stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
            string = (cursor < 0) ? string.trim() : string.substring(0, cursor).trim();

            if (string.length() == 0) continue;

            //der Cookie wird eingerichtet
            cookie = new Cookie(string, stream);

            while (tokenizer.hasMoreTokens()) {

                //der Wert und der Feldname werden ermittelt
                string = tokenizer.nextToken();
                cursor = string.indexOf('=');
                stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
                string = (cursor < 0) ? string.trim() : string.substring(0, cursor).trim();
                string = string.toLowerCase();

                //die Werte werden entsprechend der Feldnamen uebernommen
                if (string.equals("path")) cookie.setPath(stream);
                else if (string.equals("domain")) cookie.setDomain(stream);
                else if (string.equals("secure")) cookie.setSecure(true);
                else if (string.equals("expires")) {

                    try {cookie.setExpire(new SimpleDateFormat("E, dd-MMM-yy HH:mm:ss z", Locale.US).parse(stream).getTime());
                    } catch (Exception exception) {

                        //keine Fehlerbehandlung vorgesehen
                    }
                }
            }

            //das Array von Cookies wird aufgebaut
            array = new Cookie[cookies.length +1];
            System.arraycopy(cookies, 0, array, 0, cookies.length);
            array[cookies.length] = cookie;
            cookies = array;
        }

        return cookies;
    }

    /**
     *  R&uuml;ckgabe des Headers.
     *  @return der Header
     */
    public byte[] getHeader() {

        return this.header.getBytes();
    }

    /**
     *  R&uuml;ckage des Datenbereiches.
     *  @return der Datenbereich
     */
    public byte[] getData() {

        return (byte[])this.data.clone();
    }
}