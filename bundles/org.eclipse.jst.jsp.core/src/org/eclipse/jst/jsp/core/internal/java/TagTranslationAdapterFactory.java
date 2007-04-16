/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

public class TagTranslationAdapterFactory extends JSPTranslationAdapterFactory {

	private TagTranslationAdapter fAdapter = null;

	public TagTranslationAdapterFactory() {
		super();
	}

	public INodeAdapterFactory copy() {
		return new TagTranslationAdapterFactory();
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		if (target instanceof IDOMNode && fAdapter == null) {
			fAdapter = new TagTranslationAdapter(((IDOMNode) target).getModel());
			if (DEBUG) {
				System.out.println("(+) TagTranslationAdapterFactory [" + this + "] created adapter: " + fAdapter); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return fAdapter;
	}

	public void release() {
		if (fAdapter != null) {
			if (DEBUG) {
				System.out.println("(-) TagTranslationAdapterFactory [" + this + "] releasing adapter: " + fAdapter); //$NON-NLS-1$ //$NON-NLS-2$
			}
			fAdapter.release();
		}
		super.release();
	}
}
