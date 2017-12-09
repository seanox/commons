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

import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

import com.seanox.common.Accession;
import com.seanox.common.Components;
import com.seanox.common.Initialize;
import com.seanox.common.Section;

/**
 *  Context ist eine Erweiterung der Seanox Devwex Module-API, zur
 *  Implementierung von Server-Erweiterungen, welche als Dienste im Hintergrund
 *  vom Server-Context laufen, realisieren. Da der Modul-Container von Seanox
 *  Devwex (Service Modul-Control) alle Module &uuml;ber Refelections initiiert und
 *  kontrolliert.<br>
 *  <br>
 *  Sequenz vom Modul-Aufruf:<br>
 *  <br>
 *  <ul>
 *    <li>
 *      <code>Modul()</code> - das Modul wird durch Seanox Devwex &uuml;ber den
 *      Standard-Konstruktor initiiert, liegt dieser Konstruktor nicht vor, wird
 *      das Module als inkompatible zur API betrachtet und nicht geladen
 *    </li>
 *    <li>
 *      {@link #initialize(String)} - das Modul wird mit &Uuml;bergabe der
 *      Konfigurationsparameter geladen, die Konfiguration l&auml;sst sich mit
 *      der Methode {@link Section#parse(String)} konvertieren, was den Zugriff
 *      auf die einzelnen Parameter vereinfacht
 *    </li>
 *    <li>
 *      {@link #initialize(Section)} - das Modul wird alternativ &uuml;ber den
 *      Context initialisiert und die Konfiguration als {@link Section}
 *      &uuml;bergeben
 *    </li>
 *    <li>
 *      {@link #getCaption()} -  - dient der allgemeinen Information und gibt
 *      die Modulkennung im Format <code>[PRODUCER-MODULE/VERSION]</code>
 *      zur&uuml;ck und sollte mit dem Aufruf des Konstruktors oder statisch
 *      gesetzt werden
 *    </li>
 *    <li>
 *       {@link #destroy()} - das Modul wird &uuml;ber die Modul-API zum Beenden
 *       aufgeforder, erfolgt das Beenden &uum;ber den Modul-Container, wird das
 *       Modul samt ClassLoader komplett entladen
 *    </li>
 *  </ul>
 *  <br>
 *  Deklarieren l&auml;sst sich ein Modul in verschiedenen Sektionen. So werden
 *  Module in der Sektion <code>INITIALIZE</code> mit dem Start des Servers
 *  geladen, was u.a. f&uuml;r Hintergrunddienste sinnvoll ist oder wenn
 *  HTTP-Module vor dem ersten Zugriff vorgeladen werden sollen. In den
 *  Sektionen <code>SERVER:REF</code> und <code>VIRTUAL:REF</code> werden Module
 *  deklariert, wenn f&uuml;r diese ein HTTP-Zugriff &uuml;ber einen virtuellen
 *  Pfad vorgesehen ist. In jeder Form der Deklaration l&auml;sst sich die
 *  Konfiguration &uuml;ber den Parameter <code>extends</code> in
 *  eigenst&auml;ndige Sektionen ausgelagern.<br>
 *  <br>
 *  Context 1.2013.0413<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0413
 */
public abstract class Context {

    /**
     *  Hinweis - Die folgenden Ressourcen werden per Reflections verwendet:
     *
     *    - com.seanox.devwex.Service::service
     *    - com.seanox.devwex.Service::initialize
     *    - com.seanox.devwex.Service::verbose
     *    - com.seanox.devwex.Service::print
     *    - com.seanox.devwex.Service::call
     */

    /**
     *  Einsprung f&uuml;r die Initialisierung des Moduls.
     *  @param  options Konfiguration
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    public void initialize(String options) throws Exception {

        this.initialize(Context.parseOptions(options));
    }

    /**
     *  Einsprung f&uuml;r die Initialisierung des Moduls.
     *  @param  options Konfiguration
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    public void initialize(Section options) throws Exception {

        return;
    }

    /**
     *  R&uuml;ckgabe der Kennung des Moduls.
     *  @return die Kennung im Format <code>[PRODUCER-MODULE/VERSION]</code>
     */
    public abstract String getCaption();

    /**
     *  Ermittelt die aktuelle Konfiguration des Servers.
     *  @return die aktuelle Konfiguration des Servers
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    public static Initialize getConfiguration() {

        Initialize initialize;
        Object     object;

        try {

            initialize = new Initialize();

            //der Service vom Server wird ermittelt
            object = Context.mountField(Class.forName("com.seanox.devwex.Service"), "service.initialize", true);

            //die Konfiguration wird kopiert
            Accession.storeField(object, initialize, "entries");
            Accession.storeField(object, initialize, "list");

            return initialize;

        } catch (Exception exception) {

            throw new RuntimeException("Access on service configuration failed", exception);
        }
    }

    /**
     *  Ermittelt per Java Reflections das angegebene Feld, wobei auch
     *  Objektstrukturen durch den Punkt getrennt unterst&uuml;tzt werden.
     *  R&uuml;ckgabe der Inhalt vom angegebenen Feld als Referenz bei Objekten
     *  und als Wrapper bei einfachen Datentypen.
     *  @param  object Zugriffobjekt
     *  @param  field  Name des Felds
     *  @return der Inhalt des angeforderten Felds
     *  @throws IllegalArgumentException bei ung&uml;tigem Argumenten
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    protected static Object mountField(Object object, String field) {

        return Context.mountField(object, field, true);
    }

    /**
     *  Ermittelt per Java Reflections das angegebene Feld, wobei auch
     *  Objektstrukturen durch den Punkt getrennt unterst&uuml;tzt werden.
     *  R&uuml;ckgabe der Inhalt vom angegebenen Feld als Referenz bei Objekten
     *  und als Wrapper bei einfachen Datentypen. Mit der Option
     *  <code>clone</code> kann bei Objekten eine Kopie angefordert werden. Dazu
     *  muss das Objekt aber die <code>Clonable</code> implementieren. Bei
     *  einfachen Datentypen wird die Option <code>clone</code> ignoriert, da
     *  der Wert &uuml;ber den Wrapper immer als Kopie zur&uuml;ckgegeben wird.
     *  @param  object Zugriffobjekt
     *  @param  field  Name des Felds
     *  @param  clone  Option <code>true</code> zur R&uuml;ckgabe als Kopie
     *  @return der Inhalt des angeforderten Felds
     *  @throws IllegalArgumentException bei ung&uml;tigem Argumenten
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    protected static Object mountField(Object object, String field, boolean clone) {

        Object          result;
        StringTokenizer tokenizer;

        if (field  == null) throw new IllegalArgumentException("Invalid field [null]");
        if (object == null) throw new IllegalArgumentException("Invalid object [null]");

        //die Elemente werden ueber den Punkt geteilt
        tokenizer = new StringTokenizer(field, ".");

        for (result = object; tokenizer.hasMoreTokens();) {

            if (result == null) throw new RuntimeException("Invalid object [null]");

            //das Element wird ermittelt
            field = tokenizer.nextToken();

            //leere Elemente werden nicht unterstuetzt
            if (field.trim().length() == 0) throw new RuntimeException("Invalid field [empty]");

            //der Inhalt vom Feld wird ermittelt
            try {result = Accession.get(result, field);
            } catch (Exception exception) {

                throw new RuntimeException(("Access on field ").concat(field).concat(" failed"), exception);
            }
        }

        //fuer einfache Datentypen und null wird keine Kopie erstellt
        if (result == null || result.getClass().isPrimitive() || !clone) return result;

        //die Kopie wird ueber den Aufruf der Clone Methode erstellt
        try {return Accession.invoke(result, "clone");
        } catch (Exception exception) {

            throw new RuntimeException(("Access on clone method for field ").concat(field).concat(" failed"), exception);
        }
    }

    /**
     *  Ausgabe des Objekts im Context des Servers. Dieser Protokolliert das
     *  &uuml;bergebene Objekt zeilenweise mit vorangestelltem Zeitstempel in
     *  den Standard IO. Zur Ermittlung des Protokolltexts wird die Methode
     *  <code>Object.toString()</code> vom &uuml;bergebenen Objekt verwendet.
     *  Bei Fehlerobjekten wird der StackTrace zeilenweise protokolliert. Mit
     *  der Option <code>verbose</code> erfolgt die Ausgabe nur, wenn die
     *  erweiterte Serverausgabe aktiv ist.
     *  @param object Objekt mit dem Protokolleintrag
     */
    public static void print(Object object) {

        Context.print(object, false);
    }

    /**
     *  Ausgabe des Objekts im Context des Servers. Dieser Protokolliert das
     *  &uuml;bergebene Objekt zeilenweise mit vorangestelltem Zeitstempel in
     *  den Standard IO. Zur Ermittlung des Protokolltexts wird die Methode
     *  <code>Object.toString()</code> vom &uuml;bergebenen Objekt verwendet.
     *  Bei Fehlerobjekten wird der StackTrace zeilenweise protokolliert. Mit
     *  der Option <code>verbose</code> erfolgt die Ausgabe nur, wenn die
     *  erweiterte Serverausgabe aktiv ist.
     *  @param object  Objekt mit dem Protokolleintrag
     *  @param verbose <code>true</code> erweiterte Ausgabe
     */
    public static void print(Object object, boolean verbose) {

        Object service;

        try {

            //der Server wird ermittelt
            service = Class.forName("com.seanox.devwex.Service");

            //Zusatzinformationen werden ohne die Server-Option unterdrueckt
            if (!((Boolean)Accession.get(service, "verbose")).booleanValue() && verbose) return;

            Accession.invoke(service, "print", new Class[] {Object.class}, new Object[] {object});

        } catch (Throwable throwable) {

            //das die Methode fuer die abschliessenden Fehlerausgabe verwendet
            //werden kann, werden alle auftretende Fehler abgefangen um eine
            //ungewollte Rekursion zu verhindern
        }
    }

    /**
     *  Fordert ein Module vom Server Context an. Ist dieses noch nicht
     *  registriert, wird es vom Server eingerichtet wenn es sich um einen
     *  Module Context handelt, sonst wird nur die Klasse als Referenz
     *  vorgehalten. Bei der Initialisierung auftretende Fehler werden nicht
     *  behandelt und weitergereicht.
     *  @param  module Modul Klasse
     *  @return die Klasse deren vorhandene Instanz, sonst <code>null</code>
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder Inkompatibilit&auml;t der Laufzeitumgebung oder
     *          dem angeforderten Modul
     *  @throws Exception bei Fehlern im Zusammenhang mit der Initialisierung
     */
    public static Object call(Class module) throws Exception {

        return Context.call(module, null);
    }

    /**
     *  Fordert ein Module vom Server Context an. Ist dieses noch nicht
     *  registriert, wird es vom Server mit den optional &uuml;bergebenen Daten
     *  eingerichtet wenn es sich um einen Module Context handelt, sonst wird
     *  nur die Klasse als Referenz vorgehalten. Bei der Initialisierung
     *  auftretende Fehler werden nicht behandelt und weitergereicht.
     *  @param  module  Modul Klasse
     *  @param  options optionale Daten zur Einrichtung
     *  @return die Klasse deren vorhandene Instanz, sonst <code>null</code>
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder Inkompatibilit&auml;t der Laufzeitumgebung oder
     *          dem angeforderten Modul
     *  @throws Exception bei Fehlern im Zusammenhang mit der Initialisierung
     */
    public static Object call(Class module, String options) throws Exception {

        Object service;

        try {

            //der Server wird ermittelt
            service = Class.forName("com.seanox.devwex.Service");

            return Accession.invoke(service, "call", new Class[] {Class.class, String.class}, new Object[] {module, options});

        } catch (InvocationTargetException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new RuntimeException("Access on service module container failed", exception);
        }
    }

    /**
     *  Erweitert die &uuml;bergebene Konfiguration, wenn diese eine
     *  <code>extends</code> Angabe enth&auml;lt. In diesem Fall wird erst die
     *  per <code>extends</code> angegebene Konfiguration geladen und dann um
     *  die &uuml;bergeben Parameter erweitert, bzw. durch diese
     *  &uuml;berschrieben.
     *  @param  options Parameter der ausgehenden Deklaration
     *  @return die Ermittelte Konfiguration, kann keine ermittelt werden wird
     *          eine leere Konfiguration zur&uuml;ckgegeben
     */
    private static Section extendsOptions(Section options) {

        Initialize initialize;
        Section    parameters;
        String     string;

        string = options.get("extends");

        if (string.length() == 0) return options;

        //die Serverkonfiguration wird ermittelt
        initialize = com.seanox.module.Context.getConfiguration();
        
        parameters = Section.parse(initialize.get(string));
        
        parameters.merge(options);

        return parameters;
    }

    /**
     *  Ermittelt die einzelnen Optionen der &uuml;bergebene Optionszeile im
     *  Format <code>[OPTION:VALUE]</code>. Die Maskierung von [ erfolgt dabei
     *  durch [[ und von  ] als ]].
     *  @param  options Optionszeile
     *  @return die ermittelten Optionen als Parameter
     */
    protected static Section parseOptions(String options) {

        Section         parameters;
        StringTokenizer tokenizer;
        String          stream;
        String          string;

        boolean         control;
        int             cursor;

        parameters = new Section();

        options = Components.strcln(options);
        options = Components.strset(options, "[[", "\1");
        options = Components.strset(options, "]]", "\2");

        tokenizer = new StringTokenizer(options, "[]", true);

        for (stream = "", control = true; tokenizer.hasMoreTokens();) {

            string = tokenizer.nextToken();

            control = string.equals("]") ? true : string.equals("[") ? false : control;

            if (control || string.equals("[") || string.equals("]")) continue;

            string = string.replace('\1', '[').replace('\2', '[').trim();
            cursor = string.indexOf(':');
            stream = (cursor >= 0) ? string.substring(cursor +1).trim() : "";
            string = (cursor >= 0) ? string.substring(0, cursor).trim() : string;

            if (string.length() > 0) parameters.set(string, stream);

            control = true;
        }

        return Context.extendsOptions(parameters);
    }

    /**
     *  Einsprung des Service zum beenden des Moduls.
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    public void destroy() throws Exception {

        return;
    }
}