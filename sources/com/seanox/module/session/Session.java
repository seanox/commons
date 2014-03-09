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
package com.seanox.module.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *  Session stellt ein Objekt zur sitzungsbezogenen Haltung von Daten zur
 *  Verf&uuml;gung. Die Sessions werden &uuml;ber den Session-Context verwaltet,
 *  womit nur Sessions, die &uuml;ber den Context eingerichtet wurden,
 *  g&uuml;ltig.<br>
 *  <br>
 *  Session 1.2007.1207<br>
 *  Copyright (C) 2012 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2007.1207
 */
public class Session {

    /** Attribute der Session */
    private volatile Hashtable attributes;

    /** Sessionverwaltung */
    private volatile String signature;

    /** aktueller Status der Session */
    private volatile int state;

    /** Zeitpunkt der letzen Anforderung in Millisekunden */
    private volatile long accession;

    /** Zeitpunkt der Erstellung in Millisekunden */
    private volatile long creation;

    /** Zeitpunkt der Verfallsdauer in Millisekunden */
    private volatile long expiration;

    /** die L&auml;nge der Session Signatur */
    private static final int SIGNATURE_SIZE = 16;

    /** Konstante f&uuml;r den Status Neu */
    private static final int STATE_NEW = 0;

    /** Konstante f&uuml;r den Status G&uuml;ltig */
    private static final int STATE_VALID = 1;

    /** Konstante f&uuml;r den Status Ung&uuml;ltig */
    private static final int STATE_INVALID = 2;

    /** Konstante f&uuml;r den Status Verfallen */
    private static final int STATE_EXPIRED = 3;

    /**
     *  Erstellt eine neue Session mit Dauer des Verfalls bei Nichtbenutzung.
     *  @param expiration Dauer in Millisekunden
     */
    public Session(long expiration) {

        this.attributes = new Hashtable();
        this.creation   = System.currentTimeMillis();
        this.expiration = expiration;
        this.accession  = this.creation;
        this.signature  = this.createSignature();

        this.state = Session.STATE_INVALID;
    }

    /**
     *  Erstellt eine auf SHA basierende Session Signatrue.
     *  @return die auf SHA basierende Session Signatrue
     */
    private String createSignature() {

        MessageDigest encoder;
        SecureRandom  random;
        String        string;
        StringBuffer  result;

        byte[]        bytes;
        char[]        digest;

        byte          high;
        byte          low;
        int           loop;
        long          seed;

        try {

            random = new SecureRandom();
            result = new StringBuffer();
            bytes  = new byte[Session.SIGNATURE_SIZE];

            seed   = System.currentTimeMillis();
            string = Long.toString(seed, 36).concat(Long.toString(Math.abs(this.hashCode()), 36));
            digest = string.toCharArray();

            for (loop = 0; loop < digest.length; loop++) seed ^= ((byte)digest[loop]) << ((loop % 8) *8);

            random.setSeed(seed);
            random.nextBytes(bytes);

            encoder = MessageDigest.getInstance("SHA");

            encoder.update(bytes);

            bytes = encoder.digest();

            for (loop = 0; loop < bytes.length; loop++) {

                high = (byte)((bytes[loop] & 0xF0) >> 4);
                low  = (byte)((bytes[loop] & 0x0F));

                if (high < 10) result.append((char)('0' +high));
                else result.append((char)('A' +(high -10)));
                if (low < 10) result.append((char)('0' +low));
                else result.append((char)('A' +(low -10)));
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {

            string = String.valueOf(exception);

            throw new RuntimeException(("Session signatrue creation failed (").concat(string).concat(")"));
        }
    }

    /**
     *  R&uuml;ckgabe der vom Session-Context f&uuml;r diese Session erstellte
     *  Signatur. Wurde die Session nicht &uuml;ber den Session-Context
     *  erstellt, wird <code>null</code> zur&uuml;ckgegeben.
     *  @return die vom Session-Context f&uuml;r diese Session erstellte
     *          Signatur sonst <code>null</code>
     */
    public String getSignature()  {

        this.accession = System.currentTimeMillis();

        return this.signature;
    }

    /**
     *  R&uuml;ckgabe aller eingetragener Attribute als Enumeration.
     *  @return alle eingetragene Attribute als Enumeration
     */
    public Enumeration getAttributes() {

        this.accession = System.currentTimeMillis();

        return this.attributes.keys();
    }

    /** Entfernt alle Attribute. */
    public void removeAllAttributes() {

        this.accession = System.currentTimeMillis();

        this.attributes.clear();
    }

    /**
     *  Entfernt das angegebene Attribut. Beim Namen der wird die Gross- und
     *  Klein- Schreibung nicht ber&uuml;cksichtigt.
     *  @param name Name des Attributs
     */
    public void removeAttribute(String name) {

        this.accession = System.currentTimeMillis();

        if (name == null || name.trim().length() == 0) return;

        this.attributes.remove(name.toLowerCase().trim());
    }

    /**
     *  Setzt das Attribut mit dem &uuml;bergebenen Inhalt, existiert dieses
     *  bereits wird das bestehende ge&auml;ndert. Beim Attribute wird die
     *  Gross- und Klein- Schreibung nicht ber&uuml;cksichtigt. Beim setzten vom
     *  Wert <code>null</code> wird das angegebene Attribute entfernt. Das
     *  Setzten von Attribute ohne oder mit leerem Namen ist nicht m&ouml;glich.
     *  @param name   Name des Attributs
     *  @param object Inhalt des Attributs
     */
    public void setAttribute(String name, Object object) {

        synchronized (this) {

            if (this.state < Session.STATE_VALID) this.state = Session.STATE_VALID;
        }

        this.accession = System.currentTimeMillis();

        if (name == null || name.trim().length() == 0) return;

        name = name.toLowerCase().trim();

        if (object == null) this.attributes.remove(name);
        else this.attributes.put(name, object);
    }

    /**
     *  R&uuml;ckgabe vom Inhalt des angegebenen Attributs. Beim Attribut wird
     *  die Gross- und Klein- Schreibung nicht ber&uuml;cksichtigt. Kann das
     *  Attribut nicht ermittelt werden, wird <code>null</code>
     *  zur&uuml;ckgegeben.
     *  @param  name Name des Attributs
     *  @return der Inhalt des angegebenen Attributs, sonst <code>null</code>
     */
    public Object getAttribute(String name) {

        this.accession = System.currentTimeMillis();

        if (name == null) return null;

        return this.attributes.get(name.toLowerCase().trim());
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn das angegebenen Attributs
     *  enthalten ist. Beim Attribut wird die Gross- und Klein- Schreibung nicht
     *  ber&uuml;cksichtigt.
     *  @param  name Name des Attributs
     *  @return <code>true</code>, wenn das angegebenen Attributs enthalten ist
     */
    public boolean containsAttribute(String name) {

        this.accession = System.currentTimeMillis();

        if (name == null) return false;

        return this.attributes.containsKey(name.toLowerCase().trim());
    }

    /**
     *  Aktiviert die Session, alternativ erfolgt das auch mit dem Setzen von
     *  Attributen. Eine Reaktivierung ist hier&uuml;ber nicht m&ouml;glich.
     */
    public void activate() {

        this.accession = System.currentTimeMillis();

        synchronized (this) {

            if (this.state < Session.STATE_VALID) this.state = Session.STATE_VALID;
        }
    }

    /** Kennzeichnet die Session als ung&uuml;ltig. */
    public void invalidate() {

        synchronized (this) {

            if (this.state < Session.STATE_INVALID) this.state = Session.STATE_INVALID;
        }
    }

    /**
     *  R&uuml;ckgabe <code>true</code> wenn die Session g&uuml;ltig ist.
     *  @return <code>true</code> wenn die Session g&uuml;ltig ist
     */
    public boolean isValid() {

        boolean expired;
        long    timing;

        synchronized (this) {

            //der Verfallszeitpunkt wird ermittelt
            timing = this.accession +this.expiration;

            //der Verfallszeitpunkt wird geprueft
            expired = (timing < System.currentTimeMillis());

            if (expired && this.state < Session.STATE_EXPIRED) this.state = Session.STATE_EXPIRED;
        }

        this.accession = System.currentTimeMillis();

        if (this.state > Session.STATE_VALID) return false;

        return true;
    }

    /**
     *  R&uuml;ckgabe <code>true</code>, wenn die Session neu eingerichtet und
     *  noch nicht verwendet wurde, sonst <code>false</code>.
     *  @return <code>true</code>, wenn die Session neu eingerichtet wurde
     */
    public boolean isNew() {

        this.accession = System.currentTimeMillis();

        return this.state == Session.STATE_NEW;
    }

    /**
     *  R&uuml;ckgabe vom Zeitpunkt der Erstellung der Session in Millisekunden.
     *  @return der Zeitpunkt von der Erstellung der Session in Millisekunden
     */
    public long getCreationTime() {

        this.accession = System.currentTimeMillis();

        return this.creation;
    }

    /**
     *  R&uuml;ckgabe vom Zeitpunkt der letzen Anforderung in Millisekunden.
     *  @return der Zeitpunkt von letzen Anforderung in Millisekunden
     */
    public long getLastAccessedTime() {

        this.accession = System.currentTimeMillis();

        return this.accession;
    }

    /**
     *  R&uuml;ckgabe der Dauer des Verfalls bei nicht Benutzung der Session.
     *  @return die Dauer des Verfalls in Millisekunden
     */
    public long getExpirationInterval() {

        this.accession = System.currentTimeMillis();

        return this.expiration;
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zur Session als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zur Session als String
     */
    public synchronized String toString() {

        String result;
        String stream;
        String string;

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        result = ("[").concat(this.getClass().getName()).concat("]").concat(string);
        result = result.concat("  signature  = ").concat(this.signature).concat(string);
        result = result.concat("  created    = ").concat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.creation))).concat(string);
        result = result.concat("  used       = ").concat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.accession))).concat(string);
        result = result.concat("  expired    = ").concat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.accession +this.expiration))).concat(string);

        if (this.state == Session.STATE_NEW) stream = "new";
        else if (this.state == Session.STATE_VALID) stream = "valid";
        else if (this.state == Session.STATE_INVALID) stream = "invalid";
        else if (this.state == Session.STATE_EXPIRED) stream = "expired";
        else stream = "unknown";

        result = result.concat("  state      = ").concat(stream).concat(string);

        stream = this.attributes.isEmpty() ? "none" : String.valueOf(this.attributes.size()).concat("x");

        result = result.concat("  attributes = ").concat(stream).concat(string);

        return result;
    }
}