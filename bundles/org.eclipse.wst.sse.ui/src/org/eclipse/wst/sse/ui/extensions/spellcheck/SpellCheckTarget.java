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
package org.eclipse.wst.sse.ui.extensions.spellcheck;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;



/**
 * ISpellCheckTarget
 */
public interface SpellCheckTarget {
	void setSpellChecker(SpellChecker checker);

	void setTextEditor(ITextEditor editor);

	boolean canPerformSpellCheck();

	boolean canPerformIgnore();

	boolean canPerformIgnoreAll();

	boolean canPerformChange();

	boolean canPerformChangeAll();

	SpellCheckElement getAndSelectNextMisspelledElement(boolean init) throws SpellCheckException;

	SpellCheckOptionDialog getOptionDialog();

	void replaceSelection(String text, Shell shell) throws SpellCheckException;

	int findAndSelect(int start, String find);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label);

	/**
	 * End recording undo transactions.
	 */
	void endRecording(Object requester);

	SpellCheckSelectionManager getSpellCheckSelectionManager();
}
