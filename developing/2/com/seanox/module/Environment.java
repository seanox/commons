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
package com.seanox.module;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  Abstrakte Klasse zur Implementierung eines Environment, welche Methoden
 *  f&uuml;r den Zugriff auf die (CGI-)Umgebungsvariablen zur Verf&uuml;gung
 *  stellt. Alle auf den Header des Request bezogenen Umgebungsvariablen
 *  beginnen dabei mit HTTP und sind im Format HTTP_FIELD_NAME enthalten, somit
 *  wird z.B. der Header Eintrag User-Agent als HTTP_USER_AGENT eingetragen.<br>
 *  <br>
 *  Environment 1.2013.0427<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0427
 */
public abstract class Environment implements Cloneable {

    /** Hashtable mit den Umgebungsvariablen */
    protected Hashtable entries;

    /** lineare Liste der Umgebungsvariablen als FIFO */
    protected Vector list;

    /** Konstruktor, richtet Environment ein. */
    protected Environment() {

        this.entries = new Hashtable();
        this.list    = new Vector();
    }

    /**
     *  Entfernt die Steuerzeichen zur Textausrichtung aus dem &uuml;bergebenen
     *  String und wandelt alle Zeichen in Grossbuchstaben um.
     *  @param name zu optimierender String
     *  @return der optimierte und getrimmte String
     */
    private static String optimizeName(String name) {

        if (name == null) return "";

        name = name.replace('\b', ' ');
        name = name.replace('\f', ' ');
        name = name.replace('\n', ' ');
        name = name.replace('\r', ' ');
        name = name.replace('\t', ' ');

        return name.toUpperCase().trim();
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn die Umgebungsvariable enthalten
     *  ist, sonst <code>false</code>.
     *  @param  name Name der Umgebungsvariablen
     *  @return <code>true</code> wenn die Umgebungsvariable enthalten ist
     */
    public boolean contains(String name) {

        name = Environment.optimizeName(name);

        if (name.length() == 0) return false;

        return this.entries.containsKey(name);
    }

    /**
     *  R&uuml;ckgabe aller Umgebungsvariablen als Enumeration.
     *  @return alle Umgebungsvariablen als Enumeration
     */
    public Enumeration elements() {

        return this.list.elements();
    }

    /**
     *  R&uuml;ckgabe der vorgehaltenen Eintr&auml;ge als Hashtable Kopie.
     *  @return die vorgehaltenen Eintr&auml;ge als Hashtable Kopie
     */
    public Hashtable export() {

        return (Hashtable)this.entries.clone();
    }

    /**
     *  R&uuml;ckgabe des Umgebungsvariable. Ist diese nicht enthalten bzw. kann
     *  nicht ermittelt werden, wird ein leerer String zur&uuml;ck gegeben.
     *  @param  name Name der Umgebungsvariablen
     *  @return der Umgebungsvariablen, sonst ein leerer String
     */
    public String get(String name) {

        name = Environment.optimizeName(name);

        if (name.length() == 0) return "";

        name = (String)this.entries.get(name);

        return (name == null) ? "" : name;
    }

    /**
     *  Erweitert Environment um eine leere Umgebungsvariable.
     *  @param name Name der anzulegenden Umgebungsvariable
     */
    public void set(String name) {

        this.set(name, null);
    }

    /**
     *  Setzt die Umgebungsvariable mit dem entsprechenden Wert.
     *  @param name  Name der Umgebungsvariable
     *  @param entry Werteeintrag der Umgebungsvariable
     */
     public void set(String name, String entry) {

         //der Name wird optimiert
         name = Environment.optimizeName(name);

         //leere oder Umgebungsvariablen werden ignoriert
         if (name.length() == 0) return;

         //der Umgebungsvariablenwert wird gegebenfalls korrigiert
         if (entry == null) entry = "";

         this.entries.put(name, entry.trim());

         if (!this.list.contains(name)) this.list.add(name);
    }

    /**
     *  Entfernt die angegebenen Umgebungsvariable aus dem Environment.
     *  @param name Name der zu entfernenden Umgebungsvariable
     */
    public void remove(String name) {

        name = Environment.optimizeName(name);

        this.entries.remove(name);

        this.list.remove(name);
    }

    /**
     *  Erweitert die Environment um die Eintr&auml;ge der &uuml;bergebenen Map.
     *  Bereits vorhandene Eintr&auml;ge werden aktualisiert, neue
     *  hinzugef&uuml;gt.
     *  @param map zu &uuml;bernehmende Umgebungsvariablen
     */
    public void merge(Map map) {

        Iterator iterator;
        Object   object;
        String   string;

        if (map == null) return;

        //die Umgebungsvariablen werden ermittelt
        iterator = map.keySet().iterator();

        //die Eintraege werden einzeln uebernommen um ungueltige Eintraege
        //auszuschliessen und um die Umgebungsvariablen optimieren zu koennen
        while (iterator.hasNext()) {

            string = (String)iterator.next();
            object = map.get(string);

            if (!(object instanceof String)) continue;

            this.set(string, (String)object);
        }
    }

    /**
     *  F&uuml;hrt die &uuml;bergebenen Umgebungsvariablen mit den bestehenden
     *  zusammen. Bereits vorhandene Eintr&auml;ge werden dabei
     *  &uuml;berschrieben, sonst angelegt.
     *  @param environment zu &uuml;bernehmende Umgebungsvariablen
     */
    public void merge(Environment environment) {

        Enumeration enumeration;
        String      string;

        if (environment == null) return;

        //die Umgebungsvariablen werden ermittelt
        enumeration = environment.elements();

        //die Eintraege werden einzeln uebernommen um ungueltige Eintraege
        //auszuschliessen und um die Umgebungsvariablen optimieren zu koennen
        while (enumeration.hasMoreElements()) {

            string = (String)enumeration.nextElement();

            this.set(string, environment.get(string));
        }
    }

    /**
     *  R&uuml;ckgabe der Anzahl von Eintr&auml;gen.
     *  @return die Anzahl von Eintr&auml;gen
     */
    public int size() {

        return this.entries.size();
    }

    /** Setzt Environment komplett zur&uuml;ck. */
    public void clear() {

        this.entries.clear();
        this.list.clear();
    }

    /**
     *  R&uuml;ckgabe einer Kopie von Environment.
     *  @return eine Kopie von Environment
     */
    public Object clone() {

        Environment environment;
        
        try {environment = (Environment)super.clone();
        } catch (CloneNotSupportedException exception) {
            
            throw new InternalError();
        }
        
        //die Umgebungsvariablen werden als Kopie uebernommen
        environment.entries = (Hashtable)environment.entries.clone();

        //die Umgebungsvariablenliste wird als Kopie uebernommen
        environment.list = (Vector)environment.list.clone();

        return environment;
    }

    /**
     *  Formatiert den String zeilenweise mit der angegebenen Einr&uuml;ckung.
     *  @param spaces Weite der Einr&uuml;ckung
     *  @param string zur formatierender String
     *  @return der zeilenweise einger&uuml;ckte String
     */
    private static String toStringValue(int spaces, String string) {

        String          stream;
        StringBuffer    result;
        StringTokenizer tokenizer;

        result = new StringBuffer();

        //die Einrueckung wird zusammengestellt
        for (stream = ""; stream.length() < spaces;) stream = stream.concat(" ");

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        stream = System.getProperty("line.separator", "\r\n").concat(stream);

        tokenizer = new StringTokenizer(string, "\r\n");

        while (tokenizer.hasMoreTokens()) result.append(stream).append(tokenizer.nextToken());

        return result.toString().trim();
    }

    /**
     *  R&uuml;ckgabe der formatierten Struktur vom Environment als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Environmenttruktur als String
     */
    public String toString() {

        Enumeration  elements;
        String       buffer;
        String       stream;
        String       string;
        StringBuffer result;

        int          size;

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //alle Umgebungsvariablen werden ermittelt
        elements = this.list.elements();

        for (size = 0; elements.hasMoreElements();) {

            //die Maximale Zeichenlaenge der Umgebungsvariablen wird ermittelt
            buffer = (String)elements.nextElement();

            size = Math.max(size, buffer.length() +1);
        }

        result = new StringBuffer();

        //alle Umgebungsvariablen werden ermittelt
        elements = this.list.elements();

        while (elements.hasMoreElements()) {

            buffer = (String)elements.nextElement();
            stream = Environment.toStringValue(size, this.get(buffer));

            //der Umgebungsvariablenname wird ausgeglichen
            while (buffer.length() < size) buffer = buffer.concat(" ");

            result.append(buffer);

            if (stream.length() > 0) result.append("= ").append(stream);

            result.append(string);
        }

        return result.toString();
    }
}