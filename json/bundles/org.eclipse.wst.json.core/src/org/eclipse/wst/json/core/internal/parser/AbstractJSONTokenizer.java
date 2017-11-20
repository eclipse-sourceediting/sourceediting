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
package org.eclipse.wst.json.core.internal.parser;

import java.util.Stack;

import org.eclipse.wst.json.core.regions.JSONRegionContexts;

public abstract class AbstractJSONTokenizer implements JSONRegionContexts,
		IJSONTokenizer {

	protected final Stack<Boolean> jsonContextStack = new Stack<Boolean>();

	protected String startElement(boolean isArray) {
		jsonContextStack.push(isArray);
		if (isArray) {
			setJSONArrayState(); // yybegin(ST_JSON_ARRAY);
			return JSON_ARRAY_OPEN;
		}
		setJSONObjectState(); // yybegin(ST_JSON_OBJECT);
		return JSON_OBJECT_OPEN;
	}

	protected String endElement(boolean isArray) {
		boolean arrayContent = isArrayParsing();
		if (!jsonContextStack.isEmpty()) {
			jsonContextStack.pop();
		}
		if (isArray) {
			if (arrayContent) {
				if (!jsonContextStack.isEmpty()) {
					setJSONValueState(); // yybegin(ST_JSON_VALUE);
				}
				return JSON_ARRAY_CLOSE;
			}
			return UNDEFINED;
		}
		if (!arrayContent) {
			if (!jsonContextStack.isEmpty()) {
				setJSONValueState(); // yybegin(ST_JSON_VALUE);
			}
			return JSON_OBJECT_CLOSE;
		}
		return UNDEFINED;
	}

	public boolean isArrayParsing() {
		if (jsonContextStack.isEmpty()) {
			return false;
		}
		return jsonContextStack.peek();
	}

	protected abstract void setJSONArrayState();

	protected abstract void setJSONObjectState();

	protected abstract void setJSONValueState();

}
