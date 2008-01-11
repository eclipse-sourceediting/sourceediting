/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.translation;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.validation.JSPJavaValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPValidator;
import org.eclipse.jst.jsp.core.tests.JSPCoreTestsPlugin;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.jst.jsp.core.tests.validation.ReporterForTest;
import org.eclipse.jst.jsp.core.tests.validation.ValidationContextForTest;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSPJavaTranslatorCoreTest extends TestCase {

	static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";

	public JSPJavaTranslatorCoreTest() {
	}

	public JSPJavaTranslatorCoreTest(String name) {
		super(name);
	}

	String wtp_autotest_noninteractive = null;

	protected void setUp() throws Exception {
		super.setUp();
		String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, wtp_autotest_noninteractive);
	}

	public void test_107338() throws Exception {
		String projectName = "bug_107338";
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject(projectName, null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + projectName, "/" + projectName);
		IFile file = project.getFile("WebContent/test107338.jsp");
		assertTrue(file.exists());

		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(file);
		IDOMModel jspModel = (IDOMModel) model;

		String jspSource = model.getStructuredDocument().get();

		assertTrue("line delimiters have been converted to Windows [CRLF]", jspSource.indexOf("\r\n") < 0);
		assertTrue("line delimiters have been converted to Mac [CR]", jspSource.indexOf("\r") < 0);

		if (model.getFactoryRegistry().getFactoryFor(IJSPTranslation.class) == null) {
			JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
			model.getFactoryRegistry().addFactory(factory);
		}
		IDOMDocument xmlDoc = jspModel.getDocument();
		JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
		JSPTranslation translation = translationAdapter.getJSPTranslation();
		// System.err.print(translation.getJavaText());

		assertTrue("new-line beginning scriptlet missing from translation", translation.getJavaText().indexOf("int i = 0;") >= 0);

		model.releaseFromRead();
	}

	/**
	 * Tests jsp translation when jsp is within html comments. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=126377
	 * 
	 * @throws Exception
	 */
	public void test_126377() throws Exception {
		String projectName = "bug_126377";
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject(projectName, null, null);
		assertTrue(project.exists());
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + projectName, "/" + projectName);
		IFile file = project.getFile("WebContent/test126377_noerror.jsp");
		assertTrue(file.exists());
				
		JSPValidator validator = new JSPJavaValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		helper.setURI(file.getFullPath().toOSString());
		validator.validate(helper, reporter);
		
		assertTrue("found jsp java error within html comments when there are none", reporter.getMessages().isEmpty());
		
		file = project.getFile("WebContent/test126377_error.jsp");
		assertTrue(file.exists());
		helper.setURI(file.getFullPath().toOSString());
		validator.validate(helper, reporter);
		
		int errors = reporter.getMessages().size();
		assertTrue("found "+errors+" jsp java errors within html comments when there should be 3", (errors == 3));
	}

	// public void testMangling() {
	// assertEquals("simple_tag", JSP2ServletNameUtil.mangle("simple.tag"));
	// assertEquals("simple_jspf", JSP2ServletNameUtil.mangle("simple.jspf"));
	// assertEquals("sim_005f_005fple_tagx",
	// JSP2ServletNameUtil.mangle("sim__ple.tagx"));
	// assertEquals(new Path("Project.folder.simple_tag"),
	// JSP2ServletNameUtil.mangle(new Path("/Project/folder/simple.tag")));
	// assertEquals(new Path("Project.fold_005fer.simple_jspx"),
	// JSP2ServletNameUtil.mangle(new Path("/Project/fold_er/simple.jspx")));
	// }
	//
	// public void testUnmangling() {
	// assertEquals("simple.tag", JSP2ServletNameUtil.unmangle("simple_tag"));
	// assertEquals("simple.jspf",
	// JSP2ServletNameUtil.unmangle("simple_jspf"));
	// assertEquals("sim__ple.tagx",
	// JSP2ServletNameUtil.unmangle("sim_005f_005fple_tagx"));
	// assertEquals(new Path("/Project/folder/simple.tag"),
	// JSP2ServletNameUtil.unmangle(new Path("Project.folder.simple_tag")));
	// assertEquals(new Path("/Project/fold_er/simple.jspx"),
	// JSP2ServletNameUtil.unmangle(new
	// Path("Project.fold_005fer.simple_jspx")));
	// }
	public void test_174042() throws Exception {
		boolean doValidateSegments = JSPCorePlugin.getDefault().getPluginPreferences().getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		String testName = "bug_174042";
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject(testName, null, null);
		assertTrue(project.exists());
		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + testName, "/" + testName);
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/struts.jar", "/" + testName + "/struts.jar");
		waitForBuildAndValidation(project);
		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, doValidateSegments);
		IFile main = project.getFile("main.jsp");
		IMarker[] markers = main.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < markers.length; i++) {
			s.append("\nproblem marker on line " + markers[i].getAttribute(IMarker.LINE_NUMBER) + ": \"" + markers[i].getAttribute(IMarker.MESSAGE) + "\" ");
		}
		assertEquals("problem markers found, " + s.toString(), 0, markers.length);
	}

	private void waitForBuildAndValidation(IProject project) throws CoreException {
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.FULL_BUILD, "org.eclipse.wst.validation.validationbuilder", null, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		try {
			ResourcesPlugin.getWorkspace().checkpoint(true);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
			Job.getJobManager().join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Job.getJobManager().beginRule(ResourcesPlugin.getWorkspace().getRoot(), null);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (OperationCanceledException e) {
			e.printStackTrace();
		}
		finally {
			Job.getJobManager().endRule(ResourcesPlugin.getWorkspace().getRoot());
		}
	}

	public void test_178443() throws Exception {
		boolean doValidateSegments = JSPCorePlugin.getDefault().getPluginPreferences().getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		String testName = "bug_178443";
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject(testName, Platform.getStateLocation(JSPCoreTestsPlugin.getDefault().getBundle()).append(testName), null);
		assertTrue(project.exists());
		/*
		 * Should be set to false. A referenced class in an included segment
		 * does not exist.
		 */
		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false);
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + testName, "/" + testName);
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/struts.jar", "/" + testName + "/struts.jar");

		waitForBuildAndValidation(project);

		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, doValidateSegments);
		IFile main = project.getFile("main.jsp");
		IMarker[] markers = main.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);

		StringBuffer s = new StringBuffer();
		for (int i = 0; i < markers.length; i++) {
			s.append("\nproblem on line " + markers[i].getAttribute(IMarker.LINE_NUMBER) + ": " + markers[i].getAttribute(IMarker.MESSAGE));
		}
		assertEquals("problem markers found" + s.toString(), 0, markers.length);
	}

	public void test_109721() throws Exception {
		boolean doValidateSegments = JSPCorePlugin.getDefault().getPluginPreferences().getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		String testName = "bug_109721";
		// Create new project
		IProject project = BundleResourceUtil.createSimpleProject(testName, Platform.getStateLocation(JSPCoreTestsPlugin.getDefault().getBundle()).append(testName), null);
		assertTrue(project.exists());
		/*
		 * Should be set to false. A referenced class in an included segment
		 * does not exist.
		 */
		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false);
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + testName, "/" + testName);
		BundleResourceUtil.copyBundleEntryIntoWorkspace("/testfiles/struts.jar", "/" + testName + "/WebContent/WEB-INF/lib/struts.jar");

		waitForBuildAndValidation(project);

		JSPCorePlugin.getDefault().getPluginPreferences().setValue(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, doValidateSegments);
		IFile main = project.getFile("WebContent/main.jsp");
		IMarker[] markers = main.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);

		StringBuffer s = new StringBuffer();
		for (int i = 0; i < markers.length; i++) {
			s.append("\nproblem on line " + markers[i].getAttribute(IMarker.LINE_NUMBER) + ": " + markers[i].getAttribute(IMarker.MESSAGE));
		}
		assertEquals("problem markers found" + s.toString(), 0, markers.length);
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

		waitForBuildAndValidation(project);

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
}
