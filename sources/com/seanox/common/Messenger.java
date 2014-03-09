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
package com.seanox.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 *  Messenger stellt Methoden zum protokollieren von Informationen in
 *  Datenstr&ouml;me und Dateien zur Verf&uuml;gung.<br>
 *  <br>
 *  Messenger 1.2008.1101<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2008.1101
 */
public class Messenger {

    /** Datei zur Datenausgabe */
    private File file;

    /** Datenstrom zur Datenausgabe */
    private OutputStream output;

    /** Kontext f&uuml;r die Protokollierung */
    private String context;

    /**
     *  Konstruktor, richtet den Messenger mit einer Datei zur Ausgabe ein.
     *  @param file Datei zur Datenausgabe
     */
    public Messenger(File file) {

        this.file = file;
    }

    /**
     *  Konstruktor, richtet den Messenger mit einer Datei zur Ausgabe ein.
     *  @param context Context der Protokollierung
     *  @param file Datei zur Datenausgabe
     */
    public Messenger(String context, File file) {

        this.context = context;
        this.file    = file;
    }

    /**
     *  Konstruktor, richtet den Messenger mit einem Datenstrom zur Ausgabe ein.
     *  @param output Datenstrom zur Datenausgabe
     */
    public Messenger(OutputStream output) {

        this.output = output;
    }

    /**
     *  Konstruktor, richtet den Messenger mit einem Datenstrom zur Ausgabe ein.
     *  @param context Context der Protokollierung
     *  @param output Datenstrom zur Datenausgabe
     */
    public Messenger(String context, OutputStream output) {

        this.context = context;
        this.output  = output;
    }

    /**
     *  Schreibt die Informationen je nach Konfiguration in die Protokolldatei,
     *  in Datenausgabestrom und/oder in Protokollpuffer.
     *  @param  bytes Informationsdaten als ByteArray
     *  @throws IOException bei fehlerhaftem Datenzugriff
     */
    private void write(byte[] bytes) throws IOException {

        RandomAccessFile output;

        synchronized (Messenger.class) {

            //initiale Einrichtung des Datenstroms
            output = null;

            try {

                if (this.file != null) {

                    //der Datenstrom wird eingerichtet
                    output = new RandomAccessFile(this.file, "rw");

                    //der Datenzeiger wird positioniert
                    output.seek(output.length());

                    //die Daten werden ausgegeben
                    output.write(bytes);

                } else if (this.output != null) {

                    this.output.write(bytes);
                }

            } finally {

                //der Datenstrom wird geschlossen
                try {if (this.file != null) output.close();
                } catch (Exception exception) {

                    //keine Fehlerbehandlung vorgesehen
                }
            }
        }
    }

    /**
     *  Erweitert die Protokollierung um den angebenen Eintrag zeilenweise. Zur
     *  Ermittlung des Protokolltexts wird <code>Object.toString()</code> vom
     *  &uuml;bergebenen Objekt verwendet. Bei der &Uuml;bergabe von
     *  Fehlerobjekten wird der StackTrace zeilenweise protoklliert.
     *  @param object Objekt mit dem Protokolleintrag
     */
    public void print(Object object) {

        this.print(object, true);
    }

    /**
     *  Erweitert die Protokollierung um den angebenen Eintrag zeilenweise. Zur
     *  Ermittlung des Protokolltexts wird <code>Object.toString()</code> vom
     *  &uuml;bergebenen Objekt verwendet. Bei der &Uuml;bergabe von
     *  Fehlerobjekten wird der StackTrace zeilenweise protoklliert.
     *  @param object Objekt mit dem Protokolleintrag
     *  @param trim   Option <code>true</code> entfernt die Whitespace-Zeichen
     *                am Anfang und am Ende einer Protokollzeile
     */
    public void print(Object object, boolean trim) {

        ByteArrayOutputStream buffer;
        StringTokenizer       tokenizer;
        String                stream;
        String                string;
        String                timing;
        Throwable             throwable;

        boolean               control;
        boolean               smooth;

        //der Datenpuffer wird eingerichtet
        buffer = new ByteArrayOutputStream();

        if (object == null) return;

        while (object instanceof InvocationTargetException && (throwable = ((InvocationTargetException)object).getTargetException()) != null) object = throwable;

        if (object instanceof Throwable) {

            ((Throwable)object).printStackTrace(new PrintStream(buffer));

            string = buffer.toString();

        } else string = String.valueOf(object);

        //der Informationstext wird bereinigt und in einzelne Token zerlegt
        string = string.replace('\t', ' ');
        string = string.replace('\b', ' ');
        string = string.replace('\f', ' ').trim();

        if (string.length() == 0) return;

        synchronized (Messenger.class) {

            control = true;

            //der Datumsstring wird zusammengestellt
            timing = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date());

            //der Contexte wird ermittelt und optimiert
            stream = (this.context == null) ? "" : this.context.trim();

            //der Context wird bei Bedarf gesetzt
            if (stream.length() > 0) timing = timing.concat(stream).concat(" ");

            //der Zeilenumbruch wird entsprechend dem System ermittelt
            stream = System.getProperty("line.separator", "\r\n");

            //die einzelnen Zeilen werden ermittelt
            tokenizer = new StringTokenizer(string, "\r\n");

            while (tokenizer.hasMoreTokens()) {

                //die Nachrichtenzeile wird ermittelt
                string = tokenizer.nextToken();

                smooth = (buffer.size() == 0 || string.charAt(0) <= 32);

                if (trim) string = string.trim();

                if (string.trim().length() > 0) {

                    //die Folgezeilen werden gekennzeichnet
                    if (!control && smooth) string = ("... ").concat(string);

                    //die Zeile wird ausgegeben
                    try {this.write(timing.concat(string.concat(stream)).getBytes());
                    } catch (Throwable violation) {

                        //das die Methode fuer die abschliessenden Fehlerausgabe
                        //verwendet werden kann, werden alle auftretende Fehler
                        //abgefangen um eine ungewollte Rekursion zu verhindern
                    }

                    //die Zeilenkontrolle wird gesetzt
                    control = false;
                }
            }
        }
    }
}