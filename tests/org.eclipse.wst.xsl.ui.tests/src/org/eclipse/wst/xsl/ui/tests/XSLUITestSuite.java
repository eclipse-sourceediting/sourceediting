/*******************************************************************************
 * Copyright (c) 2007, 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xsl.ui.internal.validation.TestDelegatingSourceValidatorForXSL;
import org.eclipse.wst.xsl.ui.tests.editor.TestExcludeResultPrefixesCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.editor.TestTemplateModeCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.editor.XSLCompletionTest;


public class XSLUITestSuite extends TestSuite {
	public static Test suite() {
		return new XSLUITestSuite();
	}

	public XSLUITestSuite() {
		super("XSL UI Test Suite");
		addTestSuite(TestDelegatingSourceValidatorForXSL.class);
		addTestSuite(XSLCompletionTest.class);
		addTestSuite(TestExcludeResultPrefixesCompletionProposal.class);
		addTestSuite(TestTemplateModeCompletionProposal.class);
	}
}
