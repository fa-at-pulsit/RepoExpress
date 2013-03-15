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

import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * Accepts an ID string in UUID Type 3 format (or Base64 encoded Type 3 format) and
 * converts it to a UUID instance.
 * 
 * @author toddf
 * @since Mar 11, 2013
 */
public class StringToUuidAdapter
implements IdentiferAdapter<UUID>
{
	@Override
    public UUID convert(String id)
    throws InvalidObjectIdException
    {
		try
		{
			return UUID.fromString(id);
		}
		catch(IllegalArgumentException e)
		{
			throw new InvalidObjectIdException(e);
		}
    }
}
