/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.ui;

import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


public class ConvertLineDelimitersToCRLFActionDelegate extends ResourceActionDelegate {
	protected String fLineDelimiter = "\r\n"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 */
	protected boolean processorAvailable(IResource resource) {
		boolean result = false;
		try {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;

				IContentDescription contentDescription = file.getContentDescription();
				if (contentDescription != null) {
					IContentType contentType = contentDescription.getContentType();
					if (contentType.isKindOf(Platform.getContentTypeManager().getContentType("org.eclipse.core.runtime.text"))) //$NON-NLS-1$
						return true;
				}
			}
			else if (resource instanceof IContainer) {
				IContainer container = (IContainer) resource;

				IResource[] members;
				members = container.members();
				for (int i = 0; i < members.length; i++) {
					boolean available = processorAvailable(members[i]);

					if (available) {
						result = true;
						break;
					}
				}
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	protected void process(IProgressMonitor monitor, IResource resource) {
		convert(monitor, resource);

		try {
			resource.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch (CoreException e) {
			monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + resource.getName()); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	protected void initProgressDialog(ResourceActionProgressDialog progressDialog) {
		progressDialog.setDialogTitle(ResourceHandler.getString("ConvertLineDelimitersToCRLFActionDelegate.0")); //$NON-NLS-1$
		progressDialog.setActionCompletedMessage(ResourceHandler.getString("ConvertLineDelimitersToCRLFActionDelegate.1")); //$NON-NLS-1$
		progressDialog.setActionCancelledMessage(ResourceHandler.getString("ConvertLineDelimitersToCRLFActionDelegate.2")); //$NON-NLS-1$
	}

	protected void convert(IProgressMonitor monitor, IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;

			if (monitor == null || !monitor.isCanceled())
				convert(monitor, file);
		}
		else if (resource instanceof IContainer) {
			IContainer container = (IContainer) resource;

			try {
				IResource[] members = container.members();
				for (int i = 0; i < members.length; i++) {
					if (monitor == null || !monitor.isCanceled())
						convert(monitor, members[i]);
				}
			}
			catch (CoreException e) {
				monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + resource.getName()); //$NON-NLS-1$
			}
		}
	}

	protected void convert(IProgressMonitor monitor, IFile file) {
		try {
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null)
				return;

			IContentType contentType = contentDescription.getContentType();
			if (contentType.isKindOf(Platform.getContentTypeManager().getContentType("org.eclipse.core.runtime.text"))) { //$NON-NLS-1$
				if (monitor == null || !monitor.isCanceled()) {
					monitor.setTaskName(ResourceHandler.getString("ConvertLineDelimitersToCRLFActionDelegate.3") + " " + file.getFullPath() + "..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					try {
						IFileEditorInput input = new FileEditorInput(file);
						IDocumentProvider documentProvider = new TextFileDocumentProvider();
						documentProvider.connect(new FileEditorInput(file));
						IDocument document = documentProvider.getDocument(new FileEditorInput(file));
						documentProvider.disconnect(input);
						
						int lineCount = document.getNumberOfLines();
						for (int i = 0; i < lineCount; i++) {
							if (!monitor.isCanceled()) {
								final String delimiter = document.getLineDelimiter(i);
								if (delimiter != null && delimiter.length() > 0 && !delimiter.equals(fLineDelimiter)) {
									IRegion region = document.getLineInformation(i);
									document.replace(region.getOffset() + region.getLength(), delimiter.length(), fLineDelimiter);
								}
							}
						}

						writeFile(monitor, file, document.get());
					}
					catch (CoreException e) {
						monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + file.getName()); //$NON-NLS-1$
					}
					catch (BadLocationException e) {
						monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + file.getName()); //$NON-NLS-1$
					}
				}
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
	}

	protected void writeFile(IProgressMonitor monitor, IFile file, String outputString) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file.getLocation().toString());
			outputStream.write(outputString.getBytes(file.getCharset()));
		}
		catch (IOException e) {
			monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + file.getName()); //$NON-NLS-1$
		}
		catch (CoreException e) {
			monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + file.getName()); //$NON-NLS-1$
		}
		finally {
			try {
				if (outputStream != null)
					outputStream.close();
			}
			catch (IOException e) {
				// do nothing
			}
		}
	}

	/**
	 * @param lineDelimiter
	 *            The fLineDelimiter to set.
	 */
	public void setLineDelimiter(String lineDelimiter) {
		fLineDelimiter = lineDelimiter;
	}
}
