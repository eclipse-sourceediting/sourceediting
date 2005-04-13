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
package org.eclipse.wst.sse.ui.internal.debug;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.ui.extensions.breakpoint.IBreakpointProvider;
import org.eclipse.wst.sse.ui.internal.IExtendedMarkupEditor;
import org.eclipse.wst.sse.ui.internal.IExtendedSimpleEditor;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.extension.BreakpointProviderBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * ToggleBreakpointAction
 */
public class ToggleBreakpointAction extends BreakpointRulerAction {
	/**
	 * @param editor
	 * @param rulerInfo
	 */
	public ToggleBreakpointAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		super(editor, rulerInfo);
		setText(SSEUIMessages.ToggleBreakpointAction_0); //$NON-NLS-1$
	}

	protected boolean createBreakpoints(int lineNumber) {
		// Note: we'll always allow processing to continue, even
		// for a "read only" IStorageEditorInput, for the ActiveScript
		// debugger. But this means sometimes the ActiveScript provider
		// might get an input from CVS or something that is not related
		// to debugging.

		ITextEditor editor = getTextEditor();
		IEditorInput input = editor.getEditorInput();
		IDocument document = editor.getDocumentProvider().getDocument(input);
		if (document == null)
			return false;

		String contentType = getContentType(document);
		IBreakpointProvider[] providers = BreakpointProviderBuilder.getInstance().getBreakpointProviders(editor, contentType, getFileExtension(input));

		Document doc = null;
		Node node = null;
		if (editor instanceof IExtendedMarkupEditor) {
			doc = ((IExtendedMarkupEditor) editor).getDOMDocument();
			node = ((IExtendedMarkupEditor) editor).getCaretNode();
		}

		int pos = -1;
		if (editor instanceof IExtendedSimpleEditor) {
			pos = ((IExtendedSimpleEditor) editor).getCaretPosition();
		}

		final int n = providers.length;
		List errors = new ArrayList(0);
		for (int i = 0; i < n; i++) {
			try {
				if (Debug.debugBreakpoints)
					System.out.println(providers[i].getClass().getName() + " adding breakpoint to line " + lineNumber); //$NON-NLS-1$
				IStatus status = providers[i].addBreakpoint(doc, document, input, node, lineNumber, pos);
				if (status != null && !status.isOK()) {
					errors.add(status);
				}
			} catch (CoreException e) {
				errors.add(e.getStatus());
			} catch (Exception t) {
				Logger.logException("exception while adding breakpoint", t); //$NON-NLS-1$
			}
		}

		if (errors.size() > 0) {
			Shell shell = editor.getSite().getShell();
			MultiStatus allStatus = new MultiStatus(SSEUIPlugin.ID, IStatus.INFO, (IStatus[]) errors.toArray(new IStatus[0]), SSEUIMessages.ManageBreakpoints_error_adding_message1, null); //$NON-NLS-1$
			// show for conditions more severe than INFO or when no
			// breakpoints were created
			if (allStatus.getSeverity() > IStatus.INFO || getBreakpoints(getMarkers()).length < 1) {
				ErrorDialog.openError(shell, SSEUIMessages.ManageBreakpoints_error_adding_title1, SSEUIMessages.ManageBreakpoints_error_adding_message1, allStatus); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
		}
		return true;
	}

	protected String getContentType(IDocument document) {
		IModelManager mgr = StructuredModelManager.getModelManager();
		String contentType = null;
		IStructuredModel model = null;
		try {
			model = mgr.getExistingModelForRead(document);
			if (model != null) {
				contentType = model.getContentTypeIdentifier();
			}
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return contentType;
	}

	protected void removeBreakpoints(int lineNumber) {
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = getBreakpoints(getMarkers());
		for (int i = 0; i < breakpoints.length; i++) {
			try {
				breakpoints[i].getMarker().delete();
				breakpointManager.removeBreakpoint(breakpoints[i], true);
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}

	public void run() {
		int lineNumber = fRulerInfo.getLineOfLastMouseButtonActivity() + 1;
		boolean doAdd = !hasMarkers();
		if (doAdd)
			createBreakpoints(lineNumber);
		else
			removeBreakpoints(lineNumber);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		ITextEditor editor = getTextEditor();
		IEditorInput input = editor.getEditorInput();
		IDocument document = editor.getDocumentProvider().getDocument(input);
		if (document != null) {
			String contentType = getContentType(document);
			setEnabled(BreakpointProviderBuilder.getInstance().isAvailable(contentType, getFileExtension(input)));
		} else {
			setEnabled(false);
		}
	}
}
