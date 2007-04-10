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

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Basic Type</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicType#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDBasicType()
 * @model
 * @generated
 */
public interface DTDBasicType extends EClass, DTDType {
	// NON-GEN interfaces DTDType

	public String getTypeDescription();

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute. The
	 * literals are from the enumeration
	 * {@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind
	 * @see #setKind(DTDBasicTypeKind)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDBasicType_Kind()
	 * @model
	 * @generated
	 */
	DTDBasicTypeKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDBasicType#getKind <em>Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDBasicTypeKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(DTDBasicTypeKind value);
} // DTDBasicType
