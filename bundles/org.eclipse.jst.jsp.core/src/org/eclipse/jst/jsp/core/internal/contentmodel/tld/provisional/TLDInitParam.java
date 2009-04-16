/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
 * A name/value pair as an initialization param along with a description
 * @see JSP 1.2
 */
public interface TLDInitParam {
	String getDescription();

	/**
	 * The param-name element contains the name of a parameter.
	 */
	String getName();

	/**
	 * The param-value element contains the name of a parameter.
	 */
	String getValue();
}
