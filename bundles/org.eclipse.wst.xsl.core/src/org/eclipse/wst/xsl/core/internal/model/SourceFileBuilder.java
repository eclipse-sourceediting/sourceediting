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

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public class SourceFileBuilder {
	private XPathExpression xpathInclude;
	private XPathExpression xpathImport;
	private XPathExpression xpathTemplate;
	private XPathExpression xpathCallTemplate;

	/**
	 * TODO: Add Javadoc
	 */
	public SourceFileBuilder() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {

			/**
			 * TODO: Add Javadoc
			 */
			public String getNamespaceURI(String arg0) {
				if ("xsl".equals(arg0)) //$NON-NLS-1$
					return XSLCorePlugin.XSLT_NS;
				return null;
			}

			/**
			 * TODO: Add Javadoc
			 */
			public String getPrefix(String arg0) {
				if (XSLCorePlugin.XSLT_NS.equals(arg0))
					return "xsl"; //$NON-NLS-1$
				return null;
			}

			/**
			 * TODO: Add Javadoc
			 */
			public Iterator getPrefixes(String arg0) {
				if (XSLCorePlugin.XSLT_NS.equals(arg0)) {
					List list = new ArrayList();
					list.add("xsl"); //$NON-NLS-1$
					return list.iterator();
				}
				return null;
			}

		});
		try {
			xpathInclude = xpath.compile("/xsl:stylesheet/xsl:include"); //$NON-NLS-1$
			xpathImport = xpath.compile("/xsl:stylesheet/xsl:import"); //$NON-NLS-1$
			xpathTemplate = xpath.compile("/xsl:stylesheet/xsl:template"); //$NON-NLS-1$
			xpathCallTemplate = xpath.compile("/xsl:stylesheet/xsl:template//xsl:call-template"); //$NON-NLS-1$
		} catch (XPathExpressionException e) {
			XSLCorePlugin.log(e);
		}
	}

	/**
	 * TODO: Add Javadoc
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
			NodeList includeNodes = (NodeList) xpathInclude.evaluate(document,XPathConstants.NODESET);
			for (int i = 0; i < includeNodes.getLength(); i++) {
				IDOMNode node = (IDOMNode) includeNodes.item(i);
				Attr att = (Attr) node.getAttributes().getNamedItem("href"); //$NON-NLS-1$
				String href = att == null ? null : att.getValue();
				Include inc = new Include(sf, href, Include.INCLUDE);
				configure(node, inc, document.getStructuredDocument());
				sf.addInclude(inc);
			}
			NodeList importNodes = (NodeList) xpathImport.evaluate(document,XPathConstants.NODESET);
			for (int i = 0; i < importNodes.getLength(); i++) {
				IDOMNode node = (IDOMNode) importNodes.item(i);
				Attr att = (Attr) node.getAttributes().getNamedItem("href"); //$NON-NLS-1$
				String href = att == null ? null : att.getValue();
				Include inc = new Include(sf, href, Include.IMPORT);
				configure(node, inc, document.getStructuredDocument());
				sf.addInclude(inc);
			}
			NodeList templateNodes = (NodeList) xpathTemplate.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < templateNodes.getLength(); i++) {
				IDOMNode node = (IDOMNode) templateNodes.item(i);
				Attr att = (Attr) node.getAttributes().getNamedItem("name"); //$NON-NLS-1$
				String name = att == null ? null : att.getValue();
				if (name != null) {
					Template template = new Template(sf, name);
					configure(node, template, document.getStructuredDocument());
					sf.addNamedTemplate(template);
				}
			}
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
