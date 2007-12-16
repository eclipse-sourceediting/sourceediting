/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.StylesheetViewer;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class AddExternalFileAction extends OpenDialogAction
{

	public AddExternalFileAction(StylesheetViewer viewer, String dialogSettingsPrefix)
	{
		super(ActionMessages.AddExternalFileAction_Text, viewer, dialogSettingsPrefix);
	}

	@Override
	public void run()
	{
		String lastUsedPath = getDialogSetting(LAST_PATH_SETTING);
		if (lastUsedPath == null)
		{
			lastUsedPath = "";
		}
		FileDialog dialog = new FileDialog(getShell(), SWT.MULTI);
		dialog.setText(ActionMessages.AddExternalFileAction_Selection_3);
		dialog.setFilterPath(lastUsedPath);
		dialog.setFilterExtensions(new String[]{ "*.xsl" });
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
