package org.eclipse.wst.xsl.core.model;

import java.util.HashSet;
import java.util.Set;


public class Includes extends XSLModelObject
{
	private final Set<Stylesheet> stylesheets = new HashSet<Stylesheet>();
	private final Set<Import> imports = new HashSet<Import>();
	private final Set<Include> includes = new HashSet<Include>();

	public Set<Stylesheet> getStylesheets()
	{
		return stylesheets;
	}

	public void addInclude(Include include)
	{
		includes.add(include);
	}

	public void addImport(Import include)
	{
		imports.add(include);
	}
	
	public Set<Import> getImports()
	{
		return imports;
	}
	
	public Set<Include> getIncludes()
	{
		return includes;
	}

	public Type getModelType()
	{
		return Type.INCLUDES;
	}
}
