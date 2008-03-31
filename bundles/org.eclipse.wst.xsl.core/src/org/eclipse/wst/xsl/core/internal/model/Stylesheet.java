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
import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * TODO: Add Javadoc
 * 
 * @author Doug Satchwell
 * 
 */
public class Stylesheet extends XSLElement
{
	final IFile file;
	final List<Include> includes = new ArrayList<Include>();
	final List<Import> imports = new ArrayList<Import>();
	final List<Template> templates = new ArrayList<Template>();
	final List<CallTemplate> calledTemplates = new ArrayList<CallTemplate>();
	final List<Variable> globalVariables = new ArrayList<Variable>();
	final List<XSLElement> elements = new ArrayList<XSLElement>();

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param file
	 */
	public Stylesheet(IFile file)
	{
		super(null);
		this.file = file;
	}
	
	@Override
	public Stylesheet getStylesheet()
	{
		return this;
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
	public void addImport(Import include)
	{
		imports.add(include);
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param template
	 */
	public void addTemplate(Template template)
	{
		templates.add(template);
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param template
	 */
	public void addCalledTemplate(CallTemplate template)
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
	
	public List<Import> getImports()
	{
		return imports;
	}

	/**
	 * @return
	 */
	public List<Template> getTemplates()
	{
		return templates;
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @return
	 */
	public List<CallTemplate> getCalledTemplates()
	{
		return calledTemplates;
	}


	/**
	 * TODO: Add Javadoc
	 * @param var
	 */
	public void addGlobalVariable(Variable var)
	{
		globalVariables.add(var);
	}
}
