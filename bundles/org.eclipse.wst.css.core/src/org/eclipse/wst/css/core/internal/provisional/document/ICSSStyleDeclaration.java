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
package org.eclipse.wst.css.core.internal.provisional.document;



import org.w3c.dom.css.CSSStyleDeclaration;

public interface ICSSStyleDeclaration extends ICSSDocument, CSSStyleDeclaration {

	ICSSStyleDeclItem getDeclItemNode(String propertyName);

	ICSSStyleDeclItem removeDeclItemNode(ICSSStyleDeclItem oldDecl) throws org.w3c.dom.DOMException;

	ICSSStyleDeclItem setDeclItemNode(ICSSStyleDeclItem newDecl) throws org.w3c.dom.DOMException;
}
