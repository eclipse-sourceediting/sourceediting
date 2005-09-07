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
package org.eclipse.wst.dtd.ui.internal.provisional;

import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.dtd.core.internal.provisional.text.IDTDPartitionTypes;
import org.eclipse.wst.dtd.ui.internal.style.LineStyleProviderForDTD;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.ui.internal.provisional.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProviderForNoOp;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredRegionProcessor;


/**
 * A source viewer configuration for DTDs.
 * 
 * @plannedfor 1.0
 */
public class StructuredTextViewerConfigurationDTD extends StructuredTextViewerConfiguration {
	/*
	 * One instance per configuration because not sourceviewer-specific and
	 * it's a String array
	 */
	private String[] fConfiguredContentTypes;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForDTD;
	/*
	 * One instance per configuration
	 */
	private LineStyleProvider fLineStyleProviderForNoop;
	/*
	 * One instance per configuration
	 */
	private IReconciler fReconciler;

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			fConfiguredContentTypes = new String[]{IDTDPartitionTypes.DTD_DEFAULT, IStructuredPartitionTypes.DEFAULT_PARTITION, IStructuredPartitionTypes.UNKNOWN_PARTITION};
		}
		return fConfiguredContentTypes;
	}
	
	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IDTDPartitionTypes.DTD_DEFAULT || partitionType == IStructuredPartitionTypes.DEFAULT_PARTITION) {
			providers = new LineStyleProvider[]{getLineStyleProviderForCSS()};
		} else if (partitionType == IStructuredPartitionTypes.UNKNOWN_PARTITION) {
			providers = new LineStyleProvider[]{getLineStyleProviderForNoop()};
		}

		return providers;
	}

	private LineStyleProvider getLineStyleProviderForCSS() {
		if (fLineStyleProviderForDTD == null) {
			fLineStyleProviderForDTD = new LineStyleProviderForDTD();
		}
		return fLineStyleProviderForDTD;
	}
	
	private LineStyleProvider getLineStyleProviderForNoop() {
		if (fLineStyleProviderForNoop == null) {
			fLineStyleProviderForNoop = new LineStyleProviderForNoOp();
		}
		return fLineStyleProviderForNoop;
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		boolean reconcilingEnabled = fPreferenceStore.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);
		if (sourceViewer == null || !reconcilingEnabled)
			return null;

		/*
		 * Only create reconciler if sourceviewer is present
		 */
		if (fReconciler == null && sourceViewer != null) {
			StructuredRegionProcessor reconciler = new StructuredRegionProcessor();

			// reconciler configurations
			reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

			// IReconcilingStrategy markupStrategy = new
			// StructuredTextReconcilingStrategyForMarkup((ITextEditor)
			// editorPart);
			// fReconciler.setReconcilingStrategy(markupStrategy,
			// IXMLPartitions.XML_DEFAULT);
			// fReconciler.setDefaultStrategy(markupStrategy);

			fReconciler = reconciler;
		}
		return fReconciler;
	}
}
