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
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.contentmodel.tld.DocumentProvider;
import org.eclipse.jst.jsp.core.contentmodel.tld.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.Logger;
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

	class TaglibRecordEvent implements ITaglibRecordEvent {
		ITaglibRecord fTaglibRecord = null;
		short fType = -1;

		TaglibRecordEvent(ITaglibRecord record, short type) {
			fTaglibRecord = record;
			fType = type;
		}

		public ITaglibRecord getTaglibRecord() {
			return fTaglibRecord;
		}

		public short getType() {
			return fType;
		}
	}

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation"));
	static boolean _debugIndexTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indextime"));

	private static final String WEB_INF = "WEB-INF";

	private static final IPath WEB_INF_PATH = new Path("WEB-INF");
	private static final String WEB_XML = "web.xml";

	// this table is special in that it holds tables of references according
	// to local roots
	Hashtable fImplicitReferences;
	Hashtable fJARReferences;
	IProject fProject;
	Hashtable fServletReferences;
	Hashtable fTagDirReferences;

	Hashtable fTLDReferences;

	IResourceDeltaVisitor fVisitor;

	private long time0;

	ProjectDescription(IProject project) {
		super();
		fProject = project;
		fJARReferences = new Hashtable(0);
		fTagDirReferences = new Hashtable(0);
		fTLDReferences = new Hashtable(0);
		fServletReferences = new Hashtable(0);
		fImplicitReferences = new Hashtable(0);
	}

	void addJAR(IResource jar) {
		if (_debugIndexCreation)
			System.out.println("creating records for JAR " + jar.getFullPath());
		String jarLocationString = jar.getLocation().toString();
		String[] entries = JarUtilities.getEntryNames(jar);
		JarRecord jarRecord = (JarRecord) createJARRecord(jar);
		fJARReferences.put(jar.getFullPath().toString(), jarRecord);
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
							getImplicitReferences(jar.getLocation().toString()).put(uri, record);
							if (_debugIndexCreation)
								System.out.println("created record for " + uri + "@" + record.getURL());
						}
						catch (MalformedURLException e) {
							// don't record this URI
							Logger.logException(e);
						}
					}
					try {
						contents.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(jarRecord, ITaglibRecordEvent.ADD));
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
			Logger.logException(e);
		}
		finally {
			if (webxmlContents != null)
				try {
					webxmlContents.close();
				}
				catch (IOException e1) {
					// ignore
				}
		}
		if (document == null)
			return;
		if (_debugIndexCreation)
			System.out.println("creating records for " + webxml.getFullPath());

		ServletRecord servletRecord = new ServletRecord();
		servletRecord.location = webxml.getLocation();
		fServletReferences.put(servletRecord.getWebXML().toString(), servletRecord);
		NodeList taglibs = document.getElementsByTagName(JSP12TLDNames.TAGLIB);
		for (int i = 0; i < taglibs.getLength(); i++) {
			String uri = readTextofChild(taglibs.item(i), "taglib-uri");
			// specified location is relative to root of the webapp
			String location = readTextofChild(taglibs.item(i), "taglib-location");
			TLDRecord record = new TLDRecord();
			record.uri = uri;
			if (location.startsWith("/")) {
				record.location = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(getLocalRoot(webxml.getLocation().toString()) + location);
			}
			else {
				record.location = new Path(URIHelper.normalize(location, webxml.getLocation().toString(), getLocalRoot(webxml.getLocation().toString())));
			}
			servletRecord.tldRecords.add(record);
			getImplicitReferences(webxml.getLocation().toString()).put(uri, record);
			if (_debugIndexCreation)
				System.out.println("created record for " + uri + "@" + record.location);
		}
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(servletRecord, ITaglibRecordEvent.ADD));
	}

	void addTagDir(IResource tagFile) {
		return;
		/**
		 * Make sure the tag file is n a WEB-INF/tags folder because of the
		 * shortname computation requirements
		 */
		// if ((tagFile.getType() & IResource.FOLDER) > 0 ||
		// tagFile.getFullPath().toString().indexOf("WEB-INF/tags") < 0)
		// return;
		// TagDirRecord record = createTagdirRecord(tagFile);
		// if (record != null) {
		// record.tags.add(tagFile.getName());
		// }
	}

	void addTLD(IResource tld) {
		if (_debugIndexCreation)
			System.out.println("creating record for " + tld.getFullPath());
		TLDRecord record = createTLDRecord(tld);
		fTLDReferences.put(tld.getFullPath().toString(), record);
		if (record.uri != null) {
			getImplicitReferences(tld.getLocation().toString()).put(record.uri, record);
		}
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.ADD));
	}

	void clear() {
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
	 * @return
	 */
	TagDirRecord createTagdirRecord(IResource tagFile) {
		IContainer tagdir = tagFile.getParent();
		String tagdirLocation = tagdir.getFullPath().toString();
		TagDirRecord record = (TagDirRecord) fTagDirReferences.get(tagdirLocation);
		if (record == null) {
			record = new TagDirRecord();
			record.location = tagdir.getFullPath();
			// JSP 2.0 section 8.4.3
			if (tagdir.getName().equals("tags"))
				record.shortName = "tags";
			else {
				IPath tagdirPath = tagdir.getFullPath();
				String[] segments = tagdirPath.segments();
				for (int i = 1; record.shortName == null && i < segments.length; i++) {
					if (segments[i - 1].equals("WEB-INF") && segments[i].equals("tags")) {
						IPath tagdirLocalPath = tagdirPath.removeFirstSegments(i + 1);
						record.shortName = tagdirLocalPath.toString().replace('/', '-');
					}
				}
			}

		}
		return record;
	}

	/**
	 * @param resource
	 * @return
	 */
	private TLDRecord createTLDRecord(IResource tld) {
		TLDRecord record = new TLDRecord();
		record.location = tld.getLocation();
		InputStream contents = null;
		try {
			contents = ((IFile) tld).getContents(true);
			String baseLocation = record.location.toString();
			String defaultURI = extractURI(baseLocation, contents);
			if (defaultURI != null && defaultURI.length() > 0) {
				record.uri = defaultURI;
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		finally {
			try {
				if (contents != null) {
					contents.close();
				}
			}
			catch (IOException e) {
				// ignore
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
		provider.setRootElementName(JSP12TLDNames.TAGLIB);
		provider.setBaseReference(baseLocation);
		result = provider.getRootElement();
		if (result.getNodeType() != Node.ELEMENT_NODE)
			return null;
		Element taglibElement = (Element) result;
		if (taglibElement != null) {
			Node child = taglibElement.getFirstChild();
			while (child != null && !(child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(JSP12TLDNames.URI))) {
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

	synchronized List getAvailableTaglibRecords(IPath location) {
		Collection implicitReferences = getImplicitReferences(location.toString()).values();
		List records = new ArrayList(fTLDReferences.size() + fTagDirReferences.size() + fJARReferences.size() + fServletReferences.size());
		records.addAll(fTLDReferences.values());
		records.addAll(fTagDirReferences.values());
		records.addAll(fJARReferences.values());
		records.addAll(fServletReferences.values());
		records.addAll(implicitReferences);
		return records;
	}

	/**
	 * @return Returns the implicitReferences for the given path
	 */
	Hashtable getImplicitReferences(String location) {
		String localRoot = getLocalRoot(location);
		Hashtable implicitReferences = (Hashtable) fImplicitReferences.get(localRoot);
		if (implicitReferences == null) {
			implicitReferences = new Hashtable(1);
			fImplicitReferences.put(localRoot, implicitReferences);
		}
		return implicitReferences;
	}

	/**
	 * @param baseLocation
	 * @return the applicable Web context root path, if one exists
	 */
	IPath getLocalRoot(IPath baseLocation) {
		IResource file = FileBuffers.getWorkspaceFileAtLocation(baseLocation);
		while (file != null) {
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
			// getFolder on a workspace root must use a full path, skip
			if (folder != null && (folder.getType() & IResource.ROOT) == 0) {
				IFolder webinf = folder.getFolder(WEB_INF_PATH);
				if (webinf != null && webinf.exists()) {
					return folder.getFullPath();
				}
			}
			file = file.getParent();
		}

		return fProject.getFullPath();
	}

	/**
	 * @param baseLocation
	 * @return
	 */
	private String getLocalRoot(String baseLocation) {
		return getLocalRoot(new Path(baseLocation)).toString();
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
			Logger.logException(e);
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
		if (_debugIndexCreation)
			System.out.println("removing records for JAR " + jar.getFullPath());
		JarRecord record = (JarRecord) fJARReferences.remove(jar.getFullPath());
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				getImplicitReferences(jar.getLocation().toString()).remove(records[i].getURI());
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVE));
		}
	}

	void removeServlets(IResource webxml) {
		if (_debugIndexCreation)
			System.out.println("removing records for " + webxml.getFullPath());
		ServletRecord record = (ServletRecord) fServletReferences.remove(webxml.getLocation().toString());
		if (record != null) {
			TLDRecord[] records = (TLDRecord[]) record.getTLDRecords().toArray(new TLDRecord[0]);
			for (int i = 0; i < records.length; i++) {
				if (_debugIndexCreation)
					System.out.println("removed record for " + records[i].uri + "@" + records[i].location);
				getImplicitReferences(webxml.getLocation().toString()).remove(records[i].getURI());
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVE));
		}
	}

	void removeTagDir(IResource tagFile) {
		// IContainer tagdir = tagFile.getParent();
		// String tagdirLocation = tagdir.getFullPath().toString();
		// fTagDirReferences.remove(tagdirLocation);
	}

	void removeTLD(IResource tld) {
		if (_debugIndexCreation)
			System.out.println("removing record for " + tld.getFullPath());
		TLDRecord record = (TLDRecord) fTLDReferences.remove(tld.getFullPath());
		if (record != null) {
			if (record.uri != null) {
				getImplicitReferences(tld.getLocation().toString()).remove(record.uri);
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVE));
		}
	}

	/**
	 * @param path
	 * @param reference
	 * @return
	 */
	ITaglibRecord resolve(String path, String reference) {
		ITaglibRecord record = null;
		String location = null;

		/**
		 * Workaround for problem in URIHelper; uris starting with '/' are
		 * returned as-is.
		 */
		if (reference.startsWith("/")) {
			location = getLocalRoot(path) + reference;
		}
		else {
			location = URIHelper.normalize(reference, path, getLocalRoot(path));
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
			record = (ITaglibRecord) getImplicitReferences(path).get(reference);
		}
		if (record == null) {
			record = (ITaglibRecord) fTagDirReferences.get(location);
		}
		return record;
	}
}
