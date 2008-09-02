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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.model.Include;
import org.eclipse.wst.xsl.core.internal.model.Stylesheet;
import org.eclipse.wst.xsl.core.internal.model.Template;
import org.eclipse.wst.xsl.core.internal.util.Debug;

/**
 * The composed stylesheet, consisting of all templates and variables available
 * via imports and includes.
 * 
 * <p>
 * The <code>fix()</code> method does the actual work of populating the fields
 * of this, so it must be called before calling any of the other methods.
 * </p>
 * 
 * <p>
 * Note that this model may not be valid - for instance there may be more than
 * one named template for a given name or more than one global variable with a
 * given name.
 * </p>
 * 
 * @author Doug Satchwell
 */
public class StylesheetModel extends XSLModelObject {
	private final Stylesheet stylesheet;
	boolean circularReference;
	final Set<IFile> files = new HashSet<IFile>();
	final Includes includeModel = new Includes();
	final Set<Template> templateSet = new HashSet<Template>();
	final Templates templateModel = new Templates();
	final GlobalVariables globalVariableModel = new GlobalVariables();

	/**
	 * Create a new instance of this.
	 * 
	 * @param stylesheet
	 *            the stylesheet that this is the model for
	 */
	public StylesheetModel(Stylesheet stylesheet) {
		this.stylesheet = stylesheet;
	}

	/**
	 * Get all stylesheets that are included in this stylesheet anywhere in the
	 * hierarchy via either import or include.
	 * 
	 * @return the set of stylesheets in the entire hierarchy
	 */
	public Includes getIncludes() {
		return includeModel;
	}

	/**
	 * Get all files that are included in this stylesheet anywhere in the
	 * hierarchy via either import or include.
	 * 
	 * @return the set of files in the entire hierarchy
	 */
	public Set<IFile> getFileDependencies() {
		return files;
	}

	/**
	 * Get the stylesheet that this is the model for.
	 * 
	 * @return the stylesheet that this is the model for
	 */
	public Stylesheet getStylesheet() {
		return this.stylesheet;
	}

	/**
	 * Get all global variables that are included in this stylesheet anywhere in
	 * the hierarchy via either import or include.
	 * 
	 * @return the set of files in the entire hierarchy
	 */
	public GlobalVariables getGlobalVariables() {
		return globalVariableModel;
	}

	/**
	 * Get all templates that are included in this stylesheet anywhere in the
	 * hierarchy via either import or include.
	 * 
	 * @return the set of templates in the entire hierarchy
	 */
	public Templates getTemplates() {
		return templateModel;
	}

	/**
	 * Get all named templates that are included in this stylesheet anywhere in
	 * the hierarchy via either import or include which have the given name.
	 * 
	 * @param name
	 *            the template name
	 * @return the set of named templates with the given name
	 */
	public List<Template> getTemplatesByName(String name) {
		List<Template> matching = new ArrayList<Template>(templateModel.getTemplates().size());
		for (Template template : templateModel.getTemplates()) {
			if (name.equals(template.getName()))
				matching.add(template);
		}
		return matching;
	}

	/**
	 * Get all templates that match the given template (determined from
	 * <code>Template.equals()</code>).
	 * 
	 * @param toMatch
	 *            the template to match
	 * @return the set of templates that match
	 */
	public List<Template> findMatching(Template toMatch) {
		List<Template> matching = new ArrayList<Template>(templateModel.getTemplates().size());
		for (Template template : templateModel.getTemplates()) {
			if (template.equals(toMatch))
				matching.add(template);
		}
		return matching;
	}

	/**
	 * Get whether this has a circular reference anywhere in its import/included
	 * hierarchy.
	 * 
	 * @return <code>true</code> if this has a circular reference
	 */
	public boolean hasCircularReference() {
		return circularReference;
	}

	/**
	 * Perform the process of traversing the hierarchy to determine all of the
	 * properties of this. Note that this method may force other
	 * <code>StylesheetModel</code>'s to be built during the process of fixing.
	 */
	public void fix() {
		long start = System.currentTimeMillis();

		if (Debug.debugXSLModel) {
			System.out.println("Fixing " + stylesheet.getFile() + "...");
		}
		templateModel.getTemplates().addAll(stylesheet.getTemplates());
		templateSet.addAll(stylesheet.getTemplates());
		for (Include inc : stylesheet.getIncludes()) {
			handleInclude(inc);
		}
		for (Include inc : stylesheet.getImports()) {
			handleInclude(inc);
		}
		if (Debug.debugXSLModel) {
			long end = System.currentTimeMillis();
			System.out.println("FIX " + stylesheet.getFile() + " in "
					+ (end - start) + "ms");
		}
	}

	private void handleInclude(Include include) {
		IFile file = include.getHrefAsFile();

		if (file == null || !file.exists()) {
			return;
		} else if (stylesheet.getFile().equals(file) || files.contains(file)) {
			circularReference = true;
			return;
		}
		files.add(file);

		StylesheetModel includedModel = XSLCore.getInstance().getStylesheet(
				file);
		if (includedModel == null)
			return;
		includeModel.getStylesheets().add(includedModel.getStylesheet());

		if (include.getIncludeType() == Include.INCLUDE) {
			templateModel.getTemplates().addAll(includedModel.templateModel.getTemplates());
			templateSet.addAll(includedModel.templateModel.getTemplates());
		} else {
			for (Template includedTemplate : includedModel.templateModel.getTemplates()) {
				if (!templateSet.contains(includedTemplate)) {
					templateModel.getTemplates().add(includedTemplate);
					templateSet.add(includedTemplate);
				}
			}
		}
	}
}