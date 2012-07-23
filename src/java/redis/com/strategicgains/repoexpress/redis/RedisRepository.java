/*
    Copyright 2012, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.event.AbstractRepositoryObserver;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;

/**
 * This Redis repository works on simpler objects than RedisJOhmRepository, in that, the persisted
 * entities must only implement Identifiable and can have String types as their ID.
 * <p/>
 * Note that this repository requires the client to set appropriate ID values.  Otherwise, since
 * this repository is 'observable', a RepositoryObserver may be used to perform that task automatically
 * on a create operation if desired.
 * <p/>
 * Usage of this repository requires de/serialization to occur during the persistence operation.
 * Consequently, sub-classes must implement the marshalFrom(T) and marshalTo(String, T) methods.
 * If using this within a RestExpress service suite, DefaultJsonProcessor or DefaultXmlProcessor
 * can be used.  Or, if the RestExpress kickstart process was used, ResponseProcessors.JSON_SERIALIZER
 * or ResponseProcessors.XML_SERIALIZER may be leveraged.
 * 
 * @author toddf
 * @since Jul 19, 2012
 * @see AbstractRepositoryObserver
 * @see RedisJOhmRepository
 */
public abstract class RedisRepository<T extends Identifiable>
extends AbstractObservableRepository<T>
{
	private JedisPool jedisPool;
	private Class<? extends T> entityClass;

	public RedisRepository(JedisPool jedisPool, Class<? extends T> entityClass)
	{
		super();
		this.jedisPool = jedisPool;
		this.entityClass = entityClass;
	}

	@Override
	public T doCreate(T item)
	{
		if (exists(item.getId()))
		{
			throw new DuplicateItemException(item.getClass().getSimpleName()
			    + " ID already exists: " + item.getId());
		}

		Jedis jedis = jedisPool.getResource();
		
		try
		{
			if (!jedis.set(item.getId(), marshalFrom(item)).equalsIgnoreCase("OK"))
			{
				throw new RepositoryException("Error creating object: " + item.getId());
			}

			return item;
		}
		finally
		{
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public void doDelete(String id)
	{
		Jedis jedis = jedisPool.getResource();

		try
		{
			Long reply = jedis.del(id);
			if (reply < 1)
			{
				throw new ItemNotFoundException("ID not found: " + id);
			}
		}
		finally
		{
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public T doRead(String id)
	{
		Jedis jedis = jedisPool.getResource();

		try
		{
			String json = jedis.get(id);

			if (json == null || json.trim().isEmpty())
			{
				throw new ItemNotFoundException("ID not found: " + id);
			}

			return marshalTo(json, entityClass);
		}
		finally
		{
			jedisPool.returnResource(jedis);
		}
	}

	@Override
	public T doUpdate(T item)
	{
		if (!exists(item.getId()))
		{
			throw new ItemNotFoundException(item.getClass().getSimpleName()
			    + " ID not found: " + item.getId());
		}

		Jedis jedis = jedisPool.getResource();
		
		try
		{
			if (!jedis.set(item.getId(), marshalFrom(item)).equalsIgnoreCase("OK"))
			{
				throw new RepositoryException("Error updating object: " + item.getId());
			}

			return item;
		}
		finally
		{
			jedisPool.returnResource(jedis);
		}
	}


	// SECTION: UTILITY

	protected boolean exists(String id)
	{
		if (id == null) return false;

		Jedis jedis = jedisPool.getResource();
		
		try
		{
			return jedis.exists(id);
		}
		finally
		{
			jedisPool.returnResource(jedis);
		}
	}
	
	protected abstract T marshalTo(String json, Class<? extends T> entityClass);
	protected abstract String marshalFrom(T instance);
}
