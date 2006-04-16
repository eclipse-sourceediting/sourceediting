package org.eclipse.wst.xsd.ui.internal.refactor;

import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;


public class XMLRefactoringComponent implements RefactoringComponent
{
	// The name of the component being refactored
	String name;
	
	// The namespace in which component is defined, e.g. XML or WSDL target namespace
	String targetNamespace;
	
	// Optional model object that is refactored
	Object model;
	
	// SED DOM object that underlines the component being refactored
	IDOMElement domElement;

	public XMLRefactoringComponent(Object modelObject, IDOMElement domElement, String name, String namespace)
	{
		super();
		this.model = modelObject;
		this.domElement = domElement;
		this.name = name;
		this.targetNamespace = namespace;
		
		
	}
	
	public XMLRefactoringComponent(IDOMElement domElement, String name, String namespace)
	{
		super();
		this.domElement = domElement;
		this.name = name;
		this.targetNamespace = namespace;
	}

	public Object getModelObject()
	{
		return model;
	}

	public IDOMElement getElement()
	{
		return domElement;
	}

	public String getName()
	{
		
		return name;
	}

	public String getNamespaceURI()
	{
		return targetNamespace;
	}

	
	public QualifiedName getTypeQName()
	{
		return new QualifiedName(domElement.getNamespaceURI(), domElement.getLocalName());
	}
	
}
