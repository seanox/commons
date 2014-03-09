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
package com.seanox.module.tcp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.seanox.common.Components;
import com.seanox.module.Context;

/**
 *  Session nimmt als Thread Requests vom TCP-ServerSocket an und &uuml;bernimmt
 *  die Verarbeitung. Das Objekt ist um Wiederverwendung seiner Instanzen
 *  bem&uuml;ht und recyceln sich selbst, solange Requests eingehen. Ger&auml;t
 *  die Session bei nachlassenden Requests in einen Leerlauf, baut sich diese
 *  selbst ab. Das Beenden der Session erfolgt mehrstufig. Zuerst wird diese
 *  isoliert, was der Aufforderung zum Beenden entspricht. So wird die aktuelle
 *  Request-Verarbeitung bis zum Ende ausgef&uuml;hrt, in der zweiten Stufe
 *  enden und in der dritten Stufe aus dem Pool des Servers entfernt.<br>
 *  <br>
 *  Session 1.2012.0706<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2012.0706
 */
public abstract class Session implements Runnable {

    /** Dateneingangsstrom */
    protected volatile InputStream input;

    /** Datenausgangsstrom */
    protected volatile OutputStream output;

    /** physischer Host der Session */
    protected volatile Server server;

    /** Socket des Servers */
    protected volatile ServerSocket mount;

    /** Socket der Session */
    protected volatile Socket socket;

    /** Timeout beim ausgehenden Datentransfer in Millisekunden */
    protected volatile int isolation;

    /** Timeout bei Datenleerlauf in Millisekunden */
    protected volatile int timeout;

    /**
     *  Konstruktor, richtet die Session mit Socket und Server ein.
     *  @param  socket Socket mit dem eingegangen Request
     *  @param  server physischer Host
     *  @throws Exception bei fehlerhafter Einrichtung der Session
     */
    public Session(ServerSocket socket, Server server) throws Exception {

        this.mount  = socket;
        this.server = server;
    }

    /**
     *  Entfernt aus dem String die Optionsinformationen im Format
     *  <code>&#91;...&#93;</code>. R&uuml;ckgabe der String ohne endende
     *  Optionen.
     *  @param  string zu bereinigender String
     *  @return der String ohne endende Optionen
     */
    protected static String cleanOptions(String string) {

        int cursor;

        string = string.trim();

        while (string.endsWith("]") && (cursor = string.lastIndexOf("[")) >= 0) {

            string = string.substring(0, cursor).trim();
        }

        return string;
    }

    /**
     *  Formatiert das Datum im angebenden Format und in der angegebenen Zone.
     *  R&uuml;ckgabe das formatierte Datum, im Fehlerfall ein leerer String.
     *  @param  format Formatbeschreibung
     *  @param  date   zu formatierendes Datum
     *  @param  zone   Zeitzone, <code>null</code> Standardzone
     *  @return das formatierte Datum als String, im Fehlerfall leerer String
     */
    protected static String formatDate(String format, Date date, String zone) {

        SimpleDateFormat pattern;

        //die Formatierung wird eingerichtet
        pattern = new SimpleDateFormat(format, Locale.US);

        //die Zeitzone wird gegebenenfalls fuer die Formatierung gesetzt
        if (zone != null) pattern.setTimeZone(TimeZone.getTimeZone(zone));

        //die Zeitangabe wird formatiert
        return pattern.format(date);
    }

    /**
     *  Initialisiert die Session und bereitet diese f&uuml;r die Annahme und
     *  Verarbeitung von eingehenden Requests vor. Die Methode wird bereits im
     *  Vorfeld aufgerufen, da eine Sessions wiederverwendet wird und nach der
     *  Verarbeitung eines Request auf weitere eingehende Requests wartet,
     *  bevor diese entg&uuml;ltig verworfen wird.
     *  @throws Exception bei fehlerhafter Initialisierung der Session
     */
    protected abstract void initialize() throws Exception;

    /**
     *  Einsprung zur Verarbeitung von eingehenden Requests.
     *  @throws Exception bei fehlerhafter Verarbeitung vom Request
     */
    protected abstract void service() throws Exception;

    /**
     *  Merkt die Session zum Schliessen vor, wenn diese in der naechsten Zeit
     *  nicht mehr verwendet wird. Der Zeitpunkt zum Bereinigen betr&auml;gt
     *  250ms Leerlaufzeit nach der letzen Nutzung. Die Zeit wird &uuml;ber das
     *  SoTimeout vom ServerSocket definiert.
     */
    public void isolate() {

        if (this.socket == null && this.mount != null) this.mount = null;
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn die Session als aktives Objekt zur
     *  Verf&uuml;gung steht und somit weiterhin Requests entgegen nehmen kann.
     *  @return <code>true</code>, wenn die Session aktiv verf&uuml;bar ist
     */
    public boolean available() {

        //der Socket wird auf Blockaden geprueft und ggf. geschlossen
        try {if (this.mount != null && this.isolation > 0 && this.isolation < System.currentTimeMillis() -this.timeout) this.destroy();
        } catch (Throwable throwable) {

            return false;
        }

        return this.mount != null && this.socket == null;
    }

    /**
     *  Einsprung zur Protokollierung der Request-Verarbeitung. Der Aufruf der
     *  Methode erfolgt erst nach dem Schliesse der Datenstr&ouml;me, um die
     *  die Beantwortung nicht durch synchronisierte Prozeduren zu blokieren.
     *  @throws Exception bei fehlerhafter Protokollierung vom Request
     */
    protected abstract void register() throws Exception;

    /**
     *  Einsprung zur Protokollierung der Request-Verarbeitung. Der Aufruf der
     *  Methode erfolgt erst nach dem Schliesse der Datenstr&ouml;me, um die
     *  die Beantwortung nicht durch synchronisierte Prozeduren zu blokieren.
     *  @param  message zu protokollierende Nachricht
     *  @throws Exception bei fehlerhafter Protokollierung vom Request
     */
    protected void register(String message) throws Exception {

        Date         date;
        OutputStream output;
        String       access;
        String       buffer;
        String       path;
        String       stream;
        String       string;

        int          loop;

        //der Datenstrom wird initial eingerichtet
        output = null;

        message = message == null ? "" : message.trim();

        if (message.length() == 0) return;

        //der Pfad der Protokolldatei wird ermittelt
        path = this.server.options.get(Server.ACCESSLOG);

        //die Fragmente vom ACCESSLOG werden ermittelt
        if (path.toLowerCase().equals("off")) return;

        //der Pfad wird fuer das Aufloesen der Zeitsymbole vorbereitet
        path = path == null ? "" : path.trim().concat("[]");

        for (loop = 0, buffer = "", stream = ""; loop < path.length(); loop++) {

            string = path.substring(loop, loop+1);
            buffer = buffer.concat(string);

            if (string.equals("[") || string.equals("]")) {

                if (buffer.startsWith("]")) buffer = buffer.substring(1);
                if (buffer.endsWith("[")) buffer = buffer.substring(0, buffer.length() -1);
                if (!buffer.startsWith("[") && buffer.endsWith("]")) buffer = ("[").concat(buffer);
                if (buffer.startsWith("[") && !buffer.endsWith("]")) buffer = buffer.concat("]");

                if (buffer.length() > 0) stream = stream.concat(buffer);

                buffer = string.equals("[") ? string : "";
            }
        }

        //die Syntax der Zeitsymbole im Dateinamen werden so optimiert, dass
        //diese per SimpleDateFormat formatiert werden koennen
        stream = stream.startsWith("[") ? stream.substring(1) : ("'").concat(stream);
        stream = stream.endsWith("]") ? stream.substring(0, stream.length() -1) : stream.concat("'");

        stream = Components.strset(stream, "[]", "");
        stream = Components.strset(stream, "][", "");
        stream = Components.strset(stream, "[", "'");
        stream = Components.strset(stream, "]", "'");

        synchronized (getClass()) {

            date = new Date();

            //die Adresse vom Remote-Host wird ermittelt bzw. aufgeloest
            access = this.socket.getInetAddress().getHostName();

            try {access = InetAddress.getByName(access).getHostName();
            } catch (Throwable throwable) {

                //keine Fehlerbehandlung erforderlich
            }

            //der Timestamp wird ermittelt, vor Java 1.4.x wird die Zeitzone
            //nicht als Zeitsymbol und muss als separat berechnet werden
            access = Session.formatDate("[dd/MMM/yyyy:HH:mm:ss Z]", date, null).concat(" ").concat(access);

            //die Eingabe wird protokolliert
            access = access.concat(" ").concat(message).trim();

            //der Zeilenumbruch wird ermittelt und der Inhalt erweitert
            access = access.concat(System.getProperty("line.separator", "\r\n"));

            stream = new SimpleDateFormat(stream, Locale.US).format(date).trim();

            //wurde keine Protokolldatei eingetragen wird in den STDIO
            //sonst in die entsprechende Datei geschrieben
            if (stream.length() > 0) {

                output = new FileOutputStream(stream, true);

                output.write(access.getBytes());

            } else Context.print(("ACCESS ").concat(access));

            //der Datenstrom wird geschlossen
            try {output.close();
            } catch (Throwable throwable) {

                //keine Fehlerbehandlung vorgesehen
            }
        }
    }

    /**
     *  Beendet die Session durch das Schliessen der Datenstr&ouml;me.
     *  Die Session wird wenn m&oouml;lich danach wiederverwendet.
     */
    public void destroy() {

        //der ServerSocket wird zurueckgesetzt
        this.mount = null;

        if (this.isolation != 0) this.isolation = -1;

        //der Datenausgabestrom wird geschlossen
        try {this.output.close();
        } catch (Throwable throwable) {

            //keine Fehlerbehandlung erforderlich
        }

        //der Dateneingabestrom wird geschlossen
        try {this.input.close();
        } catch (Throwable throwable) {

            //keine Fehlerbehandlung erforderlich
        }

        //der Socket wird geschlossen
        try {this.socket.close();
        } catch (Throwable throwable) {

            //keine Fehlerbehandlung erforderlich
        }
    }

    /**
     *  Stellt den Einsprung in den Thread zur Verf&uuml;gung und initialisiert
     *  die Session. Um den Serverprozess nicht zu behindern wird die
     *  eigentliche Initialisierung der Session erst mit dem laufenden Thread
     *  als asynchroner Prozess vorgenommen.
     */
    public void run() {

        ServerSocket socket;
        String       string;

        //der ServerSocket wird vorgehalten
        socket = this.mount;

        while (this.mount != null) {

            try {this.initialize();
            } catch (Throwable throwable) {

                Context.print(throwable);
            }

            string = this.server.options.get(Server.TIMEOUT);

            this.isolation = string.toUpperCase().indexOf("[S]") >= 0 ? -1 : 0;

            //das Sessiontimeout wird ermittelt
            try {this.timeout = Integer.parseInt(Session.cleanOptions(string));
            } catch (Throwable throwable) {this.timeout = 0;}

            //die maximale Prozesslaufzeit wird gegebenfalls korrigiert
            if (this.timeout < 0) this.timeout = 0;

            try {this.socket = socket.accept();
            } catch (InterruptedIOException exception) {

                continue;

            } catch (IOException exception) {

                break;
            }

            try {

                //das SO-Timeout wird fuer den Serversocket gesetzt
                this.socket.setSoTimeout(this.timeout);

                //die Datenstroeme werden eingerichtet
                this.output = this.socket.getOutputStream();
                this.input  = this.socket.getInputStream();

                //der Request wird verarbeitet
                this.service();

            } catch (Throwable throwable) {

                Context.print(throwable);
            }

            //die Session wird beendet
            try {this.destroy();
            } catch (Throwable throwable) {

                Context.print(throwable);
            }

            //HINWEIS - Beim Schliessen wird der ServerSocket verworfen, um
            //die Session von Aussen beenden zu koennen, intern wird diese
            //daher nach dem Beenden neu gesetzt

            //der Zugriff wird registriert
            try {this.register();
            } catch (Throwable throwable) {

                Context.print(throwable);
            }

            //der Socket wird alternativ geschlossen
            try {this.socket.close();
            } catch (Throwable throwable) {

                //keine Fehlerbehandlung erforderlich
            }

            //durch das Zuruecksetzen wird die Session ggf. reaktivert
            this.mount = socket;

            //der Socket wird verworfen
            this.socket = null;
        }
    }
}