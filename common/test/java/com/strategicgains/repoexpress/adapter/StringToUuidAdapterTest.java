package com.strategicgains.repoexpress.adapter;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.UUID;

import org.junit.Test;

import sun.misc.BASE64Encoder;

import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

public class StringToUuidAdapterTest
{
	private String uuidString = "00993542-ba2f-4d9f-82bf-0000cd938f95";
	private String uuidEncoded = "AJk1QrovTZ+CvwAAzZOPlQ";
	private UUID uuid = UUID.fromString(uuidString);
	private IdentiferAdapter<UUID> adapter = new StringToUuidAdapter();

	@Test
	public void shouldConvertStringToUuid()
	{
		UUID result = adapter.convert(uuid.toString());
		assertEquals(uuid, result);
	}

	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowInvalidObjectIdException()
	{
		UUID result = adapter.convert("abcde");
		assertEquals(uuid, result);
	}

	@Test
	public void shouldConvertBase64EncodedStringToUuid()
	{
		BASE64Encoder encoder = new BASE64Encoder();
//		System.out.println("UUID:   " + uuidString);
		String encoded = encoder.encode(uuidString.getBytes(Charset.forName("UTF-8")));
//		System.out.println("BASE64: " + encoded);
		assertEquals(uuidEncoded, encoded);
		UUID result = adapter.convert(encoded);
		assertEquals(uuid, result);
	}
}
