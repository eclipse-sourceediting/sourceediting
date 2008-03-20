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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;

/**
 * TODO: Add Javadoc
 * 
 * @author Doug Satchwell
 * 
 */
public class Stylesheet
{
	private static final int INCLUDE = 1;
	private static final int IMPORT = 2;

	final IFile file;
	final List<Include> includes = new ArrayList<Include>();
	final List<Template> namedTemplates = new ArrayList<Template>();
	final List<Template> calledTemplates = new ArrayList<Template>();
	final List<Variable> globalVariables = new ArrayList<Variable>();

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param file
	 */
	public Stylesheet(IFile file)
	{
		this.file = file;
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param include
	 */
	public void addInclude(Include include)
	{
		includes.add(include);
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param include
	 */
	public void addImport(Include include)
	{
		includes.add(include);
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param template
	 */
	public void addNamedTemplate(Template template)
	{
		namedTemplates.add(template);
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param template
	 */
	public void addCalledTemplate(Template template)
	{
		calledTemplates.add(template);
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @return
	 */
	public IFile getFile()
	{
		return file;
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @return
	 */
	public List<Include> getIncludes()
	{
		return includes;
	}

	/**
	 * @return
	 */
	public List<Template> getNamedTemplates()
	{
		return namedTemplates;
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @return
	 */
	public List<Template> getCalledTemplates()
	{
		return calledTemplates;
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @return
	 */
	public Map<String, List<Template>> calculateTemplates()
	{
		Map<String, List<Template>> templateMap = new HashMap<String, List<Template>>();
		Set<Stylesheet> files = new HashSet<Stylesheet>();
		calculateTemplates(files,templateMap, INCLUDE);
		return templateMap;
	}

	private void calculateTemplates(Set<Stylesheet> stylesheets, Map<String, List<Template>> templateMap, int type)
	{
		if (type == INCLUDE)
		{// add all named templates
			for (Template template : namedTemplates)
			{
				List<Template> list = templateMap.get(template.getName());
				if (list == null)
				{
					list = new ArrayList<Template>();
					templateMap.put(template.getName(), list);
				}
				list.add(template);
			}
		}
		else if (type == IMPORT)
		{
			for (Template template : namedTemplates)
			{// only add if not over-ridden
				if (!templateMap.containsKey(template.getName()))
				{
					List<Template> list = new ArrayList<Template>();
					list.add(template);
					templateMap.put(template.getName(), list);
				}
			}
		}
		for (Include include : includes)
		{
			// for includes, just add all templates
			Stylesheet stylesheet = include.findIncludedStylesheet();
			if (stylesheet != null)
			{
				// TODO may need to be more intelligent for imports?
				// ensure we don't use the same stylesheet more than once (prevents circular reference problems)
				if (!stylesheets.contains(stylesheet))
				{
					stylesheets.add(stylesheet);
					stylesheet.calculateTemplates(stylesheets, templateMap, include.getNodeType());
				}
			}
		}
	}

	public void addGlobalVariable(Variable var)
	{
		globalVariables.add(var);
	}

	@Override
	public String toString()
	{
		return file.getProjectRelativePath().toString();
	}
}
