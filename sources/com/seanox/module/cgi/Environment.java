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
 *  Environment stellt Methoden f&uuml;r den Zugriff auf die
 *  CGI-Umgebungsvariablen zur Verf&uuml;gung. Alle auf den Header des Request
 *  bezogenen Umgebungsvariablen beginnen dabei mit HTTP und sind im Format
 *  HTTP_FIELD_NAME enthalten, somit wird z.B. der Header Eintrag User-Agent als
 *  HTTP_USER_AGENT eingetragen.<br>
 *  <br>
 *  <b>Hinweis</b> - Das Bezugsobjekt wurde speziell auf Seanox Devwex
 *  abgestimmt. Bei der Verwendung einer anderen Serverumgebung m&uuml;ssen die
 *  Zugriffe auf die entsprechenden Ressourcen speziell implementiert werden.<br>
 *  <br>
 *  Environment 1.2013.0426<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0426
 */
public class Environment extends com.seanox.module.Environment {

    /**
     *  Konstruktor, richtet das Environment auf Basis der Schnittstelle ein.
     *  @param  connector Schnittstelle
     *  @throws IllegalArgumentException bei ung&uml;tiger Schnittstelle
     */
    Environment(Connector connector) {

        super();
        
        if (connector == null) throw new IllegalArgumentException("Invalid connector [null]");
    }
}