/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentTypeSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * Dialog offering to add input filename to default content type when editor
 * was opened on unsupported content type.
 * 
 * https://bugs.eclipse.org/320996
 */
public class UnknownContentTypeDialog2 extends MessageDialog {

	private String fFileName;
	private IContentTypeSettings fContentTypeSettings;

	public UnknownContentTypeDialog2(Shell parent, IPreferenceStore store, String fileName, IContentTypeSettings contentTypeSettings) {
		// set message to null in super so that message does not appear twice
		super(parent, SSEUIMessages.UnknownContentTypeDialog_0, null, null, MessageDialog.QUESTION_WITH_CANCEL, new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
		this.fFileName = fileName;
		this.fContentTypeSettings = contentTypeSettings;
	}

	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);

		if (buttonId == IDialogConstants.OK_ID) {
			try {
				fContentTypeSettings.addFileSpec(fFileName, IContentTypeSettings.FILE_NAME_SPEC);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}

	protected Control createMessageArea(Composite composite) {
		super.createMessageArea(composite);
		Link messageLink = new Link(composite, SWT.WRAP);
		messageLink.setText(NLS.bind(SSEUIMessages.UnknownContentTypeDialog_3, Platform.getContentTypeManager().getContentType(fContentTypeSettings.getId()).getName()));
		messageLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				linkClicked();
			}
		});
		return composite;
	}

	private void linkClicked() {
		String pageId = "org.eclipse.ui.preferencePages.ContentTypes"; //$NON-NLS-1$
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), pageId, new String[]{pageId}, null);
		dialog.open();
	}
}
