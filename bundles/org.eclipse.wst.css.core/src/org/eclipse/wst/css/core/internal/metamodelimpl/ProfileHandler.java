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
package org.eclipse.wst.css.core.internal.metamodelimpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMSelector;
import org.eclipse.wst.css.core.internal.metamodel.CSSProfile;
import org.eclipse.wst.css.core.internal.metamodel.CSSProfileRegistry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


class ProfileHandler extends DefaultHandler {

	private StringBuffer fCharacters = null;

	public ProfileHandler(CSSMetaModelImpl metamodel, ResourceBundle resourceBundle, boolean logging) {
		super();
		fMetaModel = metamodel;
		fNodePool = metamodel.getNodePool();
		fResourceBundle = resourceBundle;
		fLogging = logging;
	}

	public ProfileHandler(CSSMetaModelImpl metamodel, ResourceBundle resourceBundle) {
		super();
		fMetaModel = metamodel;
		fNodePool = metamodel.getNodePool();
		fResourceBundle = resourceBundle;
		fLogging = false;
	}

	private String getResourceString(String key) {
		if (key.equals("%")) { //$NON-NLS-1$
			return key;
		}
		if (!key.startsWith("%")) { //$NON-NLS-1$
			return key;
		}
		if (key.startsWith("%%")) { //$NON-NLS-1$
			return key.substring(1);
		}

		if (fResourceBundle != null) {
			return fResourceBundle.getString(key.substring(1));
		}
		else {
			return key;
		}
	}

	public void startDocument() throws SAXException {
		// System.out.println("startDocument");
		// fNodeStack.push(metamodel);
		// fCurrentNode = null;
	}

	public void endDocument() throws SAXException {
		new ErrorCorrector().doCorrect(fMetaModel);
		if (fLogging) {
			Iterator i = fNodePool.getStrayNodes();
			while (i.hasNext()) {
				CSSMMNode node = (CSSMMNode) i.next();
				String str = "[CSSProfile Warning] " + node.getName(); //$NON-NLS-1$
				str += "(" + node.getType() + ") is not referred by any node."; //$NON-NLS-1$ //$NON-NLS-2$
				Logger.log(Logger.WARNING, str);
				// System.out.println(str);
			}
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		TagNode tagNode = null;
		CSSMMNodeImpl parentNode = null;
		if (0 < fNodeStack.size()) {
			tagNode = (TagNode) fNodeStack.peek();
			parentNode = tagNode.node;
		}

		CSSMMNodeImpl node = null;
		if (qName.equals(ProfileKeywords.PROFILE_IMPORT)) { // import
			String profileName = attributes.getValue(ATTR_NAME_REFERENCE);
			importProfile(profileName);
		}
		else if (isDefinition(qName)) { // node creation
			String nodeName = attributes.getValue(ATTR_NAME_DEFINITION);
			node = fNodePool.getNode(qName, nodeName);
			if (node != null) {
				String overwrite = attributes.getValue(ATTR_OVERWRITE);
				if (overwrite == null || overwrite.equals(ATTR_VALUE_OVERWRITE_FALSE)) {
					node.removeAllChildNodes();
				}
				Map attrMap = new HashMap();
				for (int i = 0; i < attributes.getLength(); i++) {
					attrMap.put(attributes.getQName(i), attributes.getValue(i));
				}
				try {
					node.initializeAttribute(attrMap);
				}
				catch (IllegalArgumentException e) {
					Logger.logException(e);
				}
			}
		}
		else if (node == null) { // node reference
			String nodeName = attributes.getValue(ATTR_NAME_REFERENCE);
			node = fNodePool.getNode(qName, nodeName);
		}

		if (node != null) {
			if (parentNode != null && parentNode.canContain(node)) {
				String enabled = attributes.getValue(ATTR_ENABLED);
				if (enabled != null && enabled.equals(ATTR_VALUE_ENABLED_FALSE)) {
					parentNode.removeChild(node);
				}
				else {
					parentNode.appendChild(node);
				}
			}
			else if (node.getType() == CSSMMNode.TYPE_STYLE_SHEET || node.getType() == CSSMMNode.TYPE_CATEGORY) {
				fMetaModel.appendChild(node);
			}
			else {
				if (fLogging && parentNode != null) {
					Logger.log(Logger.ERROR, parentNode.getType() + " cannot contain " + //$NON-NLS-1$
								node.getType() + " (" + qName + ")"); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}

		fNodeStack.push(new TagNode(qName, (node != null) ? node : parentNode));
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		TagNode tagNode = (TagNode) fNodeStack.peek();
		CSSMMNodeImpl node = tagNode.node;
		String tagName = tagNode.tag;
		if (fCharacters != null && (tagName.equals(ProfileKeywords.KEYWORD_VALUE) || tagName.equals(ProfileKeywords.UNIT_VALUE) || tagName.equals(ProfileKeywords.FUNCTION_VALUE) || tagName.equals(ProfileKeywords.SELECTOR_VALUE) || tagName.equals(ProfileKeywords.DESCRIPTION) || tagName.equals(ProfileKeywords.CAPTION))) {
			String value = getResourceString(fCharacters.toString().trim());
			if (node != null) {
				if (node.getType() == CSSMMNode.TYPE_KEYWORD && tagName.equals(ProfileKeywords.KEYWORD_VALUE)) {
					((CSSMMKeywordImpl) node).setKeywordString(value);
				}
				else if (node.getType() == CSSMMNode.TYPE_UNIT && tagName.equals(ProfileKeywords.UNIT_VALUE)) {
					((CSSMMUnitImpl) node).setUnitString(value);
				}
				else if (node.getType() == CSSMMNode.TYPE_FUNCTION && tagName.equals(ProfileKeywords.FUNCTION_VALUE)) {
					((CSSMMFunctionImpl) node).setFunctionString(value);
				}
				else if (node.getType() == CSSMMNode.TYPE_SELECTOR && ((CSSMMSelector) node).getSelectorType() == CSSMMSelector.TYPE_PSEUDO_ELEMENT) {
					((CSSMMPseudoElementImpl) node).setSelectorString(value);
				}
				else if (node.getType() == CSSMMNode.TYPE_SELECTOR && ((CSSMMSelector) node).getSelectorType() == CSSMMSelector.TYPE_PSEUDO_CLASS) {
					((CSSMMPseudoClassImpl) node).setSelectorString(value);
				}
				else if (node.getType() == CSSMMNode.TYPE_CATEGORY && tagName.equals(ProfileKeywords.CAPTION)) {
					((CSSMMCategoryImpl) node).setCaption(value);
				}
				else if (tagName.equals(ProfileKeywords.DESCRIPTION)) {
					node.setDescription(value);
				}
			}
		}
		fNodeStack.pop();
		fCharacters = null;
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		
		TagNode tagNode = (TagNode) fNodeStack.peek();
		String tagName = tagNode.tag;
		if (tagName.equals(ProfileKeywords.KEYWORD_VALUE) || tagName.equals(ProfileKeywords.UNIT_VALUE) || tagName.equals(ProfileKeywords.FUNCTION_VALUE) || tagName.equals(ProfileKeywords.SELECTOR_VALUE) || tagName.equals(ProfileKeywords.DESCRIPTION) || tagName.equals(ProfileKeywords.CAPTION)) {
			if (fCharacters == null) {
				fCharacters = new StringBuffer(length);
			}
			fCharacters.append(ch, start, length);
		}
	}

	private boolean isDefinition(String tagName) {
		return (tagName.equals(ProfileKeywords.STYLESHEET_DEF) || tagName.equals(ProfileKeywords.CHARSET_RULE_DEF) || tagName.equals(ProfileKeywords.IMPORT_RULE_DEF) || tagName.equals(ProfileKeywords.PAGE_RULE_DEF) || tagName.equals(ProfileKeywords.MEDIA_RULE_DEF) || tagName.equals(ProfileKeywords.FONTFACE_RULE_DEF) || tagName.equals(ProfileKeywords.STYLE_RULE_DEF) || tagName.equals(ProfileKeywords.KEYWORD_DEF) || tagName.equals(ProfileKeywords.NUMBER_DEF) || tagName.equals(ProfileKeywords.PROPERTY_DEF) || tagName.equals(ProfileKeywords.DESCRIPTOR_DEF) || tagName.equals(ProfileKeywords.CONTAINER_DEF) || tagName.equals(ProfileKeywords.UNIT_DEF) || tagName.equals(ProfileKeywords.FUNCTION_DEF) || tagName.equals(ProfileKeywords.STRING) || tagName.equals(ProfileKeywords.CATEGORY_DEF) || tagName.equals(ProfileKeywords.PSEUDO_CLASS_DEF) || tagName.equals(ProfileKeywords.PSEUDO_ELEMENT_DEF) || tagName.equals(ProfileKeywords.SELECTOR_EXPRESSION) || tagName.equals(ProfileKeywords.SEPARATOR));
	}

	private void importProfile(String profileName) {
		URL profileURL = null;
		CSSProfileRegistry reg = CSSProfileRegistry.getInstance();
		CSSProfile profile = reg.getProfile(profileName);
		if (profile != null) {
			// first: find URL by ID
			profileURL = profile.getProfileURL();
		}
		else {
			// second: find URL by filename of profile URL
			Iterator i = reg.getProfiles();
			while (i.hasNext()) {
				profile = (CSSProfile) i.next();
				URL url = profile.getProfileURL();
				if (url.getFile().endsWith(profileName)) {
					profileURL = url;
					break;
				}
			}
		}
		if (profileURL == null) {
			// final: it may be url itself
			try {
				profileURL = new URL(profileName);
			}
			catch (MalformedURLException e) {
				Logger.logException(e);
			}
		}
		if (profileURL != null) {
			try {
				ProfileLoader.loadProfile(fMetaModel, profileURL.openStream(), fResourceBundle, fLogging);
			}
			catch (IOException e) {
				Logger.logException("Cannot open stream for profile", //$NON-NLS-1$
							e);
			}
		}
	}

	class TagNode {

		String tag = null;
		CSSMMNodeImpl node = null;

		TagNode(String tag, CSSMMNodeImpl node) {
			this.tag = tag;
			this.node = node;
		}
	}

	class ErrorCorrector {

		void doCorrect(CSSMMNodeImpl node) {
			Iterator i = node.getChildNodes();
			ArrayList errorNodes = new ArrayList();
			while (i.hasNext()) {
				CSSMMNodeImpl child = (CSSMMNodeImpl) i.next();
				doCorrect(child);
				short error = child.getError();
				if (error != MetaModelErrors.NO_ERROR) {
					// node.removeChild(child);
					errorNodes.add(child);

					String str = "[CSSProfile Error] " + node.getName(); //$NON-NLS-1$
					str += "(" + node.getType() + ") contains error node: "; //$NON-NLS-1$ //$NON-NLS-2$
					str += child.getName() + "(" + child.getType() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					str += " - error code = " + error; //$NON-NLS-1$
					if (fLogging) {
						Logger.log(Logger.ERROR, str);
						// System.out.println(str);
					}
				}
			}
			int errorSize = errorNodes.size();
			if (errorSize > 0) {
				for (int j = 0; j < errorSize; j++) {
					CSSMMNodeImpl errorNode = (CSSMMNodeImpl) errorNodes.get(j);
					node.removeChild(errorNode);
				}
			}
		}
	}


	private CSSMetaModelImpl fMetaModel = null;
	private NodePool fNodePool = null;
	private Stack fNodeStack = new Stack();
	boolean fLogging = false;
	private ResourceBundle fResourceBundle = null;

	private final static String ATTR_NAME_DEFINITION = "name"; //$NON-NLS-1$
	private final static String ATTR_NAME_REFERENCE = "name"; //$NON-NLS-1$
	private final static String ATTR_OVERWRITE = "overwrite"; //$NON-NLS-1$
	private final static String ATTR_ENABLED = "enabled"; //$NON-NLS-1$
	private final static String ATTR_VALUE_OVERWRITE_FALSE = "false"; //$NON-NLS-1$
	private final static String ATTR_VALUE_ENABLED_FALSE = "false"; //$NON-NLS-1$
}
