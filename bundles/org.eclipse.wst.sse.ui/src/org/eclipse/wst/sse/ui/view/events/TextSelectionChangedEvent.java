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
package org.eclipse.wst.sse.ui.view.events;



public class TextSelectionChangedEvent extends java.util.EventObject {

	int fTextSelectionStart;
	int fTextSelectionEnd;

	public TextSelectionChangedEvent(Object source, int textSelectionStart, int textSelectionEnd) {
		super(source);
		fTextSelectionStart = textSelectionStart;
		fTextSelectionEnd = textSelectionEnd;
	}

	public int getTextSelectionEnd() {
		return fTextSelectionEnd;
	}

	public int getTextSelectionStart() {
		return fTextSelectionStart;
	}
}
