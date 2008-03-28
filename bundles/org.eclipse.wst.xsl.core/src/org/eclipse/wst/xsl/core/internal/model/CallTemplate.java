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
public class CallTemplate extends XSLElement
{
	final List<Parameter> parameters = new ArrayList<Parameter>();
	
	/**
	 * TODO: Add Javadoc
	 * @param stylesheet
	 * @param name
	 * @param match 
	 * @param mode 
	 */
	public CallTemplate(Stylesheet stylesheet)
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
	public List<Parameter> getParameters()
	{
		return parameters;
	}
}
