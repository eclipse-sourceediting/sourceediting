/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.document;



import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.traversal.DocumentTraversal;

/**
 * This interface enables creation of DOCTYPE declaration and some DOM Level 2
 * interfaces.
 */
public interface XMLDocument extends XMLNode, Document, DocumentRange, DocumentTraversal {

	/**
	 * create comment element. tagName must be registered as comment element
	 * name in plugin.xml
	 * 
	 * @param tagName
	 *            the element name
	 * @param isJSPTag
	 *            true if the element is JSP style comment (&lt;%-- ...
	 *            --%&gt;)
	 * @return Element element instance
	 * @throws DOMException
	 *             throwed if the element name is registered as comment
	 *             element
	 */
	Element createCommentElement(String tagName, boolean isJSPTag) throws DOMException;

	/**
	 */
	DocumentType createDoctype(String name);

	/**
	 */
	String getDocumentTypeId();

	/**
	 */
	boolean isJSPDocument();

	/**
	 */
	boolean isJSPType();

	/**
	 */
	boolean isXMLType();

}
