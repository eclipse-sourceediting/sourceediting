/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.contentassist;

import org.eclipse.wst.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.contentmodel.CMElementDeclaration;
import org.eclipse.wst.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.contentmodel.CMNode;
import org.eclipse.wst.contentmodel.util.DOMNamespaceHelper;
import org.w3c.dom.Node;

public abstract class AbstractContentModelGenerator {

	public static boolean generateChildren = false;

	public AbstractContentModelGenerator() {
		super();
	}

	public abstract void generateAttribute(CMAttributeDeclaration attrDecl, StringBuffer buffer);

	protected void generateAttributes(CMElementDeclaration elementDecl, StringBuffer buffer) {
		CMNamedNodeMap attributes = elementDecl.getAttributes();
		if (attributes == null)
			return;
		for (int i = 0; i < attributes.getLength(); i++) {
			generateAttribute((CMAttributeDeclaration) attributes.item(i), buffer);
		}
		return;
	}

	protected abstract void generateEndTag(String tagName, Node parentNode, CMElementDeclaration elementDecl, StringBuffer buffer);

	public void generateRequiredChildren(Node parentNode, CMElementDeclaration elementDecl, StringBuffer buffer) {
		if (generateChildren) {
		}
		return;
	}

	protected abstract void generateStartTag(String tagName, Node parentNode, CMElementDeclaration elementDecl, StringBuffer buffer);

	public void generateTag(Node parent, CMElementDeclaration elementDecl, StringBuffer buffer) {
		if (elementDecl == null || buffer == null)
			return;

		String tagName = getRequiredName(parent, elementDecl);

		generateStartTag(tagName, parent, elementDecl, buffer);
		generateRequiredChildren(parent, elementDecl, buffer);
		generateEndTag(tagName, parent, elementDecl, buffer);
		return;
	}

	public abstract int getMinimalStartTagLength(Node node, CMElementDeclaration elementDecl);

	public String getRequiredName(Node ownerNode, CMNode cmnode) {
		if (ownerNode != null) {
			return DOMNamespaceHelper.computeName(cmnode, ownerNode, null);
		}
		return cmnode.getNodeName();
	}

	public abstract String getStartTagClose(Node parentNode, CMElementDeclaration elementDecl);

	protected boolean requiresAttributes(CMElementDeclaration ed) {
		CMNamedNodeMap attributes = ed.getAttributes();
		if (attributes == null)
			return false;
		for (int i = 0; i < attributes.getLength(); i++) {
			if (((CMAttributeDeclaration) attributes.item(i)).getUsage() == CMAttributeDeclaration.REQUIRED)
				return true;
		}
		return false;
	}
}
