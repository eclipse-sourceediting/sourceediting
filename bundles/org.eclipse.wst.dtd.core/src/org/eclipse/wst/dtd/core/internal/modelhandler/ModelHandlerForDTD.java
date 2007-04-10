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
package org.eclipse.wst.dtd.core.internal.modelhandler;

import org.eclipse.wst.dtd.core.internal.encoding.DTDDocumentCharsetDetector;
import org.eclipse.wst.dtd.core.internal.encoding.DTDDocumentLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;


public final class ModelHandlerForDTD extends AbstractModelHandler implements IModelHandler {
	private static String AssociatedContentTypeId = "org.eclipse.wst.dtd.core.dtdsource"; //$NON-NLS-1$
	private static String ModelHandlerID = "org.eclipse.wst.dtd.core.internal.modelhandler"; //$NON-NLS-1$

	public ModelHandlerForDTD() {
		super();
		setId(ModelHandlerID);
		setAssociatedContentTypeId(AssociatedContentTypeId);
	}

	public IDocumentLoader getDocumentLoader() {
		return new DTDDocumentLoader();
	}

	public IDocumentCharsetDetector getEncodingDetector() {
		return new DTDDocumentCharsetDetector();
	}

	public IModelLoader getModelLoader() {
		return new DTDModelLoader();
	}

}
