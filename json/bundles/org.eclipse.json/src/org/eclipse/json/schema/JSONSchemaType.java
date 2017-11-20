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
package org.eclipse.json.schema;

/**
 * 
 * @see http://json-schema.org/latest/json-schema-core.html#rfc.section.3.5
 *
 */
public enum JSONSchemaType {

	Array("array"), Boolean("boolean"), Integer("integer"), Number("number"), Null(
			"null"), Object("object"), String("string");

	public static final JSONSchemaType[] EMPTY_TYPES = new JSONSchemaType[0];

	private final String name;

	private JSONSchemaType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static JSONSchemaType getType(String type) {
		if (type == null) {
			return null;
		}
		JSONSchemaType t = null;
		JSONSchemaType[] types = JSONSchemaType.values();
		for (int i = 0; i < types.length; i++) {
			t = types[i];
			if (t.getName().equals(type)) {
				return t;
			}
		}
		return null;
	}
}
