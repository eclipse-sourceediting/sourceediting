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

import org.eclipse.wst.xml.validation.tests.internal.XMLValidatorTestsPlugin;

/**
 * Test the ErrorMessageCustomizerDelegate class.
 */
public class ErrorMessageCustomizerDelegateTest extends TestCase 
{
  /**
   * Test the loadCustomizer method with the following tests:
   * 1. Test that a valid bundle a class succeeds.
   * 2. Test that an invalid class fails and keeps the customizer set to null.
   * 3. Test that a null bundle fails and keeps the customizer set to null.
   */
  public void testLoadCustomizer()
  {
	// 1. Test that a valid bundle a class succeeds.
	ErrorMessageCustomizerDelegateWrapper delegate = new ErrorMessageCustomizerDelegateWrapper(XMLValidatorTestsPlugin.getPlugin().getBundle(), "org.eclipse.wst.xml.core.internal.validation.errorcustomization.SampleErrorMessageCustomizer");
	delegate.loadCustomizer();
	assertNotNull("1. The customizer loaded was null for a valid customizer and bundle.", delegate.getCustomizer());
	
	// 2. Test that an invalid class fails and keeps the customizer set to null.
	ErrorMessageCustomizerDelegateWrapper delegate2 = new ErrorMessageCustomizerDelegateWrapper(XMLValidatorTestsPlugin.getPlugin().getBundle(), "org.eclipse.wst.xml.core.internal.validation.errorcustomization.NonexistantErrorMessageCustomizer");
	delegate2.loadCustomizer();
	assertNull("2. The customizer loaded was not null for an invalid customizer class.", delegate2.getCustomizer());
	
	// 3. Test that a null bundle fails and keeps the customizer set to null.
	ErrorMessageCustomizerDelegateWrapper delegate3 = new ErrorMessageCustomizerDelegateWrapper(null, "org.eclipse.wst.xml.core.internal.validation.errorcustomization.SampleErrorMessageCustomizer");
	delegate3.loadCustomizer();
	assertNull("3. The customizer loaded was not null for a null bundle.", delegate3.getCustomizer());
  }
  
  /**
   * Test the customizeMessage method with the following tests:
   * 1. Test that the message returned is correct for a valid customizer.
   * 2. Test that the message returned is null for an invalid customizer class.
   */
  public void testCustomizeMessage()
  {
    // 1. Test that the message returned is correct for a valid customizer.
    ErrorMessageCustomizerDelegateWrapper delegate = new ErrorMessageCustomizerDelegateWrapper(XMLValidatorTestsPlugin.getPlugin().getBundle(), "org.eclipse.wst.xml.core.internal.validation.errorcustomization.SampleErrorMessageCustomizer");
    String message = delegate.customizeMessage(null, null, null);
	assertEquals("1. The message returned was not AAAA.", "AAAA", message);
	
	// 2. Test that the message returned is null for an invalid customizer class.
	ErrorMessageCustomizerDelegateWrapper delegate2 = new ErrorMessageCustomizerDelegateWrapper(XMLValidatorTestsPlugin.getPlugin().getBundle(), "org.eclipse.wst.xml.core.internal.validation.errorcustomization.NonexistantErrorMessageCustomizer");
	message = delegate2.customizeMessage(null, null, null);
	assertNull("2. The message returned was not null for an invalid customizer.", message);
  }
}
