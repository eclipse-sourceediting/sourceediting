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
package org.eclipse.jst.jsp.core.internal.java.search;

import java.io.File;
import java.util.zip.CRC32;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.JSP2ServletNameUtil;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;

/**
 * Central access to java indexing and search. All contact between JDT indexing
 * and Searching should be done through here.
 * 
 * Clients should access the methods of this class via the single instance via
 * <code>getInstance()</code>.
 * 
 * @author pavery
 */
public class JSPSearchSupport {

    // for debugging
    static final boolean DEBUG;
    static {
        String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspsearch"); //$NON-NLS-1$
        DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
    }

    private static JSPSearchSupport singleton = null;

    private JSPSearchParticipant fParticipant = null;

    private IPath fJspPluginLocation = null;

    // pa_TODO may be slow (esp for indexing entire workspace)
    private final CRC32 fChecksumCalculator = new CRC32();

    /** main cancel montior for all search support */
    private final IProgressMonitor fMonitor = new NullProgressMonitor();

    private JSPSearchSupport() {
        // force use of single instance
    }

    /**
     * This operation ensures that the live resource's search markers show up in
     * the open editor. It also allows the ability to pass in a ProgressMonitor
     */
    private class SearchJob extends Job implements IJavaSearchConstants {

        String fSearchText = ""; //$NON-NLS-1$

        IJavaSearchScope fScope = null;

        int fSearchFor = FIELD;

        int fLimitTo = ALL_OCCURRENCES;

        int fMatchMode = SearchPattern.R_PATTERN_MATCH;

        SearchRequestor fRequestor = null;

        IJavaElement fElement = null;

        // constructor w/ java element
        public SearchJob(IJavaElement element, IJavaSearchScope scope, SearchRequestor requestor) {

            super(JSPCoreMessages.JSP_Search + element.getElementName());
            this.fElement = element;
            this.fScope = scope;
            this.fRequestor = requestor;
        }

        // constructor w/ search text
        public SearchJob(String searchText, IJavaSearchScope scope, int searchFor, int limitTo, int matchMode, boolean isCaseSensitive, SearchRequestor requestor) {

            super(JSPCoreMessages.JSP_Search + searchText);
            this.fSearchText = searchText;
            this.fScope = scope;
            this.fSearchFor = searchFor;
            this.fLimitTo = limitTo;
            this.fMatchMode = matchMode;
            this.fRequestor = requestor;
        }

        public IStatus run(IProgressMonitor jobMonitor) {

            if (jobMonitor != null && jobMonitor.isCanceled())
                return Status.CANCEL_STATUS;
            if (JSPSearchSupport.getInstance().isCanceled())
                return Status.CANCEL_STATUS;

            SearchPattern javaSearchPattern = null;
            // if an element is available, use that to create search pattern
            // (eg. LocalVariable)
            // otherwise use the text and other paramters
            if (this.fElement != null)
                javaSearchPattern = SearchPattern.createPattern(this.fElement, this.fLimitTo);
            else
                javaSearchPattern = SearchPattern.createPattern(this.fSearchText, this.fSearchFor, this.fLimitTo, this.fMatchMode);

            if (javaSearchPattern != null) {
                JSPSearchParticipant[] participants = { getSearchParticipant() };
                SearchEngine engine = new SearchEngine();
                try {
                    if (jobMonitor != null)
                        jobMonitor.beginTask("", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                    engine.search(javaSearchPattern, participants, this.fScope, this.fRequestor, jobMonitor);
                } catch (CoreException e) {
                    if (DEBUG)
                        Logger.logException(e);
                }
                // non-CoreExceptions will permanently stall the Worker thread
                catch (Exception e) {
                    if (DEBUG)
                        Logger.logException(e);
                } finally {
                    if (jobMonitor != null)
                        jobMonitor.done();
                }
            }
            return Status.OK_STATUS;
        }
    }

    // end SearchJob
    /**
     * Runnable forces caller to wait until finished (as opposed to using a Job)
     */
    private class SearchRunnable implements IWorkspaceRunnable, IJavaSearchConstants {

        String fSearchText = ""; //$NON-NLS-1$

        IJavaSearchScope fScope = null;

        int fSearchFor = FIELD;

        int fLimitTo = ALL_OCCURRENCES;

        int fMatchMode = SearchPattern.R_PATTERN_MATCH;

        SearchRequestor fRequestor = null;

        IJavaElement fElement = null;

        // constructor w/ java element
        public SearchRunnable(IJavaElement element, IJavaSearchScope scope, SearchRequestor requestor) {

            this.fElement = element;
            this.fScope = scope;
            this.fRequestor = requestor;
        }

        public void run(IProgressMonitor monitor) throws CoreException {

            if (monitor != null && monitor.isCanceled())
                return;
            if (JSPSearchSupport.getInstance().isCanceled())
                return;

            SearchPattern javaSearchPattern = null;
            // if an element is available, use that to create search pattern
            // (eg. LocalVariable)
            // otherwise use the text and other paramters
            if (this.fElement != null)
                javaSearchPattern = SearchPattern.createPattern(this.fElement, fLimitTo);
            else
                javaSearchPattern = SearchPattern.createPattern(fSearchText, fSearchFor, fLimitTo, fMatchMode);

            if (javaSearchPattern != null) {
                JSPSearchParticipant[] participants = { getSearchParticipant() };
                SearchEngine engine = new SearchEngine();
                try {
                    if (monitor != null)
                        monitor.beginTask("", 0); //$NON-NLS-1$
                    engine.search(javaSearchPattern, participants, fScope, fRequestor, monitor);
                } catch (CoreException e) {
                    Logger.logException(e);
                    //throw e;
                }
                // non-CoreExceptions will permanently stall the Worker thread
                catch (Exception e) {
                    Logger.logException(e);
                } finally {
                    if (monitor != null)
                        monitor.done();
                }
            }
        }
    }

    // end SearchRunnable

    /**
     * Clients should access the methods of this class via the single instance
     * via getInstance()
     * 
     * @return
     */
    public synchronized static JSPSearchSupport getInstance() {

        if (singleton == null)
            singleton = new JSPSearchSupport();
        return singleton;
    }

    /**
     * Utility method to check if a file is a jsp file (since this is done
     * frequently)
     */
    public static boolean isJsp(IFile file) {
    	// (pa) 20051025 removing deep content type check
    	// because this method is called frequently
    	// and IO is expensive
        boolean isJsp = false;

        if (file != null && file.exists()) {
        	
            IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
            // check this before description, it's less expensive
            if (contentTypeJSP.isAssociatedWith(file.getName())) {
            	isJsp = true;
            }
        }

        return isJsp;
    }

    /**
     * schedules a search document representing this JSP file for indexing (by
     * the java indexer)
     * 
     * @param file
     *            the JSP file
     * @return true if indexing was successful, false otherwise
     * @throws CoreException
     */
    public SearchDocument addJspFile(IFile file) {
        if (JSPSearchSupport.getInstance().isCanceled() || !file.isAccessible()) {
            return null;
        }

        if (DEBUG)
            System.out.println("adding JSP file:" + file.getFullPath()); //$NON-NLS-1$

        // create
        SearchDocument delegate = createSearchDocument(file);
        // null if not a jsp file
        if (delegate != null) {
            try {
                getSearchParticipant().scheduleDocumentIndexing(delegate, computeIndexLocation(file.getParent().getFullPath()));
            } catch (Exception e) {
                // ensure that failure here doesn't keep other documents from
                // being indexed
                // if peformed in a batch call (like JSPIndexManager)
                if (DEBUG)
                    e.printStackTrace();
            }
        }

        if (DEBUG)
            System.out.println("scheduled" + delegate + "for indexing"); //$NON-NLS-1$ //$NON-NLS-2$

        return delegate;
    }
    
    /**
     * Perform a java search w/ the given parameters. Runs in a background Job
     * (results may still come in after this method call)
     * 
     * @param searchText
     *            the string of text to search on
     * @param searchFor
     *            IJavaSearchConstants.TYPE, METHOD, FIELD, PACKAGE, etc...
     * @param limitTo
     *            IJavaSearchConstants.DECLARATIONS,
     *            IJavaSearchConstants.REFERENCES,
     *            IJavaSearchConstants.IMPLEMENTORS, or
     *            IJavaSearchConstants.ALL_OCCURRENCES
     * @param matchMode
     *            allow * wildcards or not
     * @param isCaseSensitive
     * @param requestor
     *            passed in to accept search matches (and do "something" with
     *            them)
     * 
     * @deprecated use {@link #search(String, IJavaSearchScope, int, int, int, boolean, SearchRequestor, IProgressMonitor)}
     */
    public void search(String searchText, IJavaSearchScope scope, int searchFor, int limitTo, int matchMode, boolean isCaseSensitive, SearchRequestor requestor) {
    	this.search(searchText, scope, searchFor, limitTo, matchMode, isCaseSensitive,
    			requestor, new NullProgressMonitor());
    }

    /**
     * Search for an IJavaElement, constrained by the given parameters. Runs in
     * a background Job (results may still come in after this method call)
     * 
     * @param element
     * @param scope
     * @param requestor
     * 
     * @deprecated use {@link #search(IJavaElement, IJavaSearchScope, SearchRequestor, IProgressMonitor)}
     */
    public void search(IJavaElement element, IJavaSearchScope scope, SearchRequestor requestor) {
    	this.search(element, scope, requestor, new NullProgressMonitor());
    }

    /**
     * Search for an IJavaElement, constrained by the given parameters. Runs in
     * an IWorkspace runnable (results will be reported by the end of this
     * method)
     * 
     * @param element
     * @param scope
     * @param requestor
     */
    public void searchRunnable(IJavaElement element, IJavaSearchScope scope, SearchRequestor requestor) {
    	this.searchRunnable(element, scope, requestor, new NullProgressMonitor());
    }
    
    /**
     * Perform a java search w/ the given parameters. Runs in a background Job
     * (results may still come in after this method call)
     * 
     * @param searchText
     *            the string of text to search on
     * @param searchFor
     *            IJavaSearchConstants.TYPE, METHOD, FIELD, PACKAGE, etc...
     * @param limitTo
     *            IJavaSearchConstants.DECLARATIONS,
     *            IJavaSearchConstants.REFERENCES,
     *            IJavaSearchConstants.IMPLEMENTORS, or
     *            IJavaSearchConstants.ALL_OCCURRENCES
     * @param matchMode
     *            allow * wildcards or not
     * @param isCaseSensitive
     * @param requestor
     *            passed in to accept search matches (and do "something" with
     *            them)
     */
    public void search(String searchText, IJavaSearchScope scope, int searchFor, int
    		limitTo, int matchMode, boolean isCaseSensitive, SearchRequestor requestor,
    		IProgressMonitor monitor) {

    	//wait for the index
		JSPIndexManager.getDefault().waitForConsistent(monitor);

        SearchJob job = new SearchJob(searchText, scope, searchFor, limitTo, matchMode, isCaseSensitive, requestor);
        setCanceled(false);
        job.setUser(true);
        // https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5032
        // pops up user operation blocked dialog if you perform a long search,
        // then open a file because it locks the workspace
        //job.setRule(ResourcesPlugin.getWorkspace().getRoot());
        job.schedule();
    }

    /**
     * Search for an IJavaElement, constrained by the given parameters. Runs in
     * a background Job (results may still come in after this method call)
     * 
     * @param element
     * @param scope
     * @param requestor
     */
    public void search(IJavaElement element, IJavaSearchScope scope, SearchRequestor requestor,
    		IProgressMonitor monitor) {

    	//wait for the index
		JSPIndexManager.getDefault().waitForConsistent(monitor);
    	
        SearchJob job = new SearchJob(element, scope, requestor);
        setCanceled(false);
        job.setUser(true);
        // https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=5032
        //job.setRule(ResourcesPlugin.getWorkspace().getRoot());
        job.schedule();
    }

    /**
     * Search for an IJavaElement, constrained by the given parameters. Runs in
     * an IWorkspace runnable (results will be reported by the end of this
     * method)
     * 
     * @param element
     * @param scope
     * @param requestor
     */
    public void searchRunnable(IJavaElement element, IJavaSearchScope scope,
    		SearchRequestor requestor, IProgressMonitor monitor) {
    	
    	//wait for the index
		JSPIndexManager.getDefault().waitForConsistent(monitor);

        SearchRunnable searchRunnable = new SearchRunnable(element, scope, requestor);
        try {
            setCanceled(false);
            ResourcesPlugin.getWorkspace().run(searchRunnable, JSPSearchSupport.getInstance().getProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param jspFile
     * @return SearchDocument if the file is not null, exists, and is a JSP
     *         file, otherwise null.
     */
    private SearchDocument createSearchDocument(IFile jspFile) {

        JavaSearchDocumentDelegate delegate = null;
        if (jspFile != null && jspFile.exists() && isJsp(jspFile)) {

            delegate = new JavaSearchDocumentDelegate(new JSPSearchDocument(jspFile.getFullPath().toString(), getSearchParticipant()));
        }
        return delegate;

    }

    /**
     * Centralized place to access JSPSearchDocuments (used by
     * JSPSearchParticipant and JSPSearchRequestor)
     * 
     * @param searchDocPath
     * @param doc
     * @return the JSPSearchDocument or null if one is not found
     */
    public SearchDocument getSearchDocument(String searchDocPath) {
         
        SearchDocument delegate = null;
        IFile f = fileForCUPath(searchDocPath);
        if (f != null) {
            delegate = createSearchDocument(f);
        } else {
            // handle failure case... (file deleted maybe?)
        }
        return delegate;
    }

    /**
     * Unmangles the searchDocPath and returns the corresponding JSP file.
     * 
     * @param searchDocPath
     */
    private IFile fileForCUPath(String searchDocPath) {
    
        String[] split = searchDocPath.split("/"); //$NON-NLS-1$
        String classname = split[split.length - 1];

        // ignore anything but .java matches (like .class binary matches)
        if(!searchDocPath.endsWith(".java")) { //$NON-NLS-1$
            return null;
        }

        String filePath = JSP2ServletNameUtil.unmangle(classname);
       
        // try absolute path
        IFile f = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(filePath));
        // workspace relative then
        if(f == null) {
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=86009
            // must have a project name as well
            // which would mean >= 2 path segments
            IPath path = new Path(filePath);
            if(path.segmentCount() >= 2) {
                f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            }
        }
        return f;
    }

    JSPSearchParticipant getSearchParticipant() {

        if (this.fParticipant == null)
            this.fParticipant = new JSPSearchParticipant();
        return this.fParticipant;
    }

    // This is called from JSPPathIndexer
    // pa_TODO
    //how can we make sure participant indexLocations are updated at startup?
    public final IPath computeIndexLocation(IPath containerPath) {

        IPath indexLocation = null;
        // we don't want to inadvertently use a JDT Index
        // we want to be sure to use the Index from the JSP location
        //Object obj = indexLocations.get(containerPath);
        //if (obj != null) {
        //    indexLocation = (String) obj;
        //} else {
            // create index entry
            String pathString = containerPath.toOSString();
            this.fChecksumCalculator.reset();
            this.fChecksumCalculator.update(pathString.getBytes());
            String fileName = Long.toString(this.fChecksumCalculator.getValue()) + ".index"; //$NON-NLS-1$
            // this is the only difference from
            // IndexManager#computeIndexLocation(...)
            indexLocation = getModelJspPluginWorkingLocation().append(fileName);
        //}
        return indexLocation;
    }

    // copied from JDT IndexManager
    public IPath getModelJspPluginWorkingLocation() {

        if (this.fJspPluginLocation != null)
            return this.fJspPluginLocation;

        // Append the folder name "jspsearch" to keep the state location area cleaner
        IPath stateLocation = JSPCorePlugin.getDefault().getStateLocation().append("jspsearch"); //$NON-NLS-1$

        // pa_TODO workaround for
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=62267
        // copied from IndexManager
        String device = stateLocation.getDevice();
        if (device != null && device.charAt(0) == '/')
            stateLocation = stateLocation.setDevice(device.substring(1));

        // ensure that it exists on disk
        File folder = new File(stateLocation.toOSString());
		if (!folder.isDirectory()) {
			try {
				folder.mkdir();
			}
			catch (SecurityException e) {
			}
		}

        return this.fJspPluginLocation = stateLocation;
    }

    /**
     * JSP Indexing and Search jobs check this
     * 
     * @return
     */
    public synchronized final void setCanceled(boolean cancel) {
    	//System.out.println("search support monitor" + fMonitor);
        fMonitor.setCanceled(cancel);
    }

    /**
     * JSP Indexing and Search jobs check this
     * 
     * @return
     */
    public synchronized final boolean isCanceled() {

        return fMonitor.isCanceled();
    }

    /**
     * JSP Indexing and Search jobs check this
     * 
     * @return
     */
    public final IProgressMonitor getProgressMonitor() {

        return this.fMonitor;
    }
}
