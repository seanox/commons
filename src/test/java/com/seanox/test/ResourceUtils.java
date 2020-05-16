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

/**
 * Utilities for resources.
 * 
 * Resources are a simple text content from the ClassPath.<br>
 * The content is based on a text file (file extension: txt) which is locate
 * to a class in the same package.<br>
 * <br>
 * Furthermore, the content consists of sections.<br>
 * Sections begin at the beginning of the line with {@code #### <name>} and
 * ends with the following or the file end.<br>
 * The name is unrestricted. The names of the methods can be used in the
 * context. When the resource is called, the file is searched in the package
 * of the class and from this file, only the segments of the currently
 * executed method are used.<br>
 * <br>
 * Furthermore, the name of a section can be extended by decimal numbers at
 * the end {@code #### <name>_<number>}. These are used as indexes.<br>
 * <br>
 * ResourceUtils 3.0.0 20200516<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 3.0.0 20200516
 */
public class ResourceUtils {
    
    /** Constructor, creates a new ResourceUtils object. */
    private ResourceUtils() {
    }      
    
    /**
     * Determines the context (package, class, method) from the current call.
     * @return context (package, class, method) from the current call
     */
    private static StackTraceElement getContext() {
        
        Throwable throwable = new Throwable();
        for (StackTraceElement stackTraceElement : throwable.getStackTrace())
            if (!ResourceUtils.class.getName().equals(stackTraceElement.getClassName()))
                return stackTraceElement;
        return null;
    }

    /**
     * Determines the context content for the called class.
     * Absolute means that the resource file is included in the resource package
     * of the test class.<br>
     *     <dir>e.g. resource.txt -&gt; {@code \tld\foo\TestClass\resource.txt}</dir>
     * Relative means that the resource is based on the name of the test method,
     * which is extended with the resource and separated by an underscore.<br>
     *     <dir>e.g. 1.txt -&gt; {@code \tld\foo\TestClass\testtestMethod_1.txt}</dir>
     * Absolute resources have priority.
     * @param  resource resource
     * @return content to the called class, otherwise {@code null}
     */
    public static byte[] getContent(String resource) {
        
        if (resource == null
                || resource.trim().isEmpty())
            return null;
        
        StackTraceElement stackTraceElement = ResourceUtils.getContext();
        
        Class<?> context;
        try {context = Class.forName(stackTraceElement.getClassName());
        } catch (ClassNotFoundException exception) {
            return null;
        }
        
        String root = stackTraceElement.getClassName().replaceAll("\\.", "/");
        if (context.getClassLoader().getResource(root + "/" + resource) == null)
            resource = stackTraceElement.getMethodName() + "_" + resource;
        resource = root + "/" + resource;  
        try (InputStream inputStream = context.getClassLoader().getResourceAsStream(resource)) {
            return StreamUtils.read(inputStream);
        } catch (IOException exception) {
            return null;
        } 
    }
    
    /**
     * Determines the context content for the called class as string.
     * Absolute means that the resource file is included in the resource package
     * of the test class.<br>
     *     <dir>e.g. resource.txt -&gt; {@code \tld\foo\TestClass\resource.txt}</dir>
     * Relative means that the resource is based on the name of the test method,
     * which is extended with the resource and separated by an underscore.<br>
     *     <dir>e.g. 1.txt -&gt; {@code \tld\foo\TestClass\testtestMethod_1.txt}</dir>
     * Absolute resources have priority.
     * @param  resource resource
     * @return content to the called class as string, otherwise {@code null}
     */
    public static String getContentPlain(String resource) {
        return new String(ResourceUtils.getContent(resource));
    }    
}