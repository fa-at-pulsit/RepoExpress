/* Copyright (c) 2007 - PSTechnology - All Rights Reserved */

package com.strategicgains.repoexpress.eclipselink.relations;

import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * @author toddf
 * @since August 28, 2007
 */
public class OneToOneRelationship
extends RelationshipDefinition
{
	// SECTION: CONSTRUCTION
	
	/**
	 * @param project
	 * @param describedClass
	 * @param attributeName
	 * @param relatedClass
	 */
	public OneToOneRelationship(Class<?> describedClass, String attributeName, Class<?> relatedClass)
	{
		super(describedClass, attributeName, relatedClass);
	}

	
	// SECTION: TESTING

	@Override
	public boolean isOneToOneRelationship()
	{
		return true;
	}
	

	// SECTION: SUBCLASS RESPONSIBILITIES

	@Override
	protected OneToOneMapping newMappingInstance()
	{
		return new OneToOneMapping();
	}
    
    protected void configureIndirection(ForeignReferenceMapping map)
    {
    	map.useBasicIndirection();
    }
}
