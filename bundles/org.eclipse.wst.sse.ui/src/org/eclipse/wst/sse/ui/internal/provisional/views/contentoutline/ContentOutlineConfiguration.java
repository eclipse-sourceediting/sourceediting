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
package org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.wst.sse.ui.internal.IExtendedConfiguration;
import org.eclipse.wst.sse.ui.internal.view.events.NodeSelectionChangedEvent;

/**
 * Configuration class for Outline Pages.  Not finalized.
 * 
 * @since 1.0
 *  
 */
public class ContentOutlineConfiguration implements IExtendedConfiguration, IAdaptable {

	public final static String ID = "contentoutlineconfiguration"; //$NON-NLS-1$

	private IContentProvider fContentProvider;
	private String fDeclaringID = null;
	protected IDoubleClickListener fDoubleClickListener;
	private KeyListener[] fKeyListeners;
	private ILabelProvider fLabelProvider;

	public ContentOutlineConfiguration() {
		super();
		fDeclaringID = getClass().getName();
	}

	private IContentProvider createTreeContentProvider() {
		return new ITreeContentProvider() {
			public void dispose() {
				// do nothing
			}

			public Object[] getChildren(Object parentElement) {
				return new Object[0];
			}

			public Object[] getElements(Object inputElement) {
				return new Object[0];
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// do nothing
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * @param viewer
	 * @return the ITreeContentProvider to use with this viewer
	 */
	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null)
			fContentProvider = createTreeContentProvider();
		return fContentProvider;
	}

	/**
	 * @return Returns the declaringID, useful for remembering settings.
	 */
	public String getDeclaringID() {
		if (fDeclaringID == null)
			return "???"; //$NON-NLS-1$
		return fDeclaringID;
	}

	/**
	 * @param viewer
	 * @return the IDoubleClickListener to be notified when the viewer
	 *         receives a double click event
	 */
	public IDoubleClickListener getDoubleClickListener(TreeViewer viewer) {
		return null;
	}

	/**
	 * 
	 * @param viewer
	 * @return an array of KeyListeners to attach to the TreeViewer's Control.
	 *         The listeners should adhere to the KeyEvent.doit field to
	 *         ensure proper behaviors. Ordering of the event notifications is
	 *         dependent on the Control in the TreeViewer.
	 */
	public KeyListener[] getKeyListeners(TreeViewer viewer) {
		if (fKeyListeners == null)
			fKeyListeners = new KeyListener[0];
		return fKeyListeners;
	}

	/**
	 * @param viewer
	 * @return the ILabelProvider for items within the viewer
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null)
			fLabelProvider = new LabelProvider();
		return fLabelProvider;
	}

	/**
	 * @param viewer
	 * @return IContributionItem[] for the local menu
	 */
	public IContributionItem[] getMenuContributions(TreeViewer viewer) {
		return new IContributionItem[0];
	}

	/**
	 * @param viewer
	 * @return the IMenuListener to notify when the viewer's context menu is
	 *         about to be show
	 */
	public IMenuListener getMenuListener(TreeViewer viewer) {
		return null;
	}

	/**
	 * @param nodes
	 * @return The list of nodes from this List that should be seen in the
	 *         Outline. Possible uses include programmatic selection setting.
	 */
	public List getNodes(List nodes) {
		return nodes;
	}

	/**
	 * @param event
	 * @return The (filtered) list of selected nodes from this event. Uses
	 *         include mapping model selection onto elements provided by the
	 *         content provider. Should only return elements that will be
	 *         shown in the Tree Control.
	 */
	public List getSelectedNodes(NodeSelectionChangedEvent event) {
		return event.getSelectedNodes();
	}

	/**
	 * @param viewer
	 * @return the ISelectionChangedListener to notify when the viewer's
	 *         selection changes
	 */
	public ISelectionChangedListener getSelectionChangedListener(TreeViewer viewer) {
		return null;
	}

	/**
	 * @param viewer
	 * @return IContributionItem[] for the local toolbar
	 */
	public IContributionItem[] getToolbarContributions(TreeViewer viewer) {
		return new IContributionItem[0];
	}

	/**
	 * Adopted since you can't easily removeDragSupport from StructuredViewers
	 * 
	 * @param treeViewer
	 * @return
	 */
	public TransferDragSourceListener[] getTransferDragSourceListeners(TreeViewer treeViewer) {
		return new TransferDragSourceListener[0];
	}

	/**
	 * Adopted since you can't easily removeDropSupport from StructuredViewers
	 * 
	 * @param treeViewer
	 * @return
	 */
	public TransferDropTargetListener[] getTransferDropTargetListeners(TreeViewer treeViewer) {
		return new TransferDropTargetListener[0];
	}

	/**
	 * Should node selection changes affect selection in the TreeViewer?
	 * 
	 * @return
	 */
	public boolean isLinkedWithEditor(TreeViewer treeViewer) {
		return false;
	}

	/**
	 * @param declaringID
	 *            The declaringID to set.
	 */
	public void setDeclaringID(String declaringID) {
		fDeclaringID = declaringID;
	}

	/**
	 * General hook for resource releasing and listener removal when
	 * configurations change or the viewer is disposed of
	 * 
	 * @param viewer
	 */
	public void unconfigure(TreeViewer viewer) {
	}

}
