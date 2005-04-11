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
package org.eclipse.wst.html.ui.registry;

import org.eclipse.wst.html.core.internal.modelhandler.EmbeddedHTML;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryProvider;

public class AdapterFactoryProviderForEmbeddedHTML implements EmbeddedAdapterFactoryProvider {

	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	public void addAdapterFactories(IStructuredModel structuredModel) {
		//        if (structuredModel instanceof XMLModel) {
		//            XMLDocument doc = ((XMLModel) structuredModel).getDocument();
		//        }
	}

	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(EmbeddedTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof EmbeddedHTML);
	}
}