/*******************************************************************************
 * Copyright (c) 2004, 2014 IBM Corporation and others.
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



import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;


/**
 * 
 */
public class DefaultJSONSourceFormatter extends AbstractJSONSourceFormatter {

	/**
	 * 
	 */
	DefaultJSONSourceFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void appendSpaceBetween(IJSONNode node, CompoundRegion prev, CompoundRegion next, StringBuilder source) {
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action
		final Preferences preferences = JSONCorePlugin.getDefault().getPluginPreferences();
		// in selector
		String prevType = prev.getType();
		String nextType = next.getType();

//		if (prevType == JSONRegionContexts.JSON_PAGE || prevType == JSONRegionContexts.JSON_CHARSET || prevType == JSONRegionContexts.JSON_ATKEYWORD || prevType == JSONRegionContexts.JSON_FONT_FACE || prevType == JSONRegionContexts.JSON_IMPORT || prevType == JSONRegionContexts.JSON_MEDIA) {
//			appendSpaceBefore(node, next, source);
//			return;
//		}

//		if (prevType == JSONRegionContexts.JSON_UNKNOWN && nextType != JSONRegionContexts.JSON_COMMENT) {
//			if (prev.getEndOffset() != next.getStartOffset()) { // not
//																// sequential
//				appendSpaceBefore(node, next, source);
//			}
//			return;
//		}
//
//		if (prevType == JSONRegionContexts.JSON_DECLARATION_VALUE_OPERATOR || prevType == JSONRegionContexts.JSON_COMMENT || nextType == JSONRegionContexts.JSON_COMMENT || nextType == JSONRegionContexts.JSON_LBRACE || nextType == JSONRegionContexts.JSON_UNKNOWN || (prevType == JSONRegionContexts.JSON_SELECTOR_SEPARATOR && preferences.getBoolean(JSONCorePreferenceNames.FORMAT_SPACE_BETWEEN_SELECTORS))) {
//			appendSpaceBefore(node, next, source);
//			return;
//		}
	}

	/**
	 * 
	 */
	protected void formatBefore(IJSONNode node, IJSONNode child, String toAppend, StringBuilder source, IRegion exceptFor) {
	}

	/**
	 * 
	 */
	protected void formatBefore(IJSONNode node, IJSONNode child, IRegion region, String toAppend, StringBuilder source) {
	}

	/**
	 * 
	 */
	protected void formatPost(IJSONNode node, StringBuilder source) {
	}

	/**
	 * 
	 */
	protected void formatPost(IJSONNode node, IRegion region, StringBuilder source) {
	}

	/**
	 * 
	 */
	protected void formatPre(IJSONNode node, StringBuilder source) {
	}

	/**
	 * 
	 */
	protected void formatPre(IJSONNode node, IRegion region, StringBuilder source) {
	}

	/**
	 * 
	 */
	public int getChildInsertPos(IJSONNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0)
			return n;
		return -1;
	}
}