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

import static org.junit.Assert.assertNotNull;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * @author toddf
 * @since Mar 24, 2011
 */
public class ObjectIdAdapterTest
{
	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowExceptionOnInvalidId()
	{
		new ObjectIdAdapter().convert(new Identifier("invalid"));
	}

	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowExceptionOnNullId()
	{
		new ObjectIdAdapter().convert(null);
	}

	@Test
	public void shouldConvertToId()
	{
		String stringValue = new ObjectId().toString();
		ObjectId objectId = new ObjectIdAdapter().convert(new Identifier(stringValue));
		assertNotNull(objectId);
	}
}
