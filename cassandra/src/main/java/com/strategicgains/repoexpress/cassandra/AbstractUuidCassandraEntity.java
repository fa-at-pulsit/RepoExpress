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
package com.strategicgains.repoexpress.cassandra;

import java.util.UUID;

import com.strategicgains.repoexpress.domain.AbstractTimestampedIdentifiable;
import com.strategicgains.repoexpress.domain.UuidIdentifiable;
import com.strategicgains.repoexpress.util.UuidConverter;

/**
 * A Cassandra entity that is identified by a UUID as its primary identifier.
 * It also has createdAt and updatedAt properties.
 * 
 * @author toddf
 * @since Mar 18, 2013
 */
public abstract class AbstractUuidCassandraEntity
extends AbstractTimestampedIdentifiable
implements UuidIdentifiable
{
	private UUID id;

	@Override
	public String getId()
	{
		return (id == null ? null : UuidConverter.format(id));
//		return (id == null ? null : id.toString());
	}

	@Override
	public void setId(String idString)
	{
		this.id = (idString ==null ? null : UuidConverter.parse(idString));
//		this.id = (idString ==null ? null : UUID.fromString(idString));
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
