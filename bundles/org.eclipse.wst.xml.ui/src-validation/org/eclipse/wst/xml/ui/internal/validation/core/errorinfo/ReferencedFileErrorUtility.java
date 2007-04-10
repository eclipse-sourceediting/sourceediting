/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.validation.core.errorinfo;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.xml.core.internal.validation.core.logging.LoggerFactory;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;


public class ReferencedFileErrorUtility {
	public static void openEditorAndGotoError(String uristring, final int line, final int column) {
		if (uristring != null) {
			try {
				URL uri = new URL(uristring);
				if (uri != null) {
					if ("file".equals(uri.getProtocol())) //$NON-NLS-1$
					{
						String pathString = uri.getPath();
						IPath path = new Path(pathString);
						String device = path.getDevice();
						if ((device != null) && device.startsWith("/")) //$NON-NLS-1$
						{
							path = path.setDevice(device.substring(1));
						}
						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
						if ((file != null) && file.exists()) {
							// WorkbenchUtility.openEditor(file);
							// Open the editor for this file.
							final IFile iFile = file;
							IWorkbench workbench = XMLUIPlugin.getInstance().getWorkbench();
							final IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();

							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									try {
										IEditorDescriptor descriptor = XMLUIPlugin.getInstance().getWorkbench().getEditorRegistry().getDefaultEditor(iFile.getName());
										String editorId;
										if (descriptor != null) {
											editorId = descriptor.getId();
										}
										else {
											editorId = XMLUIPlugin.getInstance().getWorkbench().getEditorRegistry().getDefaultEditor(iFile.getName() + ".txt").getId(); //$NON-NLS-1$
										}
										workbenchWindow.getActivePage().openEditor(new FileEditorInput(iFile), editorId);
									}
									catch (PartInitException ex) {
										LoggerFactory.getLoggerInstance().logError("Exception encountered when attempting to open file: " + iFile + "\n\n", ex); //$NON-NLS-1$ //$NON-NLS-2$
										// B2BGUIPlugin.getPlugin().getMsgLogger().write("Exception
										// encountered when attempting to open
										// file: " + iFile + "\n\n" + ex);
									}
								}
							});

							Runnable runnable = new Runnable() {
								public void run() {
									// IEditorPart editorPart =
									// WorkbenchUtility.getActiveEditor();
									IEditorPart editorPart = XMLUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
									gotoError(editorPart, line, column);
								}
							};
							Display.getCurrent().asyncExec(runnable);
						}
					}
				}
			}
			catch (Exception e) {
				// Do nothing.
			}
		}
	}

	static void gotoError(IEditorPart editorPart, int line, int column) {
		if (editorPart != null) {
			TextEditor textEditor = (TextEditor) editorPart.getAdapter(TextEditor.class);
			if (textEditor != null) {
				try {
					IDocumentProvider dp = textEditor.getDocumentProvider();
					IDocument document = (dp != null) ? dp.getDocument(textEditor.getEditorInput()) : null;
					textEditor.selectAndReveal(document.getLineOffset(line - 1) + column - 1, 0);
				}
				catch (BadLocationException x) {
					// marker refers to invalid text position -> do nothing
				}
			}
		}
	}
}
