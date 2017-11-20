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
package org.eclipse.wst.dtd.ui.internal.views.properties;



import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;


public class DTDPropertySourceAdapterFactory extends AbstractAdapterFactory {

	public DTDPropertySourceAdapterFactory() {
		super(IPropertySource.class, true);
	}

	public DTDPropertySourceAdapterFactory(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}

	public INodeAdapterFactory copy() {
		return new DTDPropertySourceAdapterFactory(getAdapterKey(), isShouldRegisterAdapter());
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		// at the moment, only one implementation exists
		return new DTDPropertySourceAdapter(target);
	}
}
