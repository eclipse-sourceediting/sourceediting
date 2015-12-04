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
package org.eclipse.json.impl.schema;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.json.IValidationReporter;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONPath;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;

public class JSONSchemaDocument extends JSONSchemaNode implements
		IJSONSchemaDocument {

	public JSONSchemaDocument(Reader reader) throws IOException {
		super(JsonObject.readFrom(reader), null);
	}

	@Override
	public IJSONSchemaProperty getProperty(IJSONPath path) {
		return this;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public JSONSchemaType[] getType() {
		return null;
	}

	@Override
	public JSONSchemaType getFirstType() {
		return null;
	}

	public void validate(JsonValue value, IValidationReporter reporter) {
		// TODO Auto-generated method stub

	}

}
