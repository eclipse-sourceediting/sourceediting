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

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentPropertiesManager;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslatorPersister;
import org.eclipse.jst.jsp.core.internal.java.search.JSPIndexManager;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelperManager;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class JSPCorePlugin extends Plugin {
	// The shared instance.
	private static JSPCorePlugin plugin;
	
	/** Save participant for this plugin */
	private ISaveParticipant fSaveParticipant;

	/**
	 * The constructor.
	 */
	public JSPCorePlugin() {
		super();
		plugin = this;
		fSaveParticipant = new SaveParticipant();
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

		//restore save state and process any events that happened before plugin loaded
		if(JSPTranslatorPersister.ACTIVATED) {
			try {
				ISavedState savedState = ResourcesPlugin.getWorkspace().addSaveParticipant(
						plugin.getBundle().getSymbolicName(), this.fSaveParticipant);
				if (savedState != null) {
					savedState.processResourceChangeEvents(JSPTranslatorPersister.getDefault());
				}
			} catch(CoreException e) {
				Logger.logException("Could not load previous save state", e);
			}
		}
		
		//set up persister to listen to resource change events
		ResourcesPlugin.getWorkspace().addResourceChangeListener(JSPTranslatorPersister.getDefault());
		
		//init the JSP index
		JSPIndexManager.getInstance().initialize();

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

		/*
		 * stop listening for resource changes to update content properties
		 * keys
		 */
		JSPFContentPropertiesManager.shutdown();

		//remove the plugin save participant
		ResourcesPlugin.getWorkspace().removeSaveParticipant(plugin.getBundle().getSymbolicName());
		
		//remove the translator persister
		if(JSPTranslatorPersister.ACTIVATED) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(JSPTranslatorPersister.getDefault());
		}
		
		// stop any indexing
		JSPIndexManager.getInstance().shutdown();

		// stop listening for classpath changes
		JavaCore.removeElementChangedListener(TaglibHelperManager.getInstance());

		// stop taglib controller
		TaglibController.shutdown();
		TaglibIndex.shutdown();

		super.stop(context);
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
