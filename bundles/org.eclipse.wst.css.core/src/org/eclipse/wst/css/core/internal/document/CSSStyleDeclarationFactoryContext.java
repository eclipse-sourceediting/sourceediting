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
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;

public abstract class CSSStyleDeclarationFactoryContext {

	protected ICSSDocument fOwnerDocument = null;

	/**
	 * 
	 */
	protected void cloneChildNodes(ICSSNode src, ICSSNode dest) {
		if (fOwnerDocument == null) {
			return;
		}
		if (!(dest instanceof CSSNodeImpl)) {
			return;
		}

		CSSNodeImpl container = (CSSNodeImpl) dest;
		container.removeChildNodes();

		for (ICSSNode child = src.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child instanceof CSSStyleDeclItemImpl) {
				// extract shorthand property ..
				// --> Temp
				ICSSNode cloned = child.cloneNode(false);
				if (cloned instanceof CSSNodeImpl) {
					((CSSNodeImpl) cloned).setOwnerDocument(fOwnerDocument);
					container.appendChild((CSSNodeImpl) cloned);
					cloneChildNodes(child, cloned);
				}
				// <-- Temp
			}
			else {
				ICSSNode cloned = child.cloneNode(false);
				if (cloned instanceof CSSNodeImpl) {
					((CSSNodeImpl) cloned).setOwnerDocument(fOwnerDocument);
					container.appendChild((CSSNodeImpl) cloned);
					cloneChildNodes(child, cloned);
				}
			}
		}
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSStyleDeclaration
	 */
	public ICSSStyleDeclaration createStyleDeclaration() {
		CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(true);
		decl.setOwnerDocument(decl);
		return decl;
	}

}
