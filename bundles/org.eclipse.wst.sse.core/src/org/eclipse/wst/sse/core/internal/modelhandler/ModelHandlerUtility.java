/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.modelhandler;

import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;

/**
 * 
 * Likely a temporary class to be replaced by plugin, eventually.
 */
public class ModelHandlerUtility {

	private static ModelHandlerRegistry contentTypeRegistry;

	public static IDocumentTypeHandler getContentTypeFor(String string) {
		return getContentTypeRegistry().getHandlerForContentTypeId(string);
	}

	private static ModelHandlerRegistry getContentTypeRegistry() {
		if (contentTypeRegistry == null) {
			contentTypeRegistry = ModelHandlerRegistry.getInstance();
		}
		return contentTypeRegistry;
	}

	public static EmbeddedTypeHandler getDefaultEmbeddedType() {
		return getEmbeddedContentTypeFor("text/html"); //$NON-NLS-1$
	}

	public static EmbeddedTypeHandler getEmbeddedContentTypeFor(String string) {
		EmbeddedTypeHandler instance = null;
		instance = EmbeddedTypeRegistryImpl.getInstance().getTypeFor(string);
		return instance;
	}

	public ModelHandlerUtility() {
		super();
	}
}
