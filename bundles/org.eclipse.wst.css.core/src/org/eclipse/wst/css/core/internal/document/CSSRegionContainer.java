/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

/**
 * 
 */
public abstract class CSSRegionContainer extends CSSNodeImpl {

	private ITextRegion fFirstRegion = null;
	private ITextRegion fLastRegion = null;
	private IStructuredDocumentRegion fParentRegion = null;

	/**
	 * CSSRegionContainer constructor comment.
	 */
	CSSRegionContainer() {
		super();
	}

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
				source.append(fParentRegion.getFullText(current));
				if (current == fLastRegion)
					break;
			}
			else {
				if (current == fFirstRegion) {
					bIn = true;
					source.append(fParentRegion.getFullText(current));
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
		int result = -1;
		ITextRegion region = getLastRegion();
		if (!(region == null || fParentRegion == null)) {
			result = fParentRegion.getEndOffset(region);
		}
		return result;
	}

	IStructuredDocumentRegion getDocumentRegion() {
		return fParentRegion;
	}

	public ITextRegion getFirstRegion() {
		return fFirstRegion;
	}

	ITextRegion getLastRegion() {
		return fLastRegion;
	}

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
		int result = -1;
		ITextRegion region = getFirstRegion();
		if (!(region == null || fParentRegion == null)) {
			result = fParentRegion.getStartOffset(region);
		}
		return result;
	}



	/**
	 * @deprecated
	 */
	ITextRegion setFirstRegion(ITextRegion region) {
		this.fFirstRegion = region;
		return region;
	}

	/**
	 * @deprecated
	 */
	ITextRegion setLastRegion(ITextRegion lastRegion) {
		this.fLastRegion = lastRegion;
		return lastRegion;
	}

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
