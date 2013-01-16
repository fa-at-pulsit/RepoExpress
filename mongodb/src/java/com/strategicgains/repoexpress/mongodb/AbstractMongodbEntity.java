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
package com.strategicgains.repoexpress.mongodb;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Id;
import com.strategicgains.repoexpress.domain.AbstractTimestampedIdentifiable;

/**
 * @author toddf
 * @since Oct 27, 2011
 */
public abstract class AbstractMongodbEntity
extends AbstractTimestampedIdentifiable
{
	@Id
	private ObjectId id;

	@Override
	public String getId()
	{
		return (id == null ? null : id.toString());
	}

	@Override
	public void setId(String idString)
	{
		this.id = (idString ==null ? null : new ObjectId(idString));
	}
	
	public ObjectId getObjectId()
	{
		return id;
	}
}
