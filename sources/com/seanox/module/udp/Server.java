/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt. Diese
 *  Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2012 Seanox Software Solutions
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
package com.seanox.module.udp;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

import com.seanox.common.Accession;
import com.seanox.common.Initialize;
import com.seanox.common.Section;
import com.seanox.module.Context;
import com.seanox.module.udp.Session;

/**
 *  Server stellt eine abstrakte Klasse zur Implementierung physischer Hosts
 *  auf Basis von UDP f&uuml;r die Seanox Devwex Server-API bereit und kapselt
 *  elementare Funktionen wie Basis-Konfiguration und Connection-Handling. Die
 *  Verarbeitung eingehender Anfragen (Requests) erfolgt &uuml;ber einen Worker.
 *  Dieser implementiert {@link Session} und wird mit dem Konstruktor oder
 *  {@link #initialize} &uuml;ber das Feld {@link #worker} gesetzt.<br>
 *  <br>
 *  Server laufen immer im Context von Seanox Devwex und verwenden den gleichen
 *  ClassLoader.<br>
 *  <br>
 *  Sequenz vom Server-Aufruf:
 *  <ul>
 *    <li>
 *      {@link #Server(String, Object)} - der Server wird durch Seanox Devwex
 *      geladen und &uuml;ber den Konstruktor der Seanox Commons Server-API
 *      initiiert, womit die Basis-Konfiguration von {@link #address} - Adresse
 *      des Servers, {@link #caption} - Serverkennung und Bezeichnung,
 *      <code>dimension</code> - G&ouml;sse des Puufers f&uuml;r DatagramPacket,
 *      <code>maxaccess</code> - maximale Anzahl gleichzeitiger Verbindungen,
 *      {@link #port} - Port des Servers und {@link #timeout} - maximaler
 *      Datenleerlauf in Millisekunden, erfolgt
 *    </li>
 *    <li>
 *      {@link #initialize(String, Initialize)} - optional kann hier eine
 *      erweiterte Einrichtung der Server-Instanz erfolgen, da die Konfiguration
 *      bereits geladen wurde und als {@link Initialize} verf&uuml;gbar ist, die
 *      Methode wird vor der Initialisierung vom DatagramSocket durch den
 *      Konstruktor aufgerufen
 *    </li>
 *    <li>
 *      {@link #bind()} - die Einrichtung vom {@link DatagramSocket} wird durch
 *      den Konstruktor aufgerufen, die Methode wurde herausgef&uuml;hrt, um die
 *      Initialisierung vom {@link DatagramSocket} ohne das &Uuml;berschreibe
 *      vom Konstruktor zu erm&ouml;glichen
 *    </li>
 *    <li>
 *      {@link #caption} - das Feld wird auf Basis vom Server-Namen in der
 *      Konfiguration und dem verwendeten Protokoll gesetzt
 *    </li>
 *    <li>
 *      {@link #run()} - der Server wird als Thread durch Seanox Devwex
 *      gestartet, mit dem Start wird das Connection-Handling und das
 *      Pool-Management f&uuml;r die Connections ({@link Session}) etabliert,
 *      dabei wird ein Pool mit Session-Objekte eingerichtet, welche als Thread
 *      gestartet werden und auf eingehende Anfragen warten, innherhalb einer
 *      maximalen Datenleerlaufzeit werden diese dann wiederverwendet oder zur
 *      Entlastung der Laufzeitumgebung beendet
 *    </li>
 *    <li>
 *      {@link #destroy()} - die Methode wird durch die Seanox Devwex Server-API
 *      zum Beenden der Server aufgerufen
 *    </li>
 *  </ul>
 *  <br>
 *  Die Konfiguration der Server erfolgt in <code>devwex.ini</code>. Seanox
 *  Devwex ermittelt und l&auml;dt alle Server anhand einer speziellen
 *  Konvention im Sektionsnamen. So m&uuml;ssen diese mit <code>SERVER:</code>
 *  beginnen und mit <code>:BAS</code> enden.<br>
 *  <br>
 *  Durch Seanox Commons ergibt sich folgende Grundkonfiguration:
 *  <pre>
 *  [SERVER:ECHO:BAS]
 *    ADDRESS   = 127.0.0.1                 ;local address of server [AUTO|LOCALHOST|IP|NAME]
 *    PORT      = ...                       ;local port of server
 *    MAXACCESS = 100                       ;maximum number of simultaneous connections (100)
 *    DIMENSION = 100                       ;size of datagram packet puffer
 *    ISOLATION = 250                       ;maximum idle time of connection in milliseconds
 *    TIMEOUT   = 30000 [S]                 ;maximum idle time of data stream in milliseconds
 *    ACCESSLOG = ../access-[yyyy.MMdd].log ;file to register of access (empty StdIo)
 *  </pre>
 *  <b>Hinweis</b> - Die Implementierung der abstrakten Klasse kann, muss aber
 *  nicht erfolgen, da der Service von Seanox Devwex alle Server via Reflections
 *  initiiert und kontrolliert. Beim Start auftretende Fehler k&ouml;nnen
 *  behandelt aber auch an den initiieren Service weitergereicht werden. Der
 *  Service wird in dem Fall den Aufruf der Methode {@link #destroy()}
 *  versuchen.<br>
 *  <br>
 *  Server 1.2012.0706<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2012.0706
 */
public abstract class Server implements Runnable {

    /** Server Konfigurationsobjekt */
    protected final Initialize initialize;

    /** Server Konfiguration */
    protected final Section options;

    /** Klasse zur Request-Verarbeitung */
    protected volatile Class worker;

    /** Session Objekte der eingerichteten Verbindungen */
    protected volatile List sessions;

    /** Socket des Servers */
    protected volatile DatagramSocket socket;

    /** Adresse des Servers */
    protected volatile String address;

    /** Serverkennung und Bezeichnung */
    protected volatile String caption;

    /** Status zum Beenden des Servers */
    protected volatile boolean control;

    /** Server Port */
    protected volatile int port;

    /** Timeout bei Datenleerlauf in Millisekunden */
    protected volatile int timeout;

    /** Konstante f&uuml;r den Konfigurationsparameter ACCESSLOG */
    protected static final String ACCESSLOG = "accesslog";

    /** Konstante f&uuml;r den Konfigurationsparameter ADDRESS */
    protected static final String ADDRESS = "address";

    /** Konstante f&uuml;r den Konfigurationsparameter DIMENSION */
    protected static final String DIMENSION = "dimension";

    /** Konstante f&uuml;r den Konfigurationsparameter ISOLATION */
    protected static final String ISOLATION = "isolation";

    /** Konstante f&uuml;r den Konfigurationsparameter MAXACCESS */
    protected static final String MAXACCESS = "maxaccess";

    /** Konstante f&uuml;r den Konfigurationsparameter PORT */
    protected static final String PORT = "port";

    /**
     *  Konstruktor, richtet den Server entsprechenden der Konfiguration ein.
     *  @param  name Name des Servers
     *  @param  data Konfigurationsdaten des Servers
     *  @throws Exception bei fehlerhafter Einrichtung des Servers
     */
    public Server(String name, Object data) throws Exception {

        int isolation;

        //der Kontext wird gegebenenfalls korrigiert und optimiert
        name = (name == null) ? "" : name.toLowerCase().trim();

        if (name.length() == 0) throw new IllegalArgumentException("Invalid server name [empty]");

        this.initialize = Server.convert(data);

        //die Basiskonfiguration wirde ermittelt
        this.options = Section.parse(this.initialize.get(name.concat(":bas")));

        //der Port des Servers wird ermittelt
        try {this.port = Integer.parseInt(this.options.get(Server.PORT));
        } catch (Exception exception) {this.port = 0;}

        //bei Abweichungen wird der Standard verwendet
        if (this.port <= 0) this.port = 0;

        //die Hostadresse des Servers wird ermittelt
        this.address = this.options.get(Server.ADDRESS);

        if (this.address.length() == 0) this.address = "auto";

        this.initialize(name, this.initialize);

        this.socket = this.bind();

        //die maximale Leerlaufzeit fuer den Verbindungsaufbau
        try {isolation = Integer.parseInt(this.options.get(Server.ISOLATION));
        } catch (Throwable throwable) {isolation = 0;}

        //das Timeout fuer den Socket wird gesetzt
        this.socket.setSoTimeout(isolation <= 0 ? 250 : isolation);

        //die Serverkennung wird zusammengestellt
        this.caption = ("UDP ").concat(this.socket.getLocalAddress().getHostAddress()).concat(":").concat(String.valueOf(this.port));
    }

    /**
     *  Optionale Einrichtung vom Server entsprechenden der Konfiguration. Diese
     *  Methode wird ausschliesslich durch Seanox-Commons aufgerufen und ist
     *  nicht Bestandteil der elementaren Devwex Server-API.
     *  @param  name       Name des Servers
     *  @param  initialize Konfiguration des Servers
     *  @throws Exception bei fehlerhafter Einrichtung des Servers
     */
    protected void initialize(String name, Initialize initialize) throws Exception {

        return;
    }

    /**
     *  Methode zur Anbindung und Einrichtung vom DatagramSocket.
     *  @return der eingrichtete DatagramSocket
     *  @throws Exception bei fehlerhafter Einrichtung vom DatagramSocket
     */
    protected DatagramSocket bind() throws Exception {

        DatagramSocket socket;

        //der DatagramSocket wird eingerichtet
        socket = this.address.equalsIgnoreCase("auto") ? new DatagramSocket(this.port) : new DatagramSocket(this.port, InetAddress.getByName(this.address));

        return socket;
    }

    /**
     *  Konvertiert das auf (Devwex)Initialize basierende Objekt.
     *  @param  data Konfigurationsdaten
     *  @return die konvertiert Instanz von Initialize
     *  @throws IllegalAccessException wenn die Konvertierung nicht m&ouml;glich
     *          ist
     */
    private static Initialize convert(Object data) throws IllegalAccessException {

        Initialize initialize;

        initialize = new Initialize();

        Accession.storeField(data, initialize, "entries");
        Accession.storeField(data, initialize, "list");
        Accession.storeField(data, initialize, "resolve");

        return (Initialize)initialize.clone();
    }

    /**
     *  R&uuml;ckgabe der Kennung vom Server.
     *  @return die Kennung im Format <code>[PROTOKOLL HOST-NAME:PORT]</code>
     *          oder <code>[PROTOKOLL HOST-ADRESSE:PORT]</code>
     */
    public String getCaption() {

        return this.caption;
    }

    /** Beendet den Server als Thread */
    public void destroy() {

        //der Status zum Beenden wird gesetzt
        this.control = true;

        //der Socket wird geschlossen
        try {this.socket.close();
        } catch (Throwable throwable) {

            //keine Fehlerbehandlung erforderlich
        }
    }

    /** Stellt den Einsprung in den Thread zur Verf&uuml;gung. */
    public void run() {

        Session  session;
        Thread   thread;
        String   caption;

        Object[] objects;

        boolean  control;

        int      count;
        int      loop;
        int      volume;

        //die Sessions werden eingerichtet
        this.sessions = new Vector(256, 256);

        caption = this.getCaption();
        caption = caption == null ? "" : caption.trim();

        if (caption.length() == 0) caption = ("<").concat(getClass().getName()).concat(">");

        //Initialisierung wird als Information ausgegeben
        Context.print(("SERVER ").concat(caption).concat(" READY"));

        //MAXACCESS - die Anzahl max. gleichzeitiger Verbindungen wird ermittelt
        try {volume = Integer.parseInt(this.options.get(Server.MAXACCESS));
        } catch (Throwable throwable) {volume = 0;}

        //die initiale Anzahl zusaetzlicher Sessions wird angelegt
        count = 0;

        //die Session wird initial angelegt
        session = null;

        try {

            if (this.worker == null) throw new ClassNotFoundException("Missing implementation of com.seanox.module.udp.Session");

            while (!this.control) {

                for (control = false, loop = this.sessions.size() -1; !this.control && loop >= 0; loop--) {

                    objects = (Object[])this.sessions.get(loop);

                    //der Thread wird ermittelt
                    thread = (Thread)objects[1];

                    //ausgelaufene Sessions werden entfernt
                    if (control && !thread.isAlive()) this.sessions.remove(loop);

                    //die Session wird ermittelt
                    session = (Session)objects[0];

                    //ueberzaehlige Sessions werden beendet
                    try {if (control && session.available()) session.isolate();
                    } catch (Throwable throwable) {

                        //keine Fehlerbehandlung erforderlich
                    }

                    //laeuft der Thread nicht wird die Session entfernt
                    try {if (session.available() && thread.isAlive()) control = true;
                    } catch (Throwable throwable) {

                        //keine Fehlerbehandlung erforderlich
                    }
                }

                //die Anzahl der nachtraeglich einzurichtenden Sessions auf
                //Basis der letzten Anzahl ermittelt
                count = control ? 0 : volume <= 0 ? 1 : count +count +1;

                //liegt keine freie Session vor, werden neue eingerichtet, die
                //Anzahl ist durch die Angabe vom MAXACCESS begrenzt, weitere
                //Anfragen werden sonst im Backlog geparkt
                if (!control && (this.sessions.size() < volume || volume <= 0)) {

                    for (loop = count; !this.control && loop > 0 && (this.sessions.size() < volume || volume <= 0); loop--) {

                        //die Session wird eingerichtet
                        session = (Session)Accession.newInstance(this.worker, new Object[] {this.socket, this});

                        //der Thread der Session wird eingerichet, ueber den
                        //Service wird dieser automatisch als Daemon verwendet
                        thread = new Thread(session);

                        //die Session wird als Thread gestartet
                        thread.start();

                        //die Session wird mit Thread registriert
                        this.sessions.add(new Object[] {session, thread});
                    }
                }

                Thread.sleep(25);
            }

        } catch (Throwable throwable) {

            Context.print(throwable);

            try {if (session != null) session.destroy();
            } catch (Throwable ignore) {

                //keine Fehlerbehandlung erforderlich
            }
        }

        //das Beenden vom Server wird eingeleitet
        this.destroy();

        //alle Sessions werden zwangsweise beendet
        for (loop = this.sessions.size() -1; loop >= 0; loop--) {

            //dier Session wird ermittelt und beendet
            try {((Session)((Object[])this.sessions.get(loop))[0]).destroy();
            } catch (Throwable throwable) {

                //keine Fehlerbehandlung erforderlich
            }
        }

        //die Terminierung wird ausgegeben
        Context.print(("SERVER ").concat(caption).concat(" STOPPED"));
    }
}