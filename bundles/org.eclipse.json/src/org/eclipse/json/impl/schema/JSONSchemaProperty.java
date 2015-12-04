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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONSchemaNode;
import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;

public class JSONSchemaProperty extends JSONSchemaNode implements
		IJSONSchemaProperty {

	private final String name;
	private final String description;
	private JSONSchemaType[] type;

	public JSONSchemaProperty(String name, JsonObject jsonObject,
			IJSONSchemaNode parent) {
		super(jsonObject, parent);
		this.name = name;
		this.description = jsonObject.getString("description", null);
		this.type = getType(jsonObject.get("type"));
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
}
