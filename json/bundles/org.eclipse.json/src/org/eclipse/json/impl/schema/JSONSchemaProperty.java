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

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.schema.IJSONSchemaNode;
import org.eclipse.json.schema.IJSONSchemaProperty;

@SuppressWarnings("serial")
public class JSONSchemaProperty extends JSONSchemaNode implements
		IJSONSchemaProperty {

	protected final String name;
	public JSONSchemaProperty(String name, JsonObject jsonObject,
			IJSONSchemaNode parent) {
		super(jsonObject, parent);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
