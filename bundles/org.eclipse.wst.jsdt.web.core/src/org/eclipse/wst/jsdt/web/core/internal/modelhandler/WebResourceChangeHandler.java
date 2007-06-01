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
package org.eclipse.wst.jsdt.web.core.internal.modelhandler;

import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

/**
 	* Copyright IBM 2007.  All rights reserved.
	* This class maintains resource change events for web document moedels.
	* Author Bradley Childs. (childsb@us.ibm.com)
 */
public class WebResourceChangeHandler implements IResourceChangeListener, IDocumentListener, IModelLifecycleListener{
	
	
	private static Hashtable instances = new Hashtable();
	
	
	private ArrayList fchangeListener = new ArrayList();
	private ModelIrritantThread irritator;
	/* Check the level of dirty regions when signaling for document events.
	 * 
	 * sometimes the SSE editor doesn't re-validate regions when text is inserted, so setting this to false
	 * causes every document change event to trigger a revalidation.
	 * 
	 * setting to true may speed things up.
	 * 
	 */
	private static final boolean CHECK_INTREST_LEVEL=false;
	
	
	private WebResourceChangeHandler() {}
	
	private void initialize() {
		IStructuredModel xmlModel = getModel();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,	IResourceChangeEvent.POST_CHANGE);
		xmlModel.addModelLifecycleListener(this);
		IStructuredDocument fJspDocument = xmlModel.getStructuredDocument();
		if (fJspDocument != null) {
			fJspDocument.addDocumentListener(this);
		}
		irritator = new ModelIrritantThread();
	}
	
	private IStructuredModel getModel() {
		
			if(instances==null) return null;
			return (IStructuredModel)instances.get(this);
		
	}


	public static WebResourceChangeHandler getInstance(IStructuredModel xmlModel, IWebResourceChangedListener changeListener) {
		WebResourceChangeHandler handler = null;
	
		synchronized(instances) {
			Enumeration values = instances.elements();
			while(values.hasMoreElements()) {
				Object model = values.nextElement();
				if(model==xmlModel) {
					handler = (WebResourceChangeHandler)instances.get(model);
					
				}
			}
			if(handler==null) {
				handler = new WebResourceChangeHandler(); 
				instances.put(handler, xmlModel);
			}
		}
		
		handler.initialize();
		handler.fchangeListener.add(changeListener);
		
		
		return handler;	
	}
	
	public void resourceChanged(IResourceChangeEvent event) {	
		Display.getDefault().asyncExec(irritator);
	}
	
	public boolean equals(Object o) {
			return (o instanceof WebResourceChangeHandler && ((WebResourceChangeHandler)o).fchangeListener == this.fchangeListener) ;
	}

	private class ModelIrritantThread implements Runnable{
		
		public void run() {
			signalAllDirtyModel();
		}
		
		public void signalAllDirtyModel() {
			
				IDOMModel xmlModel=null;
				Object modelRef = getModel();
				
				if(modelRef==null) {
					return;
				}
				
				try {
					for(int i =0;i<fchangeListener.size();i++) {
						((IWebResourceChangedListener)fchangeListener.get(i)).resourceChanged();
					}
					
					xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForEdit(((IStructuredModel)modelRef).getBaseLocation());
					if(xmlModel!=null){
						
						IStructuredDocument doc = xmlModel.getStructuredDocument();
						
						xmlModel.aboutToChangeModel();
						//xmlModel.setReinitializeNeeded(true);
						//(doc).replace(0, doc.getLength(),doc.get());
						xmlModel.changedModel();
						
						
					}
				}catch(Exception e) {
					System.out.println("Error when trying to agitate the model..");
				}finally {
					if(xmlModel!=null)
						xmlModel.releaseFromEdit();
				}
			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentChanged(DocumentEvent event) {	
		if(CHECK_INTREST_LEVEL) {
			for(int i =0;i<fchangeListener.size();i++) {
				Object o = fchangeListener.get(i);
				if(o instanceof IWebDocumentChangeListener ) {
					int intrest = ((IWebDocumentChangeListener)o).getIntrestLevelAtOffset(event.fOffset);
					switch(intrest) {
						case IWebDocumentChangeListener.DIRTY_MODEL:
							irritator.signalAllDirtyModel();
							return;
						case IWebDocumentChangeListener.DIRTY_DOC:
							((IWebDocumentChangeListener)o).resourceChanged();
						break;
					}
					return;
				}
			}
		}else {
			irritator.signalAllDirtyModel();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {}

	public void finalize() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		if(instances==null) return;
		Object o=null;
		
		synchronized(instances) {
			o = instances.get(this);
			instances.remove(this);
		}
		
		if(o!=null) {
				IStructuredModel fXMLModel = (IStructuredModel)o;
				IStructuredDocument fJspDocument = fXMLModel.getStructuredDocument();	
				if (fJspDocument != null) {
					fJspDocument.removeDocumentListener(this);
				}
		}		
	}

	public void processPostModelEvent(ModelLifecycleEvent event) {
		if(instances==null) return;
		if(event.getType() == ModelLifecycleEvent.MODEL_RELEASED) {
			synchronized(instances) {	
				instances.remove(this);
			}
		}
	}
	
	public void processPreModelEvent(ModelLifecycleEvent event) {}	
}

