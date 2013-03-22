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
import com.strategicgains.repoexpress.exception.RepositoryException;

/**
 * @author toddf
 * @since May 27, 2011
 */
public abstract class AbstractVoldemortRepository<T>
implements Adaptable<String>
{
	// SECTION: CONSTANTS

	private static final int DEFAULT_RETRIES_BEFORE_FAILURE = 0;


	// SECTION: INSTANCE VARIABLES

	private StoreClient<String, String> storeClient;
	private int writeRetries = DEFAULT_RETRIES_BEFORE_FAILURE;
	private int readRetries = DEFAULT_RETRIES_BEFORE_FAILURE;


	// SECTION: CONSTRUCTOR

	public AbstractVoldemortRepository(ClientConfig config)
	{
		StoreClientFactory factory = new SocketStoreClientFactory(config);
		StoreClient<String, String> client = factory.getStoreClient(getStoreName());
		initialize(client);
	}

	public AbstractVoldemortRepository(final StoreClient<String, String> storeClient)
	{
		initialize(storeClient);
	}

	protected void initialize(final StoreClient<String, String> storeClient)
	{
		this.storeClient = storeClient;
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

	protected abstract String getStoreName();

	protected abstract Class<T> getPersistedClass();

	
	// SECTION: UTILITY - SUBCLASSES

	protected void performPut(String key, String value)
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
					throw new RepositoryException(e);
				}

				logError("Error on put(String, String). Key: " + key, retries,
				    getWriteRetries(), e);
			}
		}
		while (true);
	}

	protected void performPut(String key, Versioned<String> value)
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
					throw new RepositoryException(e);
				}

				logError("Error on put(String, Versioned). Key: " + key,
				    retries, getWriteRetries(), e);
			}
		}
		while (true);
	}

	boolean performDelete(String key)
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
					throw new RepositoryException(e);
				}

				logError("Error on delete(String). Key: " + key, retries,
				    getWriteRetries(), e);
			}
		}
		while (true);
	}

	Versioned<String> performGet(String key)
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
					throw new RepositoryException(e);
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