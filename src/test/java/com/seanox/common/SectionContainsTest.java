/**
 * LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 * im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 * Diese Software unterliegt der Version 2 der Apache License.
 *
 * Seanox Commons, Advanced Programming Interface
 * Copyright (C) 2020 Seanox Software Solutions
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.seanox.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

/**
 * Test cases for {@link com.seanox.common.Section#contains(String, String)}.<br>
 * <br>
 * SectionContainsTest 5.2.0 20200517<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 5.2.0 20200517
 */
@RunWith(JUnitPlatform.class)
@SuppressWarnings("javadoc")
public class SectionContainsTest {
    
    @Test
    public void testKeyInvalid_1() {
        Assertions.assertThrows(Exception.class, () -> {
            Section section = new Section();
            section.set("", null);
        });        
    }
    
    @Test
    public void testKeyInvalid_2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set(" ", null);
        });        
    }

    @Test
    public void testKeyInvalid_3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set("   ", null);
        });        
    }

    @Test
    public void testKeyInvalid_5() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set(null, null);
        });        
    }

    @Test
    public void testKeyInvalid_6() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set(" \0\0 ", null);
        });        
    }

    @Test
    public void testKeyInvalid_7() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set(" \r\n ", null);
        });        
    }
    
    @Test
    public void testKeyInvalid_8() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set(" \07\07 ", null);
        });        
    }

    @Test
    public void testKeyInvalid_9() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set(" \40\40 ", null);
        });        
    }

    @Test
    public void testKeyInvalid_A() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Section section = new Section();
            section.set(" \33\33 ", null);
        });        
    }
    
    @Test
    public void testKeyTolerance_1() {
        
        Section section = new Section();
        section.set("A", "a1");
        Assertions.assertEquals("a1", section.get("A"));
        Assertions.assertEquals("a1", section.get("a"));
        
        section.set("a", "a2");
        Assertions.assertEquals("a2", section.get("A"));
        Assertions.assertEquals("a2", section.get("a"));
        
        section.set(" a", "a3");
        Assertions.assertEquals("a3", section.get("A"));
        Assertions.assertEquals("a3", section.get("a"));
        
        section.set(" a ", "a4");
        Assertions.assertEquals("a4", section.get("A"));
        Assertions.assertEquals("a4", section.get("a"));

        section.set("a ", "a5");
        Assertions.assertEquals("a5", section.get("A"));
        Assertions.assertEquals("a5", section.get("a"));
    }
    
    @Test
    public void testKeyOverwrite_1() {
        
        Section section = new Section();
        section.set("A", "a1");
        section.set("a", "a2");
        section.set(" A", "a3");
        section.set(" a   ", "a4");
        Assertions.assertEquals("a4", section.get("A"));
        Assertions.assertEquals("a4", section.get("a"));
    }
}