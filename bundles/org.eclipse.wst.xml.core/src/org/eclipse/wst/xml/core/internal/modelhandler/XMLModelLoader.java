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
package org.eclipse.wst.xml.core.internal.modelhandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.model.AbstractModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.DebugAdapterFactory;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryAdapterFactoryForXML;
import org.eclipse.wst.xml.core.internal.propagate.PropagatingAdapterFactoryImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;


/**
 * This class reads an XML file and creates an XML Structured Model.
 *  
 */
public class XMLModelLoader extends AbstractModelLoader {

	//	private static final String STR_ENCODING = "encoding"; //$NON-NLS-1$

	/**
	 * XMLLoader constructor comment.
	 */
	public XMLModelLoader() {
		super();
	}

	public List getAdapterFactories() {
		List result = new ArrayList();
		INodeAdapterFactory factory = null;
		factory = new ModelQueryAdapterFactoryForXML();
		result.add(factory);
		// Does XML need propagating adapter? Or just JSP?
		factory = new PropagatingAdapterFactoryImpl();
		result.add(factory);
		return result;
	}

	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new XMLDocumentLoader();
		}
		return documentLoaderInstance;
	}

	public IModelLoader newInstance() {
		return new XMLModelLoader();
	}

	public IStructuredModel newModel() {
		return new DOMModelImpl();
	}

	protected void preLoadAdapt(IStructuredModel structuredModel) {
		super.preLoadAdapt(structuredModel);
		IDOMModel domModel = (IDOMModel) structuredModel;
		// if there is a model in the adapter, this will adapt it to
		// first node. After that the PropagatingAdater spreads over the
		// children being
		// created. Each time that happends, a side effect is to
		// also "spread" sprecific registered adapters,
		// they two can propigate is needed.
		((INodeNotifier) domModel.getDocument()).getAdapterFor(PropagatingAdapter.class);

		if (Debug.debugNotificationAndEvents) {
			PropagatingAdapter propagatingAdapter = (PropagatingAdapter) ((INodeNotifier) domModel.getDocument()).getAdapterFor(PropagatingAdapter.class);
			propagatingAdapter.addAdaptOnCreateFactory(new DebugAdapterFactory());
		}

	}

}
