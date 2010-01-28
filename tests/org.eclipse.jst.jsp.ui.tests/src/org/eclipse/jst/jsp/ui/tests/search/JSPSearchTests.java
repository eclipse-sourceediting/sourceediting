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
package org.eclipse.jst.jsp.ui.tests.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchScope;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.ui.internal.java.search.BasicJSPSearchRequestor;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;


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

		private List matches = null;

		public TestJspSearchRequestor() {
			this.matches = new ArrayList();
		}

		// called by search framework
		protected void addSearchMatch(IStructuredDocument jspDocument, IFile jspFile, int jspStart, int jspEnd, String jspText) {
			this.matches.remove(new TestSearchMatch(jspFile.getName(), jspStart, jspEnd));
		}

		public void beginReporting() {
			// do nothing since we have no UI, don't access ISearchResultView
		}

		public void endReporting() {
			// do nothing since we have no UI, don't access ISearchResultView
		}

		public void addCheckMatch(String filename, int jspStart, int jspEnd) {
			this.matches.add(new TestSearchMatch(filename, jspStart, jspEnd));
		}

		public boolean checkValid() {
			return this.matches.isEmpty();
		}

		public void clear() {
			this.matches.clear();
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
		SearchDocument doc = JSPSearchSupport.getInstance().getSearchDocument("/judo/searchTestJSP.java");
		assertNotNull("couldn't retrieve:'/judo/searchTestJSP.java'", doc);

		doc = support.getSearchDocument("/judo/searchTestJSP2.java");
		assertNotNull("couldn't retrieve:'/judo/searchTestJSP2.java'", doc);

		doc = support.getSearchDocument("/judo/searchTestJSP3.java");
		assertNotNull("couldn't retrieve:'/judo/searchTestJSP3.java'", doc);
	}

	public void testSearchField() {

		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP.jsp", 93, 106);
		JSPSearchSupport.getInstance().search("searchForThis", new JSPSearchScope(), FIELD, ALL_OCCURRENCES, SearchPattern.R_EXACT_MATCH, true, requestor);
		assertTrue("did not find all expected matches: searchForThis", requestor.checkValid());
	}

	public void testSearchMethod() {

		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP.jsp", 143, 158);
		requestor.addCheckMatch("searchTestJSP.jsp", 298, 315);
		JSPSearchSupport.getInstance().search("searchForMethod", new JSPSearchScope(), METHOD, ALL_OCCURRENCES, SearchPattern.R_EXACT_MATCH, true, requestor);
		assertTrue("did not find all expected matches: searchForMethod", requestor.checkValid());
	}

	public void testSearchPatternMatch() {

		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP.jsp", 93, 106);
		requestor.addCheckMatch("searchTestJSP2.jsp", 116, 129);
		requestor.addCheckMatch("searchTestJSP2.jsp", 152, 165);
		JSPSearchSupport.getInstance().search("search*", new JSPSearchScope(), FIELD, ALL_OCCURRENCES, SearchPattern.R_PATTERN_MATCH, true, requestor);
		assertTrue("did not find all expected matches: search*", requestor.checkValid());
	}

	public void testSearchProjectClasses() {

		TestJspSearchRequestor requestor = new TestJspSearchRequestor();
		requestor.addCheckMatch("searchTestJSP3.jsp", 148, 157);
		requestor.addCheckMatch("searchTestJSP3.jsp", 170, 179);
		requestor.addCheckMatch("searchTestJSP3.jsp", 263, 273);
		requestor.addCheckMatch("searchTestJSP3.jsp", 299, 309);
		requestor.addCheckMatch("searchTestJSP3.jsp", 408, 417);
		requestor.addCheckMatch("searchTestJSP3.jsp", 430, 439);
		JSPSearchSupport.getInstance().search("Jellybean*", new JSPSearchScope(), TYPE, ALL_OCCURRENCES, SearchPattern.R_PATTERN_MATCH, true, requestor);
		assertTrue("did not find all expected matches: search*", requestor.checkValid());
	}

	public void testSearchLocalVariable() {

		IDOMModel xmlModel = null;
		try {
			IPath jspTestFilePath = new Path("judo/SEARCH/searchTestJSP3.jsp");
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(jspTestFilePath);
			xmlModel = (IDOMModel) getStructuredModelForRead(file);
			setupAdapterFactory(xmlModel);

			IDOMDocument doc = xmlModel.getDocument();
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) doc.getAdapterFor(IJSPTranslation.class);
			JSPTranslation translation = adapter.getJSPTranslation();
			IJavaElement element = translation.getElementsFromJspRange(377, 384)[0];

			TestJspSearchRequestor requestor = new TestJspSearchRequestor();
			requestor.addCheckMatch("searchTestJSP3.jsp", 377, 384);
			JSPSearchSupport.getInstance().search(element, new JSPSearchScope(), requestor);
			assertTrue("did not find all expected matches: search*", requestor.checkValid());
		}
		finally {
			if (xmlModel != null)
				xmlModel.releaseFromRead();
		}
	}

	private IStructuredModel getStructuredModelForRead(IFile file) {

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			return modelManager.getModelForRead(file);
		}
		catch (IOException ioex) {
			System.out.println("couldn't open file:" + file);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * add the factory for JSPTranslationAdapter here
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {

		JSPTranslationAdapterFactory factory = JSPTranslationAdapterFactory.getDefault();
		sm.getFactoryRegistry().addFactory(factory);
	}
}
