/*******************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.DefaultCSSSourceFormatter
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;



import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;

public class DefaultJSONSourceFormatter extends AbstractJSONSourceFormatter {

	DefaultJSONSourceFormatter() {
		super();
	}

	protected void appendSpaceBetween(IJSONNode node, CompoundRegion prev, CompoundRegion next, StringBuilder source) {
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action
		String prevType = prev.getType();
		String nextType = next.getType();
		if (prevType == JSONRegionContexts.JSON_COLON) {
			// appendSpaceBefore(node, next, source);
			source.append(' ');
		}
		if (prevType == JSONRegionContexts.JSON_OBJECT_OPEN) {
			String delim = getLineDelimiter(node);
			source.append(delim);
		}
		if (prevType == JSONRegionContexts.JSON_ARRAY_OPEN) {
			String delim = getLineDelimiter(node);
			source.append(delim);
		}

	}

	@Override
	protected void formatPost(IJSONNode node, StringBuilder source) {
	}

	@Override
	protected void formatPost(IJSONNode node, IRegion region, StringBuilder source) {
	}

	@Override
	protected void formatPre(IJSONNode node, StringBuilder source) {
	}

	@Override
	protected void formatPre(IJSONNode node, IRegion region, StringBuilder source) {
	}

}