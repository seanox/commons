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

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 *  Sequence stellt eine Datenhaltung mit einer nach dem FIFO (first in first
 *  out) Prinzip funktionierenden Mengenkontrolle f&uuml;r Schl&uuml;sselobjekte
 *  mit entsprechend zugeordneten Datenobjekten zur Verf&uuml;gung. Ein
 *  Datensatz wird dabei mit jedem Zugriff auf das entsprechenden
 *  Schl&uuml;sselobjekt am Ende der Datenhaltung eingetragen. Wird dabei die
 *  Mengenbegrenzung erreicht wird jeweils der erste und somit der am
 *  l&auml;ngsten nicht verwendete Datensatz aus der Datenhaltung entfernt.<br>
 *  <br>
 *  Sequence 1.2009.0926<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2009.0926
 */
public class Sequence implements Cloneable, Serializable, Map {

    /** Tabelle der Schl&uuml;ssel- und Datenobjekte */
    private volatile Hashtable entries;

    /** Liste der Schl&uuml;sseleintr&auml;ge */
    private volatile Vector stack;

    /** Mengenbegrenzung der Eintr&auml;ge */
    private volatile int volume;

    /** Versionskennung f&uuml;r die Serialisierung */
    private static final long serialVersionUID = -5955089422988437208L;

    /** Konstruktor, richtet Sequence ein. */
    public Sequence() {

        this(null, 65535);
    }

    /**
     *  Konstruktor, richtet Sequence mit der angegebenen Mengenbegrenzung ein.
     *  Bei der Verwendung ung&uuml;ltiger Mengenbegrenzung, also Werten &#60; 1
     *  wird der Standard von 65535 verwendet.
     *  @param volume maximale Anzahl von Datens&auml;tzen
     */
    public Sequence(int volume) {

        this(null, volume);
    }

    /**
     *  Konstruktor, richtet Sequence mit den Schl&uuml;ssel- und
     *  Dateneintr&auml;gen der &uuml;bergebenen Map ein.<br><br>
     *  <b>Hinweis</b> - Die &Uuml;bernahme erfolgt unter Ber&uuml;cksichtigung
     *  der Standard Mengenbegrenzung von 65535 Eintr&auml;gen nach dem FIFO
     *  Prinzip. Somit werden alle Eintr&auml;ge in der &uuml;bergebenen
     *  Reihenfolge &uuml;bernommen, wird dabei die Standard Mengenbegrenzung
     *  &uuml;berschritten, wird f&uuml;r jeden weiteren &uuml;bernommenen
     *  Eintrag der erste Eintrag entfernt.
     *  @param map zu &uuml;bernehmende Sequence Eintr&auml;ge.
     */
    public Sequence(Map map) {

        this(map, 65535);
    }

    /**
     *  Konstruktor, richtet Sequence mit den Schl&uuml;ssel- und
     *  Dateneintr&auml;gen der &uuml;bergebenen Map ein. Bei der Verwendung
     *  ung&uuml;ltiger Mengenbegrenzung, also Werten &#60; 1 wird der Standard
     *  von 65535 verwendet.<br><br>
     *  <b>Hinweis</b> - Die &Uuml;bernahme erfolgt unter Ber&uuml;cksichtigung
     *  der Standard Mengenbegrenzung von 65535 Eintr&auml;gen nach dem FIFO
     *  Prinzip. Somit werden alle Eintr&auml;ge in der &uuml;bergebenen
     *  Reihenfolge &uuml;bernommen, wird dabei die Standard Mengenbegrenzung
     *  &uuml;berschritten, wird f&uuml;r jeden weiteren &uuml;bernommenen
     *  Eintrag der erste Eintrag entfernt.
     *  @param map    zu &uuml;bernehmende Sequence Eintr&auml;ge.
     *  @param volume maximale Anzahl von Datens&auml;tzen
     */
    public Sequence(Map map, int volume) {

        //die Datenhaltung und der Stack werden eingerichtet
        this.entries = new Hashtable();
        this.stack   = new Vector();

        //die Mengenbegrenzung wird gesetzt
        this.volume = (volume < 1) ? 65535 : volume;

        //die Eintraege werden uebernommen
        this.putAll(map);
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn die Datentabelle keinen Eintrag
     *  enth&auml;lt sonst wird <code>false</code> zur&uuml;ckgegeben.
     *  @return <code>true</code>, wenn kein Eintrag enth&auml;lt ist
     */
    public synchronized boolean isEmpty() {

        return this.entries.isEmpty();
    }

    /**
     *  Liefert die momentane Anzahl der Eintr&auml;ge in der Datentabelle.
     *  @return die momentane Anzahl der Eintr&auml;ge in der Datentabelle
     */
    public synchronized int size() {

        return this.entries.size();
    }

    /**
     *  R&uuml;ckgabe der aktuellen Anzahl von zu haltenden Datens&auml;tzen.
     *  @return die aktuelle Anzahl von zu haltenden Datens&auml;tzen
     */
    public synchronized int getVolume() {

        return this.volume;
    }

    /**
     *  Registriert den Zugriff auf das angebene Schl&uuml;sselobjekt. Damit
     *  wird dieses im Stack am Ende eingetragen.
     *  @param key Schl&uuml;sselobjekt
     */
    private synchronized void registerKey(Object key) {

        int cursor;

        //der Eintrag wird im Stack entfernt
        if ((cursor = this.stack.indexOf(key)) >= 0) this.stack.remove(cursor);

        //das Schluesselobjekt wird am Ende des Stacks eingetragen
        this.stack.addElement(key);
    }

    /**
     *  Setzt das Datenvolumen f&uuml;r die Mengenbegrenzung.<br><br>
     *  <b>Hinweis</b> - Liegt der neue Wert unter der Anzahl aktuell gehaltener
     *  Datens&auml;tze werden diese nach dem FIFO Prinzip verk&uuml;rzt. Dazu
     *  werden die ersten und damit &auml;ltesten Eintr&auml;ge entfernt.
     *  @param volume maximale Anzahl von Datens&auml;tzen
     */
    public synchronized void setVolume(int volume) {

        //die Mengenbegrenzung wird gesetzt
        this.volume = (volume < 1) ? 65535 : volume;

        //die ueberschuessigen Datensaetze werden entfernt
        while (this.stack.size() > this.volume) this.remove(this.stack.elementAt(0));
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn das Schl&uuml;sselobjekt in der
     *  Datentabelle enthalten ist, sonst <code>false</code>.
     *  @param  key gesuchtes Datenobjekt
     *  @return <code>true</code>, wenn das Schl&uuml;sselobjekt enthalten ist
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public synchronized boolean containsKey(Object key) {

        if (key == null) throw new IllegalArgumentException("Invalid key [null]");

        //der Zugriff wird im Stack registiert
        this.registerKey(key);

        return this.entries.containsKey(key);
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, falls das Datenobjekt als Wert
     *  mindestens eines Eintrags vorkommt, sonst <code>false</code>.
     *  @param  value gesuchtes Datenobjekt
     *  @return <code>true</code>, wenn das Datenobjekt enthalten ist
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public synchronized boolean containsValue(Object value) {

        if (value == null) throw new IllegalArgumentException("Invalid value [null]");

        return this.entries.containsValue(value);
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn das Datenobjekt in der
     *  Datentabelle enthalten ist, sonst <code>false</code>.
     *  @param  value gesuchtes Datenobjekt
     *  @return <code>true</code>, wenn das Datenobjekt enthalten ist
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public synchronized boolean contains(Object value) {

        if (value == null) throw new IllegalArgumentException("Invalid value [null]");

        return this.entries.contains(value);
    }

    /**
     *  R&uuml;ckgabe vom dem Schl&uuml;sselobjekt zugeordneten Datenobjekt.
     *  Kann keines ermittelt werden wird <code>null</code> zur&uuml;ckgegeben.
     *  @param  key Schl&uuml;sselobjekt
     *  @return das zugeordneten Datenobjekt, sonst <code>null</code>
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public synchronized Object get(Object key) {

        if (key == null) throw new IllegalArgumentException("Invalid key [null]");

        //der Zugriff wird im Stack registiert
        this.registerKey(key);

        return this.entries.get(key);
    }

    /**
     *  &Uuml;bernimmt alle Schl&uuml;ssel- und Datenobjekt der &uuml;bgebenen
     *  Map.<br><br>
     *  <b>Hinweis</b> - Die &Uuml;bernahme der Daten erfolgt nach dem FIFO
     *  Prinzip. Dabei wird der angegebene Schl&uuml;ssel an das Ende der
     *  Mengenkontrolle gesetzt. Mit dem Erreichen der Mengenbegrenzung wird
     *  zuvor der erste Eintrag und somit der &auml;lteste unge&auml;nderte aus
     *  der Mengenkontrolle entfernt. Diese Zuordnung erfolgt jedoch
     *  unabh&auml;ngig von der gehaltenen Datentabelle. Bei der Ermittlung
     *  aller eingetragenen Schl&uuml;ssel- und Datenobjekt werden diese in
     *  ihrer Reihenfolge unabh&auml;ngig der Platzierung zur&uuml;ckgegeben.
     *  @param  map Bezugsdaten
     */
     public synchronized void putAll(Map map) {

        Iterator iterator;
        Object   key;

        //leere Mappen werden nicht beruecksichtigt
        if (map == null) return;

        //die Schluesselobjekte werden ermittelt
        iterator = map.keySet().iterator();

        while (iterator.hasNext()) {

            //das Schluessel- und Datenobjekt werden ermittelt
            key = iterator.next();

            //das Schluessel- und Datenobjekt werden uebernommen
            this.put(key, map.get(key));
        }
    }

    /**
     *  &Uuml;bernimmt das Schl&uuml;ssel- und Datenobjekt. R&uuml;ckgabe
     *  <code>null</code>, wenn der Schl&uuml;ssel bisher nicht vorhanden war,
     *  ansonsten wird das Datenobjekt, das diesem zuvor zugeordnet war
     *  zur&uuml;ckgegeben.<br><br>
     *  <b>Hinweis</b> - Die &Uuml;bernahme der Daten erfolgt nach dem FIFO
     *  Prinzip. Dabei wird der angegebene Schl&uuml;ssel an das Ende der
     *  Mengenkontrolle gesetzt. Mit dem Erreichen der Mengenbegrenzung wird
     *  zuvor der erste Eintrag und somit der &auml;lteste unge&auml;nderte aus
     *  der Mengenkontrolle entfernt. Diese Zuordnung erfolgt jedoch
     *  unabh&auml;ngig von der gehaltenen Datentabelle. Bei der Ermittlung
     *  aller eingetragenen Schl&uuml;ssel- und Datenobjekt werden diese in
     *  ihrer Reihenfolge unabh&auml;ngig der Platzierung zur&uuml;ckgegeben.
     *  @param  key   Schl&uuml;sselobjekt
     *  @param  value Datenobjekt
     *  @return das zuvor zugeordnet Datenobjekt, sonst <code>null</code>
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public synchronized Object put(Object key, Object value) {

        //der Wert [null] ist fuer Key und Value nicht zulaessig
        if (key == null) throw new IllegalArgumentException("Invalid key [null]");
        if (value == null) throw new IllegalArgumentException("Invalid value [null]");

        //wurde die Mengenbegrenzung erreicht, wird der Stack nach dem FIFO
        //Prinzip verschoben, dabei wird das erste Element entfernt bzw.
        //an das Ende des Stacks verschoben
        if (!this.entries.containsKey(key) && this.stack.size() >= this.volume) {

            //das erste Element wird aus der Datentabelle entfernt
            this.entries.remove(this.stack.get(0));

            //das erste Element des Stacks wird entfernt
            this.stack.remove(0);
        }

        //der Zugriff wird im Stack registiert
        this.registerKey(key);

        //das Schluessel- und Datenobjekt werden uebernommen
        return this.entries.put(key, value);
    }

    /**
     *  Entfernt das Schl&uuml;sselobjekt und das entsprechende Datenobjekt aus
     *  der Datentabelle. R&uuml;ckgabe das zum Schl&uuml;sselobjekt
     *  geh&ouml;rige Datenobjekt, sonst <code>null</code>.
     *  @param  key Schl&uuml;sselobjekt
     *  @return das zum Schl&uuml;sselobjekt geh&ouml;rige Datenobjekt
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public synchronized Object remove(Object key) {

        if (key == null) throw new IllegalArgumentException("Invalid key [null]");

        this.stack.remove(key);

        return this.entries.remove(key);
    }

    /**
     *  R&uuml;ckgabe aller enthaltenen Schl&uuml;sselobjekte.<br><br>
     *  <b>Hinweis</b> - Die Reihenfolge ist hierbei unabh&auml;ngig von der
     *  Sortierung der Mengenkontrolle.
     *  @return alle enthaltenen Schl&uuml;sselobjekte als Enumeration
     */
    public synchronized Enumeration keys() {

        return this.entries.keys();
    }

    /**
     *  R&uuml;ckgabe aller enthaltenen Schl&uuml;sselobjekte.<br><br>
     *  <b>Hinweis</b> - Die Reihenfolge ist hierbei unabh&auml;ngig von der
     *  Sortierung der Mengenkontrolle.
     *  @return alle enthaltenen Schl&uuml;sselobjekte als Set
     */
    public synchronized Set keySet() {

        return this.entries.keySet();
    }

    /**
     *  R&uuml;ckgabe der enthaltenen Datenobjekte.<br><br>
     *  <b>Hinweis</b> - Die Reihenfolge ist hierbei unabh&auml;ngig von der
     *  Sortierung der Mengenkontrolle.
     *  @return alle enthaltenen Datenobjekte als Enumeration
     */
    public synchronized Enumeration elements() {

        return this.entries.elements();
    }

    /**
     *  R&uuml;ckgabe der enthaltenen Datenobjekte.<br><br>
     *  <b>Hinweis</b> - Die Reihenfolge ist hierbei unabh&auml;ngig von der
     *  Sortierung der Mengenkontrolle.
     *  @return alle enthaltenen Datenobjekte als Collection
     */
    public Collection values() {

        return this.entries.values();
    }

    /**
     *  R&uuml;ckgabe aller enthaltenen Schl&uuml;ssel/Daten Paare.<br><br>
     *  <b>Hinweis</b> - Die Reihenfolge ist hierbei unabh&auml;ngig von der
     *  Sortierung der Mengenkontrolle.
     *  @return alle enthaltenen Schl&uuml;ssel/Daten Paare als Set
     */

    public synchronized Set entrySet() {

        return this.entries.entrySet();
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn die Daten der Mengenkontrolle mit
     *  denen dieser Sequence &uuml;bereinstimmen, sonst <code>false</code>.
     *  @return <code>true</code> wenn die Sequences &uuml;bereinstimmen.
     */
    public synchronized boolean equals(Object object) {

        Sequence sequence;

        if (object == null || !(object instanceof Sequence)) return false;

        sequence = (Sequence)object;

        if (!this.entries.equals(sequence.entries)) return false;
        if (!this.stack.equals(sequence.stack)) return false;
        if (this.volume != sequence.volume) return false;

        return true;
    }

    /** L&ouml;scht alle Eintr&auml;ge aus der Datentabelle. */
    public synchronized void clear() {

        //die Datenhaltung wird zurueckgesetzt
        this.entries.clear();

        //die Mengenkontrolle wird zurueckgesetzt
        this.stack.clear();
    }

    /**
     *  Erzeugt ein Kopie der Sequence.
     *  @return ein Kopie der Sequence
     */
    public synchronized Object clone() {

        Sequence sequence = new Sequence();

        sequence.entries = (Hashtable)this.entries.clone();
        sequence.stack   = (Vector)this.stack.clone();
        sequence.volume  = this.volume;

        return sequence;
    }

    /**
     *  Liefert einen String, der eine Aneinanderreihung aller enthaltenen
     *  Schl&uuml;ssel- und Datenobjekte als String. Die Reihenfolge ist hierbei
     *  unabh&auml;ngig von der Sortierung der Mengenkontrolle.
     *  @return alle enthaltenen Schl&uuml;ssel- und Datenobjekte als String
     */
    public synchronized String toString() {

        return this.entries.toString();
    }
}