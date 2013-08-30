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

import com.strategicgains.repoexpress.Adaptable;
import com.strategicgains.repoexpress.adapter.IdentiferAdapter;
import com.strategicgains.repoexpress.exception.DuplicateItemException;
import com.strategicgains.repoexpress.exception.ItemNotFoundException;
import com.strategicgains.repoexpress.exception.RepositoryException;

/**
 * A Repository that uses Voldemort as its back-end store as a big HashMap.
 * 
 * @author toddf
 * @since May 27, 2011
 */
@Deprecated
public class VoldemortRepository
implements Adaptable<String>
{
	// SECTION: CONSTANTS

	private static final int DEFAULT_RETRIES_BEFORE_FAILURE = 0;

	// SECTION: INSTANCE VARIABLES

	private StoreClient<String, String> storeClient;
	private int writeRetries = DEFAULT_RETRIES_BEFORE_FAILURE;
	private int readRetries = DEFAULT_RETRIES_BEFORE_FAILURE;
	private IdentiferAdapter<String> prefixer;

	
	// SECTION: CONSTRUCTOR

	public VoldemortRepository(ClientConfig config, String storeName)
	{
		StoreClientFactory factory = new SocketStoreClientFactory(config);
		StoreClient<String, String> client = factory.getStoreClient(storeName);
		initialize(client);
	}

	public VoldemortRepository(StoreClient<String, String> storeClient)
	{
		initialize(storeClient);
	}

	protected void initialize(final StoreClient<String, String> storeClient)
	{
		this.storeClient = storeClient;
	}

    public IdentiferAdapter<String> getIdentifierAdapter()
    {
    	return prefixer;
    }
    
    public boolean hasIdentifierAdapter()
    {
    	return (getIdentifierAdapter() != null);
    }

    public void setIdentifierAdapter(IdentiferAdapter<String> adapter)
    {
    	this.prefixer = adapter;
    }

	@Override
	public String adaptId(String id)
	{
		if (hasIdentifierAdapter())
		{
			return getIdentifierAdapter().convert(id);
		}
		
		return id;
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

	public void create(String key, String value)
	{
		String prefixedKey = (String) adaptId(key);
		Versioned<String> existingValue;
		try
		{
			existingValue = performGet(prefixedKey);
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}

		if (existingValue != null)
		{
			throw new DuplicateItemException(prefixedKey + "' already exists");
		}
		try
		{
			performPut(prefixedKey, value);
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
	}

	public void delete(String id)
	{
		try
		{
			boolean result = performDelete((String) adaptId(id));

			if (!result)
			{
				throw new ItemNotFoundException("ID not found: " + id);
			}
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
	}

	public String read(String id)
	{
		try
		{
			Versioned<String> vj = performGet(adaptId(id));

			if (vj == null)
			{
				throw new ItemNotFoundException("ID not found: " + id);
			}

			return vj.getValue();
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
	}

	public void update(String key, String value)
	{
		String prefixedKey = adaptId(key);
		try
		{
			Versioned<String> versioned = performGet(prefixedKey);
			versioned.setObject(value);
			performPut(prefixedKey, versioned);
		}
		catch (Exception e)
		{
			throw new RepositoryException(e);
		}
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
