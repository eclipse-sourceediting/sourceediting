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
package org.eclipse.jst.jsp.core.internal.parser;



import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;

public class JSPCodeRegion extends ForeignRegion {

	protected RegionParser fBlockNodeChecker = null;

	/**
	 * JavaCodeRegion constructor comment.
	 */
	public JSPCodeRegion() {
		super();
	}

	public JSPCodeRegion(String newContext, int newStart, int newTextLength, int newLength) {
		super(newContext, newStart, newTextLength, newLength, "jsp-java"); //$NON-NLS-1$
	}

	/**
	 * 
	 * @return RegionParser
	 */
	protected RegionParser getBlockNodeChecker() {
		if (fBlockNodeChecker == null) {
			IModelManager mmanager = StructuredModelManager.getModelManager();
			fBlockNodeChecker = mmanager.createStructuredDocumentFor(IContentTypeIdentifier.ContentTypeID_JSP).getParser();
		}
		return fBlockNodeChecker;
	}

	/**
	 * @see com.ibm.sed.interfaces.core.IRegionCodeAssistProcessor
	 */
	public void setStructuredDocument(IStructuredDocument structuredDocument) {
	}
}