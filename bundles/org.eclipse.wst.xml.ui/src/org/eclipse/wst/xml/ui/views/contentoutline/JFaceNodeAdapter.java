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
package org.eclipse.wst.xml.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.views.contentoutline.IJFaceNodeAdapterFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManagerListener;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.contentoutline.BufferedOutlineUpdater;
import org.eclipse.wst.xml.ui.internal.editor.CMImageUtil;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.w3c.dom.Node;

/**
 * Adapts a DOM node to a JFace viewer.
 */
public class JFaceNodeAdapter implements IJFaceNodeAdapter {

	public class CMDocumentManagerListenerImpl implements CMDocumentManagerListener {

		List beingRefreshed = Collections.synchronizedList(new ArrayList());

		public void cacheCleared(org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache cache) {

		}

		public void cacheUpdated(CMDocumentCache cache, final String uri, int oldStatus, int newStatus, CMDocument cmDocument) {

			if (newStatus == CMDocumentCache.STATUS_LOADED || newStatus == CMDocumentCache.STATUS_ERROR) {
				refreshViewers();
			}
		}

		private Display getDisplay() {

			return PlatformUI.getWorkbench().getDisplay();
		}

		public void propertyChanged(CMDocumentManager cmDocumentManager, String propertyName) {

			if (cmDocumentManager.getPropertyEnabled(CMDocumentManager.PROPERTY_AUTO_LOAD)) {
				refreshViewers();
			}
		}

		protected void refreshViewers() {

			// we're counting on getListers returning a "copy" of the
			// listeners, so we'll be thread safe.
			Collection listeners = ((IJFaceNodeAdapterFactory) adapterFactory).getListeners();
			Iterator iterator = listeners.iterator();
			while (iterator.hasNext()) {
				Object listener = iterator.next();
				// now that we use aynchExec, we ourselves have to gaurd
				// against
				// agains adding some refreshes when its already being
				// refreshed.
				if (listener instanceof PropertySheetPage && (!beingRefreshed.contains(listener))) {
					final PropertySheetPage propertySheetPage = (PropertySheetPage) listener;
					beingRefreshed.add(propertySheetPage);
					getDisplay().asyncExec(new Runnable() {

						public void run() {

							if (getDisplay().isDisposed()) {
								return;
							}
							if (propertySheetPage.getControl() != null && !propertySheetPage.getControl().isDisposed()) {
								propertySheetPage.refresh();
								beingRefreshed.remove(propertySheetPage);
							}
						}
					});
				}
			}
		}
	}

	final static Class ADAPTER_KEY = IJFaceNodeAdapter.class;

	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.sse.ui/debug/outline"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	protected IAdapterFactory adapterFactory;
	protected CMDocumentManagerListener cmDocumentManagerListener;
	private BufferedOutlineUpdater fUpdater = null;

	public JFaceNodeAdapter(IAdapterFactory adapterFactory) {

		super();
		this.adapterFactory = adapterFactory;
	}

	protected Image createImage(Object object) {

		Image image = null;
		Node node = (Node) object;
		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_ELEMENT);
				break;
			}
			case Node.ATTRIBUTE_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE);
				break;
			}
			case Node.TEXT_NODE : { // actually, TEXT should never be seen in
				// the tree
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_ELEMENT);
				break;
			}
			case Node.CDATA_SECTION_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_CDATASECTION);
				break;
			}
			case Node.ENTITY_REFERENCE_NODE :
			case Node.ENTITY_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_ENTITY);
				break;
			}
			case Node.PROCESSING_INSTRUCTION_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_PROCESSINGINSTRUCTION);
				break;
			}
			case Node.COMMENT_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_COMMENT);
				break;
			}
			case Node.DOCUMENT_TYPE_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_DOCTYPE);
				break;
			}
			case Node.NOTATION_NODE : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_NOTATION);
				break;
			}
			default : {
				image = createXMLImageDescriptor(XMLEditorPluginImages.IMG_OBJ_ELEMENT);
				break;
			}
		}
		return image;
	}

	protected Image createXMLImageDescriptor(String imageResourceName) {
		return XMLEditorPluginImageHelper.getInstance().getImage(imageResourceName);
	}

	public Object[] getChildren(Object object) {

		// (pa) 20021217
		// cmvc defect 235554
		// performance enhancement: using child.getNextSibling() rather than
		// nodeList(item) for O(n) vs. O(n*n)
		//
		Node node = (Node) object;
		ArrayList v = new ArrayList(node.getChildNodes().getLength());
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			Node n = child;
			if (n.getNodeType() != Node.TEXT_NODE)
				v.add(n);
		}
		return v.toArray();
	}

	/**
	 * Returns a CMDocumentManagerListener that can update JFace views when
	 * notified of CMDocumentManager events
	 */
	public org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManagerListener getCMDocumentManagerListener() {

		if (cmDocumentManagerListener == null)
			cmDocumentManagerListener = new CMDocumentManagerListenerImpl();
		return cmDocumentManagerListener;
	}

	Display getDisplay() {

		// Note: the workbench should always have a display
		// (unless running headless), whereas Display.getCurrent()
		// only returns the display if the currently executing thread
		// has one.
		if (PlatformUI.isWorkbenchRunning())
			return PlatformUI.getWorkbench().getDisplay();
		else
			return null;
	}

	/**
	 * Returns an enumeration with the elements belonging to the passed
	 * element. These are the top level items in a list, tree, table, etc...
	 */
	public Object[] getElements(Object node) {

		return getChildren(node);
	}

	/**
	 * Fetches the label image specific to this object instance.
	 */
	public Image getLabelImage(Object node) {

		Image image = CMImageUtil.getImage(CMImageUtil.getDeclaration((Node) node));
		if (image == null && JFaceResources.getImageRegistry() != null) {
			ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
			String nodeName = getNodeName(node);
			image = imageRegistry.get(nodeName);
			if (image == null) {
				image = createImage(node);
				if (image != null)
					imageRegistry.put(nodeName, image);
			}
		}
		return image;
	}

	/**
	 * Fetches the label text specific to this object instance.
	 */
	public String getLabelText(Object node) {

		return getNodeName(node);
	}

	private String getNodeName(Object object) {

		Node node = (Node) object;
		String nodeName = node.getNodeName();
		if (node.getNodeType() == Node.DOCUMENT_TYPE_NODE)
			nodeName = "DOCTYPE:" + nodeName; //$NON-NLS-1$
		return nodeName;
	}

	private BufferedOutlineUpdater getOutlineUpdater() {
		if (fUpdater == null)
			fUpdater = new BufferedOutlineUpdater();
		return fUpdater;
	}

	public Object getParent(Object object) {

		Node node = (Node) object;
		return node.getParentNode();
	}

	public boolean hasChildren(Object object) {

		// (pa) 20021217
		// cmvc defect 235554 > use child.getNextSibling() instead of
		//                          nodeList(item) for O(n) vs. O(n*n)
		Node node = (Node) object;
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != Node.TEXT_NODE)
				return true;
		}
		return false;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {

		return type.equals(ADAPTER_KEY);
	}

	/**
	 * Called by the object being adapter (the notifier) when something has
	 * changed.
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

		// future_TODO: the 'uijobs' used in this method were added to solve
		// threading problems when the dom
		// is updated in the background while the editor is open. They may be
		// a bit overkill and not that useful.
		// (That is, may be be worthy of job manager management). If they are
		// found to be important enough to leave in,
		// there's probably some optimization that can be done.
		Collection listeners = ((JFaceNodeAdapterFactory) adapterFactory).getListeners();
		Iterator iterator = listeners.iterator();

		while (iterator.hasNext()) {
			Object listener = iterator.next();
			if (notifier instanceof Node && (listener instanceof StructuredViewer) && (eventType == INodeNotifier.STRUCTURE_CHANGED || (eventType == INodeNotifier.CHANGE && changedFeature == null))) {

				if (DEBUG)
					System.out.println("JFaceNodeAdapter notified on event type > " + eventType);

				// refresh on structural and "unknown" changes
				StructuredViewer structuredViewer = (StructuredViewer) listener;
				// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5230
				if (structuredViewer.getControl() != null /*
														   * &&
														   * structuredViewer.getControl().isVisible()
														   */)
					getOutlineUpdater().processNode(structuredViewer, (Node) notifier);
			} else if ((listener instanceof PropertySheetPage) && ((eventType == INodeNotifier.CHANGE) || (eventType == INodeNotifier.STRUCTURE_CHANGED))) {
				PropertySheetPage propertySheetPage = (PropertySheetPage) listener;
				if (propertySheetPage.getControl() != null /*
														    * &&
														    * !propertySheetPage.getControl().isDisposed()
														    */) {
					RefreshPropertySheetJob refreshPropertySheetJob = new RefreshPropertySheetJob(getDisplay(), XMLUIPlugin.getResourceString("%JFaceNodeAdapter.1"), propertySheetPage); //$NON-NLS-1$
					refreshPropertySheetJob.schedule();
				}
			}
		}
	}
}
