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
package org.eclipse.wst.css.core.internal.util;



import org.eclipse.wst.css.core.internal.document.CSSStyleDeclarationFactoryContext;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;


/**
 * 
 */
public class CSSStyleDeclarationFactory extends CSSStyleDeclarationFactoryContext {

	private static CSSStyleDeclarationFactory fInstance = null;

	/**
	 * CSSStyleDeclarationFactory constructor comment.
	 */
	private CSSStyleDeclarationFactory() {
		super();
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSStyleDeclaration
	 * @param decl
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSStyleDeclaration
	 */
	public ICSSStyleDeclaration createStyleDeclaration(ICSSStyleDeclaration decl) {
		if (decl == null) {
			return null;
		}
		ICSSStyleDeclaration newNode = createStyleDeclaration();

		if (newNode == null) {
			return null;
		}
		fOwnerDocument = newNode;
		cloneChildNodes(decl, newNode);
		return newNode;
	}

	/**
	 * 
	 */
	public synchronized static CSSStyleDeclarationFactory getInstance() {
		if (fInstance == null) {
			fInstance = new CSSStyleDeclarationFactory();
		}
		return fInstance;
	}
}
