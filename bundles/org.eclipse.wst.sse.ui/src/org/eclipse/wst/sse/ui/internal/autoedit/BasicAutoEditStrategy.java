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
package org.eclipse.wst.sse.ui.internal.autoedit;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @deprecated Just copy the one helper method into your IAutoEditStrategy
 */
public abstract class BasicAutoEditStrategy implements IAutoEditStrategy {
	/**
	 * Return the active text editor if possible, otherwise the active editor
	 * part.
	 * 
	 * @return
	 */
	protected Object getActiveTextEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IEditorPart editor = page.getActiveEditor();
				if (editor != null) {
					if (editor instanceof ITextEditor)
						return editor;
					ITextEditor textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
					if (textEditor != null)
						return textEditor;
					return editor;
				}
			}
		}
		return null;
	}
}
