package com.strategicgains.repoexpress.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class UuidShortenerTest
{
	private String longUuid = "00993542-ba2f-4d9f-82bf-0000cd938f95";
	private String shortUuid = "AJk1QrovTZ+CvwAAzZOPlQ";
	private UUID uuid = UUID.fromString(longUuid);
	private UuidConverter converter = new UuidConverter();

	@Test
	public void shouldShortenUuid()
	{
		String shortened = converter.shorten(uuid);
		assertEquals(shortUuid, shortened);
	}

	@Test
	public void shouldExpandShortUuid() throws IOException
	{
		UUID expanded = converter.expand(shortUuid);
		assertEquals(uuid, expanded);
	}

	@Test
	public void shouldExpandLongUuid() throws IOException
	{
		UUID expanded = converter.expand(longUuid);
		assertEquals(uuid, expanded);
	}
	
	@Test
	public void shouldDoSomethingHereToo()
	{
		String base64 = "b8tRS7h4TJ2Vt43Dp85v2A";
		String name = "6fcb514b-b878-4c9d-95b7-8dc3a7ce6fd8";
		UUID expect = UUID.fromString(name);

		String shortened = converter.shorten(expect);
		assertEquals(base64, shortened);

		UUID expanded = converter.expand(name);
		assertEquals(expect, expanded);
		
		expanded = converter.expand(base64);
		assertEquals(expect, expanded);
	}
}
