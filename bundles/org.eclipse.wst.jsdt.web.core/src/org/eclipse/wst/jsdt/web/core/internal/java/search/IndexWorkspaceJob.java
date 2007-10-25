/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.java.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.jsdt.web.core.internal.JsCoreMessages;
import org.eclipse.wst.jsdt.web.core.internal.validation.Util;

/**
 * Re-indexes the entire workspace. Ensures the JSP Index is in a stable state
 * before performing a search. (like after a crash or if previous indexing was
 * canceled)
 * 
 * @author pavery
 */
public class IndexWorkspaceJob extends Job {
	// for debugging
	static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspindexmanager"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	/**
	 * Visitor that retrieves jsp project paths for all jsp files in the
	 * workspace, and adds the files to be indexed as they are encountered
	 */
	private class JSPFileVisitor implements IResourceProxyVisitor {
		private List files = new ArrayList();
		// monitor from the Job
		IProgressMonitor fInnerMonitor = null;
		
		public JSPFileVisitor(IProgressMonitor monitor) {
			this.fInnerMonitor = monitor;
		}
		
		public final IFile[] getFiles() {
			return (IFile[]) this.files.toArray(new IFile[this.files.size()]);
		}
		
		public boolean visit(IResourceProxy proxy) throws CoreException {
			// check job canceled
			if (this.fInnerMonitor != null && this.fInnerMonitor.isCanceled()) {
				setCanceledState();
				return false;
			}
			// check search support canceled
			if (JsSearchSupport.getInstance().isCanceled()) {
				setCanceledState();
				return false;
			}
			if (proxy.getType() == IResource.FILE) {
				// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3553
				// check this before description
				// check name before actually getting the file (less work)
				if (Util.isJsType(proxy.getName())) {
					IFile file = (IFile) proxy.requestResource();
					if (file.exists()) {
						if (IndexWorkspaceJob.DEBUG) {
							System.out.println("(+) IndexWorkspaceJob adding file: " + file.getName()); //$NON-NLS-1$
						}
						// this call will check the ContentTypeDescription, so
						// don't need to do it here.
						// JSPSearchSupport.getInstance().addJspFile(file);
						this.files.add(file);
						this.fInnerMonitor.subTask(proxy.getName());
						// don't search deeper for files
						return false;
					}
				}
			}
			return true;
		}
	}
	private IContentType fContentTypeJSP = null;
	
	public IndexWorkspaceJob() {
		// pa_TODO may want to say something like "Rebuilding JSP Index" to be
		// more
		// descriptive instead of "Updating JSP Index" since they are 2
		// different things
		super(JsCoreMessages.JSPIndexManager_0);
		setPriority(Job.LONG);
		setSystem(true);
	}
	

	
	protected IStatus run(IProgressMonitor monitor) {
		IStatus status = Status.OK_STATUS;
		if (monitor.isCanceled()) {
			setCanceledState();
			return Status.CANCEL_STATUS;
		}
		if (IndexWorkspaceJob.DEBUG) {
			System.out.println(" ^ IndexWorkspaceJob started: "); //$NON-NLS-1$
		}
		long start = System.currentTimeMillis();
		try {
			JSPFileVisitor visitor = new JSPFileVisitor(monitor);
			// collect all jsp files
			ResourcesPlugin.getWorkspace().getRoot().accept(visitor, IResource.DEPTH_INFINITE);
			// request indexing
			// this is pretty much like faking an entire workspace resource
			// delta
			JsIndexManager.getInstance().indexFiles(visitor.getFiles());
		} catch (CoreException e) {
			if (IndexWorkspaceJob.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
		long finish = System.currentTimeMillis();
		if (IndexWorkspaceJob.DEBUG) {
			System.out.println(" ^ IndexWorkspaceJob finished\n   total time running: " + (finish - start)); //$NON-NLS-1$
		}
		return status;
	}
	
	void setCanceledState() {
		JsIndexManager.getInstance().setIndexState(JsIndexManager.S_CANCELED);
	}
}
