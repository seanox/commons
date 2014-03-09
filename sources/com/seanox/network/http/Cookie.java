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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.seanox.common.Codec;

/**
 *  Cookie stellt ein Objekt zur Datenhaltung von Cookie Informationen zur
 *  Verf&uuml;gung. Die Datenhaltung erfolgt in entsprechenden Feldern. Zur
 *  Information k&ouml;nnen diese formatiert ausgegeben werden.<br>
 *  <br>
 *  <b>Hinweis</b> - Die Verarbeitung von Sonderzeichen ausserhalb von ASCII 32
 *  bis 127 erfolgt nur kodiert. Zu eventuelle Kodierung bei Set- bzw.
 *  Dekodierung bei Get-Methoden kann <code>Content.decode(...)</code> bzw.
 *  <code>Content.encode(...)</code> verwendet werden.<br>
 *  <br>
 *  Cookie 1.2013.0420<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0420
 */
public class Cookie implements Cloneable, Serializable {

    /** Versionskennung f&uuml;r die Serialisierung */
    private static final long serialVersionUID = 8972375329928498023L;

    /** Domain des Cookies */
    private String domain;

    /** Name des Cookies */
    private String name;

    /** Pfad des Cookies */
    private String path;

    /** Wert des Cookies */
    private String value;

    /** Sicherheitskennung des Cookies */
    private boolean secure;

    /** Verfallszeitpunkt des Cookies */
    private long expire;

    /**
     *  Konstruktor, richtet den Cookie ein.
     *  @param  name Name des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen, bei leeren Strings oder <code>null</code>
     */
    public Cookie(String name) {

        //alle Felder werden initialisiert
        this.clear();

        //der Name wird gesetzt
        this.setName(name);
    }

    /**
     *  Konstruktor, richtet den Cookie ein.
     *  @param  name  Name des Cookies
     *  @param  value Wert des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen, bei leeren Strings oder <code>null</code>
     */
    public Cookie(String name, String value) {

        //alle Felder werden initialisiert
        this.clear();

        //die Meta Daten werden gesetzt
        this.setName(name);
        this.setValue(value);
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn der String der Regelung von
     *  Sonderzeichen laut RFC 2068 entspricht, sonst <code>false</code>.
     *  @param  string zu pr&uuml;fender String
     *  @return <code>true</code>, wenn der String der RFC 2068 entspricht
     */
    private static boolean valid(String string) {

        char digit;
        int  loop;
        int  size;

        //kein String wird wie ein leerer String behandelt
        if (string == null) return true;

        //die Laenge des String wird ermittelt
        size = string.length();

        //die einzelnen Zeichen werden geprueft, zugelassen sind nur die ASCII
        //Zeichen 32 - 127 nicht aber Komma und Semikolon
        for (loop = 0; loop < size; loop++) {

            if ((digit = string.charAt(loop)) < 0x20 || digit > 0x7F || digit == 0x3B || digit == 0x2C) return false;
        }

        return true;
    }

    /**
     *  R&uuml;ckgabe von der Domain des Cookies.
     *  @return die Domain des Cookies
     */
    public String getDomain() {

        return this.domain;
    }

    /**
     *  R&uuml;ckgabe der Verfallszeit des Cookies in Millisekunden.
     *  @return die Verfallszeit des Cookies in Millisekunden
     */
    public long getExpire() {

        return this.expire;
    }

    /**
     *  R&uuml;ckgabe vom Namen des Cookies.
     *  @return der Name des Cookies
     */
    public String getName() {

        return this.name;
    }

    /**
     *  R&uuml;ckgabe vom Pfad des Cookies.
     *  @return der Pfad des Cookies
     */
    public String getPath() {

        return this.path;
    }

    /**
     *  R&uuml;ckgabe des Sicherheitskennung des Cookies.
     *  @return die Sicherheitskennung des Cookies
     */
    public boolean isSecure() {

        return this.secure;
    }

    /**
     *  R&uuml;ckgabe vom Wert des Cookie.
     *  @return der Wert vom Cookie
     */
    public String getValue() {

        return this.value;
    }

    /**
     *  Setzt die Domain des Cookies.
     *  @param  domain Domain des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen
     */
    public void setDomain(String domain) {

        if (!Cookie.valid(domain)) throw new IllegalArgumentException(("Invalid character in ").concat(domain));

        if (domain != null && domain.length() > 4096) throw new IllegalArgumentException("Invalid string length, over 4096 characters");

        this.domain = (domain == null) ? "" : domain.trim();
    }

    /**
     *  Setzt den Verfallszeitpunkt des Cookies in Millisekunden.
     *  @param time Verfallszeitpunkt in Millisekunden
     */
    public void setExpire(long time) {

        this.expire = time;
    }

    /**
     *  Setzt die Namen des Cookies.
     *  @param  name Namen des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen, bei leeren Strings oder <code>null</code>
     */
    public void setName(String name) {

        if (!Cookie.valid(name) || (name != null && name.indexOf('=') >= 0)) throw new IllegalArgumentException(("Invalid character in ").concat(name));

        if (name != null && name.length() > 4096) throw new IllegalArgumentException("Invalid string length, over 4096 characters");

        if (name == null || name.trim().length() == 0) throw new IllegalArgumentException("Invalid name");

        this.name = name.trim();
    }

    /**
     *  Setzt den Pfad des Cookies.
     *  @param  path Pfad des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen, bei leeren Strings oder <code>null</code>
     */
    public void setPath(String path) {

        if (!Cookie.valid(path)) throw new IllegalArgumentException(("Invalid character in ").concat(path));

        if (path != null && path.length() > 4096) throw new IllegalArgumentException("Invalid string length, over 4096 characters");

        this.path = (path == null) ? "" : path;
    }

    /**
     *  Setzt die Sicherheitskennung des Cookies.
     *  @param secure Sicherheitskennung des Cookies
     */
    public void setSecure(boolean secure) {

        this.secure = secure;
    }

    /**
     *  Setzt den Wert des Cookies.
     *  @param  value Wert des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen
     */
    public void setValue(String value) {

        if (value != null && value.length() > 4096) throw new IllegalArgumentException("Invalid string length, over 4096 characters");

        this.value = (value == null) ? "" : value;
    }

    /** Setzt den kompletten Cookie bis auf den Namen zur&uuml;ck. */
    public void clear() {

        this.domain = "";
        this.path   = "";
        this.value  = "";

        this.secure = false;

        this.expire = 0;
    }

    /**
     *  R&uuml;ckgabe einer Kopie vom Cookies als Objekt.
     *  @return eine Kopie des Cookies als Objekt
     */
    public Object clone() {

        Cookie cookie = new Cookie(this.name);

        cookie.domain = this.domain;
        cookie.expire = this.expire;
        cookie.path   = this.path;
        cookie.secure = this.secure;
        cookie.value  = this.value;

        return cookie;
    }

    /**
     *  Formatiert das Datum im angebenden Format und in der angegebenen Zone.
     *  R&uuml;ckgabe das formatierte Datum als String.
     *  @param  format Formatbeschreibung
     *  @param  date   zu formatierendes Datum
     *  @param  zone   Zeitzone, <code>null</code> Standardzone
     *  @return das formatierte Datum als String
     */
    private static String formatDate(String format, Date date, String zone) {

        SimpleDateFormat pattern;

        //die Formatierung wird eingerichtet
        pattern = new SimpleDateFormat(format, Locale.US);

        //die Zeitzone wird gegebenenfalls fuer die Formatierung gesetzt
        if (zone != null) pattern.setTimeZone(TimeZone.getTimeZone(zone));

        //die Zeitangabe wird formatiert
        return pattern.format(date);
    }

    /**
     *  R&uuml;ckgabe der HTTP optimierten Information zum Cookie als String
     *  f&uuml;r die Verwendung als Set-Cookie Eintrag des HTTP-Headers.
     *  @return die HTTP optimierten Information zum Cookie als String
     */
    public String toString() {

        String stream;
        String string;

        stream = (this.getValue().length() == 0) ? "deleted" : this.getValue();
        string = this.getName().concat("=").concat(Codec.encode(stream, Codec.MIME));
        stream = Cookie.formatDate("E, dd-MMM-yy HH:mm:ss z", new Date(this.getExpire()), "GMT").trim();

        if (stream.length() > 0 && this.getExpire() != 0) string = string.concat("; expires=").concat(stream);
        if (this.getPath().length() > 0) string = string.concat("; path=").concat(Codec.encode(this.getPath(), Codec.MIME));
        if (this.getDomain().length() > 0) string = string.concat("; domain=").concat(this.getDomain());
        if (this.isSecure()) string = string.concat("; secure");

        return string;
    }
}