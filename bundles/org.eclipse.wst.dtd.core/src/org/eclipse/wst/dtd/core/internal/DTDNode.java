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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.TextRegionListImpl;
import org.eclipse.wst.xml.core.internal.document.NodeContainer;
import org.w3c.dom.Node;


public abstract class DTDNode extends NodeContainer implements IndexedRegion {

	// these are characteroffsets
	protected DTDFile dtdFile;

	// flat node that contains this node
	protected IStructuredDocumentRegion flatNode;

	protected TextRegionListImpl regions = new TextRegionListImpl();

	protected TextRegionListImpl whitespace = new TextRegionListImpl();

	public DTDNode(DTDFile dtdFile, IStructuredDocumentRegion flatNode) {
		this.dtdFile = dtdFile;
		this.flatNode = flatNode;
	}

	public void addRegion(ITextRegion region) {
		/*
		 * if (startRegion == null) { startRegion = region; } endRegion =
		 * region;
		 */
		regions.add(region);
	}

	public void addWhitespaceRegion(ITextRegion region) {
		whitespace.add(region);
	}

	public void beginRecording(Object requestor, String label) {
		getDTDFile().getDTDModel().beginRecording(requestor, label);
	}

	public Node cloneNode(boolean deepest) {
		return null;
	}

	public boolean contains(int testPosition) {
		return containsRange(testPosition, testPosition);
	}

	public boolean containsRange(int start, int end) {
		return getStartOffset() <= start && end <= getEndOffset();
	}

	public void delete(DTDNode child) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_NODE_DELETE); //$NON-NLS-1$
		delete(this, child);
		endRecording(this);
	}

	public void delete(Object requestor, DTDNode child) {
		replaceText(requestor, child.getStartOffset(), child.getFullNodeLength(), ""); //$NON-NLS-1$
	}

	public void endRecording(Object requestor) {
		getDTDFile().getDTDModel().endRecording(requestor);
	}

	public Object[] getChildren() {
		return getChildrenList().toArray();
	}

	public List getChildrenList() {
		Node child = getFirstChild();
		if (child != null) {
			List children = new ArrayList();
			for (; child != null; child = child.getNextSibling()) {
				children.add(child);
			}
			return children;
		}
		else {
			return Collections.EMPTY_LIST;
		}
	}

	public DTDNode getDeepestNode(int offset) {
		if (contains(offset)) {
			// now see if a child contains this offset
			Object[] children = getChildren();
			for (int i = 0; i < children.length; i++) {
				DTDNode child = (DTDNode) children[i];
				DTDNode deepest = child.getDeepestNode(offset);
				if (deepest != null) {
					return deepest;
				}
			} // end of for ()
			return this;
		}
		return null;
	}

	public DTDNode getDeepestNode(int start, int end) {
		if (containsRange(start, end)) {
			// now see if a child contains this offset
			Object[] children = getChildren();
			for (int i = 0; i < children.length; i++) {
				DTDNode child = (DTDNode) children[i];
				DTDNode deepest = child.getDeepestNode(start, end);
				if (deepest != null) {
					return deepest;
				}
			} // end of for ()
			return this;
		}
		return null;
	}

	public DTDFile getDTDFile() {
		return dtdFile;
	}

	public int getEndOffset() {
		return getStructuredDTDDocumentRegion().getEndOffset(getEndRegion());
	}

	public ITextRegion getEndRegion() {
		return regions.get(regions.size() - 1);// endRegion;
	}

	/**
	 */
	public FactoryRegistry getFactoryRegistry() {
		DTDModelImpl model = dtdFile.getDTDModel();
		if (model != null) {
			FactoryRegistry reg = model.getFactoryRegistry();
			if (reg != null)
				return reg;
		}
		return null;
	}

	public int getFullNodeLength() {
		return getWhitespaceEndOffset() - getStartOffset();
	}

	public String getFullNodeText() {
		String text = getNodeText();
		if (whitespace.size() > 0) {
			RegionIterator iter = new RegionIterator(whitespace);
			while (iter.hasNext()) {
				ITextRegion region = iter.next();
				text += getStructuredDTDDocumentRegion().getText(region);
			}
		}
		return text;
	}

	abstract public String getImagePath();

	public String getName() {
		ITextRegion region = getNameRegion();
		if (region != null) {
			return getStructuredDTDDocumentRegion().getText(region);
		}
		return ""; //$NON-NLS-1$
	}

	public ITextRegion getNameRegion() {
		RegionIterator iter = iterator();
		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (region.getType() == DTDRegionTypes.NAME) {
				return region;
			}
		}
		return null;
	}

	// return the first token containing the specified token type
	public ITextRegion getNextRegion(RegionIterator iter, String type) {
		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (region.getType().equals(type)) {
				return region;
			}
		}
		return null;
	}

	public int getNodeLength() {
		return getEndOffset() - getStartOffset();
	}

	public String getNodeName() {
		return getName();
	}

	public String getNodeText() {
		StringBuffer sb = new StringBuffer();

		RegionIterator iter = iterator();
		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			sb.append(getStructuredDTDDocumentRegion().getText(region));
		}
		return sb.toString();
	}

	public short getNodeType() {
		return -1;
	}

	public int getStartOffset() {
		return getStructuredDTDDocumentRegion().getStartOffset(getStartRegion());
	}

	// private Region startRegion,endRegion;
	public ITextRegion getStartRegion() {
		return regions.get(0);
		// return startRegion;
	}

	/**
	 * Get the value of flatNode.
	 * 
	 * @return value of flatNode.
	 * 
	 * ISSUE:named changed not to be confused with default access protected
	 * super class method, but should re-think if this is correct technique.
	 * Perhaps getFirstRegion?
	 */
	public IStructuredDocumentRegion getStructuredDTDDocumentRegion() {
		return flatNode;
	}

	// return end offset including whitespace
	// or just the end offset if there is no whitespace
	public int getWhitespaceEndOffset() {
		if (whitespace.size() > 0) {
			ITextRegion region = whitespace.get(whitespace.size() - 1);
			return getStructuredDTDDocumentRegion().getEndOffset(region);
		}

		return getEndOffset();
	}

	public boolean hasTrailingWhitespace() {
		return whitespace.size() > 0;
	}

	public RegionIterator iterator() {
		// System.out.println("create region iter " + this.getClass() + " with
		// start , end = " + getStartOffset() + ", " +getEndOffset());
		return new RegionIterator(regions);
	}

	public void replaceText(Object requestor, int start, int length, String newText) {
		getDTDFile().getStructuredDocument().replaceText(requestor, start, length, newText);
	}

	public void resolveRegions() {
	}

	public void setName(Object requestor, String name) {
		if (!getName().equals(name)) {
			ITextRegion nameRegion = getNameRegion();
			if (nameRegion != null) {
				// nameToken.updateText(name);
				getDTDFile().getDTDModel().getReferenceUpdater().nameAboutToChange(requestor, this, name);
				replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(nameRegion), nameRegion.getLength(), name);
			}
		}
	}

	public void setName(String name) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_NODE_NAME_CHG); //$NON-NLS-1$
		setName(this, name);
		endRecording(this);
	}

	/**
	 * Set the value of flatNode.
	 * 
	 * @param v
	 *            Value to assign to flatNode. ISSUE:named changed not to be
	 *            confused with default access protected super class method,
	 *            but should re-think if this is correct technique
	 */
	void setStructuredDTDDocumentRegion(IStructuredDocumentRegion v) {
		this.flatNode = v;
	}

	// skips past next name token in the iterator
	protected void skipPastName(RegionIterator iter) {
		while (iter.hasNext()) {
			ITextRegion currentRegion = iter.next();
			if (currentRegion.getType() == DTDRegionTypes.NAME) {
				break;
			}
		}
	}

	public boolean supports(java.lang.String feature, java.lang.String version) {
		return false;
	}

}
