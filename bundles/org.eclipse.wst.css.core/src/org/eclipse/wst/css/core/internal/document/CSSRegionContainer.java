/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;

/**
 * 
 */
abstract class CSSRegionContainer extends CSSNodeImpl {

	private ITextRegion fFirstRegion = null;
	private ITextRegion fLastRegion = null;
	private IStructuredDocumentRegion fParentRegion = null;

	/**
	 * CSSRegionContainer constructor comment.
	 */
	CSSRegionContainer() {
		super();
	}

	/**
	 * CSSRegionContainer constructor comment.
	 * 
	 * @param that
	 *            com.ibm.sed.css.treemodel.CSSRegionContainer
	 */
	CSSRegionContainer(CSSRegionContainer that) {
		super(that);
	}

	/**
	 * @return java.lang.String
	 */
	public String getCssText() {
		if (fFirstRegion == null || fLastRegion == null)
			return generateSource();

		ITextRegionList regions = fParentRegion.getRegions();
		StringBuffer source = new StringBuffer();
		boolean bIn = false;
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion current = regions.get(i);
			if (bIn) {
				source.append(fParentRegion.getText(current));
				if (current == fLastRegion)
					break;
			}
			else {
				if (current == fFirstRegion) {
					bIn = true;
					source.append(fParentRegion.getText(current));
					if (current == fLastRegion)
						break;
				}
			}
		}

		return source.toString();
	}

	/**
	 * @return int
	 */
	public int getEndOffset() {
		ITextRegion region = getLastRegion();
		if (region == null || fParentRegion == null) {
			return -1;
		}
		else {
			return fParentRegion.getEndOffset(region);
		}
		/*
		 * int offset = 0; if (lastRegion != null) offset =
		 * lastRegion.getEnd();
		 * 
		 * CSSStructuredDocumentRegionContainer item = null; ICSSNode node =
		 * getParentNode(); while (node != null) { if (node instanceof
		 * CSSStructuredDocumentRegionContainer) item =
		 * (CSSStructuredDocumentRegionContainer) node; node =
		 * node.getParentNode(); } if (item != null) { // assumption: regions
		 * are attached to FIRST node IStructuredDocumentRegion flatNode =
		 * item.getFirstStructuredDocumentRegion(); if (flatNode != null)
		 * return (flatNode.getStart() + offset); } return 0;
		 */
	}

	IStructuredDocumentRegion getDocumentRegion() {
		return fParentRegion;
	}

	/**
	 * @return com.ibm.sed.structuredDocument.ITextRegion
	 */
	ITextRegion getFirstRegion() {
		return fFirstRegion;
	}

	/**
	 * @return com.ibm.sed.structuredDocument.ITextRegion
	 */
	ITextRegion getLastRegion() {
		return fLastRegion;
	}

	/**
	 * @return com.ibm.sed.structuredDocument.ITextRegion
	 * @param index
	 *            int
	 */
	ITextRegion getRegion(int index) {
		if (getFirstRegion() == null)
			return null;
		ITextRegionList regions = fParentRegion.getRegions();

		for (int i = 0; i < regions.size(); i++) {
			if (regions.get(i) == getFirstRegion()) {
				if (i + index < regions.size()) {
					ITextRegion target = regions.get(i + index);
					if (target.getStart() <= getLastRegion().getStart())
						return target;
				}
				return null;
			}
		}
		return null;
	}

	/**
	 * @return int
	 */
	int getRegionCount() {
		validateRange();

		if (getFirstRegion() == null)
			return 0;
		if (getFirstRegion() == getLastRegion())
			return 1;
		ITextRegionList regions = fParentRegion.getRegions();

		int j = 0;
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion current = regions.get(i);
			if (j != 0 || current == getFirstRegion())
				j++;
			if (current == getLastRegion())
				break;
		}
		return j;
	}

	/**
	 * @return int
	 */
	public int getStartOffset() {
		ITextRegion region = getFirstRegion();
		if (region == null || fParentRegion == null) {
			return -1;
		}
		else {
			return fParentRegion.getStartOffset(region);
		}
		/*
		 * int offset = 0; if (firstRegion != null) offset =
		 * firstRegion.getStart();
		 * 
		 * CSSStructuredDocumentRegionContainer item = null; ICSSNode node =
		 * getParentNode(); while (node != null) { if (node instanceof
		 * CSSStructuredDocumentRegionContainer) item =
		 * (CSSStructuredDocumentRegionContainer) node; node =
		 * node.getParentNode(); } if (item != null) { // assumption: regions
		 * are attached to FIRST node IStructuredDocumentRegion flatNode =
		 * item.getFirstStructuredDocumentRegion(); if (flatNode != null)
		 * return (flatNode.getStart() + offset); } return 0;
		 */
	}

	/**
	 * @param region
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 * @deprecated
	 */
	ITextRegion setFirstRegion(ITextRegion region) {
		this.fFirstRegion = region;
		return region;
	}

	/**
	 * @return com.ibm.sed.structuredDocument.ITextRegion
	 * @param lastRegion
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 * @deprecated
	 */
	ITextRegion setLastRegion(ITextRegion lastRegion) {
		this.fLastRegion = lastRegion;
		return lastRegion;
	}

	/**
	 * @param firstRegion
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 * @param lastRegion
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 */
	void setRangeRegion(IStructuredDocumentRegion parentRegion, ITextRegion firstRegion, ITextRegion lastRegion) {
		this.fParentRegion = parentRegion;
		this.fFirstRegion = firstRegion;
		this.fLastRegion = lastRegion;

		if (firstRegion == null && lastRegion == null) {
			setFirstRegion(null);
			setLastRegion(null);
		}
		else { // range validation
			validateRange();
		}
	}

	/**
	 * @return boolean
	 */
	private boolean validateRange() {
		boolean bModified = false;

		if (this.fFirstRegion != null || this.fLastRegion != null) {
			if (this.fFirstRegion == null) {
				this.fFirstRegion = this.fLastRegion;
				bModified = true;
			}
			else if (this.fLastRegion == null) {
				this.fLastRegion = this.fFirstRegion;
				bModified = true;
			}
			else if (this.fFirstRegion.getStart() > this.fLastRegion.getStart()) {
				// need to swap first for last
				ITextRegion reg = fFirstRegion;
				fFirstRegion = fLastRegion;
				fLastRegion = reg;
				bModified = true;
			}
		}
		return bModified;
	}
}