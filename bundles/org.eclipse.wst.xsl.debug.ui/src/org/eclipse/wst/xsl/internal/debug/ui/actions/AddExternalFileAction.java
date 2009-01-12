/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 245772 - NLS Message refactoring
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.StylesheetViewer;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;
import org.eclipse.wst.xsl.debug.ui.Messages;


/**
 * An action that opens a dialog to allow the user to select a file from the file system.
 * 
 * @author Doug Satchwell
 */
public class AddExternalFileAction extends OpenDialogAction
{
	/**
	 * Create a new instance of this.
	 * 
	 * @param viewer the stylesheet viewer
	 * @param dialogSettingsPrefix a prefix to use for saving dialog preferences
	 */
	public AddExternalFileAction(StylesheetViewer viewer, String dialogSettingsPrefix)
	{
		super(Messages.AddExternalFileAction_Text, viewer, dialogSettingsPrefix);
	}

	@Override
	public void run()
	{
		String lastUsedPath = getDialogSetting(LAST_PATH_SETTING);
		if (lastUsedPath == null)
		{
			lastUsedPath = ""; //$NON-NLS-1$
		}
		FileDialog dialog = new FileDialog(getShell(), SWT.MULTI);
		dialog.setText(Messages.AddExternalFileAction_Selection_3);
		dialog.setFilterPath(lastUsedPath);
		IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
		IContentType contentType = contentTypeManager.getContentType("org.eclipse.wst.xml.core.xslsource"); //$NON-NLS-1$
		String[] xslContentTypes = contentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);

		dialog.setFilterExtensions(xslContentTypes);
		String res = dialog.open();
		if (res == null)
		{
			return;
		}

		IPath filterPath = new Path(dialog.getFilterPath());
		LaunchTransform[] lts = new LaunchTransform[1];
		IPath path = new Path(res).makeAbsolute();
		lts[0] = new LaunchTransform(path.toPortableString(), LaunchTransform.EXTERNAL_TYPE);

		setDialogSetting(LAST_PATH_SETTING, filterPath.toOSString());

		addTransforms(lts);
	}
}
