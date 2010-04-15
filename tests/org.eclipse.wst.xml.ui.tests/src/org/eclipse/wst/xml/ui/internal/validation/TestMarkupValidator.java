/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.validation;

import junit.framework.TestCase;

import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalReporter;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.validation.MarkupValidator;

/**
 * Tests MarkupValidator
 */
public class TestMarkupValidator extends TestCase {
	private MarkupValidator fValidator;
	private IReporter fReporter;
	private IStructuredDocument fDocument;

	/**
	 * Validates document
	 * 
	 * @param contents
	 *            contents to set in document
	 * @return true if there was a validation error false otherwise
	 */
	private boolean validateError(String contents) {
		fDocument.set(contents);
		fValidator.validate(new Region(0, fDocument.getLength()), null, fReporter);
		return fReporter.getMessages().isEmpty();
	}

	protected void setUp() throws Exception {
		// just create once
		if (fValidator == null)
			fValidator = new MarkupValidator();
		if (fReporter == null)
			fReporter = new IncrementalReporter(null);
		if (fDocument == null)
			fDocument = StructuredModelManager.getModelManager().createStructuredDocumentFor("onfire.xml", "", null);

		fValidator.connect(fDocument);
	}

	protected void tearDown() throws Exception {
		fValidator.disconnect(fDocument);
	}

	public void testAttributesInEndTag() {
		// test for error
		assertTrue("Should get attributes in end tag error", !validateError("<stop></stop drop=\"roll\">"));

		// test for no error
		assertTrue("Should not get attributes in end tag error", validateError("<stop></stop>"));
	}

	public void testClosingBracket() {
		// test for error
		assertTrue("Should get closing bracket error", !validateError("<stop </stop>"));

		// test for no error
		assertTrue("Should not get closing bracket error", validateError("<stop></stop>"));
	}

	public void testEmptyTag() {
		// test for error
		assertTrue("Should get empty tag error", !validateError("<>"));

		// test for no error
		assertTrue("Should not get empty tag error", validateError("<stop></stop>"));
	}

	public void testAttributeValue() {
		// test for error
		assertTrue("Should get attribute has no value error", !validateError("<stop drop></stop>"));
		assertTrue("Should get attribute missing value error", !validateError("<stop drop=></stop>"));

		// test for no error
		assertTrue("Should not get attribute missing value error", validateError("<stop drop=\"roll\"></stop>"));
	}

	public void testSpaceBeforeName() {
		// test for error
		assertTrue("Should get tag has space before name error", !validateError("<    stop></stop>"));

		// test for no error
		assertTrue("Should not get tag has space before name error", validateError("<stop></stop>"));
	}

	public void testQuotesForAttributeValues() {
		// test for error
		assertTrue("Should get missing end quote error", !validateError("<stop drop=\"></stop>"));
		assertTrue("Should get missing end quote error", !validateError("<stop drop=\"roll></stop>"));
		assertTrue("Should get missing end quote error", !validateError("<stop drop=\'></stop>"));
		assertTrue("Should get missing quotes error", !validateError("<stop drop=roll></stop>"));

		// test for no error
		assertTrue("Should not get missing end quote error", validateError("<stop drop=\"\"></stop>"));
		assertTrue("Should not get missing end quote error", validateError("<stop drop=\"roll\"></stop>"));
		assertTrue("Should not get missing end quote error", validateError("<stop drop=\'\'></stop>"));
		assertTrue("Should not get missing quotes error", validateError("<stop drop=\'roll\'></stop>"));
	}
}
