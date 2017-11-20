/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.support.jsp.internal;

import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;

public class AdapterFactoryProviderForJSDT extends org.eclipse.wst.jsdt.web.ui.internal.registry.AdapterFactoryProviderForJSDT {
	public AdapterFactoryProviderForJSDT() {
		super();
	}
	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForJSP);
	}
}
