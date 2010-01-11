/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSCharsetRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSMediaRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPageRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSRuleContainer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.text.StructuredDocumentWalker;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.text.CoreNodeList;
import org.eclipse.wst.sse.core.internal.text.TextRegionListImpl;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;


/**
 * currently public but may be made default access protected in future.
 */
public class CSSModelParser {

	private ICSSDocument fDocument = null;
	private CSSModelCreationContext fCreationContext = null;
	private CSSModelDeletionContext fDeletionContext = null;
	private CSSModelUpdateContext fUpdateContext = null;
	private CSSModelNodeFeeder fFeeder = null;
	private StructuredDocumentWalker fStructuredDocumentWalker = null;
	private boolean fParseFloating = false;

	/**
	 * CSSModelParser constructor comment.
	 */
	private CSSModelParser() {
		super();
	}

	/**
	 * currently provded this method but may be removed in future.
	 */
	protected boolean isParseFloating(){
		return fParseFloating;
	}
	/**
	 * currently provded this method but may be removed in future.
	 */
	protected CSSModelCreationContext getCreationContext(){
		return fCreationContext;
	}
	/**
	 * currently provded this method but may be removed in future.
	 */
	protected boolean isUpdateContextActive(){
		return fUpdateContext != null ? fUpdateContext.isActive() : false;
	}
	/**
	 * currently public but may be made default access protected in future.
	 */
	public CSSModelParser(ICSSDocument doc) {
		this();
		fDocument = doc;
		fCreationContext = new CSSModelCreationContext((CSSNodeImpl)doc);
		fDeletionContext = new CSSModelDeletionContext(doc);
		fUpdateContext = new CSSModelUpdateContext();
		fFeeder = new CSSModelNodeFeeder(doc, fUpdateContext);
	}

	/**
	 * 
	 */
	void changeStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		replaceStructuredDocumentRegions(new CoreNodeList(flatNode, flatNode), new CoreNodeList(flatNode, flatNode));
	}

	/**
	 * 
	 */
	void changeRegion(IStructuredDocumentRegion flatNode, ITextRegion region) {
		if (flatNode == null || region == null) {
			return;
		}
		if (fDocument == null) {
			return;
		}

		changeStructuredDocumentRegion(flatNode);
	}

	/**
	 * 
	 */
	private void checkNextNode(IStructuredDocumentRegion flatNode, String type) {
		IStructuredDocumentRegion next = CSSUtil.findNextSignificantNode(flatNode);
		if (CSSUtil.getStructuredDocumentRegionType(next) == type) {
			fCreationContext.setReparseStart(flatNode.getEnd());
			fCreationContext.setReparseEnd(next.getEnd());
		}
	}

	/**
	 * 
	 */
	private void cleanupDeletionContext() {
		// setupDeletionContext(null);
	}

	/**
	 * 
	 */
	boolean cleanupFirstNode(IStructuredDocumentRegion firstNode, CSSStructuredDocumentRegionContainer target) {
		if (firstNode == null || target == null) {
			return false;
		}

		if (firstNode == target.getFirstStructuredDocumentRegion()) {
			IStructuredDocumentRegion nextNode = fStructuredDocumentWalker.getNextNodeInCurrent(firstNode);
			IStructuredDocumentRegion lastNode = target.getLastStructuredDocumentRegion();
			if (lastNode == null || fStructuredDocumentWalker.isOldNode(lastNode) || nextNode == null || lastNode.getStartOffset() < nextNode.getStartOffset()) {
				target.setRangeStructuredDocumentRegion(null, null);
			}
			else {
				target.setFirstStructuredDocumentRegion(nextNode);
			}
			ICSSNode parent = target.getParentNode();
			if (parent instanceof CSSStructuredDocumentRegionContainer) {
				cleanupFirstNode(firstNode, (CSSStructuredDocumentRegionContainer) parent);
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 */
	boolean cleanupLastNode(IStructuredDocumentRegion lastNode, CSSStructuredDocumentRegionContainer target) {
		if (lastNode == null || target == null) {
			return false;
		}

		if (lastNode == target.getLastStructuredDocumentRegion()) {
			IStructuredDocumentRegion prevNode = fStructuredDocumentWalker.getPrevNodeInCurrent(lastNode);
			IStructuredDocumentRegion firstNode = target.getFirstStructuredDocumentRegion();
			if (firstNode == null || fStructuredDocumentWalker.isOldNode(firstNode) || prevNode == null || prevNode.getStartOffset() < firstNode.getStartOffset()) {
				target.setRangeStructuredDocumentRegion(null, null);
			}
			else {
				target.setLastStructuredDocumentRegion(prevNode);
			}
			ICSSNode parent = target.getParentNode();
			if (parent instanceof CSSStructuredDocumentRegionContainer) {
				cleanupLastNode(lastNode, (CSSStructuredDocumentRegionContainer) parent);
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 */
	int cleanupUpdateContext() {
		int remains = fUpdateContext.getNodeCount();
		fUpdateContext.cleanupContext();
		return remains;
	}

	/**
	 * create floating CSS rule (owner document will be set) this is for
	 * CSSStyleSheet.createCSSRule(String)
	 * 
	 * @return org.w3c.dom.css.CSSRule
	 */
	CSSRule createCSSRule(IStructuredDocumentRegionList flatNodes) {
		if (flatNodes == null) {
			return null;
		}

		fParseFloating = true;

		// setup creation context
		fCreationContext.clear();
		fCreationContext.setTargetNode(null);
		fCreationContext.setNextNode(null);
		CSSRuleImpl parentRule = null;

		for (Enumeration e = flatNodes.elements(); e.hasMoreElements();) {
			IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e.nextElement();
			if (flatNode == null) {
				continue;
			}
			CSSNodeImpl modified = insertStructuredDocumentRegion(flatNode);
			if (parentRule == null && modified instanceof CSSRuleImpl) {
				parentRule = (CSSRuleImpl) modified;
			}
		}

		fParseFloating = false;

		if (parentRule != null) {
			CSSModelUtil.cleanupContainer(parentRule);
		}

		return parentRule;
	}

	/**
	 * 
	 */
	private IStructuredDocumentRegion findBraceClose(int depth, IStructuredDocumentRegion start, boolean bBreakSibling) {
		IStructuredDocumentRegion result = null;
		int braceLevel = 0;
		IStructuredDocumentRegion prevNode = null;
		IStructuredDocumentRegion region = start.getNext();
		for (; region != null; region = region.getNext()) {
			int offset = region.getStart();
			if (offset < 0) {
				Assert.isTrue(false);
				break;
			}
			CSSNodeImpl node = getNodeAt(offset);
			int depthHit = (node != null) ? CSSModelUtil.getDepth(node) : -1;
			if (depth <= depthHit) {
				// sibling is found before "}"
				if (bBreakSibling) {
					CSSNodeImpl parent = (CSSNodeImpl) node.getParentNode();
					while (depth <= CSSModelUtil.getDepth(parent)) {
						node = parent;
						parent = (CSSNodeImpl) parent.getParentNode();
					}
					if (parent != null && node != null) {
						parent.removeChild(node);
					}
				}
				else {
					result = prevNode;
					break;
				}
			}
			String type = CSSUtil.getStructuredDocumentRegionType(region);
			if (type == CSSRegionContexts.CSS_LBRACE) {
				braceLevel++;
			}
			if (type == CSSRegionContexts.CSS_RBRACE) {
				braceLevel--;
				if (braceLevel < 0) {
					result = region;
					break;
				}
			}
			prevNode = region;
		}

		if (result == null && region == null) {
			// reach the end of document
			result = prevNode;
		}

		return result;
	}

	/**
	 * 
	 */
	private IStructuredDocumentRegionList getStructuredDocumentRegionList(int start, int end) {
		IStructuredDocumentRegionList nodeList = null;
		if (0 <= start && start <= end) {
			ICSSModel model = fDocument.getModel();
			if (model instanceof CSSModelImpl) {
				IStructuredDocument structuredDocument = ((CSSModelImpl) model).getStructuredDocument();
				if (structuredDocument != null) {
					IStructuredDocumentRegion startNode = structuredDocument.getRegionAtCharacterOffset(start);
					IStructuredDocumentRegion endNode = structuredDocument.getRegionAtCharacterOffset(end - 1);
					if (startNode != null && endNode != null) {
						nodeList = new CoreNodeList(startNode, endNode);
					}
				}
			}
		}

		return nodeList;
	}

	/**
	 * 
	 */
	private CSSNodeImpl getNodeAt(int offset) {
		CSSNodeImpl rootNode = fCreationContext.getRootNode();
		ICSSNode target = rootNode.getNodeAt(offset);
		if (target instanceof CSSNodeImpl) {
			return (CSSNodeImpl) target;
		}
		else {
			return null;
		}
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertBraceClose(IStructuredDocumentRegion region) {
		ICSSNode target = CSSModelUtil.findBraceContainer(fCreationContext.getTargetNode());

		if (!(target instanceof CSSStructuredDocumentRegionContainer)) {
			return null;
		}

		CSSStructuredDocumentRegionContainer parent = (CSSStructuredDocumentRegionContainer) target;
		if (CSSModelUtil.isInterruption(parent, region)) {
			ICSSNode node = parent.getParentNode();
			if (node instanceof CSSNodeImpl) {
				fCreationContext.setReparseStart(parent.getStartOffset());
				fCreationContext.setReparseEnd(parent.getEndOffset());
				((CSSNodeImpl) node).removeChild(parent);
			}
			return null;
		}

		if (parent instanceof ICSSPageRule || parent instanceof ICSSMediaRule || parent instanceof CSSFontFaceRule || parent instanceof ICSSStyleRule) {
			CSSModelUtil.expandStructuredDocumentRegionContainer(parent, region);
			if (!CSSModelUtil.isBraceClosed(target)) {
				fCreationContext.setTargetNode(parent.getParentNode());
				fCreationContext.setNextNode(parent.getNextSibling());
				return parent;
			}
		}

		return null;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertBraceOpen(IStructuredDocumentRegion region) {
		IStructuredDocumentRegion keyRegion = CSSUtil.findPreviousSignificantNode(region);
		if (keyRegion == null) {
			return null;
		}
		if (!fParseFloating) {
			CSSNodeImpl node = getNodeAt(keyRegion.getStartOffset());
			if (node != null && !(node instanceof ICSSRuleContainer)) {
				return null;
			}
		}
		String type = CSSUtil.getStructuredDocumentRegionType(keyRegion);

		CSSNodeImpl inserted = null;
		if (type == CSSRegionContexts.CSS_PAGE) {
			inserted = insertPageRule(keyRegion, region);
		}
		else if (type == CSSRegionContexts.CSS_MEDIA) {
			inserted = insertMediaRule(keyRegion, region);
		}
		else if (type == CSSRegionContexts.CSS_FONT_FACE) {
			inserted = insertFontFaceRule(keyRegion, region);
		}
		else if (CSSUtil.isSelectorText(keyRegion)) {
			inserted = insertStyleRule(keyRegion, region);
		}

		if (inserted instanceof CSSStructuredDocumentRegionContainer) {
			// CSSModelUtil.expandStructuredDocumentRegionContainer((CSSStructuredDocumentRegionContainer)inserted,
			// flatNode);
			IStructuredDocumentRegion braceClose = findBraceClose(CSSModelUtil.getDepth(inserted), region, (type == CSSRegionContexts.CSS_MEDIA));
			if (braceClose != null) {
				fCreationContext.setReparseStart(region.getEnd());
				fCreationContext.setReparseEnd(braceClose.getEnd());
			}
		}

		return inserted;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertCharsetRule(IStructuredDocumentRegion beginDocRegion, IStructuredDocumentRegion endDocRegion) {
		CSSNodeImpl parent = fCreationContext.getTargetNode();
		if (!fParseFloating && !(parent instanceof ICSSRuleContainer)) {
			return null;
		}

		ITextRegionList regions = new TextRegionListImpl(beginDocRegion.getRegions());
		regions.remove(0); // must be "@charset"
		ITextRegion encodingRegion = null;
		while (!regions.isEmpty()) {
			ITextRegion textRegion = regions.remove(0);
			if (textRegion == null) {
				continue;
			}
			String type = textRegion.getType();
			if (type == CSSRegionContexts.CSS_S || type == CSSRegionContexts.CSS_COMMENT) {
				continue;
			}
			if (type == CSSRegionContexts.CSS_STRING) {
				encodingRegion = textRegion;
				break;
			}
			else {
				break;
			}
		}
		if (encodingRegion == null) {
			return null;
		}

		CSSCharsetRuleImpl rule = fFeeder.getCSSCharsetRule();
		if (rule == null) {
			return null;
		}

		if (!fUpdateContext.isActive()) {
			rule.setAttribute(ICSSCharsetRule.ENCODING, beginDocRegion.getText(encodingRegion));
		}

		// setup flat container
		rule.setRangeStructuredDocumentRegion(beginDocRegion, endDocRegion);
		CSSAttrImpl attr = rule.getAttributeNode(ICSSCharsetRule.ENCODING);
		if (attr != null) {
			attr.setRangeRegion(beginDocRegion, encodingRegion, encodingRegion);
		}

		// insert to tree
		if (!fUpdateContext.isActive() && parent != null) {
			propagateRangePreInsert(parent, rule);
			CSSNodeImpl next = fCreationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(rule, next);
			}
			else {
				parent.appendChild(rule);
			}
		}

		return rule;
	}

	/**
	 * currently public but may be made default access protected in future.
	 */
	protected CSSNodeImpl insertStructuredDocumentRegion(IStructuredDocumentRegion region) {
		if (fCreationContext == null || region == null) {
			return null;
		}

		String type = ((BasicStructuredDocumentRegion) region).getType();
		CSSNodeImpl modified = null;

		ICSSNode target = fCreationContext.getTargetNode();

		if ((fParseFloating && target == null) || target instanceof ICSSRuleContainer) {
			if (type == CSSRegionContexts.CSS_DELIMITER) {
				modified = insertSemiColonForRule(region);
			}
			else if (type == CSSRegionContexts.CSS_LBRACE) {
				modified = insertBraceOpen(region);
			}
			else if (type == CSSRegionContexts.CSS_RBRACE) {
				modified = insertBraceClose(region);
			}
			else if (type == CSSRegionContexts.CSS_MEDIA || type == CSSRegionContexts.CSS_PAGE || type == CSSRegionContexts.CSS_FONT_FACE || CSSUtil.isSelectorText(region)) {
				checkNextNode(region, CSSRegionContexts.CSS_LBRACE);
			}
			else if (type == CSSRegionContexts.CSS_IMPORT || type == CSSRegionContexts.CSS_CHARSET) {
				checkNextNode(region, CSSRegionContexts.CSS_DELIMITER);
			}
		}
		else if ((target instanceof CSSRuleDeclContainer || target instanceof CSSStyleDeclaration) && type == CSSRegionContexts.CSS_DECLARATION_PROPERTY) {
			modified = insertStyleDeclarationItem(region);
		}
		else if (target instanceof ICSSStyleDeclItem && type == CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
			modified = insertSemiColonForStyleDeclarationItem(region);
		}
		else if (type == CSSRegionContexts.CSS_RBRACE) {
			modified = insertBraceClose(region);
		}

		// post process
		if (modified != null) {
			if (modified instanceof CSSStructuredDocumentRegionContainer) {
				((CSSStructuredDocumentRegionContainer) modified).propagateRangeStructuredDocumentRegion();
			}
		}

		return modified;
	}

	/**
	 * 
	 */
	private void insertStructuredDocumentRegions(IStructuredDocumentRegionList regionList) {
		for (Enumeration e = regionList.elements(); e.hasMoreElements();) {
			IStructuredDocumentRegion region = (IStructuredDocumentRegion) e.nextElement();
			if (region == null) {
				continue;
			}
			insertStructuredDocumentRegion(region);
			if (fCreationContext.isToReparse()) {
				int origStart = region.getEnd();
				int origEnd = regionList.item(regionList.getLength() - 1).getEnd();
				int newStart = fCreationContext.getReparseStart();
				int newEnd = fCreationContext.getReparseEnd();
				if (newStart < origStart || origEnd < newEnd) {
					IStructuredDocumentRegionList newNodeList = getStructuredDocumentRegionList(newStart, newEnd);
					setupCreationContext(newNodeList.item(0));
					insertStructuredDocumentRegions(newNodeList);
					return;
				}
				else {
					fCreationContext.resetReparseRange();
				}
			}
		}
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertFontFaceRule(IStructuredDocumentRegion region, IStructuredDocumentRegion braceNode) {
		CSSNodeImpl parent = fCreationContext.getTargetNode();
		if (!fParseFloating && !(parent instanceof ICSSRuleContainer)) {
			return null;
		}

		CSSFontFaceRuleImpl rule = fFeeder.getCSSFontFaceRule();
		if (rule == null) {
			return null;
		}

		// setup flat container
		rule.setRangeStructuredDocumentRegion(region, braceNode);

		// insert to tree
		if (!fUpdateContext.isActive() && parent != null) {
			propagateRangePreInsert(parent, rule);
			CSSNodeImpl next = fCreationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(rule, next);
			}
			else {
				parent.appendChild(rule);
			}
		}

		fCreationContext.setTargetNode(rule);
		// TargetNext is set to null automatically

		return rule;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertImportRule(IStructuredDocumentRegion beginDocRegion, IStructuredDocumentRegion endDocRegion) {
		CSSNodeImpl parent = fCreationContext.getTargetNode();
		if (!fParseFloating && !(parent instanceof ICSSRuleContainer)) {
			return null;
		}

		ITextRegionList regions = new TextRegionListImpl(beginDocRegion.getRegions());
		regions.remove(0); // must be "@import"
		ITextRegion hrefRegion = null;
		while (!regions.isEmpty()) {
			ITextRegion textRegion = regions.remove(0);
			if (textRegion == null) {
				continue;
			}
			String type = textRegion.getType();
			if (type == CSSRegionContexts.CSS_S || type == CSSRegionContexts.CSS_COMMENT) {
				continue;
			}
			if (type == CSSRegionContexts.CSS_URI || type == CSSRegionContexts.CSS_STRING) {
				hrefRegion = textRegion;
				break;
			}
			else {
				break;
			}
		}
		if (hrefRegion == null) {
			return null;
		}

		CSSImportRuleImpl rule = fFeeder.getCSSImportRule();
		if (rule == null) {
			return null;
		}

		CSSUtil.stripSurroundingSpace(regions);
		MediaListImpl mediaList = (MediaListImpl) rule.getMedia();
		setMediaList(mediaList, beginDocRegion, regions);

		if (!fUpdateContext.isActive()) {
			rule.setAttribute(ICSSImportRule.HREF, beginDocRegion.getText(hrefRegion));
		}

		// setup flat container
		rule.setRangeStructuredDocumentRegion(beginDocRegion, endDocRegion);
		CSSAttrImpl attr = rule.getAttributeNode(ICSSImportRule.HREF);
		if (attr != null) {
			attr.setRangeRegion(beginDocRegion, hrefRegion, hrefRegion);
		}

		// insert to tree
		if (!fUpdateContext.isActive() && parent != null) {
			propagateRangePreInsert(parent, rule);
			CSSNodeImpl next = fCreationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(rule, next);
			}
			else {
				parent.appendChild(rule);
			}
		}

		return rule;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertMediaRule(IStructuredDocumentRegion flatNode, IStructuredDocumentRegion braceNode) {
		CSSNodeImpl parent = fCreationContext.getTargetNode();
		if (!fParseFloating && !(parent instanceof ICSSRuleContainer)) {
			return null;
		}

		CSSMediaRuleImpl rule = fFeeder.getCSSMediaRule();
		if (rule == null) {
			return null;
		}

		ITextRegionList regions = new TextRegionListImpl(flatNode.getRegions());
		regions.remove(0); // must be "@media"

		CSSUtil.stripSurroundingSpace(regions);
		MediaListImpl mediaList = (MediaListImpl) rule.getMedia();
		setMediaList(mediaList, flatNode, regions);

		// setup flat container
		rule.setRangeStructuredDocumentRegion(flatNode, braceNode);

		// insert to tree
		if (!fUpdateContext.isActive() && parent != null) {
			propagateRangePreInsert(parent, rule);
			CSSNodeImpl next = fCreationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(rule, next);
			}
			else {
				parent.appendChild(rule);
			}
		}

		fCreationContext.setTargetNode(rule);
		// TargetNext is set to null automatically

		return rule;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertPageRule(IStructuredDocumentRegion flatNode, IStructuredDocumentRegion braceNode) {
		CSSNodeImpl parent = fCreationContext.getTargetNode();
		if (!fParseFloating && !(parent instanceof ICSSRuleContainer)) {
			return null;
		}

		// get selector regions
		ITextRegionList selectorRegions = new TextRegionListImpl(flatNode.getRegions());
		selectorRegions.remove(0); // must be "@page"
		CSSUtil.stripSurroundingSpace(selectorRegions);

		CSSPageRuleImpl rule = fFeeder.getCSSPageRule();
		if (rule == null) {
			return null;
		}

		if (!fUpdateContext.isActive()) {
			String selectorStr = CSSUtil.getRegionText(flatNode, selectorRegions);
			if (0 < selectorStr.length()) {
				rule.setSelectorText(selectorStr);
			}
		}

		// setup flat container
		rule.setRangeStructuredDocumentRegion(flatNode, braceNode);
		CSSAttrImpl attr = rule.getAttributeNode(ICSSPageRule.SELECTOR);
		if (attr != null && selectorRegions != null && !selectorRegions.isEmpty()) {
			attr.setRangeRegion(flatNode, selectorRegions.get(0), selectorRegions.get(selectorRegions.size() - 1));
		}

		// insert to tree
		if (!fUpdateContext.isActive() && parent != null) {
			propagateRangePreInsert(parent, rule);
			CSSNodeImpl next = fCreationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(rule, next);
			}
			else {
				parent.appendChild(rule);
			}
		}

		fCreationContext.setTargetNode(rule);
		// TargetNext is set to null automatically

		return rule;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertSemiColonForRule(IStructuredDocumentRegion region) {
		IStructuredDocumentRegion keyRegion = CSSUtil.findPreviousSignificantNode(region);
		String type = CSSUtil.getStructuredDocumentRegionType(keyRegion);

		CSSNodeImpl inserted = null;
		if (type == CSSRegionContexts.CSS_IMPORT) {
			inserted = insertImportRule(keyRegion, region);
		}
		else if (type == CSSRegionContexts.CSS_CHARSET) {
			inserted = insertCharsetRule(keyRegion, region);
		}

		return inserted;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertSemiColonForStyleDeclarationItem(IStructuredDocumentRegion region) {
		// only target/net node is changed. nothing to do.

		CSSNodeImpl targetNode = fCreationContext.getTargetNode();
		if (targetNode instanceof ICSSStyleDeclItem) {
			int offset = targetNode.getStartOffset();
			// widen document region range
			// ((CSSStyleDeclItemImpl)targetNode).setLastStructuredDocumentRegion(region);
			CSSModelUtil.expandStructuredDocumentRegionContainer((CSSStyleDeclItemImpl) targetNode, region);
			// psStructuredDocumentRegion indicates CSSStyleDeclItem
			ICSSNode parentNode = targetNode.getParentNode();
			fCreationContext.setTargetNode(parentNode);
			ICSSNode next = null;
			if (parentNode.hasChildNodes()) {
				for (ICSSNode child = targetNode.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child instanceof CSSStructuredDocumentRegionContainer && offset < ((CSSStructuredDocumentRegionContainer) child).getStartOffset()) {
						next = child;
						break;
					}
				}
			}
			fCreationContext.setNextNode(next);

			return targetNode;
		}

		return null;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertStyleDeclarationItem(IStructuredDocumentRegion docRegion) {
		CSSStyleDeclarationImpl parent = null;
		CSSNodeImpl node = fCreationContext.getTargetNode();
		if (node instanceof CSSRuleDeclContainer) {
			CSSRuleDeclContainer declContainer = (CSSRuleDeclContainer) node;
			parent = (CSSStyleDeclarationImpl) declContainer.getStyle();
		}
		else if (node instanceof CSSStyleDeclarationImpl) {
			parent = (CSSStyleDeclarationImpl) node;
		}

		CSSDeclarationItemParser itemParser = new CSSDeclarationItemParser(parent.getOwnerDocument());
		itemParser.setStructuredDocumentTemporary(false);
		itemParser.setUpdateContext(fUpdateContext);
		CSSStyleDeclItemImpl declItem = itemParser.setupDeclarationItem(docRegion);
		if (declItem == null) {
			return null;
		}

		// setup flat container
		declItem.setRangeStructuredDocumentRegion(docRegion, docRegion);

		// insert to tree
		if (!fUpdateContext.isActive()) {
			propagateRangePreInsert(parent, declItem);
			CSSNodeImpl next = fCreationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(declItem, next);
			}
			else {
				parent.appendChild(declItem);
			}
		}

		fCreationContext.setTargetNode(declItem);
		// TargetNext is set to null automatically

		return declItem;
	}

	/**
	 * 
	 */
	private CSSNodeImpl insertStyleRule(IStructuredDocumentRegion flatNode, IStructuredDocumentRegion braceNode) {
		CSSNodeImpl parent = fCreationContext.getTargetNode();
		if (!fParseFloating && !(parent instanceof ICSSRuleContainer)) {
			return null;
		}

		// get selector regions
		ITextRegionList selectorRegions = new TextRegionListImpl(flatNode.getRegions());
		CSSUtil.stripSurroundingSpace(selectorRegions);

		CSSStyleRuleImpl rule = fFeeder.getCSSStyleRule();
		if (rule == null) {
			return null;
		}

		if (!fUpdateContext.isActive()) {
			String selectorStr = CSSUtil.getRegionText(flatNode, selectorRegions);
			if (selectorStr != null && 0 < selectorStr.length()) {
				rule.setSelectorText(selectorStr);
			}
		}

		// setup flat container
		rule.setRangeStructuredDocumentRegion(flatNode, braceNode);
		CSSAttrImpl attr = rule.getAttributeNode(ICSSPageRule.SELECTOR);
		if (attr != null && selectorRegions != null && !selectorRegions.isEmpty()) {
			attr.setRangeRegion(flatNode, selectorRegions.get(0), selectorRegions.get(selectorRegions.size() - 1));
		}

		// insert to tree
		if (!fUpdateContext.isActive() && parent != null) {
			propagateRangePreInsert(parent, rule);
			CSSNodeImpl next = fCreationContext.getNextNode();
			if (next != null) {
				parent.insertBefore(rule, next);
			}
			else {
				parent.appendChild(rule);
			}
		}

		fCreationContext.setTargetNode(rule);
		// TargetNext is set to null automatically

		return rule;
	}

	/**
	 * 
	 */
	private void pretendRemoveNode() {
		CSSStructuredDocumentRegionContainer node = (CSSStructuredDocumentRegionContainer) fUpdateContext.getDeletionTarget();
		if (node == null) {
			return;
		}

		IStructuredDocumentRegion firstNode = node.getFirstStructuredDocumentRegion();
		if (firstNode != null) {
			fDeletionContext.expandRemovedRangeBegin(firstNode);
		}
		IStructuredDocumentRegion lastNode = node.getLastStructuredDocumentRegion();
		if (lastNode != null) {
			fDeletionContext.expandRemovedRangeEnd(lastNode);
		}

		shrinkContainer((CSSStructuredDocumentRegionContainer) fUpdateContext.getDeletionTargetParent(), node);
	}

	/**
	 *	currently public but may be made default access protected in future.
	 *
	 * @param parent
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 * @param child
	 *            org.eclipse.wst.css.core.model.CSSNodeImpl
	 */
	protected void propagateRangePreInsert(CSSNodeImpl parent, CSSNodeImpl child) {
		if (!(child instanceof CSSStructuredDocumentRegionContainer) || !(parent instanceof CSSStructuredDocumentRegionContainer)) {
			return;
		}

		CSSStructuredDocumentRegionContainer parentContainer = (CSSStructuredDocumentRegionContainer) parent;
		CSSStructuredDocumentRegionContainer childContainer = (CSSStructuredDocumentRegionContainer) child;
		IStructuredDocumentRegion firstNode = childContainer.getFirstStructuredDocumentRegion();
		IStructuredDocumentRegion lastNode = childContainer.getLastStructuredDocumentRegion();
		if (firstNode == null || lastNode == null) {
			return;
		}

		boolean bModified = parentContainer.includeRangeStructuredDocumentRegion(firstNode, lastNode);
		if (bModified) {
			parentContainer.propagateRangeStructuredDocumentRegion();
		}
	}

	/**
	 * 
	 */
	private void removeStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		String type = CSSUtil.getStructuredDocumentRegionType(flatNode);
		if (type == CSSRegionContexts.CSS_DELIMITER || type == CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
			do {
				flatNode = fStructuredDocumentWalker.getPrevNode(flatNode);
				type = (flatNode != null) ? CSSUtil.getStructuredDocumentRegionType(flatNode) : null;
			}
			while (type != null && (type == CSSRegionContexts.CSS_S || type == CSSRegionContexts.CSS_COMMENT));
		}
		if (flatNode == null) {
			return;
		}

		// if (fDeletionContext.isInRemovedRange(flatNode)) { // already
		// removed
		// return;
		// }

		CSSStructuredDocumentRegionContainer node = fDeletionContext.findDeletionTarget((CSSNodeImpl)fDocument, flatNode);
		if (node == null || node == fDocument) {
			return; // not attached with any treeNode
		}

		if (node instanceof CSSStyleDeclarationImpl) {
			ICSSNode rule = node.getParentNode();
			if (rule instanceof CSSStyleRuleImpl) {
				node = (CSSStructuredDocumentRegionContainer) rule;
			}
			else {
				return;
			}
		}

		// ICSSNode p = node.getParentNode();
		// if (p == null || ! (p instanceof
		// CSSStructuredDocumentRegionContainer)) {
		// return;
		// }
		// CSSStructuredDocumentRegionContainer parent =
		// (CSSStructuredDocumentRegionContainer)p;

		if (fDeletionContext.addNodeToBeRemoved(node)) {
			IStructuredDocumentRegion firstNode = node.getFirstStructuredDocumentRegion();
			if (firstNode != null) {
				fDeletionContext.expandRemovedRangeBegin(firstNode);
			}
			IStructuredDocumentRegion lastNode = node.getLastStructuredDocumentRegion();
			if (lastNode != null) {
				fDeletionContext.expandRemovedRangeEnd(lastNode);
			}
		}

		// shrinkContainer(node);
		// parent.removeChild(node);
	}

	/**
	 * 
	 */
	private void removeStructuredDocumentRegions(IStructuredDocumentRegionList flatNodes) {
		for (Enumeration e = flatNodes.elements(); e.hasMoreElements();) {
			IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e.nextElement();
			if (flatNode == null) {
				continue;
			}
			removeStructuredDocumentRegion(flatNode);
		}

		Iterator i = fDeletionContext.getNodesToBeRemoved();
		while (i.hasNext()) {
			CSSNodeImpl node = (CSSNodeImpl) i.next();
			if (!(node instanceof CSSStructuredDocumentRegionContainer)) {
				continue;
			}
			CSSNodeImpl parent = (CSSNodeImpl) node.getParentNode();
			if (!(parent instanceof CSSStructuredDocumentRegionContainer)) {
				continue;
			}
			shrinkContainer((CSSStructuredDocumentRegionContainer) parent, (CSSStructuredDocumentRegionContainer) node);
			parent.removeChild(node);
		}
	}

	/**
	 * 
	 */
	void replaceStructuredDocumentRegions(IStructuredDocumentRegionList newStructuredDocumentRegions, IStructuredDocumentRegionList oldStructuredDocumentRegions) {
		if (fDocument == null || fCreationContext == null) {
			return;
		}

		if (oldStructuredDocumentRegions != null && 0 < oldStructuredDocumentRegions.getLength()) {
			setupDeletionContext(newStructuredDocumentRegions, oldStructuredDocumentRegions);
			short updateMode = fUpdateContext.getUpdateMode();
			if (updateMode == CSSModelUpdateContext.UPDATE_IDLE) {
				removeStructuredDocumentRegions(oldStructuredDocumentRegions);
			}
			else {
				pretendRemoveNode();
			}
			newStructuredDocumentRegions = getStructuredDocumentRegionList(fDeletionContext.getRemovedRangeBegin(), fDeletionContext.getRemovedRangeEnd());
			cleanupDeletionContext();
		}

		if (newStructuredDocumentRegions != null && 0 < newStructuredDocumentRegions.getLength()) {
			/* when removing old nodes the creation context should be set up based on them
			 * else creation context is that of the new nodes
			 */
			if( oldStructuredDocumentRegions != null && oldStructuredDocumentRegions.getLength() < 0) {
				setupCreationContext(oldStructuredDocumentRegions.item(0));
			} else {
				setupCreationContext(newStructuredDocumentRegions.item(0));
			}
			insertStructuredDocumentRegions(newStructuredDocumentRegions);
		}

		// make document hold whole structuredDocument
		/*
		 * boolean bUpdate = false; IStructuredDocumentRegion flatNode;
		 * flatNode = fDocument.getFirstStructuredDocumentRegion(); bUpdate =
		 * bUpdate || (flatNode == null ||
		 * fStructuredDocumentWalker.isOldNode(flatNode) ||
		 * flatNode.getPrevious() != null); flatNode =
		 * fDocument.getLastStructuredDocumentRegion(); bUpdate = bUpdate ||
		 * (flatNode == null || fStructuredDocumentWalker.isOldNode(flatNode) ||
		 * flatNode.getNext() != null);
		 */
		IStructuredDocument structuredDocument = fStructuredDocumentWalker.getStructuredDocument();
		((CSSStructuredDocumentRegionContainer)fDocument).setRangeStructuredDocumentRegion(structuredDocument.getFirstStructuredDocumentRegion(), structuredDocument.getLastStructuredDocumentRegion());
		/* } */

		// remove in official release
		// CSSModelUtil.diagnoseTree(fDocument,
		// fStructuredDocumentWalker.getStructuredDocument());
	}

	/**
	 * 
	 */
	void replaceRegions(IStructuredDocumentRegion flatNode, ITextRegionList newRegions, ITextRegionList oldRegions) {
		if (flatNode == null) {
			return;
		}
		if (newRegions == null || oldRegions == null) {
			return;
		}
		if (fDocument == null) {
			return;
		}

		changeStructuredDocumentRegion(flatNode);
	}

	/**
	 */
	private void resetCreationTarget(IStructuredDocumentRegion newStructuredDocumentRegion) {
		if (newStructuredDocumentRegion == null || newStructuredDocumentRegion.getStartOffset() <= 0) {
			// top of document
			fCreationContext.setTargetNode(fDocument);
			fCreationContext.setNextNode(fDocument.getFirstChild());
			return;
		}

		int cursorPos = newStructuredDocumentRegion.getStartOffset();
		CSSNodeImpl cursorNode = getNodeAt(cursorPos);
		if (cursorNode == null) { // end of document
			cursorNode = (CSSNodeImpl)fDocument;
		}

		// find edge of tree node
		CSSNodeImpl node = null;
		// boolean bOverSemiColon = false;
		boolean bOverOpenBrace = false;
		IStructuredDocumentRegion flatNode;
		for (flatNode = newStructuredDocumentRegion; flatNode != null; flatNode = flatNode.getPrevious()) {
			node = getNodeAt(flatNode.getStartOffset());
			if (node == null) {
				node = (CSSNodeImpl)fDocument;
			}
			if (node != cursorNode || node.getStartOffset() == flatNode.getStartOffset()) {
				break;
			}
			if (flatNode != newStructuredDocumentRegion) {
				String type = CSSUtil.getStructuredDocumentRegionType(flatNode);
				// if (type == CSSRegionContexts.CSS_DELIMITER ||
				// type == CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
				// bOverSemiColon = true;
				// } else
				if (type == CSSRegionContexts.CSS_LBRACE) {
					bOverOpenBrace = true;
				}
			}
		}

		CSSNodeImpl targetNode = null;

		// if (flatNode == null) {
		// v<--|
		// AAAAAA
		// BBBBBBBBBB cursorNode:A , node:B -> target is A
		// targetNode = (node == null) ? fDocument : node;
		// } else
		if (cursorNode == node) {
			// v<--|
			// AAAAAA
			// BBBBBBBBBB cursorNode:A , node:B -> target is A
			if (bOverOpenBrace && cursorNode instanceof CSSRuleDeclContainer) {
				targetNode = (CSSNodeImpl) ((CSSRuleDeclContainer) cursorNode).getStyle();
			}
			else {
				targetNode = cursorNode;
			}
		}
		else {
			// v<--|
			// AAA
			// BBBBBBBBBB cursorNode:B , node:A -> depend on A's node type
			short nodeType = node.getNodeType();
			if (nodeType == ICSSNode.STYLEDECLITEM_NODE || nodeType == ICSSNode.CHARSETRULE_NODE || nodeType == ICSSNode.IMPORTRULE_NODE) {
				// targetNode = (CSSNodeImpl)((bOverSemiColon) ?
				// node.getParentNode() : node); // NP
				String regionType = CSSUtil.getStructuredDocumentRegionType(flatNode);
				if (regionType == CSSRegionContexts.CSS_DELIMITER || regionType == CSSRegionContexts.CSS_DECLARATION_DELIMITER) {
					targetNode = (CSSNodeImpl) node.getParentNode();
				}
				else {
					targetNode = node;
				}
			}
			else if (CSSUtil.getStructuredDocumentRegionType(flatNode) == CSSRegionContexts.CSS_RBRACE) {
				targetNode = (CSSNodeImpl) node.getParentNode();
			}
			else {
				targetNode = node;
			}
		}

		fCreationContext.setTargetNode(targetNode);
		ICSSNode next = null;
		if (targetNode.hasChildNodes()) {
			for (ICSSNode child = targetNode.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child instanceof CSSStructuredDocumentRegionContainer && cursorPos < ((CSSStructuredDocumentRegionContainer) child).getStartOffset()) {
					next = child;
					break;
				}
			}
		}
		fCreationContext.setNextNode(next);

	}

	/**
	 */
	private void resetReparseRange() {
		fCreationContext.clear();
	}

	void setStructuredDocumentEvent(StructuredDocumentEvent event) {
		if (fStructuredDocumentWalker == null) {
			fStructuredDocumentWalker = new StructuredDocumentWalker();
		}
		fStructuredDocumentWalker.initialize(event);
	}

	/**
	 * regions: surrounding spaces should be removed. Q. Why did you set
	 * mediaTable ? A. MediaList may have two or more medium that have same
	 * value, then searcing in MediaList is not perfect. Q.
	 * fUpdateContext.isActive() is not care. Are you OK? A. OK.
	 */
	private void setMediaList(MediaListImpl mediaList, IStructuredDocumentRegion region, ITextRegionList textRegions) {
		if (mediaList == null || textRegions == null) {
			return;
		}
		Collection mediaTable = new HashSet();
		CSSNodeListImpl attrs = mediaList.getMedia();
		for (int i = 0; i != attrs.getLength(); i++) {
			mediaTable.add(attrs.item(i));
		}

		ITextRegion start = null;
		ITextRegion end = null;

		Iterator i = textRegions.iterator();
		ITextRegion textRegion = (ITextRegion) ((i.hasNext()) ? i.next() : null);
		while (textRegion != null) {
			if (textRegion.getType() == CSSRegionContexts.CSS_MEDIUM) {
				String mediumStr = region.getText(textRegion);
				if (0 < mediumStr.length()) {
					CSSAttrImpl attr = null;
					// is the medium already set ?
					Iterator iTable = mediaTable.iterator();
					while (iTable.hasNext()) {
						CSSAttrImpl cai = (CSSAttrImpl) iTable.next();
						if (mediumStr.equalsIgnoreCase(cai.getValue())) {
							attr = cai;
							mediaTable.remove(cai);
							break;
						}
					}
					if (attr == null) {
						// is not set. create new attribute
						String key = "mediumP" + mediaList.mediumCounter++; //$NON-NLS-1$
						mediaList.setAttribute(key, mediumStr);
						attr = mediaList.getAttributeNode(key);
					}
					attr.setRangeRegion(region, textRegion, textRegion);
					if (start == null) {
						start = textRegion;
					}
					end = textRegion;
				}
			}
			textRegion = (ITextRegion) ((i.hasNext()) ? i.next() : null);
		}

		if (start != null && end != null) {
			mediaList.setRangeRegion(region, start, end);
		}
	}

	/**
	 * 
	 */
	private void setupCreationContext(IStructuredDocumentRegion region) {
		resetReparseRange();
		resetCreationTarget(region);
	}

	/**
	 * 
	 */
	private void setupDeletionContext(IStructuredDocumentRegionList newStructuredDocumentRegions, IStructuredDocumentRegionList oldStructuredDocumentRegions) {
		fDeletionContext.setupContext(newStructuredDocumentRegions, oldStructuredDocumentRegions);
	}

	/**
	 * 
	 */
	void setupUpdateContext(short updateMode, ICSSNode parentNode, ICSSNode targetNode) {
		fUpdateContext.setupContext(updateMode, parentNode, targetNode);
	}

	/**
	 * 
	 */
	void shrinkContainer(CSSStructuredDocumentRegionContainer parent, CSSStructuredDocumentRegionContainer child) {
		if (child == null) {
			return;
		}

		boolean bModified = false;
		bModified = bModified || cleanupLastNode(child.getLastStructuredDocumentRegion(), parent);
		bModified = bModified || cleanupFirstNode(child.getFirstStructuredDocumentRegion(), parent);

		if (bModified) {
			if (parent != null) {
				for (ICSSNode node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
					if (child != node && node instanceof CSSStructuredDocumentRegionContainer) {
						((CSSStructuredDocumentRegionContainer) node).propagateRangeStructuredDocumentRegion();
					}
				}
			}
		}

		child.setRangeStructuredDocumentRegion(null, null);
	}
}
