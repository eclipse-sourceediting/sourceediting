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
package org.eclipse.wst.sse.ui.internal.search;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;



/**
 * Basic label provider that just provides an image and default text.
 * 
 * @author pavery
 */
public class BasicSearchLabelProvider implements ILabelProvider {

	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	public void dispose() {
		// do nothing
	}

	public Image getImage(Object element) {
		return EditorPluginImageHelper.getInstance().getImage(EditorPluginImages.IMG_OBJ_OCC_MATCH);
	}

	public String getText(Object element) {

		StringBuffer text = new StringBuffer();
		if (element instanceof Match) {
			Match m = (Match) element;

			IMarker marker = (IMarker) m.getElement();
			if (marker.exists()) {
				String resultText = ""; //$NON-NLS-1$
				try {
					resultText = (String) marker.getAttribute(IMarker.MESSAGE);
				} catch (CoreException e) {
					Logger.logException(e);
				}
				text.append(resultText);
			}
		} else {
			text.append(element.toString());
		}
		return text.toString();
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}
}
