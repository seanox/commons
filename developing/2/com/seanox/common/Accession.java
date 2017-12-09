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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *  Accession stellt Zugriffsmethoden auf Konstruktoren, Felder und Methoden
 *  eines beliebigen Java-Objekts zur Verf&uuml;gung, auch wenn diese nicht
 *  &ouml;ffentlich zug&auml;nglich sind. Scheiternde Zugriffe und Freigaben
 *  k&ouml;nnen zu verschiedenen Ausnahme f&uuml;hren.<br>
 *  <br>
 *  Accession 1.2010.1130<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2010.1130
 */
public class Accession {

    /** Konstante f&uuml;r den Abgleichtyp Unbekannt */
    public static final int UNKNOWN = 0;

    /** Konstante f&uuml;r den Abgleichtyp Export */
    public static final int EXPORT = 1;

    /** Konstante f&uuml;r den Abgleichtyp Import */
    public static final int IMPORT = 2;

    /** Konstruktor, richtet das Accession-Objekt ein. */
    private Accession() {

        return;
    }

    /**
     *  Ermittelt den angegebenen Konstruktor, auch nicht &ouml;ffentliche.
     *  R&uuml;ckgabe der angegebene Konstruktor oder <code>null</code> wenn
     *  dieser nicht ermittelt oder freigegeben werden kann.
     *  @param  object Bezugsobjekt
     *  @param  types  Typenklassen der Argumente als Array
     *  @return der angegebene Konstruktor, sonst <code>null</code>
     */
    public static Constructor getConstructor(Object object, Class[] types) {

        Class       source;
        Constructor constructor;

        source = (object instanceof Class) ? (Class)object : object.getClass();

        //der Konstruktor wird ermittelt und freigegeben
        try {(constructor = source.getDeclaredConstructor(types)).setAccessible(true);
        } catch (Exception exception) {

            constructor = null;
        }

        return constructor;
    }

    /**
     *  Ermittelt alle Konstruktoren, auch nicht &ouml;ffentliche. R&uuml;ckgabe
     *  aller ermittelter Konstruktoren als Array. Kann kein Konstruktor
     *  ermittelt werden, wird eine leeres Array zur&uuml;ckgegeben.
     *  @param  object Bezugsobjekt
     *  @return die ermittelten Konstruktoren als Array
     */
    public static Constructor[] getConstructors(Object object) {

        Class         source;
        Constructor   constructor;
        List          result;

        Constructor[] constructors;

        int           loop;

        source = (object instanceof Class) ? (Class)object : object.getClass();

        constructors = source.getDeclaredConstructors();

        for (loop = 0, result = new ArrayList(); loop < constructors.length; loop++) {

            //das Feld wird ermittelt und freigegeben
            try {(constructor = constructors[loop]).setAccessible(true);
            } catch (Exception exception) {

                continue;
            }

            //das Feld wird uebernommen
            result.add(constructor);
        }

        return (Constructor[])result.toArray(new Constructor[0]) ;
    }

    /**
     *  Ermittelt das angegebene Feld, auch nicht &ouml;ffentliche oder wenn es
     *  sich in einer verwendeten Superklasse befindet. R&uuml;ckgabe das
     *  angegebene Feld oder <code>null</code> wenn dieses nicht ermittelt oder
     *  freigegeben werden kann.
     *  @param  object Bezugsobjekt
     *  @param  name   Name des Feldes
     *  @return das angegebene Feld, sonst <code>null</code>
     */
    public static Field getField(Object object, String name) {

        Class sheet;
        Field field;
        List  list;

        if (name == null || (name = name.trim()).length() == 0) return null;

        sheet = (object instanceof Class) ? (Class)object : object.getClass();

        for (field = null, list = new ArrayList(); field == null && sheet != null; sheet = sheet.getSuperclass()) {

            if (list.contains(sheet)) continue;

            list.add(sheet);

            //das Feld wird ermittelt und freigegeben
            try {(field = sheet.getDeclaredField(name)).setAccessible(true);
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }

        return field;
    }

    /**
     *  Ermittelt alle Felder, auch nicht &ouml;ffentliche oder es sich in einer
     *  verwendeten Superklasse befindet. R&uuml;ckgabe aller ermittelten Felder
     *  als Array. Kann kein Feld ermittelt werden, wird eine leeres Array
     *  zur&uuml;ckgegeben.
     *  @param  object Bezugsobjekt
     *  @return die ermittelten Felder als Array
     */
    public static Field[] getFields(Object object) {

        Class   sheet;
        List    result;
        Field   field;

        Field[] fields;

        int     loop;

        sheet = (object instanceof Class) ? (Class)object : object.getClass();

        for (result = new ArrayList(); sheet != null;) {

            fields = sheet.getDeclaredFields();

            for (loop = 0; loop < fields.length; loop++) {

                //das Feld wird ermittelt und freigegeben
                try {(field = fields[loop]).setAccessible(true);
                } catch (Exception exception) {

                    continue;
                }

                //das Feld wird uebernommen
                result.add(field);
            }

            sheet = sheet.getSuperclass();
        }

        return (Field[])result.toArray(new Field[0]) ;
    }

    /**
     *  Ermittelt die angegebene Methode, auch nicht &ouml;ffentliche oder diese
     *  sich in einer verwendeten Superklasse befindet. R&uuml;ckgabe die
     *  angegebene Methode oder <code>null</code> wenn diese nicht ermittelt
     *  oder freigegeben werden kann.
     *  @param  object Bezugsobjekt
     *  @param  name   Name der Methode
     *  @param  types  Typenklassen der Argumente als Array
     *  @return die angegebene Methode, sonst <code>null</code>
     */
    public static Method getMethod(Object object, String name, Class[] types) {

        Class  sheet;
        Method method;

        //der Name wird bereinigt
        name  = (name == null) ? "" : name.trim();

        sheet = (object instanceof Class) ? (Class)object : object.getClass();

        for (method = null; method == null && sheet != null; sheet = sheet.getSuperclass()) {

            //die Methode wird ermittelt und freigegeben
            try {(method = sheet.getDeclaredMethod(name, types)).setAccessible(true);
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }

        return method;
    }

    /**
     *  Ermittelt alle Methoden, auch nicht &ouml;ffentliche oder diese sich
     *  in einer verwendeten Superklasse befindet. R&uuml;ckgabe aller
     *  ermittelter Methoden als Array. Kann keine Methode ermittelt werden,
     *  wird eine leeres Array zur&uuml;ckgegeben.
     *  @param  object Bezugsobjekt
     *  @return die ermittelten Methode als Array
     */
    public static Method[] getMethods(Object object) {

        Class    sheet;
        List     result;
        Method   method;

        Method[] methods;

        int      loop;

        sheet = (object instanceof Class) ? (Class)object : object.getClass();

        for (result = new ArrayList(); sheet != null;) {

            methods = sheet.getDeclaredMethods();

            for (loop = 0; loop < methods.length; loop++) {

                //die Methode wird ermittelt und freigegeben
                try {(method = methods[loop]).setAccessible(true);
                } catch (Exception exception) {

                    continue;
                }

                //die Methode wird uebernommen
                result.add(method);
            }

            sheet = sheet.getSuperclass();
        }

        return (Method[])result.toArray(new Method[0]) ;
    }

    /**
     *  Ermittelt die vererbte Klassenhierarchie. Kann keine ermittelt werden,
     *  wird ein leeres Array zur&uuml;ckgegeben. Wird <code>null</code>
     *  &uuml;bergeben, wird auch <code>null</code> zur&uuml;ckgegeben.
     *  @param  source zu analysierende Klasse oder Objekt
     *  @return die ermittelt Klassenhierarchie der Vererbung, kann keine
     *          ermittelt werden, wird ein leeres Array zur&uuml;ckgegeben
     */
    public static Class[] getClassHerachie(Object source) {

        return Accession.getClassHerachie(source, false);
    }

    /**
     *  Ermittelt die vererbte Klassenhierarchie. Kann keine ermittelt werden,
     *  wird ein leeres Array zur&uuml;ckgegeben. Wird <code>null</code>
     *  &uuml;bergeben, wird auch <code>null</code> zur&uuml;ckgegeben.
     *  @param  source  zu analysierende Klasse oder Objekt
     *  @param  reverse Option <code>true</code> um die Klassenhierarchie zu
     *          drehen, diese beginnt dann mit der Super-Klasse
     *  @return die ermittelt Klassenhierarchie der Vererbung, kann keine
     *          ermittelt werden, wird ein leeres Array zur&uuml;ckgegeben
     */
    public static Class[] getClassHerachie(Object source, boolean reverse) {

        Class sheet;
        List  classes;

        if (source == null) return null;

        if (!source.getClass().equals(Class.class)) source = source.getClass();

        classes = new ArrayList();

        if (source != null) {

            for (sheet = (Class)source; sheet != null;) {

                classes.add(sheet);

                sheet = sheet.getSuperclass();
            }
        }

        if (reverse) Collections.reverse(classes);

        return (Class[])classes.toArray(new Class[0]);
    }

    /**
     *  Synchronisiert das angebebene Feld beider Objekte, auch wenn diese nicht
     *  &ouml;ffentlich sind (<code>Source.&#91;name&#93; -&gt;
     *  Targer.&#91;name&#93;</code>).
     *  @param  source Objekt des zu &uuml;bernehmenden Felds
     *  @param  target Objekt des zu setzenden Felds
     *  @param  name   Name des z synchronisierenden Felds
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     */
    public static void storeField(Object source, Object target, String name)
        throws IllegalAccessException {

        if (source == null) throw new IllegalArgumentException("Invalid source [null]");
        if (name == null) throw new IllegalArgumentException("Invalid name [null]");
        if (target == null) throw new IllegalArgumentException("Invalid target [null]");

        Accession.storeField(source, target, name, Accession.EXPORT);
    }

    /**
     *  Synchronisiert das angegebene Feld beider Objekte im angegebenen Modus,
     *  auch nicht &ouml;ffentliche. Im Modus <code>EXPORT</code> wird der
     *  Inhalt des angegebenen Felds des Accessions Objekts in das gleichnamige
     *  Feld des einbezogenen Objekts &uuml;bernommen
     *  (<code>Accession.&#91;name&#93; -&gt; Object.[name]</code>). Der Modus
     *  <code>IMPORT</code> verf&auml;hrt genau entgegengesetzt, hier wird der
     *  Inhalt des Objektfelds in das gleichnamige Feld vom Accessions Objekt
     *  &uuml;bernommen (<code>Object.&#91;name&#93; -&gt;
     *  Accession.&#91;name&#93;</code>).
     *  @param  object Bezugsobjekt
     *  @param  other  Objekt dessen Feld einbezogen werden soll
     *  @param  name   Feldname des einbezogenen Objekts
     *  @param  mode   Modus der Synchronisation
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     *  @see    #EXPORT
     *  @see    #IMPORT
     */
    public static void storeField(Object object, Object other, String name, int mode)
        throws IllegalAccessException {

        if (other == null) throw new IllegalArgumentException("Invalid object [null]");
        if (name == null) throw new IllegalArgumentException("Invalid name [null]");

        Accession.storeField(object, other, name, name, mode);
    }

    /**
     *  Synchronisiert die angegebenen Felder der Objekte im angegebenen Modus,
     *  auch nicht &ouml;ffentliche. Im Modus <code>EXPORT</code> wird der
     *  Inhalt des angegebenen Felds des Accessions Objekts in das gleichnamige
     *  Feld des einbezogenen Objekts &uuml;bernommen
     *  (<code>Accession.&#91;store&#93; -&gt; Object.&#91;name&#93;</code>).
     *  Der Modus <code>IMPORT</code> verf&auml;hrt genau entgegengesetzt, hier
     *  wird der Inhalt des Objektfeldes in das gleichnamige Feld vom Accessions
     *  Objekt &uuml;bernommen (<code>Object.&#91;name&#93; -&gt;
     *  Accession.&#91;store&#93;</code>).
     *  @param  object Bezugsobjekt
     *  @param  other  Objekt dessen Feld einbezogen werden soll
     *  @param  name   Feldname vom einbezogenen Objekt
     *  @param  store  Feldname vom Bezugsobjekt
     *  @param  mode   Modus der Synchronisation
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws IllegalArgumentException bei der Angabe von <code>null</code>
     *          oder einem umg&uuml;ltigen Modus
     *  @see    #EXPORT
     *  @see    #IMPORT
     */
    public static void storeField(Object object, Object other, String name, String store, int mode)
        throws IllegalAccessException {

        Field  base;
        Field  swap;
        Object remote;
        Object source;
        String stream;
        String string;

        if (other == null) throw new IllegalArgumentException("Invalid object [null]");
        if (name == null) throw new IllegalArgumentException("Invalid name [null]");
        if (store == null) throw new IllegalArgumentException("Invalid store [null]");

        if (mode != Accession.EXPORT && mode != Accession.IMPORT) throw new IllegalArgumentException("Invalid mode export or import required");

        //die Bezugsobjekte werden zugewiesen
        remote = (mode == Accession.EXPORT) ? other : object;
        source = (mode == Accession.IMPORT) ? other : object;

        //der Alias wird gegebenfalls aufgeloest
        string = (mode == Accession.EXPORT) ? store.trim() : name.trim();
        stream = (mode == Accession.EXPORT) ? name.trim() : store.trim();

        //das Feld wird ermittelt und fuer den Zugriff freigegeben
        base = Accession.getField(source, string);

        //das Feld wird ermittelt und fuer den Zugriff freigegeben
        swap = Accession.getField(remote, stream);

        //das Feld wird entsprechend dem Type synchronisiert
        if (base.getType().equals(Boolean.TYPE)) swap.setBoolean(remote, base.getBoolean(source));
        else if (base.getType().equals(Byte.TYPE)) swap.setByte(remote, base.getByte(source));
        else if (base.getType().equals(Character.TYPE)) swap.setChar(remote, base.getChar(source));
        else if (base.getType().equals(Double.TYPE)) swap.setDouble(remote, base.getDouble(source));
        else if (base.getType().equals(Float.TYPE)) swap.setFloat(remote, base.getFloat(source));
        else if (base.getType().equals(Integer.TYPE)) swap.setInt(remote, base.getInt(source));
        else if (base.getType().equals(Long.TYPE)) swap.setLong(remote, base.getLong(source));
        else if (base.getType().equals(Short.TYPE)) swap.setShort(remote, base.getShort(source));
        else if (!base.getType().isPrimitive()) swap.set(remote, base.get(source));
    }

    /**
     *  F&uuml;hrt den Standard Konstruktor ohne weitere Argumente aus.
     *  R&uuml;ckgabe die neu eingerichtete Instanz.
     *  @param  object Bezugsobjekt
     *  @return die neu eingerichtete Objekt Instanz
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws InstantiationException bei fehlerhafter Einrichtung der Instanz
     *  @throws InvocationTargetException beim Aufruf auftretende Exceptions
     *  @throws NoSuchMethodException wenn die Methode nicht vorhanden ist
     */
    public static Object newInstance(Object object)
        throws IllegalAccessException, InstantiationException, InvocationTargetException,
               NoSuchMethodException {

        return Accession.newInstance(object, null, null);
    }

    /**
     *  F&uuml;hrt den Konstruktor mit den als Array von Objekten
     *  &uuml;bergebenen Argumenten aus. Da die Objekte auch die Basis der
     *  &uuml;bergebenen R&uuml;ckgabe die neu eingerichtete Instanz.
     *  @param  object    Bezugsobjekt
     *  @param  arguments Argumente als Array von Objekten
     *  @return die neu eingerichtete Objekt Instanz
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws InstantiationException bei fehlerhafter Einrichtung der Instanz
     *  @throws InvocationTargetException beim Aufruf auftretende Exceptions
     *  @throws NoSuchMethodException wenn die Methode nicht vorhanden ist
     */
    public static Object newInstance(Object object, Object[] arguments)
        throws IllegalAccessException, InstantiationException, InvocationTargetException,
               NoSuchMethodException {

        Class[] types;
        Class[] array;

        int     loop;

        //die Klassen der Argumente werden ermittelt
        for (loop = 0, types = new Class[0]; arguments != null && loop < arguments.length; loop++) {

            array = new Class[types.length +1];
            System.arraycopy(types, 0, array, 0, types.length);
            array[types.length] = (arguments[loop] == null) ? null : arguments[loop].getClass();
            types = array;
        }

        return Accession.newInstance(object, types, arguments);
    }

    /**
     *  F&uuml;hrt den Konstruktor mit den als Array von Datentypen und Objekten
     *  &uuml;bergebenen Argumenten aus. Die Spezifierung einfacher Datentypen
     *  erfolgt dabei &uuml;ber den Typ des entsprechenden Wrappers
     *  (Bsp. int = Integer.Type). R&uuml;ckgabe die neu eingerichtete Instanz.
     *  @param  object    Bezugsobjekt
     *  @param  types     Datentypen als Array von Klassen
     *  @param  arguments Argumente als Array von Objekten
     *  @return die neu eingerichtete Objekt Instanz
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws InstantiationException bei fehlerhafter Einrichtung der Instanz
     *  @throws InvocationTargetException beim Aufruf auftretende Exceptions
     *  @throws NoSuchMethodException wenn die Methode nicht vorhanden ist
     */
    public static Object newInstance(Object object, Class[] types, Object[] arguments)
        throws IllegalAccessException, InstantiationException, InvocationTargetException,
               NoSuchMethodException {

        Constructor constructor;

        //der Konstruktor wird ermittelt und freigegeben
        constructor = Accession.getConstructor(object, types);

        if (constructor == null) throw new NoSuchMethodException();

        //die Methode wird ausgefuehrt
        return constructor.newInstance(arguments);
    }

    /**
     *  Ermittelt den Inhalt des angegebenen Feld und gibt dessen Objekt
     *  zur&uuml;ck. Einfache Datentypen werden dabei als entsprechendes Wrapper
     *  Objekt zur&uuml;ckgegeben.
     *  @param  object Bezugsobjekt
     *  @param  name   Name des Felds
     *  @return das Objekt des Felds, bzw. das entsprechende Wrapper Objekt
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws NoSuchFieldException wenn das Feld nicht vorhanden ist
     */
    public static Object get(Object object, String name)
        throws IllegalAccessException, NoSuchFieldException {

        Field  field;

        //der Name wird bereinigt
        name = (name == null) ? "" : name.trim();

        //das Feld wird ermittelt und feigegeben
        field = Accession.getField(object, name);

        if (field == null) throw new NoSuchFieldException();

        //das Feld wird entsprechend dem Type synchronisiert
        if (field.getType().equals(Boolean.TYPE)) return new Boolean(field.getBoolean(object));
        else if (field.getType().equals(Byte.TYPE)) return new Byte(field.getByte(object));
        else if (field.getType().equals(Character.TYPE)) return new Character(field.getChar(object));
        else if (field.getType().equals(Double.TYPE)) return new Double(field.getDouble(object));
        else if (field.getType().equals(Float.TYPE)) return new Float(field.getFloat(object));
        else if (field.getType().equals(Integer.TYPE)) return new Integer(field.getInt(object));
        else if (field.getType().equals(Long.TYPE)) return new Long(field.getLong(object));
        else if (field.getType().equals(Short.TYPE)) return new Short(field.getShort(object));

        else if (!field.getType().isPrimitive()) return field.get(object);

        return null;
    }

    /**
     *  Setzt den Inhalt des angegebenen Feld mit dem &uuml;bergebenen Objekt.
     *  Einfache Datentypen werden dabei als entsprechendes Wrapper Objekt
     *  &uuml;bergebengegeben.
     *  @param  object Bezugsobjekt
     *  @param  name   Name des Felds
     *  @param  value  zu setztender Wert als Objekt
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws NoSuchFieldException wenn das Feld nicht vorhanden ist
     */
    public static void set(Object object, String name, Object value)
        throws IllegalAccessException, NoSuchFieldException {

        Field field;

        //der Name wird bereinigt
        name = (name == null) ? "" : name.trim();

        //das Feld wird ermittelt und freigegeben
        field = Accession.getField(object, name);

        if (field == null) throw new NoSuchFieldException();

        if (value == null && field.getType().isPrimitive()) {

            if (field.getType().equals(Boolean.TYPE)) value = new Boolean(false);
            else if (field.getType().equals(Byte.TYPE)) value = new Byte((byte)0);
            else if (field.getType().equals(Character.TYPE)) value = new Character((char)0);
            else if (field.getType().equals(Double.TYPE)) value = new Double(0);
            else if (field.getType().equals(Float.TYPE)) value = new Float(0);
            else if (field.getType().equals(Integer.TYPE)) value = new Integer(0);
            else if (field.getType().equals(Long.TYPE)) value = new Long(0);
            else if (field.getType().equals(Short.TYPE)) value = new Short((short)0);
        }

        //das Feld wird entsprechend dem Type synchronisiert
        if (field.getType().equals(Boolean.TYPE)) field.setBoolean(object, ((Boolean)value).booleanValue());
        else if (field.getType().equals(Byte.TYPE)) field.setByte(object, ((Byte)value).byteValue());
        else if (field.getType().equals(Character.TYPE)) field.setChar(object, ((Character)value).charValue());
        else if (field.getType().equals(Double.TYPE)) field.setDouble(object, ((Double)value).doubleValue());
        else if (field.getType().equals(Float.TYPE)) field.setFloat(object, ((Float)value).floatValue());
        else if (field.getType().equals(Integer.TYPE)) field.setInt(object, ((Integer)value).intValue());
        else if (field.getType().equals(Long.TYPE)) field.setLong(object, ((Long)value).longValue());
        else if (field.getType().equals(Short.TYPE)) field.setShort(object, ((Short)value).shortValue());
        else if (!field.getType().isPrimitive()) field.set(object, value);
    }

    /**
     *  F&uuml;hrt die angegebene Methode ohne weitere Argumente am aus.
     *  R&uuml;ckgabe des R&uuml;ckgabewerts der Methode als Objekt. Einfache
     *  Datentypen werden als Wrapper seiner Art zur&uuml;ckgegeben.
     *  @param  object Bezugsobjekt
     *  @param  name   auszuf&uuml;hrende Methode
     *  @return der R&uuml;ckgabewert des Methodenaufrufs
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws InvocationTargetException beim Aufruf auftretende Exceptions
     *  @throws NoSuchMethodException wenn die Methode nicht vorhanden ist
     */
    public static Object invoke(Object object, String name)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        return Accession.invoke(object, name, null, null);
    }

    /**
     *  F&uuml;hrt die angegebene Methode mit den als Array von Objekten
     *  &uuml;bergebenen Argumenten am Objekt aus. Da die Objekte auch die Basis
     *  der &uuml;bergebenen Datentypen spezifizieren, kann diese Methode nicht
     *  f&uuml;r einfache Datentypen als Argumente verwendet werden.
     *  R&uuml;ckgabe des R&uuml;ckgabewerts der Methode als Objekt. Einfache
     *  Datentypen werden als Wrapper seiner Art zur&uuml;ckgegeben.
     *  @param  object    Bezugsobjekt
     *  @param  name      auszuf&uuml;hrende Methode
     *  @param  arguments Argumente als Array von Objekten
     *  @return der R&uuml;ckgabewert des Methodenaufrufs
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws InvocationTargetException beim Aufruf auftretende Exceptions
     *  @throws NoSuchMethodException wenn die Methode nicht vorhanden ist
     */
    public static Object invoke(Object object, String name, Object[] arguments)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Class[] array;
        Class[] types;

        int     loop;

        //die Klassen der Argumente werden ermittelt
        for (loop = 0, types = new Class[0]; arguments != null && loop < arguments.length; loop++) {

            array = new Class[types.length +1];
            System.arraycopy(types, 0, array, 0, types.length);
            array[types.length] = (arguments[loop] == null) ? null : arguments[loop].getClass();
            types = array;
        }

        return Accession.invoke(object, name, types, arguments);
    }

    /**
     *  F&uuml;hrt die angegebene Methode mit den als Array von Datentypen und
     *  Objekten &uuml;bergebenen Argumenten am Objekt aus. Die Spezifierung
     *  einfacher Datentypen erfolgt dabei &uuml;ber den Typ des entsprechenden
     *  Wrappers (Bsp. int = Integer.Type). R&uuml;ckgabe des R&uuml;ckgabewerts
     *  der Methode als Objekt. Einfache Datentypen werden als Wrapper seiner
     *  Art zur&uuml;ckgegeben.
     *  @param  object    Bezugsobjekt
     *  @param  name      auszuf&uuml;hrende Methode
     *  @param  types     Datentypen als Array von Klassen
     *  @param  arguments Argumente als Array von Objekten
     *  @return der R&uuml;ckgabewert des Methodenaufrufs
     *  @throws IllegalAccessException bei Zugriffsverletzungen auf die Methode
     *  @throws InvocationTargetException beim Aufruf auftretende Exceptions
     *  @throws NoSuchMethodException wenn die Methode nicht vorhanden ist
     */
    public static Object invoke(Object object, String name, Class[] types, Object[] arguments)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Method method;

        //der Name wird bereinigt
        name = (name == null) ? "" : name.trim();

        //die Methode wird ermittelt und freigegeben
        method = Accession.getMethod(object, name, types);

        if (method == null) throw new NoSuchMethodException();

        //die Methode wird ausgefuehrt
        return method.invoke(object, arguments);
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
                || object instanceof String
                || object instanceof Class) return false;

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
            if (Accession.isObjectRecursive(entry, list)) return true;
        }

        //Arrays werden in Listen aufgeloest
        if (object.getClass().isArray()) {

            try {entry = Arrays.asList((Object[])object);
            } catch (ClassCastException exception) {

                return false;
            }

            iterator = ((Collection)entry).iterator();

            while (iterator.hasNext()) {

                if (Accession.isObjectRecursive(iterator.next(), list)) return true;
            }
        }

        return false;
    }

    /**
     *  R&uuml;ckgabe des Feldzugriffs. Die R&uuml;ckgabe erfolgt bei als
     *  Methode, Feld oder <code>null</code>, wenn kein &ouml;ffentlicher
     *  Zugriff auf das Feld m&ouml;gloch ist. Vorzugsweise erfolg der Zugriff
     *  &uuml;ber eine f&uuml;r das Feld implemtierte Get bzw. bei boolischen
     *  Werten auch die Is Methode. Alternativ wird auch bei &ouml;ffentlichen
     *  Felder direkt zugegriffen wenn diese keine separate Zugriffsmethode
     *  bereitstellen.
     *  @param  object Bezugsobjekt
     *  @param  field Feld
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
     *  Ermittelt eine &Uuml;bersicht alle eigenen zug&auml;nglichen Felder, wie
     *  auch die der Superklasse(n), mit den entsprechenden Werten des
     *  &uuml;bergeben Objekts als String.<br><br>
     *  <b>Hinweis</b> - Alle Felder werden auf Rekursion gepr&uuml;ft. Bei
     *  Feldern mit Rekursionspotenzial wird die <code>toString()</code> Methode
     *  nicht aufgerufen. Der Wert wird in diesem Fall als "[...]" dargestellt.
     *  @param  object Bezugsobjekt
     *  @return die erstellte Objekt Information als String
     */
    public static String toString(Object object) {

        return Accession.toString(object, null);
    }

    /**
     *  Ermittelt eine &Uuml;bersicht alle eigenen zug&auml;nglichen Felder, wie
     *  auch die der Superklasse(n), mit den entsprechenden Werten des
     *  &uuml;bergeben Objekts als String. Zus&auml;tzlich kann noch eine Liste
     *  von zu ignorierenden Feldern &uuml;bergeben werden.<br><br>
     *  <b>Hinweis</b> - Alle Felder werden auf Rekursion gepr&uuml;ft. Bei
     *  Feldern mit Rekursionspotenzial wird die <code>toString()</code> Methode
     *  nicht aufgerufen. Der Wert wird in diesem Fall als "[...]" dargestellt.
     *  @param  object     Bezugsobjekt
     *  @param  exclusions Liste der zu ignorierenden Felder
     *  @return die erstellte Objekt Information als String
     */
    public static String toString(Object object, String[] exclusions) {

        Field   field;
        List    list;
        Object  access;
        Object  value;
        String  source;
        String  stream;
        String  string;

        Field[] fields;

        int     cursor;

        //der Klassenname wird emrittelt
        source = object.getClass().getName();
        cursor = source.lastIndexOf('.');

        //der Klassenname ohne Paket wird ermittelt
        if (cursor >= 0) source = source.substring(cursor +1);

        //alle Felder werden ermittelt
        fields = Accession.getFields(object);

        //die Liste der Ausschluesse wird ermittelt
        list = (exclusions != null) ? Arrays.asList(exclusions) : new ArrayList();

        for (cursor = 0, stream = ""; cursor < fields.length; cursor++) {

            field = fields[cursor];

            //die Art des Zugriffs wird ermittelt
            access = Accession.getPublicFieldAccess(object, field);

            //nur oeffentlich zugaengliche Felder werden beruecksichtigt
            if (access == null || !(access instanceof Field || access instanceof Method)) continue;

            //ausgeschlossene Felder werden ignoriert
            if (list.contains(field.getName())) continue;

            //das Feld wird registriert
            list.add(field.getName());

            try {

                //der Wert vom Feld wird ermittelt
                value = (access instanceof Method) ? Accession.invoke(object, ((Method)access).getName()) : Accession.get(object, field.getName());

                if (value == null) {

                    string = "null";

                } else {

                    //elementare Objekttypen werden direkt aufgeloest
                    if (value instanceof Boolean
                            || value instanceof Byte
                            || value instanceof Character
                            || value instanceof Double
                            || value instanceof Float
                            || value instanceof Integer
                            || value instanceof Long
                            || value instanceof Short
                            || value instanceof String) {

                        string = String.valueOf(value);

                    } else if (Accession.isObjectRecursive(value, null)) {

                        //Objekte mit Rekursion werden nicht aufgeloest
                        string = "[...]";

                    } else {

                        //Arrays werden in Listen aufgeloest
                        if (value.getClass().isArray()) value = Arrays.asList((Object[])value);

                        string = (value == null) ? null : String.valueOf(value);
                    }
                }

            } catch (Exception exception) {string = "[failed]";}

            string = Components.strmsc(string).trim();

            if (stream.length() > 0) stream = stream.concat(", ");

            stream = stream.concat(field.getName()).concat(" = ").concat(string);
        }

        return source.concat("[").concat(stream).concat("]");
    }
}