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

import org.eclipse.wst.xsl.core.model.CallTemplate;
import org.eclipse.wst.xsl.core.model.Function;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Parameter;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.XSLElement;
import org.eclipse.wst.xsl.core.model.XSLModelObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A Factory that creates XSL Elements of the requested type.
 * @since 1.1
 */
public class XSLElementFactory {

	private StylesheetParserData stylesheetParserData;
	private Element element;
	
	public XSLElementFactory(StylesheetParserData data, Element element) {
		stylesheetParserData = data;
		this.element = element;
	}
		
	public XSLElement createStyleSheet() {
		String version = element.getAttribute("version"); //$NON-NLS-1$
		stylesheetParserData.getStylesheet().setVersion(version);
		return stylesheetParserData.getStylesheet();
	}
	
	public XSLElement createInclude() {
		Include include = new Include(stylesheetParserData.getStylesheet());
		stylesheetParserData.getStylesheet().addInclude(include);
		return include;
	}
	
	public XSLElement createImport() {
		Import include = new Import(stylesheetParserData.getStylesheet());
		stylesheetParserData.getStylesheet().addImport(include);
		return include;
	}
	
	public XSLElement createTemplate() {
		stylesheetParserData.setCurrentTemplate(new Template(stylesheetParserData.getStylesheet()));
		stylesheetParserData.getStylesheet().addTemplate(stylesheetParserData.getCurrentTemplate());
		return stylesheetParserData.getCurrentTemplate();
	}
	
	public XSLElement createParamater() {
		Parameter param = new Parameter(stylesheetParserData.getStylesheet());
		// determine whether param has a value
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() != Node.ATTRIBUTE_NODE) {
				param.setValue(true);
				break;
			}
		}
		if (stylesheetParserData.getParentEl().getModelType() == XSLModelObject.Type.FUNCTION) {
			Function function = (Function) stylesheetParserData.getParentEl();
			function.addParameter(param);
		} else if (stylesheetParserData.getParentEl().getModelType() == XSLModelObject.Type.TEMPLATE
				&& stylesheetParserData.getElementStack().size() == 2 && stylesheetParserData.getCurrentTemplate() != null) {
			Template template = (Template) stylesheetParserData.getParentEl();
			template.addParameter(param);
		}
		return param;
	}
	
	public XSLElement createCallTemplate() {
		CallTemplate currentCallTemplate = new CallTemplate(stylesheetParserData.getStylesheet());
		stylesheetParserData.getCallTemplates().push(currentCallTemplate);
		stylesheetParserData.getStylesheet().addCalledTemplate(currentCallTemplate);
		return currentCallTemplate;
	}

	public XSLElement createWithParamParm() {
		Parameter param = new Parameter(stylesheetParserData.getStylesheet());
		// determine whether param has a value
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() != Node.ATTRIBUTE_NODE) {
				param.setValue(true);
				break;
			}
		}
		// get the previous call-template
		CallTemplate currentCallTemplate = stylesheetParserData.getCallTemplates().peek();
		currentCallTemplate.addParameter(param);
		return param;
	}

	public XSLElement createVariable(XSLElement xslEl) {
		if (stylesheetParserData.getElementStack().size() == 1) {// global variable
			Variable var = new Variable(stylesheetParserData.getStylesheet());
			stylesheetParserData.getStylesheet().addGlobalVariable(var);
			xslEl = var;
		} else if (stylesheetParserData.getElementStack().size() > 1 && stylesheetParserData.getCurrentTemplate() != null) {// local
																		// variable
			Variable var = new Variable(stylesheetParserData.getStylesheet());
			stylesheetParserData.getCurrentTemplate().addVariable(var);
			xslEl = var;
		}
		return xslEl;
	}
	
	public XSLElement createFunction() {
		stylesheetParserData.setCurrentTemplate(null);
		Function function = new Function(stylesheetParserData.getStylesheet());
		stylesheetParserData.getFunctions().push(function);
		stylesheetParserData.getStylesheet().addFunction(function);
		return function;
	}
	
	public XSLElement createXSLElement() {
		return new XSLElement(stylesheetParserData.getStylesheet());
	}
	
	
}
