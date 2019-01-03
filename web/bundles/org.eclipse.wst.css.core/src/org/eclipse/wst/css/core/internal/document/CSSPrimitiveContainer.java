/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;

/**
 * 
 */
abstract class CSSPrimitiveContainer extends CSSPrimitiveValueImpl {

	CSSPrimitiveContainer(CSSPrimitiveContainer that) {
		super(that);
	}

	CSSPrimitiveContainer(short primitiveType) {
		super(primitiveType);
	}

	protected abstract void initPrimitives();
	/**
	 * currently public but may be made default access protected in future.
	 */
	public void setOwnerDocument(ICSSDocument ownerDocument) {
		super.setOwnerDocument(ownerDocument);

		initPrimitives();
	}
}
