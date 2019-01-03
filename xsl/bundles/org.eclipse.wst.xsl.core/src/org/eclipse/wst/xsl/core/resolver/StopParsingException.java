/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Carver - STAR
 *******************************************************************************/
package org.eclipse.wst.xsl.core.resolver;

import org.xml.sax.SAXException;

/**
 * An exception indicating that the parsing should stop. This is usually
 * triggered when the top-level element has been found.
 * 
 * @since 1.0
 */

class StopParsingException extends SAXException {
	/**
	 * All serializable objects should have a stable serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an instance of <code>StopParsingException</code> with a
	 * <code>null</code> detail message.
	 */
	public StopParsingException() {
		super((String) null);
	}
}