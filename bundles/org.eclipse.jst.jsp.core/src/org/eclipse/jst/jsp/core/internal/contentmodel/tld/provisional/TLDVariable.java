/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional;

/**
 * Information on the scripting variables defined by this tag.
 * 
 * @see JSP 1.2
 */
public interface TLDVariable {

	/**
	 * Whether the variable is declared or not, true is the default.
	 */
	boolean getDeclare();

	/**
	 * @returnthe the description for this variable
	 */
	String getDescription();

	/**
	 * The name of an attribute whose (translation time) value will give the
	 * name of the variable, or null of the name is not to be obtained this
	 * way.
	 */
	String getNameFromAttribute();

	/**
	 * The variable name given as a constant, or null of the name is not
	 * specified.
	 */
	String getNameGiven();

	/**
	 * The scope of the scripting variable defined.
	 */
	String getScope();

	/**
	 * Name of the class of the variable, java.lang.String if null
	 */
	String getVariableClass();

	/**
	 * A locally scoped attribute to hold the value of this variable
	 */
	String getAlias();
}
