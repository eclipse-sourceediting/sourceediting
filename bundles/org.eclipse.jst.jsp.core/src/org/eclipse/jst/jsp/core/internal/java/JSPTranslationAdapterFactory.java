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

import java.util.HashMap;

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
	// for debugging
	static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jsptranslation")).booleanValue(); //$NON-NLS-1$;
	
	private static final JSPTranslationAdapterFactory DEFAULT = new JSPTranslationAdapterFactory();
	
	/**
	 * <code>{@link HashMap}<{@link String}, {@link JSPTranslationAdapter}></code>
	 * <ul><li>key - location of JSP file</li>
	 * <li>value - adapter associated with file at base location</li></ul>
	 */
	private HashMap fAdapters = null;
	
	protected JSPTranslationAdapterFactory() {
		super(IJSPTranslation.class, true);
		this.fAdapters = new HashMap();
	}
	
	/**
	 * @return Singleton instance of the {@link JSPTranslationAdapterFactory}
	 */
	public static JSPTranslationAdapterFactory getDefault() {
		return DEFAULT;
	}

	/**
	 * <p>If an adapter for the model associated with the given <code>target</code>
	 * already exists then the model is updated for that adapter and returned,
	 * else a new adapter is created for the model associated with the given
	 * <code>target</code> and that new adapter is returned</p>
	 * 
	 * @param target get {@link JSPTranslationAdapter} for model associated with this {@link INodeNotifier}
	 * 
	 * @return {@link JSPTranslationAdapter} assoicated with the model for the given <code>target</code>
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory#createAdapter(org.eclipse.wst.sse.core.internal.provisional.INodeNotifier)
	 */
	protected INodeAdapter createAdapter(INodeNotifier target) {
		JSPTranslationAdapter adapter = null;
		if (target instanceof IDOMNode) {
			/* attempt to load externalized translator and create adapter from it
			 * else create new adapter */
			IDOMModel model = ((IDOMNode) target).getModel();
			
			adapter = (JSPTranslationAdapter)this.fAdapters.get(model.getBaseLocation());
			if(adapter == null) {
				JSPTranslator translator = JSPTranslatorPersister.getPersistedTranslator(model);
				if(translator != null) {
					adapter = new JSPTranslationAdapter(model, translator);
				} else {
					adapter= new JSPTranslationAdapter(model);
				}
				
				if(DEBUG) {
					System.out.println("(+) JSPTranslationAdapterFactory [" + this + "] created adapter: " + adapter); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				this.fAdapters.put(model.getBaseLocation(), adapter);
			} else {
				adapter.setXMLModel(model);
			}
			
		}
		return adapter;
	}
}
