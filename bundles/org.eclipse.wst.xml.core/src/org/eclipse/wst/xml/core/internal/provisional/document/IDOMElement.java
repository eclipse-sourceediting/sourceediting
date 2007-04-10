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
package org.eclipse.wst.xml.core.internal.provisional.document;



import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

/**
 * This interface provides extensions to corresponding DOM interface to enable
 * functions for source editing and incremental parsing.
 * 
 * @plannedfor 1.0
 * 
 */
public interface IDOMElement extends IDOMNode, Element {

	/**
	 * Retuns the start offset of the end tag.
	 * 
	 * ISSUE: need to sort out need for this
	 * 
	 * @return int - the start offset of the end tag.
	 */
	int getEndStartOffset();

	/**
	 * Returns the end offset of the
	 * 
	 * ISSUE: need to sort out need for this
	 * 
	 * @return int - the end offset of the start tag.
	 */
	int getStartEndOffset();

	/**
	 * Returns true if has an end tag.
	 * 
	 * In our source-oriented DOM, sometimes Elements are "ended", even
	 * without an explicit end tag in the source.
	 * 
	 * @return true if has an end tag.
	 */
	boolean hasEndTag();

	/**
	 * returns true if has a start tag.
	 * 
	 * In our source-oriented DOM, a lone end tag will cause a node to be
	 * created in the tree, unlike well-formed-only DOMs.
	 * 
	 * @return true if has a start tag.
	 */
	boolean hasStartTag();

	/**
	 * returns true if this element is a comment element
	 * 
	 * @return true if this element is a comment element
	 */
	boolean isCommentTag();

	/**
	 * isEmptyTag method
	 * 
	 * @return boolean - true if is empty tag, false otherwise
	 */
	boolean isEmptyTag();

	/**
	 * Returns true if floating end tag.
	 * 
	 * @return true if floating end tag.
	 */
	boolean isEndTag();

	/**
	 * Returns true for "global tag" (basically, without prefix)
	 * 
	 * @return true for "global tag" (basically, without prefix)
	 */
	boolean isGlobalTag();

	/**
	 * Returns true for no start and the end tags in source.
	 * 
	 * Provided for some very special cases when, for example, and HTML tag is
	 * assumed in an HTML document that does not have a literal HTML tag.
	 * 
	 * ISSUE: check with clients to see if still needed
	 * 
	 * @return true or no start and the end tags in source.
	 */
	boolean isImplicitTag();

	/**
	 * isJSPTag method
	 * 
	 * @return boolean
	 * 
	 * ISSUE: change to isContainerLanguageTag(String type);
	 */
	boolean isJSPTag();

	/**
	 * Returns true if start tag is closed.
	 * 
	 * @return true if start tag is closed.
	 */
	boolean isStartTagClosed();

	/**
	 * returns true if is xml tag
	 * 
	 * ISSUE: need to spec this better.
	 * 
	 * @return true if is xml tag
	 */
	boolean isXMLTag();

	/**
	 * NOT CLIENT API
	 * 
	 * notifyEndTagChanged
	 * 
	 */
	void notifyEndTagChanged();

	/**
	 * NOT CLIENT API
	 * 
	 * notifyStartTagChanged
	 * 
	 */
	void notifyStartTagChanged();

	/**
	 * NOT CLIENT API
	 * 
	 * Signify that this tag is a comment
	 * 
	 * For use only by parsers.
	 * 
	 */
	void setCommentTag(boolean isCommentTag);

	/**
	 * NOT CLIENT API
	 * 
	 * Signify that this tag is an empty tag
	 * 
	 * For use only by parsers
	 */
	void setEmptyTag(boolean isEmptyTag);

	/**
	 * NOT CLIENT API
	 * 
	 * Signify that this tag is a JSP tag
	 * 
	 * For use only by parsers
	 * 
	 * ISSUE: I have had one non-parsing client who has had to use this ...
	 * need to check
	 * 
	 */
	void setJSPTag(boolean isJSPTag);

	/**
	 * NOT IMPLEMENTED. Is defined here in preparation for DOM 3.
	 */
	public void setIdAttribute(String name, boolean isId);

	/**
	 * NOT IMPLEMENTED. Is defined here in preparation for DOM 3.
	 */
	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId);

	/**
	 * NOT IMPLEMENTED. Is defined here in preparation for DOM 3.
	 */
	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException;

}
