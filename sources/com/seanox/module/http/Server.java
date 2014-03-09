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

import java.net.ServerSocket;

import com.seanox.common.Accession;
import com.seanox.common.Initialize;
import com.seanox.common.Section;

/**
 *  Server stellt Methoden f&uuml;r den Zugriff auf die Ressourcen des
 *  physischen Hosts zur Verf&uuml;gung. Zur Information k&ouml;nnen die
 *  aktuellen Daten formatiert ausgegeben werden.<br>
 *  <br>
 *  <b>Hinweis</b> - Das Bezugsobjekt wurde speziell auf Seanox Devwex
 *  abgestimmt. Bei der Verwendung anderer Umgebungen m&uuml;ssen die Zugriffe
 *  auf die entsprechenden Ressourcen speziell implementiert werden.<br>
 *  <br>
 *  Server 1.2013.0429<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0429
 */
public class Server {

    /** Server Konfigurationsobjekt */
    private Initialize initialize;

    /** Server Socket */
    private ServerSocket socket;

    /** Server Statuscodes */
    private Section statuscodes;

    /** Server Umgebungsvariablen */
    private Section environment;

    /** Server Common Gateway Interfaces */
    private Section interfaces;

    /** Server Aliase */
    private Section references;

    /** Server Mimetypes */
    private Section mimetypes;

    /** Server Konfiguration */
    private Section options;

    /** Server Filter */
    private Section filters;

    /** Server Module */
    private Section modules;

    /** Serverkennung und Bezeichnung */
    private String caption;

    /**
     *  Konstruktor, richtet den Server auf Basis des Accession Objekts ein.
     *  @param  accession Bezugsobjekt
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Server(Object accession) {

        Object server;
        Object object;

        this.initialize  = new Initialize();
        this.environment = new Section();
        this.filters     = new Section();
        this.interfaces  = new Section();
        this.mimetypes   = new Section();
        this.modules     = new Section();
        this.options     = new Section();
        this.references  = new Section();
        this.statuscodes = new Section();

        if (accession == null) throw new IllegalArgumentException("Invalid accession [null]");

        try {

            //der Server wird ueber Reflections als Accession eingerichtet
            server = Accession.get(accession, "server");

            //das Feld socket wird ueber Reflections synchronisiert
            Accession.storeField(server, this, "socket", Accession.EXPORT);

            if (this.socket == null) throw new RuntimeException("Server socket is not established");

            //das Feld caption wird ueber Reflections synchronisiert
            Accession.storeField(server, this, "caption", Accession.EXPORT);

            if (this.caption == null) throw new RuntimeException("Server caption is not established");

            //der Inhalt vom Feld initialize wird kopiert
            object = Context.mountField(server, "initialize", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.initialize, "entries");
            Accession.storeField(object, this.initialize, "list");

            //der Inhalt vom Feld statuscodes wird kopiert
            object = Context.mountField(server, "statuscodes", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.statuscodes, "entries");
            Accession.storeField(object, this.statuscodes, "list");

            //der Inhalt vom Feld environment wird kopiert
            object = Context.mountField(server, "environment", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.environment, "entries");
            Accession.storeField(object, this.environment, "list");

            //der Inhalt vom Feld interfaces wird kopiert
            object = Context.mountField(server, "interfaces", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.interfaces, "entries");
            Accession.storeField(object, this.interfaces, "list");

            //der Inhalt vom Feld references wird kopiert
            object = Context.mountField(server, "references", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.references, "entries");
            Accession.storeField(object, this.references, "list");

            //der Inhalt vom Feld mimetypes wird kopiert
            object = Context.mountField(server, "mimetypes", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.mimetypes, "entries");
            Accession.storeField(object, this.mimetypes, "list");

            //der Inhalt vom Feld options wird kopiert
            object = Context.mountField(server, "options", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.options, "entries");
            Accession.storeField(object, this.options, "list");

            //der Inhalt vom Feld filters wird kopiert
            object = Context.mountField(server, "filters", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.filters, "entries");
            Accession.storeField(object, this.filters, "list");

            //der Inhalt vom Feld modules wird kopiert
            object = Context.mountField(server, "modules", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.modules, "entries");
            Accession.storeField(object, this.modules, "list");

        } catch (Exception exception) {

            throw new RuntimeException("Synchronization to server failed", exception);
        }
    }

    /**
     *  R&uuml;ckgabe der Umgebungsvariablen des Servers.
     *  @return die Umgebungsvariablen des Servers
     */
    public Section getEnvironment() {

        return this.environment;
    }

    /**
     *  R&uuml;ckgabe der Interfaces des Servers.
     *  @return die Interfaces des Servers
     */
    public Section getInterfaces() {

        return this.interfaces;
    }

    /**
     *  R&uuml;ckgabe der Referenzen des Servers.
     *  @return die Referenzen des Servers
     */
    public Section getReferences() {

        return this.references;
    }

    /**
     *  R&uuml;ckgabe der Mimetypes des Servers.
     *  @return die Mimetypes des Servers
     */
    public Section getMimetypes() {

        return this.mimetypes;
    }

    /**
     *  R&uuml;ckgabe der Statuscodes des Servers.
     *  @return die Statuscodes des Servers
     */
    public Section getStatuscodes() {

        return this.statuscodes;
    }

    /**
     *  R&uuml;ckgabe der Optionen des Servers.
     *  @return die Optionen des Servers
     */
    public Section getOptions() {

        return this.options;
    }

    /**
     *  R&uuml;ckgabe der Filter des Servers.
     *  @return die Filter des Servers
     */
    public Section getFilters() {

        return this.filters;
    }

    /**
     *  R&uuml;ckgabe der Modulliste des Servers.
     *  @return die Modulliste des Servers
     */
    public Section getModules() {

        return this.modules;
    }

    /**
     *  R&uuml;ckgabe der Konfiguration des Servers.
     *  @return die Konfiguration des Servers
     */
    public Initialize getInitialize() {

        return this.initialize;
    }

    /**
     *  R&uuml;ckgabe des Sockets des Servers.
     *  @return der Sockets des Servers
     */
    public ServerSocket getSocket() {

        return this.socket;
    }

    /**
     *  R&uuml;ckgabe der Serverkennung.
     *  @return die Serverkennung
     */
    public String getCaption() {

        return this.caption;
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zum Server als String. Der
     *  Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zum Server als String
     */
    public String toString() {

        String       string;
        StringBuffer buffer;
        StringBuffer result;

        //der Zeilenumbruch wird ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = new StringBuffer("[").append(this.getClass().getName()).append("]").append(string);

        result = result.append("  caption   = ").append(this.caption).append(string);
        result = result.append("  socket    = ").append(this.socket).append(string);

        buffer = new StringBuffer();

        buffer.append(this.options.size()).append("x Options");
        buffer.append(", ").append(this.references.size()).append("x References");
        buffer.append(", ").append(this.interfaces.size()).append("x Interfaces");
        buffer.append(", ").append(this.environment.size()).append("x Environment");
        buffer.append(", ").append(this.filters.size()).append("x Filters");
        buffer.append(", ").append(this.modules.size()).append("x Modules");
        buffer.append(", ").append(this.statuscodes.size()).append("x Statuscodes");
        buffer.append(", ").append(this.mimetypes.size()).append("x Mimetypes");

        result = result.append("  structure = ").append(buffer).append(string);

        return result.toString();
    }
}