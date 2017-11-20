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
package org.eclipse.json.schema;

import java.util.List;
import java.util.Map;

import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;

public interface IJSONSchemaNode {

	static final String NOT = "not"; //$NON-NLS-1$
	static final String ONE_OF = "oneOf"; //$NON-NLS-1$
	static final String ANY_OF = "anyOf"; //$NON-NLS-1$
	static final String ALL_OF = "allOf"; //$NON-NLS-1$
	static final String TYPE = "type"; //$NON-NLS-1$
	static final String REQUIRED = "required"; //$NON-NLS-1$
	static final String MIN_PROPERTIES = "minProperties"; //$NON-NLS-1$
	static final String MAX_PROPERTIES = "maxProperties"; //$NON-NLS-1$
	static final String PATTERN = "pattern"; //$NON-NLS-1$
	static final String MIN_LENGTH = "minLength"; //$NON-NLS-1$
	static final String MAX_LENGTH = "maxLength"; //$NON-NLS-1$
	static final String MULTIPLEOF = "multipleOf"; //$NON-NLS-1$
	static final String MAXIMUM = "maximum"; //$NON-NLS-1$
	static final String EXCLUSIVE_MAXIMUM = "exclusiveMaximum"; //$NON-NLS-1$
	static final String MINIMUM = "minimum"; //$NON-NLS-1$
	static final String EXCLUSIVE_MINIMUM = "exclusiveMinimum"; //$NON-NLS-1$
	static final String MIN_ITEMS = "minItems"; //$NON-NLS-1$
	static final String MAX_ITEMS = "maxItems"; //$NON-NLS-1$
	static final String UNIQUE_ITEMS = "uniqueItems"; //$NON-NLS-1$
	static final String ADDITIONAL_ITEMS = "additionalItems"; //$NON-NLS-1$
	static final String ITEMS = "items"; //$NON-NLS-1$
	static final String ADDITIONAL_PROPERTIES = "additionalProperties"; //$NON-NLS-1$
	static final String PATTERN_PROPERTIES = "patternProperties"; //$NON-NLS-1$
	static final String ENUM = "enum"; //$NON-NLS-1$
	String PROPERTIES = "properties"; //$NON-NLS-1$
	String REF = "$ref"; //$NON-NLS-1$
	String QUOTE = "\""; //$NON-NLS-1$
	String DEFINITIONS = "#/definitions/"; //$NON-NLS-1$

	String getDescription();

	JSONSchemaType[] getType();

	JSONSchemaType getFirstType();

	List<String> getEnumList();

	String getDefaultValue();

	IJSONSchemaNode getParent();

	IJSONSchemaProperty[] getPropertyValues();

	Map<String, IJSONSchemaProperty> getProperties();

	void setJsonObject(JsonObject jsonObject);

	JsonObject getJsonObject();

	void addProperty(IJSONSchemaProperty property);

	IJSONSchemaDocument getSchemaDocument();
}
