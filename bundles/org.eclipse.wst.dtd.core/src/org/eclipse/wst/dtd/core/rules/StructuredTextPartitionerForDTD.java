/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.rules;

import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;

public class StructuredTextPartitionerForDTD extends StructuredTextPartitioner {

	public static final String ST_DTD_DEFAULT = "org.eclipse.wst.dtd.default"; //$NON-NLS-1$

	public StructuredTextPartitionerForDTD() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner#getDefault()
	 */
	public String getDefault() {
		return ST_DTD_DEFAULT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner#initLegalContentTypes()
	 */
	protected void initLegalContentTypes() {
		fSupportedTypes = new String[]{ST_DTD_DEFAULT, ST_UNKNOWN_PARTITION};
	}
}
