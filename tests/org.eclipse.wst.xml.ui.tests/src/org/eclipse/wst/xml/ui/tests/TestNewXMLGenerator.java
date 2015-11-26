/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.ui.tests;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMContentBuilder;
import org.eclipse.wst.xml.ui.internal.wizards.NewXMLGenerator;

public class TestNewXMLGenerator extends TestCase {
	
	static String PLUGIN_ABSOLUTE_PATH;
	
	static final String SCHEMA_1 = "Schema1"; //$NON-NLS-1$
	static final String SCHEMA_2 = "Schema2"; //$NON-NLS-1$
	static final String ROOT_ELEMENT_1 = "GolfCountryClub"; //$NON-NLS-1$
	static final String ROOT_ELEMENT_2 = "BillInfo"; //$NON-NLS-1$
	
	/**
	 * Sets up the test.
	 */
	protected void setUp() throws Exception {
		super.setUp();	
	}
	
	/**
	 * Test generation with no options selected.
	 */
	public void testNXG_bare() {
		runOneTest(0, ROOT_ELEMENT_1, SCHEMA_1);
	}
	
	/**
	 * Test generation with optional attributes included.
	 */
	public void testNXG_optAttr() {
		runOneTest(DOMContentBuilder.BUILD_OPTIONAL_ATTRIBUTES, ROOT_ELEMENT_1, SCHEMA_1);
	}
	
	/**
	 * Test generation with optional elements included.
	 */
	public void testNXG_optElem() {
		runOneTest(DOMContentBuilder.BUILD_OPTIONAL_ELEMENTS, ROOT_ELEMENT_1, SCHEMA_1);
	}
	
	/**
	 * Test generation with elements set to initial first value.
	 */
	public void testNXG_optFirst() {
		runOneTest(DOMContentBuilder.BUILD_FIRST_CHOICE | DOMContentBuilder.BUILD_FIRST_SUBSTITUTION, ROOT_ELEMENT_1, SCHEMA_1);
	}
	
	/**
	 * Test generation with text nodes.
	 */
	public void testNXG_optTextNodes() {
		runOneTest(DOMContentBuilder.BUILD_TEXT_NODES, ROOT_ELEMENT_1, SCHEMA_1);
	}
	
	/**
	 * Test generation with all options selected.
	 */
	public void testNXG_all() {
		runOneTest(DOMContentBuilder.BUILD_OPTIONAL_ATTRIBUTES | DOMContentBuilder.BUILD_OPTIONAL_ELEMENTS | 
				  DOMContentBuilder.BUILD_FIRST_CHOICE | DOMContentBuilder.BUILD_FIRST_SUBSTITUTION | DOMContentBuilder.BUILD_TEXT_NODES, ROOT_ELEMENT_1, SCHEMA_1);
	}
	
	/**
	 * Test generation with a separate schema in which namespaces must be qualified.
	 */
	public void testNXG_nsQualified(){
		runOneTest(DOMContentBuilder.BUILD_OPTIONAL_ATTRIBUTES | DOMContentBuilder.BUILD_OPTIONAL_ELEMENTS | 
				  DOMContentBuilder.BUILD_FIRST_CHOICE | DOMContentBuilder.BUILD_FIRST_SUBSTITUTION | DOMContentBuilder.BUILD_TEXT_NODES, ROOT_ELEMENT_2, SCHEMA_2);
	}
	
	/**
	 * Runs a generic generate + comparison test.
	 * @param buildPolicy Integer representing the build policy for this generation.
	 * @param rootElement Desired root element.
	 * @param xsdUri URI of the desired XML schema.
	 */
	private void runOneTest(int buildPolicy, String rootElement, String xsdUriFile) {
	  try {
		  // generic setup
		  PLUGIN_ABSOLUTE_PATH = XMLUITestsPlugin.getInstallURL();
		  String uri = "file:///" + PLUGIN_ABSOLUTE_PATH + "testresources/" + xsdUriFile + ".xsd"; //$NON-NLS-3$
		  String[] errorInfo = new String[2];
		  CMDocument cmd = NewXMLGenerator.createCMDocument(uri, errorInfo);
		  NewXMLGenerator generator = new NewXMLGenerator(uri, cmd);
			  
		  String id = xsdUriFile + "-" + rootElement + "-" + buildPolicy;
		  
		  String fileNameResult = PLUGIN_ABSOLUTE_PATH + "testresources/Tested-" + id + ".xml"; //$NON-NLS-2$
		  File nFile = new File(fileNameResult);
		  if (!nFile.exists()) {
			  nFile.createNewFile();
		  }
		  
		  String fileNameCompare = PLUGIN_ABSOLUTE_PATH + "testresources/Compare-" + id + ".xml"; //$NON-NLS-2$
		  
		  generator.setBuildPolicy(buildPolicy);
		  generator.setRootElementName(rootElement);
		  generator.setDefaultSystemId(xsdUriFile + ".xsd"); //$NON-NLS-1$
		  generator.createNamespaceInfoList();
		  generator.createXMLDocument(fileNameResult);
		  
		  XMLDiff differ = new XMLDiff();
		  assertTrue("The XML files are not identical.", differ.diff(fileNameCompare, fileNameResult, "XML")); //$NON-NLS-2$
		  
		  // if we've made it this far, delete the output file
		  remove(nFile);
	  }
	  catch (Exception e) {
		  e.printStackTrace();
	  } 	  		
	}
	
	/**
	 * @param file
	 */
	private void remove(File file) {
		if(file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++) {
				remove(children[i]);
			}
		}
		file.delete();
	}
}
