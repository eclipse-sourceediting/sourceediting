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
package org.eclipse.wst.sse.core.validate;



import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.IndexedRegion;

/**
 */
public interface ValidationAdapter extends INodeAdapter {

	/**
	 */
	void setReporter(ValidationReporter reporter);

	/**
	 */
	void validate(IndexedRegion node);
}
