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



import org.eclipse.wst.sse.core.text.ITextRegion;
import org.w3c.dom.Attr;


/**
 * This interface provides extensions to corresponding DOM interface to enable
 * functions for source editing and incremental parsing.
 * 
 * @since 1.0
 * 
 */
public interface IDOMAttr extends IDOMNode, Attr {

	/**
	 * Get's the region in attribute representing the '=' sign. May or may not
	 * have whitespace surrounding it.
	 * 
	 */
	ITextRegion getEqualRegion();

	/**
	 * Gets the source location of the end of the attribute name, including
	 * whitespace.
	 * 
	 * @return
	 */
	int getNameRegionEndOffset();

	/**
	 * Gets the source location of the start of the attribute name.
	 * 
	 * @return
	 */
	int getNameRegionStartOffset();


	/**
	 * Gets the text associated with the attribute name.
	 * 
	 * @return
	 */
	String getNameRegionText();

	/**
	 * Gets the source location of the end of the attribute name, excluding
	 * whitespace.
	 * 
	 * @return
	 */
	int getNameRegionTextEndOffset();

	/**
	 * Gets the source location of the start of the attribute value.
	 * 
	 * @return
	 */
	int getValueRegionStartOffset();

	/**
	 * Gets the text associated with the attribute value.
	 * 
	 * @return
	 */
	String getValueRegionText();

	/**
	 * Check if Attr has a nested value (such as a JSP expression).
	 * 
	 * @return true if contains a nested value
	 */
	boolean hasNestedValue();

	/**
	 * Check if Attr has only name but not equal sign nor value.
	 * 
	 * @return true if has only name but not equal sign nor value.
	 */
	boolean hasNameOnly();

	/**
	 * Returns true if attribute is defined globally for document. Returns
	 * false if attribute is preceeded by a prefix (whether valid name space
	 * or not). Returns true if its owning element is a global element.
	 * 
	 */
	boolean isGlobalAttr();

	/**
	 * Returns true if is xml attr
	 * 
	 * ISSUE: need to figure out how to specify this one.
	 */
	boolean isXMLAttr();

}
