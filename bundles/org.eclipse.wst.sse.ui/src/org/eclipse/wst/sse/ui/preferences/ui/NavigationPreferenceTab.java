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
package org.eclipse.wst.sse.ui.preferences.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.text.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;
import org.eclipse.wst.sse.ui.util.EditorUtility;

/**
 * Preference page tab that contains Navigation preferences
 * @deprecated Users will use the Navigation preferences on All Text Editors preference page instead
 * @author amywu
 */
public class NavigationPreferenceTab extends AbstractPreferenceTab {
	private static final String DELIMITER = "+"; //$NON-NLS-1$
	Button fBrowserLikeLinksCheckBox;

	Text fBrowserLikeLinksKeyModifierText;

	public NavigationPreferenceTab(PreferencePage mainPreferencePage, OverlayPreferenceStore store) {
		Assert.isNotNull(mainPreferencePage);
		Assert.isNotNull(store);
		setMainPreferencePage(mainPreferencePage);
		setOverlayStore(store);
		getOverlayStore().addKeys(createOverlayStoreKeys());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createContents(Composite tabFolder) {
		Composite composite = new Composite(tabFolder, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		String text = SSEUIPlugin.getResourceString("%StructuredTextEditorPreferencePage.7"); //$NON-NLS-1$
		fBrowserLikeLinksCheckBox = addCheckBox(composite, text, CommonEditorPreferenceNames.BROWSER_LIKE_LINKS, 0);
		fBrowserLikeLinksCheckBox.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				boolean state = fBrowserLikeLinksCheckBox.getSelection();
				fBrowserLikeLinksKeyModifierText.setEnabled(state);
				handleBrowserLikeLinksKeyModifierModified();
			}
		});

		// Text field for modifier string
		text = SSEUIPlugin.getResourceString("%StructuredTextEditorPreferencePage.8"); //$NON-NLS-1$
		fBrowserLikeLinksKeyModifierText = addTextField(composite, text, CommonEditorPreferenceNames.BROWSER_LIKE_LINKS_KEY_MODIFIER, 20, 0, false);
		fBrowserLikeLinksKeyModifierText.setTextLimit(Text.LIMIT);

		if (EditorUtility.computeStateMask(getOverlayStore().getString(CommonEditorPreferenceNames.BROWSER_LIKE_LINKS_KEY_MODIFIER)) == -1) {
			fBrowserLikeLinksKeyModifierText.setText(""); //$NON-NLS-1$
		}

		fBrowserLikeLinksKeyModifierText.addKeyListener(new KeyListener() {
			private boolean isModifierCandidate;

			public void keyPressed(KeyEvent e) {
				isModifierCandidate = e.keyCode > 0 && e.character == 0 && e.stateMask == 0;
			}

			public void keyReleased(KeyEvent e) {
				if (isModifierCandidate && e.stateMask > 0 && e.stateMask == e.stateMask && e.character == 0) {// &&
					// e.time
					// -time
					// <
					// 1000)
					// {
					String modifierString = fBrowserLikeLinksKeyModifierText.getText();
					Point selection = fBrowserLikeLinksKeyModifierText.getSelection();
					int i = selection.x - 1;
					while (i > -1 && Character.isWhitespace(modifierString.charAt(i))) {
						i--;
					}
					boolean needsPrefixDelimiter = i > -1 && !String.valueOf(modifierString.charAt(i)).equals(DELIMITER);

					i = selection.y;
					while (i < modifierString.length() && Character.isWhitespace(modifierString.charAt(i))) {
						i++;
					}
					boolean needsPostfixDelimiter = i < modifierString.length() && !String.valueOf(modifierString.charAt(i)).equals(DELIMITER);

					String insertString;

					if (needsPrefixDelimiter && needsPostfixDelimiter)
						insertString = " \\ + " + Action.findModifierString(e.stateMask) + " + "; //$NON-NLS-1$ //$NON-NLS-2$
					else if (needsPrefixDelimiter)
						insertString = "\\ + " + Action.findModifierString(e.stateMask); //$NON-NLS-1$
					else if (needsPostfixDelimiter)
						insertString = "\\ " + Action.findModifierString(e.stateMask) + " + "; //$NON-NLS-1$ //$NON-NLS-2$
					else
						insertString = Action.findModifierString(e.stateMask);

					fBrowserLikeLinksKeyModifierText.insert(insertString);
				}
			}
		});

		fBrowserLikeLinksKeyModifierText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				handleBrowserLikeLinksKeyModifierModified();
			}
		});

		initializeFields();

		WorkbenchHelp.setHelp(composite, IHelpContextIds.PREFSTE_NAVIGATION_HELPID);
		return composite;
	}

	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		ArrayList overlayKeys = new ArrayList();

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, CommonEditorPreferenceNames.LINK_COLOR));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, CommonEditorPreferenceNames.BROWSER_LIKE_LINKS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, CommonEditorPreferenceNames.BROWSER_LIKE_LINKS_KEY_MODIFIER));

		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#getTitle()
	 */
	public String getTitle() {
		return SSEUIPlugin.getResourceString("%StructuredTextEditorPreferencePage.34"); //$NON-NLS-1$;
	}

	void handleBrowserLikeLinksKeyModifierModified() {
		IStatus status = new StatusInfo();

		String modifiers = fBrowserLikeLinksKeyModifierText.getText();
		int stateMask = EditorUtility.computeStateMask(modifiers);

		if (fBrowserLikeLinksCheckBox.getSelection() && (stateMask == -1 || (stateMask & SWT.SHIFT) != 0)) {
			if (stateMask == -1) {
				Object[] args = {modifiers};
				String message = SSEUIPlugin.getResourceString("%NavigationPreferenceTab.0", args);
				status = new StatusInfo(IStatus.ERROR, message);
			} else
				status = new StatusInfo(IStatus.ERROR, SSEUIPlugin.getResourceString("%StructuredTextEditorPreferencePage.15")); //$NON-NLS-1$
		}
		updateStatus(status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferenceTab#initializeFields()
	 */
	protected void initializeFields() {
		super.initializeFields();

		// disable if checkbox is unchecked
		boolean state = fBrowserLikeLinksCheckBox.getSelection();
		fBrowserLikeLinksKeyModifierText.setEnabled(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#performApply()
	 */
	public void performApply() {
		// all preferences are stored in overlay store so main preference page
		// should handle apply
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#performDefaults()
	 */
	public void performDefaults() {
		initializeFields();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.IPreferenceTab#performOk()
	 */
	public void performOk() {
		// all preferences are stored in overlay store so main preference page
		// should handle OK
	}
}
