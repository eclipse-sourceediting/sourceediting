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
package org.eclipse.wst.html.core.internal.htmlcss;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetListAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.util.CSSClassTraverser;
import org.eclipse.wst.css.core.internal.util.ImportRuleCollector;
import org.eclipse.wst.html.core.internal.contentmodel.JSP11Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;



/**
 */
public class HTMLDocumentAdapter implements IStyleSheetListAdapter, StyleSheetList {

	private Document document = null;
	private Vector styleAdapters = null;
	private Vector oldStyleAdapters = null;

	/**
	 */
	HTMLDocumentAdapter() {
		super();
	}

	/**
	 */
	private void addStyleSheet(Element node) {
		IDOMElement element = (IDOMElement) node;
		String tagName = element.getTagName();
		if (tagName == null)
			return;
		boolean isContainer = false;
		if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.HTML) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.HEAD) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.NOSCRIPT) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.BASE) || tagName.equalsIgnoreCase(JSP11Namespace.ElementName.ROOT) || (!element.isGlobalTag() && element.isContainer())) {
			isContainer = true;
		}
		else if (element.isCommentTag()) {
			Node parent = element.getParentNode();
			if (parent == element.getOwnerDocument()) {
				// This condition is too severe, actually do not work for JSF template.
				// But above (! globalTag() && isContainer()) cover JSF template + tpl template
				isContainer = true;
			}
			else if (parent.getNodeType() == Node.ELEMENT_NODE) {
				tagName = ((Element) parent).getTagName();
				if (tagName != null && tagName.equalsIgnoreCase(HTML40Namespace.ElementName.HEAD)) {
					isContainer = true;
				}
			}
		}

		else {
			String localName = element.getLocalName();
			if (localName != null && localName.equalsIgnoreCase(HTML40Namespace.ElementName.HTML)) {
				// taglib html tag
				isContainer = true;
			}
			else {
				INodeNotifier notifier = element;
				INodeAdapter adapter = notifier.getAdapterFor(IStyleSheetAdapter.class);
				if (adapter != null && adapter instanceof IStyleSheetAdapter) {
					this.styleAdapters.addElement(adapter);
				}
			}
		}
		if (isContainer) {
			INodeNotifier notifier = element;
			if (notifier.getExistingAdapter(IStyleSheetListAdapter.class) == null) {
				notifier.addAdapter(this);
			}
			for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				addStyleSheet((Element) child);
			}
		}
	}

	/**
	 */
	void childReplaced() {
		if (this.styleAdapters == null)
			return;

		// backup old adapters to be released on updating in getStyleSheets()
		this.oldStyleAdapters = this.styleAdapters;
		// invalidate the list
		this.styleAdapters = null;

		notifyStyleSheetsChanged(this.document);
	}

	/**
	 */
	public Enumeration getClasses() {
		StyleSheetList sheetList = getStyleSheets();
		int nSheets = sheetList.getLength();

		final ArrayList classes = new ArrayList();

		CSSClassTraverser traverser = new CSSClassTraverser();
		traverser.setTraverseImported(true);

		for (int i = 0; i < nSheets; i++) {
			org.w3c.dom.stylesheets.StyleSheet sheet = sheetList.item(i);
			if (sheet instanceof ICSSNode) {
				traverser.apply((ICSSNode) sheet);
			}
		}
		classes.addAll(traverser.getClassNames());

		return new Enumeration() {
			int i = 0;

			public boolean hasMoreElements() {
				return i < classes.size();
			}

			public Object nextElement() {
				return classes.get(i++);
			}
		};
	}

	/**
	 */
	private List getValidAdapters() {
		Vector validAdapters = new Vector();
		if (this.styleAdapters != null) {
			Iterator i = this.styleAdapters.iterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (obj instanceof AbstractStyleSheetAdapter && ((AbstractStyleSheetAdapter) obj).isValidAttribute()) {
					validAdapters.add(obj);
				}
			}
		}
		return validAdapters;
	}

	/**
	 */
	public int getLength() {
		return getValidAdapters().size();
	}

	/**
	 */
	public CSSStyleDeclaration getOverrideStyle(Element element, String pseudoName) {
		StyleSheetList ssl = getStyleSheets();
		int numStyles = ssl.getLength();

		CSSQueryTraverser query = new CSSQueryTraverser();
		query.setTraverseImported(true);
		query.setTraverseImportFirst(true);
		query.setElement(element, pseudoName);

		for (int i = 0; i < numStyles; i++) {
			// loop for styles (<style> and <link>)
			org.w3c.dom.stylesheets.StyleSheet ss = ssl.item(i);

			try {
				query.apply((ICSSNode) ss);
			}
			catch (ClassCastException ex) {
				// I can handle only CSS style
			}
		}

		return query.getDeclaration();
	}

	/**
	 */
	public StyleSheetList getStyleSheets() {
		if (this.styleAdapters == null) {
			if (this.document == null)
				return null;

			this.styleAdapters = new Vector();
			for (Node child = this.document.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				addStyleSheet((Element) child);
			}

			removeOldStyleSheets();
		}
		return this;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return (type == IStyleSheetListAdapter.class);
	}

	/**
	 */
	public StyleSheet item(int index) {
		if (this.styleAdapters == null)
			return null;

		List validAdapters = getValidAdapters();

		if (index < 0 || index >= validAdapters.size())
			return null;
		StyleSheet sheet = ((IStyleSheetAdapter) validAdapters.get(index)).getSheet();
		if (sheet == null) {// for LINK element whose link is broken
			ICSSModel model = ((AbstractStyleSheetAdapter) validAdapters.get(index)).createModel();
			sheet = ((model != null) ? (StyleSheet) model.getDocument() : null);
		}
		return sheet;
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		Node node = null;
		switch (eventType) {
			case INodeNotifier.ADD :
				if (newValue instanceof Node)
					node = (Node) newValue;
				else if (changedFeature instanceof Attr)
					node = ((Attr) changedFeature);
				break;
			case INodeNotifier.REMOVE :
				if (oldValue instanceof Node)
					node = (Node) oldValue;
				else if (changedFeature instanceof Attr)
					node = ((Attr) changedFeature);
				break;
			case INodeNotifier.CHANGE :
				node = (Node) notifier;
				break;
			default :
				break;
		}
		
		if (node == null)
			return;
		
		switch(node.getNodeType()) {
			case Node.ELEMENT_NODE: {
				IDOMElement element = (IDOMElement) node;
				String tagName = element.getTagName();
				if (tagName == null)
					return;

				if (eventType == INodeNotifier.CHANGE) {
					if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.BASE)) {
						refreshAdapters();
					}
				}
				else {
					if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.HTML) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.HEAD) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.STYLE) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.LINK) || tagName.equalsIgnoreCase(HTML40Namespace.ElementName.NOSCRIPT) || tagName.equalsIgnoreCase(JSP11Namespace.ElementName.ROOT) || element.isCommentTag() || (!element.isGlobalTag() && element.isContainer())) {
						childReplaced();
					}
					else if (tagName.equalsIgnoreCase(HTML40Namespace.ElementName.BASE)) {
						refreshAdapters();
					}
					else {
						String localName = element.getLocalName();
						if (localName != null && localName.equalsIgnoreCase(HTML40Namespace.ElementName.HTML)) {
							// taglib html tag
							childReplaced();
						}
					}
				}
				break;
			}
			case Node.ATTRIBUTE_NODE : {
				if (HTML40Namespace.ElementName.BASE.equals(((Attr) node).getOwnerElement().getLocalName())) {
					refreshAdapters();
				}
			}
		}
		
	}

	/**
	 * reload LINK / @import if BASE changed
	 */
	private void refreshAdapters() {
		Iterator iAdapter = this.styleAdapters.iterator();
		while (iAdapter.hasNext()) {
			Object adapter = iAdapter.next();
			if (adapter instanceof LinkElementAdapter) {
				((LinkElementAdapter) adapter).refreshSheet();
			}
			else if (adapter instanceof StyleElementAdapter) {
				ICSSModel model = ((StyleElementAdapter) adapter).getModel();
				ImportRuleCollector trav = new ImportRuleCollector();
				trav.apply(model);
				Iterator iRule = trav.getRules().iterator();
				while (iRule.hasNext()) {
					ICSSImportRule rule = (ICSSImportRule) iRule.next();
					rule.refreshStyleSheet();
				}
			}
		}
	}

	/**
	 */
	private void notifyStyleSheetsChanged(Document target) {
		INodeNotifier notifier = (INodeNotifier) target;
		if (notifier == null)
			return;
		Collection adapters = notifier.getAdapters();
		if (adapters == null)
			return;
		Iterator it = adapters.iterator();
		if (it == null)
			return;
		while (it.hasNext()) {
			INodeAdapter adapter = (INodeAdapter) it.next();
			if (adapter instanceof StyleListener) {
				StyleListener listener = (StyleListener) adapter;
				listener.styleChanged();
			}
		}
	}

	/**
	 */
	private void releaseOldStyleSheets() {
		if (this.oldStyleAdapters == null)
			return;
		Iterator it = this.oldStyleAdapters.iterator();
		while (it.hasNext()) {
			IStyleSheetAdapter adapter = (IStyleSheetAdapter) it.next();
			if (adapter == null)
				continue;
			// if the same adapter is in the current list,
			// do not release
			if (this.styleAdapters != null && this.styleAdapters.contains(adapter))
				continue;
			adapter.released();
		}
		this.oldStyleAdapters = null;
	}

	/**
	 */
	public void releaseStyleSheets() {
		releaseOldStyleSheets();

		if (this.styleAdapters == null)
			return;
		Iterator it = this.styleAdapters.iterator();
		while (it.hasNext()) {
			IStyleSheetAdapter adapter = (IStyleSheetAdapter) it.next();
			if (adapter != null)
				adapter.released();
		}
		this.styleAdapters = null;
	}

	/**
	 */
	private void removeOldStyleSheets() {
		if (this.oldStyleAdapters == null)
			return;
		Iterator it = this.oldStyleAdapters.iterator();
		while (it.hasNext()) {
			IStyleSheetAdapter adapter = (IStyleSheetAdapter) it.next();
			if (adapter == null)
				continue;
			// if the same adapter is in the current list,
			// do not release
			if (this.styleAdapters != null && this.styleAdapters.contains(adapter))
				continue;
			adapter.removed();
		}
		this.oldStyleAdapters = null;
	}

	/**
	 */
	void setDocument(Document document) {
		this.document = document;
	}
}
