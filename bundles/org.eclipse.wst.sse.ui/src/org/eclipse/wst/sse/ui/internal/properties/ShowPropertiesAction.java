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
package org.eclipse.wst.sse.ui.internal.properties;

import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.ShowViewAction;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;

/**
 * Surfaces the Properties view
 * 
 * @author Nitin Dahyabhai
 */
public class ShowPropertiesAction extends ShowViewAction {
	private final static String VIEW_ID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$

	public ShowPropertiesAction() {
		super(SSEUIMessages.ShowPropertiesAction_0, EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_OBJ_PROP_PS)); //$NON-NLS-1$
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IHelpContextIds.CONTMNU_PROPERTIES_HELPID);
	}

	protected String getViewID() {
		return VIEW_ID;
	}
}
