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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  Initialize, verarbeitet Initialisierungsdaten im INI-Format und stellt diese
 *  als Sektionen zur Verf&uuml;gung.<br>
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
 *  Initialize 1.2013.0420<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0420
 */
public class Initialize implements Cloneable {

    /** Hashtable der Sektionen */
    private volatile Hashtable entries;

    /** Liste der Ableitungen */
    private volatile Hashtable resolve;

    /** lineare Liste der Sektionen als FIFO */
    private volatile Vector list;

    /** Konstruktor, richtet Initialize ein.*/
    public Initialize() {

        this.entries = new Hashtable();
        this.resolve = new Hashtable();
        this.list    = new Vector();
    }

    /**
     *  R&uuml;ckgabe aller Sektionen als Enumeration.
     *  @return alle Sektionen als Enumeration
     */
    public Enumeration elements() {

        return this.list.elements();
    }

    /**
     *  Ermittelt aus dem String die enthaltenen Sektionen.
     *  R&uuml;ckgabe die ermittelten Sektionen als Initialize.
     *  @param  string zu parsender String
     *  @return die ermittelten Sektionen als Initialize
     */
    public static Initialize parse(String string) {

        Initialize      initialize;
        StringTokenizer tokenizer;
        String          buffer;
        String          stream;

        int             cursor;

        initialize = new Initialize();

        if (string == null) return initialize;

        string = string.replace('\b', ' ');
        string = string.replace('\f', ' ');
        string = string.replace('\t', ' ');

        buffer = null;

        tokenizer = new StringTokenizer(string, "\r\n");

        while (tokenizer.hasMoreTokens()) {

            stream = ((String)tokenizer.nextElement()).trim();

            if (stream.startsWith("[")) {

                //die Begrenzung der Sektion wird vereinfacht
                stream = stream.substring(1);

                //der Kommentarteil wird entfernt
                if ((cursor = stream.indexOf(';')) >= 0) stream = stream.substring(0, cursor);

                //nachfolgende Sektionen sind nicht zulaessig und werden entfernt
                if ((cursor = stream.indexOf('[')) >= 0) stream = stream.substring(0, cursor);

                //der eindeutige Name der Sektion wird ermittelt
                buffer = Initialize.optimizeField(((cursor = stream.indexOf(']')) >= 0) ? stream.substring(0, cursor) : stream);

                //weitere Optionen zur Sektion werden ermittelt
                stream = (cursor >= 0) ? Initialize.optimizeField(stream.substring(cursor +1)) : "";

                if (buffer.length() > 0 && stream.startsWith(("EXTENDS").concat(" "))) {

                    stream = stream.substring(8).trim();

                    if ((cursor = stream.indexOf(' ')) >= 0) stream = stream.substring(0, cursor).trim();

                    if (stream.length() > 0) initialize.resolve.put(buffer, Initialize.optimizeField(stream));
                }

                //die Sektion wird eingerichtet
                initialize.set(buffer, null);

            } else initialize.set(buffer, stream);
        }

        return initialize;
    }

    /**
     *  Ermittelt die in der angegebenen Datei enthaltenen Sektionen.
     *  R&uuml;ckgabe die ermittelten Sektionen als Initialize.
     *  @param  file Pfad der Konfigurationsdatei
     *  @return die ermittelten Sektionen als Initialize
     *  @throws IOException bei fehlerhaftem Datenzugriff
     */
    public static Initialize load(String file) throws IOException {

        ByteArrayOutputStream buffer;
        InputStream           input;
        Initialize            initialize;

        byte[]                bytes;

        int                   size;

        //Initialize wird initial eingerichtet
        initialize = new Initialize();

        if (file == null || file.trim().length() == 0) return initialize;

        //die Variablen werden eingerichtet
        buffer = new ByteArrayOutputStream();
        bytes  = new byte[65535];
        input  = null;

        try {

            //der Datenstrom wird eingerichtet
            input = new FileInputStream(file);

            //die Daten werden gelesen und in Buffer uebernommen
            while ((size = input.read(bytes)) >= 0) buffer.write(bytes, 0, size);

            //die Daten werden in Initialize uebernommen
            initialize = Initialize.parse(buffer.toString());

        } finally {

            //der Datenstrom wird geschlossen
            try {input.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }

        return initialize;
    }

    /**
     *  Entfernt die Steuerzeichen zur Textausrichtung aus dem &uuml;bergebenen
     *  String und wandelt alle Zeichen in Grossbuchstaben um.
     *  @param string zu optimierender String
     *  @return der optimierte und getrimmte String
     */
    private static String optimizeField(String string) {

        if (string == null) return "";

        string = string.replace('\b', ' ');
        string = string.replace('\f', ' ');
        string = string.replace('\n', ' ');
        string = string.replace('\r', ' ');
        string = string.replace('\t', ' ');

        return string.toUpperCase().trim();
    }

    /**
     *  Speichert die enthaltenen Sektion im INI-Format in der angegeben Datei.
     *  Besteht die Datei bereits wird diese &uuml;berschrieben. Der
     *  Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @param  filename Pfad der Datei
     *  @throws IOException bei fehlerhaftem Dateizugriff
     */
    public void save(String filename) throws IOException {

        FileOutputStream output;

        //initiale Einrichtung des Datenstroms
        output = null;

        try {

            //der Datenstrom wird eingerichtet
            output = new FileOutputStream(filename);

            //die Daten werden in die Datei ausgegeben
            output.write(this.toString().getBytes());

        } finally {

            //der Datenstrom wird geschlossen
            try {output.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn die Sektion enthalten ist.
     *  @param  name Name der Sektion
     *  @return <code>true</code> wenn die Sektion enthalten ist
     */
    public boolean contains(String name) {

        return this.entries.containsKey(Initialize.optimizeField(name));
    }

    /**
     *  R&uuml;ckgabe der Sektion mit allen per <code>EXTENDS</code> direkt und
     *  indirekt angegebenen Ableitung(en).
     *  @param  name Name der Sektion
     *  @return die Sektion als String
     */
    public String get(String name) {

        return this.get(name, true);
    }

    /**
     *  R&uuml;ckgabe der entsprechenden Sektion.
     *  @param  name    Name der Sektion
     *  @param  resolve Option <code>true</code> zum Aufl&ouml;sen der Vererbung
     *  @return die Sektion als String
     */
    public String get(String name, boolean resolve) {

        String buffer;
        String result;
        String stream;
        String string;
        Vector vector;

        string = Initialize.optimizeField(name);
        stream = (String)this.entries.get(string);

        if (stream == null) return "";

        if (!resolve) return stream;

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        stream = System.getProperty("line.separator", "\r\n");

        //die Vererbung wird ermittelt und die Sektion kumuliert aufgebaut
        for (vector = new Vector(), result = ""; vector.indexOf(string) < 0 && string.length() > 0;) {

            vector.add(string);

            buffer = (String)this.entries.get(string);
            buffer = (buffer == null) ? "" : buffer;
            result = buffer.concat((buffer.length() == 0 || result.length() == 0) ? "" : stream).concat(result);
            string = (String)this.resolve.get(string);

            if (string == null) break;
        }

        return result;
    }

    /**
     *  Erweitert Initialize um eine leere Sektion.
     *  @param name Name der Sektion
     */
    public void set(String name) {

        this.remove(name);

        this.add(name, null, null);
    }

    /**
     *  Setzt die entsprechende Sektion mit dem angegebenen Inhalt.
     *  Bestehende Sektionen werden entfernt und neu gesetzt.
     *  @param name    Name der Sektion
     *  @param content Inhalt der Sektion
     */
    public void set(String name, String content) {

        this.remove(name);

        this.add(name, content, null);
    }

    /**
     *  Setzt die entsprechende Sektion mit dem angegebenen Inhalt.
     *  Bestehende Sektionen werden entfernt und neu gesetzt.
     *  @param name    Name der Sektion
     *  @param content Inhalt der Sektion
     *  @param extend  optionaler Verweis auf eine Sektion
     */
    public void set(String name, String content, String extend) {

        this.remove(name);

        this.add(name, content, extend);
    }

    /**
     *  Erweitert Initialize um die &uuml;bergebene Sektion. Existiert diese
     *  bereits, wird diese erweitert, sonst neu angelegt. Leere Zeileninhalte,
     *  die mit <code>[</code> oder <code>]</code> beginnen werden als interne
     *  Sektion betrachtet und ignoriert.
     *  @param  name    Name der Sektion
     *  @param  content Inhalt der Sektion
     *  @param  extend  optionaler Verweis auf eine Sektion
     *  @throws IllegalArgumentException bei ung&uuml;ltigen Namen und Inhalten
     *          der Sektion
     */
    private void add(String name, String content, String extend) {

        StringTokenizer tokenizer;
        String          stream;
        String          string;

        //nur Angaben mit Name sind zulaessig
        if (name == null) return;

        //der Name wird vereinfacht
        name = Initialize.optimizeField(name);

        //leere Namen sind nicht zulaessig
        if (name.length() == 0) throw new IllegalArgumentException("Invalid empty section name");

        //Namen mit Secktions- bzw. Kommentarzeichen sind nicht zulaessig
        if (name.indexOf('[') >= 0 || name.indexOf(']') >= 0 || name.indexOf(';') >= 0) throw new IllegalArgumentException(("Invalid character in section name").concat(name));

        //der Inhalt wird gegebenfalls korrigiert bzw. getrimmt
        content = (content == null) ? content = "" : content.trim();

        //im Inhalt enthaltene Untersektionen werden entfernt
        tokenizer = new StringTokenizer(content, "\r\n");

        for (stream = ""; tokenizer.hasMoreTokens();) {

            string = tokenizer.nextToken().trim();

            //leere oder Zeilen mit beginnenden Sektionszeichen [] werden ignoriert
            if (string.length() == 0 || string.startsWith("[") || string.startsWith("]")) continue;

            stream = stream.concat(string).concat("\r\n");
        }

        //der Inhalt zur Sektion wird wenn moeglich ermittelt und erweitert
        string = ((string = (String)this.entries.get(name)) == null) ? "" : string.trim();

        if (string.length() > 0) string = string.concat("\r\n");

        //die Sektion wird gesetzt
        this.entries.put(name, string.concat(stream).trim());

        extend = Initialize.optimizeField(extend);

        if (extend.length() > 0) this.resolve.put(name, extend); else this.resolve.remove(name);

        if (!this.list.contains(name)) this.list.add(name);
    }

    /**
     *  Entfernt die angegebene Sektion aus Initialize.
     *  @param name Name der zu entfernenden Sektion
     */
    public void remove(String name) {

        name = Initialize.optimizeField(name);

        this.entries.remove(name);
        this.resolve.remove(name);
        this.list.remove(name);
    }

    /**
     *  &Uuml;bernimmt die Sektionen der &uuml;bergebenen Map. Bereits
     *  vorhandene werden dabei aktualisiert, sonst neue hinzugef&uuml;gt.
     *  @param map zu &uuml;bernehmende Sektionen
     */
    public void merge(Map map) {

        Iterator iterator;
        Object   object;
        String   string;

        if (map == null) return;

        //die Sektionen werden ermittelt
        iterator = map.keySet().iterator();

        //die Eintraege werden einzeln uebernommen um ungueltige Eintraege
        //auszuschliessen und um die Sektion optimieren zu koennen
        while (iterator.hasNext()) {

            string = (String)iterator.next();
            object = map.get(string);

            if (!(object instanceof String)) continue;

            this.set(string, (String)object);
        }
    }

    /**
     *  F&uuml;hrt die &uuml;bergebenen Sektionen mit den bestehenden zusammen.
     *  Bereits vorhandene werden dabei aktualisiert, neue angelegt.
     *  @param initialize zu &uuml;bernehmende Sektionen
     */
    public void merge(Initialize initialize) {

        Enumeration enumeration;
        String      extend;
        String      stream;
        String      string;

        if (initialize == null) return;

        //die Sektionen werden ermittelt
        enumeration = initialize.elements();

        //die Eintraege werden einzeln uebernommen um ungueltige Eintraege
        //auszuschliessen und um die Sektionen optimieren zu koennen
        while (enumeration.hasMoreElements()) {

            string = (String)enumeration.nextElement();
            stream = (String)initialize.entries.get(string);
            extend = (String)initialize.resolve.get(string);

            this.set(string, stream, extend);
        }
    }

    /**
     *  R&uuml;ckgabe der Anzahl von Sektionen.
     *  @return die Anzahl von Sektionen
     */
    public int size() {

        return this.entries.size();
    }

    /** Setzt Initialize komplett zur&uuml;ck und verwirft alle Sektionen. */
    public void clear() {

        this.entries.clear();

        this.list.clear();
    }

    /**
     *  R&uuml;ckgabe einer Kopie von Initialize.
     *  @return eine Kopie von Initialize
     */
    public Object clone() {

        Initialize initialize;

        //Initialize wird eingerichtet
        initialize = new Initialize();

        //die Sektionen werden als Kopie uebernommen
        initialize.entries = (Hashtable)this.entries.clone();

        //die Ableitungen werden als Kopie uebernommen
        initialize.resolve = (Hashtable)this.resolve.clone();

        //die Liste der Sektionen wird als Kopie uebernommen
        initialize.list = (Vector)this.list.clone();

        return initialize;
    }

    /**
     *  R&uuml;ckgabe der formatierten Sektion als String. Der Zeilenumbruch
     *  erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Sektion als String
     */
    public String toString() {

        Enumeration     enumeration;
        String          buffer;
        String          context;
        String          extend;
        String          stream;
        String          string;
        StringTokenizer tokenizer;

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        context = System.getProperty("line.separator", "\r\n");

        //die Sektionen werden ermittelt
        enumeration = this.list.elements();

        for (stream = ""; enumeration.hasMoreElements();) {

            //der Name wird ermittelt umd im Stream gesetzt
            string = (String)enumeration.nextElement();
            stream = (stream.length() > 0) ? stream.concat(context) : stream;
            buffer = (string.length() > 0) ? ("[").concat(string).concat("]").concat(context) : "";
            extend = (string.length() > 0) ? (String)this.resolve.get(string) : "";

            buffer = (extend != null && extend.length() > 0) ? buffer.concat(" EXTENDS ").concat(extend) : buffer;

            stream = stream.concat(buffer);

            //der Inhalt der Sektion wird ermittelt
            buffer = ((String)this.entries.get(string)).trim();

            //die Zeilen der Sektion werden ermittelt
            tokenizer = new StringTokenizer(buffer, "\r\n");

            //der Stream wird Zeilenweise mit Einrueckung aufgebaut
            while (tokenizer.hasMoreTokens()) {

                string = tokenizer.nextToken().trim();
                string = string.startsWith(";") ? (" ").concat(string) : ("  ").concat(string);
                stream = stream.concat(string).concat(context);
            }
        }

        return stream;
    }
}