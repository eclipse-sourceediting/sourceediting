/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui.tests.translation;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AnnotationTypeLookup;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.jsdt.core.compiler.IProblem;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;


/**
 * Opens the current markup editor's current translated source in the JavaScript editor
 * 
 * Invoke with M1+M2+8
 * 
 * @author nitin
 */
public class ShowTranslationHandler extends AbstractHandler {

	/**
	 * 
	 */
	public ShowTranslationHandler() {
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// IDE.openEditor(event.getApplicationContext(), createEditorInput(),
		// JavaUI.ID_CU_EDITOR, true);
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			List list = ((IStructuredSelection) selection).toList();
			if (!list.isEmpty()) {
				if (list.get(0) instanceof IDOMNode) {
					final IDOMModel model = ((IDOMNode) list.get(0)).getModel();
					INodeAdapter adapter = model.getDocument().getAdapterFor(IJsTranslation.class);
					if (adapter != null) {
						Job opener = new UIJob("Opening JavaScript Translation") {
							public IStatus runInUIThread(IProgressMonitor monitor) {
								JsTranslationAdapter translationAdapter = (JsTranslationAdapter) model.getDocument().getAdapterFor(IJsTranslation.class);
								final IJsTranslation translation = translationAdapter.getJsTranslation(false);

								// create an IEditorInput for the Java editor
								final IStorageEditorInput input = new JSTranslationEditorInput(translation, model.getBaseLocation());
								try {
									IEditorPart editor = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), input, JavaScriptUI.ID_CU_EDITOR, true);
									// Now add the problems we found
									if (editor instanceof ITextEditor) {
										IAnnotationModel annotationModel = ((ITextEditor) editor).getDocumentProvider().getAnnotationModel(input);
										translation.reconcileCompilationUnit();
										List problemsList = translation.getProblems();
										IProblem[] problems = (IProblem[]) problemsList.toArray(new IProblem[problemsList.size()]);
										AnnotationTypeLookup lookup = new AnnotationTypeLookup();
										for (int i = 0; i < problems.length; i++) {
											int length = problems[i].getSourceEnd() - problems[i].getSourceStart() + 1;
											Position position = new Position(problems[i].getSourceStart(), length);
											Annotation annotation = null;
											String type = lookup.getAnnotationType(IMarker.PROBLEM, IMarker.SEVERITY_INFO);
											if (problems[i].isError()) {
												type = lookup.getAnnotationType(IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
											}
											else if (problems[i].isWarning()) {
												type = lookup.getAnnotationType(IMarker.PROBLEM, IMarker.SEVERITY_WARNING);
											}
											annotation = new Annotation(type, false, problems[i].getMessage());
											if (annotation != null) {
												annotationModel.addAnnotation(annotation, position);
											}
										}
									}
								}
								catch (PartInitException e) {
									e.printStackTrace();
									Display.getCurrent().beep();
								}
								return Status.OK_STATUS;
							}
						};
						opener.setSystem(false);
						opener.setUser(true);
						opener.schedule();
					}
				}
			}
		}
		return null;
	}
}
