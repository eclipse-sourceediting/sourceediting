/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xml.xpath.ui.internal.Messages;
import org.eclipse.wst.xml.xpath.ui.internal.XPathUIPlugin;

class XPathViewActions {
	private static final ImageDescriptor COLLAPSE_D = AbstractUIPlugin
			.imageDescriptorFromPlugin(XPathUIPlugin.PLUGIN_ID,
					"icons/full/dlcl16/collapseall.gif");
	private static final ImageDescriptor COLLAPSE_E = AbstractUIPlugin
			.imageDescriptorFromPlugin(XPathUIPlugin.PLUGIN_ID,
					"icons/full/elcl16/collapseall.gif");
	private static final ImageDescriptor SYNCED_D = AbstractUIPlugin
			.imageDescriptorFromPlugin(XPathUIPlugin.PLUGIN_ID,
					"icons/full/dlcl16/synced.gif");
	private static final ImageDescriptor SYNCED_E = AbstractUIPlugin
			.imageDescriptorFromPlugin(XPathUIPlugin.PLUGIN_ID,
					"icons/full/elcl16/synced.gif");

	private boolean linkWithEditor = false;
	private CollapseTreeAction collapseAction;
	private ToggleLinkAction toggleAction;

	protected IAction[] createMenuContributions(TreeViewer viewer) {
		return new IAction[] {};
	}

	protected IAction[] createToolbarContributions(TreeViewer viewer) {
		this.collapseAction = new CollapseTreeAction(viewer);
		this.toggleAction = new ToggleLinkAction();
		toggleAction.setChecked(isLinkWithEditor());
		return new IAction[] { collapseAction, toggleAction };
	}

	public ISelection getSelection(TreeViewer viewer, ISelection selection) {
		return selection;
	}

	public boolean isLinkedWithEditor(TreeViewer treeViewer) {
		return isLinkWithEditor();
	}

	void setLinkWithEditor(boolean isLinkWithEditor) {
		linkWithEditor = isLinkWithEditor;
	}

	void fillContextMenu(IMenuManager manager) {
		manager.add(collapseAction);
		manager.add(toggleAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public boolean isLinkWithEditor() {
		return linkWithEditor;
	}

	private class CollapseTreeAction extends Action {
		private final TreeViewer fTreeViewer;

		public CollapseTreeAction(TreeViewer viewer) {
			super(Messages.XPathViewActions_0, AS_PUSH_BUTTON);
			setImageDescriptor(COLLAPSE_E);
			setDisabledImageDescriptor(COLLAPSE_D);
			setToolTipText(getText());
			fTreeViewer = viewer;
		}

		public void run() {
			fTreeViewer.collapseAll();
		}
	}

	private class ToggleLinkAction extends Action {
		public ToggleLinkAction() {
			super(Messages.XPathViewActions_1, AS_CHECK_BOX);
			setToolTipText(getText());
			setDisabledImageDescriptor(SYNCED_D);
			setImageDescriptor(SYNCED_E);
		}

		public void run() {
			setLinkWithEditor(isChecked());
		}
	}
}
