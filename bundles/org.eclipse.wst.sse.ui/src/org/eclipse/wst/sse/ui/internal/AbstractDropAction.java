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
package org.eclipse.wst.sse.ui.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 */
public abstract class AbstractDropAction implements IDropAction {

	/*
	 * replace targetEditor's current selection by "text"
	 */
	protected boolean insert(String text, IExtendedSimpleEditor targetEditor) {
		if (text == null || text.length() == 0) {
			return false;
		}

		Point pt = targetEditor.getSelectionRange();
		IDocument doc = targetEditor.getDocument();

		try {
			doc.replace(pt.x, pt.y, text);
		} catch (BadLocationException e) {
			return false;
		}

		ITextEditor textEditor = null;

		if (targetEditor instanceof ITextEditor) {
			textEditor = (ITextEditor) targetEditor;
		}
		if (textEditor == null && targetEditor.getEditorPart() instanceof ITextEditor) {
			textEditor = (ITextEditor) targetEditor.getEditorPart();
		}
		if (textEditor == null && targetEditor instanceof IAdaptable) {
			textEditor = (ITextEditor) ((IAdaptable) targetEditor).getAdapter(ITextEditor.class);
		}
		if (textEditor == null) {
			textEditor = (ITextEditor) targetEditor.getEditorPart().getAdapter(ITextEditor.class);
		}
		if (textEditor != null) {
			ISelectionProvider sp = textEditor.getSelectionProvider();
			ISelection sel = new TextSelection(pt.x, text.length());
			sp.setSelection(sel);
			textEditor.selectAndReveal(pt.x, text.length());
		}

		return true;
	}


	/**
	 * @see IDropAction#isSupportedData(Object)
	 */
	public boolean isSupportedData(Object data) {
		return true;
	}

	/**
	 * @see IDropAction#run(DropTargetEvent, IExtendedSimpleEditor)
	 */
	public abstract boolean run(DropTargetEvent event, IExtendedSimpleEditor targetEditor);
}
