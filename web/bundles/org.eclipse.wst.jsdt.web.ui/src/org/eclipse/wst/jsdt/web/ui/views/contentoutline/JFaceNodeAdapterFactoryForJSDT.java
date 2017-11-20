/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline.JsContentOutlineConfig;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JFaceNodeAdapterFactoryForJSDT extends JFaceNodeAdapterFactory {
	public JFaceNodeAdapterFactoryForJSDT() {
		this(IJFaceNodeAdapter.class, true);
	}
	
	public JFaceNodeAdapterFactoryForJSDT(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}
	
	
	public INodeAdapterFactory copy() {
		return new JFaceNodeAdapterFactoryForJSDT(getAdapterKey(), isShouldRegisterAdapter());
	}
	
	
	protected INodeAdapter createAdapter(INodeNotifier node) {
		if (singletonAdapter == null) {
			// create the JFaceNodeAdapter
			// singletonAdapter = new JFaceNodeAdapterForJSDT(this);
			if (JsContentOutlineConfig.USE_ADVANCED) {
				singletonAdapter = new org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline.JFaceNodeAdapterForJs(this);
			} else {
				singletonAdapter = new org.eclipse.wst.jsdt.web.ui.views.contentoutline.JFaceNodeAdapterForJs(this);
			}
			initAdapter(singletonAdapter, node);
		}
		return singletonAdapter;
	}
}
