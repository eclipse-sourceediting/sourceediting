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
package org.eclipse.wst.sse.core.modelhandler;

import java.util.List;

import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.parser.JSPCapableParser;


/**
 */
public interface EmbeddedTypeHandler {

	/**
	 * Returns a list of mime tyeps (as Strings) this handler is appropriate for
	 */
	List getSupportedMimeTypes();

	/**
	 * Returns the unique identifier for the content type family this ContentTypeDescription
	 * belongs to.
	 */
	public String getFamilyId();

	/**
	 * These AdapterFactories are NOT added to IStructuredModel's
	 * AdapterFactory Registry ... 
	 * they are for use by "JSP Aware AdapterFactories"
	 * The are added to the "registry" in the PageDirectiveAdapter.
	 */
	List getAdapterFactories();

	/**
	 * initializeParser 
	 * Its purpose is to setBlockTags
	 */
	void initializeParser(JSPCapableParser parser);

	void uninitializeParser(JSPCapableParser parser);

	/**
	 * This method is to give the EmbeddedContentType 
	 * an opportunity to add factories directly to the 
	 * IStructuredModel's AdapterFactory registry.
	 */
	void initializeFactoryRegistry(IFactoryRegistry registry);

	void uninitializeFactoryRegistry(IFactoryRegistry registry);

	public EmbeddedTypeHandler newInstance();

	boolean isDefault();
}
