/*******************************************************************************
 * Copyright (c) 2007, 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching;

/**
 * An output property supported by an XSLT processor. 
 * 
 * @author Doug Satchwell
 */
public interface IOutputProperty
{
	/**
	 * Get the URI of this output property. 
	 * @return a unique URI
	 */
	String getURI();

	/**
	 * Get a description for this output property. 
	 * @return a description
	 */
	String getDescription();
}
