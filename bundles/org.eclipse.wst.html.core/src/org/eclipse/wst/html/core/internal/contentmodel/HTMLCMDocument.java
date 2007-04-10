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
package org.eclipse.wst.html.core.internal.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;



/**
 * HTML extension for CMDocument.
 * This interface provides some short hand methods to get declarations
 * by a name.
 */
public interface HTMLCMDocument extends CMDocument {

	/**
	 * A short hand method to get a element declaration for a HTML element.<br>
	 * @param elementName java.lang.String
	 */
	HTMLElementDeclaration getElementDeclaration(String elementName);

	/**
	 * A short hand method to get a entity declaration of HTML documents.<br>
	 * @param entityName java.lang.String
	 */
	HTMLEntityDeclaration getEntityDeclaration(String entityName);
}
