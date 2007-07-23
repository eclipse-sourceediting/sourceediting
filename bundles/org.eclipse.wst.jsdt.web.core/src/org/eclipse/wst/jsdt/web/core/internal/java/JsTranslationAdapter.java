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
package org.eclipse.wst.jsdt.web.core.internal.java;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * An adapter for getting a JSPTranslation of the document.
 * 
 * @author pavery
 */
public class JsTranslationAdapter implements INodeAdapter, IResourceChangeListener {
	// for debugging
	private static final boolean DEBUG = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsptranslation")); //$NON-NLS-1$  //$NON-NLS-2$
	// private boolean fDocumentIsDirty = true;
	private IDocument fJspDocument = null;
	private JsTranslation fJSPTranslation = null;
	private NullProgressMonitor fTranslationMonitor = null;
	// private JsTranslator fTranslator = null;
	private IDOMModel fXMLModel;
	
	public JsTranslationAdapter(IDOMModel xmlModel, boolean listenForProjectChanges) {
		fXMLModel = xmlModel;
		fJspDocument = fXMLModel.getStructuredDocument();
		initializeJavaPlugins();
		
		if(listenForProjectChanges) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		}
	}
	

	public IJavaProject getJavaProject() {
		IJavaProject javaProject = null;
		try {
			String baseLocation = fXMLModel.getBaseLocation();
			// 20041129 (pa) the base location changed for XML model
			// because of FileBuffers, so this code had to be updated
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=79686
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baseLocation);
			IProject project = null;
			if (filePath.segmentCount() > 0) {
				project = root.getProject(filePath.segment(0));
			}
			if (project != null) {
				javaProject = JavaCore.create(project);
			}
		} catch (Exception ex) {
			if (fXMLModel != null) {
				Logger.logException("(JSPTranslationAdapter) problem getting java project from the XMLModel's baseLocation > " + fXMLModel.getBaseLocation(), ex); //$NON-NLS-1$
			} else {
				Logger.logException("(JSPTranslationAdapter) problem getting java project", ex); //$NON-NLS-1$
			}
		}
		return javaProject;
	}
	
	/**
	 * Returns the JSPTranslation for this adapter.
	 * 
	 * @return a JSPTranslationExtension
	 */
	public JsTranslation getJSPTranslation() {
		synchronized (fXMLModel) {
			if (fJSPTranslation == null) {
				
				fJSPTranslation = new JsTranslation(fXMLModel.getStructuredDocument(), getJavaProject());
			}
		}
		
		return fJSPTranslation;
	}
	
	
	private void initializeJavaPlugins() {
		JavaCore.getPlugin();
	}
	
	public boolean isAdapterForType(Object type) {
		return type.equals(IJsTranslation.class);
	}
	
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {}
	
	public void release() {
		if (fTranslationMonitor != null) {
			fTranslationMonitor.setCanceled(true);
		}
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		if (fJSPTranslation != null) {
			if (JsTranslationAdapter.DEBUG) {
				System.out.println("JSPTranslationAdapter releasing:" + fJSPTranslation); //$NON-NLS-1$
			}
			fJSPTranslation.release();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IProject changedProject = (event==null || event.getResource()==null)?null:event.getResource().getProject();
		if(changedProject!=null && getJavaProject().getProject().equals(changedProject) && fJSPTranslation!=null){
			fJSPTranslation.classpathChange();
		}	
	}
}