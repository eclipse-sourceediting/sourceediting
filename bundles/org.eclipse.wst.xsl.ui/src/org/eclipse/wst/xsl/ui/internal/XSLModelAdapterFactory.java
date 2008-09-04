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
	
	private class XSLWorkbenchAdapter implements IWorkbenchAdapter
	{
		private final Object[] EMPTY = new Object[0];

		public Object[] getChildren(Object o)
		{
			XSLModelObject obj = (XSLModelObject)o;
			switch(obj.getModelType())
			{
//				case STYLESHEET_MODEL:
//					StylesheetModel model = (StylesheetModel)obj;
//					Stylesheet stylesheet = model.getStylesheet();
//					return new Object[]{
//						new ListWorkbenchAdapter(stylesheet,null,stylesheet.getVersion(),"icons/full/xslt_launch.gif"),
//						new ListWorkbenchAdapter(stylesheet,model.getImports(),"Imports","icons/full/imports.gif"),
//						new ListWorkbenchAdapter(stylesheet,model.getIncludes(),"Includes","icons/full/imports.gif"),
//						new ListWorkbenchAdapter(stylesheet,model.getGlobalVariables(),"Variables",null),
//						new ListWorkbenchAdapter(stylesheet,model.getTemplates(),"Templates",null)
//					};
				case STYLESHEET:
					Stylesheet stylesheet = (Stylesheet)obj;
					return new Object[]{
						new ListWorkbenchAdapter(stylesheet,null,stylesheet.getVersion(),"icons/full/xslt_launch.gif"),
						new ListWorkbenchAdapter(stylesheet,stylesheet.getImports(),"Imports","icons/full/imports.gif"),
						new ListWorkbenchAdapter(stylesheet,stylesheet.getIncludes(),"Includes","icons/full/imports.gif"),
						new ListWorkbenchAdapter(stylesheet,stylesheet.getGlobalVariables(),"Variables",null),
						new ListWorkbenchAdapter(stylesheet,stylesheet.getTemplates(),"Templates",null)
					};
			}
			return EMPTY;
		}

		public ImageDescriptor getImageDescriptor(Object object)
		{
			XSLModelObject obj = (XSLModelObject)object;
			String path = null;
			switch(obj.getModelType())
			{
				case STYLESHEET:
					path = "icons/full/xslt_launch.gif";
					break;
				case IMPORT:
				case INCLUDE:
					path = "icons/full/import.gif";
					break;
				case TEMPLATE:
					path = "icons/full/methdef_obj.gif";
					break;
				case VARIABLE:
					path = "icons/full/field_default_obj.gif";
					break;
			}
			return path == null ? null : XSLUIPlugin.imageDescriptorFromPlugin(XSLUIPlugin.PLUGIN_ID, path);
		}

		public String getLabel(Object o)
		{
			String label = null;
			XSLModelObject obj = (XSLModelObject)o;
			switch(obj.getModelType())
			{
				case STYLESHEET:
					Stylesheet stylesheet = (Stylesheet)obj;
					label = stylesheet.getVersion() == null ? "?" : stylesheet.getVersion();
					break;
				case IMPORT:
					Import imp = (Import)obj;
					label = imp.getHref();
					break;
				case INCLUDE:
					Include inc = (Include)obj;
					label = inc.getHref();
					break;
				case TEMPLATE:
					Template t = (Template)obj;
					StringBuffer sb = new StringBuffer();
					if (t.getName() != null)
						sb.append(t.getName()).append(" ");
					if (t.getMatch() != null)
						sb.append(t.getMatch()).append(" ");
					if (t.getMode() != null)
						sb.append("(").append(t.getMode()).append(")");
					label = sb.toString();
					break;
				case VARIABLE:
					Variable v = (Variable)obj;
					label = v.getName();
					break;
			}
			return label;
		}

		public Object getParent(Object o)
		{
			return null;
		}		
	}
}
