/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
 * These are items that do not change from element to element.
 * Passed from node to node in a recursive call because sometimes
 * child nodes don't have access to the preferences
 */
public interface IStructuredFormatPreferences {
	
	boolean getClearAllBlankLines();

	String getIndent();

	int getLineWidth();

	void setClearAllBlankLines(boolean clearAllBlankLines);

	void setIndent(String indent);

	void setLineWidth(int lineWidth);
}
