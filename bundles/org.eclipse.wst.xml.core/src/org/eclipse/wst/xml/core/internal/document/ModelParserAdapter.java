/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 */
public interface ModelParserAdapter extends INodeAdapter {

	/**
	 */
	public boolean canBeImplicitTag(Element element);

	/**
	 */
	public boolean canBeImplicitTag(Element element, Node child);

	/**
	 */
	public boolean canContain(Element element, Node child);

	/**
	 */
	public Element createCommentElement(Document document, String data, boolean isJSPTag);

	/**
	 */
	public Element createImplicitElement(Document document, Node parent, Node child);

	/**
	 */
	public String getFindRootName(String tagName);

	/**
	 */
	public boolean isEndTag(IDOMElement element);

	boolean isEndTagOmissible(Element element);
}
