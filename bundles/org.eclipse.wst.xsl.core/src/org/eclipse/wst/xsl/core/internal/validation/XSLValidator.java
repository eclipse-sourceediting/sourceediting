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
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.model.Include;
import org.eclipse.wst.xsl.core.internal.model.Parameter;
import org.eclipse.wst.xsl.core.internal.model.SourceArtifact;
import org.eclipse.wst.xsl.core.internal.model.SourceFile;
import org.eclipse.wst.xsl.core.internal.model.Template;

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
		SourceFile sourceFile = XSLCore.getInstance().buildSourceFile(xslFile);
		ValidationInfo valinfo = new ValidationInfo(uri);
		calculateProblems(sourceFile,valinfo);
		return valinfo;
	}

	private void calculateProblems(SourceFile sf, ValidationInfo valinfo) throws CoreException
	{
		// TODO these need to be real preferences
		int REPORT_EMPTY_PARAM_PREF = IMarker.SEVERITY_WARNING;
		int REPORT_MISSING_PARAM_PREF = IMarker.SEVERITY_ERROR;

		Map<String,List<Template>> templateMap = sf.calculateTemplates();
		
		// check for includes that don't exist
		for (Include include : sf.getIncludes()) {
			SourceFile includedSource = include.findIncludedSourceFile();
			if (includedSource == null)
			{
				createMarker(valinfo,include,IMarker.SEVERITY_ERROR,"Missing include: "+include.getHref());
			}
		}
		
		// check for missing called templates
		for (Template calledTemplate : sf.getCalledTemplates())
		{
			List<Template> templateList = templateMap.get(calledTemplate.getName());
			if (templateList == null)
			{
				createMarker(valinfo,calledTemplate,IMarker.SEVERITY_ERROR,"Missing template: "+calledTemplate.getName());
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
								createMarker(valinfo,calledTemplateParam,REPORT_EMPTY_PARAM_PREF,"Parameter "+calledTemplateParam.getName()+" does not have default value");
							break;
						}
					}
					if (!found)
						createMarker(valinfo,calledTemplateParam,IMarker.SEVERITY_ERROR,"Parameter "+calledTemplateParam.getName()+" does not exist");
				}
				if (REPORT_MISSING_PARAM_PREF > IMarker.SEVERITY_INFO)
				{
					for (Parameter namedTemplateParam : namedTemplate.getParameters())
					{
						System.out.println("named:"+namedTemplateParam);
						
						if (!namedTemplateParam.isValue())
						{
							boolean found = false;
							for (Parameter calledTemplateParam : calledTemplate.getParameters())
							{
								System.out.println("called:"+calledTemplateParam);
								if (calledTemplateParam.getName().equals(namedTemplateParam.getName()))
								{
									found = true;
									break;
								}
							}
							if (!found)
								createMarker(valinfo,calledTemplate,REPORT_MISSING_PARAM_PREF,"Missing parameter: "+namedTemplateParam.getName());
						}
					}
				}
			}
		}
		// check for duplicate templates
		Set<Include> includesWithConflictingTemplates = new HashSet<Include>();
		for (Map.Entry<String, List<Template>> entry : templateMap.entrySet())
		{
			List<Template> templateList = entry.getValue();
			//System.out.println("------------------------------------");
			//System.out.println(entry.getKey());
			//System.out.println("------------------------------------");
			//for (Template template : templateList)
			//{
			//	System.out.println(template.getParentSourceFile().getFile());
			//}
			if(templateList.size() > 1)
			{ // more than one template with the same name exists
				for (Template template : templateList)
				{
					if (template.getSourceFile().equals(sf))
					{// duplicate template exists in this source file
						createMarker(valinfo,template,IMarker.SEVERITY_ERROR,"Duplicate template: "+template.getName());
					}
					else
					{// duplicate template exists in imported templates
/*						for (Include include : sf.getIncludes())
						{
							if (recurse(include.getSourceFile().getIncludes(),template))
							{
								includesWithConflictingTemplates.add(include);
							}
						}
*/					}
				}
			}
		}
		for (Include include : includesWithConflictingTemplates)
		{
			createMarker(valinfo,include,IMarker.SEVERITY_ERROR,"Conflicting includes: "+include.getSourceFile().getFile());
		}
	}
	
	private void createMarker(ValidationInfo valinfo, SourceArtifact sourceArt, int severity, String message) {
		int line = sourceArt.getLineNumber()+1;
		int col = sourceArt.getColumnNumber()+1;
		String uri = sourceArt.getSourceFile().getFile().getLocationURI().toString();
		switch(severity)
		{
			case IMarker.SEVERITY_ERROR:
				valinfo.addError(message, line, col, uri);
			case IMarker.SEVERITY_WARNING:
				valinfo.addWarning(message, line, col, uri);
		}
	}

	/*	private boolean recurse(List<Include> includes, Template template) throws CoreException
	{
		boolean found = false;
		for (Include include : includes)
		{
			if (include.getParentSourceFile().getNamedTemplates().contains(template))
			{
				found = true;
			}
			else
			{
				found = recurse(include.getSourceFile().getIncludes(),template);
			}
			if (found)
				break;
		}
		return found;
	}	
*/
	
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
