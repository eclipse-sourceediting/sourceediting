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
package org.eclipse.wst.html.core.internal.document;



import org.eclipse.wst.html.core.internal.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLPropertyDeclaration;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.document.CMNodeUtil;
import org.eclipse.wst.xml.core.internal.document.ModelParserAdapter;
import org.eclipse.wst.xml.core.internal.document.TagAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * HTMLDocumentImpl class
 */
public class HTMLModelParserAdapter implements ModelParserAdapter {
	/**
	 * note: I made public, temparily, so could be used by JSPLoader
	 */
	protected HTMLModelParserAdapter() {
		super();
	}

	private boolean shouldTerminateAt(CMElementDeclaration parent, CMElementDeclaration child) {
		if (!parent.supports(HTMLCMProperties.TERMINATORS))
			return false;
		java.util.Iterator i = (java.util.Iterator) parent.getProperty(HTMLCMProperties.TERMINATORS);
		if (i == null)
			return false;
		String nextName = child.getElementName();
		while (i.hasNext()) {
			// NOTE: CMElementDeclaration of child is not always HTMLCMElementDeclaration.
			// It might be one of based on DTD (for XHTML element).  So, comparison must
			// be performed ignoring case.
			// -- 3/20/2002
			String terminator = (String) i.next();
			if (terminator == null)
				continue;
			if (nextName.equalsIgnoreCase(terminator))
				return true;
		}
		return false;
	}

	public boolean isEndTagOmissible(Element element) {
		CMElementDeclaration dec = CMNodeUtil.getElementDeclaration(element);
		if (dec == null || !(dec instanceof HTMLPropertyDeclaration))
			return false;
		int type = ((HTMLPropertyDeclaration)dec ).getOmitType();
		return type == HTMLElementDeclaration.OMIT_BOTH || type == HTMLElementDeclaration.OMIT_END || type == HTMLElementDeclaration.OMIT_END_DEFAULT || type == HTMLElementDeclaration.OMIT_END_MUST;
	}

	/**
	 */
	public boolean canContain(Element element, Node child) {
		if (element == null || child == null)
			return false;
		IDOMElement impl = (IDOMElement) element;

		if (child.getNodeType() == Node.ELEMENT_NODE) {
			if (!impl.isGlobalTag())
				return true; // non HTML tag
			IDOMElement childElement = (IDOMElement) child;

			CMElementDeclaration myDec = CMNodeUtil.getElementDeclaration(element);
			if (myDec == null)
				return true;
			//if (!(myDec instanceof HTMLElementDeclaration)) return true;
			if (myDec.getContentType() == CMElementDeclaration.EMPTY)
				return false;

			if (!childElement.isGlobalTag())
				return true; // non HTML tag
			CMElementDeclaration childDec = CMNodeUtil.getElementDeclaration(childElement);
			if (childDec == null)
				return true;
			//if (!(childDec instanceof HTMLElementDeclaration)) return true;

			if (myDec instanceof HTMLElementDeclaration) {
				if (((Boolean) ((HTMLElementDeclaration) myDec).getProperty(HTMLCMProperties.IS_JSP)).booleanValue())
					return true;
			}
			if (shouldTerminateAt(myDec, childDec) && !isValidChild(myDec, childDec)) {
				return false;
			}

			String tagName = impl.getTagName();
			if (tagName == null)
				return true;
			String childName = childElement.getTagName();
			if (childName == null)
				return true;
			if (!impl.hasStartTag() && !impl.hasEndTag()) {
				// implicit element
				if (tagName.equalsIgnoreCase(childElement.getTagName()))
					return false;
				if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.HEAD)) {
					if (!childName.equalsIgnoreCase(HTML40Namespace.ElementName.META) && !childName.equalsIgnoreCase(HTML40Namespace.ElementName.TITLE) && !childName.equalsIgnoreCase(HTML40Namespace.ElementName.LINK) && !childName.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE) && !childName.equalsIgnoreCase(HTML40Namespace.ElementName.BASE) && !childName.equalsIgnoreCase(HTML40Namespace.ElementName.ISINDEX)) {
						return false;
					}
				}

				Node parent = element.getParentNode();
				if (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
					IDOMElement parentElement = (IDOMElement) parent;
					if (!parentElement.hasStartTag() && !parentElement.hasEndTag()) {
						if (!canContain(parentElement, child))
							return false;
					}
				}
				return true;
			}

			// contexual termination for TABLE content tags
			boolean isTableContent = false;
			if (childName.equalsIgnoreCase(HTML40Namespace.ElementName.TBODY) || childName.equalsIgnoreCase(HTML40Namespace.ElementName.THEAD) || childName.equalsIgnoreCase(HTML40Namespace.ElementName.TFOOT)) {
				if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TABLE))
					return true;
				isTableContent = true;
			}
			else if (childName.equalsIgnoreCase(HTML40Namespace.ElementName.TR)) {
				if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TBODY) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.THEAD) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TFOOT) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TABLE))
					return true;
				isTableContent = true;
			}
			else if (childName.equalsIgnoreCase(HTML40Namespace.ElementName.TD) || childName.equalsIgnoreCase(HTML40Namespace.ElementName.TH)) {
				if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TR) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TBODY) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.THEAD) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TFOOT) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.TABLE))
					return true;
				isTableContent = true;
			}
			if (isTableContent) {
				// TABLE content tags should terminate non TABLE content tags,
				// if in TABLE
				for (Node parent = element.getParentNode(); parent != null; parent = parent.getParentNode()) {
					if (parent.getNodeType() != Node.ELEMENT_NODE)
						break;
					IDOMElement parentElement = (IDOMElement) parent;
					String parentName = parentElement.getTagName();
					if (parentName == null)
						continue;
					if (parentName.equalsIgnoreCase(HTML40Namespace.ElementName.TABLE))
						return false;
				}
			}
			if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.EMBED)) {
				if (!childName.equalsIgnoreCase(HTML40Namespace.ElementName.NOEMBED))
					return false;
			}
		}
		else if (child.getNodeType() == Node.TEXT_NODE) {
			String tagName = impl.getTagName();
			if (tagName != null && tagName.equalsIgnoreCase(HTML40Namespace.ElementName.EMBED)) {
				IDOMText text = (IDOMText) child;
				if (!text.isElementContentWhitespace())
					return false;
			}
		}
		else if (child.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
			if (impl.isImplicitTag())
				return false;
		}

		return true;
	}

	/**
	 */
	public boolean canBeImplicitTag(Element element) {
		return false;
	}

	/**
	 */
	public boolean canBeImplicitTag(Element element, Node child) {
		return false;
	}

	/**
	 */
	public Element createCommentElement(Document document, String data, boolean isJSPTag) {
		if (document == null || data == null || data.length() == 0)
			return null;

		return createMetaElement(document, data, isJSPTag);
	}

	/**
	 * This routine create an implicit Element for given parent and child,
	 * such as HTML, BODY, HEAD, and TBODY for HTML document.
	 */
	public Element createImplicitElement(Document document, Node parent, Node child) {
		return null;
	}

	/**
	 */
	private Element createMetaElement(Document document, String data, boolean isJSPTag) {
		if (data == null || data.length() == 0)
			return null;

		TagScanner scanner = new TagScanner(data, 0, true); // one line
		String name = scanner.nextName();
		if (name == null || !name.equalsIgnoreCase(MetaData.METADATA))
			return null;

		String type = null;
		boolean isStartSpan = false;
		boolean isEndSpan = false;
		name = scanner.nextName();
		while (name != null) {
			String value = scanner.nextValue();
			if (name.equalsIgnoreCase(MetaData.TYPE)) {
				if (value == null)
					return null;
				if (value.equalsIgnoreCase(MetaData.DESIGNER_CONTROL)) {
					type = MetaData.DESIGNER_CONTROL;
				}
				else if (value.equalsIgnoreCase(MetaData.DYNAMIC_DATA)) {
					type = MetaData.DYNAMIC_DATA;
				}
				else if (value.equalsIgnoreCase(MetaData.AUTHOR_TIME_VISUAL)) {
					type = MetaData.AUTHOR_TIME_VISUAL;
				}
				else if (value.equalsIgnoreCase(MetaData.ANNOTATION)) {
					type = MetaData.ANNOTATION;
				}
				else {
					return null;
				}
			}
			else if (name.equalsIgnoreCase(MetaData.STARTSPAN)) {
				isStartSpan = true;
			}
			else if (name.equalsIgnoreCase(MetaData.ENDSPAN)) {
				if (!isStartSpan)
					isEndSpan = true;
			}
			name = scanner.nextName();
		}
		if (type == null)
			return null;
		if (!isStartSpan && !isEndSpan)
			return null;
		String metaData = null;
		int offset = scanner.getNextOffset(); // skip new line
		if (offset < data.length())
			metaData = data.substring(offset);
		if (metaData == null)
			metaData = new String();

		IDOMElement element = (IDOMElement) document.createElement(MetaData.PREFIX + type);

		MetaDataAdapter adapter = new MetaDataAdapter(type);
		if (isStartSpan) {
			if (metaData != null)
				adapter.setData(metaData);
		}
		else {
			if (metaData != null)
				adapter.setEndData(metaData);
		}
		element.addAdapter(adapter);
		adapter.setElement(element);
		element.setJSPTag(isJSPTag);

		return element;
	}

	/**
	 */
	public String getFindRootName(String tagName) {
		if (tagName == null)
			return null;
		// tag matching should not beyond TABLE tag except BODY tag
		if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.BODY))
			return null;
		return HTML40Namespace.ElementName.TABLE;
	}

	/**
	 */
	public boolean isAdapterForType(Object type) {
		return (type == ModelParserAdapter.class);
	}

	/**
	 */
	public boolean isEndTag(IDOMElement element) {
		TagAdapter adapter = (TagAdapter) element.getExistingAdapter(TagAdapter.class);
		if (adapter != null)
			return adapter.isEndTag();
		return element.isEndTag();
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// do nothing on notifiy change
		// TODO: this means good candidate for regular platform adapter
	}

	private static boolean isValidChild(CMElementDeclaration parent, CMElementDeclaration child) {
		if (parent == null || child == null)
			return false;
		CMContent content = parent.getContent();
		if (content == null)
			return false;
		return isChild(content, child);
	}

	/**
	 */
	private static boolean isChild(CMContent content, CMElementDeclaration target) {
		switch (content.getNodeType()) {
			case CMNode.ELEMENT_DECLARATION :
				return isSameDeclaration((CMElementDeclaration) content, target);
			case CMNode.GROUP :
				CMNodeList children = ((CMGroup) content).getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					CMNode child = children.item(i);
					switch (child.getNodeType()) {
						case CMNode.ELEMENT_DECLARATION :
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
	private static boolean isSameDeclaration(CMElementDeclaration aDec, CMElementDeclaration otherDec) {
		return aDec.getElementName() == otherDec.getElementName();
	}

}
