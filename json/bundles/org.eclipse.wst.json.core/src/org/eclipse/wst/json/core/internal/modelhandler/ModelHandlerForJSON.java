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
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.modelhandler.ModelHandlerForXML
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.modelhandler;

import org.eclipse.wst.json.core.internal.encoding.JSONDocumentCharsetDetector;
import org.eclipse.wst.json.core.internal.encoding.JSONDocumentLoader;
import org.eclipse.wst.json.core.modelhandler.IIModelHandlerForJSON;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;

public class ModelHandlerForJSON extends AbstractModelHandler implements
		IIModelHandlerForJSON {

	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten at
	 * run time with what's in registry! (so should never be 'final')
	 */
	private static final String AssociatedContentTypeID = "org.eclipse.wst.json.core.jsonsource"; //$NON-NLS-1$
	
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten at
	 * run time with what's in registry! (so should never be 'final')
	 */
	private static final String ModelHandlerID = "org.eclipse.wst.json.core.modelhandler"; //$NON-NLS-1$

	public ModelHandlerForJSON() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeID);
	}

	@Override
	public IDocumentCharsetDetector getEncodingDetector() {
		return new JSONDocumentCharsetDetector();
	}

	@Override
	public IDocumentLoader getDocumentLoader() {
		return new JSONDocumentLoader();
	}

	@Override
	public IModelLoader getModelLoader() {
		return new JSONModelLoader();
	}

}
