/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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
import java.util.Vector;

import org.eclipse.wst.css.core.internal.event.ICSSStyleListener;
import org.eclipse.wst.css.core.internal.eventimpl.CSSEmbededStyleNotifyAdapter;
import org.eclipse.wst.css.core.internal.eventimpl.CSSStyleNotifyAdapter;
import org.eclipse.wst.css.core.internal.parser.CSSSourceParser;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValue;
import org.eclipse.wst.css.core.internal.util.ImportRuleCollector;
import org.eclipse.wst.css.core.internal.util.ImportedCollector;
import org.eclipse.wst.css.core.internal.util.SelectorsCollector;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.model.AbstractStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.ResourceInUse;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



public class CSSModelImpl extends AbstractStructuredModel implements ICSSModel, IStructuredDocumentListener {

	private CSSDocumentImpl document = null;
	private org.w3c.dom.Node ownerNode = null;
	private CSSStyleNotifyAdapter styleNotifier = null;
	private CSSModelParser fParser = null;
	private CSSModelUpdater fUpdater = null;
	private boolean fStructuredDocumentUpdate = false;
	private final static String ID_NON_EXTERNAL_CSS = "**_NON_EXTERNAL_CSS_***";//$NON-NLS-1$

	/**
	 * CSSModelImpl constructor comment.
	 * 
	 */
	public CSSModelImpl() {
		super();
	}

	/**
	 * @param listener
	 *            org.eclipse.wst.css.core.event.CSSStyleListener
	 */
	public void addStyleListener(ICSSStyleListener listener) {
		getStyleNotifier().addStyleListener(listener);
	}

	void attrReplaced(CSSNodeImpl parentNode, CSSNodeImpl newAttr, CSSNodeImpl oldAttr) {
		if (!fStructuredDocumentUpdate) {
			CSSModelUpdater updater = getUpdater();
			updater.attrReplaced(parentNode, newAttr, oldAttr);
		}

		ICSSSelector removed[] = null, added[] = null;
		if (oldAttr != null && oldAttr.getNodeType() == ICSSNode.ATTR_NODE && ((CSSAttrImpl) oldAttr).getName().equals(ICSSStyleRule.SELECTOR)) {
			CSSAttrImpl attr = (CSSAttrImpl) oldAttr;
			// collect changed selector
			ICSSSelectorList list = new CSSSelectorListImpl(attr.getValue());
			removed = new ICSSSelector[list.getLength()];
			for (int i = 0; i < list.getLength(); i++)
				removed[i] = list.getSelector(i);
		}
		if (newAttr != null && newAttr.getNodeType() == ICSSNode.ATTR_NODE && ((CSSAttrImpl) newAttr).getName().equals(ICSSStyleRule.SELECTOR)) {
			CSSAttrImpl attr = (CSSAttrImpl) newAttr;
			// collect changed selector
			ICSSSelectorList list = new CSSSelectorListImpl(attr.getValue());
			added = new ICSSSelector[list.getLength()];
			for (int i = 0; i < list.getLength(); i++)
				added[i] = list.getSelector(i);
		}
		if (removed != null || added != null || getDocument().getNodeType() == ICSSNode.STYLEDECLARATION_NODE) {
			getStyleNotifier().fire(removed, added, null);
		}
		// for href attribute
		if (getStyleListeners() != null && getStyleListeners().size() > 0) {
			boolean update = false;
			if (oldAttr != null && oldAttr.getNodeType() == ICSSNode.ATTR_NODE && ((CSSAttrImpl) oldAttr).getName().equals(ICSSImportRule.HREF)) {
				update = true;
			}
			if (newAttr != null && newAttr.getNodeType() == ICSSNode.ATTR_NODE && ((CSSAttrImpl) newAttr).getName().equals(ICSSImportRule.HREF)) {
				update = true;
			}
			if (update)
				((ICSSImportRule) parentNode).getStyleSheet();
		}
	}

	/**
	 * 
	 */
	public void beginRecording(Object requester, String label, String description) {
		getStyleNotifier().beginRecording();

		Node node = getOwnerDOMNode();
		if (node != null && node instanceof IDOMNode) {
			IStructuredModel model = ((IDOMNode) node).getModel();
			if (model != null) {
				model.beginRecording(requester, label, description);
				return;
			}
		}
		super.beginRecording(requester, label, description);
	}

	void childReplaced(CSSNodeImpl parentNode, CSSNodeImpl newChild, CSSNodeImpl oldChild) {
		if (!fStructuredDocumentUpdate) {
			CSSModelUpdater updater = getUpdater();
			updater.childReplaced(parentNode, newChild, oldChild);
		}

		// always check and send selector event
		ICSSSelector removed[] = null, added[] = null;
		if (parentNode.getNodeType() == ICSSNode.STYLESHEET_NODE || parentNode.getNodeType() == ICSSNode.MEDIARULE_NODE) {
			// collect old selectors
			SelectorsCollector selTrav = new SelectorsCollector();
			selTrav.apply(oldChild);
			int nSel = selTrav.getSelectors().size();
			if (nSel > 0) {
				removed = new ICSSSelector[nSel];
				for (int i = 0; i < nSel; i++)
					removed[i] = (ICSSSelector) selTrav.getSelectors().get(i);
			}
			// collect new selectors
			selTrav = new SelectorsCollector();
			selTrav.apply(newChild);
			nSel = selTrav.getSelectors().size();
			if (nSel > 0) {
				added = new ICSSSelector[nSel];
				for (int i = 0; i < nSel; i++)
					added[i] = (ICSSSelector) selTrav.getSelectors().get(i);
			}
		}
		else {
			// modification
			ICSSNode rule = parentNode;
			while (rule != null) {
				if (rule instanceof ICSSStyleRule)
					break;
				rule = rule.getParentNode();
			}
			if (rule != null) {
				ICSSSelectorList list = ((ICSSStyleRule) rule).getSelectors();
				added = new ICSSSelector[list.getLength()];
				for (int i = 0; i < list.getLength(); i++)
					added[i] = list.getSelector(i);
			}
		}
		if (removed != null || added != null || getDocument().getNodeType() == ICSSNode.STYLEDECLARATION_NODE) {
			// send selector changed event
			getStyleNotifier().fire(removed, added, null);
		}
		// close removed import-rule's external style sheets
		{
			ImportRuleCollector trav = new ImportRuleCollector();
			trav.apply(oldChild);
			Iterator it = trav.getRules().iterator();
			while (it.hasNext()) {
				((CSSImportRuleImpl) it.next()).closeStyleSheet();
			}
		}
		// send events to listener for new import-rules
		if (getStyleListeners() != null && getStyleListeners().size() > 0) {
			ImportedCollector trav = new ImportedCollector();
			trav.apply(newChild);
		}
	}

	/**
	 * 
	 */
	private void closeImported() {
		if (!isShared()) {
			// release listeners
			if (getStyleListeners() != null) {
				Vector toRemove = new Vector(getStyleListeners());
				Iterator it = toRemove.iterator();
				while (it.hasNext()) {
					removeStyleListener((ICSSStyleListener) it.next());
				}
			}
			// close import rules
			ImportRuleCollector trav = new ImportRuleCollector();
			trav.apply(getDocument());
			Iterator it2 = trav.getRules().iterator();
			while (it2.hasNext()) {
				((CSSImportRuleImpl) it2.next()).releaseRule();
			}
		}
	}

	private CSSDocumentImpl createDocument() {
		CSSDocumentImpl doc = null;
		int parserMode = CSSSourceParser.MODE_STYLESHEET;
		if (ownerNode == null) {
			// this case is external CSS file
			doc = (CSSStyleSheetImpl) DOMCSSImpl.createCSSStyleSheet(null, null); // parameters
			// are
			// for
			// STYLE-tag
			parserMode = CSSSourceParser.MODE_STYLESHEET;
		}
		else if (ownerNode instanceof org.w3c.dom.Element && ((Element) ownerNode).getTagName().toUpperCase().equals("STYLE")) {//$NON-NLS-1$
			// this case is STYLE-tag
			Element style = (Element) ownerNode;
			doc = (CSSStyleSheetImpl) DOMCSSImpl.createCSSStyleSheet(style.getAttribute("TITLE"), //$NON-NLS-1$
						style.getAttribute("MEDIA"));//$NON-NLS-1$
			parserMode = CSSSourceParser.MODE_STYLESHEET;
		}
		else if (ownerNode instanceof org.w3c.dom.Element || ownerNode instanceof org.w3c.dom.Attr) {
			// Inline attributes
			doc = (CSSStyleDeclarationImpl) DOMCSSImpl.createCSSStyleDeclaration();
			parserMode = CSSSourceParser.MODE_DECLARATION;
		}
		RegionParser regionParser = getStructuredDocument().getParser();
		if (regionParser instanceof CSSSourceParser) {
			((CSSSourceParser) regionParser).setParserMode(parserMode);
		}
		return doc;
	}

	/**
	 * 
	 */
	public void endRecording(Object requester) {
		Node node = getOwnerDOMNode();
		if (node != null && node instanceof IDOMNode) {
			IStructuredModel model = ((IDOMNode) node).getModel();
			if (model != null) {
				model.endRecording(requester);
				return;
			}
		}
		super.endRecording(requester);

		getStyleNotifier().endRecording();
	}

	public ICSSDocument getDocument() {
		if (document == null) {
			this.document = createDocument();
			this.document.setModel(this);
		}
		return this.document;
	}

	public IStructuredDocument getStructuredDocument() {
		IStructuredDocument structuredDocument = null;
		structuredDocument = super.getStructuredDocument();
		if (structuredDocument != null)
			return structuredDocument;

		// the first time
		Assert.isNotNull(getModelHandler());
		structuredDocument = (IStructuredDocument) getModelHandler().getDocumentLoader().createNewStructuredDocument();

		setStructuredDocument(structuredDocument);
		return structuredDocument;
	}

	/**
	 * getNode method comment.
	 */
	public IndexedRegion getIndexedRegion(int offset) {
		if (getDocument() == null)
			return null;
		return ((CSSStructuredDocumentRegionContainer) getDocument()).getContainerNode(offset);
	}

	/**
	 * @return org.w3c.dom.Node
	 */
	public Node getOwnerDOMNode() {
		return ownerNode;
	}

	/**
	 * @param ownerNode
	 *            org.w3c.dom.Node if the case of external CSS model, you
	 *            should set null, else if internal css, you should set
	 *            STYLE-tag node, else if inline css, you should set the
	 *            element that is the owner of style-attribute.
	 */
	public void setOwnerDOMNode(Node node) {
		// prohibit owner change
		Assert.isTrue(ownerNode == null);
		ownerNode = node;
		if (ownerNode != null) { // for internal/inline CSS context
			try {
				setId(ID_NON_EXTERNAL_CSS);
			}
			catch (ResourceInUse e) {
				// impossible
			}
		}
	}

	/**
	 * currently public but may be made default access protected in future.
	 */
	protected CSSModelParser getParser() {
		if (fParser == null) {
			if (getDocument() != null) {
				fParser = new CSSModelParser(document);
			}
		}
		return fParser;
	}

	/**
	 * @return java.util.List
	 */
	public List getStyleListeners() {
		return getStyleNotifier().getStyleListeners();
	}

	/**
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.Object getStyleSheetType() {
		if (getDocument() instanceof ICSSStyleDeclaration)
			return INLINE;
		if (getOwnerDOMNode() != null)
			return EMBEDDED;
		else
			return EXTERNAL;
	}

	private CSSStyleNotifyAdapter getStyleNotifier() {
		if (styleNotifier == null) {
			styleNotifier = (ownerNode != null) ? new CSSEmbededStyleNotifyAdapter(this) : new CSSStyleNotifyAdapter(this);
		}
		return styleNotifier;
	}

	/**
	 * 
	 */
	private CSSModelUpdater getUpdater() {
		if (fUpdater == null) {
			fUpdater = new CSSModelUpdater(this);
			fUpdater.setParser(getParser());
		}
		return fUpdater;
	}

	/**
	 */
	public boolean isRecording() {
		return getStyleNotifier().isRecording();
	}

	/**
	 * This function returns true if there are other references to the
	 * underlying model.
	 */
	public boolean isShared() {
		return (getStyleSheetType() == EXTERNAL) && super.isShared();
	}

	public void newModel(NewDocumentEvent structuredDocumentEvent) {
		if (structuredDocumentEvent == null)
			return;
		IStructuredDocument structuredDocument = structuredDocumentEvent.getStructuredDocument();
		if (structuredDocument == null)
			return;
		// this should not happen, but for the case
		if (structuredDocument != getStructuredDocument())
			setStructuredDocument(structuredDocument);
		IStructuredDocumentRegionList flatNodes = structuredDocument.getRegionList();
		if (flatNodes == null)
			return;
		if (getDocument() == null)
			return;

		fStructuredDocumentUpdate = true;

		((CSSStructuredDocumentRegionContainer) getDocument()).removeChildNodes();

		CSSModelParser parser = getParser();
		parser.setStructuredDocumentEvent(structuredDocumentEvent);
		parser.replaceStructuredDocumentRegions(flatNodes, null);

		fStructuredDocumentUpdate = false;
	}

	/**
	 * noChange method comment.
	 */
	public void noChange(NoChangeEvent structuredDocumentEvent) {
		// nop
	}

	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
		if (structuredDocumentEvent == null) {
			return;
		}
		IStructuredDocumentRegionList oldStructuredDocumentRegions = structuredDocumentEvent.getOldStructuredDocumentRegions();
		IStructuredDocumentRegionList newStructuredDocumentRegions = structuredDocumentEvent.getNewStructuredDocumentRegions();
		if (oldStructuredDocumentRegions == null && newStructuredDocumentRegions == null) {
			return;
		}

		fStructuredDocumentUpdate = true;

		CSSModelParser parser = getParser();
		parser.setStructuredDocumentEvent(structuredDocumentEvent);
		if (structuredDocumentEvent.isEntireDocumentReplaced())
			parser.replaceDocument(newStructuredDocumentRegions);
		else
			parser.replaceStructuredDocumentRegions(newStructuredDocumentRegions, oldStructuredDocumentRegions);

		fStructuredDocumentUpdate = false;
	}

	/**
	 * cleanup -> rebuild CSS Nodes This is pre-beta fix for 178176.
	 */
	public void refreshNodes() {
		// cleanup old nodes
		fStructuredDocumentUpdate = true;
		((CSSStructuredDocumentRegionContainer) getDocument()).removeChildNodes();
		fStructuredDocumentUpdate = false;

		getParser().cleanupUpdateContext();

		IStructuredDocument structuredDocument = getStructuredDocument();
		String source = structuredDocument.getText();

		structuredDocument.replaceText(this, 0, source.length(), null);
		structuredDocument.replaceText(this, 0, 0, source);
	}

	public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
		if (structuredDocumentEvent == null) {
			return;
		}
		IStructuredDocumentRegion flatNode = structuredDocumentEvent.getStructuredDocumentRegion();
		if (flatNode == null) {
			return;
		}
		ITextRegion region = structuredDocumentEvent.getRegion();
		if (region == null) {
			return;
		}

		fStructuredDocumentUpdate = true;

		CSSModelParser parser = getParser();
		parser.setStructuredDocumentEvent(structuredDocumentEvent);
		parser.changeRegion(flatNode, region);

		fStructuredDocumentUpdate = false;
	}

	public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
		if (structuredDocumentEvent == null)
			return;
		IStructuredDocumentRegion flatNode = structuredDocumentEvent.getStructuredDocumentRegion();
		if (flatNode == null)
			return;
		ITextRegionList oldRegions = structuredDocumentEvent.getOldRegions();
		ITextRegionList newRegions = structuredDocumentEvent.getNewRegions();
		if (oldRegions == null && newRegions == null)
			return;

		fStructuredDocumentUpdate = true;

		CSSModelParser parser = getParser();
		parser.setStructuredDocumentEvent(structuredDocumentEvent);
		parser.replaceRegions(flatNode, newRegions, oldRegions);

		fStructuredDocumentUpdate = false;

	}

	/**
	 * 
	 */
	public void releaseFromEdit() {
		closeImported();
		if (getStyleSheetType() == EXTERNAL) {
			super.releaseFromEdit();
		}
	}

	/**
	 * 
	 */
	public void releaseFromRead() {
		closeImported();
		if (getStyleSheetType() == EXTERNAL) {
			super.releaseFromRead();
		}
	}

	/**
	 * @param listener
	 *            org.eclipse.wst.css.core.event.CSSStyleListener
	 */
	public void removeStyleListener(ICSSStyleListener listener) {
		getStyleNotifier().removeStyleListener(listener);
	}

	public void setStructuredDocument(IStructuredDocument newStructuredDocument) {
		IStructuredDocument oldStructuredDocument = super.getStructuredDocument();
		if (newStructuredDocument == oldStructuredDocument)
			return; // noting to do

		if (oldStructuredDocument != null)
			oldStructuredDocument.removeDocumentChangingListener(this);
		super.setStructuredDocument(newStructuredDocument);

		if (newStructuredDocument != null) {
			if (newStructuredDocument.getLength() > 0) {
				newModel(new NewDocumentEvent(newStructuredDocument, this));
			}
			newStructuredDocument.addDocumentChangingListener(this);
		}
	}

	/**
	 * @param srcModel
	 *            com.imb.sed.css.mode.intefaces.ICSSModel
	 * @param removed
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSSelector[]
	 * @param added
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSSelector[]
	 */
	public void styleChanged(ICSSModel srcModel, ICSSSelector[] removed, ICSSSelector[] added, String media) {
		getStyleNotifier().styleChanged(srcModel, removed, added, media);
	}

	/**
	 * @param srcModel
	 *            org.eclipse.wst.css.core.model.interfaces.ICSSModel
	 */
	public void styleUpdate(ICSSModel srcModel) {
		getStyleNotifier().styleUpdate(srcModel);
	}

	void valueChanged(CSSNodeImpl node, String oldValue) {
		if (!fStructuredDocumentUpdate) {
			CSSModelUpdater updater = getUpdater();
			updater.valueChanged(node, oldValue);
		}

		ICSSSelector removed[] = null, added[] = null;
		if (node != null) {
			if (node.getNodeType() == ICSSNode.ATTR_NODE && ((CSSAttrImpl) node).getName().equals(ICSSStyleRule.SELECTOR)) {
				CSSAttrImpl attr = (CSSAttrImpl) node;
				// collect changed selector
				ICSSSelectorList list = new CSSSelectorListImpl(attr.getValue());
				added = new ICSSSelector[list.getLength()];
				for (int i = 0; i < list.getLength(); i++)
					added[i] = list.getSelector(i);

				// get old value
				list = new CSSSelectorListImpl(oldValue);
				removed = new ICSSSelector[list.getLength()];
				for (int i = 0; i < list.getLength(); i++)
					removed[i] = list.getSelector(i);
			}
			else if (node instanceof ICSSValue) {
				ICSSNode rule = node;
				while (rule != null) {
					if (rule instanceof ICSSStyleRule)
						break;
					rule = rule.getParentNode();
				}
				if (rule != null) {
					ICSSSelectorList list = ((ICSSStyleRule) rule).getSelectors();
					added = new ICSSSelector[list.getLength()];
					for (int i = 0; i < list.getLength(); i++)
						added[i] = list.getSelector(i);
				}
			}
		}
		if (removed != null || added != null || getDocument().getNodeType() == ICSSNode.STYLEDECLARATION_NODE) {
			// send selector changed event
			getStyleNotifier().fire(removed, added, null);
		}
		// for href attribute
		if (getStyleListeners() != null && getStyleListeners().size() > 0) {
			if (node != null && node.getNodeType() == ICSSNode.ATTR_NODE && ((CSSAttrImpl) node).getName().equals(ICSSImportRule.HREF)) {
				((ICSSImportRule) ((ICSSAttr) node).getOwnerCSSNode()).getStyleSheet();
			}
		}
	}
}
