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

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.CSSCoreMessages;
import org.eclipse.wst.css.core.internal.encoding.CSSDocumentLoader;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSCharsetRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSMediaRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPageRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.internal.util.ImportRuleCollector;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;

/**
 * 
 */
class CSSStyleSheetImpl extends CSSDocumentImpl implements ICSSStyleSheet {


	class InternalNodeList implements NodeList {

		Vector nodes = new Vector();

		public int getLength() {
			if (nodes == null)
				return 0;
			else
				return nodes.size();
		}

		public Node item(int i) {
			if (nodes == null)
				return null;
			if (i < 0 || nodes.size() <= i)
				return null;
			return (Node) nodes.get(i);
		}
	}

	class InternalStyleSheetList extends AbstractCSSNodeList implements StyleSheetList {

		public ICSSNode appendNode(ICSSNode node) {
			if (nodes == null || !nodes.contains(node))
				return super.appendNode(node);
			else
				return node;
		}

		public StyleSheet item(int i) {
			return (StyleSheet) itemImpl(i);
		}
	}

	private boolean fDisabled = false;

	/**
	 * 
	 */
	CSSStyleSheetImpl() {
		super();
		setOwnerDocument(this);
	}

	CSSStyleSheetImpl(CSSStyleSheetImpl that) {
		super(that);
		setOwnerDocument(this);
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public org.w3c.dom.css.CSSRule appendRule(org.w3c.dom.css.CSSRule rule) throws org.w3c.dom.DOMException {
		if (rule == null)
			return null;

		CSSRule ret = (CSSRule) appendChild((CSSNodeImpl) rule);
		return ret;
	}

	public ICSSNode cloneNode(boolean deep) {
		CSSStyleSheetImpl cloned = new CSSStyleSheetImpl(this);

		if (deep)
			cloneChildNodes(cloned, deep);

		return cloned;
	}

	/**
	 * @return org.w3c.dom.css.CSSCharsetRule
	 */
	public ICSSCharsetRule createCSSCharsetRule() {
		CSSCharsetRuleImpl rule = new CSSCharsetRuleImpl();
		rule.setOwnerDocument(this);
		return rule;
	}

	/**
	 * @return org.w3c.dom.css.CSSFontFaceRule
	 */
	public CSSFontFaceRule createCSSFontFaceRule() {
		CSSFontFaceRuleImpl rule = new CSSFontFaceRuleImpl();
		CSSStyleDeclarationImpl style = (CSSStyleDeclarationImpl) createCSSStyleDeclaration();

		rule.appendChild(style);
		rule.setOwnerDocument(this);

		return rule;
	}

	/**
	 * @return org.w3c.dom.css.CSSImportRule
	 */
	public ICSSImportRule createCSSImportRule() {
		CSSImportRuleImpl rule = new CSSImportRuleImpl();
		MediaListImpl media = (MediaListImpl) createMediaList();

		rule.appendChild(media);
		rule.setOwnerDocument(this);

		return rule;
	}

	/**
	 * @return org.w3c.dom.css.ICSSMediaRule
	 */
	public ICSSMediaRule createCSSMediaRule() {
		CSSMediaRuleImpl rule = new CSSMediaRuleImpl();
		MediaListImpl media = (MediaListImpl) createMediaList();

		rule.insertBefore(media, (CSSNodeImpl) rule.getFirstChild()); // media
																		// must
																		// be
																		// the
																		// top
																		// of
																		// children
																		// list
		rule.setOwnerDocument(this);

		return rule;
	}

	/**
	 * @return org.w3c.dom.css.CSSPageRule
	 */
	public ICSSPageRule createCSSPageRule() {
		CSSPageRuleImpl rule = new CSSPageRuleImpl();
		CSSStyleDeclarationImpl style = (CSSStyleDeclarationImpl) createCSSStyleDeclaration();

		rule.appendChild(style);
		rule.setOwnerDocument(this);

		return rule;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            java.lang.String
	 */
	public CSSRule createCSSRule(String rule) {
		CSSDocumentLoader loader = new CSSDocumentLoader();
		IStructuredDocument structuredDocument = (IStructuredDocument) loader.createNewStructuredDocument();
		structuredDocument.set(rule);

		//CSSModelParser modelParser = new CSSModelParser((CSSDocumentImpl) getOwnerDocument());
		CSSModelParser modelParser = new CSSModelParser(getOwnerDocument());
		return modelParser.createCSSRule(structuredDocument.getRegionList());
	}

	/**
	 * @return org.w3c.dom.css.CSSStyleDeclaration
	 */
	public ICSSStyleDeclaration createCSSStyleDeclaration() {
		CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(false);
		decl.setOwnerDocument(this);

		return decl;
	}

	/**
	 * @return org.w3c.dom.css.CSSStyleRule
	 */
	public ICSSStyleRule createCSSStyleRule() {
		CSSStyleRuleImpl rule = new CSSStyleRuleImpl();
		CSSStyleDeclarationImpl style = (CSSStyleDeclarationImpl) createCSSStyleDeclaration();

		rule.appendChild(style);
		rule.setOwnerDocument(this);

		return rule;
	}

	/**
	 * @return org.w3c.dom.css.CSSUnknownRule
	 */
	public CSSUnknownRule createCSSUnknownRule() {
		CSSUnknownRuleImpl rule = new CSSUnknownRuleImpl();
		rule.setOwnerDocument(this);

		return rule;
	}

	/**
	 * @return org.w3c.dom.stylesheets.MediaList
	 */
	public MediaList createMediaList() {
		MediaListImpl media = new MediaListImpl();
		media.setOwnerDocument(this);

		return media;
	}

	/**
	 * Used to delete a rule from the style sheet.
	 * 
	 * @param index
	 *            The index within the style sheet's rule list of the rule to
	 *            remove.
	 * @exception DOMException
	 *                INDEX_SIZE_ERR: Raised if the specified index does not
	 *                correspond to a rule in the style sheet's rule list.
	 *                <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if this style sheet
	 *                is readonly.
	 */
	public void deleteRule(int index) throws DOMException {
		CSSNodeImpl node = getIndexedRule(index);
		if (node != null)
			removeChild(node);
	}

	/**
	 * The list of all CSS rules contained within the style sheet. This
	 * includes both rule sets and at-rules.
	 */
	public CSSRuleList getCssRules() {
		CSSRuleListImpl list = new CSSRuleListImpl();

		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof CSSRule) {
				list.appendNode(node);
			}
		}

		return list;
	}

	public CSSRuleList getCssRules(boolean shouldImport) {
		if (!shouldImport)
			return getCssRules();

		CSSRuleListImpl list = new CSSRuleListImpl();
		Stack refs = new Stack();
		getRules(list, this, refs);
		return list;
	}

	private void getRules(CSSRuleListImpl list, ICSSStyleSheet sheet, Stack refs) {
		String href = sheet.getHref();
		if (href != null) {
			// Avoid circular @imports
			if (refs.contains(href))
				return;
			refs.push(href);
		}
		boolean acceptImports = true;
		for (ICSSNode node = sheet.getFirstChild(); node != null; node = node.getNextSibling()) {
			// Import the stylesheet into the list
			// @import rules must precede all other rules, according to the spec
			if (node.getNodeType() == ICSSNode.IMPORTRULE_NODE && acceptImports) {
				CSSStyleSheet importSheet = ((ICSSImportRule) node).getStyleSheet();
				if (importSheet instanceof ICSSStyleSheet)
					getRules(list, (ICSSStyleSheet) importSheet, refs);
				else
					list.appendNode(node);
			}
			// Add the rule to the list
			else if (node instanceof CSSRule) {
				list.appendNode(node);
				if (node.getNodeType() != ICSSNode.CHARSETRULE_NODE)
					acceptImports = false;
			}
		}
		if (href != null)
			refs.pop();
	}

	/**
	 * <code>false</code> if the style sheet is applied to the document.
	 * <code>true</code> if it is not. Modifying this attribute may cause a
	 * new resolution of style for the document. A stylesheet only applies if
	 * both an appropriate medium definition is present and the disabled
	 * attribute is false. So, if the media doesn't apply to the current user
	 * agent, the <code>disabled</code> attribute is ignored.
	 */
	public boolean getDisabled() {
		return fDisabled;
	}

	/**
	 * If the style sheet is a linked style sheet, the value of its attribute
	 * is its location. For inline style sheets, the value of this attribute
	 * is <code>null</code>. See the href attribute definition for the
	 * <code>LINK</code> element in HTML 4.0, and the href pseudo-attribute
	 * for the XML style sheet processing instruction.
	 */
	public String getHref() {
		ICSSModel model = getModel();
		if (model != null && model.getStyleSheetType() == ICSSModel.EXTERNAL) {
			return model.getBaseLocation();
			/*
			 * Object id = model.getId(); if (id != null) { if (id instanceof
			 * IResource) { // TODO: need to check whether this is correct or
			 * not, later. return ((IResource)id).getFullPath().toString(); }
			 * return id.toString(); }
			 */}
		return null;
	}

	CSSRuleImpl getIndexedRule(int index) {
		if (index < 0)
			return null;

		int i = 0;
		for (ICSSNode node = getFirstChild(); node != null; node = node.getNextSibling()) {
			if (node instanceof CSSRule) {
				if (i++ == index)
					return (CSSRuleImpl) node;
			}
		}
		return null;
	}

	/**
	 * The intended destination media for style information. The media is
	 * often specified in the <code>ownerNode</code>. If no media has been
	 * specified, the <code>MediaList</code> will be empty. See the media
	 * attribute definition for the <code>LINK</code> element in HTML 4.0,
	 * and the media pseudo-attribute for the XML style sheet processing
	 * instruction . Modifying the media list may cause a change to the
	 * attribute <code>disabled</code>.
	 */
	public MediaList getMedia() {
		return null;
	}

	/**
	 * @return short
	 */
	public short getNodeType() {
		return STYLESHEET_NODE;
	}

	/**
	 * The node that associates this style sheet with the document. For HTML,
	 * this may be the corresponding <code>LINK</code> or <code>STYLE</code>
	 * element. For XML, it may be the linking processing instruction. For
	 * style sheets that are included by other style sheets, the value of this
	 * attribute is <code>null</code>.
	 */
	public Node getOwnerNode() {
		// for <LINK> tag or <STYLE> tag
		ICSSModel model = getModel();
		if (model != null)
			return model.getOwnerDOMNode();
		return null;
	}

	/**
	 * @return org.w3c.dom.NodeList
	 */
	public org.w3c.dom.NodeList getOwnerNodes() {
		List list = (getModel().getStyleListeners() != null) ? new Vector(getModel().getStyleListeners()) : null;
		if (list == null)
			return null;
		InternalNodeList nodes = new InternalNodeList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof IStyleSheetAdapter) {
				nodes.nodes.add(((IStyleSheetAdapter) obj).getElement());
			}
		}
		if (nodes.getLength() > 0)
			return nodes;
		else
			return null;
	}

	/**
	 * @return org.w3c.dom.NodeList
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	public NodeList getOwnerNodes(Document doc) {
		List list = (getModel().getStyleListeners() != null) ? new Vector(getModel().getStyleListeners()) : null;
		if (list == null)
			return null;
		InternalNodeList nodes = new InternalNodeList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof IStyleSheetAdapter) {
				Element ele = ((IStyleSheetAdapter) obj).getElement();
				if (ele.getOwnerDocument() == doc)
					nodes.nodes.add(ele);
			}
		}
		if (nodes.getLength() > 0)
			return nodes;
		else
			return null;
	}

	/**
	 * If this style sheet comes from an <code>@import</code> rule, the <code>ownerRule</code> attribute will
	 *         contain the <code>CSSImportRule</code>. In that case, the
	 *         <code>ownerNode</code> attribute in the
	 *         <code>StyleSheet</code> interface will be <code>null</code>.
	 *         If the style sheet comes from an element or a processing
	 *         instruction, the <code>ownerRule</code> attribute will be
	 *         <code>null</code> and the <code>ownerNode</code> attribute
	 *         will contain the <code>Node</code>.
	 */
	public CSSRule getOwnerRule() {
		Assert.isTrue(false, CSSCoreMessages.You_cannot_use_CSSStyleShe_UI_); //$NON-NLS-1$ = "You cannot use CSSStyleSheet.getOwnerRule() because of many referencers of this rule\nPlease use getOnwerRules()"
		// for @import
		return null;
	}

	/**
	 * @return org.w3c.dom.css.CSSRuleList
	 */
	public org.w3c.dom.css.CSSRuleList getOwnerRules() {
		StyleSheetList list = getParentStyleSheets();
		if (list == null)
			return null;
		CSSRuleListImpl ruleList = new CSSRuleListImpl();
		for (int i = 0; i < list.getLength(); i++) {
			ImportRuleCollector trav = new ImportRuleCollector(this);
			trav.apply((ICSSStyleSheet) list.item(i));
			ruleList.nodes.addAll(trav.getRules());
		}
		return ruleList;
	}

	/**
	 * For style sheet languages that support the concept of style sheet
	 * inclusion, this attribute represents the including style sheet, if one
	 * exists. If the style sheet is a top-level style sheet, or the style
	 * sheet language does not support inclusion, the value of this attribute
	 * is <code>null</code>.
	 */
	public StyleSheet getParentStyleSheet() {
		CSSRule owner = getOwnerRule();
		if (owner != null)
			return owner.getParentStyleSheet();
		return null;
	}

	/**
	 * @return org.w3c.dom.stylesheets.StyleSheetList
	 */
	public org.w3c.dom.stylesheets.StyleSheetList getParentStyleSheets() {
		List list = (getModel().getStyleListeners() != null) ? new Vector(getModel().getStyleListeners()) : null;
		if (list == null)
			return null;
		InternalStyleSheetList sheets = new InternalStyleSheetList();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof ICSSModel) {
				sheets.appendNode(((ICSSModel) obj).getDocument());
			}
		}
		if (sheets.getLength() > 0)
			return sheets;
		else
			return null;
	}

	/**
	 * The advisory title. The title is often specified in the
	 * <code>ownerNode</code>. See the title attribute definition for the
	 * <code>LINK</code> element in HTML 4.0, and the title pseudo-attribute
	 * for the XML style sheet processing instruction.
	 */
	public String getTitle() {
		Node node = getOwnerNode();
		if (node instanceof Element) {
			return ((Element) node).hasAttribute("TITLE") ? ((Element) node).getAttribute("TITLE") : null;//$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	/**
	 * This specifies the style sheet language for this style sheet. The style
	 * sheet language is specified as a content type (e.g. "text/css"). The
	 * content type is often specified in the <code>ownerNode</code>. Also
	 * see the type attribute definition for the <code>LINK</code> element
	 * in HTML 4.0, and the type pseudo-attribute for the XML style sheet
	 * processing instruction.
	 */
	public String getType() {
		Node node = getOwnerNode();
		if (node instanceof Element) {
			return ((Element) node).hasAttribute("TYPE") ? ((Element) node).getAttribute("TYPE") : null;//$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

	/**
	 * Used to insert a new rule into the style sheet. The new rule now
	 * becomes part of the cascade.
	 * 
	 * @param rule
	 *            The parsable text representing the rule. For rule sets this
	 *            contains both the selector and the style declaration. For
	 *            at-rules, this specifies both the at-identifier and the rule
	 *            content.
	 * @param index
	 *            The index within the style sheet's rule list of the rule
	 *            before which to insert the specified rule. If the specified
	 *            index is equal to the length of the style sheet's rule
	 *            collection, the rule will be added to the end of the style
	 *            sheet.
	 * @return The index within the style sheet's rule collection of the newly
	 *         inserted rule.
	 * @exception DOMException
	 *                HIERARCHY_REQUEST_ERR: Raised if the rule cannot be
	 *                inserted at the specified index e.g. if an
	 *                <code>@import</code> rule is inserted after a standard rule set or other
	 *         at-rule. <br>
	 *         INDEX_SIZE_ERR: Raised if the specified index is not a valid
	 *         insertion point. <br>
	 *         NO_MODIFICATION_ALLOWED_ERR: Raised if this style sheet is
	 *         readonly. <br>
	 *         SYNTAX_ERR: Raised if the specified rule has a syntax error and
	 *         is unparsable.
	 */
	public int insertRule(String rule, int index) throws DOMException {
		int length = getCssRules().getLength();
		if (index < 0 || length < index)
			throw new DOMException(DOMException.INDEX_SIZE_ERR, "");//$NON-NLS-1$

		IStructuredDocument doc = getModel().getStructuredDocument();
		CSSRuleImpl refRule = (length != index) ? getIndexedRule(index) : null;
		int offset = (refRule != null) ? refRule.getStartOffset() : doc.getLength();
		doc.replaceText(this, offset, 0, rule);

		// insertBefore((CSSNodeImpl) createCSSRule(rule),refRule);
		return index;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param newRule
	 *            org.w3c.dom.css.CSSRule
	 * @param refRule
	 *            org.w3c.dom.css.CSSRule
	 */
	public org.w3c.dom.css.CSSRule insertRuleBefore(org.w3c.dom.css.CSSRule newRule, org.w3c.dom.css.CSSRule refRule) throws org.w3c.dom.DOMException {
		if (newRule == null && refRule == null)
			return null;

		CSSRule ret = (CSSRule) insertBefore((CSSNodeImpl) newRule, (CSSNodeImpl) refRule);
		return ret;
	}

	/**
	 * @return boolean
	 */
	public boolean isDocument() {
		return true;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param rule
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public org.w3c.dom.css.CSSRule removeRule(org.w3c.dom.css.CSSRule rule) throws org.w3c.dom.DOMException {
		if (rule == null)
			return null;

		CSSRule ret = (CSSRule) removeChild((CSSNodeImpl) rule);
		return ret;
	}

	/**
	 * @return org.w3c.dom.css.CSSRule
	 * @param newChild
	 *            org.w3c.dom.css.CSSRule
	 * @param oldChild
	 *            org.w3c.dom.css.CSSRule
	 * @exception org.w3c.dom.DOMException
	 *                The exception description.
	 */
	public org.w3c.dom.css.CSSRule replaceRule(org.w3c.dom.css.CSSRule newRule, org.w3c.dom.css.CSSRule oldRule) throws org.w3c.dom.DOMException {
		if (newRule == null && oldRule == null)
			return null;

		CSSRule ret = (CSSRule) replaceChild((CSSNodeImpl) newRule, (CSSNodeImpl) oldRule);
		return ret;
	}

	/**
	 * setDisabled method comment.
	 */
	public void setDisabled(boolean disabled) {
		this.fDisabled = disabled;
	}
}
