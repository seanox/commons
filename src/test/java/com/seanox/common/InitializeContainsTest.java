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
 * Test cases for {@link com.seanox.common.Initialize#contains(String)}.<br>
 * <br>
 * InitializeTest_Contains 5.2.0 20200516<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 5.2.0 20200516
 */
@RunWith(JUnitPlatform.class)
@SuppressWarnings("javadoc")
public class InitializeContainsTest {
    
    @Test
    public void testKeyInvalid_1() {
        Assertions.assertThrows(Exception.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set("", null);
        });        
    }
    
    @Test
    public void testKeyInvalid_2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set(" ", null);
        });        
    }

    @Test
    public void testKeyInvalid_3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set("   ", null);
        });  
    }

    @Test
    public void testKeyInvalid_5() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set(null, null);
        });  
    }

    @Test
    public void testKeyInvalid_6() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set(" \0\0 ", null);
        });  
    }

    @Test
    public void testKeyInvalid_7() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set(" \r\n ", null);
        });  
    }
    
    @Test
    public void testKeyInvalid_8() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set(" \07\07 ", null);
        });  
    }

    @Test
    public void testKeyInvalid_9() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set(" \40\40 ", null);
        });  
    }

    @Test
    public void testKeyInvalid_A() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Initialize initialize = new Initialize();
            initialize.set(" \33\33 ", null);
        });  
    }
    
    @Test
    public void testKeyTolerance_1() {
        
        Initialize initialize = new Initialize();

        Section section1 = new Section();
        initialize.set("A", section1);
        Assertions.assertEquals(section1, initialize.get("A"));
        Assertions.assertEquals(section1, initialize.get("a"));
        
        Section section2 = new Section();
        initialize.set("a", section2);
        Assertions.assertEquals(section2, initialize.get("A"));
        Assertions.assertEquals(section2, initialize.get("a"));
        
        Section section3 = new Section();
        initialize.set(" a", section3);
        Assertions.assertEquals(section3, initialize.get("A"));
        Assertions.assertEquals(section3, initialize.get("a"));
        
        Section section4 = new Section();
        initialize.set(" a ", section4);
        Assertions.assertEquals(section4, initialize.get("A"));
        Assertions.assertEquals(section4, initialize.get("a"));

        Section section5 = new Section();
        initialize.set("a ", section5);
        Assertions.assertEquals(section5, initialize.get("A"));
        Assertions.assertEquals(section5, initialize.get("a"));
    }
    
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
        Assertions.assertEquals(section4, initialize.get("A"));
        Assertions.assertEquals(section4, initialize.get("a"));
    }
}