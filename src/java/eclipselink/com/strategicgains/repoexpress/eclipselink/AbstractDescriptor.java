/*
	Copyright 2012 Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.eclipselink;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;

/**
 * A template EclipseLink descriptor that provides convenience methods for
 * configuring descriptors of various types, as well as a structure to
 * configure object-relational-mappings and various descriptor policies.
 * 
 * @author toddf
 * @since Sep 5, 2012
 */
@SuppressWarnings("serial")
public abstract class AbstractDescriptor
extends RelationalDescriptor
{
	// SECTION: CONSTRUCTORS

	public AbstractDescriptor()
	{
		super();
		configure();
	}


	// SECTION: CONFIGURATION

	protected void configure()
	{
		configureBasics();
		configureCachingPolicy();
		configureLockingPolicy();
		configureQueryManager();
		configureEventManager();
		configureMappings();
		configureCacheSynchronization();
	}

	protected void configureBasics()
	{
	}

	protected void configureCachingPolicy()
	{
	}

	protected void configureLockingPolicy()
	{
	}

	protected void configureQueryManager()
	{
	}

	protected void configureEventManager()
	{
	}

	protected void configureMappings()
	{
	}

	protected void configureCacheSynchronization()
	{
	}


	// SECTION: CONVENIENCE
	
	/**
	 * Map an entity property directly to a database column.
	 * 
	 * @param attributeName the entity property/attribute name.
	 * @param databaseFieldName the database column name.
	 * @return the resulting added mapping for later augmentation, if necessary.
	 */
	protected DirectToFieldMapping addDirectToFieldMapping(String attributeName, String databaseFieldName)
	{
		DirectToFieldMapping mapping = newDirectToFieldMapping(attributeName, databaseFieldName);
		addMapping(mapping);
		return mapping;
	}

	/**
	 * Map an entity property directly to a database column as a read-only property (does not get updated).
	 * 
	 * @param attributeName the entity property/attribute name.
	 * @param fieldName the database column name.
	 * @return the resulting DatabaseMapping object for later augmentation, if necessary.
	 */
	protected DatabaseMapping addReadOnlyDirectMapping(String attributeName, String fieldName)
	{
		DatabaseMapping mapping = addDirectMapping(attributeName, fieldName);
		mapping.readOnly();
		return mapping;
	}

	/**
	 * Map an entity property directly to a database column, where the entity property requires conversion to and
	 * from the database field type.  For example, properties of type, ??? require conversion.
	 * 
	 * @param attributeName the entity property/attribute name.
	 * @param databaseFieldName the database column name.
	 * @param converter the property converter.
	 * @return the resulting added mapping for later augmentation, if necessary.
	 * @see addYesNoBooleanConversionMapping
	 * @see addOneZeroBooleanConversionMapping
	 */
	protected DirectToFieldMapping addDirectToFieldConversionMapping(String attributeName, String databaseFieldName, Converter converter)
	{
		DirectToFieldMapping mapping = newDirectToFieldMapping(attributeName, databaseFieldName);
		mapping.setConverter(converter);
		addMapping(mapping);
		return mapping;
	}

	/**
	 * Map an entity relationship as a one-to-one relationship using a foreign key.
	 * 
	 * @param attributeName the entity property/attribute name.
	 * @param targetClass the class of the related object.
	 * @param foreignKeyFieldName the database column name that represents the relationship in this entity's table.
	 * @param targetPrimaryKeyFieldName the column name for the primary key of the related entity's table. 
	 * @return the resulting added mapping for later augmentation, if necessary.
	 */
	protected OneToOneMapping addForeignKeyOneToOneMapping(String attributeName, Class<?> targetClass, String foreignKeyFieldName,
	    String targetPrimaryKeyFieldName)
	{
		OneToOneMapping mapping = newForeignKeyOneToOneMapping(attributeName, targetClass, foreignKeyFieldName, targetPrimaryKeyFieldName);
		addMapping(mapping);
		return mapping;
	}

	/**
	 * Map an entity relationship as a one-to-many relationship using a foreign key.
	 * 
	 * @param attributeName the entity property/attribute name.
	 * @param targetClass the class of the related object.
	 * @param foreignKeyFieldName the database column name that represents the relationship in this entity's table.
	 * @param targetPrimaryKeyFieldName the column name for the primary key of the related entity's table. 
	 * @return the resulting added mapping for later augmentation, if necessary.
	 */
	protected OneToManyMapping addForeignKeyOneToManyMapping(String attributeName, Class<?> targetClass, String foreignKeyFieldName,
	    String targetPrimaryKeyFieldName)
	{
		OneToManyMapping mapping = newForeignKeyOneToManyMapping(attributeName, targetClass, foreignKeyFieldName, targetPrimaryKeyFieldName);
		mapping.useTransparentList();
		addMapping(mapping);
		return mapping;
	}

	/**
	 * Map a boolean entity property to a database column containing "Y" (true), "N" (false), or "" (false).
	 * 
	 * @param attributeName the name of the entity property/attribute (a boolean type).
	 * @param databaseFieldName the name of the database column (typically varchar2(1)).
	 * @return the resulting added mapping for later augmentation, if necessary.
	 */
	protected DirectToFieldMapping addYesNoBooleanConversionMapping(String attributeName, String databaseFieldName)
	{
		return addDirectToFieldConversionMapping(attributeName, databaseFieldName, newYesNoConverter());
	}

	/**
	 * Map a boolean entity property to a database column containing "0" (for false) or "1" (for true).
	 * 
	 * @param attributeName the name of the entity property/attribute (a boolean type).
	 * @param databaseFieldName the name of the database column (typically number(1)).
	 * @return the resulting added mapping for later augmentation, if necessary.
	 */
	protected DirectToFieldMapping addOneZeroBooleanConversionMapping(String attributeName, String databaseFieldName)
	{
		return addDirectToFieldConversionMapping(attributeName, databaseFieldName, newOneZeroConverter());
	}

	protected DirectToFieldMapping newDirectToFieldMapping(String attributeName, String databaseFieldName)
	{
		DirectToFieldMapping mapping = new DirectToFieldMapping();
		mapping.setAttributeName(attributeName);
		mapping.setFieldName(databaseFieldName);
		return mapping;
	}

	protected OneToOneMapping newForeignKeyOneToOneMapping(String attributeName, Class<?> targetClass, String foriegnKeyFieldName,
	    String targetPrimaryKeyFieldName)
	{
		OneToOneMapping mapping = new OneToOneMapping();
		mapping.setAttributeName(attributeName);
		mapping.setReferenceClass(targetClass);
		mapping.addForeignKeyFieldName(foriegnKeyFieldName, targetPrimaryKeyFieldName);
		mapping.useBasicIndirection();
		return mapping;
	}

	protected OneToManyMapping newForeignKeyOneToManyMapping(String attributeName, Class<?> targetClass, String foreignKeyFieldName,
	    String targetPrimaryKeyFieldName)
	{
		OneToManyMapping mapping = new OneToManyMapping();
		mapping.setAttributeName(attributeName);
		mapping.setReferenceClass(targetClass);
		mapping.addTargetForeignKeyFieldName(foreignKeyFieldName, targetPrimaryKeyFieldName);
		return mapping;
	}

	protected ObjectTypeConverter newOneZeroConverter()
	{
		ObjectTypeConverter converter = new ObjectTypeConverter();
		converter.setDefaultAttributeValue(Boolean.FALSE);
		converter.addConversionValue("0", Boolean.FALSE);
		converter.addConversionValue("1", Boolean.TRUE);
		return converter;
	}

	protected ObjectTypeConverter newYesNoConverter()
	{
		ObjectTypeConverter converter = new ObjectTypeConverter();
		converter.setDefaultAttributeValue(Boolean.FALSE);
		converter.addConversionValue("", Boolean.FALSE);
		converter.addConversionValue("N", Boolean.FALSE);
		converter.addConversionValue("Y", Boolean.TRUE);
		return converter;
	}

	protected void useSendObjectChangesCacheSynchronization()
	{
		setCacheSynchronizationType(RelationalDescriptor.SEND_OBJECT_CHANGES);
	}

	protected void useInvalidateChangedObjectCacheSynchronization()
	{
		setCacheSynchronizationType(RelationalDescriptor.INVALIDATE_CHANGED_OBJECTS);
	}

	protected void useSendNewObjectsWithChangesCacheSynchronization()
	{
		setCacheSynchronizationType(RelationalDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES);
	}

	protected void useDoNotSendChangesCacheSynchronization()
	{
		setCacheSynchronizationType(RelationalDescriptor.DO_NOT_SEND_CHANGES);
	}
}
