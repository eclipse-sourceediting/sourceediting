package org.eclipse.wst.xsl.core.internal.model;

public class XSLAttribute extends XSLNode
{
	final String name;
	final String value;

	public XSLAttribute(XSLElement element, String name, String value)
	{
		super(element.getStylesheet(), XSLNode.ATTRIBUTE_NODE);
		this.name = name;
		this.value = value;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getValue()
	{
		return value;
	}
}
