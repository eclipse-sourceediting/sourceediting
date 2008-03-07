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
package org.eclipse.wst.jsdt.web.core.javascript.search;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.internal.core.JavaModelManager;
import org.eclipse.wst.jsdt.internal.core.search.indexing.IndexManager;
import org.eclipse.wst.jsdt.web.core.internal.JsCoreMessages;
import org.eclipse.wst.jsdt.web.core.internal.JsCorePlugin;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;
import org.eclipse.wst.jsdt.web.core.internal.validation.Util;
import org.osgi.framework.Bundle;

/**
 * Responsible for keeping the JSP index up to date.
 * 
 * @author pavery
 */
public class JsIndexManager {
	// for debugging
	// TODO move this to Logger, as we have in SSE
	static final boolean DEBUG;
	private static JsIndexManager fSingleton = null;
	static long fTotalTime = 0;
	private static final String PKEY_INDEX_STATE = "jspIndexState"; //$NON-NLS-1$
	// TODO: consider enumeration for these int constants
	// set to S_UPDATING once a resource change comes in
	// set to S_STABLE if:
	// - we know we aren't interested in the resource change
	// - or the ProcessFilesJob completes
	// set to S_CANCELED if an indexing job is canceled
	// set to S_REBUILDING if re-indexing the entire workspace
	/**
	 * indexing job was canceled in the middle of it, index needs to be rebuilt
	 */
	public static final int S_CANCELED = 4;
	/** entire index is being rebuilt */
	public static final int S_REBUILDING = 3;
	// the int '0' is reserved for the default value if a preference is not
	// there
	/** index is reliable to use */
	public static final int S_STABLE = 1;
	/** index is being updated (from a resource delta) */
	public static final int S_UPDATING = 2;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspindexmanager"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}
	
	public synchronized static JsIndexManager getInstance() {
		if (JsIndexManager.fSingleton == null) {
			JsIndexManager.fSingleton = new JsIndexManager();
		}
		return JsIndexManager.fSingleton;
	}
	// end class JSPResourceVisitor
	private class IndexJobCoordinator extends JobChangeAdapter {
		
		public void aboutToRun(IJobChangeEvent event) {
			Job jobToCoordinate = event.getJob();
			if (isJobToAvoid(jobToCoordinate)) {
				// job will be rescheduled when the job we
				// are avoiding (eg. build) is done
				getProcessFilesJob().cancel();
				// System.out.println("cancel:" + jobToCoordinate.getName());
			}
		}
		
		
		public void done(IJobChangeEvent event) {
			Job jobToCoordinate = event.getJob();
			if (isJobToAvoid(jobToCoordinate)) {
				if (getProcessFilesJob().getFiles().length > 0) {
					getProcessFilesJob().schedule(500);
					// System.out.println("schedule:" +
					// jobToCoordinate.getName());
				}
			}
		}
		
		private boolean isJobToAvoid(Job jobToCoordinate) {
			boolean result = false;
			if (jobToCoordinate.belongsTo(ResourcesPlugin.FAMILY_AUTO_BUILD) || jobToCoordinate.belongsTo(ResourcesPlugin.FAMILY_MANUAL_BUILD) || jobToCoordinate.belongsTo(ResourcesPlugin.FAMILY_AUTO_REFRESH)) {
				result = true;
			}
			return result;
		}
	}
	// end class ProcessFilesJob
	private class JSPResourceChangeListener implements IResourceChangeListener {
		/**
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			if (isInitializing()) {
				return;
			}
			// ignore resource changes if already rebuilding
			if (getIndexState() == JsIndexManager.S_REBUILDING) {
				return;
			}
			// previously canceled, needs entire index rebuild
			if (getIndexState() == JsIndexManager.S_CANCELED) {
				// rebuildIndex();
				// just resume indexing
				getProcessFilesJob().schedule(500);
				// System.out.println("schedule: resource changed, previously
				// canceled");
				return;
			}
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				// only care about adds or changes right now...
				int kind = delta.getKind();
				boolean added = (kind & IResourceDelta.ADDED) == IResourceDelta.ADDED;
				boolean changed = (kind & IResourceDelta.CHANGED) == IResourceDelta.CHANGED;
				if (added || changed) {
					// only analyze the full (starting at root) delta
					// hierarchy
					if (delta.getFullPath().toString().equals("/")) { //$NON-NLS-1$
						try {
							JSPResourceVisitor v = getVisitor();
							// clear from last run
							v.reset();
							// count files, possibly do this in a job too...
							// don't include PHANTOM resources
							delta.accept(v, false);
							// process files from this delta
							IFile[] files = v.getFiles();
							if (files.length > 0) {
								/*
								 * Job change listener should set back to stable
								 * when finished
								 */
								setUpdatingState();
								// processFiles(files);
								indexFiles(files);
							}
						} catch (CoreException e) {
							// need to set state here somehow, and reindex
							// otherwise index will be unreliable
							if (JsIndexManager.DEBUG) {
								Logger.logException(e);
							}
						} catch (Exception e) {
							// need to set state here somehow, and reindex
							// otherwise index will be unreliable
							if (JsIndexManager.DEBUG) {
								Logger.logException(e);
							}
						}
					}
				}
			}
		}
	}
	/**
	 * Collects JSP files from a resource delta.
	 */
	private class JSPResourceVisitor implements IResourceDeltaVisitor {
		// using hash map ensures only one of each file
		// must be reset before every use
		private HashMap jspFiles = null;
		
		public JSPResourceVisitor() {
			this.jspFiles = new HashMap();
		}
		
		private void deleteIndex(IFile folder) {
			// cleanup index
			IndexManager im = JavaModelManager.getJavaModelManager().getIndexManager();
			IPath folderPath = folder.getFullPath();
			IPath indexLocation = JsSearchSupport.getInstance().computeIndexLocation(folderPath);
			im.removeIndex(indexLocation);
			// im.indexLocations.removeKey(folderPath);
			// im.indexLocations.removeValue(indexLocation);
			File f = indexLocation.toFile();
			f.delete();
		}
		
		public IFile[] getFiles() {
			return (IFile[]) this.jspFiles.values().toArray(new IFile[this.jspFiles.size()]);
		}
		
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=93463
		private boolean isHiddenResource(IPath p) {
			String[] segments = p.segments();
			for (int i = 0; i < segments.length; i++) {
				if (segments[i].startsWith(".")) { //$NON-NLS-1$
					return true;
				}
			}
			return false;
		}
		
		public void reset() {
			this.jspFiles.clear();
		}
		
		public boolean visit(IResourceDelta delta) throws CoreException {
			// in case JSP search was canceled (eg. when closing the editor)
			if (JsSearchSupport.getInstance().isCanceled() || frameworkIsShuttingDown()) {
				setCanceledState();
				return false;
			}
			try {
				if (!isHiddenResource(delta.getFullPath())) {
					int kind = delta.getKind();
					boolean added = (kind & IResourceDelta.ADDED) == IResourceDelta.ADDED;
					boolean isInterestingChange = false;
					if ((kind & IResourceDelta.CHANGED) == IResourceDelta.CHANGED) {
						int flags = delta.getFlags();
						// ignore things like marker changes
						isInterestingChange = (flags & IResourceDelta.CONTENT) == IResourceDelta.CONTENT || (flags & IResourceDelta.REPLACED) == IResourceDelta.REPLACED;
					}
					boolean removed = (kind & IResourceDelta.REMOVED) == IResourceDelta.REMOVED;
					if (added || isInterestingChange) {
						visitAdded(delta);
					} else if (removed) {
						visitRemoved(delta);
					}
				}
			} catch (Exception e) {
				// need to set state here somehow, and reindex
				// otherwise index will be unreliable
				if (JsIndexManager.DEBUG) {
					Logger.logException("Delta analysis may not be complete", e); //$NON-NLS-1$
				}
			}
			// if the delta has children, continue to add/remove files
			return true;
		}
		
		private void visitAdded(IResourceDelta delta) {
			// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3553
			// quick check if it's even JSP related to improve
			// performance
			// checking name from the delta before getting
			// resource because it's lighter
			int numSegments = delta.getFullPath().segmentCount();
			String filename = delta.getFullPath().segment(numSegments - 1);
			if (Util.isJsType(filename)) {
				IResource r = delta.getResource();
				if (r != null && r.exists() && r.getType() == IResource.FILE) {
					this.jspFiles.put(r.getFullPath(), r);
				}
			}
		}
		
		private void visitRemoved(IResourceDelta delta) {
			// handle cleanup
			if (delta.getResource() != null) {
				IResource r = delta.getResource();
				if (r.getType() == IResource.FOLDER && r.exists()) {
					deleteIndex((IFile) r);
				}
			}
		}
	}
	/**
	 * schedules JSP files for indexing by Java core
	 */
	private class ProcessFilesJob extends Job {
		List fileList = null;
		// keep track of how many files we've indexed
		int lastFileCursor = 0;
		
		ProcessFilesJob(String taskName) {
			super(taskName);
			fileList = new ArrayList();
		}
		
		synchronized void clearFiles() {
			fileList.clear();
			lastFileCursor = 0;
			// System.out.println("cleared files");
		}
		
		synchronized IFile[] getFiles() {
			return (IFile[]) fileList.toArray(new IFile[fileList.size()]);
		}
		
		private boolean isCanceled(IProgressMonitor runMonitor) {
			boolean canceled = false;
			// check specific monitor passed into run method (the progress
			// group in this case)
			// check main search support canceled
			if (runMonitor != null && runMonitor.isCanceled()) {
				canceled = true;
			} else if (JsSearchSupport.getInstance().isCanceled()) {
				canceled = true;
				if (runMonitor != null) {
					runMonitor.setCanceled(true);
				}
			}
			return canceled;
		}
		
		synchronized void process(IFile[] files) {
			for (int i = 0; i < files.length; i++) {
				fileList.add(files[i]);
			}
			if (JsIndexManager.DEBUG) {
				System.out.println("JSPIndexManager queuing " + files.length + " files"); //$NON-NLS-2$ //$NON-NLS-1$
			}
		}
		
		
		protected IStatus run(IProgressMonitor monitor) {
			// System.out.println("indexer monitor" + monitor);
			if (isCanceled(monitor) || frameworkIsShuttingDown()) {
				setCanceledState();
				return Status.CANCEL_STATUS;
			}
			long start = System.currentTimeMillis();
			try {
				IFile[] filesToBeProcessed = getFiles();
				if (JsIndexManager.DEBUG) {
					System.out.println("JSPIndexManager indexing " + filesToBeProcessed.length + " files"); //$NON-NLS-2$ //$NON-NLS-1$
				}
				// API indicates that monitor is never null
				monitor.beginTask("", filesToBeProcessed.length); //$NON-NLS-1$
				JsSearchSupport ss = JsSearchSupport.getInstance();
				String processingNFiles = ""; //$NON-NLS-1$
				for (; lastFileCursor < filesToBeProcessed.length; lastFileCursor++) {
					if (isCanceled(monitor) || frameworkIsShuttingDown()) {
						setCanceledState();
						return Status.CANCEL_STATUS;
					}
					try {
						ss.addJspFile(filesToBeProcessed[lastFileCursor]);
						// JSP Indexer processing n files
						processingNFiles = NLS.bind(JsCoreMessages.JSPIndexManager_2, new String[] { Integer.toString((filesToBeProcessed.length - lastFileCursor)) });
						monitor.subTask(processingNFiles + " - " + filesToBeProcessed[lastFileCursor].getName()); //$NON-NLS-1$
						monitor.worked(1);
						if (JsIndexManager.DEBUG) {
							System.out.println("JSPIndexManager Job added file: " + filesToBeProcessed[lastFileCursor].getName()); //$NON-NLS-1$
						}
					} catch (Exception e) {
						// RATLC00284776
						// ISSUE: we probably shouldn't be catching EVERY
						// exception, but
						// the framework only allows to return IStatus in
						// order to communicate
						// that something went wrong, which means the loop
						// won't complete, and we would hit the same problem
						// the next time.
						// 
						// a possible solution is to keep track of the
						// exceptions logged
						// and only log a certain amt of the same one,
						// otherwise skip it.
						if (!frameworkIsShuttingDown()) {
							String filename = filesToBeProcessed[lastFileCursor] != null ? filesToBeProcessed[lastFileCursor].getFullPath().toString() : ""; //$NON-NLS-1$
							Logger.logException("JSPIndexer problem indexing:" + filename, e); //$NON-NLS-1$
						}
					}
				} // end for
			} finally {
				// just in case something didn't follow API (monitor is null)
				if (monitor != null) {
					monitor.done();
				}
			}
			// successfully finished, clear files list
			clearFiles();
			long finish = System.currentTimeMillis();
			long diff = finish - start;
			if (JsIndexManager.DEBUG) {
				JsIndexManager.fTotalTime += diff;
				System.out.println("============================================================================"); //$NON-NLS-1$
				System.out.println("this time: " + diff + " cumulative time for resource changed: " + JsIndexManager.fTotalTime); //$NON-NLS-1$ //$NON-NLS-2$
				System.out.println("============================================================================"); //$NON-NLS-1$
			}
			return Status.OK_STATUS;
		}
	}
	private IContentType fContentTypeJSP = null;
	private JSPResourceVisitor fVisitor = null;
	private IndexWorkspaceJob indexingJob = new IndexWorkspaceJob();
	private IndexJobCoordinator indexJobCoordinator;
	private boolean initialized;
	private boolean initializing = true;
	private IResourceChangeListener jspResourceChangeListener;
	/** symbolic name for OSGI framework */
	private final String OSGI_FRAMEWORK_ID = "org.eclipse.osgi"; //$NON-NLS-1$
	// Job for processing resource delta
	private ProcessFilesJob processFilesJob = null;
	
	private JsIndexManager() {
		processFilesJob = new ProcessFilesJob(JsCoreMessages.JSPIndexManager_0);
		// only show in verbose mode
		processFilesJob.setSystem(true);
		processFilesJob.setPriority(Job.LONG);
		processFilesJob.addJobChangeListener(new JobChangeAdapter() {
			
			public void done(IJobChangeEvent event) {
				super.done(event);
				setStableState();
			}
		});
	}
	
	/**
	 * A check to see if the OSGI framework is shutting down.
	 * 
	 * @return true if the System Bundle is stopped (ie. the framework is
	 *         shutting down)
	 */
	boolean frameworkIsShuttingDown() {
		// in the Framework class there's a note:
		// set the state of the System Bundle to STOPPING.
		// this must be done first according to section 4.19.2 from the OSGi
		// R3 spec.
		boolean shuttingDown = Platform.getBundle(OSGI_FRAMEWORK_ID).getState() == Bundle.STOPPING;
		if (JsIndexManager.DEBUG && shuttingDown) {
			System.out.println("JSPIndexManager: system is shutting down!"); //$NON-NLS-1$
		}
		return shuttingDown;
	}
	
	IndexWorkspaceJob getIndexingJob() {
		return indexingJob;
	}
	
	int getIndexState() {
		return JsCorePlugin.getDefault().getPluginPreferences().getInt(JsIndexManager.PKEY_INDEX_STATE);
	}
	

	
	ProcessFilesJob getProcessFilesJob() {
		return processFilesJob;
	}
	
	/**
	 * Package protected for access by inner Job class in resourceChanged(...)
	 * 
	 * @return
	 */
	JSPResourceVisitor getVisitor() {
		if (this.fVisitor == null) {
			this.fVisitor = new JSPResourceVisitor();
		}
		return this.fVisitor;
	}
	
	/**
	 * Creates and schedules a Job to process collected files. All JSP indexing
	 * should be done through this method or processFiles(IFile file)
	 * 
	 * @param files
	 */
	final void indexFiles(IFile[] files) {
		// don't use this rule
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4931
		// processFiles.setRule(new IndexFileRule());
		processFilesJob.process(files);
	}
	
	public void initialize() {
		JsIndexManager singleInstance = JsIndexManager.getInstance();
		if (!singleInstance.initialized) {
			singleInstance.initialized = true;
			singleInstance.indexJobCoordinator = new IndexJobCoordinator();
			singleInstance.jspResourceChangeListener = new JSPResourceChangeListener();
			// added as JobChange listener so JSPIndexManager can be smarter
			// about when it runs
			Platform.getJobManager().addJobChangeListener(singleInstance.indexJobCoordinator);
			// add JSPIndexManager to keep JSP Index up to date
			// listening for IResourceChangeEvent.PRE_DELETE and
			// IResourceChangeEvent.POST_CHANGE
			ResourcesPlugin.getWorkspace().addResourceChangeListener(jspResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
			// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5091
			// makes sure IndexManager is aware of our indexes
			saveIndexes();
			rebuildIndexIfNeeded();
			singleInstance.initializing = false;
		}
	}
	
	boolean isInitializing() {
		return initializing;
	}
	
	void rebuildIndex() {
		if (JsIndexManager.DEBUG) {
			System.out.println("*** JSP Index unstable, requesting re-indexing"); //$NON-NLS-1$
		}
		getIndexingJob().addJobChangeListener(new JobChangeAdapter() {
			
			public void aboutToRun(IJobChangeEvent event) {
				super.aboutToRun(event);
				setRebuildingState();
			}
			
			
			public void done(IJobChangeEvent event) {
				super.done(event);
				setStableState();
				getIndexingJob().removeJobChangeListener(this);
			}
		});
		// we're about to reindex everything anyway
		getProcessFilesJob().clearFiles();
		getIndexingJob().schedule();
	}
	
	synchronized void rebuildIndexIfNeeded() {
		if (getIndexState() != JsIndexManager.S_STABLE) {
			rebuildIndex();
		}
	}
	
	// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5091
	// makes sure IndexManager is aware of our indexes
	void saveIndexes() {
		IndexManager indexManager = JavaModelManager.getJavaModelManager().getIndexManager();
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int j = 0; j < allProjects.length; j++) {
			if (!JsWebNature.hasNature(allProjects[j]) || !allProjects[j].isOpen()) {
				continue;
			}
			IPath jspModelWorkingLocation = JsSearchSupport.getInstance().getModelJspPluginWorkingLocation(allProjects[j]);
			File folder = new File(jspModelWorkingLocation.toOSString());
			String[] files = folder.list();
			String locay = ""; //$NON-NLS-1$
			try {
				for (int i = 0; i < files.length; i++) {
					if (files[i].toLowerCase().endsWith(".index")) { //$NON-NLS-1$
						locay = jspModelWorkingLocation.toString() + "/" + files[i]; //$NON-NLS-1$
						// reuse index file
// index = new Index(locay, allProjects[j].getFullPath().toOSString(), true);
// //$NON-NLS-1$
// index.save();
						indexManager.getIndex(allProjects[j].getFullPath(), new Path(locay), true, false);
						// indexManager.saveIndex(index);
					}
				}
			} catch (Exception e) {
				// we should be shutting down, want to shut down quietly
				if (JsIndexManager.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}
	
	void setCanceledState() {
		setIndexState(JsIndexManager.S_CANCELED);
	}
	
	synchronized void setIndexState(int state) {
		if (JsIndexManager.DEBUG) {
			System.out.println("JSPIndexManager setting index state to: " + state2String(state)); //$NON-NLS-1$
		}
		Plugin jspModelPlugin = JsCorePlugin.getDefault();
		jspModelPlugin.getPluginPreferences().setValue(JsIndexManager.PKEY_INDEX_STATE, state);
		jspModelPlugin.savePluginPreferences();
	}
	
	void setRebuildingState() {
		setIndexState(JsIndexManager.S_REBUILDING);
	}
	
	void setStableState() {
		// if (getIndexState() != S_CANCELED)
		setIndexState(JsIndexManager.S_STABLE);
	}
	
	void setUpdatingState() {
		// if (getIndexState() != S_CANCELED)
		setIndexState(JsIndexManager.S_UPDATING);
	}
	
	public void shutdown() {
		// stop listening
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(jspResourceChangeListener);
		// saveIndexes();
		// stop any searching
		JsSearchSupport.getInstance().setCanceled(true);
		// stop listening to jobs
		Platform.getJobManager().removeJobChangeListener(indexJobCoordinator);
		int maxwait = 5000;
		if (processFilesJob != null) {
			processFilesJob.cancel();
		}
		// attempt to make sure this indexing job is litterally
		// done before continuing, since we are shutting down
		waitTillNotRunning(maxwait, processFilesJob);
		if (indexingJob != null) {
			indexingJob.cancel();
		}
		waitTillNotRunning(maxwait, processFilesJob);
	}
	
	private String state2String(int state) {
		String s = "UNKNOWN"; //$NON-NLS-1$
		switch (state) {
			case (JsIndexManager.S_STABLE):
				s = "S_STABLE"; //$NON-NLS-1$
			break;
			case (JsIndexManager.S_UPDATING):
				s = "S_UPDATING"; //$NON-NLS-1$
			break;
			case (JsIndexManager.S_CANCELED):
				s = "S_CANCELED"; //$NON-NLS-1$
			break;
			case (JsIndexManager.S_REBUILDING):
				s = "S_REBUILDING"; //$NON-NLS-1$
			break;
		}
		return s;
	}
	
	private void waitTillNotRunning(int maxSeconds, Job job) {
		int pauseTime = 10;
		int maxtries = maxSeconds / pauseTime;
		int count = 0;
		while (count++ < maxtries && job.getState() == Job.RUNNING) {
			try {
				Thread.sleep(pauseTime);
				// System.out.println("count: " + count + " max: " +
				// maxtries);
			} catch (InterruptedException e) {
				Logger.logException(e);
			}
		}
	}
}