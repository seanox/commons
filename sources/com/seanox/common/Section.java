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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  Section , stellt eine Schnittstelle zu den in den Sektionen von Initialize
 *  enthaltenen Werten zur Verf&uuml;gung.<br>
 *  <br>
 *  <b>Hinweis</b> - F&uuml;r eine optimale Verarbeitung von
 *  Initialisierungsdateien sollte Initialize immer in Kombination mit Section
 *  verwendet werden. Womit die nachfolgende Beschreibung auf der Kombination
 *  beider Komponenten basiert.<br>
 *  <br>
 *  Das verarbeitete INI-Format wurde zur klassischen Form erweitert. Die
 *  Unterteilung erfolgt auch hier in Sektionen, in denen zeilenweise
 *  Schl&uuml;ssel mit zugeh&ouml;rigen Werten abgelegt sind. Beim Namen von
 *  Sektion und Schl&uuml;ssel wird die Gross- und Kleinschreibung nicht
 *  ber&uuml;cksichtig. Mehrfache Deklarationen werden zusammengef&uuml;hrt,
 *  bereits vorhandene Schl&uuml;ssel &uuml;berschrieben und neue
 *  hinzugef&uuml;gt. Dadurch k&ouml;nnen Sektionen auch geteilt werden, was die
 *  &Uuml;bersichtlichkeit aber meist erschwert.<br>
 *  <br>
 *  Als Erweiterung zum Orginalformat lassen sich Sektionen vererben. Dazu wird
 *  einer Sektion das Sch&uuml;sselwort <code>EXTENDS</code> gefolgt vom Namen
 *  der referenzierenden Sektion nachgestellt. Damit &uuml;bernimmt die so
 *  abgeleitete Sektion alle Schl&uuml;ssel und Werte der referenzierten Sektion
 *  und kann diese erweitern oder &uuml;berschreiben.<br>
 *  <br>
 *  Das Zuweisen eines Wertes zu einem Schl&uuml;ssel erfolgt &uuml;ber das
 *  Gleichheitszeichen. Abweichend vom Orginalformat, kann die Zuweisung in der
 *  Folgezeile ohne erneute Angabe des Schl&uuml;ssels und durch die Verwendung
 *  des Pluszeichens fortgesetzt werden. Werte lassen sich zudem fest, variabel
 *  und optional zuweisen. Durch die zus&auml;tzliche Option <code>[?]</code> am
 *  Ende eines Schl&uuml;ssels, wird versucht f&uuml;r diesen einen Wert
 *  &uuml;ber die System-Properties der Java-Laufzeitumgebung zu ermitteln. Kann
 *  kein Wert ermittelt werden, wird der optional eingetragene zugewiesen. Ohne
 *  Wert gilt ein Schl&uuml;ssel mit der Option <code>[?]</code> als nicht
 *  angegeben und wird damit ignoriert.<br>
 *  <br>
 *  F&uuml;r Kommentare wird Semikolon verwendet. Wiederum Abweichend vom
 *  Orginalformat kann ein Kommentar an einer beliebiger Stelle in einer Zeile
 *  verwendet werden. Die nachfolgenden Zeichen sind somit kein Bestandteil von
 *  Sektion, Schl&uuml;ssel oder Wert. Zus&auml;tzlich zum Orginalformat,
 *  l&auml;sst sich die Kommentarfunktion f&uuml;r eine Zeile mit der Option
 *  <code>[+]</code> am Ende von Schl&uuml;ssel oder Wert abgeschalten. Somit
 *  kann auch das Semikolon als Zeichen verwendet werden.<br>
 *  <br>
 *  <pre>
 *     Bsp. 001 [SECTION] EXTENDS SECTION-A           ;Kommentar
 *          002   PARAM-A         = WERT-1            ;Kommentar
 *          003   PARAM-B         = WERT-2; WERT-3 [+]
 *          004                   + WERT-4; WERT-5 [+]
 *          005   PARAM-C     [+] = WERT-6; WERT-7
 *          006   PARAM-D  [?][+] = WERT-8; WERT-9
 *          007   PARAM-E  [?]    = WERT-0            ;Kommentar
 *          008   PARAM-F  [?]                        ;Kommentar
 *  </pre>
 *  Zeile 001 - Die Sektion mit dem Namen <code>"SECTION"</code> wird definiert.
 *  Die Option <code>EXTENDS</code> verweist auf die Ableitung von der Sektion
 *  <code>"SECTION-A"</code>. Somit werden bei der Anforderung der Sektion
 *  <code>"SECTION"</code> alle &uuml;ber <code>EXTENDS</code> direkt und/oder
 *  indirekt angegebenen Sektionen einbezogen. Ab dem Semikolon werden die
 *  nachfolgenden Zeichen als Kommentar interpretiert.<br>
 *  <br>
 *  Zeile 002 - Dem Schl&uuml;ssel <code>"PARAM-A"</code> wird der Wert
 *  <code>"WERT-1"</code> zugewiesen. Die nachfolgenden Zeichen werden ab dem
 *  Semikolon als Kommentar interpretiert.<br>
 *  <br>
 *  Zeile 003 - Dem Schl&uuml;ssel <code>"PARAM-B"</code> wird der Wert
 *  <code>"WERT-2; WERT-3"</code> zugewiesen. Durch die Option <code>[+]</code>
 *  am Ende des Werts wird der Zeilenkommentar abgeschaltet und alle Zeichen
 *  f&uuml;r die Wertzuweisung verwendet. Die Eingabe eines Kommentars ist in
 *  dieser Zeile nicht m&ouml;glich.<br>
 *  <br>
 *  Zeile 004 - Die Wertzuweisung von Zeile 003 wird fortgesetzt und der Wert
 *  <code>"WERT-4; WERT-5"</code> dem bestehenden Wert vom Schl&uuml;ssel
 *  <code>"PARAM-B"</code> hinzugef&uuml;gt. Durch die Option <code>[+]</code>
 *  am Ende der Wertzuweisung wird der Zeilenkommentar abgeschaltet und alle
 *  Zeichen f&uuml;r die Wertzuweisung verwendet. Die Eingabe eines Kommentars
 *  ist in dieser Zeile nicht m&ouml;glich. Weitere vorangestellte Optionen
 *  werden nicht unterst&uuml;tzt.<br>
 *  <br>
 *  Zeile 005 - &Auml;hnlich der Zeile 003 wird dem Schl&uuml;ssel
 *  <code>"PARAM-C"</code> der Wert <code>"WERT-2; WERT-3"</code> zugewiesen.
 *  Durch die Option <code>[+]</code> am Ende des Schl&uuml;ssels wird der
 *  Zeilenkommentar abgeschaltet, womit alle Zeichen f&uuml;r die Wertzuweisung
 *  verwendet werden. Die Eingabe eines Kommentars ist in dieser Zeile nicht
 *  m&ouml;glich.<br>
 *  <br>
 *  Zeile 006 - Die Wertzuweisung f&uuml;r den Schl&uuml;ssel
 *  <code>"PARAM-D"</code> erfolgt dynamisch. Dabei wird versucht, zu diesem ein
 *  Wert &uuml;ber die System-Properties der Java-Laufzeitumgebung zu ermitteln.
 *  Dazu muss dieser Schl&uuml;ssel ein Bestandteile der Laufzeitumgebung sein
 *  oder kann mit dem Programmstart in der Form <code>-Dname=wert</code>
 *  &uuml;bergeben werden. Die Gross- und Kleinschreibung vom Namen kann dabei
 *  unbeachtet bleiben. So &uuml;bergebene Werte werden komplett zugewiesen.
 *  Kommentare werden hierbei nicht unterst&uuml;tzt. Kann f&uuml;r den
 *  Schl&uuml;ssel kein Wert &uuml;ber die System-Properties ermittelt werden,
 *  wird die eingetragene Wertzuweisung <code>"WERT-8; WERT-9"</code> verwendet.
 *  Ein Kommentar ist durch Verwendung der Option <code>[+]</code> nicht
 *  m&ouml;glich.<br>
 *  <br>
 *  Zeile 007 - &Auml;hnlich der Zeile 006 wird auch hier der Wert f&uuml;r den
 *  Schl&uuml;ssel <code>"PARAM-E"</code> dynamisch &uuml;ber die
 *  System-Properties der Java-Laufzeitumgebung aufgel&ouml;st. Ist dies nicht
 *  m&ouml;glich, wird die eingetragene Wertzuweisung verwendet. Ohne die Option
 *  <code>[+]</code> ist in dieser Zeile auch ein Kommentar anwendbar.<br>
 *  <br>
 *  Zeile 008 - Wie in den Zeilen 006 und 007 erfolgt auch hier die Zuweisung
 *  vom Wert f&uuml;r den Schl&uuml;ssel <code>"PARAM-F"</code> dynamisch
 *  &uuml;ber die System-Properties der Java-Laufzeitumgebung. Ist dies nicht
 *  m&ouml;glich, wird der Schl&uuml;ssel nicht &uuml;bernommen.<br>
 *  <br>
 *  Section (Essential) 1.2013.0420<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0420
 */
public class Section implements Cloneable {

    /** Hashtable mit den Sch&uuml;sseln */
    private volatile Hashtable entries;

    /** lineare Liste der Sch&uuml;ssel als FIFO */
    private volatile Vector list;

    /** Konstruktor, richtet Section ein. */
    public Section() {

        this.entries = new Hashtable();
        this.list    = new Vector();
    }

    /**
     *  Entfernt die Steuerzeichen zur Textausrichtung aus dem String.
     *  @param  string zu optimierender String
     *  @return der optimierte und getrimmte String
     */
    private static String optimize(String string) {

        if (string == null) return "";

        string = string.replace('\b', ' ');
        string = string.replace('\f', ' ');
        string = string.replace('\n', ' ');
        string = string.replace('\r', ' ');
        string = string.replace('\t', ' ');

        return string.trim();
    }

    /**
     *  Erstellt ein Section-Objekt aus dem &uuml;bergebenen String.
     *  @param  string zu parsender String
     *  @return das erstellte Section-Objekt
     */
    public static Section parse(String string) {

        Enumeration     elements;
        Section         options;
        StringTokenizer tokenizer;
        String          buffer;
        String          entry;
        String          shadow;
        String          stream;

        int             cursor;
        int             option;

        options = new Section();

        if (string == null) return options;

        //Steuerzeichen zur Textausgabe werden entfernt
        string = string.replace('\b', ' ');
        string = string.replace('\f', ' ');
        string = string.replace('\t', ' ');

        //die Zeilen der Schuesselinformation werden ermittelt
        tokenizer = new StringTokenizer(string.trim(), "\r\n");

        for (shadow = ""; tokenizer.hasMoreTokens();) {

            //die naechste Zeile wird ermittelt
            stream = tokenizer.nextToken().trim();

            //der Kommentarteil wird ermittelt
            cursor = stream.indexOf(';');
            cursor = (cursor < 0) ? stream.length() : cursor;
            buffer = stream.substring(0, cursor).trim();

            if (buffer.startsWith("+")) {

                stream = stream.substring(1).trim();
                buffer = shadow;

                option = 4;

            } else {

                //der Schuessel und der Inhalt werden ermittelt
                cursor = buffer.indexOf('=');
                buffer = (cursor >= 0) ? buffer.substring(0, cursor).trim() : buffer;
                stream = (cursor >= 0) ? stream.substring(cursor +1).trim() : "";

                option = 0;
            }

            //die Zeilenoptionen werden im Schuessel ermittelt und entfernt
            while (buffer.endsWith("[+]") || buffer.endsWith("[?]")) {

                if (buffer.endsWith("[+]")) option |= 1;
                if (buffer.endsWith("[?]")) option |= 2;

                buffer = buffer.substring(0, buffer.length() -3).trim();
            }

            if (stream.endsWith("[+]")) option |= 1;

            shadow = ((option & 2) == 2) ? buffer.concat("[?]") : buffer;
            shadow = ((option & 1) == 1) ? shadow.concat("[+]") : shadow;

            //leere Schuessel werden ignoriert
            if (buffer.length() == 0) continue;

            //Schuesselnamen mit beginnenden Sektionszeichen werden ignoriert
            if (buffer.startsWith("[") || buffer.startsWith("]")) {shadow = ""; continue;}

            //die Zeilenoptionen werden im Wert ermittelt und entfernt
            while (stream.endsWith("[+]")) stream = stream.substring(0, stream.length() -3).trim();

            //ohne die Zeilenoption [+] zur Unterdrueckung des Zeilenkommentars
            //wird der Kommentarteil aus dem Wert zum Schuessel entfernt
            if ((option & 1) != 1 && ((cursor = stream.indexOf(';')) >= 0)) stream = stream.substring(0, cursor).trim();

            //die Zeilenoption [?] loest den Schuessel ueber Systemproperties
            //auf, sonst wird der gesetzt Standardwert verwendet
            if ((option & 2) == 2) {

                //es wird versucht den Wert direkt zur ermitteln
                entry = System.getProperty(buffer, "").trim();

                //alle Systemproperties werden ermittelt
                elements = System.getProperties().keys();

                while (elements.hasMoreElements() && (entry.length() == 0)) {

                    //die Systemproperties werden unabhaengig von der
                    //Gross- / Kleinschreibung nach dem Schuessel durchsucht
                    entry = (String)elements.nextElement();
                    entry = buffer.equalsIgnoreCase(entry.trim()) ? System.getProperty(entry, "").trim() : "";
                }

                //der Wert wird uebernommen, wenn dieser vorliegt
                if (entry.length() > 0) stream = entry;
            }

            //der Schuessel wird uebernommen wenn dieser aufgeloest werden kann
            if ((option & 2) != 2 || stream.length() > 0) {

                buffer = buffer.toUpperCase();

                string = (option & 4) == 4 ? (String)options.entries.get(buffer) : null;

                options.entries.put(buffer, (string == null ? stream : string.concat(" ").concat(stream)).trim());

                if (!options.list.contains(buffer)) options.list.add(buffer);
            }
        }

        return options;
    }

    /**
     *  R&uuml;ckgabe einer Kopie von Section.
     *  @return eine Kopie von Section
     */
    public Object clone() {

        Section options;

        //Section wird eingerichtet
        options = new Section();

        //die Schuessel werden als Kopie uebernommen
        options.entries = (Hashtable)this.entries.clone();

        //die Schuesselliste wird als Kopie uebernommen
        options.list = (Vector)this.list.clone();

        return options;
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn der Sch&uuml;ssel ist.
     *  @param  name Name des Sch&uuml;ssels
     *  @return <code>true</code> wenn der Sch&uuml;ssel enthalten ist
     */
    public boolean contains(String name) {

        name = Section.optimize(name).toUpperCase();

        return this.entries.containsKey(name);
    }

    /**
     *  R&uuml;ckgabe aller Sch&uuml;ssel als Enumeration.
     *  @return alle Sch&uuml;ssel als Enumeration
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
     *  R&uuml;ckgabe des Wert zum Sch&uuml;ssel. Ist dieser nicht enthalten
     *  bzw. kann nicht ermittelt werden, wird ein leerer String
     *  zur&uuml;ckgegeben.
     *  @param  name Name des Sch&uuml;ssels
     *  @return der Wert des Sch&uuml;ssels, sonst ein leerer String
     */
    public String get(String name) {

        name = (String)this.entries.get(Section.optimize(name).toUpperCase());

        return (name == null) ? "" : name;
    }

    /**
     *  F&uuml;hrt die Sch&uuml;ssel dieser und der &uuml;bergebenen Sektion
     *  zusammen. Bereits vorhandene Eintr&auml;ge werden &uuml;berschrieben,
     *  neue werden hinzugef&uuml;gt.
     *  @param section zu &uuml;bernehmende Sektion
     */
    public void merge(Section section) {

        Enumeration enumeration;
        String      string;

        if (section == null) return;

        //die Schuessel werden ermittelt
        enumeration = section.elements();

        //die Eintraege werden einzeln uebernommen um ungueltige Eintraege
        //auszuschliessen und um die Schuessel optimieren zu koennen
        while (enumeration.hasMoreElements()) {

            string = (String)enumeration.nextElement();

            this.set(string, section.get(string));
        }
    }

    /**
     *  F&uuml;hrt die Sch&uuml;ssel dieser Sektion und der &uuml;bergebenen Map
     *  zusammen. Bereits vorhandene Eintr&auml;ge werden &uuml;berschrieben,
     *  neue werden hinzugef&uuml;gt.
     *  @param map zu &uuml;bernehmende Schl&uuml;ssel
     */
    public void merge(Map map) {

        Iterator iterator;
        Object   object;
        String   string;

        if (map == null) return;

        //die Schluessel werden ermittelt
        iterator = map.keySet().iterator();

        //die Eintraege werden einzeln uebernommen um ungueltige Eintraege
        //auszuschliessen und um die Schluessel optimieren zu koennen
        while (iterator.hasNext()) {

            string = (String)iterator.next();
            object = map.get(string);

            if (!(object instanceof String)) continue;

            this.set(string, (String)object);
        }
    }

    /**
     *  Entfernt den angegebenen Sch&uuml;ssel.
     *  @param name Name des zu entfernenden Sch&uuml;ssels
     */
    public void remove(String name) {

        name = Section.optimize(name).toUpperCase();

        this.entries.remove(name);

        this.list.remove(name);
    }

    /** Setzt Section komplett zur&uuml;ck. */
    public void clear() {

        this.entries.clear();
        this.list.clear();
    }

    /**
     *  Erweitert Section um einen leeren Sch&uuml;ssel.
     *  @param name Name des anzulegenden Sch&uuml;ssel
     */
    public void set(String name) {

        this.set(name, null);
    }

    /**
     *  Setzt den Sch&uuml;ssel mit dem entsprechenden Wert.
     *  @param name  Name des Sch&uuml;ssels
     *  @param entry Wert des Sch&uuml;ssels
     */
     public void set(String name, String entry) {

         //der Name wird optimiert
         name = Section.optimize(name).toUpperCase();

         //leere oder Schuessel mit beginnenden Sektions- bzw. enthaltenen
         //Wert-/Kommentar- Trennzeichen []=; werden ignoriert
         if (name.length() == 0 || name.startsWith("[") || name.startsWith("]") || name.indexOf(';') >= 0 || name.indexOf('=') >= 0) return;

         this.entries.put(name, Section.optimize(entry));

         if (!this.list.contains(name)) this.list.add(name);
    }

    /**
     *  R&uuml;ckgabe der Anzahl von Eintr&auml;gen.
     *  @return die Anzahl der Eintr&auml;ge
     */
    public int size() {

        return this.entries.size();
    }

    /**
     *  R&uuml;ckgabe der formatierten Struktur als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Struktur als String
     */
    public String toString() {

        Enumeration elements;
        String      buffer;
        String      result;
        String      stream;
        String      string;

        int         offset;
        int         size;

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        buffer = System.getProperty("line.separator", "\r\n");

        //alle Schluessel werden ermittelt
        elements = this.list.elements();

        for (size = 0; elements.hasMoreElements();) {

            //die Maximale Zeichenlaenge der Schluessel wird ermittelt
            string = (String)elements.nextElement();
            stream = (String)this.entries.get(string);
            offset = (stream == null || stream.indexOf(';') < 0) ? 1 : 5;

            if (size < string.length() +offset) size = string.length() +offset;
        }

        //alle Schluessel werden ermittelt
        elements = this.list.elements();

        for (result = ""; elements.hasMoreElements();) {

            string = (String)elements.nextElement();
            stream = (String)this.entries.get(string);

            if (stream == null) stream = "";

            offset = (stream.indexOf(';') < 0) ? 0 : 4;

            //der Schluessel wird ausgeglichen
            while (string.length() < size -offset) string = string.concat(" ");

            if (offset > 0) string = string.concat("[+] ");

            result = result.concat(string);

            if (stream.length() > 0) result = result.concat("= ").concat(stream);

            result = result.concat(buffer);
        }

        return result;
    }
}