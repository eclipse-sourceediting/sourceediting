/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.1
 */
public class Function extends XSLElement {

	final List<Variable> variables = new ArrayList<Variable>();
	final List<Parameter> parameters = new ArrayList<Parameter>();

	/**
	 * Return the variables defined in this function.
	 * @return
	 */
	public List<Variable> getVariables() {
		return variables;
	}

	/**
	 * Return the parameters defined in this function.
	 * @return
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	public Function(Stylesheet stylesheet) {
		super(stylesheet);
	}

	@Override
	public Type getModelType() {
		return Type.FUNCTION;
	}

	/**
	 * Get the value of the <code>name</code> attribute if one exists.
	 * 
	 * @return the function name, or null
	 */
	@Override
	public String getName() {
		return getAttributeValue("name"); //$NON-NLS-1$
	}

	/**
	 * Add a parameter to this.
	 * 
	 * @param parameter
	 *            the parameter to add
	 */
	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}

	/**
	 * Add a variable to this.
	 * 
	 * @param var
	 *            the variable to add
	 */
	public void addVariable(Variable var) {
		variables.add(var);
	}
	
	public String getOverRideValue() {
		return getAttributeValue("override"); //$NON-NLS-1$
	}
	
	public String getAsValue() {
		return getAttributeValue("as"); //$NON-NLS-1$
	}
}
