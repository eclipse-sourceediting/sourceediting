/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver (Intalio) - bug 323510 - convert to junit 4
 *******************************************************************************/
package org.eclipse.wst.xsl.tests;


import org.eclipse.wst.xsl.core.tests.XSLCoreTestSuite;
import org.eclipse.wst.xsl.exslt.core.tests.EXSLTCoreTestSuite;
import org.eclipse.wst.xsl.exslt.ui.tests.EXSLTUITestSuite;
import org.eclipse.wst.xsl.jaxp.debug.test.AllJAXPDebugTests;
import org.eclipse.wst.xsl.launching.tests.LaunchingSuite;
import org.eclipse.wst.xsl.ui.tests.XSLUITestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * This class specifies all the bundles of this component that provide a test
 * suite to run during automated testing.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { XSLUITestSuite.class, XSLCoreTestSuite.class, LaunchingSuite.class, 
	                   AllJAXPDebugTests.class, EXSLTCoreTestSuite.class, EXSLTUITestSuite.class})

public class AllTestsSuite {
}
