/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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


// external node contains code to help set and get public ids
public class Entity extends ExternalNode {

	private static String setExternalEntity = DTDCoreMessages._UI_LABEL_ENTITY_SET_EXT_ENTITY; //$NON-NLS-1$
	private static String setGeneralEntity = DTDCoreMessages._UI_LABEL_ENTITY_SET_GENERAL_ENTITY; //$NON-NLS-1$
	private static String setInternalEntity = DTDCoreMessages._UI_LABEL_ENTITY_SET_INT_ENTITY; //$NON-NLS-1$

	private static String setParameterEntity = DTDCoreMessages._UI_LABEL_ENTITY_SET_PARM_ENTITY; //$NON-NLS-1$

	public Entity(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode, DTDRegionTypes.ENTITY_TAG);
	}

	public String getImagePath() {
		return DTDResource.ENTITYICON;
	}


	/**
	 * Get the value of notationName.
	 * 
	 * @return value of notationName.
	 */
	public String getNotationName() {
		ITextRegion ndataRegion = getNextRegion(iterator(), DTDRegionTypes.NDATA_VALUE);
		if (ndataRegion != null) {
			return getStructuredDTDDocumentRegion().getText(ndataRegion);
		}
		return ""; //$NON-NLS-1$
	}

	public ITextRegion getPercentRegion() {
		return getNextRegion(iterator(), DTDRegionTypes.PERCENT);
	}

	/**
	 * Get the value of value.
	 * 
	 * @return value of value.
	 */
	public String getValue() {
		if (!isExternalEntity()) {
			ITextRegion valueRegion = getNextQuotedLiteral(iterator());
			if (valueRegion != null) {
				return getValueFromQuotedRegion(valueRegion);
			}
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Get the value of externalEntity.
	 * 
	 * @return value of externalEntity.
	 */
	public boolean isExternalEntity() {
		return getPublicKeywordRegion(iterator()) != null || getSystemKeywordRegion(iterator()) != null;
	}

	/**
	 * Get the value of isParameterEntity.
	 * 
	 * @return value of isParameterEntity.
	 */
	public boolean isParameterEntity() {
		return getPercentRegion() != null;
	}

	private void removeNData(Object requestor) {
		ITextRegion ndataRegion = null;

		// see if we have an NDATA keyword
		ndataRegion = getNextRegion(iterator(), DTDRegionTypes.NDATA_KEYWORD);
		int startOffset = 0, endOffset = 0;
		if (ndataRegion != null) {
			startOffset = getStructuredDTDDocumentRegion().getStartOffset(ndataRegion);
			endOffset = getStructuredDTDDocumentRegion().getEndOffset(ndataRegion);
		}
		ITextRegion value = getNextRegion(iterator(), DTDRegionTypes.NDATA_VALUE);
		if (value != null) {
			if (startOffset == 0) {
				startOffset = getStructuredDTDDocumentRegion().getStartOffset(value);
			}
			endOffset = getStructuredDTDDocumentRegion().getEndOffset(value);
		}
		replaceText(requestor, startOffset, endOffset - startOffset, ""); //$NON-NLS-1$
	}

	/**
	 * Set the value of externalEntity.
	 * 
	 * @param v
	 *            Value to assign to externalEntity.
	 */
	public void setExternalEntity(boolean isExternalEntity) {
		if (isExternalEntity() != isExternalEntity) {
			// externalEntity = v;
			beginRecording(this, isExternalEntity ? setExternalEntity : setInternalEntity);
			if (isExternalEntity) {
				// we need to get rid of the value literal
				ITextRegion quote = getNextRegion(iterator(), DTDRegionTypes.SINGLEQUOTED_LITERAL);
				if (quote == null) {
					quote = getNextRegion(iterator(), DTDRegionTypes.DOUBLEQUOTED_LITERAL);
				}
				if (quote != null) {
					replaceText(this, getStructuredDTDDocumentRegion().getStartOffset(quote), quote.getLength(), ""); //$NON-NLS-1$
				}
				setSystemID(""); //$NON-NLS-1$
			}
			else {
				// we need to get rid of text between end of name region and
				// the last double quoted literal
				RegionIterator iter = iterator();
				ITextRegion keyword = getSystemKeywordRegion(iter);
				int startOffset = 0;
				int length = 0;
				if (keyword == null) {
					// reset the iterator
					iter = iterator();
					keyword = getPublicKeywordRegion(iter);
				}
				if (keyword != null) {
					startOffset = getStructuredDTDDocumentRegion().getStartOffset(keyword);
					// start with a length just equal to the keyword for now
					length = keyword.getLength();
				}
				else {
					// reset the iterator since we didn't find the keyword
					iter = iterator();
					// just go from after the name
					startOffset = getStructuredDTDDocumentRegion().getEndOffset(getNameRegion());
				}

				// now that we have the start, look for the end
				ITextRegion lastRegion = null;

				if (lastRegion == null) {
					// then look for last quoted literal
					while (iter.hasNext()) {
						ITextRegion literal = getNextQuotedLiteral(iter);
						if (literal != null) {
							lastRegion = literal;
						}
					}
				}

				if (lastRegion != null) {
					length = getStructuredDTDDocumentRegion().getEndOffset(lastRegion) - startOffset;
				}
				replaceText(this, startOffset, length, "\"\""); //$NON-NLS-1$
				removeNData(this);
			}
			endRecording(this);
		}
	}

	public void setNotationName(Object requestor, String newNotation) {
		if (!getNotationName().equals(newNotation)) {
			if (!newNotation.equals("")) { //$NON-NLS-1$
				// 
				ITextRegion ndataRegion = getNextRegion(iterator(), DTDRegionTypes.NDATA_VALUE);
				if (ndataRegion != null) {
					replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(ndataRegion), ndataRegion.getLength(), newNotation);
				}
				else {
					// time to create one
					int startOffset = 0;
					String string = ""; //$NON-NLS-1$
					RegionIterator iter = iterator();
					ITextRegion ndataKeyword = getNextRegion(iter, DTDRegionTypes.NDATA_KEYWORD);
					if (ndataKeyword == null) {
						// we'll need to create one after the last quoted
						// literal
						// Reset iterator
						string += " NDATA "; //$NON-NLS-1$
						iter = iterator();
						ITextRegion lastQuotedLiteral = null;
						while (iter.hasNext()) {
							ITextRegion literal = getNextQuotedLiteral(iter);
							if (literal != null) {
								lastQuotedLiteral = literal;
							}
						}
						if (lastQuotedLiteral != null) {
							startOffset = getStructuredDTDDocumentRegion().getEndOffset(lastQuotedLiteral);
						}
						else {
							// created after the system or public keyword
							ITextRegion keyword = getPublicKeywordRegion(iterator());
							if (keyword == null) {
								keyword = getSystemKeywordRegion(iterator());
							}
							// we shouldn't be null here since we check if we
							// were external already
							startOffset = getStructuredDTDDocumentRegion().getEndOffset(keyword);
						}

					}
					else {
						startOffset = getStructuredDTDDocumentRegion().getEndOffset(ndataKeyword);
					}
					replaceText(requestor, startOffset, 0, string + newNotation);
				}
			}
			else {
				// need to remove the ndata stuff
				removeNData(requestor);
			}
		}
	}

	/**
	 * Set the value of notationName.
	 * 
	 * @param newNotation
	 *            Value to assign to notationName.
	 */
	public void setNotationName(String newNotation) {
		beginRecording(this, "NDATA " + DTDCoreMessages._UI_LABEL_ENTITY_NDATA_CHANGE); //$NON-NLS-1$ //$NON-NLS-2$
		setNotationName(this, newNotation);
		endRecording(this);
	}

	/**
	 * Set the value of isParameterEntity.
	 * 
	 * @param v
	 *            Value to assign to isParameterEntity.
	 */
	public void setParameterEntity(boolean v) {
		if (isParameterEntity() != v) {
			beginRecording(this, v ? setParameterEntity : setGeneralEntity);
			if (v) {
				RegionIterator iter = iterator();
				ITextRegion startTag = getNextRegion(iter, DTDRegionTypes.ENTITY_TAG);
				int startOffset = 0, length = 0;

				if (iter.hasNext()) {
					ITextRegion region = iter.next();
					startOffset = getStructuredDTDDocumentRegion().getStartOffset(region);
					if (region.getType() == DTDRegionTypes.WHITESPACE && region.getLength() > 1) {
						length = 1;
					}
				}
				else {
					startOffset = getStructuredDTDDocumentRegion().getEndOffset(startTag);
				}
				replaceText(this, startOffset, length, " %"); //$NON-NLS-1$
				// now get rid of any NData since it is only allowed if the
				// entity is a general entity and not a parameter entity
				removeNData(this);
			}
			else {
				// get rid of percent region
				ITextRegion percentRegion = getPercentRegion();
				replaceText(this, getStructuredDTDDocumentRegion().getStartOffset(percentRegion), percentRegion.getLength(), ""); //$NON-NLS-1$
			}

			endRecording(this);
		}
	}

	/**
	 * Set the value of value.
	 * 
	 * @param v
	 *            Value to assign to value.
	 */
	public void setValue(Object requestor, String v) {
		if (!isExternalEntity()) {
			if (!getValue().equals(v)) {
				// then it makes sense to change the value
				ITextRegion valueRegion = getNextQuotedLiteral(iterator());
				String quoteChar = v.indexOf("\"") == -1 ? "\"" : "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (valueRegion != null) {
					replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(valueRegion), valueRegion.getLength(), quoteChar + v + quoteChar);
				}
				else {
					int startOffset = 0;
					RegionIterator iter = iterator();
					ITextRegion region = getNextRegion(iter, DTDRegionTypes.NAME);
					if (region == null) {
						// create it after the percent if there is one
						region = getPercentRegion();
					}
					if (region == null) {
						// if still null, then create it after the element tag
						region = getStartTag(iterator());
					}

					if (region != null) {
						startOffset = getStructuredDTDDocumentRegion().getEndOffset(region);
						replaceText(requestor, startOffset, 0, quoteChar + v + quoteChar);
					}
				}
			}
		}
	}

	/**
	 * Set the value of value.
	 * 
	 * @param v
	 *            Value to assign to value.
	 */
	public void setValue(String v) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ENTITY_VALUE_CHG); //$NON-NLS-1$
		setValue(this, v);
		endRecording(this);
	}

}
