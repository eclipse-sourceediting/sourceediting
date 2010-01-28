/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests.translation;

import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;
import org.eclipse.jst.jsp.core.tests.ProjectUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * 
 */
public class JSPTranslatorPersistenceTest extends TestCase {
	/** The name of the project that all of these tests will use */
	private static final String PROJECT_NAME = "JSPTranslatorPersistenceTest";
	
	/** The location of the testing files */
	private static final String PROJECT_FILES = "/testfiles/JSPTranslatorPersistenceTest";
	
	/** The project that all of the tests use */
	private static IProject fProject;
	
	/**
	 * <p>Default constructor<p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @see #suite()
	 */
	public JSPTranslatorPersistenceTest() {
		super("JSPTranslator Externalization Test");
	}
	
	/**
	 * <p>Constructor that takes a test name.</p>
	 * <p>Use {@link #suite()}</p>
	 * 
	 * @param name The name this test run should have.
	 * 
	 * @see #suite()
	 */
	public JSPTranslatorPersistenceTest(String name) {
		super(name);
	}
	
	/**
	 * <p>Use this method to add these tests to a larger test suite so set up
	 * and tear down can be performed</p>
	 * 
	 * @return a {@link TestSetup} that will run all of the tests in this class
	 * with set up and tear down.
	 */
	public static Test suite() {
		TestSuite ts = new TestSuite(JSPTranslatorPersistenceTest.class, "JSPTranslator Externalization Test");
		return new JSPTranslatorExternalizationTestSetup(ts);
	}
	
	public void testTranslationTextComparison() throws Exception {
		String outFileName = "testTranslationTextComparison.obj";

		JSPTranslator originalTranslator = writeTranslator("Test1.jsp", outFileName);
		StringBuffer originalTranslation = originalTranslator.getTranslation();
		
		JSPTranslator externalizedTranslator = (JSPTranslator)readObject(outFileName);
		StringBuffer externalizedTranslation = externalizedTranslator.getTranslation();
			
		assertEquals("The original translation should be the same as the restored externalized translation",
				originalTranslation.toString(), externalizedTranslation.toString());
	}
	
	/**
	 * <p>This test case follows the general pattern of how a translation is created, then can be
	 * externalized when the workspace is closed, then reloaded when its opened again.</p>
	 * 
	 * @throws Exception
	 */
	public void testCreateTranslationAdapter() throws Exception {
		String outFileName = "testCreateTranslationAdapter.obj";

		IStructuredModel structModel = getModelForRead("Test1.jsp");
		
		//verify there is not already an existing translation adapter
		IDOMDocument domDoc = ((IDOMModel)structModel).getDocument();
		INodeAdapter existingAdapter = domDoc.getAdapterFor(IJSPTranslation.class);
		assertNull("There should be no existing adapter for IJSPTranslation", existingAdapter);
		
		//create a translator and externalize it, then load the externalized translator
		JSPTranslator originalTranslator = writeTranslator(structModel, outFileName);
		JSPTranslator externalizedTranslator = (JSPTranslator)readObject(outFileName);
		
		//create an adaptr from the loaded externalized translator and add it to the doc
		JSPTranslationAdapter restoredAdapter = new JSPTranslationAdapter((IDOMModel)structModel, externalizedTranslator);
		domDoc.addAdapter(restoredAdapter);
		
		//verify we can retrieve the adapter we just set
		existingAdapter = domDoc.getAdapterFor(IJSPTranslation.class);
		assertNotNull("There should now be an existing adapter for IJSPTranslation", existingAdapter);
		assertTrue("Expected " + existingAdapter + " to be an instance of JSPTranslationAdapter",
				existingAdapter instanceof JSPTranslationAdapter);
		JSPTranslationAdapter retrievedAdapter = (JSPTranslationAdapter)existingAdapter;
		JSPTranslationExtension jspTranslationExtension = retrievedAdapter.getJSPTranslation();
		
		/* verify that the original translation is equal to that of the
		 * retrieved adapter created from the previously externalized translator
		 */
		assertEquals("The original translation should be the same as the restored externalized translation",
				originalTranslator.getTranslation().toString(), jspTranslationExtension.getJavaText());
	}
	
	private static JSPTranslator writeTranslator(String jspFileName, String externalizedFileName) throws IOException, CoreException {
		JSPTranslator translator = new JSPTranslator();
		IStructuredModel model = null;
		try {
			model = getModelForRead(jspFileName);
			translator = writeTranslator(model, externalizedFileName);
		} finally {
			if(model != null) {
				model.releaseFromRead();
			}
		}
		
		return translator;
	}
	
	private static IStructuredModel getModelForRead(String fileName) throws IOException, CoreException {
		IFile jspFile = fProject.getFile(fileName);
		assertTrue(jspFile.exists());
		IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(jspFile);
		assertNotNull(model);
		
		return model;
	}
	
	private static JSPTranslator writeTranslator(IStructuredModel jspModel, String externalizedFileName) throws IOException, CoreException {
		JSPTranslator translator = new JSPTranslator();
		translator.reset((IDOMNode)jspModel.getIndexedRegion(0), new NullProgressMonitor());
		translator.translate();
		writeObject(externalizedFileName, translator);
	
		return translator;
	}
	
	/**
	 * <p>Write an {@link Externalizable} object to a file</p>
	 * 
	 * @param fileName
	 * @param obj
	 * @throws IOException
	 */
	private static void writeObject(String fileName, Object obj) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(obj);
		out.close();
	}
	
	/**
	 * <p>Read an {@link Externalizable} object from a file</p>
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Object readObject(String fileName) throws IOException, ClassNotFoundException {
		Object obj = null;
		
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fis);
		obj = in.readObject();
		in.close();
		
		return obj;
	}
	
	/**
	 * <p>This inner class is used to do set up and tear down before and
	 * after (respectively) all tests in the inclosing class have run.</p>
	 */
	private static class JSPTranslatorExternalizationTestSetup extends TestSetup {
		private static final String WTP_AUTOTEST_NONINTERACTIVE = "wtp.autotest.noninteractive";
		private static String previousWTPAutoTestNonInteractivePropValue = null;
		
		/**
		 * Default constructor
		 * 
		 * @param test do setup for the given test
		 */
		public JSPTranslatorExternalizationTestSetup(Test test) {
			super(test);
		}

		/**
		 * <p>This is run once before all of the tests</p>
		 * 
		 * @see junit.extensions.TestSetup#setUp()
		 */
		public void setUp() throws Exception {
			fProject = ProjectUtil.createProject(PROJECT_NAME, null, null);
			ProjectUtil.copyBundleEntriesIntoWorkspace(PROJECT_FILES, PROJECT_NAME);
			
			String noninteractive = System.getProperty(WTP_AUTOTEST_NONINTERACTIVE);
			
			if (noninteractive != null) {
				previousWTPAutoTestNonInteractivePropValue = noninteractive;
			} else {
				previousWTPAutoTestNonInteractivePropValue = "false";
			}
			System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, "true");
		}

		/**
		 * <p>This is run once after all of the tests have been run</p>
		 * 
		 * @see junit.extensions.TestSetup#tearDown()
		 */
		public void tearDown() throws Exception {
			if (previousWTPAutoTestNonInteractivePropValue != null) {
				System.setProperty(WTP_AUTOTEST_NONINTERACTIVE, previousWTPAutoTestNonInteractivePropValue);
			}
			
			fProject.delete(true, new NullProgressMonitor());
		}
	}
}
