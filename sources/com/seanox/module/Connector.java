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
 *  Abstrakte Klasse zur Implementierung eines Connector, welche eine
 *  Schnittstelle zur Seanox Module API zur Verf&uuml;gung. Der Zugriff erfolgt
 *  dabei &uuml;ber die bereitgestellten Objekte Connection, Environment,
 *  Request, Response, Fragments und Cookie. Diese unterst&uuml;tzen unter
 *  anderem den Umgang mit Multipart-Objekten sowie die Kodierung und
 *  Dekodierung von BASE64, UTF8, MIME und DOT.<br>
 *  <br>
 *  Connector 1.2013.0427<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0427
 */
public abstract class Connector {

    /** Objekt der Serververbindung */
    protected Connection connection;

    /** Objekt der Systemumgebung */
    protected Environment environment;

    /** Request Objekt zur Bearbeitung der Anfrage */
    protected Request request;

    /** Response Objekt zur Beantwortung des Request */
    protected Response response;

    /**
     *  R&uuml;ckgabe der formatierten Information zum Connector als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zum Response als String
     */
    public String toString() {
        
        String       string;
        StringBuffer result;

        //der Zeilenumbruch wird ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = new StringBuffer("[").append(this.getClass().getName()).append("]").append(string);

        result.append("  connection  = ").append(this.connection.getClass().getName()).append(string);
        result.append("  environment = ").append(this.environment.getClass().getName()).append(string);
        result.append("  request     = ").append(this.request.getClass().getName()).append(string);
        result.append("  response    = ").append(this.response.getClass().getName()).append(string);

        return result.toString();
    }
}