/*
    Copyright 2011, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress.mongodb;

import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.strategicgains.repoexpress.AbstractObservableAdaptableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.restexpress.query.FilterCallback;
import com.strategicgains.restexpress.query.OrderCallback;
import com.strategicgains.restexpress.query.OrderComponent;
import com.strategicgains.restexpress.query.QueryFilter;
import com.strategicgains.restexpress.query.QueryOrder;
import com.strategicgains.restexpress.query.QueryRange;

/**
 * Uses MongoDB as its back-end store. This repository can handle
 * "single-table inheritance" by passing all the supported types into the
 * constructor, with the inheritance root first.
 * 
 * @author toddf
 * @since Aug 24, 2010
 */
public class MongodbRepository<T extends Identifiable, I>
extends AbstractObservableAdaptableRepository<T, I>
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
		T item = datastore.get(inheritanceRoot, adaptId(id));

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

	/**
	 * Count the instances of the given type matching the given filter criteria.
	 * 
	 * @param range
	 * @param filter
	 * @param order
	 */
	public long count(Class<T> type, QueryFilter filter)
	{
		return getBaseQuery(type, filter).countAll();
	}


	// SECTION: UTILITY

	protected boolean exists(String id)
	{
		if (id == null) return false;

		return (datastore.getCount(datastore.find(inheritanceRoot, "_id", adaptId(id))) > 0);
	}

	protected Datastore getDataStore()
	{
		return datastore;
	}
	
	protected Mongo getMongo()
	{
		return mongo;
	}

	/**
	 * @param range
	 * @param filter
	 * @param order
	 */
	protected List<T> query(Class<T> type, QueryRange range, QueryFilter filter, QueryOrder order)
	{
		Query<T> q = getBaseQuery(type, filter);
		configureQueryRange(q, range);
		configureQueryOrder(q, order);
		return q.asList();
	}

	private Query<T> getBaseQuery(Class<T> type, QueryFilter filter)
	{
		Query<T> q = getDataStore().find(type);
		configureQueryFilter(q, filter);
		return q;
	}

	/**
	 * @param q
	 * @param range
	 */
	private void configureQueryRange(Query<T> q, QueryRange range)
	{
		if (range.isInitialized())
		{
			q.offset((int) range.getStart());
			q.limit(range.getOffset() + 1);
		}
	}

	private void configureQueryFilter(final Query<T> q, QueryFilter filter)
	{
		filter.iterate(new FilterCallback()
		{
			@Override
			public void filterOn(String name, String value)
			{
				q.field(name).contains(value.toLowerCase());
			}
		});
	}

	/**
	 * @param q
	 * @param order
	 */
	private void configureQueryOrder(Query<T> q, QueryOrder order)
	{
		if (order.isSorted())
		{
			final StringBuilder sb = new StringBuilder();
			
			order.iterate(new OrderCallback()
			{
				boolean isFirst = true;

				@Override
				public void orderBy(OrderComponent component)
				{
					if (!isFirst)
					{
						sb.append(',');
					}
					
					if (component.isDescending())
					{
						sb.append('-');
					}

					sb.append(component.getFieldName());
					isFirst = false;
				}
			});
			
			q.order(sb.toString());
		}
	}
}
