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
package org.eclipse.wst.sse.ui.extensions.spellcheck;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
/**
 * @deprecated - to be removed in M4
 */
public interface SpellCheckTarget {

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label);

	boolean canPerformChange();

	boolean canPerformChangeAll();

	boolean canPerformIgnore();

	boolean canPerformIgnoreAll();

	boolean canPerformSpellCheck();

	/**
	 * End recording undo transactions.
	 */
	void endRecording(Object requester);

	int findAndSelect(int start, String find);

	SpellCheckElement getAndSelectNextMisspelledElement(boolean init) throws SpellCheckException;

	SpellCheckOptionDialog getOptionDialog();

	SpellCheckSelectionManager getSpellCheckSelectionManager();

	void replaceSelection(String text, Shell shell) throws SpellCheckException;

	void setSpellChecker(SpellChecker checker);

	void setTextEditor(ITextEditor editor);
}
