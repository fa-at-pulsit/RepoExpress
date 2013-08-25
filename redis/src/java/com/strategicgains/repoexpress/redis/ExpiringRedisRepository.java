package com.strategicgains.repoexpress.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.strategicgains.repoexpress.domain.ExpiringIdentifiable;
import com.strategicgains.repoexpress.event.AbstractRepositoryObserver;
import com.strategicgains.repoexpress.exception.RepositoryException;

/**
 * This Redis repository extends RedisRepository and allows the persisted entities to set an
 * expiration time. A time of -1 is used to specify that the entity should never expire.
 * 
 * @since Aug 24, 2013
 * @see AbstractRepositoryObserver
 * @see RedisRepository
 */
public abstract class ExpiringRedisRepository<T extends ExpiringIdentifiable>
extends RedisRepository<T>
{
	private JedisPool jedisPool;

	public ExpiringRedisRepository(JedisPool jedisPool, Class<? extends T> entityClass)
	{
		super(jedisPool, entityClass);
		this.jedisPool = jedisPool;
	}

	@Override
	public T doCreate(T item)
	{
		//Item expires immediately, so no sense in storing it.
		if (item.getTtlSeconds() == 0)
		{
			return item;
		}

		item = super.doCreate(item);

		//Redis uses a ttl value of -1 if the key never expires. This is the default,
		//so no need to expire it.
		if (item.getTtlSeconds() > 0)
		{
			item = expire(item);
		}

		return item;
	}

	@Override
	public T doUpdate(T item)
	{
		item = super.doUpdate(item);

		return expire(item);
	}

	private T expire(T item)
	{
		Jedis jedis = jedisPool.getResource();
		
		try
		{
			if (jedis.expire(item.getId(), item.getTtlSeconds()) == 0)
			{
				throw new RepositoryException("Error setting TTL value " + item.getTtlSeconds() + "s for object: " + item.getId());
			}

			return item;
		}
		finally
		{
			jedisPool.returnResource(jedis);
		}
	}
}
