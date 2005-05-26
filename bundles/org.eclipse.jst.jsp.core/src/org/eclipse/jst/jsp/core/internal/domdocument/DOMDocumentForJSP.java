/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.ui.internal.IReleasable;
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
	 * Default behavior for getting an adapter.
	 * Overridden in case embedded type provides a different adapter
	 * we don't want to use a cached one.
	 */
	public INodeAdapter getAdapterFor(Object type) {

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=85484

		// just always create a new one for JSP
		// need to investigate if this is a performance hit...
		// seems to make HTML Validator (even) slower
		
		INodeAdapter oldAdapter = getExistingAdapter(type);
		if(oldAdapter != null) {
			if(oldAdapter instanceof IReleasable) {
				((IReleasable)oldAdapter).release();
				removeAdapter(oldAdapter);
			}
		}
		
		INodeAdapter result = null;
		
		// if we didn't find one in our list already,
		// let's create it
		FactoryRegistry reg = getFactoryRegistry();
		if (reg != null) {
			INodeAdapterFactory factory = reg.getFactoryFor(type);
			if (factory != null) {
				INodeAdapter newAdapter = factory.adapt(this);
				result = newAdapter;
			}
		}
		// We won't prevent null from being returned, but it would be
		// unusual.
		// It might be because Factory is not working correctly, or
		// not installed, so we'll allow warning message.
		if ((result == null) && (org.eclipse.wst.sse.core.internal.util.Debug.displayWarnings)) {
			System.out.println("Warning: no adapter was found or created for " + type); //$NON-NLS-1$
		}
		return result;
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
