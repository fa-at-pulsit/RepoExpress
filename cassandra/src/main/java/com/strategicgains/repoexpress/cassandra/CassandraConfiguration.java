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

import com.netflix.astyanax.AstyanaxConfiguration;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.ConnectionPoolMonitor;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

/**
 * @author toddf
 * @since Apr 12, 2013
 */
public class CassandraConfiguration
{
	private String clusterName;
	private String keyspaceName;
	private AstyanaxConfiguration astyanax;
	private ConnectionPoolConfiguration poolConfig;
	private ConnectionPoolMonitor poolMonitor = new CountingConnectionPoolMonitor();

	public String getClusterName()
	{
		return clusterName;
	}

	public void setClusterName(String clusterName)
	{
		this.clusterName = clusterName;
	}

	public AstyanaxConfiguration getAstyanax()
	{
		return astyanax;
	}

	public void setAstyanax(AstyanaxConfiguration astyanax)
	{
		this.astyanax = astyanax;
	}

	public ConnectionPoolConfiguration getPoolConfig()
	{
		return poolConfig;
	}

	public void setPoolConfig(ConnectionPoolConfiguration poolConfig)
	{
		this.poolConfig = poolConfig;
	}

	public ConnectionPoolMonitor getPoolMonitor()
	{
		return poolMonitor;
	}

	public void setPoolMonitor(ConnectionPoolMonitor poolMonitor)
	{
		this.poolMonitor = poolMonitor;
	}

	public String getKeyspaceName()
	{
		return keyspaceName;
	}

	public void setKeyspaceName(String keyspaceName)
	{
		this.keyspaceName = keyspaceName;
	}

	public AstyanaxContext<Keyspace> buildContext()
    {
		return new AstyanaxContext.Builder()
		.forKeyspace(keyspaceName)
		.withAstyanaxConfiguration(astyanax)
		.withConnectionPoolConfiguration(poolConfig)
		.withConnectionPoolMonitor(poolMonitor)
		.buildKeyspace(ThriftFamilyFactory.getInstance());
    }
}
