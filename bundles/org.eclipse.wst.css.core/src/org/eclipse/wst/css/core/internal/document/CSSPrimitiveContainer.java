/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.document.ICSSDocument;

/**
 * 
 */
abstract class CSSPrimitiveContainer extends CSSPrimitiveValueImpl {

	/**
	 * CSSRegionContainer constructor comment.
	 * 
	 * @param that
	 *            com.ibm.sed.css.treemodel.CSSPrimitiveValueImpl
	 */
	CSSPrimitiveContainer(CSSPrimitiveContainer that) {
		super(that);
	}

	/**
	 * CSSRegionContainer constructor comment.
	 * 
	 * @param primitiveType
	 *            short
	 */
	CSSPrimitiveContainer(short primitiveType) {
		super(primitiveType);
	}

	/**
	 * 
	 */
	protected abstract void initPrimitives();

	/**
	 * @param ownerDocument
	 *            com.ibm.sed.css.interfaces.ICSSDocument
	 */
	void setOwnerDocument(ICSSDocument ownerDocument) {
		super.setOwnerDocument(ownerDocument);

		initPrimitives();
	}
}