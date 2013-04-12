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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.EntityManager;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.strategicgains.repoexpress.AbstractObservableAdaptableRepository;
import com.strategicgains.repoexpress.Queryable;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.InvalidObjectIdException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;
import com.strategicgains.restexpress.common.query.QueryFilter;
import com.strategicgains.restexpress.common.query.QueryOrder;
import com.strategicgains.restexpress.common.query.QueryRange;

/**
 * Uses Cassandra as its back-end store. This repository can handle
 * "single-table inheritance" by passing all the supported types into the
 * constructor, with the inheritance root listed first.
 * 
 * @author toddf
 * @since Apr 12, 2013
 */
public class CassandraRepository<T extends Identifiable, I>
extends AbstractObservableAdaptableRepository<T, I>
implements Queryable<T>
{
	private CassandraConfiguration config;
	private Keyspace keyspace;
	private ColumnFamily<I, String> columnFamily;
	private Class<T> inheritanceRoot;
	private EntityManager<T, I> mapper;

	/**
	 * @param cassandra a pre-configured CassandraConfiguration instance.
	 * @param columnFamilyName the name of the keyspace (in Cassandra).
	 * @param entityClasses Class(es) managed by this repository. Inheritance root first.
	 */
	public CassandraRepository(CassandraConfiguration cassandra, String columnFamilyName, Class<? extends T>... entityClasses)
	{
		super();
		this.config = cassandra;
		AstyanaxContext<Keyspace> context = config.buildContext();
		context.start();
		this.keyspace = context.getClient();
		this.inheritanceRoot = (Class<T>) entityClasses[0];
		initialize(columnFamilyName, entityClasses);
	}

	private void initialize(String columnFamilyName, Class<? extends T>... entityClasses)
	{
		ColumnFamily.newColumnFamily(columnFamilyName, keySerializer, columnSerializer);
//		columnFamily = new ColumnFamily<I, String>(columnFamilyName, SerializerTypeInferer.getSerializer(I), StringSerializer.get());
		DefaultEntityManager.Builder<T, I> b = new DefaultEntityManager.Builder<T, I>()
		    .withEntityType(inheritanceRoot)
		    .withKeyspace(keyspace)
		    .withColumnFamily(columnFamilyName);
		
		b.
	}

	@Override
	public T doCreate(T item)
	{
		if (exists(item.getId()))
		{
			throw new DuplicateItemException(item.getClass().getSimpleName()
			    + " ID already exists: " + item.getId());
		}

		mapper.put(item);
		return item;
	}

	@Override
	public T doRead(String id)
	{
		T item = mapper.get(adaptId(id));

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

		mapper.put(item);
		return item;
	}

	@Override
	public void doDelete(T object)
	{
		try
		{
			mapper.delete(adaptId(object.getId()));
		}
		catch (InvalidObjectIdException e)
		{
			throw new ItemNotFoundException("ID not found: " + object.getId());
		}
	}

	/**
	 * A general-purpose 'finder' method, useful for implementing alternate-key
	 * queries. Since it does not support ordering and range sub-sets, it's best
	 * for creating queries that return a list size of 1.
	 * <p/>
	 * Essentially, just calls readAll() with null range and order. So if you
	 * need ordering, call readAll(filter, null, order).
	 * 
	 * @param filter
	 *            query criteria.
	 * @return a list of results. Never null.
	 */
	public List<T> find(QueryFilter filter)
	{
		return readAll(filter, null, null);
	}

	/**
	 * Implements a 'default' readAll' method that queries for all instances of
	 * the inheritance root class matching the given criteria.
	 * 
	 * This method does not invoke an observer method, so is not observable by
	 * default. Override, calling super() to get that functionality, or call
	 * notifyBeforeXXX() and/or notifyAfterXXX() methods, if desired.
	 * 
	 * @param filter
	 * @param range
	 * @param order
	 * @return a list of results. Never null.
	 */
	@Override
	public List<T> readAll(QueryFilter filter, QueryRange range,
	    QueryOrder order)
	{
		return query(inheritanceRoot, filter, range, order);
	}

	/**
	 * Read each of the instances corresponding to the given Collection of IDs,
	 * returning the results as a list. If an ID in the provided Collection does
	 * not exist, it is simply not included in the returned results.
	 * 
	 * @param ids
	 *            a Collection of IDs to read.
	 */
	@Override
	public List<T> readList(Collection<String> ids)
	{
		Collection<I> c = new ArrayList<I>(ids.size());

		for (I id : new AdaptedIdIterable(ids))
		{
			c.add(id);
		}

		return getMapper().get(c);
	}

	/**
	 * Count the instances of the inheritance root (class) that match the given
	 * filter criteria.
	 * 
	 * @param filter
	 */
	@Override
	public long count(QueryFilter filter)
	{
		return count(inheritanceRoot, filter);
	}

	/**
	 * Count the instances of the given type matching the given filter criteria.
	 * 
	 * @param type
	 * @param filter
	 */
	public long count(Class<T> type, QueryFilter filter)
	{
//		return getBaseFilterQuery(type, filter).countAll();
		return 0l;
	}

	/**
	 * Returns true if the given id exists in the repository.
	 * 
	 * @param id
	 *            the identifier of the object.
	 */
	@Override
	public boolean exists(String id)
	{
		if (id == null) return false;

		try
		{
			Column<String> result = keyspace.prepareQuery(columnFamily)
				.getKey(adaptId(id))
				.getColumn("id")
				.execute()
				.getResult();
			return (result != null);
		}
		catch(ConnectionException e)
		{
			throw new RepositoryException(e);
		}
	}

	// SECTION: UTILITY

	/**
	 * Get the underlying Cassandra Astyanax EntityManager instance with which to construct
	 * queries against.
	 * 
	 * @return the underlying Astyanax EntityManager.
	 */
	protected EntityManager<T, I> getMapper()
	{
		return mapper;
	}

	/**
	 * Return the underlying Cassandra keyspace instance.
	 * 
	 */
	protected Keyspace getKeyspace()
	{
		return keyspace;
	}
	
	protected ColumnFamily<I, String> getColumnFamily()
	{
		return columnFamily;
	}

	/**
	 * Execute a query against the repository, using QueryFilter, QueryRange and
	 * QueryOrder as criteria against the type. Returns the results as a List.
	 * 
	 * @param type
	 * @param range
	 * @param filter
	 * @param order
	 */
	protected List<T> query(Class<T> type, QueryFilter filter,
	    QueryRange range, QueryOrder order)
	{
//		return getBaseQuery(type, filter, range, order).asList();
		return null;
	}

	/**
	 * Create and configure a basic query utilizing provided QueryFilter,
	 * QueryRange and QueryOrder criteria, returning the query.
	 * 
	 * @param type
	 * @param range
	 * @param filter
	 * @param order
	 */
//	protected Query<T> getBaseQuery(Class<T> type, QueryFilter filter,
//	    QueryRange range, QueryOrder order)
//	{
//		Query<T> q = getBaseFilterQuery(type, filter);
//		configureQueryRange(q, range);
//		configureQueryOrder(q, order);
//		return q;
//	}

	/**
	 * Create and configure a basic query utilizing just QueryFilter as
	 * criteria.
	 * 
	 * @param type
	 * @param filter
	 * @return a Morphia Query instance configured for the QueryFilter criteria.
	 */
//	private Query<T> getBaseFilterQuery(Class<T> type, QueryFilter filter)
//	{
//		Query<T> q = getDataStore().find(type);
//		configureQueryFilter(q, filter);
//		return q;
//	}

	/**
	 * @param q
	 * @param range
	 */
//	private void configureQueryRange(Query<T> q, QueryRange range)
//	{
//		if (range == null) return;
//
//		if (range.isInitialized())
//		{
//			q.offset((int) range.getStart());
//			q.limit(range.getLimit());
//		}
//	}

//	private void configureQueryFilter(final Query<T> q, QueryFilter filter)
//	{
//		if (filter == null) return;
//
//		filter.iterate(new FilterCallback()
//		{
//			@Override
//			public void filterOn(FilterComponent c)
//			{
//				q.field(c.getField()).contains(c.getValue());
//			}
//		});
//	}

	/**
	 * @param q
	 * @param order
	 */
//	private void configureQueryOrder(Query<T> q, QueryOrder order)
//	{
//		if (order == null) return;
//
//		if (order.isSorted())
//		{
//			final StringBuilder sb = new StringBuilder();
//
//			order.iterate(new OrderCallback()
//			{
//				boolean isFirst = true;
//
//				@Override
//				public void orderBy(OrderComponent component)
//				{
//					if (!isFirst)
//					{
//						sb.append(',');
//					}
//
//					if (component.isDescending())
//					{
//						sb.append('-');
//					}
//
//					sb.append(component.getFieldName());
//					isFirst = false;
//				}
//			});
//
//			q.order(sb.toString());
//		}
//	}
}
