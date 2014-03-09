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

import java.net.Socket;

import com.seanox.common.Accession;

/**
 *  Service stellt die Basis zur Implementierung von Modulen des gleichnamigen
 *  Modultyps zur Verf&uuml;gung.<br>
 *  Services &uuml;bernehmen die kompletten Requestverarbeitung und sind zur
 *  Implementierung weiterer auf TCP/IP basierenden Protokollen gedacht. Dieser
 *  Modultype steht nur den Servern, nicht aber den virtuellen Hosts, zur
 *  Verf&uuml;gung. (zur Konfiguration siehe auch Dokumentation des Servers).<br>
 *  <br>
 *  Service 1.2013.0429<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0429
 */
public class Service extends Extension {

    /** Objekt der Serververbindung */
    public final Connection connection;

    /** Server Objekt */
    public final Server server;

    /** Server Socket */
    public final Socket socket;

    /**
     *  Konstruktor, richtet Service auf Basis vom &uuml;bergebenen Context ein.
     *  @param  context Context
     *  @param  object  Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Service(Context context, Object object) {

        super(context, object);

        //die Modul-Ressourcen werden eingerichtet
        this.connection = new Connection(super.socket, super.secure);
        this.server     = new Server(super.accession);
        this.socket     = super.socket;

        this.mount();
    }

    /**
     *  Richtet zus&auml;tzlich ben&ouml;tigte Ressourcen ein bzw. manipuliert
     *  diese zur Bereitstellung der modultypischen Anforderungen.
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    private void mount() {

        boolean control;

        //aus dem Socket wird die Secure Layer Verwendung ermittel
        control = this.server.getOptions().get("socket").toLowerCase().equals("secure");

        //die Secure Layer Verwendung wird ueber Reflections gesetzt
        try {Accession.set(this.connection, "secure", new Boolean(control));
        } catch (Exception exception) {

            throw new RuntimeException("Connector mount failed", exception);
        }
    }
}