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
package com.strategicgains.repoexpress.event;

import java.util.UUID;

import com.strategicgains.repoexpress.domain.UuidIdentifiable;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * Assigns a UUID to an Identifiable entity before create.
 * 
 * @author toddf
 * @since Mar 11, 2013
 */
public class UuidIdentityRepositoryObserver<T extends UuidIdentifiable>
extends AbstractRepositoryObserver<T>
{
	@Override
	public void beforeCreate(T object)
	{
		super.beforeCreate(object);

		if (object.getUuid() == null)
		{
			object.setUuid(UUID.randomUUID());
		}
	}

	@Override
	public void beforeUpdate(T object)
	{
		if (object.getUuid() == null)
		{
			throw new InvalidObjectIdException("UUID required on update");
		}
	}
}
