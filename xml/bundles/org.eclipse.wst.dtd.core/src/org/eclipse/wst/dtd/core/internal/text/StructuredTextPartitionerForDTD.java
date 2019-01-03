/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.text;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.dtd.core.text.IDTDPartitions;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;

public class StructuredTextPartitionerForDTD extends StructuredTextPartitioner {

	public StructuredTextPartitionerForDTD() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner#getDefault()
	 */
	public String getDefaultPartitionType() {
		return IDTDPartitions.DTD_DEFAULT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner#initLegalContentTypes()
	 */
	protected void initLegalContentTypes() {
		fSupportedTypes = new String[]{IDTDPartitions.DTD_DEFAULT, IStructuredPartitions.UNKNOWN_PARTITION};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner#newInstance()
	 */
	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitionerForDTD();
	}
}
