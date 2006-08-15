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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.util.DocumentProvider;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.w3c.dom.Document;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.icu.util.StringTokenizer;

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
		boolean isMappedInWebXML;

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

		public int getRecordType() {
			return ITaglibRecord.JAR;
		}

		/**
		 * @return Returns the recommended/default prefix if one was given.
		 */
		public String getShortName() {
			if (info == null)
				return null;
			return info.shortName;
		}

		/**
		 * @return Returns the uri.
		 */
		public String getURI() {
			if (info == null)
				return null;
			return info.uri;
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

	/**
	 * A brief representation of the information in a TLD.
	 */
	static class TaglibInfo {
		public TaglibInfo() {
			super();
		}

		// extract only when asked?
		String description;
		float jspVersion;
		String largeIcon;
		String shortName;
		String smallIcon;
		String tlibVersion;
		String uri;

		public String toString() {
			return "TaglibInfo|" + shortName + "|" + tlibVersion + "|" + smallIcon + "|" + largeIcon + "|" + jspVersion + "|" + uri + "|" + description;
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
					string = " ADDED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case ITaglibRecordEvent.CHANGED :
					string = " CHANGED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case ITaglibRecordEvent.REMOVED :
					string = " REMOVED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					break;
				default :
					string = " other:" + fType + " (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

		public int getRecordType() {
			return ITaglibRecord.TLD;
		}

		public String getShortName() {
			if (info == null)
				return null;
			return info.shortName;
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
			return "TLDRecord: " + getURI() + " <-> " + path; //$NON-NLS-1$ //$NON-NLS-2$
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

		public int getRecordType() {
			return ITaglibRecord.URL;
		}

		/**
		 * @return Returns the recommended/default prefix if one was given.
		 */
		public String getShortName() {
			if (info == null)
				return null;
			return info.shortName;
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
	private static final String SAVE_FORMAT_VERSION = "1.0";

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
	private String fSaveStateFilename;

	ProjectDescription(IProject project, String saveStateFile) {
		super();
		fProject = project;
		fSaveStateFilename = saveStateFile;

		fClasspathJars = new Hashtable(0);
		fJARReferences = new Hashtable(0);
		fTagDirReferences = new Hashtable(0);
		fTLDReferences = new Hashtable(0);
		fWebXMLReferences = new Hashtable(0);
		fImplicitReferences = new Hashtable(0);
		fClasspathReferences = new Hashtable(0);

		restoreReferences();
	}

	private Collection _getJSP11AndWebXMLJarReferences(Collection allJARs) {
		List collection = new ArrayList(allJARs.size());
		Iterator i = allJARs.iterator();
		while (i.hasNext()) {
			JarRecord record = (JarRecord) i.next();
			if (record.has11TLD || record.isMappedInWebXML) {
				collection.add(record);
			}
		}
		return collection;
	}

	/**
	 * Erases all known tables
	 */
	void clear() {
		fClasspathJars = new Hashtable(0);
		fJARReferences = new Hashtable(0);
		fTagDirReferences = new Hashtable(0);
		fTLDReferences = new Hashtable(0);
		fWebXMLReferences = new Hashtable(0);
		fImplicitReferences = new Hashtable(0);
		fClasspathReferences = new Hashtable(0);
	}

	private ITaglibRecord createCatalogRecord(String urlString) {
		ITaglibRecord record = null;
		if (urlString.toLowerCase(Locale.US).endsWith((".jar")) && urlString.startsWith("file:")) { //$NON-NLS-1$ //$NON-NLS-2$
			String fileLocation = null;
			try {
				URL url = new URL(urlString);
				fileLocation = url.getFile();
			}
			catch (MalformedURLException e) {
				// not worth reporting
			}
			if (fileLocation != null) {
				JarRecord jarRecord = createJARRecord(fileLocation);
				String[] entries = JarUtilities.getEntryNames(fileLocation);
				for (int jEntry = 0; jEntry < entries.length; jEntry++) {
					if (entries[jEntry].endsWith(".tld")) { //$NON-NLS-1$
						if (entries[jEntry].equals(JarUtilities.JSP11_TAGLIB)) {
							jarRecord.has11TLD = true;
							InputStream contents = JarUtilities.getInputStream(fileLocation, entries[jEntry]);
							if (contents != null) {
								TaglibInfo info = extractInfo(fileLocation, contents);
								jarRecord.info = info;
							}
							try {
								contents.close();
							}
							catch (IOException e) {
							}
						}
					}
				}
				if (jarRecord.has11TLD) {
					if (_debugIndexCreation)
						Logger.log(Logger.INFO, "created catalog record for " + urlString + "@" + jarRecord.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$
					record = jarRecord;
				}

			}
		}
		else {
			URL url = null;
			ByteArrayInputStream cachedContents = null;
			InputStream tldStream = null;
			try {
				url = new URL(urlString);
				URLConnection connection = url.openConnection();
				connection.setDefaultUseCaches(false);
				tldStream = connection.getInputStream();
			}
			catch (Exception e1) {
				Logger.logException(e1);
			}

			int c;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			// array dim restriction?
			byte bytes[] = new byte[2048];
			try {
				while ((c = tldStream.read(bytes)) >= 0) {
					buffer.write(bytes, 0, c);
				}
				cachedContents = new ByteArrayInputStream(buffer.toByteArray());
			}
			catch (IOException ioe) {
				// no cleanup can be done
			}
			finally {
				try {
					tldStream.close();
				}
				catch (IOException e) {
				}
			}

			URLRecord urlRecord = null;
			TaglibInfo info = extractInfo(urlString, cachedContents);
			if (info != null) {
				urlRecord = new URLRecord();
				urlRecord.info = info;
				urlRecord.baseLocation = urlString;
				urlRecord.url = url; //$NON-NLS-1$ //$NON-NLS-2$
			}
			try {
				cachedContents.close();
			}
			catch (IOException e) {
			}
			record = urlRecord;
		}
		return record;
	}

	/**
	 * @param resource
	 * @return
	 */
	private JarRecord createJARRecord(IResource jar) {
		return createJARRecord(jar.getLocation().toString());
	}

	private JarRecord createJARRecord(String fileLocation) {
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
				String basePath = tld.getFullPath().toString();
				TaglibInfo info = extractInfo(basePath, contents);
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

	private TaglibInfo extractInfo(String basePath, InputStream tldContents) {
		TaglibInfo info = new TaglibInfo();
		if (tldContents != null) {
			DocumentProvider provider = new DocumentProvider();
			provider.setInputStream(tldContents);
			provider.setValidating(false);
			provider.setRootElementName(JSP12TLDNames.TAGLIB);
			provider.setBaseReference(basePath);
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

	synchronized List getAvailableTaglibRecords(IPath path) {
		Collection implicitReferences = getImplicitReferences(path.toString()).values();
		List records = new ArrayList(fTLDReferences.size() + fTagDirReferences.size() + fJARReferences.size() + fWebXMLReferences.size());
		records.addAll(fTLDReferences.values());
		records.addAll(fTagDirReferences.values());
		records.addAll(_getJSP11AndWebXMLJarReferences(fJARReferences.values()));
		records.addAll(fClasspathReferences.values());
		records.addAll(implicitReferences);

		ICatalog catalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		if (catalog != null) {
			ICatalogEntry[] entries = catalog.getCatalogEntries();
			for (int i = 0; i < entries.length; i++) {
				ITaglibRecord record = createCatalogRecord(entries[i].getURI());
				records.add(record);
			}
		}

		return records;
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
		IResource file = FileBuffers.getWorkspaceFileAtLocation(basePath);

		// Try the base path as a folder first
		if (file == null && basePath.segmentCount() > 1) {
			file = workspaceRoot.getFolder(basePath);
		}
		// If not a folder, then try base path as a file
		if (file != null && !file.exists() && basePath.segmentCount() > 1) {
			file = workspaceRoot.getFile(basePath);
		}

		if (file == null && basePath.segmentCount() == 1) {
			file = workspaceRoot.getProject(basePath.segment(0));
		}

		if (file == null) {
			/*
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=116529
			 * 
			 * This method produces a less accurate result, but doesn't
			 * require that the file exist yet.
			 */
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(basePath);
			if (files.length > 0)
				file = files[0];
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

	void handleElementChanged(IJavaElementDelta delta) {
		// Logger.log(Logger.INFO_DEBUG, "IJavaElementDelta: " + delta);
		IJavaElement element = delta.getElement();
		if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT && ((IPackageFragmentRoot) element).isArchive()) {
			time0 = System.currentTimeMillis();
			String libPath = null;
			int taglibRecordEventKind = -1;
			if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) > 0) {
				taglibRecordEventKind = ITaglibRecordEvent.ADDED;
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(element.getPath());
				if (file.exists())
					libPath = file.getLocation().toString();
				else
					libPath = element.getPath().toString();
			}
			else if ((delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0) {
				taglibRecordEventKind = ITaglibRecordEvent.REMOVED;
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(element.getPath());
				if (file.getLocation() != null)
					libPath = file.getLocation().toString();
				else
					libPath = element.getPath().toString();
			}
			else if ((delta.getFlags() & IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED) > 0) {
				taglibRecordEventKind = ITaglibRecordEvent.CHANGED;
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(element.getPath());
				if (file.exists())
					libPath = file.getLocation().toString();
				else
					libPath = element.getPath().toString();
			}
			if (libPath != null) {
				updateClasspathLibrary(libPath, taglibRecordEventKind);
			}
			if (_debugIndexTime)
				Logger.log(Logger.INFO, "processed build path delta for " + fProject.getName() + "(" + element.getPath() + ") in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
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
			Logger.log(Logger.INFO, "indexed " + fProject.getName() + " contents in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	void indexClasspath() {
		time0 = System.currentTimeMillis();
		fClasspathProjects = new Stack();

		fClasspathReferences.clear();
		fClasspathJars.clear();

		IJavaProject javaProject = JavaCore.create(fProject);
		indexClasspath(javaProject);
		if (_debugIndexTime)
			Logger.log(Logger.INFO, "indexed " + fProject.getName() + " classpath in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
							updateClasspathLibrary(libPath.toString(), ITaglibRecordEvent.ADDED);
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
								updateClasspathLibrary(libFile.getLocation().toString(), ITaglibRecordEvent.ADDED);
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

	void removeJAR(IResource jar) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "removing records for JAR " + jar.getFullPath()); //$NON-NLS-1$
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
			Logger.log(Logger.INFO, "removing record for " + tld.getFullPath()); //$NON-NLS-1$
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
			Logger.log(Logger.INFO, "removing records for " + webxml.getFullPath()); //$NON-NLS-1$
		WebXMLRecord record = (WebXMLRecord) fWebXMLReferences.remove(webxml.getLocation().toString());
		if (record != null) {
			TLDRecord[] records = (TLDRecord[]) record.getTLDRecords().toArray(new TLDRecord[0]);
			for (int i = 0; i < records.length; i++) {
				if (_debugIndexCreation)
					Logger.log(Logger.INFO, "removed record for " + records[i].getURI() + "@" + records[i].path); //$NON-NLS-1$ //$NON-NLS-2$
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

		// Check the XML Catalog
		if (record == null) {
			ICatalog catalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
			if (catalog != null) {
				String resolvedString = null;
				try {
					// Check as system reference first
					resolvedString = catalog.resolveSystem(reference);
					// Check as URI
					if (resolvedString == null || resolvedString.trim().length() == 0) {
						resolvedString = catalog.resolveURI(reference);
					}
					// Check as public ID
					if (resolvedString == null || resolvedString.trim().length() == 0) {
						resolvedString = catalog.resolvePublic(reference, basePath);
					}
				}
				catch (Exception e) {
					Logger.logException(e);
				}
				if (resolvedString != null && resolvedString.trim().length() > 0) {
					record = createCatalogRecord(resolvedString);
				}
			}
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

	/**
	 * Restores any saved reference tables
	 */
	private void restoreReferences() {
		if (TaglibIndex.ENABLED) {
			index();

			// ================ test reload time ========================
			boolean restored = false;
			File savedState = new File(fSaveStateFilename);
			if (savedState.exists()) {
				ITextFileBufferManager textFileBufferManager = FileBuffers.getTextFileBufferManager();
				Path savedStatePath = new Path(fSaveStateFilename);
				try {
					time0 = System.currentTimeMillis();
					textFileBufferManager.connect(savedStatePath, new NullProgressMonitor());
					ITextFileBuffer buffer = textFileBufferManager.getTextFileBuffer(savedStatePath);
					IDocument doc = buffer.getDocument();
					int lines = doc.getNumberOfLines();
					if (lines > 0) {
						IRegion line = doc.getLineInformation(0);
						String lineText = doc.get(line.getOffset(), line.getLength());
						JarRecord libraryRecord = null;
						if (SAVE_FORMAT_VERSION.equals(lineText)) {
							for (int i = 1; i < lines; i++) {
								line = doc.getLineInformation(i);
								lineText = doc.get(line.getOffset(), line.getLength());
								StringTokenizer toker = new StringTokenizer(lineText, "|");
								if (toker.hasMoreTokens()) {
									String referenceType = toker.nextToken();
									if ("JAR".equalsIgnoreCase(referenceType)) {
										boolean has11TLD = Boolean.valueOf(toker.nextToken()).booleanValue();
										// make the rest the libraryLocation
										String libraryLocation = toker.nextToken();
										while (toker.hasMoreTokens()) {
											libraryLocation = libraryLocation + "|" + toker.nextToken();
										}
										libraryLocation = libraryLocation.trim();
										if (libraryRecord != null) {
											TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(libraryRecord, ITaglibRecordEvent.ADDED));
										}
										// Create a new JarRecord
										libraryRecord = createJARRecord(libraryLocation);
										libraryRecord.has11TLD = has11TLD;

										// Add a URLRecord for the 1.1 TLD
										if (has11TLD) {
											InputStream contents = JarUtilities.getInputStream(libraryLocation, JarUtilities.JSP11_TAGLIB);
											if (contents != null) {
												TaglibInfo info = extractInfo(libraryLocation, contents);

												if (info != null && info.uri != null && info.uri.length() > 0) {
													URLRecord record = new URLRecord();
													record.info = info;
													record.baseLocation = libraryLocation;
													try {
														record.url = new URL("jar:file:" + libraryLocation + "!/" + JarUtilities.JSP11_TAGLIB); //$NON-NLS-1$ //$NON-NLS-2$
														libraryRecord.urlRecords.add(record);
														fClasspathReferences.put(record.getURI(), record);
														if (_debugIndexCreation)
															Logger.log(Logger.INFO, "created record for " + record.getURI() + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
													}
													catch (MalformedURLException e) {
														// don't record this
														// URI
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

										fClasspathJars.put(libraryLocation, libraryRecord);
									}
									else if ("URL".equalsIgnoreCase(referenceType)) {
										// make the rest the URL
										String urlString = toker.nextToken();
										while (toker.hasMoreTokens()) {
											urlString = urlString + "|" + toker.nextToken();
										}
										urlString = urlString.trim();
										// Append a URLrecord
										URLRecord urlRecord = new URLRecord();
										urlRecord.url = new URL(urlString); //$NON-NLS-1$ //$NON-NLS-2$
										urlRecord.baseLocation = libraryRecord.location.toString();
										libraryRecord.urlRecords.add(urlRecord);

										ByteArrayInputStream cachedContents = null;
										InputStream tldStream = null;
										try {
											URLConnection connection = urlRecord.url.openConnection();
											connection.setDefaultUseCaches(false);
											tldStream = connection.getInputStream();
										}
										catch (IOException e1) {
											Logger.logException(e1);
										}

										int c;
										ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
										// array dim restriction?
										byte bytes[] = new byte[2048];
										try {
											while ((c = tldStream.read(bytes)) >= 0) {
												byteArrayOutput.write(bytes, 0, c);
											}
											cachedContents = new ByteArrayInputStream(byteArrayOutput.toByteArray());
										}
										catch (IOException ioe) {
											// no cleanup can be done
										}
										finally {
											try {
												tldStream.close();
											}
											catch (IOException e) {
											}
										}

										TaglibInfo info = extractInfo(urlRecord.url.toString(), cachedContents);
										if (info != null) {
											urlRecord.info = info;
										}
										try {
											cachedContents.close();
										}
										catch (IOException e) {
										}
										fClasspathReferences.put(urlRecord.getURI(), urlRecord);
									}
								}
							}
							if (libraryRecord != null) {
								TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(libraryRecord, ITaglibRecordEvent.ADDED));
							}
						}
					}
					restored = true;
					if (_debugIndexTime)
						Logger.log(Logger.INFO, "time spent reloading " + fProject.getName() + " build path: " + (System.currentTimeMillis() - time0));
				}
				catch (Exception e) {
					restored = false;
					if (_debugIndexTime)
						Logger.log(Logger.INFO, "failure reloading " + fProject.getName() + " build path index", e);
				}
				finally {
					try {
						textFileBufferManager.disconnect(savedStatePath, new NullProgressMonitor());
					}
					catch (CoreException e) {
						Logger.logException(e);
					}
				}
			}

			// ================ test reload time (end) ==================


			if (!restored) {
				indexClasspath();
			}
		}
	}

	/**
	 * Saves any storable references to disk. This is only called when the
	 * description is being cleared and not after every update.
	 */
	void saveReferences() {
		time0 = System.currentTimeMillis();
		Writer writer = null;

		/**
		 * <pre>
		 *                                 1.0
		 *                                 Save classpath information (! is field delimiter)
		 *                                 Jars are saved as &quot;JAR:&quot;+ has11TLD + jar path 
		 *                                 URLRecords as &quot;URL:&quot;+URL
		 * </pre>
		 */
		try {
			writer = new OutputStreamWriter(new FileOutputStream(fSaveStateFilename), "utf8");
			writer.write(SAVE_FORMAT_VERSION);
			writer.write('\n');

			Enumeration jars = fClasspathJars.keys();
			while (jars.hasMoreElements()) {
				String jarPath = jars.nextElement().toString();
				JarRecord jarRecord = (JarRecord) fClasspathJars.get(jarPath);
				writer.write("JAR|");
				writer.write(Boolean.toString(jarRecord.has11TLD));
				writer.write('|');
				writer.write(jarPath);
				writer.write('\n');
				Iterator i = jarRecord.urlRecords.iterator();
				while (i.hasNext()) {
					IURLRecord urlRecord = (IURLRecord) i.next();
					writer.write("URL|");
					writer.write(urlRecord.getURL().toExternalForm());
					writer.write('\n');
				}
			}
		}
		catch (IOException e) {
		}
		finally {
			try {
				if (writer != null) {
					writer.close();
				}
			}
			catch (Exception e) {
			}
		}

		if (_debugIndexTime)
			Logger.log(Logger.INFO, "time spent saving index for " + fProject.getName() + ": " + (System.currentTimeMillis() - time0));
	}

	void updateClasspathLibrary(String libraryLocation, int deltaKind) {
		JarRecord libraryRecord = null;
		if (deltaKind == ITaglibRecordEvent.REMOVED || deltaKind == ITaglibRecordEvent.CHANGED) {
			libraryRecord = (JarRecord) fClasspathJars.remove(libraryLocation);
			if (libraryRecord != null) {
				IURLRecord[] urlRecords = (IURLRecord[]) libraryRecord.urlRecords.toArray(new IURLRecord[0]);
				for (int i = 0; i < urlRecords.length; i++) {
					fClasspathReferences.remove(urlRecords[i].getURI());
				}
			}
		}
		if (deltaKind == ITaglibRecordEvent.ADDED || deltaKind == ITaglibRecordEvent.CHANGED) {
			String[] entries = JarUtilities.getEntryNames(libraryLocation);

			libraryRecord = createJARRecord(libraryLocation);
			fClasspathJars.put(libraryLocation, libraryRecord);
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].endsWith(".tld")) { //$NON-NLS-1$
					if (entries[i].equals(JarUtilities.JSP11_TAGLIB)) {
						libraryRecord.has11TLD = true;
					}
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
									Logger.log(Logger.INFO, "created record for " + record.getURI() + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
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
		}
		if (libraryRecord != null) {
			TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(libraryRecord, deltaKind));
		}
	}

	void updateJAR(IResource jar, int deltaKind) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "creating records for JAR " + jar.getFullPath()); //$NON-NLS-1$

		String jarLocationString = jar.getLocation().toString();
		String[] entries = JarUtilities.getEntryNames(jar);
		JarRecord jarRecord = createJARRecord(jar);
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
								Logger.log(Logger.INFO, "created record for " + record.getURI() + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
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
					Logger.log(Logger.ERROR_DEBUG, getClass().getName() + "could not read resource " + jar.getFullPath()); //$NON-NLS-1$
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
			Logger.log(Logger.INFO, "creating record for " + tld.getFullPath()); //$NON-NLS-1$
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
			provider.setRootElementName("web-app"); //$NON-NLS-1$
			provider.setBaseReference(webxml.getParent().getFullPath().toString());
			document = provider.getDocument(false);
		}
		catch (CoreException e) {
			Logger.log(Logger.ERROR_DEBUG, "", e); //$NON-NLS-1$
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
			Logger.log(Logger.INFO, "creating records for " + webxml.getFullPath()); //$NON-NLS-1$

		WebXMLRecord webxmlRecord = new WebXMLRecord();
		webxmlRecord.path = webxml.getFullPath();
		fWebXMLReferences.put(webxmlRecord.getWebXML().toString(), webxmlRecord);
		NodeList taglibs = document.getElementsByTagName(JSP12TLDNames.TAGLIB);
		for (int iTaglib = 0; iTaglib < taglibs.getLength(); iTaglib++) {
			String taglibUri = readTextofChild(taglibs.item(iTaglib), "taglib-uri").trim(); //$NON-NLS-1$
			// specified location is relative to root of the webapp
			String taglibLocation = readTextofChild(taglibs.item(iTaglib), "taglib-location").trim(); //$NON-NLS-1$
			IPath path = null;
			if (taglibLocation.startsWith("/")) { //$NON-NLS-1$
				path = new Path(getLocalRoot(webxml.getFullPath().toString()) + taglibLocation);
			}
			else {
				path = new Path(URIHelper.normalize(taglibLocation, webxml.getFullPath().toString(), getLocalRoot(webxml.getFullPath().toString())));
			}
			if (path.segmentCount() > 1) {
				IFile resource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (resource.isAccessible()) {
					ITaglibRecord record = null;
					/*
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=125960
					 * 
					 * Also support mappings to .jar files
					 */
					if (resource.getFileExtension().equalsIgnoreCase(("jar"))) { //$NON-NLS-1$
						JarRecord jarRecord = createJARRecord(resource);
						String[] entries = JarUtilities.getEntryNames(resource);
						for (int jEntry = 0; jEntry < entries.length; jEntry++) {
							if (entries[jEntry].endsWith(".tld")) { //$NON-NLS-1$
								if (entries[jEntry].equals(JarUtilities.JSP11_TAGLIB)) {
									jarRecord.has11TLD = true;
									InputStream contents = JarUtilities.getInputStream(resource, entries[jEntry]);
									if (contents != null) {
										TaglibInfo info = extractInfo(resource.getFullPath().toString(), contents);
										jarRecord.info = info;
									}
									try {
										contents.close();
									}
									catch (IOException e) {
									}
								}
							}
						}
						record = jarRecord;
						// the stored URI should reflect the web.xml's value
						jarRecord.info.uri = taglibUri;
						jarRecord.isMappedInWebXML = true;
						if (_debugIndexCreation)
							Logger.log(Logger.INFO, "created web.xml record for " + taglibUri + "@" + jarRecord.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else {
						TLDRecord tldRecord = createTLDRecord(resource);
						record = tldRecord;
						// the stored URI should reflect the web.xml's value
						tldRecord.info.uri = taglibUri;
						if (_debugIndexCreation)
							Logger.log(Logger.INFO, "created web.xml record for " + taglibUri + "@" + tldRecord.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					webxmlRecord.tldRecords.add(record);
					getImplicitReferences(webxml.getFullPath().toString()).put(taglibUri, record);
					TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record, deltaKind));
				}
			}
		}
	}
}
