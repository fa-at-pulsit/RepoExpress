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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.strategicgains.repoexpress.domain.AbstractUuidEntity;
import com.strategicgains.repoexpress.domain.Identifier;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;
import com.strategicgains.repoexpress.event.UuidIdentityRepositoryObserver;

/**
 * A Cassandra repository that manages types of AbstractCassandraEntity, which are
 * identified by a single UUID primary key. It utilizes the {@link UuidIdentityRepositoryObserver}
 * to assign a UUID on creation.  It also uses {@link DefaultTimestampedIdentifiableRepositoryObserver}
 * to set the createAt and updatedAt dates on the object as appropriate.
 * <p/>
 * Storing a UUID as the ID (as this repository does) requires four (4) bytes for the ID.
 * 
 * @author toddf
 * @since Apr 12, 2013
 */
public abstract class CassandraEntityRepository<T extends AbstractUuidEntity>
extends AbstractCassandraRepository<T>
{
	private static final String EXISTENCE_CQL = "SELECT count(*) FROM %s WHERE %s = ?";
	private static final String READ_CQL = "SELECT * FROM %s WHERE %s = ?";

	private String rowKey;
	private PreparedStatement existStmt;
	private PreparedStatement readStmt;

	/**
	 * @param session a pre-configured Session instance.
	 * @param databaseName the name of a database this repository works against.
	 */
    public CassandraEntityRepository(Session session, String tableName, String rowKeyName)
	{
		super(session, tableName);
		this.rowKey = rowKeyName;
	    initializeObservers();
		initialize();
	}

    protected void initializeObservers()
    {
		addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
		addObserver(new UuidIdentityRepositoryObserver<T>());
    }

    protected void initialize()
    {
		existStmt = getSession().prepare(String.format(EXISTENCE_CQL, getTable(), rowKey));
		readStmt = getSession().prepare(String.format(READ_CQL, getTable(), rowKey));
    }

	@Override
	public boolean exists(Identifier identifier)
	{
		if (identifier == null || identifier.isEmpty()) return false;

		BoundStatement bs = new BoundStatement(existStmt);
		bs.bind(identifier.primaryKey());
		return (getSession().execute(bs).one().getInt(0) > 0);
	}

	protected T readEntityById(Identifier identifier)
	{
		if (identifier == null || identifier.isEmpty()) return null;
		
		BoundStatement bs = new BoundStatement(readStmt);
		bs.bind(identifier.primaryKey());
		return marshalRow(getSession().execute(bs).one());
	}

	protected abstract T marshalRow(Row row);
}
