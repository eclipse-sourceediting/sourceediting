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
import java.util.Hashtable;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;
import org.eclipse.wst.sse.core.internal.provisional.IModelLifecycleListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * Copyright IBM 2007. All rights reserved. This class maintains resource change
 * events for web document moedels. Author Bradley Childs. (childsb@us.ibm.com)
 */
public class WebResourceChangeHandler implements IResourceChangeListener, IDocumentListener, IModelLifecycleListener {
	/*
	 * Check the level of dirty regions when signaling for document events.
	 * 
	 * sometimes the SSE editor doesn't re-validate regions when text is
	 * inserted, so setting this to false causes every document change event to
	 * trigger a revalidation.
	 * 
	 * setting to true may speed things up.
	 * 
	 */
	private static final boolean CHECK_INTREST_LEVEL = false;
	private static Hashtable instances = new Hashtable();
	private static final boolean SIGNAL_MODEL = false;
	
	public static WebResourceChangeHandler getInstance(IStructuredModel xmlModel, IWebResourceChangedListener changeListener) {
		WebResourceChangeHandler handler = null;
		synchronized (WebResourceChangeHandler.instances) {
			Enumeration values = WebResourceChangeHandler.instances.elements();
			while (values.hasMoreElements()) {
				Object model = values.nextElement();
				if (model == xmlModel) {
					handler = (WebResourceChangeHandler) WebResourceChangeHandler.instances.get(model);
				}
			}
			if (handler == null) {
				handler = new WebResourceChangeHandler();
				WebResourceChangeHandler.instances.put(handler, xmlModel);
			}
		}
		handler.initialize();
		handler.fchangeListener.add(changeListener);
		return handler;
	}
	private class ModelIrritantThread implements Runnable {
		public void run() {
			signalAllDirtyModel();
		}
		
		public void signalAllDirtyModel() {
			for (int i = 0; i < fchangeListener.size(); i++) {
				((IWebResourceChangedListener) fchangeListener.get(i)).resourceChanged();
			}
			if (!WebResourceChangeHandler.SIGNAL_MODEL) {
				return;
			}
			IDOMModel xmlModel = null;
			Object modelRef = getModel();
			if (modelRef == null) {
				return;
			}
			try {
// for(int i =0;i<fchangeListener.size();i++) {
// ((IWebResourceChangedListener)fchangeListener.get(i)).resourceChanged();
// }
				xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForEdit(((IStructuredModel) modelRef).getBaseLocation());
				if (xmlModel != null) {
					IStructuredDocument doc = xmlModel.getStructuredDocument();
					xmlModel.aboutToChangeModel();
					// xmlModel.setReinitializeNeeded(true);
					// (doc).replace(0, doc.getLength(),doc.get());
					xmlModel.changedModel();
				}
			} catch (Exception e) {
				System.out.println(Messages.getString("WebResourceChangeHandler.0")); //$NON-NLS-1$
			} finally {
				if (xmlModel != null) {
					xmlModel.releaseFromEdit();
				}
			}
		}
	}
	private ArrayList fchangeListener = new ArrayList();
	private ModelIrritantThread irritator;
	
	private WebResourceChangeHandler() {}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	public void documentChanged(DocumentEvent event) {
//		if (WebResourceChangeHandler.CHECK_INTREST_LEVEL) {
//			for (int i = 0; i < fchangeListener.size(); i++) {
//				Object o = fchangeListener.get(i);
//				if (o instanceof IWebDocumentChangeListener) {
//					int intrest = ((IWebDocumentChangeListener) o).getIntrestLevelAtOffset(event.fOffset);
//					switch (intrest) {
//						case IWebDocumentChangeListener.DIRTY_MODEL:
//							irritator.signalAllDirtyModel();
//							return;
//						case IWebDocumentChangeListener.DIRTY_DOC:
//							((IWebDocumentChangeListener) o).resourceChanged();
//						break;
//					}
//					return;
//				}
//			}
//		} else {
			irritator.signalAllDirtyModel();
		//}
	}
	
	
	public boolean equals(Object o) {
		return (o instanceof WebResourceChangeHandler && ((WebResourceChangeHandler) o).fchangeListener == this.fchangeListener);
	}
	
	
	public void finalize() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		if (WebResourceChangeHandler.instances == null) {
			return;
		}
		Object o = null;
		synchronized (WebResourceChangeHandler.instances) {
			o = WebResourceChangeHandler.instances.get(this);
			WebResourceChangeHandler.instances.remove(this);
		}
		if (o != null) {
			IStructuredModel fXMLModel = (IStructuredModel) o;
			IStructuredDocument fJspDocument = fXMLModel.getStructuredDocument();
			if (fJspDocument != null) {
				fJspDocument.removeDocumentListener(this);
			}
		}
	}
	
	private IStructuredModel getModel() {
		if (WebResourceChangeHandler.instances == null) {
			return null;
		}
		return (IStructuredModel) WebResourceChangeHandler.instances.get(this);
	}
	
	private void initialize() {
		IStructuredModel xmlModel = getModel();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		xmlModel.addModelLifecycleListener(this);
		IStructuredDocument fJspDocument = xmlModel.getStructuredDocument();
		if (fJspDocument != null) {
			fJspDocument.addDocumentListener(this);
		}
		irritator = new ModelIrritantThread();
	}
	
	public void processPostModelEvent(ModelLifecycleEvent event) {
		if (irritator != null) {
			irritator.signalAllDirtyModel();
		}
		if (WebResourceChangeHandler.instances == null) {
			return;
		}
		if (event.getType() == ModelLifecycleEvent.MODEL_RELEASED) {
			synchronized (WebResourceChangeHandler.instances) {
				WebResourceChangeHandler.instances.remove(this);
			}
		}
	}
	
	public void processPreModelEvent(ModelLifecycleEvent event) {}
	
	public void resourceChanged(IResourceChangeEvent event) {
		Display.getDefault().asyncExec(irritator);
	}
}
