/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.launching;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.wst.xsl.launching.IFeature;

public class Feature implements IFeature, Comparable
{
	private final String uri;
	private final String description;
	private final String type;

	public Feature(String uri, String type, String description)
	{
		this.uri = uri;
		this.type = type;
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public String getType()
	{
		return type;
	}

	public String getURI()
	{
		return uri;
	}

	public IStatus validateValue(String value)
	{
		IStatus status = null;
		if (TYPE_BOOLEAN.equals(type))
		{
			boolean valid = "true".equals(value) || "false".equals(value);
			if (!valid)
				status = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, 0, "Valid values are 'true' or 'false'", null);
		}
		else if (TYPE_INT.equals(type))
		{
			try
			{
				Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				status = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, 0, "Value must be an integer", null);
			}
		}
		else if (TYPE_DOUBLE.equals(type))
		{
			try
			{
				Double.parseDouble(value);
			}
			catch (NumberFormatException e)
			{
				status = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, 0, "Value must be a double", null);
			}
		}
		else if (TYPE_FLOAT.equals(type))
		{
			try
			{
				Float.parseFloat(value);
			}
			catch (NumberFormatException e)
			{
				status = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, 0, "Value must be a float", null);
			}
		}
		else if (TYPE_CLASS.equals(type) || TYPE_OBJECT.equals(type))
		{
			status = JavaConventions.validateJavaTypeName(value);
		}
		return status;
	}

	public int compareTo(Object o)
	{
		if (o instanceof IFeature)
		{
			IFeature f = (IFeature) o;
			return f.getURI().compareTo(getURI());
		}
		return 0;
	}
}
