/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;


public class PreferenceInitializer extends AbstractPreferenceInitializer {
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();

		// use the base annotation & quick diff preference page
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);

		// preferences relative to all sse editors are initialized
		AbstractDecoratedTextEditorPreferenceConstants.initializeDefaultValues(store);

		// let annotations show up in the vertical ruler if that's what the
		// preference is
		//		// these annotation preferences have a different default value than
		// the one the base provides
		//		store.setDefault("errorIndicationInVerticalRuler", false);
		// //$NON-NLS-1$
		//		store.setDefault("warningIndicationInVerticalRuler", false);
		// //$NON-NLS-1$

		// these annotation preferences are not part of base text editor
		// preference
		store.setDefault(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, true);
		store.setDefault(CommonEditorPreferenceNames.SHOW_QUICK_FIXABLES, true);

		// matching brackets is not part of base text editor preference
		store.setDefault(CommonEditorPreferenceNames.MATCHING_BRACKETS, true);
		PreferenceConverter.setDefault(store, CommonEditorPreferenceNames.MATCHING_BRACKETS_COLOR, new RGB(192, 192, 192));

		// open file hyperlink navigation is not part of base text editor
		// preference
		String mod1Name = Action.findModifierString(SWT.MOD1); // SWT.COMMAND
		// on Mac;
		// SWT.CONTROL
		// elsewhere
		store.setDefault(CommonEditorPreferenceNames.BROWSER_LIKE_LINKS, true);
		store.setDefault(CommonEditorPreferenceNames.BROWSER_LIKE_LINKS_KEY_MODIFIER, mod1Name);
		PreferenceConverter.setDefault(store, CommonEditorPreferenceNames.LINK_COLOR, new RGB(0, 0, 255));

		// hover help preferences are not part of base text editor preference
		String mod2Name = Action.findModifierString(SWT.MOD2); // SWT.COMMAND
		// on Mac;
		// SWT.CONTROL
		// elsewhere
		store.setDefault(CommonEditorPreferenceNames.EDITOR_TEXT_HOVER_MODIFIERS, "combinationHover|true|0;problemHover|false|0;documentationHover|false|0;annotationHover|true|" + mod2Name); //$NON-NLS-1$
		store.setDefault(CommonEditorPreferenceNames.EDITOR_SHOW_TEXT_HOVER_AFFORDANCE, true);
		store.setDefault(CommonEditorPreferenceNames.EDITOR_ANNOTATION_ROLL_OVER, false);

		// tab width is also a model-side preference so need to load default
		// from there
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, CommonModelPreferenceNames.DEFAULT_TAB_WIDTH);

		// set default read-only foreground color scale value
		store.setDefault(CommonEditorPreferenceNames.READ_ONLY_FOREGROUND_SCALE, 30);
	
	}
}