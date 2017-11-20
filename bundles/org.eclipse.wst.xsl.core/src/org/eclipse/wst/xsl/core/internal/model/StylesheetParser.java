/*******************************************************************************
 * Copyright (c) 2007, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 290286 - Model loading of parameters not respecting functions.
 *     David Carver (STAR) - no bug - Refactored Code for easier maintenance 
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal.model;

import java.util.Stack;

import org.eclipse.wst.xml.core.internal.provisional.document.*;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.*;
import org.w3c.dom.*;

public class StylesheetParser {
	private StylesheetParserData stylesheetParserData = new StylesheetParserData(
			new Stack<Element>(), new Stack<CallTemplate>(), new Stack<Function>());

	public StylesheetParser(Stylesheet stylesheet) {
		this.stylesheetParserData.setStylesheet(stylesheet);
	}

	public void walkDocument(IDOMDocument document) {

		if (document.getDocumentElement() != null)
			recurse(document.getDocumentElement());
	}

	private void recurse(Element element) {
		XSLElement xslEl = null;
		if (XSLCore.XSL_NAMESPACE_URI.equals(element.getNamespaceURI())) {
			XSLModelObjectFactory factory = new XSLModelObjectFactory(element, xslEl, stylesheetParserData);
			xslEl = factory.createXSLModelObject();
		}
		stylesheetParserData.getElementStack().push(element);
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				recurse((Element) node);
			}
		}
		
		if (xslEl instanceof CallTemplate)
			stylesheetParserData.getCallTemplates().pop();
		if (xslEl instanceof Function) {
			stylesheetParserData.getFunctions().pop();
		}
		stylesheetParserData.getElementStack().pop();
	}
}
