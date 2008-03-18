package org.eclipse.wst.xsl.core.internal.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.XSLCorePlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

public class SourceFileBuilder {
	private final XPathExpression xpathInclude;
	private final XPathExpression xpathImport;
	private final XPathExpression xpathTemplate;
	private final XPathExpression xpathCallTemplate;
	private final XPathExpression xpathTemplateParam;
	private final XPathExpression xpathCallTemplateParam;

	public SourceFileBuilder() throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {

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
			public Iterator<String> getPrefixes(String arg0) {
				if (XSLCorePlugin.XSLT_NS.equals(arg0)) {
					List<String> list = new ArrayList<String>();
					list.add("xsl");
					return list.iterator();
				}
				return null;
			}

		});
		xpathInclude = xpath.compile("/xsl:stylesheet/xsl:include");
		xpathImport = xpath.compile("/xsl:stylesheet/xsl:import");
		xpathTemplate = xpath.compile("/xsl:stylesheet/xsl:template");
		xpathTemplateParam = xpath.compile("xsl:param");
		xpathCallTemplate = xpath.compile("/xsl:stylesheet/xsl:template//xsl:call-template");
		xpathCallTemplateParam = xpath.compile("xsl:with-param");
	}

	/**
	 * 
	 * @param file
	 * @return a source file, or null if creation failed
	 */
	public SourceFile buildSourceFile(IFile file) {
		SourceFile sf = null;
		IStructuredModel smodel = null;
		try {
			smodel = StructuredModelManager.getModelManager().getModelForRead(file);
			if (smodel instanceof IDOMModel) {
				IDOMModel model = (IDOMModel) smodel;
				sf = parseModel(model,file);
			}
		} catch (IOException e) {
			XSLCorePlugin.log(e);
		} catch (CoreException e) {
			XSLCorePlugin.log(e);
		} finally {
			if (smodel != null)
				smodel.releaseFromRead();
		}
		return sf;
	}

	private SourceFile parseModel(IDOMModel model, IFile file) {
		IDOMDocument document = model.getDocument();
		SourceFile sf = new SourceFile(file);
		try {
			NodeList nodes;
			nodes = (NodeList) xpathInclude.evaluate(document,XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				IDOMNode node = (IDOMNode) nodes.item(i);
				Attr att = (Attr) node.getAttributes().getNamedItem("href");
				String href = att == null ? null : att.getValue();
				Include inc = new Include(sf, href, Include.INCLUDE);
				configure(node, inc, document.getStructuredDocument());
				sf.addInclude(inc);
			}
			nodes = (NodeList) xpathImport.evaluate(document,XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				IDOMNode node = (IDOMNode) nodes.item(i);
				Attr att = (Attr) node.getAttributes().getNamedItem("href");
				String href = att == null ? null : att.getValue();
				Include inc = new Include(sf, href, Include.IMPORT);
				configure(node, inc, document.getStructuredDocument());
				sf.addInclude(inc);
			}
			nodes = (NodeList) xpathTemplate.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				IDOMNode node = (IDOMNode) nodes.item(i);
				Attr att = (Attr) node.getAttributes().getNamedItem("name");
				String name = att == null ? null : att.getValue();
				if (name != null) {
					Template template = new Template(sf, name);
					configure(node, template, document.getStructuredDocument());
					sf.addNamedTemplate(template);
					
					NodeList paramNodes = (NodeList)xpathTemplateParam.evaluate(node, XPathConstants.NODESET);
					for (int j = 0; j < nodes.getLength(); j++) {
						IDOMNode paramNode = (IDOMNode) paramNodes.item(j);
						att = (Attr) paramNode.getAttributes().getNamedItem("name");
						String paramName = att == null ? null : att.getValue();
						att = (Attr) paramNode.getAttributes().getNamedItem("select");
						String paramSelect = att == null ? null : att.getValue();
						Parameter parameter = new Parameter(sf,paramName,paramSelect);
						configure(paramNode, parameter, document.getStructuredDocument());
						template.addParameter(parameter);
					}
				}
			}
			nodes = (NodeList) xpathCallTemplate.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				IDOMNode node = (IDOMNode) nodes.item(i);
				Attr att = (Attr) node.getAttributes().getNamedItem("name");
				String name = att == null ? null : att.getValue();
				if (name != null) {
					Template template = new Template(sf, name);
					configure(node, template, document.getStructuredDocument());
					sf.addCalledTemplate(template);

					NodeList paramNodes = (NodeList)xpathCallTemplateParam.evaluate(node, XPathConstants.NODESET);
					for (int j = 0; j < nodes.getLength(); j++) {
						IDOMNode paramNode = (IDOMNode) paramNodes.item(j);
						att = (Attr) paramNode.getAttributes().getNamedItem("name");
						String paramName = att == null ? null : att.getValue();
						att = (Attr) paramNode.getAttributes().getNamedItem("select");
						String paramSelect = att == null ? null : att.getValue();
						Parameter parameter = new Parameter(sf,paramName,paramSelect);
						configure(paramNode, parameter, document.getStructuredDocument());
						template.addParameter(parameter);
					}				
				}
			}
			// TODO template params
		} catch (XPathExpressionException e) {
			XSLCorePlugin.log(e);
		}
		return sf;
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

}
