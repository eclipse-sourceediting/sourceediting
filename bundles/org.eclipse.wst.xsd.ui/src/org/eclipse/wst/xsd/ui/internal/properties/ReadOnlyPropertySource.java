/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.properties;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class ReadOnlyPropertySource implements IPropertySource
{
	protected Element element;

	public ReadOnlyPropertySource(IEditorPart editPart, Element element)
	{
			this.element = element;
	}

	public Object getEditableValue()
	{
			return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors()
	{
			List list = new ArrayList();
			NamedNodeMap map = element.getAttributes();
			int mapLength = map.getLength();
			for (int i = 0; i < mapLength; i++)
			{
					Attr attr = (Attr) map.item(i);
					list.add(new PropertyDescriptor(attr.getName(), attr.getName()));
			}
			IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
			list.toArray(result);
			return result;
	}

	public Object getPropertyValue(Object id)
	{
			Object result = null;
			if (id instanceof String)
			{
					result = element.getAttribute((String) id);
			}
			return result != null ? result : "";
	}

	public boolean isPropertySet(Object id)
	{
			return false;
	}

	public void resetPropertyValue(Object id)
	{
	}

	public void setPropertyValue(Object id, Object value)
	{
	}

}
