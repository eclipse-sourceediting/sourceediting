/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * <p>When undoing {@link JSPRenameChange}s need to be sure not only to perform
 * the UndoDocumentChange but to also save the JSP file otherwise one of two
 * unwanted things could happen:
 * <ul><li>an open editor with the file could be marked dirty</li>
 * <li>or if the changed file is not open in an editor then the changes will
 * not be saved to the file and thus not persist.</li></ul></p>
 * 
 * <p>Therefore a {@link JSPRenameUndoChange} wraps another {@link Change} which
 * is considered the "undo change".  When a perform is called on {@link JSPRenameUndoChange}
 * the perform is called on the wrapped "undo change", another {@link JSPRenameUndoChange}
 * is created as the "redo change" from the return of the perform on the "undo change"
 * and then most importantly the updated {@link IDocument} is saved to the {@link IFile}
 * if the JSP file is not open in an editor, or a save is called on the open {@link ITextEditor}
 * that is editing the changed JSP file.</p>
 */
public class JSPRenameUndoChange extends JSPRenameChange {
	/**
	 * The "undo change" being wrapped
	 */
	private Change fUndoChange;
	
	/**
	 * <p>Create the {@link JSPRenameUndoChange} from the {@link JSPRenameChange}
	 * that created the undo change and the undo change itself.</p>
	 * 
	 * @param originalChange the {@link JSPRenameChange} that created the <code>undoChange</code>
	 * @param undoChange the undo change to be wrapped by this {@link JSPRenameUndoChange}
	 */
	public JSPRenameUndoChange(JSPRenameChange originalChange, Change undoChange) {
		super(originalChange);
		fUndoChange = undoChange;
	}
	
	/**
	 * <p>See {@link JSPRenameUndoChange} class description for more details.</p>
	 * 
	 * @return a {@link JSPRenameUndoChange} wrapping the "redo change" returned by the
	 * call to {@link Change#perform(IProgressMonitor)} on the wrapped "undo change".
	 * 
	 * @see JSPRenameUndoChange
	 * @see org.eclipse.jst.jsp.ui.internal.java.refactoring.JSPRenameChange#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {
		//apply edit
		Change redoChange = fUndoChange.perform(pm);
		redoChange = new JSPRenameUndoChange(this, redoChange);
		
		//save the file
		saveJSPFile(this.fJSPFile, this.getJSPDoc());
		
		return redoChange;
	}
}
