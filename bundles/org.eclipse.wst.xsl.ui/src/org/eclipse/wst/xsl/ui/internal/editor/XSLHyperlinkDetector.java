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
package org.eclipse.wst.xsl.ui.internal.editor;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.model.SourceFile;
import org.eclipse.wst.xsl.core.internal.model.Template;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * A hyperlink detector for XSL files.
 * 
 * The detector makes use of the model built by SSE validation (found in <code>XSLCore</code>).
 * 
 * Currently supports hyperlinking for includes, imports or called templates.
 * 
 * @author Doug Satchwell
 */
public class XSLHyperlinkDetector extends AbstractHyperlinkDetector
{
	/**
	 * Try to create hyperlinks for viewer and region
	 * 
	 * @see AbstractHyperlinkDetector
	 * @param textViewer 
	 * @param region 
	 * @param canShowMultipleHyperlinks 
	 * @return array of hyperlinks for current region
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks)
	{
		IHyperlink hyperlink = null;
		
		if (region != null && textViewer != null)
		{
			IDocument document = textViewer.getDocument();
			Node currentNode = getCurrentNode(document, region.getOffset());

			Element xslEl = null;
			Attr xslAttr = null;
			if (currentNode.getNodeType() == Node.ATTRIBUTE_NODE)
			{
				Attr att = (Attr) currentNode;
				if (XSLCore.XSL_NAMESPACE_URI.equals(att.getOwnerElement().getNamespaceURI()))
				{
					xslEl = att.getOwnerElement();
					xslAttr = att;
				}
			}
			else if (currentNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element el = (Element)currentNode;
				if (XSLCore.XSL_NAMESPACE_URI.equals(el.getNamespaceURI()))
				{
					xslEl = el;
					xslAttr = getCurrentAttrNode(el, region.getOffset());
				}
			}
			// try to create hyperlink from information gathered
			if (xslEl != null && xslAttr != null)
			{
				IRegion hyperlinkRegion = getHyperlinkRegion(xslAttr);
				IFile file = getFileForDocument(document);
				if (file != null)
				{
					if ("call-template".equals(xslEl.getLocalName()) && "name".equals(xslAttr.getLocalName()))
					{
						hyperlink = createCallTemplateHyperLink(file,xslAttr.getValue(), hyperlinkRegion);
					}
					else if (("include".equals(xslEl.getLocalName()) || "import".equals(xslEl.getLocalName()))
						&& "href".equals(xslAttr.getLocalName()))
					{
						hyperlink = createIncludeHyperLink(file,xslAttr.getValue(), hyperlinkRegion);
					}
				}
			}
		}
		return hyperlink == null ? null : new IHyperlink[]{hyperlink};
	}

	private IHyperlink createIncludeHyperLink(IFile currentFile, String include, IRegion hyperlinkRegion)
	{
		IHyperlink hyperlink = null;
		IPath path = new Path(include);
		IFile linkedFile = currentFile.getProject().getFile(path);
		if (linkedFile.exists())
			hyperlink = new SourceFileHyperlink(hyperlinkRegion,linkedFile);
		return hyperlink;
	}

	private IHyperlink createCallTemplateHyperLink(IFile currentFile, String templateName, IRegion hyperlinkRegion)
	{
		IHyperlink hyperlink = null;
		SourceFile sf = XSLCore.getInstance().getSourceFile(currentFile);
		if (sf != null)
		{
			Map<String,List<Template>> map = sf.calculateTemplates();
			List<Template> templates = map.get(templateName);
			if (templates != null && templates.size()>0)
			{
				Template template = templates.get(0);
				hyperlink = new SourceFileHyperlink(hyperlinkRegion,template.getSourceFile().getFile(),template);
			}
		}
		return hyperlink;
	}

	private Attr getCurrentAttrNode(Node node, int offset)
	{
		if ((node instanceof IndexedRegion) && ((IndexedRegion) node).contains(offset) && (node.hasAttributes()))
		{
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); ++i)
			{
				IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
				if (attRegion.contains(offset))
				{
					return (Attr) attrs.item(i);
				}
			}
		}
		return null;
	}

	private Node getCurrentNode(IDocument document, int offset)
	{
		IndexedRegion inode = null;
		IStructuredModel sModel = null;
		try
		{
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			inode = sModel.getIndexedRegion(offset);
			if (inode == null)
				inode = sModel.getIndexedRegion(offset - 1);
		}
		finally
		{
			if (sModel != null)
				sModel.releaseFromRead();
		}

		if (inode instanceof Node)
		{
			return (Node) inode;
		}
		return null;
	}

	private IRegion getHyperlinkRegion(Node node)
	{
		IRegion hyperRegion = null;

		if (node != null)
		{
			short nodeType = node.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE)
			{
				// handle doc type node
				IDOMNode docNode = (IDOMNode) node;
				hyperRegion = new Region(docNode.getStartOffset(), docNode.getEndOffset() - docNode.getStartOffset());
			}
			else if (nodeType == Node.ATTRIBUTE_NODE)
			{
				// handle attribute nodes
				IDOMAttr att = (IDOMAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				ITextRegion valueRegion = att.getValueRegion();
				if (valueRegion != null)
				{
					int regLength = valueRegion.getTextLength();
					String attValue = att.getValueRegionText();
					if (StringUtils.isQuoted(attValue))
					{
						++regOffset;
						regLength = regLength - 2;
					}
					hyperRegion = new Region(regOffset, regLength);
				}
			}
		}
		return hyperRegion;
	}
	
	private IFile getFileForDocument(IDocument document)
	{
		IFile file = null;
		IStructuredModel sModel = null;
		try
		{
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (sModel != null)
			{
				IPath path = new Path(sModel.getBaseLocation());
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			}
		}
		finally
		{
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return file != null && file.exists() ? file : null;
	}

}
