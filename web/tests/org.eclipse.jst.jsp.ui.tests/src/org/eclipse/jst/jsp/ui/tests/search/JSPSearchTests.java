/*******************************************************************************
 * Copyright (c) 2004, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.java.JSP2ServletNameUtil;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchScope;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.ui.internal.java.search.BasicJSPSearchRequestor;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.osgi.service.datalocation.Location;

import junit.framework.TestCase;


/**
 * Tests for JSPSearchSupport
 * @author pavery
 */
public class JSPSearchTests extends TestCase implements IJavaSearchConstants {

	/**
	 * to accept search matches
	 * addCheckMatch(...) for matches you expect, run search support, checkValid() to see if all expected got hit
	 */
	class TestJspSearchRequestor extends BasicJSPSearchRequestor {

		private List expected = null;
		List actual = null;

		public TestJspSearchRequestor() {
			this.expected = new ArrayList();
			this.actual = new ArrayList();
		}

		// called by search framework
		protected void addSearchMatch(IDocument jspDocument, IFile jspFile, int jspStart, int jspEnd, String jspText) {
			Iterator iterator = this.expected.iterator();
			TestSearchMatch obj = new TestSearchMatch(jspFile.getName(), jspStart, jspEnd);
			actual.add(obj);
			while (iterator.hasNext()) {
				if (iterator.next().equals(obj)) {
					iterator.remove();
				}
			}
		}

		public void beginReporting() {
			// do nothing since we have no UI, don't access ISearchResultView
		}

		public void endReporting() {
			// do nothing since we have no UI, don't access ISearchResultView
		}

		public void addCheckMatch(String filename, int jspStart, int jspEnd) {
			this.expected.add(new TestSearchMatch(filename, jspStart, jspEnd));
		}

		public boolean checkValid() {
			return this.expected.isEmpty();
		}

		public void clear() {
			this.expected.clear();
		}
	}

	/**
	 * to validate search matches (checkMatch matches a search hit)
	 */
	class TestSearchMatch {

		public String filename = null;
		public int jspStart = -1;
		public int jspEnd = -1;

		public TestSearchMatch(String filename, int jspStart, int jspEnd) {
			this.filename = filename;
			this.jspStart = jspStart;
			this.jspEnd = jspEnd;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof TestSearchMatch))
				return false;

			TestSearchMatch possible = (TestSearchMatch) obj;
			return this.filename.equalsIgnoreCase(possible.filename) && this.jspStart == possible.jspStart && this.jspEnd == possible.jspEnd;
		}

		@Override
		public String toString() {
			return "TestSearchMatch [filename=" + filename + ", jspStart=" + jspStart + ", jspEnd=" + jspEnd + "]";
		}
	}

	private ProjectUnzipUtility projUtil = null;
	private boolean isSetup = false;

	public JSPSearchTests() {
		super("JSP Search Tests");
	}

	protected void setUp() throws Exception {
		super.setUp();
		if (!this.isSetup) {
			doSetup();
			this.isSetup = true;
		}
	}

	private void doSetup() throws Exception {

		this.projUtil = new ProjectUnzipUtility();

		// root of workspace directory
		Location platformLocation = Platform.getInstanceLocation();

		File zipFile = FileUtil.makeFileFor(ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, "jspsearch_tests.zip", ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
		this.projUtil.unzipAndImport(zipFile, platformLocation.getURL().getFile());
		this.projUtil.initJavaProject("judo");

		//JSPSearchSupport.getInstance().indexWorkspaceAndWait();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		// do nothing
	}

	public void testIndexWorkspace() {
		JSPSearchSupport support = JSPSearchSupport.getInstance();

		SearchDocument doc = JSPSearchSupport.getInstance().getSearchDocument(JSP2ServletNameUtil.mangle("/judo/SEARCH/searchTestJSP.jsp")+".java");
		assertNotNull("couldn't retrieve:'/judo/SEARCH/searchTestJSP.java'", doc);

		doc = support.getSearchDocument(JSP2ServletNameUtil.mangle("/judo/SEARCH/searchTestJSP2.jsp")+".java");
		assertNotNull("couldn't retrieve:'/judo/searchTestJSP2.java'", doc);

		doc = support.getSearchDocument(JSP2ServletNameUtil.mangle("/judo/SEARCH/searchTestJSP3.jsp")+".java");
		assertNotNull("couldn't retrieve:'/judo/searchTestJSP3.java'", doc);
	}

	public void testSearchField() throws InterruptedException {
		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP.jsp", 93, 106);
		JSPSearchSupport.getInstance().search("searchForThis", new JSPSearchScope(), FIELD, ALL_OCCURRENCES, SearchPattern.R_EXACT_MATCH, true, requestor, new NullProgressMonitor());
		Job.getJobManager().join(JSPSearchSupport.class, null);
		assertTrue("did not find all expected matches: searchForThis: " + requestor.expected, requestor.checkValid());
	}

	public void testSearchMethod() throws InterruptedException {
		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP.jsp", 143, 158);
		requestor.addCheckMatch("searchTestJSP.jsp", 298, 315);
		JSPSearchSupport.getInstance().search("searchForMethod", new JSPSearchScope(), METHOD, ALL_OCCURRENCES, SearchPattern.R_EXACT_MATCH, true, requestor, new NullProgressMonitor());
		Job.getJobManager().join(JSPSearchSupport.class, null);
		assertTrue("did not find all expected matches: searchForMethod", requestor.checkValid());
	}

	public void testSearchPatternMatch() throws InterruptedException {
		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP.jsp", 93, 106);
		requestor.addCheckMatch("searchTestJSP2.jsp", 116, 129);
		requestor.addCheckMatch("searchTestJSP2.jsp", 152, 165);
		JSPSearchSupport.getInstance().search("search*", new JSPSearchScope(), FIELD, ALL_OCCURRENCES, SearchPattern.R_PATTERN_MATCH, true, requestor, new NullProgressMonitor());
		Job.getJobManager().join(JSPSearchSupport.class, null);
		assertTrue("did not find all expected matches: search*", requestor.checkValid());
	}

	public void testSearchProjectClasses() throws InterruptedException {
		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP3.jsp", 179, 188);
		requestor.addCheckMatch("searchTestJSP3.jsp", 326, 336);
		requestor.addCheckMatch("searchTestJSP3.jsp", 440, 449);
		requestor.addCheckMatch("searchTestJSP3.jsp", 462, 471);
		JSPSearchSupport.getInstance().search("Jellybean*", new JSPSearchScope(), TYPE, ALL_OCCURRENCES, SearchPattern.R_PATTERN_MATCH, true, requestor, new NullProgressMonitor());
		Job.getJobManager().join(JSPSearchSupport.class, null);
		assertTrue("did not find all expected matches: search*\nexpected:" + requestor.expected + "\nactual:" + requestor.actual, requestor.checkValid());
	}

	/**
	 * XXX: Seems to fail because
	 * org.eclipse.jdt.internal.core.SelectionRequestor#acceptLocalVariable(LocalVariableBinding,
	 * ICompilationUnit) limits creating a parent handle to the contents of
	 * lambdas?
	 **/
	public void testSearchLocalVariable_DISABLED() throws InterruptedException {
//		IDOMModel xmlModel = null;
//		try {
//			IPath jspTestFilePath = new Path("judo/SEARCH/searchTestJSP3.jsp");
//			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(jspTestFilePath);
//			xmlModel = (IDOMModel) getStructuredModelForRead(file);
//			ModelHandlerForJSP.ensureTranslationAdapterFactory(xmlModel);
//
//			IDOMDocument doc = xmlModel.getDocument();
//			JSPTranslationAdapter adapter = (JSPTranslationAdapter) doc.getAdapterFor(IJSPTranslation.class);
//			JSPTranslation translation = adapter.getJSPTranslation();
//			String localVarName = "llcoolj";
//			int startOfLocal = translation.getJspText().indexOf(localVarName);
//			IJavaElement element = translation.getElementsFromJspRange(startOfLocal, startOfLocal + localVarName.length())[0];
//
//			TestJspSearchRequestor requestor = new TestJspSearchRequestor();
//			requestor.addCheckMatch("searchTestJSP3.jsp", startOfLocal, startOfLocal + localVarName.length());
//			JSPSearchSupport.getInstance().search(element, new JSPSearchScope(), requestor, new NullProgressMonitor());
//			Job.getJobManager().join(JSPSearchSupport.class, null);
//			assertTrue("did not find all expected matches: search*", requestor.checkValid());
//		}
//		finally {
//			if (xmlModel != null)
//				xmlModel.releaseFromRead();
//		}
	}

//	private IStructuredModel getStructuredModelForRead(IFile file) {
//
//		try {
//			IModelManager modelManager = StructuredModelManager.getModelManager();
//			return modelManager.getModelForRead(file);
//		}
//		catch (IOException ioex) {
//			System.out.println("couldn't open file:" + file);
//		}
//		catch (CoreException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
