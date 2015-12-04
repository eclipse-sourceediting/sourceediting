/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter
 *                                           modified in order to process JSON Objects.               
 *******************************************************************************/
package org.eclipse.wst.json.ui.internal.contentoutline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.ui.internal.editor.JSONEditorPluginImageHelper;
import org.eclipse.wst.json.ui.internal.editor.JSONEditorPluginImages;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;

/**
 * Adapts a JSON node to a JFace viewer.
 */
public class JFaceNodeAdapter implements IJFaceNodeAdapter,
		IStyledJFaceNodeAdapter {

	final static Class ADAPTER_KEY = IJFaceNodeAdapter.class;

	/**
	 * debug .option
	 */
	private static final boolean DEBUG = getDebugValue();

	private static boolean getDebugValue() {
		String value = Platform
				.getDebugOption("org.eclipse.wst.sse.ui/debug/outline"); //$NON-NLS-1$
		boolean result = (value != null) && value.equalsIgnoreCase("true"); //$NON-NLS-1$
		return result;
	}

	JFaceNodeAdapterFactory fAdapterFactory;
	RefreshStructureJob fRefreshJob = null;

	public JFaceNodeAdapter(JFaceNodeAdapterFactory adapterFactory) {
		super();
		this.fAdapterFactory = adapterFactory;
	}

	public Object[] getChildren(Object object) {

		// (pa) 20021217
		// cmvc defect 235554
		// performance enhancement: using child.getNextSibling() rather than
		// nodeList(item) for O(n) vs. O(n*n)
		//
		ArrayList v = new ArrayList();
		if (object instanceof IJSONNode) {
			IJSONNode node = (IJSONNode) object;
			if (node.getNodeType() == IJSONNode.PAIR_NODE) {
				node = ((IJSONPair) node).getValue();
			}
			if (node != null) {
				// if (node.getNodeType() == IJSONNode.OBJECT_NODE) {
				// List<IJSONPair> pairs = ((IJSONObject) node).getPairs();
				// v.addAll(pairs);
				// } else {
				for (IJSONNode child = node.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					IJSONNode n = child;
					// if (n.getNodeType() != Node.TEXT_NODE) {
					v.add(n);
					// }
				}
			}
			// }
		}
		return v.toArray();
	}

	/**
	 * Returns an enumeration with the elements belonging to the passed element.
	 * These are the top level items in a list, tree, table, etc...
	 */
	public Object[] getElements(Object node) {
		return getChildren(node);
	}

	/**
	 * Fetches the label image specific to this object instance.
	 */
	public Image getLabelImage(Object node) {
		Image image = null;
		if (node instanceof IJSONNode) {
			// check for an image from the content model
			image = null;// CMImageUtil.getImage(CMImageUtil.getDeclaration((Node)
							// node));
			if (image == null) {
				/*
				 * Create/get image based on Node type. Images are cached
				 * transparently in this class, subclasses must do this for
				 * themselves if they're going to return their own results.
				 */
				image = createImage((IJSONNode) node);
			}
		}
		return image;
	}

	protected Image createImage(IJSONNode node) {
		Image image = null;
		switch (node.getNodeType()) {
		case IJSONNode.PAIR_NODE: {
			IJSONPair pair = (IJSONPair) node;
			short nodeType = pair.getNodeValueType();
			image = JSONEditorPluginImageHelper.getInstance()
					.getImage(nodeType);
			break;
		}
		default:
			image = JSONEditorPluginImageHelper.getInstance().getImage(
					node.getNodeType());
		}
		return image;
	}

	/**
	 * Fetches the label text specific to this object instance.
	 */
	public String getLabelText(Object node) {
		return getStyledLabelText(node).getString();
	}

	@Override
	public StyledString getStyledLabelText(Object element) {
		StyledString styledString = new StyledString();
		if (element instanceof IJSONNode) {
			IJSONNode node = (IJSONNode) element;
			switch (node.getNodeType()) {
			case IJSONNode.PAIR_NODE:
				IJSONPair pair = ((IJSONPair) node);
				String name = pair.getName();
				if (name != null) {
					styledString.append(name);
					String value = pair.getSimpleValue();
					if (value != null) {
						styledString.append(" : ");
						Styler styler = fAdapterFactory.getStyler(pair
								.getValueRegionType());
						styledString.append(value, styler);
					}
				}
				break;
			case IJSONNode.VALUE_BOOLEAN_NODE:
			case IJSONNode.VALUE_NULL_NODE:
			case IJSONNode.VALUE_NUMBER_NODE:
			case IJSONNode.VALUE_STRING_NODE:
				String value = ((IJSONValue) node).getSimpleValue();
				if (value != null) {
					styledString.append(" : ");
					Styler styler = fAdapterFactory
							.getStyler(((IJSONValue) node).getValueRegionType());
					styledString.append(value, styler);
				}
				break;
			}
		}
		return styledString;
	}

	public Object getParent(Object object) {
		if (object instanceof IJSONNode) {
			IJSONNode node = (IJSONNode) object;
			return node.getParentOrPairNode();
		}
		return null;
	}

	private synchronized RefreshStructureJob getRefreshJob() {
		if (fRefreshJob == null) {
			fRefreshJob = new RefreshStructureJob();
		}
		return fRefreshJob;
	}

	public boolean hasChildren(Object object) {
		// (pa) 20021217
		// cmvc defect 235554 > use child.getNextSibling() instead of
		// nodeList(item) for O(n) vs. O(n*n)
		IJSONNode node = (IJSONNode) object;
		if (node.getNodeType() == IJSONNode.PAIR_NODE) {
			node = ((IJSONPair) node).getValue();
		}
		if (node == null) {
			return false;
		}
		// if (node.getNodeType() == IJSONNode.OBJECT_NODE) {
		// return ((IJSONObject) node).getPairs().size() > 0;
		// }
		for (IJSONNode child = node.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			// if (child.getNodeType() != Node.TEXT_NODE) {
			return true;
			// }
		}
		return false;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it to
	 * return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		if (type == null) {
			return false;
		}
		return type.equals(ADAPTER_KEY);
	}

	/**
	 * Called by the object being adapter (the notifier) when something has
	 * changed.
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType,
			Object changedFeature, Object oldValue, Object newValue, int pos) {
		// future_TODO: the 'uijobs' used in this method were added to solve
		// threading problems when the dom
		// is updated in the background while the editor is open. They may be
		// a bit overkill and not that useful.
		// (That is, may be be worthy of job manager management). If they are
		// found to be important enough to leave in,
		// there's probably some optimization that can be done.
		if (notifier instanceof IJSONNode) {
			Collection listeners = fAdapterFactory.getListeners();
			Iterator iterator = listeners.iterator();

			while (iterator.hasNext()) {
				Object listener = iterator.next();
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=90637
				// if (notifier instanceof Node && (listener instanceof
				// StructuredViewer) && (eventType ==
				// INodeNotifier.STRUCTURE_CHANGED || (eventType ==
				// INodeNotifier.CHANGE && changedFeature == null))) {
				if ((listener instanceof StructuredViewer)
						&& ((eventType == INodeNotifier.STRUCTURE_CHANGED)
								|| (eventType == INodeNotifier.CONTENT_CHANGED) || (eventType == INodeNotifier.CHANGE))) {
					if (DEBUG) {
						System.out
								.println("JFaceNodeAdapter notified on event type > " + eventType); //$NON-NLS-1$
					}

					// refresh on structural and "unknown" changes
					StructuredViewer structuredViewer = (StructuredViewer) listener;
					// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5230
					if (structuredViewer.getControl() != null) {
						getRefreshJob().refresh(structuredViewer,
								(IJSONNode) notifier);
					}
				}
			}
		}
	}

}
