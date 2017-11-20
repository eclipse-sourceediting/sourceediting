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
package org.eclipse.wst.sse.core.internal.ltk.modelhandler;

import java.util.List;

import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;


/**
 */
public interface EmbeddedTypeHandler {

	/**
	 * These AdapterFactories are NOT added to IStructuredModel's
	 * IAdapterFactory Registry, but instead expected to be consulted as
	 * needed by functionality aware of embedded content types.  Additions
	 * to the model's own factory registry should be done in
	 * {@link #initializeFactoryRegistry(FactoryRegistry)}
	 */
	List getAdapterFactories();

	/**
	 * Returns the unique identifier for the content type family this
	 * ContentTypeDescription belongs to.
	 */
	String getFamilyId();

	/**
	 * Returns a list of mime types (as Strings) this handler is appropriate
	 * for
	 */
	List getSupportedMimeTypes();
	
	/**
	 * If this hander can handle a given mimeType.
	 * 
	 * This is a looser check than simply checking if a give mimeType
	 * in the list of supported types, so it should be used with that
	 * in mind.  That is, the supported mimeType list should ideally be
	 * checked first.
	 * 
	 * eg. if a mime type ends with "+xml", like voice+xml
	 *     the EmbeddedXML handler should be able to handle it
	 *     
	 * @return true if this handler thinks can handle the given mimeType
	 */
	boolean canHandleMimeType(String mimeType);

	/**
	 * This method is to give the EmbeddedContentType an opportunity to add
	 * factories directly to the IStructuredModel's IAdapterFactory registry. 
	 */
	void initializeFactoryRegistry(FactoryRegistry registry);

	/**
	 * initializeParser, for example, setting up a "block" tags list using an
	 * extended interface
	 */
	void initializeParser(RegionParser parser);

	boolean isDefault();

	EmbeddedTypeHandler newInstance();

	void uninitializeFactoryRegistry(FactoryRegistry registry);

	void uninitializeParser(RegionParser parser);
}
