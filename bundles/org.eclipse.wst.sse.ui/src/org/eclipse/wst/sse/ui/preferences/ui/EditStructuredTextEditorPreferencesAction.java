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
package org.eclipse.wst.sse.ui.preferences.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.util.DocumentInputStream;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.extension.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.extension.IExtendedEditorAction;
import org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor;
import org.eclipse.wst.sse.ui.extensions.ConfigurationPointCalculator;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;


/**
 * This action displays the preferences relative to the current editor
 * @author amywu
 */
public class EditStructuredTextEditorPreferencesAction extends Action implements IExtendedEditorAction {
	private final String EXTENSION_TYPE_ID = "preferencepages"; //$NON-NLS-1$
	private final String EXTENSION_ATTRIBUTE_PREFERENCE_IDS = "preferenceids"; //$NON-NLS-1$
	private IExtendedSimpleEditor fEditor;

	public EditStructuredTextEditorPreferencesAction() {
		super(ResourceHandler.getString("EditPreferences.label"), EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_OBJ_PREFERENCES)); //$NON-NLS-1$
	}

	/**
	 * Open the given preference page in a preference dialog.
	 * 
	 * @param shell
	 *            The shell to open on
	 * @param manager
	 *            The preference manager to use/display in the preference
	 *            dialog
	 * @return Returns <code>true</code> if the user ended the page by
	 *         pressing OK.
	 */
	public boolean showPreferencePage(Shell shell, PreferenceManager manager) {
		final PreferenceDialog dialog = new PreferenceDialog(shell, manager);
		final boolean[] result = new boolean[]{false};
		BusyIndicator.showWhile(shell.getDisplay(), new Runnable() {
			public void run() {
				dialog.create();
				dialog.setMessage(ResourceHandler.getString("EditStructuredTextEditorPreferencesAction.0")); //$NON-NLS-1$
				result[0] = (dialog.open() == Window.OK);
			}
		});
		return result[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		PreferenceManager manager = buildPreferenceManager();
		if (manager != null) {
			showPreferencePage(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), manager);
		}
	}

	private IModelManager getModelManager() {
		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
	}

	/**
	 * Return the content type of the current fEditor for this action
	 * 
	 * @return content type identifier of the current fEditor
	 */
	private String getContentType() {
		String contentType = null;

		IStructuredModel model = getModelManager().getExistingModelForRead(getEditor().getDocument());
		if (model != null) {
			contentType = model.getContentTypeIdentifier();
			model.releaseFromRead();
		}
		if(contentType == null) {
			IContentType type = null;
			try {
				type = Platform.getContentTypeManager().findContentTypeFor(new DocumentInputStream(getEditor().getDocument()), getEditor().getEditorPart().getEditorInput().getName());
			}
			catch (IOException e) {
				// do nothing, shouldn't even be possible
			}
			if(type != null) {
				contentType = type.getId();
			}
		}
		return contentType;
	}

	/**
	 * Create the preference manager to use to display in the preference
	 * dialog
	 * 
	 * @return a preference manager or null if there is no editor/preference
	 *         page to display
	 */
	protected PreferenceManager buildPreferenceManager() {
		if (getEditor() == null) {
			return null;
		}

		// gather all preference page ids to display
		String[] ids = buildPreferencePageIds();

		// if no preference pages to display, return null
		if (ids.length == 0) {
			return null;
		}

		// create a new preference manager for this action/dialog
		PreferenceManager manager = new PreferenceManager();

		// get the workbench preference manager
		PreferenceManager platformManager = PlatformUI.getWorkbench().getPreferenceManager();

		for (int i = 0; i < ids.length; ++i) {
			final IPreferenceNode targetNode = platformManager.find(ids[i]);
			if (targetNode != null) {
				manager.addToRoot(targetNode);
			}
		}

		return manager;
	}

	/**
	 * Create a list of preference page ids of the preference pages to display
	 * 
	 * @return String[]
	 */
	protected String[] buildPreferencePageIds() {
		List prefIds = new ArrayList();

		// figure out all preference page ids contributed
		String[] pointIds = null;
		if(getEditor() instanceof IEditorPart) {
			pointIds = ConfigurationPointCalculator.getConfigurationPoints((IEditorPart) getEditor(), getContentType(), ConfigurationPointCalculator.SOURCE, StructuredTextEditor.class);
		}
		else {
			pointIds = ConfigurationPointCalculator.getConfigurationPoints(getEditor().getEditorPart(), getContentType(), ConfigurationPointCalculator.SOURCE, StructuredTextEditor.class);
		}
		ExtendedConfigurationBuilder builder = ExtendedConfigurationBuilder.getInstance();

		// go through each configuration point and extract the preference page
		// ids
		// going through configuration points backwards so base fEditor
		// preferences show up first
		for (int i = pointIds.length - 1; i >= 0; --i) {
			IConfigurationElement config = builder.getConfigurationElement(EXTENSION_TYPE_ID, pointIds[i]);
			// get the list of preference page ids from the preferenceids
			// attribute
			if (config != null) {
				String preferenceIdsString = config.getAttribute(EXTENSION_ATTRIBUTE_PREFERENCE_IDS);
				// separate out the list of preference ids
				if (preferenceIdsString != null) {
					StringTokenizer tokenizer = new StringTokenizer(preferenceIdsString, ","); //$NON-NLS-1$
					while (tokenizer.hasMoreTokens()) {
						String prefId = tokenizer.nextToken().trim();
						prefIds.add(prefId);
					}
				}
			}
		}
		return (String[]) prefIds.toArray(new String[prefIds.size()]);
	}

	private IExtendedSimpleEditor getEditor() {
		return fEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public void setActiveExtendedEditor(IExtendedSimpleEditor targetEditor) {
		fEditor = targetEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public boolean isVisible() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		if (getEditor() == null) {
			setEnabled(false);
		}
		else {
			setEnabled(true);
		}
	}
}
