/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.tei;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelper;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NodeList;

public class TEIValidation extends TestCase {

	private static final String PROJECT_NAME = "testTEI";

	protected void setUp() throws Exception {
		super.setUp();
		
		if (!getProject().exists()) {
			BundleResourceUtil.createSimpleProject(PROJECT_NAME, null, new String[]{JavaCore.NATURE_ID});
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
		}
		assertTrue("project could not be created", getProject().exists());

	}

	private IProject getProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public void testCustomTagInAttribute() throws Exception {
		final String path = "/" + PROJECT_NAME + "/WebContent/test.jsp"; //$NON-NLS-1$
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(file);
		try {
			assertTrue("Not an IDOMModel", model instanceof IDOMModel);
			NodeList divs = ((IDOMModel) model).getDocument().getElementsByTagName("div");
			assertTrue("Missing a div", divs.getLength() > 0);
			IDOMNode node = (IDOMNode) divs.item(0);
			IStructuredDocumentRegion region = node.getStartStructuredDocumentRegion();
			ITextRegionList regions = region.getRegions();
			assertTrue(regions.size() > 2);
			waitForBuildAndValidation(getProject());
			final TaglibHelper helper = new TaglibHelper(getProject());
			final List problems = new ArrayList();
			final IStructuredDocument document = model.getStructuredDocument();
			ITextRegion embedded = regions.get(2);
			assertTrue("Not a container region", embedded instanceof ITextRegionContainer);
			helper.getCustomTag("test:foo", document, (ITextRegionContainer) embedded, problems);
			assertEquals("No problems should be generated", 0, problems.size());
		}
		finally {
			if (model != null) model.releaseFromRead();
		}
	}

	private void waitForBuildAndValidation(IProject project) throws CoreException {
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		waitForBuildAndValidation();
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		waitForBuildAndValidation();
	}

	private void waitForBuildAndValidation() throws CoreException {
		IWorkspaceRoot root = null;
		try {
			ResourcesPlugin.getWorkspace().checkpoint(true);
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor());
			Job.getJobManager().join(ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor());
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor());
			Thread.sleep(200);
			Job.getJobManager().beginRule(root = ResourcesPlugin.getWorkspace().getRoot(), null);
		}
		catch (InterruptedException e) {
		}
		catch (IllegalArgumentException e) {
			fail("Illegal argument exception: " + e.getMessage());
		}
		catch (OperationCanceledException e) {
			fail("Operation canceled exception: " + e.getMessage());
		}
		finally {
			if (root != null) {
				Job.getJobManager().endRule(root);
			}
		}
	}
}
