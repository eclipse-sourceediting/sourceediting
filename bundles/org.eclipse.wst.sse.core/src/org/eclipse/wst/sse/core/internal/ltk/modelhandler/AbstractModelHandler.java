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
package org.eclipse.wst.sse.core.internal.ltk.modelhandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.modelhandler.PluginContributedFactoryReader;

/**
 * ISSUE: need to provide this functionality in improved API.
 */

public abstract class AbstractModelHandler implements IModelHandler {
	private String associatedContentTypeId;
	private boolean defaultSetting;
	private String modelHandlerID;

	public AbstractModelHandler() {
		super();
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

	public String getAssociatedContentTypeId() {
		return associatedContentTypeId;
	}

	public abstract IDocumentCharsetDetector getEncodingDetector();

	public String getId() {
		return modelHandlerID;
	}

	public boolean isDefault() {
		return defaultSetting;
	}

	protected void setAssociatedContentTypeId(String contentTypeId) {
		associatedContentTypeId = contentTypeId;
	}

	public void setDefault(boolean defaultParam) {
		defaultSetting = defaultParam;
	}

	protected void setId(String id) {
		modelHandlerID = id;
	}
}
