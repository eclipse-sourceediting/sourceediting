/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - STAR - bug 224777 - fix spaces in path names in URIs.
 *                           bug 226578 - remove extraneous @override statements
 *                                        this was causing compilation problems
 *                                        
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.validation.eclipse;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.uriresolver.internal.util.URIEncoder;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationMessage;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.eclipse.wst.xsl.core.model.XSLNode;

/**
 * The XSL validator extends the XML <code>AbstractNestedValidator</code>.
 * 
 * @author Doug Satchwell
 */
public class Validator extends AbstractNestedValidator
{
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// BUILD VALIDATION METHODS
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	private boolean asYouTypeValidation;

	@Override
	public void clean(IProject project, ValidationState state, IProgressMonitor monitor)
	{
		super.clean(project, state, monitor);
		XSLCore.getInstance().clean(project,monitor);
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor)
	{
		ValidationResult res = super.validate(resource, kind, state, monitor);
		if (resource.getType() == IResource.FILE)
		{
			StylesheetModel stylesheet = XSLCore.getInstance().getStylesheet((IFile) resource);
			IFile[] dependencies = stylesheet.getFileDependencies().toArray(new IFile[0]);
			res.setDependsOn(dependencies);
		}
		return res;
	} 

	
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// AS YOU TYPE VALIDATION METHODS
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public IStatus validateInJob(IValidationContext context, IReporter reporter) throws ValidationException
	{
		asYouTypeValidation = true;
		return super.validateInJob(context, reporter);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// COMMON METHODS
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public ValidationReport validate(final String uri, InputStream inputstream, NestedValidatorContext context)
	{
		ValidationReport valreport = new XSLValidationReport(uri);
		
		try
		{
			String encUri = URIEncoder.encode(uri);
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new URI(encUri));
			if (files.length > 0)
			{
				IFile xslFile = files[0];
				valreport = XSLValidator.getInstance().validate(xslFile,asYouTypeValidation);
			}
		}
		catch (URISyntaxException e)
		{
			XSLCorePlugin.log(e);
		}
		catch (CoreException e)
		{
			XSLCorePlugin.log(e);
		}
		return valreport;
	}

	@Override
	protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message)
	{
		XSLValidationMessage msg = (XSLValidationMessage) validationMessage;
		XSLNode node = msg.getNode();
		// set this here as it gets set to the wrong value by the superclass
		message.setSeverity(msg.getRealSeverity());
		// constants are defined in org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator
		if (node.getNodeType() == XSLNode.ATTRIBUTE_NODE)
		{
			message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");  //$NON-NLS-1$//$NON-NLS-2$
			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, Integer.valueOf(validationMessage.getColumnNumber()));
			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "ATTRIBUTE_VALUE"); // whether to squiggle the element, attribute or text //$NON-NLS-1$
			message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, ((XSLAttribute) node).getName());
		}
		else if (node.getNodeType() == XSLNode.ELEMENT_NODE)
		{
			message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");  //$NON-NLS-1$//$NON-NLS-2$
			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, Integer.valueOf(validationMessage.getColumnNumber()));
			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "START_TAG"); // whether to squiggle the element, attribute or text //$NON-NLS-1$
		}
	}
}
