/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

import junit.framework.TestCase;

/**
 * Tests for the ErrorCustomizationRegistry class.
 */
public class ErrorCustomizationRegistryTest extends TestCase 
{
  private ErrorCustomizationRegistry registry = null;
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception 
  {
	registry = new ErrorCustomizationRegistryWrapper();
    super.setUp();
  }

  /**
   * Test the AddErrorMessageCustomizerToRegistry method with the following tests:
   * 1. Test that adding an error customizer for a namespace is successful.
   * 2. Test that adding two error customizers for a namespace returns both customizers.
   * 3. Test that adding an entry with a null namespace adds the entry for the empty string
   *    namespace.
   */
  public void testAddErrorMessageCustomizerToRegistry()
  {
    // 1. Test that adding an error customizer for a namespace is successful.
	String namespace1 = "http://namespace1";
	IErrorMessageCustomizer customizer = new IErrorMessageCustomizer(){
      public String customizeMessage(ElementInformation elementInfo, String errorKey, Object[] arguments) 
      {
        // This stub for testing does not require an implementation.
		return null;
      }
    };
    registry.addErrorMessageCustomizer(namespace1, customizer);
    IErrorMessageCustomizer[] registeredCustomizers = registry.getCustomizers(namespace1);
    assertEquals("1. There should only be 1 customizer registered for the namespace but there are " + registeredCustomizers.length, 1, registeredCustomizers.length);
    assertEquals("1. The IErrorMessageCustomizer returned is not the same one registered.", customizer, registeredCustomizers[0]);
    
    // 2. Test that adding two error customizers for a namespace returns both customizers.
	IErrorMessageCustomizer customizer2 = new IErrorMessageCustomizer(){
      public String customizeMessage(ElementInformation elementInfo, String errorKey, Object[] arguments) 
      {
        // This stub for testing does not require an implementation.
		return null;
      }
    };
    registry.addErrorMessageCustomizer(namespace1, customizer2);
    registeredCustomizers = registry.getCustomizers(namespace1);
    assertEquals("2. There should be 2 customizers registered for the namespace but there are " + registeredCustomizers.length, 2, registeredCustomizers.length);
    assertEquals("2. The first IErrorMessageCustomizer returned is not the same one registered.", customizer, registeredCustomizers[0]);
    assertEquals("2. The second IErrorMessageCustomizer returned is not the same one registered.", customizer2, registeredCustomizers[1]);
    
    // 3. Test that adding an entry with a null namespace adds the entry for the empty string namespace.
    registry.addErrorMessageCustomizer(null, customizer);
    registeredCustomizers = registry.getCustomizers("");
    assertEquals("3. There should be 1 customizers registered for the namespace but there are " + registeredCustomizers.length, 1, registeredCustomizers.length);
    assertEquals("3. The IErrorMessageCustomizer returned is not the same one registered.", customizer, registeredCustomizers[0]);
  }
	
  /**
   * Test the getCustomizers method with the following tests:
   * 1. Test that requesting an error customizer array for a namespace that has not been
   *    registered produces an empty array.
   * 2. Test that requesting an error customizer array for the null namespace returns the
   *    one registered customizer.
   * 3. Test that requesting an error customizer array for the empty string namespace returns
   *    the one registered customizer.
   */
  public void testGetCustomizers()
  {
	// 1. Test that requesting an error customizer list for a namespace that has not been
	// registered produces an empty list.
	IErrorMessageCustomizer[] customizers = registry.getCustomizers("http://nonregisterednamespace");
	assertEquals("1. The array of customizers for an unregistered namespace is not empty.", 0, customizers.length);
	
	// 2. Test that requesting an error customizer array for the null namespace returns the
	// one registered customizer.
	IErrorMessageCustomizer customizer = new IErrorMessageCustomizer(){
	  public String customizeMessage(ElementInformation elementInfo, String errorKey, Object[] arguments) 
	  {
	    // This stub for testing does not require an implementation.
        return null;
	  }
	};
	registry.addErrorMessageCustomizer(null, customizer);
	customizers = registry.getCustomizers(null);
	assertEquals("2. The array of customizers for a registered customizer for the null namespace does not contain 1 customizer.", 1, customizers.length);
	assertEquals("2. The customizer for the null namespace was not successfully returned.", customizer, customizers[0]);
	
	// 3. Test that requesting an error customizer array for the empty string namespace returns
	//    the one registered customizer for the null namespace.
	customizers = registry.getCustomizers("");
	assertEquals("3. The array of customizers for a registered customizer for the null namespace does not contain 1 customizer for the empty string namespace.", 1, customizers.length);
	assertEquals("3. The customizer for the empty string namespace was not successfully returned.", customizer, customizers[0]);
  }

}
