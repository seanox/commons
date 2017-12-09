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
package com.seanox.module.http;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.seanox.common.Accession;
import com.seanox.common.Loader;
import com.seanox.common.Section;

/**
 *  Context ist eine Erweiterung der Seanox Devwex (HTTP)Module-API zur
 *  Implementierung von HTTP-Erweiterungen (Modulen). Je nach Modultyp bestehen
 *  unterschiedliche Anforderungen beim Umgang mit den Ressourcen des Servers.
 *  Diese Erweiterung der (HTTP)Module-API kapselt diese Anforderungen und
 *  stellt auf jedem Modultyp abgestimmte Laufzeitobjekte und Wrapper bereit.
 *  <br>
 *  Zur Anbindung und Ausf&uuml;hrung der (HTTP)Module f&uuml;gt der Context
 *  zudem einen weiteren (Application)ClassLoader ein, womit sich die Module in
 *  eigenst&auml;ndigen Instanzen verwenden lassen.<br>
 *  <br>
 *  Sequenz vom Modul-Aufruf:<br>
 *  <br>
 *  <ul>
 *    <li>
 *      <code>Modul()</code> - das Modul wird durch die Seanox Devwex Modul-API
 *      &uuml;ber den Standard-Konstruktor initiiert, liegt dieser Konstruktor
 *      nicht vor, wird das Module als inkompatible zur API betrachtet und nicht
 *      geladen
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
 *      {@link #getCaption()} - dient zur allgemeinen Information und gibt die
 *      Modulkennung im Format <code>[PRODUCER-MODULE/VERSION]</code>
 *      zur&uuml;ck und sollte mit dem Aufruf des Konstruktors oder statisch
 *      gesetzt werden
 *    </li>
 *    <li>
 *      {@link #bind(Object, int)} - das Modul wird zur Verarbeitung
 *      angefordert, die Art (Modultyp) wird als <code>type</code>
 *      &uuml;bergeben, im Standardfall wird diese Methode nicht
 *      &uuml;berschrieben
 *    </li>
 *    <li>
 *      <code>public void service(...)</code> - die Methode wird vom Dispatcher
 *      passend zum angeforderten Modultyp aufgerufen und ein ebenfalls zum
 *      angeforderten Modultyp passendes Laufzeitobjekt &uuml;bergeben
 *    </li>
 *    <li>
 *      {@link #destroy()} - fordert das Modul zum Beenden auf, mit dem Beenden
 *      eines Moduls &uuml;ber die Seanox Devwex Modul-API wird die Instanz des
 *      Moduls samt ClassLoader entladen und ggf. neu initialisiert
 *    </li>
 *  </ul>
 *  <br>
 *  Die Konfiguration der Module, welche &uuml;ber den Context implementiert
 *  sind, erfolgt direkt &uuml;ber die Context-Klasse, damit der
 *  (Application)ClassLoader zwischen Modul und Seanox Devwex (HTTP)Module-API
 *  geladen werden kann. Folgende Parameter stehen hierzu zur Verf&uuml;gung:<br>
 *  <ul>
 *    <li>
 *      <b>context</b> - Name/Alias der ClassLoader-Instanz, diese wird u.a.
 *      zum Referenzieren des Moduls innerhalb der Konfiguration verwendet, z.B.
 *      wenn die Konfiguration in eigene Sektion ausgelagert und die Deklaration
 *      eines Moduls in verschiedenen Sektionen verwendet wird, zum anderen
 *      kann bestimmt werden, mit welcher ClassLoader-Instanz ein Modul geladen
 *      werden soll, z.B. wenn mehre Module einen bestimmten ClassLoader
 *      verwenden sollen
 *    </li>
 *    <li>
 *      <b>class</b> - Basisklasse vom Modul, welche den Context implementiert
 *    </li>
 *    <li>
 *      <b>libraries</b> - Liste zus&auml;tzlicher und moduleigener Bibliotheken,
 *      durch den eigenen (Application)ClassLoader sind diese nur f&uuml;r das
 *      deklarierte Modul verf&uuml;bar
 *    </li>
 *  </ul>
 *  <br>
 *  Deklarieren l&auml;sst sich ein Modul in verschiedenen Sektionen. So werden
 *  Module in der Sektion <code>INITIALIZE</code> mit dem Start des Servers
 *  geladen, was u.a. f&uuml;r Hintergrunddienste sinnvoll ist oder wenn Module
 *  vor dem ersten Zugriff vorgeladen werden sollen. In den Sektionen
 *  <code>SERVER:REF</code> und <code>VIRTUAL:REF</code> werden Module
 *  deklariert, wenn f&uuml;r diese ein HTTP Zugriff &uuml;ber einen virtuellen
 *  Pfad vorgesehen ist. In jeder Form der Deklaration l&auml;sst sich die
 *  Konfiguration &uuml;ber den Parameter <code>extends</code> in
 *  eigenst&auml;ndige Sektionen ausgelagern.<br>
 *  <br>
 *  Beispiel f&uuml;r die Deklaration in den Sektionen <code>INITIALIZE</code>
 *  und <code>SERVER:REF</code> bzw. <code>VIRTUAL:REF</code>. Dabei wird das
 *  Modul mit dem Start des Servers geladen und sp&auml;ter &uuml;ber
 *  <code>context</code> referenziert.
 *  <pre>
 *    [INITIALIZE]
 *      EXAMPLE = com.seanox.module.http.Context
 *              + [context:example]
 *              + [class:example.Module]
 *              + [libraries:...jar ...jar] ...
 *
 *    [SERVER:HTTP:REF]
 *      EXAMPLE = /example &gt; com.seanox.module.http.Context
 *              + [context:example]
 *              + ... [M]
 *  </pre>
 *  Beispiel f&uuml;r die direkte Deklaration in den Sektionen
 *  <code>SERVER/VIRTUAL:REF</code>. Dabei wird das Modul mit dem ersten Aufruf
 *  vom Pfad <code>/example</code> initialisiert.
 *  <pre>
 *    [SERVER:HTTP:REF]
 *      EXAMPLE = /example &gt; com.seanox.module.http.Context
 *              + [context:example]
 *              + [class:example.Module]
 *              + [libraries:...jar ...jar]
 *              + ... [M]
 *  </pre>
 *  Beispiel f&uuml;r die Deklaration in den Sektionen <code>INITIALIZE</code>
 *  und <code>SERVER/VIRTUAL:REF</code> mit ausgelagerter Modulkonfiguration.
 *  Dabei wird das Modul ebenfalls mit dem Start des Servers geladen und
 *  sp&auml;ter &uuml;ber <code>context</code> referenziert.
 *  <pre>
 *    [INITIALIZE]
 *      EXAMPLE = com.seanox.module.http.Context
 *              + [extends:example:mod] ...
 *
 *    [SERVER:HTTP:REF]
 *      EXAMPLE = /example &gt; com.seanox.module.http.Context
 *              + [context:example]
 *              +  ... [M]
 *
 *    [EXAMPLE:MOD]
 *      CONTEXT   = EXAMPLE
 *      CLASS     = test.Module
 *      LIBRARIES = ...jar ...jar
 *      ...
 *  </pre>
 *  Context 1.2013.0701<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0701
 */
public class Context extends com.seanox.module.Context {

    /** Verzeichnis der verf&uuml;gbaren ClassLoader und Module */
    private volatile Hashtable universe;

    /** Konstante der aktuellen Version */
    private static final String VERSION = "@@@ant-project-version";

    /** Konstante <code>class</code> der Modulkonfiguration */
    private static final String PROPERTIES_CLASS = "class";

    /** Konstante <code>libraries</code> der Modulkonfiguration */
    private static final String PROPERTIES_LIBRARIES = "libraries";

    /** Konstante <code>context</code> der Modulkonfiguration */
    private static final String PROPERTIES_CONTEXT = "context";

    /** Konstante f&uuml;r den Modultyp Service */
    public static final int MODUL_TYPE_SERVICE = 0;

    /** Konstante f&uuml;r den Modultyp Filter */
    public static final int MODUL_TYPE_FILTER = 1;

    /** Konstante f&uuml;r den Modultyp Process */
    public static final int MODUL_TYPE_PROCESS = 7;

    /**
     *  Ermittelt alle Module in der Sektion <code>INITIALIZE</code>, welche mit
     *  dem Start vom Context geladen werden m&uuml;ssen.
     *  @return die Konfigurationen der ermittelten Module als Sektion, kann
     *          kein Modul ermittelt werden, wird eine leeres Sektion
     *          zur&uuml;ckgegeben
     */
    private static Section findModuleSections() {

        Enumeration enumeration;
        Section     section;
        String      filter;
        String      stream;
        String      string;

        //die Basisoptionen werden ermittelt
        section = Section.parse(com.seanox.module.Context.getConfiguration().get("initialize"));

        //alle Parameter werden ermittelt
        enumeration = ((Section)section.clone()).elements();

        //der Filter fuer das Laden ueber den Context wird gesetzt
        filter = ("^\\Q").concat(Context.class.getName()).concat("\\E[^\\.^\\w]+.*$");

        while (enumeration.hasMoreElements()) {

            string = (String)enumeration.nextElement();

            //die Ressource wird ermittelt
            stream = section.get(string);

            if (stream.matches(filter)) continue;

            section.remove(string);
        }

        return section;
    }

    /**
     *  Erweitert den Klassenpfad vom angegebenen ClassLoader um eine Liste noch
     *  nicht enthaltene Bibliotheken.
     *  @param  loader    ClassLoader
     *  @param  libraries Bibliotheken als Liste von File-Objekten
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    private static void expandClassPath(Loader loader, List libraries) throws Exception {

        File     file;
        Iterator iterator;
        List     list;

        if (libraries == null || libraries.isEmpty()) return;

        list = (List)Accession.get(loader, "libraries");

        iterator = libraries.iterator();

        while (iterator.hasNext()) {

            file = (File)iterator.next();

            if (list.contains(file)) continue;

            list.add(file);
        }
    }

    /**
     *  Ermittelt die Liste der Bibliotheken aus der &uuml;bergebenen Sektion
     *  auf Basis vom Parameter <code>libraries</code>.
     *  @param  options Konfiguration
     *  @return die ermittelten Bibliotheken die Liste von File-Objekten
     */
    private static List findLibraries(Section options) {

        File            file;
        Iterator        iterator;
        List            libraries;
        StringTokenizer tokenizer;

        File[]          files;

        libraries = new ArrayList();

        //die Pfade des Klassenpfads werden ermittelt
        tokenizer = new StringTokenizer(options.get(Context.PROPERTIES_LIBRARIES), File.pathSeparator.concat(" "));

        while (tokenizer.hasMoreTokens()) {

            file = new File(tokenizer.nextToken());

            files = file.isFile() ? new File[] {file} : file.listFiles();

            if (files == null) continue;

            iterator = Arrays.asList(files).iterator();

            while (iterator.hasNext()) {

                try {

                    file = ((File)iterator.next()).getCanonicalFile();

                    //es werden nur Dateien beruecksichtigt
                    if (file.isFile()) libraries.add(file);

                } catch (Throwable throwable) {

                    //keine Fehlerbehandlung vorgesehen
                }
            }
        }

        return libraries;
    }

    /**
     *  L&auml;dt das Modul zur &uuml;bergebenen Konfiguration, wozu die
     *  Parameter {@link #PROPERTIES_CONTEXT}, {@link #PROPERTIES_LIBRARIES}
     *  sowie {@link #PROPERTIES_CLASS} herangezogen werden. liegt da Modul
     *  bereits vor wird dessen Instanz, ohne erneute Initialisierung
     *  zur&uuml;ckgegeben, sonst die neu erstellte Instanz.
     *  @param  options Konfiguration
     *  @return die Instanz vom vorliegenden bzw. neu erstellten Modul
     *  @throws IllegalArgumentException bei ung&uuml;tiger Bennenung vom Modul
     *  @throws ClassNotFoundException bei nicht gefundener Klasse zum Modul
     *  @throws InstantiationException bei scheiternder Instanziierung vom Modul
     *  @throws IllegalAccessException bei scheiterndem Zugriff auf die Klassen
     *  @throws SecurityException bei scheiterndem Zugriff auf die Klassen
     *  @throws Exception bei sonstigen Initialisierungsfehlern
     */
    private Context loadModule(String options)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, SecurityException, Exception {

        return this.loadModule(null, options);
    }

    /**
     *  L&auml;dt das Modul zur &uuml;bergebenen Konfiguration, wozu die
     *  Parameter {@link #PROPERTIES_CONTEXT}, {@link #PROPERTIES_LIBRARIES}
     *  sowie {@link #PROPERTIES_CLASS} herangezogen werden. liegt da Modul
     *  bereits vor wird dessen Instanz, ohne erneute Initialisierung
     *  zur&uuml;ckgegeben, sonst die neu erstellte Instanz.
     *  @param  options Konfiguration
     *  @param  scope   optional Context
     *  @return die Instanz vom vorliegenden bzw. neu erstellten Modul
     *  @throws IllegalArgumentException bei ung&uuml;tiger Bennenung vom Modul
     *  @throws ClassNotFoundException bei nicht gefundener Klasse zum Modul
     *  @throws InstantiationException bei scheiternder Instanziierung vom Modul
     *  @throws IllegalAccessException bei scheiterndem Zugriff auf die Klassen
     *  @throws SecurityException bei scheiterndem Zugriff auf die Klassen
     *  @throws Exception bei sonstigen Initialisierungsfehlern
     */
    private Context loadModule(String scope, String options)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, SecurityException, Exception {

        Class   source;
        Context context;
        List    libraries;
        Loader  loader;
        Section section;
        String  mount;

        section = super.parseOptions(options);

        scope = scope == null ? "" : scope.trim().toLowerCase();
        scope = scope.length() == 0 ? section.get(Context.PROPERTIES_CONTEXT).toLowerCase() : scope;
        mount = section.get(Context.PROPERTIES_CLASS);

        if (scope.length() == 0) throw new IllegalArgumentException("Invalid context [empty]");

        synchronized (this) {

            synchronized (this.universe) {

                //das Modul wird ueber den Context im Universum ermittelt
                context = (Context)this.universe.get(scope.concat("\0").concat(mount));

                if (context != null) return context;

                //die Bibliotheken fuer den den Klassenpfads werden ermittelt
                libraries = Context.findLibraries(section);

                //der Application-ClassLoader wird im Universum ermittelt
                loader = (Loader)this.universe.get(scope);

                //liegt zum Context kein Application-ClassLoader im Universum
                //vor, wird dieser eingerichtet, sonst wird der Klassenpfad vom
                //ermittelten bei Bedarf erweitert
                if (loader == null) {

                    //der Application-ClassLoader wird eingerichtet
                    loader = new Loader(libraries, this.getClass().getClassLoader());

                    //der Application-ClassLoader wird im Universum registriert
                    this.universe.put(scope, loader);

                } else Context.expandClassPath(loader, libraries);

                //die Klasse vom Modul wird ermittelt
                source = loader.loadClass(mount);

                //die Instanz vom Modul wird optional eingerichtet
                context = (Context)source.newInstance();

                //die Initialisierung Modulen ist optional
                //die Konfiguration wird dabei als String erwartet
                context.initialize(options);

                //das Modul wird im Universum registriert
                this.universe.put(scope.concat("\0").concat(mount), context);

                return context;
            }
        }
    }

    /**
     *  L&auml;dt alle Module in der Sektion <code>INITIALIZE</code>, welche mit
     *  dem Start vom Context geladen werden m&uuml;ssen.
     */
    private void loadModules() {

        Enumeration enumeration;
        Section     options;
        Section     section;
        String      context;

        section = Context.findModuleSections();

        enumeration = section.elements();

        while (enumeration.hasMoreElements()) {

            context = (String)enumeration.nextElement();
            options = super.parseOptions(section.get(context));

            //namenlose Module koennen nicht zugeordnet werden und werden daher
            //bei der Initialisierung ignoriert
            if (context.length() == 0) continue;

            //das Modul wird initial eingerichtet, bereits vorliegende Module
            //werden ignoriert und nicht erneut initiiert
            try {this.loadModule(context, section.get(context));
            } catch (Throwable throwable) {

                if (!options.contains("*") || !(throwable instanceof ClassNotFoundException)) {

                    com.seanox.module.Context.print(("MODULE ").concat(context).concat(" SUPPLY FAILED"));
                    com.seanox.module.Context.print(throwable);
                }
            }
        }
    }

    /**
     *  Ermittelt den erforderlichen Context. Diese Unterscheidung ist nur
     *  f&uuml;r Module mit eigenem (Application)ClassLoader erforderlich.
     *  @param  object Bezugsobjekt
     *  @return der ermittelte Context
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    private Context getContext(Object object) throws Exception {

        Context     context;
        Environment environment;
        String      options;

        if (!this.getClass().equals(Context.class)) return this;

        environment = new Environment(object);

        //die Konfiguration wird ermittelt
        options = environment.get("module_opts");

        context = this.loadModule(options);

        return (context == null) ? this : context;
    }

    /**
     *  R&uuml;ckgabe der Kennung des Moduls.
     *  @return die Kennung im Format <code>[PRODUCER-MODULE/VERSION]</code>
     */
    public String getCaption() {

        return ("Seanox-Module-Context/").concat(Context.VERSION);
    }

    /**
     *  Einsprung f&uuml;r die Initialisierung des Moduls.
     *  @param  options Konfiguration
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    public void initialize(String options) throws Exception {

        Section section;

        section = Section.parse(super.parseOptions(options).toString());

        if (!this.getClass().equals(Context.class)) {

            this.initialize(section);

            return;
        }

        synchronized (this) {

            //Der Modul-Container vom Server unterstzetzt nur eine Instanz einer
            //Klasse zur Laufzeit. Context-Module lassen aber mehrere Instanzen
            //zu, wozu diese diese in einem zweiten Modul-Container im
            //Basis-Context-Modul verwalte werden. Der Server initiiert das
            //Basis-Context-Modul jedoch einmalig. Damit muss die erste Instanz
            //alle anderen Context-Module initialisieren.
            //Liegt mindestens ein Context-Modul in der Sektion INITIALIZE vor,
            //werden nachfolgend alle weiteren Context-Module in dieser Sektion
            //ermittelt und von der Basis-Context-Instanz initialisiert.
            //Context-Module, welche nicht in dieser Sektion definiert sind,
            //werden mit dem ersten Aufruf ueber die Bind-Methode geladen und
            //initialisiert.

            if (this.universe == null) {

                this.universe = new Hashtable();

                this.loadModules();
            }
        }
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
     *  Einsprung zur Anbindung von Filter im HTTP-Context.
     *  @param  filter Bezugssobjekt
     *  @throws UnsupportedOperationException wenn die Methode f&uuml;r den
     *          Modultype nicht implementiert wurde
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    protected void service(Filter filter) throws Exception {

        throw new UnsupportedOperationException("Module type filter not supported");
    }

    /**
     *  Einsprung zur Anbindung von Process im HTTP-Context.
     *  @param  process Bezugssobjekt
     *  @throws UnsupportedOperationException wenn die Methode f&uuml;r den
     *          Modultype nicht implementiert wurde
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    protected void service(Process process) throws Exception {

        throw new UnsupportedOperationException("Module type process not supported");
    }

    /**
     *  Einsprung zur Anbindung von Service im HTTP-Context.
     *  @param  service Bezugssobjekt
     *  @throws UnsupportedOperationException wenn die Methode f&uuml;r den
     *          Modultype nicht implementiert wurde
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    protected void service(Service service) throws Exception {

        throw new UnsupportedOperationException("Module type service not supported");
    }

    /**
     *  Einsprung f&uuml;r die HTTP-Modul-API mit dem notwendigen Mount-Objekt.
     *  Es erfolgt die Ermittlung des Modul-Typs und der Aufruf der
     *  entsprechenden Einsprungsmethode.<br><br>
     *  <b>Hinweis</b> - Das &Uuml;berschreiben essentielle Methode der
     *  HTTP-Modul-API sollte nur in Ausnahmef&auml;llen erfolgen.
     *  @param  object Bezugsobjekt
     *  @param  type   Modultyp
     *  @throws UnsupportedOperationException wenn die Methode f&uuml;r den
     *          Modultype nicht implementiert wurde
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     *  @see    #MODUL_TYPE_FILTER
     *  @see    #MODUL_TYPE_PROCESS
     *  @see    #MODUL_TYPE_SERVICE
     */
    public void bind(Object object, int type) throws Exception {

        Context context;

        if (object == null) throw new IllegalArgumentException("Invalid object [null]");

        try {

            context = this.getContext(object);

            switch (type) {

                case com.seanox.module.http.Context.MODUL_TYPE_FILTER:

                    context.service((Filter)(object = new Filter(context, object)));

                    break;

                case com.seanox.module.http.Context.MODUL_TYPE_PROCESS:

                    context.service((Process)(object = new Process(context, object)));

                    break;

                case com.seanox.module.http.Context.MODUL_TYPE_SERVICE:

                    context.service((Service)(object = new Service(context, object)));

                    break;

                default: throw new UnsupportedOperationException(("Module type (").concat(String.valueOf(type)).concat(") not supported"));
            }

        } finally {

            if (object instanceof com.seanox.module.http.Extension) {

                try {object = Accession.get(object, "response");
                } catch (Exception exception) {

                    //keine Fehlerbehandlung vorgesehen
                }
            }

            if (object instanceof Response && ((Response)object).isUsable()) {

                try {((Response)object).sendStatus(Response.STATUS_NO_CONTENT);
                } catch (Exception exception) {

                    //keine Fehlerbehandlung vorgesehen
                }

                try {((Response)object).flush();
                } catch (Exception exception) {

                    //keine Fehlerbehandlung vorgesehen
                }
            }
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

        return com.seanox.module.Context.mountField(object, field, false);
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

        return com.seanox.module.Context.mountField(object, field, clone);
    }

    /**
     *  Ermittelt die einzelnen Optionen der &uuml;bergebene Optionszeile im
     *  Format <code>[OPTION:VALUE]</code>. Die Maskierung von [ erfolgt dabei
     *  durch [[ und von  ] als ]].
     *  @param  options Optionszeile
     *  @return die ermittelten Optionen als Parameter
     */
    protected static Section parseOptions(String options) {

        return com.seanox.module.Context.parseOptions(options);
    }

    /**
     *  Einsprung des Service zum beenden der Instanz.
     *  @throws Exception allgemein auftretende nicht behandelte Fehler
     */
    public void destroy() throws Exception {

        Enumeration enumeration;

        if (!this.getClass().equals(Context.class) || this.universe == null) return;

        synchronized (this.universe) {

            //alle Instanzen werden ermittelt
            enumeration = this.universe.elements();

            while (enumeration.hasMoreElements()) {

                //die Instanz wird zum Beenden aufgefordert
                try {((Context)enumeration.nextElement()).destroy();
                } catch (Throwable throwable) {

                    //keine Fehlerbehandlung vorgesehen
                }
            }

            this.universe.clear();
        }
    }
}