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
package org.eclipse.jst.jsp.ui.internal.correction;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation;
import org.eclipse.jdt.internal.ui.text.correction.AssistContext;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;
import org.eclipse.jdt.internal.ui.text.correction.QuickFixProcessor;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickAssistProcessor;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.correction.StructuredCorrectionProcessor;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;

public class CorrectionProcessorJSP extends StructuredCorrectionProcessor {
	protected IQuickFixProcessor fQuickFixProcessor;
	protected IQuickAssistProcessor fQuickAssistProcessor;

	public CorrectionProcessorJSP(ITextEditor editor) {
		super(editor);
	}

	protected IQuickFixProcessor getQuickFixProcessorJSP() {
		if (fQuickFixProcessor == null)
			fQuickFixProcessor = new QuickFixProcessor();

		return fQuickFixProcessor;
	}

	protected IQuickAssistProcessor getQuickAssistProcessorJSP() {
		if (fQuickAssistProcessor == null)
			fQuickAssistProcessor = new QuickAssistProcessorJSP();

		return fQuickAssistProcessor;
	}

	protected void addQuickFixProposals(StructuredTextViewer viewer, ArrayList proposals, int documentOffset) {
		ArrayList problems = new ArrayList();
		Iterator iter = fAnnotationModel.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation annotation = (Annotation) iter.next();
			Position pos = fAnnotationModel.getPosition(annotation);
			if (documentOffset >= pos.offset && documentOffset <= pos.offset + pos.length) {
				IQuickFixProcessor processor = getQuickFixProcessorJSP();

				XMLModel xmlModel = (XMLModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
				XMLDocument xmlDoc = xmlModel.getDocument();
				xmlModel.releaseFromRead();
				JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				if (translationAdapter != null) {
					JSPTranslation translation = translationAdapter.getJSPTranslation();

					ICompilationUnit cu = translation.getCompilationUnit();
					int problemID = -1;
					if (annotation instanceof TemporaryAnnotation) {
						problemID = ((TemporaryAnnotation) annotation).getProblemID();
						if (problemID != -1) {
							IJavaAnnotation javaAnnotation = new ProblemAnnotation((IProblem) ((TemporaryAnnotation) annotation).getAdditionalFixInfo(), cu);
							problems.add(new ProblemLocation(translation.getJavaOffset(pos.offset), pos.getLength(), javaAnnotation));
						}
					}

					int length = viewer != null ? viewer.getSelectedRange().y : 0;
					AssistContext context = new AssistContext(cu, translation.getJavaOffset(documentOffset), length);

					if (processor != null && processor.hasCorrections(cu, problemID)) {
						try {
							IProblemLocation[] problemLocations = (IProblemLocation[]) problems.toArray(new IProblemLocation[problems.size()]);
							IJavaCompletionProposal[] res = ((QuickFixProcessor) processor).getCorrections(context, problemLocations);
							if (res != null) {
								for (int k = 0; k < res.length; k++) {
									//proposals.add(res[k]);
								}
							}
						}
						catch (CoreException e) {
							throw new SourceEditingRuntimeException();
						}
					}
				}
			}
		}
	}

	protected void addQuickAssistProposals(StructuredTextViewer viewer, ArrayList proposals, int documentOffset) {
		try {
			IQuickAssistProcessor processor = getQuickAssistProcessorJSP();

			XMLModel xmlModel = (XMLModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
			XMLDocument xmlDoc = xmlModel.getDocument();
			xmlModel.releaseFromRead();
			JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			if (translationAdapter != null) {
				JSPTranslation translation = translationAdapter.getJSPTranslation();
				ICompilationUnit cu = translation.getCompilationUnit();

				if (cu != null) {
					synchronized(cu) {
						if (processor instanceof QuickAssistProcessorJSP)
							((QuickAssistProcessorJSP) processor).setJSPDocument(viewer.getDocument());
	
						int length = viewer != null ? viewer.getSelectedRange().y : 0;
						AssistContext context = new AssistContext(cu, translation.getJavaOffset(documentOffset), length);
	
						if (processor != null) {
							ICompletionProposal[] res = processor.getAssists(context, null);
							if (res != null) {
								for (int k = 0; k < res.length; k++) {
									proposals.add(res[k]);
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw new SourceEditingRuntimeException();
		}
	}
}