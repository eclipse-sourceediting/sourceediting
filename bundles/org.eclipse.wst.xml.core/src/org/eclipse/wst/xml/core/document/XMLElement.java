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

public interface XMLElement extends XMLNode, Element {

	/**
	 * @deprecated this should probably not be public, but already implemented
	 */
	int getEndStartOffset();

	/**
	 */
	String getEndTagName();

	/**
	 * @deprecated this should probably not be public, but already implemented
	 *  
	 */
	int getStartEndOffset();

	/**
	 */
	boolean hasEndTag();

	/**
	 */
	boolean hasStartTag();

	/**
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
	 */
	boolean isImplicitTag();

	/**
	 * isJSPTag method
	 * 
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
	 * @deprecated this should probably not be public, but already implemented
	 */
	void notifyEndTagChanged();

	/**
	 * @deprecated this should probably not be public, but already implemented
	 */
	void notifyStartTagChanged();

	/**
	 */
	void setCommentTag(boolean isCommentTag);

	/**
	 */
	void setEmptyTag(boolean isEmptyTag);

	/**
	 */
	void setJSPTag(boolean isJSPTag);
}
