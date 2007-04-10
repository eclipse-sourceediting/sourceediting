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



import java.util.LinkedList;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.util.AbstractCssTraverser;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;


/**
 * 
 */
class CSSModelUpdateContext {


	class CSSNodeUpdateTraverser extends AbstractCssTraverser {
		public CSSNodeUpdateTraverser() {
			super();
			setTraverseImported(false);
			setTraverseImportFirst(false);
		}

		public LinkedList getNodeList() {
			return fTravNodeList;
		}

		protected void begin(ICSSNode node) {
			fTravNodeList = new LinkedList();
		}

		protected short preNode(ICSSNode node) {
			if (node instanceof CSSStyleDeclarationImpl || node instanceof CSSAttrImpl || node instanceof MediaListImpl) {
				// skip
			}
			else {
				fTravNodeList.add(node);
			}
			if (node instanceof CSSPrimitiveContainer) {
				return TRAV_PRUNE;
			}
			else {
				return TRAV_CONT;
			}
		}

		LinkedList fTravNodeList = null;
	}

	ICSSNode fLastNode = null;
	ICSSNode fDeletionTargetParent = null;
	ICSSNode fDeletionTarget = null;
	LinkedList fNodeList = null;
	private short fUpdateMode = UPDATE_IDLE;
	static final short UPDATE_IDLE = 0;
	static final short UPDATE_INSERT_FNCONTAINER = 1;
	static final short UPDATE_INSERT_RCONTAINER = 2;
	static final short UPDATE_REMOVE_FNCONTAINER = 3;
	static final short UPDATE_REMOVE_RCONTAINER = 4;
	static final short UPDATE_CHANGE_RCONTAINER = 5;

	/**
	 * CSSNodeUpdateQueue constructor comment.
	 */
	CSSModelUpdateContext() {
		super();
	}

	/**
	 * 
	 */
	void cleanupContext() {
		fNodeList = null;
		fDeletionTarget = null;
		fDeletionTargetParent = null;
		fUpdateMode = UPDATE_IDLE;
	}

	/**
	 * 
	 */
	CounterImpl getCounter() {
		ICSSNode node = getNode();
		if (node instanceof CounterImpl) {
			return (CounterImpl) node;
		}
		else {
			CSSUtil.debugOut("CounterImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSCharsetRuleImpl getCSSCharsetRule() {
		ICSSNode node = getNode();
		if (node instanceof CSSCharsetRuleImpl) {
			return (CSSCharsetRuleImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSCharsetRuleImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSFontFaceRuleImpl getCSSFontFaceRule() {
		ICSSNode node = getNode();
		if (node instanceof CSSFontFaceRuleImpl) {
			return (CSSFontFaceRuleImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSFontFaceRuleImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSImportRuleImpl getCSSImportRule() {
		ICSSNode node = getNode();
		if (node instanceof CSSImportRuleImpl) {
			return (CSSImportRuleImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSImportRuleImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSMediaRuleImpl getCSSMediaRule() {
		ICSSNode node = getNode();
		if (node instanceof CSSMediaRuleImpl) {
			return (CSSMediaRuleImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSMediaRuleImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSPageRuleImpl getCSSPageRule() {
		ICSSNode node = getNode();
		if (node instanceof CSSPageRuleImpl) {
			return (CSSPageRuleImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSPageRuleImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSPrimitiveValueImpl getCSSPrimitiveValue(short type) {
		ICSSNode node = getNode();
		if (node instanceof CSSPrimitiveValueImpl) {
			short nodeType = ((CSSPrimitiveValueImpl) node).getPrimitiveType();
			if (nodeType == type || ((nodeType == CSSPrimitiveValue.CSS_NUMBER || nodeType == ICSSPrimitiveValue.CSS_INTEGER) && (type == CSSPrimitiveValue.CSS_NUMBER || type == ICSSPrimitiveValue.CSS_INTEGER))) {
				return (CSSPrimitiveValueImpl) node;
			}
		}

		if (node instanceof CSSPrimitiveValueImpl) {
			CSSPrimitiveValueImpl value = (CSSPrimitiveValueImpl) node;
			CSSUtil.debugOut("CSSPrimitiveValueImpl [" + type + //$NON-NLS-1$
						"] is expected, but type is [" + //$NON-NLS-1$
						value.getPrimitiveType() + "]: \"" + value.generateSource() + "\"");//$NON-NLS-2$//$NON-NLS-1$
		}
		else {
			CSSUtil.debugOut("CSSPrimitiveValueImpl(" + type + //$NON-NLS-1$
						") is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
		}

		ungetNode();
		throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
	}

	/**
	 * 
	 */
	CSSPrimitiveValueImpl getCSSPrimitiveValueAny() {
		ICSSNode node = getNode();
		if (node instanceof CSSPrimitiveValueImpl) {
			return (CSSPrimitiveValueImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSPrimitiveValueImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSStyleDeclItemImpl getCSSStyleDeclItem(String propertyName) {
		ICSSNode node = getNode();
		if (node instanceof CSSStyleDeclItemImpl && ((CSSStyleDeclItemImpl) node).getPropertyName().equalsIgnoreCase(propertyName)) {
			return (CSSStyleDeclItemImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSStyleDeclItemImpl(" + propertyName + //$NON-NLS-1$
						") is expected, but " + CSSUtil.getClassString(node));//$NON-NLS-1$
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	CSSStyleRuleImpl getCSSStyleRule() {
		ICSSNode node = getNode();
		if (node instanceof CSSStyleRuleImpl) {
			return (CSSStyleRuleImpl) node;
		}
		else {
			CSSUtil.debugOut("CSSStyleRuleImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 */
	ICSSNode getDeletionTarget() {
		return fDeletionTarget;
	}

	/**
	 * @return org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 */
	ICSSNode getDeletionTargetParent() {
		return fDeletionTargetParent;
	}

	/**
	 * 
	 */
	private ICSSNode getNode() {
		ICSSNode node = null;
		if (fNodeList != null && 0 < fNodeList.size()) {
			node = (ICSSNode) fNodeList.removeFirst();
		}
		fLastNode = node;
		return node;
	}

	/**
	 * 
	 */
	int getNodeCount() {
		if (fNodeList != null) {
			return fNodeList.size();
		}
		else {
			return -1;
		}
	}

	/**
	 * 
	 */
	// ICSSNode getParentNode() {
	// return fParentNode;
	// }
	/**
	 * 
	 */
	RectImpl getRect() {
		ICSSNode node = getNode();
		if (node instanceof RectImpl) {
			return (RectImpl) node;
		}
		else {
			CSSUtil.debugOut("RectImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	RGBColorImpl getRGBColor() {
		ICSSNode node = getNode();
		if (node instanceof RGBColorImpl) {
			return (RGBColorImpl) node;
		}
		else {
			CSSUtil.debugOut("RGBColorImpl is expected, but " + //$NON-NLS-1$
						CSSUtil.getClassString(node));
			ungetNode();
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, "");//$NON-NLS-1$
		}
	}

	/**
	 * @return short
	 */
	short getUpdateMode() {
		return fUpdateMode;
	}

	/**
	 * 
	 */
	boolean isActive() {
		return (fNodeList != null);
	}

	/**
	 * 
	 */
	void setupContext(short updateMode, ICSSNode parentNode, ICSSNode targetNode) {
		fUpdateMode = updateMode;

		ICSSNode traverseRoot;
		if (updateMode == UPDATE_REMOVE_RCONTAINER || updateMode == UPDATE_INSERT_RCONTAINER || updateMode == UPDATE_CHANGE_RCONTAINER) {
			traverseRoot = parentNode;
			while (traverseRoot instanceof CSSRegionContainer) {
				traverseRoot = traverseRoot.getParentNode();
			}
		}
		else {
			traverseRoot = targetNode;
		}

		if (updateMode == UPDATE_REMOVE_RCONTAINER || updateMode == UPDATE_INSERT_RCONTAINER || // region
																								// insert
																								// =>
																								// replace
																								// flat
																								// node
					updateMode == UPDATE_CHANGE_RCONTAINER || updateMode == UPDATE_REMOVE_FNCONTAINER) {
			fDeletionTarget = traverseRoot;
			if (fDeletionTarget == targetNode) {
				fDeletionTargetParent = parentNode;
			}
			else {
				fDeletionTargetParent = fDeletionTarget.getParentNode();
			}
		}
		else {
			fDeletionTarget = null;
			fDeletionTargetParent = null;
		}

		if (updateMode == UPDATE_INSERT_RCONTAINER || updateMode == UPDATE_INSERT_FNCONTAINER || updateMode == UPDATE_REMOVE_RCONTAINER || // region
																																			// remove
																																			// =>
																																			// re-insert
																																			// flat
																																			// node
					updateMode == UPDATE_CHANGE_RCONTAINER) {
			CSSNodeUpdateTraverser traverser = new CSSNodeUpdateTraverser();
			traverser.apply(traverseRoot);
			fNodeList = traverser.getNodeList();
		}
		else {
			fNodeList = null;
		}
	}

	/**
	 * 
	 */
	private void ungetNode() {
		if (fNodeList != null && fLastNode != null) {
			fNodeList.addFirst(fLastNode);
		}
	}
}
