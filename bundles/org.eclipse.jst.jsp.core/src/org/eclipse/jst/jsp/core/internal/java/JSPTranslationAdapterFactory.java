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
package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.core.document.XMLNode;

/**
 * Factory for JSPTranslationAdapters.
 * 
 * @author pavery
 *  
 */
public class JSPTranslationAdapterFactory extends AbstractAdapterFactory {

	private JSPTranslationAdapter fAdapter = null;

	// for debugging
	private static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	public JSPTranslationAdapterFactory() {
		super();
	}

	/**
	 * @see com.ibm.sse.model.IAdapterFactory#copy()
	 */
	public IAdapterFactory copy() {
		return new JSPTranslationAdapterFactory();
	}

	protected INodeAdapter createAdapter(INodeNotifier target) {
		if (target instanceof XMLNode && fAdapter == null) {
			fAdapter = new JSPTranslationAdapter(((XMLNode) target).getModel());
			if(DEBUG) {
				System.out.println("(+) JSPTranslationAdapterFactory [" + this + "] created adapter: " + fAdapter);
			}
		}
		return fAdapter;
	}

	/**
	 * @see com.ibm.sse.model.AbstractAdapterFactory#isFactoryForType(java.lang.Object)
	 */

	public boolean isFactoryForType(Object type) {
		return type.equals(IJSPTranslation.class);
	}

	/**
	 * @see com.ibm.sse.model.AbstractAdapterFactory#release()
	 */
	public void release() {
		if (fAdapter != null) {
			if(DEBUG) {
				System.out.println("(-) JSPTranslationAdapterFactory [" + this + "] releasing adapter: " + fAdapter);
			}
			fAdapter.release();
		}
		super.release();
	}
}