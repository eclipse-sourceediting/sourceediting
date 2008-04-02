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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.model.CallTemplate;
import org.eclipse.wst.xsl.core.internal.model.Include;
import org.eclipse.wst.xsl.core.internal.model.Parameter;
import org.eclipse.wst.xsl.core.internal.model.StylesheetModel;
import org.eclipse.wst.xsl.core.internal.model.Template;
import org.eclipse.wst.xsl.core.internal.model.XSLAttribute;
import org.eclipse.wst.xsl.core.internal.model.XSLElement;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;

/**
 * TODO: Add Javadoc
 * 
 * @author Doug Satchwell
 * 
 */
public class XSLValidator
{
	private static XSLValidator instance;
	private XPath xpath = XPathFactory.newInstance().newXPath();

	private XSLValidator()
	{
	}

	public ValidationReport validate(String uri, IFile xslFile) throws CoreException
	{
		long start = System.currentTimeMillis();
		StylesheetModel stylesheet = XSLCore.getInstance().buildStylesheet(xslFile);
		XSLValidationReport report = new XSLValidationReport(stylesheet.getStylesheet());
		calculateProblems(stylesheet, report);
		long end = System.currentTimeMillis();
		System.out.println("VALIDATE "+xslFile+" in "+(end-start)+"ms");
		return report;
	}

	private void calculateProblems(StylesheetModel stylesheetComposed, XSLValidationReport report) throws CoreException
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
		checkXPaths(stylesheetComposed.getStylesheet(), report);
		
		// TODO a) check globals and b) apply-templates where mode does not exist
	}

	private void checkXPaths(XSLElement xslEl, XSLValidationReport report)
	{
		validateXPath(xslEl, report, "select");
		validateXPath(xslEl, report, "test");
		validateXPath(xslEl, report, "match");
		for (XSLElement childEl : xslEl.getChildElements())
		{
			checkXPaths(childEl, report);
		}
	}

	private void validateXPath(XSLElement xslEl, XSLValidationReport report, String attName)
	{
		XSLAttribute att = xslEl.getAttribute(attName);
		if (att != null && att.getValue() != null)
		{
			try
			{
				xpath.compile(att.getValue());
			}
			catch (XPathExpressionException e)
			{
				createMarker(report, att, IMarker.SEVERITY_ERROR, "Xpath is invalid");
			}
			catch (NullPointerException e)
			{
				// not sure why NPE is being thrown here
			}
		}
	}

	private void checkCircularRef(StylesheetModel stylesheetComposed, XSLValidationReport report)
	{
		if (stylesheetComposed.hasCircularReference())
			createMarker(report, stylesheetComposed.getStylesheet(), IMarker.SEVERITY_ERROR, "Included stylesheets form a circular reference");
	}

	private void checkIncludes(StylesheetModel stylesheetComposed, XSLValidationReport report)
	{		
		// includes
		for (Include include : stylesheetComposed.getStylesheet().getIncludes())
		{
			IFile includedFile = include.getHrefAsFile();
			if (includedFile == null || !includedFile.exists())
			{ // included file does not exist
				createMarker(report, include.getAttribute("href"), IMarker.SEVERITY_ERROR, "Missing include: " + include.getHref());
			}
			else if (includedFile.equals(include.getStylesheet().getFile()))
			{ // stylesheet including itself!
				createMarker(report, include.getAttribute("href"), IMarker.SEVERITY_ERROR, "A stylesheet must not include itself");
			}
		}
		//imports
		for (Include include : stylesheetComposed.getStylesheet().getImports())
		{
			IFile includedFile = include.getHrefAsFile();
			if (includedFile == null || !includedFile.exists())
			{ // included file does not exist
				createMarker(report, include.getAttribute("href"), IMarker.SEVERITY_ERROR, "Missing import: " + include.getHref());
			}
			else if (includedFile.equals(include.getStylesheet().getFile()))
			{ // stylesheet including itself!
				createMarker(report, include.getAttribute("href"), IMarker.SEVERITY_ERROR, "A stylesheet must not import itself");
			}
		}
	}

	private void checkTemplates(StylesheetModel stylesheetComposed, XSLValidationReport report)
	{
		for (Template template : stylesheetComposed.getStylesheet().getTemplates())
		{
			// check attributes are correct
			if (template.getName() != null)
			{// named template
				if (template.getMatch() != null)
					createMarker(report, template, IMarker.SEVERITY_ERROR, "Template cannot specify both name and match attributes");
				if (template.getMode() != null)
					createMarker(report, template, IMarker.SEVERITY_ERROR, "Named templates cannot specify a mode");
				checkParameters(report, template);
			}

			for (Template checkTemplate : stylesheetComposed.getTemplates())
			{
				if (checkTemplate != template && checkTemplate.equals(template))
				{
					if (template.getStylesheet() == stylesheetComposed.getStylesheet() && checkTemplate.getStylesheet() == stylesheetComposed.getStylesheet())
					{// templates in this stylesheet conflict with each other
						createMarker(report, template, IMarker.SEVERITY_ERROR, "Template conflicts with another template in this stylesheet");
					}
					else if (template.getStylesheet() == stylesheetComposed.getStylesheet())
					{// template in included stylesheet conflicts with this
						createMarker(report, template, IMarker.SEVERITY_ERROR, "Template conflicts with an included template");
					}
					else
					{// templates in included stylesheets conflict with each other
						createMarker(report, template.getStylesheet(), IMarker.SEVERITY_ERROR, "Included templates conflict with each other");
					}
				}
			}
		}
	}

	private void checkParameters(XSLValidationReport report, Template template)
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
				createMarker(report, param, IMarker.SEVERITY_ERROR, "Name attribute is required");
			}
			else if (param.getName().trim().length() == 0)
			{// name value is required
				createMarker(report, param, IMarker.SEVERITY_ERROR, "Name must be specified");
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
							createMarker(report, param, IMarker.SEVERITY_ERROR, "Parameter already defined");
						}
					}
				}
			}
		}
	}

	private void checkCallTemplates(StylesheetModel stylesheetComposed, XSLValidationReport report)
	{
		// TODO these need to be real preferences
		int REPORT_EMPTY_PARAM_PREF = IMarker.SEVERITY_WARNING;
		int REPORT_MISSING_PARAM_PREF = IMarker.SEVERITY_ERROR;

		for (CallTemplate calledTemplate : stylesheetComposed.getStylesheet().getCalledTemplates())
		{
			// get the list of templates that might be being called by this
			// template call
			List<Template> templateList = stylesheetComposed.getTemplatesByName(calledTemplate.getName());
			if (templateList.size() == 0)
			{
				createMarker(report, calledTemplate.getAttribute("name"), IMarker.SEVERITY_ERROR, "Named template '" + calledTemplate.getName() + "' is not available");
			}
	/*		else
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
							if (REPORT_EMPTY_PARAM_PREF > IMarker.SEVERITY_INFO && !namedTemplateParam.isValue() && !calledTemplateParam.isValue())
								createMarker(report, calledTemplateParam, REPORT_EMPTY_PARAM_PREF, "Parameter " + calledTemplateParam.getName() + " does not have a default value");
							break;
						}
					}
					if (!found)
						createMarker(report, calledTemplateParam.getAttribute("name"), IMarker.SEVERITY_ERROR, "Parameter " + calledTemplateParam.getName() + " does not exist");
				}
				if (REPORT_MISSING_PARAM_PREF > IMarker.SEVERITY_INFO)
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
								createMarker(report, calledTemplate, REPORT_MISSING_PARAM_PREF, "Missing parameter: " + namedTemplateParam.getName());
						}
					}
				}
			} */
		}
	}

	private void createMarker(XSLValidationReport report, XSLNode xslNode, int severity, String message)
	{
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

	public static XSLValidator getInstance()
	{
		if (instance == null)
			instance = new XSLValidator();
		return instance;
	}
}
