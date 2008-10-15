/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.validation.JSPJavaValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPValidator;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

/**
 * Tests JSP Batch Validator
 */
public class JSPBatchValidatorTest extends TestCase {
	String wtp_autotest_noninteractive = null;
	private static final String PROJECT_NAME = "batchvalidation";

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
		JSPFContentProperties.setProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, fragment, null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
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
		assertTrue("jsp errors were not found in both files", reporter.getMessages().size() == 2);
	}

	public void testFragmentValidationPreferenceOnFile() throws Exception {
		String filePath = "/" + PROJECT_NAME + "/WebContent/header.jspf";
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));

		// disable, no problem markers expected
		JSPFContentProperties.setProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, Boolean.toString(false));
		ValidationFramework.getDefault().validate(new IProject[]{getProject()}, true, false, new NullProgressMonitor());
		IMarker[] problemMarkers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		StringBuffer buffer = new StringBuffer("Problem markers found while fragment validation was disabled");
		for (int i = 0; i < problemMarkers.length; i++) {
			buffer.append("\n");
			buffer.append(problemMarkers[i].getAttribute(IMarker.MESSAGE));
		}
		assertEquals(buffer.toString(), 0, problemMarkers.length);

		// enable, some problem markers expected
		JSPFContentProperties.setProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, Boolean.toString(true));
		ValidationFramework.getDefault().validate(new IProject[]{getProject()}, true, false, new NullProgressMonitor());
		problemMarkers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		assertTrue("problem markers not found while fragment validation was enabled", problemMarkers.length != 0);
	}

	public void testFragmentValidationPreferenceOnWorkspace() throws Exception {
		IEclipsePreferences jspInstanceContext = new InstanceScope().getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName());

		String filePath = "/" + PROJECT_NAME + "/WebContent/header.jspf";
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));

		// disable, no problem markers expected
		jspInstanceContext.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, false);
		ValidationFramework.getDefault().validate(new IProject[]{getProject()}, true, false, new NullProgressMonitor());
		IMarker[] problemMarkers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		StringBuffer buffer = new StringBuffer("Problem markers found while fragment validation was disabled");
		for (int i = 0; i < problemMarkers.length; i++) {
			buffer.append("\n");
			buffer.append(problemMarkers[i].getAttribute(IMarker.MESSAGE));
		}
		assertEquals(buffer.toString(), 0, problemMarkers.length);

		// enable, some problem markers expected
		jspInstanceContext.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
		ValidationFramework.getDefault().validate(new IProject[]{getProject()}, true, false, new NullProgressMonitor());
		problemMarkers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		assertTrue("Problem markers not found while fragment validation was enabled", problemMarkers.length != 0);

		// check default value of true
		jspInstanceContext.remove(JSPCorePreferenceNames.VALIDATE_FRAGMENTS);
		ValidationFramework.getDefault().validate(new IProject[]{getProject()}, true, false, new NullProgressMonitor());
		problemMarkers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		assertTrue("Problem markers not found while fragment validation was default", problemMarkers.length != 0);
	}

	/**
	 * @return
	 */
	private IProject getProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}
}
