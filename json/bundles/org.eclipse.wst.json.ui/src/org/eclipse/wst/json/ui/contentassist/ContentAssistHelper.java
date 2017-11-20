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
		String defaultValue = null;
		if (property.getDefaultValue() != null) {
			defaultValue = property.getDefaultValue();
		} else if (property.getEnumList() != null && property.getEnumList().size() > 0) {
			defaultValue = property.getEnumList().get(0);
		}
		return getRequiredName(property.getName(), property.getFirstType(), defaultValue);
	}

	public static String getRequiredName(String propertyName, JSONSchemaType type, String defaultValue) {
		StringBuilder name = new StringBuilder("\""); //$NON-NLS-1$
		name.append(propertyName);
		name.append("\""); //$NON-NLS-1$
		if (type != null) {
			name.append(":"); //$NON-NLS-1$
			if (defaultValue != null) {
				if (type == JSONSchemaType.String) {
					name.append("\""); //$NON-NLS-1$
					name.append(defaultValue);
					name.append("\""); //$NON-NLS-1$
				} else {
					name.append(defaultValue);
				}
			} else {
				switch (type) {
				case Array:
					name.append("["); //$NON-NLS-1$
					name.append("]"); //$NON-NLS-1$
					break;
				case Boolean:
					name.append("false"); //$NON-NLS-1$
					break;
				case Null:
					name.append("null"); //$NON-NLS-1$
					break;
				case Object:
					name.append("{"); //$NON-NLS-1$
					name.append("}"); //$NON-NLS-1$
					break;
				case String:
					name.append("\"\""); //$NON-NLS-1$
					break;
				default:
					break;
				}
			}
		}
		return name.toString();
	}

	public static String getRequiredName(String propertyName, JSONSchemaType type) {
		return getRequiredName(propertyName, type, null);
	}
}
