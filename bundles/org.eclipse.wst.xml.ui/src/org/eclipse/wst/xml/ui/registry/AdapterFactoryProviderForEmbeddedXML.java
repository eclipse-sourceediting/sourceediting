/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.registry;

import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryProvider;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.modelhandler.EmbeddedXML;

public class AdapterFactoryProviderForEmbeddedXML implements EmbeddedAdapterFactoryProvider {

	/*
	 * @see AdapterFactoryProvider#addAdapterFactories(IStructuredModel)
	 */
	public void addAdapterFactories(IStructuredModel structuredModel) {
		if (structuredModel instanceof XMLModel) {
			XMLDocument doc = ((XMLModel) structuredModel).getDocument();
		}
	}

	/*
	 * @see AdapterFactoryProvider#isFor(ContentTypeDescription)
	 */
	public boolean isFor(EmbeddedTypeHandler contentTypeDescription) {
		return (contentTypeDescription instanceof EmbeddedXML);
	}
}
