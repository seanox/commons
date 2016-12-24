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
 *  TestCases for {@link com.seanox.common.Initialize#parse(String)}.
 */
public class InitializeTest_Parse {
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_1() {
        
        Initialize initialize = Initialize.parse(ResourceUtils.getContextContent());
        assertEquals(initialize.toString(), ResourceUtils.getContextContent("testAceptance_1_1"));
    }
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_2() {

        Initialize initialize = Initialize.parse(ResourceUtils.getContextContent());
        assertEquals(initialize.toString(), ResourceUtils.getContextContent("testAceptance_2_1"));
    }
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_3() {

        Initialize initialize = Initialize.parse(ResourceUtils.getContextContent());
        assertEquals(initialize.toString(), ResourceUtils.getContextContent("testAceptance_3_1"));
    }
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_4() {

        Initialize initialize = Initialize.parse(ResourceUtils.getContextContent());
        assertEquals(initialize.toString(), ResourceUtils.getContextContent("testAceptance_4_1"));
    }
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_5() {

        Initialize initialize = Initialize.parse(ResourceUtils.getContextContent());
        assertEquals(initialize.toString(), ResourceUtils.getContextContent("testAceptance_5_1"));
    }
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_6() {

        Initialize initialize = Initialize.parse(ResourceUtils.getContextContent());
        assertEquals(initialize.toString(), ResourceUtils.getContextContent("testAceptance_6_1"));
    }  
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_7() {

        Initialize initialize = Initialize.parse(ResourceUtils.getContextContent());
        assertEquals(initialize.toString(), ResourceUtils.getContextContent("testAceptance_7_1"));
    } 
}