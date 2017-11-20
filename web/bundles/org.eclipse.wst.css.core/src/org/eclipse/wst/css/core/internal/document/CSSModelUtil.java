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

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNodeList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSRuleContainer;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * 
 */
class CSSModelUtil {

	/**
	 * 
	 */
	static boolean canContainBrace(ICSSNode node) {
		return (node != null && (node instanceof ICSSRuleContainer || node instanceof CSSRuleDeclContainer)) ? true : false;
	}

	/**
	 * @param parent
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 */
	static void cleanupContainer(ICSSNode parent) {
		if (parent == null) {
			return;
		}
		for (ICSSNode child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			cleanupContainer(child);
		}
		if (parent instanceof CSSStructuredDocumentRegionContainer) {
			((CSSStructuredDocumentRegionContainer) parent).setRangeStructuredDocumentRegion(null, null);
		}
		else if (parent instanceof CSSRegionContainer) {
			((CSSRegionContainer) parent).setRangeRegion(null, null, null);
		}
	}

	static boolean diagnoseNode(ICSSNode parent, IStructuredDocument structuredDocument) {
		// check this
		Vector errors = new Vector();
		if (parent instanceof CSSStructuredDocumentRegionContainer) {
			CSSStructuredDocumentRegionContainer node = (CSSStructuredDocumentRegionContainer) parent;
			String nodeText = CSSUtil.getClassString(node) + ": ";//$NON-NLS-1$
			IStructuredDocumentRegion flatNode = node.getFirstStructuredDocumentRegion();
			if (flatNode == null && (!(node instanceof CSSStyleDeclarationImpl || node instanceof CSSStyleSheetImpl) || node.getFirstChild() != null)) {
				errors.add(nodeText + "first flat node is null."); //$NON-NLS-1$
			}
			else if (flatNode != null) {
				IStructuredDocumentRegion modelNode = structuredDocument.getRegionAtCharacterOffset(flatNode.getStart());
				if (flatNode != modelNode) {
					errors.add(nodeText + "first flat node is not in model."); //$NON-NLS-1$
				}
			}
			flatNode = node.getLastStructuredDocumentRegion();
			if (flatNode == null && (!(node instanceof CSSStyleDeclarationImpl || node instanceof CSSStyleSheetImpl) || node.getFirstChild() != null)) {
				errors.add(nodeText + "last flat node is null."); //$NON-NLS-1$
			}
			else if (flatNode != null) {
				IStructuredDocumentRegion modelNode = structuredDocument.getRegionAtCharacterOffset(flatNode.getStart());
				if (flatNode != modelNode) {
					errors.add(nodeText + "last flat node is not in model."); //$NON-NLS-1$
				}
			}
		}
		else if (parent instanceof CSSRegionContainer) {
			CSSRegionContainer node = (CSSRegionContainer) parent;
			String nodeText = CSSUtil.getClassString(node) + ": ";//$NON-NLS-1$
			ITextRegion region = node.getFirstRegion();
			IStructuredDocumentRegion parentRegion = node.getDocumentRegion();
			if (region == null && (!(node instanceof MediaListImpl) || node.getFirstChild() != null)) {
				errors.add(nodeText + "first region is null."); //$NON-NLS-1$
			}
			else if (region != null) {
				int offset = parentRegion.getStartOffset(region);
				IStructuredDocumentRegion modelNode = structuredDocument.getRegionAtCharacterOffset(offset);
				ITextRegion modelRegion = modelNode.getRegionAtCharacterOffset(offset);
				if (region != modelRegion) {
					errors.add(nodeText + "first region is not in model."); //$NON-NLS-1$
				}
			}
			region = node.getLastRegion();
			if (region == null && (!(node instanceof MediaListImpl) || node.getFirstChild() != null)) {
				errors.add(nodeText + "last region is null."); //$NON-NLS-1$
			}
			else if (region != null) {
				int offset = parentRegion.getStartOffset(region);
				IStructuredDocumentRegion modelNode = structuredDocument.getRegionAtCharacterOffset(offset);
				ITextRegion modelRegion = modelNode.getRegionAtCharacterOffset(offset);
				if (region != modelRegion) {
					errors.add(nodeText + "last region is not in model."); //$NON-NLS-1$
				}
			}
		}

		ICSSNodeList attrs = parent.getAttributes();
		int nAttrs = attrs.getLength();
		for (int i = 0; i < nAttrs; i++) {
			ICSSAttr attr = (ICSSAttr) attrs.item(i);
			CSSRegionContainer node = (CSSRegionContainer) attr;
			String nodeText = CSSUtil.getClassString(node) + "(" + attr.getName() + "): ";//$NON-NLS-2$//$NON-NLS-1$
			ITextRegion region = node.getFirstRegion();
			IStructuredDocumentRegion parentRegion = node.getDocumentRegion();
			if (region == null && 0 < attr.getValue().length()) {
				errors.add(nodeText + "first region is null."); //$NON-NLS-1$
			}
			else if (region != null) {
				int offset = parentRegion.getStartOffset(region);
				IStructuredDocumentRegion modelNode = structuredDocument.getRegionAtCharacterOffset(offset);
				ITextRegion modelRegion = modelNode.getRegionAtCharacterOffset(offset);
				if (region != modelRegion) {
					errors.add(nodeText + "first region is not in model."); //$NON-NLS-1$
				}
			}
			region = node.getLastRegion();
			if (region == null && 0 < attr.getValue().length()) {
				errors.add(nodeText + "last region is null."); //$NON-NLS-1$
			}
			else if (region != null) {
				int offset = parentRegion.getStartOffset(region);
				IStructuredDocumentRegion modelNode = structuredDocument.getRegionAtCharacterOffset(offset);
				ITextRegion modelRegion = modelNode.getRegionAtCharacterOffset(offset);
				if (region != modelRegion) {
					errors.add(nodeText + "last region is not in model."); //$NON-NLS-1$
				}
			}
		}

		if (!errors.isEmpty()) {
			Iterator i = errors.iterator();
			while (i.hasNext()) {
				CSSUtil.debugOut((String) i.next());
			}
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * @return boolean
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSNode
	 */
	static boolean diagnoseTree(ICSSNode parent, IStructuredDocument structuredDocument) {
		if (parent == null) {
			return false;
		}
		// check children
		for (ICSSNode child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			diagnoseTree(child, structuredDocument);
		}

		diagnoseNode(parent, structuredDocument);

		return true;
	}

	/**
	 * If needed, modify last flat node
	 */
	static void expandStructuredDocumentRegionContainer(CSSStructuredDocumentRegionContainer target, IStructuredDocumentRegion flatNode) {
		if (target == null || flatNode == null) {
			return;
		}

		IStructuredDocumentRegion lastNode = target.getLastStructuredDocumentRegion();
		if (lastNode == flatNode) {
			return;
		}
		if (lastNode == null || lastNode.getStart() < flatNode.getStart()) {
			target.setLastStructuredDocumentRegion(flatNode);
		}
	}

	/*
	 * 
	 */
	static ICSSNode findBraceContainer(ICSSNode node) {
		for (ICSSNode i = node; i != null; i = i.getParentNode()) {
			if (CSSModelUtil.canContainBrace(i)) {
				return i;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	static int getDepth(ICSSNode node) {
		int depth = -1;
		while (node != null) {
			depth++;
			node = node.getParentNode();
		}
		return depth;
	}

	/**
	 * 
	 */
	static boolean isBraceClosed(ICSSNode node) {
		boolean bClosed = true;
		if (!(node instanceof CSSStructuredDocumentRegionContainer)) {
			return bClosed;
		}

		IStructuredDocumentRegion first = ((CSSStructuredDocumentRegionContainer) node).getFirstStructuredDocumentRegion();
		IStructuredDocumentRegion last = ((CSSStructuredDocumentRegionContainer) node).getLastStructuredDocumentRegion();
		if (first == null || last == null) {
			return bClosed;
		}
		if (last.getStart() < first.getStart()) {
			return bClosed;
		}

		IStructuredDocumentRegion flatNode = first;
		int nOpen = 0;
		int nClose = 0;
		do {
			String type = CSSUtil.getStructuredDocumentRegionType(flatNode);
			if (type == CSSRegionContexts.CSS_LBRACE) {
				nOpen++;
			}
			else if (type == CSSRegionContexts.CSS_RBRACE) {
				nClose++;
			}
			flatNode = flatNode.getNext();
		}
		while (flatNode != null && flatNode != last);

		if ((nOpen == 0 && nClose == 0) || nClose < nOpen) {
			bClosed = false;
		}

		return bClosed;
	}

	/**
	 * only for insertion..
	 */
	static boolean isInterruption(CSSStructuredDocumentRegionContainer target, IStructuredDocumentRegion flatNode) {
		if (target == null || flatNode == null) {
			return false;
		}
		int start = flatNode.getStart();
		IStructuredDocumentRegion firstNode = target.getFirstStructuredDocumentRegion();
		IStructuredDocumentRegion lastNode = target.getLastStructuredDocumentRegion();
		if (firstNode != null && firstNode.getStart() < start && lastNode != null && start < lastNode.getStart()) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	static boolean isParentOf(ICSSNode parent, ICSSNode child) {
		if (parent == null || child == null) {
			return false;
		}

		for (ICSSNode node = child; node != null; node = node.getParentNode()) {
			if (parent == node) {
				return true;
			}
		}

		return false;
	}
}
