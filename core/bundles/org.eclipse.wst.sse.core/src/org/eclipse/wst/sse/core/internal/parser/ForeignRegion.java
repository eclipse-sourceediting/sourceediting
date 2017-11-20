/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.parser;



import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


public class ForeignRegion extends ContextRegion {

	private String language = null;
	private String surroundingTag = null;

	public ForeignRegion(String newContext, int newStart, int newTextLength, int newLength) {
		super(newContext, newStart, newTextLength, newLength);
	}

	public ForeignRegion(String newContext, int newStart, int newTextLength, int newLength, String newLanguage) {
		super(newContext, newStart, newTextLength, newLength);
		setLanguage(newLanguage);
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getLanguage() {
		return language;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getSurroundingTag() {
		return surroundingTag;
	}

	/**
	 * 
	 * @param newLanguage
	 *            java.lang.String
	 */
	public void setLanguage(java.lang.String newLanguage) {
		language = newLanguage;
	}

	/**
	 * @param newSurroundingTag
	 *            java.lang.String
	 */
	public void setSurroundingTag(java.lang.String newSurroundingTag) {
		surroundingTag = newSurroundingTag;
	}

	public String toString() {
		return "FOREIGN: " + super.toString();//$NON-NLS-1$
	}

	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion flatnode, String changes, int requestStart, int lengthToReplace) {
		org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent result = null;
		int lengthDifference = org.eclipse.wst.sse.core.internal.util.Utilities.calculateLengthDifference(changes, lengthToReplace);
		fLength += lengthDifference;
		fTextLength += lengthDifference;
		result = new RegionChangedEvent(flatnode.getParentDocument(), requester, flatnode, this, changes, requestStart, lengthToReplace);

		return result;
	}
}
