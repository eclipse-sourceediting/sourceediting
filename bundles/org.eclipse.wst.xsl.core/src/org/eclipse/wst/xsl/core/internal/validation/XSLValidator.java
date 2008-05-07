/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xsl.core.ValidationPreferences;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.model.CallTemplate;
import org.eclipse.wst.xsl.core.internal.model.Include;
import org.eclipse.wst.xsl.core.internal.model.Parameter;
import org.eclipse.wst.xsl.core.internal.model.StylesheetModel;
import org.eclipse.wst.xsl.core.internal.model.Template;
import org.eclipse.wst.xsl.core.internal.model.XSLAttribute;
import org.eclipse.wst.xsl.core.internal.model.XSLElement;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;

/**
 * The XSL validator for workspace XSL files.
 * 
 * @author Doug Satchwell
 */
public class XSLValidator
{
	private static XSLValidator instance;

	private XSLValidator()
	{
	}

	/**
	 * Validate the given XSL file. Same as <code>validate(xslFile,report,forceBuild)</code> except a new report is created and returned.
	 * 
	 * @param xslFile the XSL file
	 * @param forceBuild true if build should always be forced
	 * @return the validation report
	 * @throws CoreException if any exception occurs while validating
	 */
	public ValidationReport validate(IFile xslFile, boolean forceBuild) throws CoreException
	{
		XSLValidationReport report = new XSLValidationReport(xslFile.getLocationURI().toString());
		validate(xslFile, report, forceBuild);
		return report;
	}

	/**
	 * Validate the given XSL file using the specified report.
	 * 
	 * @param xslFile the XSL file
	 * @param report the report to use for reporting validation errors
	 * @param forceBuild true if build should always be forced
	 * @return the validation report
	 * @throws CoreException if any exception occurs while validating
	 */
	public void validate(IFile xslFile, XSLValidationReport report, boolean forceBuild) throws CoreException
	{
		StylesheetModel stylesheet;
		if (forceBuild)
			stylesheet = XSLCore.getInstance().buildStylesheet(xslFile);
		else
			stylesheet = XSLCore.getInstance().getStylesheet(xslFile);

//		long start = System.currentTimeMillis();
		if (stylesheet!=null)
		{
			try
			{
				calculateProblems(stylesheet, report);
			}
			catch (MaxErrorsExceededException e)
			{
				// do nothing
			}
		}
//		long end = System.currentTimeMillis();
//		System.out.println("VALIDATE "+xslFile+" in "+(end-start)+"ms");
	}

	private void calculateProblems(StylesheetModel stylesheetComposed, XSLValidationReport report) throws MaxErrorsExceededException
	{
		// circular reference check
		checkCircularRef(stylesheetComposed, report);
		// include checks
		checkIncludes(stylesheetComposed, report);
		// template checks
		checkTemplates(stylesheetComposed, report);
		// call-template checks
		checkCallTemplates(stylesheetComposed, report);
		// call-template checks
		if (getPreference(ValidationPreferences.XPATHS) > IMarker.SEVERITY_INFO)
			checkXPaths(stylesheetComposed.getStylesheet(), report);
		
		// TODO a) check globals and b) apply-templates where mode does not exist
	}
	
	private int getPreference(String key)
	{
		return XSLCorePlugin.getDefault().getPluginPreferences().getInt(key);
	}

	private void checkXPaths(XSLElement xslEl, XSLValidationReport report) throws MaxErrorsExceededException
	{
		validateXPath(xslEl, report, "select"); //$NON-NLS-1$
		validateXPath(xslEl, report, "test"); //$NON-NLS-1$
		validateXPath(xslEl, report, "match"); //$NON-NLS-1$
		for (XSLElement childEl : xslEl.getChildElements())
		{
			checkXPaths(childEl, report);
		}
	}

	private void validateXPath(XSLElement xslEl, XSLValidationReport report, String attName) throws MaxErrorsExceededException
	{
		XSLAttribute att = xslEl.getAttribute(attName);
		if (att != null && att.getValue() != null)
		{
			try
			{
				XSLTXPathHelper.compile(att.getValue());
			}
			catch (XPathExpressionException e)
			{
				createMarker(report, att, getPreference(ValidationPreferences.XPATHS), Messages.XSLValidator_1);
			}
			catch (NullPointerException e)
			{
				// not sure why NPE is being thrown here
			}
		}
	}

	private void checkCircularRef(StylesheetModel stylesheetComposed, XSLValidationReport report) throws MaxErrorsExceededException
	{
		if (stylesheetComposed.hasCircularReference())
			createMarker(report, stylesheetComposed.getStylesheet(), getPreference(ValidationPreferences.CIRCULAR_REF), Messages.XSLValidator_2);
	}

	private void checkIncludes(StylesheetModel stylesheetComposed, XSLValidationReport report) throws MaxErrorsExceededException
	{		
		// includes
		for (Include include : stylesheetComposed.getStylesheet().getIncludes())
		{
			IFile includedFile = include.getHrefAsFile();
			if (includedFile == null || !includedFile.exists())
			{ // included file does not exist
				createMarker(report, include.getAttribute("href"), getPreference(ValidationPreferences.MISSING_INCLUDE), Messages.XSLValidator_4 + include.getHref()); //$NON-NLS-1$
			}
			else if (includedFile.equals(include.getStylesheet().getFile()))
			{ // stylesheet including itself!
				createMarker(report, include.getAttribute("href"), getPreference(ValidationPreferences.CIRCULAR_REF), Messages.XSLValidator_6); //$NON-NLS-1$
			}
		}
		//imports
		for (Include include : stylesheetComposed.getStylesheet().getImports())
		{
			IFile includedFile = include.getHrefAsFile();
			if (includedFile == null || !includedFile.exists())
			{ // included file does not exist
				createMarker(report, include.getAttribute("href"), getPreference(ValidationPreferences.MISSING_INCLUDE), Messages.XSLValidator_8 + include.getHref()); //$NON-NLS-1$
			}
			else if (includedFile.equals(include.getStylesheet().getFile()))
			{ // stylesheet including itself!
				createMarker(report, include.getAttribute("href"), getPreference(ValidationPreferences.CIRCULAR_REF), Messages.XSLValidator_10); //$NON-NLS-1$
			}
		}
	}

	private void checkTemplates(StylesheetModel stylesheetComposed, XSLValidationReport report) throws MaxErrorsExceededException
	{
		for (Template template : stylesheetComposed.getStylesheet().getTemplates())
		{
			// check attributes are correct
			if (template.getName() != null)
			{// named template
//				if (template.getMatch() != null)
//					createMarker(report, template, IMarker.SEVERITY_ERROR, "Template cannot specify both name and match attributes");
//				if (template.getMode() != null)
//					createMarker(report, template, IMarker.SEVERITY_ERROR, "Named templates cannot specify a mode");
				checkParameters(report, template);
			} 

			for (Template checkTemplate : stylesheetComposed.getTemplates())
			{
				if (checkTemplate != template && checkTemplate.equals(template))
				{
					if (template.getStylesheet() == stylesheetComposed.getStylesheet() && checkTemplate.getStylesheet() == stylesheetComposed.getStylesheet())
					{// templates in this stylesheet conflict with each other
						createMarker(report, template, getPreference(ValidationPreferences.TEMPLATE_CONFLICT), Messages.XSLValidator_11);
					}
					else if (template.getStylesheet() == stylesheetComposed.getStylesheet())
					{// template in included stylesheet conflicts with this
						createMarker(report, template, getPreference(ValidationPreferences.TEMPLATE_CONFLICT), Messages.XSLValidator_12);
					}
					else
					{// templates in included stylesheets conflict with each other
						createMarker(report, template.getStylesheet(), getPreference(ValidationPreferences.TEMPLATE_CONFLICT), Messages.XSLValidator_13);
					}
				}
			}
		}
	}

	private void checkParameters(XSLValidationReport report, Template template) throws MaxErrorsExceededException
	{
		List<Parameter> parameters = new ArrayList<Parameter>(template.getParameters());
		// reverse the parameters order for checking - for duplicate parameters
		// the first one is valid
		Collections.reverse(parameters);
		Set<Parameter> duplicateParameters = new HashSet<Parameter>();
		// check parameters
		for (Parameter param : parameters)
		{
			if (param.getName() == null)
			{// name is required
				createMarker(report, param, getPreference(ValidationPreferences.NAME_ATTRIBUTE_MISSING), Messages.XSLValidator_14);
			}
			else if (param.getName().trim().length() == 0)
			{// name value is required
				createMarker(report, param, getPreference(ValidationPreferences.NAME_ATTRIBUTE_EMPTY), Messages.XSLValidator_15);
			}
			else if (duplicateParameters.contains(param))
			{// don't recheck the parameter
				continue;
			}
			else
			{// check a parameter with the same name does not exist
				for (Parameter checkParam : parameters)
				{
					if (param != checkParam)
					{
						if (param.getName().equals(checkParam.getName()))
						{
							duplicateParameters.add(checkParam);
							createMarker(report, param, getPreference(ValidationPreferences.DUPLICATE_PARAMETER), Messages.XSLValidator_16);
						}
					}
				}
			}
		}
	}

	private void checkCallTemplates(StylesheetModel stylesheetComposed, XSLValidationReport report) throws MaxErrorsExceededException
	{
		for (CallTemplate calledTemplate : stylesheetComposed.getStylesheet().getCalledTemplates())
		{
			// get the list of templates that might be being called by this
			// template call
			List<Template> templateList = stylesheetComposed.getTemplatesByName(calledTemplate.getName());
			if (templateList.size() == 0)
			{
				createMarker(report, calledTemplate.getAttribute("name"), getPreference(ValidationPreferences.CALL_TEMPLATES), MessageFormat.format(Messages.XSLValidator_18, calledTemplate.getName())); //$NON-NLS-1$
			}
			else
			{
				Template namedTemplate = templateList.get(0);
				for (Parameter calledTemplateParam : calledTemplate.getParameters())
				{
					boolean found = false;
					for (Parameter namedTemplateParam : namedTemplate.getParameters())
					{
						if (calledTemplateParam.getName().equals(namedTemplateParam.getName()))
						{
							found = true;
							if (!namedTemplateParam.isValue() && !calledTemplateParam.isValue())
								createMarker(report, calledTemplateParam, getPreference(ValidationPreferences.EMPTY_PARAM), MessageFormat.format(Messages.XSLValidator_20, calledTemplateParam.getName()));
							break;
						}
					}
					if (!found)
						createMarker(report, calledTemplateParam.getAttribute("name"), getPreference(ValidationPreferences.MISSING_PARAM), MessageFormat.format(Messages.XSLValidator_22, calledTemplateParam.getName())); //$NON-NLS-1$
				}
				if (getPreference(ValidationPreferences.MISSING_PARAM) > IMarker.SEVERITY_INFO)
				{
					for (Parameter namedTemplateParam : namedTemplate.getParameters())
					{
						if (!namedTemplateParam.isValue())
						{
							boolean found = false;
							for (Parameter calledTemplateParam : calledTemplate.getParameters())
							{
								if (calledTemplateParam.getName().equals(namedTemplateParam.getName()))
								{
									found = true;
									break;
								}
							}
							if (!found)
								createMarker(report, calledTemplate, getPreference(ValidationPreferences.MISSING_PARAM), MessageFormat.format(Messages.XSLValidator_3, namedTemplateParam.getName()));
						}
					}
				}
			} 
		}
	}

	private void createMarker(XSLValidationReport report, XSLNode xslNode, int severity, String message) throws MaxErrorsExceededException
	{
		if (severity > IMarker.SEVERITY_INFO)
		{
			if (report.getErrors().size() + report.getWarnings().size() > getPreference(ValidationPreferences.MAX_ERRORS))
				throw new MaxErrorsExceededException();
			switch (severity)
			{
				case IMarker.SEVERITY_ERROR:
					report.addError(xslNode, message);
					break;
				case IMarker.SEVERITY_WARNING:
					report.addWarning(xslNode, message);
					break;
			}
		}
	}

	/**
	 * Get the singleton XSLValidator instance.
	 * 
	 * @return the singleson XSLValidator instance
	 */
	public static XSLValidator getInstance()
	{
		if (instance == null)
			instance = new XSLValidator();
		return instance;
	}
}
