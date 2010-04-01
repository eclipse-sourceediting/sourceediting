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
import org.eclipse.wst.xsl.ui.tests.contentassist.TestCallTemplateCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.TestElementCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.TestEmptyFileCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.TestExcludeResultPrefixesCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.TestHrefCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.TestNamedTemplateCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.TestTemplateModeCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.TestXPathXMLCompletionProposal;
import org.eclipse.wst.xsl.ui.tests.contentassist.XSLCompletionTest;
import org.eclipse.wst.xsl.ui.tests.extensions.TestContentAssistProcessorFactory;
import org.eclipse.wst.xsl.ui.tests.hyperlinkdetector.TestXSLHyperlinkDetector;
import org.eclipse.wst.xsl.ui.tests.style.TestSemanticHighlighting;
import org.eclipse.wst.xsl.ui.tests.style.TestXMLRegionMap;
import org.eclipse.wst.xsl.ui.tests.style.TestXSLLineStyleProvider;
import org.eclipse.wst.xsl.ui.tests.style.TestXSLRegionMap;
import org.eclipse.wst.xsl.ui.tests.templates.TestTemplateContextTypeXSL;


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
		addTestSuite(TestHrefCompletionProposal.class);
		addTestSuite(TestElementCompletionProposal.class);
		addTestSuite(TestXSLLineStyleProvider.class);
		addTestSuite(TestXMLRegionMap.class);
		addTestSuite(TestXSLRegionMap.class);
		addTestSuite(TestXSLHyperlinkDetector.class);
		addTestSuite(TestNamedTemplateCompletionProposal.class);
		addTestSuite(TestCallTemplateCompletionProposal.class);
		addTestSuite(TestXPathXMLCompletionProposal.class);
		addTestSuite(TestTemplateContextTypeXSL.class);
		addTestSuite(TestEmptyFileCompletionProposal.class);
		addTestSuite(TestContentAssistProcessorFactory.class);
		addTestSuite(TestSemanticHighlighting.class);
	}
}
