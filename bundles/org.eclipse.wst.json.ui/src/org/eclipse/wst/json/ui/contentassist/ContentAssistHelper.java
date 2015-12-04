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
package org.eclipse.wst.json.ui.contentassist;

import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;
import org.eclipse.wst.json.core.document.IJSONNode;

public class ContentAssistHelper {

	public static String getRequiredName(IJSONNode parent,
			IJSONSchemaProperty property) {
		return getRequiredName(property.getName(), property.getFirstType());
	}

	public static String getRequiredName(String propertyName,
			JSONSchemaType type) {
		StringBuilder name = new StringBuilder("\"");
		name.append(propertyName);
		name.append("\"");
		if (type != null) {
			name.append(":");
			switch (type) {
			case Array:
				name.append("[");
				name.append("]");
				break;
			case Boolean:
				name.append("false");
				break;
			case Null:
				name.append("null");
				break;
			case Object:
				name.append("{");
				name.append("}");
				break;
			case String:
				name.append("\"\"");
				break;
			default:
				break;
			}
		}
		return name.toString();
	}
}
