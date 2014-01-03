/*
    Copyright 2013, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.domain;

import java.util.UUID;

import com.strategicgains.repoexpress.domain.AbstractTimestampedIdentifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.domain.UuidIdentifiable;
import com.strategicgains.repoexpress.util.UuidConverter;

/**
 * An entity that is identified by a UUID as its primary identifier.
 * It also has createdAt and updatedAt properties.
 * 
 * @author toddf
 * @since Mar 18, 2013
 */
public abstract class AbstractUuidEntity
extends AbstractTimestampedIdentifiable
implements UuidIdentifiable
{
	private UUID id;

	@Override
	public Identifier getId()
	{
		return (id == null ? null : new Identifier(id));
	}

	/**
	 * Assumes the id is either null or empty, or has only one element: a valid UUID-formatted string.
	 * 
	 * @throws IllegalArgumentException - if the first component of the id is not a valid UUID representation.
	 */
	@Override
	public void setId(Identifier id)
	{
		this.id = (id ==null || id.isEmpty() ? null : UuidConverter.parse((String) id.primaryKey()));
	}

	@Override
    public UUID getUuid()
    {
	    return id;
    }

	@Override
    public void setUuid(UUID uuid)
    {
		this.id = uuid;
    }
}
