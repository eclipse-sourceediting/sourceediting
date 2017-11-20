/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.modelhandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryAdapterFactoryForEmbeddedXML;


public class EmbeddedXML implements EmbeddedTypeHandler {

	private static List supportedMimeTypes;
	public String ContentTypeID_EmbeddedXML = "org.eclipse.wst.xml.core.contenttype.EmbeddedXML"; //$NON-NLS-1$

	/**
	 * Constructor for EmbeddedXML.
	 */
	public EmbeddedXML() {
		super();
	}

	/*
	 * @see EmbeddedContentType#getAdapterFactories()
	 */
	public List getAdapterFactories() {
		List factories = new ArrayList();
		factories.add(new ModelQueryAdapterFactoryForEmbeddedXML());
		// factories.addAll(PluginContributedFactoryReader.getInstance().getFactories(this));
		return factories;
	}

	/**
	 * @see EmbeddedContentType#getFamilyId()
	 */
	public String getFamilyId() {
		return ModelHandlerForXML.AssociatedContentTypeID;
	}

	public List getSupportedMimeTypes() {
		if (supportedMimeTypes == null) {
			supportedMimeTypes = new ArrayList();
			supportedMimeTypes.add("application/xml"); //$NON-NLS-1$
			supportedMimeTypes.add("text/xml"); //$NON-NLS-1$
		}
		return supportedMimeTypes;
	}

	public void initializeFactoryRegistry(FactoryRegistry registry) {
		//TODO: initialize
	}

	public void initializeParser(RegionParser parser) {
		// nothing to initialize for "pure" XML
		// compare with XHTML
	}

	public boolean isDefault() {
		return false;
	}

	public EmbeddedTypeHandler newInstance() {
		return new EmbeddedXML();
	}

	public void uninitializeFactoryRegistry(FactoryRegistry registry) {
		// TODO: need to undo anything we did in initialize

	}

	public void uninitializeParser(RegionParser parser) {
		// need to undo anything we did in initialize
	}

	public boolean canHandleMimeType(String mimeType) {
		boolean canHandle = getSupportedMimeTypes().contains(mimeType);
		if(!canHandle) {
			canHandle = mimeType.endsWith("+xml"); //$NON-NLS-1$
		}
		return canHandle;
	}
}
