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
package org.eclipse.jst.jsp.core.internal.contentmodel;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

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
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.util.DocumentProvider;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.eclipse.wst.xml.uriresolver.internal.util.URIHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ProjectDescription {

	class DeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE) {
				if (resource.getName().endsWith(".tld")) { //$NON-NLS-1$
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeTLD(resource);
					}
					else {
						updateTLD(resource, delta.getKind());
					}
				}
				else if (resource.getName().endsWith(".jar")) { //$NON-NLS-1$
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeJAR(resource);
					}
					else {
						updateJAR(resource, delta.getKind());
					}
				}
				else if (resource.getName().endsWith(".tag") || resource.getName().endsWith(".tagx")) { //$NON-NLS-1$ //$NON-NLS-2$
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeTagDir(resource);
					}
					else {
						updateTagDir(resource, delta.getKind());
					}
				}
				else if (resource.getName().equals(WEB_XML) && resource.getParent().getName().equals(WEB_INF)) {
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeServlets(resource);
					}
					else {
						updateServlets(resource, delta.getKind());
					}
				}
			}
			return resource.getName().length() != 0 && resource.getName().charAt(0) != '.';
		}
	}

	class Indexer implements IResourceProxyVisitor {
		public boolean visit(IResourceProxy proxy) throws CoreException {
			if (proxy.getType() == IResource.FILE) {
				if (proxy.getName().endsWith(".tld")) { //$NON-NLS-1$
					updateTLD(proxy.requestResource(), ITaglibRecordEvent.ADDED);
				}
				else if (proxy.getName().endsWith(".jar")) { //$NON-NLS-1$
					updateJAR(proxy.requestResource(), ITaglibRecordEvent.ADDED);
				}
				else if (proxy.getName().endsWith(".tag") || proxy.getName().endsWith(".tagx")) { //$NON-NLS-1$ //$NON-NLS-2$
					updateTagDir(proxy.requestResource(), ITaglibRecordEvent.ADDED);
				}
				else if (proxy.getName().equals(WEB_XML) && proxy.requestResource().getParent().getName().equals(WEB_INF)) {
					updateServlets(proxy.requestResource(), ITaglibRecordEvent.ADDED);
				}
			}
			String name = proxy.getName();
			return name.length() != 0 && name.charAt(0) != '.';
		}
	}

	class TaglibRecordEvent implements ITaglibRecordEvent {
		ITaglibRecord fTaglibRecord = null;
		int fType = -1;

		TaglibRecordEvent(ITaglibRecord record, int type) {
			fTaglibRecord = record;
			fType = type;
		}

		public ITaglibRecord getTaglibRecord() {
			return fTaglibRecord;
		}

		public int getType() {
			return fType;
		}

		public String toString() {
			String string = fTaglibRecord.toString();
			switch (fType) {
				case ITaglibRecordEvent.ADDED :
					string += " ADDED (" + TaglibRecordEvent.class.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case ITaglibRecordEvent.CHANGED :
					string += " CHANGED (" + TaglibRecordEvent.class.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case ITaglibRecordEvent.REMOVED :
					string += " REMOVED (" + TaglibRecordEvent.class.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				default :
					string += " other:" + fType + " (" + TaglibRecordEvent.class.getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
			}
			return string;
		}
	}

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation")); //$NON-NLS-1$ //$NON-NLS-2$
	static boolean _debugIndexTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indextime")); //$NON-NLS-1$ //$NON-NLS-2$

	private static final String WEB_INF = "WEB-INF"; //$NON-NLS-1$

	private static final IPath WEB_INF_PATH = new Path("WEB-INF"); //$NON-NLS-1$
	private static final String WEB_XML = "web.xml"; //$NON-NLS-1$

	/*
	 * Records active JARs on the classpath. Taglib descriptors should be
	 * usable, but the jars by themselves should not.
	 */
	Hashtable fClasspathJars;

	Stack fClasspathProjects = null;

	// holds references by URI to TLDs
	Hashtable fClasspathReferences;

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
		fClasspathReferences = new Hashtable(0);
		fClasspathJars = new Hashtable(0);
		fJARReferences = new Hashtable(0);
		fTagDirReferences = new Hashtable(0);
		fTLDReferences = new Hashtable(0);
		fServletReferences = new Hashtable(0);
		fImplicitReferences = new Hashtable(0);
	}

	void updateClasspathLibrary(String libraryLocation, int deltaKind) {
		String[] entries = JarUtilities.getEntryNames(libraryLocation);
		JarRecord libraryRecord = (JarRecord) createJARRecord(libraryLocation);
		fClasspathJars.put(libraryLocation, libraryRecord);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].endsWith(".tld")) { //$NON-NLS-1$
				InputStream contents = JarUtilities.getInputStream(libraryLocation, entries[i]);
				if (contents != null) {
					String uri = extractURI(libraryLocation, contents);
					if (uri != null && uri.length() > 0) {
						URLRecord record = new URLRecord();
						record.uri = uri;
						record.baseLocation = libraryLocation;
						try {
							record.url = new URL("jar:file:" + libraryLocation + "!/" + entries[i]); //$NON-NLS-1$ //$NON-NLS-2$
							libraryRecord.urlRecords.add(record);
							fClasspathReferences.put(uri, record);
							if (_debugIndexCreation)
								System.out.println("created record for " + uri + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
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
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(libraryRecord, deltaKind));
	}

	void updateJAR(IResource jar, int deltaKind) {
		if (_debugIndexCreation)
			System.out.println("creating records for JAR " + jar.getFullPath()); //$NON-NLS-1$
		String jarLocationString = jar.getLocation().toString();
		String[] entries = JarUtilities.getEntryNames(jar);
		JarRecord jarRecord = (JarRecord) createJARRecord(jar);
		fJARReferences.put(jar.getFullPath().toString(), jarRecord);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].endsWith(".tld")) { //$NON-NLS-1$
				InputStream contents = JarUtilities.getInputStream(jar, entries[i]);
				if (contents != null) {
					String uri = extractURI(jarLocationString, contents);
					if (uri != null && uri.length() > 0) {
						URLRecord record = new URLRecord();
						record.uri = uri;
						record.baseLocation = jarLocationString;
						try {
							record.url = new URL("jar:file:" + jarLocationString + "!/" + entries[i]); //$NON-NLS-1$ //$NON-NLS-2$
							jarRecord.urlRecords.add(record);
							getImplicitReferences(jarLocationString).put(uri, record);
							if (_debugIndexCreation)
								System.out.println("created record for " + uri + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
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
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(jarRecord, deltaKind));
	}

	void updateServlets(IResource webxml, int deltaKind) {
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
			System.out.println("creating records for " + webxml.getFullPath()); //$NON-NLS-1$

		ServletRecord servletRecord = new ServletRecord();
		servletRecord.location = webxml.getLocation();
		fServletReferences.put(servletRecord.getWebXML().toString(), servletRecord);
		NodeList taglibs = document.getElementsByTagName(JSP12TLDNames.TAGLIB);
		for (int i = 0; i < taglibs.getLength(); i++) {
			String uri = readTextofChild(taglibs.item(i), "taglib-uri").trim(); //$NON-NLS-1$
			// specified location is relative to root of the webapp
			String location = readTextofChild(taglibs.item(i), "taglib-location").trim(); //$NON-NLS-1$
			TLDRecord record = new TLDRecord();
			record.uri = uri;
			if (location.startsWith("/")) { //$NON-NLS-1$
				record.location = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(getLocalRoot(webxml.getLocation().toString()) + location);
			}
			else {
				record.location = new Path(URIHelper.normalize(location, webxml.getLocation().toString(), getLocalRoot(webxml.getLocation().toString())));
			}
			servletRecord.tldRecords.add(record);
			getImplicitReferences(webxml.getLocation().toString()).put(uri, record);
			if (_debugIndexCreation)
				System.out.println("created record for " + uri + "@" + record.location); //$NON-NLS-1$ //$NON-NLS-2$
		}
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(servletRecord, deltaKind));
	}

	void updateTagDir(IResource tagFile, int deltaKind) {
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

	void updateTLD(IResource tld, int deltaKind) {
		if (_debugIndexCreation)
			System.out.println("creating record for " + tld.getFullPath()); //$NON-NLS-1$
		TLDRecord record = createTLDRecord(tld);
		fTLDReferences.put(tld.getFullPath().toString(), record);
		if (record.uri != null) {
			getImplicitReferences(tld.getLocation().toString()).put(record.uri, record);
		}
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, deltaKind));
	}

	void clear() {
	}

	/**
	 * @param resource
	 * @return
	 */
	private ITaglibRecord createJARRecord(IResource jar) {
		return createJARRecord(jar.getLocation().toString());
	}

	private ITaglibRecord createJARRecord(String fileLocation) {
		JarRecord record = new JarRecord();
		record.location = new Path(fileLocation);
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
			if (tagdir.getName().equals("tags")) //$NON-NLS-1$
				record.shortName = "tags"; //$NON-NLS-1$
			else {
				IPath tagdirPath = tagdir.getFullPath();
				String[] segments = tagdirPath.segments();
				for (int i = 1; record.shortName == null && i < segments.length; i++) {
					if (segments[i - 1].equals("WEB-INF") && segments[i].equals("tags")) { //$NON-NLS-1$ //$NON-NLS-2$
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
						uri.append(text.getNodeValue().trim());
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
			System.out.println("indexed " + fProject.getName() + " in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	void indexClasspath() {
		time0 = System.currentTimeMillis();
		fClasspathProjects = new Stack();
		fClasspathReferences.clear();
		fClasspathJars.clear();
		IJavaProject javaProject = JavaCore.create(fProject);
		indexClasspath(javaProject);
		if (_debugIndexTime)
			System.out.println("indexed " + fProject.getName() + " classpath in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * @param entry
	 */
	private void indexClasspath(IClasspathEntry entry) {
		switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_CONTAINER : {
				IClasspathContainer container = (IClasspathContainer) entry;
				IClasspathEntry[] containedEntries = container.getClasspathEntries();
				for (int i = 0; i < containedEntries.length; i++) {
					indexClasspath(containedEntries[i]);
				}
			}
				break;
			case IClasspathEntry.CPE_LIBRARY : {
				/*
				 * Ignore libs in required projects that are not exported
				 */
				if (fClasspathProjects.size() < 2 || entry.isExported()) {
					IPath libPath = entry.getPath();
					if (!fClasspathJars.containsKey(libPath.toString())) {
						if (libPath.toFile().exists()) {
							updateClasspathLibrary(libPath.toString(), ITaglibRecordEvent.CHANGED);
						}
						else {
							IFile libFile = ResourcesPlugin.getWorkspace().getRoot().getFile(libPath);
							if (libFile != null && libFile.exists()) {
								updateClasspathLibrary(libFile.getLocation().toString(), ITaglibRecordEvent.CHANGED);
							}
						}
					}
				}
			}
				break;
			case IClasspathEntry.CPE_PROJECT : {
				/*
				 * Ignore required projects of required projects that are not
				 * exported
				 */
				if (fClasspathProjects.size() < 2 || entry.isExported()) {
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(entry.getPath().lastSegment());
					if (project != null && !fClasspathProjects.contains(project.getName())) {
						indexClasspath(JavaCore.create(project));
					}
				}
			}
				break;
			case IClasspathEntry.CPE_SOURCE :
				break;
			case IClasspathEntry.CPE_VARIABLE :
				break;
		}
	}

	/**
	 * @param javaProject
	 */
	private void indexClasspath(IJavaProject javaProject) {
		if (javaProject != null && javaProject.exists()) {
			fClasspathProjects.push(javaProject.getElementName());
			try {
				IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
				for (int i = 0; i < entries.length; i++) {
					indexClasspath(entries[i]);
				}
			}
			catch (JavaModelException e) {
				Logger.logException("Error searching Java Build Path + (" + fProject.getName() + ") for tag libraries", e); //$NON-NLS-1$ //$NON-NLS-2$
			}
			fClasspathProjects.pop();
		}
	}

	protected String readTextofChild(Node node, String childName) {
		StringBuffer buffer = new StringBuffer();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childName)) {
				Node text = child.getFirstChild();
				while (text != null) {
					buffer.append(text.getNodeValue().trim());
					text = text.getNextSibling();
				}
			}
		}
		return buffer.toString();
	}

	void removeClasspathLibrary(String libraryLocation) {
		JarRecord record = (JarRecord) fClasspathJars.remove(libraryLocation);
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				fClasspathReferences.remove(records[i].getURI());
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVED));
		}
	}

	void removeJAR(IResource jar) {
		if (_debugIndexCreation)
			System.out.println("removing records for JAR " + jar.getFullPath()); //$NON-NLS-1$
		JarRecord record = (JarRecord) fJARReferences.remove(jar.getFullPath());
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				getImplicitReferences(jar.getLocation().toString()).remove(records[i].getURI());
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVED));
		}
	}

	void removeServlets(IResource webxml) {
		if (_debugIndexCreation)
			System.out.println("removing records for " + webxml.getFullPath()); //$NON-NLS-1$
		ServletRecord record = (ServletRecord) fServletReferences.remove(webxml.getLocation().toString());
		if (record != null) {
			TLDRecord[] records = (TLDRecord[]) record.getTLDRecords().toArray(new TLDRecord[0]);
			for (int i = 0; i < records.length; i++) {
				if (_debugIndexCreation)
					System.out.println("removed record for " + records[i].uri + "@" + records[i].location); //$NON-NLS-1$ //$NON-NLS-2$
				getImplicitReferences(webxml.getLocation().toString()).remove(records[i].getURI());
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVED));
		}
	}

	void removeTagDir(IResource tagFile) {
		// IContainer tagdir = tagFile.getParent();
		// String tagdirLocation = tagdir.getFullPath().toString();
		// fTagDirReferences.remove(tagdirLocation);
	}

	void removeTLD(IResource tld) {
		if (_debugIndexCreation)
			System.out.println("removing record for " + tld.getFullPath()); //$NON-NLS-1$
		TLDRecord record = (TLDRecord) fTLDReferences.remove(tld.getFullPath());
		if (record != null) {
			if (record.uri != null) {
				getImplicitReferences(tld.getLocation().toString()).remove(record.uri);
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVED));
		}
	}

	/**
	 * @param basePath
	 * @param reference
	 * @return
	 */
	ITaglibRecord resolve(String basePath, String reference) {
		ITaglibRecord record = null;
		String location = null;

		/**
		 * Workaround for problem in URIHelper; uris starting with '/' are
		 * returned as-is.
		 */
		if (reference.startsWith("/")) { //$NON-NLS-1$
			location = getLocalRoot(basePath) + reference;
		}
		else {
			location = URIHelper.normalize(reference, basePath, getLocalRoot(basePath));
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
			record = (ITaglibRecord) getImplicitReferences(basePath).get(reference);
		}
		if (record == null) {
			record = (ITaglibRecord) fTagDirReferences.get(location);
		}
		if (record == null) {
			record = (ITaglibRecord) fClasspathReferences.get(reference);
		}
		return record;
	}
}
