/*
 * Copyright 2011, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.repoexpress;

/**
 * Defines the interface to adapt a String identifer to an object.
 * 
 * @author toddf
 * @since May 27, 2011
 * @param <I>
 */
public interface Adaptable<I>
{
	public I adaptId(String id);
}