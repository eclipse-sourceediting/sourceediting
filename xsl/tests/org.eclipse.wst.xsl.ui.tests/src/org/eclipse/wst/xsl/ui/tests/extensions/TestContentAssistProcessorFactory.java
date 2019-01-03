/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.tests.extensions;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.wst.xsl.ui.internal.contentassist.ContentAssistProcessorFactory;

import junit.framework.TestCase;

public class TestContentAssistProcessorFactory extends TestCase {

	private static final String XML_CONTENT_ASSIST_PROCESSOR = "org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor";
	private static final String XSL_CONTENT_ASSIST_PROCESSOR = "org.eclipse.wst.xsl.ui.internal.contentassist.XSLContentAssistProcessor";

	public void testContentAssistProcessorsExist() {
		IContentAssistProcessor[] processors = ContentAssistProcessorFactory.createProcessors();
		assertNotNull(processors);
	}
	
	public void testXMLContentAssistProcessorExists() {
		IContentAssistProcessor[] processors = ContentAssistProcessorFactory.createProcessors();
		if (processors == null) {
			fail("Missing Content Assist Processors.");
		}
		assertTrue("Did not find XML Content Assist Processor", findProcessor(processors, XML_CONTENT_ASSIST_PROCESSOR));
	}

	
	public void testXSLContentAssistProcessorExists() {
		IContentAssistProcessor[] processors = ContentAssistProcessorFactory.createProcessors();
		if (processors == null) {
			fail("Missing Content Assist Processors.");
		}
		assertTrue("Did not find XSL Content Assist Processor", findProcessor(processors, XSL_CONTENT_ASSIST_PROCESSOR));
	}
	
	private boolean findProcessor(IContentAssistProcessor[] processors, String name) {
		boolean fndsw = false;
		for (IContentAssistProcessor processor : processors) {
			if (processor.getClass().getName().equals(name)) {
				fndsw = true;
			}
		}
		return fndsw;
	}
}
