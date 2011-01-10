/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.indexing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;

/**
 * <p>A generic class for implementing a resource index manager.  It is important
 * to note that this only provides the framework for managing an index, not actually
 * indexing.  The subtle difference is that the manager is in charge of paying attention
 * to all of the resource actions that take place in the workspace and filtering those
 * actions down to simple actions that need to be performed on whatever index this manager
 * is managing.</p>
 * 
 * <p>The manager does its very best to make sure the index is always consistent, even if
 * resource events take place when the manager is not running.  In the event that the
 * manager determines it has missed, lost, or corrupted any resource change events
 * that have occurred before, during, or after its activation or deactivation then the
 * manager will inspect the entire workspace to insure the index it is managing is
 * consistent.</p>
 *  
 */
public abstract class AbstractIndexManager {
	
	/** Used to encode path bytes in case they contain double byte characters */
	private static final String ENCODING_UTF16 = "utf16"; //$NON-NLS-1$
	
	/** Default time to wait for other tasks to finish */
	private static final int WAIT_TIME = 300;
	
	/**
	 * <p>Used to report progress on jobs where the total work to complete is unknown.
	 * The created effect is a progress bar that moves but that will never complete.</p>
	 */
	private static final int UNKNOWN_WORK = 100;
	
	/** The amount of events to batch up before sending them off to the processing job*/
	private static final int BATCH_UP_AMOUNT = 100;
	
	/** If this file exists then a full workspace re-processing is needed */ 
	private static final String RE_PROCESS_FILE_NAME = ".re-process"; //$NON-NLS-1$
	
	/** Common error message to log */
	private static final String LOG_ERROR_INDEX_INVALID =
		"Index may become invalid, incomplete, or enter some other inconsistent state."; //$NON-NLS-1$
	
	/** State: manager is stopped */
	private static final byte STATE_DISABLED = 0;
	
	/** State: manager is running */
	private static final byte STATE_ENABLED = 1;
	
	/** Action: add to index */
	protected static final byte ACTION_ADD = 0;
	
	/** Action: remove from index */
	protected static final byte ACTION_REMOVE = 1;
	
	/** Action: add to index caused by move operation */
	protected static final byte ACTION_ADD_MOVE_FROM = 2;
	
	/** Action: remove from index caused by move operation */
	protected static final byte ACTION_REMOVE_MOVE_TO = 3;
	
	/** Source: action originated from resource change event */
	protected static final byte SOURCE_RESOURCE_CHANGE = 0;
	
	/** Source: action originated from workspace scan */
	protected static final byte SOURCE_WORKSPACE_SCAN = 1;
	
	/** Source: action originated from saved state */
	protected static final byte SOURCE_SAVED_STATE = 2;
	
	/** Source: preserved resources to index */
	protected static final byte SOURCE_PRESERVED_RESOURCES_TO_INDEX = 3;
	
	/** the name of this index manager */
	private String fName;
	
	/** {@link IResourceChangeListener} to listen for file changes */
	private ResourceChangeListener fResourceChangeListener;
	
	/** The {@link Job} that does all of the indexing */
	private ResourceEventProcessingJob fResourceEventProcessingJob;
	
	/** A {@link Job} to search the workspace for all files */
	private Job fWorkspaceVisitorJob;
	
	/**
	 * <p>Current state of the manager</p>
	 * 
	 * @see #STATE_DISABLED
	 * @see #STATE_ENABLED
	 */
	private volatile byte fState;
	
	/** used to prevent manager from starting and stopping at the same time */
	private Object fStartStopLock = new Object();
	
	/** <code>true</code> if the manager is currently starting, <code>false</code> otherwise */
	private boolean fStarting;

	/**
	 * <p>Creates the manager with a given name.</p>
	 * 
	 * @param name This will be pre-pended to progress reporting messages and thus should
	 * be translated
	 */
	protected AbstractIndexManager(String name) {
		this.fName = name;
		this.fState = STATE_DISABLED;
		this.fResourceChangeListener = new ResourceChangeListener();
		this.fResourceEventProcessingJob = new ResourceEventProcessingJob();
		this.fStarting = false;
	}
	
	/**
	 * <p>Creates the manager with a given name.</p>
	 * 
	 * @param name This will be pre-pended to progress reporting messages and thus should
	 * be translated
	 * 
	 * @param messageRunning ignored
	 * @param messagegInitializing ignored
	 * @param messageProcessingFiles ignored
	 * 
	 * @deprecated This constructor ignores the last three parameters.
	 * @see #AbstractIndexManager(String)
	 */
	
	protected AbstractIndexManager(String name, String messageRunning,
			String messagegInitializing, String messageProcessingFiles) {
		this(name);
	}
	
	/**
	 * <p>Starts up the {@link AbstractIndexManager}.  If a {@link IResourceDelta}
	 * is provided then it is assumed that all other files in the workspace
	 * have already been index and thus only those in the provided
	 * {@link IResourceDelta} will be processed.  Else if the provided
	 * {@link IResourceDelta} is <code>null</code> it is assumed no files
	 * have been indexed yet so the entire workspace will be searched for
	 * files to be indexed.</p>
	 * 
	 * <p>If {@link IResourceDelta} is provided this will block until that delta
	 * has finished processing.  If no {@link IResourceDelta} provided then a
	 * separate job will be created to process the entire workspace and this method
	 * will return without waiting for that job to complete</p>
	 * 
	 * <p>Will block until {@link #stop()} has finished running if it is
	 * currently running</p>
	 * 
	 * @param savedStateDelta the delta from a saved state, if <code>null</code>
	 * then the entire workspace will be searched for files to index, else
	 * only files in this {@link IResourceDelta} will be indexed
	 * @param monitor This action can not be canceled but this monitor will be used
	 * to report progress
	 */
	public final void start(IResourceDelta savedStateDelta, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor);
		synchronized (this.fStartStopLock) {
			this.fStarting = true;
			
			if(this.fState == STATE_DISABLED) {
				//report status
				progress.beginTask(NLS.bind(SSECoreMessages.IndexManager_0_starting, this.fName), 2);
				
				//start listening for resource change events
				this.fResourceChangeListener.start();
				
				//check to see if a full re-index is required
				boolean forcedFullReIndexNeeded = this.isForcedFullReIndexNeeded();
				
				/* start the indexing job only loading preserved state if not doing full index
				 * if failed loading preserved state then force full re-index
				 */
				forcedFullReIndexNeeded = !this.fResourceEventProcessingJob.start(!forcedFullReIndexNeeded,
						progress.newChild(1));
				progress.setWorkRemaining(1);
				
				//don't bother processing saved delta if forced full re-index is needed
				boolean stillNeedFullReIndex = forcedFullReIndexNeeded;
				if(!forcedFullReIndexNeeded) {
					//if there is a delta attempt to process it
					if(savedStateDelta != null) {
						stillNeedFullReIndex = false;
						try {
							//deal with reporting progress
							SubMonitor savedStateProgress = progress.newChild(1, SubMonitor.SUPPRESS_NONE);
							
							savedStateProgress.setTaskName(NLS.bind(SSECoreMessages.IndexManager_0_starting_1,
									new String[] {this.fName, SSECoreMessages.IndexManager_processing_deferred_resource_changes}));
							
							//process delta
							ResourceDeltaVisitor visitor = new ResourceDeltaVisitor(savedStateProgress,
									AbstractIndexManager.SOURCE_SAVED_STATE);
							savedStateDelta.accept(visitor);
							
							//process any remaining batched up resources to index
							visitor.processBatchedResourceEvents();
						} catch (CoreException e) {
							stillNeedFullReIndex = true;
							Logger.logException(this.fName + ": Could not process saved state. " + //$NON-NLS-1$
									"Forced to do a full workspace re-index.", e); //$NON-NLS-1$
						}
					}
				}
				progress.worked(1);
				
				//if need to process the entire workspace do so in another job
				if(stillNeedFullReIndex){
					this.fWorkspaceVisitorJob = new WorkspaceVisitorJob();
					this.fWorkspaceVisitorJob.schedule();
				}
				
				//update state
				this.fState = STATE_ENABLED;
			}
			this.fStarting = false;
		}
	}
	
	/**
	 * <p>Safely shuts down the manager.</p>
	 * 
	 * <p>This will block until the {@link #start(IResourceDelta, IProgressMonitor)} has
	 * finished (if running).  Also until the current resource event has finished being
	 * processed.  Finally it will block until the events still to be processed by
	 * the processing job have been preserved to be processed on the next call to
	 * {@link #start(IResourceDelta, IProgressMonitor)}.</p>
	 * 
	 * <p>If at any point during this shut down processes something goes wrong the
	 * manager will be sure that on the next call to {@link #start(IResourceDelta, IProgressMonitor)}
	 * the entire workspace will be re-processed.</p>
	 * 
	 * @throws InterruptedException
	 */
	public final void stop() throws InterruptedException {
		synchronized (this.fStartStopLock) {
			if(this.fState != STATE_DISABLED) {
				
				//stop listening for events, and wait for the current event to finish
				this.fResourceChangeListener.stop();
				
				// if currently visiting entire workspace, give up and try again next load
				boolean forceFullReIndexNextStart = false;
				if(this.fWorkspaceVisitorJob != null) {
					if (this.fWorkspaceVisitorJob.getState() != Job.NONE) {
						this.fWorkspaceVisitorJob.cancel();
						
						this.forceFullReIndexNextStart();
						forceFullReIndexNextStart = true;
					}
				}
				
				//stop the indexing job, only preserve if not already forcing a re-index
				forceFullReIndexNextStart = !this.fResourceEventProcessingJob.stop(!forceFullReIndexNextStart);
				
				//if preserving failed, then force re-index
				if(forceFullReIndexNextStart) {
					this.forceFullReIndexNextStart();
				}
				
				//update status
				this.fState = STATE_DISABLED;
			}
		}
	}
	
	/**
	 * @return the name of this indexer
	 */
	protected String getName() {
		return this.fName;
	}
	
	/**
	 * <p>Should be called by a client of the index this manager manages before the index
	 * is accessed, assuming the client wants an index consistent with the latest
	 * resource changes.</p>
	 * 
	 * <p>The supplied monitor will be used to supply user readable progress as the manager
	 * insures the index has been given all the latest resource events.  This monitor
	 * may be canceled, but if it is the state of the index is not guaranteed to be
	 * consistent with the latest resource change events.</p>
	 * 
	 * @param monitor Used to report user readable progress as the manager insures the
	 * index is consistent with the latest resource events.  This monitor can be canceled
	 * to stop waiting for consistency but then no guaranty is made about the consistency
	 * of the index in relation to unprocessed resource changes
	 * 
	 * @return <code>true</code> if the wait finished successfully and the manager is consistent,
	 * <code>false</code> otherwise, either an error occurred while waiting for the manager
	 * or the monitor was canceled
	 * 
	 * @throws InterruptedException This can happen when waiting for other jobs
	 */
	public final boolean waitForConsistent(IProgressMonitor monitor) {
		boolean success = true;
		boolean interupted = false;
		SubMonitor progress = SubMonitor.convert(monitor);
		
		//set up the progress of waiting
		int remainingWork = 4;
		progress.beginTask(NLS.bind(SSECoreMessages.IndexManager_Waiting_for_0, this.fName),
				remainingWork);
		
		//wait for start up
		if(this.fStarting && !monitor.isCanceled()) {
			SubMonitor startingProgress = progress.newChild(1);
			startingProgress.subTask(NLS.bind(SSECoreMessages.IndexManager_0_starting, this.fName));
			while(this.fStarting && !monitor.isCanceled()) {
				//this creates a never ending progress that still moves forward
				startingProgress.setWorkRemaining(UNKNOWN_WORK);
				startingProgress.newChild(1).worked(1);
				try {
					Thread.sleep(WAIT_TIME);
				} catch (InterruptedException e) {
					interupted = true;
				}
			}
		}
		progress.setWorkRemaining(--remainingWork);
		
		//wait for workspace visiting job
		if(this.fWorkspaceVisitorJob != null && this.fWorkspaceVisitorJob.getState() != Job.NONE && !monitor.isCanceled()) {
			SubMonitor workspaceVisitorProgress = progress.newChild(1);
			workspaceVisitorProgress.subTask(SSECoreMessages.IndexManager_Processing_entire_workspace_for_the_first_time);
			while(this.fWorkspaceVisitorJob.getState() != Job.NONE && !monitor.isCanceled()) {
				//this creates a never ending progress that still moves forward
				workspaceVisitorProgress.setWorkRemaining(UNKNOWN_WORK);
				workspaceVisitorProgress.newChild(1).worked(1);
				try {
					Thread.sleep(WAIT_TIME);
				} catch (InterruptedException e) {
					interupted = true;
				}
			}
		}
		progress.setWorkRemaining(--remainingWork);
		
		//wait for the current resource event
		if(this.fResourceChangeListener.isProcessingEvents() && !monitor.isCanceled()) {
			SubMonitor workspaceVisitorProgress = progress.newChild(1);
			workspaceVisitorProgress.subTask(SSECoreMessages.IndexManager_processing_recent_resource_changes);
			while(this.fResourceChangeListener.isProcessingEvents() && !monitor.isCanceled()) {
				workspaceVisitorProgress.setWorkRemaining(UNKNOWN_WORK);
				workspaceVisitorProgress.newChild(1).worked(1);
				try {
					this.fResourceChangeListener.waitForCurrentEvent(WAIT_TIME);
				} catch (InterruptedException e) {
					interupted = true;
				}
			}
		}
		progress.setWorkRemaining(--remainingWork);
		
		//wait for all files to be indexed
		if(this.fResourceEventProcessingJob.getNumResourceEventsToProcess() != 0 && !monitor.isCanceled()) {
			SubMonitor indexingProgress = progress.newChild(1);
			int prevNumResrouces;
			int numResources = this.fResourceEventProcessingJob.getNumResourceEventsToProcess();
			while(numResources != 0 && !monitor.isCanceled()) {
				//update the progress indicator
				indexingProgress.subTask(NLS.bind(SSECoreMessages.IndexManager_0_Indexing_1_Files,
								new Object[] {AbstractIndexManager.this.fName, "" + numResources})); //$NON-NLS-1$
				indexingProgress.setWorkRemaining(numResources);
				prevNumResrouces = numResources;
				numResources = this.fResourceEventProcessingJob.getNumResourceEventsToProcess();
				int numProcessed = prevNumResrouces - numResources;
				indexingProgress.worked(numProcessed > 0 ? numProcessed : 0);
				
				//give the index some time to do some indexing
				try {
					this.fResourceEventProcessingJob.waitForConsistant(WAIT_TIME);
				} catch (InterruptedException e) {
					interupted = true;
				}
			}
		}
		progress.setWorkRemaining(--remainingWork);
		
		if(monitor.isCanceled()) {
			success = false;
		}
		
		//reset the interrupted flag if we were interrupted
		if(interupted) {
			Thread.currentThread().interrupt();
		}
		
		return success;
	}
	
	/**
	 * <p>Called for each {@link IResource} given in a resource delta.  If the resource
	 * type is a file then used to determine if that file should be processed by the manager,
	 * if the resource type is a project or directory then it is used to determine if the children
	 * of the project or directory should be processed looking for file resources.</p>
	 * 
	 * <p><b>NOTE:</b> Even if <code>true</code> is returned for a directory resource that
	 * only means the children of the directory should be inspected for possible files to index.
	 * Directories themselves can not be managed in order to add to an index.</p>
	 * 
	 * @param type the {@link IResource#getType()} result of the resource to possibly index
	 * @param path the full {@link IPath} to the resource to possibly index
	 * @return <code>true</code> If the resource with the given <code>type</code> and
	 * <code>path</code> should either itself be indexed, or its children should be indexed,
	 * <code>false</code> if neither the described resource or any children should be indexed
	 */
	protected abstract boolean isResourceToIndex(int type, IPath path);
	
	/**
	 * <p>Called for each {@link ResourceEvent} gathered by the various sources and processed
	 * by the {@link ResourceEventProcessingJob}.  The implementation of this method
	 * should use the given information to update the index this manager is managing.</p>
	 * 
	 * @param source The source that reported this resource event
	 * @param action The action to be taken on the given <code>resource</code>
	 * @param resource The index should perform the given <code>action</code> on this
	 * resource
	 * @param movePath If the given <code>action</code> is {@link AbstractIndexManager#ACTION_ADD_MOVE_FROM}
	 * or {@link AbstractIndexManager#ACTION_REMOVE_MOVE_TO} then this field will not be
	 * null and reports the path the given <code>resource</code> was either moved from or
	 * moved to respectively.
	 * 
	 * @see AbstractIndexManager#SOURCE_RESOURCE_CHANGE
	 * @see AbstractIndexManager#SOURCE_SAVED_STATE
	 * @see AbstractIndexManager#SOURCE_WORKSPACE_SCAN
	 * @see AbstractIndexManager#SOURCE_PRESERVED_RESOURCES_TO_INDEX
	 * 
	 * @see AbstractIndexManager#ACTION_ADD
	 * @see AbstractIndexManager#ACTION_REMOVE
	 * @see AbstractIndexManager#ACTION_ADD_MOVE_FROM
	 * @see AbstractIndexManager#ACTION_REMOVE_MOVE_TO
	 */
	protected abstract void performAction(byte source, byte action, IResource resource,
			IPath movePath);
	
	/**
	 * <p>Gets the working location of the manager. This is where any relevant
	 * state can be persisted.</p>
	 * 
	 * @return the working location of the manager
	 */
    protected abstract IPath getWorkingLocation();
	
	/**
	 * <p>Next time the manager starts up force a full workspace index</p>
	 */
	private void forceFullReIndexNextStart() {
		IPath reIndexPath =
			AbstractIndexManager.this.getWorkingLocation().append(RE_PROCESS_FILE_NAME);
		File file = new File(reIndexPath.toOSString());
		try {
			file.createNewFile();
		} catch (IOException e) {
			Logger.logException(this.fName + ": Could not create file to tell manager to" + //$NON-NLS-1$
					" do a full re-index on next load. " + //$NON-NLS-1$
					AbstractIndexManager.LOG_ERROR_INDEX_INVALID, e);
		}
	}
	
	/**
	 * @return <code>true</code> if a full workspace index is needed as dictated by
	 * a previous call to {@link #forceFullReIndexNextStart()}, <code>false</code>
	 * otherwise
	 */
	private boolean isForcedFullReIndexNeeded() {
		boolean forcedFullReIndexNeeded = false;
		IPath reIndexPath =
			AbstractIndexManager.this.getWorkingLocation().append(RE_PROCESS_FILE_NAME);
		File file = new File(reIndexPath.toOSString());
		if(file.exists()) {
			file.delete();
			forcedFullReIndexNeeded = true;
		}
		
		return forcedFullReIndexNeeded;
	}
	
	/**
	 * <p>A system {@link Job} used to visit all of the files in the workspace
	 * looking for files to index.</p>
	 * 
	 * <p>This should only have to be done once per workspace on the first load,
	 * but if it fails or a SavedState can not be retrieved on a subsequent
	 * workspace load then this will have to be done again.</p>
	 */
	private class WorkspaceVisitorJob extends Job {
		/**
		 * <p>Default constructor that sets up this job as a system job</p>
		 */
		protected WorkspaceVisitorJob() {
			super(NLS.bind(SSECoreMessages.IndexManager_0_Processing_entire_workspace_for_the_first_time,
					AbstractIndexManager.this.fName));
			
			this.setUser(false);
			this.setSystem(true);
			this.setPriority(Job.LONG);
		}
		
		/**
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			try {
				//update status
				monitor.beginTask(
						NLS.bind(SSECoreMessages.IndexManager_0_Processing_entire_workspace_for_the_first_time,
								AbstractIndexManager.this.fName),
						IProgressMonitor.UNKNOWN);
				
				//visit the workspace
				WorkspaceVisitor visitor = new WorkspaceVisitor(monitor);
				ResourcesPlugin.getWorkspace().getRoot().accept(visitor, IResource.NONE);
				
				//process any remaining batched up resources to index
				visitor.processBatchedResourceEvents();
			} catch(CoreException e) {
				Logger.logException(AbstractIndexManager.this.fName +
						": Failed visiting entire workspace for initial index. " + //$NON-NLS-1$
						AbstractIndexManager.LOG_ERROR_INDEX_INVALID, e);
			}
			
			IStatus status;
			if(monitor.isCanceled()) {
				status = Status.CANCEL_STATUS;
			} else {
				status = Status.OK_STATUS;
			}
			
			return status;
		}
		
		/**
		 * <p>An {@link IResourceProxyVisitor} used to visit all of the files in the
		 * workspace looking for files to add to the index.</p>
		 * 
		 * <p><b>NOTE: </b>After this visitor is used {@link WorkspaceVisitor#processBatchedResourceEvents()
		 * must be called to flush out the last of the {@link ResourceEvent}s produced
		 * by this visitor.</p>
		 */
		private class WorkspaceVisitor implements IResourceProxyVisitor {
			/** {@link IProgressMonitor} used to report status and check for cancellation */
			private SubMonitor fProgress;
			
			/**
			 * {@link Map}&lt{@link IResource}, {@link ResourceEvent}&gt
			 * <p>Map of resources events created and batched up by this visitor.
			 * These events are periodical be sent off to the
			 * {@link ResourceEventProcessingJob} but need to be sent off
			 * one final time after this visitor finishes it work.</p>
			 * 
			 * @see #processBatchedResourceEvents()
			 */
			private Map fBatchedResourceEvents;
			
			/**
			 * <p>Default constructor</p>
			 * @param monitor used to report status and allow this visitor to be canceled
			 */
			protected WorkspaceVisitor(IProgressMonitor monitor) {
				this.fProgress = SubMonitor.convert(monitor);
				this.fBatchedResourceEvents = new LinkedHashMap(BATCH_UP_AMOUNT);
			}

			/**
			 * <p>As long as the monitor is not canceled visit each file in the workspace
			 * that should be visited.</p>
			 * 
			 * @see org.eclipse.core.resources.IResourceProxyVisitor#visit(org.eclipse.core.resources.IResourceProxy)
			 * @see AbstractIndexManager#shouldVisit(String)
			 */
			public boolean visit(IResourceProxy proxy) throws CoreException {
				this.fProgress.subTask(proxy.getName());
				
				boolean visitChildren = false;
				
				/* if not canceled or a hidden resource then process file
				 * else don't visit children
				 */
				if(!this.fProgress.isCanceled()) {
					/* if root node always visit children
					 * else ask manager implementation if resource and its children should be visited 
					 */
					IPath path = proxy.requestFullPath();
					if(path.toString().equals("/")) { //$NON-NLS-1$
						visitChildren = true;
					} else if(isResourceToIndex(proxy.getType(), path)) {
						if(proxy.getType() == IResource.FILE) {
							//add the file to be indexed
							IFile file = (IFile) proxy.requestResource();
							if(file.exists()) {
								this.fBatchedResourceEvents.put(file, new ResourceEvent(
										AbstractIndexManager.SOURCE_WORKSPACE_SCAN,
										AbstractIndexManager.ACTION_ADD,
										null));
							}
						}
						visitChildren = true;
					}
					visitChildren = false;
				} else {
					visitChildren = false;
				}
				
				//batch up resource changes before sending them out
				if(this.fBatchedResourceEvents.size() >= BATCH_UP_AMOUNT) {
					this.processBatchedResourceEvents();
				}
				
				return visitChildren;
			}
			
			/**
			 * <p>Sends any batched up resource events created by this visitor to the
			 * {@link ResourceEventProcessingJob}.<p>
			 * 
			 * <p><b>NOTE:</b> This will be called every so often as the visitor is
			 * visiting resources but needs to be called a final time by the user of
			 * this visitor to be sure the final events are sent off</p>
			 */
			protected void processBatchedResourceEvents() {
				AbstractIndexManager.this.fResourceEventProcessingJob.addResourceEvents(
						this.fBatchedResourceEvents);
				this.fBatchedResourceEvents.clear();
			}
		}
	}
	
	/**
	 * <p>Used to listen to resource change events in the workspace.  These events
	 * are batched up and then passed onto the {@link ResourceEventProcessingJob}.</p>
	 */
	private class ResourceChangeListener implements IResourceChangeListener {
		/**
		 * <p>The number of events currently being processed by this listener.</p>
		 * <p>Use the {@link #fEventsBeingProcessedLock} when reading or writing this field</p>
		 * 
		 * @see #fEventsBeingProcessedLock
		 */
		private volatile int fEventsBeingProcessed;
		
		/**
		 * Lock to use when reading or writing {@link #fEventsBeingProcessed}
		 * 
		 * @see #fEventsBeingProcessed
		 */
		private final Object fEventsBeingProcessedLock = new Object();
		
		/**
		 * <p>Current state of this listener</p>
		 * 
		 * @see AbstractIndexManager#STATE_DISABLED
		 * @see AbstractIndexManager#STATE_ENABLED
		 */
		private volatile byte fState;
		
		/**
		 * <p>Default constructor</p>
		 */
		protected ResourceChangeListener() {
			this.fState = STATE_DISABLED;
			this.fEventsBeingProcessed = 0;
		}
		
		/**
		 * <p>Start listening for resource change events</p>
		 */
		protected void start() {
			this.fState = STATE_ENABLED;
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		}
		
		/**
		 * <p>Stop listening for resource change events and if already processing
		 * an event then wait for that processing to finish</p>
		 * 
		 * @throws InterruptedException waiting for a current event to finish processing
		 * could be interrupted
		 */
		protected void stop() throws InterruptedException {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
			
			//wait indefinitely for current event to finish processing
			this.waitForCurrentEvent(0);
			
			this.fState = STATE_DISABLED;
		}
		
		/**
		 * <p>Blocks until either the current resource event has been processed or
		 * until the given timeout has passed.</p>
		 * 
		 * @param timeout block until either this timeout elapses (0 means never to timeout)
		 * or the current resource change event finishes being processed
		 * 
		 * @throws InterruptedException This can happen when waiting for a lock
		 */
		protected void waitForCurrentEvent(int timeout) throws InterruptedException {
			synchronized (this.fEventsBeingProcessedLock) {
				if(this.fEventsBeingProcessed != 0) {
					this.fEventsBeingProcessedLock.wait(timeout);
				}
			}
		}
		
		/**
		 * @return <code>true</code> if this listener is currently processing any
		 * events, <code>false</code> otherwise.
		 */
		protected boolean isProcessingEvents() {
			return this.fEventsBeingProcessed != 0;
		}
		
		/**
		 * <p>Process a resource change event.  If it is a pre-close or pre-delete then
		 * the {@link ResourceEventProcessingJob} is paused so it does not try to
		 * process resources that are about to be deleted.  The {@link ResourceDeltaVisitor}
		 * is used to actually process the event.</p>
		 * 
		 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
		 * @see ResourceDeltaVisitor
		 */
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				//update the number of events being processed
				synchronized (this.fEventsBeingProcessedLock) {
					++this.fEventsBeingProcessed;
				}
				
				if(this.fState == STATE_ENABLED) {
					switch(event.getType()) {
						case IResourceChangeEvent.PRE_CLOSE:
						case IResourceChangeEvent.PRE_DELETE:{
							//pre-close or pre-delete pause the persister job so it does not interfere
							AbstractIndexManager.this.fResourceEventProcessingJob.pause();
							break;
						}
						case IResourceChangeEvent.POST_BUILD:
						case IResourceChangeEvent.POST_CHANGE: {
							//post change start up the indexer job and process the delta
							AbstractIndexManager.this.fResourceEventProcessingJob.unPause();
							
							// only analyze the full (starting at root) delta hierarchy
							IResourceDelta delta = event.getDelta();
							if (delta != null && delta.getFullPath().toString().equals("/")) { //$NON-NLS-1$
								try {
									//use visitor to visit all children
									ResourceDeltaVisitor visitor = new ResourceDeltaVisitor(
											AbstractIndexManager.SOURCE_RESOURCE_CHANGE);
									delta.accept(visitor, false);
									
									//process any remaining batched up resources to index
									visitor.processBatchedResourceEvents();
								} catch (CoreException e) {
									Logger.logException(AbstractIndexManager.this.fName +
											": Failed visiting resrouce change delta. " + //$NON-NLS-1$
											AbstractIndexManager.LOG_ERROR_INDEX_INVALID, e);
								}
							}
							break;
						}
					}
				} else {
					Logger.log(Logger.ERROR, "A resource change event came in after " + //$NON-NLS-1$
							AbstractIndexManager.this.fName + " shut down. This should never " + //$NON-NLS-1$
							"ever happen, but if it does the index may now be inconsistant."); //$NON-NLS-1$
				}
			} finally {
				//no matter how we exit be sure to update the number of events being processed
				synchronized (this.fEventsBeingProcessedLock) {
					--this.fEventsBeingProcessed;
					
					//if currently not events being processed, then notify
					if(this.fEventsBeingProcessed == 0) {
						this.fEventsBeingProcessedLock.notifyAll();
					}
				}
			}
		}
	}
	
	/**
	 * <p>Used to visit {@link IResourceDelta}s from both the {@link IResourceChangeListener}
	 * and from a {@link ISaveParticipant} given to {@link AbstractIndexManager#start(IResourceDelta, IProgressMonitor)}.
	 * The resource events are batched into groups of {@link AbstractIndexManager#BATCH_UP_AMOUNT}
	 * before being passed onto the {@link ResourceEventProcessingJob}.</p>
	 * 
	 * <p><b>NOTE 1: </b> This class is intended for one time use, thus a new instance should
	 * be instantiated each time this visitor is needed to process a new {@link IResourceDelta}.</p>
	 * 
	 * <p><b>NOTE 2: </b> Be sure to call {@link ResourceDeltaVisitor#processBatchedResourceEvents()}
	 * after using this visitor to be sure any remaining events get passed onto the
	 * {@link ResourceEventProcessingJob}.</p>
	 * 
	 * @see ResourceDeltaVisitor#processBatchedResourceEvents()
	 */
	private class ResourceDeltaVisitor implements IResourceDeltaVisitor {
		/** {@link IProgressMonitor} used to report status */
		private SubMonitor fProgress;
		
		/**
		 * <p>The source that should be used when sending resource events to the
		 * {@link ResourceEventProcessingJob}.</p>
		 * 
		 * @see AbstractIndexManager#SOURCE_SAVED_STATE
		 * @see AbstractIndexManager#SOURCE_RESOURCE_CHANGE
		 */
		private byte fSource;
		
		/**
		 * <p>Due to the nature of a visitor it has no way of knowing the total amount
		 * of work it has to do but it can start to predict it based on the number of
		 * children of each event it processes and whether it plans on visiting those
		 * children</p>
		 */
		private int fPredictedWorkRemaining;
		
		/**
		 * {@link Map}&lt{@link IResource}, {@link ResourceEvent}&gt
		 * <p>Map of resources events created and batched up by this visitor.
		 * These events are periodical be sent off to the
		 * {@link ResourceEventProcessingJob} but need to be sent off
		 * one final time after this visitor finishes it work.</p>
		 * 
		 * @see #processBatchedResourceEvents()
		 */
		private Map fBatchedResourceEvents;
		
		/**
		 * <p>Creates a visitor that will create resource events based on the resources
		 * it visits and using the given source as the source of the events.</p>
		 * 
		 * @param source The source of the events that should be used when creating
		 * resource events from visited resources
		 * 
		 * @see AbstractIndexManager#SOURCE_RESOURCE_CHANGE
		 * @see AbstractIndexManager#SOURCE_SAVED_STATE
		 */
		protected ResourceDeltaVisitor(byte source) {
			this(SubMonitor.convert(null), source);
		}
		
		/**
		 * <p>Creates a visitor that will create resource events based on the resources
		 * it visits and using the given source as the source of the events and
		 * report its status to the given progress as best it can as it visits
		 * resources.</p>
		 * 
		 * <p><b>NOTE:</b> While the {@link SubMonitor} is provided to report status the
		 * visitor will not honor any cancellation requests.</p>
		 * 
		 * @param progress Used to report status. This visitor can <b>not</b> be
		 * canceled
		 * @param source The source of the events that should be used when creating
		 * resource events from visited resources
		 * 
		 * @see AbstractIndexManager#SOURCE_RESOURCE_CHANGE
		 * @see AbstractIndexManager#SOURCE_SAVED_STATE
		 */
		protected ResourceDeltaVisitor(SubMonitor progress, byte source) {
			this.fProgress = progress;
			this.fSource = source;
			this.fBatchedResourceEvents = new LinkedHashMap(BATCH_UP_AMOUNT);
			this.fPredictedWorkRemaining = 1;
		}
		
		/**
		 * <p>Transforms each {@link IResourceDelta} into a {@link ResourceEvent}.
		 * Batches up these {@link ResourceEvent}s and then passes them onto the
		 * {@link ResourceEventProcessingJob}.</p>
		 * 
		 * <p><b>NOTE 1: </b> Be sure to call {@link ResourceDeltaVisitor#processBatchedResourceEvents()}
		 * after using this visitor to be sure any remaining events get passed onto the
		 * {@link ResourceEventProcessingJob}.</p>
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 * @see #processBatchedResourceEvents()
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			//report status
			this.fProgress.subTask(
					NLS.bind(SSECoreMessages.IndexManager_0_resources_to_go_1,
							new Object[] {"" + fPredictedWorkRemaining, delta.getFullPath().toString()})); //$NON-NLS-1$
			
			//process delta if resource not hidden
			boolean visitChildren = false;
			
			/* if root node always visit its children
			 * else ask manager implementation if resource and its children should be visited 
			 */
			if(delta.getFullPath().toString().equals("/")) { //$NON-NLS-1$
				visitChildren = true;
			} else {
				IResource resource = delta.getResource();
	
				//check if should index resource or its children
				if(isResourceToIndex(resource.getType(), resource.getFullPath())) {
					if(resource.getType() == IResource.FILE) {
					
						switch (delta.getKind()) {
							case IResourceDelta.CHANGED : {
								/* ignore any change that is not a CONTENT, REPLACED, TYPE,
								 * or MOVE_FROM change
								 */
								if( !((delta.getFlags() & IResourceDelta.CONTENT) != 0) &&
										!((delta.getFlags() & IResourceDelta.REPLACED) != 0) &&
										!((delta.getFlags() & IResourceDelta.TYPE) != 0) &&
										!(((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0))) {
									
									break;
								}
							}
							//$FALL-THROUGH$ it is intended that sometimes a change will fall through to add
							case IResourceDelta.ADDED : {
								if((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
									//create add move from action
									this.fBatchedResourceEvents.put(resource, new ResourceEvent(
											this.fSource,
											AbstractIndexManager.ACTION_ADD_MOVE_FROM,
											delta.getMovedFromPath()));
		
								} else {
									//create add action
									this.fBatchedResourceEvents.put(resource, new ResourceEvent(
											this.fSource,
											AbstractIndexManager.ACTION_ADD,
											null));
								}
								
								break;
							}
							case IResourceDelta.REMOVED : {
								if((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
									//create remove move to action
									this.fBatchedResourceEvents.put(resource, new ResourceEvent(
											this.fSource,
											AbstractIndexManager.ACTION_REMOVE_MOVE_TO,
											delta.getMovedToPath()));
								} else {
									//create remove action
									this.fBatchedResourceEvents.put(resource, new ResourceEvent(
											this.fSource,
											AbstractIndexManager.ACTION_REMOVE,
											null));
								}
								break;
							}
						}
					}//end is file
					
					visitChildren = true;
				} else {
					visitChildren = false;
				}
					
				
				
				//deal with trying to report progress
				if(visitChildren) {
					this.fPredictedWorkRemaining += delta.getAffectedChildren().length;
				}
				this.fProgress.setWorkRemaining(this.fPredictedWorkRemaining);
				this.fProgress.worked(1);
				--this.fPredictedWorkRemaining;
				
				//batch up resource changes before sending them out
				if(this.fBatchedResourceEvents.size() >= BATCH_UP_AMOUNT) {
					this.processBatchedResourceEvents();
				}
			}
			
			return visitChildren;
		}
		
		/**
		 * <p>Sends any batched up resource events created by this visitor to the
		 * {@link ResourceEventProcessingJob}.<p>
		 * 
		 * <p><b>NOTE:</b> This will be called every so often as the visitor is
		 * visiting resources but needs to be called a final time by the user of
		 * this visitor to be sure the final events are sent off</p>
		 */
		protected void processBatchedResourceEvents() {
			AbstractIndexManager.this.fResourceEventProcessingJob.addResourceEvents(
					this.fBatchedResourceEvents);
			this.fBatchedResourceEvents.clear();
		}
	}
	
	/**
	 * <p>Collects {@link ResourceEvent}s from the different sources and then processes
	 * each one by calling {@link AbstractIndexManager#performAction(byte, byte, IResource, IPath)}
	 * for each {@link ResourceEvent}.</p>
	 * 
	 * @see AbstractIndexManager#performAction(byte, byte, IResource, IPath)
	 */
	private class ResourceEventProcessingJob extends Job {
		/** Length to delay when scheduling job */
		private static final int DELAY = 500;
		
		/**
		 * <p>Name of the file where resource events still to index
		 * will be preserved for the next start up.</p>
		 */
		private static final String PRESERVED_RESOURCE_EVENTS_TO_PROCESS_FILE_NAME = ".preservedResourceEvents";  //$NON-NLS-1$
		
		/**
		 * <p>This needs to be updated if {@link #preserveReceivedResourceEvents()} ever
		 * changes how it persists resource events so that {@link #loadPreservedReceivedResourceEvents(SubMonitor)}
		 * knows when opening a file that the format is of the current version and if not
		 * knows it does not know how to read the older version.</p>
		 * 
		 * @see #preserveReceivedResourceEvents()
		 * @see #loadPreservedReceivedResourceEvents(SubMonitor)
		 */
		private static final long serialVersionUID = 1L;
		
		/** Whether this job has been paused or not */
		private volatile boolean fIsPaused;
		
		/**
		 * {@link Map}&lt{@link IResource}, {@link ResourceEvent}&gt
		 * <p>The list of resources events to be processed</p>
		 */
		private Map fResourceEvents;
		
		/** Lock used when accessing {@link #fBatchedResourceEvents} */
		private final Object fResourceEventsLock = new Object();
		
		/**
		 * Locked used for allowing other jobs to wait on this job.  This job
		 * will notify those waiting on this lock whenever it is done processing
		 * all resource events it currently knows about.
		 * 
		 * @see #waitForConsistant(int)
		 */
		private final Object fToNotifyLock = new Object();
		
		/**
		 * <p>Sets up this job as a long running system job</p>
		 */
		protected ResourceEventProcessingJob() {
			super(NLS.bind(SSECoreMessages.IndexManager_0_Processing_resource_events,
					AbstractIndexManager.this.fName));
			
			//set this up as a long running system job
			this.setUser(false);
			this.setSystem(true);
			this.setPriority(Job.LONG);
			
			this.fIsPaused = false;
			this.fResourceEvents = new LinkedHashMap();
		}
		
		/**
		 * <p>Loads any preserved {@link ResourceEvent}s from the last time {@link #stop(boolean)}
		 * was invoked and schedules the job to be run</p>
		 * 
		 * <p><b>NOTE: </b>Should be used instead of of calling {@link Job#schedule()}
		 * because this method also takes care of loading preserved state.</p>
		 * 
		 * @param loadPreservedResourceEvents <code>true</code> if should load any
		 * preserved {@link ResourceEvent}s from the last time {@link #stop(boolean)}
		 * was invoked
		 * 
		 * @return <code>true</code> if either <code>loadPreservedResourceEvents</code>
		 * was false or there was success in loading the preserved {@link ResourceEvent}s.
		 * If <code>false</code> then some {@link ResourceEvent}s may have been loosed
		 * and to insure index consistency with the workspace a full workspace  re-index
		 * is needed.
		 * 
		 * @see #stop(boolean)
		 */
		protected synchronized boolean start(boolean loadPreservedResourceEvents, 
				SubMonitor progress) {
			
			boolean successLoadingPreserved = true;
			
			//attempt to load preserved resource events if requested
			if(!loadPreservedResourceEvents) {
				File preservedResourceEventsFile = this.getPreservedResourceEventsFile();
				preservedResourceEventsFile.delete();
			} else {
				successLoadingPreserved = this.loadPreservedReceivedResourceEvents(progress);
			}
			
			//start up the job
			this.schedule();
			
			return successLoadingPreserved;
		}
		
		/**
		 * <p>Immediately stops the job and preserves any {@link ResourceEvent}s in the queue
		 * to be processed by not yet processed if requested</p>
		 * 
		 * @param preserveResourceEvents <code>true</code> to preserve any {@link ResourceEvent}s
		 * in the queue yet to be processed, <code>false</code> otherwise
		 * 
		 * @return <code>true</code> if either <code>preserveResourceEvents</code> is 
		 * <code>false</code> or if there was success in preserving the {@link ResourceEvent}s
		 * yet to be processed.  If <code>false</code> then the preserving failed and a
		 * full workspace re-processing is needed the next time the manager is started
		 * 
		 * @throws InterruptedException This could happen when trying to cancel or join 
		 * the job in progress, but it really shouldn't
		 * 
		 * @see #start(boolean, SubMonitor)
		 */
		protected synchronized boolean stop(boolean preserveResourceEvents) throws InterruptedException {
			//this will not block indefinitely because it is known this job can be canceled
			this.cancel();
			this.join();
			
			//preserve if requested, else be sure no preserve file is left over for next start
			boolean success = true;
			if(preserveResourceEvents && this.hasResourceEventsToProcess()) {
				success = this.preserveReceivedResourceEvents();
			} else {
				this.getPreservedResourceEventsFile().delete();
			}
			
			return success;
		}
		
		/**
		 * @return <code>true</code> if job is currently running or paused
		 * 
		 * @see #pause()
		 * @see #unPause()
		 */
		protected synchronized boolean isProcessing() {
			return this.getState() != Job.NONE || this.fIsPaused;
		}
		
		/**
		 * <p>Un-pauses this job.  This has no effect if the job is already running.</p>
		 * <p>This should be used in place of {@link Job#schedule()} to reset state
		 * caused by calling {@link #pause()}</p>
		 * 
		 * @see #pause()
		 */
		protected synchronized void unPause() {
			this.fIsPaused = false;
			
			//get the job running again depending on its current state
			if(this.getState() == Job.SLEEPING) {
				this.wakeUp(DELAY);
			} else {
				this.schedule(DELAY);
			}
		}
		
		/**
		 * <p>Pauses this job, even if it is running</p>
		 * <p>This should be used in place of {@link Job#sleep()} because {@link Job#sleep()}
		 * will not pause a job that is already running but calling this will pause this job
		 * even if it is running. {@link #unPause()} must be used to start this job again</p>
		 * 
		 * @see #unPause()
		 */
		protected synchronized void pause() {
			//if job is already running this will force it to pause
			this.fIsPaused = true;
			
			//this only works if the job is not running
			this.sleep();
		}
		
		/**
		 * <p>Adds a batch of {@link ResourceEvent}s to the queue of events to be processed.
		 * Will also un-pause the job if it is not already running</p>
		 * 
		 * @param resourceEvents {@link Map}&lt{@link IResource}, {@link ResourceEvent}&gt
		 * A batch of {@link ResourceEvent}s to be processed
		 * 
		 * @see #addResourceEvent(ResourceEvent)
		 * @see #unPause()
		 */
		protected void addResourceEvents(Map resourceEvents) {
			Iterator iter = resourceEvents.keySet().iterator();
			while(iter.hasNext()) {
				IResource resource = (IResource)iter.next();
				ResourceEvent resourceEvent = (ResourceEvent)resourceEvents.get(resource);
				addResourceEvent(resource, resourceEvent);
			}
			
			//un-pause the processor if it is not already running
			if(!isProcessing()) {
				this.unPause();
			}
		}
		
		/**
		 * <p>Gets the number of {@link ResourceEvent}s left to process by this job. This
		 * count is only valid for the exact moment it is returned because events are
		 * constantly being added and removed from the queue of events to process</p>
		 * 
		 * @return the number of {@link ResourceEvent}s left to process
		 */
		protected int getNumResourceEventsToProcess() {
			return this.fResourceEvents.size();
		}
		
		/**
		 * <p>Blocks until either the given timeout elapses (0 means never to timeout), or
		 * there are currently no {@link ResourceEvent}s to process or being processed
		 * by this job</p>
		 * 
		 * @param timeout block until either this timeout elapses (0 means never to timeout)
		 * or there are currently no {@link ResourceEvent}s to process or being processed
		 * by this job
		 * 
		 * @throws InterruptedException This can happen when waiting for a lock
		 */
		protected void waitForConsistant(int timeout) throws InterruptedException {
			if(hasResourceEventsToProcess() || isProcessing()) {
				synchronized (this.fToNotifyLock) {
					this.fToNotifyLock.wait(timeout);
				}
			}
		}
		
		/**
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			try {
				//report status
				SubMonitor progress = SubMonitor.convert(monitor);
				
				while(!this.fIsPaused && !monitor.isCanceled() && this.hasResourceEventsToProcess()) {
					//report status
					progress.setTaskName(NLS.bind(SSECoreMessages.IndexManager_0_Indexing_1_Files,
									new Object[] {AbstractIndexManager.this.fName, "" + getNumResourceEventsToProcess()})); //$NON-NLS-1$
					progress.setWorkRemaining(getNumResourceEventsToProcess());
					
					//get the next event to process
					ResourceEvent resourceEvent = null;
					IResource resource = null;
					synchronized (this.fResourceEventsLock) {
						resource = (IResource) this.fResourceEvents.keySet().iterator().next();
						resourceEvent = (ResourceEvent)this.fResourceEvents.remove(resource);
					}
					
					//report status
					monitor.subTask(resource.getName());
					
					//perform action safely
					final byte source = resourceEvent.fSource;
					final byte action = resourceEvent.fAction;
					final IResource finResource = resource;
					final IPath movePath = resourceEvent.fMovePath;
					SafeRunner.run(new ISafeRunnable() {
						public void run() throws Exception {
							AbstractIndexManager.this.performAction(source, action,
									finResource, movePath);
						}
	
						public void handleException(Throwable e) {
							Logger.logException("Error while performing an update to the index. " + //$NON-NLS-1$
									AbstractIndexManager.LOG_ERROR_INDEX_INVALID, e);
						}
					});
					
					//report progress
					progress.worked(1);
					
					//avoid dead locks
					Job.getJobManager().currentJob().yieldRule(monitor);
				}
				
				//done work
				monitor.done();
			} finally {
				//want to be sure we notify no matter how we exit
				this.notifyIfConsistant();
			}
			
			/* if canceled then return CANCEL,
			 * else if done or paused return OK
			 */
			IStatus exitStatus;
			if(monitor.isCanceled()) {
				exitStatus = Status.CANCEL_STATUS;
			} else {
				exitStatus = Status.OK_STATUS;
			}
			
			return exitStatus;
		}
		
		/**
		 * <p>If resource not already scheduled to be processed, schedule it
		 * else if resource already scheduled to be processed, update the action only if
		 * the new action comes from a resource change event.</p>
		 * 
		 * <p>Ignore other sources for updating existing resource events because all other
		 * sources are "start-up" type sources and thus only {@link ResourceEvent} with a
		 * source of a resource change event trump existing events.</p>
		 * 
		 * @param resourceEvent {@link ResourceEvent} to be processed by this job
		 */
		private void addResourceEvent(IResource resource, ResourceEvent resourceEvent) {
			
			synchronized (this.fResourceEventsLock) {
				/* if resource not already scheduled to be processed, schedule it
				 * else if resource already scheduled to be processed, update the action only if
				 * 		the new action comes from a resource change event
				 */
				if(!this.fResourceEvents.containsKey(resource)) {
					this.fResourceEvents.put(resource, resourceEvent);
				} else if(resourceEvent.fSource == AbstractIndexManager.SOURCE_RESOURCE_CHANGE) {
					((ResourceEvent)this.fResourceEvents.get(resource)).fAction = resourceEvent.fAction;
				} else {
					//Purposely ignoring all other resource events
				}
			}
		}
		
		/**
		 * @return <code>true</code> if there are any resources to process,
		 * <code>false</code> otherwise
		 */
		private boolean hasResourceEventsToProcess() {
			return !this.fResourceEvents.isEmpty();
		}
		
		/**
		 * <p>Preserves all of the resource events that have been received by this
		 * manager but not yet processed</p>
		 * 
		 * <p>If this operation was successful then the next time the manager starts
		 * it can load these events and process them.  If it was not successful then a
		 * full re-processing of the entire workspace will need to take place to be
		 * sure the index is consistent.</p>
		 * 
		 * <p><b>NOTE:</b> If this method changes how it preserves these events then
		 * {@link #serialVersionUID} will need to be incremented so that the manager
		 * does not attempt to load an old version of the file that may exist in a users
		 * workspace.  Also {@link #loadPreservedReceivedResourceEvents(SubMonitor)} will
		 * have to be updated to load the new file structure.</p>
		 * 
		 * @return <code>true</code> if successfully preserved the resource events that
		 * have been received by not yet processed, <code>false</code> otherwise
		 * 
		 * @see #serialVersionUID
		 * @see #loadPreservedReceivedResourceEvents(SubMonitor)
		 */
		private boolean preserveReceivedResourceEvents() {
			File preservedResourceEventsFile = this.getPreservedResourceEventsFile();
			boolean success = true;
			
			synchronized (this.fResourceEventsLock) {
				DataOutputStream dos = null;
				try {
					//if file already exists delete it
					if(preservedResourceEventsFile.exists()) {
						preservedResourceEventsFile.delete();
						preservedResourceEventsFile.createNewFile();
					}
					
					//create output objects
					FileOutputStream fos = new FileOutputStream(preservedResourceEventsFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					dos = new DataOutputStream(bos);
					
					//write serial version
					dos.writeLong(serialVersionUID);
					
					//write size
					dos.writeInt(this.getNumResourceEventsToProcess());
					
					//write out all the information needed to restore the resource events to process
					Iterator iter = this.fResourceEvents.keySet().iterator();
					while(iter.hasNext()) {
						IResource resource = (IResource)iter.next();
						ResourceEvent resourceEvent = (ResourceEvent)this.fResourceEvents.get(resource);
						
						if(resourceEvent.fSource != AbstractIndexManager.SOURCE_WORKSPACE_SCAN) {
							//write out information
							dos.writeByte(resourceEvent.fAction);
							dos.writeByte(resource.getType());
							byte[] pathBytes = resource.getFullPath().toString().getBytes(ENCODING_UTF16);
							dos.write(pathBytes);
							dos.writeByte('\0');
							if(resourceEvent.fMovePath != null) {
								dos.writeBytes(resourceEvent.fMovePath.toPortableString());
							}
							dos.writeByte('\0');
						}
					}
					
					this.fResourceEvents.clear();
					
					dos.flush();
				} catch (FileNotFoundException e) {
					Logger.logException(AbstractIndexManager.this.fName +
							": Exception while opening file to preserve resrouces to index.", //$NON-NLS-1$
							e);
					success = false;
				} catch (IOException e) {
					Logger.logException(AbstractIndexManager.this.fName +
							": Exception while writing to file to preserve resrouces to index.", //$NON-NLS-1$
							e);
					success = false;
				} finally {
					//be sure to close output
					if(dos != null) {
						try {
							dos.close();
						} catch (IOException e) {
							Logger.logException(AbstractIndexManager.this.fName +
									": Exception while closing file with preserved resources to index.", //$NON-NLS-1$
									e);
							success = false;
						}
					}
				}
				
				//if failed, for consistency must do a full re-process next workspace load
				if(!success) {
					preservedResourceEventsFile.delete();
				}
			}
			
			return success;
		}
		
		/**
		 * <p>Loads the received resource events that were preserved during the manager's
		 * last shut down so they can be processed now</p>
		 * 
		 * <p>If this operation is not successful then a full re-processing of the
		 * entire workspace is needed to be sure the index is consistent.</p>
		 * 
		 * @param progress used to report status of loading the preserved received resource events
		 * @return <code>true</code> if the loading of the preserved received resource events
		 * was successful, <code>false</code> otherwise.
		 * 
		 * @see #serialVersionUID
		 * @see #preserveReceivedResourceEvents()
		 */
		private boolean loadPreservedReceivedResourceEvents(SubMonitor progress) {
			progress.subTask(SSECoreMessages.IndexManager_processing_deferred_resource_changes);
			
			boolean success = true;
			File preservedResourceEventsFile = this.getPreservedResourceEventsFile();
			
			if(preservedResourceEventsFile.exists()) {
				Map preservedResourceEvents = null;
				
				DataInputStream dis = null;
				try {
					FileInputStream fis = new FileInputStream(preservedResourceEventsFile);
					BufferedInputStream bis = new BufferedInputStream(fis);
					dis = new DataInputStream(bis);
					
					//check serial version first
					long preservedSerialVersionUID = dis.readLong();
					if(preservedSerialVersionUID == serialVersionUID) {
						
						//read each record
						int numberOfRecords = dis.readInt();
						preservedResourceEvents = new LinkedHashMap(numberOfRecords);
						progress.setWorkRemaining(numberOfRecords);
						for(int i = 0; i < numberOfRecords; ++i) {
							//action is first byte
							byte action = dis.readByte();
							
							//file type is the next byte 
							byte fileType = dis.readByte();
							
							//resource location are the next bytes
							ByteArrayOutputStream resourceLocoationBOS = new ByteArrayOutputStream();
							byte b = dis.readByte();
							while(b != '\0') {
								resourceLocoationBOS.write(b);
								b = dis.readByte();
							}
							
							//move path are the next bytes
							ByteArrayOutputStream movePathBOS = new ByteArrayOutputStream();
							b = dis.readByte();
							while(b != '\0') {
								movePathBOS.write(b);
								b = dis.readByte();
							}
							
							//get the resource
							IResource resource = null;
							IPath resourcePath = new Path(new String(resourceLocoationBOS.toByteArray(), ENCODING_UTF16));
							if(fileType == IResource.FILE) {
								resource =
									ResourcesPlugin.getWorkspace().getRoot().getFile(resourcePath);
							} else {
								resource =
									ResourcesPlugin.getWorkspace().getRoot().getFolder(resourcePath);
							}
							
							//get the move path
							IPath movePath = null;
							if(movePathBOS.size() != 0) {
								movePath = new Path(new String(movePathBOS.toByteArray(), ENCODING_UTF16));
							}
							
							//add the object to the list of of preserved resources
							preservedResourceEvents.put(resource, new ResourceEvent(
									AbstractIndexManager.SOURCE_PRESERVED_RESOURCES_TO_INDEX,
									action, movePath));
							
							progress.worked(1);
						}	
					} else {
						success = false;
					}
				} catch (FileNotFoundException e) {
					Logger.logException(AbstractIndexManager.this.fName +
							": Exception while opening file to read preserved resources to index.", //$NON-NLS-1$
							e);
					success = false;
				} catch (IOException e) {
					Logger.logException(AbstractIndexManager.this.fName +
							": Exception while reading from file of preserved resources to index.", //$NON-NLS-1$
							e);
					success = false;
				} finally {
					if(dis != null) {
						try {
							dis.close();
						} catch (IOException e) {
							Logger.logException(AbstractIndexManager.this.fName +
									": Exception while closing file of preserved resources" + //$NON-NLS-1$
									" to index that was just read.  This should have no" + //$NON-NLS-1$
									" effect on the consistency of the index.", //$NON-NLS-1$
									e);
						}
					}
				}
				
				//if success loading preserved then add to master list
				if(success) {
					synchronized (this.fResourceEventsLock) {
						Iterator iter = preservedResourceEvents.keySet().iterator();
						while(iter.hasNext()) {
							IResource resource = (IResource)iter.next();
							ResourceEvent event = (ResourceEvent)preservedResourceEvents.get(resource);
							this.fResourceEvents.put(resource, event);
						}
					}
				} else {
					//failed reading file, so delete it
					preservedResourceEventsFile.delete();
				}
			}
			
			return success;
		}
		
		/**
		 * @return {@link File} that contains any resource events received but not processed
		 * by this manager the last time it shutdown. This file may or may not actually
		 * exist.
		 * 
		 * @see #preserveReceivedResourceEvents()
		 * @see #loadPreservedReceivedResourceEvents(SubMonitor)
		 */
		private File getPreservedResourceEventsFile() {
			IPath preservedResroucesToIndexPath =
				AbstractIndexManager.this.getWorkingLocation().append(
						PRESERVED_RESOURCE_EVENTS_TO_PROCESS_FILE_NAME);
			return new File(preservedResroucesToIndexPath.toOSString());
		}
		
		/**
		 * <p>If all resource events have been processed 
		 */
		private void notifyIfConsistant() {
			if(!this.hasResourceEventsToProcess()) {
				synchronized (this.fToNotifyLock) {
					this.fToNotifyLock.notifyAll();
				}
			}
		}
	}
	
	/**
	 * <p>Represents a resource that was discovered by this manager.  Contains
	 * all the information this manager and the index needs to know about this
	 * resource.  Such has how the manager was notified about this resource and
	 * the type of action occurring on the resource.</p>
	 */
	private static class ResourceEvent {
		/**
		 * <p>The source of this resource event</p>
		 * 
		 * @see AbstractIndexManager#SOURCE_RESOURCE_CHANGE
		 * @see AbstractIndexManager#SOURCE_SAVED_STATE
		 * @see AbstractIndexManager#SOURCE_WORKSPACE_SCAN
		 * @see AbstractIndexManager#SOURCE_PRESERVED_RESOURCES_TO_INDEX
		 */
		protected byte fSource;
		
		/**
		 * <p>The action that the index should take with this resource</p>
		 *
		 * @see AbstractIndexManager#ACTION_ADD
		 * @see AbstractIndexManager#ACTION_REMOVE
		 * @see AbstractIndexManager#ACTION_ADD_MOVE_FROM
		 * @see AbstractIndexManager#ACTION_REMOVE_MOVE_TO
		 */
		protected byte fAction;
		
		/**
		 * 
		 * <p>If the {@link #fAction} is {@link AbstractIndexManager#ACTION_ADD_MOVE_FROM} or
		 * {@link AbstractIndexManager#ACTION_REMOVE_MOVE_TO} then this field will have the
		 * path the resource was moved from or moved to. Else this field will be <code>null</code></p>
		 * 
		 * <p><b>NOTE: </b>Maybe <code>null</code>.</p>
		 * 
		 * @see AbstractIndexManager#ACTION_ADD_MOVE_FROM
		 * @see AbstractIndexManager#ACTION_REMOVE_MOVE_TO
		 */
		protected IPath fMovePath;
		
		/**
		 * <p>Creates a resource event that the index needs to react to in some way</p>
		 * 
		 * @param source source that the manager used to learn of this resource
		 * @param action action the index should take on this resource
		 * @param resource resource that the index should know about
		 * @param movePath if action is {@link AbstractIndexManager#ACTION_ADD_MOVE_FROM} or
		 * {@link AbstractIndexManager#ACTION_REMOVE_MOVE_TO} then this should be the path the
		 * resource was moved from or moved to respectively, else should be <code>null</code>
		 * 
		 * @see AbstractIndexManager#SOURCE_RESOURCE_CHANGE
		 * @see AbstractIndexManager#SOURCE_SAVED_STATE
		 * @see AbstractIndexManager#SOURCE_WORKSPACE_SCAN
		 * @see AbstractIndexManager#SOURCE_PRESERVED_RESOURCES_TO_INDEX
		 * 
		 * @see AbstractIndexManager#ACTION_ADD
		 * @see AbstractIndexManager#ACTION_REMOVE
		 * @see AbstractIndexManager#ACTION_ADD_MOVE_FROM
		 * @see AbstractIndexManager#ACTION_REMOVE_MOVE_TO
		 */
		protected ResourceEvent(byte source, byte action, IPath movePath) {
			this.fSource = source;
			this.fAction = action;
			this.fMovePath = movePath;
		}
	}
}