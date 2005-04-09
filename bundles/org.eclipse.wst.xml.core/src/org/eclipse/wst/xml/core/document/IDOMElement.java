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



import org.w3c.dom.Element;

/**
 * This interface provides extensions to corresponding DOM interface to enable
 * functions for source editing and incremental parsing.
 * 
 * @since 1.0
 * 
 */
public interface IDOMElement extends IDOMNode, Element {

	/**
	 * @deprecated this should probably not be public
	 */
	int getEndStartOffset();

	/**
	 * get's the end tag name
	 */
	String getEndTagName();

	/**
	 * @deprecated this should probably not be public
	 * 
	 */
	int getStartEndOffset();

	/**
	 * Returns true if has an end tag
	 */
	boolean hasEndTag();

	/**
	 * returns true if has a start tag.
	 */
	boolean hasStartTag();

	/**
	 * returns true if this element is a comment
	 */
	boolean isCommentTag();

	/**
	 * isEmptyTag method
	 * 
	 * @return boolean
	 */
	boolean isEmptyTag();

	/**
	 */
	boolean isEndTag();

	/**
	 * Returns true for "global tag" (basically, without prefix)
	 */
	boolean isGlobalTag();

	/**
	 * Returns true for no the start and the end tags
	 * 
	 * Provided for some very special cases when, for example, and HTML tag is
	 * assumed in an HTML document that does not have an HTML tag.
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
	 */
	boolean isStartTagClosed();

	/**
	 * returns true if is xml tag
	 * 
	 * ISSUE: I need to spec this better.
	 * 
	 */
	boolean isXMLTag();

	/**
	 * notifyEndTagChanged
	 * 
	 * @deprecated this should probably not be public
	 */
	void notifyEndTagChanged();

	/**
	 * notifyStartTagChanged
	 * 
	 * @deprecated this should probably not be public
	 */
	void notifyStartTagChanged();

	/**
	 * Signify that this tag is a comment
	 * 
	 * For use only by parsers.
	 * 
	 */
	void setCommentTag(boolean isCommentTag);

	/**
	 * Signify that this tag is an empty tag
	 * 
	 * For use only by parsers
	 */
	void setEmptyTag(boolean isEmptyTag);

	/**
	 * Signify that this tag is a JSP tag
	 * 
	 * For use only by parsers
	 * 
	 */
	void setJSPTag(boolean isJSPTag);
}
