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
package org.eclipse.jst.jsp.core.modelquery;


import java.util.List;

import org.eclipse.jst.jsp.core.PageDirectiveAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.sse.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.sse.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.sse.core.internal.contentmodel.CMNode;
import org.eclipse.wst.sse.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.internal.contentmodel.modelqueryimpl.ModelQueryImpl;
import org.eclipse.wst.sse.core.modelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JSPModelQueryImpl extends ModelQueryImpl {

	protected IStructuredModel jspModel = null;
	protected ModelQuery embeddedModelQuery = null;
	public JSPModelQueryImpl(IStructuredModel model, IdResolver resolver) {
		super(new JSPModelQueryAssociationProvider());
		jspModel = model;
	}

	/*
	 * @see ModelQuery#getCMElementDeclaration(Element)
	 */
	public CMElementDeclaration getCMElementDeclaration(Element element) {
		CMElementDeclaration result = super.getCMElementDeclaration(element);
		if (result == null) {
			ModelQuery query = getEmbeddedModelQuery(element);
			if (query != null) {
				result = query.getCMElementDeclaration(element);
			}
		}
		return result;
	}

	/*
	 * @see ModelQuery#getCorrespondingCMDocument(Node)
	 */
	public CMDocument getCorrespondingCMDocument(Node node) {
		CMDocument doc = super.getCorrespondingCMDocument(node);
		if (doc == null) {
			ModelQuery query = getEmbeddedModelQuery(node);
			if (query != null) {
				doc = query.getCorrespondingCMDocument(node);
			}
		}
		return doc;
	}

	/*
	 * @see ModelQuery#getCMNode(Node)
	 */
	public CMNode getCMNode(Node node) {
		CMNode result = super.getCMNode(node);
		if (result == null) {
			ModelQuery query = getEmbeddedModelQuery(node);
			if (query != null) {
				result = query.getCMNode(node);
			}
		}
		return result;
	}

	public List getAvailableContent(Element element, CMElementDeclaration ed, int includeOptions) {
		ModelQuery emq = getEmbeddedModelQuery(element);
		if (emq != null)
			return emq.getAvailableContent(element, ed, includeOptions);
		else
			return super.getAvailableContent(element, ed, includeOptions);
	}

	protected ModelQuery getEmbeddedModelQuery(Node node) {
		if (this.embeddedModelQuery == null && node instanceof INodeNotifier) {
			Node ownerNode = node.getOwnerDocument();
			if (ownerNode == null) {
				// then must be the document itself
				ownerNode = node;
			}
			PageDirectiveAdapter typeadapter = (PageDirectiveAdapter) ((INodeNotifier) ownerNode).getAdapterFor(PageDirectiveAdapter.class);
			if (typeadapter != null) {
				ModelQueryAdapter embeddedAdapter = (ModelQueryAdapter) typeadapter.adapt((INodeNotifier) node, ModelQueryAdapter.class);
				if (embeddedAdapter != null)
					this.embeddedModelQuery = embeddedAdapter.getModelQuery();
			}
		}
		return this.embeddedModelQuery;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.core.internal.contentmodel.modelquery.ModelQuery#getCMAttributeDeclaration(org.w3c.dom.Attr)
	 */
	public CMAttributeDeclaration getCMAttributeDeclaration(Attr attr) {
		CMAttributeDeclaration result = super.getCMAttributeDeclaration(attr);
		if (result == null) {
			ModelQuery query = getEmbeddedModelQuery(attr);
			if (query != null) {
				result = query.getCMAttributeDeclaration(attr);
			}
		}
		return result;
	}
}