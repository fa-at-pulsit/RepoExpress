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
package com.strategicgains.repoexpress.mongodb;

import org.bson.types.ObjectId;

import com.mongodb.Mongo;
import com.strategicgains.repoexpress.event.DefaultTimestampedIdentifiableRepositoryObserver;

/**
 * @author toddf
 * @since Sept 23, 2012
 */
public class MongodbEntityRepository<T extends AbstractMongodbEntity>
extends MongodbRepository<T, ObjectId>
{
    public MongodbEntityRepository(Mongo mongo, String databaseName, Class<T>... types)
    {
	    super(mongo, databaseName, types);
	    initializeObservers();
	    setIdentifierAdapter(new ObjectIdAdapter());
    }

    protected void initializeObservers()
    {
		addObserver(new DefaultTimestampedIdentifiableRepositoryObserver<T>());
    }
}
