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
package org.eclipse.wst.sse.ui.edit.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

/**
 *  @deprecated Eclipse now provides a file conversion action. This will be deleted.
 */
public class ConvertLineDelimitersToCRLFActionDelegate extends ResourceActionDelegate {

	class ConvertLineDelimitersJob extends Job {

		public ConvertLineDelimitersJob(String name) {
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
				fErrorStatus = new MultiStatus(SSEUIPlugin.ID, IStatus.ERROR, SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.errorStatusMessage"), null); //$NON-NLS-1$
			}

			return status;
		}

	}

	private MultiStatus fErrorStatus = new MultiStatus(SSEUIPlugin.ID, IStatus.ERROR, SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.errorStatusMessage"), null); //$NON-NLS-1$
	protected String fLineDelimiter = "\r\n"; //$NON-NLS-1$

	protected void convert(IProgressMonitor monitor, IFile file) {
		try {
			monitor.worked(1);
			IContentDescription contentDescription = file.getContentDescription();
			if (contentDescription == null)
				return;

			IContentType contentType = contentDescription.getContentType();
			if (contentType.isKindOf(Platform.getContentTypeManager().getContentType("org.eclipse.core.runtime.text"))) { //$NON-NLS-1$
				if (monitor == null || !monitor.isCanceled()) {
					String message = MessageFormat.format(SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.3"), new String[]{file.getFullPath().toString()}); //$NON-NLS-1$
					monitor.subTask(message);

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
			}
		} catch (CoreException e) {
			String message = MessageFormat.format(SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.4"), new String[]{file.getName()}); //$NON-NLS-1$
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		} catch (BadLocationException e) {
			String message = MessageFormat.format(SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.4"), new String[]{file.getName()}); //$NON-NLS-1$
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		}
	}

	protected void convert(IProgressMonitor monitor, IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;

			if (monitor == null || !monitor.isCanceled())
				convert(monitor, file);
		} else if (resource instanceof IContainer) {
			IContainer container = (IContainer) resource;

			try {
				IResource[] members = container.members();
				for (int i = 0; i < members.length; i++) {
					if (monitor == null || !monitor.isCanceled())
						convert(monitor, members[i]);
				}
			} catch (CoreException e) {
				String message = MessageFormat.format(SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.4"), new String[]{resource.getFullPath().toString()}); //$NON-NLS-1$
				fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
			}
		}
	}

	protected Job getJob() {
		return new ConvertLineDelimitersJob(SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.jobName")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.edit.util.ResourceActionDelegate#process(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.resources.IResource)
	 */
	protected void process(IProgressMonitor monitor, IResource resource) {
		convert(monitor, resource);

		try {
			resource.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			monitor.setTaskName(SSEUIPlugin.getResourceString("%ActionDelegate.0") + resource.getName()); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.edit.util.ResourceActionDelegate#processorAvailable(org.eclipse.core.resources.IResource)
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
			} else if (resource instanceof IContainer) {
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
		} catch (CoreException e) {
			Logger.logException(e);
		}

		return result;
	}

	/**
	 * @param lineDelimiter
	 *            The fLineDelimiter to set.
	 */
	public void setLineDelimiter(String lineDelimiter) {
		fLineDelimiter = lineDelimiter;
	}

	protected void writeFile(IProgressMonitor monitor, IFile file, String outputString) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file.getLocation().toString());
			outputStream.write(outputString.getBytes(file.getCharset()));
		} catch (IOException e) {
			String message = MessageFormat.format(SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.4"), new String[]{file.getName()}); //$NON-NLS-1$
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		} catch (CoreException e) {
			String message = MessageFormat.format(SSEUIPlugin.getResourceString("%ConvertLineDelimitersToCRLFActionDelegate.4"), new String[]{file.getName()}); //$NON-NLS-1$
			fErrorStatus.add(new Status(IStatus.ERROR, SSEUIPlugin.ID, IStatus.ERROR, message, e));
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}
}
