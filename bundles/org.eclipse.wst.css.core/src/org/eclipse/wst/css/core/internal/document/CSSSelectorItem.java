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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorItem;

/**
 * 
 */
abstract class CSSSelectorItem implements ICSSSelectorItem {

	/**
	 * CSSSelectorItem constructor comment.
	 */
	public CSSSelectorItem() {
		super();
	}

	/**
	 * @return int
	 */
	public int getItemType() {
		return 0;
	}

	/**
	 * @return java.lang.String
	 */
	public String getString() {
		return null;
	}
}
