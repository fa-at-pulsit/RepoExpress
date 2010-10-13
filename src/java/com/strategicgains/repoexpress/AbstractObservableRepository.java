/*
    Copyright 2010, Strategic Gains, Inc.

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
package com.strategicgains.repoexpress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.event.RepositoryObserver;

/**
 * A base, abstract repository implementation that supports observation.
 * 
 * @author toddf
 * @since Oct 12, 2010
 */
public abstract class AbstractObservableRepository<T extends Identifiable>
extends AbstractRepository<T>
implements ObservableRepository<T>
{
	// SECTION: INSTANCE VARIABLES
	
	List<RepositoryObserver<T>> observers = new ArrayList<RepositoryObserver<T>>();

	
	// SECTION: CONSTRUCTORS
	
	public AbstractObservableRepository()
	{
		super();
	}


	// SECTION: ACCESSORS/MUTATORS
	
	public void addObserver(RepositoryObserver<T> observer)
	{
		getObserversInternal().add(observer);
	}
	
	/**
	 * Remove all observers from this repository.
	 */
	public void clearObservers()
	{
		getObserversInternal().clear();
	}
	
	private List<RepositoryObserver<T>> getObserversInternal()
	{
		return observers;
	}
	
	/**
	 * Returns the observers for this AbstractRepository as an unmodifiable list.
	 * 
	 * @return the repository's observers.
	 */
	public List<RepositoryObserver<T>> getObservers()
	{
		return Collections.unmodifiableList(getObserversInternal());
	}
	
	public boolean removeObserver(RepositoryObserver<T> observer)
	{
		return getObserversInternal().remove(observer);
	}

	
	// SECTION: REPOSITORY

    @Override
    public final T create(T object)
    {
    	notifyBeforeCreate(object);
    	T created = doCreate(object);
    	notifyAfterCreate(created);
    	return created;
    }

	@Override
    public final void delete(String id)
    {
		notifyBeforeDelete(id);
		doDelete(id);
		notifyAfterDelete(id);
    }

	@Override
    public final T read(String id)
    {
		notifyBeforeRead(id);
		T result = doRead(id);
		notifyAfterRead(result);
	    return result;
    }

	@Override
    public final void update(T object)
    {
		notifyBeforeUpdate(object);
		doUpdate(object);
		notifyAfterUpdate(object);
    }

	public List<T> readList(Collection<String> ids)
    {
    	List<T> results = new ArrayList<T>(ids.size());
    	
    	for (String id : ids)
    	{
    		results.add(read(id));
    	}
    	
    	return results;
    }
	
	
	// SECTION: EVENT OBSERVATION
	
	private void notifyAfterCreate(T object)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.afterCreate(object);
		}
	}
	
	private void notifyAfterDelete(String id)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.afterDelete(id);
		}
	}
	
	private void notifyAfterRead(T object)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.afterRead(object);
		}
	}
	
	private void notifyAfterUpdate(T object)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.afterUpdate(object);
		}
	}

	private void notifyBeforeCreate(T object)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.beforeCreate(object);
		}
	}
	
	private void notifyBeforeDelete(String id)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.beforeDelete(id);
		}
	}
	
	private void notifyBeforeRead(String id)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.beforeRead(id);
		}
	}
	
	private void notifyBeforeUpdate(T object)
	{
		for (RepositoryObserver<T> observer : getObservers())
		{
			observer.beforeUpdate(object);
		}
	}
}
