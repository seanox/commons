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
package com.seanox.common;

/**
 *  Components stellt mehre allgemeine statistische Funktionen als Erweiterung
 *  der Java Laufzeitumgebung zur Verf&uuml;gung.<br><br>
 *  <b>Hinweis</b> - Diese Klasse wird auch in der Zukunft um weitere allgemeine
 *  Funktionen erweitert, wobei diese auch zu neuen Paketen zusammengef&uuml;hrt
 *  werden und damit wegfallen k&ouml;nnen.<br>
 *  <br>
 *  Components 1.2010.1201<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2010.1201
 */
public class Components {

    /** Konstante f&uuml;r die Sprache Deutsch */
    public static final int LANGUAGE_GERMAN = 1;

    /** Konstruktor, richtet das Components-Objekt ein. */
    private Components() {

        return;
    }

    /**
     *  Ermittelt den phonetischen Wert zum String in der angegebenen Sprache.
     *  @param  string   zu berechender String
     *  @param  language Bezugsprache
     *  @return der ermittelte phonetische Wert
     *  @throws IllegalArgumentException bei nicht unterst&uuml;zter Sprache
     *  @see    #LANGUAGE_GERMAN
     */
    public static String strsnd(String string, int language) {

        StringBuffer result;
        String       buffer;
        String       digits;
        String       stream;

        byte[]       bytes;
        int[]        codes;

        int          count;
        int          loop ;

        if (string == null) return "";

        //nicht unterstuetzte Sprachen fuehren zur IllegalArgumentException
        if (language != Components.LANGUAGE_GERMAN) throw new IllegalArgumentException("Language not supported");

        //der Datenpuffer wird eingerichtet
        result = new StringBuffer();

        //die Bytes des String werden ermitttelt
        bytes = string.toLowerCase().trim().getBytes();

        //das phonetische Muster wird eingerichtet (a-z)
        digits = "1343254 244677134893155929";

        for (loop = 0, buffer = "", stream = null; language == LANGUAGE_GERMAN && loop < bytes.length; loop++, stream = null) {

            //zur Analyse werden die aktuellen 3 Zeichen ermittelt
            for (count = 0, codes = new int[3]; count < codes.length && (bytes.length -loop) > count; count++) {

                codes[count] = bytes[loop +count] & 0xFF;
            }

            //zur Berechnung des phonetischen Werts werden nur a-z(h),ß,ae,oe,ue beruecksichtigt
            if ((codes[0] < 0x61 || codes[0] > 0x7A) && codes[0] != 0x68 && codes[0] != 0xDF && codes[0] != 0xE4 && codes[0] != 0xF6 && codes[0] != 0xFC) continue;

            //phonetische Regel - folgt auf aou eiy wird daraus Gruppe 2
            if ((codes[0] == 0x61 || codes[0] == 0x6F || codes[0] == 0x75) && (codes[1] == 0x65 || codes[1] == 0x69 || codes[1] == 0x79)) {stream = "2"; loop++;}

            //phonetische Regel - folgt auf s c h wird daraus Gruppe 0
            //phonetische Regel - aeoeue entsprechen der Gruppe 2
            //phonetische Regel - folgt auf s tp wird daraus Gruppe 0
            //phonetische Regel - folgt auf c h wird daraus Gruppe 0
            //phonetische Regel - folgt auf p fh wird daraus Gruppe 5
            //phonetische Regel - ß entspricht der Gruppe 9
            if (stream == null && codes[0] == 0x73 &&  codes[1] == 0x63 && codes[2] == 0x68)  stream = "0";
            if (stream == null && codes[0] == 0xE4 ||  codes[0] == 0xF6 || codes[0] == 0xFC)  stream = "2";
            if (stream == null && codes[0] == 0x73 && (codes[1] == 0x70 || codes[1] == 0x74)) stream = "0";
            if (stream == null && codes[0] == 0x63 &&  codes[1] == 0x68) stream = "0";
            if (stream == null && codes[0] == 0x70 && (codes[1] == 0x66 || codes[1] == 0x68)) stream = "5";
            if (stream == null && codes[0] == 0xDF) stream = "9";

            //der phonetische Wert wird ohne Folgewerte erweitert (12221 -> 121)
            if (stream != null) {if (!stream.equals(buffer)) result.append(buffer = stream); continue;}

            //der phonetische Wert wird ermittelt
            if ((codes[0] -= 0x60) > 0 && codes[0] <= digits.length()) stream = String.valueOf(digits.charAt(codes[0] -1)).trim();

            //der phonetische Wert wird ohne Folgewerte erweitert (12221 -> 121)
            if (stream != null && !stream.equals(buffer)) result.append(buffer = stream);
        }

        return result.toString();
    }

    /**
     *  Ersetzt regellos in <code>search</code> in <code>string</code> durch
     *  <code>replace</code>, die Gross- und Kleinschreibung wird dabei nicht
     *  ber&uuml;cksichtigt. R&uuml;ckgabe der ersetzte String.
     *  @param  string  zu durchsuchender String
     *  @param  search  gesuchter String
     *  @param  replace zu ersetzender String
     *  @return der ersetzte String
     */
    public static String strcrs(String string, String search, String replace) {

        return Components.strcrs(string, search, replace, false);
    }

    /**
     *  Ersetzt regellos in <code>search</code> in <code>string</code> durch
     *  <code>replace</code>, die Gross- und Kleinschreibung wird bei
     *  <code>casing</code> ber&uuml;cksichtigt.
     *  @param  string  zu durchsuchender String
     *  @param  search  gesuchter String
     *  @param  replace zu ersetzender String
     *  @param  casing  Option <code>true</code> Schreibweise wird beachtet
     *  @return der ersetzte String
     *  @throws IllegalArgumentException bei Zirkel Ausdr&uuml;cken, wenn
     *          <code>replace</code> in <code>search</code> enthalten ist
     */
    public static String strcrs(String string, String search, String replace, boolean casing) {

        String stream;

        int    cursor;

        if (replace == null) replace = "";
        if (search  == null) search  = "";
        if (string  == null) string  = "";

        stream = string;

        if (casing) {

            stream = string.toLowerCase();
            search = search.toLowerCase();
        }

        if (search.length() == 0) return string;

        if ((casing ? replace : replace.toLowerCase()).indexOf(search) >= 0) throw new IllegalArgumentException("Circular expression in seach and replace");

        while ((cursor = stream.indexOf(search)) >= 0 && search.length() > 0) {

            string = string.substring(0, cursor).concat(replace).concat(string.substring(cursor +search.length()));
            stream = !casing ? string.toLowerCase() : string;
        }

        return string;
    }

    /**
     *  Ersetzt in <code>search</code> in <code>string</code> durch
     *  <code>replace</code>, die Gross- und Kleinschreibung wird dabei nicht
     *  ber&uuml;cksichtigt. R&uuml;ckgabe der ersetzte String.
     *  @param  string  zu durchsuchender String
     *  @param  search  gesuchter String
     *  @param  replace zu ersetzender String
     *  @return der ersetzte String
     */
    public static String strset(String string, String search, String replace) {

        return Components.strset(string, search, replace, false);
    }

    /**
     *  Ersetzt in <code>search</code> in <code>string</code> durch
     *  <code>replace</code>, die Gross- und Kleinschreibung wird bei
     *  <code>casing</code> ber&uuml;cksichtigt.
     *  @param  string  zu durchsuchender String
     *  @param  search  gesuchter String
     *  @param  replace zu ersetzender String
     *  @param  casing  Option <code>true</code> Schreibweise wird beachtet
     *  @return der ersetzte String
     */
    public static String strset(String string, String search, String replace, boolean casing) {

        String result;
        String stream;

        int    cursor;

        if (replace == null) replace = "";
        if (search  == null) search  = "";
        if (string  == null) string  = "";

        stream = string;

        if (casing) {

            stream = string.toLowerCase();
            search = search.toLowerCase();
        }

        for (result = ""; (cursor = stream.indexOf(search)) >= 0 && search.length() > 0;) {

            result = result.concat(string.substring(0, cursor)).concat(replace);
            string = string.substring(cursor +search.length());
            stream = stream.substring(cursor +search.length());
        }

        if (string.length() > 0) result = result.concat(string);

        return result;
    }

    /**
     *  Maskiert alle Steuerzeichen unterhalb des ASCII Code 32 symbolisch.
     *  Die Maskierung erfolgt in Kleinbuchstaben.
     *  @param  string zu maskierender String
     *  @return der symbolisch maksierte Text
     */
    public static String strmsc(String string) {

        return Components.strmsc(string, false);
    }

    /**
     *  Maskiert alle Steuerzeichen unterhalb des ASCII Code 32 symbolisch.
     *  @param  string zu maskierender String
     *  @param  uppercase  Option zur Verwendung der Grosschreibung
     *  @return der symbolisch maksierte Text
     */
    public static String strmsc(String string, boolean uppercase) {

        String   stream;

        String[] array;

        int      loop;

        if (string == null) string = (uppercase) ? "[NUL]" : "[nul]";

        array = new String[] {"nul", "soh", "stx", "etx", "eot", "enq", "ack", "bel", "bs", "ht", "lf", "vt", "ff", "cr", "so", "si", "dle", "dc1", "dc2", "dc3", "dc4", "nak", "syn", "etb", "can", "em", "sub", "esc", "fs", "gs", "rs", "us"};

        for (loop = 0; loop < array.length; loop++) {

            stream = (uppercase) ? array[loop].toUpperCase() : array[loop];
            stream = ("[").concat(stream).concat("]");
            string = Components.strset(string, String.valueOf((char)loop), stream);
        }

        return string;
    }

    /**
     *  Entfernt alle Steuerzeichen unterhalb des ASCII Code 32.
     *  @param  string zu maskierender String
     *  @return der bereinigte Text
     */
    public static String strcln(String string) {

        return Components.strcln(string, null);
    }

    /**
     *  Entfernt alle Steuerzeichen unterhalb des ASCII Code 32.
     *  Mit Ausnahme der optional angegebenen.
     *  @param  string  zu maskierender String
     *  @param  excepts vom Ersetzen ausgenommene Zeichen
     *  @return der bereinigte Text
     */
    public static String strcln(String string, String excepts) {

        String result;

        int    loop;

        if (excepts == null) excepts = "";
        if (string  == null) string  = "";

        for (loop = 0, result = string; loop < 32; loop++) {

            if (excepts.indexOf((char)loop) >= 0) continue;

            result = result.replace((char)loop, ' ');
        }

        return result;
    }

    /**
     *  Konvertiert alle f&uuml;r die Ausgabe typischen Steuerzeichen in die
     *  markierte Schreibweise, so wird z.B. aus [CRLF] \r\n.
     *  @param  string zu konvertierender String
     *  @return der String mit den konvertierten Steuerzeichen
     */
    public static String strprn(String string) {

        StringBuffer result;
        String       stream;

        int          loop;

        if (string == null) return "";

        result = new StringBuffer();

        for (loop = 0; loop < string.length(); loop++) {

            stream = string.substring(loop, loop +1);

            if (stream.equals("\\")) stream = "\\\\";
            else if (stream.equals("\b")) stream = "\\b";
            else if (stream.equals("\f")) stream = "\\f";
            else if (stream.equals("\n")) stream = "\\n";
            else if (stream.equals("\r")) stream = "\\r";
            else if (stream.equals("\n")) stream = "\\n";

            result.append(stream);
        }

        return result.toString();
    }

    /**
     *  Konvertiert alle typischen maskierten Steuerzeichen in reale Strings, so
     *  so wird z.B. aus \r\n [CRLF].
     *  @param  string zu konvertierender String
     *  @return der String mit den konvertierten Steuerzeichen
     */
    public static String strprs(String string) {

        StringBuffer result;
        String       stream;

        boolean      control;
        int          loop;
        int          code;

        if (string == null) return "";

        result = new StringBuffer();

        for (loop = 0, control = true; loop < string.length(); loop++) {

            stream = string.substring(loop, loop +1);
            code   = stream.charAt(0);

            if (code == 92) {control = false; continue;}

            if (!control) {

                switch (code) {

                    //Maskierungen \r \n \b \f \t \\ \' \"
                    //ASCII Code   13 10  8 12  9 92 34 39

                    case  34: break;
                    case  39: break;
                    case  92: break;

                    case  98: stream = "\b"; break;
                    case 102: stream = "\f"; break;
                    case 110: stream = "\n"; break;
                    case 114: stream = "\r"; break;
                    case 116: stream = "\t"; break;

                    default:  continue;
                }
            }

            result.append(stream);

            control = true;
        }

        return result.toString();
    }

    /**
     *  Sortiert mehre StringArrays. Vorraussetzung sind gleichlange Arrays
     *  f&uuml;r Strings, Sortierungsrichtung und Sortierungsoption. Es muss je
     *  ein Satz von mehren Strings, einer Sortierrichtung und einer
     *  Sortieroption vorliegen.
     *  <pre>
     *  Beispiel:      String names[]  = new String[2]
     *                 String dates[]  = ...
     *
     *                 Object array[]  = new Object[2]
     *                        array[0] = names
     *                        array[1] = dates
     *
     *                  int sorting[]  = new int[2];
     *                      sorting[0] = 1;
     *                      sorting[1] = -1;
     *
     *               boolean casing[]  = new boolean[2];
     *                       casing[0] = 1;
     *                       casing[1] = 1;
     *
     *               sort(arrays, directions, options);
     *
     *                           names = arrays[0];
     *                           dates = arrays[1];
     *  </pre>
     *
     *  @param  strings  StringArray welches sortiert werden soll
     *  @param  sortings Sortierung (1 Aufsteigend, 0 ignoriert, -1 Absteigend)
     *  @param  casings  Option <code>true</code> Gross- und Kleinschreibung
     *                   wird beachtet
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public static void sort(Object[] strings, int[] sortings, boolean[] casings) {

        String   buffer;
        String   string;

        String[] cache;

        boolean  casing;
        boolean  control;
        int      beginn;
        int      compare;
        int      count;
        int      cursor;
        int      end;
        int      index;
        int      length;
        int      loop;
        int      pointer;
        int      range;
        int      sorting;
        int      source;

        if (strings  == null) throw new IllegalArgumentException("Invalid strings [null]");
        if (sortings == null) throw new IllegalArgumentException("Invalid sortings [null]");
        if (casings  == null) throw new IllegalArgumentException("Invalid casings [null]");

        //die gesamte Anzahl der Array wird ermittelt
        if ((count = strings.length) == 0) return;

        //die temporaeren Datenfelder werden eingerichtet
        cache = new String[count];

        //die Groesse der Arrays wird ermittelt
        length = ((String[])strings[0]).length -1;

        //Voraussetzung fuer das Sortieren sind gleichgrosse Arrays
        for (loop = 0; loop < count && length >= 0; loop++) {

            if (loop > 0 && (length != ((String[])strings[loop]).length -1)) return;
        }

        for (index = 0; index < sortings.length && index < strings.length && index < casings.length; index++) {

            //das Sorting und das Casing des Arrays wird ermittelt
            sorting = sortings[index];
            casing  = casings[index];

            beginn  = 0;
            end     = 0;

            while (beginn < (length +1)) {

                //das erste Array wird komplett sortiert dazu werden
                //bei INDEX == 0 die Start und Endposition fest vorgegeben
                if (index == 0) {beginn = 0; end = length;} else end = beginn;

                //im Durchlauf wird nach weiteren gleichen Eintraegen gesucht
                //um das Ausmass der des zu sortierenden Array zu ermitteln
                if (index > 0) {

                    control = true;

                    //in der Schliefe werden weitere gleiche
                    //Eintraege in der Folge gesucht
                    while (end < length && control)  {

                        for (range = 0; range < index; range++) {

                            if (!casing && !((String[])strings[range])[beginn].equalsIgnoreCase(((String[])strings[range])[end +1])) control = false;
                            if (casing && !((String[])strings[range])[beginn].equals(((String[])strings[range])[end +1])) control =  false;
                        }

                        if (control) end++;
                    }
                }

                //wurde ein zusortierendes Array ermittelt wird es sortiert
                if (beginn < end) {

                    //die Arrays werden mit dem ersten Array sortiert
                    for (loop = beginn; loop <= end; loop++) {

                        //das Datenfeld zum vergleichen wird ermittelt
                        string = ((String[])strings[index])[loop];

                        pointer = loop;
                        source  = loop;

                        for (cursor = end; cursor > loop; cursor--) {

                            buffer = ((String[])strings[index])[cursor];

                            compare = (casing) ? buffer.compareTo(string) : buffer.compareToIgnoreCase(string);

                            if ((compare < 0 && sorting > 0) || (compare > 0 && sorting < 0)) {

                                string = ((String[])strings[index])[cursor];

                                pointer = cursor;
                            }
                        }

                        //alle Datenfelder werden zugeordnet
                        for (range = 0; range < count;  range++) {

                            cache[range] = ((String[])strings[range])[pointer];

                            ((String[])strings[range])[pointer] = ((String[])strings[range])[source];
                            ((String[])strings[range])[source]  = cache[range];
                        }
                    }
                }

                beginn = end +1;
            }
        }
    }
}