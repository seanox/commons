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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.seanox.common.Codec;

/**
 *  Address, parst Strings zu URL und erm&ouml;glicht den Zugriff auf die
 *  einzelnen Fragmente. Sonderzeichen werden mit der Verarbeitung UTF8/MIME
 *  kodiert.<br>
 *  <br>
 *  Address 1.2013.0314<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0314
 */
public class Address implements Serializable {

    /** Protokoll aus der URL */
    private String protocol;

    /** Name oder IP des Servers */
    private String server;

    /** Datei in der URL */
    private int port;

    /** komplettes Directory in der URL */
    private String directory;

    /** Datei in der URL */
    private String file;

    /** Seitenposition aus der URL "#" */
    private String position;

    /** dynamische Werte&uuml;bergabe aus der URL "?" */
    private String query;

    /** Versionskennung f&uuml;r die Serialisierung */
    private static final long serialVersionUID = 8184234295976511625L;

    /** Konstruktoren, richtet das Address-Objekt ein. */
    private Address() {

        return;
    }

    /**
     *  Erstellt aus dem &uuml;bergeben String ein Address-Objekt.
     *  @param  string der zum Address-Objekt zu parsende String
     *  @return das erstellte Address-Objekt
     *  @throws IllegalArgumentException bei ung&uuml;ltigen Angaben in der URL
     */
    public static Address parse(String string) {

        Address address;
        String  stream;

        char[]  chars;

        boolean control;
        char    digit;
        int     cursor;
        int     loop;

        address = new Address();

        chars = string.toCharArray();

        //es wird nach zu maskierenden Zeichen gesucht (7Bit ohne Steuerzeichen)
        for (loop = 0, control = true; control && loop < chars.length; loop++) control = (chars[loop] <= 0x7F && chars[loop] >= 0x20);

        //ggf. wird die Adresse UTF-8 und anschliessend MIME kodiert
        if (!control) string = Codec.encode(Codec.encode(string, Codec.UTF8), Codec.MIME);

        //der Querystring wird ermittelt
        cursor = string.indexOf('?');
        stream = (cursor >= 0) ? string.substring(cursor +1) : "";
        string = (cursor >= 0) ? string.substring(0, cursor) : string;

        //der Querystring wird gesetzt
        address.setQuery(stream);

        //die Seitenposition wird ermittelt
        cursor = string.indexOf('#');
        stream = (cursor >= 0) ? string.substring(cursor +1) : "";
        string = (cursor >= 0) ? string.substring(0, cursor) : string;

        //der Seitenposition wird gesetzt
        address.setPosition(stream);

        //das Protokoll wird ermittelt
        string = string.replace('\\', '/');
        cursor = string.indexOf("://");
        stream = (cursor >= 0) ? string.substring(0, cursor) : "";
        stream = stream.toLowerCase();

        for (loop = 0, control = true; control && loop < stream.length(); loop++) {

            control = ((digit = stream.charAt(0)) >= 'a' && digit <= 'z') || (digit >= '0' && digit <= '9');
        }

        stream = (cursor >= 0 && control) ? string.substring(0, cursor) : "";
        string = (cursor >= 0 && control) ? string.substring(cursor +3) : string;

        //das Protokoll wird gesetzt
        address.setProtocol(stream);

        //die Datei wird ermittelt
        cursor = string.lastIndexOf('/');
        stream = (cursor >= 0) ? string.substring(cursor +1) : "";
        string = (cursor >= 0) ? string.substring(0, cursor +1) : string;

        //der Datei wird gesetzt
        address.setFile(stream);

        //der Pfad wird ermittelt
        cursor = string.indexOf('/');
        stream = (cursor >= 0) ? string.substring(cursor) : "";
        string = (cursor >= 0) ? string.substring(0, cursor) : string;

        //der Pfad wird gesetzt
        address.setDirectory(stream);

        //der Port wird ermittelt
        cursor = string.indexOf(':');
        stream = (cursor >= 0) ? string.substring(cursor +1).trim() : "";
        string = (cursor >= 0) ? string.substring(0, cursor) : string;

        //der Port wird gesetzt
        try {if (stream.length() > 0) address.setPort(Integer.parseInt(stream.trim()));
        } catch (Exception exception) {

            throw new IllegalArgumentException(("Invalid port ").concat(stream));
        }

        //der Server wird gesetzt
        address.setServer(string);

        return address;
    }

    /**
     *  R&uuml;ckgabe vom Protokoll bzw. ein leerer String wenn keines vorliegt.
     *  @return das Protokoll bzw. ein leerer String wenn keines vorliegt
     */
    public String getProtocol() {

        return this.protocol;
    }

    /**
     *  Setzt das Protokoll. Die Gross- und Kleinschreibung wird ignoriert.
     *  @param  protocol Protokoll der Adresse
     *  @throws IllegalArgumentException bei ung&uuml;ltigem Protokoll, f&uuml;r
     *          die Angabe sind nur die Zeichen a-z zul&auml;ssig
     */
    private void setProtocol(String protocol) {

        boolean control;
        char    digit;
        int     cursor;

        //das Protokoll wird optimiert und vereinfacht
        protocol = Codec.decode(Codec.decode(protocol, Codec.MIME), Codec.UTF8).toLowerCase().trim();

        //es werden nur die Zeichen a-z unterstuetzt
        for (cursor = 0, control = true; control && cursor < protocol.length(); cursor++) {

            control = ((digit = protocol.charAt(cursor)) >= 'a' && digit <= 'z');
        }

        //fehlerhafte Zeichen fuehren zur IllegalArgumentException
        if (!control) throw new IllegalArgumentException(("Invalid character in ").concat(protocol));

        this.protocol = protocol;
    }

    /**
     *  R&uuml;ckgabe vom Server bzw. ein leerer String wenn keiner vorliegt.
     *  @return der Server bzw. ein leerer String wenn keiner vorliegt
     */
    public String getServer() {

        return this.server;
    }

    /**
     *  Setzt den Server. Die Gross- und Kleinschreibung wird dabei ignoriert.
     *  @param  server Server der Adresse
     *  @throws IllegalArgumentException bei ung&uuml;ltigem Server, f&uuml;r
     *          die Angabe sind nur die Zeichen a-z, 0-9 sowie -_ und .
     *          zul&auml;ssig
     */
    private void setServer(String server) {

        boolean control;
        char    digit;
        int     cursor;

        //der Server wird optimiert und vereinfacht
        server = Codec.decode(Codec.decode(server, Codec.MIME), Codec.UTF8).toLowerCase().trim();

        //es werden nur die Zeichen a-z, 0-9, sowie -_ und . unterstuetzt
        for (cursor = 0, control = true; control && cursor < server.length(); cursor++) {

            control = ((digit = server.charAt(cursor)) >= 'a' && digit <= 'z') || digit == '.' || digit == '-' || digit == '_' || (digit >= '0' && digit <= '9');
        }

        //fehlerhafte Zeichen fuehren zur IllegalArgumentException
        if (!control) throw new IllegalArgumentException(("Invalid character in ").concat(server));

        this.server = server;
    }

    /**
     *  R&uuml;ckgabe vom Port bzw. 0 wenn kein Port vorliegt.
     *  @return der Server bzw. ein leerer String wenn keiner vorliegt
     */
    public int getPort() {

        if (this.port == 0 && this.protocol.equals("http")) return 80;
        if (this.port == 0 && this.protocol.equals("https")) return 443;

        return this.port;
    }

    /**
     *  Setzt den Port der Adresse. Mit dem Wert 0 wird der Port f&uuml;r HTTP
     *  und HTTPS automatisch ermittelt.
     *  @param  port Port der Adresse
     *  @throws IllegalArgumentException bei ung&uuml;ltigem Port, f&uuml;r die
     *          Angabe ist nur der Bereich 0-65535 zul&auml;ssig
     */
    private void setPort(int port) {

        if (port < 0 || port > 65535) throw new IllegalArgumentException(("Invalid port ").concat(String.valueOf(port)));

        this.port = port;
    }

    /**
     *  R&uuml;ckgabe der Position bzw. ein leerer String wenn keine vorliegt.
     *  @return die Position bzw. ein leerer String wenn keine vorliegt
     */
    public String getPosition() {

        return this.position;
    }

    /**
     *  Setzt die Position. Die Gross- und Kleinschreibung bleibt erhalten.
     *  @param  position Position der Adresse
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Position, f&uuml;r
     *          die Angabe sind die Zeichen ?# sowie Steuerzeichen sind nicht
     *          zul&auml;ssig
     */
    private void setPosition(String position) {

        boolean control;
        char    digit;
        int     cursor;

        //der Server wird dekodiert, optimiert und vereinfacht
        position = position == null ? "" : position.trim();

        //die Zeichen ?# sowie alle Steuerzeichen werden nicht unterstuetzt
        for (cursor = 0, control = true; control && cursor < position.length(); cursor++) {

            control = !((digit = position.charAt(cursor)) == '#' || digit == '?' || digit <= ' ');
        }

        //fehlerhafte Zeichen fuehren zur IllegalArgumentException
        if (!control) throw new IllegalArgumentException(("Invalid character in ").concat(position));

        this.position = position;
    }

    /**
     *  R&uuml;ckgabe vom Verzeichnis bzw. ein leerer String wenn keins vorliegt.
     *  @return das Verzeichnis bzw. ein leerer String wenn keins vorliegt
     */
    public String getDirectory() {

        return (this.server.length() > 0 && !this.directory.startsWith("/")) ? ("/").concat(this.directory) : this.directory;
    }

    /**
     *  Setzt das Verzeichnis. Die Gross- und Kleinschreibung bleibt erhalten.
     *  Die Pfad werden generell ausgeglichen, Backslash in Slash umgesetzt und
     *  alle endenden nicht druckbare Zeichen entfernt. Insgesamt sollte auf
     *  die Zeichen <b>:*?"&#60;&#62;|</b> verzeichnet werden, da diese von
     *  verschiedenen Betriebssystemen im Dateisystem nicht zul&auml;ssig sind.
     *  @param  directory Verzeichnis der Adresse
     *  @throws IllegalArgumentException bei ung&uuml;ltigem Directory, f&uuml;r
     *          die Angabe sind die Zeichen ?# sowie Steuerzeichen sind nicht
     *          zul&auml;ssig
     */
    private void setDirectory(String directory) {

        boolean control;
        char    digit;
        int     cursor;

        //das Directory wird dekodiert, optimiert und vereinfacht
        directory = directory == null ? "" : directory.trim();

        //es werden alle Zeichen ausser ?#[Steuerzeichen] unterstuetzt
        for (cursor = 0, control = true; control && cursor < directory.length(); cursor++) {

            control = !((digit = directory.charAt(cursor)) == '?' || digit == '#' || digit < ' ');
        }

        //fehlerhafte Zeichen fuehren zur IllegalArgumentException
        if (!control) throw new IllegalArgumentException(("Invalid character in ").concat(directory));

        this.directory = directory;
    }

    /**
     *  R&uuml;ckgabe der Datei bzw. ein leerer String wenn keine gesetzt wurde.
     *  @return die Datei bzw. ein leerer String wenn keine gesetzt wurde
     */
    public String getFile() {

        return this.file;
    }

    /**
     *  Setzt die Datei. Die Gross- und Kleinschreibung bleibt erhalten.
     *  Insgesamt sollte auf die Zeichen <b>/\:*?"&#60;&#62;|</b> verzeichnet
     *  werden, da diese von verschiedenen Betriebssystemen im Dateinamen nicht
     *  zul&auml;ssig sind.
     *  @param  file Datei der Adresse
     *  @throws IllegalArgumentException bei ung&uuml;ltigem Directory, f&uuml;r
     *          die Angabe sind die Zeichen /\?# sowie Steuerzeichen sind nicht
     *          zul&auml;ssig
     */
    private void setFile(String file) {

        boolean control;
        char    digit;
        int     cursor;

        //der File wird dekodiert, optimiert und vereinfacht
        file = file == null ? "" : file.trim();

        //es werden alle Zeichen ausser /\?#[Steuerzeichen] unterstuetzt
        for (cursor = 0, control = true; control && cursor < file.length(); cursor++) {

            control = !((digit = file.charAt(cursor)) == '?' || digit == '/' || digit == '\\' || digit == '#' || digit < ' ');
        }

        //fehlerhafte Zeichen fuehren zur IllegalArgumentException
        if (!control) throw new IllegalArgumentException(("Invalid character in ").concat(file));

        this.file = file;
    }

    /**
     *  R&uuml;ckgabe der Parameter bzw. ein leerer String wenn keine vorliegt.
     *  @return die Parameter bzw. ein leerer String wenn keine vorliegt
     */
    public String getQuery() {

        return this.query;
    }

    /**
     *  Setzt die Parameter. Die Gross- und Kleinschreibung bleibt erhalten. Die
     *  Parameter werden alphabetisch sortiert um ein sp&auml;teres Vergleichen
     *  anderer Adressen zu erm&ouml;glichen.
     *  @param  query Parameterliste
     *  @throws IllegalArgumentException bei ung&uuml;ltigem Wertzuweisungen
     *          ohne Parameter
     */
    private void setQuery(String query) {

        List            strings;
        String          stream;
        String          string;
        StringTokenizer tokenizer;

        int             cursor;

        //die Query wird dekodiert, optimiert und vereinfacht
        query     = query == null ? "" : query.trim();
        tokenizer = new StringTokenizer(query, "&");
        strings   = new ArrayList();

        while (tokenizer.hasMoreTokens()) {

            string = tokenizer.nextToken();
            cursor = string.indexOf('=');
            stream = (cursor >= 0) ? string.substring(0, cursor) : string;
            string = (cursor >= 0) ? string.substring(cursor +1) : "";

            //Werte ohne Parameterangaben sind nicht zulaessig
            if (stream.trim().length() == 0) throw new IllegalArgumentException(("Invalid structure in ").concat(query));

            //es werden nur Parameter mit Wert in Verbindung mit "=", sonst nur
            //als Parameter ohne Wertzuweisung uebernommen
            strings.add((string.length() > 0) ? stream.concat("=").concat(string) : stream);
        }

        //die Parameter werden alphabetisch sortiert
        Collections.sort(strings);

        for (cursor = 0, string = ""; cursor < strings.size(); cursor++) {

            //der Query wird optimiert aufgebaut
            string = (string.length() > 0) ? string.concat("&") : string;
            string = string.concat((String)strings.get(cursor));
        }

        this.query = string;
    }

    /**
     *  R&uuml;ckgabe der kompletten Adresse ohne Protokoll, Server und
     *  Portangabe. Sonderzeichen werden automatisch UTF8/MIME kodiert.
     *  Eine eventuelle Positionsangabe ist nicht enthalten.
     *  @return die kompletten Adresse
     */
    public String toStringShort() {

        String stream;
        String string;

        string = this.directory;
        string = (!string.endsWith("/")) ? string.concat("/") : string;
        stream = string.concat(this.file);
        stream = (stream.length() == 0) ? stream = "/" : stream;

        if (this.query.length() > 0) stream = stream.concat("?").concat(this.query);

        return stream;
    }

    /**
     *  R&uuml;ckgabe der kompletten Adresse mit UTF8/MIME kodierten
     *  Sonderzeichen. Eine eventuelle Positionsangabe ist nicht enthalten.
     *  @return die kompletten Adresse
     */
    public String toString() {

        String stream;
        String string;

        stream = (this.protocol.length() > 0) ? this.protocol.concat("://") : this.protocol;

        if (stream.length() == 0 && this.server.length() > 0 && this.port != 443) stream = "http://";
        if (stream.length() == 0 && this.server.length() > 0 && this.port == 443) stream = "https://";

        string = String.valueOf(this.getPort());

        if (string.equals("80") && this.protocol.equals("http") || (string.equals("443") && this.protocol.equals("https")) || string.equals("0")) string = "";

        stream = (this.server.length() > 0) ? stream.concat(this.server) : "";

        if (stream.length() > 0 && string.length() > 0) stream = stream.concat(":").concat(string);

        string = this.getDirectory();

        if (string.length() > 0 && stream.length() > 0 && !string.endsWith("/")) string = string.concat("/");

        stream = stream.concat(string).concat(this.file);

        if (this.query.length() > 0) stream = stream.concat("?").concat(this.query);

        return stream;
    }
}