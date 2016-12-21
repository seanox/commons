/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt. Diese
 *  Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2016 Seanox Software Solutions
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

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class SectionTest_ToString {
    
    @Test
    public void testEncodingKey_1() {
        
        Section section = new Section();
        section.set(" 1\0\0 ", "xxx");
        section.set(" 2\r\n ", "xxx");        
        section.set(" 1\0\0a ", "xxx");
        section.set(" 2\r\nb ", "xxx");        
        section.set(" \0\0a ", "xxx");
        section.set(" \r\nb ", "xxx");    
        section.set(" a1\7\7b2 ", "xxx");
        section.set(" a1\7\7 ", "xxx");
        section.set(" \7\7b2 ", "xxx");
        section.set(" \00A7\00A7 ", "xxx");
        section.set(" a1\7\7b2 ", "xxx");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        printWriter.println("1              = xxx");
        printWriter.println("2              = xxx");
        printWriter.println("0x31000041     = xxx");
        printWriter.println("0x320D0A42     = xxx");
        printWriter.println("A              = xxx");
        printWriter.println("B              = xxx");
        printWriter.println("0x413107074232 = xxx");
        printWriter.println("A1             = xxx");
        printWriter.println("B2             = xxx");
        printWriter.println("0x4137004137   = xxx");
        
        assertEquals(section.toString(), stringWriter.toString());
    }
    
    @Test
    public void testEncodingKey_2() {
        
        Section section = new Section();
        section.set(" 12345 ", "xxx");
        section.set(" 12[5] ", "xxx");
        section.set(" 1 [5] ", "xxx");
        section.set(" 12[34 ", "xxx");
        section.set(" 1234] ", "xxx");
        section.set(" 12=34 ", "xxx");
        section.set(" 12;34 ", "xxx");
        section.set(" 1 = 2 ", "xxx");
        section.set(" 1 + 2 ", "xxx"); 
        section.set(" = 2a ", "xxx");
        section.set(" + 2b ", "xxx"); 
        section.set(" 2c = ", "xxx");
        section.set(" 2d + ", "xxx");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        printWriter.println("12345        = xxx");
        printWriter.println("0x31325B355D = xxx");
        printWriter.println("0x31205B355D = xxx");
        printWriter.println("0x31325B3334 = xxx");
        printWriter.println("0x313233345D = xxx");
        printWriter.println("0x31323D3334 = xxx");
        printWriter.println("0x31323B3334 = xxx");
        printWriter.println("0x31203D2032 = xxx");
        printWriter.println("1 + 2        = xxx");
        printWriter.println("0x3D203241   = xxx");
        printWriter.println("0x2B203242   = xxx");
        printWriter.println("0x3243203D   = xxx");
        printWriter.println("2D +         = xxx");      
        
        assertEquals(section.toString(), stringWriter.toString());
    }
    
    @Test
    public void testEncodingKey_3() {
        
        Section section = new Section();
        section.set("a", "xx1");
        section.set(" b ", "xx2");        
        section.set("  c  ", "xx3");
        section.set("    a   ", "xx4");        
        section.set(" \0\0a ", "xx5");
        section.set(" \r\nb ", "xx6");    
        section.set(" a1\7\7b2 ", "xx7");
        section.set(" a1\7\7 ", "xx8");
        section.set(" \7\7b2 ", "xx9");
        section.set(" \00A7\00A7 ", "xxA");
        section.set(" a1\7\7b2 ", "xxB");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);        
        
        printWriter.println("A              = xx5");
        printWriter.println("B              = xx6");
        printWriter.println("C              = xx3");
        printWriter.println("0x413107074232 = xxB");
        printWriter.println("A1             = xx8");
        printWriter.println("B2             = xx9");
        printWriter.println("0x4137004137   = xxA");     
        
        assertEquals(section.toString(), stringWriter.toString());
    }
    
    @Test
    public void testEncodingValue_1() {
        
        Section section = new Section();
        section.set("c1", "xxxx\n");
        section.set("c2", "xxxx\txxxx");
        section.set("c3", "xxxx\0xxxx");
        section.set("c4", "xxxx;xxxx");
        section.set("c5", "xxxx;\0xxxx");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        printWriter.println("C1     = xxxx");
        printWriter.println("C2     = 0x787878780978787878");
        printWriter.println("C3     = 0x787878780078787878");
        printWriter.println("C4 [+] = xxxx;xxxx");
        printWriter.println("C5     = 0x787878783B0078787878");
        
        assertEquals(section.toString(), stringWriter.toString());   
        
        section.set("d1", "+ xxxx");
        section.set("d2", "; xxxx");
        section.set("d3", "= xxxx");
        section.set("d4", "~ xxxx");
        section.set("d5", " 12345 ");
    }
    
    @Test
    public void testEncodingValue_2() {
        
        Section section = new Section();
        section.set("d1", "+ xxxx");
        section.set("d2", "; xxxx");
        section.set("d3", "= xxxx");
        section.set("d4", "~ xxxx");
        section.set("d5", " 12345 ");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        
        printWriter.println("D1     = 0x2B2078787878");
        printWriter.println("D2 [+] = ; xxxx");
        printWriter.println("D3     = 0x3D2078787878");
        printWriter.println("D4     = ~ xxxx");
        printWriter.println("D5     = 12345");
        
        assertEquals(section.toString(), stringWriter.toString());   
    }    

    @Test
    public void testIndenting_1() {
        
        Section section = new Section();
        section.set("x", "xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx");
        section.set("xxxx", "    xxxx xxxx    ");
        section.set("xxxx xxxx", "    xxxx xxxx xxxx    ");
        section.set("xxxx xxxx xxxx xxxx", "   xxxx xxxx   ");
        section.set("xxxx xxxx xxxx", "   xxxx;xxxx   ");
        
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);        

        printWriter.println("X                   = xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx");
        printWriter.println("XXXX                = xxxx xxxx");
        printWriter.println("XXXX XXXX           = xxxx xxxx xxxx");
        printWriter.println("XXXX XXXX XXXX XXXX = xxxx xxxx");
        printWriter.println("XXXX XXXX XXXX  [+] = xxxx;xxxx");
        
        assertEquals(section.toString(), stringWriter.toString()); 
    }
}