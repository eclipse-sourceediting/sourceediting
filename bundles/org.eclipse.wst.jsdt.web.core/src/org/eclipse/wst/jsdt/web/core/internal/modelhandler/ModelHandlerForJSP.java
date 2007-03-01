/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.modelhandler;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.jsdt.web.core.internal.JSPCorePlugin;
import org.eclipse.wst.jsdt.web.core.internal.encoding.JSPDocumentHeadContentDetector;
import org.eclipse.wst.jsdt.web.core.internal.encoding.JSPDocumentLoader;

import org.eclipse.wst.jsdt.web.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;

public class ModelHandlerForJSP extends AbstractModelHandler {

	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten at
	 * run time with what's in registry! (so should never be 'final')
	 */
	static String AssociatedContentTypeID = "org.eclipse.wst.html.core.htmlsource"; //$NON-NLS-1$
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten at
	 * run time with what's in registry! (so should never be 'final')
	 */
	private static String ModelHandlerID = "org.eclipse.wst.jsdt.web.core.modelhandler"; //$NON-NLS-1$

	public ModelHandlerForJSP() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeID);
	}



	public IModelLoader getModelLoader() {
		return new JSPModelLoader();
	}

	public Preferences getPreferences() {
		return JSPCorePlugin.getDefault().getPluginPreferences();
	}

	@Override
	public IDocumentCharsetDetector getEncodingDetector() {
		return new JSPDocumentHeadContentDetector();
	}

	public IDocumentLoader getDocumentLoader() {
		return new JSPDocumentLoader();
	}

}