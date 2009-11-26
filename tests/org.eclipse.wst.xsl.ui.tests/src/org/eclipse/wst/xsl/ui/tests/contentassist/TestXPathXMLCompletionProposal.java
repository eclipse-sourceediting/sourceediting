/*******************************************************************************
 * Copyright (c) Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - bug 244978 - intial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.ui.tests.contentassist;

import java.io.File;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.xsl.ui.tests.AbstractSourceViewerTest;

/**
 * Tests everything about code completion and code assistance.
 * 
 */
public class TestXPathXMLCompletionProposal extends AbstractSourceViewerTest {

	public void testProposalsIncludeXSD() throws Exception {
		fileName = "TestXPathXMLProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);

		ICompletionProposal[] proposals = getProposals(5, 24);
		assertNotNull("Did not find proposals.", proposals);

		for (int i = 0; i < proposals.length; i++) {
			if (proposals[i].getDisplayString().contains("xsd:")) {
				return;
			}
		}
		fail("Did not find XSD proposals.");
	}

	public void testProposalsIncludeAfterColon() throws Exception {
		fileName = "TestXPathXMLProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);

		ICompletionProposal[] proposals = getProposals(11, 44);
		assertNotNull("Did not find proposals.", proposals);

		for (int i = 0; i < proposals.length; i++) {
			if (proposals[i].getDisplayString().contains("xsd:")) {
				return;
			}
		}
		fail("Did not find XSD proposals.");
	}

	public void testProposalsIncludeXSDAfterForwardSlash() throws Exception {
		fileName = "TestXPathXMLProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);

		ICompletionProposal[] proposals = getProposals(8, 41);
		assertNotNull("Did not find proposals.", proposals);

		for (int i = 0; i < proposals.length; i++) {
			if (proposals[i].getDisplayString().contains("xsd:")) {
				return;
			}
		}
		fail("Did not find XSD proposals.");
	}

	public void testTestAttributeProposal() throws Exception {
		fileName = "TestTestAttributeProposals.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);

		ICompletionProposal[] proposals = getProposals(4, 28);
		assertNotNull("Did not find proposals.", proposals);

		for (int i = 0; i < proposals.length; i++) {
			if (proposals[i].getDisplayString().contains("document")) {
				return;
			}
		}
		fail("Did not find XPath proposals for the test attribute.");
	}

	public void testCurlyBraceProposal() throws Exception {
		fileName = "bug294079.xsl";
		String xslFilePath = projectName + File.separator + fileName;
		loadFileForTesting(xslFilePath);

		ICompletionProposal[] proposals = getProposals(4, 12);
		assertNotNull("Did not find proposals.", proposals);

		for (int i = 0; i < proposals.length; i++) {
			if (proposals[i].getDisplayString().contains("document")) {
				return;
			}
		}
		fail("Did not find XPath proposals for the test attribute.");
	}
}