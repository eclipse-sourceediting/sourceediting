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

/**
 * @deprecated - to be removed in M4
 */

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckElement;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckException;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckOptionDialog;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckSelectionManager;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckTarget;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellChecker;

/**
 * @deprecated - will be removed in M4
 */
public class SpellCheckTargetImpl implements SpellCheckTarget {

	public static class EditorSpellCheckException extends SpellCheckException {
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		EditorSpellCheckException(String msg) {
			super(msg);
		}

		public IStatus getStatus() {
			return new Status(IStatus.ERROR, SSEUIPlugin.ID, 0, getMessage(), this);
		}
	}

	public static final String ID = "spellchecktarget"; //$NON-NLS-1$

	private SpellChecker checker;
	private ITextEditor fTextEditor;
	private IFindReplaceTarget target;
	private IStructuredModel fUndoRecorder = null;

	public SpellCheckTargetImpl() {
		super();
	}

	/**
	 * @see ISpellCheckTarget#beginRecording()
	 */
	public void beginRecording(Object requester, String label) {
		if (fTextEditor == null)
			return;

		fUndoRecorder = StructuredModelManager.getModelManager().getExistingModelForEdit(getEditingDocument());
		if (fUndoRecorder == null)
			return;

		fUndoRecorder.beginRecording(requester, label);
	}

	/**
	 * @see ISpellCheckTarget#canPerformChange()
	 */
	public boolean canPerformChange() {
		if (fTextEditor == null || checker == null || target == null)
			return false;

		return target.isEditable() && fTextEditor.isEditable();
	}

	/**
	 * @see ISpellCheckTarget#canPerformChangeAll()
	 */
	public boolean canPerformChangeAll() {
		return canPerformChange();
	}

	/**
	 * @see ISpellCheckTarget#canPerformIgnore()
	 */
	public boolean canPerformIgnore() {
		return fTextEditor != null && checker != null;
	}

	/**
	 * @see ISpellCheckTarget#canPerformIgnoreAll()
	 */
	public boolean canPerformIgnoreAll() {
		return canPerformIgnore();
	}

	/**
	 * @see ISpellCheckTarget#canPerformSpellCheck()
	 */
	public boolean canPerformSpellCheck() {
		return fTextEditor != null && checker != null;
	}

	/**
	 * @see ISpellCheckTarget#endRecording()
	 */
	public void endRecording(Object requester) {
		if (fUndoRecorder == null)
			return;

		fUndoRecorder.endRecording(requester);
		fUndoRecorder = null;
	}

	/**
	 * @see ISpellCheckTarget#findAndSelect(int, String)
	 */
	public int findAndSelect(int pos, String find) {
		return (target != null ? target.findAndSelect(pos, find, true, true, true) : -1);
	}

	IDocument getEditingDocument() {
		return fTextEditor.getDocumentProvider().getDocument(fTextEditor.getEditorInput());
	}

	/**
	 * @see ISpellCheckTarget#getAndSelectNextMisspelledElement(boolean)
	 */
	public SpellCheckElement getAndSelectNextMisspelledElement(boolean init) throws SpellCheckException {
		if (checker == null || fTextEditor == null || target == null)
			return null;

		IDocument document = getEditingDocument();
		
		if (document instanceof IStructuredDocument) {
			IStructuredDocument structuredDocument = (IStructuredDocument) getEditingDocument();

			int start;
			if (init) {
				start = 0;
			}
			else {
				Point pt = target.getSelection();
				start = pt.x + pt.y;
			}

			IStructuredDocumentRegion documentRegion = structuredDocument.getRegionAtCharacterOffset(start);
			if (documentRegion == null) {
				return null;
			}

			ITextRegion startRegion = documentRegion.getRegionAtCharacterOffset(start);
			if (startRegion == null) {
				return null;
			}

			boolean skip = true;
			while (documentRegion != null) {
				ITextRegionList regions = documentRegion.getRegions();
				int size = regions.size();
				for (int i = 0; i < size; ++i) {
					ITextRegion r = regions.get(i);
					if (skip && startRegion.equals(r)) {
						skip = false;
					}
					if (skip == false) {
						if (isValidType(r.getType())) {
							String text;
							if (start > documentRegion.getStartOffset(r)) {
								text = documentRegion.getText(r);
								int offset = start - documentRegion.getStartOffset(r);
								for (; offset < text.length(); ++offset) {
									if (!Character.isLetterOrDigit(text.charAt(offset - 1))) {
										break;
									}
								}
								text = text.substring(offset);
							}
							else {
								text = documentRegion.getText(r);
							}
							SpellCheckElement[] elms = checker.createSingleWords(text);
							if (elms != null) {
								for (int j = 0; j < elms.length; ++j) {
									SpellCheckElement element = checker.verifySpell(elms[j]);
									if (element.isSpellError()) {
										target.findAndSelect(start, element.getString(), true, true, true);
										return element;
									}
								}
							}
						}
					}
				}
				documentRegion = documentRegion.getNext();
			}
		}

		return null;
	}

	/**
	 * @see ISpellCheckTarget#getOptionDialog()
	 */
	public SpellCheckOptionDialog getOptionDialog() {
		return null;
	}

	/**
	 * @see ISpellCheckTarget#getSpellCheckSelectionManager()
	 */
	public SpellCheckSelectionManager getSpellCheckSelectionManager() {
		if (fTextEditor == null)
			return null;

		ViewerSelectionManager manager = (ViewerSelectionManager) fTextEditor.getAdapter(ViewerSelectionManager.class);
		if (!(manager instanceof SpellCheckSelectionManager))
			return null;
		return (SpellCheckSelectionManager) manager;
	}

	private boolean isModifyable() {
		if (fTextEditor instanceof ITextEditorExtension2)
			return ((ITextEditorExtension2) fTextEditor).validateEditorInputState();
		else if (fTextEditor instanceof ITextEditorExtension)
			return !((ITextEditorExtension) fTextEditor).isEditorInputReadOnly();
		else if (fTextEditor != null)
			return fTextEditor.isEditable();
		else
			return false;
	}

	/**
	 */
	protected boolean isValidType(String type) {
		return false;
	}

	/**
	 * @see ISpellCheckTarget#replaceSelection(String)
	 */
	public void replaceSelection(String text, Shell shell) throws SpellCheckException {
		if (target == null || !isModifyable())
			return;

		target.replaceSelection(text);
	}

	/**
	 * @see ISpellCheckTarget#setSpellChecker(ISpellChecker)
	 */
	public void setSpellChecker(SpellChecker spellChecker) {
		checker = spellChecker;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckTarget#setTextEditor(org.eclipse.ui.texteditor.ITextEditor)
	 */
	public void setTextEditor(ITextEditor editor) {
		fTextEditor = editor;
		target = (IFindReplaceTarget) editor.getAdapter(IFindReplaceTarget.class);
	}
}
