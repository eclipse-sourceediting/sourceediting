/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.events.AboutToBeChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;


/**
 * Purely for use in debugging
 */
public class EveryNodeDebugAdapter implements IDebugAdapter {

	static class InternalDocumentListener implements IDocumentListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentAboutToBeChanged(DocumentEvent event) {
			Debug.println("IdocumentAboutToBeChanged: " + event); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentChanged(DocumentEvent event) {
			Debug.println("IdocumentChanged: " + event); //$NON-NLS-1$

		}

	}

	static class InternalModelStateListener implements IModelStateListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.IModelStateListener#modelAboutToBeChanged(org.eclipse.wst.sse.core.IStructuredModel)
		 */
		public void modelAboutToBeChanged(IStructuredModel model) {
			Debug.println("modelAboutToBeChanged: " + model); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.IModelStateListener#modelChanged(org.eclipse.wst.sse.core.IStructuredModel)
		 */
		public void modelChanged(IStructuredModel model) {
			Debug.println("modelChanged: " + model); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.IModelStateListener#modelDirtyStateChanged(org.eclipse.wst.sse.core.IStructuredModel,
		 *      boolean)
		 */
		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			Debug.println("modelDirtyStateChanged: " + model); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.IModelStateListener#modelResourceDeleted(org.eclipse.wst.sse.core.IStructuredModel)
		 */
		public void modelResourceDeleted(IStructuredModel model) {
			Debug.println("modelResourceDeleted: " + model); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.IModelStateListener#modelResourceMoved(org.eclipse.wst.sse.core.IStructuredModel,
		 *      org.eclipse.wst.sse.core.IStructuredModel)
		 */
		public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
			Debug.println("modelResourceMoved: " + "oldModel: " + oldModel + "newModel: " + newModel); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
			Debug.println("modelAboutToBeReinitialized: " + "structuredModel: " + structuredModel); //$NON-NLS-1$ //$NON-NLS-2$

		}

		public void modelReinitialized(IStructuredModel structuredModel) {
			Debug.println("modelReinitialized: " + "structuredModel: " + structuredModel); //$NON-NLS-1$ //$NON-NLS-2$

		}

	}

	static class InternalStructuredDocumentAboutToChange implements IModelAboutToBeChangedListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IModelAboutToBeChangedListener#modelAboutToBeChanged(org.eclipse.wst.sse.core.events.AboutToBeChangedEvent)
		 */
		public void modelAboutToBeChanged(AboutToBeChangedEvent structuredDocumentEvent) {
			Debug.println("structuredDocumentAboutToBeChanged: " + structuredDocumentEvent); //$NON-NLS-1$

		}

	}

	static class InternalStructuredDocumentListener implements IStructuredDocumentListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#newModel(org.eclipse.wst.sse.core.events.NewDocumentContentEvent)
		 */
		public void newModel(NewDocumentEvent structuredDocumentEvent) {
			Debug.println("structuredDocumentChanged - newModel: " + structuredDocumentEvent); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#noChange(org.eclipse.wst.sse.core.events.NoChangeEvent)
		 */
		public void noChange(NoChangeEvent structuredDocumentEvent) {
			Debug.println("structuredDocumentChanged - noChange: " + structuredDocumentEvent); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#nodesReplaced(org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent)
		 */
		public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
			Debug.println("structuredDocumentChanged - nodesReplaced: " + structuredDocumentEvent); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#regionChanged(org.eclipse.wst.sse.core.events.RegionChangedEvent)
		 */
		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
			Debug.println("structuredDocumentChanged - regionChanged: " + structuredDocumentEvent); //$NON-NLS-1$

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.core.events.IStructuredDocumentListener#regionsReplaced(org.eclipse.wst.sse.core.events.RegionsReplacedEvent)
		 */
		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
			Debug.println("structuredDocumentChanged - regionsReplaced: " + structuredDocumentEvent); //$NON-NLS-1$

		}

	}

	private static EveryNodeDebugAdapter singletonInstance;

	public static EveryNodeDebugAdapter getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new EveryNodeDebugAdapter();
		}
		return singletonInstance;
	}

	InternalDocumentListener fInternalDocumentListener;
	InternalModelStateListener fInternalModelStateListener;
	InternalStructuredDocumentAboutToChange fInternalStructuredDocumentAboutToChange;
	InternalStructuredDocumentListener fInternalStructuredDocumentListener;
	IStructuredModel fModel;

	/**
	 * 
	 */
	public EveryNodeDebugAdapter() {
		super();
		fInternalDocumentListener = new InternalDocumentListener();
		fInternalStructuredDocumentAboutToChange = new InternalStructuredDocumentAboutToChange();
		fInternalStructuredDocumentListener = new InternalStructuredDocumentListener();
		fInternalModelStateListener = new InternalModelStateListener();
	}

	/**
	 * @param target
	 */
	public EveryNodeDebugAdapter(INodeNotifier target) {
		this();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.INodeAdapter#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return (type == IDebugAdapter.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.INodeAdapter#notifyChanged(org.eclipse.wst.sse.core.INodeNotifier,
	 *      int, java.lang.Object, java.lang.Object, java.lang.Object, int)
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		if (notifier instanceof IDOMNode) {
			setModel(((IDOMNode) notifier).getModel());
		}
		Debug.println("notifier: " + notifier + " " + INodeNotifier.EVENT_TYPE_STRINGS[eventType] + " changedFeature: " + changedFeature + " oldValue: " + oldValue + " newValue: " + newValue + " pos: " + pos); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.IDebugAdapter#setDocument(org.eclipse.wst.sse.core.text.IStructuredDocument)
	 */
	private void setModel(IStructuredModel structuredModel) {
		if (fModel == structuredModel)
			return;

		if (fModel != null) {
			fModel.removeModelStateListener(fInternalModelStateListener);
			//
			IStructuredDocument structuredDocument = fModel.getStructuredDocument();
			if (structuredDocument != null) {
				structuredDocument.removeDocumentListener(fInternalDocumentListener);
				structuredDocument.removeDocumentAboutToChangeListener(fInternalStructuredDocumentAboutToChange);
				structuredDocument.removeDocumentChangedListener(fInternalStructuredDocumentListener);
			}
		}
		fModel = structuredModel;
		if (fModel != null) {

			fModel.addModelStateListener(fInternalModelStateListener);
			//
			IStructuredDocument structuredDocument = fModel.getStructuredDocument();
			if (structuredDocument != null) {
				structuredDocument.addDocumentListener(fInternalDocumentListener);
				structuredDocument.addDocumentAboutToChangeListener(fInternalStructuredDocumentAboutToChange);
				structuredDocument.addDocumentChangedListener(fInternalStructuredDocumentListener);
			}
		}


	}
}
