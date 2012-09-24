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
package org.eclipse.wst.sse.core.internal.format;

/**
 * These are items that change from element to element.
 * Passed from node to node in a recursive call.
 * eg. current indent is 2 deep, but for the next node might be 3...
 */
public interface IStructuredFormatContraints {
	boolean getClearAllBlankLines();

	String getCurrentIndent();

	boolean getFormatWithSiblingIndent();

	boolean getInPreserveSpaceElement();

	/** 
	 * some special elements can ignore clearing blank lines
	 * */
	void setClearAllBlankLines(boolean clearAllBlankLines);

	void setCurrentIndent(String currentIndent);

	void setFormatWithSiblingIndent(boolean formatWithSiblingIndent);
	
	void setInPreserveSpaceElement(boolean inPreserveSpaceElement);
}
