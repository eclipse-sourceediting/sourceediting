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
package org.eclipse.wst.xml.core.document;



import org.w3c.dom.Element;

/**
 */
public interface XMLElement extends XMLNode, Element {

	/**
	 */
	String getEndTagName();

	/**
	 */
	boolean hasStartTag();

	/**
	 */
	boolean hasEndTag();

	/**
	 */
	boolean isCommentTag();

	/**
	 * isEmptyTag method
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
	 */
	boolean isImplicitTag();

	/**
	 * isJSPTag method
	 * @return boolean
	 */
	boolean isJSPTag();

	/**
	 */
	boolean isStartTagClosed();

	/**
	 */
	boolean isXMLTag();

	/**
	 */
	void setCommentTag(boolean isCommentTag);

	/**
	 */
	void setEmptyTag(boolean isEmptyTag);

	/**
	 */
	void setJSPTag(boolean isJSPTag);

	/**
	 * @deprecated this should probably not be public
	 *
	 */
	int getStartEndOffset();

	/**
	 * @deprecated this should probably not be public
	 *
	 */
	int getEndStartOffset();

	/**
	 * @deprecated this should not be public
	 *
	 */
	void notifyEndTagChanged();

	/**
	 * @deprecated this should not be public
	 *
	 */
	void notifyStartTagChanged();
}
