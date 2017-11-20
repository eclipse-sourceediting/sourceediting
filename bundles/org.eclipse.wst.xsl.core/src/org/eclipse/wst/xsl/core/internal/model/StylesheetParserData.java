/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.model;

import java.util.Stack;

import org.eclipse.wst.xsl.core.model.CallTemplate;
import org.eclipse.wst.xsl.core.model.Function;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.XSLElement;
import org.w3c.dom.Element;

/**
 * This class is used to hold Data necessary for parsing and walking
 * an XSL Stylesheet into a Model.
 * 
 * @author dcarver
 * @since 1.1
 */
public class StylesheetParserData {
	private Stylesheet stylesheet;
	private Stack<Element> elementStack;

	private Template currentTemplate;

	private Stack<CallTemplate> callTemplates;

	private Stack<Function> functions;

	private XSLElement parentEl;

	public StylesheetParserData(Stack<Element> elementStack,
			Stack<CallTemplate> callTemplates, Stack<Function> functions) {
		this.elementStack = elementStack;
		this.callTemplates = callTemplates;
		this.functions = functions;
	}

	public Stack<CallTemplate> getCallTemplates() {
		return callTemplates;
	}

	public Template getCurrentTemplate() {
		return currentTemplate;
	}

	public Stack<Element> getElementStack() {
		return elementStack;
	}

	public Stack<Function> getFunctions() {
		return functions;
	}

	public XSLElement getParentEl() {
		return parentEl;
	}

	public Stylesheet getStylesheet() {
		return stylesheet;
	}

	public void setCallTemplates(Stack<CallTemplate> callTemplates) {
		this.callTemplates = callTemplates;
	}
	public void setCurrentTemplate(Template currentTemplate) {
		this.currentTemplate = currentTemplate;
	}
	public void setElementStack(Stack<Element> elementStack) {
		this.elementStack = elementStack;
	}
	public void setFunctions(Stack<Function> functions) {
		this.functions = functions;
	}
	public void setParentEl(XSLElement parentEl) {
		this.parentEl = parentEl;
	}

	public void setStylesheet(Stylesheet sf) {
		this.stylesheet = sf;
	}
}