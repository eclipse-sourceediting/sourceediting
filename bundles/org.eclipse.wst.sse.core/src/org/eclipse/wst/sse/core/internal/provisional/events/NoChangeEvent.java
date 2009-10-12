/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.provisional.events;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * This event is sent if, after analysis, it is found there is no reason to
 * change the structuredDocument. This might occur, for example, if someone
 * pastes in the exact same text that they are replacing, or if someone tries
 * to change a read-only region.
 * 
 * This might be important, for example, if some state variables are set on an
 * "about the change" event. then if there is no change (i.e.no other event is
 * fired), those state variables could reset, or whatever, upon receiving this
 * event.
 * 
 * @plannedfor 1.0
 */
public class NoChangeEvent extends StructuredDocumentEvent {
	/**
	 * NO_CONTENT_CHANGE means that a some text was requested to be replaced
	 * with identical text, so no change is actually done.
	 */
	public final static int NO_CONTENT_CHANGE = 2;
	/**
	 * NO_EVENT is used in rare error conditions, when, basically, a request
	 * to change the document is made before the previous request has
	 * completed. This event to used so aboutToChange/Changed cycles can
	 * complete as required, but the document is most likely not modified as
	 * expected.
	 */
	public final static int NO_EVENT = 8;
	/**
	 * READ_ONLY_STATE_CHANGE means that the "read only" state of the text was
	 * changed, not the content itself.
	 */
	public final static int READ_ONLY_STATE_CHANGE = 4;

	/**
	 * set to one of the above detailed reasons for why no change was done.
	 */
	public int reason = 0;

	/**
	 * NoChangeEvent constructor. This event can occur if there was a request
	 * to modify a document or its properties, but there as not really is no
	 * change to a document's content.
	 * 
	 * @param source
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
