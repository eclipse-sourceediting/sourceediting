/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 287499 - add XML Catalog Resolution for a file.
 *******************************************************************************/
package org.eclipse.wst.xsl.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xsl.core.internal.model.StylesheetBuilder;
import org.eclipse.wst.xsl.core.internal.util.FileUtil;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The interface to all aspects of the XSL core functionality.
 * <p>
 * This is responsible for building and maintaining the cache of built XSL
 * models.
 * </p>
 * 
 * @author Doug Satchwell
 */
public class XSLCore {
	/**
	 * The XSL namespace URI (= http://www.w3.org/1999/XSL/Transform)
	 */
	public static final String XSL_NAMESPACE_URI = "http://www.w3.org/1999/XSL/Transform"; //$NON-NLS-1$

	/**
	 * The XSL content type (= org.eclipse.wst.xml.core.xslsource)
	 */
	public static final String XSL_CONTENT_TYPE = "org.eclipse.wst.xml.core.xslsource"; //$NON-NLS-1$

	private static XSLCore instance;
	private Map<IFile, StylesheetModel> stylesheetsComposed = new HashMap<IFile, StylesheetModel>();

	private XSLCore() {
	}

	/**
	 * Get the cached stylesheet, or build it if it has not yet been built.
	 * 
	 * @param file
	 * @return source file, or null if could not be built
	 * @since 1.0
	 */
	public synchronized StylesheetModel getStylesheet(IFile file) {
		StylesheetModel stylesheet = stylesheetsComposed.get(file);
		if (stylesheet == null)
			stylesheet = buildStylesheet(file);
		return stylesheet;
	}

	/**
	 * Completely rebuild the source file from its DOM
	 * 
	 * @param file
	 * @return the stylesheet model, or null if it could not be created.
	 * @since 1.0
	 */
	public synchronized StylesheetModel buildStylesheet(IFile file) {
		Stylesheet stylesheet = StylesheetBuilder.getInstance().getStylesheet(
				file, true);
		if (stylesheet == null)
			return null;
		StylesheetModel stylesheetComposed = new StylesheetModel(stylesheet);
		stylesheetsComposed.put(file, stylesheetComposed);
		stylesheetComposed.fix();
		return stylesheetComposed;
	}

	/**
	 * Clean all of the stylesheets from the given project.
	 * 
	 * @param project
	 *            the project to be cleaned
	 * @param monitor
	 *            a progress monitor to track the clean progress
	 */
	public synchronized void clean(IProject project, IProgressMonitor monitor) {
		for (Iterator<StylesheetModel> iter = stylesheetsComposed.values()
				.iterator(); iter.hasNext();) {
			StylesheetModel model = iter.next();
			if (project == null
					|| project.equals(model.getStylesheet().getFile()
							.getProject())) {
				iter.remove();
			}
		}
	}

	/**
	 * Get the singleton <code>XSLCore</code> instance.
	 * 
	 * @return the <code>XSLCore</code> instance
	 */
	public static synchronized XSLCore getInstance() {
		if (instance == null)
			instance = new XSLCore();
		return instance;
	}

	/**
	 * Locates a file for the given current file and URI.
	 * 
	 * @param currentFile
	 *            the file to resolve relative to
	 * @param uri
	 *            the relative URI
	 * @return the file at the URI relative to this <code>currentFile</code>
	 */
	public static IFile resolveFile(IFile currentFile, String uri) {		
		if (uri == null || uri.trim().length() == 0)
			return null;		
		IResource resource = currentFile.getParent().findMember(new Path(uri));
		if (resource == null) {
			String baseURI = currentFile.getRawLocationURI().toString();
			String resolvedURI = URIResolverPlugin.createResolver().resolve(baseURI, "", uri);		 //$NON-NLS-1$
			if (resolvedURI != null) {
				resource = currentFile.getParent().findMember(new Path(uri));
			}
		}
		if (resource == null || resource.getType() != IResource.FILE)
			return null;
		return (IFile) resource;
	}

	/**
	 * Determine whether the given file is an XML file by inspecting its content
	 * types.
	 * 
	 * @param file
	 *            the file to inspect
	 * @return true if this file is an XML file
	 */

	public static boolean isXMLFile(IFile file) {
		return FileUtil.isXMLFile(file);
	}

	/**
	 * Determine whether the given file is an XSL file by inspecting its content
	 * types.
	 * 
	 * @param file
	 *            the file to inspect
	 * @return true if this file is an XSL file
	 */
	public static boolean isXSLFile(IFile file) {
		return FileUtil.isXSLFile(file);
	}

	/**
	 * Takes a given <code>Node</code> and returns whether it is part of the the
	 * XSLT Namespace.
	 * 
	 * @param node
	 *            The Node to be checked.
	 * @return True if part of the XSLT namespace, false otherwise.
	 * @since 1.0
	 */
	public static boolean isXSLNamespace(Node node) {
		if (hasNamespace(node)) {
			return false;
		}
		return node.getNamespaceURI().equals(XSL_NAMESPACE_URI);
	}

	/**
	 * Determine if the Node that was passed has a Namespace. If it doesn't the
	 * node is either going to be false, or the call to the getNamespace()
	 * method will return null.
	 * 
	 * @param node
	 * @return
	 */
	private static boolean hasNamespace(Node node) {
		return node == null || node.getNamespaceURI() == null;
	}

	/**
	 * Returns an Attr node for the current Node if one exits at the specified
	 * offset.
	 * 
	 * @param node
	 * @param offset
	 * @return A w3c.dom.Attr
	 * @since 1.0
	 */
	public static Attr getCurrentAttrNode(Node node, int offset) {
		if ((node instanceof IndexedRegion)
				&& ((IndexedRegion) node).contains(offset)
				&& (node.hasAttributes())) {
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); ++i) {
				IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
				if (attRegion.contains(offset)) {
					return (Attr) attrs.item(i);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the current Node at the specified offset.
	 * 
	 * @param document
	 * @param offset
	 * @return an w3c.dom.Node
	 * @since 1.0
	 */
	public static Node getCurrentNode(IDocument document, int offset) {
		IndexedRegion inode = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			inode = sModel.getIndexedRegion(offset);
			if (inode == null)
				inode = sModel.getIndexedRegion(offset - 1);
		} finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}

		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}

}
