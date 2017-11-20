/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

/**
 * Implementation of CMDocument for the JSP 2.0 tag files.
 */
class TagCMDocImpl extends JCMDocImpl {
	/**
	 * HCMDocImpl constructor comment.
	 */
	public TagCMDocImpl(String docTypeName, CMNamespaceImpl targetNamespace) {
		super(docTypeName, targetNamespace, new Tag20ElementCollection());
	}
}
