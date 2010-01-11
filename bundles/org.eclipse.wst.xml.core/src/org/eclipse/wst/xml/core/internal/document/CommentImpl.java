/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *     Balazs Banfai: Bug 154737 getUserData/setUserData support for Node
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154737
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;


/**
 * CommentImpl class
 */
public class CommentImpl extends CharacterDataImpl implements Comment {

	private boolean isJSPTag = false;

	/**
	 * CommentImpl constructor
	 */
	protected CommentImpl() {
		super();
	}

	/**
	 * CommentImpl constructor
	 * 
	 * @param that
	 *            CommentImpl
	 */
	protected CommentImpl(CommentImpl that) {
		super(that);

		if (that != null) {
			this.isJSPTag = that.isJSPTag;
		}
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		CommentImpl cloned = new CommentImpl(this);
		notifyUserDataHandlers(UserDataHandler.NODE_CLONED, cloned);
		return cloned;
	}

	/**
	 * getData method
	 * 
	 * @return java.lang.String
	 */
	public String getData() throws DOMException {
		char[] data = getCharacterData();
		if (data == null) {
			String sdata = getData(getStructuredDocumentRegion());
			if (sdata != null)
				return sdata;
			return NodeImpl.EMPTY_STRING;
		}
		return new String(data);
	}

	/**
	 */
	private String getData(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return null;
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return null;

		ITextRegion contentRegion = null;
		StringBuffer buffer = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_COMMENT_OPEN || regionType == DOMRegionContext.XML_COMMENT_CLOSE || isNestedCommentOpenClose(regionType)) {
				continue;
			}
			if (contentRegion == null) { // first content
				contentRegion = region;
			}
			else { // multiple contents
				if (buffer == null) {
					buffer = new StringBuffer(flatNode.getText(contentRegion));
				}
				buffer.append(flatNode.getText(region));
			}
		}

		if (buffer != null)
			return buffer.toString();
		if (contentRegion != null)
			return flatNode.getText(contentRegion);
		return null;
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return "#comment";//$NON-NLS-1$
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return COMMENT_NODE;
	}

	/**
	 */
	public boolean isClosed() {
		IStructuredDocumentRegion flatNode = getStructuredDocumentRegion();
		if (flatNode == null)
			return true; // will be generated
		String regionType = StructuredDocumentRegionUtil.getLastRegionType(flatNode);
		return (regionType == DOMRegionContext.XML_COMMENT_CLOSE || isNestedCommentClose(regionType));
	}

	/**
	 * Subclasses must override
	 * @param regionType
	 * @return
	 */
	protected boolean isNestedCommentClose(String regionType) {
		boolean result = false;
		return result; 
	}
	/**
	 * Subclasses must override
	 * @param regionType
	 * @return
	 */
	protected boolean isNestedCommentOpenClose(String regionType) {
		boolean result = false;
		return result; 
	}
																											
	public boolean isJSPTag() {
		return this.isJSPTag;
	}

	/**
	 * setJSPTag method
	 * 
	 * @param isJSPTag
	 *            boolean
	 */
	public void setJSPTag(boolean isJSPTag) {
		if (isJSPTag == this.isJSPTag)
			return;

		if (!isDataEditable()) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, DOMMessages.NO_MODIFICATION_ALLOWED_ERR);
		}

		DocumentImpl document = (DocumentImpl) getOwnerDocument();
		if (isJSPTag) {
			if (document == null || !document.isJSPType())
				return;
		}

		this.isJSPTag = isJSPTag;

		if (getContainerDocument() != null) {
			// already in the tree, update IStructuredDocument
			setData(getData()); // calls notifyValueChanged();
		}
	}
}
