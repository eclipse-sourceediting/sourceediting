/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor;

import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;


public interface RefactoringComponent
{
	/**
	 * @return the name of the component that is refactored.  E.g. "foo"
	 */
	public String getName();
	
	/**
	 * @return the namespace of the component that is refactored.  E.g. "http://foo"
	 */
	public String getNamespaceURI();
	
	/**
	 * The basic DOM element is used by the refactoring processor/participant to get 
	 * access to the file location.
	 * 
	 * @return the Structured Source Editor XML DOM element object that underlines the 
	 * combonent being refactore.
	 * 
	 * @see IDOMElement 
	 */
	public IDOMElement getElement();
	
	/** 
	 * @return the qualified name of the type of the refactored component. 
	 * 
	 * <p>
	 * A qualified name consists of a local name and a namespace.  
	 * E.g. "complexType"-local name, "http://www.w3.org/2001/XMLSchema"-namespace
	 * </p>
	 * 
	 * @see QualifiedName
	 */
	public QualifiedName getTypeQName();
		
	/** 
	 * The model object may be required to be given to the refactored participants as is or 
	 * other objects could be derived from it.
	 * 
	 * @return the principal object being refactored, such as an instance of WSDLElement or 
	 * XSDNamedComponent
	 */
	public Object getModelObject();

}
