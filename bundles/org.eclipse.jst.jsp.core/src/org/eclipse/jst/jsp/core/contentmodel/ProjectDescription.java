/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.contentmodel;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.contentmodel.tld.DocumentProvider;
import org.eclipse.jst.jsp.core.contentmodel.tld.JSP20TLDNames;
import org.eclipse.wst.sse.core.util.JarUtilities;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ProjectDescription {

	class DeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (delta.getResource().getType() == IResource.FILE) {
				if (delta.getResource().getName().endsWith(".tld")) {
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeTLD(delta.getResource());
					}
					else {
						addTLD(delta.getResource());
					}
				}
				else if (delta.getResource().getName().endsWith(".jar")) {
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeJAR(delta.getResource());
					}
					else {
						addJAR(delta.getResource());
					}
				}
				else if (delta.getResource().getName().endsWith(".tag") || delta.getResource().getName().endsWith(".tagx")) {
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeTagDir(delta.getResource());
					}
					else {
						addTagDir(delta.getResource());
					}
				}
				else if (delta.getResource().getName().equals(WEB_XML) && delta.getResource().getParent().getName().equals(WEB_INF)) {
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeServlets(delta.getResource());
					}
					else {
						addServlets(delta.getResource());
					}
				}
			}
			return true;
		}
	}

	class Indexer implements IResourceProxyVisitor {
		public boolean visit(IResourceProxy proxy) throws CoreException {
			if (proxy.getType() == IResource.FILE) {
				if (proxy.getName().endsWith(".tld")) {
					addTLD(proxy.requestResource());
				}
				else if (proxy.getName().endsWith(".jar")) {
					addJAR(proxy.requestResource());
				}
				else if (proxy.getName().endsWith(".tag") || proxy.getName().endsWith(".tagx")) {
					addTagDir(proxy.requestResource());
				}
				else if (proxy.getName().equals(WEB_XML) && proxy.requestResource().getParent().getName().equals(WEB_INF)) {
					addServlets(proxy.requestResource());
				}
			}
			return true;
		}
	}

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation"));
	static boolean _debugIndexTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indextime"));

	private static final String WEB_INF = "WEB-INF";

	private static final IPath WEB_INF_PATH = new Path("WEB-INF");
	private static final String WEB_XML = "web.xml";
	Hashtable fJARReferences;
	IProject fProject;
	Hashtable fServletReferences;
	Hashtable fTagDirReferences;

	Hashtable fTLDReferences;

	Hashtable fURLReferences;

	IResourceDeltaVisitor fVisitor;

	private long time0;

	ProjectDescription(IProject project) {
		super();
		fProject = project;
		fJARReferences = new Hashtable(0);
		fTagDirReferences = new Hashtable(0);
		fTLDReferences = new Hashtable(0);
		fServletReferences = new Hashtable(0);
		fURLReferences = new Hashtable(0);
	}

	void addJAR(IResource jar) {
		if (_debugIndexCreation)
			System.out.println("creating records for JAR " + jar.getFullPath());
		String jarLocationString = jar.getLocation().toString();
		String[] entries = JarUtilities.getEntryNames(jar);
		JarRecord jarRecord = (JarRecord) createJARRecord(jar);
		fTLDReferences.put(jar.getFullPath().toString(), jarRecord);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].endsWith(".tld")) {
				InputStream contents = JarUtilities.getInputStream(jar, entries[i]);
				if (contents != null) {
					String uri = extractURI(jarLocationString, contents);
					if (uri != null && uri.length() > 0) {
						URLRecord record = new URLRecord();
						record.uri = uri;
						record.baseLocation = jarLocationString;
						try {
							record.url = new URL("jar:file:" + jarLocationString + "!/" + entries[i]);
							jarRecord.urlRecords.add(record);
							fURLReferences.put(uri, record);
							if (_debugIndexCreation)
								System.out.println("created record for " + uri + "@" + record.getURL());
						}
						catch (MalformedURLException e) {
							// don't record this URI
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	void addServlets(IResource webxml) {
		if (webxml.getType() != IResource.FILE)
			return;
		InputStream webxmlContents = null;
		Document document = null;
		try {
			webxmlContents = ((IFile) webxml).getContents(true);
			DocumentProvider provider = new DocumentProvider();
			provider.setInputStream(webxmlContents);
			provider.setValidating(false);
			provider.setBaseReference(webxml.getParent().getLocation().toString());
			document = provider.getDocument();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		finally {
			if (webxmlContents != null)
				try {
					webxmlContents.close();
				}
				catch (IOException e1) {
				}
		}
		if (document == null)
			return;
		if (_debugIndexCreation)
			System.out.println("creating records for " + webxml.getFullPath());

		ServletRecord servletRecord = new ServletRecord();
		servletRecord.location = webxml.getLocation();
		fServletReferences.put(servletRecord.getWebXML().toString(), servletRecord);
		NodeList taglibs = document.getElementsByTagName(JSP20TLDNames.TAGLIB);
		for (int i = 0; i < taglibs.getLength(); i++) {
			String uri = readTextofChild(taglibs.item(i), "taglib-uri");
			// specified location is relative to root of the webapp
			String location = readTextofChild(taglibs.item(i), "taglib-location");
			TLDRecord record = new TLDRecord();
			record.uri = uri;
			// use the local web-app root
			record.location = new Path(getLocalRoot(webxml.getLocation().toString()) + location);
			servletRecord.urlRecords.add(record);
			fURLReferences.put(uri, record);
			if (_debugIndexCreation)
				System.out.println("created record for " + uri + "@" + location);
		}
	}

	void addTagDir(IResource tagDir) {
	}

	void addTLD(IResource tld) {
		if (_debugIndexCreation)
			System.out.println("creating record for " + tld.getFullPath());
		fTLDReferences.put(tld.getFullPath().toString(), createTLDRecord(tld));
	}

	/**
	 * @param resource
	 * @return
	 */
	private ITaglibRecord createJARRecord(IResource jar) {
		JarRecord record = new JarRecord();
		record.location = jar.getLocation();
		record.urlRecords = new ArrayList(0);
		return record;
	}

	/**
	 * @param resource
	 * @return
	 */
	private ITaglibRecord createTLDRecord(IResource tld) {
		TLDRecord record = new TLDRecord();
		record.location = tld.getLocation();
		InputStream contents = null;
		try {
			contents = ((IFile) tld).getContents(true);
			String baseLocation = null;
			if (tld.getLocation() != null) {
				baseLocation = tld.getLocation().toString();
			}
			else {
				baseLocation = getLocalRoot(fProject.getLocation().toString());
			}
			String defaultURI = extractURI(baseLocation, contents);
			if (defaultURI != null && defaultURI.length() > 0) {
				record.uri = defaultURI;
			}
		}
		catch (CoreException e) {
		}
		finally {
			try {
				if (contents != null) {
					contents.close();
				}
			}
			catch (IOException e) {
			}
		}
		return record;
	}

	/**
	 * @param tldContents
	 * @return
	 */
	private String extractURI(String baseLocation, InputStream tldContents) {
		StringBuffer uri = new StringBuffer();
		Node result = null;
		DocumentProvider provider = new DocumentProvider();
		provider.setInputStream(tldContents);
		provider.setValidating(false);
		provider.setRootElementName(JSP20TLDNames.TAGLIB);
		provider.setBaseReference(baseLocation);
		result = provider.getRootElement();
		if (result.getNodeType() != Node.ELEMENT_NODE)
			return null;
		Element taglibElement = (Element) result;
		if (taglibElement != null) {
			Node child = taglibElement.getFirstChild();
			while (child != null && !(child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(JSP20TLDNames.URI))) {
				child = child.getNextSibling();
			}
			if (child != null) {
				Node text = child.getFirstChild();
				while (text != null) {
					if (text.getNodeType() == Node.TEXT_NODE || text.getNodeType() == Node.CDATA_SECTION_NODE) {
						uri.append(text.getNodeValue());
					}
					text = text.getNextSibling();
				}
			}
		}
		return uri.toString();
	}

	/**
	 * @param baseLocation
	 * @return
	 */
	private String getLocalRoot(String baseLocation) {
		IResource file = FileBuffers.getWorkspaceFileAtLocation(new Path(baseLocation));
		while (file != null && (file.getType() & IResource.ROOT) == 0) {
			/**
			 * Treat any parent folder with a WEB-INF subfolder as a web-app
			 * root
			 */
			IContainer folder = null;
			if ((file.getType() & IResource.FOLDER) != 0) {
				folder = (IContainer) file;
			}
			else {
				folder = file.getParent();
			}
			IFolder webinf = folder.getFolder(WEB_INF_PATH);
			if (webinf != null && webinf.exists()) {
				return file.getLocation().toString();
			}
			file = file.getParent();
		}

		return fProject.getFullPath().toString();
	}

	/**
	 * @return Returns the visitor.
	 */
	IResourceDeltaVisitor getVisitor() {
		if (fVisitor == null) {
			fVisitor = new DeltaVisitor();
		}
		return fVisitor;
	}

	void index() {
		time0 = System.currentTimeMillis();
		fTLDReferences.clear();
		fJARReferences.clear();
		fTagDirReferences.clear();
		fServletReferences.clear();
		try {
			fProject.accept(new Indexer(), 0);
		}
		catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (_debugIndexTime)
			System.out.println("indexed " + fProject.getName() + " in " + (System.currentTimeMillis() - time0) + "ms");
	}

	protected String readTextofChild(Node node, String childName) {
		StringBuffer buffer = new StringBuffer();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childName)) {
				Node text = child.getFirstChild();
				while (text != null) {
					buffer.append(text.getNodeValue());
					text = text.getNextSibling();
				}
			}
		}
		return buffer.toString();
	}

	void removeJAR(IResource jar) {
		JarRecord record = (JarRecord) fJARReferences.remove(jar.getFullPath());
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				fURLReferences.remove(records[i].getURI());
			}
		}
	}

	void removeServlets(IResource webxml) {
		ServletRecord record = (ServletRecord) fServletReferences.remove(webxml.getFullPath());
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				fURLReferences.remove(records[i].getURI());
			}
		}
	}

	void removeTagDir(IResource tagDir) {
	}

	void removeTLD(IResource tld) {
		fTLDReferences.remove(tld.getFullPath());
	}

	/**
	 * @param baseLocation
	 * @param reference
	 * @return
	 */
	ITaglibRecord resolve(String baseLocation, String reference) {
		ITaglibRecord record = null;
		String location = null;

		/**
		 * Workaround for problem in URIHelper; uris starting with '/' are
		 * returned as-is.
		 */
		if (reference.startsWith("/")) {
			location = getLocalRoot(baseLocation) + reference;
		}
		else {
			location = URIHelper.normalize(reference, baseLocation, getLocalRoot(baseLocation));
		}
		// order dictated by JSP spec 2.0 section 7.2.3
		if (record == null) {
			record = (ITaglibRecord) fServletReferences.get(location);
		}
		if (record == null) {
			record = (ITaglibRecord) fJARReferences.get(location);
		}
		if (record == null) {
			record = (ITaglibRecord) fTLDReferences.get(location);
		}
		if (record == null) {
			record = (ITaglibRecord) fURLReferences.get(reference);
		}
		if (record == null) {
			record = (ITaglibRecord) fTagDirReferences.get(location);
		}
		return record;
	}
}
