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
package org.eclipse.wst.xsl.core.internal.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.model.Include;
import org.eclipse.wst.xsl.core.internal.model.SourceArtifact;
import org.eclipse.wst.xsl.core.internal.model.SourceFile;
import org.eclipse.wst.xsl.core.internal.model.Template;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

public class XSLValidator {
	private static XSLValidator instance;
	private XPathExpression xpathInclude;
	private XPathExpression xpathImport;
	private XPathExpression xpathTemplate;
	private XPathExpression xpathCallTemplate;
	
	private XSLValidator() {
		System.out.println("XSLValidator ctor");
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext()
		{

			@Override
			public String getNamespaceURI(String arg0) {
				if ("xsl".equals(arg0))
					return XSLCorePlugin.XSLT_NS;
				return null;
			}

			@Override
			public String getPrefix(String arg0) {
				if (XSLCorePlugin.XSLT_NS.equals(arg0))
					return "xsl";
				return null;
			}

			@Override
			public Iterator getPrefixes(String arg0) {
				if (XSLCorePlugin.XSLT_NS.equals(arg0))
				{
					List list = new ArrayList();
					list.add("xsl");
					return list.iterator();
				}
				return null;
			}
			
		});
		try {
			xpathInclude = xpath.compile("/xsl:stylesheet/xsl:include");
			xpathImport = xpath.compile("/xsl:stylesheet/xsl:import");
			xpathTemplate = xpath.compile("/xsl:stylesheet/xsl:template");
			xpathCallTemplate = xpath.compile("/xsl:stylesheet/xsl:template//xsl:call-template");
		} catch (XPathExpressionException e) {
			XSLCorePlugin.log(e);
		}
	}

	public ValidationReport validate(String uri, IFile xslFile, IDOMDocument document) {
		ValidationInfo valinfo = new ValidationInfo(uri);
		Map<IFile, SourceFile> allFiles = new HashMap<IFile, SourceFile>();
		SourceFile sf = new SourceFile(allFiles,xslFile);
		try {
			NodeList includeNodes = (NodeList) xpathInclude.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < includeNodes.getLength(); i++) {
				IDOMNode node = (IDOMNode)includeNodes.item(i);
				Attr att = (Attr)node.getAttributes().getNamedItem("href");
				String href = att == null ? null : att.getValue();
				Include inc = new Include(sf,href,Include.INCLUDE);
				configure(node,inc,document.getStructuredDocument());
				sf.addInclude(inc);
			}
			NodeList importNodes = (NodeList) xpathImport.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < importNodes.getLength(); i++) {
				IDOMNode node = (IDOMNode)importNodes.item(i);
				Attr att = (Attr)node.getAttributes().getNamedItem("href");
				String href = att == null ? null : att.getValue();
				Include inc = new Include(sf,href,Include.IMPORT);
				configure(node,inc,document.getStructuredDocument());
				sf.addInclude(inc);
			}
			NodeList templateNodes = (NodeList) xpathTemplate.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < templateNodes.getLength(); i++) {
				IDOMNode node = (IDOMNode)templateNodes.item(i);
				Attr att = (Attr)node.getAttributes().getNamedItem("name");
				String name = att == null ? null : att.getValue();
				if (name != null)
				{
					Template template = new Template(sf,name);
					configure(node,template,document.getStructuredDocument());
					sf.addNamedTemplate(template);
				}
			}
		} catch (XPathExpressionException e) {
			XSLCorePlugin.log(e);
		}
//		valinfo.addError("INCLUDE", line, col, uri);
		return valinfo;
	}
	
	private void configure(IDOMNode node, SourceArtifact inc, IStructuredDocument structuredDocument) {
		try {
			int line = structuredDocument.getLineOfOffset(node.getStartOffset());
			int lineOffset = structuredDocument.getLineOffset(line);
			int col = node.getStartOffset() - lineOffset;				
			inc.setLineNumber(line);
			inc.setColumnNumber(col);
		} catch (BadLocationException e) {
			XSLCorePlugin.log(e);
		}
	}

	public static XSLValidator getInstance()
	{
		if (instance == null)
			instance = new XSLValidator();
		return instance;
	}
}
