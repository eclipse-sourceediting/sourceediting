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
import org.eclipse.wst.contentmodel.CMDataType;
import org.eclipse.wst.contentmodel.CMElementDeclaration;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.w3c.dom.Node;

public class XMLContentModelGenerator extends AbstractContentModelGenerator {

	/**
	 * XMLContentModelGenerator constructor comment.
	 */
	public XMLContentModelGenerator() {
		super();
	}

	public void generateAttribute(CMAttributeDeclaration attrDecl, StringBuffer buffer) {
		if (attrDecl == null || buffer == null)
			return;
		int usage = attrDecl.getUsage();
		if (usage == CMAttributeDeclaration.REQUIRED) {
			buffer.append(" "); //$NON-NLS-1$
			generateRequiredAttribute(null, attrDecl, buffer); //todo pass ownerNode as 1st param
		}
		return;
	}

	protected void generateEndTag(String tagName, Node parentNode, CMElementDeclaration elementDecl, StringBuffer buffer) {
		if (elementDecl == null)
			return;
		if (elementDecl.getContentType() != CMElementDeclaration.EMPTY)
			buffer.append("</" + tagName + ">");//$NON-NLS-2$//$NON-NLS-1$
		return;
	}

	public void generateRequiredAttribute(Node ownerNode, CMAttributeDeclaration attrDecl, StringBuffer buffer) {
		if (attrDecl == null || buffer == null)
			return;

		// attribute name
		String attributeName = getRequiredName(ownerNode, attrDecl);
		CMDataType attrType = attrDecl.getAttrType();
		// = sign
		buffer.append(attributeName + "=\""); //$NON-NLS-1$
		// attribute value
		if (attrType != null) {
			// insert any value that is implied
			if (attrType.getImpliedValueKind() != CMDataType.IMPLIED_VALUE_NONE && attrType.getImpliedValue() != null) {
				buffer.append(attrType.getImpliedValue());
			}
			// otherwise, if an enumerated list of values exists, use the first value
			else if (attrType.getEnumeratedValues() != null && attrType.getEnumeratedValues().length > 0) {
				buffer.append(attrType.getEnumeratedValues()[0]);
			}
		}
		buffer.append("\""); //$NON-NLS-1$
		return;
	}

	protected void generateStartTag(String tagName, Node parentNode, CMElementDeclaration elementDecl, StringBuffer buffer) {
		if (elementDecl == null || buffer == null)
			return;
		buffer.append("<" + tagName);//$NON-NLS-1$
		generateAttributes(elementDecl, buffer);
		buffer.append(getStartTagClose(parentNode, elementDecl));
		return;
	}

	public int getMinimalStartTagLength(Node node, CMElementDeclaration elementDecl) {
		if (elementDecl == null)
			return 0;
		if (requiresAttributes(elementDecl)) {
			return getRequiredName(node, elementDecl).length() + 2; // < + name + space
		}
		else {
			return 1 + getRequiredName(node, elementDecl).length() + getStartTagClose(node, elementDecl).length(); // < + name + appropriate close
		}
	}

	public String getStartTagClose(Node parentNode, CMElementDeclaration elementDecl) {
		String other = getOtherClose(parentNode);
		if (other != null)
			return other;
		if (elementDecl == null)
			return ">";//$NON-NLS-1$
		if (elementDecl.getContentType() == CMElementDeclaration.EMPTY)
			return "/>"; //$NON-NLS-1$
		return ">"; //$NON-NLS-1$
	}

	protected String getOtherClose(Node notATagNode) {
		if (notATagNode instanceof XMLNode) {
			IStructuredDocumentRegion node = ((XMLNode) notATagNode).getStartStructuredDocumentRegion();
			if (node != null && node.getNumberOfRegions() > 1 && node.getRegions().get(0).getType() == XMLJSPRegionContexts.JSP_DIRECTIVE_OPEN) {
				return "%>"; //$NON-NLS-1$
			}
		}
		return null;
	}
}
