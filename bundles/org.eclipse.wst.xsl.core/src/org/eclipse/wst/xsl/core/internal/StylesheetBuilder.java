/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - fix issue with xslElm being null on local variables.
 *     David Carver (STAR) -  bug 243577 - Added retrieving all called-templates.
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal;

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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.util.Debug;
import org.eclipse.wst.xsl.core.model.CallTemplate;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Parameter;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.eclipse.wst.xsl.core.model.XSLElement;
import org.eclipse.wst.xsl.core.model.XSLNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A builder that creates and maintains a cache of <code>Stylesheet</code>'s.
 * 
 * @author Doug Satchwell
 */
public class StylesheetBuilder {
	private static StylesheetBuilder instance;
	private final Map<IFile, Stylesheet> builtFiles = new HashMap<IFile, Stylesheet>();

	private StylesheetBuilder() {
	}

	/**
	 * Get the <code>Stylesheet</code> associated with the given file. If either
	 * the <code>Stylesheet</code> has not yet been created or
	 * <code>force</code> is specified then the <code>Stylesheet</code> is
	 * built.
	 * 
	 * @param file
	 *            the XSL file
	 * @param force
	 *            <code>true</code> to force a parse of the file
	 * @return the <code>Stylesheet</code>
	 */
	public Stylesheet getStylesheet(IFile file, boolean force) {
		Stylesheet stylesheet = builtFiles.get(file);
		if (stylesheet == null || force) {
			stylesheet = build(file);
			builtFiles.put(file, stylesheet);
		}
		return stylesheet;
	}

	private Stylesheet build(IFile file) {
		long start = System.currentTimeMillis();
		if (Debug.debugXSLModel) {
			System.out.println("Building " + file + "...");
		}

		Stylesheet stylesheet = null;
		IStructuredModel smodel = null;
		try {
			smodel = StructuredModelManager.getModelManager()
					.getExistingModelForRead(file);
			if (smodel == null) {
				smodel = StructuredModelManager.getModelManager()
						.getModelForRead(file);
				if (Debug.debugXSLModel) {
					long endParse = System.currentTimeMillis();
					System.out.println("PARSE " + file + " in "
							+ (endParse - start) + "ms");
				}
			} else if (Debug.debugXSLModel) {
				long endParse = System.currentTimeMillis();
				System.out.println("NO-PARSE " + file + " in "
						+ (endParse - start) + "ms");
			}
			// start = System.currentTimeMillis();
			if (smodel != null && smodel instanceof IDOMModel) {
				IDOMModel model = (IDOMModel) smodel;
				stylesheet = parseModel(model, file);
			}
		} catch (IOException e) {
			XSLCorePlugin.log(e);
		} catch (CoreException e) {
			XSLCorePlugin.log(e);
		} finally {
			if (smodel != null)
				smodel.releaseFromRead();
		}
		if (Debug.debugXSLModel) {
			long end = System.currentTimeMillis();
			System.out.println("BUILD " + file + " in " + (end - start) + "ms");
		}
		return stylesheet;
	}

	private Stylesheet parseModel(IDOMModel model, IFile file) {
		IDOMDocument document = model.getDocument();
		Stylesheet sf = new Stylesheet(file);
		StylesheetParser walker = new StylesheetParser(sf);
		walker.walkDocument(document);
		return sf;
	}

	/**
	 * Get the singleton <code>StylesheetBuilder</code> instance.
	 * 
	 * @return the <code>StylesheetBuilder</code> instance
	 */
	public static synchronized StylesheetBuilder getInstance() {
		if (instance == null) {
			instance = new StylesheetBuilder();
		}
		return instance;
	}

	private static class StylesheetParser {
		private final Stylesheet sf;
		private final Stack<Element> elementStack = new Stack<Element>();
		private Template currentTemplate;
		private Stack<CallTemplate> callTemplates = new Stack<CallTemplate>();
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
					String version = element.getAttribute("version");
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
				} else if ("param".equals(elName) && elementStack.size() == 2 && currentTemplate != null) //$NON-NLS-1$
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
					currentTemplate.addParameter(param);
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
				} else if ("variable".equals(elName)) //$NON-NLS-1$
				{
					if (elementStack.size() == 1)
					{// global variable
						Variable var = new Variable(sf);
						sf.addGlobalVariable(var);
						xslEl = var;
					}
					else if (elementStack.size() > 1 && currentTemplate != null)
					{// local variable
						Variable var = new Variable(sf);
						currentTemplate.addVariable(var);
						xslEl = var;
					}
				} 
				else {
					xslEl = new XSLElement(sf);
				}
				if (xslEl!=null)
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
			parentEl = element;
		}

		private static void setPositionInfo(IDOMNode node, XSLNode inc) {
			try {
				IStructuredDocument structuredDocument = node
						.getStructuredDocument();
				int line = structuredDocument.getLineOfOffset(node
						.getStartOffset());
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
}
