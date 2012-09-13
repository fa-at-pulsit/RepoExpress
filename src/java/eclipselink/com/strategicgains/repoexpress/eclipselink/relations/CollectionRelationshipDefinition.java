package com.strategicgains.repoexpress.eclipselink.relations;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * @author toddf
 * @since October 30, 2007
 */
public abstract class CollectionRelationshipDefinition
extends RelationshipDefinition
{
	//SECTION: CONSTRUCTORS

	public CollectionRelationshipDefinition(Class<?> describedClass, String attributeName, Class<?> relatedClass)
	{
		super(describedClass, attributeName, relatedClass);
	}
	

	//SECTION: SUB-CLASS RESPONSIBILITIES

	protected void configureIndirection(ForeignReferenceMapping map)
	{
		configureIndirection((CollectionMapping) map);
	}

	private void configureIndirection(CollectionMapping map)
	{
		if (hasCollectionClass())
		{
			map.useCollectionClass(getCollectionClass());
		}
		else if (hasMapClass())
		{
			if (!hasAccessorMethodName())
			{
				String message = getDescribedClass().getSimpleName() + "." + getAttributeName()
					+ " is set to use Map class: " + getMapClass().getSimpleName()
					+ ", however, no AccessorMethodName was specified.";
				Assert.fail(message);
			}
			
			map.useMapClass(getMapClass(), getAccessorMethodName());
		}
		
		configureTransparentIndirection(map);		
	}

	private void configureTransparentIndirection(CollectionMapping map)
	{
		if (usesTransparentMap())
		{
			if (!hasAccessorMethodName())
			{
				String message = getDescribedClass().getSimpleName() + "." + getAttributeName()
					+ " is set to use transparent Map indirection."
					+ " However, no AccessorMethodName was specified.";
				Assert.fail(message);
			}

			map.useTransparentMap(getAccessorMethodName());
		}
		else if (usesTransparentList())
		{
			map.useTransparentList();
		}
		else if (usesTransparentSet())
		{
			map.useTransparentSet();
		}
		else if (usesTransparentCollection())
		{
			map.useTransparentCollection();
		}
	}
}
