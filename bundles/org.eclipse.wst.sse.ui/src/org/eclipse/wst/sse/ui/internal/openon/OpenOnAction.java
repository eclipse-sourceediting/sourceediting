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
package org.eclipse.wst.sse.ui.internal.openon;

import java.util.ResourceBundle;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.extensions.openon.IOpenOn;
import org.eclipse.wst.sse.ui.openon.OpenOnProvider;


/**
 * Determines the appropriate IOpenFileAction to call based on current partition.
 */
public class OpenOnAction extends TextEditorAction {
	public OpenOnAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		BusyIndicator.showWhile(getTextEditor().getEditorSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				ITextEditor editor = getTextEditor();
				
				// figure out current offset
				int offset = -1;
				if (editor instanceof StructuredTextEditor) {
					offset = ((StructuredTextEditor) editor).getCaretPosition();
				} else {
					if (editor.getSelectionProvider() != null) {
						ISelection sel = editor.getSelectionProvider().getSelection();
						if (sel instanceof ITextSelection) {
							offset = ((ITextSelection)sel).getOffset();
						}
					}
				}
				IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
				IOpenOn openOn = OpenOnProvider.getInstance().getOpenOn(document, offset);
				if (openOn != null) {
					openOn.openOn(document, new Region(offset, 0));
				}
			}
		});
	}
}
