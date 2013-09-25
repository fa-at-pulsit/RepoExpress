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
package com.strategicgains.repoexpress.domain;

import java.util.ArrayList;
import java.util.List;

import com.strategicgains.restexpress.common.util.ObjectUtils;
import com.strategicgains.restexpress.common.util.StringUtils;

/**
 * @author toddf
 * @since Aug 29, 2013
 */
public class CompoundIdentifier
implements Comparable<CompoundIdentifier>
{
	private List<Object> components = new ArrayList<Object>();

	public CompoundIdentifier(Object... components)
	{
		super();
		addComponents(components);
	}

	public void addComponents(Object... components)
    {
		for (Object component : components)
		{
			addComponent(component);
		}
    }

	public void addComponent(Object component)
    {
		if (!components.contains(component))
		{
			components.add(component);
		}
    }
	
	public int size()
	{
		return components.size();
	}

	@Override
	public boolean equals(Object that)
	{
		return (compareTo((CompoundIdentifier) that) == 0);
	}
	
	@Override
	public int hashCode()
	{
		return 1 + components.hashCode();
	}

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Override
    public int compareTo(CompoundIdentifier that)
    {
		if (that == null) return 1;
		if (this.size() < that.size()) return -1;
		if (this.size() > that.size()) return 1;

		int i = 0;
		int result = 0;

		while (result == 0 && i < size())
		{
			Object cThis = this.components.get(i);
			Object cThat = that.components.get(i);

			if (ObjectUtils.areComparable(cThis, cThat))
			{
				result = ((Comparable) cThis).compareTo(((Comparable) cThat));
			}
			else
			{
				result = (cThis.toString().compareTo(cThat.toString()));
			}
		}

	    return result;
    }

	@Override
	public String toString()
	{
		return "(" + StringUtils.join(", ", components) + ")";
	}
}
