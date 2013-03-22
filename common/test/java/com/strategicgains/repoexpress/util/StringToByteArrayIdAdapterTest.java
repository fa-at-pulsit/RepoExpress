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
package com.strategicgains.repoexpress.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Test;

import com.strategicgains.repoexpress.adapter.StringToByteArrayIdAdapter;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

/**
 * @author toddf
 * @since Oct 25, 2011
 */
public class StringToByteArrayIdAdapterTest
{
	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowExceptionOnNullId()
	{
		new StringToByteArrayIdAdapter().convert(null);
	}

	@Test
	public void shouldConvertToId()
	{
		String stringValue = "something of a byte array 123456";
		byte[] objectId = new StringToByteArrayIdAdapter().convert(stringValue);
		assertNotNull(objectId);
		assertEquals(Arrays.toString(stringValue.getBytes(Charset.forName("UTF-8"))), Arrays.toString(objectId));
	}
}
