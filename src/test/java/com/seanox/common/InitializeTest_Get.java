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
 * Test cases for {@link com.seanox.devwex.Initialize}.<br>
 * <br>
 * InitializeTest_Get 5.2.0 20200516<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 5.2.0 20200516
 */
@RunWith(JUnitPlatform.class)
@SuppressWarnings("javadoc")
public class InitializeTest_Get {
    
    @Test
    public void testAcceptance_01() {
        
        Initialize initialize = new Initialize();
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            Section section = initialize.get(key);
            Assertions.assertNull(section);
        }
    }
    
    @Test
    public void testAcceptance_02() {
        
        Initialize initialize = new Initialize(false);
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            Section section = initialize.get(key);
            Assertions.assertNull(section);
        }
    }    
    
    @Test
    public void testAcceptance_03() {
        
        Initialize initialize = new Initialize(true);
        for (String key : new String[] {null, "", " ", " \t ", " \r ", " \n ", " \7 "}) {
            Section section = initialize.get(key);
            Assertions.assertNotNull(section);
        }
    }
    
    @Test
    public void testAcceptance_04() {
        
        Initialize initialize = new Initialize();
        Section section = initialize.get("x");
        Assertions.assertNull(section);
    }
    
    @Test
    public void testAcceptance_05() {
        
        Initialize initialize = new Initialize(false);
        Section section = initialize.get("x");
        Assertions.assertNull(section);
    }    
    
    @Test
    public void testAcceptance_06() {
        
        Initialize initialize = new Initialize(true);
        Section section = initialize.get("x");
        Assertions.assertNotNull(section);
        Assertions.assertTrue(initialize.size() == 1);
        section.set("a", "1");
        section = initialize.get("x");
        Assertions.assertNotNull(section);
        Assertions.assertTrue(section.size() == 1);
        Assertions.assertEquals("1", section.get("a"));
    }     
}