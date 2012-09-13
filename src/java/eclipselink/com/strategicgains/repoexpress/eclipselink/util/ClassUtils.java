/*
    Copyright 2008, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.repoexpress.eclipselink.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author toddf
 * @since Aug 18, 2008
 */
public abstract class ClassUtils
{
	// SECTION: CONSTANTS

	public static final int IGNORED_FIELD_MODIFIERS = Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT;

	
	// SECTION: CLASS UTILITIES

	/**
	 * Traverses from the given object up the inheritance hierarchy to list all the
	 * declared fields.
	 * 
	 * @param object
	 * @return
	 */
	public static List<Field> getAllDeclaredFields(Class<?> aClass)
	{
		return getAllDeclaredFields(aClass, IGNORED_FIELD_MODIFIERS);
	}

	public static List<Field> getAllDeclaredFields(Class<?> aClass, int ignoredModifiers)
	{
		FieldListClosure closure = new ClassUtils.FieldListClosure(new ArrayList<Field>(), ignoredModifiers);
		getAllDeclaredFields(aClass, closure);
		return closure.getValues();
	}
    
    public static HashMap<String, Field> getAllDeclaredFieldsByName(Class<?> aClass)
    {
    	return getAllDeclaredFieldsByName(aClass, IGNORED_FIELD_MODIFIERS);
    }

    public static HashMap<String, Field> getAllDeclaredFieldsByName(Class<?> aClass, int ignoredModifiers)
    {
		FieldHashMapClosure closure = new ClassUtils.FieldHashMapClosure(new HashMap<String, Field>(), ignoredModifiers);
		getAllDeclaredFields(aClass, closure);
		return closure.getValues();
    }

	/**
	 * Traverses the inheritance hierarchy to find a field matching the given attributeName.
	 * 
	 * @param describedClass the class to begin searching--traverse up the hierarchy from here.
	 * @param attributeName the name of the desired field.
	 * @return
	 */
    public static Field getDeclaredField(Class<?> aClass, String attributeName)
    {
    	return getDeclaredField(aClass, attributeName, IGNORED_FIELD_MODIFIERS);
    }

    public static Field getDeclaredField(Class<?> aClass, String attributeName, int ignoredModifiers)
    {
		FieldNameClosure closure = new ClassUtils.FieldNameClosure(attributeName, ignoredModifiers);
		getAllDeclaredFields(aClass, closure);
		return closure.getField();
    }

    
    // SECTION: UTILITY - PRIVATE

    private static void getAllDeclaredFields(Class<?> aClass, UnaryFunction function)
    {
    	for (Field field : aClass.getDeclaredFields())
    	{
    		try
            {
	            function.perform(field);
            }
            catch (FunctionException e)
            {
	            e.printStackTrace();
            }
    	}

    	if (aClass.getSuperclass() != null)
		{
			getAllDeclaredFields(aClass.getSuperclass(), function);
		}
    }
    

    // SECTION: INNER CLASSES
    
    private static class FieldListClosure
    implements UnaryFunction
    {
    	private List<Field> values;
    	private int ignoredModifiers;

    	public FieldListClosure(List<Field> values, int modifiers)
    	{
    		super();
    		this.values = values;
    		this.ignoredModifiers = modifiers;
    	}

        @Override
        public Object perform(Object argument)
        throws FunctionException
        {
        	Field field = (Field) argument;

        	if ((field.getModifiers() & ignoredModifiers) == 0)
        	{
        		values.add((Field) argument);
        	}

	        return null;
        }
        
        public List<Field> getValues()
        {
        	return values;
        }
    }
    
    private static class FieldHashMapClosure
    implements UnaryFunction
    {
    	private HashMap<String, Field> values;
    	private int ignoredModifiers;

    	public FieldHashMapClosure(HashMap<String, Field> values, int modifiers)
    	{
    		super();
    		this.values = values;
    		this.ignoredModifiers = modifiers;
    	}

        @Override
        public Object perform(Object argument)
        throws FunctionException
        {
        	Field field = (Field) argument;

        	if ((field.getModifiers() & ignoredModifiers) == 0)
        	{
        		values.put(field.getName(), field);
        	}

	        return null;
        }
        
        public HashMap<String, Field> getValues()
        {
        	return values;
        }
    }
    
    private static class FieldNameClosure
    implements UnaryFunction
    {
    	private String fieldName;
    	private int ignoredModifiers;
    	private Field field;

    	public FieldNameClosure(String fieldName, int modifiers)
    	{
    		super();
    		this.fieldName = fieldName;
    		this.ignoredModifiers = modifiers;
    	}

        @Override
        public Object perform(Object argument)
        throws FunctionException
        {
        	Field field = (Field) argument;

        	if (((field.getModifiers() & ignoredModifiers) == 0)
        		&& field.getName().equals(fieldName))
        	{
        		this.field = field;
        	}

	        return null;
        }

        public Field getField()
        {
        	return field;
        }
    }

	/**
	 * @param aClass
	 * @return
	 */
    public static boolean isAbstract(Class<?> aClass)
    {
    	return Modifier.isAbstract(aClass.getModifiers());
    }

	/**
	 * @param field
	 * @return
	 */
    public static boolean isFieldConstant(Field field)
    {
	    return Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers());
    }

	/**
	 * @param field
	 * @return
	 */
    public static boolean isFieldTransient(Field field)
    {
    	return Modifier.isTransient(field.getModifiers());
    }
}
