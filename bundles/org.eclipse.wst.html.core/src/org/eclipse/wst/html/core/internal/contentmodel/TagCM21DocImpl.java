/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
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
 * Implementation of CMDocument for the JSP 2.1 tag files.
 */
class TagCM21DocImpl extends JCMDocImpl {
	/**
	 * HCMDocImpl constructor comment.
	 */
	public TagCM21DocImpl(String docTypeName, CMNamespaceImpl targetNamespace) {
		super(docTypeName, targetNamespace, new Tag21ElementCollection());
	}
}
