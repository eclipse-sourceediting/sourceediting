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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.wst.sse.core.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.modelhandler.PluginContributedFactoryReader;


public abstract class AbstractModelHandler implements IModelHandler {
	private String associatedContentTypeId;
	private String modelHandlerID;
	private boolean defaultSetting;

	public AbstractModelHandler() {
		super();
	}

	public boolean isDefault() {
		return defaultSetting;
	}

	public abstract IDocumentCharsetDetector getEncodingDetector();

	public String getAssociatedContentTypeId() {
		return associatedContentTypeId;
	}

	protected void setAssociatedContentTypeId(String contentTypeId) {
		associatedContentTypeId = contentTypeId;
	}

	public String getId() {
		return modelHandlerID;
	}

	protected void setId(String id) {
		modelHandlerID = id;
	}

	public void setDefault(boolean defaultParam) {
		defaultSetting = defaultParam;
	}

	/**
	 * These factories are added automatically by model manager
	 */
	public List getAdapterFactories() {
		List result = new ArrayList();
		Collection holdFactories = PluginContributedFactoryReader.getInstance().getFactories(this);
		if (holdFactories != null) {
			result.addAll(holdFactories);
		}
		return result;
	}

	// XXX: can be removed once we transition completely
	protected boolean USE_FILE_BUFFERS = false;
}
