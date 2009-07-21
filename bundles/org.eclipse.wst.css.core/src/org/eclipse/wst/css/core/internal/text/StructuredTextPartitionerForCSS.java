/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
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
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.text.ICSSPartitions;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;

public class StructuredTextPartitionerForCSS extends StructuredTextPartitioner {
	
	public final static String[] legalTypes = new String[]{ICSSPartitions.STYLE, ICSSPartitions.COMMENT, IStructuredPartitions.DEFAULT_PARTITION};

	public StructuredTextPartitionerForCSS() {
		super();
	}

	public String getDefaultPartitionType() {
		return ICSSPartitions.STYLE;
	}

	public String[] getLegalContentTypes() {
		return legalTypes;
	}

	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitionerForCSS();
	}
	
	/**
	 * <p>This partitioner breaks CSS into only two types of partitions depending on the region type:<br />
	 * <code>COMMENT</code><br />
	 * <code>STYLE</code></p>
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner#getPartitionType(org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion, int)
	 */
	public String getPartitionType(ITextRegion region, int offset) {
		String partitionType;
		
		String regionType = region.getType();
		
		if(regionType == CSSRegionContexts.CSS_COMMENT) {
			partitionType = ICSSPartitions.COMMENT;
		} else {
			partitionType = super.getPartitionType(region, offset);
		}
		
		return partitionType;
	}
}
