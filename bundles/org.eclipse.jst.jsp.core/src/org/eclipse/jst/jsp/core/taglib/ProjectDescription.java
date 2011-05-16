/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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
import java.util.zip.ZipInputStream;

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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.java.ArrayMap;
import org.eclipse.jst.jsp.core.internal.util.DocumentProvider;
import org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
import org.eclipse.wst.sse.core.internal.util.Sorter;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.w3c.dom.Document;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.icu.text.Collator;
import com.ibm.icu.util.StringTokenizer;

/**
 * Contains the tag library information for a single project.
 * 
 *  * <p>
 * This class is neither intended to be instantiated nor accessed by clients.
 * </p>
 *
 */
class ProjectDescription {
	static final String EMPTY_STRING = ""; //$NON-NLS-1$
	class BuildPathJob extends Job {
		public BuildPathJob() {
			super("Updating Tag Library Index");
			setSystem(true);
			setUser(false);
		}

		protected IStatus run(IProgressMonitor monitor) {
			try {
				LOCK.acquire();

				PackageFragmentRootDelta[] removes = (PackageFragmentRootDelta[]) fPackageFragmentRootsRemoved.values().toArray(new PackageFragmentRootDelta[fPackageFragmentRootsRemoved.size()]);
				for (int i = 0; i < removes.length; i++) {
					handleElementChanged(removes[i].elementPath, removes[i].deltaKind, removes[i].isExported);
				}
				fPackageFragmentRootsRemoved.clear();
				if (monitor.isCanceled())
					return Status.OK_STATUS;
				
				PackageFragmentRootDelta[] changes = (PackageFragmentRootDelta[]) fPackageFragmentRootsChanged.values().toArray(new PackageFragmentRootDelta[fPackageFragmentRootsChanged.size()]);
				for (int i = 0; i < changes.length; i++) {
					handleElementChanged(changes[i].elementPath, changes[i].deltaKind, changes[i].isExported);
				}
				fPackageFragmentRootsChanged.clear();
				if (monitor.isCanceled())
					return Status.OK_STATUS;
				
				PackageFragmentRootDelta[] adds = (PackageFragmentRootDelta[]) fPackageFragmentRootsAdded.values().toArray(new PackageFragmentRootDelta[fPackageFragmentRootsAdded.size()]);
				for (int i = 0; i < adds.length; i++) {
					handleElementChanged(adds[i].elementPath, adds[i].deltaKind, adds[i].isExported);
				}
				fPackageFragmentRootsAdded.clear();
			}
			finally {
				LOCK.release();
			}
			TaglibIndex.getInstance().fireCurrentDelta(BuildPathJob.this);
			return Status.OK_STATUS;
		}
	}
	
	class PackageFragmentRootDelta {
		PackageFragmentRootDelta(IPath path, int kind, boolean exported) {
			super();
			elementPath = path;
			deltaKind = kind;
			isExported = exported;
		}

		/*
			IJavaElementDelta.F_ADDED_TO_CLASSPATH
			IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED
			IJavaElementDelta.F_REMOVED_FROM_CLASSPATH
		*/ 
		int deltaKind;
		IPath elementPath;
		boolean isExported;
	}

	class DeltaVisitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE) {
				if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() == IResourceDelta.ENCODING || delta.getFlags() == IResourceDelta.MARKERS))
					return true;
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
				else if ("tag".equalsIgnoreCase(resource.getFileExtension()) || "tagx".equalsIgnoreCase(resource.getFileExtension())) { //$NON-NLS-1$ //$NON-NLS-2$
					if (delta.getKind() == IResourceDelta.REMOVED) {
						removeTag(resource);
					}
					else {
						updateTag(resource, delta.getKind());
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
			boolean visitMembers = true;
			if (proxy.getType() == IResource.FILE && !proxy.isDerived()) {
				if (proxy.getName().endsWith(".tld")) { //$NON-NLS-1$
					updateTLD(proxy.requestResource(), ITaglibIndexDelta.ADDED);
				}
				else if (proxy.getName().endsWith(".jar")) { //$NON-NLS-1$
					updateJAR(proxy.requestResource(), ITaglibIndexDelta.ADDED);
				}
				else if (proxy.getName().endsWith(".tag") || proxy.getName().endsWith(".tagx")) { //$NON-NLS-1$ //$NON-NLS-2$
					updateTagDir(proxy.requestResource().getParent(), ITaglibIndexDelta.ADDED);
					// any folder with these files will create a record for
					// that folder in one pass
					visitMembers = false;
				}
				else if (proxy.getName().equals(WEB_XML) && proxy.requestResource().getParent().getName().equals(WEB_INF)) {
					updateWebXML(proxy.requestResource(), ITaglibIndexDelta.ADDED);
				}
			}
			String name = proxy.getName();
			return name.length() != 0 && name.charAt(0) != '.' && visitMembers && !proxy.isDerived();
		}
	}

	static class JarRecord implements IJarRecord {		
		/**
		 * Whether this jar includes an entry name "META-INF/taglib.tld"
		 */
		boolean has11TLD;
		TaglibInfo info;

		/**
		 * Whether this record is in a library that was "exported" on the Java
		 * Build Path. May no longer be accurate since the library may be used
		 * in multiple projects.
		 * 
		 *  @deprecated - no current use, might not be worth preserving
		 */
		boolean isExported = true;
		
		/**
		 * Whether this record was created based on a mapping within a
		 * deployment descriptor. May also be innaccurate if the jar is used
		 * in multiple projects.
		 */
		boolean isMappedInWebXML;
		boolean isConsistent = false;
		IPath location;
		Collection urlRecords;

		public boolean equals(Object obj) {
			if (!(obj instanceof JarRecord))
				return false;
			return ((JarRecord) obj).location.equals(location) && ((JarRecord) obj).has11TLD == has11TLD && ((JarRecord) obj).info.equals(info);
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

		public Collection getURLRecords() {
			return urlRecords;
		}

		public String toString() {
			StringBuffer s = new StringBuffer("JarRecord: ");//$NON-NLS-1$ 
			s.append(location);
			if (urlRecords.size() > 0) {
				s.append('\n');//$NON-NLS-1$ 
				for (Iterator it = urlRecords.iterator(); it.hasNext();) {
					s.append(it.next());
					s.append('\n');//$NON-NLS-1$ 
				}
			}
			return s.toString();
		}
	}

	static class TagDirRecord implements ITagDirRecord {
		TaglibInfo info;
		IPath path;
		// a List holding Strings of .tag and .tagx filenames relative to the
		// tagdir's location
		List tags = new ArrayList(0);

		public boolean equals(Object obj) {
			if (!(obj instanceof TagDirRecord))
				return false;
			return ((TagDirRecord) obj).path.equals(path) && ((TagDirRecord) obj).info.equals(info);
		}

		public ITaglibDescriptor getDescriptor() {
			return info != null ? info : (info = new TaglibInfo());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jst.jsp.core.taglib.ITagDirRecord#getPath()
		 */
		public IPath getPath() {
			return path;
		}

		public int getRecordType() {
			return ITaglibRecord.TAGDIR;
		}

		/**
		 * @return Returns the tags.
		 */
		public String[] getTagFilenames() {
			return (String[]) tags.toArray(new String[tags.size()]);
		}

		public String toString() {
			return "TagdirRecord: " + path + " <-> " + info.shortName; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * A brief representation of the information in a TLD.
	 */
	static class TaglibInfo implements ITaglibDescriptor {
		// extract only when asked?
		String description = EMPTY_STRING;
		String displayName = EMPTY_STRING;
		String jspVersion = EMPTY_STRING;
		String largeIcon = EMPTY_STRING;
		String shortName = EMPTY_STRING;
		String smallIcon = EMPTY_STRING;
		String tlibVersion = EMPTY_STRING;
		String uri = EMPTY_STRING;

		public TaglibInfo() {
			super();
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof TaglibInfo))
				return false;
			return ((TaglibInfo) obj).jspVersion.equals(jspVersion) && ((TaglibInfo) obj).description.equals(description) && ((TaglibInfo) obj).largeIcon.equals(largeIcon) && ((TaglibInfo) obj).shortName.equals(shortName) && ((TaglibInfo) obj).smallIcon.equals(smallIcon) && ((TaglibInfo) obj).tlibVersion.equals(tlibVersion) && ((TaglibInfo) obj).uri.equals(uri);
		}

		public String getDescription() {
			return description;
		}

		public String getDisplayName() {
			return displayName;
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

		public String toString() {
			return "TaglibInfo|" + uri + "|" + shortName + "|" + tlibVersion + "|" + smallIcon + "|" + largeIcon + "|" + jspVersion + "|" + description; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		}
	}

	static class TLDRecord implements ITLDRecord {
		TaglibInfo info;
		IPath path;

		public boolean equals(Object obj) {
			if (!(obj instanceof TLDRecord))
				return false;
			return ((TLDRecord) obj).path.equals(path) && ((TLDRecord) obj).getURI().equals(getURI()) && ((TLDRecord) obj).info.equals(info);
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
		/**
		 * XXX: Possibly a problem if the owning jar is shared across
		 * projects--this value helps determine visibility during enumeration
		 * and resolution and may not be correct.
		 */
		boolean isExported = true;
		URL url;

		public URLRecord() {
			super();
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof URLRecord))
				return false;
			return ((URLRecord) obj).baseLocation.equals(baseLocation) && ((URLRecord) obj).url.equals(url) && ((URLRecord) obj).info.equals(info);
		}

		public int hashCode() {
			return baseLocation.hashCode() + url.hashCode() + (isExported ? 1 : 0);
		}

		public String getBaseLocation() {
			return baseLocation;
		}

		public ITaglibDescriptor getDescriptor() {
			return info != null ? info : new TaglibInfo();
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
				return EMPTY_STRING; //$NON-NLS-1$
			return info.uri;
		}

		/**
		 * @return Returns the URL.
		 */
		public URL getURL() {
			return url;
		}

		public String toString() {
			return "URLRecord: (exported=" + isExported + ") " + baseLocation + " <-> " + getURI(); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	static class WebXMLRecord {
		TaglibInfo info;
		IPath path;
		List tldRecords = new ArrayList(0);

		public boolean equals(Object obj) {
			if (!(obj instanceof WebXMLRecord))
				return false;
			return ((WebXMLRecord) obj).path.equals(path) && ((WebXMLRecord) obj).info.equals(info);
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
			StringBuffer s = new StringBuffer("WebXMLRecord: ");//$NON-NLS-1$ 
			s.append(path);
			if (tldRecords.size() > 0) {
				s.append('\n');//$NON-NLS-1$ 
				for (int i = 0; i < tldRecords.size(); i++) {
					s.append(tldRecords.get(i));
					s.append('\n');//$NON-NLS-1$ 
				}
			}
			return s.toString();
		}
	}
	
	private class TaglibSorter extends Sorter {
		TaglibSorter() {
			super();
		}
		
		Collator collator = Collator.getInstance();

		public boolean compare(Object elementOne, Object elementTwo) {
			/**
			 * Returns true if elementTwo is 'greater than' elementOne This is
			 * the 'ordering' method of the sort operation. Each subclass
			 * overides this method with the particular implementation of the
			 * 'greater than' concept for the objects being sorted.
			 */
			
			return (collator.compare(getTaglibPath((ITaglibRecord) elementOne), getTaglibPath((ITaglibRecord) elementTwo))) < 0;
		}
		
		private String getTaglibPath(ITaglibRecord record) {
			switch(record.getRecordType()) {
				case ITaglibRecord.JAR:
					return ((JarRecord) record).getLocation().toString();
				case ITaglibRecord.TAGDIR:
					return ((TagDirRecord) record).getPath().toString();
				case ITaglibRecord.TLD:
					return ((TLDRecord) record).getPath().toString();
				case ITaglibRecord.URL:
					return ((URLRecord) record).getBaseLocation();
				default:
					return EMPTY_STRING; //$NON-NLS-1$
			}
		}
	}

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation")); //$NON-NLS-1$ //$NON-NLS-2$
	static boolean _debugIndexTime = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indextime")); //$NON-NLS-1$ //$NON-NLS-2$

	private static final String BUILDPATH_DIRTY = "BUILDPATH_DIRTY"; //$NON-NLS-1$
	private static final String BUILDPATH_ENTRIES = "BUILDPATH_ENTRIES"; //$NON-NLS-1$
	private static final String BUILDPATH_PROJECT = "BUILDPATH_PROJECT"; //$NON-NLS-1$
	private static final String SAVE_FORMAT_VERSION = "Tag Library Index 1.1.0"; //$NON-NLS-1$
	private static final String WEB_INF = "WEB-INF"; //$NON-NLS-1$
	private static final IPath WEB_INF_PATH = new Path(WEB_INF);
	private static final String WEB_XML = "web.xml"; //$NON-NLS-1$
	private static final char[] TLD = { 't', 'T', 'l', 'L', 'd', 'D'} ;
	
	/**
	 * Notes that the build path information is stale. Some operations can now
	 * be skipped until a resolve/getAvailable call is made.
	 */
	boolean fBuildPathIsDirty = false;

	/**
	 * Count of entries on the build path. Primary use case is for classpath
	 * containers that add an entry. Without notification (3.3), we can only
	 * check after-the-fact.
	 */
	int fBuildPathEntryCount = 0;

	/**
	 * A cached copy of all of the records createable from the XMLCatalog.
	 */
	private Collection fCatalogRecords;

	/*
	 * Records active JARs on the classpath. Taglib descriptors should be
	 * usable, but the jars by themselves are not.
	 */
	Hashtable fClasspathJars;

	/**
	 * A set of the projects that are in this project's build path.
	 * Lookups/enumerations will be redirected to the corresponding
	 * ProjectDescription instances
	 */
	Set fClasspathProjects = null;

	// holds references by URI to JARs
	Hashtable fClasspathReferences;

	/*
	 * this table is special in that it holds tables of references according
	 * to local roots
	 */
	Hashtable fImplicitReferences;

	Hashtable fJARReferences;

	IProject fProject;

	private String fSaveStateFilename;

	/**
	 * String->ITaglibRecord
	 */
	Hashtable fTagDirReferences;

	Hashtable fTLDReferences;

	IResourceDeltaVisitor fVisitor;
	Hashtable fWebXMLReferences;
	
	Map fPackageFragmentRootsAdded = new HashMap();
	Map fPackageFragmentRootsChanged = new HashMap();
	Map fPackageFragmentRootsRemoved = new HashMap();

	ILock LOCK = Job.getJobManager().newLock();

	private long time0;
	
	private TaglibSorter fTaglibSorter = new TaglibSorter();
	private BuildPathJob fBuildPathJob = new BuildPathJob();

	/** Shared JAR records between projects */
	private static final Map fSharedJarRecords = new Hashtable();

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
	void addBuildPathReferences(Map references, List projectsProcessed, boolean exportedOnly) {
		ensureUpTodate();

		// Add the build path references that are exported from this project
		Enumeration keys = fClasspathReferences.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			URLRecord urlRecord = (URLRecord) fClasspathReferences.get(key);
			if (exportedOnly) {
				if (urlRecord.isExported) {
					references.put(key, urlRecord);
				}
			}
			else {
				references.put(key, urlRecord);
			}
		}
		IProject[] buildpathProjects = (IProject[]) fClasspathProjects.toArray(new IProject[fClasspathProjects.size()]);
		for (int i = 0; i < buildpathProjects.length; i++) {
			if (!projectsProcessed.contains(buildpathProjects[i]) && buildpathProjects[i].isAccessible()) {
				projectsProcessed.add(buildpathProjects[i]);
				ProjectDescription description = TaglibIndex.getInstance().createDescription(buildpathProjects[i]);
				description.addBuildPathReferences(references, projectsProcessed, true);

				/*
				 * 199843 (183756) - JSP Validation Cannot Find Tag Library
				 * Descriptor in Referenced Projects
				 * 
				 * Add any TLD records having URI values from projects on the
				 * build path
				 */
				Map[] rootReferences = (Map[]) description.fImplicitReferences.values().toArray(new Map[description.fImplicitReferences.size()]);
				for (int j = 0; j < rootReferences.length; j++) {
					Iterator implicitRecords = rootReferences[j].values().iterator();
					while (implicitRecords.hasNext()) {
						ITaglibRecord record = (ITaglibRecord) implicitRecords.next();
						if (record.getRecordType() == ITaglibRecord.TLD && ((ITLDRecord) record).getURI() != null && ((ITLDRecord) record).getURI().length() > 0) {
							references.put(((ITLDRecord) record).getURI(), record);
						}
					}
				}
			}
		}
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
		if (file == null)
			return;
		try {
			file.close();
		}
		catch (IOException ioe) {
			// no cleanup can be done
			Logger.logException("TaglibIndex: Could not close zip file " + file.getName(), ioe); //$NON-NLS-1$
		}
	}

	/**
	 * @param catalogEntry
	 *            a XML catalog entry pointing to a .jar or .tld file
	 * @return a ITaglibRecord describing a TLD contributed to the XMLCatalog
	 *         if one was found at the given location, null otherwise
	 */
	private ITaglibRecord createCatalogRecord(ICatalogEntry catalogEntry) {
		return createCatalogRecord(catalogEntry.getKey(), catalogEntry.getURI());
	}

	/**
	 * @param uri -
	 *            the key value that will become the returned record's "URI"
	 * @param urlString -
	 *            the string indicating where the TLD really is
	 * @return a ITaglibRecord describing a TLD contributed to the XMLCatalog
	 *         if one was found at the given location, null otherwise
	 */
	private ITaglibRecord createCatalogRecord(String uri, String urlString) {
		ITaglibRecord record = null;
		// handle "file:" URLs that point to a .jar file on disk (1.1 mode)
		if (urlString.toLowerCase(Locale.US).endsWith((".jar")) && urlString.startsWith("file:")) { //$NON-NLS-1$ //$NON-NLS-2$
			String fileLocation = null;
			try {
				URL url = new URL(urlString);
				fileLocation = url.getFile();
			}
			catch (MalformedURLException e) {
				// not worth reporting
				Logger.log(Logger.ERROR_DEBUG, null, e);
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
								/*
								 * the record's reported URI should match the
								 * catalog entry's "key" so replace the
								 * detected value
								 */
								info.uri = uri;
								jarRecord.info = info;
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
				if (jarRecord.has11TLD) {
					if (_debugIndexCreation)
						Logger.log(Logger.INFO, "created catalog record for " + urlString + "@" + jarRecord.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$
					record = jarRecord;
				}

			}
		}
		// The rest are URLs into a plug-in...somewhere
		else {
			URL url = null;
			InputStream tldStream = null;
			try {
				url = new URL(urlString);
				tldStream = JarUtilities.getInputStream(url);
			}
			catch (Exception e1) {
				Logger.logException("Exception reading TLD contributed to the XML Catalog", e1);
			}

			if (tldStream != null) {
				URLRecord urlRecord = null;
				TaglibInfo info = extractInfo(urlString, tldStream);
				if (info != null) {
					/*
					 * the record's reported URI should match the catalog
					 * entry's "key" so replace the detected value
					 */
					info.uri = uri;
					urlRecord = new URLRecord();
					urlRecord.info = info;
					urlRecord.baseLocation = urlString;
					urlRecord.url = url; //$NON-NLS-1$ //$NON-NLS-2$
				}
				try {
					tldStream.close();
				}
				catch (IOException e) {
					Logger.log(Logger.ERROR_DEBUG, null, e);
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
		IPath location = jar.getLocation();
		JarRecord jarRecord = null;
		if (location != null) {
			jarRecord = createJARRecord(location.toString());
		}
		else if (jar.getLocationURI() != null) {
			jarRecord = createJARRecord(jar.getLocationURI().toString());
		}
		return jarRecord;
	}

	private JarRecord createJARRecord(String fileLocation) {
		synchronized (fSharedJarRecords) {
			JarRecord record = (JarRecord) fSharedJarRecords.get(fileLocation);
			if (record == null) {
				record = new JarRecord();
				record.info = new TaglibInfo();
				record.location = new Path(fileLocation);
				record.urlRecords = new HashSet(0);
				fSharedJarRecords.put(fileLocation, record);
			}
			return record;
		}
	}

	/**
	 * @return
	 */
	private TagDirRecord createTagdirRecord(IFolder tagdir) {
		IPath tagdirPath = tagdir.getFullPath();
		TagDirRecord record = new TagDirRecord();
		record.path = tagdir.getFullPath();
		record.info = new TaglibInfo();
		// 8.4.3
		if (tagdir.getName().equals("tags")) //$NON-NLS-1$
			record.info.shortName = "tags"; //$NON-NLS-1$
		else {
			boolean determined = false;
			IPath path = tagdirPath;
			String[] segments = path.segments();
			for (int i = 1; i < segments.length; i++) {
				if (segments[i - 1].equals("WEB-INF") && segments[i].equals("tags")) { //$NON-NLS-1$ //$NON-NLS-2$
					IPath tagdirLocalPath = path.removeFirstSegments(i + 1);
					record.info.shortName = StringUtils.replace(tagdirLocalPath.toString(), "/", "-");
					determined = true;
				}
			}
			if (!determined) {
				record.info.shortName = StringUtils.replace(tagdirPath.toString(), "/", "-");
			}
		}
		// 8.4.3
		record.info.tlibVersion = "1.0";
		record.info.description = EMPTY_STRING;
		record.info.displayName = EMPTY_STRING;
		record.info.smallIcon = EMPTY_STRING;
		record.info.largeIcon = EMPTY_STRING;

		try {
			IResource[] tagfiles = tagdir.members();
			for (int i = 0; i < tagfiles.length; i++) {
				if (tagfiles[i].getType() != IResource.FILE)
					continue;
				String extension = tagfiles[i].getFileExtension();
				if (extension != null && (extension.equals("tag") || extension.equals("tagx"))) {
					record.tags.add(tagfiles[i].getName());
				}
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
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
			if (tld.isAccessible()) {
				contents = ((IFile) tld).getContents();
				String basePath = tld.getFullPath().toString();
				TaglibInfo info = extractInfo(basePath, contents);
				if (info != null) {
					record.info = info;
				}
			}
		}
		catch (CoreException e) {
			// out of sync
		}
		finally {
			try {
				if (contents != null) {
					contents.close();
				}
			}
			catch (IOException e) {
				// ignore
				Logger.log(Logger.ERROR_DEBUG, null, e);
			}
		}
		return record;
	}

	private void ensureUpTodate() {
		IClasspathEntry[] entries = null;
			try {
				/*
				 * If the Java nature isn't present (or something else is
				 * wrong), don't check the build path.
				 */
				IJavaProject jproject = JavaCore.create(fProject);
				if (jproject != null && jproject.exists()) {
					entries = jproject.getResolvedClasspath(true);
				}
			}
			catch (JavaModelException e) {
				Logger.logException(e);
			}
		if (entries != null) {
			try {
				LOCK.acquire();
				/*
				 * Double-check that the number of build path entries has not
				 * changed. This should cover most cases such as when a
				 * library is added into or removed from a container.
				 */
				fBuildPathIsDirty = fBuildPathIsDirty || (fBuildPathEntryCount != entries.length);

				if (fBuildPathIsDirty) {
					indexClasspath(entries);
					fBuildPathIsDirty = false;
				}
				else {
					PackageFragmentRootDelta[] removes = (PackageFragmentRootDelta[]) fPackageFragmentRootsRemoved.values().toArray(new PackageFragmentRootDelta[fPackageFragmentRootsRemoved.size()]);
					for (int i = 0; i < removes.length; i++) {
						handleElementChanged(removes[i].elementPath, removes[i].deltaKind, removes[i].isExported);
					}
					PackageFragmentRootDelta[] changes = (PackageFragmentRootDelta[]) fPackageFragmentRootsChanged.values().toArray(new PackageFragmentRootDelta[fPackageFragmentRootsChanged.size()]);
					for (int i = 0; i < changes.length; i++) {
						handleElementChanged(changes[i].elementPath, changes[i].deltaKind, changes[i].isExported);
					}
					PackageFragmentRootDelta[] adds = (PackageFragmentRootDelta[]) fPackageFragmentRootsAdded.values().toArray(new PackageFragmentRootDelta[fPackageFragmentRootsAdded.size()]);
					for (int i = 0; i < adds.length; i++) {
						handleElementChanged(adds[i].elementPath, adds[i].deltaKind, adds[i].isExported);
					}
					fPackageFragmentRootsRemoved.clear();
					fPackageFragmentRootsChanged.clear();
					fPackageFragmentRootsAdded.clear();
				}
			}
			finally {
				LOCK.release();
			}
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
					else if (child.getNodeName().equals(JSP12TLDNames.DISPLAY_NAME)) {
						info.displayName = getTextContents(child);
					}
					else if (child.getNodeName().equals(JSP12TLDNames.JSP_VERSION) || child.getNodeName().equals(JSP11TLDNames.JSPVERSION)) {
						info.jspVersion = getTextContents(child);
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
		ensureUpTodate();
		Collection records = null;
		try {
			float jspVersion = DeploymentDescriptorPropertyCache.getInstance().getJSPVersion(path);
			LOCK.acquire();

			Collection implicitReferences = new HashSet(getImplicitReferences(path.toString()).values());
			records = new HashSet(fTLDReferences.size() + fTagDirReferences.size() + fJARReferences.size() + fWebXMLReferences.size());
			records.addAll(fTLDReferences.values());
			if (jspVersion >= 1.1) {
				records.addAll(_getJSP11AndWebXMLJarReferences(fJARReferences.values()));
			}

			if (jspVersion >= 1.2) {
				records.addAll(implicitReferences);

				Map buildPathReferences = new HashMap();
				List projectsProcessed = new ArrayList(fClasspathProjects.size() + 1);
				projectsProcessed.add(fProject);
				addBuildPathReferences(buildPathReferences, projectsProcessed, false);
				records.addAll(buildPathReferences.values());
				
				if(path.segmentCount() > 1) {
					records.addAll(new HashSet(getImplicitReferences(fProject.getFullPath().toString()).values()));
				}
			}
			if (jspVersion >= 2.0) {
				records.addAll(fTagDirReferences.values());
			}
			
			IPath localWebXML = new Path(getLocalRoot(path.toString())).append("/WEB-INF/web.xml"); //$NON-NLS-1$ 
			WebXMLRecord webxmlRecord = (WebXMLRecord) fWebXMLReferences.get(localWebXML.toString());
			if(webxmlRecord != null)
				records.addAll(webxmlRecord.getTLDRecords());

			records.addAll(getCatalogRecords());
		}
		finally {
			LOCK.release();
		}
		return new ArrayList(records);
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
				}
				catch (IOException ioExc) {
					Logger.logException("Taglib Index: " + zipFile.getName(), ioExc); //$NON-NLS-1$
				}

				if (entryInputStream != null) {
					int c;
					ByteArrayOutputStream buffer = null;
					if (zipEntry.getSize() > 0) {
						buffer = new ByteArrayOutputStream((int) zipEntry.getSize());
					}
					else {
						buffer = new ByteArrayOutputStream();
					}
					// array dim restriction?
					byte bytes[] = new byte[2048];
					try {
						while ((c = entryInputStream.read(bytes)) >= 0) {
							buffer.write(bytes, 0, c);
						}
						cache = new ByteArrayInputStream(buffer.toByteArray());
					}
					catch (IOException ioe) {
						// no cleanup can be done
						Logger.log(Logger.ERROR_DEBUG, null, ioe);
					}
					finally {
						try {
							entryInputStream.close();
						}
						catch (IOException e) {
							Logger.log(Logger.ERROR_DEBUG, null, e);
						}
					}
				}
			}
		}

		return cache;
	}

	private Collection getCatalogRecords() {
		if (fCatalogRecords == null) {
			List records = new ArrayList();
			ICatalog defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
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
					ICatalog catalog = nextCatalogs[nextCatalog].getReferencedCatalog();
					ICatalogEntry[] entries2 = catalog.getCatalogEntries();
					for (int entry = 0; entry < entries2.length; entry++) {
						String uri = entries2[entry].getURI();
						if (uri != null) {
							uri = uri.toLowerCase(Locale.US);
							if (uri.endsWith((".jar")) || uri.endsWith((".tld"))) {
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
	 * @return Returns the implicitReferences for the given path
	 */
	Hashtable getImplicitReferences(String path) {
		String localRoot = getLocalRoot(path);
		Hashtable implicitReferences = (Hashtable) fImplicitReferences.get(localRoot);
		if (implicitReferences == null) {
			implicitReferences = new ArrayMap(1);
			fImplicitReferences.put(localRoot, implicitReferences);
		}
		return implicitReferences;
	}

	/**
	 * @param basePath
	 * @return the applicable Web context root path, if one exists
	 * @deprecated - does not support flexible project layouts
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
			Node child = children.item(0);
			if (child.getNodeValue() != null)
				return child.getNodeValue().trim();
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
			else if (child.getNodeValue() != null) {
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

	void handleElementChanged(IPath libraryPath, int deltaKind, boolean exported) {
		IFile file = libraryPath.segmentCount() > 1 ? ResourcesPlugin.getWorkspace().getRoot().getFile(libraryPath) : null;
		String libraryLocation = null;
		if (file != null && file.isAccessible() && file.getLocation() != null)
			libraryLocation = file.getLocation().toString();
		else
			libraryLocation = libraryPath.toString();
		updateClasspathLibrary(libraryLocation, deltaKind, exported);
	}
	
	void handleElementChanged(IJavaElementDelta delta) {
		if (fBuildPathIsDirty) {
			return;
		}

		// Logger.log(Logger.INFO_DEBUG, "IJavaElementDelta: " + delta);
		IJavaElement element = delta.getElement();
		if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
			IJavaElementDelta[] affectedChildren = delta.getAffectedChildren();
			for (int i = 0; i < affectedChildren.length; i++) {
				handleElementChanged(affectedChildren[i]);
			}
		}
		if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT && ((IPackageFragmentRoot) element).isArchive()) {
			time0 = System.currentTimeMillis();
			if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT && ((IPackageFragmentRoot) element).isExternal()) {
			}
			String libLocation = null;
			int taglibRecordEventKind = -1;
			if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) > 0 || (delta.getFlags() & IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED) > 0 || (delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0) {
				taglibRecordEventKind = ITaglibIndexDelta.CHANGED;
				if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) > 0) {
					taglibRecordEventKind = ITaglibIndexDelta.ADDED;
				}
				else if ((delta.getFlags() & IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED) > 0) {
					taglibRecordEventKind = ITaglibIndexDelta.CHANGED;
				}
				else if ((delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0) {
					taglibRecordEventKind = ITaglibIndexDelta.REMOVED;
				}
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(element.getPath());
				if (file.isAccessible() && file.getLocation() != null)
					libLocation = file.getLocation().toString();
				else
					libLocation = element.getPath().toString();
			}
			if (libLocation != null) {
				boolean fragmentisExported = true;
				try {
					IClasspathEntry rawClasspathEntry = ((IPackageFragmentRoot) element).getRawClasspathEntry();
					/*
					 * null may also be returned for deletions depending on
					 * resource/build path notification order. If it's null,
					 * it's been deleted and whether it's exported won't
					 * really matter
					 */
					if (rawClasspathEntry != null) {
						fragmentisExported = rawClasspathEntry.isExported();
					}
				}
				catch (JavaModelException e) {
					// IPackageFragmentRoot not part of the build path
				}
				if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) > 0) {
					fBuildPathEntryCount++;
				}
				else if ((delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0) {
					fBuildPathEntryCount--;
				}
				updateClasspathLibrary(libLocation, taglibRecordEventKind, fragmentisExported);
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
			fProject.accept(new Indexer(), IResource.NONE);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}

		if (_debugIndexTime)
			Logger.log(Logger.INFO, "indexed " + fProject.getName() + " contents in " + (System.currentTimeMillis() - time0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	void indexClasspath(IClasspathEntry[] entries) {
		if (_debugIndexTime)
			time0 = System.currentTimeMillis();
		fClasspathProjects.clear();
		fClasspathReferences.clear();
		fClasspathJars.clear();

		fBuildPathEntryCount = entries.length;
		for (int i = 0; i < entries.length; i++) {
			indexClasspath(entries[i]);
		}

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
				IPath libPath = entry.getPath();
				if (!fClasspathJars.containsKey(libPath.toString())) {
					if (libPath.toFile().exists()) {
						updateClasspathLibrary(libPath.toString(), ITaglibIndexDelta.ADDED, entry.isExported());
					}
					else {
						/*
						 * Note: .jars on the classpath inside of the project
						 * will have duplicate entries in the JAR references
						 * table that will e returned to
						 * getAvailableTaglibRecords().
						 */
						IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(libPath);
						if (resource != null && resource.isAccessible()) {
							if (resource.getType() == IResource.FILE) {
								if (resource.getLocation() != null) {
									updateClasspathLibrary(resource.getLocation().toString(), ITaglibIndexDelta.ADDED, entry.isExported());
								}
							}
							else if (resource.getType() == IResource.FOLDER) {
								try {
									resource.accept(new Indexer(), 0);
								}
								catch (CoreException e) {
									Logger.logException(e);
								}
							}
						}
					}
				}
			}
				break;
			case IClasspathEntry.CPE_PROJECT : {
				/*
				 * We're currently ignoring whether the project exports all of
				 * its build path
				 */
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(entry.getPath().lastSegment());
				if (project != null) {
					fClasspathProjects.add(project);
				}
			}
				break;
			case IClasspathEntry.CPE_SOURCE : {
				IPath path = entry.getPath();
				try {
					IResource sourceFolder = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
					// could be a bad .classpath file
					if(sourceFolder != null) {
						sourceFolder.accept(new Indexer(), 0);
					}
				}
				catch (CoreException e) {
					Logger.logException(e);
				}
			}
				break;
			case IClasspathEntry.CPE_VARIABLE : {
				IPath libPath = JavaCore.getResolvedVariablePath(entry.getPath());
				if (libPath != null) {
					File file = libPath.toFile();

					// file in filesystem
					if (file.exists() && !file.isDirectory()) {
						updateClasspathLibrary(libPath.toString(), ITaglibRecordEvent.ADDED, entry.isExported());
					}
					else {
						// workspace file
						IFile jarFile = ResourcesPlugin.getWorkspace().getRoot().getFile(libPath);
						if (jarFile.isAccessible() && jarFile.getType() == IResource.FILE && jarFile.getLocation() != null) {
							String jarPathString = jarFile.getLocation().toString();
							updateClasspathLibrary(jarPathString, ITaglibRecordEvent.ADDED, entry.isExported());
						}
					}
				}
			}
				break;
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
	 * ITaglibIndexDelta.REMOVED)); } }
	 */
	
	void queueElementChanged(IJavaElementDelta delta) {
		try {
			LOCK.acquire();
			IJavaElement element = delta.getElement();
			if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
				IJavaElementDelta[] affectedChildren = delta.getAffectedChildren();
				for (int i = 0; i < affectedChildren.length; i++) {
					queueElementChanged(affectedChildren[i]);
				}
			}
			if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT && ((IPackageFragmentRoot) element).isArchive()) {
				IPath path = element.getPath();
				boolean fragmentisExported = true;
				try {
					IClasspathEntry rawClasspathEntry = ((IPackageFragmentRoot) element).getRawClasspathEntry();
					/*
					 * null may also be returned for deletions depending on
					 * resource/build path notification order. If it's null,
					 * it's been deleted and whether it's exported won't
					 * really matter
					 */
					if (rawClasspathEntry != null) {
						fragmentisExported = rawClasspathEntry.isExported();
					}
				}
				catch (JavaModelException e) {
					// IPackageFragmentRoot not part of the build path
				}
				String key = path.toString();
				if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) > 0) {
					fPackageFragmentRootsAdded.put(key, new PackageFragmentRootDelta(path, ITaglibIndexDelta.ADDED, fragmentisExported));
					fPackageFragmentRootsChanged.remove(key);
					fPackageFragmentRootsRemoved.remove(key);
					fBuildPathEntryCount++;
				}
				else if ((delta.getFlags() & IJavaElementDelta.F_ARCHIVE_CONTENT_CHANGED) > 0) {
					fPackageFragmentRootsChanged.put(key, new PackageFragmentRootDelta(path, ITaglibIndexDelta.CHANGED, fragmentisExported));
				}
				else if ((delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) > 0) {
					fPackageFragmentRootsAdded.remove(key);
					fPackageFragmentRootsChanged.remove(key);
					fPackageFragmentRootsRemoved.put(key, new PackageFragmentRootDelta(path, ITaglibIndexDelta.REMOVED, fragmentisExported));
					fBuildPathEntryCount--;
				}
			}
		}
		finally {
			LOCK.release();
		}

		fBuildPathJob.cancel();
		fBuildPathJob.schedule(2000);
	}


	private String readTextofChild(Node node, String childName) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(childName)) {
				return getTextContents(child);
			}
		}
		return EMPTY_STRING; //$NON-NLS-1$
	}

	void removeJAR(IResource jar) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "removing records for JAR " + jar.getFullPath()); //$NON-NLS-1$
		JarRecord record = (JarRecord) fJARReferences.remove(jar.getFullPath().toString());
		if (record != null) {
			URLRecord[] records = (URLRecord[]) record.getURLRecords().toArray(new URLRecord[0]);
			for (int i = 0; i < records.length; i++) {
				TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, records[i], ITaglibIndexDelta.REMOVED));
				((ArrayMap) getImplicitReferences(jar.getFullPath().toString())).remove(records[i].getURI(), records[i]);
			}
			if (record.has11TLD) {
				TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, ITaglibIndexDelta.REMOVED));
			}
		}
	}

	void removeTag(IResource resource) {
		TagDirRecord record = (TagDirRecord) fTagDirReferences.get(resource.getParent().getFullPath().toString());
		if (record != null) {
			record.tags.remove(resource.getName());
			TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, ITaglibIndexDelta.CHANGED));
		}
	}

	void removeTagDir(IResource tagdir) {
		IPath tagdirPath = tagdir.getFullPath();
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "removing record for " + tagdirPath); //$NON-NLS-1$
		ITaglibRecord record = (ITaglibRecord) fTagDirReferences.remove(tagdirPath.toString());
		if (record != null) {
			TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, ITaglibIndexDelta.REMOVED));
		}
	}

	void removeTLD(IResource tld) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "removing record for " + tld.getFullPath()); //$NON-NLS-1$
		TLDRecord record = (TLDRecord) fTLDReferences.remove(tld.getFullPath().toString());
		if (record != null) {
			if (record.getURI() != null) {
				((ArrayMap) getImplicitReferences(tld.getFullPath().toString())).remove(record.getURI(), record);
			}
			TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, ITaglibIndexDelta.REMOVED));
		}
	}

	void removeWebXML(IResource webxml) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "removing records for " + webxml.getFullPath()); //$NON-NLS-1$
		WebXMLRecord record = (WebXMLRecord) fWebXMLReferences.remove(webxml.getFullPath().toString());
		if (record != null) {
			TLDRecord[] records = (TLDRecord[]) record.getTLDRecords().toArray(new TLDRecord[0]);
			for (int i = 0; i < records.length; i++) {
				if (_debugIndexCreation)
					Logger.log(Logger.INFO, "removed record for " + records[i].getURI() + "@" + records[i].path); //$NON-NLS-1$ //$NON-NLS-2$
				((ArrayMap) getImplicitReferences(webxml.getFullPath().toString())).remove(records[i].getURI(), records[i]);
				TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, records[i], ITaglibIndexDelta.REMOVED));
			}
		}
	}
	
	private boolean requestedRefresh() {
		boolean requested = false;
		String[] commandLineArgs = Platform.getCommandLineArgs();
		for (int i = 0; i < commandLineArgs.length; i++) {
			requested = requested || "-refresh".equals(commandLineArgs[i]); //$NON-NLS-1$ 
		}
		return requested;
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
		try {
			float jspVersion = DeploymentDescriptorPropertyCache.getInstance().getJSPVersion(new Path(basePath));

			/**
			 * http://bugs.eclipse.org/196177 - Support resolution in flexible
			 * projects
			 */
			IPath resourcePath = FacetModuleCoreSupport.resolve(new Path(basePath), reference);
			if (resourcePath.segmentCount() > 1) {
				String fileExtension = resourcePath.getFileExtension();
				if (fileExtension != null && fileExtension.toLowerCase(Locale.US).equals("tld")) { //$NON-NLS-1$ 
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(resourcePath);
					if (file.isAccessible()) {
						path = resourcePath.toString();
					}
				}
				else if (fileExtension != null && fileExtension.toLowerCase(Locale.US).equals("jar")) { //$NON-NLS-1$ 
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(resourcePath);
					if (file.isAccessible()) {
						path = resourcePath.toString();
					}
				}
				else {
					IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(resourcePath);
					if (folder.isAccessible()) {
						path = resourcePath.toString();
					}
				}
			}

			LOCK.acquire();

			String localRoot = getLocalRoot(basePath);
			/**
			 * Workaround for problem in URIHelper; uris starting with '/' are
			 * returned as-is.
			 */
			if (path == null) {
				if (reference.startsWith("/")) { //$NON-NLS-1$
					path = localRoot + reference;
				}
				else {
					path = URIHelper.normalize(reference, basePath, localRoot);
				}
			}

			IPath localWebXML = new Path(localRoot).append("/WEB-INF/web.xml"); //$NON-NLS-1$ 
			WebXMLRecord webxmlRecord = (WebXMLRecord) fWebXMLReferences.get(localWebXML.toString());
			if (webxmlRecord != null) {
				for (int i = 0; i < webxmlRecord.tldRecords.size(); i++) {
					ITaglibRecord record2 = (ITaglibRecord) webxmlRecord.tldRecords.get(i);
					ITaglibDescriptor descriptor = record2.getDescriptor();
					if (reference.equals(descriptor.getURI())) {
						record = record2;
					}
				}
			}

			if (record == null) {
				// order dictated by JSP spec 2.0 section 7.2.3
				record = (ITaglibRecord) fJARReferences.get(path);

				// only if 1.1 TLD was found
				if (jspVersion < 1.1 || (record instanceof JarRecord && !((JarRecord) record).has11TLD)) {
					record = null;
				}
			}
			
			if (record == null) {
				record = (ITaglibRecord) fTLDReferences.get(path);
			}
			if (record == null && jspVersion >= 1.2) {
				Object[] records = (Object[]) getImplicitReferences(basePath).get(reference);
				if (records != null && records.length > 0) {
					if (records.length > 1)
						records = fTaglibSorter.sort(records);
					record =  (ITaglibRecord) records[records.length - 1];
				}
			}


			if (record == null && jspVersion >= 2.0) {
				record = (ITaglibRecord) fTagDirReferences.get(path);
			}

			if (record == null && jspVersion >= 1.2) {
				record = (ITaglibRecord) fClasspathReferences.get(reference);
			}
			if (record == null && jspVersion >= 1.2) {
				Map buildPathReferences = new HashMap();
				List projectsProcessed = new ArrayList(fClasspathProjects.size() + 1);
				projectsProcessed.add(fProject);
				addBuildPathReferences(buildPathReferences, projectsProcessed, false);
				record = (ITaglibRecord) buildPathReferences.get(reference);
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
						record = createCatalogRecord(reference, resolvedString);
					}
				}
			}
			
			/*
			 * If no records were found and no local-root applies, check ALL
			 * of the web.xml files as a fallback
			 */
			if (record == null && fProject.getFullPath().toString().equals(localRoot)) {
				WebXMLRecord[] webxmls = (WebXMLRecord[]) fWebXMLReferences.values().toArray(new WebXMLRecord[0]);
				for (int i = 0; i < webxmls.length; i++) {
					if (record != null)
						continue;
					Object[] records = (Object[]) getImplicitReferences(webxmls[i].path.toString()).get(reference);
					if (records != null && records.length > 0) {
						if (records.length > 1)
							records = fTaglibSorter.sort(records);
						record =  (ITaglibRecord) records[records.length - 1];
					}
				}
			}
			/*
			 * If no records were found, check the implicit references on the project itself as a fallback
			 */
			if (record == null && jspVersion >= 1.2) {
				Object[] records = (Object[]) getImplicitReferences(fProject.getFullPath().toString()).get(reference);
				if (records != null && records.length > 0) {
					if (records.length > 1)
						records = fTaglibSorter.sort(records);
					record =  (ITaglibRecord) records[records.length - 1];
				}
			}

		}
		finally {
			LOCK.release();
		}

		return record;
	}

	/**
	 * Restores any saved reference tables
	 */
	private void restoreReferences() {
		final boolean notifyOnRestoration = true;
		if (TaglibIndex.ENABLED) {
			// resources first
			index();
			// now build path

			// ================ test reload time ========================
			boolean restored = false;
			File savedState = new File(fSaveStateFilename);
			if (savedState.exists() && !requestedRefresh()) {
				Reader reader = null;
				try {
					time0 = System.currentTimeMillis();
					reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(savedState)), "UTF-16"); //$NON-NLS-1$ 
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
						String lineText = doc.get(line.getOffset(), line.getLength());
						JarRecord libraryRecord = null;
						if (SAVE_FORMAT_VERSION.equals(lineText.trim())) {
							IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

							for (int i = 1; i < lines && !fBuildPathIsDirty; i++) {
								line = doc.getLineInformation(i);
								lineText = doc.get(line.getOffset(), line.getLength());
								StringTokenizer toker = new StringTokenizer(lineText, "|"); //$NON-NLS-1$
								if (toker.hasMoreTokens()) {
									String tokenType = toker.nextToken();
									if ("JAR".equalsIgnoreCase(tokenType)) { //$NON-NLS-1$ //$NON-NLS-2$
										boolean has11TLD = Boolean.valueOf(toker.nextToken()).booleanValue();
										boolean exported = Boolean.valueOf(toker.nextToken()).booleanValue();
										// make the rest the libraryLocation
										String libraryLocation = toker.nextToken();
										while (toker.hasMoreTokens()) {
											libraryLocation = libraryLocation + "|" + toker.nextToken(); //$NON-NLS-1$ //$NON-NLS-2$
										}
										libraryLocation = libraryLocation.trim();
										if (libraryRecord != null && notifyOnRestoration) {
											TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, libraryRecord, ITaglibIndexDelta.ADDED));
										}
										// Create a new JarRecord
										libraryRecord = createJARRecord(libraryLocation);
										libraryRecord.has11TLD = has11TLD;
										libraryRecord.isExported = exported;

										// Add a URLRecord for the 1.1 TLD
										if (has11TLD) {
											InputStream contents = JarUtilities.getInputStream(libraryLocation, JarUtilities.JSP11_TAGLIB);
											if (contents != null) {
												TaglibInfo info = extractInfo(libraryLocation, contents);

												if (info != null && info.uri != null && info.uri.length() > 0) {
													URLRecord urlRecord = new URLRecord();
													urlRecord.info = info;
													urlRecord.isExported = exported;
													urlRecord.baseLocation = libraryLocation;
													try {
														urlRecord.url = new URL("jar:file:" + libraryLocation + "!/" + JarUtilities.JSP11_TAGLIB); //$NON-NLS-1$ //$NON-NLS-2$
														libraryRecord.urlRecords.add(urlRecord);
														fClasspathReferences.put(urlRecord.getURI(), urlRecord);
														if (_debugIndexCreation)
															Logger.log(Logger.INFO, "created record for " + urlRecord.getURI() + "@" + urlRecord.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
													}
													catch (MalformedURLException e) {
														/*
														 * don't record this
														 * URI
														 */
														Logger.logException(e);
													}
												}
												try {
													contents.close();
												}
												catch (IOException e) {
													Logger.log(Logger.ERROR_DEBUG, null, e);
												}
											}
										}

										fClasspathJars.put(libraryLocation, libraryRecord);
									}
									else if ("URL".equalsIgnoreCase(tokenType) && libraryRecord != null) { //$NON-NLS-1$
										// relies on a previously declared JAR record
										boolean exported = Boolean.valueOf(toker.nextToken()).booleanValue();
										// make the rest the URL
										String urlString = toker.nextToken();
										while (toker.hasMoreTokens()) {
											urlString = urlString + "|" + toker.nextToken(); //$NON-NLS-1$ //$NON-NLS-2$
										}
										urlString = urlString.trim();
										// Append a URLrecord
										URLRecord urlRecord = new URLRecord();
										urlRecord.url = new URL(urlString);
										urlRecord.isExported = exported;
										urlRecord.baseLocation = libraryRecord.location.toString();

										InputStream tldStream = JarUtilities.getInputStream(urlRecord.url);
										if(tldStream != null) {
											TaglibInfo info = extractInfo(urlRecord.url.toString(), tldStream);
											if (info != null) {
												urlRecord.info = info;
											}
											libraryRecord.urlRecords.add(urlRecord);
											try {
												tldStream.close();
											}
											catch (IOException e) {
												Logger.log(Logger.ERROR_DEBUG, null, e);
											}
											if (urlRecord.getURI() != null && urlRecord.getURI().length() > 0) {
												fClasspathReferences.put(urlRecord.getURI(), urlRecord);
											}
										}
									}
									else if (BUILDPATH_PROJECT.equalsIgnoreCase(tokenType)) {
										String projectName = toker.nextToken();
										if (Path.ROOT.isValidSegment(projectName)) {
											IProject project = workspaceRoot.getProject(projectName);
											/* do not check if "open" here */
											if (project != null) {
												fClasspathProjects.add(project);
											}
										}
									}
									// last since they occur once
									else if (BUILDPATH_DIRTY.equalsIgnoreCase(tokenType)) {
										fBuildPathIsDirty = Boolean.valueOf(toker.nextToken()).booleanValue();
									}
									else if (BUILDPATH_ENTRIES.equalsIgnoreCase(tokenType)) {
										fBuildPathEntryCount = Integer.valueOf(toker.nextToken()).intValue();
									}
								}
								if (libraryRecord != null && notifyOnRestoration) {
									TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, libraryRecord, ITaglibIndexDelta.ADDED));
								}
							}
							restored = true;
						}
						else {
							Logger.log(Logger.INFO_DEBUG, "Tag Library Index: different cache format found, was \"" + lineText + "\", supports \"" + SAVE_FORMAT_VERSION + "\", reindexing build path"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
					if (_debugIndexTime)
						Logger.log(Logger.INFO, "time spent reloading " + fProject.getName() + " build path: " + (System.currentTimeMillis() - time0)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch (Exception e) {
					restored = false;
					if (_debugIndexTime)
						Logger.log(Logger.INFO, "failure reloading " + fProject.getName() + " build path index", e); //$NON-NLS-1$ //$NON-NLS-2$
				}
				finally {
					if (reader != null) {
						try {
							reader.close();
						}
						catch (IOException e) {
							Logger.log(Logger.ERROR_DEBUG, null, e);
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
		 *                     		 1.1.0
		 *                     		 Save classpath information (| is field delimiter)
		 *                     		 Jars are saved as &quot;JAR:&quot;+ has11TLD + jar path 
		 *                     		 URLRecords as &quot;URL:&quot;+URL
		 * </pre>
		 */
		try {
			writer = new OutputStreamWriter(new FileOutputStream(fSaveStateFilename), "UTF-16"); //$NON-NLS-1$
			writer.write(SAVE_FORMAT_VERSION);
			writer.write('\n'); //$NON-NLS-1$
			writer.write(BUILDPATH_DIRTY + "|" + fBuildPathIsDirty); //$NON-NLS-1$
			writer.write('\n'); //$NON-NLS-1$
			writer.write(BUILDPATH_ENTRIES + "|" + fBuildPathEntryCount); //$NON-NLS-1$
			writer.write('\n'); //$NON-NLS-1$

			IProject[] projects = (IProject[]) fClasspathProjects.toArray(new IProject[0]);
			for (int i = 0; i < projects.length; i++) {
				writer.write(BUILDPATH_PROJECT);
				writer.write("|"); //$NON-NLS-1$
				writer.write(projects[i].getName());
				writer.write('\n'); //$NON-NLS-1$
			}

			Enumeration jars = fClasspathJars.keys();
			while (jars.hasMoreElements()) {
				String jarPath = jars.nextElement().toString();
				JarRecord jarRecord = (JarRecord) fClasspathJars.get(jarPath);
				writer.write("JAR|"); //$NON-NLS-1$
				writer.write(Boolean.toString(jarRecord.has11TLD));
				writer.write('|'); //$NON-NLS-1$
				writer.write(Boolean.toString(jarRecord.isExported));
				writer.write('|'); //$NON-NLS-1$
				writer.write(jarPath);
				writer.write('\n'); //$NON-NLS-1$
				Iterator i = jarRecord.urlRecords.iterator();
				while (i.hasNext()) {
					URLRecord urlRecord = (URLRecord) i.next();
					writer.write("URL|"); //$NON-NLS-1$
					writer.write(String.valueOf(urlRecord.isExported));
					writer.write("|"); //$NON-NLS-1$
					writer.write(urlRecord.getURL().toExternalForm());
					writer.write('\n'); //$NON-NLS-1$
				}
			}
		}
		catch (IOException e) {
			Logger.log(Logger.ERROR_DEBUG, null, e);
		}
		finally {
			try {
				if (writer != null) {
					writer.close();
				}
			}
			catch (Exception e) {
				Logger.log(Logger.ERROR_DEBUG, null, e);
			}
		}

		if (_debugIndexTime)
			Logger.log(Logger.INFO, "time spent saving index for " + fProject.getName() + ": " + (System.currentTimeMillis() - time0)); //$NON-NLS-1$
	}

	void setBuildPathIsDirty() {
		fBuildPathIsDirty = true;
		if (_debugIndexTime)
			Logger.log(Logger.INFO, "marking build path information for " + fProject.getName() + " as dirty"); //$NON-NLS-1$
	}

	/**
	 * Update records for a library on the project build path
	 * @param libraryLocation
	 * @param deltaKind
	 * @param isExported
	 */
	void updateClasspathLibrary(String libraryLocation, int deltaKind, boolean isExported) {
		JarRecord libraryRecord = null;
		if (deltaKind == ITaglibIndexDelta.REMOVED || deltaKind == ITaglibIndexDelta.CHANGED) {
			libraryRecord = (JarRecord) fClasspathJars.remove(libraryLocation);
			synchronized (fSharedJarRecords) {
				fSharedJarRecords.remove(libraryLocation);
			}
			if (libraryRecord != null) {
				IURLRecord[] urlRecords = (IURLRecord[]) libraryRecord.urlRecords.toArray(new IURLRecord[0]);
				for (int i = 0; i < urlRecords.length; i++) {
					ITaglibRecord record = (ITaglibRecord) fClasspathReferences.remove(urlRecords[i].getURI());
					if (record != null) {
						TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, ITaglibIndexDelta.REMOVED));
					}
				}
			}
		}
		if (deltaKind == ITaglibIndexDelta.ADDED || deltaKind == ITaglibIndexDelta.CHANGED) {
			// XXX: runs on folders as well?!
			libraryRecord = createJARRecord(libraryLocation);
			synchronized (libraryRecord) {
				if (libraryRecord.isConsistent) {
					// Library loaded by another Project Description, initialize our references from the existing
					fClasspathJars.put(libraryLocation, libraryRecord);
					Iterator records = libraryRecord.urlRecords.iterator();
					while (records.hasNext()) {
						URLRecord record = (URLRecord)records.next();
						int urlDeltaKind = ITaglibIndexDelta.ADDED;
						if (fClasspathReferences.containsKey(record.getURI())) {
							urlDeltaKind = ITaglibIndexDelta.CHANGED;
						}
						fClasspathReferences.put(record.getURI(), record);
						TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, urlDeltaKind));
						fClasspathReferences.put(record.info.uri, record);
					}
					return;
				}
				libraryRecord.isExported = isExported;
				fClasspathJars.put(libraryLocation, libraryRecord);
	
				ZipFile jarfile = null;
				try {
					jarfile = new ZipFile(libraryLocation);
					Enumeration entries = jarfile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry z = (ZipEntry) entries.nextElement();
						if (!z.isDirectory()) {
							if (isTLD(z.getName())) {
								if (z.getName().equals(JarUtilities.JSP11_TAGLIB)) {
									libraryRecord.has11TLD = true;
								}
								InputStream contents = getCachedInputStream(jarfile, z);
								if (contents != null) {
									TaglibInfo info = extractInfo(libraryLocation, contents);
	
									if (info != null && info.uri != null && info.uri.length() > 0) {
										URLRecord urlRecord = new URLRecord();
										urlRecord.info = info;
										urlRecord.baseLocation = libraryLocation;
										try {
											urlRecord.isExported = isExported;
											urlRecord.url = new URL("jar:file:" + libraryLocation + "!/" + z.getName()); //$NON-NLS-1$ //$NON-NLS-2$
											libraryRecord.urlRecords.add(urlRecord);
											int urlDeltaKind = ITaglibIndexDelta.ADDED;
											if (fClasspathReferences.containsKey(urlRecord.getURI())) {
												// TODO: not minimized enough
												urlDeltaKind = ITaglibIndexDelta.CHANGED;
											}
											fClasspathReferences.put(urlRecord.getURI(), urlRecord);
											TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, urlRecord, urlDeltaKind));
											fClasspathReferences.put(info.uri, urlRecord);
											if (_debugIndexCreation)
												Logger.log(Logger.INFO, "created record for " + urlRecord.getURI() + "@" + urlRecord.getURL()); //$NON-NLS-1$ //$NON-NLS-2$
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
										Logger.log(Logger.ERROR_DEBUG, null, e);
									}
								}
							}
						}
					}
				}
				catch (ZipException zExc) {
					Logger.log(Logger.WARNING, "Taglib Index ZipException: " + libraryLocation + " " + zExc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				catch (IOException ioExc) {
					Logger.log(Logger.WARNING, "Taglib Index IOException: " + libraryLocation + " " + ioExc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				finally {
					closeJarFile(jarfile);
				}
				libraryRecord.isConsistent = true;
			}
		}
		if (libraryRecord != null) {
			TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, libraryRecord, deltaKind));
		}
	}

	private boolean isTLD(String name) {
		if (name == null)
			return false;

		final int length = name.length();

		if (length < 4)
			return false;
		if (name.charAt(length - 4) != '.')
			return false;

		for (int i = length - 3, j = 0; i < length; i++, j++) {
			final char c = name.charAt(i);
			if (c != TLD[2*j] && c != TLD[2*j + 1])
				return false;
		}
		return true;
	}

	void updateJAR(IResource jar, int deltaKind) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "creating records for JAR " + jar.getFullPath()); //$NON-NLS-1$

		String jarLocationString = null;
		if (jar.getLocation() != null)
			jarLocationString = jar.getLocation().toString();
		else
			jarLocationString = jar.getLocationURI().toString();
		JarRecord jarRecord = createJARRecord(jar);
		fJARReferences.put(jar.getFullPath().toString(), jarRecord);
		ZipInputStream zip = getZipInputStream(jar);
		if(zip != null) {
			try {
				ZipEntry entry;
				while ((entry = zip.getNextEntry()) != null) {
					if (isTLD(entry.getName())) { //$NON-NLS-1$
						if (entry.getName().equals(JarUtilities.JSP11_TAGLIB)) {
							jarRecord.has11TLD = true;
						}
						InputStream contents = copyZipEntry(zip);
						if (contents != null) {
							TaglibInfo info = extractInfo(jarLocationString, contents);
		
							if (info != null && info.uri != null && info.uri.length() > 0) {
								URLRecord record = new URLRecord();
								record.info = info;
								record.baseLocation = jarLocationString;
								try {
									record.url = new URL("jar:file:" + jarLocationString + "!/" + entry.getName()); //$NON-NLS-1$ //$NON-NLS-2$
									jarRecord.urlRecords.add(record);
		
									int taglibDeltaKind = ITaglibIndexDelta.ADDED;
									Hashtable table = getImplicitReferences(jar.getFullPath().toString());
									if (table != null && table.get(record.getURI()) != null) {
										taglibDeltaKind = ITaglibIndexDelta.CHANGED;
									}
		
									getImplicitReferences(jar.getFullPath().toString()).put(info.uri, record);
									TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, taglibDeltaKind));
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
								Logger.log(Logger.ERROR_DEBUG, null, e);
							}
						}
						else {
							Logger.log(Logger.ERROR_DEBUG, getClass().getName() + "could not read resource " + jar.getFullPath()); //$NON-NLS-1$
						}
					}
				}
			} catch (IOException e) { }
			finally {
				closeInputStream(zip);
			}
		}
		if (jarRecord.has11TLD) {
			TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, jarRecord, deltaKind));
		}
	}

	private InputStream copyZipEntry(ZipInputStream stream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream copy = null;

		if (stream != null) {
			int c;
			// array dim restriction?
			byte bytes[] = new byte[2048];
			try {
				while ((c = stream.read(bytes)) >= 0) {
					buffer.write(bytes, 0, c);
				}
				copy = new ByteArrayInputStream(buffer.toByteArray());
				closeZipEntry(stream);
			}
			catch (IOException ioe) {
				// no cleanup can be done
			}
		}
		return copy;
	}

	private ZipInputStream getZipInputStream(IResource jar) {
		if (jar == null || jar.getType() != IResource.FILE || !jar.isAccessible())
			return null;

		try {
			InputStream zipStream = ((IFile) jar).getContents();
			return new ZipInputStream(zipStream);
		} catch (CoreException e) { }
		return null;
	}

	private void closeInputStream(InputStream stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) { }
		}
			
	}

	private void closeZipEntry(ZipInputStream zis) {
		if (zis != null) {
			try {
				zis.closeEntry();
			} catch (IOException e) {
				System.out.println("Error");
			}
		}
			
	}

	void updateTag(IResource resource, int kind) {
		TagDirRecord record = (TagDirRecord) fTagDirReferences.get(resource.getParent().getFullPath().toString());
		if (record == null) {
			record = createTagdirRecord((IFolder) resource.getParent());
			fTagDirReferences.put(resource.getParent().getFullPath().toString(), record);
			TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, ITaglibIndexDelta.ADDED));
		}
		else {
			if (!record.tags.contains(resource.getName())) {
				record.tags.add(resource.getName());
			}
			TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, ITaglibIndexDelta.CHANGED));
		}
	}

	void updateTagDir(IResource tagdirResource, int deltaKind) {
		/**
		 * 8.4.1: tag files are loose files under /WEB-INF/tags
		 */
		if ((tagdirResource.getType() & IResource.FOLDER) != 0) {
			if (_debugIndexCreation)
				Logger.log(Logger.INFO, "creating record for directory " + tagdirResource.getFullPath()); //$NON-NLS-1$
			TagDirRecord record = (TagDirRecord) fTagDirReferences.get(tagdirResource.getFullPath().toString());
			if (record == null) {
				record = createTagdirRecord((IFolder) tagdirResource);
				fTagDirReferences.put(tagdirResource.getFullPath().toString(), record);
				TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, deltaKind));
			}
			else {

			}
		}
		/**
		 * 8.4.1: tag files can also be packaged in the /META-INF/tags folder
		 * of a jar in /WEB-INF/lib/ (8.4.2: but must be mentioned in a .tld)
		 */
		else {
			// these tags are merely surfaced when the TLD is modelled
		}
	}

	/**
	 * 
	 * @param tld
	 * @param deltaKind
	 */
	void updateTLD(IResource tld, int deltaKind) {
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "creating record for " + tld.getFullPath()); //$NON-NLS-1$
		TLDRecord record = createTLDRecord(tld);
		fTLDReferences.put(tld.getFullPath().toString(), record);
		if (record.getURI() != null && record.getURI().length() > 0) {
			getImplicitReferences(tld.getFullPath().toString()).put(record.getURI(), record);
		}
		TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, deltaKind));
	}

	void updateWebXML(IResource webxml, int deltaKind) {
		if (webxml.getType() != IResource.FILE)
			return;
		InputStream webxmlContents = null;
		Document document = null;
		try {
			webxmlContents = ((IFile) webxml).getContents(false);
			DocumentProvider provider = new DocumentProvider();
			provider.setInputStream(webxmlContents);
			provider.setValidating(false);
			provider.setRootElementName("web-app"); //$NON-NLS-1$
			provider.setBaseReference(webxml.getParent().getFullPath().toString());
			document = provider.getDocument(false);
		}
		catch (CoreException e) {
			Logger.log(Logger.ERROR_DEBUG, EMPTY_STRING, e); //$NON-NLS-1$
		}
		finally {
			if (webxmlContents != null)
				try {
					webxmlContents.close();
				}
				catch (IOException e1) {
					// ignore
					Logger.log(Logger.ERROR_DEBUG, null, e1);
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
			// specified location is relative to root of the web-app
			String taglibLocation = readTextofChild(taglibs.item(iTaglib), "taglib-location").trim(); //$NON-NLS-1$
			IPath path = null;
			if (taglibLocation.startsWith("/")) { //$NON-NLS-1$
				path = FacetModuleCoreSupport.resolve(new Path(webxml.getFullPath().toString()), taglibLocation);
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
					if ("jar".equalsIgnoreCase(resource.getFileExtension())) { //$NON-NLS-1$
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
						record = jarRecord;
						// the stored URI should reflect the web.xml's value
						if (jarRecord.info == null) {
							jarRecord.info = new TaglibInfo();
						}
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
					if (record != null) {
						webxmlRecord.tldRecords.add(record);
						getImplicitReferences(webxml.getFullPath().toString()).put(taglibUri, record);
						TaglibIndex.getInstance().addDelta(new TaglibIndexDelta(fProject, record, deltaKind));
					}
				}
			}
		}
	}
}
