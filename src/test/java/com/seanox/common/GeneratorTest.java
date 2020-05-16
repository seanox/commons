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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.seanox.test.ResourceUtils;
import com.seanox.test.Timing;

/**
 * Test cases for {@link com.seanox.common.Generator}.<br>
 * <br>
 * GeneratorTest 5.2.0 20200515<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 5.2.0 20200515
 */
@RunWith(JUnitPlatform.class)
@SuppressWarnings({"javadoc", "unchecked", "rawtypes", "serial"})
public class GeneratorTest {
    
    @Test
    public void testAcceptance_1() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("1.txt"));
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testAcceptance_2() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("1.txt"));
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testAcceptance_3() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_1.txt"));
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testAcceptance_4() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        String path = new String();
        for (String entry : ("/1/22/333/4444/55555").split("/")) {
            path = path.concat(entry);
            values.put("base", path);
            values.put("name", entry);
            generator.set("path", values);
        }
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testAcceptance_5()
            throws Exception {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int loop = 1; loop < 7; loop++) {
            String charX = Character.toString((char)('A' -1 + loop));
            values.put("case", charX + "1");
            values.put("name", charX + "2");
            values.put("date", charX + "3");
            values.put("size", charX + "4");
            values.put("type", charX + "5");
            values.put("mime", charX + "6");
            buffer.write(generator.extract("file", values));
        }
        values.put("file", buffer.toByteArray());
        generator.set(values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }  
    
    @Test
    public void testAcceptance_8() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_2.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        String path = new String();
        for (String entry : ("/1/22/333/4444/55555").split("/")) {
            path = path.concat(entry);
            values.put("base", path);
            values.put("name", entry);
            generator.set("path", values);
        }
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testAcceptance_9() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        String path = new String();
        for (String entry : ("/1/22/333/4444/55555").split("/")) {
            path = path.concat(entry);
            values.put("base", path);
            values.put("name", entry);
            generator.set("path", values);
        }
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testAcceptance_A() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        String path = new String();
        for (String entry : ("1/22/333/4444/55555").split("/")) {
            path = path.concat(entry);
            values.put("base", path);
            values.put("name", entry);
            generator.set("path", values);
        }
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }  
    
    @Test
    public void testAcceptance_B() {
        Assertions.assertEquals("A\00\00\07\00\00B", new String(Generator.parse(("A#[0x0000070000]B").getBytes()).extract()));
    }
    
    @Test
    public void testAcceptance_C() throws Exception {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        for (int loop = 1; loop < 7; loop++) {
            String charX = Character.toString((char)('A' -1 + loop));
            values.put("case", charX + "1");
            values.put("name", charX + "2");
            values.put("date", charX + "3");
            values.put("size", charX + "4");
            values.put("type", charX + "5");
            values.put("mime", charX + "6");
            generator.set("file", values);
        }
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }    

    @Test
    public void testAcceptance_D() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("1.txt"));
        Hashtable<String, Object> values = new Hashtable() {{
            put("a", new Hashtable() {{
                put("a1", "xa1");
                put("a2", "xa2");
                put("a3", "xa3");
                put("b", new Hashtable() {{
                    put("b1", "xb1");
                    put("b2", "xb2");
                    put("b3", "xb3");
                    put("c", new Hashtable() {{
                        put("c1", "xc1");
                        put("c2", "xc2");
                        put("c3", "xc3");
                    }});
                }});
            }});
        }};
        generator.set(values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()).replaceAll("\\s+", ""));
    }
    
    @Test
    public void testAcceptance_E() {

        Generator generator = Generator.parse(ResourceUtils.getContent("1.txt"));
        Hashtable<String, Object> values = new Hashtable() {{
            put("row", new ArrayList() {{
                add(new Hashtable() {{
                    put("cell", new ArrayList() {{
                        add("A1");
                        add("A2");
                        add("A3");
                    }});
                }});
                add(new Hashtable() {{
                    put("cell", new ArrayList() {{
                        add("B1");
                        add("B2");
                        add("B3");
                    }});
                }});
                add(new Hashtable() {{
                    put("cell", new ArrayList() {{
                        add("C1");
                        add("C2");
                    }});
                }});
                add(new Hashtable() {{
                    put("cell", new ArrayList() {{
                        add("D1");
                    }});
                }});
                add(new Hashtable() {{
                    put("cell", new ArrayList() {{
                    }});
                }});
            }});
        }};
        generator.set("table", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }  
    
    @Test
    public void testAcceptance_F() {

        String template = "#[0x5065746572]#[0x7c756e64]#[0x7c646572]#[0x7c576f6c66]";
        Generator generator = Generator.parse(template.getBytes());
        Assertions.assertEquals("Peter|und|der|Wolf", new String(generator.extract()));
    }

    @Test
    public void testAcceptance_G() {

        String template = "#[0x5065746572]#[0x7C756E64]#[0x7C646572]#[0x7C576F6C66]";
        Generator generator = Generator.parse(template.getBytes());
        Assertions.assertEquals("Peter|und|der|Wolf", new String(generator.extract()));
    }
    
    @Test
    public void testAcceptance_H() {

        String template = "#[0X5065746572]#[0X7C756E64]#[0X7C646572]#[0X7C576F6C66]";
        Generator generator = Generator.parse(template.getBytes());
        Assertions.assertEquals("Peter|und|der|Wolf", new String(generator.extract()));
    }    
    
    @Test
    public void testPerformance_1() throws Exception {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("name", "A");
        values.put("date", "B");
        values.put("size", "C");
        values.put("type", "D");
        values.put("mime", "E");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Timing timing = Timing.create(true);
        for (long loop = 1; loop < 25000; loop++) {
            values.put("case", "X" + loop);
            buffer.write(generator.extract("file", values));
        }
        values.put("file", buffer.toByteArray());
        generator.set("file", values);
        generator.extract();
        timing.assertTimeIn(5000);
    } 
    
    @Test
    public void testPerformance_2() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testAcceptance_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("name", "A");
        values.put("date", "B");
        values.put("size", "C");
        values.put("type", "D");
        values.put("mime", "E");
        Timing timing = Timing.create(true);
        for (long loop = 1; loop < 2500; loop++) {
            values.put("case", "X" + loop);
            generator.set("file", values);
        }
        generator.extract();
        timing.assertTimeIn(2500);
    }
    
    @Test
    public void testRecursion_1() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("teST", "xx1");
        generator.set("path", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }   
    
    @Test
    public void testRecursion_2() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("teST", "xx1");
        generator.set("path", values);
        values.put("teST", "xx2");
        generator.set("path", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }   
    
    @Test
    public void testRecursion_3() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("teST", "xx1");
        generator.set("path", values);
        values.put("teST", "xx2");
        generator.set("path", values);
        values.put("teST", "xx3");
        generator.set("path", values);
        values.put("teST", "xx4");
        generator.set("path", values);
        values.put("teST", "xx5");
        generator.set("path", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }    
    
    @Test
    public void testRecursion_4() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_2.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("teST", "xx1");
        generator.set("path", values);
        values.put("teST", "xx2");
        generator.set("path", values);
        values.put("teST", "xx3");
        generator.set("path", values);
        values.put("teST", "xx4");
        generator.set("path", values);
        values.put("teST", "xx5");
        generator.set("path", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }    

    @Test
    public void testRecursion_5() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("bv", "bv-ok");
        values.put("cv", "cv-ok");
        values.put("dv", "dv-ok");
        values.put("b1v", "b1v-ok");
        generator.set("a", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    } 
    
    @Test
    public void testRecursion_6() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("bv", "bv-ok");
        values.put("cv", "cv-ok");
        values.put("dv", "dv-ok");
        values.put("b1v", "b1v-ok");
        generator.set("a", values);
        generator.set("b", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    } 
    
    @Test
    public void testRecursion_7() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("bv", "bv-ok");
        values.put("cv", "cv-ok");
        values.put("dv", "dv-ok");
        values.put("b1v", "b1v-ok");
        generator.set("a", values);
        generator.set("b", values);
        generator.set("c", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testRecursion_8() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("bv", "bv-ok");
        values.put("cv", "cv-ok");
        values.put("dv", "dv-ok");
        values.put("b1v", "b1v-ok");
        generator.set("a", values);
        generator.set("b", values);
        generator.set("c", values);
        generator.set("d", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }     

    @Test
    public void testRecursion_9() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("bv", "bv-ok");
        values.put("cv", "cv-ok");
        values.put("dv", "dv-ok");
        values.put("b1v", "b1v-ok");
        generator.set("d", values);
        generator.set("c", values);
        generator.set("b", values);
        generator.set("a", values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    }
    
    @Test
    public void testRecursion_A() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("testRecursion_0_3.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("bv", "bv-ok");
        values.put("cv", "cv-ok");
        values.put("dv", "dv-ok");
        values.put("b1v", "b1v-ok");
        values.put("a", values);
        values.put("b", values);
        values.put("c", values);
        values.put("d", values);
        generator.set(values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    } 

    @Test
    public void testRecursion_B() {
        
        Generator generator = Generator.parse(ResourceUtils.getContent("1.txt"));
        Hashtable<String, Object> values = new Hashtable<>();
        values.put("a", "xa");
        values.put("b", "xb");
        values.put("c", "xc");
        generator.set(values);
        Assertions.assertEquals(ResourceUtils.getContentPlain("2.txt"), new String(generator.extract()));
    } 
    
    @Test
    public void testNullable_1() {
      
        Generator generator = Generator.parse(null);
        generator.extract(null);
        generator.extract("");
        generator.extract(null, null);
        generator.extract("", new Hashtable<>());
        generator.set(null);
        generator.set(new Hashtable<>());
        generator.set(null, null);
        generator.set("", new Hashtable<>());
    } 
}