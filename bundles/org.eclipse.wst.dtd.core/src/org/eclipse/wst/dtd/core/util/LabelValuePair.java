/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.dtd.core.util;

import org.eclipse.jface.util.Assert;

public class LabelValuePair {

	public String fLabel;
	public Object fValue;

	/**
	 * Creates a new name/value item
	 */
	public LabelValuePair(String label, Object value) {
		Assert.isTrue(label != null);
		fLabel = label;
		fValue = value;
	}
}
