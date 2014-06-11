/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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

/**
 * currently public but may be made default access protected in future.
 */
public abstract class CSSStructuredDocumentRegionContainer extends CSSNodeImpl {

	private IStructuredDocumentRegion firstStructuredDocumentRegion = null;
	private IStructuredDocumentRegion lastStructuredDocumentRegion = null;

	/**
	 * CSSContainer constructor comment.
	 * currently public but may be made default access protected in future.
	 */
	public CSSStructuredDocumentRegionContainer() {
		super();
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	public CSSStructuredDocumentRegionContainer(CSSStructuredDocumentRegionContainer that) {
		super(that);
	}

	/**
	 * @return java.lang.String
	 */
	public String getCssText() {
		if (getFirstStructuredDocumentRegion() == null)
			return generateSource();

		StringBuffer str = new StringBuffer(getFirstStructuredDocumentRegion().getText());
		IStructuredDocumentRegion node = getFirstStructuredDocumentRegion();

		while (node != getLastStructuredDocumentRegion()) {
			node = node.getNext();
			str.append(node.getText());
		}

		return str.toString();
	}

	/**
	 * @return int
	 */
	public int getEndOffset() {
		IStructuredDocumentRegion flatNode = getLastStructuredDocumentRegion();
		if (flatNode != null)
			return flatNode.getEnd();
		return -1;
	}

	IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		return firstStructuredDocumentRegion;
	}

	IStructuredDocumentRegion getStructuredDocumentRegion(int index) {
		IStructuredDocumentRegion node = firstStructuredDocumentRegion;
		for (int i = index; i > 0; i--) {
			if (node == null)
				return null;
			node = node.getNext();
		}
		return node;
	}

	/**
	 * @return int
	 */
	int getStructuredDocumentRegionCount() {
		if (firstStructuredDocumentRegion == null)
			return 0;

		IStructuredDocumentRegion node = firstStructuredDocumentRegion;
		int i = 0;
		for (; node != null; i++)
			node = node.getNext();
		return i;
	}

	IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		return lastStructuredDocumentRegion;
	}

	/**
	 * @return int
	 */
	public int getStartOffset() {
		IStructuredDocumentRegion flatNode = getFirstStructuredDocumentRegion();
		if (flatNode != null)
			return flatNode.getStart();
		return -1;
	}

	boolean includeRangeStructuredDocumentRegion(IStructuredDocumentRegion first, IStructuredDocumentRegion last) {
		boolean bModified = false;

		// validate range of parameters
		if (first != null && last != null) {
			if (first.getStart() > last.getStart()) {
				IStructuredDocumentRegion node = first;
				first = last;
				last = node;
			}
		}

		// validate mine
		boolean b = validateRange();
		bModified = b || bModified;

		if (first != null) {
			if (getFirstStructuredDocumentRegion() == null || getFirstStructuredDocumentRegion().getStart() > first.getStart()) {
				setFirstStructuredDocumentRegion(first);
				bModified = true;
			}
		}

		if (last != null) {
			if (getLastStructuredDocumentRegion() == null || getLastStructuredDocumentRegion().getStart() < last.getStart()) {
				setLastStructuredDocumentRegion(last);
				bModified = true;
			}
		}

		// re-validate
		if (bModified)
			validateRange();

		return bModified;
	}

	/**
	 * 
	 */
	public boolean propagateRangeStructuredDocumentRegion() {
		boolean bModified = false;

		CSSStructuredDocumentRegionContainer parent = (CSSStructuredDocumentRegionContainer) getParentNode();
		if (parent == null)
			return bModified;

		boolean b = parent.includeRangeStructuredDocumentRegion(getFirstStructuredDocumentRegion(), getLastStructuredDocumentRegion());
		bModified = b || bModified;

		if (b)
			parent.propagateRangeStructuredDocumentRegion();
		// else need not update range of ancestors

		return bModified;
	}

	/**
	 * @param cssText
	 *            java.lang.String
	 */
	public void setCssText(String cssText) {
		if (firstStructuredDocumentRegion != null) {
			getOwnerDocument().getModel().getStructuredDocument().replaceText(this, getStartOffset(), getEndOffset() - getStartOffset(), cssText);
		}
		else
			super.setCssText(cssText);
	}

	IStructuredDocumentRegion setFirstStructuredDocumentRegion(IStructuredDocumentRegion node) {
		firstStructuredDocumentRegion = node;
		return node;
	}

	IStructuredDocumentRegion setLastStructuredDocumentRegion(IStructuredDocumentRegion node) {
		lastStructuredDocumentRegion = node;
		return node;
	}

	public void setRangeStructuredDocumentRegion(IStructuredDocumentRegion firstNode, IStructuredDocumentRegion lastNode) {
		if (firstNode != null)
			setFirstStructuredDocumentRegion(firstNode);
		if (lastNode != null)
			setLastStructuredDocumentRegion(lastNode);

		if (firstNode == null && lastNode == null) {
			setFirstStructuredDocumentRegion(null);
			setLastStructuredDocumentRegion(null);
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

		if (firstStructuredDocumentRegion != null || lastStructuredDocumentRegion != null) {
			if (this.firstStructuredDocumentRegion == null) {
				this.firstStructuredDocumentRegion = this.lastStructuredDocumentRegion;
				bModified = true;
			}
			else if (this.lastStructuredDocumentRegion == null) {
				this.lastStructuredDocumentRegion = this.firstStructuredDocumentRegion;
				bModified = true;
			}
			else if (this.firstStructuredDocumentRegion.getStart() > this.lastStructuredDocumentRegion.getStart()) {
				// need to swap first for last
				IStructuredDocumentRegion node = firstStructuredDocumentRegion;
				firstStructuredDocumentRegion = lastStructuredDocumentRegion;
				lastStructuredDocumentRegion = node;
				bModified = true;
			}
		}

		return bModified;
	}
}
