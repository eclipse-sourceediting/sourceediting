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
package org.eclipse.wst.sse.core.events;

import org.eclipse.wst.sse.core.text.IStructuredDocument;

/**
 * The NewModelEvent is fired when an instance of a IStructuredDocument sets or
 * replaces all of its text. So, in the case of replacement, its a new model,
 * conceptually and practically, even though it is technically the same
 * instance of the IStructuredDocument.
 */
public class NewModelEvent extends StructuredDocumentEvent {
	public NewModelEvent(IStructuredDocument source, Object originalSource) {
		super(source, originalSource);
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument Events
	 * in the super class. All the text of the new model is returned.
	 * 
	 * @deprecated - use getText()
	 */
	public String getOriginalChanges() {
		String results = null;
		results = getStructuredDocument().getText();
		return results;
	}

	public String getText() {
		String results = getStructuredDocument().getText();
		return results;
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument Events
	 * in the super class. Since we have, basically no knowledge of what we are
	 * replacing ... so we'll return zero.
	 * 
	 * @deprecated
	 */
	public int getOriginalLength() {
		return fLength;
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument Events
	 * in the super class.
	 */
	public int getLength() {
		return fLength;
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument Events
	 * in the super class. It always will return zero.
	 * 
	 * @deprecated
	 */
	public int getOriginalStart() {
		return 0;
	}

	/**
	 * This doesn't mean quite the same thing as the IStructuredDocument Events
	 * in the super class. It always will return zero.
	 */
	public int getOffset() {
		return 0;
	}
}
