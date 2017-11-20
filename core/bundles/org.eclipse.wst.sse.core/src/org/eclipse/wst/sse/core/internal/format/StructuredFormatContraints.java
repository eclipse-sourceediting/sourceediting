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
 *     Jesper Steen Møller - xml:space='preserve' support
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.format;

public class StructuredFormatContraints implements IStructuredFormatContraints {
	private boolean fClearAllBlankLines;
	private String fCurrentIndent = ""; //$NON-NLS-1$
	private boolean fFormatWithSiblingIndent = false;
	private boolean fInPreserveSpaceElement = false;

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

	public boolean getInPreserveSpaceElement() {
		return fInPreserveSpaceElement;
	}

	public void setInPreserveSpaceElement(boolean inPreserveSpaceElement) {
		fInPreserveSpaceElement = inPreserveSpaceElement;		
	}
}
