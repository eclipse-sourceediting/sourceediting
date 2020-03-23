/*******************************************************************************
 * Copyright (c) 2015, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.preferences.AppearancePreferenceNames;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class XSDPreferenceInitializer extends AbstractPreferenceInitializer {
	
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(XSDEditorPlugin.getDefault().getBundle().getSymbolicName());
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		// formatting preferences
	    node.putBoolean(XSDEditorPlugin.CONST_SHOW_INHERITED_CONTENT, false);
	    node.put(XSDEditorPlugin.CONST_XSD_DEFAULT_PREFIX_TEXT, "xsd"); //$NON-NLS-1$
	    node.putBoolean(XSDEditorPlugin.CONST_XSD_LANGUAGE_QUALIFY, false);
	    node.put(XSDEditorPlugin.DEFAULT_PAGE, XSDEditorPlugin.DESIGN_PAGE);
	    node.put(XSDEditorPlugin.CONST_DEFAULT_TARGET_NAMESPACE, XSDEditorPlugin.DEFAULT_TARGET_NAMESPACE);
	    node.putBoolean(XSDEditorPlugin.CONST_SHOW_EXTERNALS, false);
	    node.putBoolean(XSDEditorPlugin.CONST_XSD_IMPORT_CLEANUP, false);
	    node.putBoolean(XSDEditorPlugin.CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG, true);
	    
	    //Even the last item in the list must contain a trailing List separator
	    node.put(XSDEditorPlugin.CONST_PREFERED_BUILT_IN_TYPES,     		
	    		"boolean"+ XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"date" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"dateTime" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"double" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"float" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"hexBinary" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"int" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"string" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR + //$NON-NLS-1$
	    		"time" + XSDEditorPlugin.CUSTOM_LIST_SEPARATOR); //$NON-NLS-1$

		initAppearancePreferences(XSDEditorPlugin.getDefault().getPreferenceStore(), registry);
	}

	private void initAppearancePreferences(IPreferenceStore store, ColorRegistry registry) {
		/* these annotation preferences are not part of base text editor
		 preference */
		store.setDefault(AppearancePreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, true);
		// matching brackets is not part of base text editor preference
		// set default enable folding value
		store.setDefault(AppearancePreferenceNames.FOLDING_ENABLED, true);
		
		// set default for show message dialog when unknown content type in editor
		store.setDefault(AppearancePreferenceNames.SHOW_UNKNOWN_CONTENT_TYPE_MSG, true);

		store.setDefault(AppearancePreferenceNames.SEMANTIC_HIGHLIGHTING, true);

		// matching brackets enablement and color
		store.setDefault(AppearancePreferenceNames.MATCHING_BRACKETS, true);
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.MATCHING_BRACKETS_COLOR, ColorHelper.findRGB(registry, AppearancePreferenceNames.MATCHING_BRACKETS_COLOR, new RGB(192, 192, 192)));

		// set content assist defaults
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND, new RGB(255, 255, 255)));
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND, new RGB(0, 0, 0)));
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND, new RGB(255, 255, 255)));
		PreferenceConverter.setDefault(store, AppearancePreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND, ColorHelper.findRGB(registry, AppearancePreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND, new RGB(0, 0, 0)));
	}
}
