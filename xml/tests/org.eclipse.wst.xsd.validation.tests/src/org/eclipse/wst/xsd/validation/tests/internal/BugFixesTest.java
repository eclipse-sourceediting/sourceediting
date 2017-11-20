/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver, Standards for Technology in Automotive Retail, bug 147033
 *******************************************************************************/
package org.eclipse.wst.xsd.validation.tests.internal;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.eclipse.wst.xsd.core.internal.validation.XSDValidationConfiguration;



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
    String testfile = PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "InvalidSchemaInXMLCatalog/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "InvalidSchemaInXMLCatalog/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "InvalidSchemaInXMLCatalog/" + testname + ".xsd-log";
    
    createSimpleProject("Project", new String[]{testfile});
    
    
    ICatalog catalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
    INextCatalog[] nextCatalogs = catalog.getNextCatalogs();
    for (int i = 0; i < nextCatalogs.length; i++)
	{
		INextCatalog nextCatalog = nextCatalogs[i];
		if(XMLCorePlugin.USER_CATALOG_ID.equals(nextCatalog.getId())){
			ICatalog userCatalog = nextCatalog.getReferencedCatalog();
			if(userCatalog != null)
			{
				ICatalogEntry catalogEntry = (ICatalogEntry)userCatalog.createCatalogElement(ICatalogEntry.ENTRY_TYPE_PUBLIC);
			    catalogEntry.setKey("testKey");
			    catalogEntry.setURI("http://testuri");
			    userCatalog.addCatalogElement(catalogEntry);
			    runTest("platform:/resource/Project/InvalidSchemaInXMLCatalog.xsd"/*FILE_PROTOCOL + file.getLocation().toString()*/, loglocation, idealloglocation);
			    catalog.removeCatalogElement(catalogEntry);
			}	
		}	
	} 
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
  
  /**
   * Test /BugFixes/TwoOpenBrackets/TwoOpenBrackets.xsd
   */
  public void testTwoOpenBrackets()
  {
    String testname = "TwoOpenBrackets";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "TwoOpenBrackets/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "TwoOpenBrackets/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "TwoOpenBrackets/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /BugFixes/ImportWithIncorrectSlash/A.xsd
   */
  public void testImportWithIncorrectSlash()
  {
    String testname = "A";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "ImportWithIncorrectSlash/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "ImportWithIncorrectSlash/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "ImportWithIncorrectSlash/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /BugFixes/HonourAllSchemaLocations/dog.xsd
   */
  public void testHonourAllSchemaLocations()
  {
    String testname = "dog";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "HonourAllSchemaLocations/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "HonourAllSchemaLocations/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "HonourAllSchemaLocations/" + testname + ".xsd-log";
    
    XSDValidationConfiguration configuration = new XSDValidationConfiguration();
    try
    {
      configuration.setFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, true);
    }
    catch(Exception e)
    {
      fail("Unable to set the HONOUR_ALL_SCHEMA_LOCATIONS feature to true: " + e);
    }
    runTest(testfile, loglocation, idealloglocation, configuration);
  }
  
  /**
   * Test /BugFixes/XSDRegisteredWithCatalog/InvalidSchemaWithNamespaceInCatalog.xsd
   */
  public void testInvalidSchemaWithNamespaceInCatalog()
  {
    String testname = "InvalidSchemaWithNamespaceInCatalog";
    String testfile = PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "XSDRegisteredWithCatalog/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "XSDRegisteredWithCatalog/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "XSDRegisteredWithCatalog/" + testname + ".xsd-log";
    
    createSimpleProject("Project", new String[]{testfile});
    
    
    ICatalog catalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
    INextCatalog[] nextCatalogs = catalog.getNextCatalogs();
    for (int i = 0; i < nextCatalogs.length; i++)
	{
		INextCatalog nextCatalog = nextCatalogs[i];
		if(XMLCorePlugin.USER_CATALOG_ID.equals(nextCatalog.getId())){
			ICatalog userCatalog = nextCatalog.getReferencedCatalog();
			if(userCatalog != null)
			{
				ICatalogEntry catalogEntry = (ICatalogEntry)userCatalog.createCatalogElement(ICatalogEntry.ENTRY_TYPE_PUBLIC);
			    catalogEntry.setKey("http://www.eclipse.org/webtools/Catalogue");
			    catalogEntry.setURI("platform:/resource/Project/InvalidSchemaInXMLCatalog.xsd");
			    userCatalog.addCatalogElement(catalogEntry);
			    runTest("platform:/resource/Project/InvalidSchemaWithNamespaceInCatalog.xsd", loglocation, idealloglocation);
			    catalog.removeCatalogElement(catalogEntry);
			}	
		}	
	}
  }
  
  /**
   * Test /BugFixes/FullConformance/FullConformance.xsd
   * bug 147033
   */
  public void testFullConformance()
  {
    String testname = "FullConformance";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "FullConformance/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "FullConformance/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "FullConformance/" + testname + ".xsd-log";
    
    XSDValidationConfiguration configuration = new XSDValidationConfiguration();
    try
    {
      configuration.setFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, true);
      configuration.setFeature(XSDValidationConfiguration.FULL_SCHEMA_CONFORMANCE, true);
    }
    catch(Exception e)
    {
      fail("Unable to set the FULL_SCHEMA_CONFORMANCE feature to true: " + e);
    }
    runTest(testfile, loglocation, idealloglocation, configuration);
  }
 
}