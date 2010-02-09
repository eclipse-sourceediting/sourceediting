/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelhandler;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.encoding.JSPDocumentHeadContentDetector;
import org.eclipse.jst.jsp.core.internal.encoding.JSPDocumentLoader;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.java.TagTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.parser.JSPSourceParser;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class ModelHandlerForJSP extends AbstractModelHandler {

	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	static String AssociatedContentTypeID = "org.eclipse.jst.jsp.core.jspsource"; //$NON-NLS-1$
	/**
	 * Needs to match what's in plugin registry. In fact, can be overwritten
	 * at run time with what's in registry! (so should never be 'final')
	 */
	private static String ModelHandlerID = "org.eclipse.jst.jsp.core.modelhandler"; //$NON-NLS-1$


	public ModelHandlerForJSP() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeID);
	}

	protected void addJSPTagName(JSPSourceParser parser, String tagname) {
		BlockMarker bm = new BlockMarker(tagname, null, DOMJSPRegionContexts.JSP_CONTENT, true);
		parser.addBlockMarker(bm);
	}

	public IModelLoader getModelLoader() {
		return new JSPModelLoader();
	}

	public Preferences getPreferences() {
		return JSPCorePlugin.getDefault().getPluginPreferences();
	}

	public IDocumentCharsetDetector getEncodingDetector() {
		return new JSPDocumentHeadContentDetector();
	}

	public IDocumentLoader getDocumentLoader() {
		return new JSPDocumentLoader();
	}

	public static void ensureTranslationAdapterFactory(IStructuredModel sm) {
		if (sm.getFactoryRegistry().getFactoryFor(IJSPTranslation.class) == null) {
			/*
			 * Check for tag/tagx files, otherwise add the JSP translation
			 * factory for better compatibility with other possible subtypes
			 * of JSP.
			 */
			IContentType thisContentType = Platform.getContentTypeManager().getContentType(sm.getContentTypeIdentifier());
			IContentType tagContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPTAG);
			if (thisContentType.isKindOf(tagContentType)) {
				INodeAdapterFactory factory = new TagTranslationAdapterFactory();
				sm.getFactoryRegistry().addFactory(factory);
			}
			else {
				INodeAdapterFactory factory = null;
//				if (false) {
//					IContentType textContentType = Platform.getContentTypeManager().getContentType(IContentTypeManager.CT_TEXT);
//					IContentType jspContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
//					/*
//					 * This IAdapterManager call is temporary placeholder code
//					 * that should not be relied upon in any way!
//					 */
//					if (thisContentType.isKindOf(jspContentType)) {
//						IContentType testContentType = thisContentType;
//						INodeAdapterFactory holdFactory = null;
//						while (!testContentType.equals(textContentType) && holdFactory == null) {
//							holdFactory = (INodeAdapterFactory) Platform.getAdapterManager().getAdapter(testContentType.getId(), IJSPTranslation.class.getName());
//							testContentType = testContentType.getBaseType();
//						}
//					}
//				}
				if (factory == null) {
					factory = new JSPTranslationAdapterFactory();
				}

				sm.getFactoryRegistry().addFactory(factory);
			}
		}
	}
}
