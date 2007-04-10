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
package org.eclipse.wst.html.core.internal.commentelement.handlers;



import org.eclipse.wst.xml.core.internal.commentelement.CommentElementHandler;
import org.eclipse.wst.xml.core.internal.commentelement.util.CommentElementFactory;
import org.eclipse.wst.xml.core.internal.commentelement.util.TagScanner;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class CommentElementHandlerForSSI implements CommentElementHandler {

	public Element createElement(Document document, String data, boolean isJSPTag) {
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(document);
		if (modelQuery == null) {
			return null;
		}
		CMDocument cm = modelQuery.getCorrespondingCMDocument(document);
		if (cm == null) {
			return null;
		}
		CMNamedNodeMap map = cm.getElements();
		if (map == null) {
			return null;
		}

		TagScanner scanner = new TagScanner(data, 1);
		String name = scanner.nextName();
		if (name == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer(name.length() + 4);
		buffer.append(SSI_PREFIX);
		buffer.append(':');
		buffer.append(name);
		String tagName = buffer.toString();
		// check if valid (defined) SSI tag or not
		if (map.getNamedItem(tagName) == null) {
			return null;
		}

		CommentElementFactory factory = new CommentElementFactory(document, isJSPTag, this);
		Element element = factory.create(tagName, CommentElementFactory.IS_START);

		// set attributes
		String attrName = scanner.nextName();
		while (attrName != null) {
			String attrValue = scanner.nextValue();
			Attr attr = document.createAttribute(attrName);
			if (attr != null) {
				if (attrValue != null)
					attr.setValue(attrValue);
				element.setAttributeNode(attr);
			}
			attrName = scanner.nextName();
		}
		return element;
	}

	public String generateStartTagContent(IDOMElement element) {
		ISourceGenerator generator = element.getModel().getGenerator();
		StringBuffer buffer = new StringBuffer();

		buffer.append('#');
		buffer.append(element.getLocalName());

		NamedNodeMap attributes = element.getAttributes();
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			Attr attr = (Attr) attributes.item(i);
			if (attr == null) {
				continue;
			}
			buffer.append(' ');
			String attrName = generator.generateAttrName(attr);
			if (attrName != null) {
				buffer.append(attrName);
			}
			String attrValue = generator.generateAttrValue(attr);
			if (attrValue != null) {
				// attr name only for HTML boolean and JSP
				buffer.append('=');
				buffer.append(attrValue);
			}
		}

		return buffer.toString();
	}

	public String generateEndTagContent(IDOMElement element) {
		return null; // always empty
	}

	public boolean isEmpty() {
		return true;
	}

	public boolean isCommentElement(IDOMElement element) {
		if (element == null) {
			return false;
		}
		Document document = element.getOwnerDocument();
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(document);
		if (modelQuery == null) {
			return false;
		}
		CMDocument cm = modelQuery.getCorrespondingCMDocument(document);
		if (cm == null) {
			return false;
		}
		CMNamedNodeMap map = cm.getElements();
		if (map == null) {
			return false;
		}
		String prefix = element.getPrefix();
		if (prefix == null || !prefix.equals(SSI_PREFIX)) {
			return false;
		}
		String tagName = element.getTagName();
		if (tagName.length() <= 4) {
			return false;
		}
		if (map.getNamedItem(tagName) == null) {
			return false;
		}
		else {
			return true;
		}
	}

	private static final String SSI_PREFIX = "ssi";//$NON-NLS-1$
}
