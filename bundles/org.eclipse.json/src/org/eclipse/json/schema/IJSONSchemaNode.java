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
package org.eclipse.json.schema;

import java.util.List;
import java.util.Map;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;

public interface IJSONSchemaNode {

	IJSONSchemaNode getParent();

	IJSONSchemaProperty[] getPropertyValues();

	List<String> getReferences();

	Map<String, IJSONSchemaProperty> getProperties();

	String getReference();

	void setJsonObject(JsonObject jsonObject);

	JsonObject getJsonObject();

	void addProperty(IJSONSchemaProperty property);
}
