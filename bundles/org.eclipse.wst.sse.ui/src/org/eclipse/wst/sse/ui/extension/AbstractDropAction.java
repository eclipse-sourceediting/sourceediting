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
		}
		catch (BadLocationException e) {
			return false;
		}

		if (targetEditor instanceof ITextEditor) {
			ISelectionProvider sp = ((ITextEditor) targetEditor).getSelectionProvider();
			ISelection sel = new TextSelection(pt.x, text.length());
			sp.setSelection(sel);
		}

		return true;
	}
}
