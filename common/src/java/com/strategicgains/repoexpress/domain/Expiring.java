package com.strategicgains.repoexpress.domain;

/**
 * @author seans
 * @since Aug 24, 2013
 */
public interface Expiring
{
	public int getTtlSeconds();
	public void setTtlSeconds(int seconds);
}
