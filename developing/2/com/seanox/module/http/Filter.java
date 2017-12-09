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
 *  Filter stellt die Basis zur Implementierung von Modulen des gleichnamigen
 *  Modultyps zur Verf&uuml;gung.<br>
 *  Die Zugriffe auf die physischen und virtuellen Hosts k&ouml;nnen &uuml;ber
 *  speziell definierte Regeln gesteuert werden.<br>
 *  Neben Weiterleitung und Server-Status kann die Verarbeitung auch an ein
 *  Modul vom Typ Filter weitergereicht werden. Filter-Module sind zur
 *  Pr&uuml;fung bestimmter Anforderungen und ggf. zur Manipulation von
 *  eingehenden Requests gedacht und sollten keine tiefere Logik implementieren.
 *  Module vom Typ Filter haben daher nur eingeschr&auml;nkten Zugriff auf den
 *  Request-Header (zur Konfiguration siehe auch Dokumentation des Servers).<br>
 *  <br>
 *  <b>Hinweis</b> - Der Request steht diesem Modultyp nur eingeschr&auml;nkt
 *  zur Verf&uuml;gung. Dabei ist der Zugriff auf alle Daten des Headers nicht
 *  aber auf den Body m&ouml;glich.<br>
 *  <br>
 *  Filter 1.2013.0429<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0429
 */
public class Filter extends Extension {
    
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
     *  Konstruktor, richtet Filter auf Basis vom &uuml;bergebenen Context ein.
     *  @param  context Context
     *  @param  object  Bezugsobjekt
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Filter(Context context, Object object) {

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
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    private void mount() {

        Object object;

        try {

            //synchronisiert das Feld status ueber Reflections
            Accession.storeField(super.accession, this, "status", Accession.EXPORT);

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

            //im Request wird mit dem Feld locked das lesen des Bodys gesperrt
            Accession.set(this.request, "locked", new Boolean(true));

        } catch (Exception exception) {

            throw new RuntimeException("Connector mount failed", exception);
        }
    }
}