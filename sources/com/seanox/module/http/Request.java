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
package com.seanox.module.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.seanox.common.Accession;
import com.seanox.common.Codec;
import com.seanox.common.Components;
import com.seanox.module.Cookie;
import com.seanox.network.http.Fragment;

/**
 *  Request stellt Konstanten und Methoden zur Verarbeitung von HTTP-Anfragen
 *  zur Verf&uuml;gung und gestattet dabei einen einfachen und gezielten Umgang
 *  mit dem Header, Multipart-Objekten und dem Datenstrom. Zur Information
 *  k&ouml;nnen die Daten formatiert ausgegeben werden.<br>
 *  <br>
 *  <b>Hinweis</b> - Das Bezugsobjekt wurde speziell auf Seanox Devwex
 *  abgestimmt. Bei der Verwendung anderer Umgebungen m&uuml;ssen die Zugriffe
 *  auf die entsprechenden Ressourcen speziell implementiert werden.<br>
 *  <br>
 *  Request 1.2013.0428<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0428
 */
public class Request extends com.seanox.module.Request {

    /** Bezugsobjekt */
    private Object accession;

    /** Server Socket */
    private Socket socket;

    /**
     *  Konstruktor, richtet den Request auf Basis des Accession Objekts ein.
     *  @param  accession Bezugsobjekt
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Request(Object accession) {
        
        super();

        this.accession  = accession;

        if (accession == null) throw new IllegalArgumentException("Invalid accession [null]");

        try {

            Accession.storeField(this.accession, this, "socket", Accession.EXPORT);
            Accession.storeField(this.accession, this, "input", Accession.EXPORT);
            Accession.storeField(this.accession, this, "header", Accession.EXPORT);
            Accession.storeField(this.accession, this, "secure", Accession.EXPORT);

        } catch (Exception exception) {

            throw new RuntimeException("Synchronization to server failed", exception);
        }

        if (this.socket == null) throw new RuntimeException("Socket is not established");
        if (this.input  == null) throw new RuntimeException("InputStrean is not established");
        if (this.header == null) throw new RuntimeException("Request is not established");

        this.environment = new Environment(accession);
        this.signature   = this.environment.get("unique_id");

        this.parseFields();
        this.parseCookies();
    }

    /**
     *  Parst den Header und richtet die Felder als Hashtable ein.
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    protected void parseFields() {

        Hashtable       hashtable;
        StringTokenizer tokenizer;
        String          content;
        String          result;
        String          stream;
        String          string;

        String[]        streams;
        String[]        strings;

        int             cursor;

        //das StringArray wird eingerichtet
        strings = new String[0];

        //der Inhalt der Umgebungsvariablen wird ermittelt
        try {hashtable = (Hashtable)Context.mountField(this.accession, "environment.entries");
        } catch (Exception exception) {

            throw new RuntimeException("Access on request environment failed", exception);
        }

        //die Zeilen des Headers werden ermittelt, die erste wird ignoriert
        tokenizer = new StringTokenizer(this.header, "\r\n");
        content   = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";

        while (tokenizer.hasMoreTokens()) {

            string = tokenizer.nextToken();
            cursor = string.indexOf(':');
            stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
            string = (cursor < 0) ? string : string.substring(0, cursor).trim();

            if (string.length() == 0) continue;

            //die Feldliste wird ermittelt
            result = this.getInternalSegment("fields");
            result = result.concat("\r\n").concat(string).trim();

            //die Feldliste wird uebernommen, wenn das Feld noch nicht existiert
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

        //die Elemente der ersten Headerzeile werden ermittelt
        tokenizer = new StringTokenizer(content, " ");
        strings   = new String[0];

        while (tokenizer.hasMoreTokens()) {

            streams = new String[strings.length +1];
            System.arraycopy(strings, 0, streams, 0, strings.length);
            streams[strings.length] = tokenizer.nextToken().trim();
            strings = streams;
        }

        if (strings.length > 2) this.segments.put("protocol", strings[2]);
        if (strings.length > 0) this.segments.put("method", strings[0]);

        //der Path wird ermittelt
        string = (strings.length > 1) ? strings[1] : "";
        cursor = string.indexOf('?');
        cursor = (cursor < 0) ? string.length() : cursor;
        string = string.substring(0, cursor);

        //Zeichen zur Pfadorientierung werden decodiert
        string = Components.strset(string, "%2E", ".");
        string = Components.strset(string, "%2F", "/");
        string = Components.strset(string, "%5C", "\\");

        //der Path wird aufgeloest /abc/./def/../ghi -> /abc/ghi
        stream = Codec.decode(string, Codec.DOT);

        //die URL wird ermittelt
        string = (String)hashtable.get(("script_url").toUpperCase());
        string = (string == null) ? stream : string;

        //die URL wird gesetzt
        this.segments.put("url", string);

        //der Query wird ermittelt
        string = (strings.length > 1) ? strings[1] : "";
        cursor = string.indexOf('?');
        cursor = (cursor < 0) ? string.length() : cursor +1;
        string = string.substring(cursor);

        //der Query wird gesetzt
        this.segments.put("query", string);

        //der Host wird ohne Port ermittelt und verwendet
        stream = this.getHeaderField("host");
        cursor = stream.indexOf(":");
        stream = stream.substring(0, cursor < 0 ? stream.length() : cursor);

        while (stream.endsWith(".")) stream = stream.substring(0, stream.length() -1);

        //wurde im Request kein Host angegeben, wird die aktuelle Adresse verwendet
        if (stream.length() <= 0) stream = this.socket.getLocalAddress().getHostAddress();

        //die Location wird zusammengestellt
        string = String.valueOf(this.socket.getLocalPort());
        string = ((!string.equals("80") && !this.secure || !string.equals("443") && this.secure) && string.length() > 0) ? (":").concat(string) : "";
        string = ((this.secure ? "https" : "http")).concat("://").concat(stream).concat(string);
        stream = this.getInternalSegment("url");
        string = string.concat(stream);

        //der URI wird gesetzt
        this.segments.put("uri", string);

        //die Parameter des Query Strings werden ermittelt und uebernommen
        this.setInternalParameter(this.getQueryString());
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
        int                   blocksize;
        int                   count;
        int                   cursor;
        int                   digit;
        int                   length;
        int                   loop;
        int                   pointer;
        int                   volume;
        long                  interrupt;

        //die Datenpuffer werden eingerichtet
        bytestream = new ByteArrayOutputStream();
        datastream = new ByteArrayOutputStream();

        //initiale Einrichtung der Variablen
        fragment = null;
        output   = null;
        splice   = null;

        offset   = true;

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

            //ungueltige Boundary Eintraege werden wie nicht angegeben interpretiert
            if (string.equals("boundary") && (cursor < 0 || stream.length() == 0 )) return;
        }

        //ungueltige Boundary Eintraege werden wie nicht angegeben interpretiert
        if ((splice != null && splice.length() == 0) || volume <= 0) return;

        //der Interrupt fuer Systemunterbrechungen wird ermittelt
        try {interrupt = ((Long)Accession.get(this.accession, "interrupt")).longValue();
        } catch (Exception exception) {interrupt = 0;}

        //der Interrupt wird gegebenenfalls korrigiert
        if (interrupt < 0) interrupt = 0;

        //die Groesse des Datenpuffers wird ermittelt
        try {blocksize = ((Integer)Accession.get(this.accession, "blocksize")).intValue();
        } catch (Exception exception) {blocksize = 65535;}

        //die Groesse wird gegebenenfalls korrigiert
        if (blocksize <= 0) blocksize = 65535;

        //die Datenpuffer werden eingerichtet
        bytes = new byte[blocksize];

        try {

            //der Datenzaehler wird abgeglichen
            count = bytestream.size();

            if (splice == null) {

                while (count < volume) {

                    //die Daten werden aus dem Datenstrom gelesen
                    if ((length = this.input.read(bytes)) < 0) break;

                    //die uebermittelnde Datenmenge wird ermittelt
                    //CONTENT-LENGHT wird dabei nicht ueberschritten
                    if (!((count +length) <= volume)) length = volume -count;

                    //die Daten werden ohne Ueberlaenge uebergeben
                    if (length > 0) bytestream.write(bytes, 0, length);

                    //das Datenvolumen wird berechnet
                    count += length;

                    //Unterbrechung fuer andere Systemprozesse vorgenommen
                    this.sleep(interrupt);

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

                    //die Groesse des Analysepuffers wird ermittelt
                    length = bytestream.size();

                    //die Daten werden aus dem Datenstrom gelesen
                    length = (length < splice.length()) ? this.input.read(bytes) : 0;

                    //das Datenende wird ueberprueft
                    if (length < 0) break;

                    //die zu schreibende Datenmenge wird ermittelt
                    //CONTENT-LENGHT wird dabei nicht ueberschritten
                    if (!((count +length) <= volume)) length = volume -count;

                    //die Daten werden geschrieben
                    if (length > 0) bytestream.write(bytes, 0, length);

                    //das Datenvolumen wird berechnet
                    count += length;

                    //Unterbrechung fuer andere Systemprozesse
                    this.sleep(interrupt);

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

                        //das Fragment wird analysiert und entsprechen uebernommen
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

                            if ((digit = this.input.read()) < 0) break;

                            //das Datenvolumen wird erfasst
                            count++;

                            //die Daten werden gespeichert
                            bytestream.write(digit);

                            //der Request wird auf kompletten Header geprueft
                            cursor = (digit == ((cursor % 2) == 0 ? 13 : 10)) ? cursor +1 : 0;

                            //die Position wird zwischen gespeichert
                            if (cursor == 4) pointer = loop +1;

                            //Unterbrechung fuer andere Systemprozesse
                            if ((count % blocksize) == 0) this.sleep(interrupt);
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

    /**
     *  R&uuml;ckgabe der Methode vom Request.
     *  @return die Methode vom Request
     */
    public String getMethod() {

        return this.getInternalSegment("method");
    }

    /**
     *  R&uuml;ckgabe vom Protokolls vom Request.
     *  @return das Protokoll vom Request
     */
    public String getProtocol() {

        return this.getInternalSegment("protocol");
    }

    /**
     *  R&uuml;ckgabe vom Query String vom Request.
     *  @return der Query String vom Request
     */
    public String getQueryString() {

        return this.getInternalSegment("query");
    }

    /**
     *  R&uuml;ckgabe des Schemas der Serverbindung.
     *  @return das Schema der Serverbindung
     */
    public String getScheme() {

        return this.secure ? "https" : "http";
    }

    /**
     *  R&uuml;ckgabe der URL (Path) als String (Bsp. /path).
     *  @return die URL (Path) vom Request als String
     */
    public String getURL() {

        return this.getInternalSegment("url");
    }

    /**
     *  R&uuml;ckgabe der URI als String (Bsp. http://server:123/path).
     *  @return die URI vom Request als String
     */
    public String getURI() {

        return this.getInternalSegment("uri");
    }
}