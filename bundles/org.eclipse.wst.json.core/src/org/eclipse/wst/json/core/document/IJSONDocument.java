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
package org.eclipse.wst.json.core.document;

/**
 * JSON Document API.
 *
 */
public interface IJSONDocument extends IJSONNode {

	/**
	 * Returns the SSE JSON model.
	 * 
	 * @return the SSE JSON model.
	 */
	IJSONModel getModel();

	/**
	 * Create an instance of {@link IJSONObject}.
	 * 
	 * @return an instance of {@link IJSONObject}.
	 */
	IJSONObject createJSONObject();

	/**
	 * Create an instance of {@link IJSONArray}.
	 * 
	 * @return an instance of {@link IJSONArray}.
	 */
	IJSONArray createJSONArray();

	/**
	 * Create JSON pair name/value
	 * 
	 * @param name
	 * @return an instance JSON pair name/value
	 */
	IJSONPair createJSONPair(String name);

	IJSONBooleanValue createBooleanValue();

	IJSONNumberValue createNumberValue();

	IJSONNullValue createNullValue();

	IJSONStringValue createStringValue();
}
