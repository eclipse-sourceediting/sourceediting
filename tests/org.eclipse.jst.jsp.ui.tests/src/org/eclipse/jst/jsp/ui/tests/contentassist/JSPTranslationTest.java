/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.contentassist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.ui.tests.other.ScannerUnitTests;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLModel;

/**
 * This class unit tests the sed JSPTranslation mechanism.
 * That includes JSPTranslationAdapterFactory, JSPTranslationAdapter, and JSPTranslation.
 * It uses the jsp_include_test.jsp, because of the more complicated translation and offsets.
 * 
 * @author pavery
 */
public class JSPTranslationTest extends TestCase {

	private ProjectUnzipUtility fProjUtil = null;
	private boolean isSetup = false;

	public JSPTranslationTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		if (!this.isSetup) {
			doSetup();
			this.isSetup = true;
		}
	}

	private void doSetup() throws Exception {
		fProjUtil = new ProjectUnzipUtility();

		// root of workspace directory
		Location platformLocation = Platform.getInstanceLocation();

		File zipFile = FileUtil.makeFileFor(ProjectUnzipUtility.PROJECT_ZIPS_FOLDER, "includes_tests.zip", ProjectUnzipUtility.PROJECT_ZIPS_FOLDER);
		fProjUtil.unzipAndImport(zipFile, platformLocation.getURL().getPath());
		fProjUtil.initJavaProject("INCLUDES_TESTS");
	}

	public void testAll() {
		getTranslationAdapterTest();
		translationAdapterTest();
		translationTextTest();
		translationPositionsTest();
	}

	private void translationPositionsTest() {

		XMLModel model = getIncludeTestModelForRead();
		JSPTranslationAdapter adapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);
		JSPTranslation translation = adapter.getJSPTranslation();
		try {
			HashMap java2jsp = translation.getJava2JspMap();
			assertEquals("java2jsp map size:", 11, java2jsp.size());

			HashMap jsp2java = translation.getJsp2JavaMap();
			assertEquals("jsp2java map size:", 3, jsp2java.size());

			// some test positions (out.print("" + | );)
			
			// we need to ignore the classname length in our comparisons
			// with java offsets that we get from the translation
			// since it can vary depending on workspace location
			int classnameLength = translation.getClassname().length();
			
			int jspTestPosition = translation.getJspText().indexOf("<%= ") + 4;
			int javaOffset = translation.getJavaOffset(jspTestPosition) - classnameLength;
			assertEquals("JSPTranslation java offset:", 810, javaOffset);
			
			// (<%= | %>)
			int javaTestPostition = translation.getJavaText().indexOf("out.print(\"\"+\n   \n);") + 14;
			// dont' need to worry about classname length here because we are comparing
			// with a position in the JSP document (which doesn't contain classname)
			int jspOffset = translation.getJspOffset(javaTestPostition);
			assertEquals("JSPTranslation jsp offset:", 564, jspOffset);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	private static String loadChars(InputStream input) {

		StringBuffer s = new StringBuffer();
		try {
			int c = -1;
			while ((c = (char) input.read()) >= 0) {
				if (c > 255)
					break;
				s.append((char) c);
			}
			input.close();
		}
		catch (IOException e) {
			System.out.println("An I/O error occured while loading :");
			System.out.println(e);
		}
		return s.toString();
	}

	private void translationTextTest() {

		XMLModel model = getIncludeTestModelForRead();
		ScannerUnitTests.verifyLengths(model, model.getStructuredDocument().get());
		JSPTranslationAdapter adapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);
		JSPTranslation translation = adapter.getJSPTranslation();
		// named as .bin so no line conversion occurs (\n is in use)
		InputStream in = getClass().getResourceAsStream("translated_text.bin");
		String knownTranslationText = loadChars(in);
		try {
			// improvements: may need finer tuned text compares later
			// for different types of translations (includes, xml-jsp in script, attributes, etc...)
			String text = translation.getJavaText();
			
			int splitStart = text.indexOf(translation.getClassname());
			int splitEnd = splitStart + translation.getClassname().length();
			String newText  = text.substring(0, splitStart) + text.substring(splitEnd);
			
			assertNotNull("JSP translation text:", newText);
			assertEquals("JSP translation text does not match expected", knownTranslationText, newText);
			assertEquals("translation length:", 818, newText.length());
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * 
	 */
	private void translationAdapterTest() {
		XMLModel model = getIncludeTestModelForRead();
		JSPTranslationAdapter adapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);
		JSPTranslation translation = adapter.getJSPTranslation();

		try {
			IJavaProject proj = adapter.getJavaProject();
			assertNotNull("couldn't get java project:" + proj);

			translation = adapter.getJSPTranslation();
			assertNotNull("couldn't get translation:", translation);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	private void getTranslationAdapterTest() {
		XMLModel model = getIncludeTestModelForRead();
		JSPTranslationAdapter adapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);

		try {
			assertNotNull("XMLModel null:", model);

			setupAdapterFactory(model);

			XMLDocument doc = model.getDocument();
			adapter = (JSPTranslationAdapter) doc.getAdapterFor(IJSPTranslation.class);
			assertNotNull("couldn't get JSPTranslationAdapter:", adapter);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * add the factory for JSPTranslationAdapter here
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {
		JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
		sm.getFactoryRegistry().addFactory(factory);
	}

	/**
	 * IMPORTANT - release model (model.releaseFromRead()) after use
	 * @param file
	 * @return
	 */
	private IStructuredModel getStructuredModelForRead(IFile file) {
		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();
			return modelManager.getModelForRead(file);
		}
		catch (IOException ioex) {
			System.out.println("couldn't open file > " + file);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private XMLModel getIncludeTestModelForRead() {

		IPath jspTestFilePath = new Path("INCLUDES_TESTS/jsp_include_test.jsp");
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(jspTestFilePath);
		XMLModel xmlModel = (XMLModel) getStructuredModelForRead(file);
		setupAdapterFactory(xmlModel);
		return xmlModel;
	}
}