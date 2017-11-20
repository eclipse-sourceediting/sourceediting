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

import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.util.Assert;


public class EmbeddedTypeRegistryReader {
	protected String ATT_CLASS = "class"; //$NON-NLS-1$
	protected String EXTENSION_POINT_ID = "embeddedTypeHandler"; //$NON-NLS-1$


	protected String PLUGIN_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$
	protected String TAG_NAME = "embeddedTypeHandler"; //$NON-NLS-1$

	EmbeddedTypeRegistryReader() {
		super();
	}

	protected EmbeddedTypeHandler readElement(IConfigurationElement element) {

		EmbeddedTypeHandler contentTypeDescription = null;
		if (element.getName().equals(TAG_NAME)) {
			try {
				contentTypeDescription = (EmbeddedTypeHandler) element.createExecutableExtension(ATT_CLASS);
			} catch (Exception e) {
				Logger.logException(e);
			}
		}
		Assert.isNotNull(contentTypeDescription, "Error reading content type description"); //$NON-NLS-1$
		return contentTypeDescription;
	}

	/**
	 * We simply require an 'add' method, of what ever it is we are to read
	 * into
	 */
	void readRegistry(Set set) {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = extensionRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
		if (point != null) {
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				EmbeddedTypeHandler embeddedContentType = readElement(elements[i]);
				// null can be returned if there's an error reading the
				// element
				if (embeddedContentType != null) {
					set.add(embeddedContentType);
				}
			}
		}
	}
}
