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



import java.util.Iterator;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNamedNodeMap;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;


/**
 * 
 */
public class CSSNamedNodeMapImpl extends CSSNodeListImpl implements ICSSNamedNodeMap {

	/**
	 * CSSNamedNodeMapImpl constructor comment.
	 */
	CSSNamedNodeMapImpl() {
		super();
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 * @param name
	 *            java.lang.String
	 */
	public ICSSNode getNamedItem(String name) {
		Iterator it = nodes.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof CSSAttrImpl && ((CSSAttrImpl) obj).getName().compareToIgnoreCase(name) == 0) {
				return (ICSSNode) obj;
			}
		}
		return null;
	}
}
