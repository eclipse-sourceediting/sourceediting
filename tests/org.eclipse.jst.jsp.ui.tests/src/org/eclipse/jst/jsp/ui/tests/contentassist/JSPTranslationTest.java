/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
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
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.domdocument.DOMModelForJSP;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.modelhandler.ModelHandlerForJSP;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.ui.tests.other.ScannerUnitTests;
import org.eclipse.jst.jsp.ui.tests.util.FileUtil;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUnzipUtility;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUtil;
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
	
	public void testPageDirectiveSessionVariableInFile() throws JavaModelException {
		String jspTestFilePathString = "INCLUDES_TESTS/test189924.jsp";
		ProjectUtil.copyBundleEntryIntoWorkspace("/testfiles/189924/test189924.jsp", jspTestFilePathString);
		IPath jspTestFilePath = new Path(jspTestFilePathString);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(jspTestFilePath);

		verifyTranslationHasNoSessionVariables(file);
	}
	private void verifyTranslationHasNoSessionVariables(IFile file) throws JavaModelException {
		IDOMModel model = null;
		try {
			model = (IDOMModel) getStructuredModelForRead(file);
			setupAdapterFactory(model);

			JSPTranslationAdapter adapter = (JSPTranslationAdapter) model.getDocument().getAdapterFor(IJSPTranslation.class);
			ICompilationUnit cu = adapter.getJSPTranslation().getCompilationUnit();
			cu.makeConsistent(new NullProgressMonitor());
			IType[] types = cu.getAllTypes();
			for (int i = 0; i < types.length; i++) {
				IJavaElement[] members = types[i].getChildren();
				for (int k = 0; k < members.length; k++) {
					// check fields for name "session"
					if (members[k].getElementType() == IJavaElement.FIELD) {
						assertFalse("field named \"session\" exists", members[k].getElementName().equals(JSP11Namespace.ATTR_NAME_SESSION));
					}
					/*
					 * check "public void
					 * _jspService(javax.servlet.http.HttpServletRequest
					 * request, javax.servlet.http.HttpServletResponse
					 * response)" for local variables named "session"
					 */
					else if (members[k].getElementType() == IJavaElement.METHOD && members[k].getElementName().startsWith("_jspService")) {
						ICompilationUnit compilationUnit = ((IMethod) members[k]).getCompilationUnit();
						compilationUnit.makeConsistent(new NullProgressMonitor());
						ASTParser parser = ASTParser.newParser(AST.JLS3);
						parser.setSource(cu);
						ASTNode node = parser.createAST(null);
						node.accept(new ASTVisitor() {
							public boolean visit(VariableDeclarationStatement node) {
								Iterator fragments = node.fragments().iterator();
								while (fragments.hasNext()) {
									VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.next();
									if (fragment.getName().getFullyQualifiedName().equals(JSP11Namespace.ATTR_NAME_SESSION)) {
										String typeName = ((SimpleType) node.getType()).getName().getFullyQualifiedName();
										assertFalse("local variable of type \"javax.servlet.http.HttpSession\" and named \"session\" exists", typeName.equals("javax.servlet.http.HttpSession"));
									}
								}
								return super.visit(node);
							}
						});
					}
				}
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}
	
	public void testPageDirectiveSessionVariableInSegment() throws JavaModelException {
		String jspTestFilePathString = "INCLUDES_TESTS/test189924.jsp";
		ProjectUtil.copyBundleEntryIntoWorkspace("/testfiles/189924/test189924.jsp", jspTestFilePathString);
		jspTestFilePathString = "INCLUDES_TESTS/includer.jsp";
		ProjectUtil.copyBundleEntryIntoWorkspace("/testfiles/189924/includer.jsp", jspTestFilePathString);
		IPath jspTestFilePath = new Path(jspTestFilePathString);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(jspTestFilePath);

		verifyTranslationHasNoSessionVariables(file);	
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
			assertEquals("JSPTranslation java offset:", 1147, javaOffset);
			
			// (<%= | %>)
			int javaTestPostition = translation.getJavaText().indexOf("out.print(   );") + 10;
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
		for (int i = 0; i < keys.length && !found; i++) {
			expectedJspPos = (Position)keys[i];
			if(jspPos.equals(expectedJspPos)) {
				expectedJavaPos = (Position)expected.get(expectedJspPos);
				found = true;
			}
		}
		assertTrue("expected JSP position missing: " + jspPos, found);
		assertNotNull("no expected java position for jspPos: " + printPos(jspPos), expectedJavaPos);
		assertEquals("unexpected java position for jsp position "+printPos(jspPos)+" was:" + printPos(javaPos) + " but expected:" + printPos(expectedJavaPos), expectedJavaPos, javaPos);
	}
	private String printPos(Position pos) {
		return pos != null ? "[" + pos.offset +":"+ pos.length + "]" : "null";
	}
	private HashMap getXMLJSPPositions() {
		if(fXMLJSPPositions == null) {
			fXMLJSPPositions = new HashMap();
			fXMLJSPPositions.put(new Position(882,52), new Position(1158,31));
			fXMLJSPPositions.put(new Position(961,7), new Position(1239,7));
			fXMLJSPPositions.put(new Position(1018,14), new Position(89,14));
			fXMLJSPPositions.put(new Position(640,2), new Position(1077,2));
			fXMLJSPPositions.put(new Position(406,24), new Position(860,24));
			fXMLJSPPositions.put(new Position(685,19), new Position(1086,19));
			fXMLJSPPositions.put(new Position(650,26), new Position(1050,26));
			fXMLJSPPositions.put(new Position(563,9), new Position(989,9));
			fXMLJSPPositions.put(new Position(461,23), new Position(885,23));
			fXMLJSPPositions.put(new Position(522,8), new Position(968,8));
			fXMLJSPPositions.put(new Position(323,44), new Position(277,45));
			fXMLJSPPositions.put(new Position(245,43), new Position(232,44));
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
		ModelHandlerForJSP.ensureTranslationAdapterFactory(sm);
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
