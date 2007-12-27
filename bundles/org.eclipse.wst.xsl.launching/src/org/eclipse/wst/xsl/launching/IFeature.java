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
package org.eclipse.wst.xsl.launching;

import org.eclipse.core.runtime.IStatus;

/**
 * A feature supported by an XSLT processor. 
 * 
 * @author Doug Satchwell
 */
public interface IFeature
{
	/**
	 * Constant for features of type <code>string</code>
	 * @see #getType()
	 */
	String TYPE_STRING = "string";
	
	/**
	 * Constant for features of type <code>boolean</code>
	 * @see #getType()
	 */
	String TYPE_BOOLEAN = "boolean";
	
	/**
	 * Constant for features of type <code>int</code>
	 * @see #getType()
	 */
	String TYPE_INT = "int";
	
	/**
	 * Constant for features of type <code>double</code>
	 * @see #getType()
	 */
	String TYPE_DOUBLE = "double";
	
	/**
	 * Constant for features of type <code>float</code>
	 * @see #getType()
	 */
	String TYPE_FLOAT = "float";
	
	/**
	 * Constant for features of type <code>class</code>
	 * @see #getType()
	 */
	String TYPE_CLASS = "class";
	
	/**
	 * Constant for features of type <code>object</code>
	 * @see #getType()
	 */
	String TYPE_OBJECT = "object";

	/**
	 * Get the URI for this feature
	 * @return the feature URI
	 */
	String getURI();

	/**
	 * Get a description for this feature
	 * @return the feature description
	 */
	String getDescription();

	/**
	 * Get the type of this feature. Will match one of the TYPE constants in this interface.
	 * @return the feature type: one of <code>TYPE_STRING</code>, <code>TYPE_BOOLEAN</code>, 
	 * <code>TYPE_INT</code>, <code>TYPE_DOUBLE</code>, <code>TYPE_FLOAT</code>, <code>TYPE_CLASS</code>, 
	 * or <code>TYPE_OBJECT</code>
	 */
	String getType();

	/**
	 * Determine whether the given value is a valid one for this feature.
	 * @return an <code>IStatus</code> with severity <code>OK</code> if valid, or <code>ERROR</code> if invalid.
	 */
	IStatus validateValue(String value);
}
