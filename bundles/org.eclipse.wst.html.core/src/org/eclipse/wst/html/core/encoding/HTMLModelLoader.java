/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.encoding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.html.core.document.HTMLDocumentTypeAdapterFactory;
import org.eclipse.wst.html.core.document.HTMLModelParserAdapterFactory;
import org.eclipse.wst.html.core.document.XMLStyleModelImpl;
import org.eclipse.wst.html.core.htmlcss.HTMLStyleSelectorAdapterFactory;
import org.eclipse.wst.html.core.htmlcss.StyleAdapterFactory;
import org.eclipse.wst.html.core.modelquery.ModelQueryAdapterFactoryForHTML;
import org.eclipse.wst.sse.core.AbstractModelLoader;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IModelLoader;
import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.parser.BlockMarker;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.DebugAdapterFactory;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.propagate.PropagatingAdapterFactoryImpl;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;

public class HTMLModelLoader extends AbstractModelLoader {


	public HTMLModelLoader() {
		super();
	}

	/**
	 * Convenience method to add tag names using BlockMarker object
	 */
	protected void addHTMLishTag(XMLSourceParser parser, String tagname) {
		BlockMarker bm = new BlockMarker(tagname, null, XMLRegionContext.BLOCK_TEXT, false);
		parser.addBlockMarker(bm);
	}

	public IStructuredModel newModel() {
		XMLStyleModelImpl model = new XMLStyleModelImpl();
		return model;
	}

	/**
	 * This method must return those factories which must be attached to the
	 * structuredModel before content is applied.
	 */
	public List getAdapterFactories() {
		List result = new ArrayList();
		IAdapterFactory factory = null;
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