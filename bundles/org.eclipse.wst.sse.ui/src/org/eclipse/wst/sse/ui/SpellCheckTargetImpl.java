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
package org.eclipse.wst.sse.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.IStructuredModel;
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
import org.eclipse.wst.sse.ui.util.Assert;


/**
 * SpellCheckTargetImpl
 */
public class SpellCheckTargetImpl implements SpellCheckTarget {

	public static class EditorSpellCheckException extends SpellCheckException {
		EditorSpellCheckException(String msg) {
			super(msg);
		}

		public IStatus getStatus() {
			return new Status(IStatus.ERROR, EditorPlugin.ID, 0, getMessage(), this);
		}
	}

	private SpellChecker checker;
	private StructuredTextEditor editor;
	private IFindReplaceTarget target;
	public static final String ID = "spellchecktarget"; //$NON-NLS-1$

	public SpellCheckTargetImpl() {
		super();
	}

	/**
	 * @see ISpellCheckTarget#beginRecording()
	 */
	public void beginRecording(Object requester, String label) {
		if (editor == null)
			return;

		IStructuredModel model = editor.getModel();
		if (model == null)
			return;

		model.beginRecording(requester, label);
	}

	/**
	 * @see ISpellCheckTarget#canPerformChange()
	 */
	public boolean canPerformChange() {
		if (editor == null || checker == null || target == null)
			return false;

		//return target.isEditable() && editor.isEditable() && !editor.isEditorInputReadOnly();
		return target.isEditable() && editor.isEditable();
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
		return (editor != null && checker != null ? true : false);
	}

	/**
	 * @see ISpellCheckTarget#canPerformIgnoreAll()
	 */
	public boolean canPerformIgnoreAll() {
		return (editor != null && checker != null ? true : false);
	}

	/**
	 * @see ISpellCheckTarget#canPerformSpellCheck()
	 */
	public boolean canPerformSpellCheck() {
		return (editor != null && checker != null ? true : false);
	}

	/**
	 * @see ISpellCheckTarget#endRecording()
	 */
	public void endRecording(Object requester) {
		if (editor == null)
			return;

		IStructuredModel model = editor.getModel();
		if (model == null)
			return;

		model.endRecording(requester);
	}

	/**
	 * @see ISpellCheckTarget#findAndSelect(int, String)
	 */
	public int findAndSelect(int pos, String find) {
		return (target != null ? target.findAndSelect(pos, find, true, true, true) : -1);
	}

	/**
	 * @see ISpellCheckTarget#getAndSelectNextMisspelledElement(boolean)
	 */
	public SpellCheckElement getAndSelectNextMisspelledElement(boolean init) throws SpellCheckException {
		if (checker == null || editor == null || target == null)
			return null;

		IStructuredModel sm = editor.getModel();
		IStructuredDocument fm = sm.getStructuredDocument();

		int start;
		if (init) {
			start = 0;
		}
		else {
			Point pt = target.getSelection();
			start = pt.x + pt.y;
		}

		IStructuredDocumentRegion node = fm.getRegionAtCharacterOffset(start);
		if (node == null) {
			return null;
		}

		ITextRegion startRegion = node.getRegionAtCharacterOffset(start);
		if (startRegion == null) {
			return null;
		}

		boolean skip = true;
		while (node != null) {
			ITextRegionList regions = node.getRegions();
			int size = regions.size();
			for (int i = 0; i < size; ++i) {
				ITextRegion r = regions.get(i);
				if (skip && startRegion.equals(r)) {
					skip = false;
				}
				if (skip == false) {
					if (isValidType(r.getType())) {
						String text;
						if (start > node.getStartOffset(r)) {
							text = node.getText(r);
							int offset = start - node.getStartOffset(r);
							for (; offset < text.length(); ++offset) {
								if (!Character.isLetterOrDigit(text.charAt(offset - 1))) {
									break;
								}
							}
							text = text.substring(offset);
						}
						else {
							text = node.getText(r);
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
			node = node.getNext();
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
		if (editor == null)
			return null;
		return (SpellCheckSelectionManager) editor.getViewerSelectionManager();
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
		if (target == null)
			return;
		IStatus status = editor.validateEdit(shell);
		if (!status.isOK()) {
			throw new EditorSpellCheckException(status.getMessage());
		}

		target.replaceSelection(text);
	}

	/**
	 * @see ISpellCheckTarget#setSpellChecker(ISpellChecker)
	 */
	public void setSpellChecker(SpellChecker checker) {
		this.checker = checker;
	}

	/* (non-Javadoc)
	 */
	public void setTextEditor(ITextEditor editor) {
		Assert.isTrue(editor instanceof StructuredTextEditor);
		this.editor = (StructuredTextEditor) editor;
		target = (IFindReplaceTarget) editor.getAdapter(IFindReplaceTarget.class);
	}
}
