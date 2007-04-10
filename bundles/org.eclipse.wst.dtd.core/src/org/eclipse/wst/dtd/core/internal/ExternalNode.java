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
package org.eclipse.wst.dtd.core.internal;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


// base class for nodes that can contain external content
public abstract class ExternalNode extends NamedTopLevelNode {

	String publicID;

	String systemID;

	public ExternalNode(DTDFile file, IStructuredDocumentRegion flatNode, String tagType) {
		super(file, flatNode, tagType);
	}

	public ITextRegion getNextQuotedLiteral(RegionIterator iter) {
		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (region.getType().equals(DTDRegionTypes.SINGLEQUOTED_LITERAL) || region.getType().equals(DTDRegionTypes.DOUBLEQUOTED_LITERAL)) {
				return region;
			}
		}
		return null;
	}

	/**
	 * Get the value of publicID.
	 * 
	 * @return value of publicID.
	 */
	public String getPublicID() {
		ITextRegion publicValue = getPublicValueRegion();
		if (publicValue != null) {
			return getValueFromQuotedRegion(publicValue);
		}
		return ""; //$NON-NLS-1$
	}

	public ITextRegion getPublicKeywordRegion(RegionIterator iter) {
		return getNextRegion(iter, DTDRegionTypes.PUBLIC_KEYWORD);
	}

	public ITextRegion getPublicValueRegion() {
		RegionIterator iter = iterator();

		ITextRegion publicKeyword = getPublicKeywordRegion(iter);

		if (publicKeyword != null && iter.hasNext()) {
			ITextRegion quotedLiteral = getNextQuotedLiteral(iter);
			if (quotedLiteral != null) {
				return quotedLiteral;
			}
		}
		return null;
	}

	/**
	 * Get the value of systemID.
	 * 
	 * @return value of systemID.
	 */
	public String getSystemID() {
		ITextRegion systemValue = getSystemValueRegion();
		if (systemValue != null) {
			return getValueFromQuotedRegion(systemValue);
		}
		return ""; //$NON-NLS-1$
	}

	public ITextRegion getSystemKeywordRegion(RegionIterator iter) {
		return getNextRegion(iter, DTDRegionTypes.SYSTEM_KEYWORD);
	}

	public ITextRegion getSystemValueRegion() {
		RegionIterator iter = iterator();

		ITextRegion systemKeyword = getSystemKeywordRegion(iter);
		if (systemKeyword != null && iter.hasNext()) {
			ITextRegion quotedLiteral = getNextQuotedLiteral(iter);
			if (quotedLiteral != null) {
				return quotedLiteral;
			}
		}
		else {
			// try and see if there is a second quoted literal after a public
			// keyword
			iter = iterator();
			ITextRegion publicKeyword = getPublicKeywordRegion(iter);

			if (publicKeyword != null && iter.hasNext()) {
				ITextRegion quotedLiteral = getNextQuotedLiteral(iter);
				if (quotedLiteral != null && iter.hasNext()) {
					// now get the second quoted literal
					quotedLiteral = getNextQuotedLiteral(iter);
					if (quotedLiteral != null) {
						// got it!
						return quotedLiteral;
					}
				}
			}
		}
		return null;
	}

	public String getValueFromQuotedRegion(ITextRegion region) {
		String type = region.getType();
		if (type.equals(DTDRegionTypes.SINGLEQUOTED_LITERAL) || type.equals(DTDRegionTypes.DOUBLEQUOTED_LITERAL)) {
			String text = getStructuredDTDDocumentRegion().getText(region);
			return text.substring(1, text.length() - 1);
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Set the value of publicID.
	 * 
	 * @param v
	 *            Value to assign to publicID.
	 */
	public void setPublicID(Object requestor, String v) {
		if (!v.equals(publicID)) {
			publicID = v;
			ITextRegion publicValue = getPublicValueRegion();
			ITextRegion publicKeyword = getPublicKeywordRegion(iterator());
			ITextRegion systemKeyword = getSystemKeywordRegion(iterator());
			ITextRegion systemValue = getSystemValueRegion();

			if (v.equals("")) { //$NON-NLS-1$
				if (publicKeyword != null) {
					// time to get rid of the public keyword and value
					// and replace it with the system one
					int startOffset = getStructuredDTDDocumentRegion().getStartOffset(publicKeyword);
					String newString = "SYSTEM"; //$NON-NLS-1$
					if (systemValue == null) {
						newString += " \"\""; //$NON-NLS-1$
					}
					replaceText(requestor, startOffset, getStructuredDTDDocumentRegion().getEndOffset(publicValue) - startOffset, newString);
				}
			}
			else {
				// here were setting a non empty value
				String quoteChar = v.indexOf("\"") == -1 ? "\"" : "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (publicValue != null) {
					replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(publicValue), publicValue.getLength(), quoteChar + publicID + quoteChar);
				}
				else {
					// time to create stuff
					if (publicKeyword != null) {
						// then just put our new value after the keyword
						replaceText(requestor, getStructuredDTDDocumentRegion().getEndOffset(publicKeyword), 0, " " + quoteChar + v + quoteChar); //$NON-NLS-1$
					}
					else {
						// we need the public keyword as well
						if (systemKeyword != null) {
							replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(systemKeyword), systemKeyword.getLength(), "PUBLIC " + quoteChar + v + quoteChar); //$NON-NLS-1$
						}
						else {
							ITextRegion nameRegion = getNameRegion();
							replaceText(requestor, getStructuredDTDDocumentRegion().getEndOffset(nameRegion), 0, " PUBLIC " + quoteChar + v + quoteChar); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}


	/**
	 * Set the value of publicID.
	 * 
	 * @param v
	 *            Value to assign to publicID.
	 */
	public void setPublicID(String v) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_EXT_NODE_PUBLIC_ID_CHG); //$NON-NLS-1$
		setPublicID(this, v);
		endRecording(this);
	}

	/**
	 * Set the value of systemID.
	 * 
	 * @param v
	 *            Value to assign to systemID.
	 */
	public void setSystemID(Object requestor, String v) {
		if (!v.equals(systemID) || (getPublicKeywordRegion(iterator()) == null && getSystemKeywordRegion(iterator()) == null)) {
			systemID = v;
			String quoteChar = v.indexOf("\"") == -1 ? "\"" : "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			ITextRegion systemValue = getSystemValueRegion();
			if (systemValue != null) {
				replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(systemValue), systemValue.getLength(), quoteChar + systemID + quoteChar);
			}
			else {
				ITextRegion systemKeyword = getSystemKeywordRegion(iterator());

				// time to create stuff
				if (systemKeyword != null) {
					// then just put our new value after the keyword
					replaceText(requestor, getStructuredDTDDocumentRegion().getEndOffset(systemKeyword), 0, " " + quoteChar + v + quoteChar); //$NON-NLS-1$
				}
				else {
					// see if we have a public keyword
					ITextRegion publicKeyword = getPublicKeywordRegion(iterator());
					if (publicKeyword == null) {
						ITextRegion nameRegion = getNameRegion();

						replaceText(requestor, getStructuredDTDDocumentRegion().getEndOffset(nameRegion), 0, " SYSTEM " + quoteChar + v + quoteChar); //$NON-NLS-1$
					}
					else {
						// put it after the public value region
						ITextRegion publicValueRegion = getPublicValueRegion();
						replaceText(requestor, getStructuredDTDDocumentRegion().getEndOffset(publicValueRegion), 0, " " + quoteChar + v + quoteChar); //$NON-NLS-1$
					}

				}
			}

		}
	}


	/**
	 * Set the value of systemID.
	 * 
	 * @param v
	 *            Value to assign to systemID.
	 */
	public void setSystemID(String v) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_EXT_NODE_SYSTEM_ID_CHG); //$NON-NLS-1$
		setSystemID(this, v);
		endRecording(this);
	}
}
