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
 * this event is thrown if, after analysis, it is found there is no reason to
 * change the structuredDocument. This might occur, for example, if someone
 * pastes in the exact same text that they are replacing, or if someone tries
 * to change a read-only region.
 * 
 * This might be important, for example, if some state variables are set on an
 * "about the change" event. then if there is no change (i.e.no other event is
 * fired), those state variables could reset, or whatever, upon receiving this
 * event.
 */
public class NoChangeEvent extends StructuredDocumentEvent {
	public final static int NO_CONTENT_CHANGE = 2;
	public final static int READ_ONLY_STATE_CHANGE = 4;
	public int reason = 0;

	/**
	 * NoChangeEvent constructor comment. This event can occur if there really
	 * is not change in content ... in which case use NO_CONTENT_CHANAGE flag
	 * in 'reason' field.. It does "double duty, however, in that some changes,
	 * such as changes in state of read-only regions results in a change that
	 * others may need to know about, but still do not signify content change.
	 * 
	 * @param source
	 *            IStructuredDocument
	 * @param originalSource
	 *            java.lang.Object
	 * @param changes
	 *            java.lang.String
	 * @param offset
	 *            int
	 * @param lengthToReplace
	 *            int
	 */
	public NoChangeEvent(IStructuredDocument source, Object originalSource, String changes, int offset, int lengthToReplace) {
		super(source, originalSource, changes, offset, lengthToReplace);
	}
}
