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
package org.eclipse.wst.css.ui.style;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.css.ui.preferences.CSSPreferenceManager;
import org.eclipse.wst.css.ui.preferences.ui.CSSColorManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;

/**
 * @deprecated Most methods and constants moved to LineStyleProviderForCSS or
 *             IStyleConstantsCSS. TODO remove in C5 or earlier
 */
public class CSSTextColors {
	class TextColors {

		TextColors() {
			super();
		}

		void setTextAttribute(Object key, RGB foreground, RGB background, int style) {
			TextAttribute attribute = new TextAttribute(EditorUtility.getColor(foreground), EditorUtility.getColor(background), style);
			setTextAttribute(key, attribute);
		}

		void setTextAttribute(Object key, TextAttribute attribute) {
			TextAttribute oldAttribute = (TextAttribute) fAttributeTable.get(key);
			if (oldAttribute != null) {
				fAttributeTable.remove(key);
			}
			fAttributeTable.put(key, attribute);
		}

		TextAttribute getTextAttribute(Object key) {
			return (TextAttribute) fAttributeTable.get(key);
		}

		private Map fAttributeTable = new HashMap();
	}

	// color types
	public static final String NORMAL = "NORMAL"; //$NON-NLS-1$
	public static final String ATMARK_RULE = "ATMARK_RULE"; //$NON-NLS-1$
	public static final String SELECTOR = "SELECTOR"; //$NON-NLS-1$
	public static final String MEDIA = "MEDIA"; //$NON-NLS-1$
	public static final String COMMENT = "COMMENT"; //$NON-NLS-1$
	public static final String PROPERTY_NAME = "PROPERTY_NAME"; //$NON-NLS-1$
	public static final String PROPERTY_VALUE = "PROPERTY_VALUE"; //$NON-NLS-1$
	public static final String URI = "URI"; //$NON-NLS-1$
	public static final String STRING = "STRING"; //$NON-NLS-1$
	public static final String COLON = "COLON"; //$NON-NLS-1$
	public static final String SEMI_COLON = "SEMI_COLON"; //$NON-NLS-1$
	public static final String CURLY_BRACE = "CURLY_BRACE"; //$NON-NLS-1$
	public static final String ERROR = "ERROR"; //$NON-NLS-1$
	private static CSSTextColors fInstance = null;
	private TextColors fTextColors = null;
	private Map fPrefToType = null;

	protected CSSTextColors() {
		super();
		applyPreference();
	}

	public synchronized static CSSTextColors getInstance() {
		if (fInstance == null) {
			fInstance = new CSSTextColors();
		}
		return fInstance;
	}

	public void applyPreference() {
		if (fTextColors == null) {
			fTextColors = new TextColors();
		}

		if (fPrefToType == null) {
			fPrefToType = new HashMap();
			fPrefToType.put(NORMAL, CSSColorManager.NORMAL);
			fPrefToType.put(ATMARK_RULE, CSSColorManager.ATMARK_RULE);
			fPrefToType.put(SELECTOR, CSSColorManager.SELECTOR);
			fPrefToType.put(MEDIA, CSSColorManager.MEDIA);
			fPrefToType.put(COMMENT, CSSColorManager.COMMENT);
			fPrefToType.put(PROPERTY_NAME, CSSColorManager.PROPERTY_NAME);
			fPrefToType.put(PROPERTY_VALUE, CSSColorManager.PROPERTY_VALUE);
			fPrefToType.put(URI, CSSColorManager.URI);
			fPrefToType.put(STRING, CSSColorManager.STRING);
			fPrefToType.put(COLON, CSSColorManager.COLON);
			fPrefToType.put(SEMI_COLON, CSSColorManager.SEMI_COLON);
			fPrefToType.put(CURLY_BRACE, CSSColorManager.CURLY_BRACE);
			fPrefToType.put(ERROR, CSSColorManager.ERROR);
		}
		boolean bEnabled = CSSPreferenceManager.getInstance().getColorEnabled();
		CSSColorManager pref = CSSColorManager.getInstance();

		Iterator i = fPrefToType.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			if (bEnabled) {
				String type = (String) fPrefToType.get(key);
				setTextAttribute(key, pref.getForegroundRGB(type), pref.getBackgroundRGB(type), pref.getStyle(type));
			} else {
				setTextAttribute(key, null);
			}
		}
	}

	public void dispose() {
	}

	public TextAttribute getTextAttribute(String type) {
		if (fTextColors == null) {
			applyPreference();
		}
		return fTextColors.getTextAttribute(type);
	}

	private void setTextAttribute(String type, RGB foreground) {
		setTextAttribute(type, foreground, null, SWT.NORMAL);
	}

	private void setTextAttribute(String type, RGB foreground, RGB background, int style) {
		if (fTextColors == null) {
			return;
		}
		fTextColors.setTextAttribute(type, foreground, background, style);
	}
}