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
package org.eclipse.wst.xml.ui.views.properties;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.w3c.dom.Node;

public class XMLPropertySourceAdapterFactory extends AbstractAdapterFactory {

	public XMLPropertySourceAdapterFactory() {
		super(IPropertySource.class, true);
	}

	public XMLPropertySourceAdapterFactory(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	public AdapterFactory copy() {
		return new XMLPropertySourceAdapterFactory(this.adapterKey, this.shouldRegisterAdapter);
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		// at the moment, only one implementation exists
		if (target != null && target instanceof Node && ((Node) target).getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
			return new ProcessingInstructionPropertySourceAdapter(target);
		return new XMLPropertySourceAdapter(target);
	}
}
