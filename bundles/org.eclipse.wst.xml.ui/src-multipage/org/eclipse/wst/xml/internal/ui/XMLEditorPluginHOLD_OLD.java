/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.internal.ui;



import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.registry.embedded.EmbeddedAdapterFactoryRegistryImpl;
import org.eclipse.wst.xml.core.XMLPreferenceNames;


public class XMLEditorPluginHOLD_OLD extends AbstractUIPlugin {

	public final static String PLUGIN_ID = "org.eclipse.wst.xml.ui.internal.XMLEditorPluginHOLD_OLD"; //$NON-NLS-1$
	protected static XMLEditorPluginHOLD_OLD instance = null;

	/**
	 * XMLEditorPlugin constructor comment.
	 * 
	 * @param descriptor
	 *            com.ibm.itp.core.api.plugins.IPluginDescriptor
	 */
	public XMLEditorPluginHOLD_OLD(IPluginDescriptor descriptor) {
		super(descriptor);
		instance = this;

		// reference the preference store so
		// initializeDefaultPreferences(IPreferenceStore preferenceStore) is
		// called
		getPreferenceStore();
	}

	public static XMLEditorPluginHOLD_OLD getDefault() {
		return instance;
	}

	public synchronized static XMLEditorPluginHOLD_OLD getInstance() {
		return instance;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	public AdapterFactoryRegistry getEmbeddedAdapterFactoryRegistry() {
		return EmbeddedAdapterFactoryRegistryImpl.getInstance();

	}

	/**
	 * Creates XML Preference store and initializes its default values
	 */
	protected void initializeDefaultPreferences(IPreferenceStore preferenceStore) {
		preferenceStore.setDefault(XMLPreferenceNames.LAST_ACTIVE_PAGE, 0);

		// editor prefs
		preferenceStore.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER, false);
		preferenceStore.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_OVERVIEW_RULER, true);
		preferenceStore.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, false);

		preferenceStore.setDefault(CommonEditorPreferenceNames.CONTENT_ASSIST_SUPPORTED, false);
		preferenceStore.setDefault(CommonEditorPreferenceNames.AUTO_PROPOSE, true);
		preferenceStore.setDefault(CommonEditorPreferenceNames.AUTO_PROPOSE_CODE, CommonEditorPreferenceNames.LT);

		preferenceStore.setDefault(CommonEditorPreferenceNames.MATCHING_BRACKETS, true);
		PreferenceConverter.setDefault(preferenceStore, CommonEditorPreferenceNames.MATCHING_BRACKETS_COLOR, new RGB(192, 192, 192));

		preferenceStore.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, true);
		PreferenceConverter.setDefault(preferenceStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, new RGB(225, 235, 224));

		preferenceStore.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN, true);
		PreferenceConverter.setDefault(preferenceStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR, new RGB(176, 180, 185));

		preferenceStore.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN, 80);

		preferenceStore.setDefault(CommonEditorPreferenceNames.EDITOR_USE_INFERRED_GRAMMAR, true);

		initializeDefaultAnnotationPrefs(preferenceStore);
	}

	/*
	 * Using this method for all content types initialize methods since
	 * they're all the same (for now) @param preferenceStore
	 */
	private void initializeDefaultAnnotationPrefs(IPreferenceStore preferenceStore) {

		preferenceStore.setDefault(CommonEditorPreferenceNames.ERROR_INDICATION, true);
		PreferenceConverter.setDefault(preferenceStore, CommonEditorPreferenceNames.ERROR_INDICATION_COLOR, new RGB(255, 0, 128));

		preferenceStore.setDefault(CommonEditorPreferenceNames.WARNING_INDICATION, true);
		PreferenceConverter.setDefault(preferenceStore, CommonEditorPreferenceNames.WARNING_INDICATION_COLOR, new RGB(244, 200, 45));

		preferenceStore.setDefault(CommonEditorPreferenceNames.TASK_INDICATION, false);
		PreferenceConverter.setDefault(preferenceStore, CommonEditorPreferenceNames.TASK_INDICATION_COLOR, new RGB(0, 128, 255));

		preferenceStore.setDefault(CommonEditorPreferenceNames.BOOKMARK_INDICATION, false);
		PreferenceConverter.setDefault(preferenceStore, CommonEditorPreferenceNames.BOOKMARK_INDICATION_COLOR, new RGB(34, 164, 99));

		preferenceStore.setDefault(CommonEditorPreferenceNames.SEARCH_RESULT_INDICATION, true);
		PreferenceConverter.setDefault(preferenceStore, CommonEditorPreferenceNames.SEARCH_RESULT_INDICATION_COLOR, new RGB(192, 192, 192));

		preferenceStore.setDefault(CommonEditorPreferenceNames.UNKNOWN_INDICATION, false);
		PreferenceConverter.setDefault(preferenceStore, CommonEditorPreferenceNames.UNKNOWN_INDICATION_COLOR, new RGB(0, 0, 0));

		preferenceStore.setDefault(CommonEditorPreferenceNames.ERROR_INDICATION_IN_OVERVIEW_RULER, true);
		preferenceStore.setDefault(CommonEditorPreferenceNames.WARNING_INDICATION_IN_OVERVIEW_RULER, true);
		preferenceStore.setDefault(CommonEditorPreferenceNames.TASK_INDICATION_IN_OVERVIEW_RULER, true);
		preferenceStore.setDefault(CommonEditorPreferenceNames.BOOKMARK_INDICATION_IN_OVERVIEW_RULER, true);
		preferenceStore.setDefault(CommonEditorPreferenceNames.SEARCH_RESULT_INDICATION_IN_OVERVIEW_RULER, true);
		preferenceStore.setDefault(CommonEditorPreferenceNames.UNKNOWN_INDICATION_IN_OVERVIEW_RULER, false);
	}
}