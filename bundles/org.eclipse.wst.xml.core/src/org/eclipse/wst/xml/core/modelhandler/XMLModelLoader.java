/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.modelhandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.sse.core.AbstractModelLoader;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.ModelLoader;
import org.eclipse.wst.sse.core.PropagatingAdapter;
import org.eclipse.wst.sse.core.document.IDocumentLoader;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.document.XMLModelImpl;
import org.eclipse.wst.xml.core.internal.propagate.PropagatingAdapterFactoryImpl;
import org.eclipse.wst.xml.core.modelquery.ModelQueryAdapterFactoryForXML;


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

	protected void preLoadAdapt(IStructuredModel structuredModel) {
		XMLModel domModel = (XMLModel) structuredModel;
		// if there is a model in the adapter, this will adapt it to 
		// first node. After that the PropagatingAdater spreads over the children being 
		// created. Each time that happends, a side effect is to 
		// also "spread" sprecific registered adapters, 
		// they two can propigate is needed.
		((INodeNotifier) domModel.getDocument()).getAdapterFor(PropagatingAdapter.class);
	}

	public List getAdapterFactories() {
		List result = new ArrayList();
		AdapterFactory factory = null;
		// TODO different ModelQueryAdapterFactories need to be made for each type/loader
		factory = new ModelQueryAdapterFactoryForXML();
		result.add(factory);
		// Does XML need propagating adapter? Or just JSP?
		factory = new PropagatingAdapterFactoryImpl();
		result.add(factory);

		return result;
	}

	public IStructuredModel newModel() {
		return new XMLModelImpl();
	}

	public ModelLoader newInstance() {
		return new XMLModelLoader();
	}

	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new XMLDocumentLoader();
		}
		return documentLoaderInstance;
	}

}
