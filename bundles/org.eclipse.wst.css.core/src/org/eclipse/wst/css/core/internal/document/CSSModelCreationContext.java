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



import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


/**
 * currently public but may be made default access protected in future.
 */
public class CSSModelCreationContext {

	private CSSNodeImpl fRootNode = null;
	private CSSNodeImpl fTargetNode = null;
	private CSSNodeImpl fNextNode = null;
	private int fReparseStart = -1;
	private int fReparseEnd = -1;

	/**
	 * CSSModelContext constructor comment.
	 */
	CSSModelCreationContext(CSSNodeImpl rootNode) {
		super();

		fRootNode = rootNode;
	}

	/**
	 * 
	 */
	void clear() {
		fNextNode = null;
		fTargetNode = null;
		resetReparseRange();
	}

	/**
	 * currently public but may be made default access protected in future.
	 */
	public CSSNodeImpl getNextNode() {
		return fNextNode;
	}

	/**
	 * 
	 */
	int getReparseEnd() {
		return fReparseEnd;
	}

	/**
	 * 
	 */
	int getReparseStart() {
		return fReparseStart;
	}

	/**
	 * 
	 */
	CSSNodeImpl getRootNode() {
		return fRootNode;
	}

	/**
	 * currently public but may be made default access protected in future.
	 */
	public CSSNodeImpl getTargetNode() {
		return fTargetNode;
	}

	/**
	 * 
	 */
	boolean isToReparse() {
		return (0 <= getReparseStart() && 0 <= getReparseEnd());
	}

	/**
	 * 
	 */
	void resetReparseRange() {
		fReparseStart = fReparseEnd = -1;
	}

	/**
	 * 
	 */
	void setLast() {
		fNextNode = null;

		if (!(fRootNode instanceof CSSStructuredDocumentRegionContainer)) {
			fTargetNode = fRootNode;
		}
		else {
			IStructuredDocumentRegion lastStructuredDocumentRegion = ((CSSStructuredDocumentRegionContainer) fRootNode).getLastStructuredDocumentRegion();
			CSSNodeImpl node = fRootNode;
			while (node != null) {
				ICSSNode lastChild = node.getLastChild();
				if (lastChild instanceof CSSStructuredDocumentRegionContainer && ((CSSStructuredDocumentRegionContainer) lastChild).getLastStructuredDocumentRegion() == lastStructuredDocumentRegion) {
					node = (CSSNodeImpl) lastChild;
				}
				else {
					break;
				}
			}
			fTargetNode = node;
		}
	}

	/**
	 * 
	 */
	void setNextNode(ICSSNode node) {
		fNextNode = (CSSNodeImpl) node;
		if (fNextNode != null) {
			fTargetNode = (CSSNodeImpl) fNextNode.getParentNode();
		}
	}

	/**
	 * 
	 */
	void setReparseEnd(int end) {
		fReparseEnd = end;
	}

	/**
	 * 
	 */
	void setReparseStart(int start) {
		fReparseStart = start;
	}

	/**
	 * 
	 */
	void setTargetNode(ICSSNode node) {
		fTargetNode = (CSSNodeImpl) node;
		if (fNextNode != null && fNextNode.getParentNode() != fTargetNode) {
			fNextNode = null;
		}
	}
}
