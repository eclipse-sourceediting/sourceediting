/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.modelquery;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.EmbeddedTypeHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerUtility;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryAssociationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.ModelQueryImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.core.internal.ssemodelquery.MovableModelQuery;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * ModelQuery for JSP Tag files. Prioritizes the Tag content model and any
 * loaded tag libraries in the model before falling back to the embedded model
 * query, if one is found.
 */
public class TagModelQuery extends ModelQueryImpl implements ModelQuery, MovableModelQuery {
	/**
	 * The default mime-type for the embedded ModelQuery
	 */
	public static final String DEFAULT_MIMETYPE = "text/html";


	/**
	 * The prefix used with ProjectScope to find the (possibly user-set)
	 * embedded mime-type on a .tag file.
	 */
	private static final String PREFERENCE_PREFIX = "embeddedMimeType/";

	public static String createPreferenceKey(IPath fullPath) {
		return PREFERENCE_PREFIX + fullPath.removeFirstSegments(1).toString();
	}

	private ModelQuery fEmbeddedModelQuery;

	/**
	 * @param modelQueryAssociationProvider
	 */
	public TagModelQuery(ModelQueryAssociationProvider modelQueryAssociationProvider) {
		super(modelQueryAssociationProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.
	 * ModelQueryImpl#getCMElementDeclaration(org.w3c.dom.Element)
	 */
	public CMElementDeclaration getCMElementDeclaration(Element element) {
		CMElementDeclaration cmElementDeclaration = super.getCMElementDeclaration(element);
		if (cmElementDeclaration == null) {
			ModelQuery embeddedModelQuery = getEmbeddedModelQuery(element);
			if (embeddedModelQuery != null) {
				return embeddedModelQuery.getCMElementDeclaration(element);
			}
		}
		return cmElementDeclaration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.
	 * ModelQueryImpl#getCorrespondingCMDocument(org.w3c.dom.Node)
	 */
	public CMDocument getCorrespondingCMDocument(Node node) {
		CMDocument document = super.getCorrespondingCMDocument(node);
		if (document == null) {
			ModelQuery embeddedModelQuery = getEmbeddedModelQuery(node);
			if (embeddedModelQuery != null) {
				return embeddedModelQuery.getCorrespondingCMDocument(node);
			}
		}
		return document;
	}

	private String getEmbeddedMimeType(Node node) {
		String type = DEFAULT_MIMETYPE;
		if (node instanceof IDOMNode) {
			IStructuredModel model = ((IDOMNode) node).getModel();
			String baseLocation = model.getBaseLocation();
			if (!baseLocation.equals(IModelManager.UNMANAGED_MODEL)) {
				IPath path = new Path(baseLocation);
				if (path.segmentCount() > 1) {
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
					if (project.isAccessible()) {
						String key = createPreferenceKey(path);
						IEclipsePreferences preferences = new ProjectScope(project).getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName());
						type = preferences.get(key, DEFAULT_MIMETYPE);
					}
				}
			}
		}
		return type;
	}

	private ModelQuery getEmbeddedModelQuery(Node node) {
		if (fEmbeddedModelQuery == null) {
			String embeddedMimeType = getEmbeddedMimeType(node);
			if (embeddedMimeType != null) {
				EmbeddedTypeHandler embeddedContentTypeHandler = ModelHandlerUtility.getEmbeddedContentTypeFor(embeddedMimeType);
				if (embeddedContentTypeHandler != null) {
					List adapterFactories = embeddedContentTypeHandler.getAdapterFactories();
					for (int i = 0; i < adapterFactories.size(); i++) {
						INodeAdapterFactory factory = (INodeAdapterFactory) adapterFactories.get(i);
						if (factory.isFactoryForType(ModelQueryAdapter.class)) {
							INodeAdapter adapter = factory.adapt((INodeNotifier) node.getOwnerDocument());
							if (adapter instanceof ModelQueryAdapter) {
								fEmbeddedModelQuery = ((ModelQueryAdapter) adapter).getModelQuery();
							}
						}
					}
				}
			}
		}
		return fEmbeddedModelQuery;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.xml.core.internal.ssemodelquery.MovableModelQuery#setIdResolver(org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver)
	 */
	public void setIdResolver(URIResolver newURIResolver) {
		fEmbeddedModelQuery = null;
	}
}
