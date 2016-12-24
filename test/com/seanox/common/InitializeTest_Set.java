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
 *  TestCases for {@link com.seanox.common.Initialize#set(String, Section)}.
 */
public class InitializeTest_Set {
    
    /** TestCase for a invalid key. */
    @Test(expected=Exception.class)
    public void testKeyInvalid_1() {
        
        Initialize initialize = new Initialize();
        initialize.set("", null);
    }
    
    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_2() {
        
        Initialize initialize = new Initialize();
        initialize.set(" ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_3() {
        
        Initialize initialize = new Initialize();
        initialize.set("   ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_5() {
        
        Initialize initialize = new Initialize();
        initialize.set(null, null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_6() {

        Initialize initialize = new Initialize();
        initialize.set(" \0\0 ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_7() {
        
        Initialize initialize = new Initialize();
        initialize.set(" \r\n ", null);
    }
    
    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_8() {
        
        Initialize initialize = new Initialize();
        initialize.set(" \07\07 ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_9() {
        
        Initialize initialize = new Initialize();
        initialize.set(" \20\20 ", null);
    }

    /** TestCase for a invalid key. */
    @Test(expected=IllegalArgumentException.class)
    public void testKeyInvalid_A() {
        
        Initialize initialize = new Initialize();
        initialize.set(" \17\17 ", null);
    }
    
    /** TestCase for key tolerance. */
    @Test
    public void testKeyTolerance_1() {
        
        Initialize initialize = new Initialize();

        Section section1 = new Section();
        initialize.set("A", section1);
        assertEquals(initialize.get("A"), section1);
        assertEquals(initialize.get("a"), section1);
        
        Section section2 = new Section();
        initialize.set("a", section2);
        assertEquals(initialize.get("A"), section2);
        assertEquals(initialize.get("a"), section2);
        
        Section section3 = new Section();
        initialize.set(" a", section3);
        assertEquals(initialize.get("A"), section3);
        assertEquals(initialize.get("a"), section3);
        
        Section section4 = new Section();
        initialize.set(" a ", section4);
        assertEquals(initialize.get("A"), section4);
        assertEquals(initialize.get("a"), section4);

        Section section5 = new Section();
        initialize.set("a ", section5);
        assertEquals(initialize.get("A"), section5);
        assertEquals(initialize.get("a"), section5);
    }
    
    /** TestCase for overwrite a key. */
    @Test
    public void testKeyOverwrite_1() {
        
        Initialize initialize = new Initialize();
        Section section1 = new Section();
        initialize.set("A", section1);
        Section section2 = new Section();
        initialize.set("a", section2);
        Section section3 = new Section();
        initialize.set(" A", section3);
        Section section4 = new Section();
        initialize.set(" a   ", section4);
        assertEquals(initialize.get("A"), section4);
        assertEquals(initialize.get("a"), section4);
    }
}