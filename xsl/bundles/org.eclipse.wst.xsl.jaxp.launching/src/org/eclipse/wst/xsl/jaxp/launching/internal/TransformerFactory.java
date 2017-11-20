package org.eclipse.wst.xsl.jaxp.launching.internal;

import org.eclipse.wst.xsl.jaxp.launching.ITransformerFactory;

public class TransformerFactory implements ITransformerFactory
{
	private final String factoryClass;
	private final String name;
	
	public TransformerFactory(String name,String factoryClass)
	{
		super();
		this.factoryClass = factoryClass;
		this.name = name;
	}

	public String getFactoryClass()
	{
		return factoryClass;
	}

	public String getName()
	{
		return name;
	}

}
