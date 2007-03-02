/*******************************************************************************
 * Copyright (c) 2005,2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.taglib;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

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
import org.eclipse.wst.jsdt.core.IClasspathContainer;
import org.eclipse.wst.jsdt.core.IClasspathEntry;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IJavaElementDelta;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.IPackageFragmentRoot;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.wst.jsdt.web.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.wst.jsdt.web.core.internal.util.DocumentProvider;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
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
					} else {
						updateTLD(resource, delta.getKind());
					}
				} else if (resource.getName().endsWith(".jar")) { //$NON-NLS-1$
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeJAR(resource);
					} else {
						updateJAR(resource, delta.getKind());
					}
				} else if (resource.getName().endsWith(".tag") || resource.getName().endsWith(".tagx")) { //$NON-NLS-1$ //$NON-NLS-2$
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeTagDir(resource);
					} else {
						updateTagDir(resource, delta.getKind());
					}
				} else if (resource.getName().equals(WEB_XML)
						&& resource.getParent().getName().equals(WEB_INF)) {
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeWebXML(resource);
					} else {
						updateWebXML(resource, delta.getKind());
					}
				}
			}
			return resource.getName().length() != 0
					&& resource.getName().charAt(0) != '.';
		}
	}

	class Indexer implements IResourceProxyVisitor {
		public boolean visit(IResourceProxy proxy) throws CoreException {
			if (proxy.getType() == IResource.FILE) {
				if (proxy.getName().endsWith(".tld")) { //$NON-NLS-1$
					updateTLD(proxy.requestResource(), ITaglibRecordEvent.ADDED);
				} else if (proxy.getName().endsWith(".jar")) { //$NON-NLS-1$
					updateJAR(proxy.requestResource(), ITaglibRecordEvent.ADDED);
				} else if (proxy.getName().endsWith(".tag") || proxy.getName().endsWith(".tagx")) { //$NON-NLS-1$ //$NON-NLS-2$
					updateTagDir(proxy.requestResource(),
							ITaglibRecordEvent.ADDED);
				} else if (proxy.getName().equals(WEB_XML)
						&& proxy.requestResource().getParent().getName()
								.equals(WEB_INF)) {
					updateWebXML(proxy.requestResource(),
							ITaglibRecordEvent.ADDED);
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
		boolean isExported = true;

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof JarRecord)) {
				return false;
			}
			return ((JarRecord) obj).location.equals(location)
					&& ((JarRecord) obj).has11TLD == has11TLD
					&& ((JarRecord) obj).info.equals(info);
		}

		public ITaglibDescriptor getDescriptor() {
			return info != null ? info : new TaglibInfo();
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
			if (info == null) {
				return null;
			}
			return info.shortName;
		}

		/**
		 * @return Returns the uri.
		 */
		public String getURI() {
			if (info == null) {
				return null;
			}
			return info.uri;
		}

		/**
		 * 
		 */
		public List getURLRecords() {
			return urlRecords;
		}

		@Override
		public String toString() {
			return "JarRecord: " + location + " <-> " + urlRecords; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class TagDirRecord implements ITagDirRecord {
		IPath location;
		String shortName;
		TaglibInfo info;
		// a List holding Strings of .tag and .tagx filenames relative to the
		// tagdir's location
		List tags = new ArrayList(0);

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TagDirRecord)) {
				return false;
			}
			return ((TagDirRecord) obj).location.equals(location)
					&& ((TagDirRecord) obj).info.equals(info);
		}

		public ITaglibDescriptor getDescriptor() {
			return info != null ? info : new TaglibInfo();
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

		@Override
		public String toString() {
			return "TagdirRecord: " + location + " <-> " + shortName; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * A brief representation of the information in a TLD.
	 */
	static class TaglibInfo implements ITaglibDescriptor {
		// extract only when asked?
		String description = "";
		String jspVersion = "";
		String largeIcon = "";
		String displayName = "";
		String shortName = "";
		String smallIcon = "";
		String tlibVersion = "";
		String uri = "";

		public TaglibInfo() {
			super();
		}

		@Override
		public String toString() {
			return "TaglibInfo|" + shortName + "|" + tlibVersion + "|" + smallIcon + "|" + largeIcon + "|" + jspVersion + "|" + uri + "|" + description; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		}

		public String getDescription() {
			return description;
		}

		public String getJSPVersion() {
			return jspVersion;
		}

		public String getLargeIcon() {
			return largeIcon;
		}

		public String getShortName() {
			return shortName;
		}

		public String getSmallIcon() {
			return smallIcon;
		}

		public String getTlibVersion() {
			return tlibVersion;
		}

		public String getURI() {
			return uri;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TaglibInfo)) {
				return false;
			}
			return ((TaglibInfo) obj).jspVersion == jspVersion
					&& ((TaglibInfo) obj).description.equals(description)
					&& ((TaglibInfo) obj).largeIcon.equals(largeIcon)
					&& ((TaglibInfo) obj).shortName.equals(shortName)
					&& ((TaglibInfo) obj).smallIcon.equals(smallIcon)
					&& ((TaglibInfo) obj).tlibVersion.equals(tlibVersion)
					&& ((TaglibInfo) obj).uri.equals(uri);
		}

		public String getDisplayName() {
			return displayName;
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

		@Override
		public String toString() {
			String string = fTaglibRecord.toString();
			switch (fType) {
			case ITaglibRecordEvent.ADDED:
				string = " ADDED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case ITaglibRecordEvent.CHANGED:
				string = " CHANGED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case ITaglibRecordEvent.REMOVED:
				string = " REMOVED (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				break;
			default:
				string = " other:" + fType + " (" + string + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			}
			return string;
		}
	}

	static class TLDRecord implements ITLDRecord {
		TaglibInfo info;
		IPath path;

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TLDRecord)) {
				return false;
			}
			return ((TLDRecord) obj).path.equals(path)
					&& ((TLDRecord) obj).getURI().equals(getURI())
					&& ((TLDRecord) obj).info.equals(info);
		}

		public ITaglibDescriptor getDescriptor() {
			return info != null ? info : new TaglibInfo();
		}

		public IPath getPath() {
			return path;
		}

		public int getRecordType() {
			return ITaglibRecord.TLD;
		}

		public String getShortName() {
			if (info == null) {
				return null;
			}
			return info.shortName;
		}

		/**
		 * @return Returns the uri.
		 */
		public String getURI() {
			if (info == null) {
				return null;
			}
			return info.uri;
		}

		@Override
		public String toString() {
			return "TLDRecord: " + getURI() + " <-> " + path; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class URLRecord implements IURLRecord {
		String baseLocation;
		TaglibInfo info;
		URL url;
		boolean isExported = true;

		public URLRecord() {
			super();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof URLRecord)) {
				return false;
			}
			return ((URLRecord) obj).baseLocation.equals(baseLocation)
					&& ((URLRecord) obj).url.equals(url)
					&& ((URLRecord) obj).info.equals(info);
		}

		public ITaglibDescriptor getDescriptor() {
			return info != null ? info : new TaglibInfo();
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
			if (info == null) {
				return null;
			}
			return info.shortName;
		}

		/**
		 * @return Returns the uri.
		 */
		public String getURI() {
			if (info == null) {
				return ""; //$NON-NLS-1$
			}
			return info.uri;
		}

		/**
		 * @return Returns the URL.
		 */
		public URL getURL() {
			return url;
		}

		@Override
		public String toString() {
			return "URLRecord: " + baseLocation + " <-> " + getURI(); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class WebXMLRecord {
		TaglibInfo info;
		IPath path;
		List tldRecords = new ArrayList(0);

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof WebXMLRecord)) {
				return false;
			}
			return ((WebXMLRecord) obj).path.equals(path)
					&& ((WebXMLRecord) obj).info.equals(info);
		}

		/**
		 * @return Returns the recommended/default prefix if one was given.
		 */
		public String getPrefix() {
			if (info == null) {
				return null;
			}
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

		@Override
		public String toString() {
			return "WebXMLRecord: " + path + " " + tldRecords; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/taglib/indexcreation")); //$NON-NLS-1$ //$NON-NLS-2$
	static boolean _debugIndexTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/taglib/indextime")); //$NON-NLS-1$ //$NON-NLS-2$

	private static final String WEB_INF = "WEB-INF"; //$NON-NLS-1$
	private static final IPath WEB_INF_PATH = new Path(WEB_INF);
	private static final String BUILDPATH_PROJECT = "BUILDPATH_PROJECT"; //$NON-NLS-1$
	private static final String WEB_XML = "web.xml"; //$NON-NLS-1$
	private static final String SAVE_FORMAT_VERSION = "Tag Library Index 1.0.2"; //$NON-NLS-1$
	private static final String BUILDPATH_DIRTY = "BUILDPATH_DIRTY"; //$NON-NLS-1$

	/*
	 * Records active JARs on the classpath. Taglib descriptors should be
	 * usable, but the jars by themselves are not.
	 */
	Hashtable fClasspathJars;

	/**
	 * Notes that the build path information is stale. Some operations can now
	 * be skipped until a resolve/getAvailable call is made.
	 */
	boolean fBuildPathIsDirty = false;

	/**
	 * A set of the projects that are in this project's build path.
	 * Lookups/enumerations will be redirected to the corresponding
	 * ProjectDescription instances
	 */
	Set fClasspathProjects = null;

	// holds references by URI to JARs
	Hashtable fClasspathReferences;

	/*
	 * this table is special in that it holds tables of references according to
	 * local roots
	 */
	Hashtable fImplicitReferences;

	Hashtable fJARReferences;

	IProject fProject;

	Hashtable fTagDirReferences;

	Hashtable fTLDReferences;

	IResourceDeltaVisitor fVisitor;

	Hashtable fWebXMLReferences;

	private long time0;
	private String fSaveStateFilename;

	/**
	 * A cached copy of all of the records createable from the XMLCatalog.
	 */
	private Collection fCatalogRecords;

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
		fClasspathProjects = new HashSet();

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
	 * Adds the list of known references from this project's build path to the
	 * map, appending any processed projects into the list to avoid
	 * build-path-cycles.
	 * 
	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=142408
	 * 
	 * @param references -
	 *            the map of references to ITaglibRecords
	 * @param projectsProcessed -
	 *            the list of projects already considered
	 * @param exportedOnly -
	 *            Whether to only add references derived from exported build
	 *            path containers. This method calls itself recursively with
	 *            this parameter as false.
	 */
	void addBuildPathReferences(Map references, List projectsProcessed,
			boolean exportedOnly) {
		ensureUpTodate();

		Enumeration keys = fClasspathReferences.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			URLRecord urlRecord = (URLRecord) fClasspathReferences.get(key);
			if (exportedOnly) {
				if (urlRecord.isExported) {
					references.put(key, urlRecord);
				}
			} else {
				references.put(key, urlRecord);
			}
		}
		IProject[] buildpathProjects = (IProject[]) fClasspathProjects
				.toArray(new IProject[fClasspathProjects.size()]);
		for (int i = 0; i < buildpathProjects.length; i++) {
			if (!projectsProcessed.contains(buildpathProjects[i])
					&& buildpathProjects[i].isAccessible()) {
				projectsProcessed.add(buildpathProjects[i]);
				ProjectDescription description = createDescription(buildpathProjects[i]);
				description.addBuildPathReferences(references,
						projectsProcessed, true);
			}
		}
	}
	ProjectDescription createDescription(IProject project) {
		ProjectDescription description = null;
		
		try{
		description = (ProjectDescription) project.getDescription();
		}catch(Exception ex){
			System.out.println("Exception thrown in ProjectDescript.createDescription()");
		}
		
		if (description == null) {
			// Once we've started indexing, we're dirty again
			
			description = new ProjectDescription(project, project.getFullPath().toString() + "savestate.dat");
			
		}
		return description;
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

	private void closeJarFile(ZipFile file) {
		if (file == null) {
			return;
		}
		try {
			file.close();
		} catch (IOException ioe) {
			// no cleanup can be done
			Logger
					.logException(
							"TaglibIndex: Could not close zip file " + file.getName(), ioe); //$NON-NLS-1$
		}
	}

	/**
	 * @param catalogEntry
	 *            a XML catalog entry pointing to a .jar or .tld file
	 * @return a ITaglibRecord describing a TLD contributed to the XMLCatalog if
	 *         one was found at the given location, null otherwise
	 */
	private ITaglibRecord createCatalogRecord(ICatalogEntry catalogEntry) {
		return createCatalogRecord(catalogEntry.getKey(), catalogEntry.getURI());
	}

	/**
	 * @param uri -
	 *            the key value that will become the returned record's "URI"
	 * @param urlString -
	 *            the string indicating where the TLD really is
	 * @return a ITaglibRecord describing a TLD contributed to the XMLCatalog if
	 *         one was found at the given location, null otherwise
	 */
	private ITaglibRecord createCatalogRecord(String uri, String urlString) {
		ITaglibRecord record = null;
		// handle "file:" URLs that point to a .jar file on disk (1.1 mode)
		if (urlString.toLowerCase(Locale.US).endsWith((".jar")) && urlString.startsWith("file:")) { //$NON-NLS-1$ //$NON-NLS-2$
			String fileLocation = null;
			try {
				URL url = new URL(urlString);
				fileLocation = url.getFile();
			} catch (MalformedURLException e) {
				// not worth reporting
			}
			if (fileLocation != null) {
				JarRecord jarRecord = createJARRecord(fileLocation);
				String[] entries = JarUtilities.getEntryNames(fileLocation);
				for (int jEntry = 0; jEntry < entries.length; jEntry++) {
					if (entries[jEntry].endsWith(".tld")) { //$NON-NLS-1$
						if (entries[jEntry].equals(JarUtilities.JSP11_TAGLIB)) {
							jarRecord.has11TLD = true;
							InputStream contents = JarUtilities.getInputStream(
									fileLocation, entries[jEntry]);
							if (contents != null) {
								TaglibInfo info = extractInfo(fileLocation,
										contents);
								/*
								 * the record's reported URI should match the
								 * catalog entry's "key" so replace the detected
								 * value
								 */
								info.uri = uri;
								jarRecord.info = info;
							}
							try {
								contents.close();
							} catch (IOException e) {
							}
						}
					}
				}
				if (jarRecord.has11TLD) {
					if (_debugIndexCreation) {
						Logger
								.log(
										Logger.INFO,
										"created catalog record for " + urlString + "@" + jarRecord.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					record = jarRecord;
				}

			}
		}
		// The rest are URLs into a plug-in...somewhere
		else {
			URL url = null;
			ByteArrayInputStream cachedContents = null;
			InputStream tldStream = null;
			try {
				url = new URL(urlString);
				URLConnection connection = url.openConnection();
				connection.setDefaultUseCaches(false);
				tldStream = connection.getInputStream();
			} catch (Exception e1) {
				Logger.logException(
						"Exception reading TLD contributed to the XML Catalog",
						e1);
			}

			if (tldStream != null) {
				int c;
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				// array dim restriction?
				byte bytes[] = new byte[2048];
				try {
					while ((c = tldStream.read(bytes)) >= 0) {
						buffer.write(bytes, 0, c);
					}
					cachedContents = new ByteArrayInputStream(buffer
							.toByteArray());
				} catch (IOException ioe) {
					// no cleanup can be done
				} finally {
					try {
						tldStream.close();
					} catch (IOException e) {
					}
				}

				URLRecord urlRecord = null;
				TaglibInfo info = extractInfo(urlString, cachedContents);
				if (info != null) {
					/*
					 * the record's reported URI should match the catalog
					 * entry's "key" so replace the detected value
					 */
					info.uri = uri;
					urlRecord = new URLRecord();
					urlRecord.info = info;
					urlRecord.baseLocation = urlString;
					urlRecord.url = url;
				}
				try {
					cachedContents.close();
				} catch (IOException e) {
				}
				record = urlRecord;
			}
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
		record.info = new TaglibInfo();
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
		TagDirRecord record = (TagDirRecord) fTagDirReferences
				.get(tagdirLocation);
		if (record == null) {
			record = new TagDirRecord();
			record.location = tagdir.getFullPath();
			// JSP 2.0 section 8.4.3
			if (tagdir.getName().equals("tags")) {
				record.shortName = "tags"; //$NON-NLS-1$
			} else {
				IPath tagdirPath = tagdir.getFullPath();
				String[] segments = tagdirPath.segments();
				for (int i = 1; record.shortName == null && i < segments.length; i++) {
					if (segments[i - 1].equals("WEB-INF") && segments[i].equals("tags")) { //$NON-NLS-1$ //$NON-NLS-2$
						IPath tagdirLocalPath = tagdirPath
								.removeFirstSegments(i + 1);
						record.shortName = tagdirLocalPath.toString().replace(
								'/', '-');
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
		} catch (CoreException e) {
			Logger.logException(e);
		} finally {
			try {
				if (contents != null) {
					contents.close();
				}
			} catch (IOException e) {
				// ignore
			}
		}
		return record;
	}

	private void ensureUpTodate() {
		if (fBuildPathIsDirty) {
			indexClasspath();
			fBuildPathIsDirty = false;
		}
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
			if (child == null || child.getNodeType() != Node.ELEMENT_NODE
					|| !child.getNodeName().equals(JSP12TLDNames.TAGLIB)) {
				return null;
			}
			child = child.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if (child.getNodeName().equals(JSP12TLDNames.URI)) {
						info.uri = getTextContents(child);
					} else if (child.getNodeName().equals(
							JSP12TLDNames.SHORT_NAME)
							|| child.getNodeName().equals(
									JSP11TLDNames.SHORTNAME)) {
						info.shortName = getTextContents(child);
					} else if (child.getNodeName().equals(
							JSP12TLDNames.DESCRIPTION)
							|| child.getNodeName().equals(JSP11TLDNames.INFO)) {
						info.description = getTextContents(child);
					} else if (child.getNodeName().equals(
							JSP12TLDNames.DISPLAY_NAME)) {
						info.displayName = getTextContents(child);
					} else if (child.getNodeName().equals(
							JSP12TLDNames.JSP_VERSION)
							|| child.getNodeName().equals(
									JSP11TLDNames.JSPVERSION)) {
						info.jspVersion = getTextContents(child);
					} else if (child.getNodeName().equals(
							JSP12TLDNames.TLIB_VERSION)
							|| child.getNodeName().equals(
									JSP11TLDNames.TLIBVERSION)) {
						info.tlibVersion = getTextContents(child);
					} else if (child.getNodeName().equals(
							JSP12TLDNames.SMALL_ICON)) {
						info.smallIcon = getTextContents(child);
					} else if (child.getNodeName().equals(
							JSP12TLDNames.LARGE_ICON)) {
						info.largeIcon = getTextContents(child);
					}
				}
				child = child.getNextSibling();
			}
		}
		return info;
	}

	synchronized List getAvailableTaglibRecords(IPath path) {
		ensureUpTodate();

		Collection implicitReferences = new HashSet(getImplicitReferences(
				path.toString()).values());
		Collection records = new ArrayList(fTLDReferences.size()
				+ fTagDirReferences.size() + fJARReferences.size()
				+ fWebXMLReferences.size());
		records.addAll(fTLDReferences.values());
		records.addAll(fTagDirReferences.values());
		records
				.addAll(_getJSP11AndWebXMLJarReferences(fJARReferences.values()));
		records.addAll(implicitReferences);

		Map buildPathReferences = new HashMap();
		List projectsProcessed = new ArrayList(fClasspathProjects.size() + 1);
		projectsProcessed.add(fProject);
		addBuildPathReferences(buildPathReferences, projectsProcessed, false);
		records.addAll(buildPathReferences.values());

		records.addAll(getCatalogRecords());

		return new ArrayList(records);
	}

	private Collection getCatalogRecords() {
		if (fCatalogRecords == null) {
			List records = new ArrayList();
			ICatalog defaultCatalog = XMLCorePlugin.getDefault()
					.getDefaultXMLCatalog();
			if (defaultCatalog != null) {
				// Process default catalog
				ICatalogEntry[] entries = defaultCatalog.getCatalogEntries();
				for (int entry = 0; entry < entries.length; entry++) {
					ITaglibRecord record = createCatalogRecord(entries[entry]);
					records.add(record);
				}

				// Process declared OASIS nextCatalogs catalog
				INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
				for (int nextCatalog = 0; nextCatalog < nextCatalogs.length; nextCatalog++) {
					ICatalog catalog = nextCatalogs[nextCatalog]
							.getReferencedCatalog();
					ICatalogEntry[] entries2 = catalog.getCatalogEntries();
					for (int entry = 0; entry < entries2.length; entry++) {
						String uri = entries2[entry].getURI();
						if (uri != null) {
							uri = uri.toLowerCase(Locale.US);
							if (uri.endsWith((".jar"))
									|| uri.endsWith((".tld"))) {
								ITaglibRecord record = createCatalogRecord(entries2[entry]);
								if (record != null) {
									records.add(record);
								}
							}
						}
					}
				}
			}
			fCatalogRecords = records;
		}
		return fCatalogRecords;
	}

	/**
	 * Provides a stream to a local copy of the input or null if not possible
	 */
	private InputStream getCachedInputStream(ZipFile zipFile, ZipEntry zipEntry) {
		InputStream cache = null;
		if (zipFile != null) {
			if (zipEntry != null) {
				InputStream entryInputStream = null;
				try {
					entryInputStream = zipFile.getInputStream(zipEntry);
				} catch (IOException ioExc) {
					Logger.logException(
							"Taglib Index: " + zipFile.getName(), ioExc); //$NON-NLS-1$
				}

				if (entryInputStream != null) {
					int c;
					ByteArrayOutputStream buffer = null;
					if (zipEntry.getSize() > 0) {
						buffer = new ByteArrayOutputStream((int) zipEntry
								.getSize());
					} else {
						buffer = new ByteArrayOutputStream();
					}
					// array dim restriction?
					byte bytes[] = new byte[2048];
					try {
						while ((c = entryInputStream.read(bytes)) >= 0) {
							buffer.write(bytes, 0, c);
						}
						cache = new ByteArrayInputStream(buffer.toByteArray());
					} catch (IOException ioe) {
						// no cleanup can be done
					} finally {
						try {
							entryInputStream.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}

		return cache;
	}

	/**
	 * @return Returns the implicitReferences for the given path
	 */
	Hashtable getImplicitReferences(String path) {
		String localRoot = getLocalRoot(path);
		Hashtable implicitReferences = (Hashtable) fImplicitReferences
				.get(localRoot);
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
			 * This method produces a less accurate result, but doesn't require
			 * that the file exist yet.
			 */
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
					.findFilesForLocation(basePath);
			if (files.length > 0) {
				file = files[0];
			}
		}

		while (file != null) {
			/**
			 * Treat any parent folder with a WEB-INF subfolder as a web-app
			 * root
			 */
			IContainer folder = null;
			if ((file.getType() & IResource.FOLDER) != 0) {
				folder = (IContainer) file;
			} else {
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
			} else {
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
		if (fBuildPathIsDirty) {
			return;
		}

		// Logger.log(Logger.INFO_DEBUG, "IJavaElementDelta: " + delta);
		IJavaElement element = delta.getElement();
		if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT
				&& ((IPackageFragmentRoot) element).isArchive()) {
			time0 = System.currentTimeMillis();
			String libPath = null;
			int taglibRecordEventKind = -1;
			if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) > 0) {
				taglibRecordEventKind = ITaglibRecordEvent.ADDED;
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
						element.getPath());
				if (file.exists()) {
					libPath = file.getLocation().toString();
				} else {
					libPath = element.getPath().toString();
				}
			} else if ((delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0) {
				taglibRecordEventKind = ITaglibRecordEvent.REMOVED;
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
						element.getPath());
				if (file.getLocation() != null) {
					libPath = file.getLocation().toString();
				} else {
					libPath = element.getPath().toString();
				}
			} else if ((delta.getFlags() & IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED) > 0) {
				taglibRecordEventKind = ITaglibRecordEvent.CHANGED;
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
						element.getPath());
				if (file.exists()) {
					libPath = file.getLocation().toString();
				} else {
					libPath = element.getPath().toString();
				}
			}
			if (libPath != null) {
				boolean fragmentisExported = true;
				try {
					fragmentisExported = ((IPackageFragmentRoot) element)
							.getRawClasspathEntry().isExported();
				} catch (JavaModelException e) {
					Logger
							.logException(
									"Problem handling build path entry for " + element.getPath(), e); //$NON-NLS-1$
				}
				updateClasspathLibrary(libPath, taglibRecordEventKind,
						fragmentisExported);
			}
			if (_debugIndexTime) {
				Logger
						.log(
								Logger.INFO,
								"processed build path delta for " + fProject.getName() + "(" + element.getPath() + ") in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
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
		} catch (CoreException e) {
			Logger.logException(e);
		}

		if (_debugIndexTime) {
			Logger
					.log(
							Logger.INFO,
							"indexed " + fProject.getName() + " contents in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	void indexClasspath() {
		if (_debugIndexTime) {
			time0 = System.currentTimeMillis();
		}
		fClasspathProjects.clear();
		fClasspathReferences.clear();
		fClasspathJars.clear();

		IJavaProject javaProject = JavaCore.create(fProject);
		/*
		 * If the Java nature isn't present (or something else is wrong), don't
		 * check the build path.
		 */
		if (javaProject.exists()) {
			indexClasspath(javaProject);
		}
		// else {
		// Logger.log(Logger.WARNING, "TaglibIndex was asked to index non-Java
		// Project " + fProject.getName()); //$NON-NLS-1$
		// }

		if (_debugIndexTime) {
			Logger
					.log(
							Logger.INFO,
							"indexed " + fProject.getName() + " classpath in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * @param entry
	 */
	private void indexClasspath(IClasspathEntry entry) {
		switch (entry.getEntryKind()) {
		case IClasspathEntry.CPE_CONTAINER: {
			IClasspathContainer container = (IClasspathContainer) entry;
			IClasspathEntry[] containedEntries = container
					.getClasspathEntries();
			for (int i = 0; i < containedEntries.length; i++) {
				indexClasspath(containedEntries[i]);
			}
		}
			break;
		case IClasspathEntry.CPE_LIBRARY: {
			/*
			 * Ignore libs in required projects that are not exported
			 */
			if (fClasspathProjects.size() < 2 || entry.isExported()) {
				IPath libPath = entry.getPath();
				if (!fClasspathJars.containsKey(libPath.toString())) {
					if (libPath.toFile().exists()) {
						updateClasspathLibrary(libPath.toString(),
								ITaglibRecordEvent.ADDED, entry.isExported());
					} else {
						/*
						 * Note: .jars on the classpath inside of the
						 * project will have duplicate entries in the JAR
						 * references table that will e returned to
						 * getAvailableTaglibRecords().
						 */
						IFile libFile = ResourcesPlugin.getWorkspace()
								.getRoot().getFile(libPath);
						if (libFile != null && libFile.exists()) {
							updateClasspathLibrary(libFile.getLocation()
									.toString(), ITaglibRecordEvent.ADDED,
									entry.isExported());
						}
					}
				}
			}
		}
			break;
		case IClasspathEntry.CPE_PROJECT: {
			/*
			 * We're currently ignoring whether the project exports all of
			 * its build path
			 */
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(entry.getPath().lastSegment());
			if (project != null) {
				fClasspathProjects.add(project);
			}
		}
			break;
		case IClasspathEntry.CPE_SOURCE:
			break;
		case IClasspathEntry.CPE_VARIABLE:
			break;
		}
	}

	/*
	 * private void removeClasspathLibrary(String libraryLocation) { JarRecord
	 * record = (JarRecord) fClasspathJars.remove(libraryLocation); if (record !=
	 * null) { URLRecord[] records = (URLRecord[])
	 * record.getURLRecords().toArray(new URLRecord[0]); for (int i = 0; i <
	 * records.length; i++) { fClasspathReferences.remove(records[i].getURI()); }
	 * TaglibIndex.fireTaglibRecordEvent(new TaglibRecordEvent(record,
	 * ITaglibRecordEvent.REMOVED)); } }
	 */

	/**
	 * @param javaProject
	 */
	private void indexClasspath(IJavaProject javaProject) {
		if (javaProject == null) {
			return;
		}

		IProject project = javaProject.getProject();
		if (project.equals(fProject)) {
			try {
				IClasspathEntry[] entries = javaProject
						.getResolvedClasspath(true);
				for (int i = 0; i < entries.length; i++) {
					indexClasspath(entries[i]);
				}
			} catch (JavaModelException e) {
				Logger
						.logException(
								"Error searching Java Build Path + (" + fProject.getName() + ") for tag libraries", e); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	private String readTextofChild(Node node, String childName) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals(childName)) {
				return getTextContents(child);
			}
		}
		return ""; //$NON-NLS-1$
	}

	void removeJAR(IResource jar) {
		if (_debugIndexCreation) {
			Logger.log(Logger.INFO,
					"removing records for JAR " + jar.getFullPath()); //$NON-NLS-1$
		}
		JarRecord record = (JarRecord) fJARReferences.remove(jar.getFullPath()
				.toString());
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(
					new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				
				getImplicitReferences(jar.getFullPath().toString()).remove(
						records[i].getURI());
			}
			
		}
	}

	void removeTagDir(IResource tagFile) {
		// IContainer tagdir = tagFile.getParent();
		// String tagdirLocation = tagdir.getFullPath().toString();
		// fTagDirReferences.remove(tagdirLocation);
	}

	void removeTLD(IResource tld) {
		if (_debugIndexCreation) {
			Logger.log(Logger.INFO, "removing record for " + tld.getFullPath()); //$NON-NLS-1$
		}
		TLDRecord record = (TLDRecord) fTLDReferences.remove(tld.getFullPath());
		if (record != null) {
			if (record.getURI() != null) {
				getImplicitReferences(tld.getFullPath().toString()).remove(
						record.getURI());
			}
			
		}
	}

	void removeWebXML(IResource webxml) {
		if (_debugIndexCreation) {
			Logger.log(Logger.INFO,
					"removing records for " + webxml.getFullPath()); //$NON-NLS-1$
		}
		WebXMLRecord record = (WebXMLRecord) fWebXMLReferences.remove(webxml
				.getLocation().toString());
		if (record != null) {
			TLDRecord[] records = (TLDRecord[]) record.getTLDRecords().toArray(
					new TLDRecord[0]);
			for (int i = 0; i < records.length; i++) {
				if (_debugIndexCreation) {
					Logger
							.log(
									Logger.INFO,
									"removed record for " + records[i].getURI() + "@" + records[i].path); //$NON-NLS-1$ //$NON-NLS-2$
				}
				getImplicitReferences(webxml.getFullPath().toString()).remove(
						records[i].getURI());
				
			}
		}
	}

	/**
	 * @param basePath
	 * @param reference
	 * @return
	 */
	ITaglibRecord resolve(String basePath, String reference) {
		ensureUpTodate();

		ITaglibRecord record = null;
		String path = null;

		/**
		 * Workaround for problem in URIHelper; uris starting with '/' are
		 * returned as-is.
		 */
		if (reference.startsWith("/")) { //$NON-NLS-1$
			path = getLocalRoot(basePath) + reference;
		} else {
			path = URIHelper.normalize(reference, basePath,
					getLocalRoot(basePath));
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
			record = (ITaglibRecord) getImplicitReferences(basePath).get(
					reference);
		}

		if (record == null) {
			record = (ITaglibRecord) fTagDirReferences.get(path);
		}

		if (record == null) {
			record = (ITaglibRecord) fClasspathReferences.get(reference);
		}
		if (record == null) {
			Map buildPathReferences = new HashMap();
			List projectsProcessed = new ArrayList(
					fClasspathProjects.size() + 1);
			projectsProcessed.add(fProject);
			addBuildPathReferences(buildPathReferences, projectsProcessed,
					false);
			record = (ITaglibRecord) buildPathReferences.get(reference);
		}

		// Check the XML Catalog
		if (record == null) {
			ICatalog catalog = XMLCorePlugin.getDefault()
					.getDefaultXMLCatalog();
			if (catalog != null) {
				String resolvedString = null;
				try {
					// Check as system reference first
					resolvedString = catalog.resolveSystem(reference);
					// Check as URI
					if (resolvedString == null
							|| resolvedString.trim().length() == 0) {
						resolvedString = catalog.resolveURI(reference);
					}
					// Check as public ID
					if (resolvedString == null
							|| resolvedString.trim().length() == 0) {
						resolvedString = catalog.resolvePublic(reference,
								basePath);
					}
				} catch (Exception e) {
					Logger.logException(e);
				}
				if (resolvedString != null
						&& resolvedString.trim().length() > 0) {
					record = createCatalogRecord(reference, resolvedString);
				}
			}
		}

		// If no records were found and no local-root applies, check ALL of
		// the web.xml files as a fallback
		if (record == null
				&& fProject.getFullPath().toString().equals(
						getLocalRoot(basePath))) {
			WebXMLRecord[] webxmls = (WebXMLRecord[]) fWebXMLReferences
					.values().toArray(new WebXMLRecord[0]);
			for (int i = 0; i < webxmls.length; i++) {
				if (record != null) {
					continue;
				}
				record = (ITaglibRecord) getImplicitReferences(
						webxmls[i].path.toString()).get(reference);
			}
		}

		return record;
	}

	/**
	 * Restores any saved reference tables
	 */
	private void restoreReferences() {
		if (false) {
			// resources first
			index();
			// now build path

			// ================ test reload time ========================
			boolean restored = false;
			File savedState = new File(fSaveStateFilename);
			if (savedState.exists()) {
				Reader reader = null;
				try {
					time0 = System.currentTimeMillis();
					reader = new InputStreamReader(new BufferedInputStream(
							new FileInputStream(savedState)), "UTF-16");
					// use a string buffer temporarily to reduce string
					// creation
					StringBuffer buffer = new StringBuffer();
					char array[] = new char[2048];
					int charsRead = 0;
					while ((charsRead = reader.read(array)) != -1) {
						if (charsRead > 0) {
							buffer.append(array, 0, charsRead);
						}
					}

					IDocument doc = new org.eclipse.jface.text.Document();
					doc.set(buffer.toString());
					int lines = doc.getNumberOfLines();
					if (lines > 0) {
						IRegion line = doc.getLineInformation(0);
						String lineText = doc.get(line.getOffset(), line
								.getLength());
						JarRecord libraryRecord = null;
						if (SAVE_FORMAT_VERSION.equals(lineText.trim())) {
							IWorkspaceRoot workspaceRoot = ResourcesPlugin
									.getWorkspace().getRoot();

							for (int i = 1; i < lines && !fBuildPathIsDirty; i++) {
								line = doc.getLineInformation(i);
								lineText = doc.get(line.getOffset(), line
										.getLength());
								StringTokenizer toker = new StringTokenizer(
										lineText, "|"); //$NON-NLS-1$
								if (toker.hasMoreTokens()) {
									String tokenType = toker.nextToken();
									if ("JAR".equalsIgnoreCase(tokenType)) { //$NON-NLS-1$ 
										boolean has11TLD = Boolean.valueOf(
												toker.nextToken())
												.booleanValue();
										boolean exported = Boolean.valueOf(
												toker.nextToken())
												.booleanValue();
										// make the rest the libraryLocation
										String libraryLocation = toker
												.nextToken();
										while (toker.hasMoreTokens()) {
											libraryLocation = libraryLocation
													+ "|" + toker.nextToken(); //$NON-NLS-1$ 
										}
										libraryLocation = libraryLocation
												.trim();
										
										// Create a new JarRecord
										libraryRecord = createJARRecord(libraryLocation);
										libraryRecord.has11TLD = has11TLD;
										libraryRecord.isExported = exported;

										// Add a URLRecord for the 1.1 TLD
										if (has11TLD) {
											InputStream contents = JarUtilities
													.getInputStream(
															libraryLocation,
															JarUtilities.JSP11_TAGLIB);
											if (contents != null) {
												TaglibInfo info = extractInfo(
														libraryLocation,
														contents);

												if (info != null
														&& info.uri != null
														&& info.uri.length() > 0) {
													URLRecord urlRecord = new URLRecord();
													urlRecord.info = info;
													urlRecord.isExported = exported;
													urlRecord.baseLocation = libraryLocation;
													try {
														urlRecord.url = new URL(
																"jar:file:" + libraryLocation + "!/" + JarUtilities.JSP11_TAGLIB); //$NON-NLS-1$ //$NON-NLS-2$
														libraryRecord.urlRecords
																.add(urlRecord);
														fClasspathReferences
																.put(
																		urlRecord
																				.getURI(),
																		urlRecord);
														if (_debugIndexCreation) {
															Logger
																	.log(
																			Logger.INFO,
																			"created record for " + urlRecord.getURI() + "@" + urlRecord.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
														}
													} catch (MalformedURLException e) {
														/*
														 * don't record this URI
														 */
														Logger.logException(e);
													}
												}
												try {
													contents.close();
												} catch (IOException e) {
												}
											}
										}

										fClasspathJars.put(libraryLocation,
												libraryRecord);
									} else if ("URL".equalsIgnoreCase(tokenType) && libraryRecord != null) { //$NON-NLS-1$
										// relies on a previously declared JAR
										// record
										boolean exported = Boolean.valueOf(
												toker.nextToken())
												.booleanValue();
										// make the rest the URL
										String urlString = toker.nextToken();
										while (toker.hasMoreTokens()) {
											urlString = urlString
													+ "|" + toker.nextToken(); //$NON-NLS-1$ 
										}
										urlString = urlString.trim();
										// Append a URLrecord
										URLRecord urlRecord = new URLRecord();
										urlRecord.url = new URL(urlString);
										urlRecord.isExported = exported;
										urlRecord.baseLocation = libraryRecord.location
												.toString();
										libraryRecord.urlRecords.add(urlRecord);

										ByteArrayInputStream cachedContents = null;
										InputStream tldStream = null;
										try {
											URLConnection connection = urlRecord.url
													.openConnection();
											connection
													.setDefaultUseCaches(false);
											tldStream = connection
													.getInputStream();
										} catch (IOException e1) {
											Logger.logException(e1);
										}

										int c;
										ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
										// array dim restriction?
										byte bytes[] = new byte[2048];
										try {
											while ((c = tldStream.read(bytes)) >= 0) {
												byteArrayOutput.write(bytes, 0,
														c);
											}
											cachedContents = new ByteArrayInputStream(
													byteArrayOutput
															.toByteArray());
										} catch (IOException ioe) {
											// no cleanup can be done
										} finally {
											try {
												tldStream.close();
											} catch (IOException e) {
											}
										}

										TaglibInfo info = extractInfo(
												urlRecord.url.toString(),
												cachedContents);
										if (info != null) {
											urlRecord.info = info;
										}
										try {
											cachedContents.close();
										} catch (IOException e) {
										}
										fClasspathReferences.put(urlRecord
												.getURI(), urlRecord);
									} else if (BUILDPATH_PROJECT
											.equalsIgnoreCase(tokenType)) {
										String projectName = toker.nextToken();
										if (Path.ROOT
												.isValidSegment(projectName)) {
											IProject project = workspaceRoot
													.getProject(projectName);
											/* do not check if "open" here */
											if (project != null) {
												fClasspathProjects.add(project);
											}
										}
									}
									// last since only occurs once
									else if (BUILDPATH_DIRTY
											.equalsIgnoreCase(tokenType)) {
										fBuildPathIsDirty = Boolean.valueOf(
												toker.nextToken())
												.booleanValue();
									}
								
								
								}
							}
							restored = true;
						} else {
							Logger
									.log(
											Logger.INFO,
											"Tag Library Index: different cache format found, was \"" + lineText + "\", supports \"" + SAVE_FORMAT_VERSION + "\", reindexing build path"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
					if (_debugIndexTime) {
						Logger
								.log(
										Logger.INFO,
										"time spent reloading " + fProject.getName() + " build path: " + (System.currentTimeMillis() - time0)); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} catch (Exception e) {
					restored = false;
					if (_debugIndexTime) {
						Logger
								.log(
										Logger.INFO,
										"failure reloading " + fProject.getName() + " build path index", e); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				}
			}

			// ================ test reload time (end) ==================

			if (!restored) {
				setBuildPathIsDirty();
			}
		}
	}

	/**
	 * Saves any storable references to disk. This is only called when the
	 * description is being cleared and not after every update.
	 */
	void saveReferences() {
		// the build path information is out of date, remember that
		time0 = System.currentTimeMillis();
		Writer writer = null;

		/**
		 * <pre>
		 *                      		 1.0.1
		 *                      		 Save classpath information (| is field delimiter)
		 *                      		 Jars are saved as &quot;JAR:&quot;+ has11TLD + jar path 
		 *                      		 URLRecords as &quot;URL:&quot;+URL
		 * </pre>
		 */
		try {
			writer = new OutputStreamWriter(new FileOutputStream(
					fSaveStateFilename), "UTF-16"); //$NON-NLS-1$
			writer.write(SAVE_FORMAT_VERSION);
			writer.write('\n');
			writer.write(BUILDPATH_DIRTY + "|" + fBuildPathIsDirty); //$NON-NLS-1$
			writer.write('\n');

			IProject[] projects = (IProject[]) fClasspathProjects
					.toArray(new IProject[0]);
			for (int i = 0; i < projects.length; i++) {
				writer.write(BUILDPATH_PROJECT);
				writer.write("|"); //$NON-NLS-1$
				writer.write(projects[i].getName());
				writer.write('\n');
			}

			Enumeration jars = fClasspathJars.keys();
			while (jars.hasMoreElements()) {
				String jarPath = jars.nextElement().toString();
				JarRecord jarRecord = (JarRecord) fClasspathJars.get(jarPath);
				writer.write("JAR|"); //$NON-NLS-1$
				writer.write(Boolean.toString(jarRecord.has11TLD));
				writer.write('|');
				writer.write(Boolean.toString(jarRecord.isExported));
				writer.write('|');
				writer.write(jarPath);
				writer.write('\n');
				Iterator i = jarRecord.urlRecords.iterator();
				while (i.hasNext()) {
					URLRecord urlRecord = (URLRecord) i.next();
					writer.write("URL|"); //$NON-NLS-1$
					writer.write(String.valueOf(urlRecord.isExported));
					writer.write("|"); //$NON-NLS-1$
					writer.write(urlRecord.getURL().toExternalForm());
					writer.write('\n');
				}
			}
		} catch (IOException e) {
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
			}
		}

		if (_debugIndexTime) {
			Logger
					.log(
							Logger.INFO,
							"time spent saving index for " + fProject.getName() + ": " + (System.currentTimeMillis() - time0)); //$NON-NLS-1$
		}
	}

	void setBuildPathIsDirty() {
		fBuildPathIsDirty = true;
		if (_debugIndexTime) {
			Logger
					.log(
							Logger.INFO,
							"marking build path information for " + fProject.getName() + " as dirty"); //$NON-NLS-1$
		}
	}

	void updateClasspathLibrary(String libraryLocation, int deltaKind,
			boolean isExported) {
		JarRecord libraryRecord = null;
		if (deltaKind == ITaglibRecordEvent.REMOVED
				|| deltaKind == ITaglibRecordEvent.CHANGED) {
			libraryRecord = (JarRecord) fClasspathJars.remove(libraryLocation);
			if (libraryRecord != null) {
				IURLRecord[] urlRecords = (IURLRecord[]) libraryRecord.urlRecords
						.toArray(new IURLRecord[0]);
				for (int i = 0; i < urlRecords.length; i++) {
					fClasspathReferences.remove(urlRecords[i].getURI());
				}
			}
		}
		if (deltaKind == ITaglibRecordEvent.ADDED
				|| deltaKind == ITaglibRecordEvent.CHANGED) {
			libraryRecord = createJARRecord(libraryLocation);
			libraryRecord.isExported = isExported;
			fClasspathJars.put(libraryLocation, libraryRecord);

			ZipFile jarfile = null;
			try {
				jarfile = new ZipFile(libraryLocation);
				Enumeration entries = jarfile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry z = (ZipEntry) entries.nextElement();
					if (!z.isDirectory()) {
						if (z.getName().toLowerCase(Locale.US).endsWith(".tld")) { //$NON-NLS-1$
							if (z.getName().equals(JarUtilities.JSP11_TAGLIB)) {
								libraryRecord.has11TLD = true;
							}
							InputStream contents = getCachedInputStream(
									jarfile, z);
							if (contents != null) {
								TaglibInfo info = extractInfo(libraryLocation,
										contents);

								if (info != null && info.uri != null
										&& info.uri.length() > 0) {
									URLRecord urlRecord = new URLRecord();
									urlRecord.info = info;
									urlRecord.baseLocation = libraryLocation;
									try {
										urlRecord.isExported = isExported;
										urlRecord.url = new URL(
												"jar:file:" + libraryLocation + "!/" + z.getName()); //$NON-NLS-1$ //$NON-NLS-2$
										libraryRecord.urlRecords.add(urlRecord);
										fClasspathReferences.put(urlRecord
												.getURI(), urlRecord);
										if (_debugIndexCreation) {
											Logger
													.log(
															Logger.INFO,
															"created record for " + urlRecord.getURI() + "@" + urlRecord.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
										}
									} catch (MalformedURLException e) {
										// don't record this URI
										Logger.logException(e);
									}
								}
								try {
									contents.close();
								} catch (IOException e) {
								}
							}
						}
					}
				}
			} catch (ZipException zExc) {
				Logger
						.log(
								Logger.WARNING,
								"Taglib Index ZipException: " + libraryLocation + " " + zExc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IOException ioExc) {
				Logger
						.log(
								Logger.WARNING,
								"Taglib Index IOException: " + libraryLocation + " " + ioExc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			} finally {
				closeJarFile(jarfile);
			}
		}
		
	}

	void updateJAR(IResource jar, int deltaKind) {
		if (_debugIndexCreation) {
			Logger.log(Logger.INFO,
					"creating records for JAR " + jar.getFullPath()); //$NON-NLS-1$
		}

		String jarLocationString = jar.getLocation().toString();
		String[] entries = JarUtilities.getEntryNames(jar);
		JarRecord jarRecord = createJARRecord(jar);
		fJARReferences.put(jar.getFullPath().toString(), jarRecord);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].endsWith(".tld")) { //$NON-NLS-1$
				if (entries[i].equals(JarUtilities.JSP11_TAGLIB)) {
					jarRecord.has11TLD = true;
				}
				InputStream contents = JarUtilities.getInputStream(jar,
						entries[i]);
				if (contents != null) {
					TaglibInfo info = extractInfo(jarLocationString, contents);

					if (info != null && info.uri != null
							&& info.uri.length() > 0) {
						URLRecord record = new URLRecord();
						record.info = info;
						record.baseLocation = jarLocationString;
						try {
							record.url = new URL(
									"jar:file:" + jarLocationString + "!/" + entries[i]); //$NON-NLS-1$ //$NON-NLS-2$
							jarRecord.urlRecords.add(record);
							getImplicitReferences(jar.getFullPath().toString())
									.put(record.getURI(), record);
							
							if (_debugIndexCreation) {
								Logger
										.log(
												Logger.INFO,
												"created record for " + record.getURI() + "@" + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
							}
						} catch (MalformedURLException e) {
							// don't record this URI
							Logger.logException(e);
						}
					}
					try {
						contents.close();
					} catch (IOException e) {
					}
				} else {
					Logger.log(Logger.ERROR_DEBUG, getClass().getName()
							+ "could not read resource " + jar.getFullPath()); //$NON-NLS-1$
				}
			}
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
		if (_debugIndexCreation) {
			Logger.log(Logger.INFO, "creating record for " + tld.getFullPath()); //$NON-NLS-1$
		}
		TLDRecord record = createTLDRecord(tld);
		fTLDReferences.put(tld.getFullPath().toString(), record);
		if (record.getURI() != null) {
			getImplicitReferences(tld.getFullPath().toString()).put(
					record.getURI(), record);
		}
		
	}

	void updateWebXML(IResource webxml, int deltaKind) {
		if (webxml.getType() != IResource.FILE) {
			return;
		}
		InputStream webxmlContents = null;
		Document document = null;
		try {
			webxmlContents = ((IFile) webxml).getContents(true);
			DocumentProvider provider = new DocumentProvider();
			provider.setInputStream(webxmlContents);
			provider.setValidating(false);
			provider.setRootElementName("web-app"); //$NON-NLS-1$
			provider.setBaseReference(webxml.getParent().getFullPath()
					.toString());
			document = provider.getDocument(false);
		} catch (CoreException e) {
			Logger.log(Logger.ERROR_DEBUG, "", e); //$NON-NLS-1$
		} finally {
			if (webxmlContents != null) {
				try {
					webxmlContents.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
		if (document == null) {
			return;
		}
		if (_debugIndexCreation) {
			Logger.log(Logger.INFO,
					"creating records for " + webxml.getFullPath()); //$NON-NLS-1$
		}

		WebXMLRecord webxmlRecord = new WebXMLRecord();
		webxmlRecord.path = webxml.getFullPath();
		fWebXMLReferences
				.put(webxmlRecord.getWebXML().toString(), webxmlRecord);
		NodeList taglibs = document.getElementsByTagName(JSP12TLDNames.TAGLIB);
		for (int iTaglib = 0; iTaglib < taglibs.getLength(); iTaglib++) {
			String taglibUri = readTextofChild(taglibs.item(iTaglib),
					"taglib-uri").trim(); //$NON-NLS-1$
			// specified location is relative to root of the webapp
			String taglibLocation = readTextofChild(taglibs.item(iTaglib),
					"taglib-location").trim(); //$NON-NLS-1$
			IPath path = null;
			if (taglibLocation.startsWith("/")) { //$NON-NLS-1$
				path = new Path(getLocalRoot(webxml.getFullPath().toString())
						+ taglibLocation);
			} else {
				path = new Path(URIHelper.normalize(taglibLocation, webxml
						.getFullPath().toString(), getLocalRoot(webxml
						.getFullPath().toString())));
			}
			if (path.segmentCount() > 1) {
				IFile resource = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(path);
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
								if (entries[jEntry]
										.equals(JarUtilities.JSP11_TAGLIB)) {
									jarRecord.has11TLD = true;
									InputStream contents = JarUtilities
											.getInputStream(resource,
													entries[jEntry]);
									if (contents != null) {
										TaglibInfo info = extractInfo(resource
												.getFullPath().toString(),
												contents);
										jarRecord.info = info;
										try {
											contents.close();
										} catch (IOException e) {
										}
									}
								}
							}
						}
						record = jarRecord;
						// the stored URI should reflect the web.xml's value
						jarRecord.info.uri = taglibUri;
						jarRecord.isMappedInWebXML = true;
						if (_debugIndexCreation) {
							Logger
									.log(
											Logger.INFO,
											"created web.xml record for " + taglibUri + "@" + jarRecord.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {
						TLDRecord tldRecord = createTLDRecord(resource);
						record = tldRecord;
						// the stored URI should reflect the web.xml's value
						tldRecord.info.uri = taglibUri;
						if (_debugIndexCreation) {
							Logger
									.log(
											Logger.INFO,
											"created web.xml record for " + taglibUri + "@" + tldRecord.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					if (record != null) {
						webxmlRecord.tldRecords.add(record);
						getImplicitReferences(webxml.getFullPath().toString())
								.put(taglibUri, record);
						
					}
				}
			}
		}
	}
}
