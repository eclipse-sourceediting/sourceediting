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
package org.eclipse.wst.sse.core.internal.parser;



import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;


/**
 * Regions of this class are intended specifically for XML/HTML/JSPs. Other
 * languages may need their own subclasses. (See the updateModel method).
 */
public class ContextRegion implements ITextRegion {
	protected int fLength;

	protected int fStart;
	protected int fTextLength;
	protected String fType;

	protected ContextRegion() {
		super();
	}

	public ContextRegion(String newContext, int newStart, int newTextLength, int newLength) {
		fType = newContext;
		fStart = newStart;
		fTextLength = newTextLength;
		fLength = newLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.core.ITextRegionCore#adjust(int)
	 */
	public void adjust(int i) {
		fStart += i;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.core.ITextRegionCore#adjustLengthWith(int)
	 */
	public void adjustLength(int i) {
		fLength += i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.core.ITextRegionCore#adjustStart(int)
	 */
	public void adjustStart(int i) {
		fStart += i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#adjustTextLength(int)
	 */
	public void adjustTextLength(int i) {
		fTextLength += i;

	}

	protected boolean allLetterOrDigit(String changes) {
		boolean result = true;
		for (int i = 0; i < changes.length(); i++) {
			// TO_DO_FUTURE: check that a Java Letter or Digit is
			// the same thing as an XML letter or digit
			if (!(Character.isLetterOrDigit(changes.charAt(i)))) {
				result = false;
				break;
			}
		}
		return result;
	}

	protected boolean allWhiteSpace(String changes) {
		boolean result = true;
		for (int i = 0; i < changes.length(); i++) {
			if (!Character.isWhitespace(changes.charAt(i))) {
				result = false;
				break;
			}
		}

		return result;
	}

	protected boolean canHandleAsLetterOrDigit(String changes, int requestStart, int lengthToReplace) {
		boolean result = false;
		// Make sure we are in a non-white space area
		if ((requestStart <= (getTextEnd())) && (allLetterOrDigit(changes))) {
			result = true;
		}
		return result;
	}

	protected boolean canHandleAsWhiteSpace(String changes, int requestStart, int lengthToReplace) {
		boolean result = false;
		// if we are in the "white space" area of a region, then
		// we don't want to handle, a reparse is needed.
		// the white space region is consider anywhere that would
		// leave whitespace between this character and the text part.
		// and of course, we can insert whitespace in whitespace region
		//
		// if there is no whitespace in this region, no need to look further
		if (getEnd() > getTextEnd()) {
			// no need to add one to end of text, as we used to, since we
			// change definition of length to equate to offset plus one.
			if (requestStart > getTextEnd()) {
				// ok, we are in the whitespace region, so we can't handle,
				// unless
				// we are just inserting whitespace.
				if (allWhiteSpace(changes)) {
					result = true;
				} else {
					result = false;
				}

			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.core.ITextRegionCore#contains(int)
	 */
	public boolean contains(int position) {

		return fStart <= position && position < fStart + fLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.core.ITextRegionCore#equatePositions(com.ibm.sed.structured.text.ITextRegion)
	 */
	public void equatePositions(ITextRegion region) {
		fStart = region.getStart();
		fLength = region.getLength();
		fTextLength = region.getTextLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#getEnd()
	 */
	public int getEnd() {
		return fStart + fLength;
	}

	public int getLength() {
		return fLength;
	}

	public int getStart() {
		return fStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.structured.text.ITextRegion#getTextEnd()
	 */
	public int getTextEnd() {
		return fStart + fTextLength;
	}

	public int getTextLength() {
		return fTextLength;
	}

	public String getType() {
		return fType;
	}

	public void setLength(int i) {
		fLength = i;
	}

	public void setStart(int i) {
		fStart = i;
	}

	public void setTextLength(int i) {
		fTextLength = i;
	}

	public void setType(String string) {
		fType = string;
	}

	public String toString() {
		String className = getClass().getName();
		String shortClassName = className.substring(className.lastIndexOf(".") + 1); //$NON-NLS-1$
		// ==> // String resultText = null;
		String result = shortClassName + "--> " + getType() + ": " + getStart() + "-" + getTextEnd() + (getTextEnd() != getEnd() ? ("/" + getEnd()) : ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		// NOTE: if the document held by any region has been updated and the
		// region offsets have not
		// yet been updated, the output from this method invalid.
		//return com.ibm.sed.util.StringUtils.escape("('"+(getFirstRegion()
		// == null || document == null? "" :
		// getText(getFirstRegion()))+"'"+getStart()+" -
		// "+getEnd()+"'"+(getClose() == null || document == null ||
		// getRegions().size()<2 ? "" : getText(getClose()))+"')
		// "+getRegions());
		return result;
	}

	public StructuredDocumentEvent updateRegion(Object requester, IStructuredDocumentRegion parent, String changes, int requestStart, int lengthToReplace) {
		// the four types we used to handle here, have all been moved to
		// specific region classes.
		// XML_TAG_ATTRIBUTE_VALUE
		// XML_TAG_ATTRIBUTE_NAME
		// XML_CONTENT
		// XML_CDATA_TEXT
		return null;
	}

}
