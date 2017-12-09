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
package com.seanox.module.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;

import com.seanox.common.Accession;
import com.seanox.common.Codec;
import com.seanox.module.Cookie;
import com.seanox.network.http.Fragment;

/**
 *  Request stellt Konstanten und Methoden zur Verarbeitung von HTTP-Anfragen
 *  per Servlet zur Verf&uuml;gung und gestattet dabei einen einfachen und
 *  gezielten Umgang mit dem Header, Multipart-Objekten und dem Datenstrom. Zur
 *  Information k&ouml;nnen die Daten formatiert ausgegeben werden.<br>
 *  <br>
 *  Request 1.2013.0428<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0428
 */
public class Request extends com.seanox.module.Request {

    /**
     *  Konstruktor, richtet den Request auf Basis des mit der Schnittstelle
     *  &uuml;bergebenen ServletRequests ein.
     *  @param  connector Schnittstelle
     *  @throws IllegalArgumentException bei ung&uml;tiger Schnittstelle
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Request(Connector connector) {
        
        super();

        if (connector == null) throw new IllegalArgumentException("Invalid connector [null]");

        if (connector.environment == null) throw new IllegalArgumentException("Invalid connector environment [null]");

        this.environment = connector.environment;

        try {this.input = (InputStream)Accession.invoke(Accession.get(connector, "requestbase"), "getInputStream");
        } catch (Exception exception) {

            new RuntimeException("ServletResponse access on requestbase failed", exception);
        }

        if (this.input == null) throw new RuntimeException("InputStream is not established");

        this.parseInterface();
        this.parseCookies();
    }

    /** Parst die Daten der Schnittstelle und richtet die Felder ein. */
    protected void parseInterface() {

        Enumeration enumeration;
        String      result;
        String      stream;
        String      string;

        String[]    streams;
        String[]    strings;

        //die CGI-Parameter werden ermittelt
        enumeration = this.environment.elements();

        while (enumeration.hasMoreElements()) {

            string = ((String)enumeration.nextElement()).toLowerCase();
            stream = this.environment.get(string);

            if (string.startsWith("http_")) {

                string = string.substring(5).replace('_', '-');

                if (string.length() == 0) continue;

                //die Feldliste wird ermittelt
                result = this.getInternalSegment("fields");
                result = result.concat("\r\n").concat(string).trim();

                //die Feldliste wird uebernommen, wenn das Feld nicht existiert
                if (!this.containsHeaderField(string)) this.segments.put("fields", result);

                //das Array der Parametereintraege wird aufgebaut
                streams = (String[])this.fields.get(string.toLowerCase());
                streams = (streams == null) ? new String[0] : streams;
                strings = new String[streams.length +1];

                //der Parameter wird als ByteArray uebernommen
                System.arraycopy(streams, 0, strings, 0, streams.length);
                strings[streams.length] = stream;

                //der Parametereintrag uebernommen
                this.fields.put(string.toLowerCase(), strings);
            }
        }

        //die Parameter des Query Strings werden ermittelt und uebernommen
        this.setInternalParameter(this.environment.get("query_string"));
    }

    /** Parst den Headers und richtet die Cookies als Array ein. */
    protected void parseCookies() {

        StringTokenizer tokenizer;
        String          stream;
        String          string;

        Cookie[]        cookies;

        int             cursor;

        //die Cookies werden aus dem Header-Feld ermittelt und separiert
        tokenizer = new StringTokenizer(this.getHeaderField("cookie"), ";");

        while (tokenizer.hasMoreTokens()) {

            //der Name und der Wert des Cookies werden ermittelt
            string = tokenizer.nextToken().trim();
            cursor = string.indexOf('=');
            stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
            string = (cursor < 0) ? string : string.substring(0, cursor).trim();

            if (string.length() > 0) {

                //das Array der Cookies wird aufgebaut
                cookies = new Cookie[this.cookies.length +1];
                System.arraycopy(this.cookies, 0, cookies, 0, this.cookies.length);
                cookies[this.cookies.length] = new Cookie(string, Codec.decode(stream, Codec.MIME));
                this.cookies = cookies;
            }
        }
    }

    /**
     *  Liest den kompletten Request entsprechend dem Feld Parameter
     *  <code>Content-Length</code> ein und ermittelt die enthaltenen Parameter
     *  und Dateien.<br><br>
     *  <b>Hinweis</b> - Die &Uuml;bernahme eines Parameters erfolgt als ein
     *  Array von ByteArrays. Dadurch k&ouml;nnen im Request mehrfach enthaltene
     *  Parameter als Menge &uuml;bernommen werden. Die Ablage des Wertes als
     *  ByteArray ist notwendig, da der Wert in String konvertiert, durch die
     *  Java Laufzeitumgebung bestimmte Zeichen verlieren kann. Somit kann der
     *  Wert nicht nur als String, sondern sp&auml;ter auch als reines ByteArray
     *  verarbeitet werden.<br><br>
     *  Das parsen des Request Bodys kann nur einmal erfolgen. Sollen die Daten
     *  an anderer Stelle erneut zur Verf&uuml;gung stehen, muss dies durch die
     *  Anwendung implementiert werden.
     *  @throws OutOfMemoryError bei Speichermangel beim Auslesen des Request
     *  @throws IOException bei fehlerhaftem Zugriff auf den Request oder den
     *          Datenstrom, beim Erreichen der maximalen Datenleerlaufzeit oder
     *          bei ung&uuml;ltigen Strukturen beim
     *          Multipart-Request
     */
    protected void parseBody() throws IOException {

        ByteArrayOutputStream bytestream;
        ByteArrayOutputStream datastream;
        FileOutputStream      output;
        Fragment              fragment;
        String                splice;
        String                stream;
        String                string;
        StringTokenizer       tokenizer;

        byte[]                bytes;
        byte[]                digits;

        boolean               offset;
        int                   count;
        int                   cursor;
        int                   digit;
        int                   length;
        int                   loop;
        int                   pointer;
        int                   volume;
        long                  duration;

        //die Datenpuffer werden eingerichtet
        bytestream = new ByteArrayOutputStream();
        datastream = new ByteArrayOutputStream();

        //initiale Einrichtung der Variablen
        fragment = null;
        output   = null;
        splice   = null;

        offset   = true;

        count    = 0;

        synchronized (this.getClass()) {

            //der Request Body wird nur bei Bedarf geparst
            if (this.locked || this.bounded || this.control) return;

            this.control = true;
        }

        //die CONTENT-LENGTH wird ermittelt
        try {volume = Integer.parseInt(this.getHeaderField("content-length"));
        } catch (Exception exception) {volume = 0;}

        //der BOUNDARY wird aus dem CONTENT-TYPE ermittelt
        tokenizer = new StringTokenizer(this.getHeaderField("content-type"), ";");

        while (tokenizer.hasMoreTokens() && splice == null) {

            string = tokenizer.nextToken().trim();
            cursor = string.indexOf('=');

            stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
            string = (cursor < 0) ? string : string.substring(0, cursor).trim();
            string = string.toLowerCase();

            if (string.equals("boundary")) splice = ("\r\n--").concat(stream);

            //ungueltige Boundary-Eintraege werden ignoriert
            if (string.equals("boundary") && (cursor < 0 || stream.length() == 0 )) return;
        }

        //ungueltige Boundary Eintraege werden ignoriert
        if ((splice != null && splice.length() == 0) || volume <= 0) return;

        //der Datenpuffer wird eingerichtet
        bytes = new byte[65535];

        //das aktuelle Timeout wird ermittelt
        duration = System.currentTimeMillis() +65535;

        try {

            if (splice == null) {

                while (count < volume && System.currentTimeMillis() < duration) {

                    //das aktuelle Timeout wird geprueft
                    if (System.currentTimeMillis() > duration) throw new IOException("Timeout by read");

                    //die Daten werden aus dem Datenstrom gelesen
                    if ((length = this.input.read(bytes)) < 0) break;

                    //das aktuelle Timeout wird ermittelt
                    if (length > 0) duration = System.currentTimeMillis() +65535;

                    //die uebermittelnde Datenmenge wird ermittelt
                    //CONTENT-LENGHT wird dabei nicht ueberschritten
                    if (!((count +length) <= volume)) length = volume -count;

                    //die Daten werden ohne Ueberlaenge uebergeben
                    if (length > 0) bytestream.write(bytes, 0, length);

                    //das Datenvolumen wird berechnet
                    count += length;

                    //Unterbrechung fuer andere Systemprozesse vorgenommen
                    this.sleep(25);

                    //das vorerst letzte Parametertrennzeichen & wird ermittelt
                    for (loop = 0, cursor = 0; loop < length; loop++) {

                        if ((bytes[loop] & 0xFF) == 38) cursor = loop +1;
                    }

                    //die Position wird mit erreichen der gesamten Datenmenge
                    //auf das Ende der gesetzt um alle Daten zu verarbeiten
                    if (count >= volume) cursor = length +1;

                    //wurde das Datenende oder das Trennzeichen & gefunden oder
                    //ist dieses im Datenpuffer enthalten werden die Parameter
                    //ermittelt und uebernommen
                    if (cursor > 0 || (count >= volume)) {

                        //das Parameter Segment wird ermittelt
                        string = new String(bytestream.toByteArray(), 0, bytestream.size() +((cursor == 0) ? 0 : -length +cursor -1));

                        //die Parameter werden ermittelt und gesetzt
                        this.setInternalParameter(string);

                        //das verbleibende Datenvolumen wird ermittelt
                        digits = (count < volume) ? new byte[bytestream.size() -cursor] : new byte[0];

                        //die Restdaten werden kopiert
                        if (digits.length > 0) System.arraycopy(bytestream.toByteArray(), cursor, digits, 0, digits.length);

                        //das ByteArray wird zurueckgesetzt
                        bytestream.reset();

                        //die Restdaten werden uebernommen
                        bytestream.write(digits);
                    }
                }

            } else {

                while (count < volume || bytestream.size() > 0) {

                    //das aktuelle Timeout wird geprueft
                    if (System.currentTimeMillis() > duration) throw new IOException("Timeout by read");

                    //die Groesse des Analysepuffers wird ermittelt
                    length = bytestream.size();

                    //die Daten werden aus dem Datenstrom gelesen
                    length = (length < splice.length()) ? this.input.read(bytes) : 0;

                    //das Datenende wird ueberprueft
                    if (length < 0) break;

                    //das aktuelle Timeout wird ermittelt
                    if (length > 0) duration = System.currentTimeMillis() +65535;

                    //die zu schreibende Datenmenge wird ermittelt
                    //CONTENT-LENGHT wird dabei nicht ueberschritten
                    if (!((count +length) <= volume)) length = volume -count;

                    //die Daten werden geschrieben
                    if (length > 0) bytestream.write(bytes, 0, length);

                    //das Datenvolumen wird berechnet
                    count += length;

                    //Unterbrechung fuer andere Systemprozesse
                    this.sleep(25);

                    //die Groesse des Analysepuffers wird ermittelt
                    length = bytestream.size();

                    //der Analysepuffer und die Blocktrennung wird ermittelt
                    string = bytestream.toString();
                    stream = offset ? splice.substring(2) : splice;

                    //die Position der Blocktrennung wird ermittelt
                    cursor = string.indexOf(stream);

                    //inkorrekte Multipart-Segmente werden nicht verarbeitet
                    if (offset && length >= stream.length() && cursor != 0) throw new IOException("Incorrect multipart segment");

                    if (cursor != 0 && length >= stream.length() && fragment != null) {

                        //der sichere Datenbereich wird ermittelt
                        pointer = (cursor > 0) ? cursor : length -(stream.length() -1);

                        //der Zwischenpuffer wird zwischengespeichert
                        digits = bytestream.toByteArray();

                        //der Zwischenpuffer wird in den Datenstrom bzw. Datenpuffer uebernommen
                        if (output != null) output.write(digits, 0, pointer); else datastream.write(digits, 0, pointer);

                        //der Analysepuffer wird zurueckgesetzt
                        bytestream.reset();

                        //der Analysepuffer wird erneut aufgebaut
                        bytestream.write(digits, pointer, length -pointer);
                    }

                    if (cursor >= 0) {

                        //der Datenstrom wird geschlossen
                        if (output != null) output.close();

                        //der Datenstrom wird zurueckgesetzt
                        output = null;

                        //das Fragment wird analysiert und uebernommen
                        if (fragment != null) this.setInternalFragment(fragment, datastream.toByteArray());

                        //der Datenpuffer wird zurueckgesetzt
                        datastream.reset();

                        //der Datenpuffer wird zwischengespeichert
                        digits = bytestream.toByteArray();

                        //der Analysepuffer wird zurueckgesetzt
                        bytestream.reset();

                        //der Analysepuffer wird erneut aufgebaut
                        bytestream.write(digits, stream.length(), digits.length -stream.length());

                        //der Datenpuffer wird zwischengespeichert
                        digits = bytestream.toByteArray();

                        //der Header des Fragments wird aus dem Datenpuffer ermittelt
                        for (loop = 0, pointer = 0, cursor = 0; loop < digits.length && cursor < 4; loop++) {

                            //der Request wird auf kompletten Header geprueft
                            cursor = (digits[loop] == ((cursor % 2) == 0 ? 13 : 10)) ? cursor +1 : 0;

                            //die Position wird zwischen gespeichert
                            if (cursor == 4) pointer = loop +1;
                        }

                        //der Header des Fragments wird aus dem Datenstrom ermittelt
                        for (; count < volume && cursor < 4; loop++) {

                            //das aktuelle Timeout wird geprueft
                            if (System.currentTimeMillis() > duration) throw new IOException("Timeout by read");

                            if ((digit = this.input.read()) < 0) break;

                            //das aktuelle Timeout wird ermittelt
                            duration = System.currentTimeMillis() +65535;

                            //das Datenvolumen wird erfasst
                            count++;

                            //die Daten werden gespeichert
                            bytestream.write(digit);

                            //der Request wird auf kompletten Header geprueft
                            cursor = (digit == ((cursor % 2) == 0 ? 13 : 10)) ? cursor +1 : 0;

                            //die Position wird zwischen gespeichert
                            if (cursor == 4) pointer = loop +1;

                            //Unterbrechung fuer andere Systemprozesse
                            if ((count % 65535) == 0) this.sleep(25);
                        }

                        //der Datenpuffer wird zwischengespeichert
                        digits = bytestream.toByteArray();

                        //der Pointer wird gegebenenfalls korrigiert
                        if (pointer == 0) pointer = digits.length;

                        //der Analysepuffer wird zurueckgesetzt
                        bytestream.reset();

                        //der Analysepuffer wird erneut aufgebaut
                        bytestream.write(digits, 0, pointer);

                        //das Fragment wird mit den Meta Daten eingerichtet
                        fragment = Fragment.create(bytestream.toByteArray());

                        //mit gesetztem Auslagerungsverzeichnis werden die Daten ausgelagert
                        if (this.storage != null && fragment.containsParameter("filename")) {

                            //der Auslagerungspfad wird ermittelt
                            string = Codec.decode(this.storage, Codec.DOT);
                            string = (string.endsWith("/") || string.length() == 0) ? string : string.concat("/");

                            //die Verzeichnisstruktur wird angelegt
                            new File(string).mkdirs();

                            //der Pfad der Auslagerungsdatei wird fuer das Fragment gesetzt
                            try {Accession.set(fragment, "storage", new File(string.concat(this.getNextStorageFileName())));
                            } catch (Exception exception) {

                                throw new RuntimeException(exception);
                            }

                            //der Datenstrom wird eingerichtet
                            output = new FileOutputStream(fragment.getStorage());
                        }

                        //der Analysepuffer wird zurueckgesetzt
                        bytestream.reset();

                        //der Analysepuffer wird erneut aufgebaut
                        bytestream.write(digits, pointer, digits.length -pointer);

                        //der Status der Blocktrennung wird gesetzt
                        offset = false;
                    }
                }
            }

        } finally {

            try {output.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }
    }
}