/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal.modelhandler;

import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.DebugAdapterFactory;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.modelhandler.XMLModelLoader;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsl.core.internal.encoding.XSLDocumentLoader;


/**
 * This class reads an XML file and creates an XML Structured Model.
 *  
 */
public class XSLModelLoader extends XMLModelLoader {

	//	private static final String STR_ENCODING = "encoding"; //$NON-NLS-1$

	/**
	 * XMLLoader constructor comment.
	 */
	public XSLModelLoader() {
		super();
	}

//	@Override
//	public List getAdapterFactories() {
//		List result = new ArrayList();
//		INodeAdapterFactory factory = null;
//		factory = new ModelQueryAdapterFactoryForXML();
//		result.add(factory);
//		// Does XML need propagating adapter? Or just JSP?
//		factory = new PropagatingAdapterFactoryImpl();
//		result.add(factory);
//		return result;
//	}

	@Override
	public IDocumentLoader getDocumentLoader() {
		if (documentLoaderInstance == null) {
			documentLoaderInstance = new XSLDocumentLoader();
		}
		return documentLoaderInstance;
	}

	@Override
	public IModelLoader newInstance() {
		return new XSLModelLoader();
	}

	@Override
	public IStructuredModel newModel() {
		return new DOMModelImpl();
	}

	@Override
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
