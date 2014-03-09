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
package com.seanox.module.http;

import java.net.Socket;

import com.seanox.common.Accession;

/**
 *  Extension stellt eine abstrakte Klasse zur allgemeinen Implementierung von
 *  Einsprungsobjekten f&uuml;r Module im HTTP-Context mit den notwendigen
 *  Konstanten und Methoden zur Verf&uuml;gung.<br>
 *  <br>
 *  <b>Hinweis</b> - Je nach Typ und Verwendung des Moduls werden
 *  unterschiedliche Anforderungen beim Umgang mit den Ressourcen des Server
 *  erwartet. Somit sollten immer die bereits im Paket
 *  <code>com.seanox.module.http</code> implementierten Konstrukte verwendet
 *  werden.<br>
 *  <br>
 *  Extension 1.2011.0804<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0429
 */
abstract class Extension {

    /** der aktuelle Serverstatus (Reflection) */
    private int status;

    /** Zeitpunkt der letzten Unterbrechung in Millisekunden */
    private long timing;

    /** Bezugsobjekt */
    final Object accession;
    
    /** Server Socket */
    final Socket socket;
    
    /** Option bei sicher Socket-Verbindung */
    final boolean secure;
    
    /** Context der Eingerichtet*/
    public final Context context;

    /** Blockgr&ouml;sse f&uuml;r Datenzugriffe */
    public final int blocksize;

    /** Interrupt f&uuml;r Systemprozesse im Millisekunden */
    public final long interrupt;

    /** Timeout bei Datenleerlauf in Millisekunden */
    public final long timeout;

    /**
     *  Konstruktor, richtet Filter auf Basis vom &uuml;bergebenen Context ein.
     *  @param  context Context
     *  @param  object  Bezugsobjekt
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Extension(Context context, Object object) {

        Object result;

        if (context == null) throw new IllegalArgumentException("Invalid context [null]");

        //der Start der Laufzeit wird gesetzt
        this.timing = System.currentTimeMillis();

        //der Context wird registriert
        this.context = context;

        //das Bezugsobjekt wird eingerichtet
        this.accession = object;

        try {

            this.blocksize = ((result = Accession.get(this.accession, "blocksize")) != null) ? ((Integer)result).intValue() : 0;
            this.interrupt = ((result = Accession.get(this.accession, "interrupt")) != null) ? ((Long)result).longValue() : 0;
            this.timeout   = ((result = Accession.get(this.accession, "timeout")) != null) ? ((Long)result).longValue() : 0;
            
            this.socket = (Socket)Accession.get(this.accession, "socket");
            this.secure = ((Boolean)Accession.get(this.accession, "secure")).booleanValue();

        } catch (Exception exception) {

            throw new RuntimeException("Module bind failed", exception);
        }
    }

    /**
     *  Ermittelt per Java Reflections das angegebene Feld, wobei auch
     *  Objektstrukturen durch den Punkt getrennt unterst&uuml;tzt werden.
     *  R&uuml;ckgabe der Inhalt vom angegebenen Feld als Referenz bei Objekten
     *  und als Wrapper bei einfachen Datentypen.
     *  @param  object Zugriffobjekt
     *  @param  field  Name des Felds
     *  @return der Inhalt des angeforderten Felds
     *  @throws IllegalArgumentException bei ung&uml;tigem Argumenten
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    static Object mountField(Object object, String field) {

        return Context.mountField(object, field, true);
    }

    /**
     *  Ermittelt per Java Reflections das angegebene Feld, wobei auch
     *  Objektstrukturen durch den Punkt getrennt unterst&uuml;tzt werden.
     *  R&uuml;ckgabe der Inhalt vom angegebenen Feld als Referenz bei Objekten
     *  und als Wrapper bei einfachen Datentypen. Mit der Option
     *  <code>clone</code> kann bei Objekten eine Kopie angefordert werden. Dazu
     *  muss das Objekt aber die <code>Clonable</code> implementieren. Bei
     *  einfachen Datentypen wird die Option <code>clone</code> ignoriert, da
     *  der Wert &uuml;ber den Wrapper immer als Kopie zur&uuml;ckgegeben wird.
     *  @param  object Zugriffobjekt
     *  @param  field  Name des Felds
     *  @param  clone  Option <code>true</code> zur R&uuml;ckgabe als Kopie
     *  @return der Inhalt des angeforderten Felds
     *  @throws IllegalArgumentException bei ung&uml;tigem Argumenten
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    static Object mountField(Object object, String field, boolean clone) {

        return Context.mountField(object, field, clone);
    }

    /**
     *  Unterbricht die aktuelle Verarbeitung f&uuml;r die per interrupt
     *  gesetzten Millisekunden. Die Unterbrechung erfolgt in Abh&auml;ngigkeit
     *  der maximalen Dauer eines Zyklus der Zeitscheibe des Betriebssystem von
     *  ca. 25ms. Somit wirk diese erst, wenn f&uuml;r die aktuelle Verarbeitung die
     *  vom Betriebssystem vorgesehene Verarbeitung erreicht wurde.
     */
    public void sleep() {

        this.sleep(this.interrupt);
    }

    /**
     *  Unterbricht die aktuelle Verarbeitung f&uuml;r die angegebenen
     *  Millisekunden. Die Unterbrechung erfolgt in Abh&auml;ngigkeit der
     *  maximalen Dauer eines Zyklus der Zeitscheibe des Betriebssystem von ca.
     *  25ms. Somit wirk diese erst, wenn f&uuml;r die aktuelle Verarbeitung die
     *  vom Betriebssystem vorgesehene Verarbeitung erreicht wurde.
     *  @param idle Unterbrechung in Millisekunden
     */
    public void sleep(long idle) {

        if (idle > 0 && (System.currentTimeMillis() -this.timing) > 20) {

            try {Thread.sleep(idle);
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            this.timing = System.currentTimeMillis();
        }
    }
}