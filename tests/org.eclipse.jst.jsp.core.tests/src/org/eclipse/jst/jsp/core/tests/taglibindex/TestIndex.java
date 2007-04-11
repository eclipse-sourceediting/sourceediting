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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.taglib.IJarRecord;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.jst.jsp.core.tests.JSPCoreTestsPlugin;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;

/**
 * Tests for the TaglibIndex.
 */
public class TestIndex extends TestCase {
	String wtp_autotest_noninteractive = null;

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
	}

	public void testBug118251_e() throws Exception {
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
		assertNotNull("record found for " + url, taglibRecord);

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
		assertNotNull("no record found for " + url, taglibRecord);

		records = TaglibIndex.getAvailableTaglibRecords(new Path("/bug_118251-e/WebContent/"));
		assertEquals("wrong number of taglib records found after copying", 3, records.length);
	}

	public void testBug118251_f() throws Exception {
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
		assertNull("record found for " + url, taglibRecord);

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
		assertEquals("record has wrong URI", url, ((IURLRecord) taglibRecord).getURI());
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

	public void testBug118251_g() throws Exception {
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
		assertNull("record found for " + url, taglibRecord);

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
		assertEquals("record has wrong URI", url, ((IURLRecord) taglibRecord).getURI());
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

	public void test_148717_a() throws Exception {
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
		assertEquals(uri, ((IJarRecord) taglibRecord).getURI());

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
		assertEquals(uri, ((IJarRecord) taglibRecord2).getURI());
	}

	public void test_181057a() throws Exception {
		boolean doValidateSegments = JSPCorePlugin.getDefault().getPluginPreferences().getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		String testName = "bug_181057";
		// Create new project
		IProject j = BundleResourceUtil.createSimpleProject("j", null, null);
		assertTrue(j.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/j", "/j");
		IProject k = BundleResourceUtil.createSimpleProject("k", null, null);
		assertTrue(k.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/k", "/k");

		IProject project = BundleResourceUtil.createSimpleProject(testName, Platform.getStateLocation(JSPCoreTestsPlugin.getDefault().getBundle()).append(testName), null);
		assertTrue(project.exists());
		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + testName, "/" + testName);
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/struts.jar", "/" + testName + "/struts.jar");
		j.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		k.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.FULL_BUILD, "org.eclipse.wst.validation.validationbuilder", null, new NullProgressMonitor());
		try {
			Job.getJobManager().join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (OperationCanceledException e) {
			e.printStackTrace();
		}
		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, doValidateSegments);
		/*
		 * main.jsp contains numerous references to tags in struts.jar, which
		 * is at the end of the build path
		 */
		IFile main = project.getFile("main.jsp");
		IMarker[] markers = main.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < markers.length; i++) {
			s.append("\n" + markers[i].getAttribute(IMarker.LINE_NUMBER) + ":" + markers[i].getAttribute(IMarker.MESSAGE));
		}
		assertEquals("problem markers found" + s.toString(), 0, markers.length);
	}

	/**
	 * test caching from session-to-session
	 */
	public void testCaching1() throws Exception {
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

	public void testAvailableFromExportedBuildpaths() throws Exception {
		TaglibIndex.shutdown();

		// Create project 1
		IProject project = BundleResourceUtil.createSimpleProject("testavailable1", null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/testavailable1", "/testavailable1");

		// Create project 2
		IProject project2 = BundleResourceUtil.createSimpleProject("testavailable2", null, null);
		assertTrue(project2.exists());
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
				assertFalse("was exported", ((ClasspathEntry) entry).isExported);
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
		assertTrue("/testavailable2/WebContent/WEB-INF/lib/sample_tld.jar was not found in build path", found);

		// project 2 should still have just two taglibs
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar)", 2, records.length);

		// now one taglib should be visible from project 1
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar)", 1, records.length);

		TaglibIndex.shutdown();
		TaglibIndex.startup();

		// project 2 should still have just two taglibs
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable2/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar and restarting)", 2, records.length);

		// and one taglib should still be visible from project 1
		records = TaglibIndex.getAvailableTaglibRecords(new Path("/testavailable1/WebContent"));
		assertEquals("total ITaglibRecord count doesn't match (after exporting jar and restarting)", 1, records.length);
	}
}
