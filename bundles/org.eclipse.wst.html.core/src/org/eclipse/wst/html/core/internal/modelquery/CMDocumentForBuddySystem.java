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

import java.util.Iterator;

import org.eclipse.wst.html.core.internal.contentmodel.HTMLCMDocumentFactory;
import org.eclipse.wst.html.core.internal.contentmodel.ssi.SSICMDocumentFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocType;

/**
 */
class CMDocumentForBuddySystem extends CMNodeBuddySystem implements CMDocument {


	private static CMDocument getHTMLCMDocument() {
		return HTMLCMDocumentFactory.getCMDocument(CMDocType.HTML_DOC_TYPE);
	}

	class Elements extends CMNamedNodeMapForBuddySystem {
		public Elements(CMNamedNodeMap elements, boolean isXHTML) {
			super(isXHTML);
			makeBuddySystem(elements);
			addSSIDecls();
		}

		protected String getKeyName(CMNode original) {
			CMElementDeclaration edecl = getDecl(original);
			if (edecl == null)
				return null;
			return edecl.getElementName();
		}

		protected CMNode createBuddySystem(CMNode original) {
			CMElementDeclaration edecl = getDecl(original);
			return new CMElementDeclarationBuddySystem(edecl, isXHTML());
		}

		private CMElementDeclaration getDecl(CMNode cmnode) {
			if (cmnode == null)
				return null;
			if (cmnode.getNodeType() != CMNode.ELEMENT_DECLARATION)
				return null;
			return (CMElementDeclaration) cmnode;
		}

		private void addSSIDecls() {
			CMDocument ssi = SSICMDocumentFactory.getCMDocument();
			if (ssi == null)
				return;
			CMNamedNodeMap elements = ssi.getElements();
			Iterator i = elements.iterator();
			while (i.hasNext()) {
				CMElementDeclaration decl = (CMElementDeclaration) i.next();
				if (decl == null)
					continue;
				put(decl.getElementName(), decl);
			}
		}
	}

	private Elements elements = null;

	public CMDocumentForBuddySystem(CMDocument self, boolean isXHTML) {
		super(self, getHTMLCMDocument(), isXHTML);
	}

	/*
	 * @see CMDocument#getElements()
	 */
	public CMNamedNodeMap getElements() {
		if (elements != null)
			return elements;
		CMDocument cmdoc = getSelf();
		if (cmdoc == null)
			return null;
		elements = new Elements(cmdoc.getElements(), isXHTML);
		return elements;
	}

	/*
	 * @see CMDocument#getEntities()
	 */
	public CMNamedNodeMap getEntities() {
		CMDocument cmdoc = getSelf();
		if (cmdoc == null)
			return null;
		return cmdoc.getEntities();
	}

	/*
	 * @see CMDocument#getNamespace()
	 */
	public CMNamespace getNamespace() {
		CMDocument cmdoc = getSelf();
		if (cmdoc == null)
			return null;
		return cmdoc.getNamespace();
	}

	private CMDocument getSelf() {
		if (self.getNodeType() != CMNode.DOCUMENT)
			return null;
		return (CMDocument) self;
	}
}
