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
package org.eclipse.wst.xsl.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>xsl:template</code> model element.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class Template extends XSLElement
{
	final List<Variable> variables = new ArrayList<Variable>();
	final List<Parameter> parameters = new ArrayList<Parameter>();
	
	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet the stylesheet that this belongs to
	 */
	public Template(Stylesheet stylesheet)
	{
		super(stylesheet);
	}
	
	/**
	 * Add a parameter to this.
	 * 
	 * @param parameter the parameter to add
	 */
	public void addParameter(Parameter parameter)
	{
		parameters.add(parameter);
	}
	
	/**
	 * Add a variable to this.
	 * 
	 * @param var the variable to add
	 */
	public void addVariable(Variable var)
	{
		variables.add(var);
	}

	/**
	 * Get the value of the <code>name</code> attribute if one exists.
	 * 
	 * @return the template name, or null
	 */
	@Override
	public String getName()
	{
		return getAttributeValue("name"); //$NON-NLS-1$
	}
	
	/**
	 * Get the value of the <code>match</code> attribute if one exists.
	 * 
	 * @return the match value, or null
	 */
	public String getMatch()
	{
		return getAttributeValue("match"); //$NON-NLS-1$
	}
	
	/**
	 * Get the value of the <code>mode</code> attribute if one exists.
	 * 
	 * @return the mode value, or null
	 */
	public String getMode()
	{
		return getAttributeValue("mode"); //$NON-NLS-1$
	}
	
	/**
	 * Get the value of the <code>priority</code> attribute if one exists.
	 * 
	 * @return the priority value, or null
	 */
	public String getPriority()
	{
		return getAttributeValue("priority"); //$NON-NLS-1$
	}

	/**
	 * Get the list of parameters of this.
	 * 
	 * @return the list of parameters
	 */
	public List<Parameter> getParameters()
	{
		return parameters;
	}
	
	@Override
	public int hashCode()
	{
		String name = getName();
		if (name != null)
		{
			return 3 + name.hashCode();
		}
		String match = getMatch();
		String mode = getMode();
		String priority = getPriority();
		if (match != null)
		{
			int hash = 3*match.hashCode();
			if (priority != null)
				hash += 5*priority.hashCode();
			if (mode != null)
				hash += 7*mode.hashCode();
			return 5 + hash;
		}
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj instanceof Template)
		{
			Template includedTemplate = (Template)obj;
			if (!matchesByMatchOrName(includedTemplate))
				return false;
			// only possibility is that priority is different
			String priority1 = getPriority();
			String priority2 = includedTemplate.getPriority();
			if (priority1 == null && priority2 == null || priority1 != null && priority1.equals(priority2))
				return true;
		}
		return false;
	}
	
	public boolean matchesByMatchOrName(Template includedTemplate)
	{
		String name1 = getName();
		String match1 = getMatch();
		String mode1 = getMode();
		String name2 = includedTemplate.getName();
		String match2 = includedTemplate.getMatch();
		String mode2 = includedTemplate.getMode();
		if (name1 != null && name1.equals(name2))
			return true;
		if (match1 != null && match1.equals(match2) && (mode1 == null && mode2 == null || mode1 != null && mode1.equals(mode2)))
			return true;
		return false;
	}
	
	
	@Override
	public String toString()
	{
		String name = getName();
		String match = getMatch();
		return "file="+getStylesheet().getFile()+", name="+name+", match="+match; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public Type getModelType()
	{
		return Type.TEMPLATE;
	}

}
