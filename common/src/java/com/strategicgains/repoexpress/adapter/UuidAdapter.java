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
package com.strategicgains.repoexpress.adapter;

import java.util.UUID;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.util.UuidConverter;

/**
 * Adapts (converts) between a UUID Type 3 format (or URL-safe Base64 encoded Type 3 format) and
 * an Identifier instance. Also, does the same for converting UUIDs to String formats.
 * 
 * @author toddf
 * @since Mar 11, 2013
 */
public class UuidAdapter
implements IdentifierAdapter
{
	@Override
    public Identifier parse(String id)
    throws InvalidObjectIdException
    {
		if (id == null || id.isEmpty()) throw new InvalidObjectIdException("Identifier must not be null");

		try
		{
			return new Identifier(UuidConverter.parse(id));
		}
		catch(IllegalArgumentException e)
		{
			throw new InvalidObjectIdException(e);
		}
    }

	/**
	 * Convert the Identifier into a string representation.
	 * 
	 * @param id an Identifier.
	 */
	@Override
    public String format(Identifier id)
    {
		return format(id, false);
    }

	/**
	 * Format the Identifier as a string representation of a UUID, optionally shortening it
	 * via URL-safe Base64 encoding.
	 * 
	 * @param id an Identifier containing a UUID.
	 * @param shorten if true, URL-safe Base64 encode the UUID string.
	 * @return a String or null (if the Identifier is null).
	 */
    public String format(Identifier id, boolean shorten)
    {
    	if (id == null) return null;

    	return format((UUID) id.primaryKey(), shorten);
    }

    /**
     * Convert the UUID into a string representation.
     * @param uuid
     * @return
     */
	public String format(UUID uuid)
	{
		return format(uuid, false);
	}

	/**
	 * Format the UUID as a string representation of a UUID, optionally shortening it
	 * via URL-safe Base64 encoding.
	 * 
	 * @param uuid a UUID.
	 * @param shorten if true, URL-safe Base64 encode the UUID string.
	 * @return a String or null (if the UUID is null).
	 */
	public String format(UUID uuid, boolean shorten)
    {
    	if (shorten)
		{
    		return (uuid == null ? null : UuidConverter.format(uuid));
		}
    	else
    	{
    		return (uuid == null ? null : uuid.toString());
    	}
    }
}
