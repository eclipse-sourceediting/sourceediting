/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui;



import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.contentproperties.ContentPropertiesPlugin;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;


public class EditorPlugin extends AbstractUIPlugin implements IStartup {
	
	public final static String ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$

	protected static EditorPlugin instance = null;
	private TextHoverManager fTextHoverManager;

	public EditorPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;
	}

	public static EditorPlugin getDefault() {
		return instance;
	}

	public synchronized static EditorPlugin getInstance() {
		return instance;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

	}

	/**
	 * @see AbstractUIPlugin#initializeDefaultPreferences
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		initializeDefaultEditorPreferences(store);
	}

	/**
	 * This method is here so that other editor plugins can set Editor defaults in their
	 * initialzeDefaultPreferences(...) methods.
	 * @param store
	 */
	public static void initializeDefaultEditorPreferences(IPreferenceStore store) {
		// use the base annotation & quick diff preference page
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		// preferences relative to all sse editors are initialized
		AbstractDecoratedTextEditorPreferenceConstants.initializeDefaultValues(store);

		// these annotation preferences have a different default value than the one the base provides
		store.setDefault("errorIndicationInVerticalRuler", false); //$NON-NLS-1$
		store.setDefault("warningIndicationInVerticalRuler", false); //$NON-NLS-1$

		// these annotation preferences are not part of base text editor preference
		store.setDefault(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, true);
		store.setDefault(CommonEditorPreferenceNames.SHOW_QUICK_FIXABLES, true);

		// matching brackets is not part of base text editor preference
		store.setDefault(CommonEditorPreferenceNames.MATCHING_BRACKETS, true);
		PreferenceConverter.setDefault(store, CommonEditorPreferenceNames.MATCHING_BRACKETS_COLOR, new RGB(192, 192, 192));

		// open file hyperlink navigation is not part of base text editor preference
		String mod1Name = Action.findModifierString(SWT.MOD1); // SWT.COMMAND on Mac; SWT.CONTROL elsewhere
		store.setDefault(CommonEditorPreferenceNames.BROWSER_LIKE_LINKS, true);
		store.setDefault(CommonEditorPreferenceNames.BROWSER_LIKE_LINKS_KEY_MODIFIER, mod1Name);
		store.setDefault(CommonEditorPreferenceNames.BROWSER_LIKE_LINKS_KEY_MODIFIER_MASK, SWT.MOD1);
		PreferenceConverter.setDefault(store, CommonEditorPreferenceNames.LINK_COLOR, new RGB(0, 0, 255));

		// hover help preferences are not part of base text editor preference
		store.setDefault(CommonEditorPreferenceNames.EDITOR_TEXT_HOVER_MODIFIERS, "combinationHover|true|0;problemHover|false|0;documentationHover|false|0;annotationHover|true|"+mod1Name); //$NON-NLS-1$
		store.setDefault(CommonEditorPreferenceNames.EDITOR_SHOW_TEXT_HOVER_AFFORDANCE, true);
		store.setDefault(CommonEditorPreferenceNames.EDITOR_ANNOTATION_ROLL_OVER, false);
		
		// tab width is also a model-side preference so need to load default from there
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, CommonModelPreferenceNames.DEFAULT_TAB_WIDTH);
	}
	
	private static IModelManagerPlugin getModelManagerPlugin() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin;
	}
	
	/**
	 * Return text hover manager
	 * @return TextHoverManager
	 */
	public TextHoverManager getTextHoverManager() {
		if (fTextHoverManager == null) {
			fTextHoverManager = new TextHoverManager();
		}
		return fTextHoverManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		ContentPropertiesPlugin.enableSynchronizer();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#shutdown()
	 */
	public void shutdown() throws CoreException {
		super.shutdown();
		ContentPropertiesPlugin.disableSynchronizer();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#startup()
	 */
	public void startup() throws CoreException {
		super.startup();
		ContentPropertiesPlugin.enableSynchronizer();
	}
}
