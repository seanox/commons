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

import java.net.InetAddress;
import java.net.Socket;

/**
 *  Abstrakte Klasse zur Implementierung einer Connection, welche Methoden
 *  f&uuml;r den Zugriff auf die Informationen der eingerichteten
 *  Socketverbindung zur Verf&uuml;gung stellt.<br>
 *  <br>
 *  Connection 1.2013.0427<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0427
 */
public abstract class Connection {

    /** Objekt der Systemumgebung */
    protected Environment environment;
    
    /** Socketobjekt */
    protected Socket socket;

    /** Status der sicheren Serververbindung */
    protected boolean secure;    

    /**
     *  Konstruktor, richtet die Connection auf Basis des mit der Schnittstelle
     *  &uuml;bergebenen Environments ein.
     *  @param  connector Schnittstelle
     *  @throws IllegalArgumentException bei ung&uml;tiger Schnittstelle
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    protected Connection(Connector connector) {

        if (connector == null) throw new IllegalArgumentException("Invalid connector [null]");

        if (connector.environment == null) throw new RuntimeException("Connector environment is not established");

        this.environment = connector.environment;
    }
    
    /**
     *  Konstruktor, richtet die Connection auf Basis des Accession Objekts ein.
     *  @param  socket Socket
     *  @param  secure sicheren Serververbindung
     *  @throws IllegalArgumentException bei ung&uml;tigem Bezugsobjekt
     *  @throws RuntimeException bei fehlerhaftem Zugriff auf erforderliche
     *          Ressourcen oder inkompatibler Laufzeitumgebung
     */
    protected Connection(Socket socket, boolean secure) {

        if (socket == null) throw new IllegalArgumentException("Server socket is not established");
        
        this.socket = socket;
        this.secure = secure;
    }    

    /**
     *  R&uuml;ckgabe der Adresse vom lokalen Servers.
     *  @return die Adresse des lokalen Servers
     */
    public String getServerAddress() {

        String string;
        
        if (this.socket != null) return this.socket.getLocalAddress().getHostAddress();

        string = this.getServerName();

        try {return InetAddress.getByName(string).getHostAddress();
        } catch (Exception exception) {

            return string;
        }
    }

    /**
     *  R&uuml;ckgabe vom Namen des lokalen Servers
     *  @return der Name des lokalen Servers
     */
    public String getServerName() {

        String string;
        
        if (this.socket != null) return this.socket.getLocalAddress().getHostName();

        string = this.environment.get("server_name").trim();

        if (string.length() == 0) string = this.environment.get("http_host").trim();

        try {return InetAddress.getByName(string).getHostName();
        } catch (Exception exception) {

            return string;
        }
    }

    /**
     *  R&uuml;ckgabe vom Port des lokalen Servers
     *  @return der Port des lokalen Servers
     */
    public int getServerPort() {
        
        if (this.socket != null) return this.socket.getLocalPort();

        try {return Integer.parseInt(this.environment.get("server_port").trim());
        } catch (Exception exception) {

            return 0;
        }
    }

    /**
     *  R&uuml;ckgabe der Adresse vom zugreifenden Server.
     *  @return die Adresse des zugreifenden Servers
     */
    public String getRemoteAddress() {
        
        if (this.socket != null) return this.socket.getInetAddress().getHostAddress();

        return this.environment.get("remote_addr").trim();
    }

    /**
     *  R&uuml;ckgabe vom Namen des zugreifenden Servers
     *  @return der Name des zugreifenden Servers
     */
    public String getRemoteName() {

        String string;
        
        if (this.socket != null) return this.socket.getInetAddress().getHostName();

        string = this.environment.get("remote_addr").trim();

        try {return InetAddress.getByName(string).getHostName();
        } catch (Exception exception) {

            return string;
        }
    }

    /**
     *  R&uuml;ckgabe vom Port des zugreifenden Servers
     *  @return der Port des zugreifenden Servers
     */
    public int getRemotePort() {
        
        if (this.socket != null) return this.socket.getPort();

        try {return Integer.parseInt(this.environment.get("remote_port").trim());
        } catch (Exception exception) {

            return 0;
        }
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn eine sichere Serverbindung besteht.
     *  Sonst wird <code>false</code> zur&uuml;ck gegeben.
     *  @return <code>true</code> wenn eine sichere Serverbindung besteht
     */
    public boolean isSecure() {
        
        if (this.socket != null) return this.secure;

        return this.environment.get("script_uri").toLowerCase().trim().startsWith("https://");
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zur Connection als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zur Connection als String
     */
    public String toString() {

        String       string;
        StringBuffer result;

        //der Zeilenumbruch wird ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = new StringBuffer("[").append(this.getClass().getName()).append("]").append(string);

        result.append("  remote address = ").append(this.getRemoteAddress()).append(string);
        result.append("  remote name    = ").append(this.getRemoteName()).append(string);
        result.append("  remote port    = ").append(this.getRemotePort()).append(string);
        result.append("  secure         = ").append(this.isSecure()).append(string);
        result.append("  server address = ").append(this.getServerAddress()).append(string);
        result.append("  server name    = ").append(this.getServerName()).append(string);
        result.append("  server port    = ").append(this.getServerPort()).append(string);

        return result.toString();
    }
}