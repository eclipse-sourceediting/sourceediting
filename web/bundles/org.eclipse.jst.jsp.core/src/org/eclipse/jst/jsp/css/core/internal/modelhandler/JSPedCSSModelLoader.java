/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.css.core.internal.modelhandler;

import org.eclipse.jst.jsp.css.core.internal.document.JSPedCSSModelImpl;
import org.eclipse.jst.jsp.css.core.internal.encoding.JSPedCSSDocumentLoader;
import org.eclipse.wst.css.core.internal.modelhandler.CSSModelLoader;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;


public class JSPedCSSModelLoader extends CSSModelLoader {
public IStructuredModel newModel() {
	IStructuredModel model = new JSPedCSSModelImpl();
	return model;
}
public IModelLoader newInstance() {
	return new JSPedCSSModelLoader();
}
public IDocumentLoader getDocumentLoader() {
	if (documentLoaderInstance == null) {
		documentLoaderInstance = new JSPedCSSDocumentLoader();
	}
	return documentLoaderInstance;
}


}
