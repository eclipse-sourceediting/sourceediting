/*******************************************************************************
 * Copyright (c) 2004-2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * 		Masaki Saitoh (MSAITOH@jp.ibm.com)
 *		See Bug 153000  Style Adapters should be lazier
 *		https://bugs.eclipse.org/bugs/show_bug.cgi?id=153000
 *
 ********************************************************************************/
package org.eclipse.wst.html.core.internal.htmlcss;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.wst.css.core.internal.event.ICSSStyleListener;
import org.eclipse.wst.css.core.internal.provisional.adapters.IModelProvideAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.util.ImportedCollector;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 */
public abstract class AbstractStyleSheetAdapter extends AbstractCSSModelAdapter implements ICSSStyleListener, IStyleSheetAdapter {

	// this variable to hold the class is just a VAJava trick.
	// it improves performance in VAJava by minimizing class loading.
	private final Class StyleSheetAdapterClass = IStyleSheetAdapter.class;
	private Collection styleChangedNodes;

	/**
	 */
	protected AbstractStyleSheetAdapter() {
		super();
	}

	/**
	 */
	protected ICSSModel createModel() {
		return createModel(true);
	}

	/**
	 */
	protected ICSSModel createModel(boolean notify) {
		ICSSModel newModel = super.createModel();
		if (notify && newModel != null) {
			// get ModelProvideAdapter
			IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);
			// notify adapter
			if (adapter != null)
				adapter.modelProvided(newModel);
		}
		return newModel;
	}

	/**
	 */
	public StyleSheet getSheet() {
		ICSSModel model = getModel();
		if (model == null)
			return null;
		return (StyleSheet) model.getDocument();
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return (type == StyleSheetAdapterClass);
	}

	/**
	 */
	public void released() {
		ICSSModel currentModel = getModel();

		// get ModelProvideAdapter
		IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);

		setElement(null);
		setModel(null);

		if (adapter != null)
			adapter.modelReleased(currentModel);

		if (currentModel != null)
			currentModel.releaseFromRead();
	}

	/**
	 */
	public void removed() {
		ICSSModel currentModel = getModel();

		setModel(null);

		// get ModelProvideAdapter
		IModelProvideAdapter adapter = (IModelProvideAdapter) ((INodeNotifier) getElement()).getAdapterFor(IModelProvideAdapter.class);
		if (adapter != null)
			adapter.modelRemoved(currentModel);

		if (currentModel != null)
			currentModel.releaseFromRead();
	}

	/**
	 * @param srcModel com.ibm.sed.css.model.interfaces.ICSSModel
	 * @param removed com.ibm.sed.css.model.interfaces.ICSSSelector[]
	 * @param added com.ibm.sed.css.model.interfaces.ICSSSelector[]
	 * @param media java.lang.String
	 */
	public void styleChanged(ICSSModel srcModel, ICSSSelector[] removed, ICSSSelector[] added, String media) {
		Element element = getElement();
		if (element == null)
			return; // might released
		Document doc = element.getOwnerDocument();
		if (doc == null)
			return; // error

		// to notify GEF tree 
		if (doc instanceof INodeNotifier) {
			Collection adapters = ((INodeNotifier) doc).getAdapters();
			if (adapters == null)
				return;
			Iterator it = adapters.iterator();
			if (it == null)
				return;
			while (it.hasNext()) {
				INodeAdapter adapter = (INodeAdapter) it.next();
				if (adapter instanceof ICSSStyleListener) {
					((ICSSStyleListener) adapter).styleChanged(srcModel, removed, added, media);
				}
			}
		}
		//

		if (styleChangedNodes == null) {
			styleChangedNodes = new HashSet();
		}

		try {
			int removedSelNum = removed != null ? removed.length : 0;
			int addedSelNum = added != null ? added.length : 0;

			NodeIterator iter = ((DocumentTraversal) doc).createNodeIterator(doc, NodeFilter.SHOW_ELEMENT, null, true);
			Node node;
			while ((node = iter.nextNode()) != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elm = (Element) node;
					boolean match = false;
					int i;
					for (i = 0; i < removedSelNum && !match; i++) {
						match = removed[i].match(elm, null);
					}
					for (i = 0; i < addedSelNum && !match; i++) {
						match = added[i].match(elm, null);
					}
					if (match) {
						if (!styleChangedNodes.contains(elm))
							styleChangedNodes.add(elm);
						// notifyStyleChanged(elm);
					}
				}
			}
		}
		catch (ClassCastException ex) {
			// Document doesn't implement DocumentTraversal...
		}

	}

	/**
	 * @param srcModel com.ibm.sed.css.model.interfaces.ICSSModel
	 */
	public void styleUpdate(ICSSModel srcModel) {
		IDOMNode node = (IDOMNode) getElement();
		if (node == null)
			return;
		IDOMModel model = node.getModel();
		if (model == null)
			return;
		XMLModelNotifier notifier = model.getModelNotifier();
		if (notifier == null)
			return;

		// before updating, all sub-models should be loaded!
		DocumentStyle document = (DocumentStyle) model.getDocument();
		StyleSheetList styles = document.getStyleSheets();
		if (styles != null) {
			int n = styles.getLength();
			ImportedCollector trav = new ImportedCollector();
			for (int i = 0; i < n; i++) {
				org.w3c.dom.stylesheets.StyleSheet sheet = styles.item(i);
				if (sheet instanceof ICSSNode)
					trav.apply((ICSSNode) sheet);
			}
		}

		// flash style changed events
		if (styleChangedNodes != null) {
			Object[] elements = styleChangedNodes.toArray();
			for (int i = 0; elements != null && i < elements.length; i++)
				notifyStyleChanged((Element) elements[i]);
			styleChangedNodes.clear();
		}

		// to notify GEF tree 
		if (document instanceof INodeNotifier) {
			Collection adapters = ((INodeNotifier) document).getAdapters();
			if (adapters == null)
				return;
			Iterator it = adapters.iterator();
			if (it == null)
				return;
			while (it.hasNext()) {
				INodeAdapter adapter = (INodeAdapter) it.next();
				if (adapter instanceof ICSSStyleListener) {
					((ICSSStyleListener) adapter).styleUpdate(srcModel);
				}
			}
		}

		notifier.propertyChanged(node);
	}
}