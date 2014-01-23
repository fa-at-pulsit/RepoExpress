/*
    Copyright 2014, Strategic Gains, Inc.

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

import com.datastax.driver.core.Session;
import com.strategicgains.repoexpress.domain.TimestampedUuidIdentifiable;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;

/**
 * @author toddf
 * @since Jan 22, 2014
 */
public abstract class CassandraTimestampedEntityRepository<T extends TimestampedUuidIdentifiable>
extends CassandraEntityRepository<T>
{

	public CassandraTimestampedEntityRepository(Session session, String tableName, String identifierColumn)
    {
	    super(session, tableName, identifierColumn);
		addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
    }
}
