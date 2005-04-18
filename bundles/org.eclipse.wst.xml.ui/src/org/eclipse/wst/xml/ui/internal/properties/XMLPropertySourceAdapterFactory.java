/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.internal.properties;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;

public class XMLPropertySourceAdapterFactory extends AbstractAdapterFactory {

	public XMLPropertySourceAdapterFactory() {
		super(IPropertySource.class, true);
	}

	public XMLPropertySourceAdapterFactory(Object adapterType, boolean registerAdapters) {
		super(adapterType, registerAdapters);
	}

	public INodeAdapterFactory copy() {
		return new XMLPropertySourceAdapterFactory(this.adapterKey, this.shouldRegisterAdapter);
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		// at the moment, only one implementation exists
		return new XMLPropertySourceAdapter(target);
	}
}
