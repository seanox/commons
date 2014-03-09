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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *  Loader, stellt Funktionen zum Laden von Bibliotheken zur Verf&uuml;gung. Die
 *  Dateien der geladene Bibliotheken werden dabei nicht gesperrt und
 *  k&ouml;nnen somit zur Laufzeit ge&auml;ndert werden.<br>
 *  <br>
 *  Hinweis - Diese Klasse basiert auf den Quellen des AdaptiveClassLoader von
 *  Francis J. Lacoste, Martin Pool, Jim Heintz und Stefano Mazzocchi aus dem
 *  Apache JServ Projekt (http://java.apache.org/).<br>
 *  <br>
 *  Loader 1.2009.0616<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2009.0616
 */
public class Loader extends URLClassLoader {

    /** &uuml;bergeordneter ClassLoader */
    private volatile ClassLoader loader;

    /** Zwischenspeicher geladener Klassen (Name, Klasse) */
    private volatile Hashtable classes;

    /** Verzeichnis der nach Klassen zu durchsuchenden Bibliotheken. */
    private volatile List libraries;

    /**
     *  Konstruktor, richtet den Loader ein.
     *  @param libraries Liste der nach Klassen zu durchsuchenden Bibliotheken
     *  @param loader    &uuml;bergeordneter ClassLoader
     */
    public Loader(List libraries, ClassLoader loader) {

        super(new URL[0], loader);

        this.classes   = new Hashtable();
        this.libraries = libraries;
        this.loader    = loader;
    }

    /**
     *  Liefert einen InputStream, mit dem die durch name bezeichnete Ressource
     *  ausgelesen werden kann. Falls die Ressource nicht gefunden werden
     *  konnte, wird <code>null</code> zur&uuml;ckgegeben.
     *  @param  name Name der Ressource
     *  @return der InputStream zur Ressource, sonst <code>null</code> wenn
     *          diese nicht ermittelt werden kann
     */
    public InputStream getResourceAsStream(String name) {

        InputStream input;
        Iterator    iterator;
        ZipEntry    entry;
        ZipFile     store;

        byte[]      bytes;

        int         cursor;
        int         size;
        int         volume;

        input = (this.loader == null) ? null : this.loader.getResourceAsStream(name);

        if (input == null) input = ClassLoader.getSystemResourceAsStream(name);

        if (input == null) {

            iterator = this.libraries.iterator();

            while (iterator.hasNext()) {

                store = null;

                try {

                    store = new ZipFile((File)iterator.next());

                    entry = store.getEntry(name);

                    if (entry != null) {

                        input  = store.getInputStream(entry);
                        volume = (int)entry.getSize();
                        bytes  = new byte[volume];

                        size = cursor = 0;

                        //der Datenstrom wird ausgelesen
                        while (volume > 0 && (size = input.read(bytes, cursor, volume)) >= 0) {

                            cursor += size;
                            volume -= size;
                        }

                        return new ByteArrayInputStream(bytes);
                    }

                } catch (Throwable throwable) {

                    //keine Fehlerbehandlung vorgesehen

                } finally {

                    try {store.close();
                    } catch (Throwable throwable) {

                        //keine Fehlerbehandlung vorgesehen
                    }
                }
            }
        }

        return input;
    }

    /**
     *  R&uuml;ckgabe der per Namen angeforderten Ressource als URL. Kann diese
     *  nicht ermittelt werden, wird <code>null</code> zur&uuml;ckgegeben.
     *  @param  name Name der Ressource, to be used as is.
     *  @return die URL zur angeforderten Ressource, sonst <code>null</code>
     *          wenn diese nicht ermittelt werden kann
     */
    public URL getResource(String name) {

        Iterator iterator;
        String   source;
        URL      url;
        ZipFile  store;

        if (name == null) return null;

        url = (this.loader == null) ? null : this.loader.getResource(name);

        if (url == null) url = ClassLoader.getSystemResource(name);

        if (url == null) {

            iterator = this.libraries.iterator();

            for (store = null; iterator.hasNext();) {

                source = ((File)iterator.next()).getAbsolutePath();

                try {

                    store = new ZipFile(source);

                    if (store.getEntry(name) != null) return new URL(("jar:file:").concat(source).concat("!/").concat(name));

                } catch (Throwable throwable) {

                    //keine Fehlerbehandlung vorgesehen

                } finally {

                    try {store.close();
                    } catch (Throwable throwable) {

                        //keine Fehlerbehandlung vorgesehen
                    }
                }
            }
        }

        return url;
    }

    /**
     *  L&auml;dt die per Name angegebene Klasse. sse. Im Namen kann entweder
     *  der Punkt oder der Schr&auml;gstrich als Paket-Trennzeichen benutzt
     *  werden. Mit der Option <code>resolve</code> kann entschieden werden, ob
     *  die Aufl&ouml;sung von Abh&auml;ngigkeiten n&ouml;tig ist. Bei
     *  <code>true</code> werden auch die von dieser Klasse ben&ouml;tigten
     *  Klassen geladen.
     *  @param  name    Name der Klasse
     *  @param  resolve Option <code>true</code> um Abh&auml;ngigkeitem zu laden
     *  @return die geladene Klasse, ist diese nicht ermittelbar, f&uuml;hrt der
     *          Aufruf zur Ausnahme <code>ClassNotFoundException</code>
     */
    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class           source;
        InputStream     input;
        Iterator        iterator;
        SecurityManager security;
        String          packet;
        ZipEntry        entry;
        ZipFile         store;

        byte[]          bytes;

        int             cursor;
        int             size;
        int             volume;

        name = (name == null) ? "" : name.trim();

        if (name.length() == 0) return null;

        source = (Class)this.classes.get(name);

        if (source != null) return source;

        try {

            //die Berechtigung zur Definition der Klasse wird geprueft, wenn
            //ein entsprechender SecurityManager vorliegt, ohne ist Definition
            //aller Klassen zulaessig
            security = System.getSecurityManager();

            cursor = name.lastIndexOf('.');

            packet = (cursor > -1) ? name.substring(0, cursor) : "";

            //prueft die Berechtigung zum Laden der Klasse/Paket, liegt diese
            //Berechtigung nicht vor, fuehrt dies zur SecurityException
            if (security != null) security.checkPackageDefinition(packet);

            //das Package wird registriert
            if (super.getPackage(packet) == null) super.definePackage(packet, null, null, null, null, null, null, null);

        } catch (SecurityException exception) {

            if (this.loader == null) throw exception;

            this.loader.loadClass(name);
        }

        try {

            //die Klasse wird versucht als (System)Klasse zu laden
            source = (this.loader == null) ? null : this.loader.loadClass(name);

            if (source != null) {

                if (resolve) super.resolveClass(source);

                return source;
            }

        } catch (Exception exception) {

            source = null;
        }

        //alle Bibliotheken werden ermittelt
        iterator = this.libraries.iterator();

        for (store = null; iterator.hasNext();) {

            try {

                //das Zip-Archiv wird geoeffnet
                store = new ZipFile((File)iterator.next());

                //der Klassenname wird vereinheitlicht
                entry = store.getEntry(name.replace('.', '/').concat(".class"));

                if (entry != null) {

                    input  = store.getInputStream(entry);
                    volume = (int)entry.getSize();
                    bytes  = new byte[volume];

                    size = cursor = 0;

                    //der Datenstrom wird ausgelesen
                    while (volume > 0 && (size = input.read(bytes, cursor, volume)) >= 0) {

                        cursor += size;
                        volume -= size;
                    }

                    //die Klasse wird ueber den ClassLoader definiert
                    source = super.defineClass(name, bytes, 0, bytes.length);

                    //die Klasse wird als geladen registriert
                    this.classes.put(name, source);

                    //gegebenfalls werden die Abhaengigkeitem aufgeloest
                    if (resolve) super.resolveClass(source);

                    return source;
                }

            } catch (Throwable throwable) {

                //keine Fehlerbehandlung vorgesehen

            } finally {

                //das Zip-Archiv wird geschlossen
                try {store.close();
                } catch (Throwable throwable) {

                    //keine Fehlerbehandlung vorgesehen
                }
            }
        }

        throw new ClassNotFoundException(name);
    }
}