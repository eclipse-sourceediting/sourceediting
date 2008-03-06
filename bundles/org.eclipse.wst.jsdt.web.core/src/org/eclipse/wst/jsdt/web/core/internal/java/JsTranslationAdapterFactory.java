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
package org.eclipse.wst.jsdt.web.core.internal.java;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Factory for JSPTranslationAdapters.
 * 
 * @author pavery
 * 
 */
public class JsTranslationAdapterFactory extends AbstractAdapterFactory {
	// for debugging
	private static final boolean DEBUG;
	private boolean listenForProjectChanges;
	
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	private JsTranslationAdapter fAdapter = null;
	
	public JsTranslationAdapterFactory() {
		super(IJsTranslation.class, true);
		listenForProjectChanges = true;
	}
	
	
	public INodeAdapterFactory copy() {
		return new JsTranslationAdapterFactory();
	}
	
	
	protected INodeAdapter createAdapter(INodeNotifier target) {
		if (target instanceof IDOMNode && fAdapter == null) {
			fAdapter = new JsTranslationAdapter(((IDOMNode) target).getModel());
			if (JsTranslationAdapterFactory.DEBUG) {
				System.out.println("(+) JSPTranslationAdapterFactory [" + this + "] created adapter: " + fAdapter); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return fAdapter;
	}
	
	
	public void release() {
		if (fAdapter != null) {
			if (JsTranslationAdapterFactory.DEBUG) {
				System.out.println("(-) JSPTranslationAdapterFactory [" + this + "] releasing adapter: " + fAdapter); //$NON-NLS-1$ //$NON-NLS-2$
			}
			fAdapter.release();
		}
		super.release();
	}
}