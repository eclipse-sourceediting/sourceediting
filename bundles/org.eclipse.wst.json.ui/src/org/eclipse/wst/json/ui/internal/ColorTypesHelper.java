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
package org.eclipse.wst.json.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.ui.internal.style.IStyleConstantsJSON;

/**
 * Color region types helper.
 * 
 * @author azerr
 *
 */
public class ColorTypesHelper {

	/** Contains region to style mapping */
	private static final Map<String, String> fColorTypes;

	static {
		fColorTypes = new HashMap<String, String>();
		fColorTypes.put(JSONRegionContexts.JSON_OBJECT_OPEN,
				IStyleConstantsJSON.CURLY_BRACE);
		fColorTypes.put(JSONRegionContexts.JSON_OBJECT_CLOSE,
				IStyleConstantsJSON.CURLY_BRACE);
		fColorTypes.put(JSONRegionContexts.JSON_ARRAY_OPEN,
				IStyleConstantsJSON.CURLY_BRACE);
		fColorTypes.put(JSONRegionContexts.JSON_ARRAY_CLOSE,
				IStyleConstantsJSON.CURLY_BRACE);
		fColorTypes.put(JSONRegionContexts.JSON_COLON,
				IStyleConstantsJSON.COLON);
		fColorTypes.put(JSONRegionContexts.JSON_COMMA,
				IStyleConstantsJSON.COMMA);

		fColorTypes.put(JSONRegionContexts.JSON_OBJECT_KEY,
				IStyleConstantsJSON.OBJECT_KEY);
		fColorTypes.put(JSONRegionContexts.JSON_VALUE_STRING,
				IStyleConstantsJSON.VALUE_STRING);
		fColorTypes.put(JSONRegionContexts.JSON_VALUE_NUMBER,
				IStyleConstantsJSON.VALUE_NUMBER);
		fColorTypes.put(JSONRegionContexts.JSON_VALUE_BOOLEAN,
				IStyleConstantsJSON.VALUE_BOOLEAN);
		fColorTypes.put(JSONRegionContexts.JSON_VALUE_NULL,
				IStyleConstantsJSON.VALUE_NULL);
		
		fColorTypes.put(JSONRegionContexts.JSON_COMMENT,
				IStyleConstantsJSON.COMMENT);
	}

	/**
	 * Returns the Color for the given region type {@link JSONRegionContexts}.
	 * 
	 * @param regionType
	 *            the region type.
	 * @return the Color for the given region type {@link JSONRegionContexts}.
	 */
	public static String getColor(String regionType) {
		return fColorTypes.get(regionType);
	}

	public static String getNewStyle(PropertyChangeEvent event) {
		return null;
	}
}
