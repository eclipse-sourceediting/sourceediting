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
package org.eclipse.wst.json.ui.internal.style;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.json.ui.internal.ColorTypesHelper;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * Line style provider for JSON.
 */
public class LineStyleProviderForJSON extends AbstractLineStyleProvider
		implements LineStyleProvider {

	public LineStyleProviderForJSON() {
		super();
	}

	@Override
	protected TextAttribute getAttributeFor(ITextRegion region) {
		if (region != null) {
			String type = region.getType();
			if (type != null) {
				return getAttributeFor(type);
			}
		}
		return (TextAttribute) getTextAttributes().get(
				IStyleConstantsJSON.NORMAL);
	}

	/**
	 * Look up the TextAttribute for the given region context. Might return null
	 * for unusual text.
	 * 
	 * @param type
	 * @return
	 */
	protected TextAttribute getAttributeFor(String type) {
		return (TextAttribute) getTextAttributes().get(
				ColorTypesHelper.getColor(type));
	}

	@Override
	protected void handlePropertyChange(PropertyChangeEvent event) {
		String styleKey = ColorTypesHelper.getNewStyle(event);
		if (event != null) {
			String prefKey = event.getProperty();
			// check if preference changed is a style preference
			if (IStyleConstantsJSON.COLON.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.COLON;
			} else if (IStyleConstantsJSON.COMMA.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.COMMA;
			} else if (IStyleConstantsJSON.CURLY_BRACE.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.CURLY_BRACE;
			} else if (IStyleConstantsJSON.NORMAL.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.NORMAL;
			} else if (IStyleConstantsJSON.OBJECT_KEY.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.OBJECT_KEY;
			} else if (IStyleConstantsJSON.VALUE_BOOLEAN.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.VALUE_BOOLEAN;
			} else if (IStyleConstantsJSON.VALUE_NULL.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.VALUE_NULL;
			} else if (IStyleConstantsJSON.VALUE_NUMBER.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.VALUE_NUMBER;
			} else if (IStyleConstantsJSON.VALUE_STRING.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.VALUE_STRING;
			} else if (IStyleConstantsJSON.COMMENT.equals(prefKey)) {
				styleKey = IStyleConstantsJSON.COMMENT;
			}
		}
		if (styleKey != null) {
			// overwrite style preference with new value
			addTextAttribute(styleKey);
			super.handlePropertyChange(event);
		}
	}

	@Override
	public void release() {
		super.release();
	}

	@Override
	public void loadColors() {
		addTextAttribute(IStyleConstantsJSON.NORMAL);
		addTextAttribute(IStyleConstantsJSON.CURLY_BRACE);
		addTextAttribute(IStyleConstantsJSON.COLON);
		addTextAttribute(IStyleConstantsJSON.COMMA);
		addTextAttribute(IStyleConstantsJSON.COMMENT);

		addTextAttribute(IStyleConstantsJSON.OBJECT_KEY);
		addTextAttribute(IStyleConstantsJSON.VALUE_STRING);
		addTextAttribute(IStyleConstantsJSON.VALUE_NUMBER);
		addTextAttribute(IStyleConstantsJSON.VALUE_BOOLEAN);
		addTextAttribute(IStyleConstantsJSON.VALUE_NULL);
	}

	protected IPreferenceStore getColorPreferences() {
		return JSONUIPlugin.getDefault().getPreferenceStore();
	}
}
