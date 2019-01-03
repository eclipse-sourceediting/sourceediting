/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.tabs.main;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class StylesheetLabelProvider extends LabelProvider {
	private Image image = XSLDebugUIPlugin.getImageDescriptor(
			"icons/xslt_launch.gif").createImage(); //$NON-NLS-1$

	@Override
	public Image getImage(Object element) {
		return image;
	}

	@Override
	public String getText(Object element) {
		LaunchTransform lt = (LaunchTransform) element;
		int index = lt.getPipeline().getTransformDefs().indexOf(lt);

		IPath path;
		try {
			path = lt.getPath();
		} catch (CoreException e) {
			return MessageFormat.format(
					Messages.StylesheetEntryLabelProvider_Invalid_path,
					new Object[] { "null" }); //$NON-NLS-1$
		}

		if (path == null) {
			return MessageFormat.format(
					Messages.StylesheetEntryLabelProvider_Invalid_path,
					new Object[] { "null" }); //$NON-NLS-1$
		} else if (!path.isAbsolute() || !path.isValidPath(path.toString())) {
			return MessageFormat.format(
					Messages.StylesheetEntryLabelProvider_Invalid_path,
					new Object[] { path.toString() });
		}

		String[] segments = path.segments();
		StringBuffer displayPath = new StringBuffer();
		if (segments.length > 0) {
			displayPath.append(segments[segments.length - 1]);
			displayPath.append(" - "); //$NON-NLS-1$
			String device = path.getDevice();
			if (device != null) {
				displayPath.append(device);
			}
			displayPath.append(File.separator);
			for (int i = 0; i < segments.length - 1; i++) {
				displayPath.append(segments[i]).append(File.separator);
			}
		} else {
			displayPath.append(path.toString());
		}
		return (index + 1) + ") " + displayPath.toString(); //$NON-NLS-1$
	}
}
