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



/**
 * 
 */
public interface ICSSNode {

	short ATTR_NODE = -1;
	short UNKNOWNRULE_NODE = 0;
	short STYLERULE_NODE = 1;
	short CHARSETRULE_NODE = 2;
	short IMPORTRULE_NODE = 3;
	short MEDIARULE_NODE = 4;
	short FONTFACERULE_NODE = 5;
	short PAGERULE_NODE = 6;
	short STYLESHEET_NODE = 7;
	short STYLEDECLARATION_NODE = 8;
	short STYLEDECLITEM_NODE = 9;
	short VALUELIST_NODE = 10;
	short PRIMITIVEVALUE_NODE = 11;
	short MEDIALIST_NODE = 12;
	short DOCUMENTCSSSTYLE_NODE = 13;
	short LINKSTYLE_NODE = 14;
	short ELEMENTCSSINLINESTYLE_NODE = 15;

	ICSSNode cloneNode(boolean deep);

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNamedNodeMap
	 */
	ICSSNamedNodeMap getAttributes();

	ICSSNodeList getChildNodes();

	ICSSNode getFirstChild();

	ICSSNode getLastChild();

	ICSSNode getNextSibling();

	/**
	 * @return short
	 */
	short getNodeType();

	ICSSDocument getOwnerDocument();

	ICSSNode getParentNode();

	ICSSNode getPreviousSibling();

	/**
	 * @return boolean
	 */
	boolean hasChildNodes();
}
