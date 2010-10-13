/*
 * Copyright 2010, Strategic Gains, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.strategicgains.repoexpress.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.strategicgains.repoexpress.AbstractObservableRepository;
import com.strategicgains.repoexpress.domain.Persistable;
import com.strategicgains.repoexpress.exception.ConflictException;
import com.strategicgains.repoexpress.exception.NotFoundException;

/**
 * @author toddf
 * @since Oct 12, 2010
 */
public abstract class InMemoryRepository<T extends Persistable>
extends AbstractObservableRepository<T>
{
	private static long nextId = 0;
	private Map<String, T> items = new ConcurrentHashMap<String, T>();

	@Override
	public T doCreate(T item)
	{
		if (hasId(item))
		{
			try
			{
				read(item.getId());
				throw new ConflictException(item.getClass().getSimpleName() + " ID already exists: " + item.getId());
			}
			catch(NotFoundException e)
			{
				// expected.
			}
		}
		else
		{
			item.setId(item.getClass().getSimpleName() + ++nextId);
		}

		items.put(item.getId(), item);
		return item;
	}

    @Override
    public T doRead(String id)
    {
    	T b = items.get(id);
    	
    	if (b == null)
    	{
    		throw new NotFoundException("ID not found: " + id);
    	}

    	return b;
    }

    @Override
    public void doUpdate(T item)
    {
    	items.put(item.getId(), item);
    }

    @Override
    public void doDelete(String id)
    {
    	T item = items.remove(id);

    	if (item == null)
    	{
    		throw new NotFoundException("ID not found: " + id);
    	}
    }
}
