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
package org.eclipse.wst.sse.ui.internal.reconcile;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorBuilder;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorMetaData;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorStrategy;
import org.eclipse.wst.sse.ui.internal.spelling.SpellcheckStrategy;

/**
 * Adds to DirtyRegionProcessor Job: - IDocumentListener - ValidatorStrategy -
 * Text viewer(dispose, input changed) listeners. - default, spelling, and
 * validator strategies - DirtyRegion processing logic.
 */
public class DocumentRegionProcessor extends DirtyRegionProcessor {

	private static final boolean DEBUG_VALIDATORS = Boolean.TRUE.toString().equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/debug/reconcilerValidators")); //$NON-NLS-1$

	/**
	 * A strategy to use the defined default Spelling service.
	 */
	private IReconcilingStrategy fSpellcheckStrategy;

	/**
	 * The strategy that runs validators contributed via
	 * <code>org.eclipse.wst.sse.ui.extensions.sourcevalidation</code>
	 * extension point
	 */
	private ValidatorStrategy fValidatorStrategy;

	private final String SSE_UI_ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$


	protected void beginProcessing() {
		super.beginProcessing();
		ValidatorStrategy validatorStrategy = getValidatorStrategy();
		if (validatorStrategy != null) {
			validatorStrategy.beginProcessing();
		}
	}

	protected void endProcessing() {
		super.endProcessing();
		ValidatorStrategy validatorStrategy = getValidatorStrategy();
		if (validatorStrategy != null) {
			validatorStrategy.endProcessing();
		}
	}

	protected String getContentType(IDocument doc) {
		if (doc == null)
			return null;

		String contentTypeId = null;

		IContentType ct = null;
		try {
			IContentDescription desc = Platform.getContentTypeManager().getDescriptionFor(new StringReader(doc.get()), null, IContentDescription.ALL);
			if (desc != null) {
				ct = desc.getContentType();
				if (ct != null)
					contentTypeId = ct.getId();
			}
		}
		catch (IOException e) {
			// just bail
		}
		return contentTypeId;
	}

	protected IReconcilingStrategy getSpellcheckStrategy() {
		if (fSpellcheckStrategy == null) {
			String contentTypeId = getContentType(getDocument());
			if (contentTypeId != null) {
				if (getTextViewer() instanceof ISourceViewer) {
					ISourceViewer viewer = (ISourceViewer) getTextViewer();
					fSpellcheckStrategy = new SpellcheckStrategy(viewer, contentTypeId);
					fSpellcheckStrategy.setDocument(getDocument());
				}
			}
		}
		return fSpellcheckStrategy;
	}

	/**
	 * @return Returns the ValidatorStrategy.
	 */
	protected ValidatorStrategy getValidatorStrategy() {
		if (fValidatorStrategy == null) {
			ValidatorStrategy validatorStrategy = null;

			if (getTextViewer() instanceof ISourceViewer) {
				ISourceViewer viewer = (ISourceViewer) getTextViewer();
				String contentTypeId = null;

				IDocument doc = viewer.getDocument();
				contentTypeId = getContentType(doc);

				if (contentTypeId != null) {
					validatorStrategy = new ValidatorStrategy(viewer, contentTypeId);
					ValidatorBuilder vBuilder = new ValidatorBuilder();
					ValidatorMetaData[] vmds = vBuilder.getValidatorMetaData(SSE_UI_ID);
					List enabledValidators = new ArrayList(1);
					/* if any "must" handle this content type, just add them */
					boolean foundSpecificContentTypeValidators = false;
					for (int i = 0; i < vmds.length; i++) {
						if (vmds[i].mustHandleContentType(contentTypeId)) {
							if (DEBUG_VALIDATORS)
								Logger.log(Logger.INFO, contentTypeId + " using specific validator " + vmds[i].getValidatorId()); //$NON-NLS-1$
							foundSpecificContentTypeValidators = true;
							enabledValidators.add(vmds[i]);
						}
					}
					if (!foundSpecificContentTypeValidators) {
						for (int i = 0; i < vmds.length; i++) {
							if (vmds[i].canHandleContentType(contentTypeId)) {
								if (DEBUG_VALIDATORS)
									Logger.log(Logger.INFO, contentTypeId + " using inherited(?) validator " + vmds[i].getValidatorId()); //$NON-NLS-1$
								enabledValidators.add(vmds[i]);
							}
						}
					}
					for (int i = 0; i < enabledValidators.size(); i++) {
						validatorStrategy.addValidatorMetaData((ValidatorMetaData) enabledValidators.get(i));
					}
				}
			}
			fValidatorStrategy = validatorStrategy;
		}
		return fValidatorStrategy;
	}

	/**
	 * @param dirtyRegion
	 */
	protected void process(DirtyRegion dirtyRegion) {
		if (!isInstalled() || isInRewriteSession())
			return;

		super.process(dirtyRegion);

		// Also call the validation and spell-check strategies
		ITypedRegion[] partitions = computePartitioning(dirtyRegion);

		DirtyRegion dirty = null;
		for (int i = 0; i < partitions.length; i++) {
			// [source]validator (extension) for this partition
			if (getValidatorStrategy() != null) {
				dirty = createDirtyRegion(partitions[i], DirtyRegion.INSERT);
				getValidatorStrategy().reconcile(partitions[i], dirty);
			}
		}

		// single spell-check for everything
		if (getSpellcheckStrategy() != null) {
			getSpellcheckStrategy().reconcile(dirtyRegion, dirtyRegion);
		}
	}

	public void setDocument(IDocument doc) {
		super.setDocument(doc);
		IReconcilingStrategy validatorStrategy = getValidatorStrategy();
		if (validatorStrategy != null) {
			validatorStrategy.setDocument(doc);
		}
		if (fSpellcheckStrategy != null) {
			fSpellcheckStrategy.setDocument(doc);
		}
	}

	protected void setEntireDocumentDirty(IDocument document) {
		super.setEntireDocumentDirty(document);

		// make the entire document dirty
		// this also happens on a "save as"
		if (document != null && isInstalled() && fLastPartitions != null && document.getLength() == 0) {
			/**
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=199053
			 * 
			 * Process the strategies for the last known-good partitions.
			 */
			for (int i = 0; i < fLastPartitions.length; i++) {
				getValidatorStrategy().reconcile(fLastPartitions[i], createDirtyRegion(fLastPartitions[i], DirtyRegion.REMOVE));
			}
			if (fSpellcheckStrategy != null) {
				fSpellcheckStrategy.reconcile(new Region(0, document.getLength()));
			}
		}
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#uninstall()
	 */
	public void uninstall() {
		if (isInstalled()) {

			cancel();

			IReconcilingStrategy validatorStrategy = getValidatorStrategy();

			if (validatorStrategy != null) {
				if (validatorStrategy instanceof IReleasable)
					((IReleasable) validatorStrategy).release();
			}
			if (fSpellcheckStrategy != null) {
				fSpellcheckStrategy.setDocument(null);
			}
		}
		super.uninstall();
	}
}
