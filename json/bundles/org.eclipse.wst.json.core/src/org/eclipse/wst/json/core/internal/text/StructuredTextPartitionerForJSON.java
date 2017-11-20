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
package org.eclipse.wst.json.core.internal.text;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.core.text.IJSONPartitions;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;

/**
 * Structured text partitioner for JSON.
 * 
 */
public class StructuredTextPartitionerForJSON extends StructuredTextPartitioner {

	public final static String[] legalTypes = new String[] {
			IJSONPartitions.JSON, IJSONPartitions.COMMENT,
			IStructuredPartitions.DEFAULT_PARTITION };

	public StructuredTextPartitionerForJSON() {
		super();
	}

	@Override
	public String getPartitionType(ITextRegion region, int offset) {
		String regionType = region.getType();
		if (regionType == JSONRegionContexts.JSON_COMMENT) {
			return IJSONPartitions.COMMENT;
		}
		return super.getPartitionType(region, offset);
	}

	@Override
	public String getDefaultPartitionType() {
		return IJSONPartitions.JSON;
	}

	@Override
	public String[] getLegalContentTypes() {
		return legalTypes;
	}

	@Override
	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitionerForJSON();
	}

}
