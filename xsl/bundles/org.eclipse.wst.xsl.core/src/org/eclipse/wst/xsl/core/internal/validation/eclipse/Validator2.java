package org.eclipse.wst.xsl.core.internal.validation.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationMessage;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;
import org.eclipse.wst.xsl.core.model.StylesheetModel;

/**
 * Validator for both build validation (AbstractValidator) and as-you-type validation (IValidatorJob).
 * 
 * @author Doug
 */
public class Validator2 extends AbstractValidator implements IValidatorJob
{
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// BUILD VALIDATION METHODS
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void clean(IProject project, ValidationState state, IProgressMonitor monitor)
	{
		XSLCore.getInstance().clean(project, monitor);
		super.clean(project, state, monitor);
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor)
	{
		/*
		 * String s; switch(kind) { case IResourceDelta.ADDED: s = "added"; break; case IResourceDelta.CHANGED: s = "CHANGED"; break; case IResourceDelta.CONTENT: s = "CONTENT"; break; case
		 * IResourceDelta.REMOVED: s = "REMOVED"; break; default: s = "other"; } System.out.println(s);
		 */
		ValidationResult result = new ValidationResult();
		if (resource.getType() == IResource.FILE)
		{
			IFile file = (IFile)resource;
			ValidationReport report = doValidation(file, kind, state, monitor);
			StylesheetModel stylesheet = XSLCore.getInstance().getStylesheet(file);
			IFile[] dependencies = stylesheet.getFileDependencies().toArray(new IFile[0]);
			result.setDependsOn(dependencies);
			
			for (ValidationMessage message : report.getValidationMessages())
			{
				XSLValidationMessage xslMsg = (XSLValidationMessage)message;
				ValidatorMessage msg = ValidatorMessage.create(message.getMessage(), resource);
				msg.setAttribute("lineNumber", xslMsg.getLineNumber()); //$NON-NLS-1$
				msg.setAttribute("severity", xslMsg.getSeverity()); //$NON-NLS-1$
				result.add(msg);
			}
		}
		return result;
	}

	private ValidationReport doValidation(IFile file, int kind, ValidationState state, IProgressMonitor monitor)
	{
		ValidationReport valreport = null;
		try
		{
			valreport = XSLValidator.getInstance().validate(file,true);
		}
		catch (CoreException e)
		{
			XSLCorePlugin.log(e);
		}
		return valreport;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// AS YOU TYPE VALIDATION METHODS
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	public ISchedulingRule getSchedulingRule(IValidationContext helper)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter) throws ValidationException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void cleanup(IReporter reporter)
	{
		// TODO Auto-generated method stub

	}

	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException
	{
		validateInJob(helper,reporter);
	}
}
