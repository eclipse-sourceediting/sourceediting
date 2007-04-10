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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Entity Reference Content</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent#getElementReferencedEntity <em>Element Referenced Entity</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntityReferenceContent()
 * @model
 * @generated
 */
public interface DTDEntityReferenceContent extends DTDRepeatableContent {

	/**
	 * Returns the value of the '<em><b>Element Referenced Entity</b></em>'
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getEntityReference <em>Entity Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element Referenced Entity</em>'
	 * reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Element Referenced Entity</em>'
	 *         reference.
	 * @see #setElementReferencedEntity(DTDEntity)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEntityReferenceContent_ElementReferencedEntity()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEntity#getEntityReference
	 * @model opposite="entityReference" required="true"
	 * @generated
	 */
	DTDEntity getElementReferencedEntity();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEntityReferenceContent#getElementReferencedEntity <em>Element Referenced Entity</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Element Referenced Entity</em>'
	 *            reference.
	 * @see #getElementReferencedEntity()
	 * @generated
	 */
	void setElementReferencedEntity(DTDEntity value);

} // DTDEntityReferenceContent

