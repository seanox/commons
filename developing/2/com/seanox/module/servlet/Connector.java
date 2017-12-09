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

import java.util.Enumeration;

import com.seanox.common.Accession;
import com.seanox.module.servlet.Connection;
import com.seanox.module.servlet.Environment;
import com.seanox.module.servlet.Request;
import com.seanox.module.servlet.Response;

/**
 *  Connector stellt mit eine Schnittstelle f&uuml;r Servlets zur Seanox Modul
 *  API zur Verf&uuml;gung. Der Zugriff erfolgt dabei &uuml;ber die
 *  bereitgestellten Objekte Connection, Environment, Request, Response,
 *  Fragments und Cookie. Diese unterst&uuml;tzen unter anderem den Umgang mit
 *  Multipart-Objekten sowie die Kodierung und Dekodierung von BASE64, UTF8,
 *  MIME und DOT.<br>
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

    /** Servlet Request (Reflection) */
    private Object input;

    /** Servlet Response (Reflection) */
    private Object output;

    /**
     *  Konstruktor, richtet den Connector auf Basis der &uuml;bergebenen
     *  Response und Request Objkete des Servlets ein.
     *  @param  request  Servlet Request
     *  @param  response Servlet Response
     *  @throws IllegalArgumentException bei ung&uml;tiger Schnittstelle
     */
    public Connector(Object request, Object response) {

        if (request == null)  throw new IllegalArgumentException("Invalid request [null]");
        if (response == null) throw new IllegalArgumentException("Invalid response [null]");

        this.output = response;
        this.input  = request;

        super.environment = this.environment = new Environment(this);

        this.mount();

        super.connection = this.connection = new Connection(this);
        super.request    = this.request    = new Request(this);
        super.response   = this.response   = new Response(this);
    }

    /** Richtet die Ressourcen des Connectors ein. */
    private void mount()  {

        Enumeration enumeration;
        String      buffer;
        String      stream;
        String      string;

        try {

            this.environment.set("REQUEST_METHOD", (String)Accession.invoke(this.input, "getMethod"));
            this.environment.set("SERVER_NAME", (String)Accession.invoke(this.input, "getServerName"));
            this.environment.set("REMOTE_ADDR", (String)Accession.invoke(this.input, "getRemoteAddr"));
            this.environment.set("SERVER_PROTOCOL", (String)Accession.invoke(this.input, "getProtocol"));
            this.environment.set("SCRIPT_NAME", (String)Accession.invoke(this.input, "getRequestURI"));
            this.environment.set("SCRIPT_URL", (String)Accession.invoke(this.input, "getRequestURI"));

            string = (String)Accession.invoke(this.input, "getQueryString");

            if (string != null && string.trim().length() > 0) this.environment.set("QUERY_STRING", string);

            //der Remoteport ist erst mit Servlet API 2.4 verfuegbar
            try {this.environment.set("REMOTE_PORT", ((Integer)Accession.invoke(this.input, "getRemotePort")).toString());
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            //die URI vom Request wird ermittelt
            string = this.environment.get("QUERY_STRING");
            string = string.length() > 0 ? ("?").concat(string) : string;
            stream = this.environment.get("SCRIPT_NAME");

            this.environment.set("REQUEST_URI", stream.concat(string));
            this.environment.set("SERVER_PORT", ((Integer)Accession.invoke(this.input, "getServerPort")).toString());
            this.environment.set("SCRIPT_URI", ((StringBuffer)Accession.invoke(this.input, "getRequestURL")).toString());

            //mit der Servlet API 2.4 ist die PathInfo unter Umstaenden nicht korrekt
            string = (String)Accession.invoke(this.input, "getPathInfo");
            stream = (String)Accession.invoke(this.input, "getRequestURI");
            buffer = (String)Accession.invoke(this.input, "getContextPath");

            if (string == null || string.trim().length() == 0) string = stream.substring(buffer.length());

            this.environment.set("PATH_INFO", string);

            //alle Parameter des Headers werden ermittelt
            enumeration = (Enumeration)Accession.invoke(this.input, "getHeaderNames");

            while (enumeration.hasMoreElements()) {

                string = (String)enumeration.nextElement();
                stream = (String)Accession.invoke(this.input, "getHeader", new Object[] {string});
                string = (string != null) ? string.replace('-', '_').trim() : "";

                //die Parameter des Headers werden uebernommen
                if (stream.length() > 0) this.environment.set(("HTTP_").concat(string), stream);
            }

        } catch (Exception exception) {

            throw new RuntimeException("Connector mount failed", exception);
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
        
        result.append("  input       = ").append(this.input.getClass().getName()).append(string);
        result.append("  output      = ").append(this.output.getClass().getName()).append(string);

        return result.toString();        
    }
}