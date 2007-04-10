/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.commentelement.impl;



import org.eclipse.wst.xml.core.internal.commentelement.CommentElementHandler;
import org.eclipse.wst.xml.core.internal.commentelement.util.CommentElementFactory;
import org.eclipse.wst.xml.core.internal.commentelement.util.TagScanner;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 */
class BasicCommentElementHandler implements CommentElementHandler {

	private String elementName;
	private boolean isEmpty;

	public BasicCommentElementHandler(String elementName, boolean isEmpty) {
		super();
		this.elementName = elementName;
		this.isEmpty = isEmpty;
	}


	public Element createElement(Document document, String data, boolean isJSPTag) {
		Element element = null;
		String str = data.trim();
		CommentElementFactory factory = new CommentElementFactory(document, isJSPTag, this);
		if (str.charAt(0) == '/') { // end tag
			TagScanner scanner = new TagScanner(str, 1); // skip '/'
			String name = scanner.nextName();
			if (name.equals(elementName)) {
				element = factory.create(name, CommentElementFactory.IS_END);
			}
		} else { // start tag
			TagScanner scanner = new TagScanner(str, 0);
			String name = scanner.nextName();
			if (name.equals(elementName)) {
				element = factory.create(name, (isEmpty) ? CommentElementFactory.IS_EMPTY : CommentElementFactory.IS_START);
				// set attributes
				String attrName = scanner.nextName();
				while (attrName != null) {
					String attrValue = scanner.nextValue();
					Attr attr = document.createAttribute(attrName);
					if (attr != null) {
						if (attrValue != null)
							((IDOMAttr) attr).setValueSource(attrValue);
						element.setAttributeNode(attr);
					}
					attrName = scanner.nextName();
				}
			}
		}
		return element;
	}


	public String generateEndTagContent(IDOMElement element) {
		if (isEmpty) {
			return null;
		}
		ISourceGenerator generator = element.getModel().getGenerator();
		StringBuffer buffer = new StringBuffer();

		buffer.append(" /"); //$NON-NLS-1$
		String tagName = generator.generateTagName(element);
		if (tagName != null) {
			buffer.append(tagName);
		}
		buffer.append(' ');

		return buffer.toString();
	}

	public String generateStartTagContent(IDOMElement element) {
		ISourceGenerator generator = element.getModel().getGenerator();
		StringBuffer buffer = new StringBuffer();

		buffer.append(' ');
		String tagName = generator.generateTagName(element);
		if (tagName != null) {
			buffer.append(tagName);
		}

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

		buffer.append(' ');

		return buffer.toString();
	}

	public boolean isCommentElement(IDOMElement element) {
		return (element != null && element.getTagName().equals(elementName)) ? true : false;
	}

	public boolean isEmpty() {
		return isEmpty;
	}
}
