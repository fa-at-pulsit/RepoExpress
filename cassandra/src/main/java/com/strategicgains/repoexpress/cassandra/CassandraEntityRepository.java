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
	private static final String EXISTENCE_CQL = "select count(*) from %s where %s = ?";
	private static final String READ_CQL = "select * from %s where %s = ?";
	private static final String DELETE_CQL = "delete from %s where %s = ?";

	private String identifierColumn;
	private PreparedStatement existStmt;
	private PreparedStatement readStmt;
	private PreparedStatement deleteStmt;

	/**
	 * @param session a pre-configured Session instance.
	 * @param databaseName the name of a database this repository works against.
	 * @param identifierColumn the column name that holds the row key or unique identifier.
	 */
    public CassandraEntityRepository(Session session, String tableName, String identifierColumn)
	{
		super(session, tableName);
		this.identifierColumn = identifierColumn;
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
		existStmt = getSession().prepare(String.format(EXISTENCE_CQL, getTable(), identifierColumn));
		readStmt = getSession().prepare(String.format(READ_CQL, getTable(), identifierColumn));
		deleteStmt = getSession().prepare(String.format(DELETE_CQL, getTable(), identifierColumn));
    }

	public String getIdentifierColumn()
	{
		return identifierColumn;
	}

	@Override
	public boolean exists(Identifier identifier)
	{
		if (identifier == null || identifier.isEmpty()) return false;

		BoundStatement bs = new BoundStatement(existStmt);
		bs.bind(identifier.primaryKey());
		return (getSession().execute(bs).one().getLong(0) > 0);
	}

	@Override
	protected T readEntityById(Identifier identifier)
	{
		if (identifier == null || identifier.isEmpty()) return null;
		
		BoundStatement bs = new BoundStatement(readStmt);
		bs.bind(identifier.primaryKey());
		return marshalRow(getSession().execute(bs).one());
	}

	@Override
	protected void deleteEntity(T entity)
	{
		if (entity == null) return;
		
		BoundStatement bs = new BoundStatement(deleteStmt);
		bindIdentifier(bs, entity.getId());
		getSession().execute(bs);
	}

	protected abstract T marshalRow(Row row);
}
