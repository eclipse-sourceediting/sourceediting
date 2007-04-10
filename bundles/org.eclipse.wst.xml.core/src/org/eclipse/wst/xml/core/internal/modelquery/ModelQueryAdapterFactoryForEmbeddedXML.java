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
package org.eclipse.wst.xml.core.internal.modelquery;



import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;


public class ModelQueryAdapterFactoryForEmbeddedXML extends ModelQueryAdapterFactoryForXML {


	/**
	 * Constructor for ModelQueryAdapterFactoryForEmbeddedXML.
	 */
	public ModelQueryAdapterFactoryForEmbeddedXML() {
		super(ModelQueryAdapter.class, false);
	}

	protected void configureDocumentManager(CMDocumentManager mgr) {
		super.configureDocumentManager(mgr);
		mgr.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, true);
	}
	
	/**
	 * ISSUE: this "forces" a new one to always be created/returned, 
	 * not "cached" on the node. That seems incorrect. 
	 * Simply using shouldRegisterFalse should work, except, 
	 * there might have been one there that someone else already 
	 * explicitly put there, so this is only way I know to 
	 * override that. Especially complicated here since a number
	 * of adapters are for ModelQueryAdapter.class.
	 */
	public INodeAdapter adapt(INodeNotifier object) {
		return adaptNew(object);
	}
}
