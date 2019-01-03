/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



/**
 * JSP extension for CMDocument.
 * This interface provides some short hand methods to get declarations
 * by a name.
 */
public interface JSPCMDocument extends org.eclipse.wst.xml.core.internal.contentmodel.CMDocument {

	/**
	 * A short hand method to get a element declaration for a JSP element.
	 * JSP declaration class implements HTMLElementDeclaration interface.
	 * @param elementName java.lang.String
	 */
	HTMLElementDeclaration getElementDeclaration(String elementName);
}
