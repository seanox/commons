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

/**
 *  TestCases for {@link com.seanox.common.Section#set(String, String)}.
 */
public class SectionTest_Set {
    
    /** TestCase for a invalid key. */
    @Test(expected=Exception.class)
    public void testKeyInvalid_1() {
        
        Section section = new Section();
        section.set("", null);
    }
    
    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_2() {
        
        Section section = new Section();
        section.set(" ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_3() {
        
        Section section = new Section();
        section.set("   ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_5() {
        
        Section section = new Section();
        section.set(null, null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_6() {
        
        Section section = new Section();
        section.set(" \0\0 ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_7() {
        
        Section section = new Section();
        section.set(" \r\n ", null);
    }
    
    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_8() {
        
        Section section = new Section();
        section.set(" \07\07 ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_9() {
        
        Section section = new Section();
        section.set(" \20\20 ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_A() {
        
        Section section = new Section();
        section.set(" \17\17 ", null);
    }
    
    /** TestCase for key tolerance. */
    @Test
    public void testKeyTolerance_1() {
        
        Section section = new Section();
        section.set("A", "a1");
        assertEquals("a1", section.get("A"));
        assertEquals("a1", section.get("a"));
        
        section.set("a", "a2");
        assertEquals("a2", section.get("A"));
        assertEquals("a2", section.get("a"));
        
        section.set(" a", "a3");
        assertEquals("a3", section.get("A"));
        assertEquals("a3", section.get("a"));
        
        section.set(" a ", "a4");
        assertEquals("a4", section.get("A"));
        assertEquals("a4", section.get("a"));

        section.set("a ", "a5");
        assertEquals("a5", section.get("A"));
        assertEquals("a5", section.get("a"));
    }
    
    /** TestCase for overwrite a key. */
    @Test
    public void testKeyOverwrite_1() {
        
        Section section = new Section();
        section.set("A", "a1");
        section.set("a", "a2");
        section.set(" A", "a3");
        section.set(" a   ", "a4");
        assertEquals("a4", section.get("A"));
        assertEquals("a4", section.get("a"));
    }
}