/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.internal.contentoutline;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;

public class JFaceNodeAdapterFactoryCSS extends JFaceNodeAdapterFactory {
	public JFaceNodeAdapterFactoryCSS() {
		this(IJFaceNodeAdapter.class, true);
	}

	public JFaceNodeAdapterFactoryCSS(Object adapterKey, boolean registerAdapters) {
		super(adapterKey, registerAdapters);
	}


	protected INodeAdapter createAdapter(INodeNotifier node) {
		if (singletonAdapter == null) {
			// create the JFaceNodeAdapter
			singletonAdapter = new CSSNodeAdapter(this);
		}
		return singletonAdapter;
	}


}
