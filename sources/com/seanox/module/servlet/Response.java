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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;

import com.seanox.common.Accession;

/**
 *  Response stellt Konstanten und Methoden zur Beantwortung von HTTP-Anfragen
 *  per Servlet zur Verf&uuml;gung und gestattet dabei einen einfachen und
 *  gezielten Umgang mit dem Header, Cookies und dem Datenstrom. Zur Information
 *  k&ouml;nnen die aktuellen Daten formatiert ausgegeben werden.<br>
 *  <br>
 *  Response 1.2013.0517<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0517
 */
public class Response extends com.seanox.module.Response {

    /** Datenausgabestrom */
    private OutputStream output;

    /** Servlet Response */
    private Accession response;

    /**
     *  Konstruktor, richtet den Response auf Basis des mit der Schnittstelle
     *  &uuml;bergebenen ServletResponse ein.
     *  @param  connector Schnittstelle
     *  @throws IllegalArgumentException bei ung&uml;tiger Schnittstelle
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Response(Connector connector) {
        
        super();
        
        if (connector == null) throw new IllegalArgumentException("Invalid connector [null]");

        //der Basis Response wird eingerichtet
        try {this.response = (Accession)Accession.get(connector, "responsebase");
        } catch (Exception exception) {

            throw new RuntimeException("ServletResponse access on responsebase failed", exception);
        }

        if (this.response == null) throw new RuntimeException("ServletResponse is not established");
    }

    /**
     *  Sendet den Status direkt &uuml;ber den Server.
     *  @param  status Serverstatus
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist oder sich im gesch&uuml;tzten
     *          Modus befindet
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_CREATED
     *  @see    #STATUS_ACCEPTED
     *  @see    #STATUS_MULTIPLE_CHOICES
     *  @see    #STATUS_MOVED_PERMANENTLY
     *  @see    #STATUS_FOUND
     *  @see    #STATUS_NOT_MODIFIED
     *  @see    #STATUS_USE_PROXY
     *  @see    #STATUS_TEMPORARY_REDIRECT
     *  @see    #STATUS_BAD_REQUEST
     *  @see    #STATUS_AUTHORIZATION_REQUIRED
     *  @see    #STATUS_FORBIDDEN
     *  @see    #STATUS_NOT_FOUND
     *  @see    #STATUS_METHOD_NOT_ALLOWED
     *  @see    #STATUS_NONE_ACCEPTABLE
     *  @see    #STATUS_PROXY_AUTHENTICATION_REQUIRED
     *  @see    #STATUS_REQUEST_TIMEOUT
     *  @see    #STATUS_LENGTH_REQUIRED
     *  @see    #STATUS_UNSUPPORTED_MEDIA_TYPE
     *  @see    #STATUS_METHOD_FAILURE
     *  @see    #STATUS_INTERNAL_SERVER_ERROR
     *  @see    #STATUS_NOT_IMPLEMENTED
     *  @see    #STATUS_BAD_GATEWAY
     *  @see    #STATUS_SERVICE_UNAVAILABLE
     *  @see    #STATUS_GATEWAY_TIMEOUT
     */
    public void sendStatus(int status) throws IOException {

        String string;

        if (!this.bounded) throw new IllegalStateException("Response is in bounded mode");
        if (!this.control) throw new IllegalStateException("Response header already sent");
        if (!this.allowed) throw new IllegalStateException("Response already sent");
        
        //der Responsestatus wird gesetzt
        this.setStatus(status);

        //der Text des Status wird ermittelt
        string = (this.message.length() > 0) ? this.message : null;

        //der Serverstatus wird ueber den ServletResponse gesetzt
        try {Accession.invoke(this.response, "sendError", new Class[] {Integer.TYPE, String.class}, new Object[] {new Integer(this.status), string});
        } catch (Exception exception) {

            string = exception.getMessage();
            string = string == null ? "" : (" (").concat(string).concat(")");

            throw new IOException(("ServletResponse call on method sendError failed").concat(string));

        } finally {

            //die Ausgabekontrolle wird gesetzt
            this.control = false;
            this.allowed = false;
        }
    }

    /**
     *  Sendet ein Redirect zur Umleitung der Seite an die angegebene Adresse.
     *  @param  address URL der Umleitung
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist oder sich im gesch&uuml;tzten
     *          Modus befindet
     */
    public void sendRedirect(String address) throws IOException {

        String string;

        if (!this.bounded) throw new IllegalStateException("Response is in bounded mode");
        if (!this.control) throw new IllegalStateException("Response header already sent");
        if (!this.allowed) throw new IllegalStateException("Response already sent");
        
        //der Responsestatus wird gesetzt
        this.setStatus(302);

        //die Redirection wird ueber den ServletResponse gesetzt
        try {Accession.invoke(this.response, "sendRedirect", new Object[] {address});
        } catch (Exception exception) {

            string = exception.getMessage();
            string = string == null ? "" : (" (").concat(string).concat(")");

            throw new IOException(("ServletResponse call on method sendRedirect failed").concat(string));

        } finally {

            this.allowed = this.control = false;
        }
    }

    /**
     *  Sendet den im Puffer befindlichen Header vom Response.
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void flush() throws IOException {

        Enumeration enumeration;
        String      string;
        
        String[]    values;

        int         loop;

        if (!this.bounded) throw new IllegalStateException("Response is bounded");

        if (!this.control || !this.allowed) return;

        //der Responsestatus wird ermittelt
        if (this.status == 0) this.status = 200;

        //der Text des Status wird ermittelt
        string = (this.message.length() > 0) ? this.message : null;

        //der Serverstatus wird ueber den ServletResponse gesetzt
        try {Accession.invoke(this.response, "setStatus", new Class[] {Integer.TYPE, String.class}, new Object[] {new Integer(this.status), string});
        } catch (Exception exception) {

            string = exception.getMessage();
            string = string == null ? "" : (" (").concat(string).concat(")");

            throw new IOException(("ServletResponse call on method setStatus failed").concat(string));

        } finally {

            this.control = false;
        }
        
        //ggf. wird der Date-Header gesetzt
        if (!this.containsHeaderField("date")) this.setHeaderField("Date", com.seanox.module.Response.formatDate("'Date:' E, dd MMM yyyy HH:mm:ss z", new Date(), "GMT"));

        //der Header wird einzeln gsetzt 
        enumeration = this.header.elements();
        
        while (enumeration.hasMoreElements()) {
            
            values = (String[])enumeration.nextElement();
            
            for (loop = 0; loop < values.length; loop += 2) {
                
                //die Felder des Headers werden ueber den ServletResponse gesetzt
                try {Accession.invoke(this.response, "setHeader", new Object[] {values[loop], values[loop +1]});
                } catch (Exception exception) {

                    string = exception.getMessage();
                    string = string == null ? "" : (" (").concat(string).concat(")");

                    throw new IOException(("ServletResponse call on method setHeader failed").concat(string));

                } finally {

                    this.control = false;
                }
            }
        }
        
        //der Datenausgabestrom wird ermittelt
        try { if (this.output == null) this.output = (OutputStream)Accession.invoke(this.response, "getOutputStream");
        } catch (Exception exception) {

            string = exception.getMessage();
            string = string == null ? "" : (" (").concat(string).concat(")");

            throw new IOException(("ServletResponse call on method getOutputStream failed").concat(string));
        }

        //scheitert die Einrichtung fuehrt das zur Ausnahme vom Typ IOException
        if (this.output == null) throw new IOException("OutputStream is not established");

        //der Puffer des ServletResponse wird geleert
        try {Accession.invoke(this.response, "flushBuffer");
        } catch (Exception exception) {

            string = exception.getMessage();
            string = string == null ? "" : (" (").concat(string).concat(")");

            throw new IOException(("ServletResponse call on method flushBuffer failed").concat(string));
        }

        //der Datenstrom wird geleert
        this.output.flush();
    }

    /**
     *  Schreibt den Puffer des Headers und das angegebene Byte in den
     *  Datenausgabestrom vom Response.
     *  @param  code Byte
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void write(int code) throws IOException {

        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        try {this.output.write(code);
        } finally {

            this.addTransferVolume(1);
        }
    }

    /**
     *  Schreibt den Puffer des Headers und die angegebenen Bytes in den
     *  Datenausgabestrom vom Response.
     *  @param  bytes Bytes als Array
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void write(byte[] bytes) throws IOException {

        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        //die Bytes werden in den Datenstrom geschrieben
        try {this.output.write(bytes);
        } finally {

            this.addTransferVolume(bytes.length);
        }
    }

    /**
     *  Schreibt den Puffer des Headers und die Bytes des angegebenen Bereichs
     *  aus dem ByteArray in den Datenausgabestrom vom Response.
     *  @param  bytes  Bytes als Array
     *  @param  offset Position im Byte Array
     *  @param  length Anzahl der Bytes
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void write(byte[] bytes, int offset, int length) throws IOException {

        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        //die Bytes werden in den Datenstrom geschrieben
        try {this.output.write(bytes, offset, length);
        } finally {

            this.addTransferVolume(length);
        }
    }
    
    /**
     *  Schreibt den Puffer des Headers in den Datenausgabestrom vom Response,
     *  und schliesst den Response wie auch den Datenausgabestrom.
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void close() throws IOException {

        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        //der Datenstrom wird geschlossen
        this.output.close();

        this.allowed = false;
    }   
}