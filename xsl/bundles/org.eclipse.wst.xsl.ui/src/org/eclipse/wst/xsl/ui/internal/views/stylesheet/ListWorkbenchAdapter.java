/*******************************************************************************
 * Copyright (c) 2008, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
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
package org.eclipse.wst.xsl.ui.internal.views.stylesheet;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

public class ListWorkbenchAdapter implements IWorkbenchAdapter
{
	private Object parent;
	private Object[] children;
	private String text;
	private ImageDescriptor image;
	
	public ListWorkbenchAdapter(Stylesheet stylesheet, List list, String label, String imgPath)
	{
		parent = stylesheet;
		if (list != null)
			children = list.toArray();
		else
			children = new Object[0];
		text = label;
		if (imgPath!=null)
			image = AbstractUIPlugin.imageDescriptorFromPlugin(XSLUIPlugin.PLUGIN_ID, imgPath);
	}
	
	public ImageDescriptor getImageDescriptor(Object object)
	{
		return image;
	}
	
	public String getLabel(Object o)
	{
		return text;
	}

	public Object[] getChildren(Object o)
	{
		return children;
	}

	public Object getParent(Object o)
	{
		return parent;
	}

}
