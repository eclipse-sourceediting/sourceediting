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

import java.io.IOException;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 */
public class StylesheetBuilder
{
	private static StylesheetBuilder instance;
	private final XPathExpression xpathStylesheet;
	private final XPathExpression xpathInclude;
	private final XPathExpression xpathImport;
	private final XPathExpression xpathGlobalVariable;
	private final XPathExpression xpathTemplate;
	private final XPathExpression xpathTemplateParam;
	private final XPathExpression xpathTemplateVariable;
	private final XPathExpression xpathCallTemplate;
	private final XPathExpression xpathCallTemplateParam;
	private final Map<IFile, Stylesheet> builtFiles = new HashMap<IFile, Stylesheet>();

	private StylesheetBuilder() throws XPathExpressionException
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext()
		{

			public String getNamespaceURI(String arg0)
			{
				if ("xsl".equals(arg0)) //$NON-NLS-1$
					return XSLCore.XSL_NAMESPACE_URI;
				return null;
			}

			public String getPrefix(String arg0)
			{
				if (XSLCore.XSL_NAMESPACE_URI.equals(arg0))
					return "xsl"; //$NON-NLS-1$
				return null;
			}

			public Iterator<String> getPrefixes(String arg0)
			{
				if (XSLCore.XSL_NAMESPACE_URI.equals(arg0))
				{
					List<String> list = new ArrayList<String>();
					list.add("xsl"); //$NON-NLS-1$
					return list.iterator();
				}
				return null;
			}

		});
		xpathStylesheet = xpath.compile("/xsl:stylesheet"); //$NON-NLS-1$
		xpathInclude = xpath.compile("/xsl:stylesheet/xsl:include"); //$NON-NLS-1$
		xpathImport = xpath.compile("/xsl:stylesheet/xsl:import"); //$NON-NLS-1$
		xpathGlobalVariable = xpath.compile("/xsl:stylesheet/xsl:variable"); //$NON-NLS-1$
		xpathTemplate = xpath.compile("/xsl:stylesheet/xsl:template"); //$NON-NLS-1$
		xpathTemplateParam = xpath.compile("xsl:param"); //$NON-NLS-1$
		xpathTemplateVariable = xpath.compile("xsl:variable"); //$NON-NLS-1$
		xpathCallTemplate = xpath.compile("/xsl:stylesheet/xsl:template//xsl:call-template"); //$NON-NLS-1$
		xpathCallTemplateParam = xpath.compile("xsl:with-param"); //$NON-NLS-1$
	}
	
	public Stylesheet getStylesheet(IFile file, boolean force){
		Stylesheet stylesheet = builtFiles.get(file);
		if (stylesheet == null || force)
		{
			stylesheet = build(file);
			builtFiles.put(file, stylesheet);
		}
		return stylesheet;
	}

	private Stylesheet build(IFile file)
	{
		System.out.println("Building "+file);
		Stylesheet stylesheet = null;
		IStructuredModel smodel = null;
		try
		{
			smodel = StructuredModelManager.getModelManager().getModelForRead(file);
			if (smodel instanceof IDOMModel)
			{
				IDOMModel model = (IDOMModel) smodel;
				stylesheet = parseModel(model, file);
			}
		}
		catch (IOException e)
		{
			XSLCorePlugin.log(e);
		}
		catch (CoreException e)
		{
			XSLCorePlugin.log(e);
		}
		finally
		{
			if (smodel != null)
				smodel.releaseFromRead();
		}
		return stylesheet;
	}

	private Stylesheet parseModel(IDOMModel model, IFile file)
	{
		IDOMDocument document = model.getDocument();
		Stylesheet sf = new Stylesheet(file);
		try
		{
			evaluateStylesheet(document, sf);
			evaluateIncludes(document, sf);
			evaluateImports(document, sf);
			evaluateGlobalVariables(document, sf);
			evaluateTemplates(document, sf);
			evaluateCalledTemplates(document, sf);
		}
		catch (XPathExpressionException e)
		{
			XSLCorePlugin.log(e);
		}
		return sf;
	}

	private void evaluateStylesheet(IDOMDocument document, Stylesheet sf) throws XPathExpressionException
	{
		NodeList nodes = (NodeList) xpathStylesheet.evaluate(document, XPathConstants.NODESET);
		if (nodes.getLength() == 1)
		{
			configure((IDOMNode)nodes.item(0),sf);
		}
	}

	private void evaluateGlobalVariables(IDOMDocument document, Stylesheet sf) throws XPathExpressionException
	{
		NodeList nodes = (NodeList) xpathGlobalVariable.evaluate(document, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			IDOMNode node = (IDOMNode) nodes.item(i);
			Variable var = new Variable(sf);
			configure(node, var);
			sf.addGlobalVariable(var);
		}
	}

	private void evaluateCalledTemplates(IDOMDocument document, Stylesheet sf) throws XPathExpressionException
	{
		NodeList nodes = (NodeList) xpathCallTemplate.evaluate(document, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			IDOMNode node = (IDOMNode) nodes.item(i);
			CallTemplate template = new CallTemplate(sf);
			configure(node, template);
			sf.addCalledTemplate(template);

			NodeList paramNodes = (NodeList) xpathCallTemplateParam.evaluate(node, XPathConstants.NODESET);
			for (int j = 0; j < paramNodes.getLength(); j++)
			{
				IDOMNode paramNode = (IDOMNode) paramNodes.item(j);
				Parameter parameter = new Parameter(sf);
				configure(paramNode, parameter);
				template.addParameter(parameter);
			}
		}
	}

	private void evaluateTemplates(IDOMDocument document, Stylesheet sf) throws XPathExpressionException
	{
		NodeList nodes = (NodeList) xpathTemplate.evaluate(document, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			IDOMNode node = (IDOMNode) nodes.item(i);
			
			Template template = new Template(sf);
			configure(node, template);
			sf.addTemplate(template);

			// params
			NodeList paramNodes = (NodeList) xpathTemplateParam.evaluate(node, XPathConstants.NODESET);
			for (int j = 0; j < paramNodes.getLength(); j++)
			{
				IDOMNode paramNode = (IDOMNode) paramNodes.item(j);
				Parameter parameter = new Parameter(sf);
				configure(paramNode, parameter);
				template.addParameter(parameter);
			}
				
			// variables
			NodeList varNodes = (NodeList) xpathTemplateVariable.evaluate(node, XPathConstants.NODESET);
			for (int j = 0; j < varNodes.getLength(); j++)
			{
				IDOMNode paramNode = (IDOMNode) varNodes.item(j);
				Variable var = new Variable(sf);
				configure(paramNode, var);
				template.addVariable(var);
			}			
		}
	}

	private void evaluateImports(IDOMDocument document, Stylesheet sf) throws XPathExpressionException
	{
		NodeList nodes = (NodeList) xpathImport.evaluate(document, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			IDOMNode node = (IDOMNode) nodes.item(i);
			Import inc = new Import(sf, Include.IMPORT);
			configure(node, inc);
			sf.addImport(inc);
		}
	}

	private void evaluateIncludes(IDOMDocument document, Stylesheet sf) throws XPathExpressionException
	{
		NodeList nodes = (NodeList) xpathInclude.evaluate(document, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++)
		{
			IDOMNode node = (IDOMNode) nodes.item(i);
			Include inc = new Include(sf, Include.INCLUDE);
			configure(node, inc);
			sf.addInclude(inc);
		}
	}

	private static void configure(IDOMNode node, XSLElement element)
	{
		setPositionInfo(node,element);
		NamedNodeMap map = node.getAttributes();
		for (int i = 0; i < map.getLength(); i++)
		{
			IDOMAttr attr = (IDOMAttr)map.item(i);
			XSLAttribute xslatt = new XSLAttribute(element,attr.getName(),attr.getValue());
			setPositionInfo(attr, xslatt);
			element.setAttribute(xslatt);
		}
	}

	private static void setPositionInfo(IDOMNode node, XSLNode inc)
	{
		try
		{
			IStructuredDocument structuredDocument = node.getStructuredDocument();
			int line = structuredDocument.getLineOfOffset(node.getStartOffset());
			int lineOffset = structuredDocument.getLineOffset(line);
			int col = node.getStartOffset() - lineOffset;
			inc.setLineNumber(line);
			inc.setColumnNumber(col);
			inc.setLength(node.getLength());
		}
		catch (BadLocationException e)
		{
			XSLCorePlugin.log(e);
		}
	}

	public static synchronized StylesheetBuilder getInstance()
	{
		if (instance == null)
		{
			try
			{
				instance = new StylesheetBuilder();
			}
			catch (XPathExpressionException e)
			{
				XSLCorePlugin.log(e);
			}
		}
		return instance;
	}
}
