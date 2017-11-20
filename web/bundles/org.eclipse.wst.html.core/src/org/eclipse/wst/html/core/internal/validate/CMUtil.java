/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;


import java.util.Iterator;

import org.eclipse.wst.html.core.internal.modelquery.HMQUtil;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

final class CMUtil {

	/**
	 * Never instantiate!
	 */
	private CMUtil() {
		super();
	}

	/**
	 * You cannot always retrieve HTMLElementDeclaration via an Element instance.
	 * Because, it occasionally a JSP custom tag. -- 9/7/2001
	 */
	public static CMElementDeclaration getDeclaration(Element target) {
		Document doc = target.getOwnerDocument();
		ModelQuery query = ModelQueryUtil.getModelQuery(doc);
		return query.getCMElementDeclaration(target);
	}

	/**
	 */
	public static boolean isCaseSensitive(CMElementDeclaration decl) {
		if (decl == null || (!decl.supports(HTMLCMProperties.SHOULD_IGNORE_CASE)))
			return false;
		return !((Boolean) decl.getProperty(HTMLCMProperties.SHOULD_IGNORE_CASE)).booleanValue();
	}

	/**
	 */
	private static boolean isChild(CMContent content, CMElementDeclaration target) {
		switch (content.getNodeType()) {
			case CMNode.ELEMENT_DECLARATION :
				if (isWholeTagOmissible((CMElementDeclaration) content))
					if (isChild(((CMElementDeclaration) content).getContent(), target))
						return true;
				return isSameDeclaration((CMElementDeclaration) content, target);
			case CMNode.GROUP :
				CMNodeList children = ((CMGroup) content).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					CMNode child = children.item(i);
					switch (child.getNodeType()) {
						case CMNode.ELEMENT_DECLARATION :
							if (isWholeTagOmissible((CMElementDeclaration) child))
								if (isChild(((CMElementDeclaration) child).getContent(), target))
									return true;
							if (isSameDeclaration((CMElementDeclaration) child, target))
								return true;
							continue; // Go next child.
						case CMNode.GROUP :
							if (isChild((CMContent) child, target))
								return true;
							continue; // Go next child.
						default :
							continue; // Go next child.
					}
				}
		}
		return false;
	}

	/**
	 */
	public static boolean isEndTagOmissible(CMElementDeclaration decl) {
		if (!(decl.supports(HTMLCMProperties.OMIT_TYPE)))
			return false;
		String omitType = (String) decl.getProperty(HTMLCMProperties.OMIT_TYPE);
		return !omitType.equals(HTMLCMProperties.Values.OMIT_NONE);
	}

	/**
	 */
	public static boolean isWholeTagOmissible(CMElementDeclaration decl) {
		if (!(decl.supports(HTMLCMProperties.OMIT_TYPE)))
			return false;
		String omitType = (String) decl.getProperty(HTMLCMProperties.OMIT_TYPE);
		return omitType.equals(HTMLCMProperties.Values.OMIT_BOTH);
	}
	
	/**
	 */
	public static boolean isJSP(CMElementDeclaration decl) {
		if (!decl.supports(HTMLCMProperties.IS_JSP))
			return false;
		return ((Boolean) decl.getProperty(HTMLCMProperties.IS_JSP)).booleanValue();
	}

	/**
	 */
	public static boolean isXHTML(CMElementDeclaration decl) {
		if (!decl.supports(HTMLCMProperties.IS_XHTML))
			return false;
		return ((Boolean) decl.getProperty(HTMLCMProperties.IS_XHTML)).booleanValue();
	}

	/**
	 * The method to distinguish HTML and XHTML from other mark up.
	 * This method returns true if the target is,
	 * (1) not JSP,
	 * (2) not SSI.
	 */
	public static boolean isHTML(CMElementDeclaration decl) {
		return (!isJSP(decl)) && (!isSSI(decl));
	}

	/**
	 */
	private static boolean isSameDeclaration(CMElementDeclaration aDec, CMElementDeclaration otherDec) {
		return aDec.getElementName() == otherDec.getElementName();
	}

	/**
	 */
	public static boolean isSSI(CMElementDeclaration edec) {
		if (edec == null)
			return false;
		if (!edec.supports(HTMLCMProperties.IS_SSI))
			return false;
		return ((Boolean) edec.getProperty(HTMLCMProperties.IS_SSI)).booleanValue();
	}

	/**
	 * Call this method only when the parent content type is one of
	 * the following: ANY, ELEMENT, or MIXED.
	 */
	public static boolean isValidChild(CMElementDeclaration parent, CMElementDeclaration child) {
		if (parent == null || child == null)
			return false;
		if (isHTML(parent) && (!isHTML(child)))
			return true;
		CMContent content = parent.getContent();
		if (content == null)
			return false;
		return isChild(content, child);
	}

	public static boolean isForeign(Element target) {
		if (!(target instanceof IDOMElement))
			return true;
		IDOMElement element = (IDOMElement) target;
		return !element.isGlobalTag();
	}

	/**
	 * This method returns true if all of the following conditions are met:
	 * (1) value type is ENUM,
	 * (2) only one value is defined in the enumeration,
	 * (3) the value has same name to the attribute name.
	 */
	public static boolean isBooleanAttr(CMAttributeDeclaration adec) {
		CMDataType attrtype = adec.getAttrType();
		if (attrtype == null)
			return false;
		if (attrtype.getDataTypeName() != CMDataType.ENUM)
			return false;
		String[] values = attrtype.getEnumeratedValues();
		if (values.length != 1)
			return false;
		return values[0].equals(adec.getAttrName());
	}

	public static boolean isValidInclusion(CMElementDeclaration decl, Element parent) {
		Iterator iter = HMQUtil.getInclusions(parent).iterator();
		while (iter.hasNext()) {
			CMElementDeclaration inclusion = (CMElementDeclaration) iter.next();
			if (isSameDeclaration(decl, inclusion))
				return true;
		}
		return false;
	}
	
	/**
	 * The method to distinguish HTML and XHTML from other mark up.
	 * This method returns true if the target is,
	 * (1) not JSP,
	 * (2) not SSI.
	 */
	public static boolean isObsolete(CMNode decl) {
		return decl.supports(HTMLCMProperties.IS_OBSOLETE) && ((Boolean)(decl.getProperty(HTMLCMProperties.IS_OBSOLETE))).booleanValue();
	}
	
	
}
