/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen M�ller - xml:space='preserve' support
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
