package com.strategicgains.repoexpress.adapter;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

public class UuidAdapterTest
{
	private String uuidString = "00993542-ba2f-4d9f-82bf-0000cd938f95";
	private String uuidEncoded = "AJk1QrovTZ-CvwAAzZOPlQ";
	private UUID uuid = UUID.fromString(uuidString);
	private IdentiferAdapter<UUID> adapter = new UuidAdapter();

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
	public void shouldConvertShortStringToUuid()
	{
		UUID result = adapter.convert(uuidEncoded);
		assertEquals(uuid, result);
	}
}
