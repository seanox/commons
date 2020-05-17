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
package com.seanox.test;

import java.io.IOException;
import java.io.InputStream;
import com.seanox.io.Streams;

/**
 * Utilities for test resources.<br>
 * <br>
 * Resources 3.0.0 20200517<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 3.0.0 20200517
 */
public class Resources {
    
    /** Constructor, creates a new Resources object. */
    private Resources() {
    }      
    
    /**
     * Determines the current test (package, class, method).
     * @return current test (package, class, method)
     */
    private static StackTraceElement getCurrentTest() {
        
        Throwable throwable = new Throwable();
        for (StackTraceElement stackTraceElement : throwable.getStackTrace())
            if (!Resources.class.getName().equals(stackTraceElement.getClassName()))
                return stackTraceElement;
        return null;
    }
    
    /**
     * Determines the content of a test resource file from the
     * sub-package of the current test class. 
     *     <dir>e.g. resource.txt -&gt; {@code \com\foo\TestClass\resource.txt}</dir>
     * @param  resource resource
     * @return content of the resource as byte array, otherwise {@code null}
     */
    public static byte[] getCurrentTestClassResource(String resource) {
        
        if (resource == null
                || resource.trim().isEmpty())
            return null;
        
        StackTraceElement stackTraceElement = Resources.getCurrentTest();
        
        Class<?> context;
        try {context = Class.forName(stackTraceElement.getClassName());
        } catch (ClassNotFoundException exception) {
            return null;
        }
        
        resource = stackTraceElement.getClassName().replaceAll("\\.", "/") + "/" + resource;  
        try (InputStream inputStream = context.getClassLoader().getResourceAsStream(resource)) {
            return Streams.read(inputStream);
        } catch (IOException exception) {
            return null;
        } 
    }    
    
    /**
     * Determines the content of a test resource file from the
     * sub-package of the current test class. 
     *     <dir>e.g. resource.txt -&gt; {@code \com\foo\TestClass\resource.txt}</dir>
     * @param  resource resource
     * @return content of the resource as string, otherwise {@code null}
     */
    public static String getCurrentTestClassResourcePlain(String resource) {
        return new String(Resources.getCurrentTestClassResource(resource));
    }  
    
    /**
     * Determines the content of a test resource file by a suffix for the
     * current test method from the sub-package of the current test class. 
     *     <dir>e.g. _1.txt -&gt; {@code \com\foo\TestClass\testMethod_1.txt}</dir>
     * @param  resource resource
     * @return content of the resource as byte array, otherwise {@code null}
     */
    public static byte[] getCurrentTestResource(String suffix) {
        
        if (suffix == null
                || suffix.trim().isEmpty())
            return null;
        
        StackTraceElement stackTraceElement = Resources.getCurrentTest();
        
        Class<?> context;
        try {context = Class.forName(stackTraceElement.getClassName());
        } catch (ClassNotFoundException exception) {
            return null;
        }
        
        String resource = stackTraceElement.getClassName().replaceAll("\\.", "/") + "/" + stackTraceElement.getMethodName() + suffix;
        try (InputStream inputStream = context.getClassLoader().getResourceAsStream(resource)) {
            return Streams.read(inputStream);
        } catch (IOException exception) {
            return null;
        } 
    }
    
    /**
     * Determines the content of a test resource file by a suffix for the
     * current test method from the sub-package of the current test class. 
     *     <dir>e.g. _1.txt -&gt; {@code \com\foo\TestClass\testMethod_1.txt}</dir>
     * @param  resource resource
     * @return content of the resource as string, otherwise {@code null}
     */
    public static String getCurrentTestResourcePlain(String resource) {
        return new String(Resources.getCurrentTestResource(resource));
    }    
}