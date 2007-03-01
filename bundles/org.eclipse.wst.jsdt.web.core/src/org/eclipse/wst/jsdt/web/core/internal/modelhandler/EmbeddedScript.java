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
package org.eclipse.wst.jsdt.web.core.internal.modelhandler;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapterFactory;
//import org.eclipse.wst.jsdt.web.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.wst.html.core.internal.modelhandler.ModelHandlerForHTML;

import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;


import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSelectorAdapter;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleSheetAdapter;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeAdapterFactory;
import org.eclipse.wst.html.core.internal.document.HTMLModelParserAdapterFactory;
import org.eclipse.wst.html.core.internal.htmlcss.HTMLStyleSelectorAdapterFactory;
import org.eclipse.wst.html.core.internal.htmlcss.StyleAdapterFactory;
import org.eclipse.wst.html.core.internal.modelquery.ModelQueryAdapterFactoryForEmbeddedHTML;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTagParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.JSPCapableParser;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.util.Assert;

import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.document.ModelParserAdapter;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class EmbeddedScript implements EmbeddedTypeHandler {

	public String ContentTypeID_EmbeddedHTML = "org.eclipse.wst.html.core.internal.contenttype.EmbeddedHTML"; //$NON-NLS-1$
	private List supportedMimeTypes;
	static String AssociatedContentTypeID = "org.eclipse.wst.html.core.htmlsource"; //$NON-NLS-1$
	/**
	 * Constructor for EmbeddedHTML.
	 */
	public EmbeddedScript() {
		super();
	}

	/**
	 * @see EmbeddedContentType#getFamilyId()
	 */
	public String getFamilyId() {
		return AssociatedContentTypeID;
	}

	/*
	 * Only "model side" embedded factories can be added here.
	 */
	public List getAdapterFactories() {
		List factories = new ArrayList();
		factories.add(new ModelQueryAdapterFactoryForEmbeddedHTML());
		// factories.addAll(PluginContributedFactoryReader.getInstance().getFactories(this));
		return factories;
	}

	/*
	 * @see EmbeddedContentType#initializeParser(RegionParser)
	 */
	public void initializeParser(JSPCapableParser parser) {
		
	}

	public List getSupportedMimeTypes() {
		if (supportedMimeTypes == null) {
			supportedMimeTypes = new ArrayList();
			supportedMimeTypes.add("text/html"); //$NON-NLS-1$
			supportedMimeTypes.add("text/xhtml"); //$NON-NLS-1$
			supportedMimeTypes.add("application/xhtml+xml"); //$NON-NLS-1$
			supportedMimeTypes.add("text/vnd.wap.wml"); //$NON-NLS-1$
		}
		return supportedMimeTypes;
	}

	public void initializeFactoryRegistry(FactoryRegistry registry) {
		Assert.isNotNull(registry);
		INodeAdapterFactory factory = null;
		factory = registry.getFactoryFor(IJSPTranslation.class);
		if (factory == null) {
			factory = new JSPTranslationAdapterFactory();
			registry.addFactory(factory);
		}

	}

	public void uninitializeFactoryRegistry(FactoryRegistry registry) {
		Assert.isNotNull(registry);

		// ISSUE: should these factories be released? Or just 
		// removed from this registry, because we are getting ready to
		// re-add them?
		INodeAdapterFactory factory = null;
		if (!registry.contains(IJSPTranslation.class)) {
			factory = registry.getFactoryFor(IJSPTranslation.class);
			factory.release();
			registry.removeFactory(factory);
		}
	}

	public void uninitializeParser(JSPCapableParser parser) {
		
	}

	public EmbeddedTypeHandler newInstance() {
		return new EmbeddedScript();
	}

	/**
	 * will someday be controlled via extension point
	 */
	public boolean isDefault() {
		return true;
	}

	public boolean canHandleMimeType(String mimeType) {
		return getSupportedMimeTypes().contains(mimeType);
	}
}