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
package org.eclipse.wst.sse.core.format;

public class StructuredFormatContraints implements IStructuredFormatContraints {
	protected int fAvailableLineWidth;
	protected boolean fClearAllBlankLines;
	protected String fCurrentIndent = ""; //$NON-NLS-1$
	protected boolean fFormatWithSiblingIndent = false;

	public boolean getClearAllBlankLines() {
		return fClearAllBlankLines;
	}

	public String getCurrentIndent() {
		return fCurrentIndent;
	}

	public boolean getFormatWithSiblingIndent() {
		return fFormatWithSiblingIndent;
	}

	public void setClearAllBlankLines(boolean clearAllBlankLines) {
		fClearAllBlankLines = clearAllBlankLines;
	}

	public void setCurrentIndent(String currentIndent) {
		fCurrentIndent = currentIndent;
	}

	public void setFormatWithSiblingIndent(boolean formatWithSiblingIndent) {
		fFormatWithSiblingIndent = formatWithSiblingIndent;
	}
}
