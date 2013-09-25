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

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

/**
 * Uses Cassandra as its back-end store. Since Cassandra is a little different
 * than say, MongoDB, this repository doesn't do much except connection
 * pooling and observer notification.  Object mapping, CQL queries, etc.
 * are left as an exercise for the implementor.
 * <p/>
 * Sub-classes must implement the, createEntity(), updateEntity(), readEntityById() and deleteEntity()
 * abstract methods. Along with any other custom-query-type methods.
 * 
 * @author toddf
 * @since Apr 12, 2013
 */
public abstract class AbstractCassandraRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
{
	private static final String EXISTENCE_CQL = "select count(*) from %s where %s = %s";

	private Session session;
	private PreparedStatement existStmt;

	/**
	 * @param session a pre-configured Session instance.
	 */
    public AbstractCassandraRepository(Session session)
	{
		super();
		this.session = session;
		initialize();
	}

	protected void initialize()
    {
		existStmt = session.prepare(EXISTENCE_CQL);
    }

	@Override
	public T doCreate(T entity)
	{
		if (exists(entity.getId()))
		{
			throw new DuplicateItemException(entity.getClass().getSimpleName()
			    + " ID already exists: " + entity.getId());
		}

		return createEntity(entity);
	}

	@Override
	public T doRead(String id)
	{
		T item = readEntityById(id);

		if (item == null)
		{
			throw new ItemNotFoundException("ID not found: " + id);
		}

		return item;
	}

	@Override
	public T doUpdate(T entity)
	{
		if (!exists(entity.getId()))
		{
			throw new ItemNotFoundException(entity.getClass().getSimpleName()
			    + " ID not found: " + entity.getId());
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
			throw new ItemNotFoundException("ID not found: " + entity.getId());
		}
	}

//	@Override
//	public boolean exists(String identifier)
//	{
//		BoundStatement bs = existStmt.bind(table, rowKey, identifier);
//		return !session.execute(bs).isExhausted();
//	}

	protected abstract T readEntityById(String identifier);
	protected abstract T createEntity(T entity);
	protected abstract T updateEntity(T entity);
	protected abstract void deleteEntity(T entity);
}
