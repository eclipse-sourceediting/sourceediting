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
import java.util.HashMap;
import java.util.Iterator;
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
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.validation.core.FileDelta;
import org.eclipse.wst.validation.core.IFileDelta;
import org.eclipse.wst.validation.core.IHelper;
import org.eclipse.wst.validation.core.IMessage;
import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.core.SeverityEnum;


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
	private IHelper fHelper = null;
	private IncrementalReporter fReporter = null;
	private int fScope = -1;
	private IValidator fValidator = null;


	public ReconcileStepForValidator(IValidator v) {
		super();
		this.fValidator = v;
	}

	public ReconcileStepForValidator(IValidator v, IReconcileStep step) {
		super(step);
		this.fValidator = v;
	}

	/**
	 * Converts a map of IValidatorForReconcile to List to annotations based
	 * on those messages
	 * 
	 * @param messages
	 * @return
	 */
	protected IReconcileResult[] createAnnotations(HashMap messages) {
		List annotations = new ArrayList();
		Iterator keys = messages.keySet().iterator();

		while (keys.hasNext() && !isCanceled()) {
			IValidator validator = (IValidator) keys.next();
			List messageList = (List) messages.get(validator);
			for (int i = 0; i < messageList.size(); i++) {
				IMessage validationMessage = (IMessage) messageList.get(i);
				int offset = validationMessage.getOffset();

				if (offset < 0)
					continue;

				String messageText = null;
				try {
                    messageText = validationMessage.getText(validator.getClass().getClassLoader());
				} catch (Exception t) {
					Logger.logException("exception reporting message from validator", t); //$NON-NLS-1$
					continue;
				}
				String type = TemporaryAnnotation.ANNOT_INFO;
				switch (validationMessage.getSeverity()) {
					case SeverityEnum.HIGH_SEVERITY :
						type = TemporaryAnnotation.ANNOT_ERROR;
						break;
					case SeverityEnum.NORMAL_SEVERITY :
						type = TemporaryAnnotation.ANNOT_WARNING;
						break;
					case SeverityEnum.LOW_SEVERITY :
						type = TemporaryAnnotation.ANNOT_WARNING;
						break;
					case SeverityEnum.ERROR_AND_WARNING :
						type = TemporaryAnnotation.ANNOT_WARNING;
						break;
				}
				Position p = new Position(offset, validationMessage.getLength());
				IReconcileAnnotationKey key = createKey(getPartitionType(getDocument(), offset), IReconcileAnnotationKey.TOTAL);
				annotations.add(new TemporaryAnnotation(p, type, messageText, key));
			}
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
					//file = project.getWorkspace().getRoot().getFileForLocation(new Path(model.getBaseLocation()));
					file = project.getWorkspace().getRoot().getFile(new Path(model.getBaseLocation()));
				} finally {
					if (model != null)
						model.releaseFromRead();
				}
			}
		}
		return file;
	}

	private IHelper getHelper(IProject project) {
		if (this.fHelper == null)
			this.fHelper = new IncrementalHelper(getStructuredDocument(), project);
		return this.fHelper;
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
		if (this.fReporter == null)
			this.fReporter = new IncrementalReporter(getProgressMonitor());
		return this.fReporter;
	}

    /**
     * remove from extension point
     * @return
     */
	public int getScope() {
		return this.fScope;
	}

	public void initialReconcile() {
		// do nothing
	}

	protected IReconcileResult[] reconcileModel(DirtyRegion dirtyRegion, IRegion subRegion) {
		if(DEBUG)
            System.out.println("[trace reconciler] > reconciling model in VALIDATOR step w/ dirty region: " + dirtyRegion.getText()); //$NON-NLS-1$

		// pa_TODO need to use dirty region if Validators can ever handle
		// partial file validation
		IReconcileResult[] results = this.EMPTY_RECONCILE_RESULT_SET;
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
		if (this.fValidator != null)
			debugString.append(this.fValidator.getClass().toString());
		return debugString.toString();
	}

	protected IReconcileResult[] validate() {
		IReconcileResult[] results = this.EMPTY_RECONCILE_RESULT_SET;

		IProject project = getProject();
		IFile file = getFile(project);

		if (file != null) {
			try {
				IHelper helper = getHelper(project);
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
