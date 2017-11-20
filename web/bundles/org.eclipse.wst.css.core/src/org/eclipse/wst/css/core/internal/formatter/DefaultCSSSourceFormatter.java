/*******************************************************************************
 * Copyright (c) 2004, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.formatter;



import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;


/**
 * 
 */
public class DefaultCSSSourceFormatter extends AbstractCSSSourceFormatter {

	/**
	 * 
	 */
	DefaultCSSSourceFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void appendSpaceBetween(ICSSNode node, CompoundRegion prev, CompoundRegion next, StringBuffer source) {
		if (isCleanup() && !getCleanupStrategy(node).isFormatSource())
			return; // for not formatting case on cleanup action
		final Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		// in selector
		String prevType = prev.getType();
		String nextType = next.getType();

		if (prevType == CSSRegionContexts.CSS_PAGE || prevType == CSSRegionContexts.CSS_CHARSET || prevType == CSSRegionContexts.CSS_ATKEYWORD || prevType == CSSRegionContexts.CSS_FONT_FACE || prevType == CSSRegionContexts.CSS_IMPORT || prevType == CSSRegionContexts.CSS_MEDIA) {
			appendSpaceBefore(node, next, source);
			return;
		}

		if (prevType == CSSRegionContexts.CSS_UNKNOWN && nextType != CSSRegionContexts.CSS_COMMENT) {
			if (prev.getEndOffset() != next.getStartOffset()) { // not
																// sequential
				appendSpaceBefore(node, next, source);
			}
			return;
		}

		if (prevType == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR || prevType == CSSRegionContexts.CSS_COMMENT || nextType == CSSRegionContexts.CSS_COMMENT || nextType == CSSRegionContexts.CSS_LBRACE || nextType == CSSRegionContexts.CSS_UNKNOWN || (prevType == CSSRegionContexts.CSS_SELECTOR_SEPARATOR && preferences.getBoolean(CSSCorePreferenceNames.FORMAT_SPACE_BETWEEN_SELECTORS))) {
			appendSpaceBefore(node, next, source);
			return;
		}
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, String toAppend, StringBuffer source, IRegion exceptFor) {
	}

	/**
	 * 
	 */
	protected void formatBefore(ICSSNode node, ICSSNode child, IRegion region, String toAppend, StringBuffer source) {
	}

	/**
	 * 
	 */
	protected void formatPost(ICSSNode node, StringBuffer source) {
	}

	/**
	 * 
	 */
	protected void formatPost(ICSSNode node, IRegion region, StringBuffer source) {
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, IRegion region, StringBuffer source) {
	}

	/**
	 * 
	 */
	public int getChildInsertPos(ICSSNode node) {
		int n = ((IndexedRegion) node).getEndOffset();
		if (n > 0)
			return n;
		return -1;
	}
}