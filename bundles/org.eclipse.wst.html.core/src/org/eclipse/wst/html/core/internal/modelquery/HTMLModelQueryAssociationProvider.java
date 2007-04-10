/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.modelquery;



import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.SimpleAssociationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;

/**
 */
public class HTMLModelQueryAssociationProvider extends SimpleAssociationProvider {

	/**
	 * @param modelQueryCMProvider org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider
	 */
	public HTMLModelQueryAssociationProvider(CMDocumentCache cache, URIResolver idResolver) {
		super(new HTMLModelQueryCMProvider(cache, idResolver));
	}

	// MIWA: We cannot cache a CMElementDeclaration any more.  Because, when the DOCTYPE
	// was changed, CMDocument would be changed.  Then, a cached CMElementDeclaration
	// would be invalid.  If some performance problems occurs, we consider a smarter
	// cache mechanism.
	//
	//	public CMElementDeclaration getCMElementDeclaration(Element element) {
	//		// check if element declaration is cached
	//		INodeNotifier notifier = (INodeNotifier) element;
	//		ElementDeclarationAdapter adapter = (ElementDeclarationAdapter) notifier.getExistingAdapter(ElementDeclarationAdapter.class);
	//		if (adapter != null)
	//			return adapter.getDeclaration();
	//
	//		CMElementDeclaration decl = super.getCMElementDeclaration(element);
	//
	//		// cache HTML element declaration only
	//		if (decl != null && decl instanceof HTMLElementDeclaration) {
	//			IAdapterFactory factory = ElementDeclarationAdapterFactory.getInstance();
	//			adapter = (ElementDeclarationAdapter) factory.adapt(notifier);
	//			if (adapter != null)
	//				adapter.setDeclaration(decl);
	//		}
	//
	//		return decl;
	//	}
}
