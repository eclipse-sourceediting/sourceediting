package org.eclipse.wst.xsl.ui.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Includes;
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
				case STYLESHEET_MODEL:
					StylesheetModel model = (StylesheetModel)obj;
					List<XSLModelObject> list = new ArrayList<XSLModelObject>();
					list.add(model.getIncludes());
					list.addAll(model.getGlobalVariables());
					list.addAll(model.getTemplates());
					return list.toArray(new XSLModelObject[0]);
				case INCLUDES:
					Includes inc = (Includes)obj;
					HashSet set = new HashSet(inc.getImports());
					set.addAll(inc.getIncludes());
					return set.toArray(new Include[0]);
			}
			return EMPTY;
		}

		public ImageDescriptor getImageDescriptor(Object object)
		{
			XSLModelObject obj = (XSLModelObject)object;
			String path = null;
			switch(obj.getModelType())
			{
				case INCLUDES:
					path = "icons/full/imports.gif";
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
			XSLModelObject obj = (XSLModelObject)o;
			switch(obj.getModelType())
			{
				case STYLESHEET_MODEL:
					StylesheetModel model = (StylesheetModel)obj;
					return model.getStylesheet().getFile().getName();
				case INCLUDES:
					return "Imports and Includes";
				case IMPORT:
					Import imp = (Import)obj;
					return "import " + imp.getHref();
				case INCLUDE:
					Include inc = (Include)obj;
					return "include " + inc.getHref();
				case TEMPLATE:
					Template t = (Template)obj;
					return t.getName();
				case VARIABLE:
					Variable v = (Variable)obj;
					return v.getName();
			}
			return null;
		}

		public Object getParent(Object o)
		{
			return null;
		}		
	}
}
