package com.seanox.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.seanox.test.utils.ResourceUtils;

/**
 *  TestCases for {@link com.seanox.common.Section#parse(String)}.
 */
public class SectionTest_Parse {
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_1() {

        Section section = Section.parse(ResourceUtils.getContextContent());
        assertEquals(section.toString(), ResourceUtils.getContextContent("testAceptance_1_1"));
    }

    /** TestCase for aceptance. */
    @Test
    public void testAceptance_2() {
        
        Section section = Section.parse(ResourceUtils.getContextContent());
        assertEquals(section.toString(), ResourceUtils.getContextContent("testAceptance_2_1"));
    }
    
    /** TestCase for aceptance. */
    @Test
    public void testAceptance_3() {
        
        Section section = Section.parse(ResourceUtils.getContextContent());
        assertEquals(section.toString(), ResourceUtils.getContextContent("testAceptance_3_1"));
    }    

    /** TestCase for override keys. */
    @Test
    public void testOverride_1() {
        
        Section section = Section.parse(ResourceUtils.getContextContent());
        assertEquals(section.toString(), ResourceUtils.getContextContent("testOverride_1_1"));
    }

    /** TestCase for dynamic keys. */
    @Test
    public void testDynamic_1() {
        
        Section section = Section.parse(ResourceUtils.getContextContent());
        assertEquals(section.toString(), ResourceUtils.getContextContent("testDynamic_1_1"));
    }
}