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
package org.eclipse.wst.sse.ui;



import org.eclipse.ui.IEditorActionBarContributor;

public interface ISourceViewerActionBarContributor extends IEditorActionBarContributor {

	/**
	 * Enables disables actions that are specific to the source viewer (and should only work when
	 * the source viewer is enabled)
	 * @param enabled true if source viewer is currently enabled, false otherwise
	 */
	public void setViewerSpecificContributionsEnabled(boolean enabled);
}
