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
package org.eclipse.wst.sse.ui.views.properties;

import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.util.ShowViewAction;


/**
 * Surfaces the Properties view
 * 
 * @author Nitin Dahyabhai
 */
public class ShowPropertiesAction extends ShowViewAction {
	private final static String VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

	public ShowPropertiesAction() {
		super(ResourceHandler.getString("ShowPropertiesAction.0"), EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_OBJ_PROP_PS));	//$NON-NLS-1$
	}

	protected String getViewID() {
		return VIEW_ID;
	}
}
