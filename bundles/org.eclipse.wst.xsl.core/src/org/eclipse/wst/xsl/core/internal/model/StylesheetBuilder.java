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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 */
public class StylesheetBuilder
{
	private static StylesheetBuilder instance;
	private final Map<IFile, Stylesheet> builtFiles = new HashMap<IFile, Stylesheet>();

	private StylesheetBuilder()
	{}
	
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
		StylesheetParser walker = new StylesheetParser(sf);
		walker.walkDocument(document);
		return sf;
	}

	public static synchronized StylesheetBuilder getInstance()
	{
		if (instance == null)
		{
			instance = new StylesheetBuilder();
		}
		return instance;
	}
	
	private static class StylesheetParser
	{
		private final Stylesheet sf;
		private final Stack elementStack = new Stack();
		private Template currentTemplate;
		private CallTemplate currentCallTemplate;

		public StylesheetParser(Stylesheet stylesheet)
		{
			this.sf = stylesheet;
		}
		
		public void walkDocument(IDOMDocument document)
		{
			recurse(document.getDocumentElement());
		}

		private void recurse(Element element)
		{
			if (XSLCore.XSL_NAMESPACE_URI.equals(element.getNamespaceURI()))
			{
				String elName = element.getLocalName();
				if ("stylesheet".equals(elName) && elementStack.size() == 0)
				{
					configure((IDOMNode)element, sf);
				}
				else if ("include".equals(elName) && elementStack.size() == 1)
				{
					Include include = new Include(sf,Include.INCLUDE);
					configure((IDOMNode)element, include);
					sf.addInclude(include);
				}
				else if ("import".equals(elName) && elementStack.size() == 1)
				{
					Import include = new Import(sf,Include.IMPORT);
					configure((IDOMNode)element, include);
					sf.addImport(include);
				}
				else if ("template".equals(elName) && elementStack.size() == 1)
				{
					currentTemplate = new Template(sf);
					configure((IDOMNode)element, currentTemplate);
					sf.addTemplate(currentTemplate);
				}
				else if ("param".equals(elName) && elementStack.size() == 2 && currentTemplate != null)
				{
					Parameter param = new Parameter(sf);
					configure((IDOMNode)element, param);
					currentTemplate.addParameter(param);
				}
				else if ("call-template".equals(elName) && elementStack.size() >= 2)
				{
					currentCallTemplate = new CallTemplate(sf);
					configure((IDOMNode)element, currentCallTemplate);				
					sf.addCalledTemplate(currentCallTemplate);
				}
				else if ("with-param".equals(elName) && elementStack.size() >= 3 && currentCallTemplate != null)
				{
					Parameter param = new Parameter(sf);
					configure((IDOMNode)element, param);
					currentCallTemplate.addParameter(param);
				}
				else if ("variable".equals(elName) && elementStack.size() == 1)
				{
					Variable var = new Variable(sf);
					configure((IDOMNode)element, var);
					sf.addGlobalVariable(var);
				}
			}
			elementStack.push(element);
			NodeList childNodes = element.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++)
			{
				Node node = childNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					recurse((Element)node);
				}
			}
			elementStack.pop();
			currentTemplate = null;
			currentCallTemplate = null;
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
	}
}
