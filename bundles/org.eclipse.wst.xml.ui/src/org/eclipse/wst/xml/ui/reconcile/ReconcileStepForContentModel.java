/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.reconcile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.PropagatingAdapter;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.StructuredTextReconciler;
import org.eclipse.wst.sse.ui.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileStepAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.ReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.internal.Logger;


/**
 * A reconcile step for ContentModel based documents.
 * @deprecated using reconcileValidator extension point
 */
public class ReconcileStepForContentModel extends StructuredReconcileStep {
	private HashSet fLocalPartitionTypes = null;

	protected boolean fRanInitialValidate = false;

	public ReconcileStepForContentModel() {
		super();
		fLocalPartitionTypes = new HashSet();
	}

	public ReconcileStepForContentModel(StructuredTextViewer viewer, IReconcileStep step) {
		super(step);
		fLocalPartitionTypes = new HashSet();
	}

	private void addPartitionTypes(String[] types) {
		for (int i = 0; i < types.length; i++)
			fLocalPartitionTypes.add(types[i]);
	}

	/**
	 * Need to add partition types for ReconcileStepAdapterForXML here...
	 * 
	 */
	public String[] getPartitionTypes() {
		String[] superPartitionTypes = super.getPartitionTypes();
		String[] results = new String[superPartitionTypes.length + fLocalPartitionTypes.size()];
		System.arraycopy(superPartitionTypes, 0, results, 0, superPartitionTypes.length);
		System.arraycopy(fLocalPartitionTypes.toArray(), 0, results, superPartitionTypes.length, fLocalPartitionTypes.size());
		return results;
	}

	public int getScope() {
		return ReconcileAnnotationKey.PARTIAL;
	}

	public void initialValidate() {

		// (pa) perf: add the adapter for every node here
		XMLModel xModel = (XMLModel) StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
		XMLDocument doc = xModel.getDocument();
		xModel.releaseFromRead();
		PropagatingAdapter propagatingAdapter = (PropagatingAdapter) doc.getAdapterFor(PropagatingAdapter.class);

		List factories = propagatingAdapter.getAdaptOnCreateFactories();
		ReconcilerAdapterFactoryForXML rAdapterFactoryForXML = null;
		AdapterFactory temp = null;
		// find the ReconcileStepAdapterFactory
		for (int i = 0; i < factories.size(); i++) {
			temp = (AdapterFactory) factories.get(i);
			if (temp.isFactoryForType(IReconcileStepAdapter.class)) {
				rAdapterFactoryForXML = (ReconcilerAdapterFactoryForXML) temp;
				break;
			}
		}

		if (rAdapterFactoryForXML != null) {
			rAdapterFactoryForXML.setShouldMarkForReconciling(false);
			initialValidateTree(doc, rAdapterFactoryForXML);
			rAdapterFactoryForXML.setShouldMarkForReconciling(true);
		}
	}

	/**
	 * Mark the INodeNotifier (Node) and all children of the INodeNotifier
	 * passed in.
	 * 
	 * @param notifier
	 */
	protected void initialValidateTree(INodeNotifier notifier, AdapterFactory rAdapterFactoryForXML) {
		if (isCanceled())
			return;

		if (notifier != null && notifier instanceof XMLNode) {
			XMLNode current = (XMLNode) notifier;
			IReconcileStepAdapter adapter = null;
			// loop siblings
			// pa_TODO for large XML files this loop goes for a LONG time
			// and the progress monitor never gets canceled
			while (current != null && !isCanceled()) {
				// adapt this notifier
				adapter = (IReconcileStepAdapter) rAdapterFactoryForXML.adapt(current);
				if (adapter != null) {
//					((AbstractReconcileStepAdapter) adapter).setParentStep(this);
//					adapter.markForReconciling(current);
//					current.addAdapter(adapter);
//					adapter.reconcile(getProgressMonitor(), current);
				}
				if (current.getFirstChild() != null) {
					initialValidateTree((XMLNode) current.getFirstChild(), rAdapterFactoryForXML);
				}
				current = (XMLNode) current.getNextSibling();
			}
		}
	}


	// Determines whether the IStructuredDocumentRegion is a XML "end tag"
	// since they're not allowed to have
	// attribute ITextRegions
	protected boolean isEndTag(IStructuredDocumentRegion structuredDocumentRegion) {
		return structuredDocumentRegion.getFirstRegion().getType() == XMLRegionContext.XML_END_TAG_OPEN;
	}

	// Determines whether the IStructuredDocumentRegion is a XML "start tag"
	// since they need to be
	// checked for proper XML attribute region sequences
	protected boolean isStartTag(IStructuredDocumentRegion structuredDocumentRegion) {
		return structuredDocumentRegion.getFirstRegion().getType() == XMLRegionContext.XML_TAG_OPEN;
	}

	// Because we check the "proper" closing separately from attribute
	// sequencing, we need to know what's
	// an appropriate close.
	protected boolean isTagCloseTextRegion(ITextRegion textRegion) {
		return textRegion.getType() == XMLRegionContext.XML_TAG_CLOSE || textRegion.getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.ui.text.StructuredReconcileStep#reconcileModel(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.IRegion)
	 */
	protected IReconcileResult[] reconcileModel(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (dirtyRegion == null)
			return EMPTY_RECONCILE_RESULT_SET;

		// logging ------------------
		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > reconciling model in CONTENT MODEL step w/ dirty region: [" + dirtyRegion.getOffset() + ":" + dirtyRegion.getLength() + "]" + dirtyRegion.getText()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// --------------------------

		int start = dirtyRegion.getOffset();
		int length = dirtyRegion.getLength();

		IReconcileResult[] results = validate(start, length);

		// logging ------------------
		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > CONTENT MODEL step done"); //$NON-NLS-1$
		// --------------------------
		return results;
	}

	/**
	 * Forces the IReconcilerAdapters for XMLNodes overlapping the given
	 * region to "validate" their Nodes.
	 * 
	 * @param startOffset
	 * @param length
	 */
	protected IReconcileResult[] validate(int startOffset, int length) {
		List results = new ArrayList();
		IReconcileResult[] temp = EMPTY_RECONCILE_RESULT_SET;

		if (!fRanInitialValidate) {
			initialValidate();
			fRanInitialValidate = true;
		} else {
			XMLModel model = (XMLModel) StructuredModelManager.getModelManager().getExistingModelForRead(getDocument());
			int endOffset = startOffset + length;

			IndexedRegion indexedNode = model.getIndexedRegion(startOffset);
			IReconcileStepAdapter adapter = null;

			// sometimes for single key type length can be 0 (startOffset ==
			// endOffset)
			for (int i = startOffset; indexedNode != null && i <= endOffset && !isCanceled(); i++) {

				XMLNode xmlNode = (XMLNode) indexedNode;
				adapter = (IReconcileStepAdapter) xmlNode.getAdapterFor(IReconcileStepAdapter.class);
				if (adapter != null) {
					temp = adapter.reconcile(getProgressMonitor(), xmlNode);
					for (int j = 0; j < temp.length; j++)
						results.add(temp[j]);
					// this is for removal purposes later
					addPartitionTypes(adapter.getPartitionTypes());
				}
				//	visited.add(indexedNode);
				if (xmlNode.getFirstStructuredDocumentRegion() != null)
					i += xmlNode.getFirstStructuredDocumentRegion().getLength();
				else
					i++;

				indexedNode = model.getIndexedRegion(i);
			}
			model.releaseFromRead();
		}
		return (IReconcileResult[]) results.toArray(new IReconcileResult[results.size()]);
	}
}
