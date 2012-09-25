/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.debug;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider;

public class JSBreakpointProvider implements IBreakpointProvider {

	private static final String SCRIPT_REGION = "org.eclipse.wst.html.SCRIPT"; //$NON-NLS-1$
	private static final String EXTENSION_POINT = "org.eclipse.wst.jsdt.web.ui.breakpointAdapter";
	IToggleBreakpointsTargetExtension[] fDelegates;
	private IEditorPart fPart;
	private ITextSelection fSelection;

	public JSBreakpointProvider() {
	}

	IToggleBreakpointsTargetExtension[] getDelegates() {
		if (fDelegates == null) {
			IConfigurationElement[] configurationElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);
			List adapters = new ArrayList();
			for (int i = 0; i < configurationElements.length; i++) {
				Object o = null;
				try {
					o = configurationElements[i].createExecutableExtension("class");
				}
				catch (CoreException e) {
					Logger.logException(e);
				}
				if (o instanceof IToggleBreakpointsTargetExtension) {
					adapters.add(o);
				}
			}
			fDelegates = (IToggleBreakpointsTargetExtension[]) adapters.toArray(new IToggleBreakpointsTargetExtension[adapters.size()]);
		}
		return fDelegates;
	}

	public IStatus addBreakpoint(IDocument document, IEditorInput input, int editorLineNumber, int offset) throws CoreException {
		final IResource resource = getResource(input);
		IWorkbenchPart part = fPart == null ? getPart(input) : fPart;
		if (resource != null && offset > -1 && part != null) {
			IToggleBreakpointsTargetExtension[] delegates = getDelegates();
			if (delegates.length > 0) {
				try {
					ITextSelection selection = fSelection != null ? fSelection : new TextSelection(document.getLineOffset(editorLineNumber - 1) + offset, 1);
					ITypedRegion[] partitions = document.computePartitioning(selection.getOffset(), selection.getLength());
					boolean scriptSectionFound = false;
					for (int i = 0; i < partitions.length; i++) {
						scriptSectionFound |= SCRIPT_REGION.equals(partitions[i].getType());
					}
					if (scriptSectionFound) {
						for (int i = 0; i < delegates.length; i++) {
							delegates[i].toggleLineBreakpoints(part, selection);
						}
					}
				}
				catch (BadLocationException e) {
				}
			}
		}
		fPart = null;
		fSelection = null;
		return Status.OK_STATUS;
	}

	private IWorkbenchPart getPart(IEditorInput input) throws PartInitException {
		IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < workbenchWindows.length; i++) {
			IWorkbenchPage[] pages = workbenchWindows[i].getPages();
			for (int j = 0; j < pages.length; j++) {
				IEditorReference[] editorReferences = pages[j].getEditorReferences();
				for (int k = 0; k < editorReferences.length; k++) {
					if (editorReferences[k].getEditorInput() == input)
						return editorReferences[k].getPart(false);
				}
			}
		}
		return null;
	}

	public IResource getResource(IEditorInput input) {
		IResource resource = (IResource) input.getAdapter(IFile.class);
		if (resource == null) {
			resource = (IResource) input.getAdapter(IResource.class);
		}
		if (resource == null) {
			resource = ResourcesPlugin.getWorkspace().getRoot();
		}
		return resource;
	}

	public void setSourceEditingTextTools(ISourceEditingTextTools tool) {
		if (tool != null) {
			fPart = tool.getEditorPart();
			fSelection = tool.getSelection();
		}
	}

}
