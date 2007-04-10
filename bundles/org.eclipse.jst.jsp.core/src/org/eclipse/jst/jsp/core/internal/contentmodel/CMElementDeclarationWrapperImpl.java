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



import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

public class CMElementDeclarationWrapperImpl extends CMNodeWrapperImpl implements CMElementDeclaration {
	private CMContent fCMContent = null;

	protected CMElementDeclaration fElementDecl = null;

	/**
	 * CMElementDeclarationWrapper constructor comment.
	 * @param prefix java.lang.String
	 * @param node org.eclipse.wst.xml.core.internal.contentmodel.CMNode
	 */
	public CMElementDeclarationWrapperImpl(String prefix, CMElementDeclaration node) {
		super(prefix, node);
		fElementDecl = node;
	}

	/**
	 * getAttributes method
	 * @return CMNamedNodeMap
	 *
	 * Returns CMNamedNodeMap of AttributeDeclaration
	 */
	public CMNamedNodeMap getAttributes() {
		return fElementDecl.getAttributes();
	}

	/**
	 * getCMContent method
	 * @return CMContent
	 *
	 * Returns the root node of this element's content model.
	 * This can be an CMElementDeclaration or a CMGroup
	 */
	public CMContent getContent() {
		if (fCMContent == null) {
			CMContent content = fElementDecl.getContent();
			if (content == null)
				return null;
			if (content instanceof CMGroup)
				fCMContent = new CMGroupWrapperImpl(fPrefix, (CMGroup) content);
			else
				fCMContent = new CMContentWrapperImpl(fPrefix, content);
		}
		return fCMContent;
	}

	/**
	 * getContentType method
	 * @return int
	 *
	 * Returns one of :
	 * ANY, EMPTY, ELEMENT, MIXED, PCDATA, CDATA.
	 */
	public int getContentType() {
		return fElementDecl.getContentType();
	}

	/**
	 * getDataType method
	 * @return java.lang.String
	 */
	public CMDataType getDataType() {
		return fElementDecl.getDataType();
	}

	/**
	 * getElementName method
	 * @return java.lang.String
	 */
	public String getElementName() {
		return getNodeName();
	}

	/**
	 * getLocalElements method
	 * @return CMNamedNodeMap
	 *
	 * Returns a list of locally defined elements.
	 */
	public CMNamedNodeMap getLocalElements() {
		return fElementDecl.getLocalElements();
	}

	/**
	 * getMaxOccur method
	 * @return int
	 *
	 * If -1, it's UNBOUNDED.
	 */
	public int getMaxOccur() {
		return fElementDecl.getMaxOccur();
	}

	/**
	 * getMinOccur method
	 * @return int
	 *
	 * If 0, it's OPTIONAL.
	 * If 1, it's REQUIRED.
	 */
	public int getMinOccur() {
		return fElementDecl.getMinOccur();
	}

	public CMNode getOriginNode() {
		return fElementDecl;
	}
}
