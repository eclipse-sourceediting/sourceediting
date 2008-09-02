package org.eclipse.wst.xsl.core.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.xsl.core.internal.model.Stylesheet;

public class Includes extends XSLModelObject
{
	private final Set<Stylesheet> stylesheets = new HashSet<Stylesheet>();

	public Set<Stylesheet> getStylesheets()
	{
		return stylesheets;
	}
}
