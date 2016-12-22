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

import org.junit.Test;

import com.seanox.test.utils.ResourceUtils;

/**
 *  TestCases for {@link com.seanox.common.Section#toString()}.
 */
public class SectionTest_ToString {
    
    /** TestCase for key encoding. */
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
        assertEquals(section.toString(), ResourceUtils.getContextContent());
    }
    
    /** TestCase for key encoding. */
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
        assertEquals(section.toString(), ResourceUtils.getContextContent());
    }
    
    /** TestCase for key encoding. */
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
        assertEquals(section.toString(), ResourceUtils.getContextContent());
    }
    
    /** TestCase for value encoding. */
    @Test
    public void testEncodingValue_1() {
        
        Section section = new Section();
        section.set("c1", "xxxx\n");
        section.set("c2", "xxxx\txxxx");
        section.set("c3", "xxxx\0xxxx");
        section.set("c4", "xxxx;xxxx");
        section.set("c5", "xxxx;\0xxxx");
        assertEquals(section.toString(), ResourceUtils.getContextContent());
    }
    
    /** TestCase for value encoding. */
    @Test
    public void testEncodingValue_2() {
        
        Section section = new Section();
        section.set("d1", "+ xxxx");
        section.set("d2", "; xxxx");
        section.set("d3", "= xxxx");
        section.set("d4", "~ xxxx");
        section.set("d5", " 12345 ");
        assertEquals(section.toString(), ResourceUtils.getContextContent());
    }    

    /** TestCase for indenting. */
    @Test
    public void testIndenting_1() {
        
        Section section = new Section();
        section.set("x", "xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx");
        section.set("xxxx", "    xxxx xxxx    ");
        section.set("xxxx xxxx", "    xxxx xxxx xxxx    ");
        section.set("xxxx xxxx xxxx xxxx", "   xxxx xxxx   ");
        section.set("xxxx xxxx xxxx", "   xxxx;xxxx   ");
        section.set("zzzz", null);
        section.set("zzzz zzzz", "    zzzz zzzz zzzz    ");
        assertEquals(section.toString(), ResourceUtils.getContextContent());
    }
}