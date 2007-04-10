/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.MediaList;


/**
 * 
 */
class MediaListImpl extends CSSRegionContainer implements MediaList {

	int mediumCounter;

	/**
	 * MediaListImpl constructor comment.
	 */
	MediaListImpl() {
		super();
	}

	/**
	 * MediaListImpl constructor comment.
	 */
	MediaListImpl(MediaListImpl that) {
		super(that);
	}

	/**
	 * Adds the medium <code>newMedium</code> to the end of the list. If the
	 * <code>newMedium</code> is already used, it is first removed.
	 * 
	 * @param newMediumThe
	 *            new medium to add.
	 * @exception DOMException
	 *                INVALID_CHARACTER_ERR: If the medium contains characters
	 *                that are invalid in the underlying style language. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this list is
	 *                readonly.
	 */
	public void appendMedium(String newMedium) throws DOMException {
		if (newMedium == null)
			return;

		CSSNodeListImpl m = getMedia();
		for (int i = 0; i != m.getLength(); i++) {
			if (newMedium.equals(item(i)))
				return;
		}

		setAttribute("medium" + Integer.toString(mediumCounter++), newMedium);//$NON-NLS-1$
	}

	public ICSSNode cloneNode(boolean deep) {
		MediaListImpl cloned = new MediaListImpl(this);
		return cloned;
	}

	/**
	 * Deletes the medium indicated by <code>oldMedium</code> from the list.
	 * 
	 * @param oldMediumThe
	 *            medium to delete in the media list.
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this list is
	 *                readonly. <br>
	 *                NOT_FOUND_ERR: Raised if <code>oldMedium</code> is not
	 *                in the list.
	 */
	public void deleteMedium(String oldMedium) throws DOMException {
		for (int i = 0; i != getLength(); i++) {
			if (oldMedium.equals(item(i))) {
				removeAttributeNode((CSSAttrImpl) fAttrs.item(i));
			}
		}
	}

	/**
	 * The number of media in the list. The range of valid media is
	 * <code>0</code> to <code>length-1</code> inclusive.
	 */
	public int getLength() {
		return getMedia().getLength();
	}

	/**
	 * @return CSSNodeListImpl
	 */
	CSSNodeListImpl getMedia() {
		if (fAttrs == null)
			fAttrs = new CSSNamedNodeMapImpl();
		return fAttrs;
	}

	/**
	 * The parsable textual representation of the media list. This is a
	 * comma-separated list of media.
	 * 
	 * @exception DOMException
	 *                SYNTAX_ERR: Raised if the specified string value has a
	 *                syntax error and is unparsable. <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this media list
	 *                is readonly.
	 */
	public String getMediaText() {
		return getCssText();
	}

	/**
	 * Insert the method's description here. Creation date: (2001/01/17
	 * 18:50:29)
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return MEDIALIST_NODE;
	}

	/**
	 * Returns the <code>index</code> th in the list. If <code>index</code>
	 * is greater than or equal to the number of media in the list, this
	 * returns <code>null</code>.
	 * 
	 * @param index
	 *            Index into the collection.
	 * @return The medium at the <code>index</code> th position in the
	 *         <code>MediaList</code>, or <code>null</code> if that is
	 *         not a valid index.
	 */
	public String item(int index) {
		if (index < 0 || getLength() <= index)
			return null;

		return ((CSSAttrImpl) getMedia().item(index)).getValue();
	}

	/**
	 * setMediaText method comment.
	 */
	public void setMediaText(String mediaText) throws DOMException {
		setCssText(mediaText);
	}
}
