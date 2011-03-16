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

import java.util.Hashtable;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


// base class for an Element's contentmodel
public class Attribute extends DTDNode {

	public static final String CDATA = DTDCoreMessages._UI_CHARACTER_DATA_DESC; //$NON-NLS-1$
	public static final String ENTITIES = DTDCoreMessages._UI_ENTITY_NAMES_DESC; //$NON-NLS-1$
	public static final String ENTITY = DTDCoreMessages._UI_ENTITY_NAME_DESC; //$NON-NLS-1$
	public static final String ENUMERATED_NAME = DTDCoreMessages._UI_ENUM_NAME_TOKENS_DESC; //$NON-NLS-1$
	public static final String ENUMERATED_NOTATION = DTDCoreMessages._UI_ENUM_NOTATION_DESC; //$NON-NLS-1$
	public static final String FIXED = "#FIXED"; //$NON-NLS-1$
	public static final String ID = DTDCoreMessages._UI_IDENTIFIER_DESC; //$NON-NLS-1$
	public static final String IDREF = DTDCoreMessages._UI_ID_REFERENCE_DESC; //$NON-NLS-1$
	public static final String IDREFS = DTDCoreMessages._UI_ID_REFERENCES_DESC; //$NON-NLS-1$

	public static final String IMPLIED = "#IMPLIED"; //$NON-NLS-1$
	public static final String NMTOKEN = DTDCoreMessages._UI_NAME_TOKEN_DESC; //$NON-NLS-1$
	public static final String NMTOKENS = DTDCoreMessages._UI_NAME_TOKENS_DESC; //$NON-NLS-1$
	public static final String REQUIRED = "#REQUIRED"; //$NON-NLS-1$

	protected static Hashtable typeHash = new Hashtable();

	public static final String[] types = {CDATA, ID, IDREF, IDREFS, ENTITY, ENTITIES, NMTOKEN, NMTOKENS, ENUMERATED_NAME, ENUMERATED_NOTATION};

	{
		typeHash.put(DTDRegionTypes.CDATA_KEYWORD, CDATA);
		typeHash.put(DTDRegionTypes.ID_KEYWORD, ID);
		typeHash.put(DTDRegionTypes.IDREF_KEYWORD, IDREF);
		typeHash.put(DTDRegionTypes.IDREFS_KEYWORD, IDREFS);
		typeHash.put(DTDRegionTypes.ENTITY_KEYWORD, ENTITY);
		typeHash.put(DTDRegionTypes.ENTITIES_KEYWORD, ENTITIES);
		typeHash.put(DTDRegionTypes.NMTOKEN_KEYWORD, NMTOKEN);
		typeHash.put(DTDRegionTypes.NMTOKENS_KEYWORD, NMTOKENS);
		typeHash.put(DTDRegionTypes.NOTATION_KEYWORD, ENUMERATED_NOTATION);
		// this one's a special case since there is no keyword for
		// enumerated name tokens
		typeHash.put("()", ENUMERATED_NAME); //$NON-NLS-1$

		// now put the reverse in place. This gives us a 2 way lookup
		// for when we want to retrieve the value and when we want to set it
		typeHash.put(CDATA, "CDATA"); //$NON-NLS-1$
		typeHash.put(ID, "ID"); //$NON-NLS-1$
		typeHash.put(IDREF, "IDREF"); //$NON-NLS-1$
		typeHash.put(IDREFS, "IDREFS"); //$NON-NLS-1$
		typeHash.put(ENTITY, "ENTITY"); //$NON-NLS-1$
		typeHash.put(ENTITIES, "ENTITIES"); //$NON-NLS-1$
		typeHash.put(NMTOKEN, "NMTOKEN"); //$NON-NLS-1$
		typeHash.put(NMTOKENS, "NMTOKENS"); //$NON-NLS-1$
		typeHash.put(ENUMERATED_NAME, ""); //$NON-NLS-1$
		typeHash.put(ENUMERATED_NOTATION, "NOTATION"); //$NON-NLS-1$
	}

	private AttributeEnumList enumList = null;

	// public static final String IMPLIED = "IMPLIED";

	public Attribute(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode);
	}

	public String getDefaultKind() {
		ITextRegion defaultKindRegion = getDefaultKindRegion();
		if (defaultKindRegion != null) {
			return getStructuredDTDDocumentRegion().getText(defaultKindRegion);
		}

		return ""; //$NON-NLS-1$
	}

	public ITextRegion getDefaultKindRegion() {
		RegionIterator iter = iterator();
		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (region.getType() == DTDRegionTypes.IMPLIED_KEYWORD || region.getType() == DTDRegionTypes.REQUIRED_KEYWORD || region.getType() == DTDRegionTypes.FIXED_KEYWORD) {
				return region;
			}
		}
		return null;
	}

	public String getDefaultValue() {
		ITextRegion defaultValue = getNextQuotedLiteral(iterator());
		if (defaultValue != null) {
			return getValueFromQuotedRegion(defaultValue);
		}
		return ""; //$NON-NLS-1$
	}

	public AttributeEnumList getEnumList() {
		return enumList;
	}

	public String getImagePath() {
		return DTDResource.ATTRIBUTEICON;
	}

	public ITextRegion getNameRegion() {
		return getNextRegion(iterator(), DTDRegionTypes.ATTRIBUTE_NAME);
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

	protected int getOffsetAfterType() {
		ITextRegion typeRegion = getTypeRegion();

		String type = getType();
		boolean isEnumeration = type.equals(ENUMERATED_NAME) || type.equals(ENUMERATED_NOTATION);
		if (isEnumeration) {
			// now check if maybe this is an enumeration
			if (getEnumList() != null) {
				return getEnumList().getEndOffset();
			}
		}
		if (typeRegion != null) {
			return getStructuredDTDDocumentRegion().getEndOffset(typeRegion);
		}
		else {
			ITextRegion nameRegion = getNameRegion();
			return getStructuredDTDDocumentRegion().getEndOffset(nameRegion);
			// // create one
			// typeRegion =
			// findOrCreateTypeRegion((String)typeHash.get(CDATA));
		}
	}

	public String getType() {
		ITextRegion region = getTypeRegion();
		if (region != null) {
			String type = (String) typeHash.get(region.getType());
			if (type == null) {
				// just return the text of the type region since this may be
				// an entity representing the type;
				return getStructuredDTDDocumentRegion().getText(region);
			}
			return type;
		}
		else if (getEnumList() != null) {
			// enumerated name tokens don't have a type keyword. just
			// the existence of the left paren is enough
			return (String) typeHash.get("()"); //$NON-NLS-1$
		}

		return ""; //$NON-NLS-1$
	}

	public ITextRegion getTypeRegion() {
		RegionIterator iter = iterator();

		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (region.getType() == DTDRegionTypes.CDATA_KEYWORD || region.getType() == DTDRegionTypes.ID_KEYWORD || region.getType() == DTDRegionTypes.IDREF_KEYWORD || region.getType() == DTDRegionTypes.IDREFS_KEYWORD || region.getType() == DTDRegionTypes.ENTITY_KEYWORD || region.getType() == DTDRegionTypes.ENTITIES_KEYWORD || region.getType() == DTDRegionTypes.NMTOKEN_KEYWORD || region.getType() == DTDRegionTypes.NMTOKENS_KEYWORD || region.getType() == DTDRegionTypes.NOTATION_KEYWORD || region.getType() == DTDRegionTypes.PARM_ENTITY_TYPE) {
				return region;
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

	public void resolveRegions() {
		removeChildNodes();
		RegionIterator iter = iterator();

		while (iter.hasNext()) {
			ITextRegion currentRegion = iter.next();
			if (currentRegion.getType().equals(DTDRegionTypes.LEFT_PAREN)) {
				enumList = new AttributeEnumList(getDTDFile(), getStructuredDTDDocumentRegion());
			}
			if (enumList != null) {
				enumList.addRegion(currentRegion);
				if (currentRegion.getType() == DTDRegionTypes.RIGHT_PAREN) {
					return;
				}
			}
		}

	}

	public void setDefaultKind(Object requestor, String kind) {

		ITextRegion defaultKindRegion = getDefaultKindRegion();
		String oldDefaultKind = defaultKindRegion == null ? "" : getStructuredDTDDocumentRegion().getText(defaultKindRegion); //$NON-NLS-1$
		if (!kind.equals(oldDefaultKind)) {
			String newText = kind;
			int startOffset = 0;
			int length = 0;
			if (defaultKindRegion != null) {
				startOffset = getStructuredDTDDocumentRegion().getStartOffset(defaultKindRegion);
				length = getStructuredDTDDocumentRegion().getEndOffset(defaultKindRegion) - startOffset;
			}
			else {
				startOffset = getOffsetAfterType();
				newText = " " + newText; //$NON-NLS-1$
			}

			ITextRegion defaultValue = getNextQuotedLiteral(iterator());

			if (kind.equals(Attribute.FIXED) || kind.equals("")) { //$NON-NLS-1$
				if (defaultValue == null) {
					// we are changing to fixed and wehave no quoted region.
					// put in an empty value
					newText += " \"\""; //$NON-NLS-1$
				}
			}
			else {
				if (defaultValue != null) {
					length = getStructuredDTDDocumentRegion().getEndOffset(defaultValue) - startOffset;
				}
			}
			replaceText(requestor, startOffset, length, newText);
			// do something if there is no "kind" region
		}
	}

	public void setDefaultKind(String kind) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ATTR_DEFAULT_KIND); //$NON-NLS-1$
		setDefaultKind(this, kind);
		endRecording(this);
	}

	public void setDefaultValue(Object requestor, String value, boolean fixed) {
		ITextRegion defaultValue = getNextQuotedLiteral(iterator());
		String quoteChar = value.indexOf("\"") == -1 ? "\"" : "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int startOffset = 0;
		int endOffset = 0;
		String newText = ""; //$NON-NLS-1$

		String oldValue = getDefaultValue();
		boolean oldKindIsFixed = getDefaultKind().equals(Attribute.FIXED);
		if (oldValue.equals(value) && fixed == oldKindIsFixed) {
			// nothing to do
			return;
		}

		if (defaultValue != null) {
			startOffset = getStructuredDTDDocumentRegion().getStartOffset(defaultValue);
			endOffset = getStructuredDTDDocumentRegion().getEndOffset(defaultValue);
		}

		ITextRegion defaultKindRegion = getDefaultKindRegion();
		if (defaultKindRegion != null) {
			startOffset = getStructuredDTDDocumentRegion().getStartOffset(defaultKindRegion);
			endOffset = endOffset == 0 ? getStructuredDTDDocumentRegion().getEndOffset(defaultKindRegion) : endOffset;
		}
		else {
			if (startOffset == 0) {
				endOffset = startOffset = getOffsetAfterType();
				newText += " "; //$NON-NLS-1$
			}
			ITextRegion typeRegion = getTypeRegion();
			if (typeRegion == null && getEnumList() == null) {
				// tack on a default type
				// newText += "CDATA ";
			}
		}
		if (fixed) {
			newText += "#FIXED "; //$NON-NLS-1$
		}
		else {
			if (getDefaultKind().equals("") && value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
				// if not fixed and value is "" then reset the default kind to
				// implied
				newText += "#IMPLIED"; //$NON-NLS-1$
			}
		}

		if (!getType().equals("") && !value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			newText += quoteChar + value + quoteChar;
		}
		replaceText(requestor, startOffset, endOffset - startOffset, newText);
	}

	public void setDefaultValue(String value, boolean fixed) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ATTR_DEFAULT_VAL); //$NON-NLS-1$
		setDefaultValue(this, value, fixed);
		endRecording(this);
	}

	public void setType(Object requestor, String type) {
		String oldType = getType();
		if (!type.equals(oldType)) {
			boolean wasEnumeration = oldType.equals(ENUMERATED_NAME) || oldType.equals(ENUMERATED_NOTATION);
			boolean isEnumeration = type.equals(ENUMERATED_NAME) || type.equals(ENUMERATED_NOTATION);
			String newText = ""; //$NON-NLS-1$
			int startOffset = 0;
			int endOffset = 0;

			if (wasEnumeration && !isEnumeration) {
				// get rid of the old enumlist
				AttributeEnumList enumList = getEnumList();
				if (enumList != null) {
					startOffset = enumList.getStartOffset();
					endOffset = enumList.getEndOffset();
				}
			}

			ITextRegion region = getTypeRegion();
			if (region != null) {
				startOffset = getStructuredDTDDocumentRegion().getStartOffset(region);
				if (endOffset == 0) {
					endOffset = getStructuredDTDDocumentRegion().getEndOffset(region);
				}
			}
			else if (startOffset == 0) {
				ITextRegion nameRegion = getNameRegion();
				newText += " "; //$NON-NLS-1$
				endOffset = startOffset = getStructuredDTDDocumentRegion().getEndOffset(nameRegion);
			}

			String newTypeWord = (String) typeHash.get(type);
			if (newTypeWord == null) {
				// then this must be a parm entity being used in the type
				// use the type text directly
				newTypeWord = type;
			}

			newText += newTypeWord;

			if (isEnumeration && !wasEnumeration) {
				// put in a new numlist
				boolean isSpaceNeeded = !type.equals(ENUMERATED_NAME);
				newText += isSpaceNeeded ? " " : ""; //$NON-NLS-1$ //$NON-NLS-2$
				newText += "()"; //$NON-NLS-1$
			}
			replaceText(requestor, startOffset, endOffset - startOffset, newText);
			if (newTypeWord.equals("") && !type.equals(ENUMERATED_NAME)) { //$NON-NLS-1$
				// the set the defaultkind to ""
				// setDefaultKind(requestor, "");
				setDefaultValue(requestor, "", false); //$NON-NLS-1$
			}

		}
	}

	public void setType(String type) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ATTR_TYPE); //$NON-NLS-1$
		setType(this, type);
		endRecording(this);
	}
}
