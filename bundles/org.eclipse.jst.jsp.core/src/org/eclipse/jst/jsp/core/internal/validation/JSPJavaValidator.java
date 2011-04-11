/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.IJSPProblem;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSPJavaValidator extends JSPValidator {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$
	private IValidator fMessageOriginator;

	private IPreferencesService fPreferencesService = Platform.getPreferencesService();
	private static final String PREFERENCE_NODE_QUALIFIER = JSPCorePlugin.getDefault().getBundle().getSymbolicName();
	private IScopeContext[] fScopes = null;
	
	private static final boolean UPDATE_JAVA_TASKS = true;
	private static final String JAVA_TASK_MARKER_TYPE = "org.eclipse.jdt.core.task"; //$NON-NLS-1$
	private static final String[] DEPEND_ONs = new String[]{".classpath", ".project", ".settings/org.eclipse.jdt.core.prefs", ".settings/org.eclipse.jst.jsp.core.prefs", ".settings/org.eclipse.wst.common.project.facet.core.xml", ".settings/org.eclipse.wst.common.component"};

	public JSPJavaValidator() {
		this.fMessageOriginator = this;
	}

	public JSPJavaValidator(IValidator validator) {
		this.fMessageOriginator = validator;
	}

	/**
	 * Assumed the message offset is an indirect position. In other words, an
	 * error from an included file.
	 * 
	 * @param m
	 * @param translation
	 */
	private void adjustIndirectPosition(IMessage m, IJSPTranslation translation) {

		if (!(translation instanceof JSPTranslationExtension))
			return;

		IDocument jspDoc = ((JSPTranslationExtension) translation).getJspDocument();
		if (!(jspDoc instanceof IStructuredDocument))
			return;

		IStructuredDocument sDoc = (IStructuredDocument) jspDoc;
		IStructuredDocumentRegion[] regions = sDoc.getStructuredDocumentRegions(0, m.getOffset() + m.getLength());
		// iterate backwards until you hit the include directive
		for (int i = regions.length - 1; i >= 0; i--) {
			IStructuredDocumentRegion region = regions[i];
			if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				if (getDirectiveName(region).equals("include")) { //$NON-NLS-1$
					ITextRegion fileValueRegion = getAttributeValueRegion(region, "file"); //$NON-NLS-1$
					if (fileValueRegion != null) {
						m.setOffset(region.getStartOffset(fileValueRegion));
						m.setLength(fileValueRegion.getTextLength());
					}
					else {
						m.setOffset(region.getStartOffset());
						m.setLength(region.getTextLength());
					}
					/**
					 * Bug 219761 - Syntax error reported at wrong location
					 * (don't forget to adjust the line number, too)
					 */
					m.setLineNo(sDoc.getLineOfOffset(m.getOffset()) + 1);
					break;
				}
			}
		}
	}

	/**
	 * Creates an IMessage from asn IProblem
	 * 
	 * @param problem
	 * @param f
	 * @param translation
	 * @param structuredDoc
	 * @return message representation of the problem, or null if it could not
	 *         create one
	 */
	private IMessage createMessageFromProblem(IProblem problem, IFile f, IJSPTranslation translation, IStructuredDocument structuredDoc) {
		int sev = -1;
		int sourceStart = -1;
		int sourceEnd = -1;

		if (problem instanceof IJSPProblem) {
			sourceStart = problem.getSourceStart();
			sourceEnd = problem.getSourceEnd();
			switch (((IJSPProblem) problem).getEID()) {
				case IJSPProblem.TEIClassNotFound :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_NOT_FOUND);
					break;
				case IJSPProblem.TEIValidationMessage :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_VALIDATION_MESSAGE);
					break;
				case IJSPProblem.TEIClassNotInstantiated :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_NOT_INSTANTIATED);
					break;
				case IJSPProblem.TEIClassMisc :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_RUNTIME_EXCEPTION);
					break;
				case IJSPProblem.TagClassNotFound :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TAG_HANDLER_CLASS_NOT_FOUND);
					break;
				case IJSPProblem.UseBeanInvalidID :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_USEBEAN_INVALID_ID);
					break;
				case IJSPProblem.UseBeanMissingTypeInfo :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_USBEAN_MISSING_TYPE_INFO);
					break;
				case IJSPProblem.UseBeanAmbiguousType :
					sev = getMessageSeverity(JSPCorePreferenceNames.VALIDATION_TRANSLATION_USEBEAN_AMBIGUOUS_TYPE_INFO);
					break;
				default :
					sev = problem.isError() ? IMessage.HIGH_SEVERITY : (problem.isWarning() ? IMessage.NORMAL_SEVERITY : ValidationMessage.IGNORE);

			}
		}
		else {
			sourceStart = translation.getJspOffset(problem.getSourceStart());
			sourceEnd = translation.getJspOffset(problem.getSourceEnd());
			switch (problem.getID()) {
				case IProblem.LocalVariableIsNeverUsed : {
					sev = getSourceSeverity(JSPCorePreferenceNames.VALIDATION_JAVA_LOCAL_VARIABLE_NEVER_USED, sourceStart, sourceEnd);
				}
					break;
				case IProblem.NullLocalVariableReference : {
					sev = getSourceSeverity(JSPCorePreferenceNames.VALIDATION_JAVA_NULL_LOCAL_VARIABLE_REFERENCE, sourceStart, sourceEnd);
				}
					break;
				case IProblem.ArgumentIsNeverUsed : {
					sev = getSourceSeverity(JSPCorePreferenceNames.VALIDATION_JAVA_ARGUMENT_IS_NEVER_USED, sourceStart, sourceEnd);
				}
					break;
				case IProblem.PotentialNullLocalVariableReference : {
					sev = getSourceSeverity(JSPCorePreferenceNames.VALIDATION_JAVA_POTENTIAL_NULL_LOCAL_VARIABLE_REFERENCE, sourceStart, sourceEnd);
				}
					break;
				case IProblem.UnusedImport : {
					sev = getSourceSeverity(JSPCorePreferenceNames.VALIDATION_JAVA_UNUSED_IMPORT, sourceStart, sourceEnd);
				}
					break;
				case IProblem.UnusedPrivateField:
				case IProblem.MissingSerialVersion : {
					// JSP files don't get serialized...right?
					sev = ValidationMessage.IGNORE;
				}
				break;

				default : {
					if (problem.isError()) {
						sev = IMessage.HIGH_SEVERITY;
					}
					else if (problem.isWarning()) {
						sev = IMessage.NORMAL_SEVERITY;
					}
					else {
						sev = IMessage.LOW_SEVERITY;
					}
				}
					if (sev == ValidationMessage.IGNORE) {
						return null;
					}

					/* problems without JSP positions are in generated code */
					if (sourceStart == -1) {
						int problemID = problem.getID();
						/*
						 * Quoting IProblem doc: "When a problem is tagged as
						 * Internal, it means that no change other than a
						 * local source code change can fix the corresponding
						 * problem." Assuming that our generated code is
						 * correct, that should reduce the reported problems
						 * to those the user can correct.
						 */
						if (((problemID & IProblem.Internal) != 0) && ((problemID & IProblem.Syntax) != 0) && translation instanceof JSPTranslation) {
							// Attach to the last code scripting section
							JSPTranslation jspTranslation = ((JSPTranslation) translation);
							Position[] jspPositions = (Position[]) jspTranslation.getJsp2JavaMap().keySet().toArray(new Position[jspTranslation.getJsp2JavaMap().size()]);
							for (int i = 0; i < jspPositions.length; i++) {
								sourceStart = Math.max(sourceStart, jspPositions[i].getOffset());
							}
							IMessage m = new LocalizedMessage(sev, problem.getMessage(), f);
							m.setOffset(sourceStart);
							m.setLength(1);
							return m;
						}
						else {
							return null;
						}
					}
			}
		}
		if (sev == ValidationMessage.IGNORE) {
			return null;
		}

		// line number for marker starts @ 1
		// line number from document starts @ 0
		int lineNo = structuredDoc.getLineOfOffset(sourceStart) + 1;

		IMessage m = new LocalizedMessage(sev, problem.getMessage(), f);

		m.setLineNo(lineNo);
		m.setOffset(sourceStart);
		m.setLength((sourceEnd >= sourceStart) ? (sourceEnd - sourceStart + 1) : 0);

		// need additional adjustment for problems from
		// indirect (included) files
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=119633
		if (translation.isIndirect(problem.getSourceStart())) {
			adjustIndirectPosition(m, translation);
		}

		return m;
	}

	/**
	 * Provides the severity for the given message key only when it's within the source range of the JSP (i.e., not boilerplate code).
	 * @param key the key to get the severity of
	 * @param start start within the JSP source
	 * @param end end wtihin the JSP source
	 * @return The message severity for the key if it is part of the JSP's source. IGNORE if it's boilerplate code.
	 */
	private int getSourceSeverity(String key, int start, int end) {
		return (start >= 0 && end >= 0 ) ? getMessageSeverity(key) : ValidationMessage.IGNORE;
	}

	int getMessageSeverity(String key) {
		int sev = fPreferencesService.getInt(PREFERENCE_NODE_QUALIFIER, key, IMessage.NORMAL_SEVERITY, fScopes);
		switch (sev) {
			case ValidationMessage.ERROR :
				return IMessage.HIGH_SEVERITY;
			case ValidationMessage.WARNING :
				return IMessage.NORMAL_SEVERITY;
			case ValidationMessage.INFORMATION :
				return IMessage.LOW_SEVERITY;
			case ValidationMessage.IGNORE :
				return ValidationMessage.IGNORE;
		}
		return IMessage.NORMAL_SEVERITY;
	}

	private void loadPreferences(IFile file) {
		fScopes = new IScopeContext[]{new InstanceScope(), new DefaultScope()};

		if (file != null && file.isAccessible()) {
			ProjectScope projectScope = new ProjectScope(file.getProject());
			if (projectScope.getNode(PREFERENCE_NODE_QUALIFIER).getBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, false)) {
				fScopes = new IScopeContext[]{projectScope, new InstanceScope(), new DefaultScope()};
			}
		}
	}

	void performValidation(IFile f, IReporter reporter, IStructuredModel model) {
		for (int i = 0; i < DEPEND_ONs.length; i++) {
			addDependsOn(f.getProject().getFile(DEPEND_ONs[i]));	
		}
		if (model instanceof IDOMModel) {
			IDOMModel domModel = (IDOMModel) model;
			ModelHandlerForJSP.ensureTranslationAdapterFactory(domModel);

			IDOMDocument xmlDoc = domModel.getDocument();
			JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
			IJSPTranslation translation = translationAdapter.getJSPTranslation();

			if (!reporter.isCancelled()) {
				loadPreferences(f);

				// only update task markers if the model is the same as what's on disk
				boolean updateJavaTasks = UPDATE_JAVA_TASKS && !domModel.isDirty() && f != null && f.isAccessible();
				if (updateJavaTasks) {
					// remove old Java task markers
					try {
						IMarker[] foundMarkers = f.findMarkers(JAVA_TASK_MARKER_TYPE, true, IResource.DEPTH_ONE);
						for (int i = 0; i < foundMarkers.length; i++) {
							foundMarkers[i].delete();
						}
					}
					catch (CoreException e) {
						Logger.logException(e);
					}
				}

				translation.setProblemCollectingActive(true);
				translation.reconcileCompilationUnit();
				List problems = translation.getProblems();
				// add new messages
				for (int i = 0; i < problems.size() && !reporter.isCancelled(); i++) {
					IProblem problem = (IProblem) problems.get(i);
					/*
					 * Possible error in problem collection; EL translation is
					 * extensible, so we must be paranoid about this.
					 */
					if (problem == null)
						continue;
					IMessage m = createMessageFromProblem(problem, f, translation, domModel.getStructuredDocument());
					if (m != null) {
						if (problem.getID() == IProblem.Task) {
							if (updateJavaTasks) {
								// add new Java task marker
								try {
									IMarker task = f.createMarker(JAVA_TASK_MARKER_TYPE);
									task.setAttribute(IMarker.LINE_NUMBER, new Integer(m.getLineNumber()));
									task.setAttribute(IMarker.CHAR_START, new Integer(m.getOffset()));
									task.setAttribute(IMarker.CHAR_END, new Integer(m.getOffset() + m.getLength()));
									task.setAttribute(IMarker.MESSAGE, m.getText());
									task.setAttribute(IMarker.USER_EDITABLE, Boolean.FALSE);

									switch (m.getSeverity()) {
										case IMessage.HIGH_SEVERITY: {
											task.setAttribute(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_HIGH));
											task.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
										}
											break;
										case IMessage.LOW_SEVERITY : {
											task.setAttribute(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_LOW));
											task.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
										}
											break;
										default : {
											task.setAttribute(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_NORMAL));
											task.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
										}
									}
								}
								catch (CoreException e) {
									Logger.logException(e);
								}
							}
						}
						else {
							reporter.addMessage(fMessageOriginator, m);
						}
					}
				}
			}
		}
		unloadPreferences();
	}

	private void unloadPreferences() {
		fScopes = null;
	}

	/**
	 * Validate one file. It's assumed that the file has JSP content type.
	 * 
	 * @param f
	 * @param reporter
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		if (DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " validating: " + f); //$NON-NLS-1$
		}

		IStructuredModel model = null;
		try {
			// get jsp model, get tranlsation
			model = StructuredModelManager.getModelManager().getModelForRead(f);
			if (!reporter.isCancelled() && model != null) {
				for (int i = 0; i < DEPEND_ONs.length; i++) {
					addDependsOn(f.getProject().getFile(DEPEND_ONs[i]));	
				}
				// get jsp model, get translation
				if (model instanceof IDOMModel) {
					reporter.removeAllMessages(fMessageOriginator, f);
					performValidation(f, reporter, model);
				}
			}
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Record that the currently validating resource depends on the given
	 * file. Only possible during batch (not source) validation.
	 * 
	 * @param file
	 */
	private void addDependsOn(IFile file) {
		if (fMessageOriginator instanceof JSPBatchValidator) {
			((JSPBatchValidator) fMessageOriginator).addDependsOn(file);
		}
	}

}
