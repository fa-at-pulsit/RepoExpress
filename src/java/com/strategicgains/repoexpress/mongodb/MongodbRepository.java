/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.strategicgains.repoexpress.mongodb;

import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

/**
 * Uses MongoDB as its back-end store. This repository can handle
 * "single-table inheritance" by passing all the supported types into the
 * constructor, with the inheritance root first.
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
	 * @param address a ServerAddress representing a single MongoDB server.
	 * @param name the name of the repository (in MongoDB).
	 * @param entityClasses Class(es) managed by this repository. Inheritance root first.
	 */
	public MongodbRepository(ServerAddress address, String name, Class<? extends T>... entityClasses)
	{
		super();
		mongo = new Mongo(address);
		initialize(name, entityClasses);
	}

	/**
	 * 
	 * @param bootstraps a list of ServerAddress that represent a Replication Set servers.
	 * @param name the name of the repository (in MongoDB).
	 * @param entityClasses Class(es) managed by this repository. Inheritance root first.
	 */
	public MongodbRepository(List<ServerAddress> bootstraps, String name, Class<? extends T>... entityClasses)
	{
		super();
		mongo = new Mongo(bootstraps);
		initialize(name, entityClasses);
	}

	@SuppressWarnings("unchecked")
	private void initialize(String name, Class<? extends T>... entityClasses)
	{
		morphia = new Morphia();
		inheritanceRoot = (Class<T>) entityClasses[0];

		for (Class<?> entityClass : entityClasses)
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
			throw new DuplicateItemException(item.getClass().getSimpleName()
			    + " ID already exists: " + item.getId());
		}

		datastore.save(item);
		return item;
	}

	@Override
	public T doRead(String id)
	{
		T item = datastore.get(inheritanceRoot, convertId(id));

		if (item == null)
		{
			throw new ItemNotFoundException("ID not found: " + id);
		}

		return item;
	}

	@Override
	public T doUpdate(T item)
	{
		if (!exists(item.getId()))
		{
			throw new ItemNotFoundException(item.getClass().getSimpleName()
			    + " ID not found: " + item.getId());
		}

		datastore.save(item);
		return item;
	}

	@Override
	public void doDelete(String id)
	{
		try
		{
			T item = doRead(id);
			datastore.delete(item);
		}
		catch (InvalidObjectIdException e)
		{
			throw new ItemNotFoundException("ID not found: " + id);
		}
	}

	// SECTION: UTILITY

	protected boolean exists(String id)
	{
		if (id == null) return false;

		return datastore.getCount(datastore.find(inheritanceRoot, "_id", new ObjectId(id))) > 0;
	}

	protected Datastore getDataStore()
	{
		return datastore;
	}
}
