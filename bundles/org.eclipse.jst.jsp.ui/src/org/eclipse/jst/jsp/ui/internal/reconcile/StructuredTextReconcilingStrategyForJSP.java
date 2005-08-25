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
package org.eclipse.jst.jsp.ui.internal.reconcile;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy;

/**
 * 
 * @author pavery
 */
public class StructuredTextReconcilingStrategyForJSP extends AbstractStructuredTextReconcilingStrategy {
	private boolean fShouldReconcile = true;

	public StructuredTextReconcilingStrategyForJSP(ITextEditor editor) {
		super(editor);
	}

	public void createReconcileSteps() {

		// the order is:
		// 1. translation step
		// 2. java step
		if (getFile() != null) {
			IReconcileStep javaStep = new ReconcileStepForJava(getFile());
			fFirstStep = new ReconcileStepForJspTranslation(javaStep);
		}
	}

	/**
	 * Returns an IFile for given document if it exists
	 * 
	 * @param document
	 *            document open in an editor
	 * @return IFile for given document, null if undetermined
	 */
	private IFile getFile(IDocument document) {
		// determine IFile
		IFile file = null;
		IStructuredModel model = null;
		String baselocation = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null) {
				baselocation = model.getBaseLocation();
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
		if (baselocation != null) {
			// assumes document is open in editor
			// copied from JSPTranslator#getFile()
			file = FileBuffers.getWorkspaceFileAtLocation(new Path(baselocation));

		}
		return file;
	}

	/**
	 * Determines if file is jsp fragment or not
	 * 
	 * @param file
	 *            assumes file is not null and exists
	 * @return true if file is jsp fragment, false otherwise
	 */
	private boolean isFragment(IFile file) {
		boolean isFragment = false;
		InputStream is = null;
		try {
			IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
			// check this before description, it's less expensive
			if (contentTypeJSP.isAssociatedWith(file.getName())) {

				IContentDescription contentDescription = file.getContentDescription();
				// it can be null
				if (contentDescription == null) {
					is = file.getContents();
					contentDescription = Platform.getContentTypeManager().getDescriptionFor(is, file.getName(), new QualifiedName[]{IContentDescription.CHARSET});
				}
				if (contentDescription != null) {
					String fileCtId = contentDescription.getContentType().getId();
					isFragment = (fileCtId != null && ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT.equals(fileCtId));
				}
			}
		}
		catch (IOException e) {
			// ignore, assume it's invalid JSP
		}
		catch (CoreException e) {
			// ignore, assume it's invalid JSP
		}
		finally {
			// must close input stream in case others need it
			if (is != null)
				try {
					is.close();
				}
				catch (Exception e) {
					// not sure how to recover at this point
				}
		}
		return isFragment;
	}

	/**
	 * @return <code>true</code> if the entire document is validated for
	 *         each edit (this strategy can't handle partial document
	 *         validation). This will greatly help performance.
	 */
	public boolean isTotalScope() {
		return true;
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=87351
		if (fShouldReconcile)
			super.reconcile(dirtyRegion, subRegion);
	}

	public void setDocument(IDocument document) {
		super.setDocument(document);

		if (document != null) {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=87351
			// get preference for validate jsp fragments
			boolean shouldReconcileFragments = Platform.getPreferencesService().getBoolean(JSPCorePlugin.getDefault().getBundle().getSymbolicName(), JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true, null);
			boolean isFragment = false;
			if (!shouldReconcileFragments) {
				IFile file = getFile(document);
				if (file != null && file.exists()) {
					isFragment = isFragment(file);
				}
			}
			fShouldReconcile = (shouldReconcileFragments || !isFragment);
		}
	}
}