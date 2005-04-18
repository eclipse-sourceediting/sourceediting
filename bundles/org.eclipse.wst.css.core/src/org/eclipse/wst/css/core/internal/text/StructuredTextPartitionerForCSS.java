/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.text;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.css.core.internal.provisional.text.ICSSPartitionTypes;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitionTypes;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;

public class StructuredTextPartitionerForCSS extends StructuredTextPartitioner {
	
	public final static String[] legalTypes = new String[]{ICSSPartitionTypes.STYLE, IStructuredPartitionTypes.DEFAULT_PARTITION};

	public StructuredTextPartitionerForCSS() {
		super();
	}

	public String getDefaultPartitionType() {
		return ICSSPartitionTypes.STYLE;
	}

	public String[] getLegalContentTypes() {
		return legalTypes;
	}

	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitionerForCSS();
	}
}