/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.modelhandler;

import java.util.List;

import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.parser.JSPCapableParser;


/**
 */
public interface EmbeddedTypeHandler {

	/**
	 * These AdapterFactories are NOT added to IStructuredModel's
	 * IAdapterFactory Registry ... they are for use by "JSP Aware
	 * AdapterFactories" The are added to the "registry" in the
	 * PageDirectiveAdapter.
	 */
	List getAdapterFactories();

	/**
	 * Returns the unique identifier for the content type family this
	 * ContentTypeDescription belongs to.
	 */
	public String getFamilyId();

	/**
	 * Returns a list of mime tyeps (as Strings) this handler is appropriate
	 * for
	 */
	List getSupportedMimeTypes();

	/**
	 * This method is to give the EmbeddedContentType an opportunity to add
	 * factories directly to the IStructuredModel's IAdapterFactory registry.
	 */
	void initializeFactoryRegistry(FactoryRegistry registry);

	/**
	 * initializeParser Its purpose is to setBlockTags
	 */
	void initializeParser(JSPCapableParser parser);

	boolean isDefault();

	public EmbeddedTypeHandler newInstance();

	void uninitializeFactoryRegistry(FactoryRegistry registry);

	void uninitializeParser(JSPCapableParser parser);
}
