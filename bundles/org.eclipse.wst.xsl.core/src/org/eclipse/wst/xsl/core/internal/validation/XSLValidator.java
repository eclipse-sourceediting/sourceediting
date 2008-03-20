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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.model.Include;
import org.eclipse.wst.xsl.core.internal.model.Parameter;
import org.eclipse.wst.xsl.core.internal.model.Stylesheet;
import org.eclipse.wst.xsl.core.internal.model.Template;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public class XSLValidator {
	private static XSLValidator instance;

	private XSLValidator() {
	}

	/**
	 * TODO: Add Javadoc
	 * @param uri
	 * @param xslFile
	 * @return
	 * @throws CoreException
	 */
	public ValidationReport validate(String uri, IFile xslFile) throws CoreException {
		Stylesheet stylesheet = XSLCore.getInstance().buildStylesheet(xslFile);
		XSLValidationReport report = new XSLValidationReport(stylesheet);
		calculateProblems(stylesheet,report);
		return report;
	}

	private void calculateProblems(Stylesheet sf, XSLValidationReport report) throws CoreException
	{
		// TODO these need to be real preferences
		int REPORT_EMPTY_PARAM_PREF = IMarker.SEVERITY_WARNING;
		int REPORT_MISSING_PARAM_PREF = IMarker.SEVERITY_ERROR;

		Map<String,List<Template>> templateMap = sf.calculateTemplates();
		
		// includes
		boolean circularReference = false;
		Set<IFile> files = new HashSet<IFile>();
		for (Include include : sf.getIncludes()) {
			Stylesheet includedSource = include.findIncludedStylesheet();
			if (includedSource == null)
			{ // included file does not exist
				createMarker(report,include.getAttribute("href"),IMarker.SEVERITY_ERROR,"Missing include: "+include.getHref());
			}
			else if (includedSource == include.getStylesheet())
			{ // stylesheet including itself!
				createMarker(report,include.getAttribute("href"),IMarker.SEVERITY_ERROR,"A stylesheet must not include itself");
				circularReference = true;
			}
			else
			{
				IFile file = includedSource.getFile();
				if (file != null)
				{
					if (files.contains(file))
					{// same file included more than once
						createMarker(report,include.getAttribute("href"),IMarker.SEVERITY_ERROR,"Stylesheet included multiple times: "+include.getHref());
					}
					else
						files.add(file);
				}
			}
		}
		
		// if we have a circular reference, it may be dangerous to continue (most likely the below code will go into an infinite loop)
		if (circularReference)
			return;
		
		// check for missing called templates
		for (Template calledTemplate : sf.getCalledTemplates())
		{
			List<Template> templateList = templateMap.get(calledTemplate.getName());
			if (templateList == null)
			{
				createMarker(report,calledTemplate,IMarker.SEVERITY_ERROR,"Missing template: "+calledTemplate.getName());
			}
			else if (templateList.size() == 1)
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
								createMarker(report,calledTemplateParam,REPORT_EMPTY_PARAM_PREF,"Parameter "+calledTemplateParam.getName()+" does not have default value");
							break;
						}
					}
					if (!found)
						createMarker(report,calledTemplateParam,IMarker.SEVERITY_ERROR,"Parameter "+calledTemplateParam.getName()+" does not exist");
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
								createMarker(report,calledTemplate,REPORT_MISSING_PARAM_PREF,"Missing parameter: "+namedTemplateParam.getName());
						}
					}
				}
			}
		}
		
		// check for duplicate templates
		final Set<Include> includesWithConflictingTemplates = new HashSet<Include>();
		for (Map.Entry<String, List<Template>> entry : templateMap.entrySet())
		{
			List<Template> templateList = entry.getValue();
			if(templateList.size() > 1)
			{ // more than one template with the same name exists
				for (final Template template : templateList)
				{
					if (template.getStylesheet().equals(sf))
					{// duplicate template exists in this source file
						createMarker(report,template,IMarker.SEVERITY_ERROR,"Duplicate template: "+template.getName());
					}
					else
					{// duplicate template exists in imported templates
						for (final Include include : sf.getIncludes())
						{
							// include.findIncludedSourceFile();
//							include.getSourceFile().accept(new IStylesheetVisitor(){
//
//								@Override
//								public boolean visit(SourceFile stylesheet)
//								{
//									List<Template> templates = stylesheet.getNamedTemplates();
//									for (Template importedTemplate : templates)
//									{
//										if (importedTemplate.conflictsWith(template))
//										{
//											includesWithConflictingTemplates.add(include);
//										}
//									}
//									return true;
//								}		
//							});
						}
					}
				}
			}
		}
		for (Include include : includesWithConflictingTemplates)
		{
			createMarker(report,include,IMarker.SEVERITY_ERROR,"Conflicting includes: "+include.getStylesheet().getFile());
		}
	}

//	private boolean recurse(List<Include> includes, Template template) throws CoreException
//	{
//		boolean found = false;
//		for (Include include : includes)
//		{
//			if (include.getSourceFile().getNamedTemplates().contains(template))
//			{
//				found = true;
//			}
//			else
//			{
//				found = recurse(include.getSourceFile().getIncludes(),template);
//			}
//			if (found)
//				break;
//		}
//		return found;
//	}	
	
	private void createMarker(XSLValidationReport report, XSLNode xslNode, int severity, String message) {
		switch(severity)
		{
			case IMarker.SEVERITY_ERROR:
				report.addError(xslNode,message);
				break;
			case IMarker.SEVERITY_WARNING:
				report.addWarning(xslNode,message);
				break;
		}
	}
	
	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public static XSLValidator getInstance() {
		if (instance == null)
			instance = new XSLValidator();
		return instance;
	}
}
