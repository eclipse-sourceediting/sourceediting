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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

/**
 */
class CMGroupImpl extends CMContentImpl implements CMGroup {

	private int operator = CMGroup.SEQUENCE;
	private CMNodeListImpl children = null;

	/**
	 * CMGroupImpl constructor comment.
	 */
	public CMGroupImpl(int op, int minOccur, int maxOccur) {
		super(null, minOccur, maxOccur);
		switch (op) {
			case CMGroup.ALL :
			case CMGroup.CHOICE :
			case CMGroup.SEQUENCE :
				operator = op;
				break;
			default :
				// should warn.
				break;
		}
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 * @param org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 */
	protected CMNode appendChild(CMNode child) {
		if (child == null)
			return null;
		if (children == null)
			children = new CMNodeListImpl();
		return children.appendNode(child);
	}

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList
	 */
	public CMNodeList getChildNodes() {
		return children;
	}

	/**
	 * getNodeType method
	 * @return int
	 *
	 * Returns one of :
	 * ELEMENT_DECLARATION, ATTRIBUTE_DECLARATION, GROUP, ENTITY_DECLARATION.
	 */
	public int getNodeType() {
		return CMNode.GROUP;
	}

	/**
	 * getOperation method
	 * @return int
	 *
	 * Returns one of :
	 * ALONE (a), SEQUENCE (a,b), CHOICE (a|b), ALL (a&b).
	 */
	public int getOperator() {
		return operator;
	}
}
