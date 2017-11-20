/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - bug 245772 - NLS Message refactoring
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.StylesheetViewer;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

/**
 * An action that opens a dialog to allow the user to select a file in the
 * workspace.
 * 
 * @author Doug Satchwell
 */
public class AddWorkspaceFileAction extends AbstractStylesheetAction {
	private final ISelectionStatusValidator validator = new ISelectionStatusValidator() {
		public IStatus validate(Object[] selection) {
			if (selection.length == 0) {
				return new Status(IStatus.ERROR, XSLDebugUIPlugin.PLUGIN_ID, 0,
						"", null); //$NON-NLS-1$
			}
			for (int i = 0; i < selection.length; i++) {
				if (!(selection[i] instanceof IFile)) {
					return new Status(IStatus.ERROR,
							XSLDebugUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
				}
			}
			return new Status(IStatus.OK, XSLDebugUIPlugin.PLUGIN_ID, 0,
					"", null); //$NON-NLS-1$
		}
	};

	/**
	 * Create a new instance of this.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public AddWorkspaceFileAction(StylesheetViewer viewer) {
		super(Messages.AddWorkspaceFileAction_Text, viewer);
	}

	@Override
	public void run() {

		// ViewerFilter filter= new StylesheetFilter(getSelectedJars());

		ILabelProvider lp = new WorkbenchLabelProvider();
		ITreeContentProvider cp = new WorkbenchContentProvider();

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), lp, cp);
		dialog.setValidator(validator);
		dialog.setTitle(Messages.AddWorkspaceFileAction_DialogTitle);
		dialog.setMessage(Messages.AddWorkspaceFileAction_DialogMessage);
		dialog.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (!(element instanceof IResource))
					return false;
				IResource resource = (IResource) element;
				if (resource.getType() == IResource.FILE) {
					if (!XSLCore.isXSLFile((IFile) resource))
						return false;
				}
				return true;
			}
		});
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

		if (dialog.open() == Window.OK) {
			Object[] elements = dialog.getResult();
			LaunchTransform[] res = new LaunchTransform[elements.length];
			for (int i = 0; i < res.length; i++) {
				IResource elem = (IResource) elements[i];
				res[i] = new LaunchTransform(elem.getFullPath()
						.toPortableString(), LaunchTransform.RESOURCE_TYPE);
			}
			addTransforms(res);
		}
	}

	@Override
	protected int getActionType() {
		return ADD;
	}
}
