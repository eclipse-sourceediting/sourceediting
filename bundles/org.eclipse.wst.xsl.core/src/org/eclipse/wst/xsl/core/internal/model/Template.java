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
public class Template extends SourceArtifact
{
	final String name;
	final String match;
	final List<Variable> variables = new ArrayList<Variable>();
	final List<Parameter> parameters = new ArrayList<Parameter>();
	
	/**
	 * TODO: Add Javadoc
	 * @param sourceFile
	 * @param name
	 * @param match 
	 */
	public Template(SourceFile sourceFile, String name, String match)
	{
		super(sourceFile);
		this.name = name;
		this.match = match;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @param parameter
	 */
	public void addParameter(Parameter parameter)
	{
		parameters.add(parameter);
	}
	
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
		return name;
	}
	
	public String getMatch()
	{
		return match;
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public List<Parameter> getParameters()
	{
		return parameters;
	}
}
