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
package com.seanox.network.ajax;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.seanox.common.Accession;
import com.seanox.common.Components;

/**
 *  Stream schreibt in der f&uuml;r Ajax g&uuml;nstigen JavaScript Syntax (JSON)
 *  <code>&#91;{"field":"value", "object":{...}, "map":&#91; {"key":"value"},
 *  {...}&#93;, &#91;entry, entry, ...&#93;}&#93;</code> Daten in einen
 *  Datenausgabestrom. Die Daten k&ouml;nnen einzeln, als Map oder als
 *  komplettes Objekt &uuml;bergeben werden. F&uuml;r den Transport werden diese
 *  optimiert, wobei alle relevanten Sonderzeichen automatische maskiert werden.
 *  Zudem kann mit der Option <code>safety</code> festgelegt werden, das alle
 *  Feldnamen validiert werden. F&uuml;r diese sind dann nur die Zeichen
 *  <code>A-Z a-z 0-9 .-_&#91;&#93;</code> zul&auml;ssig. Enth&auml;lt eine
 *  Feldname andere Zeichen, wird dieser nicht &uuml;bertragen. Ohne diese
 *  Option ist die Validierung deaktivert.<br>
 *  <br>
 *  Stream 1.2010.1130<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2010.1130
 */
public class Stream {

    /** Datenausgabestrom */
    private OutputStream output;

    /** Option f&uuml;r die Validierung der Feldnamen */
    private boolean safety;

    /** Status des Datentransfer */
    private int level;

    /**
     *  Konstruktor, richtet den Ajax Datenstrom ein.
     *  @param output  Datenstrom
     */
    public Stream(OutputStream output) {

        this(output, false);
    }

    /**
     *  Konstruktor, richtet den Ajax Datenstrom ein.
     *  @param output  Datenstrom
     *  @param safety  <code>true</code> aktiviert die Validierung von
     *                 Context und Feldnamen beim Datentransport
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public Stream(OutputStream output, boolean safety) {

        this.safety = safety;

        if (output == null) throw new IllegalArgumentException("OutputStream required");

        this.output = output;
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn der Feldname nur zul&auml;ssigen
     *  Zeichen (A-Z/a-z/0-9/./-/_/[/]) enth&auml;lt und mit A-Z/a-z beginnt.
     *  @param  name zu pr&uuml;fender Feldnae
     *  @return <code>true</code>, wenn ein g&uuml;ltiger Feldname vorliegt
     */
    private static boolean isValidFieldName(String name) {

        char digit;
        int  loop;
        int  size;

        //kein String wird wie ein leerer String behandelt
        if (name == null || name.length() == 0) return false;

        //die Laenge des String wird ermittelt
        size = name.length();

        //die einzelnen Zeichen werden geprueft,
        //zugelassen sind nur die ASCII A-Z/a-z/0-9/./-/_/[/]
        for (loop = 0; loop < size; loop++) {

            digit = name.charAt(loop);

            //der Feldname muss mit A-Z/a-z beginnen
            if (loop == 0 && !(digit >= 48 && digit <= 57 || digit >= 97 && digit <= 122)) return false;

            if (digit == 45 || digit == 46) continue;
            if (digit >= 48 && digit <= 57) continue;
            if (digit >= 65 && digit <= 90) continue;
            if (digit == 91 || digit == 93 || digit == 95) continue;
            if (digit >= 97 && digit <= 122) continue;

            return false;
        }

        return true;
    }

    /**
     *  Maskiert alle beim Transport relevanten Sonder- und Steuerzeichen.
     *  @param value zu optimierender Wert
     *  @return der f&uuml;r den Transport optimierte Wert
     */
    private static String optimizeValue(String value) {

        value = Components.strprn(value);
        value = Components.strset(value, "\"", "\\\"");
        value = Components.strset(value, "{", "\\{");
        value = Components.strset(value, "}", "\\}");

        return value;
    }

    /**
     *  Pr&uuml;ft rekursive das &uuml;bergebene Objekt auf Rekursionen.
     *  R&uuml;ckgabe <code>true</code> wenn diese m&ouml;glich sind, sonst wird
     *  <code>false</code> zur&uuml;ckgegeben.
     *  @param  object zu pr&uuml;fendes Objekt
     *  @param  stack  Liste der bereits ermittelten Objekte
     *  @return <code>true</code> wenn ein Rekursion ermittelt wurde
     */
    private static boolean isObjectRecursive(Object object, List stack) {

        Field    field;
        Iterator iterator;
        List     list;
        Object   entry;

        Field[]  fields;

        int      loop;

        if (object == null) return false;

        if (stack == null) stack = new ArrayList();

        if (stack.contains(object)) return true;

        //elementare Objektytpe ohne Rekursion werden geprueft
        if (object instanceof Boolean
                || object instanceof Byte
                || object instanceof Character
                || object instanceof Double
                || object instanceof Float
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Short
                || object instanceof String) return false;

        //das aktuelle Objket wird regisrtiert
        stack.add(object);

        list = new ArrayList(stack);

        //alle Felder werden ermittelt
        fields = Accession.getFields(object);

        for (loop = 0; loop < fields.length; loop++) {

            field = fields[loop];

            //der Wert vom Feld wird ermittelt
            try {entry = Accession.get(object, field.getName());
            } catch (Exception exception) {entry = null;}

            //die Rekursion wird rekursive geprueft
            if (Stream.isObjectRecursive(entry, list)) return true;
        }

        //Arrays werden in Listen aufgeloest
        if (object.getClass().isArray()) {

            try {entry = Arrays.asList((Object[])object);
            } catch (ClassCastException exception) {

                return false;
            }

            iterator = ((Collection)entry).iterator();

            while (iterator.hasNext()) {

                if (Stream.isObjectRecursive(iterator.next(), list)) return true;
            }
        }

        return false;
    }

    /**
     *  Schreibt die &uuml;bergebene Objektliste in den Datenstrom.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert. Diese
     *  werden somit nicht aufgel&ouml;st und in den Datenstrom geschrieben.
     *  @param  collection Werteliste
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void write(Collection collection) throws IOException {

        this.write(collection, null, this.level > 1, false);
    }

    /**
     *  Schreibt die &uuml;bergebene Objektliste in den Datenstrom.
     *  Mit <code>exclusions</code> k&ouml;nnen die Objekte, die ignoriert
     *  werden sollen, angegeben werden.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert. Diese
     *  werden somit nicht aufgel&ouml;st und in den Datenstrom geschrieben.
     *  @param  collection Werteliste
     *  @param  exclusions Liste der zu ignorierenden Objekte
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void write(Collection collection, Object[] exclusions) throws IOException {

        this.write(collection, exclusions, this.level > 1, false);
    }

    /**
     *  Schreibt die &uuml;bergebene Objektliste in den Datenstrom. Mit
     *  <code>exclusions</code> k&ouml;nnen die Objekte, die ignoriert werden
     *  sollen, angegeben werden.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert. Diese
     *  werden somit nicht aufgel&ouml;st und in den Datenstrom geschrieben.
     *  @param  collection Werteliste
     *  @param  exclusions Liste der zu ignorierenden Objekte
     *  @param  follow     <code>true</code> wenn es eine Folgesatz ist
     *  @param  assured    <code>true</code> wenn das Rekursionspotenzial
     *                     bereits gepr&uuml;ft wurde
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    private void write(Collection collection, Object[] exclusions, boolean follow, boolean assured) throws IOException {

        Iterator iterator;
        List     list;
        Object   entry;

        boolean  control;

        //gegebenenfalls wird Json initial eingerichtet
        if (this.level == 0) this.output.write(91);

        if (this.level == 0) this.level++;

        if (follow) this.output.write(44);

        if (collection == null) {

            this.output.write(("null").getBytes());

        } else {

            this.output.write(91);

            //die Liste der Ausschluesse wird ermittelt
            list = exclusions != null ? Arrays.asList(exclusions) : new ArrayList();

            iterator = collection.iterator();

            for (control = true; iterator.hasNext();) {

                entry = iterator.next();

                //ausgeschlossene Felder werden ignoriert
                if (exclusions != null && list.contains(entry)) continue;

                //das Object wird auf Rekursionen geprueft
                if (!assured && Stream.isObjectRecursive(entry, null)) continue;

                if (!control) this.output.write(44);

                this.write(entry, null, false, true);

                control = false;
            }

            this.output.write(93);
        }
    }

    /**
     *  Schreibt die &uuml;bergebene Werteliste in den Datenstrom.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert. Diese
     *  werden somit nicht aufgel&ouml;st und in den Datenstrom geschrieben.
     *  @param  map Werteliste
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void write(Map map) throws IOException {

        this.write(map, null, this.level > 1, false);
    }

    /**
     *  Schreibt die &uuml;bergebene Werteliste in den Datenstrom.
     *  Mit <code>exclusions</code> k&ouml;nnen die Schl&uuml;sselfelder, die
     *  ignoriert werden sollen, angegeben werden.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert. Diese
     *  werden somit nicht aufgel&ouml;st und in den Datenstrom geschrieben.
     *  @param  map        Werteliste
     *  @param  exclusions Liste der zu ignorierenden Werte
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void write(Map map, Object[] exclusions) throws IOException {

        this.write(map, exclusions, this.level > 1, false);
    }

    /**
     *  Schreibt die &uuml;bergebene Werteliste in den Datenstrom. Mit
     *  <code>exclusions</code> k&ouml;nnen die Schl&uuml;sselfelder, die
     *  ignoriert werden sollen, angegeben werden.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert. Diese
     *  werden somit nicht aufgel&ouml;st und in den Datenstrom geschrieben.
     *  @param  map        Werteliste
     *  @param  exclusions Liste der zu ignorierenden Werte
     *  @param  follow     <code>true</code> wenn es eine Folgesatz ist
     *  @param  assured    <code>true</code> wenn das Rekursionspotenzial
     *                     bereits gepr&uuml;ft wurde
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    private void write(Map map, Object[] exclusions, boolean follow, boolean assured) throws IOException {

        Iterator iterator;
        List     list;
        Object   entry;
        Object   field;
        String   string;

        boolean  control;

        //gegebenenfalls wird Json initial eingerichtet
        if (this.level == 0) this.output.write(91);

        if (this.level == 0) this.level++;

        if (follow) this.output.write(44);

        if (map == null) {

            this.output.write(("null").getBytes());

        } else {

            this.output.write(123);

            //die Liste der Ausschluesse wird ermittelt
            list = exclusions != null ? Arrays.asList(exclusions) : new ArrayList();

            iterator = map.keySet().iterator();

            for (control = true; iterator.hasNext();) {

                field = iterator.next();

                //ausgeschlossene Felder werden ignoriert
                if (exclusions != null && list.contains(field)) continue;

                //das Feld wird registriert
                list.add(field);

                string = String.valueOf(field);

                //Felder mit ungueltigem Namen werden ignoriert
                if (this.safety && !Stream.isValidFieldName(string)) continue;

                //das Feld wird auf Rekursionen geprueft
                if (!assured && Stream.isObjectRecursive(field, null)) continue;

                entry = map.get(field);

                //das Object wird auf Rekursionen geprueft
                if (!assured && Stream.isObjectRecursive(entry, null)) continue;

                if (!control) this.output.write(44);

                this.output.write(("\"").concat(string).concat("\":").getBytes());

                this.write(entry, null, false, true);

                control = false;
            }

            this.output.write(125);
        }
    }

    /**
     *  Schreibt die &ouml;ffentlichen Felder des &uuml;bergeben Objekts in den
     *  Datenstrom. Mit <code>exclusions</code> k&ouml;nnen die Namen der Felder
     *  die ignoriert werden sollen, angegeben werden.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert.
     *  @param  object Datenobjekt
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void write(Object object) throws IOException {

        this.write(object, null, this.level > 1, false);
    }

    /**
     *  Schreibt die &ouml;ffentlichen Felder des &uuml;bergeben Objekts in den
     *  Datenstrom. Mit <code>exclusions</code> k&ouml;nnen die Namen der Felder
     *  die ignoriert werden sollen, angegeben werden.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert.
     *  @param  object     Datenobjekt
     *  @param  exclusions Liste der zu ignorierenden Werte
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void write(Object object, Object[] exclusions) throws IOException {

        this.write(object, exclusions, this.level > 1, false);
    }

    /**
     *  Schreibt die &ouml;ffentlichen Felder des &uuml;bergeben Objekts in den
     *  Datenstrom. Mit <code>exclusions</code> k&ouml;nnen die Namen der Felder
     *  die ignoriert werden sollen, als Strings angegeben werden.<br><br>
     *  <b>Hinweis</b> - Das aufl&ouml;sen der Objekte erfolgt mit Reflections,
     *  wobei auch alle &ouml;ffentlich zug&auml;nglichen Felder der
     *  Unterobjekte rekrusive aufgel&ouml;st werden. Objekte mit
     *  Selbstrekursionspotenzial werden dabei erkannt und ignoriert.
     *  @param  object     Datenobjekt
     *  @param  exclusions Liste der zu ignorierenden Werte
     *  @param  follow     <code>true</code> wenn es eine Folgesatz ist
     *  @param  assured    <code>true</code> wenn das Rekursionspotenzial
     *                     bereits gepr&uuml;ft wurde
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    private void write(Object object, Object[] exclusions, boolean follow, boolean assured) throws IOException {

        Field   field;
        List    list;
        Object  access;
        Object  entry;
        String  string;

        Field[] fields;

        boolean control;
        int     cursor;

        //gegebenenfalls wird Json initial eingerichtet
        if (this.level == 0) this.output.write(91);

        if (this.level == 0) this.level++;

        if (object instanceof Collection) {this.write((Collection)object, exclusions); return;}
        if (object instanceof Map) {this.write((Map)object, exclusions); return;}

        if (object != null && object.getClass().isArray()) {this.write(Arrays.asList((Object[]) object), exclusions); return;}

        if (follow) this.output.write(44);

        if (object == null) {

            this.output.write(("null").getBytes());

        } else if (object instanceof Boolean
                || object instanceof Byte
                || object instanceof Character
                || object instanceof Double
                || object instanceof Float
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Short) {

            this.output.write(String.valueOf(object).getBytes());

        } else if (object instanceof String) {

            string = String.valueOf(object);
            string = Stream.optimizeValue(string);
            string = ("\"").concat(string).concat("\"");

            this.output.write(string.getBytes());

        } else {

            this.output.write(123);

            //die Liste der Ausschluesse wird ermittelt
            list = exclusions != null ? Arrays.asList(exclusions) : new ArrayList();

            //alle Felder werden ermittelt
            fields = Accession.getFields(object);

            for (cursor = 0, control = true; cursor < fields.length; cursor++) {

                field = fields[cursor];

                //die Art des Zugriffs wird ermittelt
                access = Stream.getPublicFieldAccess(object, field);

                //nur oeffentlich zugaengliche Felder werden beruecksichtigt
                if (access == null || !(access instanceof Field || access instanceof Method)) continue;

                //ausgeschlossene Felder werden ignoriert
                if (exclusions != null && list.contains(field.getName())) continue;

                //das Feld wird registriert
                list.add(field.getName());

                //der Wert vom Feld wird ermittelt
                try {entry = (access instanceof Method) ? Accession.invoke(object, ((Method)access).getName()) : Accession.get(object, field.getName());
                } catch (Exception exception) {entry = null;}

                //Felder mit ungueltigem Namen werden ignoriert
                if (this.safety && !Stream.isValidFieldName(field.getName())) continue;

                //das Object wird auf Rekursionen geprueft
                if (!assured && Stream.isObjectRecursive(entry, null)) continue;

                if (!control) this.output.write(44);

                this.output.write(("\"").concat(field.getName()).concat("\":").getBytes());

                this.write(entry, null, false, true);

                control = false;
            }

            this.output.write(125);
        }
    }

    /**
     *  R&uuml;ckgabe des Feldzugriffs. Die R&uuml;ckgabe erfolgt bei als
     *  Methode, Feld oder <code>null</code>, wenn kein &ouml;ffentlicher
     *  Zugriff auf das Feld m&ouml;gloch ist. Vorzugsweise erfolg der Zugriff
     *  &uuml;ber eine f&uuml;r das Feld implemtierte Get bzw. bei boolischen
     *  Werten auch die Is Methode. Alternativ wird auch bei &ouml;ffentlichen
     *  Felder direkt zugegriffen wenn diese keine separate Zugriffsmethode
     *  bereitstellen.
     *  @param  object Zugriffobjekt
     *  @param  field  Feld
     *  @return der Feldzugriff als Methode, Feld oder <code>null</code>
     */
    private static Object getPublicFieldAccess(Object object, Field field) {

        Method  method;
        String  string;

        boolean elemental;
        boolean primitive;
        int     modifiers;

        if (field == null) return null;

        modifiers = field.getType().getModifiers();
        elemental = Modifier.isFinal(modifiers);
        modifiers = field.getModifiers();
        primitive = field.getType().isPrimitive();

        //Konstanten sind final Klassen oder einfache Datentypen, statisch und
        //nicht aenderbar, diese werden nicht ausgegeben
        if ((primitive || elemental) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) return null;

        string = field.getName().concat(" ");
        string = string.substring(0, 1).toUpperCase().concat(string.substring(1)).trim();

        if (string.length() == 0) return null;

        method = Accession.getMethod(object, ("get").concat(string), null);

        if (method != null && Modifier.isPublic(method.getModifiers())) return method;

        if (field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class)) {

            method = Accession.getMethod(object, ("is").concat(string), null);

            if (method != null && Modifier.isPublic(method.getModifiers())) return method;
        }

        if (Modifier.isPublic(modifiers)) return field;

        return null;
    }

    /**
     *  Gibt eventuell zwischengespeicherte Daten in Datenstrom aus.
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void flush() throws IOException {

        this.output.flush();
    }

    /**
     *  Schliesst den Datenausgabestrom.
     *  @throws IOException bei fehlerhaftem Zugriff auf den Datenstrom
     */
    public void close() throws IOException {

        this.output.write(93);
        this.output.flush();
    }
}