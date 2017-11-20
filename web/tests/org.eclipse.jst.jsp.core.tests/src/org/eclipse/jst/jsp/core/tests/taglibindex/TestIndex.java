/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.tests.taglibindex;

import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDDocument;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Tests for the TaglibIndex.
 */
public class TestIndex extends TestCase {
	String wtp_autotest_noninteractive = null;
	int MAX_RETRYS = 5;
	int PAUSE_TIME = 1;
	boolean DEBUG = true;

	protected void setUp() throws Exception {
		super.setUp();
		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
		removeAllProjects();
	}

	public void testAvailableAfterAddingJARToBuildPath() throws Exception {
		String url = "http://example.com/sample2_for_118251-e";

		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject("bug_118251-e", null, null);
		assertTrue(project.exists());
		ITaglibRecord[] records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-e"));
		assertEquals("wrong number of taglib records found before unpacking", 0, records.length);

		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug_118251-e", "/bug_118251-e");
		// bug_118251-e/WebContent/WEB-INF/web.xml
		// bug_118251-e/WebContent/WEB-INF/tld/sample2_for_118251-e.tld
		// bug_118251-e/WebContent/META-INF/MANIFEST.MF
		// bug_118251-e/WebContent/test1.jsp
		// bug_118251-e/.classpath
		// bug_118251-e/.project
		ITaglibRecord taglibRecord = TaglibIndex.resolve("/bug_118251-e/WebContent/test1.jsp", url, false);
		assertNull("unexpected record found for " + url, taglibRecord);

		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-e/WebContent/"));
		assertEquals("wrong number of taglib records found after unpacking but before copying", 2, records.length);

		/*
		 * increase by <b>one</b> for the URL to the TLD in the jar (one
		 * implicit for the TLD in the jar as a resource and another implicit
		 * overwriting it with the same URL to the TLD in the jar on the
		 * classpath)
		 */
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/bug_118251-sample/sample_tld.jar", "/bug_118251-e/WebContent/WEB-INF/sample_tld.jar");

		url = "http://example.com/sample-taglib";
		taglibRecord = TaglibIndex.resolve("/bug_118251-e/WebContent/test1.jsp", url, false);
		assertNotNull("expected record missing for " + url, taglibRecord);

		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-e/WebContent/"));
		assertEquals("wrong number of taglib records found after copying", 3, records.length);
	}

	public void testAvailableAfterCopyingJARIntoProject() throws Exception {
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject("bug_118251-f", null, null);
		assertTrue(project.exists());
		ITaglibRecord[] records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-f"));
		assertEquals("wrong number of taglib records found before unpacking", 0, records.length);

		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug_118251-f", "/bug_118251-f");
		// bug_118251-f/WebContent/WEB-INF/web.xml
		// bug_118251-f/WebContent/WEB-INF/tld/sample2_for_118251-e.tld
		// bug_118251-f/WebContent/META-INF/MANIFEST.MF
		// bug_118251-f/WebContent/test1.jsp
		// bug_118251-f/.classpath
		// bug_118251-f/.project
		String url = "http://example.com/sample-taglib";
		ITaglibRecord taglibRecord = TaglibIndex.resolve("/bug_118251-f/WebContent/test1.jsp", url, false);
		assertNull("unexpected record found for " + url, taglibRecord);

		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-f/WebContent/"));
		assertEquals("wrong number of taglib records found after unpacking but before copying", 3, records.length);

		/*
		 * increase by <b>one</b> for the URL to the TLD in the jar (one
		 * implicit for the TLD in the jar as a resource and another implicit
		 * overwriting it with the same URL to the TLD in the jar on the
		 * classpath)
		 */
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/bug_118251-sample/sample_tld.jar", "/bug_118251-f/WebContent/WEB-INF/lib/sample_tld.jar");

		taglibRecord = TaglibIndex.resolve("/bug_118251-f/WebContent/test1.jsp", url, false);
		assertNotNull("no record found for " + url, taglibRecord);
		assertTrue("record found was wrong type", taglibRecord instanceof IURLRecord);
		assertNotNull("record has no base location", ((IURLRecord) taglibRecord).getBaseLocation());
		assertEquals("record has wrong short name", "sample", ((IURLRecord) taglibRecord).getShortName());
		assertEquals("record has wrong URI", url, ((IURLRecord) taglibRecord).getDescriptor().getURI());
		URL recordURL = ((IURLRecord) taglibRecord).getURL();
		assertNotNull("record has no URL", recordURL);
		assertTrue("record has wrong URL", recordURL.toString().length() > 4);
		assertEquals("record has wrong URL protocol", "jar:", recordURL.toString().substring(0, 4));
		assertEquals("record has wrong URL", "/bug_118251-f/WebContent/WEB-INF/lib/sample_tld.jar!/folder/sample_for_118251.tld", recordURL.toString().substring(recordURL.toString().length() - 81));

		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-f/WebContent/"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-f/WebContent"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-f/WebContent/WEB-INF"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-f/WebContent/WEB-INF/web.xml"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
	}

	public void testAvailableAfterCopyingJARIntoProject2() throws Exception {
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject("bug_118251-g", null, null);
		assertTrue(project.exists());
		ITaglibRecord[] records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-g"));
		assertEquals("wrong number of taglib records found before unpacking", 0, records.length);

		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug_118251-g", "/bug_118251-g");
		// bug_118251-g/Web Content/WEB-INF/web.xml
		// bug_118251-g/Web Content/WEB-INF/tld/sample2_for_118251-e.tld
		// bug_118251-g/Web Content/META-INF/MANIFEST.MF
		// bug_118251-g/Web Content/test1.jsp
		// bug_118251-g/.classpath
		// bug_118251-g/.project
		String url = "http://example.com/sample-taglib";
		ITaglibRecord taglibRecord = TaglibIndex.resolve("/bug_118251-g/Web Content/test1.jsp", url, false);
		assertNull("unexpected record found for " + url, taglibRecord);

		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-g/Web Content/"));
		assertEquals("wrong number of taglib records found after unpacking but before copying", 3, records.length);

		/*
		 * increase by <b>one</b> for the URL to the TLD in the jar (one
		 * implicit for the TLD in the jar as a resource and another implicit
		 * overwriting it with the same URL to the TLD in the jar on the
		 * classpath)
		 */
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/bug_118251-sample/sample_tld.jar", "/bug_118251-g/Web Content/WEB-INF/lib/sample_tld.jar");

		taglibRecord = TaglibIndex.resolve("/bug_118251-g/Web Content/test1.jsp", url, false);
		assertNotNull("no record found for " + url, taglibRecord);
		assertTrue("record found was wrong type", taglibRecord instanceof IURLRecord);
		assertNotNull("record has no base location", ((IURLRecord) taglibRecord).getBaseLocation());
		assertEquals("record has wrong short name", "sample", ((IURLRecord) taglibRecord).getShortName());
		assertEquals("record has wrong URI", url, ((IURLRecord) taglibRecord).getDescriptor().getURI());
		URL recordURL = ((IURLRecord) taglibRecord).getURL();
		assertNotNull("record has no URL", recordURL);
		assertTrue("record has wrong URL", recordURL.toString().length() > 4);
		assertEquals("record has wrong URL protocol", "jar:", recordURL.toString().substring(0, 4));
		assertEquals("record has wrong URL", "/bug_118251-g/Web Content/WEB-INF/lib/sample_tld.jar!/folder/sample_for_118251.tld", recordURL.toString().substring(recordURL.toString().length() - 82));

		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-g/Web Content/"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-g/Web Content"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-g/Web Content/WEB-INF"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-g/Web Content/WEB-INF/web.xml"));
		assertEquals("wrong number of taglib records found after copying", 4, records.length);
	}

	public void testUtilityProjectSupport() throws Exception {
		// Create project 1
		IProject project = BundleResourceUtil.createSimpleProject("test-jar", null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug183756/test-jar", "/test-jar");

		// Create project 2
		IProject project2 = BundleResourceUtil.createSimpleProject("test-war", null, null);
		assertTrue(project2.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug183756/test-war", "/test-war");

		IFile testFile = project2.getFile(new Path("src/main/webapp/test.jsp"));
		assertTrue("missing test JSP file!", testFile.isAccessible());

		IDOMModel jspModel = null;
		try {
			jspModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(testFile);
			NodeList tests = jspModel.getDocument().getElementsByTagName("test:test");
			assertTrue("test:test element not found", tests.getLength() > 0);
			CMElementDeclaration elementDecl = ModelQueryUtil.getModelQuery(jspModel).getCMElementDeclaration(((Element) tests.item(0)));
			assertNotNull("No element declaration was found for test:test at runtime", elementDecl);
			assertTrue("element declaration was not the expected kind", elementDecl instanceof CMNodeWrapper);
			CMNode originNode = ((CMNodeWrapper) elementDecl).getOriginNode();
			assertTrue("element declaration was not from a tag library", originNode instanceof TLDElementDeclaration);
			assertEquals("element declaration was not from expected tag library", "http://foo.com/testtags", ((TLDDocument) ((TLDElementDeclaration) originNode).getOwnerDocument()).getUri());
		}
		finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}
	}

	public void testWebXMLTaglibMappingsToJARs() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("bug_148717");
		if (!project.exists()) {
			// Create new project
			project = BundleResourceUtil.createSimpleProject("bug_148717", null, null);
			assertTrue(project.exists());
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/bug_148717", "/bug_148717");
		}

		IFile file = project.getFile("/WebContent/WEB-INF/lib/internal.jar");
		assertTrue(file.exists());

		String uri = "http://example.com/external-uri";
		ITaglibRecord taglibRecord = TaglibIndex.resolve("/bug_148717/WebContent/", uri, false);
		assertNotNull("record not found for " + uri, taglibRecord);
		assertEquals(ITaglibRecord.JAR, taglibRecord.getRecordType());
		assertEquals(uri, ((IJarRecord) taglibRecord).getDescriptor().getURI());

		ITaglibRecord taglibRecord2 = null;
		ITaglibRecord[] records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_148717/WebContent/"));
		for (int i = 0; i < records.length; i++) {
			int type = records[i].getRecordType();
			switch (type) {
				case ITaglibRecord.JAR : {
					taglibRecord2 = records[i];
				}
					break;
			}
		}
		assertNotNull("record not returned for " + uri, taglibRecord2);
		assertEquals(ITaglibRecord.JAR, taglibRecord2.getRecordType());
		assertEquals(uri, ((IJarRecord) taglibRecord2).getDescriptor().getURI());
	}

	/**
	 * test caching from session-to-session
	 */
	public void testRecordCacheCountBetweenSessions() throws Exception {
		TaglibIndex.shutdown();
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject("testcache1", null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/testcache1", "/testcache1");
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/bug_118251-sample/sample_tld.jar", "/testcache1/WebContent/WEB-INF/lib/sample_tld.jar");
		TaglibIndex.startup();

		ITaglibRecord[] records = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match", 5, records.length);

		TaglibIndex.shutdown();
		TaglibIndex.startup();
		ITaglibRecord[] records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (1st restart)", records.length, records2.length);
		TaglibIndex.shutdown();
		TaglibIndex.startup();
		records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (2nd restart)", records.length, records2.length);
		TaglibIndex.shutdown();
		TaglibIndex.startup();
		records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (3rd restart)", records.length, records2.length);
	}

	/**
	 * test caching from session-to-session with an addition in one session
	 */
	public void testCachingWithAddingLibrary() throws Exception {
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject("testcache2", null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/testcache2", "/testcache2");

		ITaglibRecord[] records = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache2/WebContent"));
		TaglibIndex.shutdown();

		TaglibIndex.startup();
		ITaglibRecord[] records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (1st restart)", records.length, records2.length);
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/bug_118251-sample/sample_tld.jar", "/testcache2/WebContent/WEB-INF/lib/sample_tld.jar");
		records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (1st restart, added jar file)", records.length + 1, records2.length);
		TaglibIndex.shutdown();

		TaglibIndex.startup();
		records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (2nd restart)", records.length + 1, records2.length);
		BundleResourceUtil.addLibraryEntry(project, "WebContent/WEB-INF/lib/sample_tld.jar");
		TaglibIndex.shutdown();

		TaglibIndex.startup();
		assertEquals("total ITaglibRecord count doesn't match (3nd restart)", records.length + 1, records2.length);
		TaglibIndex.shutdown();

		TaglibIndex.startup();
		records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testcache2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match changed value (4th restart, add jar to build path)", records.length + 2, records2.length);
	}

	public void testAvailableFromExportedOnBuildpathFromAnotherProject() throws Exception {
		TaglibIndex.shutdown();

		// Create project 1
		IProject project = BundleResourceUtil.createSimpleProject("testavailable1", null, null);
		assertTrue(project.isAccessible());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/testavailable1", "/testavailable1");

		// Create project 2
		IProject project2 = BundleResourceUtil.createSimpleProject("testavailable2", null, null);
		assertTrue(project2.isAccessible());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/testavailable2", "/testavailable2");
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/bug_118251-sample/sample_tld.jar", "/testavailable2/WebContent/WEB-INF/lib/sample_tld.jar");

		TaglibIndex.startup();

		// make sure project 1 sees no taglibs
		ITaglibRecord[] records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable1/WebContent"));
		assertEquals("ITaglibRecords were found", 0, records.length);
		// make sure project 2 sees two taglibs
		ITaglibRecord[] records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable2/WebContent"));
		if (records2.length != 2) {
			for (int i = 0; i < records2.length; i++) {
				System.err.println(records2[i]);
			}
		}
		assertEquals("total ITaglibRecord count doesn't match", 2, records2.length);

		TaglibIndex.shutdown();
		TaglibIndex.startup();


		records2 = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match after restart", 2, records2.length);

		IJavaProject created = JavaCore.create(project2);
		assertTrue("/availabletest2 not a Java project", created.exists());

		// export the jar from project 2
		IClasspathEntry[] entries = created.getRawClasspath();
		boolean found = false;
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getPath().equals(new Path("/testavailable2/WebContent/WEB-INF/lib/sample_tld.jar"))) {
				found = true;
				assertFalse("was exported", entry.isExported());
				((ClasspathEntry) entry).isExported = true;
			}
		}
		assertTrue("/testavailable2/WebContent/WEB-INF/lib/sample_tld.jar was not found in build path", found);
		IClasspathEntry[] entries2 = new IClasspathEntry[entries.length];
		System.arraycopy(entries, 1, entries2, 0, entries.length - 1);
		entries2[entries.length - 1] = entries[0];
		created.setRawClasspath(entries2, new NullProgressMonitor());

		entries = created.getRawClasspath();
		found = false;
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getPath().equals(new Path("/testavailable2/WebContent/WEB-INF/lib/sample_tld.jar"))) {
				found = true;
				assertTrue("/testavailable2/WebContent/WEB-INF/lib/sample_tld.jar was not exported", ((ClasspathEntry) entry).isExported);
			}
		}
		assertTrue("/testavailable2/WebContent/WEB-INF/lib/sample_tld.jar was not found (and exported) in build path", found);

		// project 2 should still have just two taglibs
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar)", 2, records.length);

		// now one taglib should be visible from project 1
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar), classpath provider problem?", 1, records.length);

		TaglibIndex.shutdown();
		TaglibIndex.startup();

		// project 2 should still have just two taglibs
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar and restarting)", 2, records.length);

		// and one taglib should still be visible from project 1
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar and restarting)", 1, records.length);
	}

	private void removeAllProjects() throws CoreException, InterruptedException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		IProject project = null;
		for (int i = 0; i < projects.length; i++) {
			project = projects[i];
			deleteProject(project);
		}
	}

	/**
	 * It's not easy to delete projects. If any of it's files are open by another thread, 
	 * the operation will fail. So, this method will make several attempts before giving up. 
	 * @param project
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	private void deleteProject(IProject project) throws CoreException, InterruptedException {
		int nTrys = 0;
		while (project != null && project.exists() && nTrys < MAX_RETRYS) {
			try {
				nTrys++;
				project.delete(true, true, null);
			}
			catch (ResourceException e) {
				if (DEBUG) {
					System.out.println();
					System.out.println("Could not delete project on attempt number: "+ nTrys);
					IStatus eStatus = e.getStatus();
					// should always be MultiStatus, but we'll check
					if (eStatus instanceof MultiStatus) {
						MultiStatus mStatus = (MultiStatus) eStatus;
						IStatus[] iStatus = mStatus.getChildren();
						for (int j = 0; j < iStatus.length; j++) {
							System.out.println("Status: " + j + " " + iStatus[j]);
						}
					}
					else {
						System.out.println("Status: " + eStatus);
					}
				}
				/*
				 * If we could not delete the first time, wait a bit and
				 * re-try. If we could not delete, it is likely because
				 * another thread has a file open, or similar (such as the
				 * validation thread).
				 */
				Thread.sleep(PAUSE_TIME);
			}
		}
		
		if (project != null && project.exists()) {
			fail("Error in test infrastructure. Could not delete project " + project + " after " + MAX_RETRYS + "attempts.");
		}
	}
}
