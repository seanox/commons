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
package com.seanox.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *  Resource stellt Methoden f&uuml;r den einfachen Zugriff auf Ressourcen zu
 *  Verf&uuml;gung, die sich im Klassenpfad befinden. Diese k&ouml;nnen sich
 *  direkt im Dateisystem oder in eingebunden Libraries befinden.<br>
 *  <br>
 *  Resource 1.2013.0314<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0314
 */
public class Resource {

    /** Bezugsobjekt zur Brenzung vom Wurzelverzeichnis */
    private volatile Class scope;

    /** Pfad der Ressource */
    private volatile String path;

    /** URL der Ressource */
    private volatile URL locale;

    /** Typ der Ressource */
    private volatile int type;

    /** Informationen der Ressource als Structure Bits */
    private volatile int structure;

    /** Gr&ouml;sse der Ressource in Bytes */
    private volatile long length;

    /** Zeitpunkt der letzen &Auml;nderung */
    private volatile long modified;

    /** Konstante f&uuml;r das Strukture Bit DIRECTORY */
    private static final int STRUCTURE_DIRECTORY = 1;

    /** Konstante f&uuml;r das Strukture Bit FILE */
    private static final int STRUCTURE_FILE = 2;

    /** Konstante f&uuml;r den Ressourcen Typ Datei */
    private static final int TYPE_FILE = 1;

    /** Konstante f&uuml;r den Ressourcen Typ Ressource */
    private static final int TYPE_RESOURCE = 2;

    /** Konstruktor, richtet die Resource ein. */
    private Resource() {

        return;
    }

    /**
     *  Ermittelt die per Pfad angegebene Ressource. Kann diese nicht ermittelt
     *  werden, wird <code>null</code> zur&uuml;ckgegeben.
     *  @param  scope Bezugsobjekt zur Brenzung vom Wurzelverzeichnis
     *  @param  path  Pfad der Ressource
     *  @return die ermittelte Ressource, sonst <code>null</code>
     *  @throws IOException bei fehlerhaftem Datenzugriff
     */
    public static Resource get(Object scope, String path) throws IOException {

        File     file;
        Resource resource;
        String   protocol;
        String   string;
        ZipFile  archive;
        ZipEntry check;
        ZipEntry entry;

        int      cursor;

        resource = new Resource();

        //der Name wird allgemein bereinigt uebernommen
        resource.path = path = Codec.decode(path, Codec.DOT);

        if (resource.path.length() == 0) throw new IOException("Resource not found");

        //ggf. wird der Scope als DocRoot vorangestellt
        resource.scope = scope != null ? scope instanceof Class ? (Class)scope : scope.getClass() : null;

        if (resource.scope != null) resource.path = resource.scope.getName().replace('.', '/').concat("/../").concat(resource.path);

        resource.path = Codec.decode(("/").concat(resource.path), Codec.DOT);

        //die URL der Ressource wird ermittelt
        resource.locale = resource.scope != null ? resource.scope.getResource(resource.path) : null;

        if (resource.locale == null) resource.locale = resource.scope.getClassLoader().getResource(resource.path);
        if (resource.locale == null) resource.locale = ClassLoader.getSystemResource(resource.path);

        if (resource.locale == null) throw new IOException(("Resource (").concat(String.valueOf(path)).concat(") not found"));

        archive = null;

        try {

            //das Protokoll wird ermittelt
            protocol = resource.locale.getProtocol();

            if (protocol.equalsIgnoreCase("file")) {

                //der Typ wird gesetzt
                resource.type = Resource.TYPE_FILE;

                //die Datei wird ermittelt
                file = new File(resource.locale.getPath());

                if (file.isDirectory()) resource.structure |= Resource.STRUCTURE_DIRECTORY;
                if (file.isFile())      resource.structure |= Resource.STRUCTURE_FILE;

                resource.length   = file.length();
                resource.modified = file.lastModified();

            } else if (protocol.equalsIgnoreCase("jar")) {

                //der Typ wird gesetzt
                resource.type = Resource.TYPE_RESOURCE;

                string = resource.locale.getPath();
                cursor = string.indexOf('!');

                if (cursor >= 0) string = string.substring(0, cursor);

                //das Archiv wird eingerichtet
                archive = new ZipFile(new URL(string).getPath());

                //der Archiveintrag wird ermittelt (beginnen ohne Slash)
                entry = archive.getEntry(resource.path.substring(1));

                if (entry == null) return null;

                check = archive.getEntry(resource.path.substring(1).concat("/"));

                resource.structure |= (entry.isDirectory() || (check != null && check.isDirectory())) ? Resource.STRUCTURE_DIRECTORY : Resource.STRUCTURE_FILE;

                resource.length   = entry.getSize();
                resource.modified = entry.getTime();

            } else throw new IOException(("Protocol (").concat(String.valueOf(protocol)).concat(") not supported"));

        } finally {

            try {if (archive != null) archive.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }

        return resource;
    }

    /**
     *  R&uuml;ckgabe von Namen der Ressource.
     *  @return der Name der Ressource
     */
    public String name() {

        int index;

        index = this.path().lastIndexOf('/');

        return index < 0 ? this.path : this.path.substring(index +1);
    }

    /**
     *  R&uuml;ckgabe von Pfad der Ressource.
     *  @return der Pfad der Ressource
     */
    public String path() {

        return this.path;
    }

    /**
     *  R&uuml;ckgabe der realen URL. Handelt es sich bei der Ressource um einen
     *  Archiveintrag, ist das Archiv in der URL enthalten. Konnte die Ressource
     *  im System nicht ermittelt werden ist dieser Wert <code>null</code>.
     *  @return die reale URL
     */
    public URL locale() {

        return this.locale;
    }

    /**
     *  R&uuml;ckgabe vom Zeitpunkt der letzten &Auml;nderung in Millisekunden.
     *  @return der Zeitpunkt der letzten &Auml;nderung in Millisekunden
     */
    public long lastModified() {

        return this.modified;
    }

    /**
     *  R&uuml;ckgabe der Datengr&ouml;sse in Bytes.
     *  @return die Datengr&ouml;sse in Bytes
     */
    public long length() {

        return this.length;
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn es sich um ein Verzeichnis handelt.
     *  @return <code>true</code> wenn es sich um ein Verzeichnis handelt
     */
    public boolean isDirectory() {

        return ((this.structure & Resource.STRUCTURE_DIRECTORY) == Resource.STRUCTURE_DIRECTORY);
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn es sich um eine Datei handlet.
     *  @return <code>true</code> wenn es sich um ein Datei handlet
     */
    public boolean isFile() {

        return ((this.structure & Resource.STRUCTURE_FILE) == Resource.STRUCTURE_FILE);
    }

    /**
     *  R&uuml;ckgabe vom InputStream, kann dieser nicht eingerichtet werden,
     *  wird <code>null</code> zur&uuml;ckgegeben.
     *  @return der InputStream, sonst <code>null</code>
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public InputStream getStream() throws IOException {

        //der Datenstrom der Ressource wird eingerichtet
        if (this.type == Resource.TYPE_RESOURCE) return this.scope.getResourceAsStream(this.path);

        //der Datenstrom der Ressource als Datei wird eingerichtet
        try {return new FileInputStream(new File(this.locale.toURI()));
        } catch (URISyntaxException exception) {

            throw new IOException(exception.getMessage());
        }
    }

    /**
     *  Formatiert das Datum im angebenden Format und in der angegebenen Zone.
     *  R&uuml;ckgabe das formatierte Datum, im Fehlerfall ein leerer String.
     *  @param  format Formatbeschreibung
     *  @param  date   zu formatierendes Datum
     *  @param  zone   Zeitzone, <code>null</code> Standardzone
     *  @return das formatierte Datum als String, im Fehlerfall leerer String
     */
    private static String formDate(String format, Date date, String zone) {

        SimpleDateFormat pattern;

        //die Formatierung wird eingerichtet
        pattern = new SimpleDateFormat(format, Locale.US);

        //die Zeitzone wird gegebenenfalls fuer die Formatierung gesetzt
        if (zone != null) pattern.setTimeZone(TimeZone.getTimeZone(zone));

        //die Zeitangabe wird formatiert
        return pattern.format(date);
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zur Resource als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zur Resource als String
     */
    public String toString() {

        String buffer;
        String result;
        String string;

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = ("[").concat(this.getClass().getName()).concat("]").concat(string);

        result = result.concat("  path      = ").concat(String.valueOf(this.path)).concat(string);
        result = result.concat("  locale    = ").concat(String.valueOf(this.locale)).concat(string);

        buffer = new String();

        if (this.isDirectory()) buffer = buffer.concat(" ").concat("DIRECTORY").trim();
        if (this.isFile())      buffer = buffer.concat(" ").concat("FILE").trim();

        result = result.concat("  structure = ").concat((buffer.length() == 0 ) ? "unknown" : buffer).concat(string);
        result = result.concat("  length    = ").concat(String.valueOf(this.length)).concat(string);
        result = result.concat("  modified  = ").concat(Resource.formDate("E, dd MMM yyyy HH:mm:ss z", new Date(this.modified), "GMT")).concat(string);

        return result;
    }
}