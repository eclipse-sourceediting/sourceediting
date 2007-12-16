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

public interface IFeature
{
	String TYPE_STRING = "string";
	String TYPE_BOOLEAN = "boolean";
	String TYPE_INT = "int";
	String TYPE_DOUBLE = "double";
	String TYPE_FLOAT = "float";
	String TYPE_CLASS = "class";
	String TYPE_OBJECT = "object";

	String getURI();

	String getDescription();

	String getType();

	IStatus validateValue(String value);
}
