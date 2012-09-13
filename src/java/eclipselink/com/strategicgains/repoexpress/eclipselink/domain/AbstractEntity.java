/*
	Copyright 2004-2012 Strategic Gains, Inc.
	
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
package com.strategicgains.repoexpress.eclipselink.domain;

import java.util.UUID;

import com.strategicgains.repoexpress.eclipselink.annotation.UnpersistedProperty;

/**
 * Base object for domain objects that are persistent (stored in a relational database)
 * via EclipseLink and use an OID.
 * 
 * @author Todd Fredrich
 * @created  December 10, 2004
 */
public abstract class AbstractEntity<T extends AbstractEntity<T>>
implements Entity<T>
{
	//SECTION: VARIABLES

	/**
	 * The unique object identifier for this domain object, if persistent.
	 * Null if not persistent (yet).
	 */
	private Long oid;
	
	/**
	 * The unique object identifier for this domain object before it is
	 * persistent; the transient identifier.
	 */
	@UnpersistedProperty
	private UUID uuid;


	//SECTION: CONSTRUCTORS

	public AbstractEntity()
	{
		super();
		initializeIdentity();
	}
	
	
	//SECTION: INITIALIZATION

	private void initializeIdentity()
	{
		oid = null;
		uuid = UUID.randomUUID();
	}


	// SECTION: ACCESSING
	
	/**
	 * An AbstractEntity will ALWAYS have a transient identifier, however, it's only 
	 * meaningful when an object is NOT (yet) persisted.
	 * 
	 * return a UUID reflecting the transient ID of this object.
	 */
	protected UUID getTransientIdentifier()
	{
		return uuid;
	}

	/**
	 * Answers whether this AbstractEntity is transient (not persisted) by returning
	 * true if the object has NOT been persisted (has no OID).
	 * 
	 * @return true if the object is not persisted (transient).
	 */
	protected boolean isTransient()
	{
		return !isPersistent();
	}
	
	/**
	 * Answers whether the object is persisted (has an OID) by returning true if the object
	 * has an OID assigned from the underlying relational database.
	 * 
	 * return true if the entity has an OID assigned by the underlying RDBMS.
	 */
	public boolean isPersistent()
	{
		return hasOid();
	}

	/**
	 * Return the OID for the object assigned by the underlying RDBMS.  Note that an OID may
	 * be unique ONLY within the context of this object type--not globally.
	 * 
	 * return the OID for the entity.
	 */
	@Override
	public Long getOid()
	{
		return oid;
	}
	
//	@Override
//	public void setOid(Long oid)
//	{
//		this.oid = oid;
//	}

	/**
	 * Answer whether the entity has an OID assigned by the underlying RDBMS.
	 * 
	 * @return true if the entity has an assigned OID.
	 */
	public boolean hasOid()
	{
		return (getOid() != null);
	}
}
