/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.provisional.document;



import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This interface enables creation of DOCTYPE declaration and some DOM Level 2
 * interfaces. May be referenced but not implemented by clients.
 * 
 * @plannedfor 1.0
 */
public interface IDOMDocument extends Node, Document, IDOMNode {

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
	 *             thrown if the element name is registered as comment element
	 */
	Element createCommentElement(String tagName, boolean isJSPTag) throws DOMException;

	/**
	 * Creates a DocumentType node
	 * 
	 * ISSUE: I believe this 'name' is the one specific in doctype extension,
	 * need to verify.
	 * 
	 * @param name -
	 *            name of the doctype
	 * @return DocumentType - returns a document type node.
	 */
	DocumentType createDoctype(String name);

	/**
	 * Returns the DocumentType ID. Unlike the standard DOM approach of
	 * "getDocumentType().getPublicId()", this method returns the id even if
	 * implicit.
	 * 
	 * @return the DocumentType ID
	 */
	String getDocumentTypeId();

	/**
	 * NOT API ... needs to be removed/changed
	 * 
	 * ISSUE: need to specify
	 * 
	 * @deprecated
	 * 
	 * @return true if is JSPDocument
	 * 
	 */
	boolean isJSPDocument();

	/**
	 * NOT API ... needs to be removed/changed
	 * 
	 * ISSUE: need to specify
	 * 
	 * @deprecated
	 * 
	 */
	boolean isJSPType();

	/**
	 * NOT API ... may removed/changed
	 * 
	 * ISSUE: need to specify -- currently used to denote an HTML DOM document
	 * is XHTML
	 * 
	 * @deprecated
	 * 
	 */
	boolean isXMLType();

}
