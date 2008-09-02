package org.eclipse.wst.xsl.core.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xsl.core.internal.model.Template;

public class Templates extends XSLModelObject
{
	private final List<Template> templates = new ArrayList<Template>();

	public List<Template> getTemplates()
	{
		return templates;
	}

}
