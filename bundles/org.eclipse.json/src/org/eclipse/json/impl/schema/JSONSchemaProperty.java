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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONSchemaNode;
import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;

@SuppressWarnings("serial")
public class JSONSchemaProperty extends JSONSchemaNode implements
		IJSONSchemaProperty {

	private static final String QUOTE = "\""; //$NON-NLS-1$
	private final String name;
	private final String description;
	private JSONSchemaType[] type;
	private String defaultValue;
	private List<String> enumList;
	
	public JSONSchemaProperty(String name, JsonObject jsonObject,
			IJSONSchemaNode parent) {
		super(jsonObject, parent);
		this.name = name;
		this.description = jsonObject.getString("description", null); //$NON-NLS-1$
		this.type = getType(jsonObject.get("type")); //$NON-NLS-1$
		JsonValue value = jsonObject.get("default"); //$NON-NLS-1$
		if (value != null) {
			defaultValue = removeQuote(value);
		}
		value = jsonObject.get("enum"); //$NON-NLS-1$
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

	private String removeQuote(JsonValue value) {
		String str = value.toString();
		if (str.startsWith(QUOTE)) {
			str = str.substring(1);
		}
		if (str.endsWith(QUOTE)) {
			str = str.substring(0, str.length()-1);
		}
		return str;
	}

	private JSONSchemaType[] getType(JsonValue value) {
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
	public String getName() {
		return name;
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

	public String getDefaultValue() {
		return defaultValue;
	}

	public List<String> getEnumList() {
		return enumList;
	}

	public void addEnum(String item) {
		if (enumList == null) {
			enumList = new LinkedList<String>();
		}
		this.enumList.add(item);
	}
}
