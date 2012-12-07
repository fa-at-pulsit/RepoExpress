/*
    Copyright 2012, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.cassandra;

import java.util.UUID;

import com.strategicgains.repoexpress.domain.AbstractTimestampedIdentifiable;

/**
 * A base Cassandra entity type that uses a UUID as its ID (row-key).
 * 
 * @author toddf
 * @since Nov 20, 2012
 */
public class AbstractCassandraEntity
extends AbstractTimestampedIdentifiable
{
	// TODO: we need to ensure that the ID is populated--it is required.  Can it be auto-populated via Cassandra?
	private UUID id;

	/**
	 * Returns a string representation of the underlying UUID for this entity.
	 * 
	 * @return a string representation of the underlying UUID.  May be null.
	 */
	@Override
    public String getId()
    {
	    return (id == null ? null : id.toString());
    }

	/**
	 * Set the underlying UUID of this entity from a string representation.
	 * 
	 * @param idString must be a valid UUID string representation.
	 */
	@Override
    public void setId(String idString)
    {
		this.id = (idString == null ? null : UUID.fromString(idString));
    }

	/**
	 * Return the underlying UUID of this entity. May be null.
	 * 
	 * @return the underlying UUID for this entity. May be null.
	 */
	public UUID getObjectId()
	{
		return id;
	}

	/**
	 * Set the underlying UUID for this entity directly.
	 * 
	 * @param uuid is a valid UUID or null.
	 */
	public void setObjectId(UUID uuid)
	{
		this.id = uuid;
	}
}
