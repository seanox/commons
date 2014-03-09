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
package com.seanox.network.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.seanox.common.Accession;
import com.seanox.network.http.security.Context;

/**
 *  Connector, dieser stellt Methoden f&uuml;r den Zugriff auf Ressourcen per
 *  HTTP und HTTPS zur Verf&uuml;gung.<br>
 *  <br>
 *  Die Konfiguration und Initialisierung werden durch entsprechend
 *  &uuml;bergebener Properties vorgenommen. F&uuml;r die Anforderung von Daten
 *  stellt der Connector verschiedene Methoden zur Verf&uuml;gung. Dabei ist die
 *  &Uuml;bergabe der Adresse sowie die optionale Angabe von HTTP-Header
 *  Informationen, Request Daten als Bytes oder als Datenstrom und die Option
 *  zur Auslagerung m&ouml;glich.<br>
 *  <br>
 *  Die Datenr&uuml;ckgabe erfolgt als signierter Content. Dazu wird der Header
 *  vom Response um das Feld Content-Signature erweitert. Die Signatur ist dabei
 *  eindeutig und erm&ouml;glicht von ihrem Aufbau auch eine Ermittlung des
 *  Zeitpunkts des Datentransfer. Optional ist eine Trennung von Header und
 *  Response Body m&ouml;glich (siehe auch connector.storage.directory). Bei der
 *  Trennung werden die Daten vom Response-Body in einer zur Content-Signatur
 *  passenden Auslagerungsdatei im lokalen Dateisystem abgelegt. F&uuml;r den
 *  Zugriff auf die ausgelagerten Daten, stellt der Conntector entsprechende
 *  Funktionalit&auml;ten zur Verf&uuml;gung, welche eine Zuordnung von
 *  Content-Signatur zur Auslagerungsdatei erm&ouml;glichen. Ohne Trennung wird
 *  der komplette Response als Content in den Speicher geladen.<br>
 *  <br>
 *  Connector 1.2013.0924<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0924
 */
public class Connector {

    /** Konfigurationsparameter */
    private Hashtable parameters;

    /** allgemeine untrusted SSLSocketFactory */
    private SocketFactory factory;

    /** Zeitpunkt der letzten Systemunterbrechung */
    private long timing;

    /** Konstante der aktuellen Version */
    private static final String VERSION = "@@@ant-project-version";

    /** 
     *  Konstante f&uuml;r Parameter connector.blocksize<br>
     *  Datenmenge in Bytes beim Datentransfer (65535*)
     */
    public static final String CONNECTOR_BLOCKSIZE = "connector.blocksize";

    /** 
     *  Konstante f&uuml;r Parameter connector.interrupt<br>
     *  Millisekunden der Unterbrechung f&uuml;r weitere Systemprozesse (25*)
     */
    public static final String CONNECTOR_INTERRUPT = "connector.interrupt";

    /** 
     *  Konstante f&uuml;r Parameter connector.proxy.server<br>
     *  IP oder Adresse des Proxys f&uuml;r HTTP
     */
    public static final String CONNECTOR_PROXY_SERVER = "connector.proxy.server";

    /** 
     *  Konstante f&uuml;r Parameter connector.proxy.port<br>
     *  Port des Proxys f&uuml;r HTTP (3128*)
     */
    public static final String CONNECTOR_PROXY_PORT = "connector.proxy.port";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.client.authentication<br>
     *  Option on/off* zur Aktivierung der Client-Authentifikation bei HTTPS
     */
    public static final String CONTEXT_SECURE_CLIENT_AUTHENTICATION = "connector.secure.client.authentication";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.keystore.algorithm<br>
     *  Keystore Algorithmus (SunX509*)
     */
    public static final String CONTEXT_SECURE_KEYSTORE_ALGORITHM = "connector.secure.keystore.algorithm";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.keystore.file<br>
     *  Pfad der Keystore Datei
     */
    public static final String CONTEXT_SECURE_KEYSTORE_FILE = "connector.secure.keystore.file";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.keystore.password<br>
     *  Passwort der Keystore-Datei
     */
    public static final String CONTEXT_SECURE_KEYSTORE_PASSWORD = "connector.secure.keystore.password";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.keystore.type<br>
     *  Typ des Keystores (JKS*)
     */
    public static final String CONTEXT_SECURE_KEYSTORE_TYPE = "connector.secure.keystore.type";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.protocol<br>
     *  Protokoll der HTTPS Verbdindung SSL/TLS*
     */
    public static final String CONTEXT_SECURE_PROTOCOL = "connector.secure.protocol";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.proxy.server<br>
     *  IP oder Adresse des Proxys f&uuml;r HTTPS
     */
    public static final String CONNECTOR_SECURE_PROXY_SERVER = "connector.secure.proxy.server";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.proxy.port<br>
     *  Port des Proxys f&uuml;r HTTPS (3128*)
     */
    public static final String CONNECTOR_SECURE_PROXY_PORT = "connector.secure.proxy.port";

    /** 
     *  Konstante f&uuml;r Parameter connector.secure.trusting<br>
     *  Option zur &Uuml;berpr&uuml;ft der G&uuml;ltigkeit von Zertifikaten
     *  bei HTTPS
     *  <ul>
     *    <li>
     *      standard nur g&uuml;ltige Zertifikate werden anerkannt
     *    </li>
     *    <li>
     *      ignore*, die &Uuml;berpr&uuml;fung der Zertifikate erfolgt nicht, es
     *      werden alle akzeptiert
     *    </li>
     *    <li>
     *      optionale Klasse welche die &Uuml;berpr&uuml;fung &uuml;bernimmt
     *    </li>
     *  </ul>
     */
    public static final String CONNECTOR_SECURE_TRUSTING = "connector.secure.trusting";

    /** 
     *  Konstante f&uuml;r Parameter connector.storage.directory<br>
     *  Auslagerungsverzeichnis
     */
    public static final String CONNECTOR_STORAGE_DIRECTORY = "connector.storage.directory";

    /** 
     *  Konstante f&uuml;r Parameter connector.timeout<br>
     *  Abbruch des Datentransfer bei Datenleerlauf in Millisekunden (65535*)
     */
    public static final String CONNECTOR_TIMEOUT = "connector.timeout";

    /** Konstante f&uuml;r Option keine Auslagerung des Content */
    private static final int SWAP_NONE = 0;

    /** Konstante f&uuml;r Option zur Auslagerung des Content */
    private static final int SWAP_CONTENT = 1;

    /** Konstante f&uuml;r Option zur Auslagerung des gesamten Response */
    private static final int SWAP_COMPLETE = 2;
    
    /** Konstruktor, richtet den Connector ein. */
    public Connector() {

        this(null); 
    }

    /**
     *  Konstruktor, richtet den Connector entsprechend der Konfiguration ein.
     *  @param properties Konfiguration
     */
    public Connector(Properties properties) {

        String string;

        //die Parameter werden eingerichtet
        this.parameters = new Hashtable();

        //die Konfiguration wird gegebenfalls korregiert
        if (properties == null) properties = new Properties();

        //der Proxyserver fuer HTTP wird ermittelt
        this.setParameter(properties, Connector.CONNECTOR_PROXY_SERVER, "");

        //der Proxyport fuer HTTP wird ermittelt, Standard 3128
        this.setParameter(properties, Connector.CONNECTOR_PROXY_PORT, new Integer(3128));

        //der Proxyserver fuer HTTPS wird ermittelt
        this.setParameter(properties, Connector.CONNECTOR_SECURE_PROXY_SERVER, "");

        //der Proxyport fuer HTTPS wird ermittelt, Standard 3128
        this.setParameter(properties, Connector.CONNECTOR_SECURE_PROXY_PORT, new Integer(3128));

        //die Client Authentifizierung wird ermittelt, Stanard off
        this.setParameter(properties, Connector.CONTEXT_SECURE_CLIENT_AUTHENTICATION, new Boolean(false));

        //der Keystore Algorithmus wird ermittelt, Standard SunX509
        this.setParameter(properties, Connector.CONTEXT_SECURE_KEYSTORE_ALGORITHM, "SunX509");

        //die Keystore Datei wird ermittelt
        this.setParameter(properties, Connector.CONTEXT_SECURE_KEYSTORE_FILE, "");

        //das Keystore Passwort wird ermittelt
        this.setParameter(properties, Connector.CONTEXT_SECURE_KEYSTORE_PASSWORD, "");

        //das Keystore Typ wird ermittelt, Standard JKS
        this.setParameter(properties, Connector.CONTEXT_SECURE_KEYSTORE_TYPE, "JKS");

        //das Secure Protokoll wird ermittelt, Standard TLS
        this.setParameter(properties, Connector.CONTEXT_SECURE_PROTOCOL, "TLS");

        //die Trusting Konfiguration wird ermittelt, Standard ignore
        this.setParameter(properties, Connector.CONNECTOR_SECURE_TRUSTING, "ignore", -1);

        //die Groesse der zu verwendenden Datenbloecke wird ermittelt, Standard 65535
        this.setParameter(properties, Connector.CONNECTOR_BLOCKSIZE, new Integer(65535));

        //die Systemunterbrechung in fuer andere Prozese wird ermittelt, Standard 25
        this.setParameter(properties, Connector.CONNECTOR_INTERRUPT, new Integer(25));

        //die maximale Zeit fuer Prozess- und Datenleerlauf wird ermittelt, Standard 65535
        this.setParameter(properties, Connector.CONNECTOR_TIMEOUT, new Integer(65535));

        //der Auslagerungsverzeichnis wird ermittelt, Standard aktuelles Arbeitsverzeichnis
        string = properties.getProperty(Connector.CONNECTOR_STORAGE_DIRECTORY, "").replace('\\', '/').trim();

        if (string.length() == 0) string = ".";
        if (!string.endsWith("/") ) string = string.concat("/");

        this.parameters.put(Connector.CONNECTOR_STORAGE_DIRECTORY, string);

        this.timing = System.currentTimeMillis();
    }

    /**
     *  Unterbricht die aktuelle Verarbeitung f&uuml;r die angegebene
     *  Millisekunden. Die Unterbrechung erfolgt in Abh&auml;ngigkeit der
     *  angegebenen maximal zul&auml;ssigen Laufzeit.
     *  @param idle Unterbrechung in Millisekunden
     */
    private void sleep(long idle) {

        if ((System.currentTimeMillis() -this.timing) > 20) {

            try {Thread.sleep(idle);
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            this.timing = System.currentTimeMillis();
        }
    }

    /**
     *  Ermittelt und setzt den in den Properties enthaltenen Parameter. Kann
     *  dieser nicht ermittelt werden, wird der optinal &uuml;bergeben
     *  Standardwert gesetzt.
     *  @param properties Konfigurations
     *  @param field      Name des zu setztenden Parameters
     *  @param standard   optinaler Standardwert
     */
    private void setParameter(Properties properties, String field, Object standard) {

        this.setParameter(properties, field, standard, 0);
    }

    /**
     *  Ermittelt und setzt den in den Properties enthaltenen Parameter. Kann
     *  dieser nicht ermittelt werden, wird der optinal &uuml;bergeben
     *  Standardwert gesetzt.
     *  @param properties Konfigurations
     *  @param field      Name des zu setztenden Parameters
     *  @param standard   optinaler Standardwert
     *  @param casemode   Option <code>true</code>, verwendet Grossschreibung
     */
    private void setParameter(Properties properties, String field, Object standard, int casemode) {

        String string;

        int    value;

        string = properties.getProperty(field, "").trim();

        if (casemode < 0) string = string.toLowerCase();
        if (casemode > 0) string = string.toUpperCase();

        if (standard instanceof Boolean) {

            string = string.toLowerCase();

            if (string.length() == 0) string = ((Boolean)standard).booleanValue() ? "on" : "off";

            this.parameters.put(field, new Boolean(string.equals("on")));

        } else if (standard instanceof Integer) {

            try {value = Integer.parseInt(string);
            } catch (Exception exception) {value = -1;}

            this.parameters.put(field, (value < 0) ? standard : new Integer(value));

        } else if (standard instanceof String) {

            this.parameters.put(field, (string.length() == 0) ? standard : string);
        }
    }

    /**
     *  R&uuml;ckgabe einer eindeutigen Auslagerungskennung.
     *  @return eine eindeutige Auslagerungskennung
     */
    private String getStorageContext() {

        String string;

        long   time;

        synchronized (Connector.class) {

            //der aktuelle Zeitstempel wird eindeutig ermittelt
            for (time = System.currentTimeMillis(); time == System.currentTimeMillis(); this.sleep(1)) continue;

            //die aktuelle Objektkennung wird ermittelt
            string = Long.toString(Math.abs(new Object().hashCode()), 36);

            //die Objektkennung wird auf 6 Zeichen aufgefuellt
            while (string.length() < 6) string = ("0").concat(string);

            //die eindeutige Auslagerungskennung wird zusammengesetzt
            string = Long.toString(time, 36).concat(string).toUpperCase();
        }

        return string;
    }

    /**
     *  Signiert den &uuml;bergebenen Content-Header mit dem Feld
     *  Content-Signature. Gleichnamige bestehende Felder werden dabei entfernt.
     *  @param  content   zu signierender Content-Header
     *  @param  signature Signatur Kennung
     *  @return der signierte Content-Header
     */
    private static String signContentHeader(String content, String signature) {

        StringTokenizer tokenizer;
        String          result;
        String          stream;

        int             cursor;

        //die Zeilen des Headers werden ermittelt
        tokenizer = new StringTokenizer(content, "\r\n");

        //die erste Zeile wird zwischengespeichert
        result = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim().concat("\r\n") : "";

        //die Parameter werden aus allen Zeilen ermittelt
        while (tokenizer.hasMoreTokens()) {

            content = tokenizer.nextToken().trim();
            cursor  = content.indexOf(':');
            stream  = (cursor < 0) ? content : content.substring(0, cursor);
            stream  = stream.toLowerCase().trim();

            if (stream.equals("content-signature")) continue;

            result = result.concat(content).concat("\r\n");
        }

        return result.concat("Content-Signature: ").concat(signature).concat("\r\n\r\n");
    }

    /**
     *  Erstellt entsprechend der aktuelle Konfiguration einen Socket f&uuml;r
     *  HTTP, HTTPS und untrusted HTTPS. R&uuml;ckgabe der erstellt Socket.
     *  @param  server Adresse der Socketverbindung
     *  @param  port   Port der Socketverbindung
     *  @param  secure Option <code>true</code> verwendet HTTPS
     *  @return die erstellte Socketverbindung
     *  @throws IOException bei Fehlern in Verbindung mit den Datenstr&ouml;men
     *  @throws UnknownHostException bei Fehlern beim Ausfl&ouml;sen des Hosts
     *  @throws NoSuchAlgorithmException bei unbekannten
     *          Verschl&uuml;sselungsmethoden
     *  @throws KeyStoreException bei Fehlern mit dem Basis Zertifikat
     *  @throws KeyManagementException bei Fehlern mit dem Basis Zertifikat
     */
    private Socket createSocket(String server, int port, boolean secure)
        throws UnknownHostException, IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        InputStream  input;
        OutputStream output;
        Socket       tunnel;
        Socket       socket;
        String       stream;
        String       string;

        boolean      control;
        int          cursor;
        int          timeout;
        int          value;

        //initiale Einrichtung der Variablen
        tunnel  = null;
        socket  = null;
        control = true;
        cursor  = 0;

        //das Sockettimeout in Millisekunden wird ermittelt
        timeout = ((Integer)this.parameters.get(Connector.CONNECTOR_TIMEOUT)).intValue();

        //der Proxyserver fuer HTTP/S wird ermittelt
        string = (String)this.parameters.get(Connector.CONNECTOR_PROXY_SERVER);

        //der Proxyserver fuer HTTPS wird ermittelt
        stream = (String)this.parameters.get(Connector.CONNECTOR_SECURE_PROXY_SERVER);

        try {

            //der Socket wird mit Timeout fuer HTTP ohne Proxy eingerichtet
            if (!secure && string.length() == 0) {

                tunnel = new Socket(server, port);

                tunnel.setSoTimeout(timeout);

                control = false;

                return tunnel;
            }

            //der Socket wird mit Timeout fuer HTTP mit Proxy eingerichtet
            if (!secure && string.length() > 0) {

                tunnel = new Socket(string, ((Integer)this.parameters.get(Connector.CONNECTOR_PROXY_PORT)).intValue());

                tunnel.setSoTimeout(timeout);

                control = false;

                return tunnel;
            }

            //der Socket wird mit Timeout fuer HTTPS mit Proxy eingerichtet
            if (secure && stream.length() > 0) {

                //der Proxyserver und der Proxyport werden ermittelt
                value  = ((Integer)this.parameters.get(Connector.CONNECTOR_SECURE_PROXY_PORT)).intValue();

                //der Socket zum Proxyserver wird eingerichtet
                tunnel = new Socket(stream, value);

                //das Socket Timeout wird gesetzt
                tunnel.setSoTimeout(timeout);

                //die Datenstroeme werden eingerichtet
                output = tunnel.getOutputStream();
                input  = tunnel.getInputStream();

                //der Connect Request wird ausgefuehrt
                output.write(("CONNECT ").concat(server).concat(":").concat(String.valueOf(port)).concat(" HTTP/1.0\r\n\r\n").getBytes());

                //der Datenstrom wird abgeschlossen
                output.flush();

                //der Datenstrom wird ausgelesen und ignoriert
                while (cursor != 4 && ((value = input.read()) >= 0)) {

                    cursor = (value == ((cursor % 2) == 0 ? 13 : 10)) ? cursor +1 : 0;
                }
            }

            //das Trusting wird ermittelt
            string = (String)this.parameters.get(Connector.CONNECTOR_SECURE_TRUSTING);

            //die unsichere SSL Verbindung wird ohne Zertifikats Ueberpruefung
            //initialisiert und als Socketverbindung eingerichtet
            synchronized (this.factory == null ? (Object)this : (Object)this.factory) {

                if (this.factory == null) {

                    if (string.equals("ignore") || !string.equals("standard")) {

                        this.factory = Context.getSocketFactory(this.getProperties());

                    } else {

                        //die Standard SSL Verbindung wird mit Zertifikat geprueft,
                        //initialisiert und als Socketverbindung eingerichtet
                        this.factory = SSLSocketFactory.getDefault();
                    }
                }
            }

            //der Socket wird eingerichtet
            socket = tunnel == null ? this.factory.createSocket(server, port) : ((SSLSocketFactory)this.factory).createSocket(tunnel, server, port, true);

            //das Socket Timeout wird gesetzt
            socket.setSoTimeout(timeout);

            //ggf. wird die Client Authenfizierung gesetzt
            if (socket instanceof SSLSocket) {

                ((SSLSocket)socket).setNeedClientAuth(((Boolean)this.parameters.get(CONTEXT_SECURE_CLIENT_AUTHENTICATION)).booleanValue());
                ((SSLSocket)socket).startHandshake();
            }

            control = false;

            return socket;

        } finally {

            //der Tunnel wird im Fehlerfall geschlossen
            try {if (control) tunnel.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            //der Socket wird im Fehlerfall geschlossen
            try {if (control) socket.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }
    }

    /**
     *  Ermittelt zur &uuml;bergebene Content-Signatur, die entsprechende
     *  Auslagerungsdatei. Kann diese nicht ermittelt werden oder ist die
     *  Signatur ung&uuml;ltig wird <code>null</code> zur&uuml;ckgegeben.
     *  @param  signature Content-Signatur
     *  @return die zur Content-Signatur geh&ouml;rende Auslagerungsdatei
     */
    public File getStorageFile(String signature) {

        File   result;
        String stream;
        String string;

        char   code;
        int    loop;

        stream = (signature == null) ? "" : signature.toLowerCase().trim();

        if (stream.length() == 0) return null;

        //es werden als Signatur nur die Zeichen 0-9 A-Z beruecksichtig
        for (loop = 0; loop < stream.length(); loop++) {

            if (!(((code = stream.charAt(loop)) >= '0' && code <= '9') || (code >= 'a' && code <= 'z'))) return null;
        }

        //der Pfad der Auslagerungsdatei wird ermittelt
        string = (String)this.parameters.get(Connector.CONNECTOR_STORAGE_DIRECTORY);
        string = string.concat("connector.").concat(signature.toUpperCase().trim()).concat(".content");

        //die Content-Datei muss eine Datei sein und existieren
        if (!(result = new File(string)).isFile() || !result.exists()) result = null;

        return result;
    }

    /**
     *  Entfernt die Content-Datei der angebenen Signatur aus dem Dateisystem.
     *  R&uuml;ckgabe <code>true</code>, im Fehlerfall <code>false</code>.
     *  @param  signature Content-Signatur
     *  @return <code>true</code> im Fehlerfall <code>false</code>
     */
    public boolean removeStorageFile(String signature) {

        String stream;
        String string;

        char   code;
        int    loop;

        stream = (signature == null) ? "" : signature.toLowerCase().trim();

        if (stream.length() == 0) return false;

        //es werden als Signatur nur die Zeichen 0-9 A-Z beruecksichtig
        for (loop = 0; loop < stream.length(); loop++) {

            if (!(((code = stream.charAt(loop)) >= '0' && code <= '9') || (code >= 'a' && code <= 'z'))) return false;
        }

        //der Pfad der Auslagerungsdatei wird ermittelt
        string = (String)this.parameters.get(Connector.CONNECTOR_STORAGE_DIRECTORY);
        string = string.concat("connector.").concat(signature.toUpperCase().trim()).concat(".content");

        //die Content-Datei wird aus dem Dateisystem entfernt
        return new File(string).delete();
    }

    /**
     *  R&uuml;ckgabe der akutellen Konfiguration. Ist keine Instanz
     *  eingerichtet wird <code>null</code> zur&uuml;ckgegeben.
     *  @return die aktuelle Konfiguration als Properties
     */
    private Properties getProperties() {

        Enumeration enumeration;
        Hashtable   hashtable;
        Properties  properties;
        String      string;

        hashtable = this.parameters;

        if (hashtable == null) return null;

        enumeration = hashtable.keys();
        properties  = new Properties();

        while (enumeration.hasMoreElements()) {

            string = (String)enumeration.nextElement();

            properties.setProperty(string, String.valueOf(hashtable.get(string)));
        }

        return properties;
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert.
     *  @param  address Adresse der Ressource
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, null, null, null, null, Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, null, null, null, Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert.
     *  @param  address Adresss der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, null, null, Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert. Bei der
     *  &Uuml;bergabe von Requestdaten als ByteArray wird die Content-Length
     *  Angabe automatisch ermittelt.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  data    Daten vom Request
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, byte[] data)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, data, null, Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert. Bei der
     *  &Uuml;bergabe von Requestdaten als InputStream wird die Content-Length
     *  Angabe nicht automatisch ermittelt und wird aus den &uuml;bergebenen
     *  Headereintr&auml;gen &uuml;bernommen.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  input   InputStream mit den Daten vom Request
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, InputStream input)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, input, null, Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert.
     *  @param  address Adresse der Ressource
     *  @param  output  Datenausgabestrom f&uuml;r den Content
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, OutputStream output)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, null, null, null, output, Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  output  Datenausgabestrom f&uuml;r den Content
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, OutputStream output)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, null, null, output, Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  output  Datenausgabestrom f&uuml;r den Content
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, OutputStream output)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, null, output, Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert. Bei der
     *  &Uuml;bergabe von Requestdaten als ByteArray wird die Content-Length
     *  Angabe automatisch ermittelt.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  data    Daten vom Request
     *  @param  output  Datenausgabestrom f&uuml;r den Content
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, byte[] data, OutputStream output)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, data, output, Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in das Dateisystem ausgelagert. Bei der
     *  &Uuml;bergabe von Requestdaten als InputStream wird die Content-Length
     *  Angabe nicht automatisch ermittelt und wird aus den &uuml;bergebenen
     *  Headereintr&auml;gen &uuml;bernommen.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  input   InputStream mit den Daten vom Request
     *  @param  output  Datenausgabestrom f&uuml;r den Content
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, InputStream input, OutputStream output)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, input, output, Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in den Datenstrom geschrieben.
     *  @param  address  Adresse der Ressource
     *  @param  output   Datenausgabestrom
     *  @param  complete Option <b>true</b> Content mit Header
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, OutputStream output, boolean complete)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, null, null, null, output, complete ? Connector.SWAP_COMPLETE : Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in den Datenstrom geschrieben.
     *  @param  address  Adresse der Ressource
     *  @param  method   HTTP-Methode vom Request
     *  @param  output   Datenausgabestrom
     *  @param  complete Option <b>true</b> Content mit Header
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, OutputStream output, boolean complete)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, null, null, output, complete ? Connector.SWAP_COMPLETE : Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in den Datenstrom geschrieben.
     *  @param  address  Adresse der Ressource
     *  @param  method   HTTP-Methode vom Request
     *  @param  header   zus&auml;tzliche Headerzeilen als Array
     *  @param  output   Datenausgabestrom
     *  @param  complete Option <b>true</b> Content mit Header
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, OutputStream output, boolean complete)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, null, output, complete ? Connector.SWAP_COMPLETE : Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in den Datenstrom geschrieben. Bei der
     *  &Uuml;bergabe von Requestdaten als ByteArray wird die Content-Length
     *  Angabe automatisch ermittelt.
     *  @param  address  Adresse der Ressource
     *  @param  method   HTTP-Methode vom Request
     *  @param  header   zus&auml;tzliche Headerzeilen als Array
     *  @param  data     Daten vom Request
     *  @param  output   Datenausgabestrom
     *  @param  complete Option <b>true</b> Content mit Header
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, byte[] data, OutputStream output, boolean complete)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, data, output, complete ? Connector.SWAP_COMPLETE : Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Der
     *  Content wird dabei gesplittet und der Response-Body unter der im Header
     *  enthaltenen Content-Signatur in den Datenstrom geschrieben. Bei der
     *  &Uuml;bergabe von Requestdaten als InputStream wird die Content-Length
     *  Angabe nicht automatisch ermittelt und wird aus den &uuml;bergebenen
     *  Headereintr&auml;gen &uuml;bernommen.
     *  @param  address  Adresse der Ressource
     *  @param  method   HTTP-Methode vom Request
     *  @param  header   zus&auml;tzliche Headerzeilen als Array
     *  @param  input    InputStream mit den Daten vom Request
     *  @param  output   Datenausgabestrom
     *  @param  complete Option <b>true</b> Content mit Header
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, InputStream input, OutputStream output, boolean complete)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, input, output, complete ? Connector.SWAP_COMPLETE : Connector.SWAP_CONTENT);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S).
     *  @param  address Adresse der Ressource
     *  @param  swap    Option <code>true</code> speichert den Content komplett
     *                  in den Speicher, <code>false</code> lagert den Content
     *                  in das Dateisystem unter der Signatur vom Header-Feld
     *                  <code>Content-Signature</code> aus
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, boolean swap)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, null, null, new byte[0], null, swap ? Connector.SWAP_CONTENT : Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S).
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  swap    Option <code>true</code> speichert den Content komplett
     *                  in den Speicher, <code>false</code> lagert den Content
     *                  in das Dateisystem unter der Signatur vom Header-Feld
     *                  <code>Content-Signature</code> aus
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, boolean swap)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, null, new byte[0], null, swap ? Connector.SWAP_CONTENT : Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S).
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  swap    Option <code>true</code> speichert den Content komplett
     *                  in den Speicher, <code>false</code> lagert den Content
     *                  in das Dateisystem unter der Signatur vom Header-Feld
     *                  <code>Content-Signature</code> aus
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, boolean swap)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, new byte[0], null, swap ? Connector.SWAP_CONTENT : Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Bei der
     *  &Uuml;bergabe von Requestdaten als ByteArray wird die Content-Length
     *  Angabe automatisch ermittelt.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  data    Daten vom Request
     *  @param  swap    Option <code>true</code> speichert den Content komplett
     *                  in den Speicher, <code>false</code> lagert den Content
     *                  in das Dateisystem unter der Signatur vom Header-Feld
     *                  <code>Content-Signature</code> aus
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, byte[] data, boolean swap)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, data, null, swap ? Connector.SWAP_CONTENT : Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Bei der
     *  &Uuml;bergabe von Requestdaten als InputStream wird die Content-Length
     *  Angabe nicht automatisch ermittelt und wird aus den &uuml;bergebenen
     *  Headereintr&auml;gen
     *  &uuml;bernommen.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  input   InputStream mit den Daten vom Request
     *  @param  swap    Option <code>true</code> speichert den Content komplett
     *                  in den Speicher, <code>false</code> lagert den Content
     *                  in das Dateisystem unter der Signatur vom Header-Feld
     *                  <code>Content-Signature</code> aus
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     */
    public Content commit(String address, String method, String[] header, InputStream input, boolean swap)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        return this.commit(address, method, header, input, null, swap ? Connector.SWAP_CONTENT : Connector.SWAP_NONE);
    }

    /**
     *  Ermittelt den Content der angegebene Ressource mittels HTTP(S). Bei der
     *  &Uuml;bergabe von Requestdaten als InputStream wird die Content-Length
     *  Angabe nicht automatisch ermittelt und wird aus den &uuml;bergebenen
     *  Headereintr&auml;gen &uuml;bernommen. Sonst wird Content-Length
     *  automatisch gesetzt.
     *  @param  address Adresse der Ressource
     *  @param  method  HTTP-Methode vom Request
     *  @param  header  zus&auml;tzliche Headerzeilen als Array
     *  @param  data    Daten vom Request als ByteArray oder InputStream
     *  @param  store   externer Datenausgabestrom zur Auslagerung des Content
     *  @param  mode    Art der Datenauslagerung, diese erfolgt in das
     *                  Dateisystem unter der Signatur vom Header-Feld
     *                  <code>Content-Signature</code>
     *  @return der ermittelte Content
     *  @throws IOException bei fehlerhaftem Datenzugriff
     *  @throws NoSuchAlgorithmException bei unbekannter
     *          Verschl&uuml;sselungsmethode
     *  @throws KeyStoreException bei fehlerhaftem Zugriff auf den KeyStore
     *  @throws KeyManagementException bei fehlerhafter Verwendung des KeyStores
     *  @throws IllegalArgumentException bei ung&uuml;ltiger Protokollangabe,
     *          unterst&uuml;tzt werden keine Angabe (leer), http und https, bei
     *          ung&uuml;ltiger HTTP-Methode, bei ung&uuml;ltigen Daten- bzw.
     *          Stream Objekten f&uuml;r den Request-Body
     *  @see    #SWAP_COMPLETE
     *  @see    #SWAP_CONTENT
     *  @see    #SWAP_NONE
     */
    private Content commit(String address, String method, String[] header, Object data, Object store, int mode)
        throws IOException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {

        Address               source;
        ByteArrayOutputStream buffer;
        Content               content;
        InputStream           input;
        OutputStream          output;
        OutputStream          storage;
        Socket                socket;
        String                result;
        String                stream;
        String                string;

        byte[]                bytes;

        boolean               secure;
        int                   blocksize;
        int                   count;
        int                   cursor;
        int                   filter;
        int                   length;
        int                   loop;
        int                   interrupt;
        int                   size;
        int                   value;
        int                   volume;

        //initiale Einrichtung der Variablen
        input   = null;
        output  = null;
        socket  = null;
        storage = null;

        //als Requestdaten sind nur ByteArray oder InputStream zulaessig
        if (data != null && !(data instanceof byte[] || data instanceof InputStream)) throw new IllegalArgumentException("Invalid object type for request data");

        //als Content Stream ist nur eine Outputstream zulaessig
        if (store != null && !(store instanceof OutputStream)) throw new IllegalArgumentException("Invalid object type for content stream");

        //der Datenpuffer wird eingerichtet
        buffer = new ByteArrayOutputStream();

        //das Address Objekt wird eingerichtet
        source = Address.parse(address);

        //als Protokoll werden nur HTTP und HTTPS unterstuetzt
        if (Arrays.binarySearch(new String[] {"", "http", "https"}, source.getProtocol()) < 0) throw new IllegalArgumentException(("Invalid protocol, ").concat(source.getProtocol()).concat(" not supported"));

        //die Methode wird ermittelt
        string = (method == null) ? "" : method.toUpperCase().trim();
        stream = (string.equals("GET") || string.length() == 0) ? "GET" : string;

        //ungueltige Methoden werden nicht unterstuetzt
        if (stream.indexOf(' ') >= 0) throw new IllegalArgumentException(("Invalid in method ").concat(string));

        //der Proxybetrieb wird ermittelt
        string = (String)this.parameters.get(Connector.CONNECTOR_PROXY_SERVER);
        secure = source.getProtocol().equals("https");
        stream = stream.concat(" ").concat((string.length() > 0 && !secure) ? source.toString() : source.toStringShort()).trim();
        stream = stream.concat(" ").concat("HTTP/1.0\r\n");

        //die Content-Length wird fuer ByteArrays ermittelt und gesetzt
        if (data instanceof byte[]) stream = stream.concat("Content-Length: ").concat(String.valueOf(((byte[])data).length)).concat("\r\n");

        for (loop = filter = 0; header != null && loop < header.length; loop++) {

            //die Eintraege fuer den Header werden ermittelt
            string = ((string = header[loop]) == null) ? "" : string.trim();

            if ((cursor = string.indexOf('\r')) >= 0) string = string.substring(0, cursor).trim();
            if ((cursor = string.indexOf('\n')) >= 0) string = string.substring(0, cursor).trim();

            result = ((cursor = string.indexOf(':')) >= 0) ? string.substring(0, cursor).toLowerCase().trim() : "";

            //leere Felder werden nicht uebernommen
            if (result.length() == 0) continue;

            //die Content-Length wird nur bei einem InputStream uebernommen
            if (!(data instanceof InputStream) && result.equals("content-length")) continue;

            if (result.equals("host"))       filter |= 1;
            if (result.equals("user-agent")) filter |= 2;
            if (result.equals("accept"))     filter |= 4;

            //der Header wird erweitert
            stream = stream.concat(string).concat("\r\n");
        }

        //die Eintraege Accept und User-Agent werden gegebenenfalls gesetzt
        if ((filter & 1) != 1) stream = stream.concat("Host: ").concat(source.getServer()).concat("\r\n");
        if ((filter & 2) != 2) stream = stream.concat("User-Agent: Seanox-Connector/").concat(Connector.VERSION).concat("\r\n");
        if ((filter & 4) != 4) stream = stream.concat("Accept: *").concat("/").concat("*\r\n");

        try {

            //der Socket wird eingerichtet
            socket = this.createSocket(source.getServer(), source.getPort(), secure);

            //die Datenstroeme werden eingerichtet
            output = socket.getOutputStream();
            input  = socket.getInputStream();

            //der Request wird ausgeben
            output.write(stream.concat("\r\n").getBytes());

            blocksize = ((Integer)this.parameters.get(Connector.CONNECTOR_BLOCKSIZE)).intValue();
            interrupt = ((Integer)this.parameters.get(Connector.CONNECTOR_INTERRUPT)).intValue();

            for (count = 0; count < 4;) {

                if ((value = input.read()) < 0) break;

                //der Request wird auf kompletten Header geprueft
                count = (value == ((count % 2) == 0 ? 13 : 10)) ? count +1 : 0;

                //die Daten werden gespeichert
                buffer.write(value);

                //nach jedem kompletten Block folgt eine Systemunterbrechung
                if ((buffer.size() % blocksize) == 0) this.sleep(interrupt);
            }

            //die Content-Signatur wird ermittelt
            stream = this.getStorageContext();
            string = Connector.signContentHeader(buffer.toString(), stream);

            //der Content wird mit dem Header eingerichtet
            content = new Content(string.getBytes());

            //der Datenpuffer wird zurueckgesetzt
            buffer.reset();

            //der Pfad der Auslagerungsdatei wird ermittelt
            string = (String)this.parameters.get(Connector.CONNECTOR_STORAGE_DIRECTORY);
            string = string.concat("connector-").concat(stream).concat(".content");

            //der Datenstrom zur Speicherung bzw. Auslagerung des Content wird
            //der entsprechend der angegebenen Option ermittelt
            storage = (store == null) ? (mode == Connector.SWAP_CONTENT || mode == Connector.SWAP_COMPLETE) ? (OutputStream)new FileOutputStream(string) : (OutputStream)buffer : (OutputStream)store;

            //im Modus COMPLETE wird auch der Header mit Signatur ausgelagert
            if (mode == Connector.SWAP_COMPLETE) {

                storage.write(content.getHeader());
                storage.write(("\r\n\r\n").getBytes());
            }

            //das Datenvolumen wird ermittelt
            try {length = Integer.parseInt(content.getField("Content-Length"));
            } catch (Exception exception) {length = 0;}

            //der Datenpuffer wird eingerichtet
            bytes = new byte[blocksize];

            for (volume = 0; volume < length || length == 0;) {

                //die Daten werden aus dem Datenstrom gelesen
                if ((size = input.read(bytes)) < 0) break;

                //bei Speichern der Response Datenmenge wird das ermittelte
                //Datenvolumen nicht ueberschritten
                if (size < 0) size = 0;
                if (!((volume +size) <= length || length == 0)) size = length -volume;

                //die Daten werden an das CGI uebergeben
                if (size > 0) storage.write(bytes, 0, size);

                //das Datenvolumen wird berechnet
                volume += size;

                //Systemunterbrechung fuer weitere Prozesse
                this.sleep(interrupt);
            }

            if (mode != Connector.SWAP_CONTENT && mode != Connector.SWAP_COMPLETE) {

                //der Datenpuffer des Contents wird gesetzt
                Accession.set(content, "data", buffer.toByteArray());
            }

        } catch (IllegalAccessException exception) {

            string = String.valueOf(exception.getMessage());

            string = (string == null) ? "" : (" (").concat(string).concat(")");

            throw new IOException(("Internal connector exception").concat(string));

        } catch (NoSuchFieldException exception) {

            string = String.valueOf(exception.getMessage());

            string = (string == null) ? "" : (" (").concat(string).concat(")");

            throw new IOException(("Internal connector exception").concat(string));

        } finally {

            //der Socket wird geschlossen
            try {socket.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            //der Dateneingangsstrom wird geschlossen
            try {input.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            //der Datenausgangsstrom wird geschlossen
            try {output.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }

            //der Datenauslagerungstrom wird geschlossen
            try {storage.close();
            } catch (Exception exception) {

                //keine Fehlerbehandlung vorgesehen
            }
        }

        return content;
    }
}