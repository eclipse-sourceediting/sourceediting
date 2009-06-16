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
package org.eclipse.wst.html.core.internal.modelhandler;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.document.ModelParserAdapter;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class EmbeddedHTML implements EmbeddedTypeHandler {

	public String ContentTypeID_EmbeddedHTML = "org.eclipse.wst.html.core.internal.contenttype.EmbeddedHTML"; //$NON-NLS-1$
	private List supportedMimeTypes;

	/**
	 * Constructor for EmbeddedHTML.
	 */
	public EmbeddedHTML() {
		super();
	}

	/**
	 * Convenience method to add tag names using BlockMarker object
	 */
	private void addHTMLishTag(BlockTagParser parser, String tagname) {
		BlockMarker bm = new BlockMarker(tagname, null, DOMRegionContext.BLOCK_TEXT, false);
		parser.addBlockMarker(bm);
	}

	/**
	 * @see EmbeddedContentType#getFamilyId()
	 */
	public String getFamilyId() {
		return ModelHandlerForHTML.AssociatedContentTypeID;
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
	public void initializeParser(RegionParser parser) {
		if (parser instanceof BlockTagParser) {
			addHTMLishTag((BlockTagParser) parser, "script"); //$NON-NLS-1$
			addHTMLishTag((BlockTagParser) parser, "style"); //$NON-NLS-1$
		}
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
		if (!registry.contains(DocumentTypeAdapter.class)) {
			factory = new HTMLDocumentTypeAdapterFactory();
			registry.addFactory(factory);
		}
		if (!registry.contains(ModelParserAdapter.class)) {
			factory = HTMLModelParserAdapterFactory.getInstance();
			registry.addFactory(factory);
		}
		if (!registry.contains(IStyleSelectorAdapter.class)) {

			factory = HTMLStyleSelectorAdapterFactory.getInstance();
			registry.addFactory(factory);
		}
		if (!registry.contains(IStyleSheetAdapter.class)) {

			factory = StyleAdapterFactory.getInstance();
			registry.addFactory(factory);
		}

	}

	public void uninitializeFactoryRegistry(FactoryRegistry registry) {
		Assert.isNotNull(registry);

		// ISSUE: should these factories be released? Or just 
		// removed from this registry, because we are getting ready to
		// re-add them?
		INodeAdapterFactory factory = null;
		if (!registry.contains(DocumentTypeAdapter.class)) {
			factory = registry.getFactoryFor(DocumentTypeAdapter.class);
			factory.release();
			registry.removeFactory(factory);
		}
		if (!registry.contains(ModelParserAdapter.class)) {
			factory = registry.getFactoryFor(ModelParserAdapter.class);
			factory.release();
			registry.removeFactory(factory);
		}
		if (!registry.contains(IStyleSelectorAdapter.class)) {
			factory = registry.getFactoryFor(IStyleSelectorAdapter.class);
			factory.release();
			registry.removeFactory(factory);
		}
		if (!registry.contains(IStyleSheetAdapter.class)) {
			factory = registry.getFactoryFor(IStyleSheetAdapter.class);
			factory.release();
			registry.removeFactory(factory);
		}

	}

	public void uninitializeParser(RegionParser parser) {
		// I'm assuming block markers are unique based on name only
		// we add these as full BlockMarkers, but remove based on name alone.
		if (parser instanceof BlockTagParser) {
			((BlockTagParser) parser).removeBlockMarker("style"); //$NON-NLS-1$
			((BlockTagParser) parser).removeBlockMarker("script"); //$NON-NLS-1$
		}
	}

	public EmbeddedTypeHandler newInstance() {
		return new EmbeddedHTML();
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
