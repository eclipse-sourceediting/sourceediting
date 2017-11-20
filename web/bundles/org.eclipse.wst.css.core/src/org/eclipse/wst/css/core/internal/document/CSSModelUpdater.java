/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import org.eclipse.wst.css.core.internal.formatter.AttrChangeContext;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceGenerator;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument;


/**
 * 
 */
class CSSModelUpdater {

	private CSSModelImpl fModel = null;
	private CSSModelParser fParser = null;

	/**
	 * CSSModelUpdater constructor comment.
	 */
	CSSModelUpdater() {
		super();
	}

	/**
	 * 
	 */
	CSSModelUpdater(CSSModelImpl model) {
		super();
		fModel = model;
	}

	/**
	 * @param parentNode
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param node
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 */
	private void attrInserted(CSSNodeImpl parentNode, CSSAttrImpl attr) {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(parentNode);

		if (formatter == null) {
			CSSUtil.debugOut("Cannot get format adapter : " + parentNode.getClass().toString());//$NON-NLS-1$
			return;
		}

		short updateMode = CSSModelUpdateContext.UPDATE_INSERT_RCONTAINER;
		fParser.setupUpdateContext(updateMode, parentNode, attr);

		// get formatted info
		AttrChangeContext region = new AttrChangeContext();
		String text = new String(formatter.formatAttrChanged(parentNode, attr, true, region));

		// set text
		insertText(region.start, region.end - region.start, text);

		fParser.cleanupUpdateContext();
	}

	/**
	 * @param parentNode
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param node
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 */
	private void attrRemoved(CSSNodeImpl parentNode, CSSAttrImpl attr) {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(parentNode);

		if (formatter == null) {
			CSSUtil.debugOut("Cannot get format adapter : " + parentNode.getClass().toString());//$NON-NLS-1$
			return;
		}

		short updateMode = CSSModelUpdateContext.UPDATE_REMOVE_RCONTAINER;
		fParser.setupUpdateContext(updateMode, parentNode, attr);

		// get formatted info
		AttrChangeContext region = new AttrChangeContext();
		String text = new String(formatter.formatAttrChanged(parentNode, attr, false, region));

		// set text
		insertText(region.start, region.end - region.start, text);

		fParser.cleanupUpdateContext();
	}

	/**
	 * 
	 */
	void attrReplaced(CSSNodeImpl parentNode, CSSNodeImpl newAttr, CSSNodeImpl oldAttr) {
		if (parentNode == null) {
			return;
		}

		if (oldAttr != null) {
			attrRemoved(parentNode, (CSSAttrImpl) oldAttr);
		}

		if (newAttr != null) {
			attrInserted(parentNode, (CSSAttrImpl) newAttr);
		}
	}

	/**
	 * 
	 */
	private void childInserted(CSSNodeImpl parentNode, CSSNodeImpl node) {
		short updateMode = CSSModelUpdateContext.UPDATE_IDLE;

		if (node instanceof CSSStructuredDocumentRegionContainer) {
			updateMode = CSSModelUpdateContext.UPDATE_INSERT_FNCONTAINER;
		}
		else if (node instanceof CSSRegionContainer) {
			updateMode = CSSModelUpdateContext.UPDATE_INSERT_RCONTAINER;
		}
		else {
			CSSUtil.debugOut("What's this node? : " + node.getClass().toString());//$NON-NLS-1$
			return;
		}

		fParser.setupUpdateContext(updateMode, parentNode, node);

		defaultInserted(parentNode, node);

		fParser.cleanupUpdateContext();
	}

	/**
	 * 
	 */
	private void childRemoved(CSSNodeImpl parentNode, CSSNodeImpl node) {
		short updateMode = CSSModelUpdateContext.UPDATE_IDLE;

		if (node instanceof CSSStructuredDocumentRegionContainer) {
			updateMode = CSSModelUpdateContext.UPDATE_REMOVE_FNCONTAINER;
		}
		else if (node instanceof CSSRegionContainer) {
			updateMode = CSSModelUpdateContext.UPDATE_REMOVE_RCONTAINER;
		}
		else {
			CSSUtil.debugOut("What's this node? : " + node.getClass().toString());//$NON-NLS-1$
			return;
		}

		fParser.setupUpdateContext(updateMode, parentNode, node);

		CSSNodeImpl prev = getOldPrevious(parentNode, node);
		CSSNodeImpl next = getOldNext(parentNode, node);
		int insertPos = -1, endPos = -1;
		String source = "";//$NON-NLS-1$
		if (prev != null) {
			insertPos = prev.getEndOffset();
		}
		else {
			insertPos = node.getStartOffset();
			insertPos -= nearestSpaceLengthBefore(parentNode, insertPos);
		}
		if (next != null) {
			endPos = next.getStartOffset();
		}
		else {
			endPos = node.getEndOffset();
			endPos += nearestSpaceLengthAfter(parentNode, endPos);
		}
		source = getSpaceBefore(parentNode, next, node);
		if (source.length() > 0) {
			insertText(insertPos, endPos - insertPos, source);
		}
		else {
			removeText(insertPos, endPos - insertPos);
		}

		fParser.cleanupUpdateContext();
	}

	/**
	 * 
	 */
	void childReplaced(CSSNodeImpl parentNode, CSSNodeImpl newChild, CSSNodeImpl oldChild) {
		if (parentNode == null) {
			return;
		}

		if (oldChild != null) {
			childRemoved(parentNode, oldChild);
		}

		if (newChild != null) {
			childInserted(parentNode, newChild);
		}
	}

	/**
	 * 
	 */
	private StructuredDocumentEvent defaultInserted(CSSNodeImpl parentNode, CSSNodeImpl node) {
		int insertPos = -1;
		ICSSNode sibling;
		String preSpace = "", postSpace = "";//$NON-NLS-2$//$NON-NLS-1$
		int length = 0;

		if (insertPos < 0) {
			if ((sibling = node.getPreviousSibling()) != null) {
				// after previous child
				insertPos = getTextEnd(sibling);
			}
		}

		if (insertPos < 0) {
			if ((sibling = node.getNextSibling()) != null) {
				// before next child
				insertPos = getTextStart(sibling);
			}
		}

		if (insertPos < 0) {
			// position of parent
			insertPos = getChildInsertPos(parentNode);
		}

		if (insertPos < 0) {
			// firsttime
			insertPos = 0;
		}

		// format previous spaces
		length = nearestSpaceLengthBefore(parentNode, insertPos);
		insertPos -= length;
		preSpace = getSpaceBefore(parentNode, node, null);
		// format post spaces
		length += nearestSpaceLengthAfter(parentNode, insertPos + length);
		postSpace = getSpaceBefore(parentNode, node.getNextSibling(), null);

		// set text
		String text = preSpace + node.generateSource().trim() + postSpace;
		return insertText(insertPos, length, text);
	}

	/**
	 * @return int
	 * @param node
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 */
	private int getChildInsertPos(CSSNodeImpl node) {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(node);
		if (formatter != null)
			return formatter.getChildInsertPos(node);
		else
			return node.getEndOffset();
	}

	/**
	 * @return org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param parentNode
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param node
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 */
	private CSSNodeImpl getOldNext(CSSNodeImpl parentNode, CSSNodeImpl node) {
		CSSNodeImpl child = (CSSNodeImpl) parentNode.getLastChild();
		CSSNodeImpl ret = null;
		while (child != null) {
			if (node.getEndOffset() < child.getEndOffset())
				ret = child;
			else
				break;
			child = (CSSNodeImpl) child.getPreviousSibling();
		}
		return ret;
	}

	/**
	 * @return org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param parentNode
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param node
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 */
	private CSSNodeImpl getOldPrevious(CSSNodeImpl parentNode, CSSNodeImpl node) {
		CSSNodeImpl child = (CSSNodeImpl) parentNode.getFirstChild();
		CSSNodeImpl ret = null;
		while (child != null) {
			if (child.getStartOffset() < node.getStartOffset())
				ret = child;
			else
				break;
			child = (CSSNodeImpl) child.getNextSibling();
		}
		return ret;
	}

	/**
	 * @return java.lang.String
	 * @param parentNode
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param node
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 */
	private String getSpaceBefore(ICSSNode parentNode, ICSSNode node, ICSSNode toRemove) {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) parentNode);

		if (formatter != null) {
			org.eclipse.jface.text.IRegion exceptFor = null;
			if (toRemove != null) {
				CSSNodeImpl remove = (CSSNodeImpl) toRemove;
				exceptFor = new org.eclipse.jface.text.Region(remove.getStartOffset(), remove.getEndOffset() - remove.getStartOffset());
			}
			return formatter.formatBefore(parentNode, node, exceptFor).toString();
		}
		else
			return "";//$NON-NLS-1$
	}

	/**
	 * 
	 */
	private int getTextEnd(ICSSNode node) {
		int end = -1;
		if (node != null) {
			if (node instanceof CSSStructuredDocumentRegionContainer) {
				end = ((CSSStructuredDocumentRegionContainer) node).getEndOffset();
			}
			else if (node instanceof CSSRegionContainer) {
				end = ((CSSRegionContainer) node).getEndOffset();
			}
		}
		return end;
	}

	/**
	 * 
	 */
	private int getTextStart(ICSSNode node) {
		int start = -1;
		if (node != null) {
			if (node instanceof CSSStructuredDocumentRegionContainer) {
				start = ((CSSStructuredDocumentRegionContainer) node).getStartOffset();
			}
			else if (node instanceof CSSRegionContainer) {
				start = ((CSSRegionContainer) node).getStartOffset();
			}
		}
		return start;
	}

	/**
	 * 
	 */
	private StructuredDocumentEvent insertText(int start, int oldLength, String text) {
		StructuredDocumentEvent result = null;
		BasicStructuredDocument structuredDocument = (BasicStructuredDocument) fModel.getStructuredDocument();
		if (structuredDocument != null) {
			if (text != null && 0 < oldLength && start + oldLength <= structuredDocument.getLength()) {
				// minimize text change
				String delText = structuredDocument.get(start, oldLength);
				int newLength = text.length();
				int shorterLen = Math.min(oldLength, newLength);
				int stMatchLen;
				for (stMatchLen = 0; stMatchLen < shorterLen && text.charAt(stMatchLen) == delText.charAt(stMatchLen); stMatchLen++) {
					// 
				}
				if (0 < stMatchLen && stMatchLen < shorterLen && text.charAt(stMatchLen - 1) == 0x000d && (text.charAt(stMatchLen) == 0x000a || delText.charAt(stMatchLen) == 0x000a)) {
					// must not divide 0d->0a sequence
					stMatchLen--;
				}
				if (stMatchLen == shorterLen) {
					if (oldLength < newLength) { // just insert
						oldLength = 0;
						start += stMatchLen;
						text = text.substring(stMatchLen);
					}
					else if (newLength < oldLength) { // just remove
						oldLength -= stMatchLen;
						start += stMatchLen;
						text = null;
					}
					else { // nothing to do
						oldLength = 0;
						text = null;
					}
				}
				else {
					int edMatchLen;
					for (edMatchLen = 0; stMatchLen + edMatchLen < shorterLen && text.charAt(newLength - edMatchLen - 1) == delText.charAt(oldLength - edMatchLen - 1); edMatchLen++) {
						//
					}
					if (0 < edMatchLen && text.charAt(newLength - edMatchLen) == 0x000a && ((edMatchLen < newLength && text.charAt(newLength - edMatchLen - 1) == 0x000d) || (edMatchLen < oldLength && delText.charAt(oldLength - edMatchLen - 1) == 0x000d))) {
						// must not divide 0d->0a sequence
						edMatchLen--;
					}
					oldLength -= stMatchLen + edMatchLen;
					start += stMatchLen;
					if (stMatchLen + edMatchLen < newLength) {
						text = text.substring(stMatchLen, newLength - edMatchLen);
					}
					else {
						text = null;
					}
				}
			}
			if (0 < oldLength || text != null) {
				// String delText = structuredDocument.get(start, oldLength);
				result = structuredDocument.replaceText(fModel, start, oldLength, text);
			}
		}
		return result;
	}

	/**
	 * @return int
	 * @param insertPos
	 *            int
	 */
	private int nearestSpaceLengthAfter(CSSNodeImpl node, int insertPos) {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(node);
		if (formatter != null) {
			return formatter.getLengthToReformatAfter(node, insertPos);
		}
		else
			return 0;
	}

	/**
	 * @return int
	 * @param insertPos
	 *            int
	 */
	private int nearestSpaceLengthBefore(CSSNodeImpl node, int insertPos) {
		CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(node);
		if (formatter != null) {
			return formatter.getLengthToReformatBefore(node, insertPos);
		}
		else
			return 0;
	}

	/**
	 * 
	 */
	private StructuredDocumentEvent removeText(int start, int length) {
		StructuredDocumentEvent result = null;
		IStructuredDocument structuredDocument = fModel.getStructuredDocument();
		if (structuredDocument != null) {
			result = structuredDocument.replaceText(fModel, start, length, new String(""));//$NON-NLS-1$
		}
		return result;
	}

	/**
	 * 
	 */
	void setParser(CSSModelParser parser) {
		fParser = parser;
	}

	/**
	 * 
	 */
	void valueChanged(CSSNodeImpl node, String oldValue) {
		if (!(node instanceof CSSRegionContainer)) {
			CSSUtil.debugOut("Too Bad.." + //$NON-NLS-1$
						((node == null) ? "null" : node.getClass().toString()));//$NON-NLS-1$
			return;
		}

		int start = node.getStartOffset();

		if (node.getNodeType() == ICSSNode.ATTR_NODE) {
			ICSSAttr attr = (ICSSAttr) node;
			CSSSourceGenerator formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter((INodeNotifier) attr.getOwnerCSSNode());
			if (formatter != null)
				start = formatter.getAttrInsertPos(attr.getOwnerCSSNode(), attr.getName());
		}

		int oldLength = (oldValue == null) ? 0 : oldValue.length();
		// flash old IStructuredDocumentRegion/ITextRegion
		if (node instanceof CSSStructuredDocumentRegionContainer) {
			((CSSStructuredDocumentRegionContainer) node).setFirstStructuredDocumentRegion(null);
			((CSSStructuredDocumentRegionContainer) node).setLastStructuredDocumentRegion(null);
		}
		else if (node instanceof CSSRegionContainer) {
			((CSSRegionContainer) node).setRangeRegion(null, null, null);
		}
		// generate new source
		String newValue = node.generateSource();

		ICSSNode parent;
		if (node.getNodeType() == ICSSNode.ATTR_NODE) {
			parent = ((ICSSAttr) node).getOwnerCSSNode();
		}
		else {
			parent = node.getParentNode();
		}

		fParser.setupUpdateContext(CSSModelUpdateContext.UPDATE_CHANGE_RCONTAINER, parent, node);

		insertText(start, oldLength, newValue);

		fParser.cleanupUpdateContext();
	}
}
