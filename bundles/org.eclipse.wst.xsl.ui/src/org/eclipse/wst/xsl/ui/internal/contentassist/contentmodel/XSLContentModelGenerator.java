/*******************************************************************************
 * Copyright (c) 2001, 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - bug 244978 - initial API and implementation based on
 *           org.eclipse.wst.xml.ui.internal.contentassit.XMLContentModelGenerator
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentModelGenerator;
import org.w3c.dom.Node;


/**
 * This provides the ContentModel based off the XSL xml schema grammars and other
 * grammars provided by the xml editor.
 * 
 * @since 1.1
 *
 */
public class XSLContentModelGenerator extends AbstractContentModelGenerator {


	/**
	 * XSLContentModelGenerator constructor comment.
	 */
	public XSLContentModelGenerator() {
		super();
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentModelGenerator#generateAttribute(org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration, java.lang.StringBuffer)
	 */
	@Override
	public void generateAttribute(CMAttributeDeclaration attrDecl, StringBuffer buffer) {
		if ((attrDecl == null) || (buffer == null)) {
			return;
		}
		int usage = attrDecl.getUsage();
		if (usage == CMAttributeDeclaration.REQUIRED) {
			buffer.append(" "); //$NON-NLS-1$
			generateRequiredAttribute(null, attrDecl, buffer); // todo pass
			// ownerNode as
			// 1st param
		}
		return;
	}

	@Override
	protected void generateEndTag(String tagName, Node parentNode, CMElementDeclaration elementDecl, StringBuffer buffer) {
		if (elementDecl == null) {
			return;
		}
		if (elementDecl.getContentType() != CMElementDeclaration.EMPTY) {
			buffer.append("</" + tagName + ">");//$NON-NLS-2$//$NON-NLS-1$
		}
		return;
	}

	/**
	 * @param ownerNode
	 * @param attrDecl
	 * @param buffer
	 */
	public void generateRequiredAttribute(Node ownerNode, CMAttributeDeclaration attrDecl, StringBuffer buffer) {
		if ((attrDecl == null) || (buffer == null)) {
			return;
		}

		// attribute name
		String attributeName = getRequiredName(ownerNode, attrDecl);
		CMDataType attrType = attrDecl.getAttrType();
		String defaultValue = null;
		// = sign
		buffer.append(attributeName + "="); //$NON-NLS-1$
		// attribute value
		if (attrType != null) {
			// insert any value that is implied
			if ((attrType.getImpliedValueKind() != CMDataType.IMPLIED_VALUE_NONE) && (attrType.getImpliedValue() != null)) {
				defaultValue = attrType.getImpliedValue();
			}
			// otherwise, if an enumerated list of values exists, use the
			// first value
			else if ((attrType.getEnumeratedValues() != null) && (attrType.getEnumeratedValues().length > 0)) {
				defaultValue = attrType.getEnumeratedValues()[0];
			}
		}
		
		char attrQuote = '\"';
		// Found a double quote, wrap the attribute in single quotes
		if(defaultValue != null && defaultValue.indexOf(attrQuote) >= 0) {
			attrQuote = '\'';
		}
		
		buffer.append(attrQuote);
		buffer.append(((defaultValue != null) ? defaultValue : "")); //$NON-NLS-1$
		buffer.append(attrQuote);
		return;
	}

	@Override
	protected void generateStartTag(String tagName, Node parentNode, CMElementDeclaration elementDecl, StringBuffer buffer) {
		if ((elementDecl == null) || (buffer == null)) {
			return;
		}
		buffer.append("<" + tagName);//$NON-NLS-1$
		generateAttributes(elementDecl, buffer);
		buffer.append(getStartTagClose(parentNode, elementDecl));
		return;
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentModelGenerator#getMinimalStartTagLength(org.w3c.dom.Node, org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration)
	 */
	@Override
	public int getMinimalStartTagLength(Node node, CMElementDeclaration elementDecl) {
		if (elementDecl == null) {
			return 0;
		}
		if (requiresAttributes(elementDecl)) {
			return getRequiredName(node, elementDecl).length() + 2; // < +
			// name +
			// space
		}
		else {
			return 1 + getRequiredName(node, elementDecl).length() + getStartTagClose(node, elementDecl).length(); // < +
			// name
			// +
			// appropriate
			// close
		}
	}

	protected String getOtherClose(Node notATagNode) {
		return null;
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentModelGenerator#getStartTagClose(org.w3c.dom.Node, org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration)
	 */
	@Override
	public String getStartTagClose(Node parentNode, CMElementDeclaration elementDecl) {
		String other = getOtherClose(parentNode);
		if (other != null) {
			return other;
		}
		if (elementDecl == null) {
			return ">";//$NON-NLS-1$
		}
		if (elementDecl.getContentType() == CMElementDeclaration.EMPTY) {
			return "/>"; //$NON-NLS-1$
		}
		return ">"; //$NON-NLS-1$
	}
}
