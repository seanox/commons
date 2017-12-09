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
import java.io.OutputStream;

import com.seanox.common.Accession;

/**
 *  Response stellt Konstanten und Methoden zur Beantwortung von HTTP-Anfragen
 *  per CGI und DCGI zur Verf&uuml;gung und gestattet dabei einen einfachen und
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

    /** Objekt der Systemumgebung */
    private Environment environment;

    /** Datenausgabestrom */
    private OutputStream output;

    /**
     *  Konstruktor, richtet den Response auf Basis des mit der Schnittstelle
     *  &uuml;bergebenen Datenstroms ein.
     *  @param  connector Schnittstelle
     *  @throws IllegalArgumentException bei ung&uml;tiger Schnittstelle
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Response(Connector connector) {
        
        super();

        if (connector == null) throw new IllegalArgumentException("Invalid connector [null]");

        this.environment = connector.environment;

        try {Accession.storeField(connector, this, "output");
        } catch (Exception exception) {

            throw new RuntimeException("OutputStream access failed", exception);
        }

        if (this.output == null) throw new RuntimeException("OutputStream is not established");
    }

    /**
     *  Sendet den Status direkt &uuml;ber den Server. Dieser
     *  ber&uuml;cksichtigt dabei alle serverseitigen Definitionen wie Templates
     *  und Redirections.<br><br>
     *  <b>Hinweis</b> - Die Methode wird nur bei Seanox Devwex unerst&uuml;tzt.
     *  @param  status Serverstatus
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist oder sich im gesch&uuml;tzten
     *          Modus befindet
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws UnsupportedOperationException wenn der Aufruf nicht unter Seanox
     *          Devwex erfolgt, da die Methode nur dort unterst&uuml;tzt wird
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

        string = this.environment.get("server_software");
        
        //die Funktion wird nur von Seanox Devwex unterstuetzt
        if (!string.toLowerCase().startsWith("seanox-devwex")) throw new UnsupportedOperationException("Method not supported by server");
        
        //das Protkoll muss geaendert und der Status gesetzt werden
        this.setProtocol("HTTP/STATUS");
        
        super.sendStatus(status);
    }

    /**
     *  Sendet den im Puffer befindlichen Header vom Response.
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void flush() throws IOException {

        String string;

        if (!this.bounded) throw new IllegalStateException("Response is bounded");

        if (!this.control || !this.allowed) return;

        //der Responsestatus wird ermittelt
        if (this.status == 0) this.status = 200;
        
        //der Responseheader wird zusammengestellt
        string = this.getHeader();

        //der Header wird in den Datenstrom geschrieben
        try {this.output.write(string.getBytes());
        } finally {this.control = false;}

        //der Datenpuffer wird geleert
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