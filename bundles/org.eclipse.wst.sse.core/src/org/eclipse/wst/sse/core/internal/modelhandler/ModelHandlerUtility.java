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
package org.eclipse.wst.sse.core.internal.modelhandler;

import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.modelhandler.IDocumentTypeHandler;

/**
 * 
 * Likely a temporary class to be replaced by plugin, eventually.
 */
public class ModelHandlerUtility {
	public static EmbeddedTypeHandler getDefaultEmbeddedType() {
		return getEmbeddedContentTypeFor("text/html"); //$NON-NLS-1$
	}

	private static ModelHandlerRegistry contentTypeRegistry;

	public ModelHandlerUtility() {
		super();
	}

	private static ModelHandlerRegistry getContentTypeRegistry() {
		if (contentTypeRegistry == null) {
			contentTypeRegistry = ModelHandlerRegistry.getInstance();
		}
		return contentTypeRegistry;
	}

	public static IDocumentTypeHandler getContentTypeFor(String string) {
		return getContentTypeRegistry().getHandlerForContentTypeId(string);
	}

	public static EmbeddedTypeHandler getEmbeddedContentTypeFor(String string) {
		EmbeddedTypeHandler instance = null;
		instance = EmbeddedTypeRegistryImpl.getInstance().getTypeFor(string);
		return instance;
	}
}
