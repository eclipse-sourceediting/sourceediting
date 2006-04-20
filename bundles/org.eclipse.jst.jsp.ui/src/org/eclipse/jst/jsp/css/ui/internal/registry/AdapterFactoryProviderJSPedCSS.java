/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.css.ui.internal.registry;

import org.eclipse.jst.jsp.css.core.internal.modelhandler.ModelHandlerForJSPedCSS;
import org.eclipse.wst.css.ui.internal.registry.AdapterFactoryProviderCSS;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IDocumentTypeHandler;

public class AdapterFactoryProviderJSPedCSS extends AdapterFactoryProviderCSS {


	public boolean isFor(IDocumentTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof ModelHandlerForJSPedCSS);
	}


}
