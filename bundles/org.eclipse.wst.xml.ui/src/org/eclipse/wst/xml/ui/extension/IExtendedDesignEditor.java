/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.extension;

import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.extension.IExtendedMarkupEditorExtension;


/*
 * This class is currently an internal class used by Quick Edit view.
 */
public interface IExtendedDesignEditor extends IExtendedMarkupEditorExtension {

	IEditorInput getActiveEditorInput();

	IStructuredModel getActiveModel();

	IDesignViewerSelectionManager getDesignViewerSelectionMediator();

	IEditorInput getEditorInput();

	IStructuredModel getModel();
}
