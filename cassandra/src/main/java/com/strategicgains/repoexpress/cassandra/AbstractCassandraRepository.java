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
package com.strategicgains.repoexpress.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

/**
 * Uses Cassandra as its back-end store, supporting compound-identifier based tables.
 * <p/>
 * Since Cassandra is a little different than say, MongoDB, this repository doesn't do much.
 * Object mapping, CQL queries, etc. are left as an exercise for the implementor.
 * <p/>
 * Sub-classes must implement the, createEntity(), updateEntity(), readEntityById(), exists()
 * and deleteEntity() abstract methods. Along with any other custom-query-type methods.
 * 
 * @author toddf
 * @since Apr 12, 2013
 */
public abstract class AbstractCassandraRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
{
	private Session session;
	private String table;

	/**
	 * @param session a pre-configured Session instance.
	 * @param tableName the name of the Cassandra table entities are stored in.
	 */
    public AbstractCassandraRepository(Session session, String tableName)
	{
		super();
		this.session = session;
		this.table = tableName;
	}
    
    protected Session getSession()
    {
    	return session;
    }
    
    protected String getTable()
    {
    	return table;
    }

	@Override
	public T doCreate(T entity)
	{
		if (exists(entity.getId()))
		{
			throw new DuplicateItemException(entity.getClass().getSimpleName()
			    + " ID already exists: " + entity.getId().toString());
		}

		return createEntity(entity);
	}

	@Override
	public T doRead(Identifier id)
	{
		T item = readEntityById(id);

		if (item == null)
		{
			throw new ItemNotFoundException("ID not found: " + id.toString());
		}

		return item;
	}

	@Override
	public T doUpdate(T entity)
	{
		if (!exists(entity.getId()))
		{
			throw new ItemNotFoundException(entity.getClass().getSimpleName()
			    + " ID not found: " + entity.getId().toString());
		}

		return updateEntity(entity);
	}

	@Override
	public void doDelete(T entity)
	{
		try
		{
			deleteEntity(entity);
		}
		catch (InvalidObjectIdException e)
		{
			throw new ItemNotFoundException("ID not found: " + entity.getId().toString());
		}
	}

	protected void bindIdentifier(BoundStatement bs, Identifier identifier)
	{
		bs.bind(identifier.components().toArray());
	}

	protected abstract T readEntityById(Identifier identifier);
	protected abstract T createEntity(T entity);
	protected abstract T updateEntity(T entity);
	protected abstract void deleteEntity(T entity);
}
