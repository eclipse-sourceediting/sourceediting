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
public interface XMLAttr extends XMLNode, Attr {

	/**
	 */
	ITextRegion getEqualRegion();

	/**
	 * @return
	 */
	int getNameRegionEndOffset();

	int getNameRegionStartOffset();

	String getNameRegionText();

	/**
	 * @return
	 */
	int getNameRegionTextEndOffset();

	int getValueRegionStartOffset();

	String getValueRegionText();

	/**
	 * Check if Attr has JSP in value
	 */
	boolean hasNestedValue();

	/**
	 * Check if Attr has only name but not equal sign nor value
	 */
	boolean hasNameOnly();

	/**
	 */
	boolean isGlobalAttr();

	/**
	 */
	boolean isXMLAttr();

}
