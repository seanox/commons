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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 *  Trusting stellt einen einfachen auf X509 basierenden TrustManager f&uuml;r
 *  Testzwecke mit selbsterstellten, nicht best&auml;tigten und ung&uuml;ltigen
 *  Zertifikaten f&uuml;r das <code>javax.net.ssl</code> Paket zur
 *  Verf&uuml;gung.<br>
 *  <br>
 *  <b>Hinweis</b> - Der Einsatz in produktiven Systemen ist nicht zu empfehlen
 *  und sollte immer mit g&uuml;ltigen Zertifikaten und dem Standard
 *  X509TrustManager erfolgen.<br>
 *  <br>
 *  Trusting 1.2007.1001<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2007.1001
 */
public class Trusting implements X509TrustManager {

    /**
     *  R&uuml;ckgabe der akzeptierten Aussteller als Array.
     *  @return die akzeptierten Aussteller als Array
     */
    public X509Certificate[] getAcceptedIssuers() {

        return null;
    }

    /**
     *  &Uuml;berpr&uuml;ft die Zertifikate des Clients.
     *  <b>Hinweis</b> - Trusting ist f&uuml;r Testzwecke mit selbst erstellten
     *  und nicht best&auml;tigten Zertifikaten entwickelt worden. Somit werden
     *  alle, auch ung&uuml;ltige Zertifikate f&uuml;r g&uuml;ltig erkl&auml;rt.
     *  @param  certificates  Zertifikat
     *  @param  authorization Autorisierungstyp
     *  @throws CertificateException bei ung&uuml;ltigen Zertifikaten
     */
    public void checkClientTrusted(X509Certificate[] certificates, String authorization)
        throws CertificateException {

        return;
    }

    /**
     *  &Uuml;berpr&uuml;ft die Zertifikate des Servers.
     *  <b>Hinweis</b> - Trusting ist f&uuml;r Testzwecke mit selbst erstellten
     *  und nicht best&auml;tigten Zertifikaten entwickelt worden. Somit werden
     *  alle, auch ung&uuml;ltige Zertifikate f&uuml;r g&uuml;ltig erkl&auml;rt.
     *  @param  certificates Zertifikat
     *  @param  authorization Autorisierungstyp
     *  @throws CertificateException bei ung&uuml;ltigen Zertifikaten
     */
    public void checkServerTrusted(X509Certificate[] certificates, String authorization)
        throws CertificateException {

        return;
    }
}