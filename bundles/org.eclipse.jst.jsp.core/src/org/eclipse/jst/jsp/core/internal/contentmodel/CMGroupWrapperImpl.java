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
package org.eclipse.jst.jsp.core.internal.contentmodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

public class CMGroupWrapperImpl extends CMContentWrapperImpl implements CMGroup {
	class CMNodeListImpl implements CMNodeList {
		private List nodes = null;

		/**
		 * CMNodeListImpl constructor comment.
		 */
		public CMNodeListImpl() {
			super();
			nodes = new ArrayList();
		}

		/**
		 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNode
		 * @param node org.eclipse.wst.xml.core.internal.contentmodel.CMNode
		 */
		public void appendItem(CMNode node) {
			nodes.add(node);
		}

		/**
		 * getLength method
		 * @return int
		 */
		public int getLength() {
			return nodes.size();
		}

		/**
		 * item method
		 * @return CMNode
		 * @param index int
		 */
		public CMNode item(int index) {
			if (index < 0 || index >= nodes.size())
				return null;
			return (CMNode) nodes.get(index);
		}
	}

	private CMNodeList fChildNodes = null;
	private CMGroup fGroup = null;

	/**
	 * CMGroupWrapper constructor comment.
	 * @param prefix java.lang.String
	 * @param node org.eclipse.wst.xml.core.internal.contentmodel.CMContent
	 */
	public CMGroupWrapperImpl(String prefix, CMGroup node) {
		super(prefix, node);
	}

	/**
	 * getChildNodes method
	 * @return CMNodeList
	 *
	 * Returns child CMNodeList, which includes ElementDefinition or CMElement.
	 */
	public CMNodeList getChildNodes() {
		if (fChildNodes == null) {
			CMNodeListImpl childNodes = new CMNodeListImpl();
			CMNodeList children = fGroup.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				CMNode child = children.item(i);
				if (child instanceof CMGroup)
					childNodes.appendItem(new CMGroupWrapperImpl(fPrefix, (CMGroup) child));
				else if (child instanceof CMElementDeclaration)
					childNodes.appendItem(new CMElementDeclarationWrapperImpl(fPrefix, (CMElementDeclaration) child));
				else
					// error?
					childNodes.appendItem(new CMNodeWrapperImpl(fPrefix, child));
			}
			fChildNodes = childNodes;
		}
		return fChildNodes;
	}

	/**
	 * getOperation method
	 * @return int
	 *
	 * Returns one of :
	 * ALONE (a), SEQUENCE (a,b), CHOICE (a|b), ALL (a&b).
	 */
	public int getOperator() {
		return fGroup.getOperator();
	}

	public CMNode getOriginNode() {
		return fGroup;
	}
}
