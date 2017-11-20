/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.domdocument;

import org.eclipse.wst.html.core.internal.document.DocumentStyleImpl;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMDocumentForJSP extends DocumentStyleImpl {

	/**
	 * 
	 */
	public DOMDocumentForJSP() {
		super();
	}

	/**
	 * @param that
	 */
	protected DOMDocumentForJSP(DocumentImpl that) {
		super(that);
	}
	/**
	 * cloneNode method
	 * @return org.w3c.dom.Node
	 * @param deep boolean
	 */
	public Node cloneNode(boolean deep) {
		DOMDocumentForJSP cloned = new DOMDocumentForJSP(this);
		if (deep)
			cloned.importChildNodes(this, true);
		return cloned;
	}
	/**
	 * createElement method
	 * 
	 * @return org.w3c.dom.Element
	 * @param tagName
	 *            java.lang.String
	 */
	public Element createElement(String tagName) throws DOMException {
		checkTagNameValidity(tagName);

		ElementImplForJSP element = new ElementImplForJSP();
		element.setOwnerDocument(this);
		element.setTagName(tagName);
		return element;
	}
	/**
	 * createComment method
	 * 
	 * @return org.w3c.dom.Comment
	 * @param data
	 *            java.lang.String
	 */
	public Comment createComment(String data) {
		CommentImplForJSP comment = new CommentImplForJSP();
		comment.setOwnerDocument(this);
		if (data != null)
			comment.setData(data);
		return comment;
	}

	/**
	 * createAttribute method
	 * 
	 * @return org.w3c.dom.Attr
	 * @param name
	 *            java.lang.String
	 */
	public Attr createAttribute(String name) throws DOMException {
		AttrImplForJSP attr = new AttrImplForJSP();
		attr.setOwnerDocument(this);
		attr.setName(name);
		return attr;
	}

	/**
	 */
	public Attr createAttributeNS(String uri, String name) throws DOMException {
		AttrImplForJSP attr = new AttrImplForJSP();
		attr.setOwnerDocument(this);
		attr.setName(name);
		attr.setNamespaceURI(uri);
		return attr;
	}
	/**
	 * createTextNode method
	 * 
	 * @return org.w3c.dom.Text
	 * @param data
	 *            java.lang.String
	 */
	public Text createTextNode(String data) {
		TextImplForJSP text = new TextImplForJSP();
		text.setOwnerDocument(this);
		text.setData(data);
		return text;
	}
	protected void setModel(IDOMModel model) {
		super.setModel(model);
	}
}
