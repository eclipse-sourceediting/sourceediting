/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.actions;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.exceptions.MalformedInputExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.sse.ui.internal.FormatProcessorsExtensionReader;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

public class FormatActionDelegate extends ResourceActionDelegate {

	class FormatJob extends Job {

		public FormatJob(String name) {
			super(name);
		}

		/**
		 * @param container
		 * @return
		 */
		private int getResourceCount(IResource[] members) {
			int count = 0;

			for (int i = 0; i < members.length; i++) {
				if (members[i] instanceof IContainer) {
					IContainer container = (IContainer) members[i];
					try {
						count += getResourceCount(container.members());
					} catch (CoreException e) {
						// skip counting
					}
				} else
					count++;
			}

			return count;
		}

		/**
		 * @param elements
		 * @return
		 */
		private int getResourceCount(Object[] elements) {
			int count = 0;

			for (int i = 0; i < elements.length; i++) {
				if (elements[i] instanceof IContainer) {
					IContainer container = (IContainer) elements[i];
					try {
						count += getResourceCount(container.members());
					} catch (CoreException e) {
						// skip counting
					}
				} else
					count++;
			}

			return count;
		}

		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = Status.OK_STATUS;

			Object[] elements = fSelection.toArray();
			int resourceCount = getResourceCount(elements);
			monitor.beginTask("", resourceCount); //$NON-NLS-1$
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] instanceof IResource) {
					process(monitor, (IResource) elements[i]);
					monitor.worked(1);
				}
			}
			monitor.done();

			if (fErrorStatus.getChildren().length > 0) {
				status = fErrorStatus;
				fErrorStatus = new MultiStatus(SSEUIPlugin.ID, IStatus.ERROR, SSEUIMessages.FormatActionDelegate_errorStatusMessage, null); //$NON-NLS-1$
			}

			return status;
		}

	}

	private MultiStatus fErrorStatus = new MultiStatus(SSEUIPlugin.ID, IStatus.ERROR, SSEUIMessages.FormatActionDelegate_errorStatusMessage, null); //$NON-NLS-1$

	protected void format(IProgressMonitor monitor, IFile file) {
		try {
			monitor.worked(1);
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null)
				return;

			IContentType contentType = contentDescription.getContentType();
			IStructuredFormatProcessor formatProcessor = getFormatProcessor(contentType.getId());
			if (formatProcessor != null && (monitor == null || !monitor.isCanceled())) {
				String message = NLS.bind(SSEUIMessages.FormatActionDelegate_3, new String[]{file.getFullPath().toString()});
				monitor.subTask(message);
				formatProcessor.setProgressMonitor(monitor);
				formatProcessor.formatFile(file);
			}
		} catch (MalformedInputExceptionWithDetail e) {
			String message = NLS.bind(SSEUIMessages.FormatActionDelegate_5, new String[]{file.getFullPath().toString()});
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		} catch (IOException e) {
			String message = NLS.bind(SSEUIMessages.FormatActionDelegate_4, new String[]{file.getFullPath().toString()});
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		} catch (CoreException e) {
			String message = NLS.bind(SSEUIMessages.FormatActionDelegate_4, new String[]{file.getFullPath().toString()});
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		}
	}

	protected void format(IProgressMonitor monitor, IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;

			if (monitor == null || !monitor.isCanceled())
				format(monitor, file);
		} else if (resource instanceof IContainer) {
			IContainer container = (IContainer) resource;

			try {
				IResource[] members = container.members();
				for (int i = 0; i < members.length; i++) {
					if (monitor == null || !monitor.isCanceled())
						format(monitor, members[i]);
				}
			} catch (CoreException e) {
				String message = NLS.bind(SSEUIMessages.FormatActionDelegate_4, new String[]{resource.getFullPath().toString()});
				fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
			}
		}
	}

	protected IStructuredFormatProcessor getFormatProcessor(String contentTypeId) {
		return FormatProcessorsExtensionReader.getInstance().getFormatProcessor(contentTypeId);
	}

	protected Job getJob() {
		return new FormatJob(SSEUIMessages.FormatActionDelegate_jobName); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.edit.util.ResourceActionDelegate#process(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.resources.IResource)
	 */
	protected void process(IProgressMonitor monitor, IResource resource) {
		format(monitor, resource);

		try {
			resource.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			String message = NLS.bind(SSEUIMessages.FormatActionDelegate_4, new String[]{resource.getFullPath().toString()});
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.actions.ResourceActionDelegate#processorAvailable(org.eclipse.core.resources.IResource)
	 */
	protected boolean processorAvailable(IResource resource) {
		boolean result = false;
		if (resource.isAccessible()) {
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
		}

		return result;
	}
}
