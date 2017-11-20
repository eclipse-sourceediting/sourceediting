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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EEnum;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Enumeration Type</b></em>'.
 * <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getKind <em>Kind</em>}</li>
 * <li>{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getDTDFile <em>DTD File</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEnumerationType()
 * @model
 * @generated
 */
public interface DTDEnumerationType extends EEnum, DTDType {

	public EList getEnumLiterals();

	public String getEnumerationTypeDescription();

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute. The
	 * literals are from the enumeration
	 * {@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind
	 * @see #setKind(DTDEnumGroupKind)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEnumerationType_Kind()
	 * @model
	 * @generated
	 */
	DTDEnumGroupKind getKind();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getKind <em>Kind</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Kind</em>' attribute.
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDEnumGroupKind
	 * @see #getKind()
	 * @generated
	 */
	void setKind(DTDEnumGroupKind value);

	/**
	 * Returns the value of the '<em><b>DTD File</b></em>' container
	 * reference. It is bidirectional and its opposite is '{@link org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDEnumerationType <em>DTD Enumeration Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>DTD File</em>' container reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>DTD File</em>' container reference.
	 * @see #setDTDFile(DTDFile)
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDPackage#getDTDEnumerationType_DTDFile()
	 * @see org.eclipse.wst.dtd.core.internal.emf.DTDFile#getDTDEnumerationType
	 * @model opposite="DTDEnumerationType"
	 * @generated
	 */
	DTDFile getDTDFile();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.dtd.core.internal.emf.DTDEnumerationType#getDTDFile <em>DTD File</em>}'
	 * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>DTD File</em>' container
	 *            reference.
	 * @see #getDTDFile()
	 * @generated
	 */
	void setDTDFile(DTDFile value);

} // DTDEnumerationType
