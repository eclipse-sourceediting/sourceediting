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
package org.eclipse.wst.html.ui.edit.ui;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.html.core.cleanup.HTMLCleanupProcessorImpl;
import org.eclipse.wst.sse.core.cleanup.IStructuredCleanupProcessor;
import org.eclipse.wst.sse.ui.edit.util.CleanupAction;

public class CleanupActionHTML extends CleanupAction {
	protected IStructuredCleanupProcessor fCleanupProcessor;

	public CleanupActionHTML(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	protected Dialog getCleanupDialog(Shell shell) {
		if (fCleanupDialog == null)
			fCleanupDialog = new CleanupDialogHTML(shell);

		return fCleanupDialog;
	}

	protected IStructuredCleanupProcessor getCleanupProcessor() {
		if (fCleanupProcessor == null)
			fCleanupProcessor = new HTMLCleanupProcessorImpl();

		return fCleanupProcessor;
	}
}