/*
	Copyright 2012 Strategic Gains, Inc.

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
import org.eclipse.persistence.sessions.Project;

/**
 * A template EclipseLink project that provides a hook to configure descriptors
 * via configureDescriptors().
 * 
 * @author toddf
 * @since Sep 5, 2012
 */
@SuppressWarnings("serial")
public abstract class AbstractProject
extends Project
{
	// SECTION: CONSTRUCTORS

	public AbstractProject()
	{
		super();
		configureDescriptors();
	}

	public AbstractProject(Login login)
	{
		super(login);
		configureDescriptors();
	}

	public AbstractProject(DatabaseLogin login)
	{
		super(login);
		configureDescriptors();
	}

	
	// SECTION: SUB-CLASS RESPONSIBILITIES

	protected abstract void configureDescriptors();
}
