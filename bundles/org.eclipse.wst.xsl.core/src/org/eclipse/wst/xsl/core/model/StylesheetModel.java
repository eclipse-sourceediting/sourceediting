/*******************************************************************************
 * Copyright (c) 2007, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) -  bug 243577 - Added retrieving all called-templates.
 *     David Carver (STAR) -  bug 246503 - Handled nested circular includes.
 *******************************************************************************/
package org.eclipse.wst.xsl.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsl.core.XSLCore;
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
 * @since 1.0
 */
public class StylesheetModel extends XSLModelObject {
	private final Stylesheet stylesheet;
	boolean circularReference;
	final Set<IFile> files = new HashSet<IFile>();
	final Set<Stylesheet> stylesheets = new HashSet<Stylesheet>();
	final List<Include> includeModel = new ArrayList<Include>();
	final List<Import> importModel = new ArrayList<Import>();
	final Set<Template> templateSet = new HashSet<Template>();
	final List<Template> templates = new ArrayList<Template>();
	final List<Variable> globalVariables = new ArrayList<Variable>();
	final List<CallTemplate> callTemplates = new ArrayList<CallTemplate>();
	final List<Function> functions = new ArrayList<Function>();

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
	public List<Include> getIncludes() {
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
	public List<Variable> getGlobalVariables() {
		return globalVariables;
	}

	/**
	 * Get all templates that are included in this stylesheet anywhere in the
	 * hierarchy via either import or include.
	 * 
	 * @return the set of templates in the entire hierarchy
	 */
	public List<Template> getTemplates() {
		return templates;
	}

	/**
	 * A utility method that traverses all stylesheet in the hierarchy of
	 * stylesheets (not including the current stylesheet), and adds all their
	 * templates to the returned list. Therefore the returned list has no regard
	 * for whether a template is 'visible' (i.e. whether it might be overridden
	 * since it was included via an import). The order of the templates in the
	 * list is arbitrary.
	 * 
	 * @return an unordered list of all templates from all stylesheets.
	 */
	public List<Template> findAllNestedTemplates() {
		List<Template> allTemplates = new ArrayList<Template>();
		for (Stylesheet stylesheet : stylesheets) {
			allTemplates.addAll(stylesheet.getTemplates());
		}
		return allTemplates;
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
		List<Template> matching = new ArrayList<Template>(templates.size());
		for (Template template : templates) {
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
		List<Template> matching = new ArrayList<Template>(templates.size());
		for (Template template : templates) {
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
			System.out.println("Fixing " + stylesheet.getFile() + "..."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		templates.addAll(stylesheet.getTemplates());
		templateSet.addAll(stylesheet.getTemplates());
		globalVariables.addAll(stylesheet.globalVariables);
		callTemplates.addAll(stylesheet.getCalledTemplates());
		functions.addAll(stylesheet.getFunctions());
		for (Include inc : stylesheet.getIncludes()) {
			handleInclude(inc);
		}
		for (Import inc : stylesheet.getImports()) {
			handleInclude(inc);
		}
		if (Debug.debugXSLModel) {
			long end = System.currentTimeMillis();
			System.out
					.println("FIX " + stylesheet.getFile() + " in " + (end - start) + "ms"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private void handleInclude(Include include) {
		IFile file = include.getHrefAsFile();

		if (file == null || !file.exists()) {
			return;
		}

		if (stylesheet.getFile().equals(file) || files.contains(file)) {
			circularReference = true;
			return;
		} else if (isNestedInclude(include, stylesheet.getFile())) {
			circularReference = true;
		}
		files.add(file);

		StylesheetModel includedModel = XSLCore.getInstance().getStylesheet(
				file);
		if (includedModel == null)
			return;
		stylesheets.add(includedModel.getStylesheet());
		globalVariables.addAll(includedModel.globalVariables);
		callTemplates.addAll(includedModel.getCallTemplates());
		if (include.getIncludeType() == Include.INCLUDE) {
			includeModel.add(include);
			templates.addAll(includedModel.getTemplates());
			templateSet.addAll(includedModel.getTemplates());
		} else {
			importModel.add((Import) include);
			for (Template includedTemplate : includedModel.getTemplates()) {
				if (!templateSet.contains(includedTemplate)) {
					templates.add(includedTemplate);
					templateSet.add(includedTemplate);
				}
			}
		}
	}

	/**
	 * Is the current stylesheet nested in one of the included stylesheets
	 * 
	 * @return
	 */
	private boolean isNestedInclude(Include include, IFile compareTo) {
		StylesheetModel includedModel = XSLCore.getInstance().getStylesheet(
				include.getHrefAsFile());

		for (Include inc : includedModel.getIncludes()) {
			if (inc.getHrefAsFile().equals(compareTo)
					|| isNestedInclude(inc, compareTo)) {
				return true;
			}
		}

		return false;

	}

	@Override
	public Type getModelType() {
		return Type.STYLESHEET_MODEL;
	}

	public List<CallTemplate> getCallTemplates() {
		return callTemplates;
	}

	/**
	 * Get a List of all functions that are known.
	 * @return 
	 * @since 1.1
	 */
	public List<Function> getFunctions() {
		return functions;
	}

	/**
	 * Get all functions that are included in this stylesheet anywhere in the
	 * hierarchy via either import or include which have the given name.
	 * 
	 * @param name
	 *            the template name
	 * @return the set of named templates with the given name
	 * @since 1.1
	 */
	public List<Function> getFunctionByName(String name) {
		List<Function> matching = new ArrayList<Function>(functions.size());
		for (Function function : functions) {
			if (name.equals(function.getName()))
				matching.add(function);
		}
		return matching;
	}

}