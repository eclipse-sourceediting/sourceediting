/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 290286 - Model loading of parameters not respecting functions. 
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal;

import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.*;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.*;
import org.w3c.dom.*;

public class StylesheetParser {
	private final Stylesheet sf;
	private final Stack<Element> elementStack = new Stack<Element>();
	private Template currentTemplate;
	private Stack<CallTemplate> callTemplates = new Stack<CallTemplate>();
	private Stack<Function> functions = new Stack<Function>();
	private XSLElement parentEl;

	public StylesheetParser(Stylesheet stylesheet) {
		this.sf = stylesheet;
	}

	public void walkDocument(IDOMDocument document) {

		if (document.getDocumentElement() != null)
			recurse(document.getDocumentElement());
	}

	private void recurse(Element element) {
		XSLElement xslEl = null;
		if (XSLCore.XSL_NAMESPACE_URI.equals(element.getNamespaceURI())) {
			String elName = element.getLocalName();
			if ("stylesheet".equals(elName) && elementStack.size() == 0) //$NON-NLS-1$
			{
				NamedNodeMap map = element.getAttributes();
				String version = element.getAttribute("version"); //$NON-NLS-1$
				sf.setVersion(version);
				xslEl = sf;
			} else if ("include".equals(elName) && elementStack.size() == 1) //$NON-NLS-1$
			{
				Include include = new Include(sf);
				sf.addInclude(include);
				xslEl = include;
			} else if ("import".equals(elName) && elementStack.size() == 1) //$NON-NLS-1$
			{
				Import include = new Import(sf);
				sf.addImport(include);
				xslEl = include;
			} else if ("template".equals(elName) && elementStack.size() == 1) //$NON-NLS-1$
			{
				currentTemplate = new Template(sf);
				sf.addTemplate(currentTemplate);
				xslEl = currentTemplate;
			} else if ("param".equals(elName) && parentEl.getModelType() != XSLModelObject.Type.STYLESHEET) { //$NON-NLS-1$
				Parameter param = new Parameter(sf);
				// determine whether param has a value
				NodeList childNodes = element.getChildNodes();
				for (int i = 0; i < childNodes.getLength(); i++) {
					Node childNode = childNodes.item(i);
					if (childNode.getNodeType() != Node.ATTRIBUTE_NODE) {
						param.setValue(true);
						break;
					}
				}
				if (parentEl.getModelType() == XSLModelObject.Type.FUNCTION) {
					Function function = (Function) parentEl;
					function.addParameter(param);
				} else if (parentEl.getModelType() == XSLModelObject.Type.TEMPLATE
						&& elementStack.size() == 2 && currentTemplate != null) {
					Template template = (Template) parentEl;
					template.addParameter(param);
				}
				xslEl = param;
			} else if ("call-template".equals(elName) && elementStack.size() >= 2) //$NON-NLS-1$
			{
				CallTemplate currentCallTemplate = new CallTemplate(sf);
				callTemplates.push(currentCallTemplate);
				sf.addCalledTemplate(currentCallTemplate);
				xslEl = currentCallTemplate;
			} else if ("with-param".equals(elName) && elementStack.size() >= 3 && callTemplates.size() > 0) //$NON-NLS-1$
			{
				Parameter param = new Parameter(sf);
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
				CallTemplate currentCallTemplate = callTemplates.peek();
				currentCallTemplate.addParameter(param);
				xslEl = param;
			} else if ("variable".equals(elName) || "param".equals(elName)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				if (elementStack.size() == 1) {// global variable
					Variable var = new Variable(sf);
					sf.addGlobalVariable(var);
					xslEl = var;
				} else if (elementStack.size() > 1 && currentTemplate != null) {// local
																				// variable
					Variable var = new Variable(sf);
					currentTemplate.addVariable(var);
					xslEl = var;
				}
			} else if ("function".equals(elName)) { //$NON-NLS-1$
				currentTemplate = null;
				Function function = new Function(sf);
				functions.push(function);
				sf.addFunction(function);
				xslEl = function;
			} else {
				xslEl = new XSLElement(sf);
			}
			if (xslEl != null)
				configure((IDOMNode) element, xslEl);
		}
		elementStack.push(element);
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				recurse((Element) node);
			}
		}
		
		if (xslEl instanceof CallTemplate)
			callTemplates.pop();
		if (xslEl instanceof Function) {
			functions.pop();
		}
		elementStack.pop();
		// currentTemplate = null;
		// currentCallTemplate = null;
	}

	private void configure(IDOMNode node, XSLElement element) {
		setPositionInfo(node, element);
		IDOMElement domElem = (IDOMElement) node;
		element.setName(domElem.getLocalName());
		NamedNodeMap map = node.getAttributes();
		for (int i = 0; i < map.getLength(); i++) {
			IDOMAttr attr = (IDOMAttr) map.item(i);
			XSLAttribute xslatt = new XSLAttribute(element, attr.getName(),
					attr.getValue());
			setPositionInfo(attr, xslatt);
			element.setAttribute(xslatt);
		}
		if (parentEl != null)
			parentEl.addChild(element);
		if (node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node lnode = (Node) nodeList.item(i);
				if (lnode.getNodeType() == Node.ELEMENT_NODE) {
					parentEl = element; 
					break;
				}
			}
		}
		//parentEl = element;
	}

	private static void setPositionInfo(IDOMNode node, XSLNode inc) {
		try {
			IStructuredDocument structuredDocument = node
					.getStructuredDocument();
			int line = structuredDocument
					.getLineOfOffset(node.getStartOffset());
			int lineOffset = structuredDocument.getLineOffset(line);
			int col = node.getStartOffset() - lineOffset;
			inc.setOffset(node.getStartOffset());
			inc.setLineNumber(line);
			inc.setColumnNumber(col);
			inc.setLength(node.getLength());
		} catch (BadLocationException e) {
			XSLCorePlugin.log(e);
		}
	}
}
