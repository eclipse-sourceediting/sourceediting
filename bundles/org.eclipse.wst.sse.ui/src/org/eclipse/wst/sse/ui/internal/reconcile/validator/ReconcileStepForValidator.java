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
package org.eclipse.wst.sse.ui.internal.reconcile.validator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.reconcile.ReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.validation.core.IFileDelta;
import org.eclipse.wst.validation.core.IMessage;
import org.eclipse.wst.validation.core.IValidationContext;
import org.eclipse.wst.validation.core.IValidator;
import org.eclispe.wst.validation.internal.core.FileDelta;


/**
 * A reconcile step for an IValidator for reconcile. Used the reconcile
 * framework to create TemporaryAnnotations from the validator messages.
 * 
 * @author pavery
 */
public class ReconcileStepForValidator extends StructuredReconcileStep {

    /** debug flag */
    protected static final boolean DEBUG;
    static {
        String value = Platform.getDebugOption("org.eclipse.wst.sse.ui/debug/reconcilerjob"); //$NON-NLS-1$
        DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
    }
    
	private final IReconcileResult[] EMPTY_RECONCILE_RESULT_SET = new IReconcileResult[0];
	private IValidationContext fHelper = null;
	private IncrementalReporter fReporter = null;
	private int fScope = -1;
	private IValidator fValidator = null;


	public ReconcileStepForValidator(IValidator v, int scope) {
		super();
		fValidator = v;
        fScope = scope;
	}

	public ReconcileStepForValidator(IValidator v, IReconcileStep step, int scope) {
		super(step);
		fValidator = v;
        fScope = scope;
	}

	/**
	 * Converts a map of IValidatorForReconcile to List to annotations based
	 * on those messages
	 * 
	 * @param messages
	 * @return
	 */
	protected IReconcileResult[] createAnnotations(List messageList) {
		List annotations = new ArrayList();
			for (int i = 0; i < messageList.size(); i++) {
				IMessage validationMessage = (IMessage) messageList.get(i);
				
				int offset = validationMessage.getOffset();

				if (offset < 0)
					continue;

				String messageText = null;
				try {
                    messageText = validationMessage.getText(validationMessage.getClass().getClassLoader());
				} catch (Exception t) {
					Logger.logException("exception reporting message from validator", t); //$NON-NLS-1$
					continue;
				}
				String type = TemporaryAnnotation.ANNOT_INFO;
				switch (validationMessage.getSeverity()) {
					case IMessage.HIGH_SEVERITY :
						type = TemporaryAnnotation.ANNOT_ERROR;
						break;
					case IMessage.NORMAL_SEVERITY :
						type = TemporaryAnnotation.ANNOT_WARNING;
						break;
					case IMessage.LOW_SEVERITY :
						type = TemporaryAnnotation.ANNOT_WARNING;
						break;
					case IMessage.ERROR_AND_WARNING :
						type = TemporaryAnnotation.ANNOT_WARNING;
						break;
				}
				Position p = new Position(offset, validationMessage.getLength());
				ReconcileAnnotationKey key = createKey(getPartitionType(getDocument(), offset), getScope());
				annotations.add(new TemporaryAnnotation(p, type, messageText, key));
			}
		
		return (IReconcileResult[]) annotations.toArray(new IReconcileResult[annotations.size()]);
	}

	private IFile getFile(IProject project) {

		IFile file = null;
		if (project != null) {

			IDocument doc = getDocument();
			// document may be null inbetween model/document swap
			if (doc != null) {
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
					// (pa) with FileBuffers, model base location is relative
					// so we need to use the getFile(...) call
					// file =
					// project.getWorkspace().getRoot().getFileForLocation(new
					// Path(model.getBaseLocation()));
					file = project.getWorkspace().getRoot().getFile(new Path(model.getBaseLocation()));
				} finally {
					if (model != null)
						model.releaseFromRead();
				}
			}
		}
		return file;
	}

	private IValidationContext getHelper(IProject project) {
		if (fHelper == null)
			fHelper = new IncrementalHelper(getStructuredDocument(), project);
		return fHelper;
	}

	private IProject getProject() {

		URIResolver resolver = null;
		IDocument doc = getDocument();

		if (doc != null) {
			IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
			try {
				if (model != null)
					resolver = model.getResolver();
			} finally {
				if (model != null)
					model.releaseFromRead();
			}
		}
		return (resolver != null) ? resolver.getProject() : null;
	}

	private IncrementalReporter getReporter() {
		if (fReporter == null)
			fReporter = new IncrementalReporter(getProgressMonitor());
		return fReporter;
	}

    /**
	 * remove from extension point
	 * 
	 * @return
	 */
	public int getScope() {
		return fScope;
	}

	public void initialReconcile() {
		// do nothing
	}

	protected IReconcileResult[] reconcileModel(DirtyRegion dirtyRegion, IRegion subRegion) {
		if(DEBUG)
            System.out.println("[trace reconciler] > reconciling model in VALIDATOR step w/ dirty region: [" + dirtyRegion.getText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		// pa_TODO need to use dirty region if Validators can ever handle
		// partial file validation
		IReconcileResult[] results = EMPTY_RECONCILE_RESULT_SET;
		if (dirtyRegion != null) {
			try {
				results = validate();
			} catch (Exception ex) {
				Logger.logException("EXEPTION IN RECONCILE STEP FOR VALIDATOR", ex); //$NON-NLS-1$
			}
		}

		if(DEBUG)
		    System.out.println("[trace reconciler] > VALIDATOR step done"); //$NON-NLS-1$

		return results;
	}

	public String toString() {
		StringBuffer debugString = new StringBuffer("ValidatorStep: "); //$NON-NLS-1$
		if (fValidator != null)
			debugString.append(fValidator.getClass().toString());
		return debugString.toString();
	}

	protected IReconcileResult[] validate() {
		IReconcileResult[] results = EMPTY_RECONCILE_RESULT_SET;

		IProject project = getProject();
		IFile file = getFile(project);

		if (file != null) {
			try {
				IValidationContext helper = getHelper(project);
				IncrementalReporter reporter = getReporter();

				IFileDelta fullDelta = new FileDelta(file.getFullPath().toString(), IFileDelta.CHANGED);
				fValidator.validate(helper, reporter, new IFileDelta[]{fullDelta});

				results = createAnnotations(reporter.getMessages());
				reporter.getMessages().clear();

			} catch (Exception e) {
				Logger.logException(e);
			}
		}
		return results;
	}
}
