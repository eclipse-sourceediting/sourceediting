/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
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
import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.domdocument.DOMModelForJSP;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.ui.tests.other.ScannerUnitTests;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

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
	
	private HashMap fXMLJSPPositions;

	public JSPTranslationTest(String name) {
		super(name);
	}
	public JSPTranslationTest() {
		super("JSPTranslationTest");
	}

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

	public void testTranslatePositions() {

		IDOMModel model = getIncludeTestModelForRead();
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
			assertEquals("JSPTranslation java offset:", 972, javaOffset);
			
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


	public void testJSPTranslationText() {

		IDOMModel model = getIncludeTestModelForRead();
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
			
			assertNotNull("JSP translation text:", text);
			assertEquals("JSP translation text does not match expected", knownTranslationText, text);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	public void testJSPTranslationAdapter() {
		IDOMModel model = getIncludeTestModelForRead();
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

	/**
	 * Makes sure both beans are translated even though they are
	 * right next to each other with no space.
	 * 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=103004
	 */
	public void testUseBeanNoSpace() {
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("INCLUDES_TESTS/useBean_tester.jsp"));
		DOMModelForJSP sModel = (DOMModelForJSP)getStructuredModelForRead(f);
		try {
			setupAdapterFactory(sModel);
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) sModel.getDocument().getAdapterFor(IJSPTranslation.class);
			JSPTranslation translation = adapter.getJSPTranslation();
			String javaText = translation.getJavaText();
			boolean bean1 = javaText.indexOf("javax.swing.JButton x = null;") != -1;
			boolean bean2 = javaText.indexOf("javax.swing.JButton y = null;") != -1;
			assertTrue(bean1);
			assertTrue(bean2);
		}
		finally {
			if(sModel != null)
				sModel.releaseFromRead();
		}
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=86382
	 */
	public void testXMLJSPTranslationText() {
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("INCLUDES_TESTS/xml-jsp/most-tags-xml-jsp.jsp"));
		DOMModelForJSP sModel = (DOMModelForJSP)getStructuredModelForRead(f);
		try {
			setupAdapterFactory(sModel);
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) sModel.getDocument().getAdapterFor(IJSPTranslation.class);
			JSPTranslation translation = adapter.getJSPTranslation();
			
			String javaText = translation.getJavaText();
			
			// named as .bin so no line conversion occurs (\n is in use)
			InputStream in = getClass().getResourceAsStream("translated_xml_jsp.bin");
			String knownTranslationText = loadChars(in);
			
			assertEquals(knownTranslationText, javaText);
		}
		finally {
			if(sModel != null)
				sModel.releaseFromRead();
		}
	}
	
	public void testXMLJSPMapping() {
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("INCLUDES_TESTS/xml-jsp/most-tags-xml-jsp.jsp"));
		DOMModelForJSP sModel = (DOMModelForJSP)getStructuredModelForRead(f);
		try {
			setupAdapterFactory(sModel);
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) sModel.getDocument().getAdapterFor(IJSPTranslation.class);
			JSPTranslation translation = adapter.getJSPTranslation();
			
			HashMap jsp2java = translation.getJsp2JavaMap();
			Object[] jspRanges = jsp2java.keySet().toArray();
			Position jspPos = null;
			Position javaPos = null;
			for (int i = 0; i < jspRanges.length; i++) {
				jspPos = (Position)jspRanges[i];
				javaPos = (Position)jsp2java.get(jspPos);
				//System.out.println("jsp:" + printPos(jspPos) + " >> java:" + printPos(javaPos));
				checkPosition(jspPos, javaPos);
			}
		}
		finally {
			if(sModel != null)
				sModel.releaseFromRead();
		}
	}
	
	public void testXMLJSPCDATAText() {
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path("INCLUDES_TESTS/cdata/cdata.jsp"));
		DOMModelForJSP sModel = (DOMModelForJSP)getStructuredModelForRead(f);
		try {
			setupAdapterFactory(sModel);
			JSPTranslationAdapter adapter = (JSPTranslationAdapter) sModel.getDocument().getAdapterFor(IJSPTranslation.class);
			JSPTranslation translation = adapter.getJSPTranslation();
			
			String transText = translation.getJavaText();
			
//			 named as .bin so no line conversion occurs (\n is in use)
			InputStream in = getClass().getResourceAsStream("translated_xml_jsp_cdata.bin");
			String knownText = loadChars(in);
			
			assertEquals(knownText, transText);
		}
		finally {
			if(sModel != null)
				sModel.releaseFromRead();
		}
	}
	
	private void checkPosition(Position jspPos, Position javaPos) {
		
		HashMap expected = getXMLJSPPositions();
		Object[] keys =expected.keySet().toArray();
		Position expectedJspPos= null;
		Position expectedJavaPos = null;
		boolean found = false;
		for (int i = 0; i < keys.length; i++) {
			expectedJspPos = (Position)keys[i];
			if(jspPos.equals(expectedJspPos)) {
				expectedJavaPos = (Position)expected.get(expectedJspPos);
				found = true;
			}
		}
		assertTrue("expected JSP position missing: " + jspPos, found);
		assertNotNull("no expected java position for jspPos: " + printPos(jspPos), expectedJavaPos);
		assertEquals("unexpected java position for jsp position "+printPos(jspPos)+" was:" + printPos(javaPos) + " but expected:" + printPos(expectedJavaPos), javaPos, expectedJavaPos);
	}
	private String printPos(Position pos) {
		return pos != null ? "[" + pos.offset +":"+ pos.length + "]" : "null";
	}
	private HashMap getXMLJSPPositions() {
		if(fXMLJSPPositions == null) {
			fXMLJSPPositions = new HashMap();
			fXMLJSPPositions.put(new Position(882,52), new Position(920,31));
			fXMLJSPPositions.put(new Position(961,7), new Position(952,7));
			fXMLJSPPositions.put(new Position(1018,14), new Position(89,14));
			fXMLJSPPositions.put(new Position(640,2), new Position(888,2));
			fXMLJSPPositions.put(new Position(406,24), new Position(759,24));
			fXMLJSPPositions.put(new Position(685,19), new Position(897,19));
			fXMLJSPPositions.put(new Position(650,26), new Position(861,26));
			fXMLJSPPositions.put(new Position(563,9), new Position(848,9));
			fXMLJSPPositions.put(new Position(461,23), new Position(784,23));
			fXMLJSPPositions.put(new Position(522,8), new Position(822,8));
			fXMLJSPPositions.put(new Position(323,44), new Position(274,45));
			fXMLJSPPositions.put(new Position(245,43), new Position(229,44));
		}
		return fXMLJSPPositions;
	}
	
	private static String loadChars(InputStream input) {

		StringBuffer s = new StringBuffer();
		try {
			int c = -1;
			while ((c = input.read()) >= 0) {
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

	private IDOMModel getIncludeTestModelForRead() {

		IPath jspTestFilePath = new Path("INCLUDES_TESTS/jsp_include_test.jsp");
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(jspTestFilePath);
		IDOMModel xmlModel = (IDOMModel) getStructuredModelForRead(file);
		setupAdapterFactory(xmlModel);
		return xmlModel;
	}
}
