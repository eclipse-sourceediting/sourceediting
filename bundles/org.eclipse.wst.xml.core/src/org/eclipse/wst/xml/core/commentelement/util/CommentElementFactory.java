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
package org.eclipse.wst.xml.core.commentelement.util;



import org.eclipse.wst.xml.core.commentelement.CommentElementAdapter;
import org.eclipse.wst.xml.core.commentelement.CommentElementHandler;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 */
public class CommentElementFactory {

	/**
	 * Constructor for CommentElementFactory.
	 */
	private CommentElementFactory() {
		super();
	}

	public CommentElementFactory(Document document, boolean isJSPTag, CommentElementHandler handler) {
		super();
		fDocument = document;
		fJSPTag = isJSPTag;
		fHandler = handler;
	}

	public Element create(String name, int nodeType) {
		XMLElement element = (XMLElement) fDocument.createElement(name);
		if (element == null)
			return null;
		element.setCommentTag(true);
		if (nodeType == IS_EMPTY) {
			element.setEmptyTag(true);
		}
		element.setJSPTag(fJSPTag);

		CommentElementAdapter adapter = new CommentElementAdapter((nodeType == IS_END), fHandler);
		element.addAdapter(adapter);

		return element;
	}

	public static final int IS_START = 28011;
	public static final int IS_END = 1808;
	public static final int IS_EMPTY = 4866;

	private Document fDocument;
	private CommentElementHandler fHandler;
	private boolean fJSPTag;
}
