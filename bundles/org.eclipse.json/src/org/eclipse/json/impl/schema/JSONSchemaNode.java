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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject.Member;
import org.eclipse.json.schema.IJSONSchemaNode;
import org.eclipse.json.schema.IJSONSchemaProperty;

public class JSONSchemaNode extends JsonObject implements IJSONSchemaNode {

	private final IJSONSchemaNode parent;
	private final Map<String, IJSONSchemaProperty> properties;

	public JSONSchemaNode(JsonObject jsonObject, IJSONSchemaNode parent) {
		this.parent = parent;
		this.properties = new HashMap<String, IJSONSchemaProperty>();
		walk(jsonObject, this);
	}

	private static void walk(JsonObject json, JSONSchemaNode schemaNode) {
		Member member = null;
		JsonObject properties = (JsonObject) json.get("properties");
		if (properties != null) {
			Iterator<Member> members = properties.iterator();
			while (members.hasNext()) {
				member = members.next();
				schemaNode.addProperty(new JSONSchemaProperty(member.getName(),
						(JsonObject) member.getValue(), schemaNode));
			}
		}
	}

	private void addProperty(IJSONSchemaProperty property) {
		properties.put(property.getName(), property);
	}

	@Override
	public IJSONSchemaNode getParent() {
		return parent;
	}

	@Override
	public IJSONSchemaProperty[] getProperties() {
		return properties.values().toArray(IJSONSchemaProperty.EMPTY_PROPERTY);
	}

}
