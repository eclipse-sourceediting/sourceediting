/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.extension.IExtendedEditorAction;
import org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor;
import org.eclipse.wst.sse.ui.extension.SpellCheckProviderBuilder;
import org.eclipse.wst.sse.ui.extensions.ISelfValidateEditAction;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckAction;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckProvider;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckTarget;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;
import org.osgi.framework.Bundle;

/**
 * Spell Check action
 */
public class SpellCheckActionWrapper extends Action implements IExtendedEditorAction, ISelfValidateEditAction, IPartListener {
	protected SpellCheckAction action = null;
	protected IWorkbenchPart activePart = null;

	protected IExtendedSimpleEditor fEditor = null;
	protected SpellCheckProvider provider = null;

	public SpellCheckActionWrapper() {
		super();

		IPartService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService();
		service.addPartListener(this);
		partActivated(service.getActivePart());

		//TODO DBK performance modification - change to delegate
		//     DBK There appears to only be one extender, and it doesn't specify
		//     DBK the icon / text -- it is determined by the target action.
		//     DBK This forces this plug-in to load the provider plug-in solely
		//     DBK to ask it what to display. Since presumably this is static,
		//     DBK I've moved it to here. Alternatively, you can comment out
		//     DBK the setInitializationData method, add the appropriate
		// interface,
		//     DBK and then extract the values from the extension itself (again,
		//     DBK thereby avoiding the load of the provider plug-in).
		//		setId("com.ibm.etools.spellcheck");
		//		setActionDefinitionId("com.ibm.etools.spellcheck");
		Bundle bundle = Platform.getBundle("org.eclipse.wst.sse.ui");	//$NON-NLS-1$
		setText(Platform.getResourceString(bundle, "%Spell_Check_label")); //$NON-NLS-1$
		setToolTipText(Platform.getResourceString(bundle, "%Spell_Check_tooltip")); //$NON-NLS-1$

		setImageDescriptor(EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_CTOOL16_SPELLCHECK));
		setDisabledImageDescriptor(EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_DTOOL16_SPELLCHECK));
		setHoverImageDescriptor(EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_ETOOL16_SPELLCHECK));
	}

	private Display getDisplay() {

		return PlatformUI.getWorkbench().getDisplay();
	}

	private void initializeSpellCheckProviderAction() {
		//     DBK performance modification - There is only one extender, yet
		// there is a loop?
		//     DBK No references to multiple providers in run() method either.
		//     DBK I suggest reworking this to be a single-point extension.
		SpellCheckProvider[] providers = SpellCheckProviderBuilder.getInstance().getSpellCheckProviders();
		final int n = providers.length;
		for (int i = 0; i < n; i++) {
			action = providers[i].createSpellCheckAction();
		}
		update();

		//		TODO DBK performance modification - copied into this plug-in, this
		// is now unnecessary code
		//		if (action == null) return;
		//		IAction a = (IAction)action;
		//		
		//		setText(a.getText());
		//		setToolTipText(a.getToolTipText());
		//		setImageDescriptor(a.getImageDescriptor());
		//		setHoverImageDescriptor(a.getHoverImageDescriptor());
		//		setDisabledImageDescriptor(a.getDisabledImageDescriptor());
	}

	/**
	 * @see com.ibm.sed.edit.extension.IExtendedEditorAction#isVisible()
	 */
	public boolean isVisible() {
		if (fEditor == null)
			return true;

		IEditorPart part = fEditor.getEditorPart();
		if (part == null)
			return true;

		String cls = part.getClass().getName();
		if ("com.ibm.etools.webedit.editor.HTMLEditor".equals(cls)) //$NON-NLS-1$
			return false;

		Object target = part.getAdapter(SpellCheckTarget.class);
		return (target != null);
	}

	public void partActivated(IWorkbenchPart part) {
		IExtendedSimpleEditor editor = null;
		if (part != null && part != activePart) {
			if (part instanceof IExtendedSimpleEditor)
				editor = (IExtendedSimpleEditor) part;
			else if (part.getAdapter(IExtendedSimpleEditor.class) != null)
				editor = (IExtendedSimpleEditor) part.getAdapter(IExtendedSimpleEditor.class);
			else if (part.getAdapter(ITextEditor.class) instanceof IExtendedSimpleEditor)
				editor = (IExtendedSimpleEditor) part.getAdapter(ITextEditor.class);
		}
		this.activePart = part;
		setActiveExtendedEditor(editor);
		// lock onto the first valid editor part
		if (editor != null) {
			IPartService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService();
			service.removePartListener(this);
		}
		update();
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		// do nothing
	}

	public void partClosed(IWorkbenchPart part) {
		if (part == null || part == fEditor) {
			setActiveExtendedEditor(null);
			update();
		}
	}

	public void partDeactivated(IWorkbenchPart part) {
		// do nothing
	}

	public void partOpened(IWorkbenchPart part) {
		// do nothing
	}

	public void run() {
		if (action == null)
			initializeSpellCheckProviderAction();
		if (action == null || !isEnabled()) {
			MessageDialog.openInformation(getDisplay().getActiveShell(), SSEUIPlugin.getResourceString("%Information"), //$NON-NLS-1$
						SSEUIPlugin.getResourceString("%PluginAction.operationNotAvailableMessage")); //$NON-NLS-1$
			setEnabled(false);
			return;
		}
		IAction a = (IAction) action;
		a.run();
	}

	/**
	 * @see com.ibm.sed.edit.extension.IExtendedEditorAction#setActiveExtendedEditor(IExtendedSimpleEditor)
	 */
	public void setActiveExtendedEditor(IExtendedSimpleEditor targetEditor) {
		fEditor = targetEditor;
	}

	public void update() {
		if (fEditor == null) {
			setEnabled(false);
			return;
		}

		if (action != null) {
			action.update();
			setEnabled(((IAction) action).isEnabled());
			return;
		}

		// Default is true since action isn't instantiated until run();
		// a second test is done later to see if the action is **really**
		// ready.
		setEnabled(true);
	}

}
