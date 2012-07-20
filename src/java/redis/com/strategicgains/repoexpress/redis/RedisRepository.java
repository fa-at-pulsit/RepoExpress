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
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;

/**
 * @author toddf
 * @since Jul 19, 2012
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
			String json = jedis.set(item.getId(), marshalFrom(item));
			return marshalTo(json, entityClass);
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
			String json = jedis.set(item.getId(), marshalFrom(item));
			return marshalTo(json, entityClass);
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
