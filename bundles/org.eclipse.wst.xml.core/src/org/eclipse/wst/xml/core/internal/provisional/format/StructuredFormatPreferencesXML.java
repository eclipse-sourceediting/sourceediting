/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.provisional.format;

import org.eclipse.wst.sse.core.internal.format.StructuredFormatPreferences;

public class StructuredFormatPreferencesXML extends StructuredFormatPreferences {
	private boolean fSplitMultiAttrs = false;
	private boolean fPreservePCDATAContent = false;
	private boolean fAlignEndBracket = false;

	/**
	 * True if formatter should split elements with multiple attributes onto
	 * new lines.
	 * 
	 * @return boolean
	 */
	public boolean getSplitMultiAttrs() {
		return fSplitMultiAttrs;
	}

	/**
	 * Sets whether or not formatter should split elements with multiple
	 * attributes onto new lines.
	 * 
	 * @param splitMultiAttrs
	 */
	public void setSplitMultiAttrs(boolean splitMultiAttrs) {
		fSplitMultiAttrs = splitMultiAttrs;
	}

	/**
	 * True if tags with PCDATA content should not have their whitespace
	 * messed with when formatting.
	 * 
	 * @return boolean
	 */
	public boolean isPreservePCDATAContent() {
		return fPreservePCDATAContent;
	}

	/**
	 * Sets whether or not formatter should preserve whitespace in tags with
	 * PCDATA content.
	 * 
	 * @param preservePCDATAContent
	 */
	public void setPreservePCDATAContent(boolean preservePCDATAContent) {
		fPreservePCDATAContent = preservePCDATAContent;
	}

	/**
	 * True if end brackets of start tags should be placed on a new line if
	 * the start tag spans more than one line.
	 * 
	 * @return
	 */
	public boolean isAlignEndBracket() {
		return fAlignEndBracket;
	}

	/**
	 * Sets whether or not formatter should align the end bracket of a start
	 * tag on a new line if the start tag spans more than one line.
	 * 
	 * @param alignEndBracket
	 */
	public void setAlignEndBracket(boolean alignEndBracket) {
		fAlignEndBracket = alignEndBracket;
	}
}
