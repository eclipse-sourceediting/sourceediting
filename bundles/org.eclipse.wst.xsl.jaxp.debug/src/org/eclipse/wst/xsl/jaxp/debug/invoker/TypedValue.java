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
package org.eclipse.wst.xsl.jaxp.debug.invoker;

import org.eclipse.wst.xsl.jaxp.debug.invoker.internal.CreationException;

/**
 * A value that is to be instantiated from a particular type e.g. Double, Object.
 * 
 * @author Doug Satchwell
 */
public class TypedValue
{
	public static final String TYPE_STRING = "string"; //$NON-NLS-1$
	private static final String TYPE_BOOLEAN = "boolean"; //$NON-NLS-1$
	private static final String TYPE_INT = "int"; //$NON-NLS-1$
	private static final String TYPE_DOUBLE = "double"; //$NON-NLS-1$
	private static final String TYPE_FLOAT = "float"; //$NON-NLS-1$
	private static final String TYPE_CLASS = "class"; //$NON-NLS-1$
	private static final String TYPE_OBJECT = "object"; //$NON-NLS-1$

	final String uri;
	final String type;
	final String value;

	/**
	 * Create a new instance of this from the given information. 
	 * 
	 * @param uri the URI
	 * @param type the type of value
	 * @param value the value
	 */
	public TypedValue(String uri, String type, String value)
	{
		this.uri = uri;
		this.type = type;
		this.value = value;
	}

	/**
	 * Create the type of object defined by this. 
	 * 
	 * @return the value
	 * @throws CreationException if a problem occurred
	 */
	public Object createValue() throws CreationException
	{
		Object o = null;
		if (TYPE_STRING.equals(type))
		{
			o = value;
		}
		else if (TYPE_BOOLEAN.equals(type))
		{
			boolean b = "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value); //$NON-NLS-1$ //$NON-NLS-2$
			o = new Boolean(b);
		}
		else if (TYPE_INT.equals(type))
		{
			try
			{
				o = new Integer(value);
			}
			catch (NumberFormatException e)
			{
				throw new CreationException(Messages.TypedValue_9 + value + Messages.TypedValue_10, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else if (TYPE_DOUBLE.equals(type))
		{
			try
			{
				o = new Double(value);
			}
			catch (NumberFormatException e)
			{
				throw new CreationException(Messages.TypedValue_11 + value + Messages.TypedValue_12, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else if (TYPE_FLOAT.equals(type))
		{
			try
			{
				o = new Float(value);
			}
			catch (NumberFormatException e)
			{
				throw new CreationException(Messages.TypedValue_13 + value + Messages.TypedValue_14, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else if (TYPE_CLASS.equals(type))
		{
			try
			{
				o = Class.forName(value);
			}
			catch (ClassNotFoundException e)
			{
				throw new CreationException(Messages.TypedValue_15 + value + Messages.TypedValue_16, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else if (TYPE_OBJECT.equals(type))
		{
			try
			{
				Class c = Class.forName(value);
				o = c.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				throw new CreationException(Messages.TypedValue_17 + value + Messages.TypedValue_18, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (InstantiationException e)
			{
				throw new CreationException(Messages.TypedValue_19 + value + Messages.TypedValue_20, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (IllegalAccessException e)
			{
				throw new CreationException(Messages.TypedValue_21 + value + Messages.TypedValue_22, e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else
		{
			throw new CreationException(Messages.TypedValue_23 + type + Messages.TypedValue_24); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return o;
	}
}