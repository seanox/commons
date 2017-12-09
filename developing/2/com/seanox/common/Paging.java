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
package com.seanox.common;

/**
 *  Paging stellt die Berechnung von seitenweisen Wertebereichen bereit. Auf
 *  Basis von aktueller Seite, Gesamtanzahl aller Seiten, Einheiten je Seite und
 *  L&auml;nge des Bereichsausschnitt, werden u.a. die Start-, und Folgeseite
 *  sowie die Positionen vom Bereichsausschnitt berechnet.<br>
 *  <br>
 *  Paging 1.2013.0928<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0928
 */
public class Paging {

    /** Meta-Infos zur Seite */
    private Meta page;

    /** Meta-Infos zum Bereich */
    private Meta range;

    /** Meta-Infos zum Block */
    private Meta view;

    /**
     *  Konstruktor, richtet das Paging Objekt ein und berechnet die
     *  Positionen und Wertebereiche.
     *  @param  count Anzahl der Werte gesamt
     *  @param  page  aktuelle Seite (Position im Wertebereich)
     *  @param  size  Anzahl der Werte je Seite
     *  @param  range L&auml;nge des Ausschnitts vom Wertebereich
     */
    public Paging(int count, int page, int size, int range) {

        this(count, page, size, range, -1);
    }

    /**
     *  Konstruktor, richtet das Paging Objekt ein und berechnet die
     *  Positionen und Wertebereiche.
     *  @param  count Anzahl der Werte gesamt
     *  @param  page  aktuelle Seite (Position im Wertebereich)
     *  @param  size  Anzahl der Werte je Seite
     *  @param  range L&auml;nge des Ausschnitts vom Wertebereich
     *  @param  limit optional maximale Anzahl von Seiten
     */
    public Paging(int count, int page, int size, int range, int limit) {
        
        this.page  = new Meta();
        this.range = new Meta();
        this.view  = new Meta();

        if (count < 0) count = 0;
        if (page  < 1) page  = 1;
        if (size  < 1) size  = 1;
        if (range < 1) range = 1;
        if (limit < 1) limit = 0;

        if (limit > 0 && count > limit *size) count = limit *size;

        this.page.start = 1;

        this.page.end = (int)Math.ceil(count /size);
        this.page.end = this.page.end <= 0 ? 1 : this.page.end;

        this.page.current  = page > this.page.end ? this.page.end : page;
        this.page.previous = this.page.current > 1 ? this.page.current -1 : 0;
        this.page.follow   = this.page.current < this.page.end ? this.page.current +1 : 0;

        this.view.count = this.page.current *size > count ? size +count -(this.page.current *size) : size;
        this.view.start = ((this.page.current -1) *size) +1;
        this.view.end   = this.view.start -1 +this.view.count;

        this.range.left  = (int)Math.floor(range /2);
        this.range.right = this.range.left +(this.range.left % 2 == 0 ? 0 : 1);

        this.range.start = this.page.current -this.range.left;
        this.range.start = this.range.start +(range -1) > this.page.end ? this.page.end -(range -1) : this.range.start;
        this.range.start = this.range.start <= 0 ? 1 : this.range.start;

        this.range.end = this.range.start +(range -1);
        this.range.end = this.range.end < range && this.page.end >= range ? range : this.range.end;
        this.range.end = this.range.end > this.page.end ? this.page.end : this.range.end;

        this.range.previous = this.range.start -(this.range.left +(this.range.left % 2 == 0 ? 0 : 1));
        this.range.previous = this.range.previous <= 0 && this.range.start > this.page.start ? this.page.start : this.range.previous;
        this.range.previous = this.range.previous <= 0 ? 0 : this.range.previous;

        this.range.follow = this.range.end +this.range.right +(this.range.left % 2 == 0 ? 1 : 0);
        this.range.follow = this.range.follow > this.page.end && this.range.end < this.page.end ? this.page.end : this.range.follow;
        this.range.follow = this.range.end >= this.page.end ? 0 : this.range.follow;
    }

    /**
     *  R&uuml;ckgabe der aktuellen Seite.
     *  @return die aktuelle Seite
     */
    public int getPageCurrent() {

        return this.page.current;
    }

    /**
     *  R&uuml;ckgabe der letzten Seite.
     *  @return die letzte Seite
     */
    public int getPageEnd() {

        return this.page.end;
    }

    /**
     *  R&uuml;ckgabe der aktuell folgenden Seite.
     *  Gibt es keine, wird <code>0</code> zur&uuml;ckgegeben.
     *  @return die aktuell folgende Seite
     */
    public int getPageFollow() {

        return this.page.follow;
    }

    /**
     *  R&uuml;ckgabe der aktuell vorangehenden Seite.
     *  Gibt es keine, wird <code>0</code> zur&uuml;ckgegeben.
     *  @return die aktuell vorangehenden Seite
     */
    public int getPagePrevious() {

        return this.page.previous;
    }

    /**
     *  R&uuml;ckgabe der ersten Seite.
     *  @return die erste Seite
     */
    public int getPageStart() {

        return this.page.start;
    }

    /**
     *  R&uuml;ckgabe vom Ende des Wertebereichs.
     *  @return das Ende des Wertebereichs
     */
    public int getRangeEnd() {

        return this.range.end;
    }

    /**
     *  R&uuml;ckgabe vom Start des Wertebereichs.
     *  @return der Start des Wertebereichs
     */
    public int getRangeStart() {

        return this.range.start;
    }

    /**
     *  R&uuml;ckgabe des aktuell folgenden Wertebereichs.
     *  Gibt es keinen, wird <code>0</code> zur&uuml;ckgegeben.
     *  @return der aktuell folgende Wertebereich
     */
    public int getRangeFollow() {

        return this.range.follow;
    }

    /**
     *  R&uuml;ckgabe des aktuell vorangehenden Wertebereichs.
     *  Gibt es keinen, wird <code>0</code> zur&uuml;ckgegeben.
     *  @return die aktuell vorangehende Wertebereich
     */
    public int getRangePrevious() {

        return this.range.previous;
    }

    /**
     *  R&uuml;ckgabe vom vorangehenden Block Wertebereich.
     *  @return der vorangehende Block Wertebereich
     */
    int getRangeLeft() {

        return this.range.left;
    }

    /**
     *  R&uuml;ckgabe vom nachfolgenden Block Wertebereich.
     *  @return der nachfolgende Block Wertebereich
     */
    int getRangeRight() {

        return this.range.right;
    }

    /**
     *  R&uuml;ckgabe vom Start des aktuellen Blocks.
     *  @return der Start des aktuellen Blocks
     */
    int getViewStart() {

        return this.view.start;
    }

    /**
     *  R&uuml;ckgabe vom Ende des aktuellen Blocks.
     *  @return der Ende des aktuellen Blocks
     */
    int getViewEnd() {

        return this.view.end;
    }

    /**
     *  R&uuml;ckgabe der Anzahl des aktuellen Blocks.
     *  @return die Anzahl des aktuellen Blocks
     */
    int getViewCount() {

        return this.view.count;
    }

    /**
     *  R&uuml;ckgabe der formatierten Information zum Paging als String.
     *  Der Zeilenumbruch erfolgt abh&auml;ngig vom aktuellen Betriebssystem.
     *  @return die formatierte Information zum Paging als String
     */
    public String toString() {

        String stream;
        String string;

        //der Zeilenumbruch wird ermittelt
        stream = System.getProperty("line.separator", "\r\n");

        //das Paket der Klasse wird ermittelt
        string = ("[").concat(this.getClass().getName()).concat("]").concat(stream);

        string = string.concat("  pageStart     = ").concat(String.valueOf(this.page.start)).concat(stream);
        string = string.concat("  pagePrevious  = ").concat(String.valueOf(this.page.previous)).concat(stream);
        string = string.concat("  pageCurrent   = ").concat(String.valueOf(this.page.current)).concat(stream);
        string = string.concat("  pageFollow    = ").concat(String.valueOf(this.page.follow)).concat(stream);
        string = string.concat("  pageEnd       = ").concat(String.valueOf(this.page.end)).concat(stream);
        string = string.concat("  rangeStart    = ").concat(String.valueOf(this.range.start)).concat(stream);
        string = string.concat("  rangePrevious = ").concat(String.valueOf(this.range.previous)).concat(stream);
        string = string.concat("  rangeLeft     = ").concat(String.valueOf(this.range.left)).concat(stream);
        string = string.concat("  rangeFollow   = ").concat(String.valueOf(this.range.follow)).concat(stream);
        string = string.concat("  rangeRight    = ").concat(String.valueOf(this.range.right)).concat(stream);
        string = string.concat("  rangeEnd      = ").concat(String.valueOf(this.range.end)).concat(stream);
        string = string.concat("  viewStart     = ").concat(String.valueOf(this.view.start)).concat(stream);
        string = string.concat("  viewEnd       = ").concat(String.valueOf(this.view.end)).concat(stream);
        string = string.concat("  viewCount     = ").concat(String.valueOf(this.view.count)).concat(stream);

        return string;
    }

    /** Innere Klasse mit Meta-Informationen. */
    private class Meta {

        /** 
         *  erste Seite
         *  oder Start des Wertebereichs
         *  oder Start des aktuellen Blocks
         */
        private int start;

        /** 
         *  letzte Seite
         *  oder Ende des aktuellen Blocks
         *  oder Ende des Wertebereichs
         */
        private int end;

        /** aktuelle Seite */
        private int current;

        /** 
         *  vorangehende Seite
         *  oder vorangehender Wertebereich
         */
        private int previous;

        /** 
         *  nachfolgende Seite
         *  oder nachfolgender Wertebereich
         */
        private int follow;

        /** vorangehender Block Wertebereich */
        private int left;

        /** nachfolgender Block Wertebereich */
        private int right;

        /** Anzahl von Seiten im aktuellen Block */
        private int count;
    }
}