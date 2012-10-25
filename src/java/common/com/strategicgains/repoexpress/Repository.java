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
package com.strategicgains.repoexpress;

import java.util.Collection;
import java.util.List;

import com.strategicgains.repoexpress.domain.Identifiable;

/**
 * @author toddf
 * @since Jul 12, 2010
 */
public interface Repository<T extends Identifiable>
{
	public T create(T object);
	public void delete(String id);
	public void delete(T object);
	public boolean exists(String id);
	public T read(String id);
	public List<T> readList(Collection<String> ids);
	public T update(T object);
}
