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
package com.strategicgains.repoexpress;

import java.util.Iterator;

import com.strategicgains.repoexpress.adapter.IdentiferAdapter;
import com.strategicgains.repoexpress.domain.Identifiable;
import com.strategicgains.repoexpress.domain.Identifier;

/**
 * A base, abstract repository implementation that supports observation, as well as,
 * adaptation of the identifier via an IdentifierAdapter (e.g. from String in the
 * domain model to, say, some type of ObjectId in the data store and visa versa).
 * 
 * @author toddf
 * @since May 27, 2011
 */
public abstract class AbstractObservableAdaptableRepository<T extends Identifiable, I>
extends AbstractObservableRepository<T>
implements Adaptable<I>
{
	// Convert an ID to an OID (or whatever) before reading.
	private IdentiferAdapter<I> identifierAdapter = null;

	public IdentiferAdapter<I> getIdentifierAdapter()
	{
		return identifierAdapter;
	}

	public boolean hasIdentifierAdapter()
	{
		return (identifierAdapter != null);
	}

	public void setIdentifierAdapter(IdentiferAdapter<I> adapter)
	{
		this.identifierAdapter = adapter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public I adaptId(Identifier id)
	{
		if (hasIdentifierAdapter())
		{
			return getIdentifierAdapter().convert(id);
		}

		return (I) id;
	}

	
	// SECTION: INNER CLASSES

	/**
	 * An Iterable implementation returning an Iterator that essentially converts
	 * a Collection of String IDs into a Collection of native object IDs.
	 *  
	 * @author toddf
	 * @since Oct 25, 2012
	 */
	protected class AdaptedIdIterable
	implements Iterable<I>
	{
		private Iterable<Identifier> iterable;

		public AdaptedIdIterable(Iterable<Identifier> iterable)
		{
			this.iterable = iterable;
		}

        @Override
        public Iterator<I> iterator()
        {
	        return new IdAdapterIterator(iterable.iterator());
        }
	}
	
	/**
	 * Takes an Iterator of String instances and converts them, while iterating,
	 * to native object IDs using the adaptId() method.
	 * 
	 * @author toddf
	 * @since Oct 25, 2012
	 */
	protected class IdAdapterIterator
	implements Iterator<I>
	{
		private Iterator<Identifier> iterator;

		public IdAdapterIterator(Iterator<Identifier> iterator)
		{
			this.iterator = iterator;
		}

        @Override
        public boolean hasNext()
        {
	        return iterator.hasNext();
        }

        @Override
        public I next()
        {
	        return adaptId(iterator.next());
        }

        @Override
        public void remove()
        {
        	iterator.remove();
        }
	}
}
