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
package org.eclipse.wst.dtd.ui;

import org.eclipse.wst.sse.ui.ui.ActionContributor;

/**
 * XMLEditorActionContributor
 * 
 * This class should not be used inside multi page editor's ActionBarContributor,
 * since cascaded init() call from the ActionBarContributor
 * will causes exception and it leads to lose whole toolbars. 
 *
 * Instead, use SourcePageActionContributor for source page contributor
 * of multi page editor.
 * 
 * Note that this class is still valid for single page editor.
 */
public class ActionContributorDTD extends ActionContributor {
	private static final String[] EDITOR_IDS = {"org.eclipse.wst.dtd.ui.StructuredTextEditorDTD", "org.eclipse.wst.sse.ui.StructuredTextEditor"}; //$NON-NLS-1$ //$NON-NLS-2$

	/* (non-Javadoc)
	 */
	protected String[] getExtensionIDs() {
		return EDITOR_IDS;
	}
}
