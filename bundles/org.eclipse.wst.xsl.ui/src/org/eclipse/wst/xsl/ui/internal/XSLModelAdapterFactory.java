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
package org.eclipse.wst.xsl.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.XSLModelObject;

public class XSLModelAdapterFactory implements IAdapterFactory
{
	private static Class[] LIST = new Class[]{IWorkbenchAdapter.class};
	private IWorkbenchAdapter adapter = new XSLWorkbenchAdapter();
	
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (IWorkbenchAdapter.class.equals(adapterType))
			return adapter;
		return null;
	}
	
	public Class[] getAdapterList()
	{
		return LIST;
	}
	
}
