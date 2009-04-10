package org.eclipse.wst.xsl.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.XSLModelObject;

class XSLWorkbenchAdapter implements IWorkbenchAdapter {
	private final Object[] EMPTY = new Object[0];

	public Object[] getChildren(Object o) {
		XSLModelObject obj = (XSLModelObject) o;
		switch (obj.getModelType()) {
		case STYLESHEET:
			Stylesheet stylesheet = (Stylesheet) obj;
			return new Object[] {
					new ListWorkbenchAdapter(stylesheet, null, stylesheet
							.getVersion(), "icons/full/xslt_launch.gif"), //$NON-NLS-1$
					new ListWorkbenchAdapter(stylesheet, stylesheet
							.getImports(), "Imports", "icons/full/imports.gif"), //$NON-NLS-1$ //$NON-NLS-2$
					new ListWorkbenchAdapter(stylesheet, stylesheet
							.getIncludes(), "Includes", //$NON-NLS-1$
							"icons/full/imports.gif"), //$NON-NLS-1$
					new ListWorkbenchAdapter(stylesheet, stylesheet
							.getGlobalVariables(), "Variables", null), //$NON-NLS-1$
					new ListWorkbenchAdapter(stylesheet, stylesheet
							.getTemplates(), "Templates", null) }; //$NON-NLS-1$
		}
		return EMPTY;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		XSLModelObject obj = (XSLModelObject) object;
		String path = null;
		switch (obj.getModelType()) {
		case STYLESHEET:
			path = "icons/full/xslt_launch.gif"; //$NON-NLS-1$
			break;
		case IMPORT:
		case INCLUDE:
			path = "icons/full/import.gif"; //$NON-NLS-1$
			break;
		case TEMPLATE:
			path = "icons/full/methdef_obj.gif"; //$NON-NLS-1$
			break;
		case VARIABLE:
			path = "icons/full/field_default_obj.gif"; //$NON-NLS-1$
			break;
		}
		return path == null ? null : AbstractUIPlugin.imageDescriptorFromPlugin(
				XSLUIPlugin.PLUGIN_ID, path);
	}

	public String getLabel(Object o) {
		String label = null;
		XSLModelObject obj = (XSLModelObject) o;
		switch (obj.getModelType()) {
		case STYLESHEET:
			Stylesheet stylesheet = (Stylesheet) obj;
			label = stylesheet.getVersion() == null ? "?" : stylesheet //$NON-NLS-1$
					.getVersion();
			break;
		case IMPORT:
			Import imp = (Import) obj;
			label = imp.getHref();
			break;
		case INCLUDE:
			Include inc = (Include) obj;
			label = inc.getHref();
			break;
		case TEMPLATE:
			Template t = (Template) obj;
			StringBuffer sb = new StringBuffer();
			if (t.getName() != null)
				sb.append(t.getName()).append(" "); //$NON-NLS-1$
			if (t.getMatch() != null)
				sb.append(t.getMatch()).append(" "); //$NON-NLS-1$
			if (t.getMode() != null)
				sb.append("(").append(t.getMode()).append(")");  //$NON-NLS-1$//$NON-NLS-2$
			label = sb.toString();
			break;
		case VARIABLE:
			Variable v = (Variable) obj;
			label = v.getName();
			break;
		}
		return label;
	}

	public Object getParent(Object o) {
		return null;
	}
}
