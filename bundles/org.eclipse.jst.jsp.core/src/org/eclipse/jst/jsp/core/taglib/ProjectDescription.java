/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.taglib;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
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
import org.eclipse.core.resources.IWorkspaceRoot;
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
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.util.DocumentProvider;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.EntityReference;
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
						removeWebXML(resource);
					}
					else {
						updateWebXML(resource, delta.getKind());
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
					updateWebXML(proxy.requestResource(), ITaglibRecordEvent.ADDED);
				}
			}
			String name = proxy.getName();
			return name.length() != 0 && name.charAt(0) != '.';
		}
	}

	static class JarRecord implements IJarRecord {
		boolean has11TLD;
		TaglibInfo info;
		IPath location;
		List urlRecords;

		public boolean equals(Object obj) {
			if (!(obj instanceof JarRecord))
				return false;
			return ((JarRecord) obj).location.equals(location) && ((JarRecord) obj).has11TLD == has11TLD;
		}

		/**
		 * @return Returns the location.
		 */
		public IPath getLocation() {
			return location;
		}

		/**
		 * @return Returns the recommended/default prefix if one was given.
		 */
		public String getShortName() {
			if (info == null)
				return null;
			return info.shortName;
		}

		public int getRecordType() {
			return ITaglibRecord.JAR;
		}

		/**
		 * 
		 */
		public List getURLRecords() {
			return urlRecords;
		}

		public String toString() {
			return "JarRecord: " + location + " <-> " + urlRecords; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class TagDirRecord implements ITagDirRecord {
		IPath location;
		String shortName;
		// a List holding Strings of .tag and .tagx filenames relative to the
		// tagdir's location
		List tags = new ArrayList(0);

		public boolean equals(Object obj) {
			if (!(obj instanceof TagDirRecord))
				return false;
			return ((TagDirRecord) obj).location.equals(location);
		}

		/**
		 * @return Returns the location.
		 */
		public IPath getLocation() {
			return location;
		}

		public int getRecordType() {
			return ITaglibRecord.TAGDIR;
		}

		/**
		 * @return Returns the shortName.
		 */
		public String getShortName() {
			return shortName;
		}

		/**
		 * @return Returns the tags.
		 */
		public String[] getTags() {
			return (String[]) tags.toArray(new String[tags.size()]);
		}

		public String toString() {
			return "TagdirRecord: " + location + " <-> " + shortName; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class TaglibInfo {
		String description;
		// extract only when asked?
		float jspVersion;
		String largeIcon;
		String shortName;
		String smallIcon;
		String tlibVersion;
		String uri;
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

	static class TLDRecord implements ITLDRecord {
		TaglibInfo info;
		IPath path;

		public boolean equals(Object obj) {
			if (!(obj instanceof TLDRecord))
				return false;
			return ((TLDRecord) obj).path.equals(path) && ((TLDRecord) obj).getURI().equals(getURI());
		}

		public IPath getPath() {
			return path;
		}

		public String getShortName() {
			if (info == null)
				return null;
			return info.shortName;
		}

		public int getRecordType() {
			return ITaglibRecord.TLD;
		}

		/**
		 * @return Returns the uri.
		 */
		public String getURI() {
			if (info == null)
				return null;
			return info.uri;
		}

		public String toString() {
			return "TLDRecord: " + path + " <-> " + getURI(); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class URLRecord implements IURLRecord {
		String baseLocation;
		TaglibInfo info;
		URL url;

		public URLRecord() {
			super();
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof URLRecord))
				return false;
			return ((URLRecord) obj).baseLocation.equals(baseLocation) && ((URLRecord) obj).url.equals(url);
		}

		public String getBaseLocation() {
			return baseLocation;
		}

		/**
		 * @return Returns the recommended/default prefix if one was given.
		 */
		public String getShortName() {
			if (info == null)
				return null;
			return info.shortName;
		}

		public int getRecordType() {
			return ITaglibRecord.URL;
		}

		/**
		 * @return Returns the uri.
		 */
		public String getURI() {
			if (info == null)
				return ""; //$NON-NLS-1$
			return info.uri;
		}

		/**
		 * @return Returns the URL.
		 */
		public URL getURL() {
			return url;
		}

		public String toString() {
			return "URLRecord: " + baseLocation + " <-> " + getURI(); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class WebXMLRecord {
		TaglibInfo info;
		IPath path;
		List tldRecords = new ArrayList(0);

		public boolean equals(Object obj) {
			if (!(obj instanceof WebXMLRecord))
				return false;
			return ((WebXMLRecord) obj).path.equals(path);
		}

		/**
		 * @return Returns the recommended/default prefix if one was given.
		 */
		public String getPrefix() {
			if (info == null)
				return null;
			return info.shortName;
		}

		/**
		 * 
		 */
		public List getTLDRecords() {
			return tldRecords;
		}

		/**
		 * @return Returns the webxml.
		 */
		public IPath getWebXML() {
			return path;
		}

		public String toString() {
			return "WebXMLRecord: " + path + " " + tldRecords; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation")); //$NON-NLS-1$ //$NON-NLS-2$
	static boolean _debugIndexTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indextime")); //$NON-NLS-1$ //$NON-NLS-2$

	private static final String WEB_INF = "WEB-INF"; //$NON-NLS-1$
	private static final IPath WEB_INF_PATH = new Path(WEB_INF);
	private static final String WEB_XML = "web.xml"; //$NON-NLS-1$

	/*
	 * Records active JARs on the classpath. Taglib descriptors should be
	 * usable, but the jars by themselves are not.
	 */
	Hashtable fClasspathJars;

	Stack fClasspathProjects = null;

	// holds references by URI to JARs
	Hashtable fClasspathReferences;

	// this table is special in that it holds tables of references according
	// to local roots
	Hashtable fImplicitReferences;

	Hashtable fJARReferences;

	IProject fProject;

	Hashtable fTagDirReferences;

	Hashtable fTLDReferences;

	IResourceDeltaVisitor fVisitor;

	Hashtable fWebXMLReferences;

	private long time0;

	ProjectDescription(IProject project) {
		super();
		fProject = project;
		fClasspathReferences = new Hashtable(0);
		fClasspathJars = new Hashtable(0);
		fJARReferences = new Hashtable(0);
		fTagDirReferences = new Hashtable(0);
		fTLDReferences = new Hashtable(0);
		fWebXMLReferences = new Hashtable(0);
		fImplicitReferences = new Hashtable(0);
	}

	private Collection _getJSP11JarReferences(Collection allJARs) {
		List collection = new ArrayList(allJARs.size());
		Iterator i = allJARs.iterator();
		while (i.hasNext()) {
			JarRecord record = (JarRecord) i.next();
			if (record.has11TLD) {
				collection.add(record);
			}
		}
		return collection;
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
		record.path = tld.getFullPath();
		InputStream contents = null;
		try {
			if (tld.getLocation() != null) {
				contents = ((IFile) tld).getContents(true);
				String baseLocation = tld.getLocation().toString();
				TaglibInfo info = extractInfo(baseLocation, contents);
				if (info != null) {
					record.info = info;
				}
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

	private TaglibInfo extractInfo(String baseLocation, InputStream tldContents) {
		TaglibInfo info = new TaglibInfo();
		if (tldContents != null) {
			DocumentProvider provider = new DocumentProvider();
			provider.setInputStream(tldContents);
			provider.setValidating(false);
			provider.setRootElementName(JSP12TLDNames.TAGLIB);
			provider.setBaseReference(baseLocation);
			Node child = provider.getRootElement();
			if (child == null || child.getNodeType() != Node.ELEMENT_NODE || !child.getNodeName().equals(JSP12TLDNames.TAGLIB)) {
				return null;
			}
			child = child.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if (child.getNodeName().equals(JSP12TLDNames.URI)) {
						info.uri = getTextContents(child);
					}
					else if (child.getNodeName().equals(JSP12TLDNames.SHORT_NAME) || child.getNodeName().equals(JSP11TLDNames.SHORTNAME)) {
						info.shortName = getTextContents(child);
					}
					else if (child.getNodeName().equals(JSP12TLDNames.DESCRIPTION) || child.getNodeName().equals(JSP11TLDNames.INFO)) {
						info.description = getTextContents(child);
					}
					else if (child.getNodeName().equals(JSP12TLDNames.JSP_VERSION) || child.getNodeName().equals(JSP11TLDNames.JSPVERSION)) {
						try {
							info.jspVersion = Float.parseFloat(getTextContents(child));
						}
						catch (NumberFormatException e) {
							info.jspVersion = 1;
						}
					}
					else if (child.getNodeName().equals(JSP12TLDNames.TLIB_VERSION) || child.getNodeName().equals(JSP11TLDNames.TLIBVERSION)) {
						info.tlibVersion = getTextContents(child);
					}
					else if (child.getNodeName().equals(JSP12TLDNames.SMALL_ICON)) {
						info.smallIcon = getTextContents(child);
					}
					else if (child.getNodeName().equals(JSP12TLDNames.LARGE_ICON)) {
						info.largeIcon = getTextContents(child);
					}
				}
				child = child.getNextSibling();
			}
		}
		return info;
	}

	List getAvailableTaglibRecords(IPath path) {
		Collection implicitReferences = getImplicitReferences(path.toString()).values();
		Collection records = new HashSet(fTLDReferences.size() + fTagDirReferences.size() + fJARReferences.size() + fWebXMLReferences.size());
		records.addAll(implicitReferences);
		records.addAll(fTLDReferences.values());
		records.addAll(fTagDirReferences.values());
		Collection jars = fJARReferences.values();
		records.addAll(_getJSP11JarReferences(jars));
		Iterator i = jars.iterator();
		while (i.hasNext()) {
			JarRecord record = (JarRecord) i.next();
			records.addAll(record.urlRecords);
		}
		records.addAll(fClasspathReferences.values());
		return new ArrayList(records);
	}

	/**
	 * @return Returns the implicitReferences for the given path
	 */
	Hashtable getImplicitReferences(String path) {
		String localRoot = getLocalRoot(path);
		Hashtable implicitReferences = (Hashtable) fImplicitReferences.get(localRoot);
		if (implicitReferences == null) {
			implicitReferences = new Hashtable(1);
			fImplicitReferences.put(localRoot, implicitReferences);
		}
		return implicitReferences;
	}

	/**
	 * @param basePath
	 * @return the applicable Web context root path, if one exists
	 */
	IPath getLocalRoot(IPath basePath) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		// existing workspace resources - this is the 93% case
		IResource file = null;

		if (file == null) {
			IFile[] files = workspaceRoot.findFilesForLocation(basePath.makeAbsolute());
			for (int i = 0; file == null && i < files.length; i++) {
				IPath normalizedPath = null;
				/*
				 * existing workspace resources referenced by their file
				 * system path files that do not exist (including
				 * non-accessible files) do not pass
				 */
				if (files[i].exists()) {
					normalizedPath = files[i].getFullPath();

					if (normalizedPath.segmentCount() > 1 && normalizedPath.segment(0).equals(fProject.getFullPath().segment(0))) {
						// @see IContainer#getFile for the required number of
						// segments
						file = workspaceRoot.getFile(normalizedPath);
					}
				}
			}
		}

		if (file == null) {
			file = FileBuffers.getWorkspaceFileAtLocation(basePath);
		}

		// Try it as a folder first
		if (basePath.segmentCount() > 1) {
			file = workspaceRoot.getFolder(basePath);
		}
		// If not a folder, then try as a file
		if (file != null && !file.exists()) {
			file = workspaceRoot.getFile(basePath);
		}

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
	 * @param basePath
	 * @return
	 */
	private String getLocalRoot(String basePath) {
		return getLocalRoot(new Path(basePath)).toString();
	}

	private String getTextContents(Node parent) {
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
			}
			else {
				s.append(child.getNodeValue().trim());
			}
			child = child.getNextSibling();
		}
		return s.toString().trim();
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
		fWebXMLReferences.clear();
		try {
			fProject.accept(new Indexer(), 0);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}

		if (_debugIndexTime)
			Logger.log(Logger.INFO_DEBUG, "indexed " + fProject.getName() + " contents in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	void indexClasspath() {
		time0 = System.currentTimeMillis();
		fClasspathProjects = new Stack();
		fClasspathReferences.clear();
		fClasspathJars.clear();
		IJavaProject javaProject = JavaCore.create(fProject);
		indexClasspath(javaProject);
		if (_debugIndexTime)
			Logger.log(Logger.INFO_DEBUG, "indexed " + fProject.getName() + " classpath in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
							/*
							 * Note: .jars on the classpath inside of the
							 * project will have duplicate entries in the JAR
							 * references table that will e returned to
							 * getAvailableTaglibRecords().
							 */
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
		if (javaProject != null && javaProject.exists() && !fClasspathProjects.contains(javaProject.getElementName())) {
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

	private String readTextofChild(Node node, String childName) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childName)) {
				return getTextContents(child);
			}
		}
		return ""; //$NON-NLS-1$
	}

	/*
	 * private void removeClasspathLibrary(String libraryLocation) { JarRecord
	 * record = (JarRecord) fClasspathJars.remove(libraryLocation); if (record !=
	 * null) { URLRecord[] records = (URLRecord[])
	 * record.getURLRecords().toArray(new URLRecord[0]); for (int i = 0; i <
	 * records.length; i++) {
	 * fClasspathReferences.remove(records[i].getURI()); }
	 * TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record,
	 * ITaglibRecordEvent.REMOVED)); } }
	 */

	void removeJAR(IResource jar) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO_DEBUG, "removing records for JAR " + jar.getFullPath()); //$NON-NLS-1$
		JarRecord record = (JarRecord) fJARReferences.remove(jar.getFullPath().toString());
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(records[i], ITaglibRecordEvent.REMOVED));
				getImplicitReferences(jar.getFullPath().toString()).remove(records[i].getURI());
			}
			if (record.has11TLD) {
				TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVED));
			}
		}
	}

	void removeTagDir(IResource tagFile) {
		// IContainer tagdir = tagFile.getParent();
		// String tagdirLocation = tagdir.getFullPath().toString();
		// fTagDirReferences.remove(tagdirLocation);
	}

	void removeTLD(IResource tld) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO_DEBUG, "removing record for " + tld.getFullPath()); //$NON-NLS-1$
		TLDRecord record = (TLDRecord) fTLDReferences.remove(tld.getFullPath());
		if (record != null) {
			if (record.getURI() != null) {
				getImplicitReferences(tld.getFullPath().toString()).remove(record.getURI());
			}
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.REMOVED));
		}
	}

	void removeWebXML(IResource webxml) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO_DEBUG, "removing records for " + webxml.getFullPath()); //$NON-NLS-1$
		WebXMLRecord record = (WebXMLRecord) fWebXMLReferences.remove(webxml.getLocation().toString());
		if (record != null) {
			TLDRecord[] records = (TLDRecord[]) record.getTLDRecords().toArray(new TLDRecord[0]);
			for (int i = 0; i < records.length; i++) {
				if (_debugIndexCreation)
					Logger.log(Logger.INFO_DEBUG, "removed record for " + records[i].getURI() + "@" + records[i].path); //$NON-NLS-1$ //$NON-NLS-2$
				getImplicitReferences(webxml.getFullPath().toString()).remove(records[i].getURI());
				TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(records[i], ITaglibRecordEvent.REMOVED));
			}
		}
	}

	/**
	 * @param basePath
	 * @param reference
	 * @return
	 */
	ITaglibRecord resolve(String basePath, String reference) {
		ITaglibRecord record = null;
		String path = null;

		/**
		 * Workaround for problem in URIHelper; uris starting with '/' are
		 * returned as-is.
		 */
		if (reference.startsWith("/")) { //$NON-NLS-1$
			path = getLocalRoot(basePath) + reference;
		}
		else {
			path = URIHelper.normalize(reference, basePath, getLocalRoot(basePath));
		}
		// order dictated by JSP spec 2.0 section 7.2.3
		// if (record == null) {
		// record = (ITaglibRecord) fWebXMLReferences.get(path);
		// }
		if (record == null) {
			record = (ITaglibRecord) fJARReferences.get(path);
			// only if 1.1 TLD was found
			if (record instanceof JarRecord && !((JarRecord) record).has11TLD) {
				record = null;
			}
		}
		if (record == null) {
			record = (ITaglibRecord) fTLDReferences.get(path);
		}
		if (record == null) {
			record = (ITaglibRecord) getImplicitReferences(basePath).get(reference);
		}


		if (record == null) {
			record = (ITaglibRecord) fTagDirReferences.get(path);
		}

		if (record == null) {
			record = (ITaglibRecord) fClasspathReferences.get(reference);
		}

		// If no records were found and no local-root applies, check ALL of
		// the web.xml files as a fallback
		if (record == null && fProject.getFullPath().toString().equals(getLocalRoot(basePath))) {
			WebXMLRecord[] webxmls = (WebXMLRecord[]) fWebXMLReferences.values().toArray(new WebXMLRecord[0]);
			for (int i = 0; i < webxmls.length; i++) {
				if (record != null)
					continue;
				record = (ITaglibRecord) getImplicitReferences(webxmls[i].path.toString()).get(reference);
			}
		}
		return record;
	}

	void updateClasspathLibrary(String libraryLocation, int deltaKind) {
		String[] entries = JarUtilities.getEntryNames(libraryLocation);
		JarRecord libraryRecord = (JarRecord) createJARRecord(libraryLocation);
		fClasspathJars.put(libraryLocation, libraryRecord);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].equals(JarUtilities.JSP11_TAGLIB)) {
				libraryRecord.has11TLD = true;
			}
			if (entries[i].endsWith(".tld")) { //$NON-NLS-1$
				InputStream contents = JarUtilities.getInputStream(libraryLocation, entries[i]);
				if (contents != null) {
					TaglibInfo info = extractInfo(libraryLocation, contents);

					if (info != null && info.uri != null && info.uri.length() > 0) {
						URLRecord record = new URLRecord();
						record.info = info;
						record.baseLocation = libraryLocation;
						try {
							record.url = new URL("jar:file:" + libraryLocation + "!/" + entries[i]); //$NON-NLS-1$ //$NON-NLS-2$
							libraryRecord.urlRecords.add(record);
							fClasspathReferences.put(record.getURI(), record);
							if (_debugIndexCreation)
								Logger.log(Logger.INFO_DEBUG, "created record for " + record.getURI() + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
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
			Logger.log(Logger.INFO_DEBUG, "creating records for JAR " + jar.getFullPath()); //$NON-NLS-1$
		String jarLocationString = jar.getLocation().toString();
		String[] entries = JarUtilities.getEntryNames(jar);
		JarRecord jarRecord = (JarRecord) createJARRecord(jar);
		fJARReferences.put(jar.getFullPath().toString(), jarRecord);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].endsWith(".tld")) { //$NON-NLS-1$
				if (entries[i].equals(JarUtilities.JSP11_TAGLIB)) {
					jarRecord.has11TLD = true;
				}
				InputStream contents = JarUtilities.getInputStream(jar, entries[i]);
				if (contents != null) {
					TaglibInfo info = extractInfo(jarLocationString, contents);

					if (info != null && info.uri != null && info.uri.length() > 0) {
						URLRecord record = new URLRecord();
						record.info = info;
						record.baseLocation = jarLocationString;
						try {
							record.url = new URL("jar:file:" + jarLocationString + "!/" + entries[i]); //$NON-NLS-1$ //$NON-NLS-2$
							jarRecord.urlRecords.add(record);
							getImplicitReferences(jar.getFullPath().toString()).put(record.getURI(), record);
							TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, ITaglibRecordEvent.ADDED));
							if (_debugIndexCreation)
								Logger.log(Logger.INFO_DEBUG, "created record for " + record.getURI() + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
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
				else {
					Logger.log(Logger.ERROR, getClass().getName() + "could not read resource " + jar.getFullPath()); //$NON-NLS-1$
				}
			}
		}
		if (jarRecord.has11TLD) {
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(jarRecord, deltaKind));
		}
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
			Logger.log(Logger.INFO_DEBUG, "creating record for " + tld.getFullPath()); //$NON-NLS-1$
		TLDRecord record = createTLDRecord(tld);
		fTLDReferences.put(tld.getFullPath().toString(), record);
		if (record.getURI() != null) {
			getImplicitReferences(tld.getFullPath().toString()).put(record.getURI(), record);
		}
		TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, deltaKind));
	}

	void updateWebXML(IResource webxml, int deltaKind) {
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
			Logger.log(Logger.INFO_DEBUG, "creating records for " + webxml.getFullPath()); //$NON-NLS-1$

		WebXMLRecord webxmlRecord = new WebXMLRecord();
		webxmlRecord.path = webxml.getFullPath();
		fWebXMLReferences.put(webxmlRecord.getWebXML().toString(), webxmlRecord);
		NodeList taglibs = document.getElementsByTagName(JSP12TLDNames.TAGLIB);
		for (int i = 0; i < taglibs.getLength(); i++) {
			String taglibUri = readTextofChild(taglibs.item(i), "taglib-uri").trim(); //$NON-NLS-1$
			// specified location is relative to root of the webapp
			String taglibLocation = readTextofChild(taglibs.item(i), "taglib-location").trim(); //$NON-NLS-1$
			IPath path = null;
			if (taglibLocation.startsWith("/")) { //$NON-NLS-1$
				path = new Path(getLocalRoot(webxml.getFullPath().toString()) + taglibLocation);
			}
			else {
				path = new Path(URIHelper.normalize(taglibLocation, webxml.getFullPath().toString(), getLocalRoot(webxml.getLocation().toString())));
			}
			if (path.segmentCount() > 1) {
				IFile tldResource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (tldResource.isAccessible()) {
					ITLDRecord record = createTLDRecord(tldResource);
					webxmlRecord.tldRecords.add(record);
					getImplicitReferences(webxml.getFullPath().toString()).put(taglibUri, record);
					if (_debugIndexCreation)
						Logger.log(Logger.INFO_DEBUG, "created web.xml record for " + taglibUri + "@" + record.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
					TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, deltaKind));
				}
			}
		}
	}
}
