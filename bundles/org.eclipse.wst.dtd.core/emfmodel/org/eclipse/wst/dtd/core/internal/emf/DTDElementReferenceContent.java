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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Element Reference Content</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent#getReferencedElement <em>Referenced Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElementReferenceContent()
 * @model
 * @generated
 */
public interface DTDElementReferenceContent extends DTDRepeatableContent {
	/**
	 * Returns the value of the '<em><b>Referenced Element</b></em>'
	 * reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Referenced Element</em>' reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Referenced Element</em>' reference.
	 * @see #setReferencedElement(DTDElement)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDElementReferenceContent_ReferencedElement()
	 * @model required="true"
	 * @generated
	 */
	DTDElement getReferencedElement();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent#getReferencedElement <em>Referenced Element</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Referenced Element</em>'
	 *            reference.
	 * @see #getReferencedElement()
	 * @generated
	 */
	void setReferencedElement(DTDElement value);

} // DTDElementReferenceContent
