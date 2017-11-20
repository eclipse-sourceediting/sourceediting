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
package org.eclipse.json;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.json.impl.schema.JSONSchemaDocument;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.provisonnal.com.eclipsesource.json.ParseException;

public class ValidatorHelper {

	public static void validate(Reader json, IValidationReporter reporter) {
		getJson(json, reporter);
	}

	public static void validate(Reader json, JSONSchemaDocument schema,
			IValidationReporter reporter) {
		JsonValue value = getJson(json, reporter);
		if (value != null) {
			schema.validate(value, reporter);
		}
	}

	private static JsonValue getJson(Reader json, IValidationReporter reporter) {
		try {
			return JsonValue.readFrom(json);
		} catch (IOException e) {
			reporter.addMessage(e.getMessage(), 0, 0, 0);
			return null;
		} catch (ParseException e) {
			reporter.addMessage(e.getMessage(), e.getLine(), e.getColumn(),
					e.getOffset());
			return null;
		}

	}
}
