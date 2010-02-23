/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (Intalio) - bug 300434 - Make inner classes static where possible
 *******************************************************************************/
package org.eclipse.wst.sse.internal.contentproperties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.Logger;

/**
 * @deprecated See
 *             org.eclipse.html.core.internal.contentproperties.HTMLContentProperties
 */
public class ContentSettingsSynchronizer implements IResourceChangeListener {
	static class ContentSettingsVisitor implements IResourceDeltaVisitor {
		// redefinition in ContentSettings.java
		private String contentSettingsName = ContentSettings.getContentSettingsName(); //$NON-NLS-1$
		private IResourceChangeEvent fEvent;
		private IContentSettingsHandler handler;

		public ContentSettingsVisitor(IResourceChangeEvent event) {
			this.fEvent = event;
		}

		/**
		 * @see IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) {
			if (delta == null)
				return false;
			IResource resource = delta.getResource();
			if (resource == null)
				return false;
			// parent folder or project
			if (delta.getKind() == IResourceDelta.CHANGED && delta.getFlags() == 0)
				return true;
			final int resourceType = resource.getType();
			switch (resourceType) {
				case IResource.PROJECT :
					return false;
				case IResource.FILE :
					// whether resource is .contentsettings file or not
					IProject project = delta.getResource().getProject();
					if (this.fEvent.getType() == IResourceChangeEvent.POST_CHANGE && resource.equals(project.getFile(this.contentSettingsName))) {
						// new object for .contentsettings
						handler = new ContentSettingsSelfHandler();
					}
					else if (this.fEvent.getType() == IResourceChangeEvent.PRE_BUILD && resource.getFileExtension() != null) {
						// TODO change to content type!
						if (resource.getFileExtension().equalsIgnoreCase("shtml") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("htm") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("html") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("jhtml") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("xhtml") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("jsp") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("css") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("jsf") //$NON-NLS-1$
									|| resource.getFileExtension().equalsIgnoreCase("jspf")) { //$NON-NLS-1$
							// new object for IFile
							handler = new ContentSettingsFileHandler();
							handler.handle(delta);
							handler = null;
							return true;
						}
					}
					else
						return false; // true or false;motomoto true;
					break;
				case IResource.FOLDER :
					return true;
				default :
					return true;
			}
//			final IResourceDelta fDelta = delta;
//			final IContentSettingsHandler deltaHandler = this.handler;
			// XXX FIXME
			// I commented out following to move this to "model" 
			// packages. Since wasnt't hooked up/working anyway, I think 
			// ok for now, but need to re-work as Job, once we do implement 
			// settings for resources again. 
//			Display display = getDisplay();
//			if (display != null && !display.isDisposed()) {
//				display.asyncExec(new Runnable() {
//					public void run() {
//						if (deltaHandler != null) {
//							deltaHandler.handle(fDelta);
//						}
//					}
//				});
//			}
//			else if (deltaHandler != null) {
//				deltaHandler.handle(fDelta);
//			}
			handler = null;
			return true; // true or false;motomoto true;
		}
	}

	private static ContentSettingsSynchronizer instance = null;

	public synchronized static ContentSettingsSynchronizer getInstance() {
		if (instance == null)
			instance = new ContentSettingsSynchronizer();
		return instance;
	}

	private ContentSettingsVisitor csVisitor;
	private boolean fListening = false;
	private IProject fProject;


	private final IProject getProject() {
		return fProject;
	}

	private final IWorkspace getWorkspace() {
		if (getProject() == null)
			return ResourcesPlugin.getWorkspace();
		return getProject().getWorkspace();
	}

	public synchronized void install() {
		if (!fListening) {
			getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.PRE_BUILD | IResourceChangeEvent.POST_CHANGE);
			fListening = true;
		}
	}

	/**
	 * @return
	 */
	public synchronized boolean isListening() {
		return fListening;
	}

	/*
	 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			try {
				if (csVisitor == null)
					csVisitor = new ContentSettingsVisitor(event);
				delta.accept(csVisitor);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
		csVisitor = null;
	}

	public synchronized void unInstall() {
		getWorkspace().removeResourceChangeListener(this);
		fListening = false;
	}
}
