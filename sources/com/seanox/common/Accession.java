/**
 * LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 * im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 * Diese Software unterliegt der Version 2 der GNU General Public License.
 *
 * Seanox Commons, Advanced Programming Interface
 * Copyright (C) 2020 Seanox Software Solutions
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.seanox.common;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Accession provides low-level access methods to constructors, fields, and
 * methods of any Java object, even those that are not public or in a 
 * superclass. Failed accesses and releases can cause various exceptions.<br>
 * <br>
 * Accession 2.0.1 20180107<br>
 * Copyright (C) 2020 Seanox Software Solutions<br>
 * All rights reserved.
 *
 * @author  Seanox Software Solutions
 * @version 2.0.1 20180107
 */
public class Accession {

    /** Constructor, creates a new Accession object. */
    private Accession() {
        return;
    }

    /**
     * Determines the source class for an object.
     * @param  object object
     * @return the source class for the object
     */
    private static Class<?> getSourceClass(Object object) {
        
        if (object == null)
            return null;
        if (object instanceof Class)
            return (Class<?>)object;
        return object.getClass();
    }
    
    /**
     * Determines the data types for an object(array).
     * If {@code null} is passed, {@code null} is returned.
     * @param  objects object(s)
     * @return the determineted the data types for an object(array)
     */
    private static Class<?>[] getTypes(Object... objects) {
        
        List<Class<?>> types;

        if (objects == null)
            return null;
        
        types = new ArrayList<>();
        for (Object object : objects) {
            if (object != null)
                object = object.getClass();
            types.add((Class<?>)object);
        }
        
        return types.toArray(new Class<?>[0]);
    }
    
    /**
     * Returns a string without any leading and trailing white spaces.
     * The value {@code null} becomes an empty string. 
     * @param  string string to be trimemd
     * @return string without any leading and trailing white spaces
     */    
    private static String trim(String string) {
        
        if (string == null)
            string = "";
        return string.trim();
    }    
    
    /**
     * Gets a constructor, even those that are not public, or throws
     * {@link NoSuchMethodException} if the constructor does not exist.
     * @param  object class or object to be analyzed
     * @param  types  data types as an array
     * @return the determined constructor
     * @throws NoSuchMethodException
     *     If this constructor cannot be determined.
     */    
    public static Constructor<?> getConstructor(Object object, Class<?>[] types) 
            throws NoSuchMethodException {

        Constructor<?> constructor;
        
        Objects.requireNonNull(object);

        object = Accession.getSourceClass(object);
        constructor = ((Class<?>)object).getDeclaredConstructor(types);
        constructor.setAccessible(true);
        
        return constructor;
    }
    
    /**
     * Determines all constructors, even those that are not public.
     * Returns all detected constructors as an array.
     * If no constructor can be determined, the array is empty.
     * If {@code null} is passed, {@code null} is returned.
     * @param  object class or object to be analyzed
     * @return all determined constructors as array
     */    
    public static Constructor<?>[] getConstructors(Object object) {

        List<Constructor<?>> constructors;
        
        if (object == null)
            return null;        

        constructors = new ArrayList<>();
        object = Accession.getSourceClass(object);
        for (Constructor<?> constructor : ((Class<?>)object).getDeclaredConstructors()) {
            constructor.setAccessible(true);
            constructors.add(constructor);
        }
        
        return constructors.toArray(new Constructor[0]);
    }
    
    /**
     * Gets a field, even those that are not public or in a superclass, or
     * throws {@link NoSuchFieldException} if the field does not exist.
     * @param  object class or object to be analyzed
     * @param  name   name of the field
     * @return the determined field
     * @throws NoSuchFieldException 
     *     If this field cannot be determined.
     */
    public static Field getField(Object object, String name)
            throws NoSuchFieldException {

        Class<?> source;
        Field    field;
        
        Objects.requireNonNull(object);
        
        name = Accession.trim(name);
        if (name.isEmpty())
            throw new NoSuchFieldException();
         
        for (source = Accession.getSourceClass(object);
                source != null;
                source = source.getSuperclass()) {
            try {field = source.getDeclaredField(name);
            } catch (NoSuchFieldException exception) {
                continue;
            }
            field.setAccessible(true);
            return field;
        }
        
        throw new NoSuchFieldException();
    }
    
    /**
     * Determines all fields from an object or class, even those that are not
     * public or in a superclass. Returns all detected fields as an array.
     * If no field can be determined, the array is empty.
     * If {@code null} is passed, {@code null} is returned.
     * @param  object class or object to be analyzed
     * @return all determined fields as array
     */
    public static Field[] getFields(Object object) {

        Class<?>    source;
        List<Field> fields;
        
        if (object == null)
            return null;        

        fields = new ArrayList<>();
        for (source = Accession.getSourceClass(object);
                source != null;
                source = source.getSuperclass())
            for (Field field : source.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }

        return fields.toArray(new Field[0]);
    }
    
    /**
     * Gets a method, even those that are not public or in a superclass, or
     * throws {@link NoSuchMethodException} if the method does not exist.
     * @param  object class or object to be analyzed
     * @param  name   name of the method
     * @return the determined method
     * @throws NoSuchMethodException
     *     If this method cannot be determined.
     */
    public static Method getMethod(Object object, String name)
            throws NoSuchMethodException {
        return Accession.getMethod(object, name, new Class[0]);
    }
    
    /**
     * Gets a method, even those that are not public or in a superclass, or
     * throws {@link NoSuchMethodException} if the method does not exist.
     * @param  object class or object to be analyzed
     * @param  name   name of the method
     * @param  types  data types as an array
     * @return the determined method
     * @throws NoSuchMethodException
     *     If this method cannot be determined.
     */
    public static Method getMethod(Object object, String name, Class<?>... types)
            throws NoSuchMethodException {

        Class<?> source;
        Method   method;
        
        Objects.requireNonNull(object);

        name = Accession.trim(name);
        if (name.isEmpty())
            throw new NoSuchMethodException();

        for (source = Accession.getSourceClass(object);
                source != null;
                source = source.getSuperclass()) {
            try {method = source.getDeclaredMethod(name, types);
            } catch (NoSuchMethodException exception) {
                continue;
            }
            method.setAccessible(true);
            return method; 
        }
        
        throw new NoSuchMethodException();
    }

    /**
     * Determines all methods from an object or class, even those that are not
     * public or in a superclass. Returns all detected methods as an array.
     * If no method can be determined, the array is empty.
     * If {@code null} is passed, {@code null} is returned.
     * @param  object class or object to be analyzed
     * @return all determined methods as array
     */
    public static Method[] getMethods(Object object) {

        Class<?>     source;
        List<Method> methods;
        
        if (object == null)
            return null;

        methods = new ArrayList<>();
        for (source = Accession.getSourceClass(object);
                source != null;
                source = source.getSuperclass())
            for (Method method : source.getDeclaredMethods()) {
                method.setAccessible(true);
                methods.add(method);
            }

        return methods.toArray(new Method[0]) ;
    }
    
    /**
     * Determines the inherited class hierarchy as array from an object or
     * class. If this cannot be determined, the array is empty.
     * If {@code null} is passed, {@code null} is returned.
     * @param  source class or object to be analyzed
     * @return the determined class hierarchy of the inheritance as array
     */
    public static Class<?>[] getClassHerachie(Object source) {
        return Accession.getClassHerachie(source, false);
    }

    /**
     * Determines the inherited class hierarchy as array from an object or
     * class. If this cannot be determined, the array is empty.
     * If {@code null} is passed, {@code null} is returned.
     * @param  source class or object to be analyzed
     * @param  reverse {@code true} to revert the class hierarchy, which then
     *                 starts with the super class
     * @return the determined class hierarchy of the inheritance as array
     */    
    public static Class<?>[] getClassHerachie(Object source, boolean reverse) {

        List<Class<?>> classes;

        if (source == null)
            return null;

        if (!(source instanceof Class))
            source = source.getClass();
        
        classes = new ArrayList<>();
        while (source != null) {
            classes.add((Class<?>)source);
            source = ((Class<?>)source).getSuperclass();
        }
        
        if (reverse)
            Collections.reverse(classes);

        return classes.toArray(new Class[0]);
    }
    
    /**
     * Creates a new object instance of the class without arguments, even
     * those that are not public.
     * @param  object object or class
     * @return the created instance of the class
     * @throws IllegalAccessException
     *     In case of access violations to the constructor.
     * @throws InstantiationException
     *     If the instance was created incorrectly.
     * @throws InvocationTargetException
     *     If exceptions occur.
     * @throws NoSuchMethodException
     *     If the constructor does not exist.
     */
    public static Object construct(Object object)
            throws IllegalAccessException, InstantiationException, InvocationTargetException,
                NoSuchMethodException {
        return Accession.construct(object, (Class<?>[])null, (Object[])null);
    }

    /**
     * Creates a new object instance of the class with the arguments as an
     * array of objects, even those that are not public. For arguments with a
     * primitive data type, the appropriate wrapper must be used.
     * @param  object    object or class
     * @param  arguments arguments as an array of objects
     * @return the created instance of the class
     * @throws IllegalAccessException
     *     In case of access violations to the constructor.
     * @throws InstantiationException
     *     If the instance was created incorrectly.
     * @throws InvocationTargetException
     *     If exceptions occur.
     * @throws NoSuchMethodException
     *     If the constructor does not exist.
     */
    public static Object construct(Object object, Object... arguments)
            throws IllegalAccessException, InstantiationException, InvocationTargetException,
                NoSuchMethodException {
        return Accession.construct(object, Accession.getTypes(arguments), arguments);
    }
    
    /**
     * Creates a new object instance of the class with the arguments as an
     * array of data types and objects, even those that are not public.
     * For arguments with a primitive data type, the appropriate wrapper must
     * be used.
     * @param  object    object or class
     * @param  types     data types as an array
     * @param  arguments arguments as an array of objects
     * @return the created instance of the class
     * @throws IllegalAccessException
     *     In case of access violations to the constructor.
     * @throws InstantiationException
     *     If the instance was created incorrectly.
     * @throws InvocationTargetException
     *     If exceptions occur.
     * @throws NoSuchMethodException
     *     If the constructor does not exist.
     */
    public static Object construct(Object object, Class<?>[] types, Object[] arguments)
            throws IllegalAccessException, InstantiationException, InvocationTargetException,
                NoSuchMethodException {
        return Accession.getConstructor(object, types).newInstance(arguments);
    }
    
    /**
     * Gets the value of a field from an object, even those that are not
     * public or in a superclass. Primitive data types are returned as a
     * corresponding wrapper object.
     * @param  object object
     * @param  field   name of the field
     * @return the value of the field, primitive data types are returned as a
     *         corresponding wrapper object
     * @throws IllegalAccessException
     *     In the case of an access violation.
     * @throws NoSuchFieldException
     *     If the field does not exist.
     */
    public static Object get(Object object, String field)
            throws IllegalAccessException, NoSuchFieldException {

        Objects.requireNonNull(object, "Invalid object [null]");
        
        return Accession.getField(object, field).get(object);
    }
    
    /**
     * Sets the value of a field from an object, even those that are not
     * public or in a superclass. A primitive data type are passed as
     * corresponding wrapper object.
     * @param  object object
     * @param  field name of the field
     * @param  value value to be set as object
     * @throws IllegalAccessException
     *     In case of an access violation to the field.
     * @throws NoSuchFieldException
     *     If the field does not exist.
     */
    public static void set(Object object, String field, Object value)
            throws IllegalAccessException, NoSuchFieldException {

        Objects.requireNonNull(object, "Invalid object [null]");
        
        field = Accession.trim(field);
        if (field.isEmpty())
            throw new NoSuchFieldException();
        
        Accession.set(object, Accession.getField(object, field), value);
    }
    
    /**
     * Sets the value of a field from an object, even those that are not
     * public or in a superclass. A primitive data type are passed as
     * corresponding wrapper object.
     * @param  object object
     * @param  field name of the field
     * @param  value value to be set as object
     * @throws IllegalAccessException
     *     In case of an access violation to the field.
     */
    private static void set(Object object, Field field, Object value)
            throws IllegalAccessException {

        Class<?> type;
        
        type = field.getType();
        if (value == null
                && type.isPrimitive()) {

            if (type.equals(Boolean.TYPE))
                value = Boolean.FALSE;
            else if (type.equals(Byte.TYPE))
                value = Byte.valueOf((byte)0);
            else if (type.equals(Character.TYPE))
                value = Character.valueOf((char)0);
            else if (type.equals(Double.TYPE))
                value = Double.valueOf(0);
            else if (type.equals(Float.TYPE))
                value = Float.valueOf(0);
            else if (type.equals(Integer.TYPE))
                value = Integer.valueOf(0);
            else if (type.equals(Long.TYPE))
                value = Long.valueOf(0);
            else if (type.equals(Short.TYPE))
                value = Short.valueOf((short)0);
        }
        
        field.set(object, value);
    }
    
    /**
     * Synchronizes a field in two objects by copying the value from source to
     * target, even those that are not public or in a superclass.
     * @param  source source object 
     * @param  target target object
     * @param  field  name of the field
     * @throws NoSuchFieldException
     *     If the field does not exist.
     * @throws IllegalAccessException
     *     If access to the field fails.
     */
    public static void synchronize(Object source, Object target, String field)
            throws NoSuchFieldException, IllegalAccessException {

        Objects.requireNonNull(source, "Invalid source [null]");
        Objects.requireNonNull(target, "Invalid target [null]");
        
        field = Accession.trim(field);
        if (field.isEmpty())
            throw new NoSuchFieldException();     
        
        Accession.synchronize(source, target, field, field);
    }
    
    /**
     * Synchronizes a field in two objects by copying the value from source to
     * target, even those that are not public or in a superclass.
     * @param  source source object 
     * @param  target target object
     * @param  field  name of the field
     * @param  alias  name of the field in the target object
     * @throws NoSuchFieldException
     *     If the field does not exist.
     * @throws IllegalAccessException
     *     If access to the field fails.
     */
    public static void synchronize(Object source, Object target, String field, String alias)
            throws NoSuchFieldException, IllegalAccessException {

        Object value;

        Objects.requireNonNull(source, "Invalid source [null]");
        Objects.requireNonNull(target, "Invalid target [null]");
        
        field = Accession.trim(field);
        if (field.isEmpty())
            throw new NoSuchFieldException("Source field not found");

        value = null;
        
        try {value = Accession.getField(source, field).get(source);
        } catch (NoSuchFieldException exception) {
            throw new NoSuchFieldException("Source field not found");
        }
        
        alias = Accession.trim(alias);
        if (alias.isEmpty())
            alias = field;
        
        try {Accession.set(target, alias, value);
        } catch (NoSuchFieldException exception) {
            throw new NoSuchFieldException("Target field (alias) not found");
        }
    }
    
    /**
     * Synchronizes two objects at field-level by copying the values from
     * source to target, even those that are not public or in a superclass.
     * @param  source source object 
     * @param  target target object
     * @throws NoSuchFieldException
     *     If the field does not exist.
     * @throws IllegalAccessException
     *     If access to the field fails.
     */
    public static void synchronize(Object source, Object target)
            throws NoSuchFieldException, IllegalAccessException {

        Objects.requireNonNull(source, "Invalid source [null]");
        Objects.requireNonNull(target, "Invalid target [null]");
        
        Accession.synchronize(source, target, (Filter)null, (Map<String, String>)null);
    }

    /**
     * Synchronizes two objects at field-level by copying the values from
     * source to target, even those that are not public or in a superclass.
     * @param  source source object 
     * @param  target target object
     * @param  filter  list of includes/excludes fields
     * @throws NoSuchFieldException
     *     If the field does not exist.
     * @throws IllegalAccessException
     *     If access to the field fails.
     */
    public static void synchronize(Object source, Object target, Filter filter)
            throws NoSuchFieldException, IllegalAccessException {

        Objects.requireNonNull(source, "Invalid source [null]");
        Objects.requireNonNull(target, "Invalid target [null]");
        
        Accession.synchronize(source, target, filter, (Map<String, String>)null);
    }

    /**
     * Synchronizes two objects at field-level by copying the values from
     * source to target, even those that are not public or in a superclass.
     * @param  source source object 
     * @param  target target object
     * @param  filter  list of includes/excludes fields
     * @param  mapping mapping table source -&lt; target 
     * @throws NoSuchFieldException
     *     If the field does not exist.
     * @throws IllegalAccessException
     *     If access to the field fails.
     */
    public static void synchronize(Object source, Object target, Filter filter, Map<String, String> mapping)
            throws NoSuchFieldException, IllegalAccessException {

        FieldMatcher matcher;
        Object       value;
        String       alias;
        
        Objects.requireNonNull(source, "Invalid source [null]");
        Objects.requireNonNull(target, "Invalid target [null]");

        matcher = new FieldMatcher(source, filter);

        for (Field field : Accession.getFields(source)) {
            
            if (!matcher.validate(field))
                continue;

            if (field.isSynthetic())
                continue;
            
            alias = field.getName();
            if (mapping != null)
                alias = mapping.get(field.getName());
            if (alias == null)
                alias = field.getName();
            
            value = field.get(source);
            
            try {field = Accession.getField(target, alias);
            } catch (NoSuchFieldException exception) {
                if (filter instanceof AutoExclude
                        || filter instanceof AutoExclude)
                    continue;
                throw new NoSuchFieldException("Target field (" + alias + ") not found");
            }
            
            if (Modifier.isFinal(field.getModifiers()))
                continue;
            
            Accession.set(target, field, value);
        }
    }    
    
    /**
     * Executes a method from an object or class without further arguments,
     * even if the method is not public or in a superclass. Returns the return
     * value of the method as an object. Primitive data types are returned as
     * a corresponding wrapper object.
     * @param  object object or class
     * @param  method name of the method
     * @return the return value of the method as an object
     * @throws NoSuchMethodException
     *     If the method does not exist.
     * @throws IllegalAccessException
     *     If access to the field fails.
     * @throws InvocationTargetException
     *     If call and/or execution of the method fails.
     */
    public static Object invoke(Object object, String method)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return Accession.invoke(object, method, (Class<?>[])null, (Object[])null);
    }

    /**
     * Executes a method from an object or class with the arguments as an
     * array of objects, even those that are not public or in a superclass.
     * For arguments with a primitive data type, the appropriate wrapper must
     * be used. Returns the return value of the method as an object. Primitive
     * data types are returned as a corresponding wrapper object.
     * @param  object    object or class
     * @param  method    name of the method
     * @param  arguments arguments as an array of objects
     * @return the return value of the method as an object
     * @throws NoSuchMethodException
     *     If the method does not exist.
     * @throws IllegalAccessException
     *     In case of an access violation to the method.
     * @throws InvocationTargetException
     *     If call and/or execution of the method fails.
     */    
    public static Object invoke(Object object, String method, Object... arguments)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return Accession.invoke(object, method, Accession.getTypes(arguments), arguments);
    }

    /**
     * Executes a method from an object or class with the arguments as an
     * array of data types and objects, even those that are not public or in a
     * superclass. For arguments with a primitive data type, the appropriate
     * wrapper must be used. Returns the return value of the method as an
     * object. Primitive data types are returned as a corresponding wrapper
     * object.
     * @param  object    object or class
     * @param  method    name of the method
     * @param  types     data types as an array
     * @param  arguments arguments as an array of objects
     * @return the return value of the method as an object
     * @throws NoSuchMethodException
     *     If the method does not exist.
     * @throws IllegalAccessException
     *     In case of an access violation to the method.
     * @throws InvocationTargetException
     *     If call and/or execution of the method fails.
     */    
    
    public static Object invoke(Object object, String method, Class<?>[] types, Object[] arguments)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Objects.requireNonNull(object, "Invalid object [null]");        

        method = Accession.trim(method);
        if (method.isEmpty())
            throw new NoSuchMethodException();

        return Accession.getMethod(object, method, types).invoke(object, arguments);
    }
    
    /**
     * Returns a hash code of an object.
     * The method is limited to primitive fields (String, Date, Booolean,
     * Interger, Long, Double, Double, Short) to protect against recursions.
     * If errors occur during low-level access to a field, the value
     * {@code null} is used for this field (theoretical case).
     * @param  object object
     * @return a hash code value for this object.
     */
    public static int hashCode(Object object) {
        return Accession.hashCode(object, null);
    }
    
    /**
     * Returns a hash code of an object.
     * The method is limited to primitive fields (String, Date, Booolean,
     * Interger, Long, Double, Double, Short) to protect against recursions.
     * If errors occur during low-level access to a field, the value
     * {@code null} is used for this field (theoretical case).
     * @param  object object
     * @param  filter list of includes/excludes fields
     * @return a hash code value for this object.
     */
    public static int hashCode(Object object, Filter filter) {
        
        Object       value;
        Class<?>     type;
        FieldMatcher matcher;
        
        int          hash;
        
        if (object == null)
            return 0;

        matcher = new FieldMatcher(object, filter);
        
        hash = object.getClass().getSuperclass().hashCode();
        for (Field field : Accession.getFields(object)) {
            
            if (!matcher.validate(field))
                continue;

            type = field.getType();
            if (!type.equals(String.class)
                    && !type.equals(Date.class)
                    && !type.equals(Boolean.class)
                    && !type.equals(Integer.class)
                    && !type.equals(Long.class)
                    && !type.equals(Double.class)
                    && !type.equals(Short.class)
                    && !type.isPrimitive())
                continue;

            if (object instanceof Serializable
                    && ("serialVersionUID").equals(field.getName()))
                continue;
            
            try {value = field.get(object);
            } catch (Exception exception) {
                value = null;
            }
            
            hash = 31 *hash +(value == null ? 0 : value.hashCode());
        }

        return hash;
    }    
    
    /**
     * Indicates whether two are equal objects.
     * This is only true if both objects are of the same data type and both
     * contain the same fields of the same data type with the same values.
     * The method is limited to primitive fields (String, Date, Booolean,
     * Interger, Long, Double, Double, Short) to protect against recursions.
     * If errors occur during low-level access to a field, the value
     * {@code null} is used for this field (theoretical case).
     * @param  object1 object
     * @param  object2 object for comparison
     * @return {@code true} if both objects when compared to the primitive
     *         fields is the same
     */
    public static boolean equals(Object object1, Object object2) {
        return Accession.equals(object1, object2, null);
    }
    
    /**
     * Indicates whether two are equal objects.
     * This is only true if both objects are of the same data type and both
     * contain the same fields of the same data type with the same values.
     * The method is limited to primitive fields (String, Date, Booolean,
     * Interger, Long, Double, Double, Short) to protect against recursions.
     * If errors occur during low-level access to a field, the value
     * {@code null} is used for this field (theoretical case).
     * @param  object1 object
     * @param  object2 object for comparison
     * @param  filter  list of includes/excludes fields
     * @return {@code true} if both objects when compared to the primitive
     *         fields is the same
     */
    public static boolean equals(Object object1, Object object2, Filter filter) {
        
        Object       value1;
        Object       value2;
        Class<?>     type;
        FieldMatcher matcher;

        if (object1 == null
                && object2 == null)
            return true;
        if (object1 == null
                || object2 == null)
            return false;
        if (!object1.getClass().equals(object2.getClass()))
            return false;

        matcher = new FieldMatcher(object1, filter);
        
        for (Field field1 : Accession.getFields(object1)) {

            if (!matcher.validate(field1))
                continue;

            Field field2;
            try {field2 = Accession.getField(object2, field1.getName());
            } catch (NoSuchFieldException exception) {
                return false;
            }
            
            type = field1.getType();
            if (!type.equals(field2.getType()))
                return false;
            if (!type.equals(String.class)
                    && !type.equals(Date.class)
                    && !type.equals(Boolean.class)
                    && !type.equals(Integer.class)
                    && !type.equals(Long.class)
                    && !type.equals(Double.class)
                    && !type.equals(Short.class)
                    && !type.isPrimitive())
                continue;
            
            if ((object1 instanceof Serializable
                            && ("serialVersionUID").equals(field1.getName()))
                    || (object2 instanceof Serializable
                            && ("serialVersionUID").equals(field2.getName())))
                continue;

            try {value1 = field1.get(object1);
            } catch (Exception exception) {
                value1 = null;
            }
            try {value2 = field2.get(object2);
            } catch (Exception exception) {
                value2 = null;
            }

            if (value1 == null
                    && value2 == null)
                continue;
            if (value1 == null
                    || value2 == null)
                return false;
            if (!value1.equals(value2))
                return false;
        }
        
        return true;
    }

    /**
     * Creates a representative string for an object.
     * The method is limited to primitive fields (String, Date, Booolean,
     * Interger, Long, Double, Double, Short) to protect against recursions.
     * If errors occur during low-level access to a field, the field is
     * ignored (theoretical case).
     * @param  object object
     * @return textually represents of the object
     */
    public static String toString(Object object) {
        return Accession.toString(object, null);
    }
    
    /**
     * Creates a representative string for an object.
     * The method is limited to primitive fields (String, Date, Booolean,
     * Interger, Long, Double, Double, Short) to protect against recursions.
     * If errors occur during low-level access to a field, the field is
     * ignored (theoretical case).
     * @param  object object
     * @param  filter list of includes/excludes fields
     * @return textually represents of the object
     */
    public static String toString(Object object, Filter filter) {
        
        Object       value;
        Class<?>     type;
        FieldMatcher matcher;
        StringBuffer buffer;
        
        if (object == null)
            return String.valueOf(object);
        
        matcher = new FieldMatcher(object, filter);

        buffer = new StringBuffer();
        for (Field field : Accession.getFields(object)) {            
        
            if (!matcher.validate(field))
                continue;

            type = field.getType();
            if (!type.equals(String.class)
                    && !type.equals(Date.class)
                    && !type.equals(Boolean.class)
                    && !type.equals(Integer.class)
                    && !type.equals(Long.class)
                    && !type.equals(Double.class)
                    && !type.equals(Short.class)
                    && !type.isPrimitive())
                continue;
            
            if ((field.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) == (Modifier.FINAL | Modifier.STATIC))
                continue;
            
            if (object instanceof Serializable
                    && ("serialVersionUID").equals(field.getName()))
                continue;

            try {value = Accession.get(object, field.getName());
            } catch (Exception exception) {
                continue;
            }
            
            buffer.append(", ").append(field.getName()).append("=").append(value);
        }
        
        if (buffer.length() > 0)
            buffer = new StringBuffer(buffer.toString().substring(2));
        
        return object.getClass().getSimpleName() + " [" + buffer + "]";
    }

    /** Internal class to implement filter for excludes and includes. */
    private static class Filter {
        
        /** filter entries */
        private List<Object> entries;

        /**
         * Constructor, creats a new Filer object.
         * @param entries entries
         */
        private Filter(Object... entries) {
            
            this.entries = new ArrayList<>();
            if (entries == null)
                return;            
            this.add(entries);
        }
        
        /**
         * Returns {@code true} if this filter contains the entry.
         * @param  entry entry
         * @return {@code true} if this filter contains the entry
         */
        public boolean contains(Object entry) {
            return this.entries.contains(entry);
        }

        /**
         * Adds one or more filter entries.
         * Duplicates and {@code null} are ignored.
         * @param entries entries
         */
        public void add(Object... entries) {

            if (entries == null)
                return;
            for (Object object : entries)
                if (object != null
                        && !this.entries.contains(object))
                    this.entries.add(object);
        }

        /**
         * Removes one or more filter entries.
         * Duplicates, non-existent entries and {@code null} are ignored.
         * @param entries entries
         */
        public void remove(Object... entries) {

            if (entries == null)
                return;
            for (Object object : entries)
                if (object != null)
                        this.entries.remove(object);
        }

        /**
         * Lists all contained entries.
         * @return all contained entries
         */
        public Object[] list() {
            return this.entries.toArray(new Object[0]);
        }
        
        /** Removes all all contained entries. */
        public void clear() {
            this.entries.clear();
        }
        
        /**
         * Returns the number of entries.
         * @return number of entries
         */
        public int size() {
            return this.entries.size();
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.entries);
        }
    }
    
    /** Filter for excludes */
    public static class Exclude extends Filter {

        /**
         * Constructor, creats a new Exclude object.
         * @param excludes exclude(s)
         */
        public Exclude(Object... excludes) {
            super(excludes);
        }
    }
    
    /** Filter for excludes */
    public static class AutoExclude extends Exclude {

        /**
         * Constructor, creats a new AutoExclude object.
         * @param excludes exclude(s)
         */
        public AutoExclude(Object... excludes) {
            super(excludes);
        }
    }
    
    /** Filter for includes */
    public static class Include extends Filter {

        /**
         * Constructor, creats a new Include object.
         * @param includes include(s)
         */
        public Include(Object... includes) {
            super(includes);
        }
    }
    
    /** Filter for includes */
    public static class AutoInclude extends Include {

        /**
         * Constructor, creats a new AutoInclude object.
         * @param includes include(s)
         */
        public AutoInclude(Object... includes) {
            super(includes);
        }
    }
    
    /** Internal matcher for fields, based on {@link Filter} */
    private static class FieldMatcher {
        
        /** list of fields */
        private List<Field> fields;
        
        /** type of filter */
        private Filter filter;
        
        /** 
         * Constructor, creates a new FieldMatcher object.
         * @param object object
         * @param filter filter
         */
        private FieldMatcher(Object object, Filter filter) {
            
            this.filter = filter;
            this.fields = new ArrayList<>();
            if (filter == null)
                return;
            for (Object entry : filter.list()) {
                if (entry instanceof String)
                    try {entry = Accession.getField(object, (String)entry);
                    } catch (NoSuchFieldException exception) {
                        continue;
                    }
                if (entry instanceof Field)
                    filter.add((Field)entry);
            }
        }
        
        /**
         * Checks whether the field is allowed.
         * For the {@link Exclude} / {@link AutoExclude} that does not contain
         * the field, for the {@link Include} / {@link AutoInclude} the field
         * must be included.
         * @param  field field
         * @return {@code true} if the field is allowed
         */
        private boolean validate(Field field) {
            
            boolean contains;
            
            contains = this.fields.contains(field);
            if (this.filter instanceof Exclude
                    && !contains)
                return true;
            if (this.filter instanceof Include
                    && contains)
                return true;
            return false;
        }
    }
}