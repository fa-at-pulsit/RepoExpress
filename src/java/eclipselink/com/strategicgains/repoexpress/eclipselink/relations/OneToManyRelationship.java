package com.strategicgains.repoexpress.eclipselink.relations;

import org.eclipse.persistence.mappings.OneToManyMapping;

/**
 * @author toddf
 * @since Aug 28, 2007
 */
public class OneToManyRelationship
extends CollectionRelationshipDefinition
{
	// SECTION: CONSTRUCTION

	/**
	 * @param describedClass
	 * @param attributeName
	 * @param relatedClass
	 */
	public OneToManyRelationship(Class<?> describedClass, String attributeName, Class<?> relatedClass)
	{
		super(describedClass, attributeName, relatedClass);
	}


	// SECTION: TESTING
	
	@Override
	public boolean isOneToManyRelationship()
	{
		return true;
	}
	

	// SECTION: SUBCLASS RESPONSIBILITIES

	@Override
	protected OneToManyMapping newMappingInstance()
	{
		return new OneToManyMapping();
	}
}
