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
package org.eclipse.wst.xsl.core.internal.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsl.core.XSLCore;

/**
 * The composed stylesheet, consisting of all templates and variables available 
 * via imports and includes.  
 * 
 * Note that this model may not be valid - for instance there may be more than one named template for a given name
 * or more than one global variable with a given name.
 * 
 * @author Doug Satchwell
 */
public class StylesheetModel
{
	private final Stylesheet stylesheet;
	boolean circularReference;
	final Set<IFile> files = new HashSet<IFile>();
	final Set<Stylesheet> stylesheets = new HashSet<Stylesheet>();
	final Set<Template> templateSet = new HashSet<Template>();
	final List<Template> templates = new ArrayList<Template>();
	final List<Variable> globalVariables = new ArrayList<Variable>();
	
	/**
	 * @param stylesheet
	 */
	public StylesheetModel(Stylesheet stylesheet)
	{
		this.stylesheet = stylesheet;
	}

	/**
	 * Get all stylesheets in the hierarchy (including the current stylesheet)
	 * @return
	 */
	public Set<Stylesheet> getIncludedStylesheets()
	{
		return stylesheets;
	}
	
	public Set<IFile> getFileDependencies()
	{
		return files;
	}
	
	/**
	 * Get the stylesheet that this is the model for.
	 * 
	 * @return
	 */
	public Stylesheet getStylesheet()
	{
		return this.stylesheet;
	}
	
	public List<Variable> getGlobalVariables()
	{
		return globalVariables;
	}
	
	/**
	 * Get all available templates from the current stylesheet or via includes
	 * @return
	 */
	public List<Template> getTemplates()
	{
		return templates;
	}
	
	public List<Template> getTemplatesByName(String name)
	{
		List<Template> matching = new ArrayList<Template>(templates.size());
		for (Template template : templates)
		{
			if (name.equals(template.getName()))
				matching.add(template);
		}
		return matching;
	}	
	
	public List<Template> findMatching(Template toMatch)
	{
		List<Template> matching = new ArrayList<Template>(templates.size());
		for (Template template : templates)
		{
			if (template.equals(toMatch))
				matching.add(template);
		}
		return matching;
	}
	
	public boolean hasCircularReference()
	{
		return circularReference;
	}
	
	public void fix()
	{
//		System.out.println("Fixing "+stylesheet.getFile()+"...");
		long start = System.currentTimeMillis();
		templates.addAll(stylesheet.templates);
		templateSet.addAll(stylesheet.templates);
		for (Include inc : stylesheet.includes)
		{
			handleInclude(inc);
		}
		for (Include inc : stylesheet.imports)
		{
			handleInclude(inc);
		}
		long end = System.currentTimeMillis();
		System.out.println("FIX "+stylesheet.getFile()+" in "+(end-start)+"ms");
	}
	
	private void handleInclude(Include include)
	{
		IFile file = include.getHrefAsFile();
		
		String type;
		if (include.getIncludeType() == Include.INCLUDE)
			type = " INCLUDE ";
		else
			type = " IMPORT  ";
//		System.out.println(include.getStylesheet().getFile()+type+file);
		
		if (file == null || !file.exists())
		{
			return;
		}
		else if (stylesheet.getFile().equals(file) || files.contains(file))
		{
			circularReference = true;
			return;				
		}
		files.add(file);
		
		StylesheetModel includedModel = XSLCore.getInstance().getStylesheet(file);
		if (includedModel == null)
			return;
		stylesheets.add(includedModel.getStylesheet());
		
		if (include.getIncludeType() == Include.INCLUDE)
		{
			templates.addAll(includedModel.templates);
			templateSet.addAll(includedModel.templates);
		}
		else
		{
			for (Template includedTemplate : includedModel.templates)
			{
				if (!templateSet.contains(includedTemplate))
				{
					templates.add(includedTemplate);
					templateSet.add(includedTemplate);
				}
			}
		}
	}
}