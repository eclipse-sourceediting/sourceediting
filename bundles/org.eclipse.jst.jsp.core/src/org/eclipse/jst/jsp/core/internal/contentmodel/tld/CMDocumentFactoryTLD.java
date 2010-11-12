/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP20TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDFunction;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDInitParam;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDListener;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDValidator;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDVariable;
import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.JSP20Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.core.internal.util.DocumentProvider;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITLDRecord;
import org.eclipse.jst.jsp.core.taglib.ITagDirRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.factory.CMDocumentFactory;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * CMDocumentBuilder for Tag Library Descriptors and tag files
 * 
 * Returns namespace-less CMDocuments for a tag library descriptor, loading it
 * directly from a file or extracted from a JAR archive. Content Model objects
 * will implement the TLDCMDocument, TLDElementDeclaration, and
 * TLDAttributeDeclaration interfaces for extended properties.
 */
public class CMDocumentFactoryTLD implements CMDocumentFactory {
	static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/tldcmdocument/factory")); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * CMDocumentFactoryTLD constructor comment.
	 */
	public CMDocumentFactoryTLD() {
		super();
	}

	/**
	 * NOT API
	 * 
	 * @param baselocation
	 * @param input
	 * @return
	 */
	public CMDocument buildCMDocument(String baselocation, InputStream input) {
		DocumentProvider provider = new DocumentProvider();
		provider.setValidating(false);
		provider.setRootElementName(JSP11TLDNames.TAGLIB);
		provider.setInputStream(input);
		if (baselocation != null)
			provider.setBaseReference(baselocation);
		return loadDocument(baselocation, provider.getRootElement());
	}

	/**
	 * @param fileName
	 * @return
	 */
	private CMDocumentImpl buildCMDocumentFromFolder(IPath path) {
		if (_debug) {
			System.out.println("tagdir loading for " + path); //$NON-NLS-1$
		}
		// EBNF is listed at 1.3.10
		CMDocumentImpl document = new CMDocumentImpl();
		document.setBaseLocation(path.toString());
		document.setTlibversion("1.0"); //$NON-NLS-1$
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		IResource[] tagfiles;
		try {
			tagfiles = folder.members();
			for (int i = 0; i < tagfiles.length; i++) {
				if (tagfiles[i].getType() == IResource.FILE) {
					if (tagfiles[i].getType() != IResource.FILE)
						continue;
					String extension = tagfiles[i].getFileExtension();
					if (extension != null && (extension.equals("tag") || extension.equals("tagx"))) {
						CMElementDeclaration ed = createElementDeclaration(document, (IFile) tagfiles[i]);
						if (ed != null) {
							document.fElements.setNamedItem(ed.getNodeName(), ed);
						}
					}
				}
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		return document;
	}

	/**
	 * NOT API
	 * 
	 * @param fileName
	 * @return
	 */
	protected CMDocument buildCMDocumentFromFile(String fileName) {
		// load the taglib descriptor file
		DocumentProvider provider = new DocumentProvider();
		provider.setValidating(false);
		provider.setBaseReference(fileName);
		provider.setRootElementName(JSP11TLDNames.TAGLIB);
		provider.setFileName(fileName);
		Node rootElement = provider.getRootElement();
		return loadDocument(fileName, rootElement);
	}

	/**
	 * Builds a CMDocument assuming the JSP v1.1 default path
	 * 
	 * @param jarFileName -
	 *            the name of the containing JAR file
	 */
	protected CMDocument buildCMDocumentFromJar(String jarFileName) {
		// load the taglib descriptor file
		return buildCMDocumentFromJar(jarFileName, JarUtilities.JSP11_TAGLIB);
	}

	/**
	 * Builds a CMDocument
	 * 
	 * @param jarFileName -
	 *            the name of the containing JAR file
	 * @param contentFileName -
	 *            the path within the JAR for a valid taglib descriptor
	 */
	protected CMDocument buildCMDocumentFromJar(String jarFileName, String contentFileName) {
		// load the taglib descriptor file
		DocumentProvider provider = new DocumentProvider();
		provider.setValidating(false);
		provider.setBaseReference(jarFileName);
		provider.setRootElementName(JSP11TLDNames.TAGLIB);
		provider.setJarFileName(jarFileName);
		provider.setFileName(contentFileName);
		CMDocument document = loadDocument("jar:file://" + jarFileName + "!" + contentFileName, provider.getRootElement()); //$NON-NLS-1$ //$NON-NLS-2$
		// TODO: Add the tags declared in META-INF/tags, see JSP 2.0 section
		// 8.4.1
		return document;
	}

	protected CMAttributeDeclaration createAttributeDeclaration(CMDocument document, Node attrNode) {
		CMAttributeDeclarationImpl attr = new CMAttributeDeclarationImpl(document);

		Node child = attrNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals(JSP11TLDNames.NAME) && child.hasChildNodes()) {
					attr.setNodeName(getContainedText(child));
				}
				else if (child.getNodeName().equals(JSP12TLDNames.DESCRIPTION) && child.hasChildNodes()) {
					attr.setDescription(getContainedText(child));
				}
				else if (child.getNodeName().equals(JSP11TLDNames.ID) && child.hasChildNodes()) {
					attr.setId(getContainedText(child));
				}
				else if (child.getNodeName().equals(JSP11TLDNames.REQUIRED) && child.hasChildNodes()) {
					attr.setRequiredString(getContainedText(child));
				}
				else if (child.getNodeName().equals(JSP11TLDNames.RTEXPRVALUE) && child.hasChildNodes()) {
					attr.setRtexprvalue(getContainedText(child));
				}
				else if (child.getNodeName().equals(JSP20TLDNames.FRAGMENT) && child.hasChildNodes()) {
					attr.setFragment(Boolean.valueOf(getContainedText(child)).booleanValue());
				}
			}
			child = child.getNextSibling();
		}

		return attr;
	}

	/**
	 * Builds a CMDocument from a taglib descriptor
	 * 
	 * @param uri -
	 *            the location of a valid taglib descriptor
	 */
	public CMDocument createCMDocument(String uri) {
		CMDocument result = null;
		URL url = null;
		try {
			url = new URL(uri);
		}
		catch (MalformedURLException e) {
			result = createCMDocumentFromFile(uri);
		}
		if (result == null && url != null) {
			if (url.getProtocol().equals("file")) { //$NON-NLS-1$
				result = createCMDocumentFromFile(url.getFile());
			}
			else {
				/**
				 * Skip anything else since trying to load a TLD from a remote
				 * location has unpredictable performance impact.
				 */
			}
		}
		if (result == null)
			result = new CMDocumentImpl();
		return result;
	}

	/**
	 * @param fileName
	 * @return
	 */
	private CMDocument createCMDocumentFromFile(String fileName) {
		CMDocument result = null;
		if (fileName.endsWith(".jar")) { //$NON-NLS-1$
			result = buildCMDocumentFromJar(fileName);
		}
		else {
			File file = new File(fileName);
			try {
				if (file.isDirectory()) {
					result = buildCMDocumentFromDirectory(file);
				}
				else {
					result = buildCMDocumentFromFile(fileName);
				}
			}
			catch (SecurityException e) {
				result = null;
			}
		}
		return result;
	}

	private CMDocument buildCMDocumentFromDirectory(File file) {
		IFile[] foundFilesForLocation = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(file.getPath()));
		for (int i = 0; i < foundFilesForLocation.length; i++) {
			if (foundFilesForLocation[i].isAccessible() && foundFilesForLocation[i].getType() == IResource.FOLDER) {
				return buildCMDocumentFromFolder(foundFilesForLocation[i].getFullPath());
			}
		}
		return null;
	}

	protected CMElementDeclaration createElementDeclaration(CMDocumentImpl document, Element tagFileNode, String path) {
		CMElementDeclarationImpl ed = new CMElementDeclarationImpl(document);
		/*
		 * Preload with information from the tag file--it can be overwritten
		 * by the values from the TLD
		 */
		IPath tagPath = FacetModuleCoreSupport.resolve(new Path(document.getBaseLocation()), path);
		if (tagPath.segmentCount() > 1) {
			IFile tagFile = ResourcesPlugin.getWorkspace().getRoot().getFile(tagPath);
			if (tagFile.isAccessible()) {
				ed.setPath(tagFile.getFullPath().toString());
				if (tagPath.getFileExtension().equals("tag")) {
					loadTagFile(ed, tagFile, true);
				}
				else if (tagPath.getFileExtension().equals("tagx")) {
					loadTagXFile(ed, tagFile, true);
				}

				if (tagFile.getLocation() != null && ed.getSmallIcon() != null) {
					ed.setSmallIconURL(URIHelper.normalize(ed.getSmallIcon(), "file:" + tagFile.getLocation().toString(), tagFile.getLocation().removeLastSegments(1).toString()));
				}
			}
			else if (isJarFile(document.getBaseLocation())) {
				String jarLocation = document.getBaseLocation();
				String[] entries = JarUtilities.getEntryNames(jarLocation);
				boolean tag;
				for (int jEntry = 0; jEntry < entries.length; jEntry++) {
					tag = false;
					if (((tag = entries[jEntry].endsWith(".tag")) || entries[jEntry].endsWith(".tagx")) && entries[jEntry].startsWith("META-INF/tags/")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							InputStream contents = JarUtilities.getInputStream(jarLocation, entries[jEntry]);
							if (tag) {//$NON-NLS-1$ 
								loadTagFile(ed, tagFile, true, contents);
							}
							else {
								loadTagXFile(ed, tagFile, true, contents);
							}
							try {
								contents.close();
							}
							catch (IOException e) {
								Logger.log(Logger.ERROR_DEBUG, null, e);
							}
						}
					}	
			}
		}

		// load information declared within the .tld
		Node child = tagFileNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP12TLDNames.DESCRIPTION) && child.hasChildNodes()) {
					ed.setDescription(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.DISPLAY_NAME) && child.hasChildNodes()) {
					ed.setDisplayName(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.EXAMPLE) && child.hasChildNodes()) {
					ed.setExample(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.ICON) && child.hasChildNodes()) {
					ed.setSmallIcon(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.NAME) && child.hasChildNodes()) {
					ed.setNodeName(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.PATH) && child.hasChildNodes()) {
					ed.setPath(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.TAG_EXTENSION)) {
					ed.getExtensions().add(child);
				}
			}
			child = child.getNextSibling();
		}
		
		return ed;
	}

	private boolean isJarFile(String path) {
		if (path == null)
			return false;
		final int idx = path.lastIndexOf('.');
		return idx >= 0 && idx < (path.length() - 1) && path.substring(idx + 1).equalsIgnoreCase("jar"); //$NON-NLS-1$
	}

	protected CMElementDeclaration createElementDeclaration(CMDocument document, Node tagNode) {
		CMElementDeclarationImpl ed = new CMElementDeclarationImpl(document);

		Node child = tagNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				// tag information
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP11TLDNames.NAME) && child.hasChildNodes()) {
					ed.setNodeName(getContainedText(child));
				}
				else if ((nodeName.equals(JSP11TLDNames.TAGCLASS) || nodeName.equals(JSP12TLDNames.TAG_CLASS)) && child.hasChildNodes()) {
					ed.setTagclass(getContainedText(child));
				}
				else if ((nodeName.equals(JSP11TLDNames.TEICLASS) || nodeName.equals(JSP12TLDNames.TEI_CLASS)) && child.hasChildNodes()) {
					ed.setTeiclass(getContainedText(child));
				}
				else if ((nodeName.equals(JSP11TLDNames.BODYCONTENT) || nodeName.equals(JSP12TLDNames.BODY_CONTENT)) && child.hasChildNodes()) {
					String bodycontent = getContainedText(child);
					// Apparently, Apache Tomcat is not case sensitive about
					// these values
					if (bodycontent.equalsIgnoreCase(JSP11TLDNames.CONTENT_JSP))
						ed.setBodycontent(JSP11TLDNames.CONTENT_JSP);
					else if (bodycontent.equalsIgnoreCase(JSP11TLDNames.CONTENT_TAGDEPENDENT))
						ed.setBodycontent(JSP11TLDNames.CONTENT_TAGDEPENDENT);
					else if (bodycontent.equalsIgnoreCase(JSP11TLDNames.CONTENT_EMPTY))
						ed.setBodycontent(JSP11TLDNames.CONTENT_EMPTY);
					else if (bodycontent.equalsIgnoreCase(JSP20TLDNames.CONTENT_SCRIPTLESS))
						ed.setBodycontent(JSP20TLDNames.CONTENT_SCRIPTLESS);
				}
				// info (1.1 only) or description (1.2 only)
				else if ((nodeName.equals(JSP11TLDNames.INFO) || nodeName.equals(JSP12TLDNames.DESCRIPTION)) && child.hasChildNodes()) {
					ed.setDescription(getContainedText(child));
				}
				// attributes
				else if (nodeName.equals(JSP11TLDNames.ATTRIBUTE)) {
					CMAttributeDeclaration attr = createAttributeDeclaration(document, child);
					ed.fAttributes.setNamedItem(attr.getAttrName(), attr);
				}
				// variables
				else if (nodeName.equals(JSP12TLDNames.VARIABLE)) {
					ed.getVariables().add(createVariable(child));
				}
				else if (nodeName.equals(JSP12TLDNames.LARGE_ICON) && child.hasChildNodes()) {
					ed.setLargeIcon(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.SMALL_ICON) && child.hasChildNodes()) {
					ed.setSmallIcon(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.TAG_EXTENSION) && child.getNodeType() == Node.ELEMENT_NODE) {
					ed.getExtensions().add(child);
				}
				else if (nodeName.equals(JSP20TLDNames.DYNAMIC_ATTRIBUTES) && child.hasChildNodes()) {
					ed.setDynamicAttributes(getContainedText(child));
				}
			}
			child = child.getNextSibling();
		}
		return ed;
	}

	private CMElementDeclaration createElementDeclaration(CMDocument document, IFile tagFile) {
		CMElementDeclarationImpl ed = new CMElementDeclarationImpl(document);
		// in tag files, the default body content is scriptless instead of JSP
		ed.setBodycontent(JSP20TLDNames.CONTENT_SCRIPTLESS);
		String shortFilename = tagFile.getName();
		String fileExtension = tagFile.getFileExtension();
		if (fileExtension != null && fileExtension.length() > 0) {
			shortFilename = shortFilename.substring(0, shortFilename.length() - fileExtension.length() - 1);
		}
		ed.setNodeName(shortFilename);
		ed.setPath(tagFile.getFullPath().toString());
		if (fileExtension.equals("tag")) {
			loadTagFile(ed, tagFile, true);
		}
		else if (fileExtension.equals("tagx")) {
			loadTagXFile(ed, tagFile, true);
		}

		if (tagFile.getLocation() != null && ed.getSmallIcon() != null) {
			ed.setSmallIconURL(URIHelper.normalize(ed.getSmallIcon(), "file:" + tagFile.getLocation().toString(), tagFile.getLocation().removeLastSegments(1).toString()));
		}
		return ed;
	}

	protected TLDFunction createFunction(CMDocument document, Node functionNode) {
		TLDFunctionImpl function = new TLDFunctionImpl(document);
		boolean hasName = false;

		Node child = functionNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				// tag information
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP12TLDNames.DESCRIPTION) && child.hasChildNodes()) {
					function.setDescription(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.DISPLAY_NAME) && child.hasChildNodes()) {
					function.setName(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.EXAMPLE) && child.hasChildNodes()) {
					function.setExample(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.FUNCTION_CLASS) && child.hasChildNodes()) {
					function.setClassName(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.FUNCTION_EXTENSION) && child.hasChildNodes()) {
					function.getExtensions().add(child);
				}
				else if (nodeName.equals(JSP20TLDNames.FUNCTION_SIGNATURE) && child.hasChildNodes()) {
					function.setSignature(getContainedText(child));
				}
				else if (nodeName.equals(JSP20TLDNames.ICON) && child.hasChildNodes()) {
					function.setIcon(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.NAME) && child.hasChildNodes()) {
					function.setName(getContainedText(child));
					hasName = function.getName().trim().length() > 0;
				}
			}
			child = child.getNextSibling();
		}
		if (hasName) {
			return function;
		}
		return null;
	}

	protected TLDInitParam createInitParam(Node initParamNode) {
		TLDInitParamImpl initParam = new TLDInitParamImpl();
		Node child = initParamNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP12TLDNames.VALIDATOR_PARAM_NAME) && child.hasChildNodes()) {
					initParam.setName(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.VALIDATOR_PARAM_VALUE) && child.hasChildNodes()) {
					initParam.setValue(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.DESCRIPTION) && child.hasChildNodes()) {
					initParam.setDescription(getContainedText(child));
				}
			}
			child = child.getNextSibling();
		}
		return initParam;
	}

	protected TLDListener createListener(Node listenerNode) {
		TLDListenerImpl listener = new TLDListenerImpl();
		Node child = listenerNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP12TLDNames.LISTENER_CLASS) && child.hasChildNodes()) {
					listener.setListenerClass(getContainedText(child));
				}
			}
			child = child.getNextSibling();
		}
		return listener;
	}

	protected TLDValidator createValidator(Node validatorNode) {
		TLDValidatorImpl validator = new TLDValidatorImpl();
		Node child = validatorNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP12TLDNames.VALIDATOR_CLASS) && child.hasChildNodes()) {
					validator.setValidatorClass(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.VALIDATOR_INIT_PARAM) && child.hasChildNodes()) {
					validator.getInitParams().add(createInitParam(child));
				}
			}
			child = child.getNextSibling();
		}
		return validator;
	}

	protected TLDVariable createVariable(Node variableNode) {
		TLDVariableImpl variable = new TLDVariableImpl();
		Node child = variableNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP12TLDNames.VARIABLE_CLASS) && child.hasChildNodes()) {
					variable.setVariableClass(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.VARIABLE_DECLARE) && child.hasChildNodes()) {
					variable.setDeclareString(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.VARIABLE_NAME_FROM_ATTRIBUTE) && child.hasChildNodes()) {
					variable.setNameFromAttribute(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.VARIABLE_NAME_GIVEN) && child.hasChildNodes()) {
					variable.setNameGiven(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.VARIABLE_SCOPE) && child.hasChildNodes()) {
					variable.setScope(getContainedText(child));
				}
				else if (nodeName.equals(JSP12TLDNames.DESCRIPTION) && child.hasChildNodes()) {
					variable.setDescription(getContainedText(child));
				}
			}
			child = child.getNextSibling();
		}
		return variable;
	}

	protected String getContainedText(Node parent) {
		NodeList children = parent.getChildNodes();
		if (children.getLength() == 1) {
			return getValue(children.item(0));
		}
		StringBuffer s = new StringBuffer();
		Node child = parent.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
				String reference = ((EntityReference) child).getNodeValue();
				if (reference == null && child.getNodeName() != null) {
					reference = "&" + child.getNodeName() + ";"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (reference != null) {
					s.append(reference.trim());
				}
			}
			else {
				s.append(getValue(child));
			}
			child = child.getNextSibling();
		}
		return s.toString().trim();
	}

	private String getValue(Node n) {
		if (n == null)
			return ""; //$NON-NLS-1$
		String value = n.getNodeValue();
		if (value == null)
			return ""; //$NON-NLS-1$
		return value.trim();
	}

	public boolean isBuilderForGrammar(String grammarFileName) {
		String fileName = grammarFileName.toLowerCase();
		return fileName.endsWith(".tld") || fileName.endsWith(".jar"); //$NON-NLS-2$//$NON-NLS-1$
	}

	private CMDocument loadDocument(String baseLocation, Node taglib) {
		Node root = taglib;

		// create the CMDocument
		CMDocumentImpl document = new CMDocumentImpl();
		document.setBaseLocation(baseLocation);

		if (root == null) {
			if (_debug) {
				System.out.println("null \"taglib\" element for TLD " + baseLocation); //$NON-NLS-1$
			}
			return document;
		}

		// populate the CMDocument
		Node child = root.getFirstChild();
		while (child != null) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				child = child.getNextSibling();
				continue;
			}
			String nodeName = child.getNodeName();
			if(nodeName.indexOf(':') > 0) {
				nodeName = nodeName.substring(nodeName.indexOf(':'));
			}
			// tag
			if (nodeName.equals(JSP11TLDNames.TAG)) {
				CMElementDeclaration ed = createElementDeclaration(document, child);
				if (ed != null) {
					document.fElements.setNamedItem(ed.getNodeName(), ed);
				}
			}
			// tag-file
			else if (nodeName.equals(JSP20TLDNames.TAG_FILE) && child.getNodeType() == Node.ELEMENT_NODE && child.hasChildNodes()) {
				Element tagFileElement = (Element) child;
				Node path = tagFileElement.getFirstChild();
				while (path != null) {
					if (path.getNodeType() == Node.ELEMENT_NODE && (JSP20TLDNames.PATH.equals(path.getNodeName()) || JSP20TLDNames.PATH.equals(path.getLocalName()))) {
						String pathValue = getContainedText(path);
						if (pathValue != null && pathValue.length() > 0) {
							CMElementDeclarationImpl ed = (CMElementDeclarationImpl) createElementDeclaration(document, tagFileElement, pathValue);
							if (ed != null) {
								document.fElements.setNamedItem(ed.getNodeName(), ed);
							}
						}
					}
					path = path.getNextSibling();
				}
			}
			// other one-of-a-kind children
			// JSP version
			else if ((nodeName.equals(JSP11TLDNames.JSPVERSION) || nodeName.equals(JSP12TLDNames.JSP_VERSION)) && child.hasChildNodes()) {
				document.setJspversion(getContainedText(child));
			}
			// tag library version
			else if ((nodeName.equals(JSP11TLDNames.TLIBVERSION) || nodeName.equals(JSP12TLDNames.TLIB_VERSION)) && child.hasChildNodes()) {
				document.setTlibversion(getContainedText(child));
			}
			// short name
			else if ((nodeName.equals(JSP11TLDNames.SHORTNAME) || nodeName.equals(JSP12TLDNames.SHORT_NAME)) && child.hasChildNodes()) {
				document.setShortname(getContainedText(child));
			}
			// URI/URN
			else if ((nodeName.equals(JSP11TLDNames.URI) || nodeName.equals(JSP11TLDNames.URN)) && child.hasChildNodes()) { //$NON-NLS-1$
				document.setUri(getContainedText(child));
			}
			// info
			else if (nodeName.equals(JSP11TLDNames.INFO) && child.hasChildNodes()) {
				document.setInfo(getContainedText(child));
			}
			// New JSP 1.2
			// description
			else if (nodeName.equals(JSP12TLDNames.DESCRIPTION) && child.hasChildNodes()) {
				document.setDescription(getContainedText(child));
			}
			// display name
			else if (nodeName.equals(JSP12TLDNames.DISPLAY_NAME) && child.hasChildNodes()) {
				document.setDisplayName(getContainedText(child));
			}
			// large icon
			else if (nodeName.equals(JSP12TLDNames.LARGE_ICON) && child.hasChildNodes()) {
				document.setLargeIcon(getContainedText(child));
			}
			// small icon
			else if (nodeName.equals(JSP12TLDNames.SMALL_ICON) && child.hasChildNodes()) {
				document.setSmallIcon(getContainedText(child));
			}
			// validator
			else if (nodeName.equals(JSP12TLDNames.VALIDATOR)) {
				document.setValidator(createValidator(child));
			}
			// listener
			else if (nodeName.equals(JSP12TLDNames.LISTENER)) {
				document.getListeners().add(createListener(child));
			}
			else if (nodeName.equals(JSP20TLDNames.FUNCTION)) {
				TLDFunction function = createFunction(document, child);
				if (function != null) {
					document.getFunctions().add(function);
				}
			}
			else if (nodeName.equals(JSP20TLDNames.TAGLIB_EXTENSION)) {
				document.getExtensions().add(child);
			}

			child = child.getNextSibling();
		}
		return document;
	}

	void loadTagXFile(final CMElementDeclarationImpl ed, IFile tagxFile, boolean allowIncludes) {
	  loadTagXFile(ed, tagxFile, allowIncludes, null);
	}
	
	void loadTagXFile(final CMElementDeclarationImpl ed, IFile tagxFile, boolean allowIncludes, InputStream inputStream) {
		ed.setPath(tagxFile.getFullPath().toString());
		ed.setTagSource(TLDElementDeclaration.SOURCE_TAG_FILE);
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			InputSource inputSource = new InputSource(tagxFile.getFullPath().toString());
			InputStream input = inputStream != null ? inputStream : tagxFile.getContents(false);
			inputSource.setByteStream(input);
			parser.parse(inputSource, new DefaultHandler() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
					InputSource inputSource2 = new InputSource(systemId);
					inputSource2.setByteStream(new ByteArrayInputStream(new byte[0]));
					return inputSource2;
				}

				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					super.startElement(uri, localName, qName, attributes);
					if (qName.equals(JSP20Namespace.ElementName.DIRECTIVE_TAG)) {
						if (attributes.getIndex(JSP12TLDNames.DISPLAY_NAME) >= 0)
							ed.setDisplayName(attributes.getValue(JSP12TLDNames.DISPLAY_NAME));
						if (attributes.getIndex(JSP12TLDNames.BODY_CONTENT) >= 0)
							ed.setBodycontent(attributes.getValue(JSP12TLDNames.BODY_CONTENT));
						if (attributes.getIndex(JSP20TLDNames.DYNAMIC_ATTRIBUTES) >= 0)
							ed.setDynamicAttributes(attributes.getValue(JSP20TLDNames.DYNAMIC_ATTRIBUTES));
						if (attributes.getIndex(JSP12TLDNames.SMALL_ICON) >= 0)
							ed.setSmallIcon(attributes.getValue(JSP12TLDNames.SMALL_ICON));
						if (attributes.getIndex(JSP12TLDNames.LARGE_ICON) >= 0)
							ed.setLargeIcon(attributes.getValue(JSP12TLDNames.LARGE_ICON));
						if (attributes.getIndex(JSP12TLDNames.DESCRIPTION) >= 0)
							ed.setDescription(attributes.getValue(JSP12TLDNames.DESCRIPTION));
						if (attributes.getIndex(JSP20TLDNames.EXAMPLE) >= 0)
							ed.setExample(attributes.getValue(JSP20TLDNames.EXAMPLE));
						if (attributes.getIndex(JSP20TLDNames.SCRIPTING_LANGUAGE) >= 0)
							ed.setScriptingLanguage(attributes.getValue(JSP20TLDNames.SCRIPTING_LANGUAGE));
						if (attributes.getIndex(JSP20TLDNames.IMPORT) >= 0)
							ed.setImport(attributes.getValue(JSP20TLDNames.IMPORT));
						if (attributes.getIndex(JSP20TLDNames.PAGE_ENCODING) >= 0)
							ed.setPageEncoding(attributes.getValue(JSP20TLDNames.PAGE_ENCODING));
						if (attributes.getIndex(JSP20TLDNames.IS_EL_IGNORED) >= 0)
							ed.setIsELIgnored(attributes.getValue(JSP20TLDNames.IS_EL_IGNORED));
					}
					else if (qName.equals(JSP20Namespace.ElementName.DIRECTIVE_ATTRIBUTE)) {
						CMAttributeDeclarationImpl attribute = new CMAttributeDeclarationImpl(ed.getOwnerDocument());
						String nameValue = attributes.getValue(JSP12TLDNames.NAME);
						attribute.setNodeName(nameValue);
						if (attributes.getIndex(JSP20TLDNames.FRAGMENT) >= 0)
							attribute.setFragment(Boolean.valueOf(attributes.getValue(JSP20TLDNames.FRAGMENT)).booleanValue());
						if (attributes.getIndex(JSP12TLDNames.RTEXPRVALUE) >= 0)
							attribute.setRtexprvalue(attributes.getValue(JSP12TLDNames.RTEXPRVALUE));
						if (attributes.getIndex(JSP20TLDNames.TYPE) >= 0)
							attribute.setType(attributes.getValue(JSP20TLDNames.TYPE));
						if (attributes.getIndex(JSP12TLDNames.DESCRIPTION) >= 0)
							attribute.setDescription(attributes.getValue(JSP12TLDNames.DESCRIPTION));
						if (attributes.getIndex(JSP12TLDNames.REQUIRED) >= 0)
							attribute.setRequiredString(attributes.getValue(JSP12TLDNames.REQUIRED));
						if (nameValue != null && nameValue.length() > 0) {
							ed.fAttributes.setNamedItem(nameValue, attribute);
						}
					}
					else if (qName.equals(JSP20Namespace.ElementName.DIRECTIVE_VARIABLE)) {
						TLDVariableImpl variable = new TLDVariableImpl();
						if (attributes.getIndex(JSP12TLDNames.VARIABLE_NAME_GIVEN) >= 0)
							variable.setNameGiven(attributes.getValue(JSP12TLDNames.VARIABLE_NAME_GIVEN));
						if (attributes.getIndex(JSP12TLDNames.VARIABLE_NAME_FROM_ATTRIBUTE) >= 0)
							variable.setNameFromAttribute(attributes.getValue(JSP12TLDNames.VARIABLE_NAME_FROM_ATTRIBUTE));
						if (attributes.getIndex(JSP20TLDNames.VARIABLE_ALIAS) >= 0)
							variable.setAlias(attributes.getValue(JSP20TLDNames.VARIABLE_ALIAS));
						if (attributes.getIndex(JSP12TLDNames.VARIABLE_CLASS) >= 0)
							variable.setVariableClass(attributes.getValue(JSP12TLDNames.VARIABLE_CLASS));
						if (attributes.getIndex(JSP12TLDNames.VARIABLE_DECLARE) >= 0)
							variable.setDeclareString(attributes.getValue(JSP12TLDNames.VARIABLE_DECLARE));
						if (attributes.getIndex(JSP11Namespace.ATTR_NAME_SCOPE) >= 0)
							variable.setScope(attributes.getValue(JSP11Namespace.ATTR_NAME_SCOPE));
						if (attributes.getIndex(JSP12TLDNames.DESCRIPTION) >= 0)
							variable.setDescription(attributes.getValue(JSP12TLDNames.DESCRIPTION));
						if (variable.getAlias() != null || variable.getNameFromAttribute() != null || variable.getNameGiven() != null) {
							ed.getVariables().add(variable);
						}
					}
					else if (qName.equals(JSP11Namespace.ElementName.DIRECTIVE_INCLUDE)) {
						IPath filePath = null;
						String text = attributes.getValue(JSP11Namespace.ATTR_NAME_FILE);
						if (text != null) {
							filePath = FacetModuleCoreSupport.resolve(new Path(((CMDocumentImpl) ed.getOwnerDocument()).getBaseLocation()), text);
							IFile includedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
							if (includedFile.isAccessible()) {
								loadTagXFile(ed, includedFile, false);
							}
						}
					}
				}
			});
			input.close();
		}
		catch (ParserConfigurationException e) {
			Logger.log(Logger.ERROR_DEBUG, e.getMessage(), e);
		}
		catch (SAXException e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
		catch (IOException e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
		catch (CoreException e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
		ed.setLocationString(tagxFile.getFullPath().toString());
	}

	private void loadTagFile(CMElementDeclarationImpl ed, IFile tagFile, boolean allowIncludes) {
		loadTagFile(ed, tagFile, allowIncludes, null);
	}
	
	private void loadTagFile(CMElementDeclarationImpl ed, IFile tagFile, boolean allowIncludes, InputStream inputStream) {
		try {
			ed.setPath(tagFile.getFullPath().toString());
			ed.setTagSource(TLDElementDeclaration.SOURCE_TAG_FILE);
			ed.setLocationString(tagFile.getFullPath().toString());
			IStructuredDocument document = null;
			if(inputStream != null) {
				document = (IStructuredDocument)new ModelHandlerForJSP().getDocumentLoader().createNewStructuredDocument(tagFile.getName(), inputStream);
			}
			else if(tagFile.isAccessible()) {
				document = (IStructuredDocument) new ModelHandlerForJSP().getDocumentLoader().createNewStructuredDocument(tagFile);
			}
			if (document == null)
				return;
			IStructuredDocumentRegion documentRegion = document.getFirstStructuredDocumentRegion();
			while (documentRegion != null) {
				if (documentRegion.getType().equals(DOMJSPRegionContexts.JSP_DIRECTIVE_NAME)) {
					if (documentRegion.getNumberOfRegions() > 2) {
						ITextRegionList regions = documentRegion.getRegions();
						String directiveName = documentRegion.getText(regions.get(1));
						if (JSP12TLDNames.TAG.equals(directiveName)) {
							// 8.5.1
							String attrName = null;
							for (int i = 2; i < documentRegion.getNumberOfRegions(); i++) {
								ITextRegion region = regions.get(i);
								String text = documentRegion.getText(region);
								if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
									attrName = text;
								}
								// process value
								else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
									text = StringUtils.strip(text);
									if (JSP12TLDNames.DISPLAY_NAME.equals(attrName)) {
										ed.setDisplayName(text);
									}
									else if (JSP12TLDNames.BODY_CONTENT.equals(attrName)) {
										ed.setBodycontent(text);
									}
									else if (JSP20TLDNames.DYNAMIC_ATTRIBUTES.equals(attrName)) {
										ed.setDynamicAttributes(text);
									}
									else if (JSP12TLDNames.SMALL_ICON.equals(attrName)) {
										ed.setSmallIcon(text);
									}
									else if (JSP12TLDNames.LARGE_ICON.equals(attrName)) {
										ed.setLargeIcon(text);
									}
									else if (JSP12TLDNames.DESCRIPTION.equals(attrName)) {
										ed.setDescription(text);
									}
									else if (JSP20TLDNames.EXAMPLE.equals(attrName)) {
										ed.setExample(text);
									}
									else if (JSP20TLDNames.SCRIPTING_LANGUAGE.equals(attrName)) {
										ed.setScriptingLanguage(text);
									}
									else if (JSP20TLDNames.IMPORT.equals(attrName)) {
										ed.setImport(text);
									}
									else if (JSP20TLDNames.PAGE_ENCODING.equals(attrName)) {
										ed.setPageEncoding(text);
									}
									else if (JSP20TLDNames.IS_EL_IGNORED.equals(attrName)) {
										ed.setIsELIgnored(text);
									}
								}
							}
						}
						else if (JSP12TLDNames.ATTRIBUTE.equals(directiveName)) {
							CMAttributeDeclarationImpl attribute = new CMAttributeDeclarationImpl(ed.getOwnerDocument());
							// 8.5.2
							String attrName = null;
							for (int i = 2; i < documentRegion.getNumberOfRegions(); i++) {
								ITextRegion region = regions.get(i);
								String text = documentRegion.getText(region);
								if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
									attrName = text;
								}
								// process value
								else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE && attrName != null) {
									text = StringUtils.strip(text);
									if (JSP12TLDNames.NAME.equals(attrName)) {
										attribute.setNodeName(text);
									}
									else if (JSP20TLDNames.FRAGMENT.equals(attrName)) {
										attribute.setFragment(Boolean.valueOf(text).booleanValue());
									}
									else if (JSP12TLDNames.RTEXPRVALUE.equals(attrName)) {
										attribute.setRtexprvalue(text);
									}
									else if (JSP20TLDNames.TYPE.equals(attrName)) {
										attribute.setType(text);
									}
									else if (JSP12TLDNames.DESCRIPTION.equals(attrName)) {
										attribute.setDescription(text);
									}
									else if (JSP12TLDNames.REQUIRED.equals(attrName)) {
										attribute.setRequiredString(text);
									}
								}
							}
							if (attribute.getNodeName() != null) {
								ed.fAttributes.setNamedItem(attribute.getNodeName(), attribute);
							}
						}
						else if (JSP12TLDNames.VARIABLE.equals(directiveName)) {
							TLDVariableImpl variable = new TLDVariableImpl();
							String attrName = null;
							for (int i = 2; i < documentRegion.getNumberOfRegions(); i++) {
								ITextRegion region = regions.get(i);
								String text = documentRegion.getText(region);
								if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
									attrName = text;
								}
								// process value
								else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE && attrName != null) {
									text = StringUtils.strip(text);
									if (JSP12TLDNames.VARIABLE_NAME_GIVEN.equals(attrName)) {
										variable.setNameGiven(text);
									}
									else if (JSP12TLDNames.VARIABLE_NAME_FROM_ATTRIBUTE.equals(attrName)) {
										variable.setNameFromAttribute(text);
									}
									else if (JSP20TLDNames.VARIABLE_ALIAS.equals(attrName)) {
										variable.setAlias(text);
									}
									else if (JSP12TLDNames.VARIABLE_CLASS.equals(attrName)) {
										variable.setVariableClass(text);
									}
									else if (JSP12TLDNames.VARIABLE_DECLARE.equals(attrName)) {
										variable.setDeclareString(text);
									}
									else if (JSP11Namespace.ATTR_NAME_SCOPE.equals(attrName)) {
										variable.setScope(text);
									}
									else if (JSP12TLDNames.DESCRIPTION.equals(attrName)) {
										variable.setDescription(text);
									}
								}
							}
							if (variable.getAlias() != null || variable.getNameFromAttribute() != null || variable.getNameGiven() != null) {
								ed.getVariables().add(variable);
							}
						}
						else if ("include".equals(directiveName) && allowIncludes) {
							String attrName = null;
							for (int i = 2; i < documentRegion.getNumberOfRegions(); i++) {
								ITextRegion region = regions.get(i);
								String text = documentRegion.getText(region);
								if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
									attrName = text;
								}
								// process value
								else if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE && attrName != null) {
									text = StringUtils.strip(text);
									if (JSP11Namespace.ATTR_NAME_FILE.equals(attrName)) {
										IPath filePath = FacetModuleCoreSupport.resolve(new Path(((CMDocumentImpl) ed.getOwnerDocument()).getBaseLocation()), text);

										IFile includedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
										if (includedFile.isAccessible()) {
											loadTagFile(ed, includedFile, false);
										}
									}
								}
							}
						}
					}
				}

				documentRegion = documentRegion.getNext();
			}

		}
		catch (IOException e) {
			// Logger.logException("problem parsing " + tagFile, e); // can be caused by a still-in-development file
		}
		catch (CoreException e) {
			// Logger.logException("problem parsing " + tagFile, e); // frequently out of sync
		}
	}

	/**
	 * @param reference
	 * @return
	 */
	public CMDocument createCMDocument(ITaglibRecord reference) {
		CMDocumentImpl document = null;
		switch (reference.getRecordType()) {
			case (ITaglibRecord.TLD) : {
				ITLDRecord record = (ITLDRecord) reference;
				IResource file = ResourcesPlugin.getWorkspace().getRoot().getFile(record.getPath());
				if (file.getLocation() != null) {
					document = (CMDocumentImpl) buildCMDocumentFromFile(file.getLocation().toString());
					document.setLocationString(record.getPath().toString());
					if (_debug && document != null && document.getElements().getLength() == 0) {
						System.out.println("failure parsing " + record.getPath()); //$NON-NLS-1$
					}

					if (document.getSmallIcon() != null) {
						String iconPath = URIHelper.normalize(((TLDDocument) document).getSmallIcon(), file.getLocation().toString(), "/"); //$NON-NLS-1$
						document.setProperty(JSP12TLDNames.SMALL_ICON, "file:" + iconPath); //$NON-NLS-1$
					}
					if (document.getLargeIcon() != null) {
						String iconPath = URIHelper.normalize(((TLDDocument) document).getLargeIcon(), file.getLocation().toString(), "/"); //$NON-NLS-1$
						document.setProperty(JSP12TLDNames.LARGE_ICON, "file:" + iconPath); //$NON-NLS-1$
					}
				}
			}
				break;
			case (ITaglibRecord.JAR) : {
				IJarRecord record = (IJarRecord) reference;
				document = (CMDocumentImpl) buildCMDocumentFromJar(record.getLocation().toString());
				document.setLocationString("jar:file:" + record.getLocation().toString() + "!/META-INF/taglib.tld");
				if (document.getSmallIcon() != null) {
					String iconPath = URIHelper.normalize(((TLDDocument) document).getSmallIcon(), record.getLocation().toString() + "!/META-INF/", "/"); //$NON-NLS-1$ //$NON-NLS-2$
					document.setProperty(JSP12TLDNames.SMALL_ICON, "jar:file:" + iconPath); //$NON-NLS-1$
				}
				if (document.getLargeIcon() != null) {
					String iconPath = URIHelper.normalize(((TLDDocument) document).getLargeIcon(), record.getLocation().toString() + "!/META-INF/", "/"); //$NON-NLS-1$ //$NON-NLS-2$
					document.setProperty(JSP12TLDNames.LARGE_ICON, "jar:file:" + iconPath); //$NON-NLS-1$
				}
				if (_debug && document != null && document.getElements().getLength() == 0) {
					System.out.println("failure parsing " + record.getLocation()); //$NON-NLS-1$
				}
			}
				break;
			case (ITaglibRecord.TAGDIR) : {
				ITagDirRecord record = (ITagDirRecord) reference;
				document = buildCMDocumentFromFolder(record.getPath());
			}
				break;
			case (ITaglibRecord.URL) : {
				IURLRecord record = (IURLRecord) reference;
				URL url = record.getURL();
				InputStream urlContents = JarUtilities.getInputStream(url);
				if (urlContents != null) {
					document = (CMDocumentImpl) buildCMDocument(record.getBaseLocation(), urlContents);
					String urlString = url.toString();
					document.setLocationString(urlString);
					if (document.getSmallIcon() != null) {
						String iconPath = URIHelper.normalize(((TLDDocument) document).getSmallIcon(), urlString, "/"); //$NON-NLS-1$
						document.setProperty(JSP12TLDNames.SMALL_ICON, iconPath);
					}
					if (document.getLargeIcon() != null) {
						String iconPath = URIHelper.normalize(((TLDDocument) document).getLargeIcon(), urlString, "/"); //$NON-NLS-1$
						document.setProperty(JSP12TLDNames.LARGE_ICON, iconPath);
					}
				}
				if (urlContents != null) {
					try {
						urlContents.close();
					}
					catch (IOException e) {
					}
				}
			}
				break;
		}
		return document;
	}
}
