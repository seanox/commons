/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt. Diese
 *  Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2013 Seanox Software Solutions
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.seanox.common.Codec;
import com.seanox.common.Components;

/**
 *  Fragment stellt Methoden f&uuml;r den Zugriff auf die Informationen und den
 *  Inhalt von Multipart-Objekten zur Verf&uuml;gung. Zur Information
 *  k&ouml;nnen die aktuellen Daten formatiert ausgegeben werden.<br>
 *  <br>
 *  Fragment 1.2013.0314<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0314
 */
public class Fragment implements Serializable {

    /** Auslagerungsdatei */
    private File storage;

    /** Hashtable der Felder */
    private Hashtable fields;

    /** Hashtable der Parameter */
    private Hashtable parameters;

    /** Liste der Felder */
    private String fieldslist;

    /** ByteArray */
    private byte[] content;

    /** Versionskennung f&uuml;r die Serialisierung */
    private static final long serialVersionUID = 8601928313940307418L;

    /** Konstruktor, richtet das Fragment ein. */
    private Fragment() {

        this.fields     = new Hashtable();
        this.parameters = new Hashtable();

        this.content    = new byte[0];

        this.fieldslist = "";
    }

    /**
     *  Parst das ByteArray und richtet das Fragment ein.
     *  @param  bytes zu parsendes Byte Array
     *  @return das eingerichtete Fragment
     */
    public static Fragment create(byte[] bytes) {

        StringTokenizer tokenizer;
        StringTokenizer parameter;
        Fragment        fragment;
        String          entry;
        String          stream;
        String          string;

        Object[]        entries;
        Object[]        objects;
        String[]        streams;
        String[]        strings;

        boolean         control;
        int             cursor;
        int             loop;

        if (bytes == null) return new Fragment();

        //das Fragment wird eingerichtet
        fragment = new Fragment();

        //die Position des Headers wird ermittelt HEADER\r\n\r\nDATA
        for (loop = cursor = 0; loop < bytes.length && cursor < 4; loop++) {

            cursor = (bytes[loop] == ((cursor % 2) == 0 ? 13 : 10)) ? cursor +1 : 0;
        }

        //die Position wird konkretisiert
        cursor = (cursor == 4) ? loop : bytes.length;

        //der Speicher fuer den Fragment Inhalt wird eingerichtet
        fragment.content = new byte[bytes.length -cursor];

        //der Datenbereich wird als Fragment Inhalt uebernommen
        System.arraycopy(bytes, cursor, fragment.content, 0, bytes.length -cursor);

        //die Parameterzeilen werden ermittelt
        tokenizer = new StringTokenizer(new String(bytes, 0, cursor), "\r\n");

        while (tokenizer.hasMoreElements()) {

            string = tokenizer.nextToken();
            cursor = string.indexOf(':');
            cursor = (cursor < 0) ? string.length() : cursor;

            entry  = string.substring(0, cursor).trim();
            string = (string.length() > cursor) ? string.substring(cursor +1).trim() : "";

            if (entry.length() == 0) continue;

            //die Feldliste wird ermittelt
            stream = fragment.fieldslist.concat("\r\n").concat(entry).trim();

            //die Feldliste wird uebernommen, wenn das Feld noch nicht existiert
            if (!fragment.containsField(entry)) fragment.fieldslist = stream;

            //das Array der Parametereintraege wird aufgebaut
            streams = (String[])fragment.fields.get(entry.toLowerCase());
            streams = (streams == null) ? new String[0] : streams;
            strings = new String[streams.length +1];

            //der Parameter wird als ByteArray uebernommen
            System.arraycopy(streams, 0, strings, 0, streams.length);
            strings[streams.length] = string;

            //der Parametereintrag uebernommen
            fragment.fields.put(entry.toLowerCase(), strings);

            //die Parmameter werden ermittelt
            parameter = new StringTokenizer(string, ";");
            control   = (string.indexOf(';') < 0);

            while (parameter.hasMoreTokens() && !control) {

                //der Name und der Wert wird ermittelt
                string = parameter.nextToken();
                cursor = string.indexOf('=');
                stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
                stream = stream.startsWith("\"") ? stream.substring(1) : stream;
                stream = stream.endsWith("\"") ? stream.substring(0, stream.length() -1) : stream;
                string = (cursor < 0) ? string.trim() : string.substring(0, cursor).trim();
                entry  = Codec.decode(string, Codec.MIME).trim();
                entry  = (entry.length() == 0 && string.length() > 0) ? string : entry;

                if (entry.length() == 0) continue;

                //das Array der Parametereintraege wird aufgebaut
                entries = (Object[])fragment.parameters.get(entry);
                entries = (entries == null) ? new Object[0] : entries;
                objects = new Object[entries.length +1];

                //der Parameter wird als ByteArray uebernommen
                System.arraycopy(entries, 0, objects, 0, entries.length);
                objects[entries.length] = Codec.decode(stream.getBytes(), Codec.MIME);

                //der Parametereintrag uebernommen
                fragment.parameters.put(entry, objects);
            }
        }

        return fragment;
    }

    /**
     *  Pr&uuml;ft ob das angegebene Feld im Fragment enthalten. Bei der
     *  Ermittlung wird die Gross- und Kleinschreibung ignoriert. R&uuml;ckgabe
     *  <code>true</code>, wenn das Feld im Fragment existiert. Sonst wird
     *  <code>false</code> zur&uuml;ckgegeben.
     *  @param  name Name des Felds
     *  @return <code>true</code>, wenn das Feld im Fragment enthalten ist
     */
    public boolean containsField(String name) {

        return (name == null || name.indexOf(':') >= 0) ? false : this.fields.containsKey(name.toLowerCase().trim());
    }

    /**
     *  R&uuml;ckgabe aller gesetzten Felder im Fragment als Enumeration.
     *  @return alle gesetzten Felder im Fragment als Enumeration
     */
    public Enumeration getFields() {

        StringTokenizer tokenizer;
        Vector          vector;

        //der Vektor wird eingerichtet
        vector = new Vector();

        //die Felder werden ermittelt
        tokenizer = new StringTokenizer(this.fieldslist, "\r\n");

        while (tokenizer.hasMoreTokens()) vector.addElement(tokenizer.nextToken());

        return vector.elements();
    }

    /**
     *  Ermittelt den Wert des angegebenen Felds im Fragment. R&uuml;ckgabe, der
     *  Wert des angegebenen Felds. Kann dieses nicht ermittelt werden, wird ein
     *  leerer String zur&uuml;ckgegeben. Sind mehrere gleichnamige Felder im
     *  Fragment enthalten, wird der Wert des ersten Felds zur&uuml;ckgegeben.
     *  F&uuml;r die Ermittlung aller Werte eines Felds steht die Methode
     *  <code>Fragment.getFields(String name)</code> und zur Pr&uuml;fung ob das
     *  Feld im Fragment enthalten ist, steht die Methode
     *  <code>Fragment.containsField(String name)</code> zur Verf&uuml;gung.
     *  @param  name Name Felds
     *  @return der Wert des angegebenen Felds
     */
    public String getField(String name) {

        String[] result;

        //der Inhalt des Felds wird ermittelt
        result = this.getFields(name);

        return (result.length == 0) ? "" : result[0];
    }

    /**
     *  Ermittelt die Werte des angegebenen Feldes im Fragment. R&uuml;ckgabe,
     *  die ermittelten Werte als String Array. Kann das Feld nicht ermittelt
     *  werden, wird ein leeres Array zur&uuml;ckgegeben. Zur Ermittlung ob das
     *  Feld im Fragment enthalten ist, steht die Methode
     *  <code>Fragment.containsField(String name)</code> zur Verf&uuml;gung.
     *  @param  name Name des Felds
     *  @return die Werte des Felds als String Array
     */
    public String[] getFields(String name) {

        String[] result;

        //der Feldname wird fuer die Suche im Fragment vereinfacht
        name = (name == null) ? "" : name.trim().toLowerCase();
        name = (name.indexOf(':') >= 0) ? "" : name;

        //der Inhalt des Felds wird ermittelt
        result = (String[])this.fields.get(name);
        result = (result == null) ? new String[0] : result;

        return (String[])result.clone();
    }

    /**
     *  Pr&uuml;ft ob der angegebene Parameter im Fragment enthalten ist.
     *  R&uuml;ckgabe <code>true</code>, sonst <code>false</code>.
     *  @param  name Name des Parameters
     *  @return <code>true</code>, wenn der Parameter im Fragment enthalten ist
     */
    public boolean containsParameter(String name) {

        return (name == null) ? false : this.parameters.containsKey(name.trim());
    }

    /**
     *  R&uuml;ckgabe des aller Parameter des Fragments als Enumeration.
     *  @return alle Parameter als Enumeration
     */
    public Enumeration getParameters() {

        return this.parameters.keys();
    }

    /**
     *  Ermittelt den Wert des angegebenen Parameters im Fragment, sonst wird
     *  ein leerer String zur&uuml;ckgegeben. Zur Ermittlung ob der Parameter im
     *  Fragment enthalten ist, steht die Funktion
     *  <code>Fragment.containsParameter(String name)</code> zur Verf&uuml;gung.
     *  Wurden dem Parameter im Fragment mehrfach Werte zugewiesen, wird immer
     *  der erste Wert zur&uuml;ckgegeben. F&uuml;r die Ermittlung aller Werte
     *  eines Parameters steht die Methode
     *  <code>Fragment.getParameters(String name)</code> zur Verf&uuml;gung.
     *  @param  name Name des Parameters
     *  @return der Wert des Parameters als String
     */
    public String getParameter(String name) {

        //der Wert des Parameters wird ermittelt
        return new String(this.getByteField(name));
    }

    /**
     *  Ermittelt alle Werte des angegebenen Parameters im Fragment als String
     *  Array. Kann der Parameter nicht ermittelt werden, wird ein leeres Array
     *  zur&uuml;ckgegeben. Zur Ermittlung ob der Parameter enthalten ist steht
     *  die Methode <code>Fragment.containsParameter(String name)</code> zur
     *  Verf&uuml;gung.
     *  @param  name Name des Parameters
     *  @return der Wert des Parameters als String Array
     */
    public String[] getParameters(String name) {

        Object[] objects;
        String[] fields;
        String[] result;

        int      loop;

        //das StringArray wird eingerichtet
        result = new String[0];

        //die Werte des Parameters werden ermittelt
        objects = (name == null) ? null : (Object[])this.parameters.get(name);

        for (loop = 0; objects != null && loop < objects.length; loop++) {

            name = new String((byte[])objects[loop]);

            fields = new String[result.length +1];
            System.arraycopy(result, 0, fields, 0, result.length);
            fields[result.length] = name;
            result = fields;
        }

        return result;
    }

    /**
     *  Pr&uuml;ft ob der angegebene Parameter im Fragment enthalten ist.
     *  R&uuml;ckgabe <code>true</code>, sonst <code>false</code>.
     *  @param  name Name des Parameters
     *  @return <code>true</code>, wenn der Parameters enthalten ist
     */
    public boolean containsByteField(String name) {

        return (name == null) ? false : this.parameters.containsKey(name.trim());
    }

    /**
     *  R&uuml;ckgabe der Werte als Bytes aller Parameter des Fragment.
     *  @return alle Werte der Parameter als Enumeration
     */
    public Enumeration getByteFields() {

        return this.parameters.keys();
    }

    /**
     *  Ermittelt den Wert als Bytes zum angegebenen Parameters im Fragment.
     *  Kann der Parameter nicht ermittelt werden, wird ein leeres Byte-Array
     *  zur&uuml;ckgegeben. Zur Ermittlung, ob der Parameter enthalten ist, steht
     *  die Methode <code>Fragment.containsByteField(String name)</code> zur
     *  Verf&uuml;gung. Wurden dem Parameter im Fragment mehrfach Werte
     *  zugewiesen, wird immer der erste Wert zur&uuml;ckgegeben. F&uuml;r die
     *  Ermittlung aller Werte eines Parameters steht die Funktion
     *  <code>Fragment.getByteFields(String name)</code> zur Verf&uuml;gung.
     *  @param  name Name des Parameters
     *  @return der Wert als Byte-Array
     */
    public byte[] getByteField(String name) {

        Object[] result;

        //die Werte des Parameters werden ermittelt
        result = this.getByteFields(name);

        return (result.length == 0) ? new byte[0] : (byte[])result[0];
    }

    /**
     *  Ermittelt alle Werte als Bytes zum angegebenen Parameters im Fragment.
     *  Kann der Parameter nicht ermittelt werden, wird ein leeres Array
     *  zur&uuml;ckgegeben. Zur Ermittlung, ob der Parameter enthalten ist, steht
     *  die Methode <code>Fragment.containsParameter(String name)</code> zur
     *  Verf&uuml;gung.
     *  @param  name Name des Parameters
     *  @return alle Werte des Parameters als Objekt-Array
     */
    public Object[] getByteFields(String name) {

        Object[] result;

        //die Werte des Parameters werden ermittelt
        result = (name == null) ? null : (Object[])this.parameters.get(name);

        if (result == null) result = new Object[0];

        return (Object[])result.clone();
    }

    /**
     *  R&uuml;ckgabe vom Inhalt des Fragments als InputStream.
     *  @return der Inhalt des Fragments als InputStream
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public InputStream getContent() throws IOException {

        if (this.storage != null) return new FileInputStream(this.storage);

        return new ByteArrayInputStream(this.content);
    }

    /**
     *  R&uuml;ckgabe der Gr&ouml;sse des Inhalts in Bytes.
     *  @return die Gr&ouml;sse des Inhalts in Bytes
     */
    public long getLength() {

        if (this.storage != null) return this.storage.length();

        return this.content.length;
    }

    /**
     *  R&uuml;ckgabe der dem Fragment zugeordneten Auslagerungsdatei.
     *  @return der dem Fragment zugeordneten Auslagerungsdatei
     */
    public File getStorage() {

        return this.storage;
    }

    /**
     *  Optimiert den String f&uuml;r die direkte Ausgabe.
     *  @param  string zu optimierender String
     *  @return der optimierte String
     */
    private static String optimizeString(String string) {

        return (string == null) ? "[null]" : Components.strcln(string).trim();
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zum Fragment als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zum Fragment als String
     */
    public String toString() {

        Enumeration fields;
        String      buffer;
        String      entry;
        String      stream;
        String      string;

        //der Zeilenumbruch wird ermittelt
        stream = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        string = ("[").concat(this.getClass().getName()).concat("]").concat(stream);
        string = string.concat("  content    = ").concat(String.valueOf(this.getLength())).concat(" Bytes").concat(stream);

        //die Parameter werden ermittelt
        fields = this.getFields();

        for (buffer = ""; fields.hasMoreElements();) {

            entry  = (String)fields.nextElement();
            buffer = buffer.concat(Fragment.optimizeString(entry));
            buffer = buffer.concat(": ").concat(Fragment.optimizeString(this.getField(entry)));
            buffer = buffer.concat(this.getFields(entry).length > 1 ? ", ..." : "");
            buffer = buffer.concat(stream).concat("               ");
        }

        string = string.concat("  fields     = ").concat(buffer.trim()).concat(stream);

        //die Parameter werden ermittelt
        fields = this.getParameters();

        for (buffer = ""; fields.hasMoreElements();) {

            entry  = (String)fields.nextElement();
            buffer = buffer.concat(Fragment.optimizeString(entry));
            buffer = buffer.concat(": ").concat(Fragment.optimizeString(this.getParameter(entry)));
            buffer = buffer.concat(this.getParameters(entry).length > 1 ? ", ..." : "");
            buffer = buffer.concat(stream).concat("               ");
        }

        string = string.concat("  parameters = ").concat(buffer.trim()).concat(stream);

        //der Pfad der Auslagerungsdatei wird ermittelt
        buffer = (this.storage == null) ? "" : this.storage.toString();
        buffer = Codec.decode(buffer, Codec.DOT);
        buffer = (buffer == null || buffer.length() == 0) ? "none" : buffer;

        string = string.concat("  storage    = ").concat(buffer).concat(stream);

        return string;
    }
}