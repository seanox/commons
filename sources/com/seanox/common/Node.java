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
package com.seanox.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  Node, stellt eine Knotenstruktur zur Verf&uuml;gung. Der Zugriff erfolgt
 *  &uuml;ber Pfade, &auml;hnlich einem Dateisystem. Jeder Knoten verf&uuml;gt
 *  &uuml;ber Attribute, einem Inhalt ggf. weiteren Unterknoten.<br>
 *  <br>
 *  Die Schreibweise von Pfaden/Namen/Attributen kann definiert werden, der
 *  Standard, wenn nicht anders angegeben, verwendet die Kleinschreibung.
 *  <br>
 *  Node 1.2013.0421<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  Alle Rechte vorbehalten.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0707
 */
public class Node implements Cloneable, Serializable {

    /** Artibute des Knoten als Array */
    private Hashtable attributes;

    /** Unterknoten als Array */
    private List nodes;

    /** Referenz vom &uuml;bergeordneten Knoten */
    private Node parent;

    /** Inhalt des Knotens */
    private Object content;

    /** Name des Knoten */
    private String name;
    
    /** Optionen Oder verkn&uuml;pfte Bits */
    private int options;
    
    /** Option Pfad immer in Kleinschreibung */
    public static final int OPTION_PATH_LOWER_CASE = 1;
    
    /** Option Pfad immer in Grossschreibung */
    public static final int OPTION_PATH_UPPER_CASE = 2;
    
    /** Option Pfad Gross- und Kleinschreibung */
    public static final int OPTION_PATH_LOWER_UPPER_CASE = OPTION_PATH_LOWER_CASE | OPTION_PATH_UPPER_CASE;
    
    /** Option Attribute immer in Kleinschreibung */    
    public static final int OPTION_ATTRIBUTES_LOWER_CASE = 4;
    
    /** Option Attribute immer in Grossschreibung */    
    public static final int OPTION_ATTRIBUTES_UPPER_CASE = 8;
    
    /** Option Attribute Gross- und Kleinschreibung */
    public static final int OPTION_ATTRIBUTES_LOWER_UPPER_CASE = OPTION_ATTRIBUTES_LOWER_CASE | OPTION_ATTRIBUTES_UPPER_CASE;

    /** Versionskennung f&uuml;r die Serialisierung */
    private static final long serialVersionUID = -7257606983350813031L;
    
    /** Konstruktor, richtet den Knoten anonym ein. */
    public Node() {
        
        this(null, null, OPTION_PATH_LOWER_CASE | OPTION_ATTRIBUTES_LOWER_CASE);
    }

    /** 
     *  Konstruktor, richtet den Knoten anonym ein.
     *  @param options Optionen
     */
    public Node(int options) {

        this(null, null, options);
    }    

    /**
     *  Konstruktor, richtet den Knoten ein.
     *  @param name Name des Knoten
     */
    public Node(String name) {
        
        this(null, name, OPTION_PATH_LOWER_CASE | OPTION_ATTRIBUTES_LOWER_CASE);
    }
    
    /**
     *  Konstruktor, richtet den Knoten ein.
     *  @param name    Name des Knoten
     *  @param options Optionen
     */
    public Node(String name, int options) {

        this(null, name, options);
    }

    /**
     *  Konstruktor, richtet den Knoten anonym ein.
     *  @param parent &uuml;bergeordneter Konten
     */
    public Node(Node parent) {

        this(parent, null, parent.options);
    }
    
    /**
     *  Konstruktor, richtet den Knoten ein.
     *  @param parent &uuml;bergeordneter Konten
     *  @param name   Name des Knoten
     */
    public Node(Node parent, String name) {
        
        this(parent, null, OPTION_PATH_LOWER_CASE | OPTION_ATTRIBUTES_LOWER_CASE);
    }

    /**
     *  Konstruktor, richtet den Knoten ein.
     *  @param parent  &uuml;bergeordneter Konten
     *  @param name    Name des Knoten
     *  @param options Optionen
     */
    public Node(Node parent, String name, int options) {

        //der uebergeordnete Knoten und Optionen werden gesetzt
        this.parent  = parent;
        this.options = options;

        //der Knotenname wird gesetzt
        this.setName(name);
    }

    /**
     *  Entfernt alle Knoten entsprechend dem angebeben Pfad relative oder
     *  absolute. Bei der Ermittlung wird die Gross- und Kleinschreibung beim
     *  Pfad entsprechend der gesetzten Optionen ber&uuml;cksichtigt, wenn nicht
     *  gesetzt, wird die Schreibweise ignoriert, mehrfache Slashs werden
     *  ignoriert.
     *  @param path der Pfad der zu entfernde Knoten relative oder absolute
     */
    public synchronized void removeNodes(String path) {

        Node   parent;

        Node[] nodes;

        int    loop;

        //der Path wird optimiert
        path = Codec.decode(path, Codec.DOT);
        
        switch (this.options & OPTION_PATH_LOWER_UPPER_CASE) {
            case OPTION_PATH_LOWER_CASE:
                
                path = path.toLowerCase();
                
                break;
                
            case OPTION_PATH_UPPER_CASE:
                
                path = path.toUpperCase();
                
                break;
        }

        //bei leeren Pfad wird null zurueckgegeben
        if (path.length() == 0) return;

        //der Path wird absolute oder relative komplementiert
        if (!path.startsWith("/")) path = this.getPath().concat("/").concat(path);

        //alle Knoten mit dem gesuchten Pfad werden ermittelt
        while ((nodes = this.getNodes(path)).length > 0) {

            parent = nodes[0].getParentNode();

            for (loop = 0; parent.nodes != null && loop < parent.nodes.size(); loop--) {

                if (((Node)parent.nodes.get(loop)).getPath().equals(path)) {

                    parent.nodes.remove(loop);

                    break;
                }
            }
        }
    }

    /**
     *  Ermittelt einen bzw. den ersten Knoten entsprechend dem angebeben Pfad
     *  relative oder absolute. Bei der Ermittlung wird im Pfad die Gross- und
     *  Kleinschreibung entsprechend der gesetzten Optionen ber&uuml;cksichtigt,
     *  wenn nicht gesetzt, wird die Schreibweise ignoriert. R&uuml;ckgabe der 
     *  ermittelte Knoten oder <code>null</code>, wenn keiner gefunden wurde.
     *  @param  path der Pfad des gesuchten Knoten relative oder absolute
     *  @return der gefundene Knoten, sonst <code>null</code>
     */
    public synchronized Node getNode(String path) {

        Node[] nodes;

        nodes = this.getNodes(path);

        return (nodes.length) > 0 ? nodes[0] : null;
    }

    /**
     *  Ermittelt alle Knoten entsprechend dem angebeben Pfad relative oder
     *  absolute. Bei der Ermittlung werden im Pfad mehrfache Slashes ignoriert,
     *  die Gross- und Kleinschreibung entsprechend der gesetzten Optionen
     *  ber&uuml;cksichtigt, wenn nicht gesetzt, wird die Schreibweise
     *  ignoriert. R&uuml;ckgabe ein Array mit den gefundenen Knoten, sonst ein
     *  leeres Array wenn keine Knoten gefunden wurden.
     *  @param  path der Pfad der gesuchten Knoten relative oder absolute
     *  @return die gefundenen Knoten als Array
     */
    public synchronized Node[] getNodes(String path) {

        List   result;

        Node[] nodes;

        int    loop;

        //die Liste wird eingerichtet
        result = new ArrayList();

        if (path == null) return (Node[])result.toArray(new Node[0]);

        //der Path wird optimiert
        path = Codec.decode(path, Codec.DOT);
        
        switch (this.options & OPTION_PATH_LOWER_UPPER_CASE) {
            case OPTION_PATH_LOWER_CASE:
                
                path = path.toLowerCase();
                
                break;
                
            case OPTION_PATH_UPPER_CASE:
                
                path = path.toUpperCase();
                
                break;
        }

        //bei leeren Pfad wird ein leeres Array zurueckgegeben
        if (path.length() == 0) return (Node[])result.toArray(new Node[0]);

        //die Unterknoten werden absolute oder relative in Abhaengigkeit vom Path ermittelt
        nodes = path.startsWith("/") ? this.getRootNode().getAllNodes() : this.getAllNodes();

        //der Path wird absolute oder relative komplementiert
        if (!path.startsWith("/")) path = this.getPath().concat(this.getPath().equals("/") ? "" : "/").concat(path);

        //die Knoten werden zum entsprechenden Pfad ermittelt
        for (loop = 0; loop < nodes.length; loop++) {

            //die Liste der Knoten wird erweitert
            if (path.equals(nodes[loop].getPath())) result.add(nodes[loop]);
        }

        return (Node[])result.toArray(new Node[0]);
    }

    /**
     *  R&uuml;ckgabe der Pfade aller Unterknoten vom Knoten als Array.
     *  @return die Pfade aller Unterknoten vom Knoten als Array
     */
    public synchronized String[] getAllNodesPaths() {

        List   paths;

        Node[] nodes;

        int    loop;

        //die Liste wird eingerichtet
        paths = new ArrayList();

        //alle Unterknoten werden ermittelt
        nodes = this.getAllNodes();

        for (loop = 0; loop < nodes.length; loop++) paths.add(nodes[loop].getPath());

        return (String[])paths.toArray(new String[0]);
    }

    /**
     *  R&uuml;ckgabe der direkten Unterknoten vom Knoten als Array.
     *  @return die direkten Unterknoten vom Knoten als Array
     */
    public synchronized Node[] getNodes() {

        if (this.nodes == null) this.nodes = new ArrayList();

        return (Node[])this.nodes.toArray(new Node[0]);
    }

    /**
     *  R&uuml;ckgabe alle Unterknoten vom Knoten als Array.
     *  @return alle Unterknoten vom Knoten als Array
     */
    public synchronized Node[] getAllNodes() {

        List nodes;
        Node node;

        int  loop;

        //das Array wird eingerichtet
        nodes = new ArrayList();

        //das Array wird mit allen ermittelten Unterknoten erweitert
        for (loop = 0; this.nodes != null && loop < this.nodes.size(); loop++) {

            node = (Node)this.nodes.get(loop);

            nodes.add(node);

            nodes.addAll(Arrays.asList(node.getAllNodes()));
        }

        return (Node[])nodes.toArray(new Node[0]);
    }

    /**
     *  Erweitert die Unterknoten um einen weiteren neuen und anonymen Knoten.
     *  R&uuml;ckgabe, die Referenz auf den neu erstellten Unterknoten.
     *  @return die Referenz auf den neu erstellten Unterknoten
     */
    public synchronized Node addNode() {

        Node node;
        
        node = new Node(this, null);

        if (this.nodes == null) this.nodes = new Vector();

        this.nodes.add(node);

        return node;
    }

    /**
     *  Erweitert die Unterknoten um einen weiteren neuen Knoten. Stammt der
     *  Knoten aus der aktuellen Gesamtstruktur, dann wird dieser als Kopie
     *  eingef&uuml;gt um ungewollte Rekursionen zu vermeiden.
     *  @param  node einzuf&uuml;gender Knoten
     *  @return die Referenz vom tats&auml;chlich eingef&uuml;gten Knoten
     */
    public synchronized Node addNode(Node node) {

        if (node == null) return null;
        
        if (this.getRootNode().equals(node.getRootNode())) {

            //die Kopie des Knotens wird ermittelt
            node = (Node)node.clone();

            //der Parent wird gesetzt
            node.parent = this;

            if (this.nodes == null) this.nodes = new Vector();
        }
        
        this.nodes.add(node);
        
        return node;
    }

    /**
     *  R&uuml;chgabe der aktuellen Tiefe des Knotens in der Gesamtstruktur.
     *  @return die Tiefe des Knotens in der Gesamtstruktur
     */
    public synchronized int getDeep() {

        Node node;

        int  count;

        for (count = 0, node = this; (node = node.parent) != null;) count++;

        return count;
    }

    /**
     *  R&uuml;ckgabe der Wurzel in der Gesamtstruktur.
     *  @return die Wurzel in der Gesamtstruktur
     */
    public synchronized Node getRootNode() {

        Node node;

        for (node = this; node.getParentNode() != null; node = node.getParentNode()) continue;

        return node;
    }

    /**
     *  R&uuml;ckgabe des absoluten Pfad des Knoten.
     *  @return der absolute Pfad vom Knoten
     */
    public synchronized String getPath() {

        Node         node;
        StringBuffer buffer;

        //der Ausgangsknoten wird ermittelt
        node = this;

        //der Ausgangsname wird ermittelt
        buffer = new StringBuffer(node.getName());

        while ((node = node.getParentNode()) != null
                && !node.equals(node.getRootNode())) {
            
            buffer.insert(0, node.getName().concat("/"));
        }

        if (!buffer.toString().startsWith("/")) buffer.insert(0, "/");

        return buffer.toString();
    }

    /**
     *  R&uuml;ckgabe von Namen des Knotens.
     *  @return der Name vom Knoten
     */
    public synchronized String getName() {

        return this.name;
    }

    /**
     *  Setzt den Namen des Knoten. Beim Namen wird die Gross- Kleinschreibung
     *  entsprechend der gesetzten Optionen ber&uuml;cksichtigt, wenn nicht
     *  gesetzt, wird die Schreibweise ignoriert. Leerstrings als Name und
     *  mehrfache Namen auf einer Knotenebene sind zul&auml;ssig. Slash und
     *  Backslash f&uuml;hren zur <code>IllegalArgumentException</code>.
     *  @param  name Names des Knotens
     *  @throws IllegalArgumentException wenn der Name die nicht zul&auml;ssigen
     *          Zeichen Slash oder Backslash enth&auml;lt
     */
    public synchronized void setName(String name) {

        if (name == null) name = "";

        if (name.indexOf("\\") >= 0 || name.indexOf("/") >= 0) throw new IllegalArgumentException(("Invalid character in ").concat(name));

        this.name = name;
    }

    /**
     *  R&uuml;ckgabe vom &uuml;bergeordneten Knoten.
     *  @return der &uuml;bergeordneten Knoten
     */
    public synchronized Node getParentNode() {

        return this.parent;
    }

    /**
     *  Setzt den Inhalt vom Knoten als Objekt.
     *  @param object Inhalt des Knoten als Objekt
     */
    public synchronized void setContent(Object object) {

        this.content = object;
    }

    /**
     *  R&uuml;ckgabe vom Inhalt des Knotens als Objekt.
     *  @return der Inhalt vom Knoten als Objekt
     */
    public synchronized Object getContent() {

        return this.content;
    }

    /**
     *  R&uuml;ckgabe aller eingetragener Attribute als Enumeration.
     *  @return alle eingetragene Attribute als Enumeration
     */
    public synchronized Enumeration getAttributes() {

        if (this.attributes == null) this.attributes = new Hashtable();

        return this.attributes.keys();
    }

    /** Entfernt alle Attribute. */
    public synchronized void removeAllAttributes() {

        if (this.attributes != null) this.attributes.clear();
    }

    /**
     *  Entfernt das angegebene Attribut. Beim Namen der wird die Gross- und
     *  Kleinschreibung entsprechend der gesetzten Optionen ber&uuml;cksichtigt,
     *  wenn nicht gesetzt, wird die Schreibweise ignoriert.
     *  @param name Name des Attributs
     */
    public synchronized void removeAttribute(String name) {

        if (name == null || name.trim().length() == 0) return;

        if (this.attributes == null) return;
        
        switch (this.options & OPTION_ATTRIBUTES_LOWER_UPPER_CASE) {
            case OPTION_ATTRIBUTES_LOWER_CASE:
                
                name = name.toLowerCase();
                
                break;
                
            case OPTION_ATTRIBUTES_UPPER_CASE:
                
                name = name.toUpperCase();
                
                break;
        }

        this.attributes.remove(name.trim());
    }

    /**
     *  Setzt das Attribut mit dem &uuml;bergebenen Inhalt, existiert dieses
     *  bereits wird das bestehende ge&auml;ndert. Beim setzten vom Wert
     *  <code>null</code> wird das angegebene Attribute entfernt. Das Setzten
     *  von Attribute ohne oder mit leerem Namen ist nicht m&ouml;glich. Beim
     *  Attribute wird die Gross- und Kleinschreibung entsprechend der gesetzten
     *  Optionen ber&uuml;cksichtigt, wenn nicht gesetzt, wird die Schreibweise
     *  ignoriert.
     *  @param name   Name des Attributs
     *  @param object Inhalt des Attributs
     */
    public synchronized void setAttribute(String name, Object object) {

        if (name == null || name.trim().length() == 0) return;

        name = name.trim();
        
        switch (this.options & OPTION_ATTRIBUTES_LOWER_UPPER_CASE) {
            case OPTION_ATTRIBUTES_LOWER_CASE:
                
                name = name.toLowerCase();
                
                break;
                
            case OPTION_ATTRIBUTES_UPPER_CASE:
                
                name = name.toUpperCase();
                
                break;
        }

        if (this.attributes == null) this.attributes = new Hashtable();

        if (object == null) this.attributes.remove(name);
        else this.attributes.put(name, object);
    }

    /**
     *  R&uuml;ckgabe vom Inhalt des angegebenen Attributs. Kann das Attribute
     *  nicht ermittelt werden, wird <code>null</code> zur&uuml;ckgegeben. Beim
     *  Attribut wird die Gross- und Kleinschreibung entsprechend der gesetzten
     *  Optionen ber&uuml;cksichtigt, wenn nicht gesetzt, wird die Schreibweise
     *  ignoriert.
     *  @param  name Name des Attributs
     *  @return der Inhalt vom angegebenen Attribut, sonst <code>null</code>
     */
    public synchronized Object getAttribute(String name) {

        if (name == null) return null;

        if (this.attributes == null) return null;
        
        switch (this.options & OPTION_ATTRIBUTES_LOWER_UPPER_CASE) {
            case OPTION_ATTRIBUTES_LOWER_CASE:
                
                name = name.toLowerCase();
                
                break;
                
            case OPTION_ATTRIBUTES_UPPER_CASE:
                
                name = name.toUpperCase();
                
                break;
        }

        return this.attributes.get(name.trim());
    }

    /** L&ouml;scht alle Attribute und Unterknoten. */
    public synchronized void clear() {

        if (this.attributes != null) this.attributes.clear();

        if (this.nodes != null) this.nodes.clear();

        this.content = null;
        this.name    = "";
    }

    /**
     *  Erstellt eine Kopie des Knotens.
     *  @return die Kopie des Knotens
     */
    public synchronized Object clone() {

        Node node;

        int  loop;

        //der Knoten wird eingerichtet
        node = new Node();

        //die Optionen werden kopiert
        if (this.attributes != null) node.attributes = (Hashtable)this.attributes.clone();

        //die Felder des Knotens werden eingerichtet
        node.content = this.content;
        node.name    = this.name;
        node.parent  = this.parent;

        //die Unterknoten werden kopiert
        for (loop = 0; this.nodes != null && loop < this.nodes.size(); loop++) {

            node.addNode((Node)this.nodes.get(loop));
        }

        return node;
    }

    /**
     *  R&uuml;ckgabe der Knotenstruktur als String.
     *  @return Knotenstruktur als String
     */
    public synchronized String toString() {

        String          buffer;
        String          string;
        StringBuffer    result;
        StringTokenizer tokenizer;

        int             loop;

        result = new StringBuffer();

        //der Zeilenumbruch wird entsprechend dem System ermittelt
        string = System.getProperty("line.separator", "\r\n");

        //die Strukturen und Unterstrukturen werden rekursive
        //durchlaufen und entsprechend als String aufgebaut
        for (loop = 0; this.nodes != null && loop < this.nodes.size(); loop++) {

            result.append("+-").append(((Node)this.nodes.get(loop)).getName()).append(string);

            buffer = ((Node)this.nodes.get(loop)).toString();

            tokenizer = new StringTokenizer(buffer, string);

            while (tokenizer.hasMoreTokens()) {

                result.append((loop < this.nodes.size() -1 ? "|" : " ")).append(" ").append(tokenizer.nextToken().concat(string));
            }
        }

        return result.toString();
    }
}