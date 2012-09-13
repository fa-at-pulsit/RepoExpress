package com.strategicgains.repoexpress.eclipselink.relations;

import org.eclipse.persistence.mappings.ManyToManyMapping;

/**
 * @author toddf
 * @since Aug 28, 2007
 */
public class ManyToManyRelationship
extends CollectionRelationshipDefinition
{
	// SECTION: CONSTRUCTOR

	/**
	 * @param describedClass
	 * @param attributeName
	 * @param relatedClass
	 */
	public ManyToManyRelationship(Class<?> describedClass, String attributeName, Class<?> relatedClass)
	{
		super(describedClass, attributeName, relatedClass);
	}


	// SECTION: TESTING
	
	@Override
	public boolean isManyToManyRelationship()
	{
		return true;
	}
	

	// SECTION: SUBCLASS RESPONSIBILITIES

	@Override
	protected ManyToManyMapping newMappingInstance()
	{
		return new ManyToManyMapping();
	}
}
