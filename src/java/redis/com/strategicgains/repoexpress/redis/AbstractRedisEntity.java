/*
    Copyright 2012, Pearson eCollege. All rights reserved.
*/
package com.strategicgains.repoexpress.redis;

import com.strategicgains.repoexpress.domain.Identifiable;

/**
 * @author toddf
 * @since Jul 19, 2012
 */
public class AbstractRedisEntity
implements Identifiable
{
	private String id;

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
	}
}
