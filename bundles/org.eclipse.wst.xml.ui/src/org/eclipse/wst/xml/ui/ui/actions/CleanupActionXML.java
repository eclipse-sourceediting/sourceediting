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
package org.eclipse.wst.xml.ui.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.cleanup.IStructuredCleanupProcessor;
import org.eclipse.wst.sse.ui.ui.CleanupAction;
import org.eclipse.wst.xml.core.cleanup.CleanupProcessorXML;

public class CleanupActionXML extends CleanupAction {
	protected IStructuredCleanupProcessor fCleanupProcessor;

	public CleanupActionXML(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	protected Dialog getCleanupDialog(Shell shell) {
		if (fCleanupDialog == null)
			fCleanupDialog = new CleanupDialogXML(shell);

		return fCleanupDialog;
	}

	protected IStructuredCleanupProcessor getCleanupProcessor() {
		if (fCleanupProcessor == null)
			fCleanupProcessor = new CleanupProcessorXML();

		return fCleanupProcessor;
	}
}
