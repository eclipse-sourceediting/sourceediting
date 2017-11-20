/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentPropertiesManager;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.java.search.JSPIndexManager;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelperManager;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class JSPCorePlugin extends Plugin {
	/** singleton instance of the plugin */
	private static JSPCorePlugin plugin;

	/**
	 * <p>Job used to finish tasks needed to start up the plugin but that did not have
	 * to block the plugin start up process.</p>
	 */
	private Job fPluginInitializerJob;
	
	/**
	 * The constructor.
	 */
	public JSPCorePlugin() {
		super();
		plugin = this;
		this.fPluginInitializerJob = new PluginInitializerJob();
	}

	/**
	 * Returns the shared instance.
	 */
	public static JSPCorePlugin getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		/*
		 * JSPIndexManager depends on TaglibController, so TaglibController
		 * should be started first
		 */
		TaglibIndex.startup();
		TaglibController.startup();

		// listen for classpath changes
		JavaCore.addElementChangedListener(TaglibHelperManager.getInstance());
		
		//schedule delayed initialization
		this.fPluginInitializerJob.schedule(2000);

		// listen for resource changes to update content properties keys
		JSPFContentPropertiesManager.startup();

		DeploymentDescriptorPropertyCache.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		DeploymentDescriptorPropertyCache.stop();

		// stop listening for resource changes to update content properties keys
		JSPFContentPropertiesManager.shutdown();

		//remove the plugin save participant
		ResourcesPlugin.getWorkspace().removeSaveParticipant(plugin.getBundle().getSymbolicName());
		
		// stop any indexing
		JSPIndexManager.getDefault().stop();

		// stop listening for classpath changes
		JavaCore.removeElementChangedListener(TaglibHelperManager.getInstance());

		// stop taglib controller
		TaglibController.shutdown();
		TaglibIndex.shutdown();

		super.stop(context);
	}
	
	/**
	 * <p>A {@link Job} used to perform delayed initialization for the plugin</p>
	 */
	private static class PluginInitializerJob extends Job {
		/**
		 * <p>Default constructor to set up this {@link Job} as a
		 * long running system {@link Job}</p>
		 */
		protected PluginInitializerJob() {
			super(JSPCoreMessages.JSPCorePlugin_Initializing_JSP_Tools);
			
			this.setUser(false);
			this.setSystem(true);
			this.setPriority(Job.LONG);
		}
		
		/**
		 * <p>Perform delayed initialization for the plugin</p>
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			IStatus status = Status.OK_STATUS;
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			try {
				/*
				 * Restore save state and process any events that happened before
				 * plug-in loaded. Don't do it immediately since adding the save
				 * participant requires a lock on the workspace to compute the
				 * accumulated deltas, and if the tree is not already locked it
				 * becomes a blocking call.
				 */
				workspace.run(new IWorkspaceRunnable() {
					public void run(final IProgressMonitor worspaceMonitor) throws CoreException {
						ISavedState savedState = null;
						
						try {
							//add the save participant for this bundle
							savedState = ResourcesPlugin.getWorkspace().addSaveParticipant(
									JSPCorePlugin.plugin.getBundle().getSymbolicName(), new SaveParticipant());
						} catch (CoreException e) {
							Logger.logException("JSP Core Plugin failed at loading previously saved state." + //$NON-NLS-1$
									" All componenets dependent on this state will start as if first workspace load.", e); //$NON-NLS-1$
						}
						
						//if there is a saved state start up using that, else start up cold
						if(savedState != null) {
							try {
								Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
							} finally {
								savedState.processResourceChangeEvents(new IResourceChangeListener() {
									/**
									 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
									 */
									public void resourceChanged(IResourceChangeEvent event) {
										JSPIndexManager.getDefault().start(event.getDelta(), worspaceMonitor);
									}
								});
							}
						} else {
							JSPIndexManager.getDefault().start(null, worspaceMonitor);
						}
					}
				}, monitor);
			} catch(CoreException e) {
				status = e.getStatus();
			}
			return status;
		}
		
	}
	
	/**
	 * Used so that all of the IResourceChangeEvents that occurred before
	 * this plugin loaded can be processed.
	 */
	private static class SaveParticipant implements ISaveParticipant {
		/**
		 * <p>Default constructor</p>
		 */
		protected SaveParticipant() {
		}
		
		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context) {
			//ignore
		}
	
		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException {
			//ignore
		}
	
		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context) {
			//ignore
		}
	
		/**
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException {
			context.needDelta();
		}
	}
}
