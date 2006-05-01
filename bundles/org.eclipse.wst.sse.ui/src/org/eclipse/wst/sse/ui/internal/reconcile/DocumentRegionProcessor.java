package org.eclipse.wst.sse.ui.internal.reconcile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.ui.internal.IReleasable;
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

	/**
	 * A strategy to use the defined default Spelling service.
	 */
	private SpellcheckStrategy fSpellcheckStrategy;

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

		// pa_TODO it would be nice to be able to get filename from
		// IDocument...
		// because it seems that getting content type from input stream
		// isn't all that accurate (eg. w/ a javascript file)

		// IContentType ct =
		// Platform.getContentTypeManager().findContentTypeFor("test.js");
		IContentType ct = null;
		try {
			IContentDescription desc = Platform.getContentTypeManager().getDescriptionFor(new ByteArrayInputStream(doc.get().getBytes()), null, IContentDescription.ALL);
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

	protected final StructuredTextReconcilingStrategy getSpellcheckStrategy() {
		if (fSpellcheckStrategy == null) {
			String contentTypeId = getContentType(getDocument());
			if (contentTypeId != null) {
				if (getTextViewer() instanceof ISourceViewer) {
					ISourceViewer viewer = (ISourceViewer) getTextViewer();
					fSpellcheckStrategy = new SpellcheckStrategy(viewer, contentTypeId);
					fSpellcheckStrategy.setDocument(getDocument());
					fSpellcheckStrategy.setDocumentPartitioning(getDocumentPartitioning());
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
					for (int i = 0; i < vmds.length; i++) {
						if (vmds[i].canHandleContentType(contentTypeId))
							validatorStrategy.addValidatorMetaData(vmds[i]);
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
		if (!isInstalled())
			return;

		super.process(dirtyRegion);

		// Also call the validation and spell-check strategies
		ITypedRegion[] partitions = computePartitioning(dirtyRegion);

		DirtyRegion dirty = null;
		for (int i = 0; i < partitions.length; i++) {
			dirty = createDirtyRegion(partitions[i], DirtyRegion.INSERT);
			// [source]validator (extension) for this partition
			if (getValidatorStrategy() != null)
				getValidatorStrategy().reconcile(partitions[i], dirty);
		}
		// single spell-check for everything
		if (getSpellcheckStrategy() != null)
			getSpellcheckStrategy().reconcile(dirtyRegion, dirtyRegion);
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
