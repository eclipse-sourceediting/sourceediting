/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.validation;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.validation.JSPBatchValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPJavaValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.validation.ReporterHelper;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

/**
 * Tests JSP Batch Validator
 */
public class JSPBatchValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "batchvalidation";
	Object originalWorkspaceValue = null;
	IEclipsePreferences workspaceScope = null;
	IEclipsePreferences projectScope = null;
	Object[] validatorIds = new String[]{"org.eclipse.jst.jsp.core.JSPContentValidator", "org.eclipse.jst.jsp.core.JSPBatchValidator"};
	IWorkspace workspace = ResourcesPlugin.getWorkspace();

	protected void setUp() throws Exception {
		super.setUp();
		
		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");

		if (!getProject().exists()) {
			BundleResourceUtil.createSimpleProject(PROJECT_NAME, null, new String[]{JavaCore.NATURE_ID});
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
		assertTrue("project could not be created", getProject().exists());

		String filePath = "/" + PROJECT_NAME + "/WebContent/header.jspf";
		IFile fragment = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));

		String qualifier = JSPCorePlugin.getDefault().getBundle().getSymbolicName();
		workspaceScope = new InstanceScope().getNode(qualifier);
		projectScope = new ProjectScope(fragment.getProject()).getNode(qualifier);
		originalWorkspaceValue = workspaceScope.get(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, null);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
		projectScope.remove(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		projectScope.remove(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS);
		if (originalWorkspaceValue != null)
			workspaceScope.put(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, originalWorkspaceValue.toString());
		else
			workspaceScope.remove(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
	}

	/**
	 * Tests validating 2 jsp files. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=214441
	 * 
	 * @throws Exception
	 */
	public void testValidating2Files() throws Exception {
		JSPValidator validator = new JSPJavaValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath1 = "/" + PROJECT_NAME + "/WebContent/ihaveerrors.jsp";
		IFile file1 = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath1));
		assertTrue(file1.exists());

		String filePath2 = "/" + PROJECT_NAME + "/WebContent/ihaveerrors2.jsp";
		IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath1));
		assertTrue(file2.exists());

		helper.setURIs(new String[]{filePath1, filePath2});

		validator.validate(helper, reporter);
		assertTrue("expected jsp errors were not found in both files: " + reporter.getMessages().size(), reporter.getMessages().size() >= 2);
	}

	public void testFragmentValidationPreferenceOnProject() throws Exception {
		JSPBatchValidator validator1 = new JSPBatchValidator();
		JSPContentValidator validator2 = new JSPContentValidator();
		String filePath = "/" + PROJECT_NAME + "/WebContent/header.jspf";
		ValidationResult result = null;
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));

		// enable workspace-wide but disable in project, no problem markers
		// expected
		workspaceScope.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
		projectScope.putBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, true);
		projectScope.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false);

		result = validator1.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor());
		result.mergeResults(validator2.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor()));

		assertEquals("Problems found while fragment validation was disabled in project but enabled on workspace", 0, (((ReporterHelper)result.getReporter(null)).getMessages().size()));

		/*
		 * disable workspace-wide but enable in project, some problem markers
		 * expected
		 */
		workspaceScope.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false);
		projectScope.putBoolean(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS, true);
		projectScope.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
		JSPFContentProperties.setProperty(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, file, Boolean.toString(true));

		result = validator1.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor());
		result.mergeResults(validator2.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor()));

		assertTrue("Problems not found while fragment validation was enabled for project but disabled on workspace", 0 < (((ReporterHelper)result.getReporter(null)).getMessages().size()));
	}

	public void testFragmentValidationPreferenceOnWorkspace() throws Exception {
		JSPBatchValidator validator1 = new JSPBatchValidator();
		JSPContentValidator validator2 = new JSPContentValidator();
		String filePath = "/" + PROJECT_NAME + "/WebContent/header.jspf";

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		ValidationResult result = null;

		// disable workspace-wide, no problem markers expected
		workspaceScope.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false);

		result = validator1.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor());
		result.mergeResults(validator2.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor()));

		assertEquals("Problem markers found while fragment validation was disabled", 0, (((ReporterHelper)result.getReporter(null)).getMessages().size()));

		// enable workspace-wide, some problem markers expected
		workspaceScope.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
		ValidationFramework.getDefault().validate(new IProject[]{getProject()}, true, false, new NullProgressMonitor());
		ValidationFramework.getDefault().join(new NullProgressMonitor());

		result = validator1.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor());
		result.mergeResults(validator2.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor()));

		assertTrue("Problem markers not found while fragment validation was enabled on workspace", (((ReporterHelper)result.getReporter(null)).getMessages().size()) != 0);

		// check default value is true
		workspaceScope.remove(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		projectScope.remove(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		projectScope.remove(JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS);

		result = validator1.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor());
		result.mergeResults(validator2.validate(file, IResourceDelta.CHANGED, new ValidationState(), new NullProgressMonitor()));

		assertTrue("Problem markers not found while fragment validation was preferences were default", (((ReporterHelper)result.getReporter(null)).getMessages().size()) != 0);
	}

	public void testELConditional() throws Exception {
		if (!ResourcesPlugin.getWorkspace().getRoot().getProject("testIterationTags").exists()) {
			BundleResourceUtil.createSimpleProject("testIterationTags", null, new String[]{JavaCore.NATURE_ID});
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject("testIterationTags").exists());


		JSPValidator validator = new JSPJavaValidator();
		IReporter reporter = new ReporterForTest();
		ValidationContextForTest helper = new ValidationContextForTest();
		String filePath1 = "/testIterationTags/WebContent/default.jspx";
		IFile file1 = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath1));
		assertTrue(file1.exists());

		helper.setURIs(new String[]{filePath1});

		validator.validate(helper, reporter);
		assertTrue("expected jsp errors were not found in both files: " + reporter.getMessages().size(), reporter.getMessages().size() == 0);
	}

	/**
	 * @return
	 */
	private IProject getProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}
}
