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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONSchemaNode;
import org.eclipse.json.schema.IJSONSchemaProperty;

@SuppressWarnings("serial")
public class JSONSchemaNode extends JsonObject implements IJSONSchemaNode {

	private final IJSONSchemaNode parent;
	private final Map<String, IJSONSchemaProperty> properties;
	private final String reference;
	private final List<String> references;
	private JsonObject jsonObject;

	public JSONSchemaNode(JsonObject jsonObject, IJSONSchemaNode parent) {
		this.parent = parent;
		this.jsonObject = jsonObject;
		this.properties = new HashMap<String, IJSONSchemaProperty>();
		this.references = new ArrayList<String>();
		this.reference = jsonObject.getString("$ref", null);
		walk(jsonObject, this, true);
	}

	private void add(JsonObject jsonObject, IJSONSchemaNode schemaNode, String pref) {
		JsonValue values = jsonObject.get(pref);
		if (values instanceof JsonArray) {
			JsonArray array = (JsonArray) values;
			Iterator<JsonValue> iter = array.iterator();
			while (iter.hasNext()) {
				JsonValue value = iter.next();
				if (value != null) {
					String ref = value.asObject().getString("$ref", null);
					if (ref != null) {
						references.add(ref);
					} else {
						walk(value.asObject(), schemaNode, true);
					}
				}
			}
		}
	}

	private void walk(JsonObject json, IJSONSchemaNode schemaNode, boolean add) {
		JsonObject properties = (JsonObject) json.get("properties");
		addProperties(schemaNode, properties, add);
		if (properties == null) {
			JsonObject items = (JsonObject) json.get("items");
			if (items != null) {
				properties = (JsonObject) items.get("properties");
				addProperties(schemaNode, properties, add);
				String ref = items.getString("$ref", null);
				if (ref != null) {
					if (add) {
						schemaNode.getReferences().add(ref);
					} else {
						schemaNode.getReferences().remove(ref);
					}
				} else {
					walk(items, schemaNode, add);
				}
			}
		}
		add(json, schemaNode, "allOf");
		add(json, schemaNode, "anyOf");
		add(json, schemaNode, "oneOf");
		JsonValue notMember = json.get("not");
		if (notMember != null) {
			walk(notMember.asObject(), schemaNode, false);
		}
	}

	private void addProperties(IJSONSchemaNode schemaNode, JsonObject properties, boolean add) {
		if (properties == null) {
			return;
		}
		Iterator<Member> members = properties.iterator();
		while (members.hasNext()) {
			Member member = members.next();
			if (add) {
				schemaNode.addProperty(
						new JSONSchemaProperty(member.getName(), (JsonObject) member.getValue(), schemaNode));
			} else {
				schemaNode.getProperties().remove(member.getName());
			}
		}
	}

	@Override
	public void addProperty(IJSONSchemaProperty property) {
		properties.put(property.getName(), property);
	}

	@Override
	public IJSONSchemaNode getParent() {
		return parent;
	}

	@Override
	public IJSONSchemaProperty[] getPropertyValues() {
		return properties.values().toArray(IJSONSchemaProperty.EMPTY_PROPERTY);
	}

	@Override
	public JsonObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public String getReference() {
		return reference;
	}

	@Override
	public List<String> getReferences() {
		return references;
	}

	@Override
	public Map<String, IJSONSchemaProperty> getProperties() {
		return properties;
	}
}
