/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.extensions;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.extensions.breakpoint.NodeLocation;
import org.eclipse.wst.sse.ui.extensions.breakpoint.SourceEditingTextTools;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.document.XMLText;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Implements SourceEditingTextTools interface
 */
public class XMLSourceEditingTextTools implements SourceEditingTextTools, INodeAdapter {

	protected class NodeLocationImpl implements NodeLocation {
		private XMLNode node;

		public NodeLocationImpl(XMLNode xmlnode) {
			super();
			node = xmlnode;
		}

		public int getEndTagEndOffset() {
			if (node.getEndStructuredDocumentRegion() != null)
				return node.getEndStructuredDocumentRegion().getEndOffset();
			return -1;
		}

		public int getEndTagStartOffset() {
			if (node.getEndStructuredDocumentRegion() != null)
				return node.getEndStructuredDocumentRegion().getStartOffset();
			return -1;
		}

		public int getStartTagEndOffset() {
			if (node.getStartStructuredDocumentRegion() != null)
				return node.getStartStructuredDocumentRegion().getEndOffset();
			return -1;
		}

		public int getStartTagStartOffset() {
			if (node.getStartStructuredDocumentRegion() != null)
				return node.getStartStructuredDocumentRegion().getStartOffset();
			return -1;
		}
	}


	public Document getDOMDocument(IMarker marker) {
		if (marker == null)
			return null;

		IResource res = marker.getResource();
		if (res == null || !(res instanceof IFile))
			return null;

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		IModelManager mm = plugin.getModelManager();
		IStructuredModel model = null;
		try {
			model = mm.getExistingModelForRead((IFile) res);
			if (model == null || !(model instanceof XMLModel))
				return null;

			return ((XMLModel) model).getDocument();
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extensions.SourceEditingTextTools#getNodeLocation(org.w3c.dom.Node)
	 */
	public NodeLocation getNodeLocation(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE && node instanceof XMLNode)
			return new NodeLocationImpl((XMLNode) node);
		return null;
	}

	public String getPageLanguage(Node node) {
		return ""; //$NON-NLS-1$
	}

	public int getStartOffset(Node node) {
		if (node == null || !(node instanceof XMLText))
			return -1;

		IStructuredDocumentRegion fnode = ((XMLText) node).getFirstStructuredDocumentRegion();
		return fnode.getStartOffset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.core.INodeAdapter#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return SourceEditingTextTools.class.equals(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.core.INodeAdapter#notifyChanged(org.eclipse.wst.sse.core.core.INodeNotifier,
	 *      int, java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}
}
