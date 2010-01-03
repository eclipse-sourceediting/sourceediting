/*******************************************************************************
 * Copyright (c) 2007,2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 214235 - Changed max value size to 2meg.
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.debugger;

/**
 * An abstract class that XSL processor implementations can subclass for their variables.
 * 
 * @author Doug Satchwell
 * @author David Carver
 */
public abstract class Variable
{
	private static int idSequence = 0;

	/**
	 * The constant denoting a local scope variable.
	 */
	public static final String LOCAL_SCOPE = "L"; //$NON-NLS-1$
	/**
	 * The constant denoting a tunnel scope variable.
	 */
	public static final String TUNNEL_SCOPE = "T"; //$NON-NLS-1$
	/**
	 * The constant denoting a global scope variable.
	 */
	public static final String GLOBAL_SCOPE = "G"; //$NON-NLS-1$

	protected final static int MAXIMUM_VALUE_SIZE = 2097152;

	protected static final String UNRESOLVED = "unresolved"; //$NON-NLS-1$
	protected static final String BOOLEAN = "boolean"; //$NON-NLS-1$
	protected static final String NUMBER = "number"; //$NON-NLS-1$
	protected static final String STRING = "string"; //$NON-NLS-1$
	protected static final String NODESET = "nodeset"; //$NON-NLS-1$
	protected static final String OBJECT = "object"; //$NON-NLS-1$
	protected static final String UNKNOWN = "unknown"; //$NON-NLS-1$

	protected final String name;
	protected final String scope;
	protected final int slotNumber;

	private int id;

	/**
	 * Create a new instance of this with the given name, scope and slot number.
	 * 
	 * @param name the name of this
	 * @param scope the scope of this
	 * @param slotNumber the slot number of this
	 */
	public Variable(String name, String scope, int slotNumber)
	{
		this.name = name;
		this.scope = scope;
		this.slotNumber = slotNumber;
		this.id = idSequence++;
	}

	/**
	 * Get the id used for this.
	 * 
	 * @return the variable id
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Get the variable name.
	 * 
	 * @return the variable name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the type of this variable e.g. string, or node
	 * 
	 * @return the variable type
	 */
	public abstract String getType();

	/**
	 * Get the scope of this variable - one of <code>LOCAL_SCOPE</code>, <code>TUNNEL_SCOPE</code> or <code>GLOBAL_SCOPE</code>.
	 * 
	 * @return the variable scope
	 */
	public String getScope()
	{
		return scope;
	}

	/**
	 * Get the current value of this variable as a String.
	 * 
	 * @return the value of this
	 */
	public abstract String getValue();

	/**
	 * Get the first line of the value truncating to <code>MAXIMUM_VALUE_SIZE</code> where necessary.
	 * 
	 * @return the first line of the value
	 */
	public String getValueFirstLine()
	{
		String value = getValue();
		if (value != null)
		{
			// make sure it is not too long
			value = value.replace('\n', '.');
			if (value.length() > MAXIMUM_VALUE_SIZE)
				value = value.substring(0, MAXIMUM_VALUE_SIZE);
		}
		else
			value = ""; //$NON-NLS-1$
		return value;
	}

	/**
	 * Get the slot number for this.
	 * 
	 * @return the slot number
	 */
	public int getSlotNumber()
	{
		return slotNumber;
	}

	public int hashCode()
	{
		return 3 * scope.hashCode() + 5 * slotNumber;
	}
	
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj instanceof Variable)
		{
			Variable v = (Variable) obj;
			return v.scope.equals(scope) && slotNumber == v.slotNumber;
		}
		return false;
	}

}
