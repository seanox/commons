/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 *  Diese Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Devwex, Advanced Server Developing
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
package com.seanox.common;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *  Generator, generiert Daten durch das Bef&uuml;llen von Platzhaltern (Tags).
 *  Platzhalter k&ouml;nnen dabei einzelne Elemente oder auch komplexe und in
 *  sich verschachtelte Strukturen sein. Elemente und Strukturen lassen sich
 *  dazu klassifizieren. Dabei werden diese namentlich einem Segment zugewiesen,
 *  womit ein gezieltes Bef&uuml;llen der Platzhalter m&ouml;glich ist. Zum
 *  Bef&uuml;llen werden die Werte als Wertetabelle &uuml;bergeben und dann auf
 *  ein Segment angewandt. Das generieren der Daten l&auml;sst sich dabei auf
 *  die gesamte Vorlage oder auch nur auf Teilstrukturen anwenden.<br>
 *  <br>
 *  <b>Beschreibung der Syntax</b><br>
 *  <br>
 *  <table>
 *    <tr>
 *      <td valign="top" nowrap="nowrap">
 *        <code>&lt;set:segment:field&gt;</code>
 *      </td>
 *      <td valign="top">
 *        Setzt an dieser Stelle den Wert f&uuml;r ein bestimmtes Feld aus dem
 *        angegebenen Segment und entfernt den Platzhalter.
 *      </td>
 *    </tr>
 *    <tr>
 *      <td valign="top" nowrap="nowrap">
 *        <code>
 *          &lt;set:segment:sub&gt;<br>
 *          &nbsp;&nbsp;&lt;set:segment:field&gt;<br>
 *          &nbsp;&nbsp;...<br>
 *          &lt;set:segment:end&gt;<br>
 *        </code>
 *      </td>
 *      <td valign="top">
 *        Definiert ein Segment als Struktur. Die Verschachtelung weiterer
 *        Definitionen ist m&ouml;glich. Da die Platzhalter zum Einf&uuml;gen
 *        von Segmenten erhalten bleiben, k&ouml;nnen diese zum Aufbau von
 *        Listen verwendet werden.
 *      </td>
 *    </tr>
 *    <tr>
 *      <td valign="top" nowrap="nowrap">
 *        <code>&lt;set:segment&gt;</code>
 *      </td>
 *      <td valign="top">
 *        Setzt an dieser Stelle den Inhalt des angegebenen Segments. Der
 *        Platzhalter bleibt erhalten und kann so zum Aufbau von Listen
 *        verwendet werden.
 *      </td>
 *    </tr>
 *  </table>
 *  <br>
 *  Generator 1.2011.0329<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2011.0329
 */
public class Generator {

    /** Segmente der Vorlage */
    private Hashtable segments;

    /** Datenbuffer der Vorlage */
    private byte[] structure;

    /**
     *  Konstruktor, richtet den Generator initial ein.
     *  @param structure Vorlage als Bytes
     */
    private Generator(byte[] structure) {

        this.segments = new Hashtable();

        this.structure = this.parse(structure, false);
    }

    /**
     *  Erstellt einen neuen Generator auf Basis der &uuml;bergebenen Vorlage.
     *  @param  structure Vorlage
     *  @return der Generator mit der als Bytes &uuml;bergebenen Vorlage
     */
    public static Generator parse(byte[] structure) {

        return new Generator(structure == null ? new byte[0] : (byte[])structure.clone());
    }

    /**
     *  R&uuml;ckgabe der aktuell bef&uuml;llten Struktur.
     *  @param  clean Option <code>true</code> bereinigt alle Marken
     *  @return die aktuell bef&uuml;llte Struktur
     */
    public byte[] extract(boolean clean) {

        return clean ? this.define(this.structure, null, null, true) : (byte[])this.structure.clone();
    }

    /**
     *  Extrahiert das angegebene Segment und setzt dort die Daten. Die
     *  Gesamtstruktur wird davon nicht ber&uuml;hrt.
     *  @param  segment Name des Segments
     *  @param  data    Liste der zu setzenden Daten
     *  @param  clean   Option <code>true</code> bereinigt alle Marken
     *  @return das gef&uuml;llte Segment, kann dieses nicht ermittelt werden,
     *          wird ein leeres Byte-Array zur&uuml;ckgegeben
     */
    public byte[] extract(String segment, Hashtable data, boolean clean) {

        byte[] bytes;

        //der Name wird zur Verarbeitung vereinfacht
        segment = (segment == null) ? "" : segment.trim().toLowerCase();

        //zur Verarbeitung wird die Teilstruktur ermittelt
        if (segment.length() == 0 || (bytes = (byte[])this.segments.get(segment)) == null) return new byte[0];

        //die Werte in der aktuellen Strutur werden gesetzt
        bytes = this.define(bytes, segment, data, false);

        //optional werden mit der Option clean von allen Marken entfernt
        return clean ? this.define(bytes, null, null, true) : bytes;
    }

    /**
     *  Setzt die Daten f&uuml;r das angegebene Segment in der Gesamtstruktur.
     *  Mit <code>null</code>, werden die Daten alle Segmente gesetzt.
     *  @param segment Name des Segments, alternative <code>null</code>
     *  @param data    Liste der zu setzenden Daten
     */
    public void define(String segment, Hashtable data) {

        this.structure = this.define(this.structure, segment, data, false);
    }

    /**
     *  L&ouml;st in der &uuml;bergeben Struktur alle Variablen zum angegebenen
     *  Name auf oder entfernt diese mit der Option <code>clear</code>.
     *  R&uuml;ckgabe der ge&auml;nderter Struktur als ByteArray
     *  @param  structure Generator Struktur
     *  @param  segment   Name des Definitionsblocks
     *  @param  data      Liste der zu setzenden Daten
     *  @param  clear     Option <code>true</code> zum entfernen der Tags
     *  @return die ge&auml;nderte Struktur als ByteArray
     */
    private byte[] define(byte[] structure, String segment, Hashtable data, boolean clear) {

        Enumeration     enumeration;
        Hashtable       cache;
        Object          object;
        String          content;
        String          stream;
        String          string;
        StringTokenizer tokenizer;

        byte[]          bytes;
        byte[]          entry;

        int             addition;
        int             correct;
        int             cursor;
        int             length;
        int             offset;
        int             pointer;

        //inhaltlose Strukturen werden ignoriert
        if (structure == null) return new byte[0];

        //der Name des Segments wird zur Verarbeitung vereinfacht
        segment = (segment == null) ? "" : segment.trim().toLowerCase();

        //Hinweis - die Verarbeitung erfolgt in zwei Schritten
        //1. die Struktur wird gegebenfalls um die Segmente erweitert
        //2. die Variablen der Struktur werden gesetzt, bzw. bereinigt

        //zur Verarbeitung wird die Struktur in in einen String gewandelt
        content = new String(structure);

        //das Segment wird ermittelt
        entry = (byte[])this.segments.get(segment);

        if (entry == null) entry = new byte[0];

        //die Cursor werden initialisiert
        offset = cursor = 0;

        //die Definitionsbloecke werden eingefuegt
        while ((offset = content.indexOf("<set:", offset)) >= 0 && entry.length > 0) {

            pointer = content.indexOf('>', offset);

            if (pointer < 0) pointer = content.length();

            tokenizer = new StringTokenizer(content.substring(offset +4, pointer), ":");

            stream = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";
            string = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";

            if (!stream.equals(segment)) {offset++; continue;}

            //die Cursor-Korrektur wird ermittelt wenn die End Position
            //nicht die Laenge des Contents ueberschreitet
            correct = (pointer < content.length()) ? 1 : 0;

            length = addition = 0;

            if (string.length() != 0) {offset++; continue;}

            stream   = ("<set:").concat(segment).concat(">");
            addition = stream.length();
            length   = addition +entry.length;

            //Hinweis - das folgende Kopieren wurde stark optimiert
            //1. die neue Gesamtgroesse wird ermittelt und eingerichtet
            //2. die Daten vor dem gefunden Tag werden eingefuegt
            //3. die Daten fuer den Tag  bei ADD werden eingefuegt
            //4. die Restdaten werden eingefuegt

            bytes = new byte[offset +length +(content.length() -(pointer +correct)) +cursor];
            System.arraycopy(structure, 0, bytes, 0, offset +cursor);
            System.arraycopy(entry, 0, bytes, offset +cursor, entry.length);
            System.arraycopy(stream.getBytes(), 0, bytes, offset +cursor +entry.length, addition);
            System.arraycopy(structure, pointer +cursor +correct, bytes, offset +cursor +length, (content.length() -(pointer +correct)));

            structure = bytes;

            cursor += entry.length -(pointer -offset +correct);

            offset = (pointer +correct);
        }

        //der Datenspeicher wird initial eingerichtet
        cache = new Hashtable();

        //die Teilstruktur wird als String verarbeitet
        content = new String(structure);

        //die Cursor werden initialisiert
        offset = cursor = 0;

        //die Elemente werden eingefuegt
        for (object = null; (offset = content.indexOf("<set:", offset)) >= 0;) {

            pointer = content.indexOf('>', offset);

            if (pointer < 0) pointer = content.length();

            tokenizer = new StringTokenizer(content.substring(offset +4, pointer), ":");

            stream = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";
            string = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";

            if ((!clear && stream.equals(segment) && string.length() > 0)
                    || (clear && (segment.length() == 0 || stream.equals(segment)))) {

                //die Cursor-Korrektur wird ermittelt wenn die Endposition
                //nicht die Laenge des Contents ueberschreitet
                correct = (pointer < content.length()) ? 1 : 0;

                if (!clear && data != null) {

                    object = cache.get(string);

                    if (object == null) {

                        object = data.get(string);

                        if (object == null) {

                            enumeration = data.keys();

                            while (enumeration.hasMoreElements()) {

                                object = enumeration.nextElement();
                                stream = String.valueOf(object).trim().toLowerCase();
                                object = stream.equals(string) || stream.replace('_', '-').equals(string) ? data.get(object) : null;

                                if (object == null) continue;

                                break;
                            }
                        }

                        if (object != null) {

                            if (object instanceof String) object = ((String)object).getBytes();
                            else if (!(object instanceof byte[])) object = String.valueOf(object).getBytes();

                            cache.put(string, object);
                        }
                    }
                }

                entry = (object == null) ? new byte[0] : (byte[])object;

                bytes = new byte[offset +entry.length +(content.length() -(pointer +correct)) +cursor];
                System.arraycopy(structure, 0, bytes, 0, offset +cursor);
                System.arraycopy(entry, 0, bytes, offset +cursor, entry.length);
                System.arraycopy(structure, pointer +cursor +correct, bytes, offset +cursor +entry.length, (content.length() -(pointer +correct)));
                structure = bytes;

                cursor += entry.length -(pointer -offset +correct);

                offset = (pointer +correct);

            } else offset++;
        }

        return structure;
    }

    /**
     *  Analysiert die &uuml;bergebene Datenstruktur, ermittelte enthaltene
     *  Bl&ouml;cke und erstellt daraus eine optimierte Datenvorlage ohne
     *  Unterstrukturen.
     *  @param  bytes   Daten der Vorlage
     *  @param  extract Option <code>true</code> zur rekursiven Verarbeitung
     *  @return die optimierte Datenvorlage ohne Unterstrukturen
     */
    private byte[] parse(byte[] bytes, boolean extract) {

        String          content;
        String          entry;
        String          stream;
        String          string;
        StringTokenizer tokenizer;

        byte[]          array;
        byte[]          result;

        int             correct;
        int             count;
        int             cursor;
        int             mode;
        int             offset;
        int             pointer;

        //zur Verarbeitung wird die Struktur in einen String gewandelt
        content = new String(bytes);

        //die Cursor werden initialisiert
        cursor = offset = 0;

        //zur Verarbeitung wird die Struktur vereinfacht
        if (!extract) {

            content = content.toLowerCase();

            //die Elemente werden allgemein geprueft und ggf. optimiert
            while ((offset = content.indexOf("<set:", offset)) >= 0) {

                pointer = content.indexOf('>', offset);

                if (pointer < 0) pointer = content.length();

                //die Markenstruktur wird ermittelt
                tokenizer = new StringTokenizer(content.substring(offset +4, pointer), ":");

                for (string = ""; tokenizer.hasMoreTokens();) {

                    stream = tokenizer.nextToken().trim();

                    if (stream.length() > 0) string = string.concat(":").concat(stream);
                }

                //nur gueltige Elemente werden verwendnet, ungueltige entfernt
                if (string.length() > 0) string = ("<set").concat(string).concat(">").toLowerCase();

                //die Cursor-Korrektur wird ermittelt wenn die Endposition
                //nicht die Laenge des Contents ueberschreitet
                correct = (pointer < content.length()) ? 1 : 0;

                array = new byte[offset +string.length() +(content.length() -(pointer +correct)) +cursor];
                System.arraycopy(bytes, 0, array, 0, offset +cursor);
                System.arraycopy(string.getBytes(), 0, array, offset +cursor, string.length());
                System.arraycopy(bytes, pointer +cursor +correct, array, offset +cursor +string.length(), (content.length() -(pointer +correct)));
                bytes = array;

                cursor += string.length() -(pointer -offset +correct);

                offset = pointer +correct;
            }
        }

        //zur Verarbeitung wird die Struktur in in einen String gewandelt
        content = new String(bytes);

        //die Cursor werden zurueckgesetzt
        count = cursor = offset = 0;

        for (entry = null; (offset = content.indexOf("<set:", offset)) >= 0;) {

            pointer = content.indexOf('>', offset);

            if (pointer < 0) pointer = content.length();

            tokenizer = new StringTokenizer(content.substring(offset +4, pointer), ":");

            stream = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";
            string = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";

            mode = string.equals("sub") ? 1 : string.equals("end") ? 2 : 0;

            if (entry == null && mode != 0) {cursor = offset; entry = stream;}

            if (stream.equals(entry) && mode == 1) count++;
            if (stream.equals(entry) && mode == 2) count--;

            if (count < 0) count = 0;

            if ((count == 0 || pointer == content.length()) && entry != null) {

                //die Cursor-Korrektur wird ermittelt wenn die Endposition
                //nicht die Laenge des Contents ueberschreitet
                correct = (pointer < content.length()) ? 1 : 0;

                //die Bytes werden ohne Definitionsblock neu zusammengesetzt
                array  = new byte[cursor +(content.length() -(pointer +correct))];
                System.arraycopy(bytes, 0, array, 0, cursor);
                System.arraycopy(bytes, pointer +correct, array, cursor, content.length() -(pointer +correct));
                result = new byte[pointer -cursor +correct];
                System.arraycopy(bytes, cursor, result, 0, result.length);
                bytes  = array;

                //der Content wird fuer den Definitionsblock eingerichtet
                string  = new String(result);
                cursor  = string.indexOf('>') +1;
                correct = string.lastIndexOf("<set:");

                if (pointer == content.length()) correct = string.length();
                if (cursor > correct) cursor = correct;

                //die Definitionstags werden aus dem Definitionsblock entfernt
                array  = new byte[correct -cursor];
                System.arraycopy(result, cursor, array, 0, array.length);
                result = array;

                //eventuell enthaltene Unterdefinitionsbloecke werden entfernt
                //und die Definitionen uebernommen
                this.segments.put(entry, this.parse(result, true));

                //der Content wird neu eingerichtet
                content = new String(bytes);

                //der Blockname wird zurueckgesetzt
                entry = null;

                //die Cursor werden zurueckgesetzt
                offset = (cursor = 0) -1;
            }

            offset++;
        }

        return bytes;
    }

    /**
     *  R&uuml;ckgabe aller Namen der Segmente als Enumeration.
     *  @return alle Namen der Segmente als Enumeration
     */
    public Enumeration segments() {

        return this.segments.keys();
    }
}