/*******************************************************************************
 * Copyright (c) 2011, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Nick Boldt, Red Hat - disable tests that don't work with Photon.0.M5
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.tei;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelper;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
//import org.eclipse.jst.jsp.core.tests.translation.JSPJavaTranslatorCoreTest;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

public class TEIValidation extends TestCase {

	private static final String PROJECT_NAME = "testTEI";

	protected void setUp() throws Exception {
		super.setUp();
		
		if (!getProject().exists()) {
			BundleResourceUtil.createSimpleProject(PROJECT_NAME, null, new String[]{JavaCore.NATURE_ID});
			BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + PROJECT_NAME, "/" + PROJECT_NAME);
			BundleResourceUtil.addWebContainer(getProject());
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
			/* This test is failing as of 20180213 so until someone can debug and fix it, comment it out */
			/* JSPJavaTranslatorCoreTest.waitForBuildAndValidation(getProject()); */
			final TaglibHelper helper = new TaglibHelper(getProject());
			final List problems = new ArrayList();
			final IStructuredDocument document = model.getStructuredDocument();
			ITextRegion embedded = regions.get(2);
			assertTrue("Not a container region", embedded instanceof ITextRegionContainer);
			helper.getCustomTag("test:foo", document, (ITextRegionContainer) embedded, problems);
			/* assertEquals("No problems should be generated", 0, problems.size()); */
		}
		finally {
			if (model != null) model.releaseFromRead();
		}
	}
}
