package org.eclipse.wst.xsl.core.model;

import org.eclipse.core.runtime.PlatformObject;

public abstract class XSLModelObject extends PlatformObject
{
	public enum Type {STYLESHEET_MODEL,INCLUDES,IMPORT,INCLUDE,TEMPLATE,VARIABLE, CALL_TEMPLATE, STYLESHEET, ATTRIBUTE, OTHER_ELEMENT};
	
	public abstract Type getModelType();
}
