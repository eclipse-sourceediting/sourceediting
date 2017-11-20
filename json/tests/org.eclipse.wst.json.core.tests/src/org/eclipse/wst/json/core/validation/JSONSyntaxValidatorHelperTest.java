/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.validation;

import java.io.StringReader;
import java.util.List;

import org.eclipse.wst.json.core.internal.parser.JSONLineTokenizer;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class JSONSyntaxValidatorHelperTest {

	private static final ISeverityProvider PROVIDER = new ISeverityProvider() {

		@Override
		public int getSeverity(String preferenceName) {
			return 1;
		}
	};

	@Test
	public void noErrorsWithObject() throws Exception {
		IReporter reporter = validate("{}");
		List messages = reporter.getMessages();
		Assert.assertEquals(0, messages.size());
	}

	@Test
	public void missingEndObject() throws Exception {
		IReporter reporter = validate("{");
		List messages = reporter.getMessages();
		Assert.assertEquals(1, messages.size());
		LocalizedMessage msg = (LocalizedMessage) messages.get(0);
		assertMessage(msg, "Missing end object", 1, 1);
	}

	@Test
	public void noErrorsWithArray() throws Exception {
		IReporter reporter = validate("[]");
		List messages = reporter.getMessages();
		Assert.assertEquals(0, messages.size());
	}

	@Test
	public void missingEndArray() throws Exception {
		IReporter reporter = validate("[");
		List messages = reporter.getMessages();
		Assert.assertEquals(1, messages.size());
		LocalizedMessage msg = (LocalizedMessage) messages.get(0);
		assertMessage(msg, "Missing end array", 1, 1);
	}

	@Test
	public void unexpectedColonInArray() throws Exception {
		IReporter reporter = validate("[\"a\":]");
		List messages = reporter.getMessages();
		Assert.assertEquals(1, messages.size());
		LocalizedMessage msg = (LocalizedMessage) messages.get(0);
		assertMessage(msg, "Unexpected token", 1, 1);
	}

	@Test
	public void objectsInArray() throws Exception {
		IReporter reporter = validate("[{},{}]");
		List messages = reporter.getMessages();
		Assert.assertEquals(0, messages.size());
	}

	@Test
	@Ignore
	public void badObjectKey() throws Exception {
		IReporter reporter = validate("{aa}");
		List messages = reporter.getMessages();
		Assert.assertEquals(1, messages.size());
		LocalizedMessage msg = (LocalizedMessage) messages.get(0);
		assertMessage(msg, "Expected object key but found undefined", 1, 1);
		// msg = (LocalizedMessage) messages.get(1);
		// assertMessage(msg, "Unexpected token", 1, 1);
	}

	@Test
	@Ignore
	public void missingEndObjectAndBadObjectKey() throws Exception {
		IReporter reporter = validate("{aa");
		List messages = reporter.getMessages();
		Assert.assertEquals(2, messages.size());
		LocalizedMessage msg = (LocalizedMessage) messages.get(0);
		assertMessage(msg, "Expected object key but found undefined", 1, 1);
		msg = (LocalizedMessage) messages.get(1);
		assertMessage(msg, "Missing end object", 1, 1);
	}

	private void assertMessage(LocalizedMessage msg, String message,
			int lineNumber, int length) {
		Assert.assertEquals(message, msg.getLocalizedMessage());
		Assert.assertEquals(lineNumber, msg.getLineNumber());
		Assert.assertEquals(length, msg.getLength());
	}

	public IReporter validate(String json) {
		IReporter reporter = new MockReporter();
		JSONLineTokenizer tokenizer = new JSONLineTokenizer(new StringReader(
				json));
		JSONSyntaxValidatorHelper.validate(tokenizer, reporter, null, PROVIDER);
		return reporter;
	}
}
