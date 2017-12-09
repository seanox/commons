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
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import com.seanox.common.Codec;

/**
 *  Abstrakte Klasse zur Implementierung eines Response, welche Konstanten und
 *  Methoden zur Beantwortung von HTTP-Anfragen bereitstellt.<br>
 *  <br>
 *  Response 1.2013.0630<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0630
 */
public abstract class Response extends OutputStream {

    /** Protokoll vom Response */
    protected String protocol;

    /** Statustext vom Response */
    protected String message;

    /** Header vom Response */
    protected Hashtable header;
    
    /** Status der Sperrung vom Response */
    protected boolean enabled;    

    /** Status der Datenausgabe */
    protected boolean control;

    /** Status der Datenausgabe */
    protected boolean allowed;

    /** Status des Zugriffsmodus */
    protected boolean bounded;

    /** Statuscode vom Response */
    protected int status;

    /** Transfervolumen */
    protected long volume;
    
    /** Konstante f&uuml;r die Validierungstyp Name */
    protected static final int FIELDTYPE_NAME = 1;

    /** Konstante f&uuml;r die Validierungstyp Wert */
    protected static final int FIELDTYPE_VALUE = 2;

    /** Konstante f&uuml;r den Switching Protocols */
    public static final int STATUS_SWITCHING_PROTOCOLS = 101;

    /** Konstante f&uuml;r den Serverstatus Success */
    public static final int STATUS_SUCCESS = 200;

    /** Konstante f&uuml;r den Serverstatus Created */
    public static final int STATUS_CREATED = 201;

    /** Konstante f&uuml;r den Serverstatus Accepted */
    public static final int STATUS_ACCEPTED = 202;

    /** Konstante f&uuml;r den Serverstatus Non Authoritative Information */
    public static final int STATUS_NON_AUTHORITATIVE_INFORMATION = 203;

    /** Konstante f&uuml;r den Serverstatus No Content */
    public static final int STATUS_NO_CONTENT = 204;

    /** Konstante f&uuml;r den Serverstatus Reset Content */
    public static final int STATUS_RESET_CONTENT = 205;

    /** Konstante f&uuml;r den Serverstatus Multiple Choices */
    public static final int STATUS_MULTIPLE_CHOICES = 300;

    /** Konstante f&uuml;r den Serverstatus Moved Permanently */
    public static final int STATUS_MOVED_PERMANENTLY = 301;

    /** Konstante f&uuml;r den Serverstatus Found */
    public static final int STATUS_FOUND = 302;

    /** Konstante f&uuml;r den Serverstatus Not Modified */
    public static final int STATUS_NOT_MODIFIED = 304;

    /** Konstante f&uuml;r den Serverstatus Use Proxy */
    public static final int STATUS_USE_PROXY = 305;

    /** Konstante f&uuml;r den Serverstatus Temporary Redirect */
    public static final int STATUS_TEMPORARY_REDIRECT = 307;

    /** Konstante f&uuml;r den Serverstatus Bad Request */
    public static final int STATUS_BAD_REQUEST = 400;

    /** Konstante f&uuml;r den Serverstatus Authorization Required */
    public static final int STATUS_AUTHORIZATION_REQUIRED = 401;

    /** Konstante f&uuml;r den Serverstatus Forbidden */
    public static final int STATUS_FORBIDDEN = 403;

    /** Konstante f&uuml;r den Serverstatus Not Found */
    public static final int STATUS_NOT_FOUND = 404;

    /** Konstante f&uuml;r den Serverstatus Method Not Allowed */
    public static final int STATUS_METHOD_NOT_ALLOWED = 405;

    /** Konstante f&uuml;r den Serverstatus None Acceptable */
    public static final int STATUS_NONE_ACCEPTABLE = 406;

    /** Konstante f&uuml;r den Serverstatus Proxy Authentication Required */
    public static final int STATUS_PROXY_AUTHENTICATION_REQUIRED = 407;

    /** Konstante f&uuml;r den Serverstatus Request Timeout */
    public static final int STATUS_REQUEST_TIMEOUT = 408;

    /** Konstante f&uuml;r den Serverstatus Length Required */
    public static final int STATUS_LENGTH_REQUIRED = 411;

    /** Konstante f&uuml;r den Serverstatus Precondition Failed */
    public static final int STATUS_PRECONDITION_FAILED = 412;

    /** Konstante f&uuml;r den Serverstatus Request Entity Too Large */
    public static final int STATUS_REQUEST_ENTITY_TOO_LARGE = 413;

    /** Konstante f&uuml;r den Serverstatus Unsupported Media Type */
    public static final int STATUS_UNSUPPORTED_MEDIA_TYPE = 415;

    /** Konstante f&uuml;r den Serverstatus Method Failure */
    public static final int STATUS_METHOD_FAILURE = 424;

    /** Konstante f&uuml;r den Serverstatus Internal Server Error */
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    /** Konstante f&uuml;r den Serverstatus Not Implemented */
    public static final int STATUS_NOT_IMPLEMENTED = 501;

    /** Konstante f&uuml;r den Serverstatus Bad Gateway */
    public static final int STATUS_BAD_GATEWAY = 502;

    /** Konstante f&uuml;r den Serverstatus Service Unavailable */
    public static final int STATUS_SERVICE_UNAVAILABLE = 503;

    /** Konstante f&uuml;r den Serverstatus Gateway Timeout */
    public static final int STATUS_GATEWAY_TIMEOUT = 504;
    
    /** Konstruktor, richtet den Response ein. */
    protected Response() {
        
        this.enabled = true;
        this.bounded = true;
        this.control = true;
        this.allowed = true;        

        this.header = new Hashtable();
        
        this.reset();
    }
    
    /**
     *  Formatiert das Datum im angebenden Format und in der angegebenen Zone.
     *  R&uuml;ckgabe das formatierte Datum, im Fehlerfall ein leerer String.
     *  @param  format Formatbeschreibung
     *  @param  date   zu formatierendes Datum
     *  @param  zone   Zeitzone, <code>null</code> Standardzone
     *  @return das formatierte Datum als String, im Fehlerfall leerer String
     */
    protected static String formatDate(String format, Date date, String zone) {

        SimpleDateFormat pattern;

        //die Formatierung wird eingerichtet
        pattern = new SimpleDateFormat(format, Locale.US);

        //die Zeitzone wird gegebenenfalls fuer die Formatierung gesetzt
        if (zone != null) pattern.setTimeZone(TimeZone.getTimeZone(zone));

        //die Zeitangabe wird formatiert
        return pattern.format(date);
    }
    
    /**
     *  R&uuml;ckgabe <code>true</code>, wenn der String der Regelung von
     *  Sonderzeichen laut RFC 2068 entspricht, sonst <code>false</code>.
     *  @param  string zu pr&uuml;fender String
     *  @param  type   Typ zur Validierung
     *  @return <code>true</code>, wenn der String der RFC 2068 entspricht
     *  @see    #FIELDTYPE_NAME
     *  @see    #FIELDTYPE_VALUE
     */
    protected static boolean valid(String string, int type) {

        char digit;
        int  loop;
        int  size;

        //kein String wird wie ein leerer String behandelt
        if (string == null) return true;

        //die Laenge des String wird ermittelt
        size = string.length();

        //die einzelnen Zeichen werden geprueft, zugelassen sind alle ASCII
        //Zeichen nicht aber [CR][LF]
        for (loop = 0; loop < size; loop++) {

            if ((digit = string.charAt(loop)) == 0x0A || digit == 0x0D || (type == Response.FIELDTYPE_NAME && digit == 0x3A)) return false;
        }

        return true;
    }
    
    /**
     *  Registriert die Anzahl der transferierten Bytes.
     *  @param volume Anzahl der transferierten Bytes
     */
    protected void addTransferVolume(int volume) {

        //das Datenvolumen wird erweitert
        if (volume > 0) this.volume += volume;
    }
    
    /**
     *  Setzt das Protokoll vom Response.
     *  Wenn nicht angegeben, wird <code>HTTP/1.0</code> verwendet.
     *  @param protocol Protokoll im Format <code>PROTOCOL/VERSION</code>.
     */
    public void setProtocol(String protocol) {
        
        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");
        
        protocol = protocol == null ? "" : protocol.trim();
        
        if (protocol.length() == 0) throw new IllegalArgumentException("Invalid protocol");

        this.protocol = protocol.toUpperCase();        
    }

    /**
     *  R&uuml;ckgabe des Statuscodes vom Response.
     *  @return der Statuscodes vom Response
     */    
    public String getProtocol() {
    
        return this.protocol;
    }    

    /**
     *  Setzt den Statuscode vom Response.
     *  @param  status Statuscode vom Response
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_CREATED
     *  @see    #STATUS_ACCEPTED
     *  @see    #STATUS_MULTIPLE_CHOICES
     *  @see    #STATUS_MOVED_PERMANENTLY
     *  @see    #STATUS_FOUND
     *  @see    #STATUS_NOT_MODIFIED
     *  @see    #STATUS_USE_PROXY
     *  @see    #STATUS_TEMPORARY_REDIRECT
     *  @see    #STATUS_BAD_REQUEST
     *  @see    #STATUS_AUTHORIZATION_REQUIRED
     *  @see    #STATUS_FORBIDDEN
     *  @see    #STATUS_NOT_FOUND
     *  @see    #STATUS_METHOD_NOT_ALLOWED
     *  @see    #STATUS_NONE_ACCEPTABLE
     *  @see    #STATUS_PROXY_AUTHENTICATION_REQUIRED
     *  @see    #STATUS_REQUEST_TIMEOUT
     *  @see    #STATUS_LENGTH_REQUIRED
     *  @see    #STATUS_UNSUPPORTED_MEDIA_TYPE
     *  @see    #STATUS_METHOD_FAILURE
     *  @see    #STATUS_INTERNAL_SERVER_ERROR
     *  @see    #STATUS_NOT_IMPLEMENTED
     *  @see    #STATUS_BAD_GATEWAY
     *  @see    #STATUS_SERVICE_UNAVAILABLE
     *  @see    #STATUS_GATEWAY_TIMEOUT
     */
    public void setStatus(int status) {

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");

        this.status = status;
    }    
    
    /**
     *  R&uuml;ckgabe des Statuscodes vom Response.
     *  @return der Statuscodes vom Response
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_CREATED
     *  @see    #STATUS_ACCEPTED
     *  @see    #STATUS_MULTIPLE_CHOICES
     *  @see    #STATUS_MOVED_PERMANENTLY
     *  @see    #STATUS_FOUND
     *  @see    #STATUS_NOT_MODIFIED
     *  @see    #STATUS_USE_PROXY
     *  @see    #STATUS_TEMPORARY_REDIRECT
     *  @see    #STATUS_BAD_REQUEST
     *  @see    #STATUS_AUTHORIZATION_REQUIRED
     *  @see    #STATUS_FORBIDDEN
     *  @see    #STATUS_NOT_FOUND
     *  @see    #STATUS_METHOD_NOT_ALLOWED
     *  @see    #STATUS_NONE_ACCEPTABLE
     *  @see    #STATUS_PROXY_AUTHENTICATION_REQUIRED
     *  @see    #STATUS_REQUEST_TIMEOUT
     *  @see    #STATUS_LENGTH_REQUIRED
     *  @see    #STATUS_UNSUPPORTED_MEDIA_TYPE
     *  @see    #STATUS_METHOD_FAILURE
     *  @see    #STATUS_INTERNAL_SERVER_ERROR
     *  @see    #STATUS_NOT_IMPLEMENTED
     *  @see    #STATUS_BAD_GATEWAY
     *  @see    #STATUS_SERVICE_UNAVAILABLE
     *  @see    #STATUS_GATEWAY_TIMEOUT
     */
    public int getStatus() {

        return this.status;
    }
    
    /**
     *  Setzt den Statustext vom Response.
     *  @param  message Statustext vom Response
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist
     *  @throws IllegalArgumentException wenn der nicht String der RFC 2068
     *          entspricht
     */
    public void setMessage(String message) {

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");

        if (!Response.valid(message, Response.FIELDTYPE_VALUE)) throw new IllegalArgumentException(("Invalid character in ").concat(message));

        this.message = (message == null) ? "" : message.trim();
    }

    /**
     *  R&uuml;ckgabe des Statustexts vom Response
     *  @return der Statustexts vom Response
     */
    public String getMessage() {

        return this.message;
    }    
    
    /**
     *  Erweitert den Header vom Response um den angebenen <code>Cookie</code>.
     *  @param cookie der zusetzende Cookie
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist
     */
    public void addCookie(Cookie cookie) {

        String stream;
        String string;

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");

        if (cookie == null) return;

        string = cookie.getName();
        stream = (cookie.getValue().length() == 0) ? "deleted" : cookie.getValue();
        string = string.concat("=").concat(Codec.encode(stream, Codec.MIME));
        stream = Response.formatDate("E, dd-MMM-yy HH:mm:ss z", new Date(cookie.getExpire()), "GMT");
        string = (stream.length() > 0 && cookie.getExpire() != 0) ? string.concat("; expires=").concat(stream) : string;
        string = (cookie.getPath().length() > 0) ? string.concat("; path=").concat(cookie.getPath()) : string;
        string = (cookie.getDomain().length() > 0) ? string.concat("; domain=").concat(cookie.getDomain()) : string;
        string = cookie.isSecure() ? string.concat("; secure") : string;

        this.setHeaderField("Set-Cookie", string);
    }    
    
    /**
     *  Pr&uuml;ft ob das angegebene Feld im Header enthalten. Bei der
     *  Ermittlung wird die Gross- und Kleinschreibung ignoriert. R&uuml;ckgabe
     *  <code>true</code>, wenn das Feld im Header existiert. Sonst wird
     *  <code>false</code> zur&uuml;ck gegeben.
     *  @param  name Name vom Feld
     *  @return <code>true</code>, wenn das Feld im Header enthalten ist
     */
    public boolean containsHeaderField(String name) {

        //der Feldname wird fuer die Suche im Header vereinfacht
        name = (name == null) ? "" : name.trim().toLowerCase();
        
        return this.header.containsKey(name);
    }

    /**
     *  R&uuml;ckgabe aller gesetzten Felder des Headers als Enumeration.
     *  Die Schreibweise wird dabei ignoriert. Liegt ein Header merhfach aber in
     *  unterschiedlicher Schreibweise vor, wird dieser nur in der ersten
     *  Schreibweise zur&uuml;ckgegen.
     *  @return alle gesetzten Felder des Headers als Enumeration
     */
    public Enumeration getHeaderFields() {
        
        Enumeration enumeration;
        Vector      vector;

        enumeration = this.header.elements();
        vector      = new Vector();

        while (enumeration.hasMoreElements()) {
            
            vector.add(((String[])enumeration.nextElement())[0]);
        }

        return vector.elements();
    }

    /**
     *  Ermittelt den Wert des angegebenen Feldes im Header. Kann dieses nicht
     *  ermittelt werden, wird ein leerer String zur&uuml;ck gegeben. Sind
     *  mehrere gleichnamige Felder im Header enthalten, wird der erste Wert
     *  zur&uuml;ckgegeben. Alle Werte zu einem Feld werden mit der Methode
     *  <code>Response.getHeaderFields(String name)</code> ermittelt.
     *  @param  name Name Feldes
     *  @return der Wert des angegebenen Feldes
     */
    public String getHeaderField(String name) {
        
        String[] values;
        
        values = this.getHeaderFields(name);
        
        return values.length > 0 ? values[0] : "";  
    }

    /**
     *  Ermittelt die Werte des angegebenen Feldes im Header als Array.
     *  Die Schreibweise wird dabei ignoriert. Kann das Feld nicht ermittelt
     *  werden, wird ein leeres Array zur&uuml;ck gegeben.
     *  Ob ein Feld im Header vom Response enthalten ist, wird mit der Methode 
     *  <code>Response.containsHeaderField(String name)</code> ermittelt.
     *  @param  name Name vom Feld
     *  @return die Werte vom Feld als String Array
     */
    public String[] getHeaderFields(String name) {

        List     result;

        String[] values;
        
        int      loop;
        
        result = new ArrayList();
        
        //der Feldname wird fuer die Suche im Header vereinfacht
        name = (name == null) ? "" : name.trim().toLowerCase();
        
        values = (String[])this.header.get(name);
        
        for (loop = 1; loop < values.length; loop += 2) {
            
            result.add(values[loop]);
        }
        
        return (String[])result.toArray(new String[0]);
    }

    /**
     *  Setzt das Feld im Header mit dem angegebenen Wert. Null-Werte und leere
     *  Feldnamen werden nicht ber&uuml;cksichtigt.
     *  @param  name  Name vom Feld
     *  @param  value Wert vom Feld
     *  @throws IllegalArgumentException wenn die Argumente nicht der RFC 2068
     *          entsprechen, <code>null</code> oder kein Name oder als Name die
     *          reservierten Feldnamen server oder http angegeben werden
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist
     */
    public void setHeaderField(String name, String value) {
        
        String array[];
        String values[];

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");

        if (!Response.valid(name, Response.FIELDTYPE_NAME)) throw new IllegalArgumentException(("Invalid character in ").concat(name));
        if (!Response.valid(value, Response.FIELDTYPE_VALUE)) throw new IllegalArgumentException(("Invalid character in ").concat(value));

        //der Feldname wird fuer dem Header optimiert
        name = (name == null) ? "" : name.trim();

        if (name.length() == 0) throw new IllegalArgumentException("Invalid field name ");
        
        values = (String[])this.header.get(name.toLowerCase());
        
        if (values == null) values = new String[0];
        
        //der Header wird um den Eintrag erweitert
        array = new String[values.length +2];
        System.arraycopy(values, 0, array, 0, values.length);
        array[values.length +0] = name;
        array[values.length +1] = value.trim();
        
        //der geaenderte Header wird gesetzt        
        this.header.put(name.toLowerCase(), array);
    }

    /**
     *  Entfernt das angegebene und alle gleichnamigen Felder aus dem Header.
     *  @param  name Name vom Feld
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist
     */
    public void removeHeaderField(String name) {

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");

        //der Feldname wird fuer die Suche im Header vereinfacht
        name = (name == null) ? "" : name.trim().toLowerCase();
        
        this.header.remove(name);
    }

    /**
     *  R&uuml;ckgabe der ermittelbaren Serverkennung, sonst <code>null</code>.
     *  @return die ermittelte Serverkennung, sonst <code>null</code>
     */
    protected String getServerIdentity() {
        
        return null;
    }
    
    /**
     *  R&uuml;ckgabe vom aktuellen Header.
     *  @return der erstellte Header
     */
    protected String getHeader() {
        
        Enumeration  enumeration;
        String       server;
        StringBuffer buffer;
        
        String[]     values;
        
        int          loop;
        int          status;

        //der Responsestatus wird ermittelt
        status = this.status == 0 ? 200 : this.status; 

        //der Responseheader wird zusammengestellt
        buffer = new StringBuffer(this.protocol);

        buffer.append(" ").append(status);
        buffer.append(" ").append((this.message.length() == 0) ? Response.getStatusMessage(this.status) : this.message);
        buffer.append("\r\n");
        
        //die Serverversion wird ermittelt
        server = this.getServerIdentity();
        
        //ggf. wird der Server gesetzt, wenn bekannt und nicht gesetzt
        if (server != null && !this.containsHeaderField("server")) buffer.append("Server: ").append(server).append("\r\n");
        
        //ggf. das Datum Server gesetzt, wenn nicht gesetzt        
        if (!this.containsHeaderField("date")) buffer.append(Response.formatDate("'Date:' E, dd MMM yyyy HH:mm:ss z", new Date(), "GMT")).append("\r\n");
        
        enumeration = this.header.elements();

        //die Header-Felder werden aufgebaut
        while (enumeration.hasMoreElements()) {
            
            values = (String[])enumeration.nextElement();
            
            for (loop = 0; loop +1 < values.length; loop += 2) {
                
                buffer.append(values[loop]).append(": ").append(values[loop +1]).append("\r\n");
            }
        }

        buffer.append("\r\n");

        return buffer.toString();
    }
    
    /**
     *  Ermittelt den Text zum Serverstatus aus den Konstanten der Klasse.
     *  R&uuml;ckage der ermittelte Text zum Serverstatus, kann dieser nicht
     *  ermittelt werden, wird <code>Unknown</code> zur&uuml;ckgegeben.
     *  @param  status Serverstatus
     *  @return der ermittelte Text zum Serverstatus
     */
    public static String getStatusMessage(int status) {

        StringTokenizer tokenizer;
        String          result;
        String          stream;
        String          string;

        Field[]         fields;

        int             loop;

        //alle Felder der Klasse werde ermittelt
        fields = Response.class.getDeclaredFields();

        for (loop = 0, string = null; loop < fields.length; string = null, loop++) {

            string = fields[loop].getName();

            if (!string.startsWith("STATUS_")) continue;

            //der Feldname wird uebernommen wenn er dem Status entspricht
            try {if (fields[loop].getInt(null) == status) break;
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }

        if (string == null) return "Unknown";

        tokenizer = new StringTokenizer(string.substring(7), "_");

        for (result = ""; tokenizer.hasMoreTokens();) {

            string = tokenizer.nextToken();
            stream = (string.length() > 1) ? string.substring(1).toLowerCase() : "";
            string = (string.length() > 1) ? string.substring(0, 1).toUpperCase() : "";

            result = result.concat(" ").concat(string).concat(stream).trim();
        }

        return result;
    }    
    
    /**
     *  Sendet den Status direkt &uuml;ber den Server. Dieser ber&uuml;cksichtigt
     *  dabei alle serverseitigen Definitionen wie Templates und Redirections.
     *  @param  status Serverstatus
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist oder sich im gesch&uuml;tzten
     *          Modus befindet
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_SUCCESS
     *  @see    #STATUS_CREATED
     *  @see    #STATUS_ACCEPTED
     *  @see    #STATUS_MULTIPLE_CHOICES
     *  @see    #STATUS_MOVED_PERMANENTLY
     *  @see    #STATUS_FOUND
     *  @see    #STATUS_NOT_MODIFIED
     *  @see    #STATUS_USE_PROXY
     *  @see    #STATUS_TEMPORARY_REDIRECT
     *  @see    #STATUS_BAD_REQUEST
     *  @see    #STATUS_AUTHORIZATION_REQUIRED
     *  @see    #STATUS_FORBIDDEN
     *  @see    #STATUS_NOT_FOUND
     *  @see    #STATUS_METHOD_NOT_ALLOWED
     *  @see    #STATUS_NONE_ACCEPTABLE
     *  @see    #STATUS_PROXY_AUTHENTICATION_REQUIRED
     *  @see    #STATUS_REQUEST_TIMEOUT
     *  @see    #STATUS_LENGTH_REQUIRED
     *  @see    #STATUS_UNSUPPORTED_MEDIA_TYPE
     *  @see    #STATUS_METHOD_FAILURE
     *  @see    #STATUS_INTERNAL_SERVER_ERROR
     *  @see    #STATUS_NOT_IMPLEMENTED
     *  @see    #STATUS_BAD_GATEWAY
     *  @see    #STATUS_SERVICE_UNAVAILABLE
     *  @see    #STATUS_GATEWAY_TIMEOUT
     */
    public void sendStatus(int status) throws IOException {
        
        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.setStatus(status);
        this.flush();

        //der Response wird als geschlossen markiert
        this.allowed = false;
    }

    /**
     *  Sendet ein Redirect zur Umleitung der Seite an die angegebene Adresse.
     *  @param  address URL der Umleitung
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Header bereits versand oder der
     *          Response bereits geschlossen ist oder sich im gesch&uuml;tzten
     *          Modus befindet
     */
    public void sendRedirect(String address) throws IOException {

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        this.setHeaderField("Location", address);
        this.setStatus(302);
        this.flush();

        //der Response wird als geschlossen markiert
        this.allowed = false;
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn der Response noch komplett zur
     *  Verf&uuml;gung steht und noch nicht geschlossen ist und der Header noch
     *  nicht versand wurde, sonst wird <code>false</code> zur&uuml;ck gegeben.
     *  @return <code>true</code> wenn Response komplett verf&uuml;gbar ist
     */
    public boolean isUsable() {

        return this.enabled && this.bounded && this.control && this.allowed;
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn der Header bereits versand wurde,
     *  sonst wird <code>false</code> zur&uuml;ck gegeben.
     *  @return <code>true</code> wenn der Header bereits versand wurde
     */
    public boolean isCommitted() {

        return !this.control;
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn der Response bereits geschlossen
     *  ist, sonst wird <code>false</code> zur&uuml;ck gegeben.
     *  @return <code>true</code> wenn der Response bereits geschlossen ist
     */
    public boolean isClosed() {

        return !this.allowed;
    }    
    
    /**
     *  Setzt den Puffer vom Response sowie Status und Meassge zur&uuml;ck.
     *  @throws IllegalStateException wenn der Header bzw. der Response bereits
     *          versand oder der Response bereits geschlossen ist
     */
    public void reset() {

        if (!this.enabled) throw new IllegalStateException("Response is locked");
        if (!this.bounded) throw new IllegalStateException("Response is bounded");
        if (!this.control) throw new IllegalStateException("Response header already sent");
        if (!this.allowed) throw new IllegalStateException("Response already sent");

        synchronized (this) {

            this.message  = "";
            this.protocol = "HTTP/1.0"; 
            
            this.header.clear();
    
            this.volume = 0;
            this.status = 0;
        }
    }
    
    /**
     *  Sendet den im Puffer befindlichen Header vom Response.
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public abstract void flush() throws IOException;
    
    /**
     *  Schreibt den Puffer des Headers und das angegebene Byte in den
     *  Datenausgabestrom vom Response.
     *  @param  code Byte
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public abstract void write(int code) throws IOException;
    
    /**
     *  Schreibt den Puffer des Headers und die angegebenen Bytes in den
     *  Datenausgabestrom vom Response.
     *  @param  bytes Bytes als Array
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public abstract void write(byte[] bytes) throws IOException;
    
    /**
     *  Schreibt den Puffer des Headers und die Bytes des angegebenen Bereichs
     *  aus dem ByteArray in den Datenausgabestrom vom Response.
     *  @param  bytes  Bytes als Array
     *  @param  offset Position im Byte Array
     *  @param  length Anzahl der Bytes
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public abstract void write(byte[] bytes, int offset, int length) throws IOException;    
    
    /**
     *  Schreibt den Puffer des Headers in den Datenausgabestrom vom Response,
     *  und schliesst den Response wie auch den Datenausgabestrom.
     *  @throws IOException bei Fehlern in Verbindung mit der Datenausgabe
     *  @throws IllegalStateException wenn der Response bereits geschlossen ist
     */
    public abstract void close() throws IOException;    
    
    /**
     *  R&uuml;ckgabe der formatierten Information zum Response als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zum Response als String
     */
    public String toString() {

        String          string;
        StringBuffer    buffer;
        StringBuffer    result;
        StringTokenizer tokenizer;

        //der Zeilenumbruch wird ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = new StringBuffer("[").append(this.getClass().getName()).append("]").append(string);
        
        buffer = new StringBuffer();

        tokenizer = new StringTokenizer(this.getHeader(), "\r\n");

        while (tokenizer.hasMoreTokens()) {

            buffer = new StringBuffer(buffer.toString().trim());

            buffer.append(string).append("              ").append(tokenizer.nextToken());
        }

        result.append("  header    = ").append(buffer.toString().trim()).append(string);
        result.append("  message   = ").append(this.message.length() == 0 ? Response.getStatusMessage((this.status) == 0 ? 200 : this.status) : this.message).append(string);
        result.append("  state     = ").append(!this.bounded ? !this.enabled ? !this.control ? !this.allowed ? "opened" : "committed" : "closed" : "locked" : "bounded").append(string);
        result.append("  status    = ").append((this.status) == 0 ? 200 : this.status).append(string);
        result.append("  volume    = ").append(this.volume).append(" byte").append(string);

        return result.toString();
    }
}