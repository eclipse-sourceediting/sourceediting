/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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
import org.eclipse.core.resources.IProject;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.tests.taglibindex.BundleResourceUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
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

}
