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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.seanox.common.Accession;
import com.seanox.common.Codec;
import com.seanox.module.Environment;
import com.seanox.network.http.Fragment;

/**
 *  Abstrakte Klasse zur Implementierung eines Request, welche Konstanten und
 *  Methoden zur Verarbeitung von HTTP-Anfragen zur Verf&uuml;gung stellt und
 *  dabei einen einfachen und gezielten Umgang mit dem Header,
 *  Multipart-Objekten und dem Datenstrom gestattet.<br>
 *  <br>
 *  Request 1.2013.0427<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0427
 */
public abstract class Request extends InputStream {
    
    /** Systemumgebung */
    protected Environment environment;

    /** Dateneingangsstrom */
    protected InputStream input;

    /** Hashtable der Headerfelder */
    protected Hashtable fields;

    /** Hashtable der Requestparameter */
    protected Hashtable parameters;

    /** Hashtable der Requestsegmente */
    protected Hashtable segments;

    /** Hashtable der Requestattribute */
    protected Hashtable attributes;

    /** Header vom Request */
    protected String header;

    /** UniqueId vom Request */
    protected String signature;

    /** Auslagerungsverzeichnis */
    protected String storage;

    /** Cookies vom Request */
    protected Cookie[] cookies;

    /** Fragmente vom Request */
    protected Fragment[] fragments;

    /** Status der Sperrung des Request */
    protected boolean locked;

    /** Status der Sperrung vom Body */
    protected boolean bounded;

    /** Status der Request Aufbereitung */
    protected boolean control;

    /** Status der sicheren Serververbindung */
    protected boolean secure;

    /** Anzahl der gelieferten Datenmenge */
    protected long length;

    /** Zeitpunkt der letzten Systemunterbrechung */
    protected long timing;

    /** Anzahl der gelesenen Datenmenge */
    protected long volume;

    /** Konstruktor, richtet den Request ein. */
    protected Request() {
        
        this.fields     = new Hashtable();
        this.parameters = new Hashtable();
        this.segments   = new Hashtable();
        this.attributes = new Hashtable();
        this.fragments  = new Fragment[0];
        this.cookies    = new Cookie[0];

        this.timing = System.currentTimeMillis();
        this.length = Long.MIN_VALUE;
    }

    /**
     *  Unterbricht die aktuelle Verarbeitung f&uuml;r die angegebene
     *  Millisekunden. Die Unterbrechung erfolgt in Abh&auml;ngigkeit der
     *  angegebenen maximal zul&auml;ssigen Laufzeit.
     *  @param idle Unterbrechung in Millisekunden
     */
    protected void sleep(long idle) {

        if ((System.currentTimeMillis() -this.timing) > 20) {

            try {Thread.sleep(idle);
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            this.timing = System.currentTimeMillis();
        }
    }
    
    /** Parst den Headers und richtet die Cookies als Array ein. */
    protected abstract void parseCookies();

    /**
     *  Liest den kompletten Request entsprechend dem Feld Parameter
     *  <code>Content-Length</code> ein und ermittelt die enthaltenen Parameter
     *  und Dateien.<br><br>
     *  <b>Hinweis</b> - Die &Uuml;bernahme eines Parameters erfolgt als ein
     *  Array von ByteArrays. Dadurch k&ouml;nnen im Request mehrfach enthaltene
     *  Parameter als Menge &uuml;bernommen werden. Die Ablage des Wertes als
     *  ByteArray ist notwendig, da der Wert in String konvertiert, durch die
     *  Java Laufzeitumgebung bestimmte Zeichen verlieren kann. Somit kann der
     *  Wert nicht nur als String, sondern sp&auml;ter auch als reines ByteArray
     *  verarbeitet werden.<br><br>
     *  Das parsen des Request Bodys kann nur einmal erfolgen. Sollen die Daten
     *  an anderer Stelle erneut zur Verf&uuml;gung stehen, muss dies durch die
     *  Anwendung implementiert werden.
     *  @throws OutOfMemoryError bei Speichermangel beim Auslesen des Request
     *  @throws IOException bei fehlerhaftem Zugriff auf den Request oder den
     *          Datenstrom, beim Erreichen der maximalen Datenleerlaufzeit oder
     *          bei ung&uuml;ltigen Strukturen beim
     *          Multipart-Request
     */
    protected abstract void parseBody() throws IOException;
    
    /**
     *  Parst den String nach Parametern und &uuml;bernimmt diese.
     *  @param string zu parsender String
     */
    protected void setInternalParameter(String string) {

        StringTokenizer tokenizer;
        String          entry;
        String          stream;

        Object[]        entries;
        Object[]        objects;

        int             cursor;

        //die Elemente des Query Strings werden ermittelt
        tokenizer = new StringTokenizer(string, "&");

        while (tokenizer.hasMoreTokens()) {

            //der Name und der Wert wird ermittelt
            string = tokenizer.nextToken();
            cursor = string.indexOf('=');
            stream = (cursor < 0) ? "" : string.substring(cursor +1).trim();
            string = (cursor < 0) ? string.trim() : string.substring(0, cursor).trim();
            entry  = Codec.decode(string, Codec.MIME).trim();
            entry  = (entry.length() == 0 && string.length() > 0) ? string : entry;

            if (entry.length() == 0) continue;

            //das Array der Parametereintraege wird aufgebaut
            entries = (Object[])this.parameters.get(entry);
            entries = (entries == null) ? new Object[0] : entries;
            objects = new Object[entries.length +1];

            //der Parameter wird als ByteArray uebernommen
            System.arraycopy(entries, 0, objects, 0, entries.length);
            objects[entries.length] = Codec.decode(stream.getBytes(), Codec.MIME);

            //der Parametereintrag uebernommen
            this.parameters.put(entry, objects);
        }
    }

    /**
     *  Erstellt aus dem ByteArray ein Fragment und &uuml;bernimmt dieses je
     *  nach Typ als Parameter oder Datei Fragment.
     *  @param fragment Fragment mit den Metainformationen
     *  @param content  Content des Fragments als ByteArray
     */
    protected void setInternalFragment(Fragment fragment, byte[] content) {

        String     string;

        Fragment[] fragments;
        Object[]   entries;
        Object[]   objects;

        if (fragment.containsParameter("filename")) {

            //der Dateninhalt wird nachtraeglich gesetzt
            try {Accession.set(fragment, "content", content);
            } catch (Exception exception) {

                throw new RuntimeException(exception);
            }

            //das Fragment wird uebernommen
            fragments = new Fragment[this.fragments.length +1];
            System.arraycopy(this.fragments, 0, fragments, 0, this.fragments.length);
            fragments[this.fragments.length] = fragment;
            this.fragments = fragments;

        } else if (fragment.containsField("content-disposition")) {

            //der Name des Parameters wird ermittelt
            string = fragment.getParameter("name").trim();

            if (string.length() == 0) return;

            //das Array der Parametereintraege wird aufgebaut
            entries = (Object[])this.parameters.get(string);
            entries = (entries == null) ? new Object[0] : entries;
            objects = new Object[entries.length +1];

            //der Parameter wird als ByteArray uebernommen
            System.arraycopy(entries, 0, objects, 0, entries.length);
            objects[entries.length] = content;

            //der Parametereintrag uebernommen
            this.parameters.put(string, objects);
        }
    }

    /**
     *  Ermittelt den Wert des angegebenen Request Segments. R&uuml;ckgabe, der
     *  Wert des angegebenen Segments. Kann dieses nicht ermittelt werden, wird
     *  ein leerer String zur&uuml;ck gegeben.
     *  @param  name Names Segments
     *  @return der Wert des angegebenen Segments
     */
    protected String getInternalSegment(String name) {

        //der Feldname wird fuer die Suche im Header vereinfacht
        name = (name == null) ? "" : name.trim().toLowerCase();

        //der Wert des Feldeintrags wird ermittelt
        name = (String)this.segments.get(name);

        return (name == null) ? "" : name.trim();
    }

    /**
     *  Ermittelt einen eindeutigen Dateinamen f&uuml;r Auslagerungsdateien.
     *  R&uuml;ckgabe der ermittelte Dateiname f&uuml;r Auslagerungsdateien.
     *  @return der ermittelte Dateiname f&uuml;r Auslagerungsdateien
     */
    protected String getNextStorageFileName() {

        String string;

        string = this.signature;

        if (string.length() == 0) {

            //die aktuelle Objektkennung wird ermittelt
            string = Long.toString(Math.abs(this.getClass().hashCode()), 36);

            //die Objektkennung wird auf 6 Zeichen aufgefuellt
            while (string.length() < 6) string = ("0").concat(string);

            //die eindeutige Auslagerungskennung wird zusammengesetzt
            string = Long.toString(System.currentTimeMillis(), 36).concat(string).toUpperCase();
        }

        string = string.concat("-").concat(Integer.toString(this.fragments.length, 36));
        string = ("storage-").concat(string.toUpperCase()).concat(".swap");

        return string;
    }

    /**
     *  Ermittelt und setzt die mit der Content-Length angegebene Datenmenge
     *  um die Read-Methoden besser kontrollieren zu k&ouml;nnen.
     */
    protected void readDataLength() {

        try {this.length = Long.parseLong(this.getHeaderField("content-length"));
        } catch (Exception exception) {

            this.length = -1;
        }

        if (this.length < 0) this.length = -1;
    }
    
    /**
     *  Formatiert das &uuml;bergebene Feld in ein Header-Feld.
     *  R&uuml;ckgabe der formatierte Feldname.
     *  @param  field zu formatierender Feldname
     *  @return formatierter Feldname
     */
    protected static String formHeaderField(String field) {

        String          token;
        StringBuffer    result;
        StringTokenizer tokenizer;

        //die Fragmente des Feldnamens werden ermittelt
        tokenizer = new StringTokenizer((field == null) ? "" : field, "-");

        for (result = new StringBuffer(); tokenizer.hasMoreTokens();) {

            token = tokenizer.nextToken().toLowerCase();
            field = token.substring(0, 1).toUpperCase();
            token = token.length() > 1 ? token.substring(1) : "";
            
            if (result.length() > 0) result.append("-");
            
            result.append(field).append(token);
        }

        return result.toString();
    }
    
    /**
     *  Ermittelt den HTTP-Header aus einzelnen Informationen vom Request.
     *  R&uuml;ckgabe der aufgebaute HTTP-Header als String.
     *  @return der ermittelte HTTP-Header
     */
    protected String getHeader() {

        Enumeration  enumeration;
        StringBuffer result;
        String       string;

        String[]     strings;

        int          loop;
        
        if (this.header != null) return this.header;

        result = new StringBuffer();
        
        //die erste Zeile des Headers wird ermittelt
        result.append(this.getMethod().toUpperCase().trim());
        result.append((" ").concat(this.environment.get("request_uri")).trim());
        result.append((" ").concat(this.getProtocol().toUpperCase()).trim());

        //die Felder des Headers werden ermittelt
        enumeration = this.fields.keys();

        while (enumeration.hasMoreElements()) {

            string  = (String)enumeration.nextElement();
            strings = this.getHeaderFields(string);

            for (loop = 0; loop < strings.length || loop == 0; loop++) {

                result.append("\r\n").append(Request.formHeaderField(string)).append(": ");
                
                if (strings.length > 0) result.append(strings[loop]);
            }
        }
        
        this.header = result.toString();

        return this.header;
    }    

    /**
     *  Pr&uuml;ft ob das angegebene Feld im Header des Request enthalten ist.
     *  R&uuml;ckgabe <code>true</code>, sonst <code>false</code> wenn dieses
     *  nicht im Header des Request enthalten ist.
     *  @param  name Name des Feldes
     *  @return <code>true</code> wenn das Feld im Header enthalten ist
     */
    public boolean containsHeaderField(String name) {

        return (name == null || name.indexOf(':') >= 0) ? false : this.fields.containsKey(name.trim().toLowerCase());
    }

    /**
     *  R&uuml;ckgabe aller Felder des Headers als Enumeration.
     *  @return alle Felder des Headers als Enumeration
     */
    public Enumeration getHeaderFields() {

        StringTokenizer tokenizer;
        Vector          vector;

        //der Vektor wird eingerichtet
        vector = new Vector();

        //die Liste der Felder des Headers werder ermittelt
        tokenizer = new StringTokenizer(this.getInternalSegment("fields"), "\r\n");

        //die Liste der Felder des Headers wird aufgebaut
        while (tokenizer.hasMoreTokens()) vector.addElement(tokenizer.nextToken().trim());

        return vector.elements();
    }

    /**
     *  Ermittelt den Wert des angegebenen Felds im Header des Request.
     *  R&uuml;ckgabe, der Wert des angegebenen Felds. Kann dieses nicht
     *  ermittelt werden, wird ein leerer String zur&uuml;ck gegeben. Sind
     *  mehrere gleichnamige Felder im Header des Request enthalten, wird der
     *  Wert des ersten Felds zur&uuml;ck gegeben.
     *  @param  name Name Felds
     *  @return der Wert des angegebenen Felds
     */
    public String getHeaderField(String name) {

        String[] result;

        //der Inhalt des Felds wird ermittelt
        result = this.getHeaderFields(name);

        return (result.length == 0) ? "" : result[0];
    }

    /**
     *  Ermittelt die Werte des angegebenen Felds im Header des Request.
     *  R&uuml;ckgabe, die ermittelten Werte als String Array. Kann das Feld
     *  nicht ermittelt werden, wird ein leeres Array zur&uuml;ck gegeben. Zur
     *  Ermittlung ob das Feld im Header enthalten ist, steht die Methode
     *  <code>Request.containsField(String name)</code> zur Verf&uuml;gung.
     *  @param  name Name des Felds
     *  @return die Werte des Felds als String Array
     */
    public String[] getHeaderFields(String name) {

        String[] result;

        //der Feldname wird fuer die Suche im Header vereinfacht
        name = (name == null) ? "" : name.trim().toLowerCase();
        name = (name.indexOf(':') >= 0) ? "" : name;

        //der Inhalt des Felds wird ermittelt
        result = (String[])this.fields.get(name);
        result = (result == null) ? new String[0] : result;

        //die Rueckgabe erfolgt als Kopie zur Verhinderung von Manipulationen
        return (String[])result.clone();
    }

    /**
     *  Pr&uuml;ft ob der angegebene Parameter im Request enthalten ist.
     *  R&uuml;ckgabe <code>true</code>, sonst <code>false</code><br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @param  name Name des Parameters
     *  @return <code>true</code>, wenn der Parameter im Request enthalten ist
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public boolean containsParameter(String name) throws IOException {

        //der Request Body wird bei Bedarf geparst
        if (!this.locked && !this.bounded && !this.control) this.parseBody();

        return (name == null) ? false : this.parameters.containsKey(name.trim());
    }

    /**
     *  R&uuml;ckgabe aller Parameter des Request als Enumeration.<br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @return alle Parameter als Enumeration
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public Enumeration getParameters() throws IOException {

        //der Request Body wird bei Bedarf geparst
        if (!this.locked && !this.bounded && !this.control) this.parseBody();

        return this.parameters.keys();
    }

    /**
     *  Ermittelt den Wert des angegebenen Parameters im Request. Kann der
     *  Parameter nicht ermittelt werden, wird ein leerer String zur&uuml;ck
     *  gegeben. Zur Ermittlung ob der Parameter im Request enthalten ist steht
     *  die Funktion <code>Request.containsParameter(String name)</code> zur
     *  Verf&uuml;gung. Wurden dem Parameter im Request mehrfach Werte
     *  zugewiesen, wird immer der erste Wert zur&uuml;ck gegeben. F&uuml;r die
     *  Ermittlung aller Werte eines Parameters steht die Methode
     *  <code>Request.getParameters(String name)</code> zur Verf&uuml;gung.<br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @param  name Name des Parameters im Request
     *  @return der Wert des Parameters als String
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public String getParameter(String name) throws IOException {

        //der Wert des Parameters wird ermittelt
        return new String(this.getByteField(name));
    }

    /**
     *  Ermittelt alle Werte des angegebenen Parameters im Request als String
     *  Array. Kann der Parameter nicht ermittelt werden, wird ein leeres Array
     *  zur&uuml;ck gegeben. Zur Ermittlung ob der Parameter enthalten ist,
     *  steht die Methode <code>Request.containsParameter(String name)</code>
     *  zur Verf&uuml;gung.<br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @param  name Name des Parameters im Request
     *  @return die Wert des Parameters als String Array
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public String[] getParameters(String name) throws IOException {

        Object[] objects;
        String[] fields;
        String[] result;

        int      loop;

        //der Request Body wird bei Bedarf geparst
        if (!this.locked && !this.bounded && !this.control) this.parseBody();

        //das StringArray wird eingerichtet
        result = new String[0];

        //die Werte des Parameters werden ermittelt
        objects = (name != null) ? (Object[])this.parameters.get(name) : null;

        for (loop = 0; objects != null && loop < objects.length; loop++) {

            name = new String((byte[])objects[loop]);

            fields = new String[result.length +1];
            System.arraycopy(result, 0, fields, 0, result.length);
            fields[result.length] = name;
            result = fields;
        }

        return result;
    }

    /**
     *  Pr&uuml;ft ob der angegebene Parameter im Request enthalten ist.
     *  R&uuml;ckgabe <code>true</code>, sonst <code>false</code> wenn dieser
     *  nicht enthalten ist.<br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @param  name Name des Parameters
     *  @return <code>true</code>, wenn der Parameters enthalten ist
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public boolean containsByteField(String name) throws IOException {

        //der Request Body wird bei Bedarf geparst
        if (!this.locked && !this.bounded && !this.control) this.parseBody();

        return (name == null) ? false : this.parameters.containsKey(name.trim());
    }

    /**
     *  R&uuml;ckgabe der Werte als Bytes aller Parameter im Request.<br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @return alle Werte der Parameter als Enumeration
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public Enumeration getByteFields() throws IOException {

        //der Request Body wird bei Bedarf geparst
        if (!this.locked && !this.bounded && !this.control) this.parseBody();

        return this.parameters.keys();
    }

    /**
     *  Ermittelt den Wert als Bytes zum angegebenen Parameters im Request. Kann
     *  der Parameter nicht ermittelt werden, wird ein leeres Byte-Array
     *  zur&uuml;ck gegeben. Zur Ermittlung ob der Parameter enthalten ist, steht
     *  die Methode <code>Request.containsByteField(String name)</code> zur
     *  Verf&uuml;gung. Wurden dem Parameter im Request mehrfach Werte
     *  zugewiesen, wird immer der erste Wert zur&uuml;ck gegeben. F&uuml;r die
     *  Ermittlung aller Werte eines Parameters steht die Funktion
     *  <code>Request.getByteFields(String name)</code> zur Verf&uuml;gung.<br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @param  name Name des Parameters im Request
     *  @return der Wert als Byte-Array
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public byte[] getByteField(String name) throws IOException {

        Object[] result;

        //die Werte des Parameters werden ermittelt
        result = this.getByteFields(name);

        return (result.length == 0) ? new byte[0] : (byte[])result[0];
    }

    /**
     *  Ermittelt alle Werte als Bytes zum angegebenen Parameters im Request.
     *  Kann der Parameter nicht ermittelt werden, wird ein leeres Array
     *  zur&uuml;ck gegeben. Zur Ermittlung, ob der Parameter enthalten ist,
     *  steht die Methode <code>Request.containsParameter(String name)</code>
     *  zur Verf&uuml;gung.<br><br>
     *  <b>Hinweis</b> - Im "bounded" Modus wird der Body vom Request nicht
     *  gelesen und geparst, somit sind in diesem Fall nur die Parameter der
     *  Query aus der URL enthalten.
     *  @param  name Name des Parameters im Request
     *  @return alle Werte des Parameters als Objekt-Array
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public Object[] getByteFields(String name) throws IOException {

        Object[] result;

        //der Request Body wird bei Bedarf geparst
        if (!this.locked && !this.bounded && !this.control) this.parseBody();

        //die Werte des Parameters werden ermittelt
        result = (name != null) ? (Object[])this.parameters.get(name) : null;

        if (result == null) result = new Object[0];

        //die Rueckgabe erfolgt als Kopie zur Verhinderung von Manipulationen
        return (Object[])result.clone();
    }

    /**
     *  R&uuml;ckgabe vom per Namen angeforderten Attribute. Kann das Attribut
     *  nicht ermittelt werden, wird <code>null</code> zur&uuml;ckgegeben.
     *  @param  name Name des Attributes
     *  @return das zum Attribut abgelegt Objekt, kann das Attribut nicht
     *          ermittelt werden, wird <code>null</code> zur&uuml;ckgegeben
     */
    public Object getAttribute(String name) {

        if (name == null) return null;

        return this.attributes.get(name);
    }

    /**
     *  R&uuml;ckgabe aller Namen der vorgehaltenen Attribute.
     *  @return alle Namen der vorgehaltenen Attribute
     */
    public Enumeration getAttributes() {

        return this.attributes.elements();
    }

    /**
     *  Entfernt das per Namen angegebene Attribute.
     *  @param  name Name des Attributes
     *  @throws IllegalStateException wenn der Request im gesch&uuml;tzten Modus
     *          verwendet wird
     */
    public void removeAttribute(String name) {

        if (this.locked) throw new IllegalStateException("Request is locked");

        if (name != null) this.attributes.remove(name);
    }

    /**
     *  Setzt das Objekt f&uuml;r das per Namen angegebene Attribut. Liegt das
     *  Attribut bereits vor, wird es &uuml;berschrieben. Die Angabe von
     *  <code>null</code> sind f&uuml;r Namen noch Objekt m&ouml;glich.
     *  @param  name   Name des Attributes
     *  @param  object Objekt
     *  @throws IllegalStateException wenn der Request im gesch&uuml;tzten Modus
     *          verwendet wird
     */
    public void setAttribute(String name, Object object) {

        if (this.locked) throw new IllegalStateException("Request is locked");

        if (name == null) throw new IllegalArgumentException("Attribute name required");

        if (object == null) throw new IllegalArgumentException("Attribute object required");

        this.attributes.put(name, object);
    }

    /**
     *  R&uuml;ckgabe der im Request enthaltenen Cookies als Array.
     *  @return die im Request enthaltenen Cookies als Array
     */
    public Cookie[] getCookies() {

        return this.cookies;
    }

    /**
     *  R&uuml;ckgabe der im Request enthaltenen Datei Fragmente als Array.
     *  @return die im Request enthaltenen Datei Fragmente als Array
     *  @throws IOException bei fehlerhaftem Zugriff auf Request oder Datenstrom
     */
    public Fragment[] getFragments() throws IOException {

        //der Request Body wird bei Bedarf geparst
        if (!this.locked && !this.bounded && !this.control) this.parseBody();

        return this.fragments;
    }
    
    /**
     *  Deaktiviert bzw. Aktiviert (Standard) das Lesen und Parsen vom Body des
     *  Requests. Durch die Option <code>true</code> kann z.B. auf den Query
     *  zugegriffen werden, ohne das der Body geladen wird.
     *  @param  option <code>true</code>deaktiviert, <code>false</code> aktiviert
     *  @throws IllegalStateException wenn der Request bereits ausgelesen wurde
     */
    public void setBounded(boolean option) {

        if (this.locked)  throw new IllegalStateException("Request is locked");
        if (this.control) throw new IllegalStateException("Request body already read");

        this.bounded = option;
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn das Verarbeiten vom Body des
     *  Requests deaktiviert ist, sonst <code>false</code> (Standard).
     *  @return <code>true</code> das Verarbeiten vom Body ist deaktiviert
     */
    public boolean isBounded() {

        return this.bounded;
    }

    /**
     *  R&uuml;ckgabe des aktuellen Auslagerungsverzeichnis.
     *  @return das aktuelle Auslagerungsverzeichnis
     */
    public String getStorage() {

        return this.storage;
    }

    /**
     *  Setzt das aktuelle Auslagerungsverzeichnis.<br><br>
     *  <b>Hinweis</b> - Der Wert <code>null</code> hebt die Verwendung des
     *  Auslagerungsverzeichnis auf, Datei Fragmente beim MultiPart-Request
     *  werden somit komplett in den Speicher geladen. Ein leerer String
     *  verwendet das aktuelle Arbeitsverzeichnis. Eine &Uuml;berpr&uuml;fung der
     *  G&uuml;ltigkeit des Pfads oder verf&uuml;gbaren Speicherkapazit&auml;t
     *  auf dem Auslagerungsmedium erfolgt nicht und f&uuml;hrt erst bei der
     *  Benutzung zu einer entsprechenden Ausnahme (z.B. IOException).
     *  @param  storage Pfad des Auslagerungsverzeichnis
     *  @throws IllegalStateException wenn der Request im gesch&uuml;tzten Modus
     *          verwendet wird
     */
    public void setStorage(String storage) {

        if (this.locked) throw new IllegalStateException("Request is locked");

        this.storage = storage;
    }

    /**
     *  R&uuml;ckgabe der Methode vom Request.
     *  @return die Methode vom Request
     */
    public String getMethod() {

        return this.environment.get("request_method");
    }

    /**
     *  R&uuml;ckgabe vom Protokolls vom Request.
     *  @return das Protokoll vom Request
     */
    public String getProtocol() {

        return this.environment.get("server_protocol");
    }

    /**
     *  R&uuml;ckgabe vom Query String vom Request.
     *  @return der Query String vom Request
     */
    public String getQueryString() {

        return this.environment.get("query_string");
    }

    /**
     *  R&uuml;ckgabe des Schemas der Serverbindung.
     *  @return das Schema der Serverbindung
     */
    public String getScheme() {

        String string;

        int    cursor;

        string = this.environment.get("script_uri");
        string = string.replace('\\', '/');
        cursor = string.indexOf("://");
        string = (cursor < 0) ? "" : string.substring(0, cursor);

        return string.trim().toLowerCase();
    }

    /**
     *  R&uuml;ckgabe der URL (Path) als String (Bsp. /path).
     *  @return die URL (Path) vom Request als String
     */
    public String getURL() {

        return this.environment.get("script_url");
    }

    /**
     *  R&uuml;ckgabe der URI als String (Bsp. http://server:123/path).
     *  @return die URI vom Request als String
     */
    public String getURI() {

        return this.environment.get("script_uri");
    }

    /**
     *  R&uuml;ckgabe der L&auml;nge des mit dem Request &uuml;bermittelten
     *  Inhalts (Content-Length). Dieser Wert wird direkt aus dem Request-Header
     *  ermittelt und wird nicht weiter gepr&uuml;ft. Wurde diese Angabe mit dem
     *  Request nicht &uuml;bermittelt, wird ein Wert &lt; 1 zur&uuml;ck gegeben.
     *  @return die L&auml;nge des mit dem Request &uuml;bermittelten Inhalts,
     *          kann diese nicht ermittelt werden, wird ein Wert &lt; 1
     *          zur&uuml;ck gegeben
     */
    public int getContentLength() {

        try {return Integer.parseInt(this.getHeaderField("Content-Length"));
        } catch (Exception exception) {

            return -1;
        }
    }    

    /**
     *  R&uuml;ckgabe der im Datenstrom verf&uuml;gbaren Anzahl von Bytes.
     *  @return die Anzahl der im Datenstrom verf&uuml;gbaren Bytes
     *  @throws IOException bei Fehler durch den Zugriff auf den Datenstrom
     *  @throws IllegalStateException wenn der Request im gesch&uuml;tzten Modus
     *          verwendet wird
     */
    public int available() throws IOException {

        //im eingeschraenkten Modus wird IllegalStateException zurueckgegeben
        if (this.locked)  throw new IllegalStateException("Request is locked");
        if (this.bounded) throw new IllegalStateException("Request is bounded");

        //die verfuegbaren Daten im Datenstrom werden ermittelt
        return this.input.available();
    }

    /**
     *  Liest das im Datenstrom verf&uuml;gbare Byte.
     *  @return das gelesene Byte
     *  @throws IOException bei Fehler durch den Zugriff auf den Datenstrom
     *  @throws IllegalStateException wenn der Request im gesch&uuml;tzten Modus
     *          verwendet wird
     */
    public int read() throws IOException {

        int result;

        //im eingeschraenkten Modus wird IllegalStateException zurueckgegeben
        if (this.locked)  throw new IllegalStateException("Request is locked");
        if (this.bounded) throw new IllegalStateException("Request is bounded");

        //der Aufbereitungsstatus wird gesetzt
        this.control = true;

        if (this.length == Long.MIN_VALUE) this.readDataLength();

        if (this.length >= 0 && this.length <= this.volume) return -1;

        //die Daten werden ausgelesen
        result = this.input.read();

        if (result >= 0) this.volume += 1;

        return result;
    }

    /**
     *  Liest die im Datenstrom verf&uuml;gbaren Daten und &uuml;bernimmt diese
     *  in das angebene ByteArray ab der angegebenen Position und L&auml;nge.
     *  @param  bytes  zuf&uuml;llende ByteArray
     *  @param  offset Position im ByteArray ab der die Bytes gesetzt werden
     *  @param  length Anzahl der zu lesenden Bytes
     *  @return die Anzahl der gelesenen Bytes
     *  @throws IOException bei Fehler durch den Zugriff auf den Datenstrom
     *  @throws IllegalStateException wenn der Request im gesch&uuml;tzten Modus
     *          verwendet wird
     */
    public int read(byte[] bytes, int offset, int length) throws IOException {

        int volume;

        //im eingeschraenkten Modus wird IllegalStateException zurueckgegeben
        if (this.locked)  throw new IllegalStateException("Request is locked");
        if (this.bounded) throw new IllegalStateException("Request is bounded");

        //der Aufbereitungsstatus wird gesetzt
        this.control = true;

        if (bytes == null) return 0;

        if (this.length == Long.MIN_VALUE) this.readDataLength();

        if (this.length >= 0 && this.length <= this.volume) return -1;

        //die Daten werden ausgelesen
        this.volume += volume = this.input.read(bytes, offset, length);

        return volume;
    }

    /**
     *  Liest die im Datenstrom verf&uuml;gbaren Daten und &uuml;bernimmt diese
     *  in das &uuml;bergebene ByteArray.
     *  @param  bytes zuf&uuml;llende ByteArray
     *  @return die Anzahl der gelesenen Bytes
     *  @throws IOException bei Fehler durch den Zugriff auf den Datenstrom
     *  @throws IllegalStateException wenn der Request im gesch&uuml;tzten Modus
     *          verwendet wird
     */
    public int read(byte[] bytes) throws IOException {

        int volume;

        //im eingeschraenkten Modus wird IllegalStateException zurueckgegeben
        if (this.locked)  throw new IllegalStateException("Request is locked");
        if (this.bounded) throw new IllegalStateException("Request is bounded");

        //der Aufbereitungsstatus wird gesetzt
        this.control = true;

        if (bytes == null) return 0;

        if (this.length == Long.MIN_VALUE) this.readDataLength();

        if (this.length >= 0 && this.length <= this.volume) return -1;

        //die Daten werden ausgelesen
        this.volume += volume = this.input.read(bytes, 0, bytes.length);

        return volume;
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zum Request als String. Der
     *  Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.<br><br>
     *  <b>Hinweis</b> - Die Auswertung der Parameter erfolgt vom aktuellen
     *  Datenbestand. Wurde der Request Body noch nicht geparst sind in der
     *  Ausgabe nur die Parameter des Query Strings der URL enthalten.
     *  @return die formatierte Information zum Response als String
     */
    public String toString() {

        String          string;
        StringBuffer    result;
        StringBuffer    buffer;
        StringTokenizer tokenizer;

        //der Zeilenumbruch wird ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = new StringBuffer("[").append(this.getClass().getName()).append("]").append(string);

        result.append("  state     = ").append(!this.bounded ? !this.locked ? "ready" : "locked" : "bounded").append(string);

        buffer = new StringBuffer();

        tokenizer = new StringTokenizer(this.getHeader(), "\r\n");

        while (tokenizer.hasMoreTokens()) {

            buffer = new StringBuffer(buffer.toString().trim());

            buffer.append(string).append("              ").append(tokenizer.nextToken());
        }

        result.append("  header    = ").append(buffer.toString().trim()).append(string);
        result.append("  method    = ").append(this.getMethod()).append(string);
        result.append("  path      = ").append(this.getURL()).append(string);
        result.append("  protocol  = ").append(this.getProtocol()).append(string);
        result.append("  query     = ").append(this.getQueryString()).append(string);
        result.append("  scheme    = ").append(this.getScheme()).append(string);

        //der Pfad des Auslagerungsverzeichnis wird ermittelt
        buffer = new StringBuffer(Codec.decode(this.storage == null ? "" : this.storage, Codec.DOT));

        result.append("  storage   = ").append((buffer.length() == 0) ? "none" : buffer.toString()).append(string);

        buffer = new StringBuffer("1x header");
        buffer.append(", ").append(String.valueOf(this.cookies.length)).append("x cookies");
        buffer.append(", ").append(String.valueOf(this.parameters.size())).append("x parameters");
        buffer.append(", ").append(String.valueOf(this.fragments.length)).append("x fragments");
        buffer.append(", ").append(String.valueOf(this.attributes.size())).append("x attributes");

        result.append("  structure = ").append(buffer).append(string);

        return result.toString();
    }
}