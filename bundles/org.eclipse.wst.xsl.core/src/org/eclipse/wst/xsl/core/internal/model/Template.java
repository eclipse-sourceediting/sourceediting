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
package org.eclipse.wst.xsl.core.internal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public class Template extends XSLElement
{
	final List<Variable> variables = new ArrayList<Variable>();
	final List<Parameter> parameters = new ArrayList<Parameter>();
	
	/**
	 * TODO: Add Javadoc
	 * @param stylesheet
	 * @param name
	 * @param match 
	 * @param mode 
	 */
	public Template(Stylesheet stylesheet)
	{
		super(stylesheet);
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param parameter
	 */
	public void addParameter(Parameter parameter)
	{
		parameters.add(parameter);
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param var
	 */
	public void addVariable(Variable var)
	{
		variables.add(var);
	}

	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public String getName()
	{
		return getAttributeValue("name"); //$NON-NLS-1$
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public String getMatch()
	{
		return getAttributeValue("match"); //$NON-NLS-1$
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public String getMode()
	{
		return getAttributeValue("mode"); //$NON-NLS-1$
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public List<Parameter> getParameters()
	{
		return parameters;
	}

	public boolean conflictsWith(Template includedTemplate)
	{
		if (includedTemplate == this)
			return false;
		
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
		return "file="+getStylesheet().getFile()+", name="+name+", match="+match;
	}
}
