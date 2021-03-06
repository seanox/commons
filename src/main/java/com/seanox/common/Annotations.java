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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.naming.directory.NoSuchAttributeException;

/**
 * Utilities for easy access to annotations.<br>
 * <br>
 * Annotations 2.0.0 20180106<br>
 * Copyright (C) 2020 Seanox Software Solutions.<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 2.0.0 20180106
 */
public class Annotations {
    
    /** Constructor, creates a new Annotations object. */    
    private Annotations() {
        return;
    }

    /**
     * Determines all fields of a class as array that are annotated with the
     * specified annotation. If this cannot be determined, the array is empty.
     * @param  source class to be analyzed
     * @param  type   desired annotation
     * @return the determined fields as array 
     */
    public static Field[] getFields(Class<?> source, Class<? extends Annotation> type) {
        return Annotations.getFields(source, type, null, -1);
    }
    
    /**
     * Determines all fields of a class as array that are annotated with the
     * specified annotation. If this cannot be determined, the array is empty.
     * @param  source class to be analyzed
     * @param  type   desired annotation
     * @param  filter field types to be searched (class, modifier)
     * @return the determined fields as array 
     */    
    public static Field[] getFields(Class<?> source, Class<? extends Annotation> type, Class<?>[] filter) {
        return Annotations.getFields(source, type, filter, -1);
    }

    /**
     * Determines all fields of a class as array that are annotated with the
     * specified annotation. If this cannot be determined, the array is empty.
     * @param  source    class to be analyzed
     * @param  type      desired annotation
     * @param  filter    field types to be searched (class, modifier)
     * @param  modifiers modifiers to be searched (or combination)
     * @return the determined fields as array 
     */       
    public static Field[] getFields(Class<?> source, Class<? extends Annotation> type, Class<?>[] filter, int modifiers) {

        Annotation     annotation;
        List<Class<?>> types;
        List<Field>    fields;
        
        Objects.requireNonNull(source, "Class of source required");
        Objects.requireNonNull(type, "Type of annotation required");

        fields = new ArrayList<>();
        
        if (filter == null)
            filter = new Class<?>[0];
        types = Arrays.asList(filter);

        for (Class<?> entry : Accession.getClassHerachie(source, true)) {
            for (Field field : entry.getDeclaredFields()) {
                annotation = field.getAnnotation(type);
                if (annotation == null)
                    continue;

                //only relevant fields are determined
                if ((types.isEmpty() || types.contains(field.getType())
                        && (modifiers <= 0 || (field.getModifiers() & modifiers) == modifiers))) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
        }

        return fields.toArray(new Field[0]);
    }
    
    /**
     * Determines all methods of a class as array that are annotated with the
     * specified annotation. If this cannot be determined, the array is empty.
     * @param  source class to be analyzed
     * @param  type   desired annotation
     * @return the determined methods as array 
     */  
    public static Method[] getMethods(Class<?> source, Class<? extends Annotation> type) {
        return Annotations.getMethods(source, type, null, -1);
    }

    /**
     * Determines all methods of a class as array that are annotated with the
     * specified annotation. If this cannot be determined, the array is empty.
     * @param  source class to be analyzed
     * @param  type   desired annotation
     * @param  filter field types to be searched (class, modifier)
     * @return the determined methods as array 
     */
    public static Method[] getMethods(Class<?> source, Class<? extends Annotation> type, Class<?>[] filter) {
        return Annotations.getMethods(source, type, filter, -1);
    }

    /**
     * Determines all methods of a class as array that are annotated with the
     * specified annotation. If this cannot be determined, the array is empty.
     * @param  source    class to be analyzed
     * @param  type      desired annotation
     * @param  filter    field types to be searched (class, modifier)
     * @param  modifiers modifiers to be searched (or combination)
     * @return the determined methods as array 
     */
    public static Method[] getMethods(Class<?> source, Class<? extends Annotation> type, Class<?>[] filter, int modifiers) {

        Annotation     annotation;
        List<Class<?>> types;

        Objects.requireNonNull(source, "Class of source required");
        Objects.requireNonNull(type, "Type of annotation required");

        List<Method> methods = new ArrayList<>();
        
        if (filter == null)
            filter = new Class<?>[0];
        types = Arrays.asList(filter);        

        for (Class<?> entry : Accession.getClassHerachie(source, true)) {
            for (Method method : entry.getDeclaredMethods()) {
                annotation = method.getAnnotation(type);
                if (annotation == null)
                    continue;

                //only relevant methods are determined
                if ((types.isEmpty() || types.contains(method.getReturnType())
                        && (modifiers <= 0 || (method.getModifiers() & modifiers) == modifiers))) {
                    method.setAccessible(true);
                    methods.add(method);
                }
            }
        }

        return methods.toArray(new Method[0]);
    }

    /**
     * Determines the specified annotation for a class.
     * @param  source class to be analyzed
     * @param  type   desired annotation
     * @return the determined annotation or {@code null} if this cannot be found
     */
    public static Annotation getAnnotation(Class<?> source, Class<? extends Annotation> type) {
        
        Objects.requireNonNull(source, "Class required");
        Objects.requireNonNull(type, "Type of annotation required");        
        
        for (Class<?> entry : Accession.getClassHerachie(source)) {
            Annotation annotation = entry.getAnnotation(type);
            if (annotation == null)
                continue;
            return annotation;
        }
        
        return null;
    }

    /**
     * Determines the specified annotation for a field.
     * @param  field field to be analyzed
     * @param  type  desired annotation
     * @return the determined annotation or {@code null} if this cannot be found
     */    
    public static Annotation getAnnotation(Field field, Class<? extends Annotation> type) {
        
        Objects.requireNonNull(field, "Field required");
        Objects.requireNonNull(type, "Type of annotation required");        
        
        return field.getAnnotation(type);
    }    

    /**
     * Determines the specified annotation for a method.
     * @param  method method to be analyzed
     * @param  type   desired annotation
     * @return the determined annotation or {@code null} if this cannot be found
     */        
    public static Annotation getAnnotation(Method method, Class<? extends Annotation> type) {
        
        Objects.requireNonNull(method, "Method required");
        Objects.requireNonNull(type, "Type of annotation required");        
        
        return method.getAnnotation(type);        
    }    
  
    /**
     * Determines the specified annotation for a parameter of a method.
     * @param  method    method to be analyzed
     * @param  parameter desired parameter
     * @param  type      desired annotation
     * @return the determined annotation or {@code null} if this cannot be found
     */     
    public static Annotation getAnnotation(Method method, String parameter, Class<? extends Annotation> type) {

        Objects.requireNonNull(method, "Method required");
        if (parameter != null)
            parameter = parameter.trim();
        if (parameter.isEmpty())
            parameter = null;
        Objects.requireNonNull(parameter, "Parameter required");
        Objects.requireNonNull(type, "Type of annotation required");
        
        Annotation[][] annotations = method.getParameterAnnotations();
        for (Annotation[] entries : annotations)
            for (Annotation entry : entries)
                if (type.equals(entry.getClass()))
                    return entry;
        
        return null;
    }    
    
    /**
     * Determines the data type of an attribute of an annotation or throws
     * {@link NoSuchAttributeException} if the attribute does not exist.
     * @param  annotation Annotation
     * @param  attribute  name of attribute
     * @return the determined data type
     * @throws NoSuchAttributeException 
     *     If this attribute cannot be determined.
     */
    public static Class<?> getAttributeType(Annotation annotation, String attribute)
            throws NoSuchAttributeException {
        
        Objects.requireNonNull(annotation, "Annotation required");
        if (attribute != null)
            attribute = attribute.trim();
        if (attribute.isEmpty())
            attribute = null;
        Objects.requireNonNull(attribute, "Attribute required");        
        
        try {return Accession.getMethod(annotation, attribute).getReturnType();
        } catch (NoSuchMethodException exception) {
            throw new NoSuchAttributeException(exception.getMessage());
        }
    }   
    
    /**
     * Determines the default value of an attribute of an annotation or throws
     * {@link NoSuchAttributeException} if the attribute does not exist.
     * @param  annotation Annotation
     * @param  attribute  name of attribute
     * @return the determined data type
     * @throws NoSuchAttributeException 
     *     If this attribute cannot be determined.
     */
    public static Object getAttributeDefault(Annotation annotation, String attribute)
            throws NoSuchAttributeException {
        
        Objects.requireNonNull(annotation, "Annotation required");
        if (attribute != null)
            attribute = attribute.trim();
        if (attribute.isEmpty())
            attribute = null;        
        Objects.requireNonNull(attribute, "Attribute required");        
        
        try {return Accession.getMethod(annotation, attribute).getDefaultValue();
        } catch (NoSuchMethodException exception) {
            throw new NoSuchAttributeException(exception.getMessage());
        }
    }     
    
    /**
     * Determines the value of an attribute of an annotation or throws
     * {@link NoSuchAttributeException} if the attribute does not exist.
     * @param  annotation Annotation
     * @param  attribute  name of attribute
     * @return the determined data type
     * @throws NoSuchAttributeException 
     *     If this attribute cannot be determined.
     * @throws IllegalAccessException
     *     If access to the field fails.
     * @throws InvocationTargetException
     *     If access to the field fails.
     */
    public static Object getAttributeValue(Annotation annotation, String attribute)
            throws IllegalAccessException, InvocationTargetException, NoSuchAttributeException {
        
        Objects.requireNonNull(annotation, "Annotation required");
        if (attribute != null)
            attribute = attribute.trim();
        if (attribute.isEmpty())
            attribute = null;        
        Objects.requireNonNull(attribute, "Attribute required");        

        try {return Accession.invoke(annotation, attribute);
        } catch (NoSuchMethodException exception) {
            throw new NoSuchAttributeException(exception.getMessage());
        }
    }    
}