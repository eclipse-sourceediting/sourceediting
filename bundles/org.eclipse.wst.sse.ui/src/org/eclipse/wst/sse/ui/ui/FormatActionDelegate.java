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

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.encoding.exceptions.MalformedInputExceptionWithDetail;
import org.eclipse.wst.sse.core.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.extension.FormatProcessorsExtensionReader;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


public class FormatActionDelegate extends ResourceActionDelegate {
	/*
	 * (non-Javadoc)
	 * 
	 */
	protected boolean processorAvailable(IResource resource) {
		boolean result = false;
		try {
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;

				IStructuredFormatProcessor formatProcessor = null;
				IContentDescription contentDescription = file.getContentDescription();
				if (contentDescription != null) {
					IContentType contentType = contentDescription.getContentType();
					formatProcessor = getFormatProcessor(contentType.getId());
				}
				if (formatProcessor != null)
					result = true;
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
		format(monitor, resource);

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
		progressDialog.setDialogTitle(ResourceHandler.getString("FormatActionDelegate.0")); //$NON-NLS-1$
		progressDialog.setActionCompletedMessage(ResourceHandler.getString("FormatActionDelegate.1")); //$NON-NLS-1$
		progressDialog.setActionCancelledMessage(ResourceHandler.getString("FormatActionDelegate.2")); //$NON-NLS-1$
	}

	protected IStructuredFormatProcessor getFormatProcessor(String contentTypeId) {
		return FormatProcessorsExtensionReader.getInstance().getFormatProcessor(contentTypeId);
	}

	protected void format(IProgressMonitor monitor, IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;

			if (monitor == null || !monitor.isCanceled())
				format(monitor, file);
		}
		else if (resource instanceof IContainer) {
			IContainer container = (IContainer) resource;

			try {
				IResource[] members = container.members();
				for (int i = 0; i < members.length; i++) {
					if (monitor == null || !monitor.isCanceled())
						format(monitor, members[i]);
				}
			}
			catch (CoreException e) {
				monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + resource.getName()); //$NON-NLS-1$
			}
		}
	}

	protected void format(IProgressMonitor monitor, IFile file) {
		try {
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null)
				return;

			IContentType contentType = contentDescription.getContentType();
			IStructuredFormatProcessor formatProcessor = getFormatProcessor(contentType.getId());
			if (formatProcessor != null && (monitor == null || !monitor.isCanceled())) {
				monitor.setTaskName(ResourceHandler.getString("FormatActionDelegate.3") + " " + file.getFullPath() + "..."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				formatProcessor.setProgressMonitor(monitor);
				formatProcessor.formatFile(file);
			}
		}
		catch (MalformedInputExceptionWithDetail e) {
			monitor.setTaskName(ResourceHandler.getString("FormatActionDelegate.4") + file.getName()); //$NON-NLS-1$
		}
		catch (IOException e) {
			monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + file.getName()); //$NON-NLS-1$
		}
		catch (CoreException e) {
			monitor.setTaskName(ResourceHandler.getString("ActionDelegate.0") + file.getName()); //$NON-NLS-1$
		}
	}
}
