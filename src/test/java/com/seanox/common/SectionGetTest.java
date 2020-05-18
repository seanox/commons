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
 * Test cases for {@link com.seanox.common.Section}.<br>
 * <br>
 * SectionGetTest 5.2.0 20200517<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 5.2.0 20200517
 */
@RunWith(JUnitPlatform.class)
@SuppressWarnings("javadoc")
public class SectionGetTest {
    
    @Test
    public void testAcceptance_01() {
        
        Section section = new Section();
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            String value = section.get(key);
            Assertions.assertNull(value);
        }
    }
    
    @Test
    public void testAcceptance_02() {
        
        Section section = new Section(false);
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            String value = section.get(key);
            Assertions.assertNull(value);
        }
    }    
    
    @Test
    public void testAcceptance_03() {
        
        Section section = new Section(true);
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            String value = section.get(key);
            Assertions.assertNotNull(value);
        }
    }

    @Test
    public void testAcceptance_04() {
        
        Section section = new Section();
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            String value = section.get(key, "o");
            Assertions.assertEquals("o", value);
            Assertions.assertEquals(0, section.size());
        }
    }
    
    @Test
    public void testAcceptance_05() {
        
        Section section = new Section(false);
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            String value = section.get(key, "o");
            Assertions.assertEquals("o", value);
            Assertions.assertEquals(0, section.size());
        }
    }    
    
    @Test
    public void testAcceptance_06() {
        
        Section section = new Section(true);
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            String value = section.get(key, "o");
            Assertions.assertEquals("o", value);
            Assertions.assertEquals(0, section.size());
        }
    }    
    
    @Test
    public void testAcceptance_07() {
        
        Section section = new Section();
        String value = section.get("x");
        Assertions.assertNull(value);
    }
    
    @Test
    public void testAcceptance_08() {
        
        Section section = new Section(false);
        String value = section.get("x");
        Assertions.assertNull(value);
    }    

    @Test
    public void testAcceptance_09() {
        
        Section section = new Section(true);
        String value = section.get("x");
        Assertions.assertEquals("", value);
        Assertions.assertEquals(0, section.size());
    }    

    @Test
    public void testAcceptance_10() {
        
        Section section = new Section(true);
        String value = section.get("x", "a");
        Assertions.assertEquals("a", value);
        Assertions.assertEquals(0, section.size());
    }    
}