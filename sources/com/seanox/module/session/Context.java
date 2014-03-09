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
package com.seanox.module.session;

import java.util.Enumeration;
import java.util.Hashtable;

import com.seanox.common.Accession;

/**
 *  Context stellt eine persistente Verwaltung von Sessions zur Haltung von
 *  sitzungsbezogenen Daten zur Verf&uuml;gung. Der Thread vom Context wird als
 *  Daemon und reagiert auf die aktuellen Ereignisse der Modulumgebung.<br>
 *  <br>
 *  Context 1.2010.1130<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2010.1130
 */
public class Context extends com.seanox.module.Context implements Runnable {

    /** Liste der vorgehaltenen Session mit Signatur */
    private volatile Hashtable sessions;

    /** Status des Beendens vom Thread */
    private volatile boolean terminate;

    /**
     *  Konstruktor, richtet den Service entsprechend der Konfiguration als
     *  Objekt oder mit Synchronisation als Thread ein.
     *  @param options Service Konfiguration
     */
    public synchronized void initialize(String options) {

        Thread thread;

        //das Verzeichnis der Sessions wird eingerichtet
        this.sessions = new Hashtable();

        //der Thread wird eingerichtet
        thread = new Thread(this);

        //der Thread wird als Daemon eingerichtet
        thread.setDaemon(true);

        //der Thread wird gestartet
        thread.start();
    }

    /**
     *  R&uuml;ckgabe der Modul und Versionsinformation im Format
     *  <code>[PRODUCER-MODULE/VERSION]</code>.
     *  @return die Modul und Versionsinformation als String
     */
    public String getCaption() {

        return "Seanox-Session-Context/1.2007.1205";
    }

    /**
     *  Erstellt eine neue Session mit Dauer des Verfalls bei Nichtbenutzung.
     *  @param  expiration Dauer in Millisekunden
     *  @return die neu erstellte Session
     */
    public Session createSession(long expiration) {

        Session session;
        String  string;

        session = new Session(expiration);

        try {Accession.set(session, "state", new Integer(0));
        } catch (Exception exception) {

            string = String.valueOf(exception);

            throw new RuntimeException(("Context access on session failed (").concat(string).concat(")"));
        }

        this.sessions.put(session.getSignature(), session);

        return session;
    }

    /**
     *  Ermittelt die zur angegebenen Signatur geh&ouml;rende Session. Kann
     *  keine g&uuml;ltige Session ermittelt werden, wird <code>null</code>
     *  zur&uuml;ck gegeben.
     *  @param  signature Session Signatur
     *  @return die ermittelte Session, sonst <code>null</code>
     */
    public Session getSession(String signature) {

        Session session;

        if (signature == null) return null;

        session = (Session)this.sessions.get(signature);

        if (session == null) return null;

        session.activate();

        if (!session.isValid()) this.sessions.remove(signature);

        return session.isValid() ? session : null;
    }

    /** Beendet den Service. */
    public void destroy() {

        //der Status zum Beenden wird gesetzt
        this.terminate = true;
    }

    /** Stellt den Einsprung in den Thread zur Verf&uuml;gung. */
    public void run() {

        Enumeration enumeration;
        Session     session;

        boolean     expired;
        int         state;
        long        accession;
        long        expiration;
        long        timing;

        while (!this.terminate) {

            //alle Sessions werden ermittelt
            enumeration = this.sessions.elements();

            while (enumeration.hasMoreElements()) {

                try {Thread.sleep(25);
                } catch (Exception exception) {

                    //keine Fehlerbehandlung vorgesehen
                }

                try {

                    //die Session wird ermittelt
                    session = (Session)enumeration.nextElement();

                    accession  = ((Long)Accession.get(session, "accession")).longValue();
                    expiration = ((Long)Accession.get(session, "expiration")).longValue();

                    //der Verfallszeitpunkt wird ermittelt
                    timing = accession +expiration;

                    //der Verfallszeitpunkt wird geprueft
                    expired = (timing < System.currentTimeMillis());

                    state = ((Integer)Accession.get(session, "state")).intValue();

                    //die Gueltigkeit der Session wird geprueft
                    if (!(expired || state > 1)) continue;

                    //die ungueltige Session wird entfernt
                    this.sessions.remove(session.getSignature());

                } catch (Exception exception) {

                    //keine Fehlerbehandlung vorgesehen
                }
            }

            try {Thread.sleep(1000);
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }
    }
}