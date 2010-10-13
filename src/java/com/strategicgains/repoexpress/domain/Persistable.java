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
package com.strategicgains.repoexpress.domain;

import java.util.Date;

/**
 * Abstract Java class for a Persistable complex type--a type that can be stored in a Repository.
 */
public abstract class Persistable
implements Identifiable
{
	private String id;
	private Date createdAt;
	private Date lastUpdatedAt;

	/**
	 * Gets the value of the id property.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the value of the id property.
	 */
	public void setId(String value)
	{
		this.id = value;
	}

	/**
	 * Gets the value of the lastUpdatedAt property.
	 */
	public Date getCreatedAt()
	{
		return createdAt;
	}

	/**
	 * Sets the value of the lastUpdatedAt property.
	 */
	public void setCreatedAt(Date value)
	{
		this.createdAt = (value == null ? null : new Date(value.getTime()));
	}

	/**
	 * Gets the value of the lastUpdatedAt property.
	 */
	public Date getLastUpdatedAt()
	{
		return lastUpdatedAt;
	}

	/**
	 * Sets the value of the lastUpdatedAt property.
	 */
	public void setLastUpdatedAt(Date value)
	{
		this.lastUpdatedAt = (value == null ? null : new Date(value.getTime()));
	}
}
