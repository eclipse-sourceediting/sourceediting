/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/


package org.eclipse.wst.xml.ui.internal.tabletree;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.sse.core.internal.provisional.AbstractAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapterFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManagerListener;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.util.SharedXMLEditorPluginImageHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


public class XMLTableTreeContentProvider implements ITreeContentProvider, ITableLabelProvider, ILabelProvider, CMDocumentManagerListener {

	ViewerNotifyingAdapterFactory viewerNotifyingAdapterFactory = new ViewerNotifyingAdapterFactory();
	List viewerList = new Vector();
	private TreeContentHelper treeContentHelper = new TreeContentHelper();

	protected CMDocumentManager documentManager;

	public XMLTableTreeContentProvider() {
		super();
	}

	public void addViewer(Viewer viewer) {
		viewerList.add(viewer);
	}

	public Object[] getChildren(Object element) {
		viewerNotifyingAdapterFactory.doAdapt(element);
		return treeContentHelper.getChildren(element);
	}

	public Object getParent(Object o) {
		Object result = null;
		if (o instanceof Node) {
			Node node = (Node) o;
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				result = ((Attr) node).getOwnerElement();
			}
			else {
				result = node.getParentNode();
			}
		}
		return result;
	}

	public boolean hasChildren(Object element) {
		viewerNotifyingAdapterFactory.doAdapt(element);
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object element) {
		viewerNotifyingAdapterFactory.doAdapt(element);
		return getChildren(element);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// remove our listeners to the old state
		if (oldInput != null) {
			Document domDoc = (Document) oldInput;
			ModelQuery mq = ModelQueryUtil.getModelQuery(domDoc);
			if (mq != null) {
				documentManager = mq.getCMDocumentManager();
				if (documentManager != null) {
					documentManager.removeListener(this);
				}
			}
		}

		if (oldInput != null && oldInput instanceof IDOMDocument) {
			IJFaceNodeAdapterFactory factory = (IJFaceNodeAdapterFactory) ((IDOMDocument) oldInput).getModel().getFactoryRegistry().getFactoryFor(IJFaceNodeAdapter.class);
			if (factory != null) {
				factory.removeListener(viewer);
			}
		}

		if (newInput != null && newInput instanceof IDOMDocument) {
			IJFaceNodeAdapterFactory factory = (IJFaceNodeAdapterFactory) ((IDOMDocument) newInput).getModel().getFactoryRegistry().getFactoryFor(IJFaceNodeAdapter.class);
			if (factory != null) {
				factory.addListener(viewer);
			}
		}

		if (newInput != null) {
			Document domDoc = (Document) newInput;
			ModelQuery mq = ModelQueryUtil.getModelQuery(domDoc);

			if (mq != null) {
				documentManager = mq.getCMDocumentManager();
				if (documentManager != null) {
					documentManager.setPropertyEnabled(CMDocumentManager.PROPERTY_ASYNC_LOAD, true);
					documentManager.addListener(this);
				}
			}
		}
	}

	public boolean isDeleted(Object element) {
		return element != null;
	}

	//
	// ILabelProvider stuff
	//
	public void addListener(ILabelProviderListener listener) {
		// since we always return 'false' for "isLabelProperty",
		// not need to listen. Maybe that should change some day?
	}

	public void dispose() {
		viewerList = new Vector();
	}

	public Element getRootElement(Document document) {
		Element rootElement = null;

		for (Node childNode = document.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				rootElement = (Element) childNode;
				break;
			}
		}
		return rootElement;
	}

	public Image getImage(Object object) {
		viewerNotifyingAdapterFactory.doAdapt(object);
		Image image = null;
		if (object instanceof Node) {
			Node node = (Node) object;
			switch (node.getNodeType()) {
				case Node.ATTRIBUTE_NODE : {
					image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ATTRIBUTE);
					break;
				}
				case Node.CDATA_SECTION_NODE : {
					image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_CDATASECTION);
					break;
				}
				case Node.COMMENT_NODE : {
					image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_COMMENT);
					break;
				}
				case Node.DOCUMENT_TYPE_NODE : {
					image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_DOCTYPE);
					break;
				}
				case Node.ELEMENT_NODE : {
					image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ELEMENT);
					break;
				}
				case Node.PROCESSING_INSTRUCTION_NODE : {
					image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_PROCESSINGINSTRUCTION);
					break;
				}
				case Node.TEXT_NODE : {
					image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_TXTEXT);
					break;
				}
				case Node.ENTITY_REFERENCE_NODE : {
					image = image = SharedXMLEditorPluginImageHelper.getImage(SharedXMLEditorPluginImageHelper.IMG_OBJ_ENTITY_REFERENCE);
					break;
				}
			}

			// if (image != null) {
			// Image markerOverlayImage =
			// overlayIconManager.getOverlayImageForObject(node);
			// if (markerOverlayImage != null) {
			// image = imageFactory.createCompositeImage(image,
			// markerOverlayImage, ImageFactory.BOTTOM_LEFT);
			// }
			// }
		}
		return image;
	}

	public String getText(Object object) {
		viewerNotifyingAdapterFactory.doAdapt(object);
		String result = null;
		if (object instanceof Node) {
			Node node = (Node) object;
			switch (node.getNodeType()) {
				case Node.ATTRIBUTE_NODE : {
					result = node.getNodeName();
					break;
				}
				case Node.DOCUMENT_TYPE_NODE : {
					result = "DOCTYPE"; //$NON-NLS-1$
					break;
				}
				case Node.ELEMENT_NODE : {
					result = node.getNodeName();
					break;
				}
				case Node.PROCESSING_INSTRUCTION_NODE : {
					result = ((ProcessingInstruction) node).getTarget();
					break;
				}
			}
		}
		return result != null ? result : ""; //$NON-NLS-1$
	}

	//
	// ITableLabelProvider stuff
	//
	public String getColumnText(Object object, int column) {
		viewerNotifyingAdapterFactory.doAdapt(object);
		String result = null;
		if (column == 0) {
			result = getText(object);
		}
		else if (column == 1 && object instanceof Node) {
			result = treeContentHelper.getNodeValue((Node) object);
		}
		return result != null ? result : ""; //$NON-NLS-1$
	}

	public Image getColumnImage(Object object, int columnIndex) {
		viewerNotifyingAdapterFactory.doAdapt(object);
		return (columnIndex == 0) ? getImage(object) : null;
	}

	public boolean isLabelProperty(Object object, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// since we always return 'false' for "isLabelProperty",
		// not need to listen. Maybe that should change some day?
	}

	// There is only 1 adapter associated with this factory. This single
	// adapter gets added
	// to the adapter lists of many nodes.
	public class ViewerNotifyingAdapterFactory extends AbstractAdapterFactory {
		protected ViewerNotifyingAdapter viewerNotifyingAdapter = new ViewerNotifyingAdapter();

		protected INodeAdapter createAdapter(INodeNotifier target) {
			return viewerNotifyingAdapter;
		}

		protected ViewerNotifyingAdapter doAdapt(Object object) {
			ViewerNotifyingAdapter result = null;
			if (object instanceof INodeNotifier) {
				result = (ViewerNotifyingAdapter) adapt((INodeNotifier) object);
			}
			return result;
		}
	}

	public class ViewerNotifyingAdapter implements INodeAdapter {
		public boolean isAdapterForType(Object type) {
			return type.equals(viewerNotifyingAdapterFactory);
		}

		public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
			switch (eventType) {
				// case INodeNotifier.ADD: // ignore
				// case INodeNotifier.REMOVE: // ignore
				case INodeNotifier.CHANGE :
				case INodeNotifier.STRUCTURE_CHANGED :
				case INodeNotifier.CONTENT_CHANGED : {
					Node node = (Node) notifier;
					if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.DOCUMENT_NODE) {
						for (Iterator i = viewerList.iterator(); i.hasNext();) {
							Viewer viewer = (Viewer) i.next();

							if (viewer instanceof StructuredViewer) {
								((StructuredViewer) viewer).refresh(node);
							}
							else {
								// todo... consider doing a time delayed
								// refresh here!!
								viewer.refresh();
							}
						}
					}
					break;
				}
			}
		}
	}

	// the following methods handle filtering aspects of the viewer
	//
	//
	public boolean isIgnorableText(Node node) {
		boolean result = false;
		try {
			if (node.getNodeType() == Node.TEXT_NODE) {
				String data = ((Text) node).getData();
				result = (data == null || data.trim().length() == 0);
			}
		}
		catch (Exception e) {
			Logger.logException(e);
		}
		return result;
	}

	public static Text getHiddenChildTextNode(Node node) {
		return null;
	}

	// CMDocumentManagerListener
	//
	public void cacheCleared(CMDocumentCache cache) {
		doDelayedRefreshForViewers();
	}

	public void cacheUpdated(CMDocumentCache cache, final String uri, int oldStatus, int newStatus, CMDocument cmDocument) {
		if (newStatus == CMDocumentCache.STATUS_LOADED || newStatus == CMDocumentCache.STATUS_ERROR) {
			doDelayedRefreshForViewers();
		}
	}

	public void propertyChanged(CMDocumentManager cmDocumentManager, String propertyName) {
		if (cmDocumentManager.getPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD)) {
			doDelayedRefreshForViewers();
		}
	}

	protected void doDelayedRefreshForViewers() {
		List list = new Vector();
		list.addAll(viewerList);

		for (Iterator i = list.iterator(); i.hasNext();) {
			final Viewer viewer = (Viewer) i.next();
			Control control = viewer.getControl();
			Runnable runnable = new Runnable() {
				public void run() {
					viewer.refresh();
				}
			};
			// we need to ensure that this is run via 'asyncExec' since these
			// notifications can come from a non-ui thread
			control.getDisplay().asyncExec(runnable);
		}
	}
}