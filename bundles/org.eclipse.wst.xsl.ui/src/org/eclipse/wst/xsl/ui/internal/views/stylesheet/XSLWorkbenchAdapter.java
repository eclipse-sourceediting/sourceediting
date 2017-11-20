/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 244674 - Enhanced and cleaned up view 
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.views.stylesheet;

import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.xsl.core.model.Import;
import org.eclipse.wst.xsl.core.model.Include;
import org.eclipse.wst.xsl.core.model.Stylesheet;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.Variable;
import org.eclipse.wst.xsl.core.model.Function;
import org.eclipse.wst.xsl.core.model.XSLModelObject;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;

class XSLWorkbenchAdapter implements IWorkbenchAdapter {
	private final Object[] EMPTY = new Object[0];

	public Object[] getChildren(Object o) {
		if (o instanceof XSLModelObject) {
			XSLModelObject obj = (XSLModelObject) o;
			switch (obj.getModelType()) {
			case STYLESHEET:
				Stylesheet stylesheet = (Stylesheet) obj;
				ArrayList modelItems = new ArrayList();
				modelItems.add(new ListWorkbenchAdapter(stylesheet, null, stylesheet.getVersion(), XSLPluginImages.IMG_ELM_STYLESHET));
				modelItems.add(new ListWorkbenchAdapter(stylesheet, stylesheet.getImports(), "Imports", XSLPluginImages.IMG_ELM_IMPORT_INCLUDE)); //$NON-NLS-1$
				modelItems.add(new ListWorkbenchAdapter(stylesheet, stylesheet.getIncludes(), "Includes", XSLPluginImages.IMG_ELM_IMPORT_INCLUDE)); //$NON-NLS-1$
				modelItems.add(new ListWorkbenchAdapter(stylesheet, stylesheet.getGlobalVariables(), "Variables", XSLPluginImages.IMG_ELM_VARIABLE)); //$NON-NLS-1$
				modelItems.add(new ListWorkbenchAdapter(stylesheet, stylesheet.getTemplates(), "Templates", XSLPluginImages.IMG_ELM_TEMPLATE)); //$NON-NLS-1$
				if (stylesheet.getFunctions().size() > 0) {
					modelItems.add(new ListWorkbenchAdapter(stylesheet, stylesheet.getTemplates(), "Functions", XSLPluginImages.IMG_ELM_FUNCTION)); //$NON-NLS-1$
				}
				return modelItems.toArray();
			}
		}
		return EMPTY;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		XSLModelObject obj = (XSLModelObject) object;
		String path = null;
		switch (obj.getModelType()) {
		case STYLESHEET:
			path = XSLPluginImages.IMG_ELM_STYLESHET; 
			break;
		case IMPORT:
		case INCLUDE:
			path = XSLPluginImages.IMG_ELM_IMPORT_INCLUDE;
			break;
		case TEMPLATE: {
			Template template = (Template) obj;
			if (template.getName() != null) {
				path = XSLPluginImages.IMG_ELM_TEMPLATE_NAME; 
			} else {
				path =  XSLPluginImages.IMG_ELM_TEMPLATE;
			}
			break;
		}
		case VARIABLE:
			path = XSLPluginImages.IMG_ELM_VARIABLE;
			break;
		case FUNCTION:
			path = XSLPluginImages.IMG_ELM_FUNCTION;
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
		case FUNCTION:
			Function f = (Function) obj;
			label = f.getName();
		}
		return label;
	}

	public Object getParent(Object o) {
		return null;
	}
}
