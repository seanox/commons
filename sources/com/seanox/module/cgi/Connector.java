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
package com.seanox.module.cgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 *  Connector stellt mit eine Java Schnittstelle f&uuml;r CGI und DCGI zur
 *  Verf&uuml;gung. Der Zugriff erfolgt dabei &uuml;ber die bereitgestellten
 *  Objekte Connection, Environment, Request, Response, Fragments und Cookie.
 *  Diese unterst&uuml;tzen unter anderem den Umgang mit Multipart-Objekten
 *  sowie die Kodierung und Dekodierung von BASE64, UTF8, MIME und DOT.<br>
 *  <br>
 *  Connector 1.2013.0720<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0720
 */
public class Connector extends com.seanox.module.Connector {
    
    /** Objekt der Serververbindung */
    public final Connection connection;

    /** Objekt der Systemumgebung */
    public final Environment environment;

    /** Request Objekt zur Bearbeitung der Anfrage */
    public final Request request;

    /** Response Objekt zur Beantwortung des Request */
    public final Response response;    

    /** Dateneingangsstrom */
    private InputStream input;

    /** Datenausgabestrom */
    private OutputStream output;

    /**
     *  Konstruktor, richtet den Connector per StdIO ein.
     *  @throws IOException bei fehlerhaftem Zugriff auf den Request oder den
     *          Datenstrom oder beim Erreichen der maximalen Datenleerlaufzeit
     */
    public Connector() throws IOException {

        this(System.in, System.out);
    }

    /**
     *  Konstruktor, richtet den Connector mit dem &uuml;bergebenen
     *  Dateneingangsstrom ein. Als Datenausgangsstrom wird StdIO verwendet.
     *  @param  input  Dateneingangsstrom
     *  @throws IOException bei fehlerhaftem Zugriff auf den Request oder den
     *          Datenstrom oder beim Erreichen der maximalen Datenleerlaufzeit
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     */
    public Connector(InputStream input) throws IOException {

        this(input, System.out);
    }

    /**
     *  Konstruktor, richtet den Connector mit den &uuml;bergebenen
     *  Datenstr&ouml;men ein.
     *  @param  input  Dateneingangsstrom
     *  @param  output Datenausgangsstrom
     *  @throws IOException bei fehlerhaftem Zugriff auf den Request oder den
     *          Datenstrom oder beim Erreichen der maximalen Datenleerlaufzeit
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     */
    public Connector(InputStream input, OutputStream output) throws IOException {

        if (input == null)  throw new IllegalArgumentException("Invalid connector input [null]");
        if (output == null) throw new IllegalArgumentException("Invalid connector output [null]");

        //die Datenstroeme werden eingerichtet
        this.input  = input;
        this.output = output;

        super.environment = this.environment = new Environment(this);
        super.connection  = this.connection  = new Connection(this);
        super.request     = this.request     = new Request(this);
        super.response    = this.response    = new Response(this);

        this.mount();
    }

    /** &Uuml;bernimmt alle CGI-Variablen aus den Java Argumenten. */
    private void mount() {

        Enumeration enumeration;
        String      stream;
        String      string;

        //alle Systemparameter werden ermittelt
        enumeration = System.getProperties().keys();

        while (enumeration.hasMoreElements()) {

            string = (String)enumeration.nextElement();
            stream = System.getProperties().getProperty(string, "");
            string = string.toLowerCase();

            if (string.startsWith("cgi.")) this.environment.set(string.substring(4), stream);
        }
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zum Connector als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zum Response als String
     */
    public String toString() {
        
        String       string;
        StringBuffer result;

        //der Zeilenumbruch wird ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = new StringBuffer(super.toString());

        result.append("  input       = ").append(this.input.getClass().getName()).append(this.input.equals(System.in) ? " (StdIn)" : "").append(string);
        result.append("  output      = ").append(this.output.getClass().getName()).append(this.output.equals(System.out) ? " (StdOut)" : "").append(string);

        return result.toString();
    }
}