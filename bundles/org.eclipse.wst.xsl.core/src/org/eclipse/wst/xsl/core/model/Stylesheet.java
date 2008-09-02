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
package org.eclipse.wst.xsl.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsl.core.model.XSLModelObject.Type;

/**
 * The <code>xsl:stylesheet</code> model element.
 * 
 * @author Doug Satchwell
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
	 * Create an instance of this.
	 * 
	 * @param file the file that this represents
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
	 * Add an <code>Include</code> to this.
	 * 
	 * @param include the include to add
	 */
	public void addInclude(Include include)
	{
		includes.add(include);
	}

	/**
	 * Add an <code>Import</code> to this.
	 * 
	 * @param include the import to add
	 */
	public void addImport(Import include)
	{
		imports.add(include);
	}

	/**
	 * Add a <code>Template</code> to this.
	 * 
	 * @param template the template to add
	 */
	public void addTemplate(Template template)
	{
		templates.add(template);
	}

	/**
	 * Add a <code>CallTemplate</code> to this.
	 * 
	 * @param template the template to add
	 */
	public void addCalledTemplate(CallTemplate template)
	{
		calledTemplates.add(template);
	}

	/**
	 * Get the file that this represents.
	 * 
	 * @return the file that this represents
	 */
	public IFile getFile()
	{
		return file;
	}

	/**
	 * Get the list of includes for this.
	 * 
	 * @return the list of includes
	 */
	public List<Include> getIncludes()
	{
		return includes;
	}
	
	/**
	 * Get the list of imports for this.
	 * 
	 * @return the list of imports
	 */
	public List<Import> getImports()
	{
		return imports;
	}

	/**
	 * Get the list of templates for this.
	 * 
	 * @return the list of templates
	 */
	public List<Template> getTemplates()
	{
		return templates;
	}

	/**
	 * Get the list of called templates for this.
	 * 
	 * @return the list of called templates
	 */
	public List<CallTemplate> getCalledTemplates()
	{
		return calledTemplates;
	}

	/**
	 * Add a global variable to this.
	 * 
	 * @param var the variable to add
	 */
	public void addGlobalVariable(Variable var)
	{
		globalVariables.add(var);
	}
	
	public Type getModelType()
	{
		return Type.STYLESHEET;
	}

}
