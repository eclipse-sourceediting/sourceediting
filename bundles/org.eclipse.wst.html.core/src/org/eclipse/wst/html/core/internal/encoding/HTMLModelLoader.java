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
package org.eclipse.wst.html.core.internal.encoding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.html.core.internal.document.DOMStyleModelImpl;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeAdapterFactory;
import org.eclipse.wst.html.core.internal.document.HTMLModelParserAdapterFactory;
import org.eclipse.wst.html.core.internal.htmlcss.HTMLStyleSelectorAdapterFactory;
import org.eclipse.wst.html.core.internal.htmlcss.StyleAdapterFactory;
import org.eclipse.wst.html.core.internal.modelquery.ModelQueryAdapterFactoryForHTML;
import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.model.AbstractModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.DebugAdapterFactory;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.propagate.PropagatingAdapterFactoryImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class HTMLModelLoader extends AbstractModelLoader {


	public HTMLModelLoader() {
		super();
	}

	/**
	 * Convenience method to add tag names using BlockMarker object
	 */
	protected void addHTMLishTag(XMLSourceParser parser, String tagname) {
		BlockMarker bm = new BlockMarker(tagname, null, DOMRegionContext.BLOCK_TEXT, false);
		parser.addBlockMarker(bm);
	}

	public IStructuredModel newModel() {
		DOMStyleModelImpl model = new DOMStyleModelImpl();
		return model;
	}

	/**
	 * This method must return those factories which must be attached to the
	 * structuredModel before content is applied.
	 */
	public List getAdapterFactories() {
		List result = new ArrayList();
		INodeAdapterFactory factory = null;
		factory = StyleAdapterFactory.getInstance();
		result.add(factory);
		factory = HTMLStyleSelectorAdapterFactory.getInstance();
		result.add(factory);
		factory = new HTMLDocumentTypeAdapterFactory();
		result.add(factory);
		factory = HTMLModelParserAdapterFactory.getInstance();
		result.add(factory);
		//
		factory = new ModelQueryAdapterFactoryForHTML();
		result.add(factory);

		factory = new PropagatingAdapterFactoryImpl();
		result.add(factory);
		

		return result;
	}

	protected void preLoadAdapt(IStructuredModel structuredModel) {
		super.preLoadAdapt(structuredModel);
		// DMW: just added this preload on 8/16/2002
		// I noticed the ProagatingAdapterFactory was being added,
		// that that the ProagatingAdapterAdapter was not being
		// preload adapted -- I'm assuing it ALWAYS has to be.
		IDOMModel domModel = (IDOMModel) structuredModel;
		// if there is a model in the adapter, this will adapt it to
		// first node. After that the PropagatingAdater spreads over the
		// children being
		// created. Each time that happends, a side effect is to
		// also "spread" sprecific registered adapters,
		// they two can propigate is needed.
		((INodeNotifier) domModel.getDocument()).getAdapterFor(PropagatingAdapter.class);
		if (Debug.debugNotificationAndEvents) {
			PropagatingAdapter propagatingAdapter = (PropagatingAdapter) ((INodeNotifier) domModel.getDocument()).getAdapterFor(PropagatingAdapter.class);
			propagatingAdapter.addAdaptOnCreateFactory(new DebugAdapterFactory());
		}	}

	public IModelLoader newInstance() {
		return new HTMLModelLoader();
	}

	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new HTMLDocumentLoader();
		}
		return documentLoaderInstance;
	}
}
