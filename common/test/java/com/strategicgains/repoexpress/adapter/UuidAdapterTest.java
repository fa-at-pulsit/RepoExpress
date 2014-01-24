package com.strategicgains.repoexpress.adapter;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;

public class UuidAdapterTest
{
	private String uuidString = "00993542-ba2f-4d9f-82bf-0000cd938f95";
	private String uuidEncoded = "AJk1QrovTZ-CvwAAzZOPlQ";
	private Identifier uuidId = new Identifier(UUID.fromString(uuidString));

	@Test
	public void shouldParseStringToUuid()
	{
		assertEquals(uuidId, Identifiers.UUID.parse(uuidString));
	}

	@Test(expected=InvalidObjectIdException.class)
	public void shouldThrowInvalidObjectIdException()
	{
		assertEquals(uuidId, Identifiers.UUID.parse("abcde"));
	}

	@Test
	public void shouldParseShortStringToUuid()
	{
		assertEquals(uuidId, Identifiers.UUID.parse(uuidEncoded));
	}

	@Test
	public void shouldFormatLongUuid()
	{
		assertEquals(uuidString, Identifiers.UUID.format(uuidId));
	}

	@Test
	public void shouldFormatShortUuid()
	{
		assertEquals(uuidEncoded, Identifiers.UUID.format(uuidId, true));
	}
}
