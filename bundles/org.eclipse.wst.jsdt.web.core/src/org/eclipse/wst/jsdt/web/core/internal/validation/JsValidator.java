/*******************************************************************************
 * Copyright (c) 2006, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.validation;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.jsdt.core.compiler.IProblem;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationExtension;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JsValidator extends JSPValidator {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$

	private IValidator fMessageOriginator;

	public JsValidator() {
		this.fMessageOriginator = this;
	}

	public JsValidator(IValidator validator) {
		this.fMessageOriginator = validator;
	}

	/**
	 * Assumed the message offset is an indirect position. In other words, an
	 * error from an included file.
	 * 
	 * @param m
	 * @param translation
	 */
	private void adjustIndirectPosition(IMessage m, JSPTranslation translation) {

		if (!(translation instanceof JSPTranslationExtension)) {
			return;
		}

		IDocument jspDoc = ((JSPTranslationExtension) translation).getJspDocument();
		if (!(jspDoc instanceof IStructuredDocument)) {
			return;
		}

		IStructuredDocument sDoc = (IStructuredDocument) jspDoc;
		IStructuredDocumentRegion[] regions = sDoc.getStructuredDocumentRegions(0, m.getOffset() + m.getLength());
		// iterate backwards until you hit the include directive
		for (int i = regions.length - 1; i >= 0; i--) {

			IStructuredDocumentRegion region = regions[i];
			if (region.getType() == "script import region") {
				if (getDirectiveName(region).equals("include")) { //$NON-NLS-1$
					ITextRegion fileValueRegion = getAttributeValueRegion(region, "file"); //$NON-NLS-1$
					m.setOffset(region.getStartOffset(fileValueRegion));
					m.setLength(fileValueRegion.getTextLength());
					break;
				}
			}
		}
	}

	/**
	 * Creates an IMessage from an IProblem
	 * 
	 * @param problem
	 * @param f
	 * @param translation
	 * @param structuredDoc
	 * @return message representation of the problem, or null if it could not
	 *         create one
	 */
	private IMessage createMessageFromProblem(IProblem problem, IFile f, JSPTranslation translation, IStructuredDocument structuredDoc) {

		int sourceStart = translation.getJspOffset(problem.getSourceStart());
		int sourceEnd = translation.getJspOffset(problem.getSourceEnd());
		if (sourceStart == -1) {
			return null;
		}

		// line number for marker starts @ 1
		// line number from document starts @ 0
		int lineNo = structuredDoc.getLineOfOffset(sourceStart) + 1;

		int sev = problem.isError() ? IMessage.HIGH_SEVERITY : IMessage.NORMAL_SEVERITY;

		IMessage m = new LocalizedMessage(sev, problem.getMessage(), f);

		m.setLineNo(lineNo);
		m.setOffset(sourceStart);
		m.setLength(sourceEnd - sourceStart + 1);

		// need additional adjustment for problems from
		// indirect (included) files
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=119633
		if (translation.isIndirect(problem.getSourceStart())) {
			adjustIndirectPosition(m, translation);
		}

		return m;
	}

	void performValidation(IFile f, IReporter reporter, IStructuredModel model) {

		if (model instanceof IDOMModel) {
			IDOMModel domModel = (IDOMModel) model;
			setupAdapterFactory(domModel);

			IDOMDocument xmlDoc = domModel.getDocument();
			JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			translationAdapter.documentChanged(null);
			JSPTranslation translation = translationAdapter.getJSPTranslation();

			if (!reporter.isCancelled()) {
				translation.setProblemCollectingActive(true);
				translation.reconcileCompilationUnit();
				List problems = translation.getProblems();
				// add new messages
				for (int i = 0; i < problems.size() && !reporter.isCancelled(); i++) {
					IMessage m = createMessageFromProblem((IProblem) problems.get(i), f, translation, domModel.getStructuredDocument());
					if (m != null) {
						reporter.addMessage(fMessageOriginator, m);
					}
				}
			}
		}
	}

	/**
	 * When loading model from a file, you need to explicitly add adapter
	 * factory.
	 * 
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {
		if (sm.getFactoryRegistry().getFactoryFor(IJSPTranslation.class) == null) {
			JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
			sm.getFactoryRegistry().addFactory(factory);
		}
	}

	@Override
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		reporter.removeAllMessages(this);
		super.validate(helper, reporter);
	}

	/**
	 * Validate one file. It's assumed that the file has JSP content type.
	 * 
	 * @param f
	 * @param reporter
	 */
	@Override
	protected void validateFile(IFile f, IReporter reporter) {
		if (JsValidator.DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " validating: " + f); //$NON-NLS-1$
		}

		IStructuredModel model = null;
		try {
			// get jsp model, get tranlsation
			model = StructuredModelManager.getModelManager().getModelForRead(f);
			if (!reporter.isCancelled() && model != null) {
				// get jsp model, get translation
				if (model instanceof IDOMModel) {
					reporter.removeAllMessages(fMessageOriginator, f);
					performValidation(f, reporter, model);
				}
			}
		} catch (IOException e) {
			Logger.logException(e);
		} catch (CoreException e) {
			Logger.logException(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
	}
}
