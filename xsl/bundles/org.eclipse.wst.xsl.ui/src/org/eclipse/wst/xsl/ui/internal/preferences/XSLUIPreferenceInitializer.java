/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla, b.muskalla@gmx.net - [158660] character entities should have their own syntax highlighting preference 
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;

import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.style.IStyleConstantsXSL;

/**
 * Sets default values for XSL UI preferences
 */
public class XSLUIPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = XSLUIPlugin.getDefault().getPreferenceStore();
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();


		// XSL Style Preferences
		String BOLD = " | null | true"; //$NON-NLS-1$
		String JUSTITALIC = " | null | false | true"; //$NON-NLS-1$
		String styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXSL.TAG_ATTRIBUTE_NAME, 127, 0, 127) + BOLD;
		store.setDefault(IStyleConstantsXSL.TAG_ATTRIBUTE_NAME, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE, 42, 0, 255) + JUSTITALIC;
		store.setDefault(IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXSL.TAG_BORDER, 0, 128, 128) + BOLD;
		store.setDefault(IStyleConstantsXSL.TAG_BORDER, styleValue);

		styleValue = ColorHelper.findRGBString(registry, IStyleConstantsXSL.TAG_NAME, 63, 127, 127) + BOLD;
		store.setDefault(IStyleConstantsXSL.TAG_NAME, styleValue);
		
		store.setDefault("xsl.ui.highlighting.tag.bold", true); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.tag.italic", false); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.tag.strikethrough", false); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.tag.underline", false); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.tag.enabled", true); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.tag.color", "#FF00FF"); //$NON-NLS-1$ //$NON-NLS-2$
		
		store.setDefault("xsl.ui.highlighting.attr.bold", true); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.attr.italic", false); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.attr.strikethrough", false); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.attr.underline", false); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.attr.enabled", true); //$NON-NLS-1$
		store.setDefault("xsl.ui.highlighting.attr.color", "#FF00FF"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
