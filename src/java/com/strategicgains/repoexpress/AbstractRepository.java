/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.repoexpress;

import com.strategicgains.repoexpress.domain.Identifiable;


/**
 * @author toddf
 * @since Oct 13, 2010
 */
public abstract class AbstractRepository<T extends Identifiable>
implements Repository<T>
{
	// Convert an ID to an OID (or whatever) before reading.
	private IdentiferAdapter identifierAdapter = null;

    public IdentiferAdapter getIdentifierAdapter()
    {
    	return identifierAdapter;
    }
    
    public boolean hasIdentifierAdapter()
    {
    	return (identifierAdapter != null);
    }

    /**
     * 
     * @param adapter
     */
	public void setIdentifierAdapter(IdentiferAdapter adapter)
    {
    	this.identifierAdapter = adapter;
    }

	// SECTION: UTILITY - PROTECTED

	protected boolean hasId(T item)
	{
		return (item.getId() != null && !item.getId().trim().isEmpty());
	}
	
	protected Object convertId(String id)
	{
		if (hasIdentifierAdapter())
		{
			return getIdentifierAdapter().convert(id);
		}
		
		return id;
	}
}
