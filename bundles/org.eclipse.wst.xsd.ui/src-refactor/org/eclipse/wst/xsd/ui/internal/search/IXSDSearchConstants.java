package org.eclipse.wst.xsd.ui.internal.search;

import org.eclipse.wst.common.core.search.pattern.QualifiedName;

// todo ... move
public interface IXSDSearchConstants {
	
	public static final String XMLSCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	public static String XSD_CONTENT_TYPE_ID = "org.eclipse.wst.xsd.core.xsdsource";
	
    public static final QualifiedName   COMPLEX_TYPE_META_NAME =  new QualifiedName (XMLSCHEMA_NAMESPACE, "complexType");
    public static final QualifiedName   SIMPLE_TYPE_META_NAME =  new QualifiedName (XMLSCHEMA_NAMESPACE, "simpleType");
    public static final QualifiedName   ELEMENT_META_NAME =  new QualifiedName (XMLSCHEMA_NAMESPACE, "element");
	public static final QualifiedName   ATTRIBUTE_META_NAME =  new QualifiedName (XMLSCHEMA_NAMESPACE, "attribute");
	public static final QualifiedName   ATTRIBUTE_GROUP_META_NAME =  new QualifiedName (XMLSCHEMA_NAMESPACE, "attributeGroup");
	public static final QualifiedName   GROUP_META_NAME =  new QualifiedName (XMLSCHEMA_NAMESPACE, "group");

}
