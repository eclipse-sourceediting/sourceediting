/*******************************************************************************
 * Copyright (c) 2007, 2010 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - (STAR) - initial API and implementation
 *     David Carver - (Intalio) - convert to junit 4
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestDelegatingSourceValidatorForXSL.class,
		XSLCompletionTest.class,
		TestExcludeResultPrefixesCompletionProposal.class,
		TestTemplateModeCompletionProposal.class,
		TestHrefCompletionProposal.class, TestElementCompletionProposal.class,
		TestXSLLineStyleProvider.class, TestXMLRegionMap.class,
		TestXSLRegionMap.class, TestXSLHyperlinkDetector.class,
		TestNamedTemplateCompletionProposal.class,
		TestCallTemplateCompletionProposal.class,
		TestXPathXMLCompletionProposal.class, TestTemplateContextTypeXSL.class,
		TestEmptyFileCompletionProposal.class,
		TestContentAssistProcessorFactory.class, TestSemanticHighlighting.class })
public class XSLUITestSuite {

}
