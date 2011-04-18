/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.core.JavaModelManager;
import org.eclipse.wst.jsdt.internal.core.index.Index;
import org.eclipse.wst.jsdt.internal.core.search.indexing.IndexManager;
import org.eclipse.wst.jsdt.internal.core.search.indexing.IndexRequest;
import org.eclipse.wst.jsdt.internal.core.search.indexing.ReadWriteMonitor;
import org.eclipse.wst.jsdt.web.core.internal.JsCoreMessages;
import org.eclipse.wst.jsdt.web.core.internal.JsCorePlugin;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;
import org.eclipse.wst.jsdt.web.core.internal.validation.Util;
import org.osgi.framework.Bundle;

/**
 *
 * Provisional API: This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 *(copied from JSP)
 * Responsible for keeping the JS index up to date.
 * 
 */
public class JsIndexManager {

	// for debugging
	// TODO move this to Logger, as we have in SSE
	static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsindexmanager"); //$NON-NLS-1$
 		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private static final String PKEY_INDEX_STATE = "jspIndexState"; //$NON-NLS-1$

	private IndexWorkspaceJob indexingJob = new IndexWorkspaceJob();



	// TODO: consider enumeration for these int constants
	// set to S_UPDATING once a resource change comes in
	// set to S_STABLE if:
	// - we know we aren't interested in the resource change
	// - or the ProcessFilesJob completes
	// set to S_CANCELED if an indexing job is canceled
	// set to S_REBUILDING if re-indexing the entire workspace

	// the int '0' is reserved for the default value if a preference is not
	// there
	/** index is reliable to use */
	public static final int S_STABLE = 1;
	/** index is being updated (from a resource delta) */
	public static final int S_UPDATING = 2;
	/** entire index is being rebuilt */
	public static final int S_REBUILDING = 3;
	/**
	 * indexing job was canceled in the middle of it, index needs to be
	 * rebuilt
	 */
	public static final int S_CANCELED = 4;

	/** symbolic name for OSGI framework */
	private final String OSGI_FRAMEWORK_ID = "org.eclipse.osgi"; //$NON-NLS-1$

	/**
	 * Collects web page files from a resource delta. Derived resources should
	 * be included to handle resources whose derived flag is being toggled.
	 */
	private class JSResourceDeltaVisitor implements IResourceDeltaVisitor {
		// using hash map ensures only one of each file
		// must be reset before every use
		private HashMap webPageFiles = null;

		public JSResourceDeltaVisitor() {
			this.webPageFiles = new HashMap();
		}

		public boolean visit(IResourceDelta delta) throws CoreException {

			// in case JS search was canceled (eg. when closing the editor)
			if (JsSearchSupport.getInstance().isCanceled() || frameworkIsShuttingDown()) {
				setCanceledState();
				return false;
			}

			try {
				// skip anything hidden by name
				if (isHiddenResource(delta.getFullPath())) {
					return false;
				}

				int kind = delta.getKind();
				int flags = delta.getFlags();
				boolean added = (kind & IResourceDelta.ADDED) == IResourceDelta.ADDED;
				boolean isInterestingChange = false;
				if ((kind & IResourceDelta.CHANGED) == IResourceDelta.CHANGED) {
					// ignore things like marker changes
					isInterestingChange = (flags & IResourceDelta.CONTENT) > 0 || (flags & IResourceDelta.REPLACED) > 0 || (flags & IResourceDelta.TYPE) > 0 || (flags & IResourceDelta.MOVED_FROM) > 0;
				}
				boolean removed = (kind & IResourceDelta.REMOVED) == IResourceDelta.REMOVED;
				if (added || isInterestingChange) {
					visitAdded(delta);
				}
				else if (removed) {
					visitRemoved(delta);
				}
			}
			catch (Exception e) {
				// need to set state here somehow, and reindex
				// otherwise index will be unreliable
				if (DEBUG) {
					Logger.logException("Delta analysis may not be complete", e); //$NON-NLS-1$
				}
			}
			// if the delta has children, continue to add/remove files
			return true;
		}

		private void visitRemoved(IResourceDelta delta) {
			// handle cleanup
			if (delta.getResource() != null) {
				IResource r = delta.getResource();
				if ((r.getType() == IResource.FOLDER) && r.exists()) {
					deleteIndex((IFolder) r);
				}
				if ((r.getType() == IResource.FILE) && r.exists()) {
					deleteIndex((IFile) r);
				}
			}
		}

		private void visitAdded(IResourceDelta delta) {
			// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3553
			// quick check if it's even web page related to improve
			// performance
			// checking name from the delta before getting
			// resource because it's lighter
			String filename = delta.getFullPath().lastSegment();
			if (filename != null && Util.isJsType(filename)) {
				IResource r = delta.getResource();
				if (r.isAccessible() && r.getType() == IResource.FILE) {
					this.webPageFiles.put(r.getFullPath(), r);
				}
			}
		}

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=93463
		private boolean isHiddenResource(IPath p) {
			return p.segmentCount() > 0 && p.lastSegment().startsWith(".");
		}

		private void deleteIndex(IFolder folder) {
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

		private void deleteIndex(IFile file) {
			// have to use our own job to compute the correct index location
			RemoveFileFromIndex removeFileFromIndex = new RemoveFileFromIndex(file.getFullPath());
			JavaModelManager.getJavaModelManager().getIndexManager().request(removeFileFromIndex);
		}

		public IFile[] getFiles() {
			return (IFile[]) this.webPageFiles.values().toArray(new IFile[this.webPageFiles.size()]);
		}

		public void reset() {
			this.webPageFiles.clear();
		}
	}


	/**
	 * schedules web pages for indexing
	 */
	private class ProcessFilesJob extends Job {
		List fileList = null;
		// keep track of how many files we've indexed
		int lastFileCursor = 0;

		ProcessFilesJob(String taskName) {
			super(taskName);
			fileList = new ArrayList();
		}

		synchronized void process(IFile[] files) {
			for (int i = 0; i < files.length; i++) {
				fileList.add(files[i]);
			}
			if (DEBUG) {
				System.out.println("JSIndexManager queuing " + files.length + " files"); //$NON-NLS-2$ //$NON-NLS-1$
			}
		}

		synchronized IFile[] getFiles() {
			return (IFile[]) fileList.toArray(new IFile[fileList.size()]);
		}

		synchronized void clearFiles() {
			fileList.clear();
			lastFileCursor = 0;
			//System.out.println("cleared files");
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

				if (DEBUG) {
					System.out.println("JSIndexManager indexing " + filesToBeProcessed.length + " files"); //$NON-NLS-2$ //$NON-NLS-1$
				}
				// API indicates that monitor is never null
				monitor.beginTask("", filesToBeProcessed.length); //$NON-NLS-1$
				JsSearchSupport ss = JsSearchSupport.getInstance();
				String processingNFiles = ""; //$NON-NLS-1$


				for (;lastFileCursor < filesToBeProcessed.length; lastFileCursor++) {

					if (isCanceled(monitor) || frameworkIsShuttingDown()) {
						setCanceledState();
						return Status.CANCEL_STATUS;
					}
					IFile file = filesToBeProcessed[lastFileCursor];
					// do not add files that are derived
					if(!file.isDerived()) {
						try {
							IJavaScriptProject project = JavaScriptCore.create(file.getProject());
							if (project.exists()) {
								ss.addJspFile(file);
								// JS Indexer processing n files
								processingNFiles = NLS.bind(JsCoreMessages.JSPIndexManager_2, new String[]{Integer.toString((filesToBeProcessed.length - lastFileCursor))});
								monitor.subTask(processingNFiles + " - " + file.getName()); //$NON-NLS-1$
								monitor.worked(1);
	
								if (DEBUG) {
									System.out.println("JSIndexManager Job added file: " + file.getName()); //$NON-NLS-1$
								}
							}
						}
						catch (Exception e) {
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
								String filename = file != null ? file.getFullPath().toString() : ""; //$NON-NLS-1$
								Logger.logException("JSIndexer problem indexing:" + filename, e); //$NON-NLS-1$
							}
						}
					}
				} // end for
			}
			finally {
				// just in case something didn't follow API (monitor is null)
				if (monitor != null) {
					monitor.done();
				}
			}

			// successfully finished, clear files list
			clearFiles();
			
			long finish = System.currentTimeMillis();
			long diff = finish - start;
			if (DEBUG) {
				fTotalTime += diff;
				System.out.println("============================================================================"); //$NON-NLS-1$
				System.out.println("this time: " + diff + " cumulative time for resource changed: " + fTotalTime); //$NON-NLS-1$ //$NON-NLS-2$
				System.out.println("============================================================================"); //$NON-NLS-1$
			}
			return Status.OK_STATUS;
		}

		private boolean isCanceled(IProgressMonitor runMonitor) {
			boolean canceled = false;
			// check specific monitor passed into run method (the progress
			// group in this case)
			// check main search support canceled
			if ((runMonitor != null) && runMonitor.isCanceled()) {
				canceled = true;
			} else if (JsSearchSupport.getInstance().isCanceled()) {
				canceled = true;
				if (runMonitor != null) {
					runMonitor.setCanceled(true);
				}
			}
			return canceled;
		}
		
	}

	// end class ProcessFilesJob

	class RemoveFileFromIndex extends IndexRequest {
		IPath filePath;

		public RemoveFileFromIndex(IPath filePath) {
			super(filePath.removeLastSegments(1), JavaModelManager.getJavaModelManager().getIndexManager());
		}
		public boolean execute(IProgressMonitor progressMonitor) {

			if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;

			/* ensure no concurrent write access to index */
			Index index = this.manager.getIndex(this.containerPath, JsSearchSupport.getInstance().computeIndexLocation(filePath.removeLastSegments(1)), true, /*reuse index file*/ false /*create if none*/);
			if (index == null) return true;
			ReadWriteMonitor monitor = index.monitor;
			if (monitor == null) return true; // index got deleted since acquired

			try {
				monitor.enterWrite(); // ask permission to write
				index.remove(filePath.lastSegment());
			} finally {
				monitor.exitWrite(); // free write lock
			}
			return true;
		}
		public String toString() {
			return "removing " + filePath.lastSegment() + " from index " + this.containerPath; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static JsIndexManager fSingleton = null;
	private boolean initialized;
	private boolean initializing = true;

	private IndexJobCoordinator indexJobCoordinator;
	private IResourceChangeListener jsResourceChangeListener;

	private JSResourceDeltaVisitor fVisitor = null;
	static long fTotalTime = 0;

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

	public synchronized static JsIndexManager getInstance() {
		if (fSingleton == null) {
			fSingleton = new JsIndexManager();
		}
		return fSingleton;
	}

	public void initialize() {
		JsIndexManager singleInstance = getInstance();

		if (!singleInstance.initialized) {
			singleInstance.initialized = true;

			singleInstance.indexJobCoordinator = new IndexJobCoordinator();
			singleInstance.jsResourceChangeListener = new JSResourceChangeListener();

			// added as JobChange listener so JSIndexManager can be smarter
			// about when it runs
			Job.getJobManager().addJobChangeListener(singleInstance.indexJobCoordinator);

			// add JSIndexManager to keep JSP Index up to date
			// listening for IResourceChangeEvent.PRE_DELETE and
			// IResourceChangeEvent.POST_CHANGE
			ResourcesPlugin.getWorkspace().addResourceChangeListener(jsResourceChangeListener, IResourceChangeEvent.POST_CHANGE);

			// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5091
			// makes sure IndexManager is aware of our indexes
			saveIndexes();
			//rebuildIndexIfNeeded();
			singleInstance.initializing = false;

		}

	}
	
	synchronized void setIndexState(int state) {
		if (DEBUG) {
			System.out.println("JSIndexManager setting index state to: " + state2String(state)); //$NON-NLS-1$
		}
		Plugin jspModelPlugin = JsCorePlugin.getDefault();
		jspModelPlugin.getPluginPreferences().setValue(PKEY_INDEX_STATE, state);
		jspModelPlugin.savePluginPreferences();

	}

	private String state2String(int state) {
		String s = "UNKNOWN"; //$NON-NLS-1$
		switch (state) {
			case (S_STABLE) :
				s = "S_STABLE"; //$NON-NLS-1$
				break;
			case (S_UPDATING) :
				s = "S_UPDATING"; //$NON-NLS-1$
				break;
			case (S_CANCELED) :
				s = "S_CANCELED"; //$NON-NLS-1$
				break;
			case (S_REBUILDING) :
				s = "S_REBUILDING"; //$NON-NLS-1$
				break;
		}
		return s;
	}

	int getIndexState() {
		return JsCorePlugin.getDefault().getPluginPreferences().getInt(PKEY_INDEX_STATE);
	}

	void setUpdatingState() {
		//if (getIndexState() != S_CANCELED)
		setIndexState(S_UPDATING);
	}

	void setCanceledState() {
		setIndexState(JsIndexManager.S_CANCELED);
	}

	void setStableState() {
		//if (getIndexState() != S_CANCELED)
		setIndexState(S_STABLE);
	}

	void setRebuildingState() {
		setIndexState(S_REBUILDING);
	}

	synchronized void rebuildIndexIfNeeded() {
		if (getIndexState() != S_STABLE) {
			rebuildIndex();
		}
	}

	void rebuildIndex() {

		if (DEBUG) {
			System.out.println("*** JS web page Index unstable, requesting re-indexing"); //$NON-NLS-1$
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

	/**
	 * Creates and schedules a Job to process collected files. All JSP
	 * indexing should be done through this method or processFiles(IFile file)
	 * 
	 * @param files
	 */
	final void indexFiles(IFile[] files) {
		// don't use this rule
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4931
		// processFiles.setRule(new IndexFileRule());
		processFilesJob.process(files);
	}


	/**
	 * Package protected for access by inner Job class in resourceChanged(...)
	 * 
	 * @return
	 */
	JSResourceDeltaVisitor getVisitor() {

		if (this.fVisitor == null) {
			this.fVisitor = new JSResourceDeltaVisitor();
		}
		return this.fVisitor;
	}
	void saveIndexes() {
		IndexManager indexManager = JavaModelManager.getJavaModelManager().getIndexManager();
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int j = 0; j < allProjects.length; j++) {
			if (!JsWebNature.hasNature(allProjects[j]) || !allProjects[j].isOpen()) {
				continue;
			}
			IPath jsModelWorkingLocation = JsSearchSupport.getInstance().getModelJspPluginWorkingLocation(allProjects[j]);
			File folder = new File(jsModelWorkingLocation.toOSString());
			String[] files = folder.list();
			String locay = ""; //$NON-NLS-1$
			try {
				for (int i = 0; i < files.length; i++) {
					if (files[i].toLowerCase().endsWith(".index")) { //$NON-NLS-1$
						locay = jsModelWorkingLocation.toString() + "/" + files[i]; //$NON-NLS-1$
						// XXX: might not be the right container path, check IndexManager.indexLocations ?
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
		if (DEBUG && shuttingDown) {
			System.out.println("JSIndexManager: system is shutting down!"); //$NON-NLS-1$
		}
		return shuttingDown;
	}


	public void shutdown() {

		// stop listening
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(jsResourceChangeListener);


		// stop any searching
		JsSearchSupport.getInstance().setCanceled(true);

		// stop listening to jobs
		Job.getJobManager().removeJobChangeListener(indexJobCoordinator);


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

	private void waitTillNotRunning(int maxSeconds, Job job) {
		int pauseTime = 10;
		int maxtries = maxSeconds / pauseTime;
		int count = 0;
		while ((count++ < maxtries) && (job.getState() == Job.RUNNING)) {
			try {
				Thread.sleep(pauseTime);
				// System.out.println("count: " + count + " max: " +
				// maxtries);
			}
			catch (InterruptedException e) {
				Logger.logException(e);
			}
		}
	}

	private class IndexJobCoordinator extends JobChangeAdapter {
		
		public void aboutToRun(IJobChangeEvent event) {
			Job jobToCoordinate = event.getJob();
			if (isJobToAvoid(jobToCoordinate)) {
				// job will be rescheduled when the job we
				// are avoiding (eg. build) is done
				getProcessFilesJob().cancel();
				//System.out.println("cancel:" + jobToCoordinate.getName());
			}
		}

		public void done(IJobChangeEvent event) {

			Job jobToCoordinate = event.getJob();
			if (isJobToAvoid(jobToCoordinate)) {
				if (getProcessFilesJob().getFiles().length > 0) {
					getProcessFilesJob().schedule(500);
					//System.out.println("schedule:" + jobToCoordinate.getName());
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

	private class JSResourceChangeListener implements IResourceChangeListener {


		/**
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 */
		public void resourceChanged(IResourceChangeEvent event) {

			if (isInitializing()) {
				return;
			}

			// ignore resource changes if already rebuilding
			if (getIndexState() == S_REBUILDING) {
				return;
			}
			// previously canceled, needs entire index rebuild
			if (getIndexState() == S_CANCELED) {
				// rebuildIndex();
				// just resume indexing
				getProcessFilesJob().schedule(500);
				//System.out.println("schedule: resource changed, previously canceled");
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
							JSResourceDeltaVisitor v = getVisitor();
							// clear from last run
							v.reset();
							// count files, possibly do this in a job too...
							// don't include PHANTOM resources
							delta.accept(v, false);

							// process files from this delta
							IFile[] files = v.getFiles();
							if (files.length > 0) {
								/*
								 * Job change listener should set back to
								 * stable when finished
								 */
								setUpdatingState();
								// processFiles(files);
								indexFiles(files);
							}
						}
						catch (CoreException e) {
							// need to set state here somehow, and reindex
							// otherwise index will be unreliable
							if (DEBUG) {
								Logger.logException(e);
							}
						}
						catch (Exception e) {
							// need to set state here somehow, and reindex
							// otherwise index will be unreliable
							if (DEBUG) {
								Logger.logException(e);
							}
						}
					}
				}

			}
		}

	}

	IndexWorkspaceJob getIndexingJob() {
		return indexingJob;
	}

	ProcessFilesJob getProcessFilesJob() {
		return processFilesJob;
	}

	boolean isInitializing() {
		return initializing;
	}

}
