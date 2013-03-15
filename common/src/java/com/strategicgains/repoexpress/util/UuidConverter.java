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
package com.strategicgains.repoexpress.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

/**
 * Utility class to convert between a UUID and a short (22-character) string representation of it.
 * Implements a very efficient URL-safe base64 encoding/decoding algorithm to shorthen/expand the
 * UUID.
 * 
 * @author toddf
 * @since Mar 13, 2013
 */
public class UuidConverter
{
	private static final char[] C64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
	private static final int[] I256 = new int[256];
	static
	{
		Arrays.fill(I256, -1);
		for (int i = 0; i < C64.length; i++)
		{
			I256[C64[i]] = i;
		}
	}

	/**
	 * Given a UUID instance, return a short (22-character) string representation of it.
	 * 
	 * @param uuid a UUID instance.
	 * @return a short string representation of the UUID.
	 */
	public String shorten(UUID uuid)
	{
		byte[] bytes = toByteArray(uuid);
		return encodeBase64(bytes);
	}

	/**
	 * Given a UUID representation (either a short or long version), return a UUID from it.
	 * 
	 * @param uuidString a string representation of a UUID.
	 * @return a UUID
	 * @throws IllegalArgumentException if the uuidString is not a valid UUID representation.
	 */
	public UUID expand(String uuidString)
	{
		if (uuidString == null) throw new NullPointerException("Null UUID string");
		
		if (uuidString.length() > 22)
		{
			return UUID.fromString(uuidString);
		}

		byte[] bytes = decodeBase64(uuidString);
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.put(bytes, 0, 16);
		bb.clear();
		return new UUID(bb.getLong(), bb.getLong());
	}

	/**
	 * Extracts the bytes from a UUID instance in MSB, LSB order.
	 * 
	 * @param uuid
	 * @return the bytes from the UUID instance.
	 */
	private byte[] toByteArray(UUID uuid)
	{
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}

	private String encodeBase64(byte[] bytes)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	private byte[] decodeBase64(String s)
    {
		if (s == null) throw new NullPointerException("Cannot decode null string");
		
		if (s.isEmpty()) return new byte[0];
		
		byte[] bytes = new byte[16];
		char[] chars = s.toCharArray();
		
		for (int j = 0, i = 0; j < 15;)
		{
			
			int d = I256[chars[i++]] << 18 | I256[chars[i++]] << 12 | I256[chars[i++]] << 6 | I256[chars[i++]];
			
			bytes[j++] = (byte) (d >> 16);
			bytes[j++] = (byte) (d >> 8);
			bytes[j++] = (byte) j;
		}

	    return bytes;
    }
}
