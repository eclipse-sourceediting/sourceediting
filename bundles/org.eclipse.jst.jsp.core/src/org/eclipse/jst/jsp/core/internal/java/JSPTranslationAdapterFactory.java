/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Factory for JSPTranslationAdapters.
 * 
 * @author pavery
 *  
 */
public class JSPTranslationAdapterFactory extends AbstractAdapterFactory {

	/** the adapter associated with this factory */
	private JSPTranslationAdapter fAdapter = null;

	// for debugging
	static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation")).booleanValue(); //$NON-NLS-1$;
	
	public JSPTranslationAdapterFactory() {
		super(IJSPTranslation.class, true);
	}


	protected INodeAdapter createAdapter(INodeNotifier target) {
		if (target instanceof IDOMNode && fAdapter == null) {
			/* attempt to load externalized translator and create adapter from it
			 * else create new adapter */
			IDOMModel model = ((IDOMNode) target).getModel();
			
			JSPTranslator translator = JSPTranslatorPersister.getPersistedTranslator(model);
			if(translator != null) {
				fAdapter = new JSPTranslationAdapter(model, translator);
			} else {
				fAdapter= new JSPTranslationAdapter(model);
			}

			if(DEBUG) {
				System.out.println("(+) JSPTranslationAdapterFactory [" + this + "] created adapter: " + fAdapter); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return fAdapter;
	}


	public void release() {
		if(fAdapter != null) {
			if(DEBUG) {
				System.out.println("(-) JSPTranslationAdapterFactory [" + this + "] releasing adapter: " + fAdapter); //$NON-NLS-1$ //$NON-NLS-2$
			}
			fAdapter.release();
		}
	
		super.release();
	}
}
