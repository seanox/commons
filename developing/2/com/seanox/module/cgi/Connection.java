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
package com.seanox.module.cgi;

/**
 *  Connection stellt Methoden f&uuml;r den Zugriff auf die Informationen der
 *  eingerichteten Socketverbindung zur Verf&uuml;gung. Zur Information
 *  k&ouml;nnen die aktuellen Daten formatiert ausgegeben werden.<br>
 *  <br>
 *  Connection 1.2013.0427<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0427
 */
public class Connection extends com.seanox.module.Connection {

    /**
     *  Konstruktor, richtet die Connection auf Basis des mit der Schnittstelle
     *  &uuml;bergebenen Environments ein.
     *  @param  connector Schnittstelle
     *  @throws IllegalArgumentException bei ung&uml;tiger Schnittstelle
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    Connection(Connector connector) {
        
        super(connector);
    }
}