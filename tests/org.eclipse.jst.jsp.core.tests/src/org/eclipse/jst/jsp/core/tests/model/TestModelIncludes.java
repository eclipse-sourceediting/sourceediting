/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;

/**
 * Tests for JSP include directives
 */
public class TestModelIncludes extends TestCase {
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

	/**
	 * Tests the custom tag content model when single line fragments are used
	 * without trailing white space
	 * 
	 * @throws Exception
	 */
	public void testContentModelSingleLineIncludedFileWithNoSpacesButWithTaglibInInclude() throws Exception {
		String projectName = "prj119576_a";

		BundleResourceUtil.createSimpleProject(projectName, null, null);
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + projectName, "/" + projectName);
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists());

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("/prj119576_a/WebContent/body2.jsp"));
		IDOMModel model = null;
		try {
			model = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(file);
			assertTrue("model has no content", model.getStructuredDocument().getLength() > 0);

			Element element = (Element) model.getIndexedRegion(75);
			CMElementDeclaration ed = ModelQueryUtil.getModelQuery(model).getCMElementDeclaration(element);
			assertNotNull("no (TLD) element declaration found for " + element.getNodeName(), ed);
			assertTrue("not a wrapping content model element declaration: " + ed.getNodeName(), ed instanceof CMNodeWrapper);
			assertTrue("not a taglib content model element declaration: " + ed.getNodeName(), ((CMNodeWrapper) ed).getOriginNode() instanceof TLDElementDeclaration);
			String tagClassName = ((TLDElementDeclaration) ((CMNodeWrapper) ed).getOriginNode()).getTagclass();
			assertNotNull("no tag class name found", tagClassName);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Verify included files are translated properly when they contain a
	 * multiple lines
	 * 
	 * @throws Exception
	 */
	public void testTranslateMultiLineIncludedFileWithSpacesAndScriptletInInclude() throws Exception {
		String projectName = "prj119576_c";

		BundleResourceUtil.createSimpleProject(projectName, null, null);
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + projectName, "/" + projectName);
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists());

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("/" + projectName + "/WebContent/body3.jsp"));
		IDOMModel model = null;
		try {
			model = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(file);
			assertTrue("model has no content", model.getStructuredDocument().getLength() > 0);

			JSPTranslationAdapterFactory factory = JSPTranslationAdapterFactory.getDefault();
			model.getFactoryRegistry().addFactory(factory);

			JSPTranslationAdapter adapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);
			String source = adapter.getJSPTranslation().getJavaText();
			assertTrue("scriptlet with variable declaration not found\n" + source, source.indexOf("java.util.Date headerDate") > -1);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Verify included files are translated properly when they contain a
	 * single line and document region and no trailing white space.
	 * 
	 * @throws Exception
	 */
	public void testTranslateSingleLineIncludedFileWithNoSpacesButScriptletInInclude() throws Exception {
		String projectName = "prj119576_b";

		BundleResourceUtil.createSimpleProject(projectName, null, null);
		BundleResourceUtil.copyBundleEntriesIntoWorkspace("/testfiles/" + projectName, "/" + projectName);
		assertTrue("project could not be created", ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists());

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("/" + projectName + "/WebContent/body3.jsp"));
		IDOMModel model = null;
		try {
			model = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(file);
			assertTrue("model has no content", model.getStructuredDocument().getLength() > 0);

			JSPTranslationAdapterFactory factory = JSPTranslationAdapterFactory.getDefault();
			model.getFactoryRegistry().addFactory(factory);

			JSPTranslationAdapter adapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);
			String source = adapter.getJSPTranslation().getJavaText();
			assertTrue("scriptlet with variable declaration not found", source.indexOf("java.util.Date headerDate") > -1);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
}
