/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.jsp.core.taglib.ITaglibRecord;
import org.eclipse.jst.jsp.core.taglib.IURLRecord;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;

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
}