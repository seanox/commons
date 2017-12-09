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

import com.seanox.common.Accession;
import com.seanox.common.Section;

/**
 *  Process stellt die Basis zur Implementierung von Modulen des gleichnamigen
 *  Modultyps zur Verf&uuml;gung.<br>
 *  Das Ziel einer HTTP-Anfrage sind Verzeichnisse, Dateien sowie dynamische
 *  Inhalte externe von  CGI- und DCGI-Anwendungen, welche &uuml;ber virtuelle
 *  Pfade angesprochen werden. Prozess-Module stelle eine weitere Form von
 *  Zielen dar. Auch diese werden &uuml;ber virtuelle Pfade angesprochen und
 *  &auml;hneln der Verwendung vom CGI bzw. DCGI. Im Unterschied handelt es sich
 *  dabei um interne Prozesse, welche im Context vom Server laufen und die
 *  Beantwortung der Anfrage &uuml;bernehmen (zur Konfiguration siehe auch
 *  Dokumentation des Servers).<br>
 *  <br>
 *  Process 1.2013.0429<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0429
 */
public class Process extends Extension {

    /** Objekt der Serververbindung */
    public final Connection connection;

    /** Objekt der Systemumgebung */
    public final Environment environment;

    /** Request Objekt zur Bearbeitung der Anfrage */
    public final Request request;

    /** Response Objekt zur Beantwortung des Request */
    public final Response response;

    /** Mimetypes des Servers */
    public final Section mimetypes;

    /** Konfiguration des Servers */
    public final Section options;

    /**
     *  Konstruktor, richtet Process auf Basis vom &uuml;bergebenen Context ein.
     *  @param  context Context
     *  @param  object  Bezugsobjekt
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Process(Context context, Object object) {

        super(context, object);

        //die Modul-Ressourcen werden eingerichtet
        this.connection  = new Connection(super.socket, super.secure);
        this.environment = new Environment(super.accession);
        this.request     = new Request(super.accession);
        this.response    = new Response(super.accession);
        this.mimetypes   = new Section();
        this.options     = new Section();        

        this.mount();
    }

    /**
     *  Richtet zus&auml;tzlich ben&ouml;tigte Ressourcen ein bzw. manipuliert
     *  diese zur Bereitstellung der modultypischen Anforderungen.
     *  @throws RuntimeException wenn das Modul nicht eingerichtet werden kann
     */
    private void mount() {

        Object object;

        try {

            //der Inhalt vom Feld options wird kopiert
            object = Context.mountField(super.accession, "options", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.options, "entries");
            Accession.storeField(object, this.options, "list");

            //der Inhalt vom Feld mimetypes wird kopiert
            object = Context.mountField(super.accession, "server.mimetypes", true);

            //die Eintraege werden uebernommen
            Accession.storeField(object, this.mimetypes, "entries");
            Accession.storeField(object, this.mimetypes, "list");

        } catch (Exception exception) {

            throw new RuntimeException("Connector mount failed", exception);
        }
    }
}