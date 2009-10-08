/*******************************************************************************
 * Copyright (c) 2009 Chase Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwel (Chase Technologies) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.model;

import org.eclipse.core.runtime.PlatformObject;

/**
 * 
 * @since 1.0
 *
 */
public abstract class XSLModelObject extends PlatformObject
{
	public enum Type {STYLESHEET_MODEL,IMPORT,INCLUDE,TEMPLATE,VARIABLE, CALL_TEMPLATE, STYLESHEET, ATTRIBUTE, OTHER_ELEMENT,
	/**
	 * @since 1.1
	 */
	PARAM,
	/**
	 * @since 1.1
	 */
	FUNCTION};
	
	public abstract Type getModelType();
}
