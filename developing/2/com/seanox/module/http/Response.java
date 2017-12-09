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

import java.io.IOException;
import java.io.OutputStream;

import com.seanox.common.Accession;
import com.seanox.common.Section;

/**
 *  Response stellt Konstanten und Methoden zur Beantwortung von HTTP-Anfragen
 *  zur Verf&uuml;gung und gestattet dabei einen einfachen und gezielten Umgang
 *  mit dem Header, Cookies und dem Datenstrom. Zur Information k&ouml;nnen die
 *  aktuellen Daten formatiert ausgegeben werden.<br>
 *  <br>
 *  <b>Hinweis</b> - Das Bezugsobjekt wurde speziell auf Seanox Devwex
 *  abgestimmt. Bei der Verwendung anderer Umgebungen m&uuml;ssen die Zugriffe
 *  auf die entsprechenden Ressourcen speziell implementiert werden.<br>
 *  <br>
 *  Response 1.2013.0517<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0517
 */
public class Response extends com.seanox.module.Response {
    
    /** Bezugsobjekt */
    private Object accession;

    /** Datenausgabestrom */
    private OutputStream output;
    
    /** Environment des Servers */
    private Environment environment;
    
    /** Konfiguration des Servers */
    private Section options;

    /**
     *  Konstruktor, richtet den Response auf Basis des Accession Objekts ein.
     *  @param  accession Bezugsobjekt
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Response(Object accession) {
        
        super();
        
        Object object;        

        this.accession = accession;

        if (accession == null) throw new IllegalArgumentException("Invalid accession [null]");

        try {

            Accession.storeField(this.accession, this, "control", Accession.EXPORT);
            Accession.storeField(this.accession, this, "status", Accession.EXPORT);
            Accession.storeField(this.accession, this, "output", Accession.EXPORT);
            Accession.storeField(this.accession, this, "volume", Accession.EXPORT);
            
            this.environment = new Environment(accession);
            this.options     = new Section();
            
            //der Inhalt vom Feld options wird kopiert
            object = Context.mountField(accession, "options", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.options, "entries");
            Accession.storeField(object, this.options, "list");            

        } catch (Exception exception) {

            throw new RuntimeException("Synchronization to server failed", exception);
        }

        this.allowed = (this.control = (this.status == 200 || this.control));

        if (this.output == null) throw new RuntimeException("Server output stream is not established");
    }

    /**
     *  Synchronisiert die transferierten Daten mit der Server Instanz.
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    private void synchronizeTransfer() {

        try {

            //die notwendigen Felder werden synchronisiert
            Accession.storeField(this.accession, this, "control", Accession.IMPORT);
            Accession.storeField(this.accession, this, "status", Accession.IMPORT);
            Accession.storeField(this.accession, this, "volume", Accession.IMPORT);

        } catch (Exception exception) {

            throw new RuntimeException("Synchronization to server failed", exception);
        }
    }

    /**
     *  R&uuml;ckgabe der ermittelbaren Serverkennung, sonst <code>null</code>.
     *  @return die ermittelte Serverkennung, sonst <code>null</code>
     */
    protected String getServerIdentity() {

        String string;

        if (!this.options.get("identity").toLowerCase().equals("on")) return null;

        string = this.environment.get("server_software");

        return string.length() == 0 ? null : string;
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
        
        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        //der Responsestatus wird gesetzt
        this.setStatus(status);
        
        //der Responsestatus wird ermittelt
        if (this.status == 0) this.status = 200;
        
        //der Response wird als geschlossen markiert
        this.allowed = false;
        
        //die Transferdaten werden synchronisiert
        this.synchronizeTransfer();
    }    

    /**
     *  Sendet den im Puffer befindlichen Header vom Response.
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void flush() throws IOException {

        String string;

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        
        if (!this.control || !this.allowed) return;

        //der Responsestatus wird ermittelt
        if (this.status == 0) this.status = 200;

        //der Responseheader wird zusammengestellt
        string = this.getHeader();

        //der Header wird in den Datenstrom geschrieben
        try {this.output.write(string.getBytes());
        } finally {

            this.control = false;

            //die Transferdaten werden synchronisiert
            this.synchronizeTransfer();
        }

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

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        try {

            //das Byte wird in den Datenstrom geschrieben
            this.output.write(code);

        } finally {

            //die uebermittelte Datenmenge wird registriert
            this.addTransferVolume(1);

            //die Transferdaten werden synchronisiert
            this.synchronizeTransfer();
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

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        try {

            //die Bytes werden in den Datenstrom geschrieben
            this.output.write(bytes);

        } finally {

            //die uebermittelte Datenmenge wird registriert
            this.addTransferVolume(bytes.length);

            //die Transferdaten werden synchronisiert
            this.synchronizeTransfer();
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

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        try {

            //die Bytes werden in den Datenstrom geschrieben
            this.output.write(bytes, offset, length);

        } finally {

            //die uebermittelte Datenmenge wird registriert
            this.addTransferVolume(length);

            //die Transferdaten werden synchronisiert
            this.synchronizeTransfer();
        }
    }
    
    /**
     *  Schreibt den Puffer des Headers in den Datenausgabestrom vom Response,
     *  und schliesst den Response wie auch den Datenausgabestrom.
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public void close() throws IOException {

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.flush();

        //der Datenstrom wird geschlossen
        try {this.output.close();
        } finally {

            //die Transferdaten werden synchronisiert
            this.synchronizeTransfer();
        }

        this.allowed = false;
    }
}