/*
    Copyright 2011, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.repoexpress.redis;

import redis.clients.johm.Id;

import com.strategicgains.repoexpress.domain.AbstractTimestampedIdentifiable;

/**
 * A base object representing an entity stored in Redis.  The ID is a Long that has the potential
 * to be null.  getId() and setId() operate on String instances.  getObjectId() will return the
 * underlying Long object (wrapped in a new Long instance or null, so it's immutable).
 * 
 * @author toddf
 * @since June 18, 2012
 */
public abstract class AbstractTimestampedRedisEntity
extends AbstractTimestampedIdentifiable
{
	@Id
	private Long id;

	@Override
	public String getId()
	{
		return (id == null ? null : String.valueOf(id));
	}

	@Override
	public void setId(String idString)
	{
		this.id = (idString ==null ? null : Long.valueOf(idString));
	}
	
	public Long getObjectId()
	{
		return (id == null ? null : new Long(id.longValue()));
	}
}
