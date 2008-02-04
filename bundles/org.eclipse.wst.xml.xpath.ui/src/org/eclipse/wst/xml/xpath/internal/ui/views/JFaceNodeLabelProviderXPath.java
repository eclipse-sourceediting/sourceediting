/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.internal.ui.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;

public class JFaceNodeLabelProviderXPath extends LabelProvider
{
	protected IJFaceNodeAdapter getAdapter(Object adaptable)
	{
		if (adaptable instanceof IAdaptable)
		{
			IWorkbenchAdapter adapter1 = (IWorkbenchAdapter)((IAdaptable) adaptable).getAdapter(IWorkbenchAdapter.class);
		}
		if (adaptable instanceof INodeNotifier)
		{
			INodeAdapter adapter = ((INodeNotifier) adaptable).getAdapterFor(IJFaceNodeAdapter.class);
			if (adapter instanceof IJFaceNodeAdapter)
				return (IJFaceNodeAdapter) adapter;
		}
		return null;
	}

	public Image getImage(Object element)
	{
		return getAdapter(element).getLabelImage(element);
	}

	public String getText(Object element)
	{
		return getAdapter(element).getLabelText(element);
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}
}
