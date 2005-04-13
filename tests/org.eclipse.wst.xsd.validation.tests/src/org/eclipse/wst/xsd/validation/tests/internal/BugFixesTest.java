/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.validation.tests.internal;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.xml.uriresolver.XMLCatalog;
import org.eclipse.wst.xml.uriresolver.XMLCatalogEntry;
import org.eclipse.wst.xml.uriresolver.XMLCatalogPlugin;

/**
 * Test class for bug fixes.
 */
public class BugFixesTest extends BaseTestCase
{
  private String BUGFIXES_DIR = "BugFixes/";
  
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(BugFixesTest.class);
  }
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
  {
    super.setUp();
  }
  
  /**
   * Test /BugFixes/Empty.xsd
   */
  public void testEmpty()
  {
    String testname = "Empty";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "EmptyFile/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "EmptyFile/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "EmptyFile/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /BugFixes/InvalidSchemaInXMLCatalog/InvalidSchemaInXMLCatalog.xsd
   */
  public void testInvalidSchemaInXMLCatalog()
  {
    String testname = "InvalidSchemaInXMLCatalog";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "InvalidSchemaInXMLCatalog/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "InvalidSchemaInXMLCatalog/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "InvalidSchemaInXMLCatalog/" + testname + ".xsd-log";
    
    IProject project = createSimpleProject("Project", new String[]{testfile});
    IFile file = project.getFile(testname + ".xsd");
    
    XMLCatalog catalog = XMLCatalogPlugin.getInstance().getDefaultXMLCatalog();
    catalog = catalog.getChildCatalog(XMLCatalog.USER_CATALOG_ID);
    XMLCatalogEntry entry = catalog.createEntry();
    entry.setKey("http://www.example.com/invalid");
    entry.setURI("platform:/resource/Project/InvalidSchemaInXMLCatalog.xsd");
    entry.setType(XMLCatalogEntry.PUBLIC);
    catalog.addEntry(entry);
    
    runTest("platform:/resource/Project/InvalidSchemaInXMLCatalog.xsd"/*FILE_PROTOCOL + file.getLocation().toString()*/, loglocation, idealloglocation);
    
    catalog.removeEntry(entry);
  }
  
  /**
   * Test /BugFixes/MissingClosingSchemaTag/MissingClosingSchemaTag.xsd
   */
  public void testMissingClosingSchemaTag()
  {
    String testname = "MissingClosingSchemaTag";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "MissingClosingSchemaTag/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "MissingClosingSchemaTag/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "MissingClosingSchemaTag/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /BugFixes/ImportXSDWithXSDImportInDiffDir/ImportXSDWithXSDImportInDiffDir.xsd
   */
  public void testImportXSDWithXSDImportInDiffDir()
  {
    String testname = "ImportXSDWithXSDImportInDiffDir";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "ImportXSDWithXSDImportInDiffDir/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "ImportXSDWithXSDImportInDiffDir/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "ImportXSDWithXSDImportInDiffDir/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /BugFixes/ImportInvalidLocation/ImportInvalidLocation.xsd
   */
  public void testImportInvalidLocation()
  {
    String testname = "ImportInvalidLocation";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "ImportInvalidLocation/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "ImportInvalidLocation/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "ImportInvalidLocation/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
}