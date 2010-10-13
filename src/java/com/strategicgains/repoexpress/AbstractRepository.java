/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.repoexpress;

import com.strategicgains.repoexpress.domain.Entity;


/**
 * @author toddf
 * @since Oct 13, 2010
 */
public abstract class AbstractRepository<T extends Entity>
implements Repository<T>
{
	// SECTION: UTILITY - PROTECTED

	protected boolean hasId(T item)
	{
		return (item.getId() != null && !item.getId().trim().isEmpty());
	}
}
