/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.spelling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingEngineDescriptor;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.ReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredTextReconcilingStrategy;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * A reconciling strategy that queries the SpellingService using its default
 * engine. Results are show as temporary annotations.
 * 
 * @since 1.5
 */
public class SpellcheckStrategy extends StructuredTextReconcilingStrategy {

	class SpellCheckPreferenceListener implements IPropertyChangeListener {
		private boolean isInterestingProperty(Object property) {
			return SpellingService.PREFERENCE_SPELLING_ENABLED.equals(property) || SpellingService.PREFERENCE_SPELLING_ENGINE.equals(property);
		}

		public void propertyChange(PropertyChangeEvent event) {
			if (isInterestingProperty(event.getProperty())) {
				if (event.getOldValue() == null || event.getNewValue() == null || !event.getNewValue().equals(event.getOldValue())) {
					reconcile();
				}
			}
		}
	}

	private class SpellingProblemCollector implements ISpellingProblemCollector {
		List annotations = new ArrayList();

		public void accept(SpellingProblem problem) {
			if (!isInterestingProblem(problem))
				return;

			TemporaryAnnotation annotation = new TemporaryAnnotation(new Position(problem.getOffset(), problem.getLength()), TemporaryAnnotation.ANNOT_WARNING, problem.getMessage(), fReconcileAnnotationKey);
			/*
			 * TODO: create and use an IQuickFixProcessor that uses
			 * problem.getProposals() for fixes [note, the default engine
			 * doesn't propose fixes, at least without a dictionary].
			 */
			annotation.setAdditionalFixInfo(problem);
			annotations.add(annotation);
			if (_DEBUG_SPELLING) {
				Logger.log(Logger.INFO_DEBUG, problem.getMessage());
			}
		}

		/**
		 * Judge whether a spelling problem is "interesting". Accept any
		 * regions that are explictly allowed, and since valid prose areas are
		 * rarely in a complicated document region, accept any document region
		 * with more than one text region and reject any document regions
		 * containing foreign text regions.
		 * 
		 * @param problem
		 *            a SpellingProblem
		 * @return whether the collector should accept the given
		 *         SpellingProblem
		 */
		private boolean isInterestingProblem(SpellingProblem problem) {
			if (getDocument() instanceof IStructuredDocument) {
				IStructuredDocumentRegion documentRegion = ((IStructuredDocument) getDocument()).getRegionAtCharacterOffset(problem.getOffset());
				if (documentRegion != null) {
					ITextRegion textRegion = documentRegion.getRegionAtCharacterOffset(problem.getOffset());
					if (textRegion != null && isSupportedContext(textRegion.getType())) {
						return true;
					}
					if (documentRegion.getFirstRegion() instanceof ForeignRegion)
						return false;
					if (documentRegion.getRegions().size() == 1)
						return true;
					return false;
				}
			}
			return true;
		}

		public void beginCollecting() {
		}

		void clear() {
			annotations.clear();
		}

		public void endCollecting() {
		}

		IReconcileResult[] getResults() {
			return (IReconcileResult[]) annotations.toArray(new IReconcileResult[annotations.size()]);
		}
	}

	private class SpellingStep extends StructuredReconcileStep {
		protected IReconcileResult[] reconcileModel(final DirtyRegion dirtyRegion, IRegion subRegion) {
			SpellingService service = getSpellingService(fContentTypeId + "." + SpellingService.PREFERENCE_SPELLING_ENGINE); //$NON-NLS-1$
			if (_DEBUG_SPELLING) {
				Logger.log(Logger.INFO_DEBUG, "Spell checking [" + subRegion.getOffset() + "-" + (subRegion.getOffset() + subRegion.getLength()) + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (getDocument() != null) {
				service.check(getDocument(), new IRegion[]{dirtyRegion}, fSpellingContext, fProblemCollector, getProgressMonitor());
			}
			IReconcileResult[] results = fProblemCollector.getResults();
			fProblemCollector.clear();
			return results;
		}
	}

	static final boolean _DEBUG_SPELLING = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.sse.ui/debug/reconcilerSpelling")).booleanValue(); //$NON-NLS-1$

	static final String ANNOTATION_TYPE = "org.eclipse.wst.sse.ui.temp.spelling"; //$NON-NLS-1$

	private static final String EXTENDED_BUILDER_TYPE_PARTITIONS = "spellingpartitions"; //$NON-NLS-1$
	private static final String EXTENDED_BUILDER_TYPE_CONTEXTS = "spellingregions"; //$NON-NLS-1$
	static final String KEY_CONTENT_TYPE = "org.eclipse.wst.sse.ui.temp.spelling"; //$NON-NLS-1$

	private String fContentTypeId = null;

	private String fDocumentPartitioning;

	SpellingProblemCollector fProblemCollector = new SpellingProblemCollector();

	/*
	 * Keying our Temporary Annotations based on the partition doesn't help
	 * this strategy to only remove its own TemporaryAnnotations since it's
	 * possibly run on all partitions. Instead, set the key to use an
	 * arbitrary partition type that we can check for using our own
	 * implementation of getAnnotationsToRemove(DirtyRegion).
	 * 
	 * Value initialized through
	 * 
	 * super(ISourceViewer)->super.init()->createReconcileSteps()
	 */
	ReconcileAnnotationKey fReconcileAnnotationKey;
	private IPropertyChangeListener fSpellCheckPreferenceListener;

	SpellingContext fSpellingContext;

	/*
	 * Value initialized through
	 * 
	 * super(ISourceViewer)->super.init()->createReconcileSteps()
	 */
	IReconcileStep fSpellingStep;

	String[] fSupportedPartitionTypes;
	String[] fSupportedTextRegionContexts;

	public SpellcheckStrategy(ISourceViewer viewer, String contentTypeId) {
		super(viewer);
		fContentTypeId = contentTypeId;

		fSpellingContext = new SpellingContext();
		fSpellingContext.setContentType(Platform.getContentTypeManager().getContentType(fContentTypeId));
		fReconcileAnnotationKey = new ReconcileAnnotationKey(fSpellingStep, KEY_CONTENT_TYPE, ReconcileAnnotationKey.PARTIAL);

		String[] definitions = ExtendedConfigurationBuilder.getInstance().getDefinitions(EXTENDED_BUILDER_TYPE_PARTITIONS, fContentTypeId);
		List defs = new ArrayList();
		for (int i = 0; i < definitions.length; i++) {
			defs.addAll(Arrays.asList(StringUtils.unpack(definitions[i])));
		}
		fSupportedPartitionTypes = (String[]) defs.toArray(new String[defs.size()]);

		String[] textRegionContexts = ExtendedConfigurationBuilder.getInstance().getDefinitions(EXTENDED_BUILDER_TYPE_CONTEXTS, fContentTypeId);
		List contexts = new ArrayList();
		for (int i = 0; i < textRegionContexts.length; i++) {
			contexts.addAll(Arrays.asList(StringUtils.unpack(textRegionContexts[i])));
		}
		fSupportedTextRegionContexts = (String[]) contexts.toArray(new String[contexts.size()]);

		fSpellCheckPreferenceListener = new SpellCheckPreferenceListener();
	}

	protected boolean containsStep(IReconcileStep step) {
		return fSpellingStep.equals(step);
	}

	public void createReconcileSteps() {
		fSpellingStep = new SpellingStep();
	}

	String getDocumentPartitioning() {
		return fDocumentPartitioning == null ? IDocumentExtension3.DEFAULT_PARTITIONING : fDocumentPartitioning;
	}

	private TemporaryAnnotation[] getSpellingAnnotationsToRemove(IRegion region) {
		List toRemove = new ArrayList();
		IAnnotationModel annotationModel = getAnnotationModel();
		// can be null when closing the editor
		if (getAnnotationModel() != null) {
			Iterator i = annotationModel.getAnnotationIterator();
			while (i.hasNext()) {
				Object obj = i.next();
				if (!(obj instanceof TemporaryAnnotation))
					continue;

				TemporaryAnnotation annotation = (TemporaryAnnotation) obj;
				ReconcileAnnotationKey key = (ReconcileAnnotationKey) annotation.getKey();

				// then if this strategy knows how to add/remove this
				// partition type
				if (key != null && key.equals(fReconcileAnnotationKey)) {
					Position position = annotation.getPosition();
					if (key.getScope() == ReconcileAnnotationKey.PARTIAL && position.overlapsWith(region.getOffset(), region.getLength())) {
						toRemove.add(annotation);
					}
					else if (key.getScope() == ReconcileAnnotationKey.TOTAL) {
						toRemove.add(annotation);
					}
				}
			}
		}

		return (TemporaryAnnotation[]) toRemove.toArray(new TemporaryAnnotation[toRemove.size()]);
	}

	/**
	 * @param engineID
	 * @return the SpellingService with the preferred engine
	 */
	SpellingService getSpellingService(String engineID) {
		SpellingService defaultService = EditorsUI.getSpellingService();

		SpellingService preferredService = defaultService;

		if (engineID != null) {
			/*
			 * Set up a preference store indicating that the given engine
			 * should be used instead of the default preference store's
			 * default
			 */
			SpellingEngineDescriptor[] descriptors = defaultService.getSpellingEngineDescriptors();
			for (int i = 0; i < descriptors.length; i++) {
				if (engineID.equals(descriptors[i].getId())) {
					IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), SSEUIPlugin.getDefault().getBundle().getSymbolicName());
					store.setValue(SpellingService.PREFERENCE_SPELLING_ENGINE, engineID);
					preferredService = new SpellingService(store);
					break;
				}
			}
		}
		return preferredService;
	}

	private boolean isSupportedPartitionType(String type) {
		boolean supported = false;
		if (fSupportedPartitionTypes == null || fSupportedPartitionTypes.length == 0) {
			supported = true;
		}
		else {
			for (int i = 0; i < fSupportedPartitionTypes.length; i++) {
				if (type.equals(fSupportedPartitionTypes[i])) {
					supported = true;
					break;
				}
			}
		}
		return supported;
	}

	private boolean isSupportedContext(String type) {
		boolean isSupported = false;
		if (fSupportedTextRegionContexts != null) {
			for (int i = 0; i < fSupportedTextRegionContexts.length; i++) {
				if (type.equals(fSupportedTextRegionContexts[i])) {
					isSupported = true;
					break;
				}
			}
		}
		return isSupported;
	}

	void reconcile() {
		IDocument document = getDocument();
		if (document != null) {
			IRegion documentRegion = new Region(0, document.getLength());
			reconcile(documentRegion);
		}
	}

	/**
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (isCanceled())
			return;

		TemporaryAnnotation[] annotationsToRemove = null;
		IReconcileResult[] annotationsToAdd = null;
		StructuredReconcileStep structuredStep = (StructuredReconcileStep) fSpellingStep;
		IAnnotationModel annotationModel = getAnnotationModel();

		IDocument document = getDocument();
		if (document != null) {
			try {
				ITypedRegion[] partitions = TextUtilities.computePartitioning(document, getDocumentPartitioning(), dirtyRegion.getOffset(), dirtyRegion.getLength(), true);
				for (int i = 0; i < partitions.length; i++) {
					if (isSupportedPartitionType(partitions[i].getType())) {
						annotationsToRemove = getSpellingAnnotationsToRemove(partitions[i]);
						annotationsToAdd = structuredStep.reconcile(dirtyRegion, partitions[i]);

						if (annotationModel instanceof IAnnotationModelExtension) {
							IAnnotationModelExtension modelExtension = (IAnnotationModelExtension) annotationModel;
							Map annotationsToAddMap = new HashMap();
							for (int j = 0; j < annotationsToAdd.length; j++) {
								annotationsToAddMap.put(annotationsToAdd[j], ((TemporaryAnnotation) annotationsToAdd[j]).getPosition());
							}
							modelExtension.replaceAnnotations(annotationsToRemove, annotationsToAddMap);
						}

						else {
							for (int j = 0; j < annotationsToAdd.length; j++) {
								annotationModel.addAnnotation((TemporaryAnnotation) annotationsToAdd[j], ((TemporaryAnnotation) annotationsToAdd[j]).getPosition());
							}
							for (int j = 0; j < annotationsToRemove.length; j++) {
								annotationModel.removeAnnotation(annotationsToRemove[j]);
							}
						}
					}
				}
			}
			catch (BadLocationException e) {
			}
		}
	}

	/**
	 * @param partition
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */

	public void reconcile(IRegion partition) {
		DirtyRegion region = null;
		IDocument document = getDocument();
		if (document != null) {
			try {
				region = new DirtyRegion(partition.getOffset(), partition.getLength(), DirtyRegion.INSERT, document.get(partition.getOffset(), partition.getLength()));
				reconcile(region, region);
			}
			catch (BadLocationException e) {
				Logger.logException(e);
			}
		}
	}

	public void setDocument(IDocument document) {
		if (getDocument() != null) {
			EditorsUI.getPreferenceStore().removePropertyChangeListener(fSpellCheckPreferenceListener);
		}

		super.setDocument(document);
		if (document != null) {
			if (fSpellingStep == null) {
				createReconcileSteps();
			}
			fSpellingStep.setInputModel(new DocumentAdapter(document));
		}

		if (getDocument() != null) {
			EditorsUI.getPreferenceStore().addPropertyChangeListener(fSpellCheckPreferenceListener);
		}
	}

	public void setDocumentPartitioning(String partitioning) {
		fDocumentPartitioning = partitioning;
	}
}