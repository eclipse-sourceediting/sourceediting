/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.JSP20TLDNames;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.TLDFunction;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.TLDInitParam;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.TLDListener;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.TLDValidator;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.TLDVariable;
import org.eclipse.wst.jsdt.web.core.internal.util.DocumentProvider;
import org.eclipse.wst.jsdt.web.core.taglib.IJarRecord;
import org.eclipse.wst.jsdt.web.core.taglib.ITLDRecord;
import org.eclipse.wst.jsdt.web.core.taglib.ITaglibRecord;
import org.eclipse.wst.jsdt.web.core.taglib.IURLRecord;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.factory.CMDocumentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * CMDocumentBuilder for Taglib Descriptors
 * 
 * Returns namespace-less CMDocuments for a taglib descriptor, loading it
 * directly from a file or extracted from a JAR archive. Content Model objects
 * will implement the TLDCMDocument, TLDElementDeclaration, and
 * TLDAttributeDeclaration interfaces for extended properties.
 */
public class CMDocumentFactoryTLD implements CMDocumentFactory {

	static final boolean _debug = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/tldcmdocument/factory")); //$NON-NLS-1$ //$NON-NLS-2$

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
		if (baselocation != null) {
			provider.setBaseReference(baselocation);
		}
		return loadDocument(baselocation, provider.getRootElement());
	}

	/**
	 * @param fileName
	 * @return
	 */
	private CMDocument buildCMDocumentFromDirectory(File directory) {
		if (_debug) {
			System.out
					.println("not implemented: tagdir loading for " + directory.getAbsolutePath()); //$NON-NLS-1$
		}
		return null;
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
	protected CMDocument buildCMDocumentFromJar(String jarFileName,
			String contentFileName) {
		// load the taglib descriptor file
		DocumentProvider provider = new DocumentProvider();
		provider.setValidating(false);
		provider.setBaseReference(jarFileName);
		provider.setRootElementName(JSP11TLDNames.TAGLIB);
		provider.setJarFileName(jarFileName);
		provider.setFileName(contentFileName);
		CMDocument document = loadDocument(
				"jar:file://" + jarFileName + "!" + contentFileName, provider.getRootElement()); //$NON-NLS-1$ //$NON-NLS-2$
		// TODO: Add the tags declared in META-INF/tags, see JSP 2.0 section
		// 8.4.1
		return document;
	}

	protected CMAttributeDeclaration createAttributeDeclaration(
			CMDocument document, Node attrNode) {
		CMAttributeDeclarationImpl attr = new CMAttributeDeclarationImpl(
				document);

		Node child = attrNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals(JSP11TLDNames.NAME)
						&& child.hasChildNodes()) {
					attr.setNodeName(getContainedText(child));
				} else if (child.getNodeName()
						.equals(JSP12TLDNames.DESCRIPTION)
						&& child.hasChildNodes()) {
					attr.setDescription(getContainedText(child));
				} else if (child.getNodeName().equals(JSP11TLDNames.ID)
						&& child.hasChildNodes()) {
					attr.setId(getContainedText(child));
				} else if (child.getNodeName().equals(JSP11TLDNames.REQUIRED)
						&& child.hasChildNodes()) {
					attr.setRequiredString(getContainedText(child));
				} else if (child.getNodeName()
						.equals(JSP11TLDNames.RTEXPRVALUE)
						&& child.hasChildNodes()) {
					attr.setRtexprvalue(getContainedText(child));
				} else if (child.getNodeName().equals(JSP20TLDNames.FRAGMENT)
						&& child.hasChildNodes()) {
					attr.setFragment(Boolean.valueOf(getContainedText(child))
							.booleanValue());
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
		} catch (MalformedURLException e) {
			result = createCMDocumentFromFile(uri);
		}
		if (result == null && url != null) {
			if (url.getProtocol().equals("file")) { //$NON-NLS-1$
				result = createCMDocumentFromFile(url.getFile());
			} else {
				/**
				 * Skip anything else since trying to load a TLD from a remote
				 * location has unpredictable performance impact.
				 */
			}
		}
		if (result == null) {
			result = new CMDocumentImpl();
		}
		return result;
	}

	/**
	 * @param fileName
	 * @return
	 */
	private CMDocument createCMDocumentFromFile(String fileName) {
		CMDocument result = null;
		File file = new File(fileName);
		try {
			if (file.isDirectory()) {
				result = buildCMDocumentFromDirectory(file);
			}
		} catch (SecurityException e) {
			result = null;
		}
		if (result == null) {
			if (fileName.endsWith(".jar")) { //$NON-NLS-1$
				result = buildCMDocumentFromJar(fileName);
			} else {
				result = buildCMDocumentFromFile(fileName);
			}
		}
		return result;
	}

	protected CMElementDeclaration createElementDeclaration(
			CMDocument cmdocument, Element tagFileNode, String path) {
		CMElementDeclarationImpl ed = new CMElementDeclarationImpl(cmdocument);
		boolean hasName = false;

		// load information declared within the .tld
		Node child = tagFileNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP12TLDNames.DESCRIPTION)
						&& child.hasChildNodes()) {
					ed.setDescription(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.DISPLAY_NAME)
						&& child.hasChildNodes()) {
					ed.setDisplayName(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.EXAMPLE)
						&& child.hasChildNodes()) {
					ed.setExample(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.ICON)
						&& child.hasChildNodes()) {
					ed.setSmallIcon(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.NAME)
						&& child.hasChildNodes()) {
					ed.setNodeName(getContainedText(child));
					hasName = ed.getNodeName().trim().length() > 0;
				} else if (nodeName.equals(JSP20TLDNames.PATH)
						&& child.hasChildNodes()) {
					ed.setPath(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.TAG_EXTENSION)) {
					ed.getExtensions().add(child);
				}
			}
			child = child.getNextSibling();
		}
		if (hasName) {
			// load information declared within the .tag(x) file
			// JSP2_TODO: implement for JSP 2.0
			return ed;
		}
		return null;
	}

	protected CMElementDeclaration createElementDeclaration(
			CMDocument document, Node tagNode) {
		CMElementDeclarationImpl ed = new CMElementDeclarationImpl(document);

		Node child = tagNode.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				// tag information
				String nodeName = child.getNodeName();
				if (nodeName.equals(JSP11TLDNames.NAME)
						&& child.hasChildNodes()) {
					ed.setNodeName(getContainedText(child));
				} else if ((nodeName.equals(JSP11TLDNames.TAGCLASS) || nodeName
						.equals(JSP12TLDNames.TAG_CLASS))
						&& child.hasChildNodes()) {
					ed.setTagclass(getContainedText(child));
				} else if ((nodeName.equals(JSP11TLDNames.TEICLASS) || nodeName
						.equals(JSP12TLDNames.TEI_CLASS))
						&& child.hasChildNodes()) {
					ed.setTeiclass(getContainedText(child));
				} else if ((nodeName.equals(JSP11TLDNames.BODYCONTENT) || nodeName
						.equals(JSP12TLDNames.BODY_CONTENT))
						&& child.hasChildNodes()) {
					String bodycontent = getContainedText(child);
					// Apparently, Apache Tomcat is not case sensitive about
					// these values
					if (bodycontent.equalsIgnoreCase(JSP11TLDNames.CONTENT_JSP)) {
						ed.setBodycontent(JSP11TLDNames.CONTENT_JSP);
					} else if (bodycontent
							.equalsIgnoreCase(JSP11TLDNames.CONTENT_TAGDEPENDENT)) {
						ed.setBodycontent(JSP11TLDNames.CONTENT_TAGDEPENDENT);
					} else if (bodycontent
							.equalsIgnoreCase(JSP11TLDNames.CONTENT_EMPTY)) {
						ed.setBodycontent(JSP11TLDNames.CONTENT_EMPTY);
					} else if (bodycontent
							.equalsIgnoreCase(JSP20TLDNames.CONTENT_SCRIPTLESS)) {
						ed.setBodycontent(JSP20TLDNames.CONTENT_SCRIPTLESS);
					}
				}
				// info (1.1 only) or description (1.2 only)
				else if ((nodeName.equals(JSP11TLDNames.INFO) || nodeName
						.equals(JSP12TLDNames.DESCRIPTION))
						&& child.hasChildNodes()) {
					ed.setDescription(getContainedText(child));
				}
				// attributes
				else if (nodeName.equals(JSP11TLDNames.ATTRIBUTE)) {
					CMAttributeDeclaration attr = createAttributeDeclaration(
							document, child);
					ed.fAttributes.setNamedItem(attr.getAttrName(), attr);
				}
				// variables
				else if (nodeName.equals(JSP12TLDNames.VARIABLE)) {
					ed.getVariables().add(createVariable(child));
				} else if (nodeName.equals(JSP12TLDNames.LARGE_ICON)
						&& child.hasChildNodes()) {
					ed.setLargeIcon(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.SMALL_ICON)
						&& child.hasChildNodes()) {
					ed.setSmallIcon(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.TAG_EXTENSION)
						&& child.getNodeType() == Node.ELEMENT_NODE) {
					ed.getExtensions().add(child);
				}
			}
			child = child.getNextSibling();
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
				if (nodeName.equals(JSP12TLDNames.DESCRIPTION)
						&& child.hasChildNodes()) {
					function.setDescription(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.DISPLAY_NAME)
						&& child.hasChildNodes()) {
					function.setName(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.EXAMPLE)
						&& child.hasChildNodes()) {
					function.setExample(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.FUNCTION_CLASS)
						&& child.hasChildNodes()) {
					function.setClassName(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.FUNCTION_EXTENSION)
						&& child.hasChildNodes()) {
					function.getExtensions().add(child);
				} else if (nodeName.equals(JSP20TLDNames.FUNCTION_SIGNATURE)
						&& child.hasChildNodes()) {
					function.setSignature(getContainedText(child));
				} else if (nodeName.equals(JSP20TLDNames.ICON)
						&& child.hasChildNodes()) {
					function.setIcon(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.NAME)
						&& child.hasChildNodes()) {
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
				if (nodeName.equals(JSP12TLDNames.VALIDATOR_PARAM_NAME)
						&& child.hasChildNodes()) {
					initParam.setName(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.VALIDATOR_PARAM_VALUE)
						&& child.hasChildNodes()) {
					initParam.setValue(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.DESCRIPTION)
						&& child.hasChildNodes()) {
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
				if (nodeName.equals(JSP12TLDNames.LISTENER_CLASS)
						&& child.hasChildNodes()) {
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
				if (nodeName.equals(JSP12TLDNames.VALIDATOR_CLASS)
						&& child.hasChildNodes()) {
					validator.setValidatorClass(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.VALIDATOR_INIT_PARAM)
						&& child.hasChildNodes()) {
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
				if (nodeName.equals(JSP12TLDNames.VARIABLE_CLASS)
						&& child.hasChildNodes()) {
					variable.setVariableClass(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.VARIABLE_DECLARE)
						&& child.hasChildNodes()) {
					variable.setDeclareString(getContainedText(child));
				} else if (nodeName
						.equals(JSP12TLDNames.VARIABLE_NAME_FROM_ATTRIBUTE)
						&& child.hasChildNodes()) {
					variable.setNameFromAttribute(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.VARIABLE_NAME_GIVEN)
						&& child.hasChildNodes()) {
					variable.setNameGiven(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.VARIABLE_SCOPE)
						&& child.hasChildNodes()) {
					variable.setScope(getContainedText(child));
				} else if (nodeName.equals(JSP12TLDNames.DESCRIPTION)
						&& child.hasChildNodes()) {
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
			return children.item(0).getNodeValue().trim();
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
			} else {
				s.append(child.getNodeValue().trim());
			}
			child = child.getNextSibling();
		}
		return s.toString().trim();
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
				System.out
						.println("null \"taglib\" element for TLD " + baseLocation); //$NON-NLS-1$
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
			// tag
			if (nodeName.equals(JSP11TLDNames.TAG)) {
				CMElementDeclaration ed = createElementDeclaration(document,
						child);
				if (ed != null) {
					document.fElements.setNamedItem(ed.getNodeName(), ed);
				}
			}
			// tag-file
			else if (nodeName.equals(JSP20TLDNames.TAG_FILE)
					&& child.getNodeType() == Node.ELEMENT_NODE
					&& child.hasChildNodes()) {
				Element tagFileElement = (Element) child;
				String path = tagFileElement.getAttribute(JSP20TLDNames.PATH);

				CMElementDeclarationImpl ed = (CMElementDeclarationImpl) createElementDeclaration(
						document, tagFileElement, path);
				if (ed != null) {
					document.fElements.setNamedItem(ed.getNodeName(), ed);
				}
			}
			// other one-of-a-kind children
			// JSP version
			else if ((nodeName.equals(JSP11TLDNames.JSPVERSION) || nodeName
					.equals(JSP12TLDNames.JSP_VERSION))
					&& child.hasChildNodes()) {
				document.setJspversion(getContainedText(child));
			}
			// tag library version
			else if ((nodeName.equals(JSP11TLDNames.TLIBVERSION) || nodeName
					.equals(JSP12TLDNames.TLIB_VERSION))
					&& child.hasChildNodes()) {
				document.setTlibversion(getContainedText(child));
			}
			// short name
			else if ((nodeName.equals(JSP11TLDNames.SHORTNAME) || nodeName
					.equals(JSP12TLDNames.SHORT_NAME))
					&& child.hasChildNodes()) {
				document.setShortname(getContainedText(child));
			}
			// URI/URN
			else if ((nodeName.equals(JSP11TLDNames.URI) || nodeName
					.equals(JSP11TLDNames.URN))
					&& child.hasChildNodes()) {
				document.setUri(getContainedText(child));
			}
			// info
			else if (nodeName.equals(JSP11TLDNames.INFO)
					&& child.hasChildNodes()) {
				document.setInfo(getContainedText(child));
			}
			// New JSP 1.2
			// description
			else if (nodeName.equals(JSP12TLDNames.DESCRIPTION)) {
				document.setDescription(getContainedText(child));
			}
			// display name
			else if (nodeName.equals(JSP12TLDNames.DISPLAY_NAME)
					&& child.hasChildNodes()) {
				document.setDisplayName(getContainedText(child));
			}
			// large icon
			else if (nodeName.equals(JSP12TLDNames.LARGE_ICON)
					&& child.hasChildNodes()) {
				document.setLargeIcon(getContainedText(child));
			}
			// small icon
			else if (nodeName.equals(JSP12TLDNames.SMALL_ICON)
					&& child.hasChildNodes()) {
				document.setSmallIcon(getContainedText(child));
			}
			// validator
			else if (nodeName.equals(JSP12TLDNames.VALIDATOR)) {
				document.setValidator(createValidator(child));
			}
			// listener
			else if (nodeName.equals(JSP12TLDNames.LISTENER)) {
				document.getListeners().add(createListener(child));
			} else if (nodeName.equals(JSP20TLDNames.FUNCTION)) {
				TLDFunction function = createFunction(document, child);
				if (function != null) {
					document.getFunctions().add(function);
				}
			} else if (nodeName.equals(JSP20TLDNames.TAGLIB_EXTENSION)) {
				document.getExtensions().add(child);
			}

			child = child.getNextSibling();
		}
		return document;
	}

	/**
	 * @param reference
	 * @return
	 */
	public CMDocument createCMDocument(ITaglibRecord reference) {
		CMDocumentImpl document = null;
		switch (reference.getRecordType()) {
		case (ITaglibRecord.TLD): {
			ITLDRecord record = (ITLDRecord) reference;
			IResource file = ResourcesPlugin.getWorkspace().getRoot().getFile(
					record.getPath());
			if (file.getLocation() != null) {
				document = (CMDocumentImpl) buildCMDocumentFromFile(file
						.getLocation().toString());
				if (_debug && document != null
						&& document.getElements().getLength() == 0) {
					System.out.println("failure parsing " + record.getPath()); //$NON-NLS-1$
				}

				if (document.getSmallIcon() != null) {
					String iconPath = URIHelper.normalize(
							((TLDDocument) document).getSmallIcon(), file
									.getLocation().toString(), "/"); //$NON-NLS-1$
					document.setProperty(JSP12TLDNames.SMALL_ICON,
							"file:" + iconPath); //$NON-NLS-1$
				}
				if (document.getLargeIcon() != null) {
					String iconPath = URIHelper.normalize(
							((TLDDocument) document).getLargeIcon(), file
									.getLocation().toString(), "/"); //$NON-NLS-1$
					document.setProperty(JSP12TLDNames.LARGE_ICON,
							"file:" + iconPath); //$NON-NLS-1$
				}
			}
		}
			break;
		case (ITaglibRecord.JAR): {
			IJarRecord record = (IJarRecord) reference;
			document = (CMDocumentImpl) buildCMDocumentFromJar(record
					.getLocation().toString());
			if (document.getSmallIcon() != null) {
				String iconPath = URIHelper.normalize(((TLDDocument) document)
						.getSmallIcon(), record.getLocation().toString()
						+ "!META-INF/", "/"); //$NON-NLS-1$ //$NON-NLS-2$
				document.setProperty(JSP12TLDNames.SMALL_ICON,
						"jar:file:" + iconPath); //$NON-NLS-1$
			}
			if (document.getLargeIcon() != null) {
				String iconPath = URIHelper.normalize(((TLDDocument) document)
						.getLargeIcon(), record.getLocation().toString()
						+ "!META-INF/", "/"); //$NON-NLS-1$ //$NON-NLS-2$
				document.setProperty(JSP12TLDNames.LARGE_ICON,
						"jar:file:" + iconPath); //$NON-NLS-1$
			}
			if (document != null && document.getElements().getLength() == 0) {
				System.out.println("failure parsing " + record.getLocation()); //$NON-NLS-1$
			}
		}
			break;
		case (ITaglibRecord.TAGDIR): {
			// TagDirRecord record = (TagDirRecord) reference;
			// document =
			// buildCMDocumentFromDirectory(record.getLocation().toFile());
		}
			break;
		case (ITaglibRecord.URL): {
			IURLRecord record = (IURLRecord) reference;
			InputStream urlContents = null;
			URLConnection connection = null;
			try {
				connection = record.getURL().openConnection();
				if (connection != null) {
					connection.setUseCaches(false);
					urlContents = connection.getInputStream();
					document = (CMDocumentImpl) buildCMDocument(record
							.getBaseLocation(), urlContents);
					if (document.getSmallIcon() != null) {
						String iconPath = URIHelper.normalize(
								((TLDDocument) document).getSmallIcon(), record
										.getURL().toString(), "/"); //$NON-NLS-1$
						document
								.setProperty(JSP12TLDNames.SMALL_ICON, iconPath);
					}
					if (document.getLargeIcon() != null) {
						String iconPath = URIHelper.normalize(
								((TLDDocument) document).getLargeIcon(), record
										.getURL().toString(), "/"); //$NON-NLS-1$
						document
								.setProperty(JSP12TLDNames.LARGE_ICON, iconPath);
					}
				}
			} catch (IOException e) {
				// not uncommon given invalid URLs
			} finally {
				if (urlContents != null) {
					try {
						urlContents.close();
					} catch (IOException e) {
					}
				}
			}
		}
			break;
		}
		return document;
	}
}