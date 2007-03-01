/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.css.core.internal.modelhandler;

import org.eclipse.wst.jsdt.web.css.core.internal.document.JSPedCSSModelImpl;
import org.eclipse.wst.jsdt.web.css.core.internal.encoding.JSPedCSSDocumentLoader;
import org.eclipse.wst.css.core.internal.modelhandler.CSSModelLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public class JSPedCSSModelLoader extends CSSModelLoader {
	@Override
	public IStructuredModel newModel() {
		IStructuredModel model = new JSPedCSSModelImpl();
		return model;
	}

	@Override
	public IModelLoader newInstance() {
		return new JSPedCSSModelLoader();
	}

	@Override
	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new JSPedCSSDocumentLoader();
		}
		return documentLoaderInstance;
	}

}
