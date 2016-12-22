/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 *  Diese Software unterliegt der Version 2 der GNU General Public License.
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
package com.seanox.test.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 *  Utils for resources.
 *      <dir>Context Content</dir>
 *  This is a simple text content from the ClassPath.<br>
 *  The content is based on a text file (file extension: txt) which is locate
 *  to a class in the same package. The content consists of sections. Sections
 *  also correspond to the names of methods from the context class.<br>
 *  A section starts at the beginning of the line with {@code #### <name>} and
 *  ends with the following or the file end.
 */
public class ResourceUtils {
    
    /**
     *  Determines the context (package, class, method) from the current call.
     *  @return context (package, class, method) from the current call
     */
    private static StackTraceElement getCurrentContext() {
        
        Throwable throwable = new Throwable();
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            if (!ResourceUtils.class.getName().equals(stackTraceElement.getClassName()))
                return stackTraceElement;
        }
        return null;
    }
    
    /**
     *  Determines the context content for the called class and method name.
     *  @return context content for the current call, otherwise {@code null}
     */
    public static String getContextContent() {
        return ResourceUtils.getContextContent(true);
    }
    
    /**
     *  Determines the context content for the called class and method name.
     *  @param  normalize converts line breaks into the system standard
     *  @return context content for the current call, otherwise {@code null}
     */
    public static String getContextContent(boolean normalize) {

        StackTraceElement stackTraceElement = ResourceUtils.getCurrentContext();
        return ResourceUtils.getContextContent(stackTraceElement.getMethodName(), normalize);
    }
    
    /**
     *  Determines the context content for the called class and specified name.
     *  @param  name name of content
     *  @return context content for the current call, otherwise {@code null}
     */
    public static String getContextContent(String name) {
        
        return ResourceUtils.getContextContent(name, true);
    }
    
    /**
     *  Determines the context content for the called class and specified name.
     *  @param  name      name of content
     *  @param  normalize converts line breaks into the system standard
     *  @return context content for the current call, otherwise {@code null}
     */
    public static String getContextContent(String name, boolean normalize) {

        StackTraceElement stackTraceElement = ResourceUtils.getCurrentContext();
        
        try {
            Class<?> context = Class.forName(stackTraceElement.getClassName());
            String contextName = stackTraceElement.getClassName().replaceAll("\\.", "/") + ".txt";
            InputStream inputStream = context.getClassLoader().getResourceAsStream(contextName);
            String content = new String(StreamUtils.read(inputStream)); 
            String filter = "^(?s)(?:.*?" + Pattern.LINE_BREAK + "){0,1}#{4,}" + Pattern.LINE_SPACE + "*" + name + Pattern.LINE_SPACE + "*" + Pattern.LINE_BREAK
                    + "(.*?)" + "(?:" + Pattern.LINE_BREAK + "#{4,}" + Pattern.LINE_SPACE + "*[a-zA-Z0-9_]+" + Pattern.LINE_SPACE + "*" + Pattern.LINE_BREAK + ".*){0,1}$";
            if (!content.matches(filter))
                return null;
            content = content.replaceAll(filter, "$1");
            if (normalize)
                content = content.replaceAll(Pattern.LINE_BREAK, System.lineSeparator());
            return content;
        } catch (ClassNotFoundException | IOException exception) {
            return null;
        }
    }
}