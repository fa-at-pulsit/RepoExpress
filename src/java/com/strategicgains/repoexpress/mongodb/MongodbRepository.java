/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.repoexpress.mongodb;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

/**
 * Uses MongoDB as its back-end store.  This repository can handle "single-table inheritance" by
 * passing all the supported types into the constructor, with the inheritance root first.
 * 
 * @author toddf
 * @since Aug 24, 2010
 */
public class MongodbRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
{
	private Mongo mongo;
	private Morphia morphia;
	private Datastore datastore;
	private Class<T> inheritanceRoot;
	
	/**
	 * 
	 * @param address 
	 * @param name the name of the repository (in MongoDB).
	 * @param entityClasses Class(es) managed by this repository.  Inheritance root first.
	 */
	public MongodbRepository(ServerAddress address, String name, Class<T>... entityClasses)
    {
	    super();
	    mongo = new Mongo(address);
	    morphia = new Morphia();
	    inheritanceRoot = entityClasses[0];
	    
	    for (Class<T> entityClass : entityClasses)
	    {
	    	morphia.map(entityClass);
	    }

	    datastore = morphia.createDatastore(mongo, name);
	    datastore.ensureIndexes();
	    datastore.ensureCaps();
    }

	@Override
	public T doCreate(T item)
	{
		if (exists(item.getId()))
		{
			throw new DuplicateItemException(item.getClass().getSimpleName() + " ID already exists: " + item.getId());
		}
		
		Key<T> key = datastore.save(item);
		return datastore.getByKey(inheritanceRoot, key);
	}
	
	private boolean exists(String id)
	{
		return datastore.getCount(datastore.find(inheritanceRoot, "_id ", id)) > 0;
	}

	@Override
	public T doRead(String id)
	{
		T remark = datastore.get(inheritanceRoot, id);
		
		if (remark == null)
		{
			throw new ItemNotFoundException("ID not found: " + id);
		}
		
		return remark;
	}

	@Override
	public void doUpdate(T item)
	{
		if(!exists(item.getId()))
		{
			throw new ItemNotFoundException(item.getClass().getSimpleName() + " ID not found: " + item.getId());
		}

		datastore.save(item);
	}

	@Override
	public void doDelete(String id)
	{
		T item = read(id);
		datastore.delete(item);
	}
}
