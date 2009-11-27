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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.eclipse.wst.xsl.core.model.XSLElement;
import org.eclipse.wst.xsl.core.model.XSLModelObject;
import org.eclipse.wst.xsl.core.model.XSLNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XSLModelObjectFactory {
	private static final String VARIABLE = "variable"; //$NON-NLS-1$
	private static final String FUNCTION = "function"; //$NON-NLS-1$
	private static final String WITH_PARAM = "with-param"; //$NON-NLS-1$
	private static final String CALL_TEMPLATE = "call-template"; //$NON-NLS-1$
	private static final String PARAM = "param"; //$NON-NLS-1$
	private static final String TEMPLATE = "template"; //$NON-NLS-1$
	private static final String IMPORT = "import"; //$NON-NLS-1$
	private static final String INCLUDE = "include"; //$NON-NLS-1$
	private static final String STYLESHEET = "stylesheet"; //$NON-NLS-1$
	
	private Element element;
	private XSLElement xslEl;
	private StylesheetParserData stylesheetParserData;

	public XSLModelObjectFactory(Element element, XSLElement xslEl,
			StylesheetParserData data) {
		this.element = element;
		this.xslEl = xslEl;
		stylesheetParserData = data;
	}

	/**
	 * Creates an XSL Model Object of the appropriate type.
	 * @return
	 */
	public XSLElement createXSLModelObject() {
		String elName = element.getLocalName();
		int elementSize = stylesheetParserData.getElementStack().size();
		XSLElementFactory factory = new XSLElementFactory(stylesheetParserData,
				element);
		if (elementSize == 0) {
			if (STYLESHEET.equals(elName)) {
				xslEl = factory.createStyleSheet();
			}
		}else if (INCLUDE.equals(elName) && elementSize == 1)
		{
			xslEl = factory.createInclude();
		} else if (IMPORT.equals(elName) && elementSize == 1)
		{
			xslEl = factory.createImport();
		} else if (TEMPLATE.equals(elName) && elementSize == 1)
		{
			xslEl = factory.createTemplate();
		} else if (PARAM.equals(elName) && notParentStylesheet()) {
			xslEl = factory.createParamater();
		} else if (CALL_TEMPLATE.equals(elName) && elementSize >= 2) 
		{
			xslEl = factory.createCallTemplate();
		} else if (WITH_PARAM.equals(elName) && elementSize >= 3
				&& stylesheetParserData.getCallTemplates().size() > 0) 
		{
			xslEl = factory.createWithParamParm();
		} else if (isVariable(elName)) {
			xslEl = factory.createVariable(xslEl);
		} else if (FUNCTION.equals(elName)) { 
			xslEl = factory.createFunction();
		} else {
			xslEl = factory.createXSLElement();
		}
		if (xslEl != null)
			configure((IDOMNode) element, xslEl);
		return xslEl;
	}

	private boolean isVariable(String elName) {
		return VARIABLE.equals(elName) || PARAM.equals(elName);
	}

	private boolean notParentStylesheet() {
		return stylesheetParserData.getParentEl().getModelType() != XSLModelObject.Type.STYLESHEET;
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
		setParentElement(node, element);
	}

	private void setParentElement(IDOMNode node, XSLElement element) {
		if (stylesheetParserData.getParentEl() != null)
			stylesheetParserData.getParentEl().addChild(element);
		if (node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node lnode = nodeList.item(i);
				if (lnode.getNodeType() == Node.ELEMENT_NODE) {
					stylesheetParserData.setParentEl(element);
					break;
				}
			}
		}
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
