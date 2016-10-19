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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaNode;
import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;

@SuppressWarnings("serial")
public class JSONSchemaNode extends JsonObject implements IJSONSchemaNode {

	private Map<String, JsonValue> definitions;
	private final IJSONSchemaNode parent;
	private final Map<String, IJSONSchemaProperty> properties;
	private JsonObject jsonObject;
	protected final String description;
	protected JSONSchemaType[] type;
	protected String defaultValue;
	private List<String> enumList;

	public JSONSchemaNode(JsonObject jsonObject, IJSONSchemaNode parent) {
		this.parent = parent;
		this.jsonObject = jsonObject;
		if (this instanceof IJSONSchemaDocument) {
			definitions = new HashMap<String, JsonValue>();
			addDefinitions();
			resolveReferences(jsonObject);
		}
		this.properties = new HashMap<String, IJSONSchemaProperty>();
		walk(jsonObject, this);
		this.description = jsonObject.getString("description", null); //$NON-NLS-1$
		this.type = getType(jsonObject.get(TYPE));
		JsonValue value = jsonObject.get("default"); //$NON-NLS-1$
		if (value != null) {
			defaultValue = removeQuote(value);
		}
		value = jsonObject.get(ENUM);
		if (value instanceof JsonArray) {
			JsonArray array = (JsonArray) value;
			List<JsonValue> items = array.values();
			for (JsonValue v:items) {
				if (v != null) {
					if (type == null || type.length == 0) {
						String str = v.toString();
						if (str.startsWith(QUOTE) && str.endsWith(QUOTE)) {
							type = new JSONSchemaType[] {JSONSchemaType.String};
						}
					}
					addEnum(removeQuote(v));
				}
			}
		}
	}

	private void resolveReferences(JsonObject json) {
		Iterator<Member> members = json.iterator();
		while (members.hasNext()) {
			Member member = members.next();
			JsonValue value = member.getValue();
			resolveReferences(json, member.getName(), value);
		}
	}

	private void resolveReferences(JsonObject parent, String name, JsonValue value) {
		if (value instanceof JsonObject) {
			JsonObject json = value.asObject();
			String ref = json.getString(REF, null);
			if (ref != null && ref.startsWith(DEFINITIONS)) {
				String r = ref.substring(DEFINITIONS.length());
				JsonValue v = definitions.get(r);
				parent.set(name, v);
				//json.remove(REF);
			} else {
				Iterator<Member> members = json.iterator();
				while (members.hasNext()) {
					Member member = members.next();
					JsonValue v = member.getValue();
					resolveReferences(json, member.getName(), v);
				}
			}
		} else if (value instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) value;
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonValue item = jsonArray.get(i);
				if (item instanceof JsonObject) {
					JsonObject json = item.asObject();
					String ref = json.getString(REF, null);
					if (ref != null && ref.startsWith(DEFINITIONS)) {
						String r = ref.substring(DEFINITIONS.length());
						JsonValue v = definitions.get(r);
						jsonArray.set(i, v);
					} else {
						resolveReferences(json);
					}
				}
			}
		}
	}

	private void addDefinitions() {
		JsonValue defs = jsonObject.get("definitions"); //$NON-NLS-1$
		if (defs instanceof JsonObject) {
			Iterator<Member> members = ((JsonObject) defs).iterator();
			while (members.hasNext()) {
				Member member = members.next();
				JsonValue value = member.getValue();
				if (value instanceof JsonObject) {
					definitions.put(member.getName(), member.getValue());
				}
			}
		}
	}

	private void walk(JsonObject json, IJSONSchemaNode schemaNode) {
		JsonObject properties = (JsonObject) json.get(PROPERTIES);
		addProperties(schemaNode, properties);
		add(json, schemaNode, ALL_OF);
		add(json, schemaNode, ANY_OF);
		add(json, schemaNode, ONE_OF);
		JsonValue notMember = json.get(NOT);
		if (notMember != null) {
			walk(notMember.asObject(), schemaNode);
		}
	}

	private void add(JsonObject jsonObject, IJSONSchemaNode schemaNode, String pref) {
		JsonValue values = jsonObject.get(pref);
		if (values instanceof JsonArray) {
			JsonArray array = (JsonArray) values;
			Iterator<JsonValue> iter = array.iterator();
			while (iter.hasNext()) {
				JsonValue value = iter.next();
				if (value != null) {
					walk(value.asObject(), schemaNode);
				}
			}
		}
	}

	private void addProperties(IJSONSchemaNode schemaNode, JsonObject properties) {
		if (properties == null) {
			return;
		}
		Iterator<Member> members = properties.iterator();
		while (members.hasNext()) {
			Member member = members.next();
			schemaNode
					.addProperty(new JSONSchemaProperty(member.getName(), (JsonObject) member.getValue(), schemaNode));
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
	public Map<String, IJSONSchemaProperty> getProperties() {
		return properties;
	}

	protected String[] getRequired(JsonValue value) {
		if (value == null) {
			return null;
		}
		List<String> names = new ArrayList<String>();
		if (value.isString()) {
			String s = value.asString();
			names.add(s);
		} else if (value.isArray()) {
			JsonArray array = (JsonArray) value;
			for (JsonValue item : array) {
				if (item.isString()) {
					String s = item.asString();
					names.add(s);
				}
			}
		}
		return names.toArray(new String[0]);
	}

	protected String removeQuote(JsonValue value) {
		String str = value.toString();
		if (str.startsWith(QUOTE)) {
			str = str.substring(1);
		}
		if (str.endsWith(QUOTE)) {
			str = str.substring(0, str.length()-1);
		}
		return str;
	}

	public static JSONSchemaType[] getType(JsonValue value) {
		if (value == null) {
			return JSONSchemaType.EMPTY_TYPES;
		}
		JSONSchemaType t = null;
		List<JSONSchemaType> types = new ArrayList<JSONSchemaType>();
		if (value.isString()) {
			t = JSONSchemaType.getType(value.asString());
			if (t != null) {
				types.add(t);
			}
		} else if (value.isArray()) {
			JsonArray array = (JsonArray) value;
			for (JsonValue item : array) {
				t = JSONSchemaType.getType(item.asString());
				if (t != null) {
					types.add(t);
				}
			}
		}
		return types.toArray(JSONSchemaType.EMPTY_TYPES);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public JSONSchemaType[] getType() {
		return type;
	}

	@Override
	public JSONSchemaType getFirstType() {
		if (type == null) {
			return null;
		}
		if (type.length == 0) {
			return null;
		}
		return type[0];
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public List<String> getEnumList() {
		return enumList;
	}

	public void addEnum(String item) {
		if (enumList == null) {
			enumList = new LinkedList<String>();
		}
		this.enumList.add(item);
	}

	@Override
	public IJSONSchemaDocument getSchemaDocument() {
		if (this instanceof IJSONSchemaDocument) {
			return (IJSONSchemaDocument) this;
		}
		IJSONSchemaNode p = getParent();
		if (p != null) {
			return p.getSchemaDocument();
		}
		return null;
	}

}
