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

import java.util.List;

import redis.clients.johm.JOhm;

import com.strategicgains.repoexpress.AbstractObservableAdaptableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.restexpress.query.QueryFilter;
import com.strategicgains.restexpress.query.QueryOrder;
import com.strategicgains.restexpress.query.QueryRange;

/**
 * Persist objects to a Redis datastore. Object must be an Identifiable and the ID must be numeric (e.g. Long, Integer).
 * 
 * @author toddf
 * @since Jun 6, 2012
 */
public class RedisRepository<T extends Identifiable>
extends AbstractObservableAdaptableRepository<T, Integer>
{
	private Class<T> entityClass;

	public RedisRepository(Class<T> entityClass)
	{
		super();
		this.entityClass = entityClass;
		setIdentifierAdapter(new StringIntegerIdAdapter());
	}

	@Override
	public T doCreate(T object)
	{
		if (JOhm.isNew(object))
		{
			return JOhm.save(object);
		}
		
		throw new DuplicateItemException(object.getClass().getSimpleName()
		    + " ID already exists: " + object.getId());

	}

	@Override
	public void doDelete(String id)
	{
		JOhm.delete(entityClass, adaptId(id));
	}

	@Override
	public T doRead(String id)
	{
		return JOhm.get(entityClass, adaptId(id));
	}

	@Override
	public T doUpdate(T object)
	{
		if (JOhm.isNew(object))
		{
			throw new ItemNotFoundException(object.getClass().getSimpleName()
			    + " ID not found: " + object.getId());
		}

		return JOhm.save(object);
	}

	/**
	 * Perform a query against a Redis datastore. This method is intended to assist in implementation of
	 * specific query methods (e.g. readAll(), readCustomerOrders(), etc.).  Supports the usage
	 * of QueryRange, QueryFilter and QueryOrder, which support parsing the Request in known ways.
	 * 
	 * @param range
	 * @param filter
	 * @param order
	 * @see QueryRange
	 * @see QueryFilter
	 * @see QueryOrder
	 */
	protected List<T> query(Class<T> type, QueryRange range, QueryFilter filter, QueryOrder order)
	{
//		Query<T> q = getBaseQuery(type, filter);
//		configureQueryRange(q, range);
//		configureQueryOrder(q, order);
//		return q.asList();
		return null;
	}
}
