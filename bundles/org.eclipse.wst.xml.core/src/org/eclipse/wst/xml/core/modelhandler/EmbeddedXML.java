/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.modelhandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.parser.JSPCapableParser;
import org.eclipse.wst.xml.core.modelquery.ModelQueryAdapterFactoryForEmbeddedXML;


public class EmbeddedXML implements EmbeddedTypeHandler {
	public String ContentTypeID_EmbeddedXML = "org.eclipse.wst.xml.core.contenttype.EmbeddedXML"; //$NON-NLS-1$

	private static List supportedMimeTypes;

	/**
	 * Constructor for EmbeddedXML. 
	 */
	public EmbeddedXML() {
		super();
	}

	/**
	 * @see EmbeddedContentType#getFamilyId()
	 */
	public String getFamilyId() {
		return ModelHandlerForXML.AssociatedContentTypeID;
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

	/*
	 * @see EmbeddedContentType#initializeParser(RegionParser)
	 */
	public void initializeParser(JSPCapableParser parser) {
		// nothing to initialize for "pure" XML
		// compare with XHTML
	}

	public List getSupportedMimeTypes() {
		if (supportedMimeTypes == null) {
			supportedMimeTypes = new ArrayList();
			supportedMimeTypes.add("text/xml"); //$NON-NLS-1$
		}
		return supportedMimeTypes;
	}

	public void initializeFactoryRegistry(IFactoryRegistry registry) {
		//TODO: initialize
	}

	public void uninitializeFactoryRegistry(IFactoryRegistry registry) {
		// TODO: need to undo anything we did in initialize

	}

	public void uninitializeParser(JSPCapableParser parser) {
		// need to undo anything we did in initialize
	}

	public EmbeddedTypeHandler newInstance() {
		return new EmbeddedXML();
	}

	public boolean isDefault() {
		return false;
	}
}
