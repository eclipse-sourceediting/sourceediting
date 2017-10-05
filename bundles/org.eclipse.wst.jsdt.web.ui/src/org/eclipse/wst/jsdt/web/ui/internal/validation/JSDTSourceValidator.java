/*******************************************************************************
 * Copyright (c) 2016, 2017 RedHat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  RedHat, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.validation;

import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.compiler.IProblem;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.wst.jsdt.internal.core.JavaModel;
import org.eclipse.wst.jsdt.internal.core.JavaModelManager;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;

/**
 * @author V.V. Rubezhny
 *
 */
public class JSDTSourceValidator extends AbstractValidator implements IValidatorJob, IExecutableExtension, ISourceValidator {
	private static final ASTParser parser = ASTParser.newParser(AST.JLS3);

	private String[] fAdditionalContentTypesIDs = null;
	private IDocument fDocument;
	
	static boolean shouldValidate(IFile file) {
		IProject project = file.getProject();
		JavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();
		JavaProject javaProject = (JavaProject) model.getJavaProject(project);
		if (!javaProject.exists() || !javaProject.isOnIncludepath(file))
			return false;

		IResource resource = file;
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible()
					|| (resource.getName().charAt(0) == '.' && resource.getType() == IResource.FOLDER)) {
				return false;
			}
			resource = resource.getParent();
		} while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
		// Nothing to do
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		if (helper == null)
			return;
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		String[] deltaArray = helper.getURIs();
		if (deltaArray != null && deltaArray.length > 0) {
			validateDelta(helper, reporter);
		} else {
			validateFull(helper, reporter);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		fAdditionalContentTypesIDs = new String[0];
		if (data instanceof String && data.toString().length() > 0) {
			fAdditionalContentTypesIDs = StringUtils.unpack(data.toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#validateInJob(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateInJob(IValidationContext helper, IReporter reporter) throws ValidationException {
		IStatus status = Status.OK_STATUS;
		validate(helper, reporter);
		return status;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator#connect(org.eclipse.jface.text.IDocument)
	 */
	public void connect(IDocument document) {
		fDocument = document;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator#disconnect(org.eclipse.jface.text.IDocument)
	 */
	public void disconnect(IDocument document) {
		fDocument = null;
	}

	/* (non-Javadoc)
	 * 
 	 * This validate call is for the ISourceValidator partial document validation approach
 	 * 
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator#validate(org.eclipse.jface.text.IRegion, org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		if (helper == null || fDocument == null)
			return;

		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}

		// TODO Do region validation here
	}

	/**
	 */
	private void validateContainer(IValidationContext helper, IReporter reporter, IContainer container) {
		try {
			IResource[] resourceArray = container.members(false);
			for (int i = 0; i < resourceArray.length; i++) {
				IResource resource = resourceArray[i];
				if (resource == null || reporter.isCancelled())
					continue;

				if (resource instanceof IFile) {
					Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, resource.getFullPath().toString()
							.substring(1));
					reporter.displaySubtask(this, message);
					validateFile(helper, reporter, (IFile) resource, null);
				} else if (resource instanceof IContainer) {
					validateContainer(helper, reporter, (IContainer) resource);
				}
			}
		}
		catch (CoreException ex) {
		}
	}
	
	/**
	 */
	private void validateDelta(IValidationContext helper, IReporter reporter) {
		String[] deltaArray = helper.getURIs();
		for (int i = 0; i < deltaArray.length; i++) {
			String delta = deltaArray[i];
			if (delta == null)
				continue;

			if (reporter != null) {
				Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, delta.substring(1));
				reporter.displaySubtask(this, message);
			}

			IResource resource = getResource(delta);
			if (resource == null || !(resource instanceof IFile))
				continue;
			validateFile(helper, reporter, (IFile) resource, null);
		}
	}

	/**
	 * @param result 
	 */
	private void validateFile(IValidationContext helper, IReporter reporter, IFile file, ValidationResult result) {
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		if (!shouldValidate(file)) {
			return;
		}

		char[] source = fDocument.get().toCharArray();
		if (source == null) {
			return;
		}
		
		parser.setSource(source);
		parser.setProject(JavaScriptCore.create(file.getProject()));
		JavaScriptUnit unit = (JavaScriptUnit) parser.createAST(new NullProgressMonitor());
		if (unit == null) {
			Message valMessage = new Message(JavaScriptUI.ID_PLUGIN, IMessage.HIGH_SEVERITY, JavaScriptUI.ID_PLUGIN + ".problem" ) { //$NON-NLS-1$
				/**
				 * @see IMessage#getText(Locale, ClassLoader)
				 */
				public java.lang.String getText(Locale locale, ClassLoader classLoader) {
					return "AST couldn't be created due to the fatal error"; //$NON-NLS-1$
				}

			};
			valMessage.setOffset(0);
			valMessage.setLength(0);
			valMessage.setLineNo(0);
			reporter.addMessage(this, valMessage);
			
		} else if(unit.getProblems().length > 0){
			for (IProblem problem : unit.getProblems()) {

				final String msg = problem.getMessage();
				String[] arguments = problem.getArguments();
				int severity = problem.isError() ? IMessage.HIGH_SEVERITY : IMessage.ERROR_AND_WARNING;
				int lineNumber = problem.getSourceLineNumber();
				int sourceStart = problem.getSourceStart();
				int sourceEnd = problem.getSourceEnd();
				int columnNumber = (problem instanceof DefaultProblem) ? ((DefaultProblem)problem).getSourceColumnNumber() : -1;
				
				Message valMessage = new Message(JavaScriptUI.ID_PLUGIN, severity, JavaScriptUI.ID_PLUGIN + ".problem" ) {
					/**
					 * @see IMessage#getText(Locale, ClassLoader)
					 */
					public java.lang.String getText(Locale locale, ClassLoader classLoader) {
						return msg;
					}

				};
				Position position = sourceEnd == -1 ? calcPosition(source, sourceStart) : new Position(sourceStart, sourceEnd - sourceStart);
				
				valMessage.setOffset(position.getOffset());
				valMessage.setLength(position.getLength());
				valMessage.setLineNo(lineNumber);
//				System.out.println(getClass().getName() + ": " + valMessage.getLineNumber() + 
//						"[" + valMessage.getOffset() + ":" + valMessage.getLength() + "] : " + 
//						valMessage.getText() + 
//						"==>>>" + String.copyValueOf(source, position.getOffset(), position.getLength()) + "<<<==");
				reporter.addMessage(this, valMessage);
			}
		}
	}

	private Position calcPosition(char[] source, int offset) {
		int start = offset > source.length ? source.length : offset;
		int end = start;

		// Search forward
		while (source.length > end && 
				(Character.isLetterOrDigit(source[end]) || source[end] == '_')) {
			end++;
		}
		if (end == start) {
			// search backward
			
			// skip end of statement && spaces
			boolean acceptSemiColon = true;
			while (end > 0 && 
					((acceptSemiColon && source[end - 1] == ';') ||
					Character.isWhitespace(source[end - 1]))) {
				if (source[end - 1] == ';') {
					acceptSemiColon = false;
				}
				end--;
			}
			start = end;
			while (start > 0 && 
					(Character.isLetterOrDigit(source[start - 1]) || source[start - 1] == '_')) {
				start--;
			}
		}
		
		return new Position(start, end - start);
	}

	/**
	 */
	private void validateFull(IValidationContext helper, IReporter reporter) {
		IProject project = null;
		String[] fileDelta = helper.getURIs();
		if (helper instanceof IWorkbenchContext) {
			IWorkbenchContext wbHelper = (IWorkbenchContext) helper;
			project = wbHelper.getProject();
		} else if (fileDelta.length > 0) {
			// won't work for project validation (b/c nothing in file delta)
			project = getResource(fileDelta[0]).getProject();
		}
		
		if (project == null)
			return;
		validateContainer(helper, reporter, project);
	}

	/*
	 * added to get rid or dependency on IWorkbenchHelper
	 */
	public IResource getResource(String delta) {
		Path path = new Path(delta);
		if (path.segmentCount() > 1)
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (path.segmentCount() == 1)
			return ResourcesPlugin.getWorkspace().getRoot().getProject(delta);
		return null;
	}
}
