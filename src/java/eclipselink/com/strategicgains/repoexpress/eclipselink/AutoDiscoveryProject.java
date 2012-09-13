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
package com.strategicgains.repoexpress.eclipselink;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Login;

/**
 * @author toddf
 * @since Sep 4, 2012
 */
@SuppressWarnings("serial")
public class AutoDiscoveryProject
extends ConventionBasedProject
{
	public AutoDiscoveryProject(String... packages)
	{
		super();
		initialize(packages);
	}

	/**
	 * @param login
	 */
	public AutoDiscoveryProject(Login login, String... packages)
	{
		super(login);
		initialize(packages);
	}

	/**
	 * @param login
	 */
	public AutoDiscoveryProject(DatabaseLogin login, String... packages)
	{
		super(login);
		initialize(packages);
	}

	/**
	 * @param packages
	 */
    private void initialize(String[] packages)
    {
    	for (String entityPackage : packages)
    	{
    		// find the entity descriptor(s) in the package
    		// configure the descriptor(s)
    	}
    }

	/* (non-Javadoc)
	 * @see com.strategicgains.activemapperj.ConventionBasedProject#configureDynamicDescriptors()
	 */
    @Override
    protected void configureDynamicDescriptors()
    {
	    // TODO Auto-generated method stub
    }
}
