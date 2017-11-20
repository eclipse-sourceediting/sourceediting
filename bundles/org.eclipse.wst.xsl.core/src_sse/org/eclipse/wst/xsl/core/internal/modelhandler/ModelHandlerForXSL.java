/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal.modelhandler;

import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.modelhandler.ModelHandlerForXML;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.encoding.XSLDocumentLoader;


/**
 * Provides generic XML model handling. It is also marked as the default
 * content type handler. There should be only one implementation of the
 * default.
 */
public class ModelHandlerForXSL extends ModelHandlerForXML implements IModelHandler {
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	static String AssociatedContentTypeID = XSLCore.XSL_CONTENT_TYPE;
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	private static String ModelHandlerID = "org.eclipse.wst.xsl.core.modelhandler"; //$NON-NLS-1$

	public ModelHandlerForXSL() {
		super();	
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeID);
	}

	@Override
	public IDocumentLoader getDocumentLoader() {
		return new XSLDocumentLoader();
	}

	@Override
	public IDocumentCharsetDetector getEncodingDetector() {
		return new XMLDocumentCharsetDetector();
	}

	@Override
	public IModelLoader getModelLoader() {
		return new XSLModelLoader();
	}

}
