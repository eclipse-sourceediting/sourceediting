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



import org.eclipse.wst.sse.core.text.ITextRegion;
import org.w3c.dom.Attr;

/**
 */
public interface XMLAttr extends XMLNode, Attr {

	/**
	 */
	ITextRegion getEqualRegion();

	/**
	 * Check if Attr has JSP in value
	 */
	boolean hasJSPValue();

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

	String getValueRegionText();

	int getValueRegionStartOffset();

	String getNameRegionText();

	int getNameRegionStartOffset();

	/**
	 * @return
	 */
	int getNameRegionEndOffset();

	/**
	 * @return
	 */
	int getNameRegionTextEndOffset();

}
