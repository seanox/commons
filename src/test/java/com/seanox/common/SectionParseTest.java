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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.seanox.test.Resources;

/**
 * Test cases for {@link com.seanox.common.Section#parse(String)}.<br>
 * <br>
 * SectionParseTest 5.2.0 20200517<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 5.2.0 20200517
 */
@RunWith(JUnitPlatform.class)
@SuppressWarnings("javadoc")
public class SectionParseTest {
    
    @BeforeAll 
    public static void init() {
        System.setProperty("param-c", "p_c");
    }
    
    @Test
    public void testAcceptance_1() {

        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    }

    @Test
    public void testAcceptance_2() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    }
    
    @Test
    public void testAcceptance_3() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    }    
    
    @Test
    public void testAcceptance_4() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    } 
    
    @Test
    public void testAcceptance_5() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    }

    @Test
    public void testAcceptance_6() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    }

    @Test
    public void testAcceptance_7() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    }
    
    @Test
    public void testAcceptance_8() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt"), section.toString());
    }    
    
    @Test
    public void testOverride_1() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt").toLowerCase(), section.toString().toLowerCase());
    }

    @Test
    public void testDynamic_1() {
        
        Section section = Section.parse(Resources.getCurrentTestResourcePlain("_1.txt"));
        Assertions.assertEquals(Resources.getCurrentTestResourcePlain("_2.txt").toLowerCase(), section.toString().toLowerCase());
    }
}