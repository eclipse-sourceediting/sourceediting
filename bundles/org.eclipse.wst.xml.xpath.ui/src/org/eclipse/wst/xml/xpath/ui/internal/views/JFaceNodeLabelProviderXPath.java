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
package org.eclipse.wst.xml.xpath.ui.internal.views;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class JFaceNodeLabelProviderXPath extends LabelProvider implements IFontProvider
{
	FontRegistry registry = new FontRegistry();

	protected IJFaceNodeAdapter getAdapter(Object adaptable)
	{
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
		if (element instanceof EmptyNodeList)
			return null;
		return getAdapter(element).getLabelImage(element);
	}

	public String getText(Object element)
	{
		if (element instanceof EmptyNodeList)
			return "<No Matches>";
		IJFaceNodeAdapter adapter = getAdapter(element);
		StringBuffer sb = new StringBuffer(adapter.getLabelText(element));
		if (element instanceof Element)
		{
			Element impl = (Element)element;
			NamedNodeMap nnm = impl.getAttributes();
			if (nnm.getLength() > 0)
			{
				Attr a = (Attr)nnm.item(0);
				String val = a.getNodeValue();
				if (val.length() > 15)
					val = val.substring(0,15);
				sb.append(" ").append(a.getName()).append("=").append(val);
			}
		}
		return sb.toString();
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}
	
	public Font getFont(Object element)
	{
		if (element instanceof EmptyNodeList) {
			return registry.getItalic(Display.getCurrent().getSystemFont().getFontData()[0].getName());
		}
		return null;
	}
}
