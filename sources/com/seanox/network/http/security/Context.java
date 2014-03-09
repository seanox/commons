/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt. Diese
 *  Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2012 Seanox Software Solutions
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
package com.seanox.network.http.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.seanox.network.http.Connector;

/**
 *  Context stellt eine Kapselung zur Erstellung einer SSLSocketFactory f&uuml;r
 *  das <code>javax.net.ssl</code> Paket zur Verf&uuml;gung, die eine optional
 *  Verf&uuml;gbarkeit der entsprechenden Pakete erm&ouml;glicht ohne das der
 *  HotSpot fehlernde Referenzen im Connecotr erkennt. Dazu ist jedoch
 *  erforderlich, dass der Context &uuml;ber Reflections angebunden wird.<br>
 *  <br>
 *  <b>Hinweis</b> - Die &uuml;ber diesen Context erstellte SSLSocketFactory
 *  nimmt keine Zertifikatspr&uuml;fung vor. Ist diese erforderlich, kann ein
 *  entsprechender TrustManager und Context implementiert und &uuml;ber den
 *  Parameter <code>connector.secure.trusting</code> eingebuden werden. F&uuml;r
 *  diesen Zweck steht dieser Context als Referenz Implementierung zur
 *  Verf&uuml;gung. Die ben&ouml;tigte SSL Konfiguration wird beim Aufruf des
 *  Context in Form von Properties &uuml;bergeben. F&uuml;r den Zugriff stellt
 *  der Connector entsprechende Konstanten zur Verf&uuml;gung.<br>
 *  <br>
 *  Context 1.2007.1001<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2007.1001
 */
public class Context {

    /**
     *  Erstellt entsprechend der als Properties &uuml;bergebenen Konfiguration
     *  eine SSLSocketFactory die keine G&uuml;ltigkeitspr&uuml;fung eventueller
     *  Zertifikate vornimmt.
     *  @param  properties Konfiguration
     *  @return die erstellte SSLSocketFactory
     *  @throws NoSuchAlgorithmException bei unbekannten
     *          Verschl&uuml;sselungsmethoden
     *  @throws KeyManagementException bei Fehlern mit dem Basiszertifikat
     */
    public static SSLSocketFactory getSocketFactory(Properties properties)
        throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext context;

        //der Context wird eingerichtet
        context = SSLContext.getInstance(properties.getProperty(Connector.CONTEXT_SECURE_PROTOCOL));

        //der TrustManager wird eingerichtet
        context.init(null, new TrustManager[] {new Trusting()}, null);

        //der Socket wird ermittelt
        return context.getSocketFactory();
    }
}