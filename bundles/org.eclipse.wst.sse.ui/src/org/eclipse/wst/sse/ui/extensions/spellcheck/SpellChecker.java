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



/**
 * ISpellChecker
 */
public interface SpellChecker {

	// User Dictionary
	void addWord(String word) throws SpellCheckException;

	void deleteWord(String word) throws SpellCheckException;

	String[] getUserWords() throws SpellCheckException;

	// Spell Check
	SpellCheckElement[] createSingleWords(String text) throws SpellCheckException;

	SpellCheckElement verifySpell(SpellCheckElement element) throws SpellCheckException;

	String[] getCandidates(SpellCheckElement element) throws SpellCheckException;
}
