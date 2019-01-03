/*******************************************************************************
 * Copyright (c) 2009, 2017 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Carver - initial API based off wst.xml.ui.CustomTemplateProposal
 *******************************************************************************/

package org.eclipse.wst.xml.xpath.ui.internal.hander.tests;

import org.eclipse.wst.xml.xpath.core.XPathProcessorPreferences;
import org.eclipse.wst.xml.xpath.core.util.XPathCoreHelper;
import org.osgi.service.prefs.Preferences;

import junit.framework.TestCase;

public class TestXPathProcessorHandler extends TestCase {

	Preferences prefs = null;
	StubXPathProcessorHandler handler = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		prefs = XPathCoreHelper.getPreferences();
		prefs.putBoolean(XPathProcessorPreferences.XPATH_1_0_PROCESSOR, false);
		prefs.putBoolean(XPathProcessorPreferences.XPATH_2_0_PROCESSOR, false);
		prefs.flush();
		handler = new StubXPathProcessorHandler();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		prefs = null;
		handler = null;
	}
	
	public void testToggleStateXpath10() throws Exception {
		handler.toggleState("xpath10");
		prefs = XPathCoreHelper.getPreferences();
		assertTrue("XPath 1.0 preference state not set", prefs.getBoolean(XPathProcessorPreferences.XPATH_1_0_PROCESSOR, false));
	}
	
	public void testToggleStateXpath2() throws Exception {
		handler.toggleState("xpath2");
		prefs = XPathCoreHelper.getPreferences();
		assertTrue("XPath 2.0 preference state not set", prefs.getBoolean(XPathProcessorPreferences.XPATH_2_0_PROCESSOR, false));
	}	
}
