/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.format;

public class StructuredFormatPreferences implements IStructuredFormatPreferences {
	private boolean fClearAllBlankLines;
	private String fIndent;
	private int fLineWidth;

	public boolean getClearAllBlankLines() {
		return fClearAllBlankLines;
	}

	public String getIndent() {
		return fIndent;
	}

	public int getLineWidth() {
		return fLineWidth;
	}

	public void setClearAllBlankLines(boolean clearAllBlankLines) {
		fClearAllBlankLines = clearAllBlankLines;
	}

	public void setIndent(String indent) {
		fIndent = indent;
	}

	public void setLineWidth(int lineWidth) {
		fLineWidth = lineWidth;
	}
}
