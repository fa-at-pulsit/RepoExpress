package com.strategicgains.repoexpress.eclipselink.relations;

/**
 * @author toddf
 * @since Aug 28, 2007
 */
public class BackReferenceRelationship
extends OneToOneRelationship
{
	/**
	 * @param describedClass
	 * @param attributeName
	 * @param relatedClass
	 */
	public BackReferenceRelationship(Class<?> describedClass, String attributeName, Class<?> relatedClass)
	{
		super(describedClass, attributeName, relatedClass);
	}

	@Override
	public boolean isBackReferenceRelationship()
	{
		return true;
	}
}
