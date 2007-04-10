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
package org.eclipse.wst.html.core.internal.modelquery;


import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

/**
 */
class CMElementDeclarationBuddySystem extends CMNodeBuddySystem implements CMElementDeclaration {


	private static CMDocument htmlcm = HTMLCMDocumentFactory.getCMDocument(CMDocType.HTML_DOC_TYPE);

	private static CMElementDeclaration getHTMLCMElemDecl(CMElementDeclaration original) {
		CMElementDeclaration buddy = null;
		if (htmlcm != null) {
			CMNamedNodeMap elems = htmlcm.getElements();
			if (elems != null) {
				buddy = (CMElementDeclaration) elems.getNamedItem(original.getElementName());
			}
		}
		return buddy;
	}

	private class Attrs extends CMNamedNodeMapForBuddySystem {
		private CMNamedNodeMap buddyAttrs = null;

		public Attrs(CMNamedNodeMap attrs, CMNamedNodeMap buddyAttrs, boolean isXHTML) {
			super(isXHTML);
			this.buddyAttrs = buddyAttrs;
			makeBuddySystem(attrs);
		}

		protected String getKeyName(CMNode original) {
			CMAttributeDeclaration adecl = getDecl(original);
			if (adecl == null)
				return null;
			return adecl.getAttrName();
		}

		protected CMNode createBuddySystem(CMNode original) {
			CMAttributeDeclaration adecl = getDecl(original);
			if (adecl == null)
				return null;
			CMAttributeDeclaration buddy = null;
			if (buddyAttrs != null) {
				buddy = (CMAttributeDeclaration) buddyAttrs.getNamedItem(adecl.getAttrName());
			}
			return new CMAttributeDeclarationBuddySystem(adecl, buddy, isXHTML());
		}

		private CMAttributeDeclaration getDecl(CMNode cmnode) {
			if (cmnode == null)
				return null;
			if (cmnode.getNodeType() != CMNode.ATTRIBUTE_DECLARATION)
				return null;
			return (CMAttributeDeclaration) cmnode;
		}
	}

	private Attrs attributes = null;

	public CMElementDeclarationBuddySystem(CMElementDeclaration self, boolean isXHTML) {
		super(self, getHTMLCMElemDecl(self), isXHTML);
	}

	/*
	 * @see CMElementDeclaration#getAttributes()
	 */
	public CMNamedNodeMap getAttributes() {
		if (attributes != null)
			return attributes;
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return null;
		CMElementDeclaration htmlDecl = getBuddy();
		CMNamedNodeMap htmlAttrs = (htmlDecl == null) ? null : htmlDecl.getAttributes();
		attributes = new Attrs(edecl.getAttributes(), htmlAttrs, isXHTML);
		return attributes;
	}

	/*
	 * @see CMElementDeclaration#getContent()
	 */
	public CMContent getContent() {
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return null;
		return edecl.getContent();
	}

	/*
	 * @see CMElementDeclaration#getContentType()
	 */
	public int getContentType() {
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return CMElementDeclaration.ANY;
		return edecl.getContentType();
	}

	/*
	 * @see CMElementDeclaration#getElementName()
	 */
	public String getElementName() {
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return null;
		return edecl.getElementName();
	}

	/*
	 * @see CMElementDeclaration#getDataType()
	 */
	public CMDataType getDataType() {
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return null;
		return edecl.getDataType();
	}

	/*
	 * @see CMElementDeclaration#getLocalElements()
	 */
	public CMNamedNodeMap getLocalElements() {
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return null;
		return edecl.getLocalElements();
	}

	/*
	 * @see CMContent#getMaxOccur()
	 */
	public int getMaxOccur() {
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return -1;
		return edecl.getMaxOccur();
	}

	/*
	 * @see CMContent#getMinOccur()
	 */
	public int getMinOccur() {
		CMElementDeclaration edecl = getSelf();
		if (edecl == null)
			return -1;
		return edecl.getMinOccur();
	}

	/*
	 * @see CMNode#supports(String)
	 */
	public boolean supports(String propertyName) {
		if (isXHTML && propertyName.equals(HTMLCMProperties.OMIT_TYPE))
			return true;
		return super.supports(propertyName);
	}

	/*
	 * @see CMNode#getProperty(String)
	 */
	public Object getProperty(String propertyName) {
		if (isXHTML && propertyName.equals(HTMLCMProperties.OMIT_TYPE))
			return HTMLCMProperties.Values.OMIT_NONE;
		return super.getProperty(propertyName);
	}

	private CMElementDeclaration getSelf() {
		if (self.getNodeType() != CMNode.ELEMENT_DECLARATION)
			return null;
		return (CMElementDeclaration) self;
	}

	private CMElementDeclaration getBuddy() {
		if (buddy == null)
			return null;
		if (buddy.getNodeType() != CMNode.ELEMENT_DECLARATION)
			return null;
		return (CMElementDeclaration) buddy;
	}
}
