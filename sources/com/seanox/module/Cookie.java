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

/**
 *  Cookies, stellt ein Objekt zur Datenhaltung von Cookie-Informationen zur
 *  Verf&uuml;gung. Die Datenhaltung erfolgt in entsprechenden Feldern. Zur
 *  Information k&ouml;nnen diese formatiert ausgegeben werden.<br>
 *  <br>
 *  <b>Hinweis</b> - Die Verarbeitung von Sonderzeichen ausserhalb von ASCII 32
 *  bis 127 erfolgt nur kodiert. Zu eventuelle Kodierung bei Set- bzw.
 *  Dekodierung bei Get-Methoden kann <code>Codec.decode(...)</code> bzw.
 *  <code>Codec.encode(...)</code> verwendet werden.<br>
 *  <br>
 *  Cookie 1.2013.0426<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0426
 */
public class Cookie extends com.seanox.network.http.Cookie {

    /** Versionskennung f&uuml;r die Serialisierung */
    private static final long serialVersionUID = 3585547081200860691L;

    /**
     *  Konstruktor, richtet den Cookie ein.
     *  @param  name Name des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen, bei leeren Strings oder der Angabe von <code>null</code>
     */
    public Cookie(String name) {

        super(name);
    }

    /**
     *  Konstruktor, richtet den Cookie ein.
     *  @param name  Name des Cookies
     *  @param  value Wert des Cookies
     *  @throws IllegalArgumentException bei der Verwendung von nicht konformen
     *          Zeichen nach der RFC 2068, bei der &Uuml;berschreitung von 4096
     *          Zeichen, bei leeren Strings oder der Angabe von <code>null</code>
     */
    public Cookie(String name, String value) {

        super(name, value);
    }
}