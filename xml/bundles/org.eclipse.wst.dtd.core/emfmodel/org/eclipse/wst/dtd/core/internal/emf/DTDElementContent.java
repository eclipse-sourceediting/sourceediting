/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.emf;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Element Content</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getGroup <em>Group</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElementContent()
 * @model abstract="true"
 * @generated
 */
public interface DTDElementContent extends EObject, DTDObject, DTDSourceOffset {

	// for showing names to be used in the tree
	public String getContentName();

	// for showing items in the table
	public String getContentDetail();

	public DTDElement getDTDElement();

	// get the most outer DTDGroupContent
	public DTDGroupContent getDTDGroupContent();

	// returns true if this element content is the first
	// in the element
	// eg <!ELEMENT blah firstElement>
	// <!ELEMENT blah ((firstElement, secondElement) | lastElement)
	public boolean isFirstElementContent();

	/**
	 * Returns the value of the '<em><b>Group</b></em>' container
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' container reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Group</em>' container reference.
	 * @see #setGroup(DTDGroupContent)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElementContent_Group()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDGroupContent#getContent
	 * @model opposite="content"
	 * @generated
	 */
	DTDGroupContent getGroup();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getGroup <em>Group</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Group</em>' container
	 *            reference.
	 * @see #getGroup()
	 * @generated
	 */
	void setGroup(DTDGroupContent value);

	/**
	 * Returns the value of the '<em><b>Element</b></em>' container
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElement#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element</em>' container reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Element</em>' container reference.
	 * @see #setElement(DTDElement)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElementContent_Element()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDElement#getContent
	 * @model opposite="content"
	 * @generated
	 */
	DTDElement getElement();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementContent#getElement <em>Element</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Element</em>' container
	 *            reference.
	 * @see #getElement()
	 * @generated
	 */
	void setElement(DTDElement value);

} // DTDElementContent
