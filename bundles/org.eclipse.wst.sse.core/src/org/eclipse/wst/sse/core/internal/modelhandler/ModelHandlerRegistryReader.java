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

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;


/**
 * This class just converts what's in the plugins registry into a form more
 * easily useable by others, the ContentTypeRegistry.
 */
class ModelHandlerRegistryReader {
	private HashMap allReadyCreateInstances = new HashMap();
	protected String ATT_ASSOCIATED_CONTENT_TYPE = "associatedContentTypeId"; //$NON-NLS-1$
	protected String ATT_CLASS = "class"; //$NON-NLS-1$
	protected String ATT_DEFAULT = "default"; //$NON-NLS-1$
	protected String ATT_ID = "id"; //$NON-NLS-1$
	IConfigurationElement[] elements;
	protected String EXTENSION_POINT_ID = "modelHandler"; //$NON-NLS-1$
	//
	protected String PLUGIN_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$
	protected String TAG_NAME = "modelHandler"; //$NON-NLS-1$

	//
	/**
	 * ContentTypeRegistryReader constructor comment.
	 */
	ModelHandlerRegistryReader() {
		super();
	}

	String getAssociatedContentTypeId(IConfigurationElement element) {
		String value = element.getAttribute(ATT_ASSOCIATED_CONTENT_TYPE);
		return value;
	}

	String getId(IConfigurationElement element) {
		String idValue = element.getAttribute(ATT_ID);
		return idValue;
	}

	synchronized IModelHandler getInstance(IConfigurationElement element) {
		// May need to reconsider, but for now, we'll assume all clients must
		// subclass AbstractContentTypeIdentifier. Its easier and safer, for
		// this
		// low level "system" object. (That is, we can check and set "package
		// protected"
		// attributes.
		AbstractModelHandler modelHandler = (AbstractModelHandler) allReadyCreateInstances.get(getId(element));
		if (modelHandler == null) {
			try {
				modelHandler = (AbstractModelHandler) element.createExecutableExtension(ATT_CLASS);
				if (modelHandler != null) {
					allReadyCreateInstances.put(getId(element), modelHandler);
					String defaultValue = element.getAttribute(ATT_DEFAULT);
					if (defaultValue != null && "true".equals(defaultValue)) //$NON-NLS-1$
						modelHandler.setDefault(true);
					else
						modelHandler.setDefault(false);
					// TODO -- set and check attributes vs. created instance
					//contentTypeIdentifier.setOrCheckId(element.getAttribute(ATT_ID));
				}
			} catch (CoreException e) {
				org.eclipse.wst.sse.core.internal.Logger.logException(e);
			}
		}
		return modelHandler;
	}

	public boolean isElementDefault(IConfigurationElement element) {
		String defaultValue = element.getAttribute(ATT_DEFAULT);
		if (defaultValue != null && "true".equals(defaultValue)) //$NON-NLS-1$
			return true;
		else
			return false;
	}

	ModelHandlerRegistryReader readRegistry() {
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint point = extensionRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);
		if (point != null) {
			// just remember the elements, so plugins don't have to
			// be activated, unless extension matches those "of interest".
			elements = point.getConfigurationElements();
		}
		return this;
	}
}
