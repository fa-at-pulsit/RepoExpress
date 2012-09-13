package com.strategicgains.repoexpress.eclipselink;

import java.util.HashMap;

/**
 * @author toddf
 * @since October 31, 2007
 */
public class ConventionBasedTopLinkDescriptorParameters
implements ConventionBasedDescriptorParameters
{
	// INSTANCE VARIABLES
	private HashMap<RelationParameterEnum, Object>parameterMap;
	
	
	// CONSTRUCTING
	
	public ConventionBasedTopLinkDescriptorParameters()
	{
		super();
		parameterMap = new HashMap<RelationParameterEnum, Object>();
	}


	// ACCESSING
	
	public String getColumnName()
	{
		return (String) parameterMap.get(RelationParameterEnum.ColumnName);
	}
	
	public ConventionBasedDescriptorParameters setColumnName(String columnName)
	{
		parameterMap.put(RelationParameterEnum.ColumnName, columnName);
		return this;
	}

	/**
	 * @return the foreignKeyName
	 */
	public String getForeignKeyName()
	{
		return (String) parameterMap.get(RelationParameterEnum.ForeignKeyName);
	}
	
	public ConventionBasedDescriptorParameters setForeignKeyName(String foreignKeyName)
	{
		parameterMap.put(RelationParameterEnum.ForeignKeyName, foreignKeyName);
		return this;
	}
	
	public String getRelationForeignKeyName()
	{
		return (String) parameterMap.get(RelationParameterEnum.RelationForeignKeyName);
	}
	
	public ConventionBasedDescriptorParameters setRelationForeignKeyName(String keyName)
	{
		parameterMap.put(RelationParameterEnum.RelationForeignKeyName, keyName);
		return this;
	}
	
	public String getRelationTableName()
	{
		return (String) parameterMap.get(RelationParameterEnum.RelationTableName);
	}
	
	public ConventionBasedDescriptorParameters setRelationTableName(String tableName)
	{
		parameterMap.put(RelationParameterEnum.RelationTableName, tableName);
		return this;
	}
	
	public String getTargetPrimaryKeyName()
	{
		return (String) parameterMap.get(RelationParameterEnum.TargetPrimaryKeyName);
	}
	
	public ConventionBasedDescriptorParameters setTargetPrimaryKeyName(String keyName)
	{
		parameterMap.put(RelationParameterEnum.TargetPrimaryKeyName, keyName);
		return this;
	}
	
	public Class getCollectionClass()
	{
		return (Class) parameterMap.get(RelationParameterEnum.CollectionClass);
	}
	
	public ConventionBasedDescriptorParameters setCollectionClass(Class collectionClass)
	{
		parameterMap.put(RelationParameterEnum.CollectionClass, collectionClass);
		return this;
	}
	
	public Class getMapClass()
	{
		return (Class) parameterMap.get(RelationParameterEnum.MapClass);
	}
	
	public ConventionBasedDescriptorParameters setMapClass(Class mapClass)
	{
		parameterMap.put(RelationParameterEnum.MapClass, mapClass);
		return this;
	}
	
	public String getAccessorMethodName()
	{
		return (String) parameterMap.get(RelationParameterEnum.AccessorMethodName);
	}
	
	public ConventionBasedDescriptorParameters setAccessorMethodName(String methodName)
	{
		parameterMap.put(RelationParameterEnum.AccessorMethodName, methodName);
		return this;
	}
	
	//TESTING
	
	public boolean hasForeignKeyName()
	{
		return (getForeignKeyName() != null);
	}

	public boolean hasRelationForeignKeyName()
	{
		return (getRelationForeignKeyName() != null);
	}

	public boolean hasRelationTableName()
	{
		return (getRelationTableName() != null);
	}
	
	public boolean hasColumnName()
	{
		return (getColumnName() != null);
	}
	
	public boolean hasTargetPrimaryKeyName()
	{
		return (getTargetPrimaryKeyName() != null);
	}
	
	public boolean hasCollectionClass()
	{
		return (getCollectionClass() != null);
	}

	public boolean hasMapClass()
	{
		return (getMapClass() != null);
	}
	
	public boolean hasAccessorMethodName()
	{
		return (getAccessorMethodName() != null);
	}

	/**
	 * @return the isOwned
	 */
	public boolean isOwned()
	{
		Boolean parameter = (Boolean) parameterMap.get(RelationParameterEnum.isOwned);
		return (parameter == null ? false : parameter.booleanValue());
	}
	
	public ConventionBasedDescriptorParameters setOwned()
	{
		parameterMap.put(RelationParameterEnum.isOwned, Boolean.TRUE);
		return this;
	}
    
    public boolean isReadOnly()
    {
        Boolean parameter = (Boolean) parameterMap.get(RelationParameterEnum.isReadOnly);
        return (parameter == null ? false : parameter.booleanValue());
    }
    
    public ConventionBasedDescriptorParameters setReadOnly()
    {
    	parameterMap.put(RelationParameterEnum.isReadOnly, Boolean.TRUE);
		return this;
    }
}
