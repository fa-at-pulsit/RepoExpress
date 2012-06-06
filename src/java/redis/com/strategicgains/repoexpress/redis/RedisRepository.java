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

import com.strategicgains.repoexpress.AbstractObservableAdaptableRepository;
import com.strategicgains.repoexpress.domain.Identifiable;

/**
 * @author toddf
 * @since Jun 6, 2012
 */
public class RedisRepository<T extends Identifiable, I>
extends AbstractObservableAdaptableRepository<T, I>
{
	@Override
	public T doCreate(T object)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doDelete(String id)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public T doRead(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T doUpdate(T object)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
