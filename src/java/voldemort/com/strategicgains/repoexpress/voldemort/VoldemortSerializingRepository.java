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
package com.strategicgains.repoexpress.voldemort;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.versioning.Versioned;

import com.strategicgains.repoexpress.AbstractObservableAdaptableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;
import com.strategicgains.restexpress.serialization.SerializationProcessor;

/**
 * A Repository that uses Voldemort as its back-end store.  It requires a SerializationProcessor
 * instance in order to convert the domain objects into a string for storage and back to an
 * object instance on read.
 * 
 * @author toddf
 * @since May 27, 2011
 */
public class VoldemortSerializingRepository<T extends Identifiable>
extends AbstractObservableAdaptableRepository<T, String>
{
	// SECTION: CONSTANTS

	private static final int DEFAULT_RETRIES_BEFORE_FAILURE = 0;

	// SECTION: INSTANCE VARIABLES

	private StoreClient<String, String> storeClient;
	private int writeRetries = DEFAULT_RETRIES_BEFORE_FAILURE;
	private int readRetries = DEFAULT_RETRIES_BEFORE_FAILURE;
	private SerializationProcessor serializer;
	private Class<T> persistedClass;

	
	// SECTION: CONSTRUCTOR

	public VoldemortSerializingRepository(ClientConfig config, String storeName, Class<T> persistedClass,
		SerializationProcessor serializationProcessor)
	{
		StoreClientFactory factory = new SocketStoreClientFactory(config);
		StoreClient<String, String> client = factory.getStoreClient(storeName);
		initialize(client, persistedClass, serializationProcessor);
	}

	public VoldemortSerializingRepository(StoreClient<String, String> storeClient, Class<T> persistedClass,
		SerializationProcessor serializationProcessor)
	{
		initialize(storeClient, persistedClass, serializationProcessor);
	}

	protected void initialize(final StoreClient<String, String> storeClient, Class<T> persistedClass,
		SerializationProcessor serializationProcessor)
	{
		this.storeClient = storeClient;
		this.persistedClass = persistedClass;
		this.serializer = serializationProcessor;
	}

	public int getReadRetries()
	{
		return readRetries;
	}

	public void setReadRetries(int readRetries)
	{
		this.readRetries = readRetries;
	}

	public int getWriteRetries()
	{
		return writeRetries;
	}

	public void setWriteRetries(int retriesBeforeFailure)
	{
		this.writeRetries = retriesBeforeFailure;
	}

	@Override
	public T doCreate(T persistable)
	{
		String key = (String) adaptId(persistable.getId());
		Versioned<String> existingValue;
		try
		{
			existingValue = performGet(key);
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}

		if (existingValue != null)
		{
			throw new DuplicateItemException(persistable.getClass()
			    .getSimpleName() + " record '" + key + "' already exists");
		}
		try
		{
			String json = serializer.serialize(persistable);
			performPut(key, json);
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
		
		return persistable;
	}

	@Override
	public void doDelete(String id)
	{
		try
		{
			boolean result = performDelete((String) adaptId(id));

			if (!result)
			{
				throw new ItemNotFoundException("nothing was deleted");
			}
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
	}

	@Override
	public T doRead(String id)
	{
		try
		{
			Versioned<String> vj = performGet(adaptId(id));

			if (vj == null)
			{
				throw new ItemNotFoundException("ID not found: " + id);
			}

			String json = vj.getValue();
			return (json == null ? null : serializer.deserialize(json, persistedClass));
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
	}

	@Override
	public T doUpdate(T persistable)
	{
		String key = adaptId(persistable.getId());
		try
		{
			String json = serializer.serialize(persistable);
			Versioned<String> versioned = performGet(key);
			versioned.setObject(json);
			performPut(key, versioned);
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
		
		return persistable;
	}


	// SECTION: UTILITY - PRIVATE

	private void performPut(String key, String value) throws Exception
	{
		int retries = 0;

		do
		{
			try
			{
				storeClient.put(key, value);
				return;
			}
			catch (Exception e)
			{
				if (++retries > getWriteRetries())
				{
					throw e;
				}

				logError("Error on put(String, String). Key: " + key, retries,
				    getWriteRetries(), e);
			}
		}
		while (true);
	}

	private void performPut(String key, Versioned<String> value)
	throws Exception
	{
		int retries = 0;

		do
		{
			try
			{
				storeClient.put(key, value);
				break;
			}
			catch (Exception e)
			{
				if (++retries > getWriteRetries())
				{
					throw e;
				}

				logError("Error on put(String, Versioned). Key: " + key,
				    retries, getWriteRetries(), e);
			}
		}
		while (true);
	}

	private boolean performDelete(String key) throws Exception
	{
		int retries = 0;

		do
		{
			try
			{
				return storeClient.delete(key);
			}
			catch (Exception e)
			{
				if (++retries > getWriteRetries())
				{
					throw e;
				}

				logError("Error on delete(String). Key: " + key, retries,
				    getWriteRetries(), e);
			}
		}
		while (true);
	}

	private Versioned<String> performGet(String key) throws Exception
	{
		int retries = 0;

		do
		{
			try
			{
				return storeClient.get(key);
			}
			catch (Exception e)
			{
				if (++retries > getReadRetries())
				{
					throw e;
				}

				logError("Error on get(String). Key: " + key, retries,
				    getReadRetries(), e);
			}
		}
		while (true);
	}

	private void logError(String message, int retries, int maxRetries, Throwable t)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(message);
		sb.append(" Retrying (");
		sb.append(retries);
		sb.append("/");
		sb.append(maxRetries);
		sb.append(")");
//		LOG.error(sb.toString(), t);
		System.out.println(sb.toString());
	}
}
