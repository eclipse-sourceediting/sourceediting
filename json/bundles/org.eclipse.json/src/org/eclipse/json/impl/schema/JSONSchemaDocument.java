/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
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
import java.util.Collection;

import org.eclipse.json.IValidationReporter;
import org.eclipse.json.jsonpath.IJSONPath;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaProperty;

@SuppressWarnings("serial")
public class JSONSchemaDocument extends JSONSchemaNode implements
		IJSONSchemaDocument {

	public JSONSchemaDocument(Reader reader) throws IOException {
		super(JsonObject.readFrom(reader), null);
	}

	@Override
	public IJSONSchemaProperty getProperty(IJSONPath path) {
		if (path == null || path.getSegments() == null || path.getSegments().length <= 0) {
			return this;
		}
		String[] segments = path.getSegments();
		String segment = segments[0];
		if (segment == null) {
			return null;
		}
		IJSONSchemaProperty property = getProperties().get(segment);
		if (property != null) {
			return getProperty(property, segments, 1);
		}
		return null;
	}

	private IJSONSchemaProperty getProperty(IJSONSchemaProperty node, String[] segments, int level) {
		if (segments.length < (level + 1)) {
			return node;
		}
		String segment = segments[level];
		Collection<IJSONSchemaProperty> props = node.getProperties().values();
		for (IJSONSchemaProperty property : props) {
			if (segment.equals(property.getName())) {
				return getProperty(property, segments, level + 1);
			}
		}
		return null;
	}

	public void validate(JsonValue value, IValidationReporter reporter) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return ""; //$NON-NLS-1$
	}

}
