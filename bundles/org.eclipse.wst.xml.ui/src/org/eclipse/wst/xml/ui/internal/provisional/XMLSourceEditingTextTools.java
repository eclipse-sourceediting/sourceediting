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
package org.eclipse.wst.xml.ui.internal.provisional;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.NodeLocation;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.SourceEditingTextTools;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Implements SourceEditingTextTools interface
 */
public class XMLSourceEditingTextTools implements SourceEditingTextTools, INodeAdapter {

	protected class NodeLocationImpl implements NodeLocation {
		private IDOMNode node;

		public NodeLocationImpl(IDOMNode xmlnode) {
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

		IModelManager mm = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		try {
			model = mm.getExistingModelForRead((IFile) res);
			if (model == null || !(model instanceof IDOMModel))
				return null;

			return ((IDOMModel) model).getDocument();
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
		if (node.getNodeType() == Node.ELEMENT_NODE && node instanceof IDOMNode)
			return new NodeLocationImpl((IDOMNode) node);
		return null;
	}

	public String getPageLanguage(Node node) {
		return ""; //$NON-NLS-1$
	}

	public int getStartOffset(Node node) {
		if (node == null || !(node instanceof IDOMText))
			return -1;

		IStructuredDocumentRegion fnode = ((IDOMText) node).getFirstStructuredDocumentRegion();
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
