/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.internal.operation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.wtp.common.logger.proxy.Logger;


public class WebSettings{
	protected IFile fSettingsFile;
	protected IProject fProject;
	protected Document fDomDocument;
	// Version number may not change with every release,
	// only when changes necessitate a new version number
	public static String CURRENT_VERSION = "600"; //$NON-NLS-1$
	public static String VERSION_V4 = "400"; //$NON-NLS-1$
	public static final String ELEMENT_WORKSPACE_VERSION = "version"; //$NON-NLS-1$
	

	static final String ELEMENT_WEBSETTINGS = "j2eesettings"; //$NON-NLS-1$
	static final String ELEMENT_PROJECTTYPE = "project-type"; //$NON-NLS-1$
	static final String ELEMENT_CONTEXTROOT = "context-root"; //$NON-NLS-1$
	static final String ELEMENT_WEBCONTENT = "webcontent"; //$NON-NLS-1$
	static final String ELEMENT_JSPLEVEL = "jsp-level"; //$NON-NLS-1$
	static final String ELEMENT_LIBMODULES = "lib-modules"; //$NON-NLS-1$
	static final String ELEMENT_LIBMODULE = "lib-module"; //$NON-NLS-1$
	static final String ELEMENT_LIBMODULE_JAR = "jar"; //$NON-NLS-1$
	static final String ELEMENT_LIBMODULE_PROJECT = "project"; //$NON-NLS-1$
	static final String ELEMENT_FEATURES = "features"; //$NON-NLS-1$
	static final String ELEMENT_FEATURE = "feature"; //$NON-NLS-1$
	static final String ELEMENT_FEATUREID = "feature-id"; //$NON-NLS-1$

	static final ILibModule[] EMPTY_LIBMODULES = new ILibModule[0];
	static final String[] EMPTY_FEATURES = new String[0];
	static boolean validWebSettings = true;

	public WebSettings(IProject project) {
		fProject = project;
		//this.nature = nature;
		if (getDOMDocument() == null) {
			try {
				createNewDocument();
			} catch (CoreException e) {
				//Ignore
			} catch (IOException e) {
				//Ignore
			}
		}
	}

	public WebSettings(IProject project, IFile webSettings) {
		fProject = project;
		//this.nature = nature;
		if (getDOMDocument(webSettings) == null) {
			validWebSettings = false;
		}
	}

	protected IFile getSettingsFile() {
		if (fSettingsFile == null) {
			fSettingsFile = fProject.getFile(ISimpleWebNatureConstants.WEBSETTINGS_FILE_NAME);
		}
		return fSettingsFile;
	}

	public String getContextRoot() {
		return getValue(ELEMENT_CONTEXTROOT);
	}

	public String getJSPLevel() {
		return getValue(ELEMENT_JSPLEVEL);
	}

	public String getWebContentName() {
		return getValue(ELEMENT_WEBCONTENT);
	}

	public ILibModule[] getLibModules() {
		Element root = getRootElement();
		if (root == null)
			return EMPTY_LIBMODULES;

		Element libModuleNode = findChildNode(root, ELEMENT_LIBMODULES);
		if (libModuleNode == null)
			return EMPTY_LIBMODULES;

		NodeList children = libModuleNode.getChildNodes();
		ArrayList results = new ArrayList();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			ILibModule libModule = getLibModule(node);
			if (libModule != null) {
				results.add(libModule);
			}
		}

		return (ILibModule[]) results.toArray(new ILibModule[results.size()]);
	}

	protected ILibModule getLibModule(Node node) {
		if (!node.getNodeName().equalsIgnoreCase(ELEMENT_LIBMODULE))
			return null;
		String jarName = getNodeValue((Element) node, ELEMENT_LIBMODULE_JAR);
		String projectName = getNodeValue((Element) node, ELEMENT_LIBMODULE_PROJECT);

		ILibModule libModule = new LibModule(jarName, projectName);
		return libModule;
	}

	public String[] getFeatureIds() {
		Element root = getRootElement();
		if (root == null)
			return EMPTY_FEATURES;

		Element featuresNode = findChildNode(root, ELEMENT_FEATURES);
		if (featuresNode == null)
			return EMPTY_FEATURES;

		NodeList children = featuresNode.getChildNodes();
		ArrayList results = new ArrayList();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			String featureId = getFeatureId(node);
			if (featureId != null) {
				results.add(featureId);
			}
		}

		return (String[]) results.toArray(new String[results.size()]);
	}

	protected String getFeatureId(Node node) {
		if (!node.getNodeName().equalsIgnoreCase(ELEMENT_FEATURE))
			return null;
		String id = getNodeValue((Element) node, ELEMENT_FEATUREID);

		return id;
	}

	public String getProjectType() {
		return getValue(ELEMENT_PROJECTTYPE);
	}

	public String getCurrentVersion() {
		// The following change is needed when the websettings file is
		// deleted from a version 4 workspace Checking for webapplication
		// folder - Otherwise, new projects will not work.
		IContainer webmoduleFolder = fProject.getFolder(ISimpleWebNatureConstants.WEB_MODULE_DIRECTORY_V4);
		IFolder webinfFolder = ((IFolder) webmoduleFolder).getFolder(ISimpleWebNatureConstants.INFO_DIRECTORY);
		if (webinfFolder.exists()) {
			return VERSION_V4;
		}

		return CURRENT_VERSION;
	}

	protected String getValue(String settingName) {
		Element root = getRootElement();
		if (root == null)
			return null;
		return getNodeValue(root, settingName);
	}

	protected void createNewDocument() throws CoreException, IOException {
		StringWriter writer = new StringWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
		writer.write("\n"); //$NON-NLS-1$
		writer.write("<j2eesettings version=\""); //$NON-NLS-1$
		writer.write(getCurrentVersion());
		writer.write("\">");//$NON-NLS-1$
		writer.write("\n"); //$NON-NLS-1$
		writer.write("</j2eesettings>"); //$NON-NLS-1$
		writer.write("\n"); //$NON-NLS-1$

		InputStream sourceStream = new ByteArrayInputStream(writer.toString().getBytes("UTF8")); //$NON-NLS-1$
		IFile webSettingsFile = getSettingsFile();
		if (webSettingsFile.exists())
			webSettingsFile.setContents(sourceStream, true, true, null);
		else
			webSettingsFile.create(sourceStream, true, null);
		read();
	}

	public void setContextRoot(String contextRoot) {
		setValue(ELEMENT_CONTEXTROOT, contextRoot);
	}

	public void setJSPLevel(String jspLevel) {
		setValue(ELEMENT_JSPLEVEL, jspLevel);
	}

	public void setWebContentName(String name) {
		String defaultName = getWebContentName();
		if (defaultName == null || defaultName.length() == 0 || !name.equals(defaultName))
			setValue(ELEMENT_WEBCONTENT, name);
	}

	public void setProjectType(String projectType) {
		setValue(ELEMENT_PROJECTTYPE, projectType);
	}

	public void setLibModules(ILibModule[] libModules) {
		Document doc = getOrCreateDocument();
		Node libModulesNode = findOrCreateChildNode(doc.getDocumentElement(), ELEMENT_LIBMODULES);
		Node firstChild = null;

		// Remove all of the children.
		while ((firstChild = libModulesNode.getFirstChild()) != null)
			libModulesNode.removeChild(firstChild);

		// Add new children.
		for (int i = 0; i < libModules.length; i++) {
			ILibModule iLibModule = libModules[i];
			if (iLibModule != null)
				addLibModule(libModulesNode, iLibModule);
		}
	}

	protected void addLibModule(Node libModulesNode, ILibModule libModule) {
		Document doc = getDOMDocument();
		Element libModuleNode = doc.createElement(ELEMENT_LIBMODULE);
		libModulesNode.appendChild(libModuleNode);
		setValue(libModuleNode, ELEMENT_LIBMODULE_JAR, libModule.getJarName());
		setValue(libModuleNode, ELEMENT_LIBMODULE_PROJECT, libModule.getProjectName());
	}

	public void setFeatureIds(String[] featureIds) {
		Document doc = getOrCreateDocument();
		Node featuresNode = findOrCreateChildNode(doc.getDocumentElement(), ELEMENT_FEATURES);

		// Add new children.
		for (int i = 0; i < featureIds.length; i++) {
			String sFeatureId = featureIds[i];
			if (sFeatureId != null)
				addFeatureId(featuresNode, sFeatureId);
		}
	}

	protected void addFeatureId(Node featuresNode, String featureId) {
		Document doc = getDOMDocument();
		Element featureNode = doc.createElement(ELEMENT_FEATURE);
		featuresNode.appendChild(featureNode);
		setValue(featureNode, ELEMENT_FEATUREID, featureId);
	}

	public boolean isValidWebSettings() {
		return validWebSettings;
	}

	public void removeFeatureId(String removeId) {
		Element root = getRootElement();
		if (root != null) {
			Element featuresNode = findChildNode(root, ELEMENT_FEATURES);
			if (featuresNode != null) {
				NodeList children = featuresNode.getChildNodes();
				ArrayList results = new ArrayList();
				for (int i = 0; i < children.getLength(); i++) {
					Node node = children.item(i);
					String featureId = getFeatureId(node);
					if (featureId != null) {
						// determine if in the list to remove
						if (!(featureId.equals(removeId)))
							results.add(featureId);
					}
				}
				// Remove all of the children.
				Node firstChild = null;
				while ((firstChild = featuresNode.getFirstChild()) != null)
					featuresNode.removeChild(firstChild);
				if (results.size() > 0) {
					String[] updateFeatureIds = (String[]) results.toArray(new String[results.size()]);
					// Add new children.
					for (int i = 0; i < results.size(); i++) {
						String sFeatureId = updateFeatureIds[i];
						if (sFeatureId != null)
							addFeatureId(featuresNode, sFeatureId);
					}
				}
			}
		}
	}

	public String getRootNodeName() {
		return ELEMENT_WEBSETTINGS;
	}
	protected Node findOrCreateChildNode(Element root, String nodeName) {
		Node node = findChildNode(root, nodeName);
		if (node == null) {
			// If the element does not exist yet, create one.
			node = getDOMDocument().createElement(nodeName);
			root.appendChild(node);
		}
		return node;
	}
	protected Element findChildNode(Element parent, String nodeName) {
		NodeList list = parent.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node curNode = list.item(i);
			if (curNode.getNodeType() == Node.ELEMENT_NODE) {
				Element curElement = (Element) curNode;
				if (curElement.getNodeName().equalsIgnoreCase(nodeName))
					return curElement;
			}
		}
		return null;
	}
	protected Element getRootElement() {
		Document doc = getDOMDocument();
		if (doc == null)
			return null;

		Element root = doc.getDocumentElement();
		if (root == null)
			return null;
		if (!root.getNodeName().equalsIgnoreCase(getRootNodeName()))
			return null;
		return root;
	}
	
	protected Document getOrCreateDocument() {
		Document doc = getDOMDocument();
		if (doc == null) {
			try {
				createNewDocument();
				doc = getDOMDocument();
			} catch (CoreException e) {
				//Ignore
			} catch (IOException e) {
				//Ignore
			}
		}
		return doc;
	}
	public static String getWebContentDirectory(InputStream inputStream) {
		InputStreamReader fileStream = null;
		try {
			fileStream = new InputStreamReader(inputStream, "utf-8"); //$NON-NLS-1$
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document fDomDocument = parser.parse(new InputSource(fileStream));
			Element root = fDomDocument.getDocumentElement();
			if (root.getNodeName().equalsIgnoreCase(ELEMENT_WEBSETTINGS)) {
				NodeList list = root.getChildNodes();
				for (int i = 0, length = list.getLength(); i < length; i++) {
					Node node = list.item(i);
					if (node.getNodeName().equals(ELEMENT_WEBCONTENT)) {
						NodeList childNodes = node.getChildNodes();
						for (int j = 0, childLength = childNodes.getLength(); j < childLength; j++) {
							Node curNode = childNodes.item(j);
							if (curNode.getNodeType() == Node.TEXT_NODE) {
								return curNode.getNodeValue();
							}
						}
						return null;
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			//Ignore
		} catch (ParserConfigurationException e) {
			//Ignore
		} catch (FactoryConfigurationError e) {
			//Ignore
		} catch (SAXException e) {
			//Ignore
		} catch (IOException e) {
			//Ignore
		} catch (Exception e) {
			//Ignore
		} finally {
			if (fileStream != null)
				try {
					fileStream.close();
				} catch (IOException e1) {
					//Ignore
				}
		}
		return null;
	}
	protected Document getDOMDocument() {
		if (fDomDocument == null) {
			try {
				read();
			} catch (IOException e) {
				//Ignore
			}
		}
		return fDomDocument;
	}

	// Version of getDomDocument for use by import
	protected Document getDOMDocument(IFile webSettings) {
		if (fDomDocument == null) {
			try {
				read(webSettings);
			} catch (IOException e) {
				//Ignore
			}
		}
		return fDomDocument;
	}
	protected void read() throws IOException {
		// This following was changed for Defect 212723 The Util StringReader
		// was changed to the InputStreamReader MAY
		IFile settingsFile = getSettingsFile();
		InputStream inputStream = null;
		InputStreamReader fileStream = null;
		if (settingsFile.exists()) {
			try {
				ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
				try {
					Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

					// JZ: fix to defect 240171
					inputStream = settingsFile.getContents(true);
					fileStream = new InputStreamReader(inputStream, "utf-8"); //$NON-NLS-1$

					DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					fDomDocument = parser.parse(new InputSource(fileStream));
				} finally {
					Thread.currentThread().setContextClassLoader(prevClassLoader);
				}
	/*		} catch (JavaModelException e) {
				throw new IOException("file.badFormat"); //$NON-NLS-1$	
*/			} catch (CoreException e) {
				throw new IOException("file.badFormat"); //$NON-NLS-1$			
			} catch (SAXException e) {
				throw new IOException("file.badFormat"); //$NON-NLS-1$
			} catch (ParserConfigurationException e) {
				throw new IOException("file.badFormat"); //$NON-NLS-1$
			} finally {
				if (fileStream != null)
					fileStream.close();
			}
		}
	}


	// Version of read for use by import
	protected void read(IFile settings) throws IOException {
		// This following was changed for Defect 212723 The Util StringReader
		// was changed to the InputStreamReader MAY
		IFile settingsFile = settings;

		InputStream inputStream = null;
		InputStreamReader fileStream = null;
		if (settingsFile != null) {
			try {
				ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
				try {
					Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
					inputStream = settingsFile.getContents();
					fileStream = new InputStreamReader(inputStream, "utf-8"); //$NON-NLS-1$

					DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					fDomDocument = parser.parse(new InputSource(fileStream));
				} finally {
					Thread.currentThread().setContextClassLoader(prevClassLoader);
				}

			} catch (SAXException e) {
				throw new IOException("file.badFormat"); //$NON-NLS-1$
			} catch (ParserConfigurationException e) {
				throw new IOException("file.badFormat"); //$NON-NLS-1$
			} catch (CoreException ce) {
				ce.printStackTrace();
			} finally {
				if (fileStream != null)
					fileStream.close();
			}
		}
	}
	protected void setValue(String nodeName, String value) {
		Document doc = getOrCreateDocument();
		setValue(doc.getDocumentElement(), nodeName, value);
	}
	protected void setValue(Element root, String nodeName, String value) {
		Node node = findOrCreateChildNode(root, nodeName);

		NodeList childNodes = node.getChildNodes();

		if (childNodes.getLength() == 0) {
			Text newText = getDOMDocument().createTextNode(value);
			node.appendChild(newText);
			root.appendChild(node);
		} else {
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node curNode = childNodes.item(i);
				if (curNode.getNodeType() == Node.TEXT_NODE)
					curNode.setNodeValue(value);
			}
		}
	}

	protected String getNodeValue(Element parent, String nodeName) {
		if (parent != null) {
			Element node = findChildNode(parent, nodeName);
			if (node != null)
				return getChildText(node);
		}
		return null;
	}

	protected String getChildText(Element node) {
		NodeList list = node.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node curNode = list.item(i);
			if (curNode.getNodeType() == Node.TEXT_NODE) {
				return curNode.getNodeValue();
			}
		}
		return null;
	}
	public void setVersion(String version) {
		Document doc = getDOMDocument();
		if (doc == null)
			return;

		Element root = doc.getDocumentElement();
		if (root == null)
			return;

		if (!root.getNodeName().equalsIgnoreCase(getRootNodeName()))
			return;

		root.setAttribute(ELEMENT_WORKSPACE_VERSION, version); //$NON-NLS-1$
	}
	public void write() throws CoreException {
		if (fDomDocument == null)
			return;

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			transformer.transform(new DOMSource(fDomDocument.getDocumentElement()), new StreamResult(outStream));
		} catch (TransformerConfigurationException e) {
			Logger.getLogger().logError(e);
		} catch (TransformerFactoryConfigurationError e) {
			Logger.getLogger().logError(e);
		} catch (TransformerException e) {
			Logger.getLogger().logError(e);
		}

		InputStream sourceStream = new ByteArrayInputStream(outStream.toByteArray());

		IFile settingsFile = getSettingsFile();
		if (settingsFile.exists())
			settingsFile.setContents(sourceStream, true, true, null);
		else
			settingsFile.create(sourceStream, true, null);
	}
	public String getVersion() {
		Document doc = getDOMDocument();
		if (doc == null)
			return null;

		Element root = doc.getDocumentElement();
		if (root == null)
			return null;
		if (!root.getNodeName().equalsIgnoreCase(getRootNodeName()))
			return null;

		return root.getAttribute(ELEMENT_WORKSPACE_VERSION); //$NON-NLS-1$
	}
}