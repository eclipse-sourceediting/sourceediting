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
package org.eclipse.wst.sse.ui.extension;



import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorActionBarContributor;

public interface IExtendedContributor extends IEditorActionBarContributor, IPopupMenuContributor {
	public void contributeToMenu(IMenuManager menu);

	public void contributeToToolBar(IToolBarManager manager);

	public void contributeToStatusLine(IStatusLineManager manager);

	public void updateToolbarActions();
}
