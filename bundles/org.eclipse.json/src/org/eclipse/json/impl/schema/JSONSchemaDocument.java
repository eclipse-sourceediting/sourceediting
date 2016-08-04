/**
 *  Copyright (c) 2013-2016 Angelo ZERR and others.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.json.IValidationReporter;
import org.eclipse.json.jsonpath.IJSONPath;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;

@SuppressWarnings("serial")
public class JSONSchemaDocument extends JSONSchemaNode implements
		IJSONSchemaDocument {
	private static final String DEFINITIONS = "#/definitions/"; //$NON-NLS-1$
	private final Map<String, IJSONSchemaProperty> definitions;

	public JSONSchemaDocument(Reader reader) throws IOException {
		super(JsonObject.readFrom(reader), null);
		this.definitions = new HashMap<String, IJSONSchemaProperty>();
		addDefinitions(getJsonObject());
		resolveReferences();
	}

	private void resolveReferences() {
		resolveReference(this);
		for (IJSONSchemaProperty definition : definitions.values()) {
			resolveReference(definition);
		}
		Collection<IJSONSchemaProperty> props = getProperties().values();
		for (IJSONSchemaProperty property:props) {
			resolveReference(property);
		}
	}

	private void resolveReference(IJSONSchemaProperty node) {
		String reference = node.getReference();
		if (reference != null) {
			String ref = reference.substring(DEFINITIONS.length());
			IJSONSchemaProperty property = definitions.get(ref);
			if (property != null) {
				for (IJSONSchemaProperty p : property.getProperties().values()) {
					node.addProperty(p);
				}
				Collection<IJSONSchemaProperty> props = property.getProperties().values();
				for (IJSONSchemaProperty p : props) {
					resolveReference(p);
				}
			}
		}
		Collection<IJSONSchemaProperty> props = node.getProperties().values();
		for (IJSONSchemaProperty p:props) {
			resolveReference(p);
		}
		List<String> references = node.getReferences();
		for (String ref:references) {
			String r = ref.substring(DEFINITIONS.length());
			IJSONSchemaProperty property = definitions.get(r);
			if (property != null) {
				for (IJSONSchemaProperty p:property.getProperties().values()) {
					node.addProperty(p);
				}
			}
		}
	}

	private void addDefinitions(JsonObject json) {
		Member member = null;
		JsonObject definitions = (JsonObject) json.get("definitions");
		if (definitions != null) {
			Iterator<Member> members = definitions.iterator();
			while (members.hasNext()) {
				member = members.next();
				addDefinition(new JSONSchemaProperty(member.getName(),
						(JsonObject) member.getValue(), this));
			}
		}
	}
	private void addDefinition(IJSONSchemaProperty property) {
		definitions.put(property.getName(), property);
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

	@Override
	public List<String> getEnumList() {
		return null;
	}

	@Override
	public String getDefaultValue() {
		return null;
	}

}
