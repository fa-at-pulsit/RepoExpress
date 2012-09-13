/**
 * (C)Copyright 2007 by PS Technology, Inc.  All rights reserved.
 */
package com.strategicgains.repoexpress.eclipselink.relations;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import com.strategicgains.repoexpress.eclipselink.ConventionBasedDescriptorParameters;
import com.strategicgains.repoexpress.eclipselink.ConventionBasedProject;
import com.strategicgains.repoexpress.eclipselink.ConventionBasedTopLinkDescriptorParameters;
import com.strategicgains.repoexpress.eclipselink.util.ClassUtils;

/**
 * RelationshipDefinition is a value object that describes a relationship 
 * between two objects.  Used in ConventionBasedTopLinkDescriptor.
 * 
 * @author Todd Fredrich
 * @since Apr 17, 2007
 */
public abstract class RelationshipDefinition
{
	// SECTION: INSTANCE VARIABLES

	private Class<?> describedClass;
	private String attributeName;
	private Class<?> relatedClass;
	private ConventionBasedTopLinkDescriptorParameters parameters;

	
	// SECTION: CONSTRUCTION

	public RelationshipDefinition(Class<?> describedClass, String attributeName, Class<?> relatedClass)
	{
		this.describedClass = describedClass;
		this.attributeName = attributeName;
		this.relatedClass = relatedClass;
		parameters = new ConventionBasedTopLinkDescriptorParameters();
	}

	// SECTION: ACCESSING

	/**
	 * @return the attributeName
	 */
	public String getAttributeName()
	{
		return attributeName;
	}
	
	public String getColumnName()
	{
		return parameters.getColumnName();
	}
	
	public Class<?> getDescribedClass()
	{
		return describedClass;
	}

	/**
	 * @return the foreignKeyName
	 */
	public String getForeignKeyName()
	{
		return parameters.getForeignKeyName();
	}
	
	public String getRelationForeignKeyName()
	{
		return parameters.getRelationForeignKeyName();
	}
	
	public String getRelationTableName()
	{
		return parameters.getRelationTableName();
	}

	public ConventionBasedDescriptorParameters getParameters()
	{
		return parameters;
	}

	/**
	 * @return the relatedClass
	 */
	public Class<?> getRelatedClass()
	{
		return relatedClass;
	}
	
	public String getRelatedClassName()
	{
		return getRelatedClass().getSimpleName();
	}
	
	public String getTargetPrimaryKeyName()
	{
		return parameters.getTargetPrimaryKeyName();
	}
	
	public Class<?> getCollectionClass()
	{
		return parameters.getCollectionClass();
	}
	
	public Class<?> getMapClass()
	{
		return parameters.getMapClass();
	}
	
	public String getAccessorMethodName()
	{
		return parameters.getAccessorMethodName();
	}
	
	public Class<?> getFieldType()
	{
		Field mappedField = ClassUtils.getDeclaredField(getDescribedClass(), getAttributeName());
		Class<?> result = null;
		
		if (mappedField != null)
		{
	    	result = mappedField.getType();
		}
		else
		{
			Assert.fail(getDescribedClass().getSimpleName() + "." + getAttributeName() + " does not exist.");
		}

		return result;
	}
	
	
	// SECTION: TESTING
	
	public boolean hasForeignKeyName()
	{
		return parameters.hasForeignKeyName();
	}

	public boolean hasRelationForeignKeyName()
	{
		return parameters.hasRelationForeignKeyName();
	}

	public boolean hasRelationTableName()
	{
		return parameters.hasRelationTableName();
	}
	
	public boolean hasColumnName()
	{
		return parameters.hasColumnName();
	}
	
	public boolean hasTargetPrimaryKeyName()
	{
		return parameters.hasTargetPrimaryKeyName();
	}
	
	public boolean hasCollectionClass()
	{
		return parameters.hasCollectionClass();
	}

	public boolean hasMapClass()
	{
		return parameters.hasMapClass();
	}
	
	public boolean hasAccessorMethodName()
	{
		return parameters.hasAccessorMethodName();
	}

	public boolean isOwned()
	{
		return parameters.isOwned();
	}
    
    public boolean isReadOnly()
    {
    	return parameters.isReadOnly();
    }

    public boolean isBackReferenceRelationship()
    {
    	return false;
    }

    public boolean isOneToOneRelationship()
    {
    	return false;
    }

    public boolean isOneToManyRelationship()
    {
    	return false;
    }

    public boolean isManyToManyRelationship()
    {
    	return false;
    }
    
    public boolean usesBasicIndirection()
    {
    	return ValueHolderInterface.class.isAssignableFrom(getFieldType());
    }
	
	public boolean usesTransparentMap()
	{
		return Map.class.isAssignableFrom(getFieldType());
	}
	
	public boolean usesTransparentList()
	{
		return List.class.isAssignableFrom(getFieldType());
	}
	
	public boolean usesTransparentSet()
	{
		return Set.class.isAssignableFrom(getFieldType());
	}
	
	public boolean usesTransparentCollection()
	{
		return Collection.class.isAssignableFrom(getFieldType());
	}
    
    // SECTION: CONVERSION
    
    public <T extends ForeignReferenceMapping> T asTopLinkMapping(ConventionBasedProject project)
    {
    	T map = this.<T>newMappingInstance();
    	configureMappingDefaults(map);
    	configureIndirection(map);
    	return map;
    }
    
    protected void configureMappingDefaults(ForeignReferenceMapping map)
    {
		map.setAttributeName(getAttributeName());
		map.setReferenceClass(getRelatedClass());
		map.setIsPrivateOwned(isOwned());
        map.setIsReadOnly(isReadOnly());
    }
    
    protected abstract void configureIndirection(ForeignReferenceMapping map);
	protected abstract <T extends ForeignReferenceMapping> T newMappingInstance();
}
