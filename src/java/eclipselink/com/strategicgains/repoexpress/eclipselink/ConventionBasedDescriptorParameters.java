/**
 * 
 */
package com.strategicgains.repoexpress.eclipselink;

/**
 * This is a marker interface for implementation-specific settings of
 * convention-based descriptors.  Implementors of 
 * ConventionBasedDescriptor will have their corresponding implementation
 * of ConventionBasedDescriptorParameters.
 * 
 * @author toddf
 * @since November 1, 2007
 */
public interface ConventionBasedDescriptorParameters
{
	// ACCESSING

	public String getForeignKeyName();
	public boolean hasForeignKeyName();
	public ConventionBasedDescriptorParameters setForeignKeyName(String keyName);
	
	public String getColumnName();
	public boolean hasColumnName();
	public ConventionBasedDescriptorParameters setColumnName(String columnName);
	
	public String getRelationTableName();
	public boolean hasRelationTableName();
	public ConventionBasedDescriptorParameters setRelationTableName(String tableName);
	
	public String getRelationForeignKeyName();
	public boolean hasRelationForeignKeyName();
	public ConventionBasedDescriptorParameters setRelationForeignKeyName(String keyName);
	
	public String getTargetPrimaryKeyName();
	public boolean hasTargetPrimaryKeyName();
	public ConventionBasedDescriptorParameters setTargetPrimaryKeyName(String keyName);
	
	public Class<?> getCollectionClass();
	public boolean hasCollectionClass();
	public ConventionBasedDescriptorParameters setCollectionClass(Class<?> collectionClass);
	
	public Class<?> getMapClass();
	public boolean hasMapClass();
	public ConventionBasedDescriptorParameters setMapClass(Class<?> mapClass);
	
	public String getAccessorMethodName();
	public boolean hasAccessorMethodName();
	public ConventionBasedDescriptorParameters setAccessorMethodName(String methodName);
	
	public boolean isOwned();
	public ConventionBasedDescriptorParameters setOwned();
	
	public boolean isReadOnly();
	public ConventionBasedDescriptorParameters setReadOnly();
}
